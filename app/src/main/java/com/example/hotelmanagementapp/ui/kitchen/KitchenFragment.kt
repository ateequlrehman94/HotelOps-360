package com.example.hotelmanagementapp.ui.kitchen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hotelmanagementapp.data.repository.FirebaseDueRepository
import com.example.hotelmanagementapp.data.repository.FirebaseOrder
import com.example.hotelmanagementapp.databinding.FragmentKitchenBinding
import com.example.hotelmanagementapp.ui.order.FirebaseOrderViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class KitchenFragment : Fragment() {

    private var _binding: FragmentKitchenBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FirebaseOrderViewModel by viewModels()
    private lateinit var adapter: FirebaseKitchenAdapter
    private var allOrders = listOf<FirebaseOrder>()
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

        adapter = FirebaseKitchenAdapter(
            onReady = { order ->
                viewModel.updateOrderStatus(order.id, "ready")
            },
            onPaid = { order ->
                viewModel.updateOrderStatus(order.id, "paid")
                Toast.makeText(requireContext(),
                    "✅ Payment received! Rs. ${order.totalAmount}",
                    Toast.LENGTH_SHORT).show()
            },
            onCancel = { order ->
                viewModel.deleteOrder(order.id)
            },
            onDuePayment = { order, customerName, phone ->
                viewModel.updateOrderStatus(order.id, "paid")
                CoroutineScope(Dispatchers.IO).launch {
                    FirebaseDueRepository().addDue(
                        orderId = order.id,
                        customerName = customerName,
                        customerPhone = phone,
                        totalAmount = order.totalAmount,
                        note = "Table: ${order.tableNumber ?: "Open"}"
                    )
                }
                Toast.makeText(requireContext(),
                    "⏰ Due recorded for $customerName — Rs. ${order.totalAmount}",
                    Toast.LENGTH_LONG).show()
            }
        )

        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrders.adapter = adapter

        viewModel.activeOrders.observe(viewLifecycleOwner) { orders ->
            allOrders = orders
            applyFilter()
        }

        binding.btnFilterAll.setOnClickListener {
            currentFilter = "all"; updateFilterButtons(); applyFilter()
        }
        binding.btnFilterTable.setOnClickListener {
            currentFilter = "table"; updateFilterButtons(); applyFilter()
        }
        binding.btnFilterOpen.setOnClickListener {
            currentFilter = "open"; updateFilterButtons(); applyFilter()
        }
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
        binding.btnFilterAll.setBackgroundColor(
            if (currentFilter == "all") active else inactive)
        binding.btnFilterTable.setBackgroundColor(
            if (currentFilter == "table") active else inactive)
        binding.btnFilterOpen.setBackgroundColor(
            if (currentFilter == "open") active else inactive)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}