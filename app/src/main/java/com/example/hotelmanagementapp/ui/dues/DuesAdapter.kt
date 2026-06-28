package com.example.hotelmanagementapp.ui.dues

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hotelmanagementapp.R
import com.example.hotelmanagementapp.data.model.DuePayment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DuesAdapter(
    private val onMarkPaid: (DuePayment) -> Unit,
    private val onDelete: (DuePayment) -> Unit
) : RecyclerView.Adapter<DuesAdapter.DueViewHolder>() {

    private var dues = listOf<DuePayment>()

    fun submitList(newDues: List<DuePayment>) {
        dues = newDues
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DueViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_due_card, parent, false)
        return DueViewHolder(view)
    }

    override fun onBindViewHolder(holder: DueViewHolder, position: Int) {
        holder.bind(dues[position])
    }

    override fun getItemCount() = dues.size

    inner class DueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvCustomerName)
        private val tvPhone: TextView = itemView.findViewById(R.id.tvPhone)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvDueStatus)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvDueAmount)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDueDate)
        private val tvNote: TextView = itemView.findViewById(R.id.tvDueNote)
        private val btnMarkPaid: Button = itemView.findViewById(R.id.btnMarkPaid)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDeleteDue)
        private val btnLayout: LinearLayout = itemView.findViewById(R.id.btnLayout)

        fun bind(due: DuePayment) {
            tvName.text = "👤 ${due.customerName}"
            tvPhone.text = if (due.customerPhone.isNotEmpty()) "📞 ${due.customerPhone}" else "No phone"
            tvAmount.text = "Due: Rs. ${due.dueAmount}"
            tvNote.text = due.note

            val sdf = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault())
            tvDate.text = sdf.format(Date(due.createdAt))

            if (due.status == "paid") {
                tvStatus.text = "✅ Paid"
                tvStatus.setBackgroundColor(0xFF3B6D11.toInt())
                tvAmount.text = "Paid: Rs. ${due.totalAmount}"
                tvAmount.setTextColor(0xFF3B6D11.toInt())
                btnLayout.visibility = View.GONE
            } else {
                tvStatus.text = "⏰ Due"
                tvStatus.setBackgroundColor(0xFF854F0B.toInt())
                tvAmount.setTextColor(0xFF854F0B.toInt())
                btnLayout.visibility = View.VISIBLE
                btnMarkPaid.setOnClickListener { onMarkPaid(due) }
                btnDelete.setOnClickListener { onDelete(due) }
            }
        }
    }
}