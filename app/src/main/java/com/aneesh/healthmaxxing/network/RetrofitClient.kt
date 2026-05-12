package com.aneesh.healthmaxxing.network

import retrofit2.Retrofit

object RetrofitClient {
    private const val BASE_URL = "http://192.168.0.99:3030/"
    val api: ApiService by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).build().create(ApiService::class.java)
    }
}