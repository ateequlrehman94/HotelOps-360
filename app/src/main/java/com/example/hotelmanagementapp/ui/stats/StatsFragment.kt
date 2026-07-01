package com.example.hotelmanagementapp.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.hotelmanagementapp.data.repository.FirebaseOrder
import com.example.hotelmanagementapp.databinding.FragmentStatsBinding
import com.example.hotelmanagementapp.ui.order.FirebaseOrderViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FirebaseOrderViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadToday()

        binding.btnToday.setOnClickListener {
            loadToday(); updateFilterButtons("today")
        }
        binding.btnYesterday.setOnClickListener {
            loadYesterday(); updateFilterButtons("yesterday")
        }
        binding.btnWeekly.setOnClickListener {
            loadThisWeek(); updateFilterButtons("week")
        }
        binding.btnMonthly.setOnClickListener {
            loadThisMonth(); updateFilterButtons("month")
        }
    }

    private fun loadToday() {
        val cal = startOfDay(Calendar.getInstance())
        binding.tvDateLabel.text = "Today — ${formatDate(Date())}"
        loadOrders(cal.timeInMillis)
        updateFilterButtons("today")
    }

    private fun loadYesterday() {
        val cal = startOfDay(Calendar.getInstance())
        val end = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val start = cal.timeInMillis
        binding.tvDateLabel.text = "Yesterday — ${formatDate(Date(start))}"
        loadOrders(start, end)
        updateFilterButtons("yesterday")
    }

    private fun loadThisWeek() {
        val cal = startOfDay(Calendar.getInstance())
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        val start = cal.timeInMillis
        binding.tvDateLabel.text = "This Week — from ${formatDate(Date(start))}"
        loadOrders(start)
        updateFilterButtons("week")
    }

    private fun loadThisMonth() {
        val cal = startOfDay(Calendar.getInstance())
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val start = cal.timeInMillis
        val month = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
        binding.tvDateLabel.text = "This Month — $month"
        loadOrders(start)
        updateFilterButtons("month")
    }

    private fun loadOrders(startTime: Long, endTime: Long? = null) {
        viewModel.getPaidOrdersSince(startTime) { orders ->
            val filtered = if (endTime != null)
                orders.filter { it.createdAt < endTime }
            else orders
            activity?.runOnUiThread {
                updateStats(filtered)
                updateOrdersList(filtered)
                updateProfitLoss(filtered)
            }
        }
    }

    private fun updateStats(orders: List<FirebaseOrder>) {
        val total = orders.sumOf { it.totalAmount }
        val count = orders.size
        val avg = if (count > 0) total / count else 0
        val highest = orders.maxOfOrNull { it.totalAmount } ?: 0
        binding.tvRevenue.text = "Rs. $total"
        binding.tvOrderCount.text = count.toString()
        binding.tvAvgOrder.text = "Rs. $avg"
        binding.tvHighest.text = "Rs. $highest"
        binding.tvPLSales.text = "Rs. $total"
    }

    private fun updateProfitLoss(orders: List<FirebaseOrder>) {
        val sales = orders.sumOf { it.totalAmount }
        binding.tvPLSales.text = "Rs. $sales"
        binding.tvPLExpense.text = "Rs. 0"
        val net = sales
        binding.tvPLNet.text = "Rs. $net"
        binding.tvPLNet.setTextColor(0xFF1D9E75.toInt())
    }

    private fun updateOrdersList(orders: List<FirebaseOrder>) {
        val list = binding.ordersList
        list.removeAllViews()
        if (orders.isEmpty()) {
            val tv = TextView(requireContext())
            tv.text = "No orders found for this period"
            tv.setTextColor(0xFF888780.toInt())
            list.addView(tv)
            return
        }
        val sdf = SimpleDateFormat("dd MMM  hh:mm a", Locale.getDefault())
        orders.forEach { order ->
            val row = LinearLayout(requireContext())
            row.orientation = LinearLayout.HORIZONTAL
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            params.bottomMargin = 10
            row.layoutParams = params

            val left = LinearLayout(requireContext())
            left.orientation = LinearLayout.VERTICAL
            left.layoutParams = LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            val label = TextView(requireContext())
            label.text = if (order.orderType == "table")
                "🪑 Table ${order.tableNumber}"
            else "🛍 ${order.customerName ?: "Open"}"
            label.textSize = 13f

            val waiter = TextView(requireContext())
            waiter.text = "👤 ${order.waiterName}  ${sdf.format(Date(order.createdAt))}"
            waiter.textSize = 11f
            waiter.setTextColor(0xFF888780.toInt())

            left.addView(label)
            left.addView(waiter)

            val amount = TextView(requireContext())
            amount.text = "Rs. ${order.totalAmount}"
            amount.textSize = 14f
            amount.setTextColor(0xFF533AB7.toInt())
            amount.typeface = android.graphics.Typeface.DEFAULT_BOLD

            row.addView(left)
            row.addView(amount)
            list.addView(row)

            val divider = View(requireContext())
            val dp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1)
            dp.topMargin = 6
            divider.layoutParams = dp
            divider.setBackgroundColor(0xFFEEEEEE.toInt())
            list.addView(divider)
        }
    }

    private fun startOfDay(cal: Calendar): Calendar {
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal
    }

    private fun formatDate(date: Date) =
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)

    private fun updateFilterButtons(active: String) {
        val ac = 0xFF533AB7.toInt()
        val ic = 0xFF888780.toInt()
        binding.btnToday.setBackgroundColor(if (active == "today") ac else ic)
        binding.btnYesterday.setBackgroundColor(if (active == "yesterday") ac else ic)
        binding.btnWeekly.setBackgroundColor(if (active == "week") ac else ic)
        binding.btnMonthly.setBackgroundColor(if (active == "month") ac else ic)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}