package com.example.hotelmanagementapp.data.repository

import androidx.lifecycle.LiveData
import com.example.hotelmanagementapp.data.db.OrderDao
import com.example.hotelmanagementapp.data.model.Order
import com.example.hotelmanagementapp.data.model.OrderItem

class OrderRepository(private val orderDao: OrderDao) {

    val activeOrders: LiveData<List<Order>> = orderDao.getActiveOrders()
    val paidOrders: LiveData<List<Order>> = orderDao.getPaidOrders()
    val todayRevenue: LiveData<Int?> = orderDao.getTodayRevenue()
    val paidOrderCount: LiveData<Int> = orderDao.getPaidOrderCount()

    fun getPaidOrdersSince(startTime: Long): LiveData<List<Order>> {
        return orderDao.getPaidOrdersSince(startTime)
    }

    fun getPaidOrdersBetween(startTime: Long, endTime: Long): LiveData<List<Order>> {
        return orderDao.getPaidOrdersBetween(startTime, endTime)
    }

    suspend fun placeOrder(order: Order, items: List<OrderItem>): Long {
        val orderId = orderDao.insertOrder(order)
        val itemsWithOrderId = items.map { it.copy(orderId = orderId.toInt()) }
        orderDao.insertOrderItems(itemsWithOrderId)
        return orderId
    }

    suspend fun updateOrderStatus(id: Int, status: String) {
        orderDao.updateOrderStatus(id, status)
    }

    suspend fun deleteOrder(order: Order) {
        orderDao.deleteOrder(order)
    }

    suspend fun getItemsForOrder(orderId: Int): List<OrderItem> {
        return orderDao.getItemsForOrder(orderId)
    }
}