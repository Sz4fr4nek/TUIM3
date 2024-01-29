package com.example.myapplication.menu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.myapplication.R
import com.example.myapplication.api.ApiService
import com.example.myapplication.api.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UpdateProfile : ComponentActivity() {
    private lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        sessionManager = SessionManager(this)

        val buttonEditUser = findViewById<Button>(R.id.buttonSaveChanges)


        //dodaj obsluge wyswietlania zdjecia i przycisk który bedzie przechodził do aparatu

        buttonEditUser.setOnClickListener {
            val weight = findViewById<EditText>(R.id.editTextWeight).text.toString().toFloat()
            val trainingLevel = findViewById<EditText>(R.id.editTextTrainingLevel).text.toString()

            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val updateRequest = ApiService.UpdateProfileRequest(
                weight = weight,
                training_level = trainingLevel
            )
            val apiService = retrofit.create(ApiService::class.java)
            apiService.updateProfile(sessionManager.id, updateRequest).enqueue(object : Callback<ApiService.UpdateProfileResponse> {
                override fun onResponse(call: Call<ApiService.UpdateProfileResponse>, response: Response<ApiService.UpdateProfileResponse>) {
                    if (response.isSuccessful) {
                        val intent = Intent(this@UpdateProfile, ScreenMenu::class.java)
                        startActivity(intent)
                    } else {
                        // Handle errors, e.g., show an error message
                    }
                }

                override fun onFailure(call: Call<ApiService.UpdateProfileResponse>, t: Throwable) {
                    // Handle network errors or exceptions
                }
            })
        }
    }

}