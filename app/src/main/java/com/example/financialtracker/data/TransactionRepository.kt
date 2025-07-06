package com.example.financialtracker.data

import com.example.financialtracker.data.PreferenceManager
import com.example.financialtracker.data.Transaction
import com.example.financialtracker.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class TransactionRepository(private val preferenceManager: PreferenceManager) {

    suspend fun getTransactions(): Result<List<Transaction>> = withContext(Dispatchers.IO) {
        try {
            val transactions = preferenceManager.getTransactions()
            Result.Success(transactions)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun addTransaction(transaction: Transaction): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val currentTransactions = preferenceManager.getTransactions().toMutableList()
            currentTransactions.add(transaction)
            preferenceManager.saveTransactions(currentTransactions)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun updateTransaction(updatedTransaction: Transaction): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val currentTransactions = preferenceManager.getTransactions().toMutableList()
            val index = currentTransactions.indexOfFirst { it.id == updatedTransaction.id }
            if (index != -1) {
                currentTransactions[index] = updatedTransaction
                preferenceManager.saveTransactions(currentTransactions)
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Transaction not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun deleteTransaction(transactionId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val currentTransactions = preferenceManager.getTransactions().toMutableList()
            currentTransactions.removeIf { it.id == transactionId }
            preferenceManager.saveTransactions(currentTransactions)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
