package com.example.hotelmanagementapp.data.repository

import androidx.lifecycle.LiveData
import com.example.hotelmanagementapp.data.db.MenuItemDao
import com.example.hotelmanagementapp.data.model.MenuItem

class MenuRepository(private val menuItemDao: MenuItemDao) {

    val allMenuItems: LiveData<List<MenuItem>> = menuItemDao.getAllMenuItems()

    suspend fun insertMenuItem(item: MenuItem) {
        menuItemDao.insertMenuItem(item)
    }

    suspend fun updateMenuItem(item: MenuItem) {
        menuItemDao.updateMenuItem(item)
    }

    suspend fun deleteMenuItem(item: MenuItem) {
        menuItemDao.deleteMenuItem(item)
    }

    suspend fun updatePrice(id: Int, newPrice: Int) {
        menuItemDao.updatePrice(id, newPrice)
    }
}