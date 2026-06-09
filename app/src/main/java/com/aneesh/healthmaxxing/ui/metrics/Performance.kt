package com.aneesh.healthmaxxing.ui.metrics

import android.graphics.Paint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextAlign
import com.aneesh.healthmaxxing.data.remote.PerformanceResponse
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aneesh.healthmaxxing.data.remote.Measurements
import com.aneesh.healthmaxxing.data.remote.PerformanceComment
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

private val TextPrimary = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val CardBorder = Color(0xFFE6EEF2)
private val CardBackground = Color.White.copy(alpha = 0.92f)
private val AccentPurple = Color(0xFF6D5DF6)

private fun compactTextStyle() = TextStyle(
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)

private fun PerformanceComment?.commentText(fallback: String): String {
    return this?.comment?.takeIf { it.isNotBlank() } ?: fallback
}

private fun PerformanceComment?.remarkCommentText(fallback: String): String {
    val comment = this?.comment?.takeIf { it.isNotBlank() } ?: return fallback
    val remark = this.remark?.takeIf { it.isNotBlank() }
    return if (remark != null) "$remark: $comment" else comment
}

@Composable
private fun PerformanceTextRemarkSection(
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HorizontalDivider(
            color = CardBorder,
            thickness = 1.dp
        )

        Text(
            text = text,
            color = TextSecondary,
            fontSize = 11.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.Normal,
            style = compactTextStyle()
        )
    }
}

data class GaugeRange(
    val label: String,
    val min: Float,
    val max: Float,
    val rangeText: String,
    val arcColor: Color,
    val arcGradient: Brush,
    val legendTextColor: Color,
    val legendBackgroundColor: Color,
    val legendBorderColor: Color
)

