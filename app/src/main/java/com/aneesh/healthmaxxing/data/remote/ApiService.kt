package com.aneesh.healthmaxxing.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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
}
