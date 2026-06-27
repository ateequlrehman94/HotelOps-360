package com.example.hotelmanagementapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val orderType: String,
    val tableNumber: Int? = null,
    val customerName: String? = null,
    val totalAmount: Int,
    val note: String = "",
    val status: String = "pending",
    val createdAt: Long = System.currentTimeMillis()
)