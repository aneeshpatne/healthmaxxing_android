package com.aneesh.healthmaxxing.network

import okhttp3.ResponseBody
import retrofit2.http.GET


interface ApiService {
    @GET("/client/start")
    suspend fun test(): ResponseBody

}