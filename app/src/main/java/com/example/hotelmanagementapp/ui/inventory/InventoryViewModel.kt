package com.example.hotelmanagementapp.ui.inventory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelmanagementapp.data.db.HotelDatabase
import com.example.hotelmanagementapp.data.model.InventoryItem
import com.example.hotelmanagementapp.data.repository.InventoryRepository
import kotlinx.coroutines.launch

class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = InventoryRepository(
        HotelDatabase.getDatabase(application).inventoryDao()
    )

    val allInventory = repository.allInventory
    val lowStockItems = repository.lowStockItems

    fun insertItem(item: InventoryItem) = viewModelScope.launch {
        repository.insertItem(item)
    }

    fun updateItem(item: InventoryItem) = viewModelScope.launch {
        repository.updateItem(item)
    }

    fun addStock(id: Int, qty: Double) = viewModelScope.launch {
        repository.addStock(id, qty)
    }

    fun reduceStock(id: Int, qty: Double) = viewModelScope.launch {
        repository.reduceStock(id, qty)
    }

    fun deleteItem(item: InventoryItem) = viewModelScope.launch {
        repository.deleteItem(item)
    }
}