@Composable
fun FfmiGaugeComponent(
    value: Float = 19.9f,
    remarkText: String = "FFMI highlights how much fat-free mass you carry for your height; higher values usually reflect stronger lean-mass development.",
    modifier: Modifier = Modifier
) {
    val minValue = 15f
    val maxValue = 23.5f
    val clampedValue = value.coerceIn(minValue, maxValue)

    val ranges = listOf(
        GaugeRange(
            label = "Low", min = 15f, max = 17f, rangeText = "< 17",
            arcColor = Color(0xFFFF5A6A),
            arcGradient = Brush.linearGradient(listOf(Color(0xFFFF8A95), Color(0xFFFF5A6A))),
            legendTextColor = Color(0xFFE03144),
            legendBackgroundColor = Color(0xFFFFF0F2),
            legendBorderColor = Color(0xFFFFD5DC)
        ),
        GaugeRange(
            label = "Average", min = 17f, max = 18.5f, rangeText = "17 – 18.5",
            arcColor = Color(0xFFF47B65),
            arcGradient = Brush.linearGradient(listOf(Color(0xFFFFA08B), Color(0xFFF47B65))),
            legendTextColor = Color(0xFFD65840),
            legendBackgroundColor = Color(0xFFFFF1EC),
            legendBorderColor = Color(0xFFFFD9CC)
        ),
        GaugeRange(
            label = "Fit", min = 18.5f, max = 20.5f, rangeText = "18.5 – 20.5",
            arcColor = Color(0xFF7B4FE8),
            arcGradient = Brush.linearGradient(listOf(Color(0xFFA180FF), Color(0xFF7B4FE8))),
            legendTextColor = Color(0xFF6338CE),
            legendBackgroundColor = Color(0xFFF3EEFF),
            legendBorderColor = Color(0xFFDCD2FE)
        ),
        GaugeRange(
            label = "Athletic", min = 20.5f, max = 22.5f, rangeText = "20.5 – 22.5",
            arcColor = Color(0xFF4C9EEB),
            arcGradient = Brush.linearGradient(listOf(Color(0xFF80C1FF), Color(0xFF4C9EEB))),
            legendTextColor = Color(0xFF2B83D6),
            legendBackgroundColor = Color(0xFFEDF6FF),
            legendBorderColor = Color(0xFFCBE3FA)
        ),
        GaugeRange(
            label = "Elite", min = 22.5f, max = 23.5f, rangeText = "> 22.5",
            arcColor = Color(0xFF45A96B),
            arcGradient = Brush.linearGradient(listOf(Color(0xFF6DCC91), Color(0xFF45A96B))),
            legendTextColor = Color(0xFF2E8550),
            legendBackgroundColor = Color(0xFFEFFFF4),
            legendBorderColor = Color(0xFFCDEFD9)
        )
    )

    val activeIndex =
        ranges.indexOfFirst { clampedValue >= it.min && clampedValue <= it.max }.takeIf { it >= 0 }
            ?: 2
    val activeRange = ranges[activeIndex]

    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(clampedValue) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        )
    }

    val currentAnimatedValue = minValue + (clampedValue - minValue) * animationProgress.value

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.03f),
                spotColor = Color.Black.copy(alpha = 0.03f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(CardBackground)
            .border(
                width = 1.dp,
                color = CardBorder,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "FFMI GAUGE",
                    color = AccentPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    style = compactTextStyle()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Fat-Free Mass Index (FFMI) measures your muscle mass relative to height.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Gauge Box
            Box(
                modifier = Modifier
                    .width(280.dp)
                    .height(160.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val strokeWidthPx = 18.dp.toPx()
                    val radius = (canvasWidth - strokeWidthPx - 40.dp.toPx()) / 2f
                    val centerY = canvasHeight - 20.dp.toPx()
                    val centerX = canvasWidth / 2f

                    // Draw background track
                    drawArc(
                        color = Color(0xFFF1F5F9),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(centerX - radius, centerY - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidthPx, cap = StrokeCap.Butt)
                    )

                    // Draw the segments with a small gap
                    val gapAngle = 2.5f
                    ranges.forEach { range ->
                        val segmentStartProgress = (range.min - minValue) / (maxValue - minValue)
                        val segmentEndProgress = (range.max - minValue) / (maxValue - minValue)

                        val segmentStartAngle = 180f + segmentStartProgress * 180f
                        val segmentSweepAngle = (segmentEndProgress - segmentStartProgress) * 180f

                        val isFirst = range == ranges.first()
                        val isLast = range == ranges.last()

                        val actualStartAngle =
                            segmentStartAngle + (if (isFirst) 0f else gapAngle / 2f)
                        val actualSweepAngle =
                            segmentSweepAngle - (if (isFirst || isLast) gapAngle / 2f else gapAngle)

                        drawArc(
                            brush = range.arcGradient,
                            startAngle = actualStartAngle,
                            sweepAngle = actualSweepAngle,
                            useCenter = false,
                            topLeft = Offset(centerX - radius, centerY - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Butt)
                        )
                    }

                    // Draw indicator thumb (animated)
                    val progress =
                        ((currentAnimatedValue - minValue) / (maxValue - minValue)).coerceIn(0f, 1f)
                    val markerAngle = 180f + progress * 180f
                    val angleRad = Math.toRadians(markerAngle.toDouble())

                    val thumbRadius = 14.dp.toPx()
                    val thumbX = centerX + radius * cos(angleRad).toFloat()
                    val thumbY = centerY + radius * sin(angleRad).toFloat()

                    // Thumb Shadow
                    drawContext.canvas.nativeCanvas.apply {
                        drawCircle(
                            thumbX,
                            thumbY,
                            thumbRadius,
                            android.graphics.Paint().apply {
                                color = android.graphics.Color.WHITE
                                setShadowLayer(
                                    16f,
                                    0f,
                                    8f,
                                    android.graphics.Color.argb(40, 0, 0, 0)
                                )
                            }
                        )
                    }

                    // Thumb Outer Circle (White)
                    drawCircle(
                        color = Color.White,
                        radius = thumbRadius,
                        center = Offset(thumbX, thumbY)
                    )

                    // Thumb Inner Circle (Color matching active range)
                    drawCircle(
                        color = activeRange.arcColor,
                        radius = thumbRadius * 0.45f,
                        center = Offset(thumbX, thumbY)
                    )

                    // Draw tick labels (15, 17, 19, 21, 23)
                    val ticks = listOf(15, 17, 19, 21, 23)
                    ticks.forEach { tick ->
                        val tickProgress = (tick - minValue) / (maxValue - minValue)
                        val tickAngle = 180f + tickProgress * 180f
                        val tickRad = Math.toRadians(tickAngle.toDouble())
                        val labelRadius = radius + 26.dp.toPx()
                        val labelX = centerX + labelRadius * cos(tickRad).toFloat()
                        val labelY = centerY + labelRadius * sin(tickRad).toFloat() + 4.dp.toPx()

                        drawContext.canvas.nativeCanvas.drawText(
                            tick.toString(),
                            labelX,
                            labelY,
                            android.graphics.Paint().apply {
                                color = TextSecondary.copy(alpha = 0.6f).toArgb()
                                textSize = 10.sp.toPx()
                                textAlign = android.graphics.Paint.Align.CENTER
                                isAntiAlias = true
                                typeface = android.graphics.Typeface.create(
                                    android.graphics.Typeface.DEFAULT,
                                    android.graphics.Typeface.BOLD
                                )
                            }
                        )
                    }
                }

                // Center Column (Numeric value & label)
                Column(
                    modifier = Modifier.padding(bottom = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "%.1f".format(currentAnimatedValue),
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        style = compactTextStyle(),
                        letterSpacing = (-1).sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "YOUR FFMI",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp,
                        style = compactTextStyle()
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Legend Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ranges.forEach { range ->
                    val isActive = range.label == activeRange.label
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .fillMaxWidth(0.8f)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (isActive) range.arcGradient else Brush.linearGradient(
                                        listOf(Color(0xFFF1F5F9), Color(0xFFF1F5F9))
                                    )
                                )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = range.label,
                            fontSize = 10.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                            color = if (isActive) TextPrimary else TextSecondary,
                            style = compactTextStyle()
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = range.rangeText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary.copy(alpha = 0.6f),
                            style = compactTextStyle()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PerformanceTextRemarkSection(
                text = remarkText
            )
        }
    }
}
@Composable
fun Performance(
    performanceResponse: PerformanceResponse? = null,
    isLoading: Boolean = false,
    error: String? = null,
    modifier: Modifier = Modifier
) {
    val dummyMeasurements = Measurements(
        id = "dummy",
        neckCm = 38.0,
        shoulderCm = 115.0,
        chestCm = 100.0,
        stomachCm = 85.0,
        waistCm = 80.0,
        calfCm = 36.0,
        thighCm = 55.0,
        bicepCm = 35.0,
        forearmCm = 28.0,
        createdAt = "2026-06-08T10:47:00Z"
    )
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        if (isLoading && performanceResponse == null) {
            // We can add a shimmer here later, for now just a text
            Text(
                text = "Loading performance...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                textAlign = TextAlign.Center
            )
        } else if (error != null && performanceResponse == null) {
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                textAlign = TextAlign.Center
            )
        } else if (performanceResponse != null) {
            val perf = performanceResponse.performance
            val comments = perf.comments
            FfmiGaugeComponent(
                value = perf.ffmi.toFloat(),
                remarkText = comments.ffmi.commentText("FFMI highlights how much fat-free mass you carry for your height; higher values usually reflect stronger lean-mass development.")
            )
            
            FfmiVsFmiChartComponent(
                userPoint = BodyCompositionPoint(
                    ffmi = perf.ffmiVsFmi.ffmi.toFloat(),
                    fmi = perf.ffmiVsFmi.fmi.toFloat()
                ),
                remarkText = comments.ffmiVsFmi.commentText("The best zone combines higher FFMI with lower FMI, showing more lean tissue without carrying excess fat mass.")
            )

            BodyCompositionSankeyComponent(
                data = BodyComposition(
                    totalWeight = (perf.bodyComposition.leanMassKg + perf.bodyComposition.fatMassKg).toFloat(),
                    leanMass = perf.bodyComposition.leanMassKg.toFloat(),
                    fatMass = perf.bodyComposition.fatMassKg.toFloat()
                ),
                remarkText = comments.compositionFlow.commentText("This flow separates your scale weight into lean and fat mass so progress is judged by composition, not weight alone.")
            )

            val trendPoints = perf.compositionTrends.leanMass30Days.zip(perf.compositionTrends.fatMass30Days).map { (lean, fat) ->
                val dateStr = lean.createdAt.substringBefore(" ")
                TrendDataPoint(
                    month = dateStr.substringAfter("-"), 
                    leanMass = lean.value.toFloat(), 
                    fatMass = fat.value.toFloat()
                )
            }
            if (trendPoints.isNotEmpty()) {
                LeanFatTrendChartComponent(
                    data = trendPoints,
                    remarkText = comments.compositionTrend.commentText("A strong trend shows lean mass moving up while fat mass moves down or stays controlled over the same period.")
                )
            } else {
                LeanFatTrendChartComponent(
                    remarkText = comments.compositionTrend.commentText("A strong trend shows lean mass moving up while fat mass moves down or stays controlled over the same period.")
                )
            }

            RecompVectorPlotComponent(
                start = RecompPoint(
                    fatMass = perf.weightPair.initial.fatMassKg.toFloat(),
                    leanMass = perf.weightPair.initial.leanMassKg.toFloat()
                ),
                current = RecompPoint(
                    fatMass = perf.weightPair.current.fatMassKg.toFloat(),
                    leanMass = perf.weightPair.current.leanMassKg.toFloat()
                ),
                target = RecompPoint(
                    fatMass = perf.weightPair.target.fatMassKg.toFloat(),
                    leanMass = perf.weightPair.target.leanMassKg.toFloat()
                ),
                remarkText = comments.recompVector.commentText("The ideal recomp path moves left and upward: less fat mass with equal or greater lean mass.")
            )

            ExcessFatGaugeComponent(
                data = ExcessFatGaugeData(
                    currentFatMassKg = perf.excessFatGauge.totalFatKg.toFloat(),
                    targetFatMassKg = perf.excessFatGauge.targetFatKg.toFloat()
                ),
                remarkText = comments.excessFatGauge.commentText("Excess fat is the gap between current fat mass and target fat mass; reducing it improves composition without guessing from body weight.")
            )

            val measurementsToUse = perf.bodyMeasurements.firstOrNull() ?: dummyMeasurements
            BodyMeasurementsPanel(measurements = measurementsToUse)
            BodyMeasurementRatiosPanel(
                measurements = measurementsToUse,
                heightCm = 175.0, // Height is generally stored in metadata/essentials
                waistHeightRatio = perf.lastBodyRatios.waistHeight,
                shoulderWaistRatio = perf.lastBodyRatios.shoulderWaist,
                chestWaistRatio = perf.lastBodyRatios.chestWaist,
                bicepForearmRatio = perf.lastBodyRatios.bicepForearm,
                thighCalfRatio = perf.lastBodyRatios.thighCalf,
                neckCalfRatio = perf.lastBodyRatios.neckCalf,
                waistHeightRemark = comments.bodyRatios?.waistHeight.remarkCommentText("Waist-to-height ratio compares waist size against stature."),
                shoulderWaistRemark = comments.bodyRatios?.shoulderWaist.remarkCommentText("Shoulder-to-waist ratio shows your taper."),
                chestWaistRemark = comments.bodyRatios?.chestWaist.remarkCommentText("Chest-to-waist ratio shows torso balance."),
                bicepForearmRemark = comments.bodyRatios?.bicepForearm.remarkCommentText("Bicep-to-forearm ratio shows arm balance."),
                thighCalfRemark = comments.bodyRatios?.thighCalf.remarkCommentText("Thigh-to-calf ratio shows lower-body balance."),
                neckCalfRemark = comments.bodyRatios?.neckCalf.remarkCommentText("Neck-to-calf ratio reflects classic proportional balance.")
            )
        }
    }
}

