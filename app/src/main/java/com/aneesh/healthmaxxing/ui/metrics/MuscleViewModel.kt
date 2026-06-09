package com.aneesh.healthmaxxing.ui.metrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aneesh.healthmaxxing.data.datastore.AccountPreferences
import com.aneesh.healthmaxxing.data.remote.TrendPoint
import com.aneesh.healthmaxxing.repository.MuscleRepository
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

const val MUSCLE_METRIC_BONE_MASS_KG = "bone_mass_kg"
const val MUSCLE_METRIC_MUSCLE_RATIO = "muscle_ratio"
const val MUSCLE_METRIC_SKELETAL_MUSCLE_MASS_KG = "skeletal_muscle_mass_kg"
const val MUSCLE_METRIC_SKELETAL_MUSCLE_RATIO = "skeletal_muscle_ratio"

data class MuscleUiState(
    val totalMuscleKg: Double? = null,
    val boneMassKg: Double? = null,
    val muscleRatio: Double? = null,
    val skeletalMuscleMassKg: Double? = null,
    val skeletalMuscleRatio: Double? = null,
    val comments: MuscleComments = MuscleComments(),
    val trends: Map<String, List<TrendPoint>> = emptyMap()
)

data class MuscleComments(
    val totalMuscle: MuscleComment? = null,
    val boneMass: MuscleComment? = null,
    val muscleRatio: MuscleComment? = null,
    val skeletalMuscleMass: MuscleComment? = null,
    val skeletalMuscleRatio: MuscleComment? = null
)

data class MuscleComment(
    val remark: String? = null,
    val comment: String? = null
)

@HiltViewModel
class MuscleViewModel @Inject constructor(
    private val muscleRepository: MuscleRepository,
    private val accountPreferences: AccountPreferences
) : ViewModel() {
    private val _muscle = MutableStateFlow(MuscleUiState())
    val muscle = _muscle.asStateFlow()

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
                getMuscle(profileId)
            } else {
                _error.value = "Profile ID is missing."
                _isLoading.value = false
            }
            if (isUserInitiated) _isRefreshing.value = false
        }
    }

    private suspend fun getMuscle(profileId: String) {
        _isLoading.value = true
        _error.value = null
        try {
            val response = muscleRepository.getMuscle(profileId)
            if (response.isSuccessful) {
                _muscle.value = response.body()?.toMuscleUiState() ?: MuscleUiState()
            } else {
                _error.value = "Failed to load muscle metrics: ${response.message()}"
            }
        } catch (e: Exception) {
            _error.value = e.localizedMessage ?: "An error occurred"
        } finally {
            _isLoading.value = false
        }
    }
}

private fun JsonObject.toMuscleUiState(): MuscleUiState {
    val payload = getObject("muscle") ?: getObject("data") ?: this
    val metrics = payload.getObject("metrics") ?: payload
    val trendsPayload = payload.getObject("last30Days") ?: payload.getObject("trends") ?: payload
    val trends = mapOf(
        MUSCLE_METRIC_BONE_MASS_KG to trendsPayload.extractTrend("boneMassKg"),
        MUSCLE_METRIC_MUSCLE_RATIO to trendsPayload.extractTrend("muscleRatio"),
        MUSCLE_METRIC_SKELETAL_MUSCLE_MASS_KG to trendsPayload.extractTrend("skeletalMuscleMassKg"),
        MUSCLE_METRIC_SKELETAL_MUSCLE_RATIO to trendsPayload.extractTrend("skeletalMuscleRatio")
    ).filterValues { it.isNotEmpty() }

    return MuscleUiState(
        totalMuscleKg = metrics.doubleValue("totalMuscleKg"),
        boneMassKg = metrics.doubleValue("boneMassKg") ?: trends.latestValue(MUSCLE_METRIC_BONE_MASS_KG),
        muscleRatio = metrics.doubleValue("muscleRatio") ?: trends.latestValue(MUSCLE_METRIC_MUSCLE_RATIO),
        skeletalMuscleMassKg = metrics.doubleValue("skeletalMuscleMassKg")
            ?: trends.latestValue(MUSCLE_METRIC_SKELETAL_MUSCLE_MASS_KG),
        skeletalMuscleRatio = metrics.doubleValue("skeletalMuscleRatio")
            ?: trends.latestValue(MUSCLE_METRIC_SKELETAL_MUSCLE_RATIO),
        comments = (payload.getObject("comments") ?: getObject("comments")).toMuscleComments(),
        trends = trends
    )
}

private fun JsonObject?.toMuscleComments(): MuscleComments {
    if (this == null) return MuscleComments()
    return MuscleComments(
        totalMuscle = getComment("totalMuscle"),
        boneMass = getComment("boneMass"),
        muscleRatio = getComment("muscleRatio"),
        skeletalMuscleMass = getComment("skeletalMuscleMass"),
        skeletalMuscleRatio = getComment("skeletalMuscleRatio")
    )
}

private fun JsonObject.getComment(key: String): MuscleComment? {
    val obj = getObject(key) ?: return null
    return MuscleComment(
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
    if (!isJsonObject) return null
    val obj = asJsonObject
    val value = obj.doubleValue("value") ?: return null
    val createdAt = obj.stringValue("createdAt", "created_at", "date") ?: index.toString()
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
