package com.aneesh.healthmaxxing.ui.metrics

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aneesh.healthmaxxing.data.remote.TrendPoint
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val TextPrimary = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val CardBorder = Color(0xFFE6EEF2)
private val CardBackground = Color.White.copy(alpha = 0.92f)
private val Blue = Color(0xFF2563EB)
private val Success = Color(0xFF16A34A)
private val Orange = Color(0xFFF59E0B)
private val Red = Color(0xFFEF4444)
private val PillBackground = Color(0xFFEAF7EE)
private val TrackBackground = Color(0xFFF8FAFC)

private val ColorLean = Blue
private val ColorIdeal = Success
private val ColorAverage = Orange
private val ColorOverweight = Red

private fun compactTextStyle() = TextStyle(
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)

private fun Double?.formatMetric(decimals: Int = 1, fallback: String = "--"): String {
    return this?.let { "%.${decimals}f".format(it) } ?: fallback
}

private fun List<TrendPoint>.latestPoint(): TrendPoint? {
    return maxByOrNull { it.createdAt }
}

private fun List<TrendPoint>.previousPoint(): TrendPoint? {
    return sortedBy { it.createdAt }.dropLast(1).lastOrNull()
}

private fun TrendPoint?.shortDate(): String {
    val value = this?.createdAt ?: return "--"
    return if (value.length >= 10) value.substring(5, 10) else value
}

private fun deltaText(points: List<TrendPoint>, suffix: String = ""): String {
    val latest = points.latestPoint()?.value ?: return "--"
    val previous = points.previousPoint()?.value ?: return "--"
    val delta = latest - previous
    val sign = if (delta > 0) "+" else ""
    return "$sign${"%.1f".format(delta)}$suffix"
}

@Composable
fun FatRatioCard(
    modifier: Modifier = Modifier,
    fatRatio: String = "24.3",
    delta: String = "-1.6%",
    comparisonDate: String = "Apr 14",
    remark: String = "Healthy",
    comment: String = "Your fat ratio is within the healthy range for your age and gender."
) {
    Box(
        modifier = modifier
            .width(330.dp)
            .height(205.dp)
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
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .width(155.dp)
                    .align(Alignment.Top)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "FAT RATIO",
                        color = Blue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        style = compactTextStyle()
                    )

                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Info",
                        tint = TextSecondary.copy(alpha = 0.7f),
                        modifier = Modifier.size(12.dp)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = fatRatio,
                        color = TextPrimary,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 46.sp,
                        style = compactTextStyle()
                    )
                    Text(
                        text = "%",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 2.dp, bottom = 5.dp),
                        style = compactTextStyle()
                    )
                }

                Spacer(Modifier.height(5.dp))

                FatRemarkPill(remark = remark)

                Text(
                    text = comment,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(top = 10.dp),
                    style = compactTextStyle(),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                modifier = Modifier
                    .width(130.dp)
                    .align(Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FatRatioGauge(
                    fatRatio = fatRatio,
                    modifier = Modifier.size(width = 130.dp, height = 70.dp)
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = delta,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 22.sp,
                    style = compactTextStyle()
                )
                Text(
                    text = "vs $comparisonDate",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 12.sp,
                    style = compactTextStyle()
                )
            }
        }
    }
}

@Composable
private fun FatRemarkPill(
    remark: String,
    color: Color = Success
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )

        Spacer(Modifier.width(5.dp))

        Text(
            text = remark,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 11.sp,
            style = compactTextStyle()
        )
    }
}

