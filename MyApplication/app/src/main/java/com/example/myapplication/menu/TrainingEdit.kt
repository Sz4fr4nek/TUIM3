package com.example.myapplication.menu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.api.ApiService
import com.example.myapplication.api.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TrainingEdit : ComponentActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var editTextTrainingName: EditText
    private lateinit var editTextTrainingLevel: EditText
    private lateinit var buttonSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_edit)

        sessionManager = SessionManager(this)

        // Inicjalizacja widoków
        editTextTrainingName = findViewById(R.id.editTextTrainingName)
        editTextTrainingLevel = findViewById(R.id.editTextTrainingLevel)
        buttonSave = findViewById(R.id.buttonSave)

        // Pobieranie ID treningu, który ma być zmodyfikowany, np. z intentu
        var tmp = intent.getStringExtra("tmp_ID1").toString()
        //Log.d("Moj", tmp.toString())
        val trainingId = tmp.toInt()

        // Obsługa przycisku "Zapisz zmiany"
        buttonSave.setOnClickListener {
            // Pobieranie wprowadzonych danych
            val newTrainingName = editTextTrainingName.text.toString()
            val newTrainingLevel = editTextTrainingLevel.text.toString()

            // Tworzenie obiektu do wysłania na serwer
            val trainingUpdate = ApiService.TrainingUpdate(newTrainingName, newTrainingLevel)

            // Wywołanie funkcji do aktualizacji treningu na serwerze
            updateTraining(trainingId, trainingUpdate)
        }

        findViewById<Button>(R.id.buttonSave2).setOnClickListener {
            val newTrainingName = editTextTrainingName.text.toString()
            val newTrainingLevel = editTextTrainingLevel.text.toString()
            sessionManager.id.let {
                saveTrainingHistory(it, newTrainingName, newTrainingLevel)
            }
        }

    }

    private fun saveTrainingHistory(userId: Int, trainingName: String, trainingLevel: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000") // Podaj odpowiedni adres URL API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        Log.d("Moj", userId.toString())
        val trainingHistory = ApiService.TrainingHistory(trainingName, trainingLevel)
        val call = apiService.addTrainingHistory(userId, trainingHistory)

        call.enqueue(object : Callback<ApiService.TrainingResponse> {
            override fun onResponse(call: Call<ApiService.TrainingResponse>, response: Response<ApiService.TrainingResponse>) {
                if (response.isSuccessful) {
                    val intent = Intent(this@TrainingEdit, ScreenMenu::class.java)
                    startActivity(intent)
                } else {
                    // Handle the error case
                }
            }

            override fun onFailure(call: Call<ApiService.TrainingResponse>, t: Throwable) {
                // Handle the network error scenario
            }
        })
    }



    private fun updateTraining(trainingId: Int, trainingUpdate: ApiService.TrainingUpdate) {
        // Tworzenie instancji klasy obsługującej API
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000") // Podaj odpowiedni adres URL API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)


        // Wysyłanie żądania do API
        val call = apiService.updateTraining(trainingId, trainingUpdate)
        call.enqueue(object : Callback<ApiService.TrainingResponse> {
            override fun onResponse(call: Call<ApiService.TrainingResponse>, response: Response<ApiService.TrainingResponse>) {
                if (response.isSuccessful) {
                        val intent = Intent(this@TrainingEdit, ScreenMenu::class.java)
                        startActivity(intent)
                } else {
                    // Błąd podczas zapisywania treningu
                    // Możesz dodać obsługę błędu, np. wyświetlenie komunikatu o błędzie
                }
            }

            override fun onFailure(call: Call<ApiService.TrainingResponse>, t: Throwable) {
                // Błąd połączenia z serwerem
                // Możesz dodać obsługę błędu, np. wyświetlenie komunikatu o błędzie
            }
        })
    }
}