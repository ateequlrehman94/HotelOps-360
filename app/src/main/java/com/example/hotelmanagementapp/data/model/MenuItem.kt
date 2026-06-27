package com.example.hotelmanagementapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu_items")
data class MenuItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val urduName: String,
    val price: Int,
    val emoji: String,
    val isActive: Boolean = true
)