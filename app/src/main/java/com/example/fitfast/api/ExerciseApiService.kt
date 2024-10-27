/*
package com.example.fitfast.api

import com.example.fitfast.model.ApiExercise
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ExerciseApiService {

    @GET("/exercises")
    suspend fun getAllExercises(@Query("limit") limit: Int = 1500): Response<List<ApiExercise>>

    @GET("/exercises/name/{name}")
    suspend fun searchExercises(@Path("name") query: String): Response<List<ApiExercise>>

    @GET("/exercises/exercise/{id}")
    suspend fun getExerciseDetail(@Path("id") id: String): Response<ApiExercise>

    companion object {
        private const val BASE_URL = "https://exercisedb.p.rapidapi.com/"

        fun create(): ExerciseApiService {
            val client = OkHttpClient.Builder()
                .addInterceptor(ApiKeyInterceptor())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ExerciseApiService::class.java)
        }
    }
}
*/
import com.example.fitfast.model.JsonExercise
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ExerciseApiService {
    @GET("yuhonas/free-exercise-db/main/dist/exercises.json")
    suspend fun getExercises(): List<JsonExercise>
}

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ExerciseApiService by lazy {
        retrofit.create(ExerciseApiService::class.java)
    }
}