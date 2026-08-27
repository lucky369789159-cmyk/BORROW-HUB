package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class HubLocation(
    val id: String,
    val name: String,
    val totalNearbyItems: Int,
    val totalUsers: Int,
    val type: String // "Campus", "Society", "Sector"
)

enum class ItemCategory(val displayName: String, val iconName: String) {
    ALL("All", "Apps"),
    TOOLS("Tools", "Build"),
    ELECTRONICS("Electronics", "Devices"),
    STUDY("Study", "Book"),
    SPORTS("Sports", "SportsCricket"),
    TRAVEL("Travel", "Luggage"),
    EVENTS("Events", "PartyMode")
}

enum class RentalStatus(val label: String) {
    REQUESTED("Requested"),
    ACCEPTED("Accepted - Pickup Pending"),
    INSPECTION_PENDING("Inspection Required"),
    ACTIVE("Active Rental"),
    RETURN_INSPECTION("Return Inspection"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val avatarUrl: String,
    val rating: Float,
    val borrowScore: Int, // 0 - 100
    val successfulReturns: Int,
    val damagedItemsCount: Int,
    val lateReturnsCount: Int,
    val verificationStatus: String,
    val memberSinceYear: Int,
    val hubId: String
)

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: String,
    val ownerId: String,
    val ownerName: String,
    val ownerScore: Int,
    val name: String,
    val category: String,
    val description: String,
    val pricePerDay: Int,
    val depositAmount: Int,
    val distanceKm: Float,
    val rating: Float,
    val rentalsCount: Int,
    val availabilityStatus: String, // "AVAILABLE", "RENTED"
    val imageUrl: String,
    val hubId: String
)

@Entity(tableName = "item_requests")
data class ItemRequestEntity(
    @PrimaryKey val id: String,
    val requesterId: String,
    val requesterName: String,
    val requesterScore: Int,
    val title: String,
    val timeframe: String,
    val maxPrice: Int,
    val distanceKm: Float,
    val category: String,
    val status: String, // "OPEN", "FULFILLED"
    val hubId: String,
    val createdAt: Long
)

@Entity(tableName = "rentals")
data class RentalEntity(
    @PrimaryKey val id: String,
    val itemId: String,
    val itemTitle: String,
    val itemPricePerDay: Int,
    val itemDeposit: Int,
    val ownerId: String,
    val ownerName: String,
    val borrowerId: String,
    val borrowerName: String,
    val startDate: String,
    val endDate: String,
    val totalDays: Int,
    val rentalFee: Int,
    val serviceFee: Int,
    val totalPrice: Int,
    val status: String,
    val beforePhotoFront: String = "",
    val beforePhotoBack: String = "",
    val beforePhotoLeft: String = "",
    val beforePhotoRight: String = "",
    val afterPhotoFront: String = "",
    val afterPhotoBack: String = "",
    val afterPhotoLeft: String = "",
    val afterPhotoRight: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val rentalId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val timestamp: Long,
    val isSystem: Boolean = false
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val rentalId: String,
    val reviewerName: String,
    val rating: Float,
    val comment: String,
    val date: String
)

data class AiListingResult(
    val title: String,
    val category: String,
    val suggestedPricePerDay: Int,
    val suggestedDeposit: Int,
    val description: String,
    val confidence: String
)

data class NeighborhoodInventorySummary(
    val hubName: String,
    val radiusKm: Float,
    val totalDrills: Int,
    val totalProjectors: Int,
    val totalSuitcases: Int,
    val totalCalculators: Int,
    val totalTents: Int,
    val totalSportsGear: Int
)
