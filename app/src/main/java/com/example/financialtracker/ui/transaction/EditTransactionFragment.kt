package com.example.financialtracker.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.financialtracker.R
import com.example.financialtracker.data.PreferenceManager
import com.example.financialtracker.data.Result
import com.example.financialtracker.data.Transaction
import com.example.financialtracker.data.TransactionRepository
import com.example.financialtracker.databinding.FragmentEditTransactionBinding
import com.example.financialtracker.ui.dashboard.DashboardViewModel
import com.example.financialtracker.ui.dashboard.DashboardViewModelFactory
import android.widget.ArrayAdapter

class EditTransactionFragment : Fragment() {
    private var _binding: FragmentEditTransactionBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: EditTransactionViewModel
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var transaction: Transaction

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the transaction from arguments
        transaction = requireArguments().getParcelable("transaction") ?: throw IllegalStateException("Transaction argument is required")

        val preferenceManager = PreferenceManager(requireContext())
        val transactionRepository = TransactionRepository(preferenceManager)

        viewModel = ViewModelProvider(
            this,
            EditTransactionViewModelFactory(preferenceManager)
        ).get(EditTransactionViewModel::class.java)

        dashboardViewModel = ViewModelProvider(
            requireActivity(),
            DashboardViewModelFactory(transactionRepository, preferenceManager)
        ).get(DashboardViewModel::class.java)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.apply {
            // Set up category spinner
            val categories = viewModel.getCategories()
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter

            // Set initial values
            editTextTitle.setText(transaction.title)
            editTextAmount.setText(transaction.amount.toString())

            // Set category selection
            val categoryPosition = categories.indexOf(transaction.category)
            if (categoryPosition != -1) {
                spinnerCategory.setSelection(categoryPosition)
            }

            // Set transaction type
            if (transaction.type == Transaction.Type.INCOME) {
                radioIncome.isChecked = true
            } else {
                radioExpense.isChecked = true
            }

            // Set up click listeners
            buttonSave.setOnClickListener {
                updateTransaction(transaction)
            }

            buttonCancel.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun updateTransaction(originalTransaction: Transaction) {
        binding.apply {
            val title = editTextTitle.text.toString()
            val amount = editTextAmount.text.toString().toDoubleOrNull()
            val category = spinnerCategory.selectedItem?.toString() ?: ""
            val type = if (radioIncome.isChecked) Transaction.Type.INCOME else Transaction.Type.EXPENSE

            if (title.isBlank() || amount == null || category.isBlank()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return
            }

            val updatedTransaction = Transaction(
                id = originalTransaction.id,
                title = title,
                amount = amount,
                category = category,
                type = type,
                date = originalTransaction.date
            )

            viewModel.updateTransaction(updatedTransaction)
        }
    }

    private fun observeViewModel() {
        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    dashboardViewModel.refreshData()
                    Toast.makeText(requireContext(), "Transaction updated", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), result.exception.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}