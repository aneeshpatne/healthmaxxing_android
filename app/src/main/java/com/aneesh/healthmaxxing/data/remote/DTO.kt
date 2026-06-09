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

data class BodyCompositionTrendResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("metric") val metric: String,
    @SerializedName("period") val period: String,
    @SerializedName("profileId") val profileId: String?,
    @SerializedName("points") val points: List<TrendPoint>,
    @SerializedName("error") val error: String? = null
)

data class TrendPoint(
    @SerializedName("profileId") val profileId: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("value") val value: Double
)

data class ProfileEssentialsResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("profileId") val profileId: String,
    @SerializedName("essentials") val essentials: Essentials
)

data class Essentials(
    @SerializedName("formaScore") val formaScore: FormaScore,
    @SerializedName("bodyAge") val bodyAge: Int,
    @SerializedName("realAge") val realAge: Int,
    @SerializedName("compositionSummary") val compositionSummary: CompositionSummary,
    @SerializedName("measurements") val measurements: Measurements,
    @SerializedName("currentWeight") val currentWeight: Double,
    @SerializedName("goalWeight") val goalWeight: Double,
    @SerializedName("averageWeight30d") val averageWeight30d: Double,
    @SerializedName("lowestWeight30d") val lowestWeight30d: Double,
    @SerializedName("last30DaysWeightTrend") val last30DaysWeightTrend: List<WeightTrend>
)

data class FormaScore(
    @SerializedName("score") val score: Int,
    @SerializedName("remark") val remark: String
)

data class CompositionSummary(
    @SerializedName("body_fat_pct") val bodyFatPct: Double,
    @SerializedName("lean_mass_pct") val leanMassPct: Double,
    @SerializedName("protein_pct") val proteinPct: Double,
    @SerializedName("hydration_pct") val hydrationPct: Double,
    @SerializedName("muscle_mass_pct") val muscleMassPct: Double,
    @SerializedName("composition_score") val compositionScore: Int
)

data class Measurements(
    @SerializedName("id") val id: String,
    @SerializedName("neckCm") val neckCm: Double,
    @SerializedName("shoulderCm") val shoulderCm: Double,
    @SerializedName("chestCm") val chestCm: Double,
    @SerializedName("stomachCm") val stomachCm: Double,
    @SerializedName("waistCm") val waistCm: Double,
    @SerializedName("calfCm") val calfCm: Double,
    @SerializedName("thighCm") val thighCm: Double,
    @SerializedName("bicepCm") val bicepCm: Double,
    @SerializedName("forearmCm") val forearmCm: Double,
    @SerializedName("createdAt") val createdAt: String
)

data class WeightTrend(
    @SerializedName("weight") val weight: Double,
    @SerializedName("createdAt") val createdAt: String
)

data class PerformanceResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("profileId") val profileId: String,
    @SerializedName("performance") val performance: Performance
)

data class Performance(
    @SerializedName("ffmi") val ffmi: Double,
    @SerializedName("ffmiVsFmi") val ffmiVsFmi: FfmiVsFmi,
    @SerializedName("bodyComposition") val bodyComposition: BodyCompositionData,
    @SerializedName("compositionTrends") val compositionTrends: CompositionTrends,
    @SerializedName("weightPair") val weightPair: WeightPair,
    @SerializedName("excessFatGauge") val excessFatGauge: ExcessFatGauge,
    @SerializedName("bodyMeasurements") val bodyMeasurements: List<Measurements>,
    @SerializedName("lastBodyRatios") val lastBodyRatios: LastBodyRatios,
    @SerializedName("comments") val comments: PerformanceComments
)

data class FfmiVsFmi(
    @SerializedName("ffmi") val ffmi: Double,
    @SerializedName("fmi") val fmi: Double
)

data class BodyCompositionData(
    @SerializedName("leanMassKg") val leanMassKg: Double,
    @SerializedName("fatMassKg") val fatMassKg: Double
)

data class CompositionTrends(
    @SerializedName("leanMass30Days") val leanMass30Days: List<CompositionTrendPoint>,
    @SerializedName("fatMass30Days") val fatMass30Days: List<CompositionTrendPoint>
)

data class CompositionTrendPoint(
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("value") val value: Double
)

data class WeightPair(
    @SerializedName("target") val target: BodyCompositionData,
    @SerializedName("current") val current: BodyCompositionData,
    @SerializedName("initial") val initial: BodyCompositionData
)

data class ExcessFatGauge(
    @SerializedName("totalFatKg") val totalFatKg: Double,
    @SerializedName("targetFatKg") val targetFatKg: Double,
    @SerializedName("excessFatKg") val excessFatKg: Double
)

data class LastBodyRatios(
    @SerializedName("waistHeight") val waistHeight: Double,
    @SerializedName("shoulderWaist") val shoulderWaist: Double,
    @SerializedName("chestWaist") val chestWaist: Double,
    @SerializedName("bicepForearm") val bicepForearm: Double,
    @SerializedName("thighCalf") val thighCalf: Double,
    @SerializedName("neckCalf") val neckCalf: Double
)

data class PerformanceComments(
    @SerializedName("ffmi") val ffmi: PerformanceComment?,
    @SerializedName("ffmiVsFmi") val ffmiVsFmi: PerformanceComment?,
    @SerializedName("compositionFlow") val compositionFlow: PerformanceComment?,
    @SerializedName("compositionTrend") val compositionTrend: PerformanceComment?,
    @SerializedName("recompVector") val recompVector: PerformanceComment?,
    @SerializedName("excessFatGauge") val excessFatGauge: PerformanceComment?,
    @SerializedName("bodyRatios") val bodyRatios: BodyRatioComments?,
    @SerializedName("shoulderWaist") val shoulderWaist: PerformanceComment?,
    @SerializedName("chestWaist") val chestWaist: PerformanceComment?,
    @SerializedName("bicepForearm") val bicepForearm: PerformanceComment?,
    @SerializedName("thighCalf") val thighCalf: PerformanceComment?,
    @SerializedName("neckCalf") val neckCalf: PerformanceComment?
)

data class BodyRatioComments(
    @SerializedName("waistHeight") val waistHeight: PerformanceComment?,
    @SerializedName("shoulderWaist") val shoulderWaist: PerformanceComment?,
    @SerializedName("chestWaist") val chestWaist: PerformanceComment?,
    @SerializedName("bicepForearm") val bicepForearm: PerformanceComment?,
    @SerializedName("thighCalf") val thighCalf: PerformanceComment?,
    @SerializedName("neckCalf") val neckCalf: PerformanceComment?
)

data class PerformanceComment(
    @SerializedName("remark") val remark: String?,
    @SerializedName("comment") val comment: String?
)
