package com.example.hotelmanagementapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_items")
data class OrderItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val orderId: Int,
    val menuItemId: Int,
    val menuItemName: String,
    val quantity: Int,
    val priceEach: Int
)