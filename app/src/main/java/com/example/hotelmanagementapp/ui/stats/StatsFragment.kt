package com.example.hotelmanagementapp.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.example.hotelmanagementapp.data.model.Order
import com.example.hotelmanagementapp.databinding.FragmentStatsBinding
import com.example.hotelmanagementapp.ui.dues.DueViewModel
import com.example.hotelmanagementapp.ui.expense.ExpenseViewModel
import com.example.hotelmanagementapp.ui.order.OrderViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OrderViewModel by viewModels()
    private val expenseViewModel: ExpenseViewModel by viewModels()
    private val dueViewModel: DueViewModel by viewModels()

    private var currentObserver: androidx.lifecycle.Observer<List<Order>>? = null
    private var currentLiveData: LiveData<List<Order>>? = null
    private var currentStartTime: Long = 0L

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
            loadToday()
            updateFilterButtons("today")
        }
        binding.btnYesterday.setOnClickListener {
            loadYesterday()
            updateFilterButtons("yesterday")
        }
        binding.btnWeekly.setOnClickListener {
            loadThisWeek()
            updateFilterButtons("week")
        }
        binding.btnMonthly.setOnClickListener {
            loadThisMonth()
            updateFilterButtons("month")
        }

        // Observe pending dues
        dueViewModel.totalDueAmount.observe(viewLifecycleOwner) { due ->
            binding.tvPendingDues.text = "⏰ Pending Dues: Rs. ${due ?: 0}"
        }
    }

    private fun loadToday() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        currentStartTime = cal.timeInMillis
        binding.tvDateLabel.text = "Today — ${formatDate(Date())}"
        observeOrders(viewModel.getPaidOrdersSince(currentStartTime))
        updateProfitLoss(currentStartTime)
    }

    private fun loadYesterday() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val endOfYesterday = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val startOfYesterday = cal.timeInMillis
        currentStartTime = startOfYesterday
        binding.tvDateLabel.text = "Yesterday — ${formatDate(Date(startOfYesterday))}"
        observeOrders(viewModel.getPaidOrdersBetween(startOfYesterday, endOfYesterday))
        updateProfitLoss(startOfYesterday)
    }

    private fun loadThisWeek() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        currentStartTime = cal.timeInMillis
        binding.tvDateLabel.text = "This Week — from ${formatDate(Date(currentStartTime))}"
        observeOrders(viewModel.getPaidOrdersSince(currentStartTime))
        updateProfitLoss(currentStartTime)
    }

    private fun loadThisMonth() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        currentStartTime = cal.timeInMillis
        val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
        binding.tvDateLabel.text = "This Month — $monthName"
        observeOrders(viewModel.getPaidOrdersSince(currentStartTime))
        updateProfitLoss(currentStartTime)
    }

    private fun updateProfitLoss(startTime: Long) {
        viewModel.getPaidOrdersSince(startTime).observe(viewLifecycleOwner) { orders ->
            val sales = orders.sumOf { it.totalAmount }
            binding.tvPLSales.text = "Rs. $sales"

            expenseViewModel.getTotalExpensesSince(startTime).observe(viewLifecycleOwner) { exp ->
                val expenses = exp ?: 0
                binding.tvPLExpense.text = "Rs. $expenses"
                val net = sales - expenses
                binding.tvPLNet.text = "Rs. $net"
                binding.tvPLNet.setTextColor(
                    if (net >= 0) 0xFF1D9E75.toInt() else 0xFFE24B4A.toInt()
                )
            }
        }
    }

    private fun observeOrders(liveData: LiveData<List<Order>>) {
        currentObserver?.let { currentLiveData?.removeObserver(it) }
        currentObserver = androidx.lifecycle.Observer { orders ->
            updateStats(orders)
            updateOrdersList(orders)
        }
        currentLiveData = liveData
        liveData.observe(viewLifecycleOwner, currentObserver!!)
    }

    private fun updateStats(orders: List<Order>) {
        val total = orders.sumOf { it.totalAmount }
        val count = orders.size
        val avg = if (count > 0) total / count else 0
        val highest = orders.maxOfOrNull { it.totalAmount } ?: 0
        binding.tvRevenue.text = "Rs. $total"
        binding.tvOrderCount.text = count.toString()
        binding.tvAvgOrder.text = "Rs. $avg"
        binding.tvHighest.text = "Rs. $highest"
    }

    private fun updateOrdersList(orders: List<Order>) {
        val list = binding.ordersList
        list.removeAllViews()
        if (orders.isEmpty()) {
            val tv = TextView(requireContext())
            tv.text = "No orders found for this period"
            tv.setTextColor(0xFF888780.toInt())
            tv.setPadding(0, 8, 0, 8)
            list.addView(tv)
            return
        }
        val sdf = SimpleDateFormat("dd MMM  hh:mm a", Locale.getDefault())
        orders.forEach { order ->
            val row = LinearLayout(requireContext())
            row.orientation = LinearLayout.HORIZONTAL
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = 10
            row.layoutParams = params

            val leftLayout = LinearLayout(requireContext())
            leftLayout.orientation = LinearLayout.VERTICAL
            leftLayout.layoutParams = LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            val label = TextView(requireContext())
            label.text = if (order.orderType == "table")
                "🪑 Table ${order.tableNumber}"
            else "🛍 ${order.customerName ?: "Open Order"}"
            label.textSize = 13f

            val time = TextView(requireContext())
            time.text = sdf.format(Date(order.createdAt))
            time.textSize = 11f
            time.setTextColor(0xFF888780.toInt())

            leftLayout.addView(label)
            leftLayout.addView(time)

            val amount = TextView(requireContext())
            amount.text = "Rs. ${order.totalAmount}"
            amount.textSize = 14f
            amount.setTextColor(0xFF533AB7.toInt())
            amount.typeface = android.graphics.Typeface.DEFAULT_BOLD

            row.addView(leftLayout)
            row.addView(amount)

            val divider = View(requireContext())
            val divParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1)
            divParams.topMargin = 6
            divider.layoutParams = divParams
            divider.setBackgroundColor(0xFFEEEEEE.toInt())

            val wrapper = LinearLayout(requireContext())
            wrapper.orientation = LinearLayout.VERTICAL
            wrapper.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            wrapper.addView(row)
            wrapper.addView(divider)
            list.addView(wrapper)
        }
    }

    private fun updateFilterButtons(active: String) {
        val activeColor = 0xFF533AB7.toInt()
        val inactiveColor = 0xFF888780.toInt()
        binding.btnToday.setBackgroundColor(if (active == "today") activeColor else inactiveColor)
        binding.btnYesterday.setBackgroundColor(if (active == "yesterday") activeColor else inactiveColor)
        binding.btnWeekly.setBackgroundColor(if (active == "week") activeColor else inactiveColor)
        binding.btnMonthly.setBackgroundColor(if (active == "month") activeColor else inactiveColor)
    }

    private fun formatDate(date: Date): String {
        return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}