package com.aneesh.healthmaxxing.network

import com.aneesh.healthmaxxing.model.ApiResponse
import com.aneesh.healthmaxxing.model.StartRequest
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {
    @POST("client/start")
    suspend fun test(@Body request: StartRequest): ApiResponse
}
