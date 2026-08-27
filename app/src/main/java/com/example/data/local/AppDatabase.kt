package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        ItemEntity::class,
        ItemRequestEntity::class,
        RentalEntity::class,
        ChatMessageEntity::class,
        ReviewEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun itemDao(): ItemDao
    abstract fun requestDao(): RequestDao
    abstract fun rentalDao(): RentalDao
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "borrow_hub_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed initial data asynchronously on creation
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                database.userDao().insertUser(SeedData.CURRENT_USER)
                                SeedData.SEED_USERS.forEach { database.userDao().insertUser(it) }
                                database.itemDao().insertItems(SeedData.SEED_ITEMS)
                                database.requestDao().insertRequests(SeedData.SEED_REQUESTS)
                                database.rentalDao().insertRental(SeedData.INITIAL_RENTAL)
                                database.chatDao().insertMessages(SeedData.INITIAL_MESSAGES)
                            }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
