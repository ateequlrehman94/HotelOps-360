package com.example.hotelmanagementapp.ui.expense

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hotelmanagementapp.R
import com.example.hotelmanagementapp.data.model.Expense
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpenseAdapter(
    private val onDelete: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private var expenses = listOf<Expense>()

    fun submitList(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense_card, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(expenses[position])
    }

    override fun getItemCount() = expenses.size

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvExpName)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvExpCategory)
        private val tvQty: TextView = itemView.findViewById(R.id.tvExpQty)
        private val tvDate: TextView = itemView.findViewById(R.id.tvExpDate)
        private val tvCost: TextView = itemView.findViewById(R.id.tvExpCost)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDeleteExp)

        fun bind(expense: Expense) {
            tvName.text = expense.itemName
            tvCategory.text = expense.category
            tvCost.text = "Rs. ${expense.totalCost}"

            if (expense.quantity > 0) {
                tvQty.text = "Qty: ${expense.quantity} ${expense.unit}" +
                        if (expense.pricePerUnit > 0) " @ Rs.${expense.pricePerUnit}/${expense.unit}" else ""
            } else {
                tvQty.text = if (expense.supplier.isNotEmpty()) "From: ${expense.supplier}" else ""
            }

            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            tvDate.text = sdf.format(Date(expense.date))

            btnDelete.setOnClickListener { onDelete(expense) }
        }
    }
}