@Composable
private fun FatRatioGauge(
    fatRatio: String,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 10.dp.toPx()
        val inset = strokeWidth / 2f
        val arcSize = Size(
            width = size.width - strokeWidth,
            height = size.width - strokeWidth
        )

        drawArc(
            color = ColorLean,
            startAngle = 180f,
            sweepAngle = 60f,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        drawArc(
            color = ColorIdeal,
            startAngle = 240f,
            sweepAngle = 60f,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
        )

        drawArc(
            color = ColorAverage,
            startAngle = 300f,
            sweepAngle = 30f,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
        )

        drawArc(
            color = ColorOverweight,
            startAngle = 330f,
            sweepAngle = 30f,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        val centerX = size.width / 2f
        val centerY = 65.dp.toPx()
        val radius = centerY - inset

        val innerR = radius - (strokeWidth / 2f) - 2.dp.toPx()
        val tickLength = 5.dp.toPx()
        val tickStroke = 1.5.dp.toPx()

        val tickAngles = listOf(180f, 240f, 300f, 360f)
        tickAngles.forEach { angle ->
            val rad = angle * (PI.toFloat() / 180f)
            val startX = centerX + cos(rad) * innerR
            val startY = centerY + sin(rad) * innerR
            val endX = centerX + cos(rad) * (innerR - tickLength)
            val endY = centerY + sin(rad) * (innerR - tickLength)
            drawLine(
                color = TextSecondary.copy(alpha = 0.5f),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = tickStroke
            )
        }

        val labelRadius = radius - (strokeWidth / 2f) - 13.dp.toPx()
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                color = TextSecondary.toArgb()
                textSize = 9.sp.toPx()
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            }
            val labels = listOf(
                Pair(180f, "0"),
                Pair(240f, "10"),
                Pair(300f, "20"),
                Pair(360f, "30")
            )
            labels.forEach { (angle, text) ->
                val rad = angle * (PI.toFloat() / 180f)
                val x = centerX + cos(rad) * labelRadius
                val y = centerY + sin(rad) * labelRadius + 3.dp.toPx()
                canvas.nativeCanvas.drawText(text, x, y, paint)
            }
        }

        val fatValue = fatRatio.toFloatOrNull() ?: 24.3f
        val clampedVal = fatValue.coerceIn(0f, 30f)
        val progressVal = clampedVal / 30f
        val needleAngle = 180f + progressVal * 180f
        val needleRad = needleAngle * (PI.toFloat() / 180f)

        val bubbleX = centerX + cos(needleRad) * radius
        val bubbleY = centerY + sin(needleRad) * radius

        val bubbleColor = when {
            clampedVal <= 10f -> ColorLean
            clampedVal <= 20f -> ColorIdeal
            clampedVal <= 25f -> ColorAverage
            else -> ColorOverweight
        }

        val shadowRadius = 8.dp.toPx()
        val bubbleRadius = 7.dp.toPx()
        val borderStrokeWidth = 2.dp.toPx()
        val centerDotRadius = 2.dp.toPx()

        drawCircle(
            color = Color.Black.copy(alpha = 0.12f),
            radius = shadowRadius,
            center = Offset(bubbleX, bubbleY + 1.dp.toPx())
        )

        drawCircle(
            color = Color.White,
            radius = bubbleRadius,
            center = Offset(bubbleX, bubbleY)
        )

        drawCircle(
            color = bubbleColor,
            radius = bubbleRadius,
            center = Offset(bubbleX, bubbleY),
            style = Stroke(width = borderStrokeWidth)
        )

        drawCircle(
            color = bubbleColor,
            radius = centerDotRadius,
            center = Offset(bubbleX, bubbleY)
        )
    }
}


@Composable
fun CustomizedCardClickable(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(18.dp)

    Card(
        modifier = modifier
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        border = BorderStroke(1.dp, CardBorder),
        content = content
    )
}

enum class SheetType {
    FAT_MASS,
    VISCERAL_FAT,
    SUBCUTANEOUS_FAT_RATIO,
    SUBCUTANEOUS_FAT_MASS
}

