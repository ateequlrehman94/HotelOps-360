package com.example.hotelmanagementapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val itemName: String,
    val category: String,
    val currentStock: Double,
    val unit: String,
    val minStockAlert: Double = 0.0,
    val lastUpdated: Long = System.currentTimeMillis()
)