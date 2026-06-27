package com.example.hotelmanagementapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.hotelmanagementapp.data.model.Order
import com.example.hotelmanagementapp.data.model.OrderItem

@Dao
interface OrderDao {

    @Query("SELECT * FROM orders WHERE status != 'paid' ORDER BY createdAt DESC")
    fun getActiveOrders(): LiveData<List<Order>>

    @Query("SELECT * FROM orders WHERE status = 'paid' ORDER BY createdAt DESC")
    fun getPaidOrders(): LiveData<List<Order>>

    @Query("SELECT * FROM orders WHERE status = 'paid' AND createdAt >= :startTime ORDER BY createdAt DESC")
    fun getPaidOrdersSince(startTime: Long): LiveData<List<Order>>

    @Query("SELECT * FROM orders WHERE status = 'paid' AND createdAt >= :startTime AND createdAt < :endTime ORDER BY createdAt DESC")
    fun getPaidOrdersBetween(startTime: Long, endTime: Long): LiveData<List<Order>>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getItemsForOrder(orderId: Int): List<OrderItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItem>)

    @Query("UPDATE orders SET status = :status WHERE id = :id")
    suspend fun updateOrderStatus(id: Int, status: String)

    @Delete
    suspend fun deleteOrder(order: Order)

    @Query("SELECT SUM(totalAmount) FROM orders WHERE status = 'paid'")
    fun getTodayRevenue(): LiveData<Int?>

    @Query("SELECT COUNT(*) FROM orders WHERE status = 'paid'")
    fun getPaidOrderCount(): LiveData<Int>
}