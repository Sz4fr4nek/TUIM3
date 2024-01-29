package com.example.myapplication.menu

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.myapplication.R
import com.example.myapplication.api.SessionManager

class ScreenMenu : ComponentActivity() {
    private lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen_after_login)
        sessionManager = SessionManager(this)

        // Pobieranie nazwy użytkownika z SessionManager i ustawianie jej w TextView
        val usernameTextView = findViewById<TextView>(R.id.textViewUsername1)
        sessionManager.userName?.let {
            usernameTextView.text = it
        }
        findViewById<Button>(R.id.buttonSelectTraining).setOnClickListener {
            val intent = Intent(this, ChooseTrening::class.java)
            startActivity(intent)
        }


        findViewById<Button>(R.id.buttonHistoryTraining).setOnClickListener {
            val intent = Intent(this, HistoryTrening::class.java)
            startActivity(intent)
        }


        findViewById<Button>(R.id.buttonUpdateProfile).setOnClickListener {
            val intent = Intent(this, ScreenProfile::class.java)
            startActivity(intent)
        }
    }
}
