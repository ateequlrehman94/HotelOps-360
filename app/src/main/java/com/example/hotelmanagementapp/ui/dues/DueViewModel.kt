package com.example.hotelmanagementapp.ui.dues

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelmanagementapp.data.db.HotelDatabase
import com.example.hotelmanagementapp.data.model.DuePayment
import com.example.hotelmanagementapp.data.repository.DuePaymentRepository
import kotlinx.coroutines.launch

class DueViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DuePaymentRepository(
        HotelDatabase.getDatabase(application).duePaymentDao()
    )

    val activeDues = repository.activeDues
    val allDues = repository.allDues
    val totalDueAmount = repository.totalDueAmount
    val dueCount = repository.dueCount

    fun insertDue(due: DuePayment) = viewModelScope.launch {
        repository.insertDue(due)
    }

    fun markAsPaid(id: Int) = viewModelScope.launch {
        repository.markAsPaid(id)
    }

    fun deleteDue(due: DuePayment) = viewModelScope.launch {
        repository.deleteDue(due)
    }
}