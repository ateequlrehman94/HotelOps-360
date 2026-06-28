package com.example.hotelmanagementapp.ui.expense

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hotelmanagementapp.data.model.Expense
import com.example.hotelmanagementapp.databinding.FragmentExpenseBinding
import java.util.Calendar

class ExpenseFragment : Fragment() {

    private var _binding: FragmentExpenseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExpenseViewModel by viewModels()
    private lateinit var adapter: ExpenseAdapter
    private var currentLiveData: LiveData<List<Expense>>? = null
    private var currentObserver: androidx.lifecycle.Observer<List<Expense>>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ExpenseAdapter(
            onDelete = { expense -> viewModel.deleteExpense(expense) }
        )

        binding.rvExpenses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExpenses.adapter = adapter

        loadToday()

        binding.btnExpToday.setOnClickListener { loadToday(); updateFilters("today") }
        binding.btnExpWeek.setOnClickListener { loadWeek(); updateFilters("week") }
        binding.btnExpMonth.setOnClickListener { loadMonth(); updateFilters("month") }

        binding.btnAddExpense.setOnClickListener { showAddExpenseDialog() }
    }

    private fun loadToday() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        observeExpenses(viewModel.getExpensesSince(cal.timeInMillis))
        observeTotal(viewModel.getTotalExpensesSince(cal.timeInMillis))
    }

    private fun loadWeek() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        observeExpenses(viewModel.getExpensesSince(cal.timeInMillis))
        observeTotal(viewModel.getTotalExpensesSince(cal.timeInMillis))
    }

    private fun loadMonth() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        observeExpenses(viewModel.getExpensesSince(cal.timeInMillis))
        observeTotal(viewModel.getTotalExpensesSince(cal.timeInMillis))
    }

    private fun observeExpenses(liveData: LiveData<List<Expense>>) {
        currentObserver?.let { currentLiveData?.removeObserver(it) }
        currentObserver = androidx.lifecycle.Observer { expenses ->
            adapter.submitList(expenses)
            binding.tvExpenseCount.text = expenses.size.toString()
        }
        currentLiveData = liveData
        liveData.observe(viewLifecycleOwner, currentObserver!!)
    }

    private fun observeTotal(liveData: LiveData<Int?>) {
        liveData.observe(viewLifecycleOwner) { total ->
            binding.tvTotalExpense.text = "Rs. ${total ?: 0}"
        }
    }

    private fun showAddExpenseDialog() {
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 20, 50, 10)

        val categories = arrayOf("Meat", "Grocery", "Flour", "Vegetables", "Fuel", "Salary", "Rent", "Other")

        val tvCatLabel = TextView(requireContext())
        tvCatLabel.text = "Category:"
        layout.addView(tvCatLabel)

        val spinner = Spinner(requireContext())
        val spinnerAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, categories)
        spinner.adapter = spinnerAdapter
        layout.addView(spinner)

        fun addField(hint: String, inputType: Int = android.text.InputType.TYPE_CLASS_TEXT): EditText {
            val label = TextView(requireContext())
            label.text = hint
            layout.addView(label)
            val et = EditText(requireContext())
            et.hint = hint
            et.inputType = inputType
            layout.addView(et)
            return et
        }

        val etName = addField("Item Name (e.g. Chicken)")
        val etQty = addField("Quantity (e.g. 25)",
            android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL)
        val etUnit = addField("Unit (kg / litre / piece)")
        val etPrice = addField("Price per unit (optional)",
            android.text.InputType.TYPE_CLASS_NUMBER)
        val etTotal = addField("Total Cost (Rs.)", android.text.InputType.TYPE_CLASS_NUMBER)
        val etSupplier = addField("Supplier (optional)")
        val etNote = addField("Note (optional)")

        AlertDialog.Builder(requireContext())
            .setTitle("Add Expense / Purchase")
            .setView(layout)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString().trim()
                val total = etTotal.text.toString().toIntOrNull() ?: 0
                if (name.isEmpty() || total <= 0) {
                    Toast.makeText(requireContext(),
                        "Name and total cost are required!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val expense = Expense(
                    category = spinner.selectedItem.toString(),
                    itemName = name,
                    quantity = etQty.text.toString().toDoubleOrNull() ?: 0.0,
                    unit = etUnit.text.toString().trim(),
                    totalCost = total,
                    pricePerUnit = etPrice.text.toString().toIntOrNull() ?: 0,
                    supplier = etSupplier.text.toString().trim(),
                    note = etNote.text.toString().trim()
                )
                viewModel.insertExpense(expense)
                Toast.makeText(requireContext(),
                    "✅ Expense added: Rs. $total", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateFilters(active: String) {
        val a = 0xFF0F6E56.toInt()
        val i = 0xFF888780.toInt()
        binding.btnExpToday.setBackgroundColor(if (active == "today") a else i)
        binding.btnExpWeek.setBackgroundColor(if (active == "week") a else i)
        binding.btnExpMonth.setBackgroundColor(if (active == "month") a else i)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}