@Composable
private fun MetricStatsCard(
    label: String,
    valueText: String,
    unit: String,
    caption: String,
    captionColor: Color,
    onClick: (() -> Unit)? = null,
    showInfoIcon: Boolean = true,
    modifier: Modifier = Modifier
) {
    val cardContent: @Composable ColumnScope.() -> Unit = {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 13.dp, vertical = 15.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 13.sp,
                        style = compactTextStyle()
                    )
                }

                if (showInfoIcon) {
                    Box(
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(TrackBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Info",
                            tint = TextSecondary.copy(alpha = 0.68f),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = valueText,
                        color = TextPrimary,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 32.sp,
                        style = compactTextStyle(),
                        modifier = Modifier.alignByBaseline()
                    )
                    if (unit.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = unit,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            style = compactTextStyle(),
                            modifier = Modifier.alignByBaseline()
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(captionColor.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = caption,
                        color = captionColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = compactTextStyle()
                    )
                }
            }
        }
    }

    if (onClick != null) {
        CustomizedCardClickable(
            onClick = onClick,
            modifier = modifier,
            content = cardContent
        )
    } else {
        CustomizedCard(
            modifier = modifier,
            content = cardContent
        )
    }
}

@Composable
private fun Stats(fat: FatUiState) {
    var activeSheet by rememberSaveable {
        mutableStateOf<SheetType?>(null)
    }

    BottomSheetScreen(
        activeSheet = activeSheet,
        fat = fat,
        onDismissRequest = { activeSheet = null }
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MetricStatsCard(
                label = "Fat Mass",
                valueText = fat.fatMassKg.formatMetric(),
                unit = "kg",
                caption = fat.comments.fatMass?.remark ?: "Latest reading",
                captionColor = Success,
                onClick = { activeSheet = SheetType.FAT_MASS },
                showInfoIcon = true,
                modifier = Modifier
                    .weight(1f)
                    .height(150.dp)
            )

            MetricStatsCard(
                label = "Visceral Fat",
                valueText = fat.visceralFat.formatMetric(),
                unit = "kg",
                caption = fat.comments.visceralFatMass?.remark ?: "Latest mass",
                captionColor = Success,
                onClick = { activeSheet = SheetType.VISCERAL_FAT },
                showInfoIcon = true,
                modifier = Modifier
                    .weight(1f)
                    .height(150.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MetricStatsCard(
                label = "Subcutaneous Ratio",
                valueText = fat.subcutaneousFatPct.formatMetric(),
                unit = "%",
                caption = fat.comments.subcutaneousFatRatio?.remark ?: "Latest reading",
                captionColor = Success,
                onClick = { activeSheet = SheetType.SUBCUTANEOUS_FAT_RATIO },
                showInfoIcon = true,
                modifier = Modifier
                    .weight(1f)
                    .height(150.dp)
            )

            MetricStatsCard(
                label = "Subcutaneous Mass",
                valueText = fat.subcutaneousFatMassKg.formatMetric(),
                unit = "kg",
                caption = fat.comments.subcutaneousFatMass?.remark ?: "Latest reading",
                captionColor = Success,
                onClick = { activeSheet = SheetType.SUBCUTANEOUS_FAT_MASS },
                showInfoIcon = true,
                modifier = Modifier
                    .weight(1f)
                    .height(150.dp)
            )
        }
    }
}

@Composable
fun Fat(
    fat: FatUiState = FatUiState(),
    isLoading: Boolean = false,
    error: String? = null
) {
    if (isLoading && fat.trends.isEmpty()) {
        Text(
            text = "Loading fat metrics...",
            color = TextSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            textAlign = TextAlign.Center
        )
        return
    }

    if (error != null && fat.trends.isEmpty()) {
        Text(
            text = error,
            color = Red,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            textAlign = TextAlign.Center
        )
        return
    }

    val bodyFatTrend = fat.trends[FAT_METRIC_BODY_FAT_PCT].orEmpty()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FatRatioCard(
            fatRatio = fat.bodyFatPct.formatMetric(),
            delta = deltaText(bodyFatTrend, "%"),
            comparisonDate = bodyFatTrend.previousPoint().shortDate(),
            remark = fat.comments.fatPercent?.remark ?: "Healthy",
            comment = fat.comments.fatPercent?.comment
                ?: "Your fat ratio is within the healthy range for your age and gender."
        )
        VisceralSubcutaneousDeltaCard(fat = fat)
        Stats(fat = fat)
    }
}