data class BodyCompositionPoint(
    val ffmi: Float,
    val fmi: Float
)

@Composable
fun FfmiVsFmiChartComponent(
    userPoint: BodyCompositionPoint = BodyCompositionPoint(19.9f, 6.2f),
    remarkText: String = "The best zone combines higher FFMI with lower FMI, showing more lean tissue without carrying excess fat mass.",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.03f),
                spotColor = Color.Black.copy(alpha = 0.03f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(CardBackground)
            .border(
                width = 1.dp,
                color = CardBorder,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "COMPOSITION MAP",
                    color = AccentPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    style = compactTextStyle()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Compare your Fat-Free Mass Index (FFMI) against your Fat Mass Index (FMI).",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Chart Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    val paddingBottom = 44.dp.toPx()
                    val paddingLeft = 44.dp.toPx()
                    val paddingTop = 12.dp.toPx()
                    val paddingRight = 12.dp.toPx()

                    val chartWidth = canvasWidth - paddingLeft - paddingRight
                    val chartHeight = canvasHeight - paddingTop - paddingBottom

                    val xMin = 14f
                    val xMax = 26f
                    val yMin = 0f
                    val yMax = 12f

                    val thresholdX = 20f
                    val thresholdY = 6f

                    fun getX(value: Float): Float {
                        return paddingLeft + ((value - xMin) / (xMax - xMin)) * chartWidth
                    }

                    fun getY(value: Float): Float {
                        return paddingTop + chartHeight - ((value - yMin) / (yMax - yMin)) * chartHeight
                    }

                    val divX = getX(thresholdX)
                    val divY = getY(thresholdY)

                    val cornerRadius = 16.dp.toPx()

                    // Draw Chart Background
                    drawRoundRect(
                        color = Color(0xFFF8FAFC),
                        topLeft = Offset(paddingLeft, paddingTop),
                        size = Size(chartWidth, chartHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                            cornerRadius,
                            cornerRadius
                        )
                    )

                    // Draw Quadrants (with clipping so they stay inside the rounded rect)
                    val clipPath = androidx.compose.ui.graphics.Path().apply {
                        addRoundRect(
                            androidx.compose.ui.geometry.RoundRect(
                                rect = androidx.compose.ui.geometry.Rect(
                                    paddingLeft,
                                    paddingTop,
                                    paddingLeft + chartWidth,
                                    paddingTop + chartHeight
                                ),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                                    cornerRadius,
                                    cornerRadius
                                )
                            )
                        )
                    }
                    drawContext.canvas.save()
                    drawContext.canvas.clipPath(clipPath)

                    // Top Left (Skinny Fat)
                    drawRect(
                        color = Color(0xFFFFF1F2), // Red tint
                        topLeft = Offset(paddingLeft, paddingTop),
                        size = Size(divX - paddingLeft, divY - paddingTop)
                    )
                    // Top Right (Big & Muscular)
                    drawRect(
                        color = Color(0xFFFFF7ED), // Orange tint
                        topLeft = Offset(divX, paddingTop),
                        size = Size(paddingLeft + chartWidth - divX, divY - paddingTop)
                    )
                    // Bottom Left (Lean)
                    drawRect(
                        color = Color(0xFFF0FDF4), // Green tint
                        topLeft = Offset(paddingLeft, divY),
                        size = Size(divX - paddingLeft, paddingTop + chartHeight - divY)
                    )
                    // Bottom Right (Athletic)
                    drawRect(
                        color = Color(0xFFEFF6FF), // Blue tint
                        topLeft = Offset(divX, divY),
                        size = Size(
                            paddingLeft + chartWidth - divX,
                            paddingTop + chartHeight - divY
                        )
                    )

                    // Draw Divider Lines
                    val dashPathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                        floatArrayOf(
                            15f,
                            15f
                        ), 0f
                    )
                    drawLine(
                        color = Color(0x22000000),
                        start = Offset(divX, paddingTop),
                        end = Offset(divX, paddingTop + chartHeight),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = dashPathEffect
                    )
                    drawLine(
                        color = Color(0x22000000),
                        start = Offset(paddingLeft, divY),
                        end = Offset(paddingLeft + chartWidth, divY),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = dashPathEffect
                    )
                    drawContext.canvas.restore()

                    // Chart Border
                    drawRoundRect(
                        color = Color(0xFFE2E8F0),
                        topLeft = Offset(paddingLeft, paddingTop),
                        size = Size(chartWidth, chartHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                            cornerRadius,
                            cornerRadius
                        ),
                        style = Stroke(width = 1.dp.toPx())
                    )

                    // Axis Ticks & Labels
                    val paintText = android.graphics.Paint().apply {
                        color = TextSecondary.toArgb()
                        textSize = 10.sp.toPx()
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = android.graphics.Typeface.create(
                            android.graphics.Typeface.DEFAULT,
                            android.graphics.Typeface.BOLD
                        )
                    }

                    // X-axis
                    val xTicks = listOf(14, 16, 18, 20, 22, 24, 26)
                    xTicks.forEach { tick ->
                        val x = getX(tick.toFloat())
                        val y = paddingTop + chartHeight + 14.dp.toPx()
                        drawContext.canvas.nativeCanvas.drawText(tick.toString(), x, y, paintText)
                    }

                    // Y-axis
                    val yTicks = listOf(0, 3, 6, 9, 12)
                    paintText.textAlign = android.graphics.Paint.Align.RIGHT
                    yTicks.forEach { tick ->
                        val x = paddingLeft - 8.dp.toPx()
                        val y = getY(tick.toFloat()) + 4.dp.toPx()
                        drawContext.canvas.nativeCanvas.drawText(tick.toString(), x, y, paintText)
                    }

                    // Quadrant Labels
                    val quadLabelPaint = android.graphics.Paint().apply {
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.CENTER
                    }

                    fun drawQuadLabel(
                        title: String,
                        subtitle: String,
                        cx: Float,
                        cy: Float,
                        colorStr: String
                    ) {
                        quadLabelPaint.color = android.graphics.Color.parseColor(colorStr)

                        quadLabelPaint.textSize = 13.sp.toPx()
                        quadLabelPaint.typeface = android.graphics.Typeface.create(
                            android.graphics.Typeface.DEFAULT,
                            android.graphics.Typeface.BOLD
                        )
                        drawContext.canvas.nativeCanvas.drawText(
                            title,
                            cx,
                            cy - 4.dp.toPx(),
                            quadLabelPaint
                        )

                        quadLabelPaint.color = TextSecondary.copy(alpha = 0.8f).toArgb()
                        quadLabelPaint.textSize = 9.sp.toPx()
                        quadLabelPaint.typeface = android.graphics.Typeface.create(
                            android.graphics.Typeface.DEFAULT,
                            android.graphics.Typeface.NORMAL
                        )
                        drawContext.canvas.nativeCanvas.drawText(
                            subtitle,
                            cx,
                            cy + 12.dp.toPx(),
                            quadLabelPaint
                        )
                    }

                    drawQuadLabel(
                        "Skinny Fat",
                        "High Fat • Low Muscle",
                        paddingLeft + (divX - paddingLeft) / 2f,
                        paddingTop + (divY - paddingTop) / 2f,
                        "#E11D48"
                    )
                    drawQuadLabel(
                        "Big & Muscular",
                        "High Fat • High Muscle",
                        divX + (paddingLeft + chartWidth - divX) / 2f,
                        paddingTop + (divY - paddingTop) / 2f,
                        "#EA580C"
                    )
                    drawQuadLabel(
                        "Lean",
                        "Low Fat • Low Muscle",
                        paddingLeft + (divX - paddingLeft) / 2f,
                        divY + (paddingTop + chartHeight - divY) / 2f,
                        "#16A34A"
                    )
                    drawQuadLabel(
                        "Athletic",
                        "Low Fat • High Muscle",
                        divX + (paddingLeft + chartWidth - divX) / 2f,
                        divY + (paddingTop + chartHeight - divY) / 2f,
                        "#2563EB"
                    )

                    // X-axis title
                    val titlePaint = android.graphics.Paint().apply {
                        color = TextSecondary.toArgb()
                        textSize = 11.sp.toPx()
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = android.graphics.Typeface.create(
                            android.graphics.Typeface.DEFAULT,
                            android.graphics.Typeface.BOLD
                        )
                    }
                    drawContext.canvas.nativeCanvas.drawText(
                        "FFMI (Muscle Mass)",
                        paddingLeft + chartWidth / 2f,
                        canvasHeight - 4.dp.toPx(),
                        titlePaint
                    )

                    // Y-axis title
                    drawContext.canvas.nativeCanvas.save()
                    drawContext.canvas.nativeCanvas.translate(
                        12.dp.toPx(),
                        paddingTop + chartHeight / 2f
                    )
                    drawContext.canvas.nativeCanvas.rotate(-90f)
                    drawContext.canvas.nativeCanvas.drawText("FMI (Fat Mass)", 0f, 0f, titlePaint)
                    drawContext.canvas.nativeCanvas.restore()

                    // User Scatter Point
                    val pointX = getX(userPoint.ffmi)
                    val pointY = getY(userPoint.fmi)
                    val radius = 8.dp.toPx()

                    drawContext.canvas.nativeCanvas.apply {
                        drawCircle(
                            pointX,
                            pointY,
                            radius,
                            android.graphics.Paint().apply {
                                color = AccentPurple.toArgb()
                                setShadowLayer(
                                    12f,
                                    0f,
                                    6f,
                                    android.graphics.Color.argb(80, 109, 93, 246)
                                )
                                isAntiAlias = true
                            }
                        )
                    }
                    drawCircle(
                        color = Color.White,
                        radius = radius,
                        center = Offset(pointX, pointY),
                        style = Stroke(width = 3.dp.toPx())
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Legend Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Marker
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(AccentPurple)
                    )
                    Text(
                        text = "Your Position (${userPoint.ffmi}, ${userPoint.fmi})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        style = compactTextStyle()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PerformanceTextRemarkSection(
                text = remarkText
            )
        }
    }
}

