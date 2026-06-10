package com.aneesh.healthmaxxing.ui.metrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aneesh.healthmaxxing.data.datastore.AccountPreferences
import com.aneesh.healthmaxxing.data.remote.TrendPoint
import com.aneesh.healthmaxxing.repository.FatRepository
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

const val FAT_METRIC_BODY_FAT_PCT = "body_fat_pct"
const val FAT_METRIC_FAT_MASS_KG = "fat_mass_kg"
const val FAT_METRIC_VISCERAL_FAT = "visceral_fat"
const val FAT_METRIC_VISCERAL_FAT_PCT = "visceral_fat_pct"
const val FAT_METRIC_SUBCUTANEOUS_FAT_PCT = "subcutaneous_fat_pct"
const val FAT_METRIC_SUBCUTANEOUS_FAT_MASS_KG = "subcutaneous_fat_mass_kg"

data class FatUiState(
    val bodyFatPct: Double? = null,
    val fatMassKg: Double? = null,
    val visceralFat: Double? = null,
    val subcutaneousFatPct: Double? = null,
    val subcutaneousFatMassKg: Double? = null,
    val visceralFatDeltaKg: Double? = null,
    val subcutaneousFatDeltaKg: Double? = null,
    val comments: FatComments = FatComments(),
    val trends: Map<String, List<TrendPoint>> = emptyMap()
)

data class FatComments(
    val fatPercent: FatComment? = null,
    val visceralSubcutaneous30dDelta: FatComment? = null,
    val fatMass: FatComment? = null,
    val visceralFatMass: FatComment? = null,
    val visceralFatPercent: FatComment? = null,
    val subcutaneousFatMass: FatComment? = null,
    val subcutaneousFatRatio: FatComment? = null
)

data class FatComment(
    val remark: String? = null,
    val comment: String? = null
)

@HiltViewModel
class FatViewModel @Inject constructor(
    private val fatRepository: FatRepository,
    private val accountPreferences: AccountPreferences
) : ViewModel() {
    private val _fat = MutableStateFlow(FatUiState())
    val fat = _fat.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        refresh()
    }

    fun refresh(isUserInitiated: Boolean = false) {
        viewModelScope.launch {
            if (isUserInitiated) _isRefreshing.value = true
            val profileId = accountPreferences.selectedPrimaryProfileId.first()
            if (!profileId.isNullOrBlank()) {
                getFat(profileId)
            } else {
                _error.value = "Profile ID is missing."
                _isLoading.value = false
            }
            if (isUserInitiated) _isRefreshing.value = false
        }
    }

    private suspend fun getFat(profileId: String) {
        _isLoading.value = true
        _error.value = null
        try {
            val response = fatRepository.getFat(profileId)
            if (response.isSuccessful) {
                _fat.value = response.body()?.toFatUiState() ?: FatUiState()
            } else {
                _error.value = "Failed to load fat metrics: ${response.message()}"
            }
        } catch (e: Exception) {
            _error.value = e.localizedMessage ?: "An error occurred"
        } finally {
            _isLoading.value = false
        }
    }
}

