package com.example.hotelmanagementapp.ui.order

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.hotelmanagementapp.data.db.HotelDatabase
import com.example.hotelmanagementapp.data.model.Order
import com.example.hotelmanagementapp.data.model.OrderItem
import com.example.hotelmanagementapp.data.repository.MenuRepository
import com.example.hotelmanagementapp.data.repository.OrderRepository
import kotlinx.coroutines.launch

class OrderViewModel(application: Application) : AndroidViewModel(application) {

    private val orderRepository: OrderRepository = OrderRepository(
        HotelDatabase.getDatabase(application).orderDao()
    )

    private val menuRepository: MenuRepository = MenuRepository(
        HotelDatabase.getDatabase(application).menuItemDao()
    )

    val activeOrders = orderRepository.activeOrders
    val paidOrders = orderRepository.paidOrders
    val todayRevenue = orderRepository.todayRevenue
    val paidOrderCount = orderRepository.paidOrderCount
    val allMenuItems = menuRepository.allMenuItems

    fun getPaidOrdersSince(startTime: Long): LiveData<List<Order>> {
        return orderRepository.getPaidOrdersSince(startTime)
    }

    fun getPaidOrdersBetween(startTime: Long, endTime: Long): LiveData<List<Order>> {
        return orderRepository.getPaidOrdersBetween(startTime, endTime)
    }

    fun placeOrder(order: Order, items: List<OrderItem>) = viewModelScope.launch {
        orderRepository.placeOrder(order, items)
    }

    fun updateOrderStatus(id: Int, status: String) = viewModelScope.launch {
        orderRepository.updateOrderStatus(id, status)
    }

    fun deleteOrder(order: Order) = viewModelScope.launch {
        orderRepository.deleteOrder(order)
    }
}