data class BodyComposition(
    val totalWeight: Float,
    val leanMass: Float,
    val fatMass: Float
)

@Composable
fun BodyCompositionSankeyComponent(
    data: BodyComposition = BodyComposition(
        totalWeight = 77.25f,
        leanMass = 58.86f,
        fatMass = 18.39f
    ),
    remarkText: String = "This flow separates your scale weight into lean and fat mass so progress is judged by composition, not weight alone.",
    modifier: Modifier = Modifier
) {
    val leanPct = data.leanMass / data.totalWeight
    val fatPct = data.fatMass / data.totalWeight

    // Animations
    val ribbonProgress = remember { Animatable(0f) }
    val destinationAlpha = remember { Animatable(0f) }

    LaunchedEffect(data) {
        ribbonProgress.snapTo(0f)
        destinationAlpha.snapTo(0f)
        ribbonProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
        destinationAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.03f),
                spotColor = Color.Black.copy(alpha = 0.03f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(CardBackground)
            .border(
                width = 1.dp,
                color = CardBorder,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "BODY COMPOSITION FLOW",
                    color = AccentPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    style = compactTextStyle()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Visualize the breakdown of your total body weight into lean mass and fat mass.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Sankey Diagram Area
            val containerHeight = 160.dp
            val leftWidth = 100.dp
            val leftHeight = 80.dp
            val rightWidth = 110.dp
            val rightHeight = 65.dp
            val rightGap = 12.dp

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(containerHeight)
            ) {
                // Canvas for Flow Ribbons
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val w = size.width
                    val h = size.height

                    val leftWidthPx = leftWidth.toPx()
                    val leftHeightPx = leftHeight.toPx()
                    val rightWidthPx = rightWidth.toPx()
                    val rightHeightPx = rightHeight.toPx()
                    val rightGapPx = rightGap.toPx()

                    val overlap = 16.dp.toPx()
                    val xStart = leftWidthPx - overlap
                    val xEnd = w - rightWidthPx + overlap
                    val dx = xEnd - xStart

                    val yLeftTop = (h - leftHeightPx) / 2f
                    val yLeftBottom = yLeftTop + leftHeightPx

                    val yLeanTop = (h - (rightHeightPx * 2 + rightGapPx)) / 2f
                    val yLeanBottom = yLeanTop + rightHeightPx

                    val yFatTop = yLeanBottom + rightGapPx
                    val yFatBottom = yFatTop + rightHeightPx

                    // Ribbon split at source node
                    val splitY = yLeftTop + leftHeightPx * leanPct

                    // Drawing with horizontal clip progress
                    clipRect(
                        right = xStart + dx * ribbonProgress.value
                    ) {
                        // 1. Lean Flow Ribbon
                        val leanPath = Path().apply {
                            moveTo(xStart, yLeftTop)
                            cubicTo(
                                x1 = xStart + dx / 2f, y1 = yLeftTop,
                                x2 = xStart + dx / 2f, y2 = yLeanTop,
                                x3 = xEnd, y3 = yLeanTop
                            )
                            lineTo(xEnd, yLeanBottom)
                            cubicTo(
                                x1 = xStart + dx / 2f, y1 = yLeanBottom,
                                x2 = xStart + dx / 2f, y2 = splitY,
                                x3 = xStart, y3 = splitY
                            )
                            close()
                        }
                        drawPath(
                            path = leanPath,
                            color = Color(0xFFCDEFD9).copy(alpha = 0.7f)
                        )

                        // 2. Fat Flow Ribbon
                        val fatPath = Path().apply {
                            moveTo(xStart, splitY)
                            cubicTo(
                                x1 = xStart + dx / 2f, y1 = splitY,
                                x2 = xStart + dx / 2f, y2 = yFatTop,
                                x3 = xEnd, y3 = yFatTop
                            )
                            lineTo(xEnd, yFatBottom)
                            cubicTo(
                                x1 = xStart + dx / 2f, y1 = yFatBottom,
                                x2 = xStart + dx / 2f, y2 = yLeftBottom,
                                x3 = xStart, y3 = yLeftBottom
                            )
                            close()
                        }
                        drawPath(
                            path = fatPath,
                            color = Color(0xFFFFD5DC).copy(alpha = 0.7f)
                        )
                    }
                }

                // Left Source Node (Total Weight)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(leftWidth, leftHeight)
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(16.dp),
                            ambientColor = AccentPurple.copy(alpha = 0.05f),
                            spotColor = AccentPurple.copy(alpha = 0.05f)
                        )
                        .background(
                            color = Color(0xFFF3EEFF),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0xFFDCD2FE),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Total Weight",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary,
                            style = compactTextStyle()
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "%.2f kg".format(data.totalWeight),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF6338CE),
                            style = compactTextStyle()
                        )
                    }
                }

                // Right Destination Nodes Column
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(rightWidth),
                    verticalArrangement = Arrangement.spacedBy(rightGap),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Lean Mass Node
                    Box(
                        modifier = Modifier
                            .size(rightWidth, rightHeight)
                            .graphicsLayer(alpha = destinationAlpha.value)
                            .shadow(
                                elevation = 2.dp,
                                shape = RoundedCornerShape(16.dp),
                                ambientColor = Color(0xFF45A96B).copy(alpha = 0.05f),
                                spotColor = Color(0xFF45A96B).copy(alpha = 0.05f)
                            )
                            .background(
                                color = Color(0xFFEFFFF4),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = Color(0xFFCDEFD9),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "Lean Mass",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary,
                                style = compactTextStyle()
                            )
                            Text(
                                text = "%.2f kg".format(data.leanMass),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF2E8550),
                                style = compactTextStyle()
                            )
                            Text(
                                text = "(%.1f%%)".format(leanPct * 100f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary,
                                style = compactTextStyle()
                            )
                        }
                    }

                    // Fat Mass Node
                    Box(
                        modifier = Modifier
                            .size(rightWidth, rightHeight)
                            .graphicsLayer(alpha = destinationAlpha.value)
                            .shadow(
                                elevation = 2.dp,
                                shape = RoundedCornerShape(16.dp),
                                ambientColor = Color(0xFFFF5A6A).copy(alpha = 0.05f),
                                spotColor = Color(0xFFFF5A6A).copy(alpha = 0.05f)
                            )
                            .background(
                                color = Color(0xFFFFF0F2),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = Color(0xFFFFD5DC),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "Fat Mass",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary,
                                style = compactTextStyle()
                            )
                            Text(
                                text = "%.2f kg".format(data.fatMass),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFE03144),
                                style = compactTextStyle()
                            )
                            Text(
                                text = "(%.1f%%)".format(fatPct * 100f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary,
                                style = compactTextStyle()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PerformanceTextRemarkSection(
                text = remarkText
            )
        }
    }
}

