package com.example.hotelmanagementapp.data.repository

import com.example.hotelmanagementapp.data.db.InventoryDao
import com.example.hotelmanagementapp.data.model.InventoryItem

class InventoryRepository(private val dao: InventoryDao) {

    val allInventory = dao.getAllInventory()
    val lowStockItems = dao.getLowStockItems()

    suspend fun insertItem(item: InventoryItem) = dao.insertItem(item)
    suspend fun updateItem(item: InventoryItem) = dao.updateItem(item)
    suspend fun deleteItem(item: InventoryItem) = dao.deleteItem(item)
    suspend fun addStock(id: Int, qty: Double) = dao.addStock(id, qty)
    suspend fun reduceStock(id: Int, qty: Double) = dao.reduceStock(id, qty)
}