package com.example.financialtracker.ui.budget

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.financialtracker.R
import com.example.financialtracker.data.PreferenceManager
import com.example.financialtracker.databinding.FragmentBudgetBinding
import com.example.financialtracker.ui.passcode.PasscodeActivity
import com.example.financialtracker.util.NotificationHelper
import java.text.NumberFormat
import java.util.Locale

class BudgetFragment : Fragment() {
    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var viewModel: BudgetViewModel
    private lateinit var notificationHelper: NotificationHelper
    private val numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {
            _binding = FragmentBudgetBinding.inflate(inflater, container, false)
            preferenceManager = PreferenceManager(requireContext())
            viewModel = ViewModelProvider(
                this,
                BudgetViewModel.Factory(preferenceManager)
            )[BudgetViewModel::class.java]
            notificationHelper = NotificationHelper(requireContext())

            setupUI()
            setupClickListeners()
            observeViewModel()

            return binding.root
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            updateBudgetProgress()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupUI() {
        // Set initial budget value if exists
        val currentBudget = preferenceManager.getMonthlyBudget()
        if (currentBudget > 0) {
            binding.etMonthlyBudget.setText(currentBudget.toString())
        }
    }

    private fun setupClickListeners() {
        binding.btnSaveBudget.setOnClickListener {
            saveBudget()
        }

        binding.btnLogout.setOnClickListener {
            navigateToPasscode()
        }
    }

    private fun observeViewModel() {
        viewModel.monthlyBudget.observe(viewLifecycleOwner) { budget ->
            updateBudgetDisplay(budget)
        }

        viewModel.monthlyExpenses.observe(viewLifecycleOwner) { expenses ->
            updateExpensesDisplay(expenses)
        }
    }

    private fun updateBudgetDisplay(budget: Double) {
        binding.tvBudgetAmount.text = numberFormat.format(budget)
        updateBudgetProgress()
    }

    private fun updateExpensesDisplay(expenses: Double) {
        binding.tvSpentAmount.text = numberFormat.format(expenses)
        updateBudgetProgress()
    }

    private fun updateBudgetProgress() {
        try {
            val monthlyBudget = preferenceManager.getMonthlyBudget()
            val monthlyExpenses = viewModel.getMonthlyExpenses()
            val remaining = monthlyBudget - monthlyExpenses

            // Update progress bar
            val progress = if (monthlyBudget > 0) {
                (monthlyExpenses / monthlyBudget * 100).toInt()
            } else {
                0
            }
            binding.progressBudget.progress = progress

            // Update status text
            val statusText = when {
                progress >= 100 -> "Budget Exceeded!"
                progress >= 90 -> "Almost there!"
                progress >= 70 -> "On track"
                else -> ""
            }
            binding.tvBudgetStatus.text = statusText

            // Update remaining amount
            binding.tvRemainingAmount.text = numberFormat.format(remaining)

            // Update progress bar color based on status
            val progressColor = when {
                progress >= 100 -> R.color.red_500
                progress >= 90 -> R.color.orange_500
                else -> R.color.green_500
            }
            binding.progressBudget.setIndicatorColor(ContextCompat.getColor(requireContext(), progressColor))
        } catch (e: Exception) {
            e.printStackTrace()
            binding.progressBudget.progress = 0
            binding.tvBudgetStatus.text = "0%"
            binding.tvRemainingAmount.text = numberFormat.format(0.0)
        }
    }

    private fun saveBudget() {
        try {
            val budget = binding.etMonthlyBudget.text.toString().toDouble()
            if (budget < 0) {
                Toast.makeText(requireContext(), "Budget cannot be negative", Toast.LENGTH_SHORT).show()
                return
            }
            viewModel.updateBudget(budget)
            showBudgetNotification()
            Toast.makeText(requireContext(), "Budget updated", Toast.LENGTH_SHORT).show()
        } catch (e: NumberFormatException) {
            Toast.makeText(requireContext(), "Invalid budget amount", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error saving budget", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showBudgetNotification() {
        try {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val monthlyBudget = preferenceManager.getMonthlyBudget()
                notificationHelper.showBudgetNotification(monthlyBudget)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun navigateToPasscode() {
        try {
            val intent = Intent(requireContext(), PasscodeActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error navigating to passcode screen", Toast.LENGTH_SHORT).show()
        }
    }
}