package com.example.hotelmanagementapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.hotelmanagementapp.data.model.MenuItem

@Dao
interface MenuItemDao {

    @Query("SELECT * FROM menu_items WHERE isActive = 1")
    fun getAllMenuItems(): LiveData<List<MenuItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItem(item: MenuItem)

    @Update
    suspend fun updateMenuItem(item: MenuItem)

    @Delete
    suspend fun deleteMenuItem(item: MenuItem)

    @Query("UPDATE menu_items SET price = :newPrice WHERE id = :id")
    suspend fun updatePrice(id: Int, newPrice: Int)
}