package com.example.hotelmanagementapp.ui.kitchen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hotelmanagementapp.data.model.DuePayment
import com.example.hotelmanagementapp.data.model.Order
import com.example.hotelmanagementapp.databinding.FragmentKitchenBinding
import com.example.hotelmanagementapp.ui.dues.DueViewModel
import com.example.hotelmanagementapp.ui.order.OrderViewModel

class KitchenFragment : Fragment() {

    private var _binding: FragmentKitchenBinding? = null
    private val binding get() = _binding!!
    private val orderViewModel: OrderViewModel by viewModels()
    private val dueViewModel: DueViewModel by viewModels()
    private lateinit var adapter: KitchenAdapter
    private var allOrders = listOf<Order>()
    private var currentFilter = "all"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKitchenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = KitchenAdapter(
            onReady = { order -> orderViewModel.updateOrderStatus(order.id, "ready") },
            onPaid = { order ->
                orderViewModel.updateOrderStatus(order.id, "paid")
                Toast.makeText(requireContext(), "✅ Payment received! Rs. ${order.totalAmount}", Toast.LENGTH_SHORT).show()
            },
            onCancel = { order -> orderViewModel.deleteOrder(order) },
            onDuePayment = { order, customerName, phone ->
                orderViewModel.updateOrderStatus(order.id, "paid")
                val due = DuePayment(
                    orderId = order.id,
                    customerName = customerName,
                    customerPhone = phone,
                    totalAmount = order.totalAmount,
                    dueAmount = order.totalAmount,
                    note = "Table: ${order.tableNumber ?: "Open"}"
                )
                dueViewModel.insertDue(due)
                Toast.makeText(requireContext(),
                    "⏰ Due recorded for $customerName — Rs. ${order.totalAmount}",
                    Toast.LENGTH_LONG).show()
            }
        )

        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrders.adapter = adapter

        orderViewModel.activeOrders.observe(viewLifecycleOwner) { orders ->
            allOrders = orders
            applyFilter()
        }

        binding.btnFilterAll.setOnClickListener { currentFilter = "all"; updateFilterButtons(); applyFilter() }
        binding.btnFilterTable.setOnClickListener { currentFilter = "table"; updateFilterButtons(); applyFilter() }
        binding.btnFilterOpen.setOnClickListener { currentFilter = "open"; updateFilterButtons(); applyFilter() }
    }

    private fun applyFilter() {
        val filtered = when (currentFilter) {
            "table" -> allOrders.filter { it.orderType == "table" }
            "open" -> allOrders.filter { it.orderType == "open" }
            else -> allOrders
        }
        adapter.submitList(filtered)
    }

    private fun updateFilterButtons() {
        val active = 0xFF533AB7.toInt()
        val inactive = 0xFF888780.toInt()
        binding.btnFilterAll.setBackgroundColor(if (currentFilter == "all") active else inactive)
        binding.btnFilterTable.setBackgroundColor(if (currentFilter == "table") active else inactive)
        binding.btnFilterOpen.setBackgroundColor(if (currentFilter == "open") active else inactive)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}