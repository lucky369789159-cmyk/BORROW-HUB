package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiListingService
import com.example.data.local.AppDatabase
import com.example.data.local.SeedData
import com.example.data.model.*
import com.example.data.repository.BorrowRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SortOption(val displayName: String) {
    DEFAULT("Featured"),
    PRICE_LOW_TO_HIGH("Price: Low to High"),
    PRICE_HIGH_TO_LOW("Price: High to Low"),
    DEPOSIT_LOW_TO_HIGH("Deposit: Low to High")
}

sealed class AiScannerState {
    object Idle : AiScannerState()
    object Analyzing : AiScannerState()
    data class Success(val result: AiListingResult) : AiScannerState()
    data class Error(val message: String) : AiScannerState()
}

private data class FilterState(
    val hub: HubLocation,
    val category: ItemCategory,
    val query: String,
    val sort: SortOption
)

class BorrowViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BorrowRepository(AppDatabase.getDatabase(application))

    val hubs = SeedData.HUBS

    private val _selectedHub = MutableStateFlow(hubs.first())
    val selectedHub: StateFlow<HubLocation> = _selectedHub.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ItemCategory.ALL)
    val selectedCategory: StateFlow<ItemCategory> = _selectedCategory.asStateFlow()

    private val _selectedSort = MutableStateFlow(SortOption.DEFAULT)
    val selectedSort: StateFlow<SortOption> = _selectedSort.asStateFlow()

    val currentUser: StateFlow<UserEntity?> = repository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SeedData.CURRENT_USER)

    val items: StateFlow<List<ItemEntity>> = combine(
        selectedHub,
        selectedCategory,
        searchQuery,
        selectedSort
    ) { hub, category, query, sort ->
        FilterState(hub, category, query, sort)
    }.flatMapLatest { state ->
        val rawFlow = if (state.category == ItemCategory.ALL) {
            repository.getItemsForHub(state.hub.id)
        } else {
            repository.getItemsForHubAndCategory(state.hub.id, state.category.displayName)
        }
        rawFlow.map { list ->
            val filtered = if (state.query.isBlank()) list
            else list.filter {
                it.name.contains(state.query, ignoreCase = true) ||
                it.description.contains(state.query, ignoreCase = true) ||
                it.category.contains(state.query, ignoreCase = true)
            }
            when (state.sort) {
                SortOption.DEFAULT -> filtered
                SortOption.PRICE_LOW_TO_HIGH -> filtered.sortedBy { it.pricePerDay }
                SortOption.PRICE_HIGH_TO_LOW -> filtered.sortedByDescending { it.pricePerDay }
                SortOption.DEPOSIT_LOW_TO_HIGH -> filtered.sortedBy { it.depositAmount }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val requests: StateFlow<List<ItemRequestEntity>> = selectedHub.flatMapLatest { hub ->
        repository.getRequestsForHub(hub.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rentals: StateFlow<List<RentalEntity>> = repository.getAllRentals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _aiState = MutableStateFlow<AiScannerState>(AiScannerState.Idle)
    val aiState: StateFlow<AiScannerState> = _aiState.asStateFlow()

    // Active selected rental for Chat/Inspection Detail
    private val _activeRentalId = MutableStateFlow<String?>(null)
    val activeRentalId: StateFlow<String?> = _activeRentalId.asStateFlow()

    val activeRentalMessages: StateFlow<List<ChatMessageEntity>> = combine(
        activeRentalId,
        rentals
    ) { id, rentalList ->
        id ?: rentalList.firstOrNull()?.id
    }.flatMapLatest { resolvedId ->
        if (resolvedId == null) flowOf(emptyList())
        else repository.getChatMessages(resolvedId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectHub(hub: HubLocation) {
        _selectedHub.value = hub
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: ItemCategory) {
        _selectedCategory.value = category
    }

    fun selectSort(sort: SortOption) {
        _selectedSort.value = sort
    }

    fun setActiveRentalId(id: String?) {
        _activeRentalId.value = id
    }

    // AI Photo Scanner Listing Action
    fun analyzePhotoForItemListing(promptOrKeyword: String) {
        viewModelScope.launch {
            _aiState.value = AiScannerState.Analyzing
            try {
                val res = GeminiListingService.analyzeItemFromPhotoPrompt(promptOrKeyword)
                _aiState.value = AiScannerState.Success(res)
            } catch (e: Exception) {
                _aiState.value = AiScannerState.Error(e.message ?: "Failed to analyze photo")
            }
        }
    }

    fun resetAiScanner() {
        _aiState.value = AiScannerState.Idle
    }

    fun confirmCreateItemFromAi(title: String, category: String, description: String, pricePerDay: Int, deposit: Int) {
        viewModelScope.launch {
            val user = currentUser.value ?: SeedData.CURRENT_USER
            repository.createItem(
                ownerId = user.id,
                ownerName = user.name,
                ownerScore = user.borrowScore,
                title = title,
                category = category,
                description = description,
                pricePerDay = pricePerDay,
                depositAmount = deposit,
                hubId = selectedHub.value.id
            )
            _aiState.value = AiScannerState.Idle
        }
    }

    fun postNeedRequest(title: String, timeframe: String, maxPrice: Int, category: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: SeedData.CURRENT_USER
            repository.createItemRequest(
                requesterId = user.id,
                requesterName = user.name,
                requesterScore = user.borrowScore,
                title = title,
                timeframe = timeframe,
                maxPrice = maxPrice,
                category = category,
                hubId = selectedHub.value.id
            )
        }
    }

    fun offerMineToRequest(request: ItemRequestEntity, onOfferCompleted: (String) -> Unit = {}) {
        viewModelScope.launch {
            val user = currentUser.value ?: SeedData.CURRENT_USER
            val cleanTitle = request.title.replace("🏏 Need a ", "").replace("📹 Need a ", "").replace("🪜 Need an ", "")
            // Create item listing
            repository.createItem(
                ownerId = user.id,
                ownerName = user.name,
                ownerScore = user.borrowScore,
                title = cleanTitle,
                category = request.category,
                description = "Offered directly for neighbor request: ${request.title}",
                pricePerDay = request.maxPrice,
                depositAmount = request.maxPrice * 3,
                hubId = selectedHub.value.id
            )
            val offeredItem = ItemEntity(
                id = "item_offered_${System.currentTimeMillis()}",
                ownerId = user.id,
                ownerName = user.name,
                ownerScore = user.borrowScore,
                name = cleanTitle,
                category = request.category,
                description = "Offered directly for neighbor request: ${request.title}",
                pricePerDay = request.maxPrice,
                depositAmount = request.maxPrice * 3,
                distanceKm = 0.2f,
                rating = 5.0f,
                rentalsCount = 0,
                availabilityStatus = "AVAILABLE",
                imageUrl = request.category.lowercase(),
                hubId = selectedHub.value.id
            )
            val rentalId = repository.requestRental(offeredItem, user, 1)
            _activeRentalId.value = rentalId
            onOfferCompleted(rentalId)
        }
    }

    fun requestItemRental(item: ItemEntity, totalDays: Int = 1, onRentalCreated: (String) -> Unit) {
        viewModelScope.launch {
            val borrower = currentUser.value ?: SeedData.CURRENT_USER
            val rentalId = repository.requestRental(item, borrower, totalDays)
            _activeRentalId.value = rentalId
            onRentalCreated(rentalId)
        }
    }

    fun submitPickupPhotos(rentalId: String, front: String, back: String, left: String, right: String) {
        viewModelScope.launch {
            repository.submitBeforeInspectionPhotos(rentalId, front, back, left, right)
        }
    }

    fun submitReturnPhotos(rentalId: String, front: String, back: String, left: String, right: String) {
        viewModelScope.launch {
            repository.submitAfterInspectionPhotos(rentalId, front, back, left, right)
        }
    }

    fun sendChatMessage(rentalId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val user = currentUser.value ?: SeedData.CURRENT_USER
            repository.sendChatMessage(rentalId, user.id, user.name, text)
        }
    }
}
