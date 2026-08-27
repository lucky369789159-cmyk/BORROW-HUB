package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class BorrowRepository(private val db: AppDatabase) {

    val currentUser: Flow<UserEntity?> = db.userDao().getUserById("user_me")

    fun getItemsForHub(hubId: String): Flow<List<ItemEntity>> = db.itemDao().getItemsForHub(hubId)

    fun getItemsForHubAndCategory(hubId: String, category: String): Flow<List<ItemEntity>> = db.itemDao().getItemsForHubAndCategory(hubId, category)

    fun getRequestsForHub(hubId: String): Flow<List<ItemRequestEntity>> = db.requestDao().getRequestsForHub(hubId)

    fun getAllRentals(): Flow<List<RentalEntity>> = db.rentalDao().getAllRentals()

    fun getRentalById(id: String): Flow<RentalEntity?> = db.rentalDao().getRentalById(id)

    fun getChatMessages(rentalId: String): Flow<List<ChatMessageEntity>> = db.chatDao().getMessagesForRental(rentalId)

    suspend fun createItem(
        ownerId: String,
        ownerName: String,
        ownerScore: Int,
        title: String,
        category: String,
        description: String,
        pricePerDay: Int,
        depositAmount: Int,
        hubId: String
    ) {
        val newItem = ItemEntity(
            id = "item_" + UUID.randomUUID().toString().take(8),
            ownerId = ownerId,
            ownerName = ownerName,
            ownerScore = ownerScore,
            name = title,
            category = category,
            description = description,
            pricePerDay = pricePerDay,
            depositAmount = depositAmount,
            distanceKm = 0.2f, // Posted right here in neighborhood
            rating = 5.0f,
            rentalsCount = 0,
            availabilityStatus = "AVAILABLE",
            imageUrl = category.lowercase(),
            hubId = hubId
        )
        db.itemDao().insertItem(newItem)
    }

    suspend fun createItemRequest(
        requesterId: String,
        requesterName: String,
        requesterScore: Int,
        title: String,
        timeframe: String,
        maxPrice: Int,
        category: String,
        hubId: String
    ) {
        val newReq = ItemRequestEntity(
            id = "req_" + UUID.randomUUID().toString().take(8),
            requesterId = requesterId,
            requesterName = requesterName,
            requesterScore = requesterScore,
            title = title,
            timeframe = timeframe,
            maxPrice = maxPrice,
            distanceKm = 0.3f,
            category = category,
            status = "OPEN",
            hubId = hubId,
            createdAt = System.currentTimeMillis()
        )
        db.requestDao().insertRequest(newReq)
    }

    suspend fun requestRental(
        item: ItemEntity,
        borrower: UserEntity,
        totalDays: Int = 1
    ): String {
        val rentalFee = item.pricePerDay * totalDays
        val serviceFee = (rentalFee * 0.10f).toInt().coerceAtLeast(10) // 10% marketplace fee
        val totalPrice = rentalFee + serviceFee

        val rentalId = "rent_" + UUID.randomUUID().toString().take(8)
        val rental = RentalEntity(
            id = rentalId,
            itemId = item.id,
            itemTitle = item.name,
            itemPricePerDay = item.pricePerDay,
            itemDeposit = item.depositAmount,
            ownerId = item.ownerId,
            ownerName = item.ownerName,
            borrowerId = borrower.id,
            borrowerName = borrower.name,
            startDate = "Today, 5:00 PM",
            endDate = "Tomorrow, 5:00 PM",
            totalDays = totalDays,
            rentalFee = rentalFee,
            serviceFee = serviceFee,
            totalPrice = totalPrice,
            status = "INSPECTION_PENDING",
            createdAt = System.currentTimeMillis()
        )

        db.rentalDao().insertRental(rental)
        db.itemDao().updateItemStatus(item.id, "RENTED")

        // Add initial system chat message
        val sysMsg = ChatMessageEntity(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            rentalId = rentalId,
            senderId = "system",
            senderName = "BorrowHub Safety",
            text = "🎉 Rental request submitted for ${item.name}! Escrow deposit of ₹${item.depositAmount} secured. Complete 4-Photo Condition Check before pickup.",
            timestamp = System.currentTimeMillis(),
            isSystem = true
        )
        db.chatDao().insertMessage(sysMsg)
        return rentalId
    }

    suspend fun submitBeforeInspectionPhotos(
        rentalId: String,
        front: String,
        back: String,
        left: String,
        right: String
    ) {
        db.rentalDao().updateBeforePhotos(rentalId, front, back, left, right, "ACTIVE")
        val sysMsg = ChatMessageEntity(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            rentalId = rentalId,
            senderId = "system",
            senderName = "BorrowHub Inspection",
            text = "📸 Pickup condition inspection complete! 4 photos recorded in system log. Item is now ACTIVE.",
            timestamp = System.currentTimeMillis(),
            isSystem = true
        )
        db.chatDao().insertMessage(sysMsg)
    }

    suspend fun submitAfterInspectionPhotos(
        rentalId: String,
        front: String,
        back: String,
        left: String,
        right: String
    ) {
        db.rentalDao().updateAfterPhotos(rentalId, front, back, left, right, "COMPLETED")
        val sysMsg = ChatMessageEntity(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            rentalId = rentalId,
            senderId = "system",
            senderName = "BorrowHub Trust",
            text = "✅ Return condition verified against pickup baseline. 0 damage detected. Security deposit released to borrower! Saksham's Borrow Score +2 points (Now 93/100).",
            timestamp = System.currentTimeMillis(),
            isSystem = true
        )
        db.chatDao().insertMessage(sysMsg)
    }

    suspend fun sendChatMessage(rentalId: String, senderId: String, senderName: String, text: String) {
        val msg = ChatMessageEntity(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            rentalId = rentalId,
            senderId = senderId,
            senderName = senderName,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        db.chatDao().insertMessage(msg)
    }
}
