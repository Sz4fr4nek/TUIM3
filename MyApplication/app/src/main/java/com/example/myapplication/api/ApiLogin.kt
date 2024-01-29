package com.example.myapplication.api

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.menu.ScreenMenu
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.lang.Thread.sleep

class ApiLogin : ComponentActivity() {

    private lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sessionManager = SessionManager(this)
        val usernameEditText = findViewById<EditText>(R.id.usernameEditText1)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText1)
        val loginButton = findViewById<Button>(R.id.loginButton1)

        loginButton.setOnClickListener {


            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000") // Ustaw bazowy adres URL
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(ApiService::class.java)
            val userLogin = ApiService.UserLogin(username, password)

            apiService.loginUser(userLogin).enqueue(object : Callback<ApiService.LoginResponse> {
                override fun onResponse(call: Call<ApiService.LoginResponse>, response: Response<ApiService.LoginResponse>) {
                    if (response.isSuccessful) {
                        // Logowanie udane, obsłuż zapisanie tokena i nazwy użytkownika
                        val loginResponse = response.body()!!
                        Log.d("Moj", loginResponse.toString())
                        response.body()?.let {
                            sessionManager.authToken = it.accessToken
                            sessionManager.userName = it.userName
                            sessionManager.id = it.userId
                            sessionManager.traning_level= it.traning_level
                        }
                        Toast.makeText(applicationContext, "Login Success", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@ApiLogin, ScreenMenu::class.java)
                        startActivity(intent)
                    } else {
                        // Logowanie nieudane, pokaż błąd
                        Toast.makeText(applicationContext, "Login Failed", Toast.LENGTH_LONG).show()
                        Log.d("Moj", "Login Failed")
                    }
                }

                override fun onFailure(call: Call<ApiService.LoginResponse>, t: Throwable) {
                    // Wystąpił błąd sieci lub inny, pokaż wiadomość
                    Toast.makeText(applicationContext, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                    Log.d("Moj", t.message.toString())
                }
            })

        }
    }
}