@Composable
private fun FatTrendPlotCard(
    title: String,
    subtitle: String,
    points: List<TrendPoint>,
    unit: String,
    lineColor: Color,
    modifier: Modifier = Modifier
) {
    CustomizedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = compactTextStyle()
                    )
                    Text(
                        text = subtitle,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        style = compactTextStyle()
                    )
                }

                val latest = points.latestPoint()?.value
                Text(
                    text = latest?.let { "${"%.1f".format(it)}$unit" } ?: "--",
                    color = lineColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = compactTextStyle()
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            FatLineChart(
                points = points,
                unit = unit,
                lineColor = lineColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
            )
        }
    }
}

@Composable
private fun FatLineChart(
    points: List<TrendPoint>,
    unit: String,
    lineColor: Color,
    modifier: Modifier = Modifier
) {
    val sortedPoints = points.sortedBy { it.createdAt }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (sortedPoints.size < 2) {
            Text(
                text = "Not enough data to plot yet.",
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            return@Box
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val leftPad = 34.dp.toPx()
            val rightPad = 12.dp.toPx()
            val topPad = 12.dp.toPx()
            val bottomPad = 28.dp.toPx()
            val chartWidth = size.width - leftPad - rightPad
            val chartHeight = size.height - topPad - bottomPad
            val minValue = sortedPoints.minOf { it.value }.toFloat()
            val maxValue = sortedPoints.maxOf { it.value }.toFloat()
            val range = (maxValue - minValue).takeIf { it > 0f } ?: 1f

            fun xFor(index: Int): Float {
                val denominator = (sortedPoints.lastIndex).coerceAtLeast(1)
                return leftPad + (index / denominator.toFloat()) * chartWidth
            }

            fun yFor(value: Double): Float {
                val ratio = ((value.toFloat() - minValue) / range).coerceIn(0f, 1f)
                return topPad + chartHeight - (ratio * chartHeight)
            }

            repeat(4) { index ->
                val y = topPad + (index / 3f) * chartHeight
                drawLine(
                    color = CardBorder,
                    start = Offset(leftPad, y),
                    end = Offset(size.width - rightPad, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            drawLine(
                color = TextSecondary.copy(alpha = 0.25f),
                start = Offset(leftPad, topPad),
                end = Offset(leftPad, topPad + chartHeight),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = TextSecondary.copy(alpha = 0.25f),
                start = Offset(leftPad, topPad + chartHeight),
                end = Offset(size.width - rightPad, topPad + chartHeight),
                strokeWidth = 1.dp.toPx()
            )

            val offsets = sortedPoints.mapIndexed { index, point ->
                Offset(xFor(index), yFor(point.value))
            }

            val path = androidx.compose.ui.graphics.Path()
            val fillPath = androidx.compose.ui.graphics.Path()

            if (offsets.isNotEmpty()) {
                path.moveTo(offsets.first().x, offsets.first().y)
                for (i in 0 until offsets.lastIndex) {
                    val p0 = offsets.getOrElse(i - 1) { offsets[i] }
                    val p1 = offsets[i]
                    val p2 = offsets[i + 1]
                    val p3 = offsets.getOrElse(i + 2) { p2 }

                    val minY = topPad
                    val maxY = topPad + chartHeight

                    val control1 = Offset(
                        x = p1.x + (p2.x - p0.x) / 6f,
                        y = (p1.y + (p2.y - p0.y) / 6f).coerceIn(minY, maxY)
                    )
                    val control2 = Offset(
                        x = p2.x - (p3.x - p1.x) / 6f,
                        y = (p2.y - (p3.y - p1.y) / 6f).coerceIn(minY, maxY)
                    )
                    path.cubicTo(control1.x, control1.y, control2.x, control2.y, p2.x, p2.y)
                }
            }

            if (offsets.isNotEmpty()) {
                fillPath.addPath(path)
                fillPath.lineTo(offsets.last().x, topPad + chartHeight)
                fillPath.lineTo(offsets.first().x, topPad + chartHeight)
                fillPath.close()
            }

            drawPath(
                path = fillPath,
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        lineColor.copy(alpha = 0.4f),
                        lineColor.copy(alpha = 0.0f)
                    ),
                    startY = topPad,
                    endY = topPad + chartHeight
                )
            )

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            offsets.lastOrNull()?.let { lastOffset ->
                drawCircle(
                    color = Color.White,
                    radius = 5.dp.toPx(),
                    center = lastOffset
                )
                drawCircle(
                    color = lineColor,
                    radius = 5.dp.toPx(),
                    center = lastOffset,
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            drawIntoCanvas { canvas ->
                val labelPaint = Paint().apply {
                    color = TextSecondary.toArgb()
                    textSize = 10.sp.toPx()
                    textAlign = Paint.Align.RIGHT
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                }
                val bottomLabelPaint = Paint(labelPaint).apply {
                    textAlign = Paint.Align.CENTER
                }
                val highLabel = "${"%.1f".format(maxValue)}$unit"
                val lowLabel = "${"%.1f".format(minValue)}$unit"
                canvas.nativeCanvas.drawText(highLabel, leftPad - 8.dp.toPx(), topPad + 4.dp.toPx(), labelPaint)
                canvas.nativeCanvas.drawText(lowLabel, leftPad - 8.dp.toPx(), topPad + chartHeight, labelPaint)
                canvas.nativeCanvas.drawText(
                    sortedPoints.first().shortDate(),
                    leftPad,
                    size.height - 6.dp.toPx(),
                    bottomLabelPaint
                )
                canvas.nativeCanvas.drawText(
                    sortedPoints.last().shortDate(),
                    size.width - rightPad,
                    size.height - 6.dp.toPx(),
                    bottomLabelPaint
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetScreen(
    activeSheet: SheetType?,
    fat: FatUiState = FatUiState(),
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { true }
    )

    if (activeSheet != null) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    color = CardBorder
                )
            }
        ) {
            val title: String
            val headerText: String
            val explanation: String
            val sheetRemark: String?
            val sheetComment: String?
            val trendTitle: String
            val trendSubtitle: String
            val trendPoints: List<TrendPoint>
            val trendUnit: String
            val trendColor: Color

            when (activeSheet) {
                SheetType.FAT_MASS -> {
                    title = "FAT MASS INFO"
                    headerText = "Understanding Fat Mass"
                    explanation = "Fat mass represents the actual weight of fat tissue in your body (measured in kg or lbs), whereas fat ratio is the percentage of your total weight that is fat.\n\nMaintaining a healthy amount of fat mass is vital for hormone regulation, joint cushioning, and protecting internal organs."
                    sheetRemark = fat.comments.fatMass?.remark
                    sheetComment = fat.comments.fatMass?.comment
                    trendTitle = "Fat Mass Trend"
                    trendSubtitle = "30 day fat mass"
                    trendPoints = fat.trends[FAT_METRIC_FAT_MASS_KG].orEmpty()
                    trendUnit = "kg"
                    trendColor = Orange
                }
                SheetType.VISCERAL_FAT -> {
                    title = "VISCERAL FAT INFO"
                    headerText = "Understanding Visceral Fat"
                    explanation = "Visceral fat is the body fat that is stored within the abdominal cavity, surrounding your internal organs (like the liver, pancreas, and kidneys).\n\nUnlike subcutaneous fat, high levels of visceral fat are strongly linked to cardiovascular disease, type 2 diabetes, and other metabolic issues. An index between 1 and 9 is considered healthy."
                    sheetRemark = fat.comments.visceralFatMass?.remark
                    sheetComment = fat.comments.visceralFatMass?.comment
                    trendTitle = "Visceral Fat Trend"
                    trendSubtitle = "30 day visceral fat mass"
                    trendPoints = fat.trends[FAT_METRIC_VISCERAL_FAT].orEmpty()
                    trendUnit = "kg"
                    trendColor = Red
                }
                SheetType.SUBCUTANEOUS_FAT_RATIO -> {
                    title = "SUBCUTANEOUS FAT RATIO"
                    headerText = "Understanding Subcutaneous Ratio"
                    explanation = "Subcutaneous fat is the visible fat layer located directly beneath your skin. It is the type of fat you can pinch.\n\nWhile subcutaneous fat is less metabolically active and less dangerous than visceral fat, keeping its ratio within healthy bounds supports overall fitness and aesthetic health."
                    sheetRemark = fat.comments.subcutaneousFatRatio?.remark
                    sheetComment = fat.comments.subcutaneousFatRatio?.comment
                    trendTitle = "Subcutaneous Ratio Trend"
                    trendSubtitle = "30 day subcutaneous fat ratio"
                    trendPoints = fat.trends[FAT_METRIC_SUBCUTANEOUS_FAT_PCT].orEmpty()
                    trendUnit = "%"
                    trendColor = Blue
                }
                SheetType.SUBCUTANEOUS_FAT_MASS -> {
                    title = "SUBCUTANEOUS FAT MASS"
                    headerText = "Understanding Subcutaneous Mass"
                    explanation = "Subcutaneous fat mass represents the absolute weight of the fat layer stored directly under your skin (measured in kg or lbs).\n\nMeasuring this mass helps track real changes in physical fat loss and muscle definition, which percentage calculations alone might not fully reflect."
                    sheetRemark = fat.comments.subcutaneousFatMass?.remark
                    sheetComment = fat.comments.subcutaneousFatMass?.comment
                    trendTitle = "Subcutaneous Fat Trend"
                    trendSubtitle = "30 day subcutaneous fat mass"
                    trendPoints = fat.trends[FAT_METRIC_SUBCUTANEOUS_FAT_MASS_KG].orEmpty()
                    trendUnit = "kg"
                    trendColor = Success
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    text = title,
                    color = Blue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    style = compactTextStyle(),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = headerText,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = compactTextStyle(),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(16.dp))

                sheetRemark?.let { remark ->
                    Box(modifier = Modifier.align(Alignment.Start)) {
                        FatRemarkPill(remark = remark)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Explanatory card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = TrackBackground,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = CardBorder,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = sheetComment ?: explanation,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        style = compactTextStyle()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                FatTrendPlotCard(
                    title = trendTitle,
                    subtitle = trendSubtitle,
                    points = trendPoints,
                    unit = trendUnit,
                    lineColor = trendColor
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun VisceralSubcutaneousDeltaCard(
    fat: FatUiState,
    modifier: Modifier = Modifier
) {
    val comment = fat.comments.visceralSubcutaneous30dDelta
    if (comment?.remark == null && comment?.comment == null) return

    val visDeltaVal = fat.visceralFatDeltaKg
    val subDeltaVal = fat.subcutaneousFatDeltaKg

    val visDelta = visDeltaVal?.let { if (it > 0) "+${"%.2f".format(it)}" else "%.2f".format(it) } ?: "--"
    val subDelta = subDeltaVal?.let { if (it > 0) "+${"%.2f".format(it)} kg" else "${"%.2f".format(it)} kg" } ?: "--"

    CustomizedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color(0xFFE0E7FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = Blue,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = "VISCERAL VS SUBCUTANEOUS",
                    color = Blue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp,
                    style = compactTextStyle()
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Visceral Fat",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        style = compactTextStyle()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = visDelta,
                        color = if (visDelta.startsWith("-")) Success else if (visDelta == "--") TextPrimary else Red,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        style = compactTextStyle()
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Subcutaneous",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        style = compactTextStyle()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subDelta,
                        color = if (subDelta.startsWith("-")) Success else if (subDelta == "--") TextPrimary else Orange,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        style = compactTextStyle()
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                comment.remark?.let { remark ->
                    Text(
                        text = remark,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        style = compactTextStyle()
                    )
                }

                comment.comment?.let { detail ->
                    Text(
                        text = detail,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        style = compactTextStyle()
                    )
                }
            }
        }
    }
}
