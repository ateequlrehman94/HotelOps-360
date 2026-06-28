package com.example.hotelmanagementapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.hotelmanagementapp.data.model.Expense

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date >= :startTime ORDER BY date DESC")
    fun getExpensesSince(startTime: Long): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date >= :startTime AND date < :endTime ORDER BY date DESC")
    fun getExpensesBetween(startTime: Long, endTime: Long): LiveData<List<Expense>>

    @Query("SELECT SUM(totalCost) FROM expenses WHERE date >= :startTime")
    fun getTotalExpensesSince(startTime: Long): LiveData<Int?>

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    fun getExpensesByCategory(category: String): LiveData<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)
}