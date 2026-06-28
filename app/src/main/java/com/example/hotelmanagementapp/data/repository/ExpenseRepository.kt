package com.example.hotelmanagementapp.data.repository

import androidx.lifecycle.LiveData
import com.example.hotelmanagementapp.data.db.ExpenseDao
import com.example.hotelmanagementapp.data.model.Expense

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    val allExpenses: LiveData<List<Expense>> = expenseDao.getAllExpenses()

    fun getExpensesSince(startTime: Long) = expenseDao.getExpensesSince(startTime)

    fun getExpensesBetween(startTime: Long, endTime: Long) =
        expenseDao.getExpensesBetween(startTime, endTime)

    fun getTotalExpensesSince(startTime: Long) =
        expenseDao.getTotalExpensesSince(startTime)

    fun getExpensesByCategory(category: String) =
        expenseDao.getExpensesByCategory(category)

    suspend fun insertExpense(expense: Expense) = expenseDao.insertExpense(expense)
    suspend fun updateExpense(expense: Expense) = expenseDao.updateExpense(expense)
    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)
}