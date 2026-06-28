package com.example.hotelmanagementapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.hotelmanagementapp.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        MenuItem::class,
        Order::class,
        OrderItem::class,
        Expense::class,
        InventoryItem::class,
        DuePayment::class
    ],
    version = 2,
    exportSchema = false
)
abstract class HotelDatabase : RoomDatabase() {

    abstract fun menuItemDao(): MenuItemDao
    abstract fun orderDao(): OrderDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun duePaymentDao(): DuePaymentDao

    companion object {
        @Volatile
        private var INSTANCE: HotelDatabase? = null

        fun getDatabase(context: Context): HotelDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HotelDatabase::class.java,
                    "hotel_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    database.menuItemDao().insertMenuItem(MenuItem(name="Chicken Piece", urduName="چکن پیس", price=120, emoji="🍗"))
                                    database.menuItemDao().insertMenuItem(MenuItem(name="Shammi Kabab", urduName="شامی کباب", price=80, emoji="🥩"))
                                    database.menuItemDao().insertMenuItem(MenuItem(name="Seekh Kabab", urduName="سیخ کباب", price=90, emoji="🍢"))
                                    database.menuItemDao().insertMenuItem(MenuItem(name="Chicken Tikka", urduName="چکن ٹکہ", price=150, emoji="🔥"))
                                    database.menuItemDao().insertMenuItem(MenuItem(name="Bread", urduName="روٹی", price=20, emoji="🫓"))
                                    database.menuItemDao().insertMenuItem(MenuItem(name="Cold Drink", urduName="کولڈ ڈرنک", price=50, emoji="🥤"))

                                    database.inventoryDao().insertItem(InventoryItem(itemName="Chicken", category="Meat", currentStock=25.0, unit="kg", minStockAlert=5.0))
                                    database.inventoryDao().insertItem(InventoryItem(itemName="Fish", category="Meat", currentStock=10.0, unit="kg", minStockAlert=2.0))
                                    database.inventoryDao().insertItem(InventoryItem(itemName="Flour", category="Grocery", currentStock=20.0, unit="kg", minStockAlert=5.0))
                                    database.inventoryDao().insertItem(InventoryItem(itemName="Vegetables", category="Grocery", currentStock=10.0, unit="kg", minStockAlert=2.0))
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}