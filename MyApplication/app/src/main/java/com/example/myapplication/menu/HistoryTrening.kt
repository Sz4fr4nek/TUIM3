package com.example.myapplication.menu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
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

class HistoryTrening : ComponentActivity() {

    private inner class TrainingHistoryAdapter(
        context: Context,
        private val trainingHistoryList: List<ApiService.TrainingHistoryModel>
    ) : ArrayAdapter<ApiService.TrainingHistoryModel>(context, 0, trainingHistoryList) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var listItemView = convertView
            if (listItemView == null) {
                listItemView = LayoutInflater.from(context).inflate(R.layout.activity_history_training, parent, false)
            }

            val currentItem = trainingHistoryList[position]

            val trainingNameTextView = listItemView!!.findViewById<TextView>(R.id.textViewTrainingName)
            trainingNameTextView.text = currentItem.training_name

            val trainingLevelTextView = listItemView.findViewById<TextView>(R.id.textViewTrainingLevel)
            trainingLevelTextView.text = currentItem.training_level

            // Set other TextViews for date_saved, etc.

            return listItemView
        }
    }


    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_training)


        listView = findViewById(R.id.listViewTrainingHistory)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)

        val userId = 1

        service.getTrainingHistory(userId).enqueue(object : Callback<List<ApiService.TrainingHistoryModel>> {
            override fun onResponse(call: Call<List<ApiService.TrainingHistoryModel>>, response: Response<List<ApiService.TrainingHistoryModel>>) {
                if (response.isSuccessful) {
                    // Pass the response data to the ListView adapter
                    val trainingHistoryList = response.body() ?: emptyList()
                    val trainingDetails = trainingHistoryList.map { training ->
                        "Nazwa Treningu: ${training.training_name}, Level: ${training.training_level}"
                    }

                    // Use a simple ArrayAdapter to display the training details
                    val adapter = ArrayAdapter(
                        this@HistoryTrening,
                        android.R.layout.simple_list_item_1,
                        trainingDetails
                    )
                    listView.adapter = adapter

                    listView.setOnItemClickListener { parent, view, position, id ->
                        val selectedTrainingHistory = trainingHistoryList[position]
                        val trainingHistoryId = selectedTrainingHistory.trainingHistory_id

                        Log.d("Moj", selectedTrainingHistory.toString())

                        val retrofit = Retrofit.Builder()
                            .baseUrl("http://10.0.2.2:8000")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                        val service = retrofit.create(ApiService::class.java)

                        // Call your API to delete the training history
                        service.deleteTrainingHistory(trainingHistoryId).enqueue(object : Callback<Unit> {
                            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                                if (response.isSuccessful) {
                                    val intent = Intent(this@HistoryTrening, ScreenMenu::class.java)
                                    startActivity(intent)
                                } else {
                                    // Handle error case
                                }
                            }

                            override fun onFailure(call: Call<Unit>, t: Throwable) {
                                // Handle network error scenario
                            }
                        })
                    }

                } else {
                    // Handle error case
                }
            }

            override fun onFailure(call: Call<List<ApiService.TrainingHistoryModel>>, t: Throwable) {
                // Handle network error scenario
            }
        })

    }

}