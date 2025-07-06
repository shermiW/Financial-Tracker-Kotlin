package com.example.financialtracker.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialtracker.data.PreferenceManager
import com.example.financialtracker.data.Result
import com.example.financialtracker.data.Transaction
import com.example.financialtracker.data.TransactionRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DashboardViewModel(
    private val transactionRepository: TransactionRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _totalBalance = MutableLiveData<Double>(0.0)
    val totalBalance: LiveData<Double> = _totalBalance

    private val _totalIncome = MutableLiveData<Double>(0.0)
    val totalIncome: LiveData<Double> = _totalIncome

    private val _totalExpense = MutableLiveData<Double>(0.0)
    val totalExpense: LiveData<Double> = _totalExpense

    private val _transactions = MutableLiveData<List<Transaction>>(emptyList())
    val transactions: LiveData<List<Transaction>> = _transactions

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            when (val result = transactionRepository.getTransactions()) {
                is Result.Success -> {
                    val transactions = result.data
                    _transactions.value = transactions.sortedByDescending { it.date }
                    updateTotals(transactions)
                }
                is Result.Error -> {
                    // Handle error
                }
            }
        }
    }

    private fun updateTotals(transactions: List<Transaction>) {
        val income = transactions
            .filter { it.type == Transaction.Type.INCOME }
            .sumOf { it.amount }

        val expense = transactions
            .filter { it.type == Transaction.Type.EXPENSE }
            .sumOf { it.amount }

        _totalIncome.value = income
        _totalExpense.value = expense
        _totalBalance.value = income - expense
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                preferenceManager.deleteTransaction(transaction)
                loadTransactions() // Refresh data after deletion
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Call this method when a transaction is added or updated
    fun refreshData() {
        loadTransactions()
    }
}