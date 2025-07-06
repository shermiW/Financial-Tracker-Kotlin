package com.example.financialtracker.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.financialtracker.R
import com.example.financialtracker.databinding.ActivityDashboardBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityDashboardBinding.inflate(layoutInflater)
            setContentView(binding.root)
            setupNavigation()
        } catch (e: Exception) {
            Log.e("DashboardActivity", "Error in onCreate: ${e.message}")
            Toast.makeText(this, "Error initializing dashboard", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupNavigation() {
        try {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController

            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNavigationView?.setupWithNavController(navController)
            
            // Set default selected item
            bottomNavigationView?.selectedItemId = R.id.navigation_dashboard
        } catch (e: Exception) {
            Log.e("DashboardActivity", "Error in setupNavigation: ${e.message}")
            Toast.makeText(this, "Error setting up navigation", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}