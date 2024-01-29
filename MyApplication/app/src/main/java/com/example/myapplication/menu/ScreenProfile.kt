package com.example.myapplication.menu

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
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

class ScreenProfile: ComponentActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var imageViewProfile: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_user)

        sessionManager = SessionManager(this)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        imageViewProfile = findViewById(R.id.imageViewProfile)
        val buttonOpenCamera = findViewById<Button>(R.id.buttonOpenCamera)

        buttonOpenCamera.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        }


        apiService.getUser(sessionManager.id).enqueue(object : Callback<ApiService.UserDetails> {
            override fun onResponse(call: Call<ApiService.UserDetails>, response: Response<ApiService.UserDetails>) {
                if (response.isSuccessful) {
                    val userDetails = response.body()
                    userDetails?.let { user ->
                        findViewById<TextView>(R.id.textViewUserWeight).text = "Weight: ${user.weight}"
                        findViewById<TextView>(R.id.textViewTrainingLevel).text = "Training Level: ${user.training_level}"
                        // Update other UI components as necessary
                    }
                } else {
                    // Handle errors
                }
            }

            override fun onFailure(call: Call<ApiService.UserDetails>, t: Throwable) {
                // Handle network errors
            }
        })

        val buttonEditUser = findViewById<Button>(R.id.buttonEditUser)
        buttonEditUser.setOnClickListener {
            val intent = Intent(this, UpdateProfile::class.java)
            startActivity(intent)
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageViewProfile.setImageBitmap(imageBitmap)
        }
    }
    companion object {
        private const val CAMERA_REQUEST_CODE = 1
    }
}