package com.example.financialtracker.ui.budget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.financialtracker.data.PreferenceManager

class BudgetViewModel(private val preferenceManager: PreferenceManager) : ViewModel() {
    private val _monthlyBudget = MutableLiveData<Double>(0.0)
    val monthlyBudget: LiveData<Double> = _monthlyBudget

    private val _monthlyExpenses = MutableLiveData<Double>(0.0)
    val monthlyExpenses: LiveData<Double> = _monthlyExpenses

    init {
        loadBudget()
        loadExpenses()
    }

    private fun loadBudget() {
        try {
            _monthlyBudget.value = preferenceManager.getMonthlyBudget()
        } catch (e: Exception) {
            e.printStackTrace()
            _monthlyBudget.value = 0.0
        }
    }

    private fun loadExpenses() {
        try {
            _monthlyExpenses.value = preferenceManager.getMonthlyExpenses()
        } catch (e: Exception) {
            e.printStackTrace()
            _monthlyExpenses.value = 0.0
        }
    }

    fun updateBudget(newBudget: Double) {
        try {
            preferenceManager.saveMonthlyBudget(newBudget)
            _monthlyBudget.value = newBudget
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getMonthlyExpenses(): Double {
        return try {
            preferenceManager.getMonthlyExpenses()
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }

    class Factory(private val preferenceManager: PreferenceManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BudgetViewModel(preferenceManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}