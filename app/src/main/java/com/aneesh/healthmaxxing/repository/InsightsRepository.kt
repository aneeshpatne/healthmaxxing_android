package com.aneesh.healthmaxxing.repository

import com.aneesh.healthmaxxing.data.remote.ApiService
import com.aneesh.healthmaxxing.data.remote.BodyCompositionTrendResponse
import com.aneesh.healthmaxxing.data.remote.InsightsResponse
import retrofit2.Response

import javax.inject.Inject

class InsightsRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getInsights(profileId: String): Response<InsightsResponse> {
        return apiService.getInsights(profileId)
    }

    suspend fun getTrends(
        metric: String,
        period: String,
        profileId: String? = null
    ): Response<BodyCompositionTrendResponse> {
        return apiService.getBodyCompositionTrends(metric, period, profileId)
    }
}