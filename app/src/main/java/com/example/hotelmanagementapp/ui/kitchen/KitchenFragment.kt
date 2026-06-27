package com.example.hotelmanagementapp.ui.kitchen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hotelmanagementapp.data.model.Order
import com.example.hotelmanagementapp.databinding.FragmentKitchenBinding
import com.example.hotelmanagementapp.ui.order.OrderViewModel

class KitchenFragment : Fragment() {

    private var _binding: FragmentKitchenBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OrderViewModel by viewModels()
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
            onReady = { order ->
                viewModel.updateOrderStatus(order.id, "ready")
            },
            onPaid = { order ->
                viewModel.updateOrderStatus(order.id, "paid")
            },
            onCancel = { order ->
                viewModel.deleteOrder(order)
            }
        )

        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrders.adapter = adapter

        viewModel.activeOrders.observe(viewLifecycleOwner) { orders ->
            allOrders = orders
            applyFilter()
        }

        binding.btnFilterAll.setOnClickListener {
            currentFilter = "all"
            updateFilterButtons()
            applyFilter()
        }

        binding.btnFilterTable.setOnClickListener {
            currentFilter = "table"
            updateFilterButtons()
            applyFilter()
        }

        binding.btnFilterOpen.setOnClickListener {
            currentFilter = "open"
            updateFilterButtons()
            applyFilter()
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
        val activeColor = 0xFF533AB7.toInt()
        val inactiveColor = 0xFF888780.toInt()
        binding.btnFilterAll.setBackgroundColor(if (currentFilter == "all") activeColor else inactiveColor)
        binding.btnFilterTable.setBackgroundColor(if (currentFilter == "table") activeColor else inactiveColor)
        binding.btnFilterOpen.setBackgroundColor(if (currentFilter == "open") activeColor else inactiveColor)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}