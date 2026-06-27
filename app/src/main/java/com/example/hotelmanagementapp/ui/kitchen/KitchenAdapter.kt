package com.example.hotelmanagementapp.ui.kitchen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hotelmanagementapp.R
import com.example.hotelmanagementapp.data.model.Order

class KitchenAdapter(
    private val onReady: (Order) -> Unit,
    private val onPaid: (Order) -> Unit,
    private val onCancel: (Order) -> Unit
) : RecyclerView.Adapter<KitchenAdapter.OrderViewHolder>() {

    private var orders = listOf<Order>()

    fun submitList(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_card, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount() = orders.size

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvOrderLabel: TextView = itemView.findViewById(R.id.tvOrderLabel)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val tvItems: TextView = itemView.findViewById(R.id.tvItems)
        private val tvTotal: TextView = itemView.findViewById(R.id.tvTotal)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val btnReady: Button = itemView.findViewById(R.id.btnReady)
        private val btnPaid: Button = itemView.findViewById(R.id.btnPaid)
        private val btnCancel: Button = itemView.findViewById(R.id.btnCancel)

        fun bind(order: Order) {
            tvOrderLabel.text = if (order.orderType == "table")
                "Table ${order.tableNumber}" else order.customerName ?: "Open Order"

            tvStatus.text = when (order.status) {
                "pending" -> "Pending"
                "ready" -> "Ready"
                else -> order.status
            }
            tvStatus.setBackgroundColor(
                if (order.status == "pending") 0xFFBA7517.toInt()
                else 0xFF3B6D11.toInt()
            )

            tvTotal.text = "Rs. ${order.totalAmount}"

            val mins = ((System.currentTimeMillis() - order.createdAt) / 60000).toInt()
            tvTime.text = "${mins}m ago"

            if (order.note.isNotEmpty()) {
                tvItems.text = "Note: ${order.note}"
            } else {
                tvItems.text = if (order.orderType == "table") "Table Order" else "Open Order"
            }

            btnReady.visibility = if (order.status == "pending") View.VISIBLE else View.GONE
            btnReady.setOnClickListener { onReady(order) }
            btnPaid.setOnClickListener { onPaid(order) }
            btnCancel.setOnClickListener { onCancel(order) }
        }
    }
}