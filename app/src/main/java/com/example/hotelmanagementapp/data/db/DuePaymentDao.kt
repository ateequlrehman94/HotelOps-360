package com.example.hotelmanagementapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.hotelmanagementapp.data.model.DuePayment

@Dao
interface DuePaymentDao {

    @Query("SELECT * FROM due_payments WHERE status = 'due' ORDER BY createdAt DESC")
    fun getActiveDues(): LiveData<List<DuePayment>>

    @Query("SELECT * FROM due_payments ORDER BY createdAt DESC")
    fun getAllDues(): LiveData<List<DuePayment>>

    @Query("SELECT SUM(dueAmount) FROM due_payments WHERE status = 'due'")
    fun getTotalDueAmount(): LiveData<Int?>

    @Query("SELECT COUNT(*) FROM due_payments WHERE status = 'due'")
    fun getDueCount(): LiveData<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDue(due: DuePayment)

    @Update
    suspend fun updateDue(due: DuePayment)

    @Query("UPDATE due_payments SET status = 'paid', paidAt = :time, paidAmount = totalAmount, dueAmount = 0 WHERE id = :id")
    suspend fun markAsPaid(id: Int, time: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteDue(due: DuePayment)
}