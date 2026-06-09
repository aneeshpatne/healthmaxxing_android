package com.aneesh.healthmaxxing.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import com.google.gson.JsonObject

interface ApiService {
    @POST("client/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("client/register/profiles")
    suspend fun registerProfile(@Body request: RegisterProfileRequest): Response<RegisterProfileResponse>

    @POST("client/register/metadata")
    suspend fun registerProfileMetadata(@Body request: ProfileMetadataRequest): Response<ProfileMetadataResponse>

    @POST("ingest/add_measurement")
    suspend fun addMeasurement(@Body request: AddMeasurementRequest): Response<AddMeasurementResponse>

    @GET("client/profiles/{profileId}/insights")
    suspend fun getInsights(@Path("profileId") profileId: String): Response<InsightsResponse>

    @GET("client/body-composition/trends")
    suspend fun getBodyCompositionTrends(
        @Query("metric") metric: String,
        @Query("period") period: String,
        @Query("profileId") profileId: String? = null
    ): Response<BodyCompositionTrendResponse>

    @GET("client/profiles/{profileId}/essentials")
    suspend fun getEssentials(@Path("profileId") profileId: String): Response<ProfileEssentialsResponse>

    @GET("client/profiles/{profileId}/performance")
    suspend fun getPerformance(@Path("profileId") profileId: String): Response<PerformanceResponse>

    @GET("client/profiles/{profileId}/fat")
    suspend fun getFat(@Path("profileId") profileId: String): Response<JsonObject>

    @GET("client/profiles/{profileId}/muscle")
    suspend fun getMuscle(@Path("profileId") profileId: String): Response<JsonObject>
}
