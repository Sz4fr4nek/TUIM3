package com.example.myapplication.api

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.myapplication.R
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log
import com.example.myapplication.MainActivity
import com.example.myapplication.menu.ScreenMenu

class ApiRejestacja : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rejestracja)

        // Znajdź widoki za pomocą ich identyfikatorów
        val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val weightEditText = findViewById<EditText>(R.id.weightEditText)
        val trainingLevelSpinner = findViewById<Spinner>(R.id.trainingLevelSpinner)
        val registerButton = findViewById<Button>(R.id.registerButton)

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            // Zmienione, aby obsłużyć Double lub Int zgodnie z Twoim modelem
            val weight = weightEditText.text.toString().toDoubleOrNull()
                ?: 0.0 // Domyślnie 0.0, jeśli konwersja zawiedzie
            val trainingLevel = trainingLevelSpinner.selectedItem.toString()

            val userRegister = ApiService.UserRegister(username, password, weight, trainingLevel)

            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000") // Ustaw bazowy adres URL na serwerze
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(ApiService::class.java)
            apiService.registerUser(userRegister).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        // Rejestracja udana
                        val message = response.body()?.string()
                        Log.d("Moj", message.toString())
                        val intent = Intent(this@ApiRejestacja, MainActivity::class.java)
                        startActivity(intent)
                        // Obsłuż odpowiedź serwera
                    } else {
                        // Obsługa błędów, np. użytkownik już istnieje
                        val errorMessage = response.errorBody()?.string()
                        Log.d("Moj", errorMessage.toString())
                        // Obsłuż błąd
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    //Log.d("Moj", t.message.toString())
                }
            })

        }
    }
}