private fun JsonObject.toFatUiState(): FatUiState {
    val payload = getObject("fat") ?: getObject("data") ?: this
    val metrics = payload.getObject("metrics") ?: payload
    val commentsPayload = payload.getObject("comments") ?: getObject("comments")
    val trendsPayload = payload.getObject("last30Days")
        ?: payload.getObject("trends")
        ?: getObject("last30Days")
        ?: getObject("trends")
        ?: payload
    val trends = mapOf(
        FAT_METRIC_BODY_FAT_PCT to trendsPayload.extractTrend(
            "fatPercent",
            "bodyFatPct30Days",
            "bodyFat30Days",
            "body_fat_pct",
            "bodyFatPct",
            "bodyFat"
        ),
        FAT_METRIC_FAT_MASS_KG to trendsPayload.extractTrend(
            "fatMassKg",
            "fatMassKg30Days",
            "fatMass30Days",
            "fat_mass_kg",
            "fatMassKg",
            "fatMass"
        ),
        FAT_METRIC_VISCERAL_FAT to trendsPayload.extractTrend(
            "visceralFatMassKg",
            "visceralFat30Days",
            "visceral_fat",
            "visceralFat"
        ),
        FAT_METRIC_VISCERAL_FAT_PCT to trendsPayload.extractTrend(
            "visceralFatPercent",
            "visceralFatPct30Days",
            "visceral_fat_pct",
            "visceralFatPct"
        ),
        FAT_METRIC_SUBCUTANEOUS_FAT_PCT to trendsPayload.extractTrend(
            "subcutaneousFatPercent",
            "subcutaneousFatPct30Days",
            "subcutaneousFat30Days",
            "subcutaneous_fat_pct",
            "subcutaneousFatPct"
        ),
        FAT_METRIC_SUBCUTANEOUS_FAT_MASS_KG to trendsPayload.extractTrend(
            "subcutaneousFatMassKg",
            "subcutaneousFatMassKg30Days",
            "subcutaneousFatMass30Days",
            "subcutaneous_fat_mass_kg",
            "subcutaneousFatMassKg"
        )
    ).filterValues { it.isNotEmpty() }

    val visceralSubDeltaObj = metrics.getObject("visceralSubcutaneous30dDelta")

    return FatUiState(
        bodyFatPct = metrics.doubleValue("fatPercent", "bodyFatPct", "body_fat_pct", "fatRatio", "fat_ratio")
            ?: trends.latestValue(FAT_METRIC_BODY_FAT_PCT),
        fatMassKg = metrics.doubleValue("fatMassKg", "fat_mass_kg", "fatMass")
            ?: trends.latestValue(FAT_METRIC_FAT_MASS_KG),
        visceralFat = metrics.doubleValue("visceralFatMassKg", "visceralFat", "visceral_fat")
            ?: trends.latestValue(FAT_METRIC_VISCERAL_FAT),
        subcutaneousFatPct = metrics.doubleValue(
            "subcutaneousFatPercent",
            "subcutaneousFatPct",
            "subcutaneous_fat_pct"
        )
            ?: trends.latestValue(FAT_METRIC_SUBCUTANEOUS_FAT_PCT),
        subcutaneousFatMassKg = metrics.doubleValue("subcutaneousFatMassKg", "subcutaneous_fat_mass_kg")
            ?: trends.latestValue(FAT_METRIC_SUBCUTANEOUS_FAT_MASS_KG),
        visceralFatDeltaKg = visceralSubDeltaObj?.doubleValue("visceralFatDeltaKg"),
        subcutaneousFatDeltaKg = visceralSubDeltaObj?.doubleValue("subcutaneousFatDeltaKg"),
        comments = commentsPayload.toFatComments(),
        trends = trends
    )
}

private fun JsonObject?.toFatComments(): FatComments {
    if (this == null) return FatComments()
    return FatComments(
        fatPercent = getComment("fatPercent"),
        visceralSubcutaneous30dDelta = getComment("visceralSubcutaneous30dDelta"),
        fatMass = getComment("fatMass"),
        visceralFatMass = getComment("visceralFatMass"),
        visceralFatPercent = getComment("visceralFatPercent"),
        subcutaneousFatMass = getComment("subcutaneousFatMass"),
        subcutaneousFatRatio = getComment("subcutaneousFatRatio")
    )
}

private fun JsonObject.getComment(key: String): FatComment? {
    val obj = getObject(key) ?: return null
    return FatComment(
        remark = obj.stringValue("remark"),
        comment = obj.stringValue("comment")
    )
}

private fun JsonObject.getObject(key: String): JsonObject? {
    return get(key)?.takeIf { it.isJsonObject }?.asJsonObject
}

private fun JsonObject.doubleValue(vararg keys: String): Double? {
    return keys.firstNotNullOfOrNull { key ->
        get(key)?.takeIf { !it.isJsonNull && it.isJsonPrimitive }?.asJsonPrimitive?.let { primitive ->
            when {
                primitive.isNumber -> primitive.asDouble
                primitive.isString -> primitive.asString.toDoubleOrNull()
                else -> null
            }
        }
    }
}

private fun JsonObject.extractTrend(vararg keys: String): List<TrendPoint> {
    val array = keys.firstNotNullOfOrNull { key ->
        get(key)?.takeIf { it.isJsonArray }?.asJsonArray
    } ?: return emptyList()

    return array.toTrendPoints()
}

private fun JsonArray.toTrendPoints(): List<TrendPoint> {
    return mapIndexedNotNull { index, element ->
        element.toTrendPoint(index)
    }
}

private fun JsonElement.toTrendPoint(index: Int): TrendPoint? {
    if (isJsonPrimitive) {
        val value = asJsonPrimitive.takeIf { it.isNumber }?.asDouble ?: return null
        return TrendPoint(profileId = "", createdAt = index.toString(), value = value)
    }

    if (!isJsonObject) return null
    val obj = asJsonObject
    val value = obj.doubleValue("value", "fat", "y") ?: return null
    val createdAt = obj.stringValue("createdAt", "created_at", "date", "month") ?: index.toString()
    val profileId = obj.stringValue("profileId", "profile_id") ?: ""
    return TrendPoint(profileId = profileId, createdAt = createdAt, value = value)
}

private fun JsonObject.stringValue(vararg keys: String): String? {
    return keys.firstNotNullOfOrNull { key ->
        get(key)?.takeIf { !it.isJsonNull && it.isJsonPrimitive }?.asString
    }
}

private fun Map<String, List<TrendPoint>>.latestValue(metric: String): Double? {
    return this[metric]?.maxByOrNull { it.createdAt }?.value
}
