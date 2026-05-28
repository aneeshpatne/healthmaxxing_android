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