package com.aneesh.healthmaxxing.data.remote

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("mailAddress") val mailAddress: String
)

data class RegisterResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("id") val id: String,
    @SerializedName("mailAddress") val mailAddress: String
)

data class RegisterProfileRequest(
    @SerializedName("accountId") val accountId: String,
    @SerializedName("name") val name: String,
    @SerializedName("isPrimary") val isPrimary: String
)

data class RegisterProfileResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("id") val id: String,
    @SerializedName("accountId") val accountId: String,
    @SerializedName("name") val name: String,
    @SerializedName("isPrimary") val isPrimary: String
)

data class ProfileMetadataRequest(
    @SerializedName("profileId") val profileId: String,
    @SerializedName("heightCm") val heightCm: Int,
    @SerializedName("dateOfBirth") val dateOfBirth: String,
    @SerializedName("peopleType") val peopleType: String,
    @SerializedName("gender") val gender: String
)

data class ProfileMetadataResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("profileId") val profileId: String,
    @SerializedName("heightCm") val heightCm: Int,
    @SerializedName("dateOfBirth") val dateOfBirth: String,
    @SerializedName("peopleType") val peopleType: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("profileImage") val profileImage: String?
)

data class AddMeasurementRequest(
    @SerializedName("profileId") val profileId: String,
    @SerializedName("weight") val weight: Double,
    @SerializedName("heartbeat") val heartbeat: Int?,
    @SerializedName("impedance") val impedance: Double?
)

data class AddMeasurementResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("id") val id: String
)

data class InsightsResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("profileId") val profileId: String,
    @SerializedName("insights") val insights: Insights,
    @SerializedName("effortScore") val effortScore: EffortScore
)

data class Insights(
    @SerializedName("profileId") val profileId: String,
    @SerializedName("overviewTitle") val overviewTitle: String,
    @SerializedName("overviewRemarks") val overviewRemarks: String,
    @SerializedName("foundation") val foundation: InsightSection,
    @SerializedName("momentum") val momentum: InsightSection,
    @SerializedName("biggestLever") val biggestLever: InsightSection,
    @SerializedName("physiqueArchetype") val physiqueArchetype: String,
    @SerializedName("modelName") val modelName: String,
    @SerializedName("updatedAt") val updatedAt: String
)

data class InsightSection(
    @SerializedName("headline") val headline: String,
    @SerializedName("supporting_description") val supportingDescription: String,
    @SerializedName("actionable_insight") val actionableInsight: String,
    @SerializedName("factors") val factors: List<String>? = null
)

data class EffortScore(
    @SerializedName("profileId") val profileId: String,
    @SerializedName("score") val score: Int,
    @SerializedName("remark") val remark: String,
    @SerializedName("modelName") val modelName: String,
    @SerializedName("updatedAt") val updatedAt: String
)
