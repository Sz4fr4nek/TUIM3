package com.example.myapplication.menu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.myapplication.R
import com.example.myapplication.api.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TrainingDetailActivity : ComponentActivity() {

    private lateinit var textViewTrainingName: TextView
    private lateinit var textViewTrainingLevel: TextView
    private lateinit var listViewExercises: ListView
    private lateinit var buttonEdit: Button
    private lateinit var buttonSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_detail)



        // Inicjalizacja widoków
        textViewTrainingName = findViewById(R.id.textViewTrainingName)
        textViewTrainingLevel = findViewById(R.id.textViewTrainingLevel)
        listViewExercises = findViewById(R.id.listViewExercises)


        val tmp = intent.getStringExtra("ID").toString()
        val trainingId = tmp.toInt()


        findViewById<Button>(R.id.buttonEdit2).setOnClickListener {
            val intent = Intent(this@TrainingDetailActivity, TrainingEdit::class.java).apply {
                putExtra("tmp_ID1", tmp.toString())
                Log.d("Moj", tmp.toString())
            }
            startActivity(intent)
        }





        if (trainingId != -1) {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000") // Podaj odpowiedni adres URL API
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(ApiService::class.java)

            if (trainingId != null) {
                apiService.getTraining(trainingId).enqueue(object : Callback<ApiService.TrainingResponse> {
                    override fun onResponse(call: Call<ApiService.TrainingResponse>, response: Response<ApiService.TrainingResponse>) {
                        if (response.isSuccessful) {
                            val trainingResponse = response.body()

                            // Wyświetlenie danych treningu
                            trainingResponse?.training?.let { training ->
                                textViewTrainingName.text = "Nazwa treningu: ${training.training_name[1]}"
                                textViewTrainingLevel.text = "Poziom treningu: ${training.training_level}"
                            }

                            // Wyświetlenie listy ćwiczeń
                            val exercises = trainingResponse?.exercises ?: emptyList()
                            val exerciseNames = exercises.map { it.exercise_name }
                            val adapter = ArrayAdapter(
                                this@TrainingDetailActivity,
                                android.R.layout.simple_list_item_1,
                                exerciseNames
                            )
                            listViewExercises.adapter = adapter
                        } else {
                            // Obsłuż błąd, np. wyświetl komunikat
                        }
                    }

                    override fun onFailure(call: Call<ApiService.TrainingResponse>, t: Throwable) {
                        // Obsłuż błąd, np. wyświetl komunikat o błędzie sieciowym
                    }
                })
            }


        } else {
            // Obsługa błędu, gdy ID treningu nie zostało przekazane poprawnie
        }


    }
}