package com.aneesh.healthmaxxing.data.bluetooth

data class ScaleMeasurement(
    val weightKg: Float? = null,
    val heartRate: Int? = null,
    val impedanceOhms: Float? = null,
    val isFinal: Boolean = false
)
