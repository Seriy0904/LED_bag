package com.example.led_bag

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

var BASE_URL = "http://192.168.4.1"

interface ApiService {
    @GET("/pixel")
    suspend fun setPixel(
        @Query("xPos") x: Int,
        @Query("yPos") y: Int,
        @Query("red") r: Int,
        @Query("green") g: Int,
        @Query("blue") b: Int
    )
    @GET("/fill")
    suspend fun fillBack(
        @Query("red") r: Int,
        @Query("green") g: Int,
        @Query("blue") b: Int
    )
    @GET("/show")
    suspend fun showPixels()
    @GET("/animation")
    suspend fun showAnimation()
}

object RetrofitInstance {
    val api: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}