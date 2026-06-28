package com.example.hotelmanagementapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val category: String,
    val itemName: String,
    val quantity: Double = 0.0,
    val unit: String = "",
    val totalCost: Int,
    val pricePerUnit: Int = 0,
    val supplier: String = "",
    val note: String = "",
    val date: Long = System.currentTimeMillis()
)