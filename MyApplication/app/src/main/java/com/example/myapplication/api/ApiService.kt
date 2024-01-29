package com.example.myapplication.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {
    @POST("register")
    @Headers("Accept: application/json")
    fun registerUser(@Body userRegister: UserRegister): Call<ResponseBody>

    @POST("login")
    fun loginUser(@Body userData: UserLogin): Call<LoginResponse>

    @GET("trainings")
    fun getTrainings(@Query("training_level") trainingLevel: String): Call<List<Training>>

    @GET("/training/{training_id}")
    fun getTraining(@Path("training_id") trainingId: Int): Call<TrainingResponse>

    @PUT("/training/{training_id}")
    fun updateTraining(@Path("training_id") trainingId: Int, @Body trainingUpdate: TrainingUpdate ): Call<TrainingResponse>

    @POST("/add_training_history/{user_id}")
    fun addTrainingHistory(@Path("user_id") user_id: Int, @Body trainingHistory: TrainingHistory): Call<TrainingResponse>

    @GET("/training_history/{user_id}")
    fun getTrainingHistory(@Path("user_id") userId: Int): Call<List<TrainingHistoryModel>>

    @DELETE("/training_history/{training_history_id}")
    fun deleteTrainingHistory(@Path("training_history_id") trainingHistoryId: Int): Call<Unit>

    @GET("/user/{user_id}")
    fun getUser(@Path("user_id") userId: Int): Call<UserDetails>

    @PUT("/user/{user_id}")
    fun updateProfile(
        @Path("user_id") userId: Int,
        @Body updateProfileRequest: UpdateProfileRequest
    ): Call<UpdateProfileResponse>

    data class UpdateProfileRequest(
        val weight: Float,
        val training_level: String
    )

    data class UpdateProfileResponse(val message: String)



    data class UserDetails(
        val user_id: Int,
        val weight: Float,
        val training_level: String
        // Add other fields as necessary
    )

    data class TrainingHistoryModel(
        val user_id: Int,
        val training_name: String,
        val date_saved: String,  // Adjust the type according to your format
        val training_level: String,
        val trainingHistory_id: Int
    )

    data class TrainingHistory(val training_name: String, val training_level: String)


    data class TrainingHistoryWithData (
        val training_name: String,
        val training_level: String,
        val date_saved: String
    )

    data class TrainingResponse(
        val training: Training,
        val exercises: List<Exercise>
    )

    data class TrainingHistoryResponse(
        val user_id: Int,
        val training_name: String,
        val date_saved: String,
        val training_level: String,
        val trainingHistory_id: Int
    )

    data class Exercise(
        val training_id: Int,
        val exercise_name: String,
        val weight: Double,
        val exercise_id: Int
    )

    data class TrainingUpdate(
        val training_name: String,
        val training_level: String
    )


    data class Training(
        val training_id: Int,
        val training_name: String,
        val training_level: String
    )

    data class UserLogin(
        val username: String,
        val password: String
    )

    data class LoginResponse(
        val accessToken: String,
        val userName: String,
        val userId: Int,
        val traning_level: String
    )

    data class UserRegister(
        val username: String,
        val password: String,
        val weight: Double,
        val training_level: String
    )
}