package com.example.hotelmanagementapp.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelmanagementapp.data.db.HotelDatabase
import com.example.hotelmanagementapp.data.model.MenuItem
import com.example.hotelmanagementapp.data.repository.MenuRepository
import kotlinx.coroutines.launch

class MenuViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MenuRepository = MenuRepository(
        HotelDatabase.getDatabase(application).menuItemDao()
    )

    val allMenuItems = repository.allMenuItems

    fun insertMenuItem(item: MenuItem) = viewModelScope.launch {
        repository.insertMenuItem(item)
    }

    fun updateMenuItem(item: MenuItem) = viewModelScope.launch {
        repository.updateMenuItem(item)
    }

    fun deleteMenuItem(item: MenuItem) = viewModelScope.launch {
        repository.deleteMenuItem(item)
    }

    fun updatePrice(id: Int, newPrice: Int) = viewModelScope.launch {
        repository.updatePrice(id, newPrice)
    }
}