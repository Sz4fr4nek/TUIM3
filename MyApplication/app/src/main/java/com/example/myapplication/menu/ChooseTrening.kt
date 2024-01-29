package com.example.myapplication.menu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.ComponentActivity
import androidx.core.util.toAndroidPair
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.api.ApiService
import com.example.myapplication.api.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChooseTrening : ComponentActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_trening)

        sessionManager = SessionManager(this)

        listView = findViewById(R.id.listViewTrainings)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)

        sessionManager.traning_level?.let {
            service.getTrainings(it).enqueue(object : Callback<List<ApiService.Training>> {
                override fun onResponse(
                    call: Call<List<ApiService.Training>>,
                    response: Response<List<ApiService.Training>>
                ) {
                    if (response.isSuccessful) {
                        val trainingNames = response.body()?.map {it.training_id to it.training_name } ?: emptyList()
                        // Utworzenie adaptera z listą nazw i ustawienie go dla ListView
                        val adapter = ArrayAdapter(this@ChooseTrening, android.R.layout.simple_list_item_1, trainingNames.map { it.second })
                        listView.adapter = adapter


                        listView.setOnItemClickListener { parent, view, position, id ->
                            val selectedItem = trainingNames[position]
                            val intent = Intent(this@ChooseTrening, TrainingDetailActivity::class.java).apply {
                                putExtra("ID", selectedItem.first.toString())
                            }
                            startActivity(intent)
                        }

                    }
                }

                override fun onFailure(call: Call<List<ApiService.Training>>, t: Throwable) {
                    // Obsługa błędu
                }
            })
        }
    }
}
