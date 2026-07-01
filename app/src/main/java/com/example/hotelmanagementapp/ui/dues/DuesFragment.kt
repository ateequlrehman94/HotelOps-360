package com.example.hotelmanagementapp.ui.dues

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hotelmanagementapp.data.repository.FirebaseDue
import com.example.hotelmanagementapp.databinding.FragmentDuesBinding

class DuesFragment : Fragment() {

    private var _binding: FragmentDuesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FirebaseDueViewModel by viewModels()
    private lateinit var adapter: FirebaseDuesAdapter
    private var showAll = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDuesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FirebaseDuesAdapter(
            onMarkPaid = { due ->
                viewModel.markAsPaid(due.id)
                Toast.makeText(requireContext(),
                    "✅ ${due.customerName} paid Rs. ${due.totalAmount}",
                    Toast.LENGTH_SHORT).show()
            },
            onDelete = { due -> viewModel.deleteDue(due.id) }
        )

        binding.rvDues.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDues.adapter = adapter

        viewModel.totalDueAmount.observe(viewLifecycleOwner) { amount ->
            binding.tvTotalDue.text = "Total Due: Rs. ${amount}"
        }

        viewModel.dueCount.observe(viewLifecycleOwner) { count ->
            binding.tvDueCount.text = "$count pending"
        }

        viewModel.activeDues.observe(viewLifecycleOwner) { dues ->
            if (!showAll) adapter.submitList(dues)
        }

        viewModel.allDues.observe(viewLifecycleOwner) { dues ->
            if (showAll) adapter.submitList(dues)
        }

        binding.btnShowPending.setOnClickListener {
            showAll = false
            updateFilterButtons()
            viewModel.activeDues.value?.let { adapter.submitList(it) }
        }

        binding.btnShowAll.setOnClickListener {
            showAll = true
            updateFilterButtons()
            viewModel.allDues.value?.let { adapter.submitList(it) }
        }
    }

    private fun updateFilterButtons() {
        val active = 0xFF854F0B.toInt()
        val inactive = 0xFF888780.toInt()
        binding.btnShowPending.setBackgroundColor(if (!showAll) active else inactive)
        binding.btnShowAll.setBackgroundColor(if (showAll) active else inactive)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}