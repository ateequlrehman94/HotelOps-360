package com.example.hotelmanagementapp.ui.inventory

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.hotelmanagementapp.data.model.InventoryItem
import com.example.hotelmanagementapp.databinding.FragmentInventoryBinding

class InventoryFragment : Fragment() {

    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by viewModels()
    private var inventoryItems = listOf<InventoryItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.allInventory.observe(viewLifecycleOwner) { items ->
            inventoryItems = items
            buildInventoryList(items)
        }

        viewModel.lowStockItems.observe(viewLifecycleOwner) { items ->
            binding.tvLowStock.text = if (items.isEmpty())
                "✅ All stock levels OK"
            else
                "⚠ Low Stock: ${items.joinToString(", ") { it.itemName }}"
        }

        binding.btnAddInventory.setOnClickListener {
            showAddItemDialog()
        }
    }

    private fun buildInventoryList(items: List<InventoryItem>) {
        val list = binding.inventoryList
        list.removeAllViews()

        if (items.isEmpty()) {
            val tv = TextView(requireContext())
            tv.text = "No inventory items yet"
            tv.setTextColor(0xFF888780.toInt())
            list.addView(tv)
            return
        }

        items.forEach { item ->
            val row = LinearLayout(requireContext())
            row.orientation = LinearLayout.HORIZONTAL
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            params.bottomMargin = 12
            row.layoutParams = params
            row.setPadding(0, 8, 0, 8)

            val leftLayout = LinearLayout(requireContext())
            leftLayout.orientation = LinearLayout.VERTICAL
            leftLayout.layoutParams = LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            val nameView = TextView(requireContext())
            nameView.text = "${item.itemName} (${item.category})"
            nameView.textSize = 14f
            nameView.setTextColor(0xFF2C2C2A.toInt())

            val stockView = TextView(requireContext())
            val isLow = item.minStockAlert > 0 && item.currentStock <= item.minStockAlert
            stockView.text = "Stock: ${item.currentStock} ${item.unit}"
            stockView.textSize = 12f
            stockView.setTextColor(if (isLow) 0xFFE24B4A.toInt() else 0xFF1D9E75.toInt())

            leftLayout.addView(nameView)
            leftLayout.addView(stockView)

            val btnAdd = Button(requireContext())
            btnAdd.text = "+ Add"
            btnAdd.textSize = 11f
            val btnParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            btnParams.marginStart = 8
            btnAdd.layoutParams = btnParams
            btnAdd.setBackgroundColor(0xFF1D9E75.toInt())
            btnAdd.setTextColor(0xFFFFFFFF.toInt())
            btnAdd.setOnClickListener { showUpdateStockDialog(item, true) }

            val btnReduce = Button(requireContext())
            btnReduce.text = "- Use"
            btnReduce.textSize = 11f
            btnReduce.setBackgroundColor(0xFFE24B4A.toInt())
            btnReduce.setTextColor(0xFFFFFFFF.toInt())
            btnReduce.setOnClickListener { showUpdateStockDialog(item, false) }

            row.addView(leftLayout)
            row.addView(btnAdd)
            row.addView(btnReduce)

            val divider = View(requireContext())
            val divParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1)
            divParams.topMargin = 8
            divider.layoutParams = divParams
            divider.setBackgroundColor(0xFFEEEEEE.toInt())

            val wrapper = LinearLayout(requireContext())
            wrapper.orientation = LinearLayout.VERTICAL
            wrapper.addView(row)
            wrapper.addView(divider)
            list.addView(wrapper)
        }
    }

    private fun showUpdateStockDialog(item: InventoryItem, isAdding: Boolean) {
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 20, 50, 10)

        val label = TextView(requireContext())
        label.text = if (isAdding) "Add stock quantity (${item.unit}):"
        else "Quantity used (${item.unit}):"
        layout.addView(label)

        val etQty = EditText(requireContext())
        etQty.hint = "e.g. 25"
        etQty.inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(etQty)

        AlertDialog.Builder(requireContext())
            .setTitle("${if (isAdding) "Add" else "Use"} — ${item.itemName}")
            .setView(layout)
            .setPositiveButton("Confirm") { _, _ ->
                val qty = etQty.text.toString().toDoubleOrNull() ?: return@setPositiveButton
                if (isAdding) viewModel.addStock(item.id, qty)
                else viewModel.reduceStock(item.id, qty)
                Toast.makeText(requireContext(),
                    "${if (isAdding) "Added" else "Used"} $qty ${item.unit} of ${item.itemName}",
                    Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddItemDialog() {
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 20, 50, 10)

        fun addField(hint: String): EditText {
            val label = TextView(requireContext())
            label.text = hint
            layout.addView(label)
            val et = EditText(requireContext())
            et.hint = hint
            layout.addView(et)
            return et
        }

        val etName = addField("Item Name (e.g. Chicken)")
        val etCategory = addField("Category (e.g. Meat)")
        val etStock = addField("Current Stock").also {
            it.inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                    android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        val etUnit = addField("Unit (kg / litre / piece)")
        val etAlert = addField("Low Stock Alert Level").also {
            it.inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                    android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Add Inventory Item")
            .setView(layout)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString().trim()
                if (name.isEmpty()) return@setPositiveButton
                viewModel.insertItem(
                    InventoryItem(
                        itemName = name,
                        category = etCategory.text.toString().trim().ifEmpty { "General" },
                        currentStock = etStock.text.toString().toDoubleOrNull() ?: 0.0,
                        unit = etUnit.text.toString().trim().ifEmpty { "kg" },
                        minStockAlert = etAlert.text.toString().toDoubleOrNull() ?: 0.0
                    )
                )
                Toast.makeText(requireContext(), "$name added!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}