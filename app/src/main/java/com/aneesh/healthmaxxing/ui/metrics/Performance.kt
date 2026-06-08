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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aneesh.healthmaxxing.data.remote.Measurements
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

    val activeIndex = ranges.indexOfFirst { clampedValue >= it.min && clampedValue <= it.max }.takeIf { it >= 0 } ?: 2
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

                        val actualStartAngle = segmentStartAngle + (if (isFirst) 0f else gapAngle / 2f)
                        val actualSweepAngle = segmentSweepAngle - (if (isFirst || isLast) gapAngle / 2f else gapAngle)

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
                    val progress = ((currentAnimatedValue - minValue) / (maxValue - minValue)).coerceIn(0f, 1f)
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
                                setShadowLayer(16f, 0f, 8f, android.graphics.Color.argb(40, 0, 0, 0))
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
                                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
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
                                .background(if (isActive) range.arcGradient else Brush.linearGradient(listOf(Color(0xFFF1F5F9), Color(0xFFF1F5F9))))
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

            Spacer(modifier = Modifier.height(24.dp))

            // Status Text
            val rangeSpan = activeRange.max - activeRange.min
            val positionInRange = clampedValue - activeRange.min
            val progressInRange = positionInRange / rangeSpan
            val nextRange = ranges.getOrNull(activeIndex + 1)
            val prevRange = ranges.getOrNull(activeIndex - 1)

            val statusText = if (progressInRange > 0.75f && nextRange != null) {
                "You are in the upper range of ${activeRange.label}, approaching ${nextRange.label}."
            } else if (progressInRange < 0.25f && prevRange != null) {
                "You are in the lower range of ${activeRange.label}, just above ${prevRange.label}."
            } else {
                "You are solidly within the ${activeRange.label} range."
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(CardBorder)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(activeRange.legendBackgroundColor.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Status Info",
                        tint = activeRange.legendTextColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = statusText,
                        fontSize = 13.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        style = compactTextStyle()
                    )
                    Text(
                        text = "Based on your height and body composition.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 15.sp,
                        style = compactTextStyle()
                    )
                }
            }
        }
    }
}

@Composable
fun Performance(modifier: Modifier = Modifier) {
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
        FfmiGaugeComponent(value = 19.9f)
        FfmiVsFmiChartComponent()
        BodyMeasurementsPanel(measurements = dummyMeasurements)
    }
}

data class BodyCompositionPoint(
    val ffmi: Float,
    val fmi: Float
)

@Composable
fun FfmiVsFmiChartComponent(
    userPoint: BodyCompositionPoint = BodyCompositionPoint(19.9f, 6.2f),
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
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
                    )

                    // Draw Quadrants (with clipping so they stay inside the rounded rect)
                    val clipPath = androidx.compose.ui.graphics.Path().apply {
                        addRoundRect(
                            androidx.compose.ui.geometry.RoundRect(
                                rect = androidx.compose.ui.geometry.Rect(
                                    paddingLeft, paddingTop, paddingLeft + chartWidth, paddingTop + chartHeight
                                ),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
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
                        size = Size(paddingLeft + chartWidth - divX, paddingTop + chartHeight - divY)
                    )

                    // Draw Divider Lines
                    val dashPathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
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
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius),
                        style = Stroke(width = 1.dp.toPx())
                    )

                    // Axis Ticks & Labels
                    val paintText = android.graphics.Paint().apply {
                        color = TextSecondary.toArgb()
                        textSize = 10.sp.toPx()
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
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

                    fun drawQuadLabel(title: String, subtitle: String, cx: Float, cy: Float, colorStr: String) {
                        quadLabelPaint.color = android.graphics.Color.parseColor(colorStr)
                        
                        quadLabelPaint.textSize = 13.sp.toPx()
                        quadLabelPaint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                        drawContext.canvas.nativeCanvas.drawText(title, cx, cy - 4.dp.toPx(), quadLabelPaint)

                        quadLabelPaint.color = TextSecondary.copy(alpha = 0.8f).toArgb()
                        quadLabelPaint.textSize = 9.sp.toPx()
                        quadLabelPaint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
                        drawContext.canvas.nativeCanvas.drawText(subtitle, cx, cy + 12.dp.toPx(), quadLabelPaint)
                    }

                    drawQuadLabel("Skinny Fat", "High Fat • Low Muscle", paddingLeft + (divX - paddingLeft)/2f, paddingTop + (divY - paddingTop)/2f, "#E11D48")
                    drawQuadLabel("Big & Muscular", "High Fat • High Muscle", divX + (paddingLeft + chartWidth - divX)/2f, paddingTop + (divY - paddingTop)/2f, "#EA580C")
                    drawQuadLabel("Lean", "Low Fat • Low Muscle", paddingLeft + (divX - paddingLeft)/2f, divY + (paddingTop + chartHeight - divY)/2f, "#16A34A")
                    drawQuadLabel("Athletic", "Low Fat • High Muscle", divX + (paddingLeft + chartWidth - divX)/2f, divY + (paddingTop + chartHeight - divY)/2f, "#2563EB")

                    // X-axis title
                    val titlePaint = android.graphics.Paint().apply {
                        color = TextSecondary.toArgb()
                        textSize = 11.sp.toPx()
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                    }
                    drawContext.canvas.nativeCanvas.drawText("FFMI (Muscle Mass)", paddingLeft + chartWidth/2f, canvasHeight - 4.dp.toPx(), titlePaint)

                    // Y-axis title
                    drawContext.canvas.nativeCanvas.save()
                    drawContext.canvas.nativeCanvas.translate(12.dp.toPx(), paddingTop + chartHeight/2f)
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
                                setShadowLayer(12f, 0f, 6f, android.graphics.Color.argb(80, 109, 93, 246))
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
        }
    }
}