data class TrendDataPoint(val month: String, val leanMass: Float, val fatMass: Float)

@Composable
fun LeanFatTrendChartComponent(
    data: List<TrendDataPoint> = listOf(
        TrendDataPoint("Jan", 0.0f, 0.0f),
        TrendDataPoint("Feb", 0.4f, -0.2f),
        TrendDataPoint("Mar", 0.7f, -0.5f),
        TrendDataPoint("Apr", 0.5f, -0.8f),
        TrendDataPoint("May", 0.9f, -1.1f),
        TrendDataPoint("Jun", 0.8f, -1.3f)
    ),
    remarkText: String = "A strong trend shows lean mass moving up while fat mass moves down or stays controlled over the same period.",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.03f),
                spotColor = Color.Black.copy(alpha = 0.03f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(CardBackground)
            .border(
                width = 1.dp,
                color = CardBorder,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "COMPOSITION TRENDS",
                    color = AccentPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    style = compactTextStyle()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Track relative lean and fat mass changes from your baseline.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Legend
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF45A96B))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Lean Mass",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF5A6A))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Fat Mass",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }
            }

            // Chart
            val containerHeight = 180.dp
            val animationProgress = remember { Animatable(0f) }
            LaunchedEffect(data) {
                animationProgress.animateTo(
                    1f,
                    animationSpec = tween(1200, easing = FastOutSlowInEasing)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(containerHeight)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    val leftPaddingPx = 34.dp.toPx()
                    val rightPaddingPx = 16.dp.toPx()
                    val topPaddingPx = 10.dp.toPx()
                    val bottomPaddingPx = 24.dp.toPx()

                    val plotLeft = leftPaddingPx
                    val plotRight = w - rightPaddingPx
                    val plotTop = topPaddingPx
                    val plotBottom = h - bottomPaddingPx

                    val plotWidth = plotRight - plotLeft
                    val plotHeight = plotBottom - plotTop

                    val maxAbsValue = data
                        .flatMap { listOf(it.leanMass, it.fatMass) }
                        .maxOfOrNull { kotlin.math.abs(it) }
                        ?.coerceAtLeast(0.5f) ?: 1f
                    val yLimit = ceil(maxAbsValue * 1.2f * 2f) / 2f
                    val yMax = yLimit
                    val yMin = -yLimit

                    val dx = plotWidth / (data.size - 1).coerceAtLeast(1)

                    fun getY(value: Float): Float {
                        val fraction = ((value - yMin) / (yMax - yMin)).coerceIn(0f, 1f)
                        return plotBottom - fraction * plotHeight
                    }
                    val zeroY = getY(0f)

                    // Grid lines and Y labels
                    val textPaint = Paint().apply {
                        color = android.graphics.Color.parseColor("#64748B") // TextSecondary
                        textSize = 10.sp.toPx()
                        textAlign = Paint.Align.RIGHT
                        isAntiAlias = true
                    }

                    val lines = 4
                    for (i in -lines..lines) {
                        val value = i * yLimit / lines
                        val fraction = (value - yMin) / (yMax - yMin)
                        val y = plotBottom - fraction * plotHeight
                        val isZeroLine = kotlin.math.abs(value) < 0.0001f

                        drawLine(
                            color = if (isZeroLine) Color(0xFF94A3B8) else Color(0xFFE6EEF2),
                            start = Offset(plotLeft, y),
                            end = Offset(plotRight, y),
                            strokeWidth = if (isZeroLine) 1.4.dp.toPx() else 1.dp.toPx(),
                            pathEffect = if (isZeroLine) null else androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                                floatArrayOf(8f, 8f),
                                0f
                            )
                        )

                        val textHeight =
                            textPaint.fontMetrics.descent - textPaint.fontMetrics.ascent
                        val label = when {
                            isZeroLine -> "0"
                            value > 0f -> "+%.1f".format(value)
                            else -> "%.1f".format(value)
                        }
                        drawContext.canvas.nativeCanvas.drawText(
                            label,
                            plotLeft - 8.dp.toPx(),
                            y + textHeight / 3f,
                            textPaint
                        )
                    }

                    val leanPath = Path()
                    val fatPath = Path()
                    val pointsLean = mutableListOf<Offset>()
                    val pointsFat = mutableListOf<Offset>()

                    data.forEachIndexed { index, point ->
                        val x = plotLeft + index * dx
                        pointsLean.add(Offset(x, getY(point.leanMass)))
                        pointsFat.add(Offset(x, getY(point.fatMass)))
                    }

                    fun smoothPath(points: List<Offset>, path: Path) {
                        if (points.isEmpty()) return
                        path.moveTo(points.first().x, points.first().y)
                        for (i in 0 until points.lastIndex) {
                            val p0 = points.getOrElse(i - 1) { points[i] }
                            val p1 = points[i]
                            val p2 = points[i + 1]
                            val p3 = points.getOrElse(i + 2) { p2 }

                            val control1 = Offset(
                                x = p1.x + (p2.x - p0.x) / 6f,
                                y = (p1.y + (p2.y - p0.y) / 6f).coerceIn(plotTop, plotBottom)
                            )
                            val control2 = Offset(
                                x = p2.x - (p3.x - p1.x) / 6f,
                                y = (p2.y - (p3.y - p1.y) / 6f).coerceIn(plotTop, plotBottom)
                            )
                            path.cubicTo(control1.x, control1.y, control2.x, control2.y, p2.x, p2.y)
                        }
                    }

                    smoothPath(pointsLean, leanPath)
                    smoothPath(pointsFat, fatPath)

                    val leanFillPath = Path().apply {
                        addPath(leanPath)
                        if (pointsLean.isNotEmpty()) {
                            lineTo(pointsLean.last().x, zeroY)
                            lineTo(pointsLean.first().x, zeroY)
                            close()
                        }
                    }
                    val fatFillPath = Path().apply {
                        addPath(fatPath)
                        if (pointsFat.isNotEmpty()) {
                            lineTo(pointsFat.last().x, zeroY)
                            lineTo(pointsFat.first().x, zeroY)
                            close()
                        }
                    }

                    clipRect(right = plotLeft + (plotWidth + 16.dp.toPx()) * animationProgress.value) {
                        // Fills
                        drawPath(path = leanFillPath, color = Color(0xFF45A96B).copy(alpha = 0.10f))
                        drawPath(path = fatFillPath, color = Color(0xFFFF5A6A).copy(alpha = 0.10f))

                        // Lines
                        drawPath(
                            path = leanPath,
                            color = Color(0xFF45A96B),
                            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawPath(
                            path = fatPath,
                            color = Color(0xFFFF5A6A),
                            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Small dots
                        val radiusPx = 1.75.dp.toPx()
                        pointsLean.forEach {
                            drawCircle(
                                color = Color(0xFF45A96B).copy(alpha = 0.45f),
                                radius = radiusPx,
                                center = it
                            )
                        }
                        pointsFat.forEach {
                            drawCircle(
                                color = Color(0xFFFF5A6A).copy(alpha = 0.45f),
                                radius = radiusPx,
                                center = it
                            )
                        }

                        // Vertical guide line for the last point
                        if (pointsLean.isNotEmpty()) {
                            val finalX = pointsLean.last().x
                            drawLine(
                                color = Color(0xFFE6EEF2),
                                start = Offset(finalX, plotTop),
                                end = Offset(finalX, plotBottom),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                                    floatArrayOf(6f, 6f),
                                    0f
                                )
                            )

                            // Large final dots
                            drawCircle(
                                color = Color(0xFF45A96B),
                                radius = 5.dp.toPx(),
                                center = pointsLean.last()
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 3.dp.toPx(),
                                center = pointsLean.last()
                            )

                            drawCircle(
                                color = Color(0xFFFF5A6A),
                                radius = 5.dp.toPx(),
                                center = pointsFat.last()
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 3.dp.toPx(),
                                center = pointsFat.last()
                            )
                        }
                    }

                    // X-axis labels
                    val xTextPaint = Paint().apply {
                        color = android.graphics.Color.parseColor("#64748B")
                        textSize = 10.sp.toPx()
                        textAlign = Paint.Align.CENTER
                        isAntiAlias = true
                    }
                    data.forEachIndexed { index, point ->
                        val x = plotLeft + index * dx
                        drawContext.canvas.nativeCanvas.drawText(
                            point.month,
                            x,
                            h - 2.dp.toPx(), // bottom edge
                            xTextPaint
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PerformanceTextRemarkSection(
                text = remarkText
            )
        }
    }
}

data class RecompPoint(val fatMass: Float, val leanMass: Float)

enum class ArrowDirection {
    DIAGONAL_UP_LEFT,
    HORIZONTAL_LEFT,
    VERTICAL_UP
}

@Composable
fun LegendRow(
    label: String,
    arrowColor: Color,
    isDashed: Boolean,
    direction: ArrowDirection,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Canvas(
            modifier = Modifier
                .width(40.dp)
                .height(16.dp)
        ) {
            val w = size.width
            val h = size.height
            val start: Offset
            val end: Offset
            when (direction) {
                ArrowDirection.DIAGONAL_UP_LEFT -> {
                    start = Offset(w - 6.dp.toPx(), h - 4.dp.toPx())
                    end = Offset(6.dp.toPx(), 4.dp.toPx())
                }

                ArrowDirection.HORIZONTAL_LEFT -> {
                    start = Offset(w - 6.dp.toPx(), h / 2f)
                    end = Offset(6.dp.toPx(), h / 2f)
                }

                ArrowDirection.VERTICAL_UP -> {
                    start = Offset(w / 2f, h - 3.dp.toPx())
                    end = Offset(w / 2f, 3.dp.toPx())
                }
            }

            val dx = end.x - start.x
            val dy = end.y - start.y
            val len = kotlin.math.hypot(dx, dy)
            val strokeWidth = if (isDashed) 1.5.dp.toPx() else 2.dp.toPx()

            if (len > 0) {
                val uX = dx / len
                val uY = dy / len

                drawLine(
                    color = arrowColor,
                    start = start,
                    end = end,
                    strokeWidth = strokeWidth,
                    pathEffect = if (isDashed) androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                        floatArrayOf(6f, 4f),
                        0f
                    ) else null
                )

                val arrowheadSizePx = 5.dp.toPx()
                val angle = kotlin.math.atan2(dy, dx)
                val arrowAngle = Math.PI / 6
                val x1 = end.x - arrowheadSizePx * kotlin.math.cos(angle - arrowAngle).toFloat()
                val y1 = end.y - arrowheadSizePx * kotlin.math.sin(angle - arrowAngle).toFloat()
                val x2 = end.x - arrowheadSizePx * kotlin.math.cos(angle + arrowAngle).toFloat()
                val y2 = end.y - arrowheadSizePx * kotlin.math.sin(angle + arrowAngle).toFloat()

                val path = Path().apply {
                    moveTo(end.x, end.y)
                    lineTo(x1, y1)
                    lineTo(x2, y2)
                    close()
                }
                drawPath(path = path, color = arrowColor)
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF111111),
            style = compactTextStyle()
        )
    }
}

