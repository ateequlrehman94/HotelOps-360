package com.example.hotelmanagementapp.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.hotelmanagementapp.data.repository.FirebaseMenuItem
import com.example.hotelmanagementapp.data.repository.FirebaseOrder
import com.example.hotelmanagementapp.data.repository.FirebaseOrderItem
import com.example.hotelmanagementapp.databinding.FragmentOrderBinding

class OrderFragment : Fragment() {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FirebaseOrderViewModel by viewModels()

    private val cart = mutableMapOf<String, Int>()
    private var menuItems = listOf<FirebaseMenuItem>()
    private var selectedTable = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTableButtons()
        setupOrderTypeToggle()

        viewModel.menuItems.observe(viewLifecycleOwner) { items ->
            menuItems = items
            buildMenuGrid(items)
        }

        viewModel.orderResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(),
                    "✅ Order placed successfully!", Toast.LENGTH_SHORT).show()
                cart.clear()
                buildMenuGrid(menuItems)
                updateSummary()
                binding.etNote.setText("")
                binding.etCustomerName?.setText("")
            } else {
                Toast.makeText(requireContext(),
                    "❌ Failed to place order. Check internet.", Toast.LENGTH_SHORT).show()
            }
        }

        setupButtons()
    }

    private fun setupTableButtons() {
        val tableButtons = listOf(
            binding.btnT1, binding.btnT2, binding.btnT3, binding.btnT4,
            binding.btnT5, binding.btnT6, binding.btnT7, binding.btnT8
        )
        tableButtons.forEachIndexed { index, button ->
            val tableNum = index + 1
            updateTableButtonColor(button, tableNum == selectedTable)
            button.setOnClickListener {
                selectedTable = tableNum
                tableButtons.forEachIndexed { i, btn ->
                    updateTableButtonColor(btn, i + 1 == selectedTable)
                }
            }
        }
    }

    private fun updateTableButtonColor(button: Button, isSelected: Boolean) {
        if (isSelected) {
            button.setBackgroundColor(0xFF533AB7.toInt())
            button.setTextColor(0xFFFFFFFF.toInt())
        } else {
            button.setBackgroundColor(0xFFEEEEEE.toInt())
            button.setTextColor(0xFF000000.toInt())
        }
    }

    private fun setupOrderTypeToggle() {
        binding.rbTable.setOnCheckedChangeListener { _, isChecked ->
            binding.tableLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
            binding.customerNameLayout.visibility = if (isChecked) View.GONE else View.VISIBLE
        }
    }

    private fun buildMenuGrid(items: List<FirebaseMenuItem>) {
        val grid = binding.menuGrid
        grid.removeAllViews()
        items.forEach { item ->
            val cardView = layoutInflater.inflate(
                com.example.hotelmanagementapp.R.layout.item_menu_card, grid, false)
            val tvName = cardView.findViewById<TextView>(
                com.example.hotelmanagementapp.R.id.tvItemName)
            val tvPrice = cardView.findViewById<TextView>(
                com.example.hotelmanagementapp.R.id.tvItemPrice)
            val tvQty = cardView.findViewById<TextView>(
                com.example.hotelmanagementapp.R.id.tvQty)
            val btnMinus = cardView.findViewById<Button>(
                com.example.hotelmanagementapp.R.id.btnMinus)
            val btnPlus = cardView.findViewById<Button>(
                com.example.hotelmanagementapp.R.id.btnPlus)

            tvName.text = "${item.emoji} ${item.name}"
            tvPrice.text = "Rs. ${item.price}"
            tvQty.text = (cart[item.id] ?: 0).toString()

            btnPlus.setOnClickListener {
                cart[item.id] = (cart[item.id] ?: 0) + 1
                tvQty.text = cart[item.id].toString()
                updateSummary()
            }

            btnMinus.setOnClickListener {
                val current = cart[item.id] ?: 0
                if (current > 0) {
                    cart[item.id] = current - 1
                    if (cart[item.id] == 0) cart.remove(item.id)
                    tvQty.text = (cart[item.id] ?: 0).toString()
                    updateSummary()
                }
            }

            val params = GridLayout.LayoutParams()
            params.width = 0
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            params.setMargins(4, 4, 4, 4)
            cardView.layoutParams = params
            grid.addView(cardView)
        }
    }

    private fun updateSummary() {
        val summaryLayout = binding.summaryLayout
        summaryLayout.removeAllViews()
        var total = 0

        cart.forEach { (itemId, qty) ->
            val item = menuItems.find { it.id == itemId } ?: return@forEach
            val subtotal = item.price * qty
            total += subtotal

            val row = LinearLayout(requireContext())
            row.orientation = LinearLayout.HORIZONTAL
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            params.bottomMargin = 6
            row.layoutParams = params

            val nameView = TextView(requireContext())
            nameView.text = "${item.emoji} ${item.name} x$qty"
            nameView.layoutParams = LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            nameView.textSize = 13f

            val priceView = TextView(requireContext())
            priceView.text = "Rs. $subtotal"
            priceView.textSize = 13f
            priceView.setPadding(8, 0, 8, 0)

            val removeBtn = Button(requireContext())
            removeBtn.text = "✕"
            removeBtn.textSize = 11f
            val btnParams = LinearLayout.LayoutParams(70, 60)
            removeBtn.layoutParams = btnParams
            removeBtn.setBackgroundColor(0xFFE24B4A.toInt())
            removeBtn.setTextColor(0xFFFFFFFF.toInt())
            removeBtn.setOnClickListener {
                cart.remove(itemId)
                buildMenuGrid(menuItems)
                updateSummary()
            }

            row.addView(nameView)
            row.addView(priceView)
            row.addView(removeBtn)
            summaryLayout.addView(row)
        }

        binding.tvTotal.text = "Rs. $total"
    }

    private fun setupButtons() {
        binding.btnClear.setOnClickListener {
            cart.clear()
            buildMenuGrid(menuItems)
            updateSummary()
        }

        binding.btnPlaceOrder.setOnClickListener {
            if (cart.isEmpty()) {
                Toast.makeText(requireContext(),
                    "Please select items first!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isTableOrder = binding.rbTable.isChecked
            val customerName = binding.etCustomerName?.text.toString()

            if (!isTableOrder && customerName.isEmpty()) {
                Toast.makeText(requireContext(),
                    "Please enter customer name!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var total = 0
            val orderItems = mutableListOf<FirebaseOrderItem>()

            cart.forEach { (itemId, qty) ->
                val item = menuItems.find { it.id == itemId } ?: return@forEach
                val subtotal = item.price * qty
                total += subtotal
                orderItems.add(FirebaseOrderItem(
                    menuItemName = item.name,
                    quantity = qty,
                    priceEach = item.price
                ))
            }

            val order = FirebaseOrder(
                orderType = if (isTableOrder) "table" else "open",
                tableNumber = if (isTableOrder) selectedTable else null,
                customerName = if (!isTableOrder) customerName else null,
                totalAmount = total,
                note = binding.etNote.text.toString()
            )

            viewModel.placeOrder(order, orderItems)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}