package com.example.hotelmanagementapp.data.repository

import com.example.hotelmanagementapp.data.db.DuePaymentDao
import com.example.hotelmanagementapp.data.model.DuePayment

class DuePaymentRepository(private val dao: DuePaymentDao) {

    val activeDues = dao.getActiveDues()
    val allDues = dao.getAllDues()
    val totalDueAmount = dao.getTotalDueAmount()
    val dueCount = dao.getDueCount()

    suspend fun insertDue(due: DuePayment) = dao.insertDue(due)
    suspend fun updateDue(due: DuePayment) = dao.updateDue(due)
    suspend fun markAsPaid(id: Int) = dao.markAsPaid(id)
    suspend fun deleteDue(due: DuePayment) = dao.deleteDue(due)
}