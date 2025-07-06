package com.example.financialtracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.financialtracker.ui.passcode.PasscodeActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Redirect to passcode screen
        val intent = Intent(this, PasscodeActivity::class.java)
        startActivity(intent)
        finish()
    }
}