private fun DrawScope.drawAnimatedArrow(
    start: Offset,
    end: Offset,
    color: Color,
    strokeWidth: Float,
    isDashed: Boolean,
    arrowheadSizePx: Float,
    endOffsetPx: Float,
    progress: Float
) {
    val dx = end.x - start.x
    val dy = end.y - start.y
    val len = kotlin.math.hypot(dx, dy)
    if (len > 0) {
        val uX = dx / len
        val uY = dy / len
        val adjustedStart = Offset(start.x + uX * endOffsetPx, start.y + uY * endOffsetPx)
        val adjustedEnd = Offset(end.x - uX * endOffsetPx, end.y - uY * endOffsetPx)

        val animatedEnd = Offset(
            adjustedStart.x + (adjustedEnd.x - adjustedStart.x) * progress,
            adjustedStart.y + (adjustedEnd.y - adjustedStart.y) * progress
        )

        drawLine(
            color = color,
            start = adjustedStart,
            end = animatedEnd,
            strokeWidth = strokeWidth,
            pathEffect = if (isDashed) androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                floatArrayOf(8f, 6f),
                0f
            ) else null,
            alpha = progress
        )

        val angle = kotlin.math.atan2(dy, dx)
        val arrowAngle = Math.PI / 6

        val x1 = animatedEnd.x - arrowheadSizePx * kotlin.math.cos(angle - arrowAngle).toFloat()
        val y1 = animatedEnd.y - arrowheadSizePx * kotlin.math.sin(angle - arrowAngle).toFloat()

        val x2 = animatedEnd.x - arrowheadSizePx * kotlin.math.cos(angle + arrowAngle).toFloat()
        val y2 = animatedEnd.y - arrowheadSizePx * kotlin.math.sin(angle + arrowAngle).toFloat()

        val path = Path().apply {
            moveTo(animatedEnd.x, animatedEnd.y)
            lineTo(x1, y1)
            lineTo(x2, y2)
            close()
        }
        drawPath(path = path, color = color, alpha = progress)
    }
}

