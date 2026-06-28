package com.example.hotelmanagementapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.hotelmanagementapp.data.model.InventoryItem

@Dao
interface InventoryDao {

    @Query("SELECT * FROM inventory ORDER BY category, itemName")
    fun getAllInventory(): LiveData<List<InventoryItem>>

    @Query("SELECT * FROM inventory WHERE currentStock <= minStockAlert AND minStockAlert > 0")
    fun getLowStockItems(): LiveData<List<InventoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: InventoryItem)

    @Update
    suspend fun updateItem(item: InventoryItem)

    @Delete
    suspend fun deleteItem(item: InventoryItem)

    @Query("UPDATE inventory SET currentStock = currentStock + :qty, lastUpdated = :time WHERE id = :id")
    suspend fun addStock(id: Int, qty: Double, time: Long = System.currentTimeMillis())

    @Query("UPDATE inventory SET currentStock = currentStock - :qty, lastUpdated = :time WHERE id = :id")
    suspend fun reduceStock(id: Int, qty: Double, time: Long = System.currentTimeMillis())
}