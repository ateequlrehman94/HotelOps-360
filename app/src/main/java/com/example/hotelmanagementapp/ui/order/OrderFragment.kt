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
import com.example.hotelmanagementapp.R
import com.example.hotelmanagementapp.data.model.MenuItem
import com.example.hotelmanagementapp.data.model.Order
import com.example.hotelmanagementapp.data.model.OrderItem
import com.example.hotelmanagementapp.databinding.FragmentOrderBinding

class OrderFragment : Fragment() {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OrderViewModel by viewModels()

    private val cart = mutableMapOf<Int, Int>()
    private var menuItems = listOf<MenuItem>()
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
        observeMenu()
        setupButtons()
    }

    private fun setupTableButtons() {
        val tableLayout = binding.tableButtonsLayout
        tableLayout.removeAllViews()
        for (i in 1..5) {
            val btn = Button(requireContext())
            btn.text = "T$i"
            btn.textSize = 12f
            val params = LinearLayout.LayoutParams(120, 80)
            params.marginEnd = 8
            btn.layoutParams = params
            if (i == selectedTable) {
                btn.setBackgroundColor(0xFF533AB7.toInt())
                btn.setTextColor(0xFFFFFFFF.toInt())
            } else {
                btn.setBackgroundColor(0xFFEEEEEE.toInt())
                btn.setTextColor(0xFF000000.toInt())
            }
            btn.setOnClickListener {
                selectedTable = i
                setupTableButtons()
            }
            tableLayout.addView(btn)
        }
    }

    private fun setupOrderTypeToggle() {
        binding.rbTable.setOnCheckedChangeListener { _, isChecked ->
            binding.tableLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
            binding.customerNameLayout.visibility = if (isChecked) View.GONE else View.VISIBLE
        }
    }

    private fun observeMenu() {
        viewModel.allMenuItems.observe(viewLifecycleOwner) { items ->
            menuItems = items
            buildMenuGrid(items)
        }
    }

    private fun buildMenuGrid(items: List<MenuItem>) {
        val grid = binding.menuGrid
        grid.removeAllViews()
        items.forEach { item ->
            val cardView = layoutInflater.inflate(R.layout.item_menu_card, grid, false)
            val tvName = cardView.findViewById<TextView>(R.id.tvItemName)
            val tvPrice = cardView.findViewById<TextView>(R.id.tvItemPrice)
            val tvQty = cardView.findViewById<TextView>(R.id.tvQty)
            val btnMinus = cardView.findViewById<Button>(R.id.btnMinus)
            val btnPlus = cardView.findViewById<Button>(R.id.btnPlus)

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

            val nameView = TextView(requireContext())
            nameView.text = "${item.name} x$qty"
            nameView.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)

            val priceView = TextView(requireContext())
            priceView.text = "Rs. $subtotal"

            row.addView(nameView)
            row.addView(priceView)
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
                Toast.makeText(requireContext(), "Please select items first!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isTableOrder = binding.rbTable.isChecked
            val customerName = binding.etCustomerName.text.toString()

            if (!isTableOrder && customerName.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter customer name!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var total = 0
            val orderItems = mutableListOf<OrderItem>()

            cart.forEach { (itemId, qty) ->
                val item = menuItems.find { it.id == itemId } ?: return@forEach
                val subtotal = item.price * qty
                total += subtotal
                orderItems.add(
                    OrderItem(
                        orderId = 0,
                        menuItemId = item.id,
                        menuItemName = item.name,
                        quantity = qty,
                        priceEach = item.price
                    )
                )
            }

            val order = Order(
                orderType = if (isTableOrder) "table" else "open",
                tableNumber = if (isTableOrder) selectedTable else null,
                customerName = if (!isTableOrder) customerName else null,
                totalAmount = total,
                note = binding.etNote.text.toString()
            )

            viewModel.placeOrder(order, orderItems)
            Toast.makeText(requireContext(), "Order placed! Total: Rs. $total", Toast.LENGTH_LONG).show()
            cart.clear()
            buildMenuGrid(menuItems)
            updateSummary()
            binding.etNote.setText("")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}