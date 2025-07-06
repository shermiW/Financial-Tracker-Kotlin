package com.example.financialtracker.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.financialtracker.data.PreferenceManager
import com.example.financialtracker.data.TransactionRepository

class DashboardViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val preferenceManager: PreferenceManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(transactionRepository, preferenceManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}