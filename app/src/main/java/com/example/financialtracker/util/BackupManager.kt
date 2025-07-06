package com.example.financialtracker.util

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.financialtracker.data.PreferenceManager
import com.example.financialtracker.data.Transaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BackupManager(private val context: Context) {
    private val gson = Gson()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())

    data class BackupData(
        val transactions: List<Transaction>,
        val monthlyBudget: Double,
        val currency: String
    )

    fun createBackup(uri: Uri): Boolean {
        return try {
            val preferenceManager = PreferenceManager(context)
            val backupData = BackupData(
                transactions = preferenceManager.getTransactions(),
                monthlyBudget = preferenceManager.getMonthlyBudget(),
                currency = preferenceManager.getCurrency()
            )

            val json = gson.toJson(backupData)
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(json)
                }
            }
            true
        } catch (e: Exception) {
            Log.e("BackupManager", "Error creating backup: ${e.message}")
            false
        }
    }

    fun restoreBackup(uri: Uri): Boolean {
        return try {
            val json = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }
            } ?: return false

            val backupData = gson.fromJson(json, BackupData::class.java)
            val preferenceManager = PreferenceManager(context)

            preferenceManager.saveTransactions(backupData.transactions)
            preferenceManager.saveMonthlyBudget(backupData.monthlyBudget)
            preferenceManager.saveCurrency(backupData.currency)

            true
        } catch (e: Exception) {
            Log.e("BackupManager", "Error restoring backup: ${e.message}")
            false
        }
    }

    fun getDefaultBackupFileName(): String {
        return "financial_tracker_backup_${dateFormat.format(Date())}.json"
    }
} 