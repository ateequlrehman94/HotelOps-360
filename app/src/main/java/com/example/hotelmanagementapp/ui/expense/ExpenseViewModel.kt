package com.example.hotelmanagementapp.ui.expense

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.hotelmanagementapp.data.db.HotelDatabase
import com.example.hotelmanagementapp.data.model.Expense
import com.example.hotelmanagementapp.data.repository.ExpenseRepository
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExpenseRepository(
        HotelDatabase.getDatabase(application).expenseDao()
    )

    val allExpenses = repository.allExpenses

    fun getExpensesSince(startTime: Long) = repository.getExpensesSince(startTime)

    fun getExpensesBetween(startTime: Long, endTime: Long) =
        repository.getExpensesBetween(startTime, endTime)

    fun getTotalExpensesSince(startTime: Long): LiveData<Int?> =
        repository.getTotalExpensesSince(startTime)

    fun insertExpense(expense: Expense) = viewModelScope.launch {
        repository.insertExpense(expense)
    }

    fun deleteExpense(expense: Expense) = viewModelScope.launch {
        repository.deleteExpense(expense)
    }
}