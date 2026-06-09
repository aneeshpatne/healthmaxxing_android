package com.aneesh.healthmaxxing.repository

import com.aneesh.healthmaxxing.data.remote.ApiService
import com.google.gson.JsonObject
import retrofit2.Response
import javax.inject.Inject

class MuscleRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getMuscle(profileId: String): Response<JsonObject> {
        return apiService.getMuscle(profileId)
    }
}
