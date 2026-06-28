package com.example.hotelmanagementapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.hotelmanagementapp.R
import com.example.hotelmanagementapp.databinding.FragmentHomeBinding
import com.example.hotelmanagementapp.ui.dues.DueViewModel
import com.example.hotelmanagementapp.ui.order.OrderViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val orderViewModel: OrderViewModel by viewModels()
    private val dueViewModel: DueViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Quick stats
        orderViewModel.todayRevenue.observe(viewLifecycleOwner) { revenue ->
            binding.tvHomeSales.text = "Rs. ${revenue ?: 0}"
        }

        orderViewModel.activeOrders.observe(viewLifecycleOwner) { orders ->
            binding.tvHomeActive.text = orders.size.toString()
        }

        dueViewModel.totalDueAmount.observe(viewLifecycleOwner) { due ->
            binding.tvHomeDues.text = "Rs. ${due ?: 0}"
        }

        // Navigation cards
        binding.cardOrder.setOnClickListener {
            findNavController().navigate(R.id.orderFragment)
        }

        binding.cardKitchen.setOnClickListener {
            findNavController().navigate(R.id.kitchenFragment)
        }

        binding.cardDues.setOnClickListener {
            findNavController().navigate(R.id.duesFragment)
        }

        binding.cardExpense.setOnClickListener {
            findNavController().navigate(R.id.expenseFragment)
        }

        binding.cardInventory.setOnClickListener {
            findNavController().navigate(R.id.inventoryFragment)
        }

        binding.cardReports.setOnClickListener {
            findNavController().navigate(R.id.statsFragment)
        }

        binding.cardSettings.setOnClickListener {
            findNavController().navigate(R.id.settingsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}