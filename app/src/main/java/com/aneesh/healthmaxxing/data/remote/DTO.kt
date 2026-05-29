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