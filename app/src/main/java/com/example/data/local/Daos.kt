package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
}

@Dao
interface ItemDao {
    @Query("SELECT * FROM items WHERE hubId = :hubId ORDER BY distanceKm ASC")
    fun getItemsForHub(hubId: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE hubId = :hubId AND category = :category ORDER BY distanceKm ASC")
    fun getItemsForHubAndCategory(hubId: String, category: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE id = :id")
    fun getItemById(id: String): Flow<ItemEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ItemEntity>)

    @Query("UPDATE items SET availabilityStatus = :status WHERE id = :id")
    suspend fun updateItemStatus(id: String, status: String)
}

@Dao
interface RequestDao {
    @Query("SELECT * FROM item_requests WHERE hubId = :hubId ORDER BY createdAt DESC")
    fun getRequestsForHub(hubId: String): Flow<List<ItemRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: ItemRequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequests(requests: List<ItemRequestEntity>)

    @Query("UPDATE item_requests SET status = :status WHERE id = :id")
    suspend fun updateRequestStatus(id: String, status: String)
}

@Dao
interface RentalDao {
    @Query("SELECT * FROM rentals ORDER BY createdAt DESC")
    fun getAllRentals(): Flow<List<RentalEntity>>

    @Query("SELECT * FROM rentals WHERE id = :id")
    fun getRentalById(id: String): Flow<RentalEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRental(rental: RentalEntity)

    @Query("UPDATE rentals SET status = :status WHERE id = :id")
    suspend fun updateRentalStatus(id: String, status: String)

    @Query("UPDATE rentals SET beforePhotoFront = :f, beforePhotoBack = :b, beforePhotoLeft = :l, beforePhotoRight = :r, status = :status WHERE id = :id")
    suspend fun updateBeforePhotos(id: String, f: String, b: String, l: String, r: String, status: String)

    @Query("UPDATE rentals SET afterPhotoFront = :f, afterPhotoBack = :b, afterPhotoLeft = :l, afterPhotoRight = :r, status = :status WHERE id = :id")
    suspend fun updateAfterPhotos(id: String, f: String, b: String, l: String, r: String, status: String)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE rentalId = :rentalId ORDER BY timestamp ASC")
    fun getMessagesForRental(rentalId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)
}