@Composable
fun RecompVectorPlotComponent(
    start: RecompPoint = RecompPoint(20.50f, 57.50f),
    current: RecompPoint = RecompPoint(18.39f, 58.86f),
    target: RecompPoint = RecompPoint(12.92f, 58.86f),
    remarkText: String = "The ideal recomp path moves left and upward: less fat mass with equal or greater lean mass.",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.03f),
                spotColor = Color.Black.copy(alpha = 0.03f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(CardBackground)
            .border(
                width = 1.dp,
                color = CardBorder,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "RECOMP VECTOR PLOT",
                    color = AccentPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    style = compactTextStyle()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Track your body composition journey across four distinct zones.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Animation progress
            val animationProgress = remember { Animatable(0f) }
            LaunchedEffect(Unit) {
                animationProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
                )
            }

            val containerHeight = 320.dp
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(containerHeight)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    val leftPaddingPx = 44.dp.toPx()
                    val rightPaddingPx = 16.dp.toPx()
                    val topPaddingPx = 16.dp.toPx()
                    val bottomPaddingPx = 44.dp.toPx()

                    val plotLeft = leftPaddingPx
                    val plotRight = w - rightPaddingPx
                    val plotTop = topPaddingPx
                    val plotBottom = h - bottomPaddingPx

                    val plotWidth = plotRight - plotLeft
                    val plotHeight = plotBottom - plotTop

                    val cornerRadius = 16.dp.toPx()

                    // Fixed ranges (padded to keep points away from edges)
                    val minX = 10f
                    val maxX = 22f
                    val minY = 56f
                    val maxY = 62f

                    fun getScreenX(fat: Float): Float {
                        val xRatio = (fat - minX) / (maxX - minX)
                        return plotLeft + xRatio * plotWidth
                    }

                    fun getScreenY(lean: Float): Float {
                        val yRatio = (lean - minY) / (maxY - minY)
                        return plotBottom - yRatio * plotHeight
                    }

                    val currentX = getScreenX(current.fatMass)
                    val currentY = getScreenY(current.leanMass)

                    // Draw Chart Background
                    drawRoundRect(
                        color = Color(0xFFF8FAFC),
                        topLeft = Offset(plotLeft, plotTop),
                        size = Size(plotWidth, plotHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                            cornerRadius,
                            cornerRadius
                        )
                    )

                    // Draw Grid Lines
                    val gridPathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                        floatArrayOf(
                            10f,
                            10f
                        ), 0f
                    )
                    val xTicksForGrid = listOf(10, 13, 16, 19, 22)
                    xTicksForGrid.forEach { tick ->
                        val x = getScreenX(tick.toFloat())
                        drawLine(
                            color = Color(0x11000000),
                            start = Offset(x, plotTop),
                            end = Offset(x, plotBottom),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = gridPathEffect
                        )
                    }
                    val yTicksForGrid = listOf(56, 57, 58, 59, 60, 61, 62)
                    yTicksForGrid.forEach { tick ->
                        val y = getScreenY(tick.toFloat())
                        drawLine(
                            color = Color(0x11000000),
                            start = Offset(plotLeft, y),
                            end = Offset(plotRight, y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = gridPathEffect
                        )
                    }

                    // Chart Border
                    drawRoundRect(
                        color = Color(0xFFE2E8F0),
                        topLeft = Offset(plotLeft, plotTop),
                        size = Size(plotWidth, plotHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                            cornerRadius,
                            cornerRadius
                        ),
                        style = Stroke(width = 1.dp.toPx())
                    )

                    // Text Paints
                    val titlePaint = android.graphics.Paint().apply {
                        color = TextSecondary.toArgb()
                        textSize = 11.sp.toPx()
                        typeface = android.graphics.Typeface.create(
                            "sans-serif-medium",
                            android.graphics.Typeface.BOLD
                        )
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                    }

                    val tickPaint = android.graphics.Paint().apply {
                        color = TextSecondary.toArgb()
                        textSize = 10.sp.toPx()
                        typeface = android.graphics.Typeface.create(
                            "sans-serif",
                            android.graphics.Typeface.BOLD
                        )
                        isAntiAlias = true
                    }

                    // X-axis Ticks
                    val xTicks = listOf(10, 13, 16, 19, 22)
                    xTicks.forEach { tick ->
                        val x = getScreenX(tick.toFloat())
                        val y = plotBottom + 16.dp.toPx()
                        tickPaint.textAlign = android.graphics.Paint.Align.CENTER
                        drawContext.canvas.nativeCanvas.drawText(tick.toString(), x, y, tickPaint)
                    }

                    // Y-axis Ticks
                    val yTicks = listOf(56, 57, 58, 59, 60, 61, 62)
                    yTicks.forEach { tick ->
                        val x = plotLeft - 8.dp.toPx()
                        val y = getScreenY(tick.toFloat()) + 4.dp.toPx()
                        tickPaint.textAlign = android.graphics.Paint.Align.RIGHT
                        drawContext.canvas.nativeCanvas.drawText(tick.toString(), x, y, tickPaint)
                    }

                    // Titles
                    drawContext.canvas.nativeCanvas.drawText(
                        "Fat Mass (kg)",
                        plotLeft + plotWidth / 2f,
                        h - 4.dp.toPx(),
                        titlePaint
                    )
                    drawContext.canvas.nativeCanvas.save()
                    drawContext.canvas.nativeCanvas.translate(
                        12.dp.toPx(),
                        plotTop + plotHeight / 2f
                    )
                    drawContext.canvas.nativeCanvas.rotate(-90f)
                    drawContext.canvas.nativeCanvas.drawText("Lean Mass (kg)", 0f, 0f, titlePaint)
                    drawContext.canvas.nativeCanvas.restore()

                    // Coordinates of data points
                    val startX = getScreenX(start.fatMass)
                    val startY = getScreenY(start.leanMass)
                    val targetX = getScreenX(target.fatMass)
                    val targetY = getScreenY(target.leanMass)

                    val progress = animationProgress.value

                    // Arrows
                    drawAnimatedArrow(
                        start = Offset(startX, startY),
                        end = Offset(currentX, currentY),
                        color = Color(0xFF64748B),
                        strokeWidth = 2.dp.toPx(),
                        isDashed = true,
                        arrowheadSizePx = 8.dp.toPx(),
                        endOffsetPx = 8.dp.toPx(),
                        progress = progress
                    )

                    drawAnimatedArrow(
                        start = Offset(currentX, currentY),
                        end = Offset(targetX, targetY),
                        color = Color(0xFF188038),
                        strokeWidth = 2.5.dp.toPx(),
                        isDashed = false,
                        arrowheadSizePx = 10.dp.toPx(),
                        endOffsetPx = 10.dp.toPx(),
                        progress = progress
                    )

                    // Target Aura
                    if (progress > 0) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF188038).copy(alpha = 0.2f * progress),
                                    Color.Transparent
                                ),
                                center = Offset(targetX, targetY),
                                radius = 32.dp.toPx()
                            ),
                            radius = 32.dp.toPx(),
                            center = Offset(targetX, targetY)
                        )
                    }

                    // Draw Data Points
                    val pointRadius = 6.dp.toPx()
                    fun drawShinyPoint(
                        x: Float,
                        y: Float,
                        color: Color,
                        isCurrent: Boolean = false
                    ) {
                        val r = if (isCurrent) pointRadius * 1.3f else pointRadius
                        drawContext.canvas.nativeCanvas.apply {
                            drawCircle(
                                x, y, r,
                                android.graphics.Paint().apply {
                                    this.color = color.toArgb()
                                    setShadowLayer(10f, 0f, 4f, color.copy(alpha = 0.6f).toArgb())
                                    isAntiAlias = true
                                }
                            )
                        }
                        drawCircle(
                            color = Color.White,
                            radius = r,
                            center = Offset(x, y),
                            style = Stroke(width = 2.dp.toPx()),
                            alpha = progress
                        )
                    }

                    if (progress > 0) {
                        drawShinyPoint(startX, startY, Color(0xFF94A3B8))
                        drawShinyPoint(targetX, targetY, Color(0xFF188038))
                        drawShinyPoint(currentX, currentY, AccentPurple, isCurrent = true)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Weight Summary Marker
            val currentWeight = current.fatMass + current.leanMass
            val targetWeight = target.fatMass + target.leanMass

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Current Weight",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "%.1f kg".format(currentWeight),
                        fontSize = 16.sp,
                        color = AccentPurple,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "→",
                    fontSize = 20.sp,
                    color = TextSecondary.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Target Weight",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "%.1f kg".format(targetWeight),
                        fontSize = 16.sp,
                        color = Color(0xFF188038),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PerformanceTextRemarkSection(
                text = remarkText
            )
        }
    }
}

