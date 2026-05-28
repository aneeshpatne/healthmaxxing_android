package com.aneesh.healthmaxxing.data.remote

import okhttp3.RequestBody
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("register")
    suspend fun register(@Body request: RequestBody): Response
}