package com.example.financialtracker.ui.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.financialtracker.databinding.FragmentSettingsBinding
import com.example.financialtracker.util.BackupManager

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var backupManager: BackupManager

    private val createBackupLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            if (backupManager.createBackup(it)) {
                Toast.makeText(requireContext(), "Backup created successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to create backup", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val restoreBackupLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            if (backupManager.restoreBackup(it)) {
                Toast.makeText(requireContext(), "Backup restored successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to restore backup", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        backupManager = BackupManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnBackup.setOnClickListener {
            createBackupLauncher.launch(backupManager.getDefaultBackupFileName())
        }

        binding.btnRestore.setOnClickListener {
            restoreBackupLauncher.launch("application/json")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 