package com.aneesh.healthmaxxing.repository

import com.aneesh.healthmaxxing.data.remote.ApiService
import com.aneesh.healthmaxxing.data.remote.ProfileEssentialsResponse
import retrofit2.Response

import javax.inject.Inject

class EssentialsRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getEssentials(profileId: String): Response<ProfileEssentialsResponse> {
        return apiService.getEssentials(profileId)
    }
}
