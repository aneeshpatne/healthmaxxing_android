package com.aneesh.healthmaxxing.repository

import com.aneesh.healthmaxxing.data.remote.ApiService
import com.aneesh.healthmaxxing.data.remote.PerformanceResponse
import retrofit2.Response
import javax.inject.Inject

class PerformanceRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getPerformance(profileId: String): Response<PerformanceResponse> {
        return apiService.getPerformance(profileId)
    }
}
