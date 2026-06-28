package com.example.hotelmanagementapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "due_payments")
data class DuePayment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val orderId: Int,
    val customerName: String,
    val customerPhone: String = "",
    val totalAmount: Int,
    val paidAmount: Int = 0,
    val dueAmount: Int,
    val status: String = "due",
    val promisedDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val paidAt: Long? = null,
    val note: String = ""
)