data class ExcessFatGaugeData(
    val currentFatMassKg: Float,
    val targetFatMassKg: Float
)

@Composable
fun ExcessFatGaugeComponent(
    data: ExcessFatGaugeData = ExcessFatGaugeData(
        currentFatMassKg = 18.39f,
        targetFatMassKg = 12.92f
    ),
    remarkText: String = "Excess fat is the gap between current fat mass and target fat mass; reducing it improves composition without guessing from body weight.",
    modifier: Modifier = Modifier
) {
    val currentFatMassKg = data.currentFatMassKg
    val targetFatMassKg = data.targetFatMassKg
    val excessFatKg = maxOf(0f, currentFatMassKg - targetFatMassKg)
    val hasExcessFat = excessFatKg > 0f
    val excessColor = if (hasExcessFat) Color(0xFFFF8A1E) else Color(0xFF45A96B)
    val excessGradient = Brush.linearGradient(
        listOf(
            if (hasExcessFat) Color(0xFFFFB84D) else Color(0xFF6DCC91),
            excessColor
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.03f),
                spotColor = Color.Black.copy(alpha = 0.03f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(CardBackground)
            .border(
                width = 1.dp,
                color = CardBorder,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "EXCESS FAT GAUGE",
                    color = AccentPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    style = compactTextStyle()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Compares current fat mass against your target fat mass.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Gauge Box
            Box(
                modifier = Modifier
                    .width(280.dp)
                    .height(160.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                val animationProgress = remember { Animatable(0f) }
                LaunchedEffect(currentFatMassKg, targetFatMassKg) {
                    animationProgress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
                    )
                }

                val gapAngle = 2.5f

                // Sweep angles mapping logic:
                val greenSweep: Float
                val orangeSweep: Float

                if (excessFatKg <= 0f) {
                    greenSweep = 180f
                    orangeSweep = 0f
                } else {
                    val denominator = (targetFatMassKg + excessFatKg).coerceAtLeast(0.01f)
                    val availableSweep = 180f - gapAngle

                    greenSweep = (targetFatMassKg / denominator) * availableSweep
                    orangeSweep = (excessFatKg / denominator) * availableSweep
                }

                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val strokeWidthPx = 18.dp.toPx()
                    val radius = (canvasWidth - strokeWidthPx - 40.dp.toPx()) / 2f
                    val centerX = canvasWidth / 2f
                    val centerY = canvasHeight - 20.dp.toPx()

                    val topLeft = Offset(centerX - radius, centerY - radius)
                    val arcSize = Size(radius * 2f, radius * 2f)

                    drawArc(
                        color = Color(0xFFF1F5F9),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidthPx, cap = StrokeCap.Butt)
                    )

                    val animProgress = animationProgress.value
                    val activeGreenSweep = greenSweep * animProgress
                    val activeOrangeSweep = orangeSweep * animProgress

                    // Draw Green Segment
                    if (activeGreenSweep > 0f) {
                        drawArc(
                            brush = Brush.linearGradient(
                                listOf(
                                    Color(0xFF6DCC91),
                                    Color(0xFF45A96B)
                                )
                            ),
                            startAngle = 180f,
                            sweepAngle = activeGreenSweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Butt)
                        )
                    }

                    // Draw Orange Segment
                    if (activeOrangeSweep > 0f) {
                        val orangeStartAngle = 180f + activeGreenSweep + gapAngle
                        drawArc(
                            brush = Brush.linearGradient(
                                listOf(
                                    Color(0xFFFFB84D),
                                    Color(0xFFFF8A1E)
                                )
                            ),
                            startAngle = orangeStartAngle,
                            sweepAngle = activeOrangeSweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Butt)
                        )
                    }
                }

                // Center Value Text
                Column(
                    modifier = Modifier.padding(bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val animatedExcessFat = excessFatKg * animationProgress.value

                    Text(
                        text = "%.1f kg".format(animatedExcessFat),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        style = compactTextStyle(),
                        letterSpacing = (-1).sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (hasExcessFat) "TO LOSE" else "ON TARGET",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp,
                        style = compactTextStyle()
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Legend Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ExcessFatLegendItem(
                    label = "Target",
                    rangeText = "%.1f kg".format(targetFatMassKg),
                    color = Color(0xFF45A96B),
                    gradient = Brush.linearGradient(listOf(Color(0xFF6DCC91), Color(0xFF45A96B))),
                    isActive = !hasExcessFat,
                    modifier = Modifier.weight(1f)
                )
                ExcessFatLegendItem(
                    label = "Excess",
                    rangeText = "%.1f kg".format(excessFatKg),
                    color = excessColor,
                    gradient = excessGradient,
                    isActive = hasExcessFat,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PerformanceTextRemarkSection(
                text = remarkText
            )
        }
    }
}

@Composable
private fun ExcessFatLegendItem(
    label: String,
    rangeText: String,
    color: Color,
    gradient: Brush,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .height(6.dp)
                .fillMaxWidth(0.78f)
                .clip(RoundedCornerShape(3.dp))
                .background(
                    if (isActive) gradient else Brush.linearGradient(
                        listOf(
                            Color(0xFFF1F5F9),
                            Color(0xFFF1F5F9)
                        )
                    )
                )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
            color = if (isActive) TextPrimary else TextSecondary,
            style = compactTextStyle()
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = rangeText,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            color = if (isActive) color.copy(alpha = 0.78f) else TextSecondary.copy(alpha = 0.6f),
            style = compactTextStyle()
        )
    }
}
