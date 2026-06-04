package com.aneesh.healthmaxxing.ui.metrics

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aneesh.healthmaxxing.R
import com.aneesh.healthmaxxing.data.remote.CompositionSummary
import com.aneesh.healthmaxxing.data.remote.Measurements
import com.aneesh.healthmaxxing.data.remote.ProfileEssentialsResponse
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val TextPrimary = Color(0xFF172A35)
private val TextSecondary = Color(0xFF6B7A86)
private val BorderSoft = Color(0xFFE6EEF2)
private val SurfaceSoft = Color(0xFFF8FAFC)
private val Blue = Color(0xFF4354B8)
private val Purple = Color(0xFF6E5BB8)
private val Cyan = Color(0xFF087E8B)
private val Green = Color(0xFF34A77B)
private val Orange = Color(0xFFF59E0B)
private val Success = Green

private object YoungerBodyAgeColors {
    val Background = Color.White.copy(alpha = 0.92f)
    val PanelBackground = SurfaceSoft.copy(alpha = 0.42f)
    val PrimaryText = TextPrimary
    val AccentText = Green
    val BodyAgeGreen = Green
    val BodyAgeGreenLight = Cyan
    val PaleGreenCircle = Green.copy(alpha = 0.10f)
    val BadgeBackground = Green.copy(alpha = 0.10f)
    val CardBorder = BorderSoft
    val ActualAgeGray = TextSecondary
    val SecondaryText = TextSecondary
    val TrackGray = BorderSoft
    val TickGray = BorderSoft
    val MarkerGray = TextSecondary.copy(alpha = 0.72f)
}

data class BodyAgeComparisonUiModel(
    val bodyAge: Int = 28,
    val actualAge: Int = 34,
    val minAge: Int = 20,
    val maxAge: Int = 40,
    val statusText: String = "Great work!",
    val headline: String = "Your body is younger\nthan your actual age."
) {
    val ageDifference: Int
        get() = actualAge - bodyAge

    val bodyAgeProgress: Float
        get() = ((bodyAge - minAge).toFloat() / (maxAge - minAge))
            .coerceIn(0f, 1f)

    val actualAgeProgress: Float
        get() = ((actualAge - minAge).toFloat() / (maxAge - minAge))
            .coerceIn(0f, 1f)

    val resultBadgeText: String
        get() = when {
            ageDifference > 0 -> "$ageDifference years younger"
            ageDifference == 0 -> "Age matched"
            else -> "${-ageDifference} years higher"
        }
}

private val BodyFatTopPadding = 36.dp
private val MuscleMassBottomPadding = 80.dp
private val LeanMassTopPadding = 30.dp
private val ProteinBottomPadding = 32.dp
private val HydrationBottomPadding = 44.dp
private val ChartLabelWidth = 100.dp
private val ChartLabelHorizontalPadding = 8.dp
private val MuscleMassElbowOffsetX = 20.dp

fun formatDateTime(dateString: String): String {
    return try {
        val parser = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US)
        val formatter = java.text.SimpleDateFormat("MMM dd, yyyy • h:mm a", java.util.Locale.US)
        parser.parse(dateString)?.let { formatter.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

@Composable
fun Summary(
    essentialsResponse: ProfileEssentialsResponse?,
    isLoading: Boolean,
    error: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(horizontal = 2.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        val essentials = essentialsResponse?.essentials

        if (essentials != null) {
            SummaryHeader(dateString = essentials.measurements.createdAt)
            FormaScoreCard(score = essentials.formaScore.score, status = essentials.formaScore.remark)

            BodyAgeHealthScoreCard(
                model = BodyAgeComparisonUiModel(
                    bodyAge = essentials.bodyAge,
                    actualAge = essentials.realAge
                )
            )

            BodyCompositionPanel(composition = essentials.compositionSummary)
            BodyMeasurementsPanel(measurements = essentials.measurements)
            WeightDashboard(
                currentWeight = essentials.currentWeight.toFloat(),
                goalWeight = essentials.goalWeight.toFloat(),
                averageWeight = essentials.averageWeight30d.toFloat(),
                lowestWeight = essentials.lowestWeight30d.toFloat(),
                weights = if (essentials.last30DaysWeightTrend.isEmpty()) defaultWeightDataKg else essentials.last30DaysWeightTrend.map { it.weight.toFloat() },
                dateLabel = formatDateTime(essentials.last30DaysWeightTrend.lastOrNull()?.createdAt ?: essentials.measurements.createdAt)
            )
        }
    }
}

@Composable
fun CustomizedCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.92f)
        ),
        border = BorderStroke(1.dp, BorderSoft),
        content = content
    )
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun BodyOverviewCard(
    title: String,
    remarks: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.92f)
        ),
        border = BorderStroke(1.dp, Color(0xFFE6EEF2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight()
                    .padding(start = 22.dp, top = 18.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "AI OVERVIEW",
                    color = Blue,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    style = compactTextStyle()
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 19.sp,
                    lineHeight = 23.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = compactTextStyle()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = remarks,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Normal,
                    style = compactTextStyle()
                )
            }

            Box(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight()
                    .clipToBounds(),
                contentAlignment = Alignment.CenterEnd
            ) {
                // Subtle dotted pattern background
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val dotColor = Blue.copy(alpha = 0.18f)
                    val dotRadius = 1.dp.toPx()
                    val spacing = 6.dp.toPx()

                    val cols = (size.width / spacing).toInt() + 1
                    val rows = (size.height / spacing).toInt() + 1
                    for (i in 0 until cols) {
                        for (j in 0 until rows) {
                            drawCircle(
                                color = dotColor,
                                radius = dotRadius,
                                center = Offset(i * spacing, j * spacing)
                            )
                        }
                    }
                }

                val image = AnimatedImageVector.animatedVectorResource(R.drawable.progress)
                var atEnd by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(300)
                    atEnd = true
                }
                val painter = rememberAnimatedVectorPainter(image, atEnd)

                Image(
                    painter = painter,
                    contentDescription = "Progress illustration",
                    modifier = Modifier
                        .padding(end = 0.dp)
                        .requiredSize(150.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
fun BodyAgeHealthScoreCard(
    modifier: Modifier = Modifier,
    model: BodyAgeComparisonUiModel = BodyAgeComparisonUiModel()
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = YoungerBodyAgeColors.Background
        ),
        border = BorderStroke(1.dp, YoungerBodyAgeColors.CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(YoungerBodyAgeColors.Background)
                .padding(16.dp)
        ) {
            BodyAgeDottedTexture(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(width = 132.dp, height = 110.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(YoungerBodyAgeColors.PaleGreenCircle),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = YoungerBodyAgeColors.BodyAgeGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = model.statusText,
                        color = YoungerBodyAgeColors.AccentText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 17.sp,
                        style = compactTextStyle()
                    )
                }

                Text(
                    text = model.headline,
                    color = YoungerBodyAgeColors.PrimaryText,
                    fontSize = 20.sp,
                    lineHeight = 25.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = compactTextStyle(),
                    modifier = Modifier.padding(end = 96.dp)
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = YoungerBodyAgeColors.PanelBackground,
                    border = BorderStroke(1.dp, YoungerBodyAgeColors.CardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        BodyAgeMetricsRow(model)
                        AgeComparisonScale(model)
                        ResultBadge(
                            text = model.resultBadgeText,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BodyAgeDottedTexture(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val dotColor = Blue.copy(alpha = 0.12f)
        val dotRadius = 1.dp.toPx()
        val spacing = 7.dp.toPx()

        val cols = (size.width / spacing).toInt() + 1
        val rows = (size.height / spacing).toInt() + 1
        for (i in 0 until cols) {
            for (j in 0 until rows) {
                val fadeX = 1f - (i.toFloat() / cols.toFloat())
                val fadeY = 1f - (j.toFloat() / rows.toFloat()) * 0.35f
                drawCircle(
                    color = dotColor.copy(alpha = dotColor.alpha * fadeX * fadeY),
                    radius = dotRadius,
                    center = Offset(i * spacing, j * spacing)
                )
            }
        }
    }
}

@Composable
private fun BodyAgeMetricsRow(
    model: BodyAgeComparisonUiModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        AgeMetricColumn(
            label = "Body Age",
            age = model.bodyAge,
            color = YoungerBodyAgeColors.BodyAgeGreen,
            horizontalAlignment = Alignment.Start
        )
        AgeMetricColumn(
            label = "Actual Age",
            age = model.actualAge,
            color = YoungerBodyAgeColors.ActualAgeGray,
            horizontalAlignment = Alignment.End
        )
    }
}

@Composable
private fun AgeMetricColumn(
    label: String,
    age: Int,
    color: Color,
    horizontalAlignment: Alignment.Horizontal,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 16.sp,
            style = compactTextStyle()
        )
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = age.toString(),
                color = color,
                fontSize = 42.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 42.sp,
                style = compactTextStyle(),
                modifier = Modifier.alignByBaseline()
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "years",
                color = color,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                style = compactTextStyle(),
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .alignByBaseline()
            )
        }
    }
}

@Composable
private fun AgeComparisonScale(
    model: BodyAgeComparisonUiModel,
    modifier: Modifier = Modifier
) {
    val ticks = listOf(model.minAge, 25, 30, 35, model.maxAge)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
        ) {
            val markerSize = 30.dp
            val bodyMarkerOffset = (maxWidth * model.bodyAgeProgress) - (markerSize / 2)
            val actualOffset = maxWidth * model.actualAgeProgress

            Canvas(modifier = Modifier.fillMaxSize()) {
                val trackTop = 40.dp.toPx()
                val trackHeight = 20.dp.toPx()
                val cornerRadius = trackHeight / 2f
                val fillWidth = size.width * 0.56f

                drawRoundRect(
                    color = YoungerBodyAgeColors.TrackGray,
                    topLeft = Offset(0f, trackTop),
                    size = Size(size.width, trackHeight),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            YoungerBodyAgeColors.BodyAgeGreen,
                            YoungerBodyAgeColors.BodyAgeGreenLight
                        ),
                        startX = 0f,
                        endX = fillWidth
                    ),
                    topLeft = Offset(0f, trackTop),
                    size = Size(fillWidth, trackHeight),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )

                ticks.forEach { tick ->
                    val progress = ((tick - model.minAge).toFloat() / (model.maxAge - model.minAge))
                        .coerceIn(0f, 1f)
                    val x = size.width * progress
                    drawLine(
                        color = YoungerBodyAgeColors.TickGray,
                        start = Offset(x, trackTop + trackHeight + 3.dp.toPx()),
                        end = Offset(x, trackTop + trackHeight + 11.dp.toPx()),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }

            Box(
                modifier = Modifier
                    .offset(x = actualOffset)
                    .width(1.dp)
                    .height(27.dp)
                    .align(Alignment.TopStart)
                    .background(YoungerBodyAgeColors.MarkerGray)
            )

            Column(
                modifier = Modifier
                    .offset(x = bodyMarkerOffset)
                    .align(Alignment.TopStart),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(markerSize)
                        .clip(CircleShape)
                        .background(YoungerBodyAgeColors.BodyAgeGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(18.dp)
                        .background(YoungerBodyAgeColors.BodyAgeGreen)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ticks.forEach { tick ->
                Text(
                    text = tick.toString(),
                    color = YoungerBodyAgeColors.SecondaryText,
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                    style = compactTextStyle()
                )
            }
        }
    }
}

@Composable
private fun ResultBadge(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = YoungerBodyAgeColors.BadgeBackground,
        shape = RoundedCornerShape(999.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = YoungerBodyAgeColors.BodyAgeGreen,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                color = YoungerBodyAgeColors.PrimaryText,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium,
                style = compactTextStyle()
            )
        }
    }
}

private object FormaScoreColors {
    val Background = Color.White.copy(alpha = 0.92f)
    val DeepGreen = TextPrimary
    val GaugeGreenStart = Green
    val GaugeGreenEnd = Cyan
    val Track = BorderSoft
    val StatusGreen = Success
    val PrimaryText = TextPrimary
    val SecondaryText = TextSecondary
}

@Composable
fun FormaScoreCard(
    modifier: Modifier = Modifier,
    score: Int = 86,
    maxScore: Int = 100,
    status: String = "Good",
) {
    val progress = (score.toFloat() / maxScore.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = FormaScoreColors.Background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, BorderSoft)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Your Forma Score",
                fontSize = 22.sp,
                color = FormaScoreColors.DeepGreen,
                fontWeight = FontWeight.SemiBold,
                style = compactTextStyle()
            )

            Text(
                text = "Today’s snapshot of your body-health\nand readiness.",
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = FormaScoreColors.SecondaryText,
                style = compactTextStyle()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(240.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 11.dp.toPx()
                    val inset = strokeWidth / 2f
                    val diameter = size.width - strokeWidth
                    val topLeft = Offset(inset, inset)
                    val arcSize = Size(diameter, diameter)
                    val startAngle = 150f
                    val totalSweep = 240f

                    drawArc(
                        color = FormaScoreColors.Track,
                        startAngle = startAngle,
                        sweepAngle = totalSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    drawArc(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                FormaScoreColors.GaugeGreenStart,
                                FormaScoreColors.GaugeGreenEnd
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height)
                        ),
                        startAngle = startAngle,
                        sweepAngle = totalSweep * progress,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = score.toString(),
                        fontSize = 64.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FormaScoreColors.DeepGreen,
                        lineHeight = 72.sp,
                        style = compactTextStyle()
                    )
                    Text(
                        text = status,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = FormaScoreColors.StatusGreen,
                        lineHeight = 24.sp,
                        style = compactTextStyle()
                    )
                    Text(
                        text = "Forma Score",
                        fontSize = 14.sp,
                        color = FormaScoreColors.PrimaryText,
                        lineHeight = 18.sp,
                        style = compactTextStyle()
                    )
                }

                Text(
                    text = "0",
                    fontSize = 11.sp,
                    color = FormaScoreColors.SecondaryText,
                    style = compactTextStyle(),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 30.dp, y = 188.dp)
                )

                Text(
                    text = maxScore.toString(),
                    fontSize = 11.sp,
                    color = FormaScoreColors.SecondaryText,
                    style = compactTextStyle(),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-38).dp, y = 188.dp)
                )
            }
        }
    }
}


@Composable
private fun SummaryHeader(dateString: String = "May 12, 2024 • 8:15 AM") {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(
                text = "Latest Scan",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 26.sp,
                style = compactTextStyle()
            )
            Text(
                text = formatDateTime(dateString),
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                style = compactTextStyle()
            )
        }

        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(SurfaceSoft)
                .border(1.dp, BorderSoft, CircleShape)
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                contentDescription = "Scan History",
                modifier = Modifier.size(17.dp),
                tint = TextPrimary
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = "History",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 16.sp,
                style = compactTextStyle()
            )
        }
    }
}

@Composable
private fun BodyCompositionPanel(composition: CompositionSummary) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.92f),
        border = BorderStroke(1.dp, BorderSoft),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val chartSize = maxWidth.coerceAtMost(340.dp)
                val dynamicSegments = listOf(
                    SummarySegment("Lean Mass", "${composition.leanMassPct}%", Blue, composition.leanMassPct.toFloat(), isLeft = false),
                    SummarySegment("Protein", "${composition.proteinPct}%", Purple, composition.proteinPct.toFloat(), isLeft = false),
                    SummarySegment("Hydration", "${composition.hydrationPct}%", Cyan, composition.hydrationPct.toFloat(), isLeft = false),
                    SummarySegment("Muscle Mass", "${composition.muscleMassPct}%", Green, composition.muscleMassPct.toFloat(), isLeft = true),
                    SummarySegment("Body Fat", "${composition.bodyFatPct}%", Orange, composition.bodyFatPct.toFloat(), isLeft = true)
                )
                DonutChart(chartSize = chartSize, segments = dynamicSegments, compositionScore = composition.compositionScore)
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                color = BorderSoft
            )

            ProgressMessageContent()
        }
    }
}

@Composable
private fun ChartLabel(
    label: String,
    value: String,
    color: Color,
    alignment: Alignment,
    modifier: Modifier = Modifier
) {
    val isLeft = alignment == Alignment.TopStart || alignment == Alignment.BottomStart
    Column(
        modifier = modifier
            .width(ChartLabelWidth)
            .padding(horizontal = 4.dp),
        horizontalAlignment = if (isLeft) Alignment.Start else Alignment.End,
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        Text(
            text = value,
            color = color,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 18.sp,
            style = compactTextStyle(),
            textAlign = if (isLeft) TextAlign.Start else TextAlign.End
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 14.sp,
            style = compactTextStyle(),
            textAlign = if (isLeft) TextAlign.Start else TextAlign.End
        )
    }
}

@Composable
private fun DonutChart(chartSize: Dp, segments: List<SummarySegment>, compositionScore: Int) {
    Box(
        modifier = Modifier
            .size(width = chartSize, height = 300.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerX = width / 2f
            val centerY = height / 2f

            // donut radius is 25% of available width to leave room for labels/pointers
            val donutRadius = minOf(width, height) * 0.25f
            val strokeWidth = donutRadius * 0.32f
            val outerRadius = donutRadius + strokeWidth / 2f
            val innerRadius = donutRadius - strokeWidth / 2f

            val total = segments.sumOf { it.amount.toDouble() }.toFloat().takeIf { it > 0f } ?: 1f
            var startAngle = -90f
            val gapAngle = 4f

            // Draw background track
            drawArc(
                color = Color(0xFFEFF2F6),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(centerX - donutRadius, centerY - donutRadius),
                size = Size(donutRadius * 2, donutRadius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )

            // Match the fixed ChartLabel size and manual padding used below.
            val labelHeight = 32.dp.toPx()
            val labelHorizontalPadding = ChartLabelHorizontalPadding.toPx()

            segments.forEach { segment ->
                val sectionAngle = (segment.amount / total) * 360f
                val sweepAngle = sectionAngle - gapAngle

                // Draw colored segment arc
                drawArc(
                    color = segment.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(centerX - donutRadius, centerY - donutRadius),
                    size = Size(donutRadius * 2, donutRadius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )

                // Calculate midpoint angle of segment
                val midAngle = startAngle + sweepAngle / 2f
                val midAngleRad = midAngle * (PI.toFloat() / 180f)

                // Outer edge start point
                val startX = centerX + cos(midAngleRad) * outerRadius
                val startY = centerY + sin(midAngleRad) * outerRadius

                // Elbow point
                val extendedRadius = outerRadius + 24.dp.toPx()
                val elbowX = centerX + cos(midAngleRad) * extendedRadius + when (segment.label) {
                    "Muscle Mass" -> MuscleMassElbowOffsetX.toPx()
                    else -> 0f
                }

                // Determine target Y based on the label's manual layout position
                val targetY = when (segment.label) {
                    "Body Fat" -> BodyFatTopPadding.toPx() + labelHeight / 2f
                    "Lean Mass" -> LeanMassTopPadding.toPx() + labelHeight / 2f
                    "Protein" -> (height / 2f) - (ProteinBottomPadding.toPx() / 2f)
                    "Hydration" -> height - HydrationBottomPadding.toPx() - labelHeight / 2f
                    "Muscle Mass" -> height - MuscleMassBottomPadding.toPx() - labelHeight / 2f
                    else -> centerY
                }

                val endX = if (segment.isLeft) {
                    labelHorizontalPadding
                } else {
                    width - labelHorizontalPadding
                }
                val endY = targetY

                // Draw angled line segment
                drawLine(
                    color = segment.color.copy(alpha = 0.5f),
                    start = Offset(startX, startY),
                    end = Offset(elbowX, endY),
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Draw horizontal line segment
                drawLine(
                    color = segment.color.copy(alpha = 0.5f),
                    start = Offset(elbowX, endY),
                    end = Offset(endX, endY),
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                startAngle += sectionAngle
            }

            // Draw center white circle
            drawCircle(
                color = Color.White,
                radius = innerRadius - 2.dp.toPx(),
                center = Offset(centerX, centerY)
            )
        }

        // Center score column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = compositionScore.toString(),
                color = TextPrimary,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 40.sp,
                style = compactTextStyle()
            )
            Text(
                text = "Composition Score",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 14.sp,
                style = compactTextStyle()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(Color(0xFFE8F5E9))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "Excellent",
                    color = Success,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    style = compactTextStyle()
                )
            }
        }

        // Position the labels manually around the chart
        ChartLabel(
            label = "Body Fat",
            value = segments.find { it.label == "Body Fat" }?.value ?: "",
            color = Orange,
            alignment = Alignment.TopStart,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = BodyFatTopPadding)
        )

        ChartLabel(
            label = "Muscle Mass",
            value = segments.find { it.label == "Muscle Mass" }?.value ?: "",
            color = Green,
            alignment = Alignment.BottomStart,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 8.dp, bottom = MuscleMassBottomPadding)
        )

        ChartLabel(
            label = "Lean Mass",
            value = segments.find { it.label == "Lean Mass" }?.value ?: "",
            color = Blue,
            alignment = Alignment.TopEnd,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 8.dp, top = LeanMassTopPadding)
        )

        ChartLabel(
            label = "Protein",
            value = segments.find { it.label == "Protein" }?.value ?: "",
            color = Purple,
            alignment = Alignment.CenterEnd,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp, bottom = ProteinBottomPadding)
        )

        ChartLabel(
            label = "Hydration",
            value = segments.find { it.label == "Hydration" }?.value ?: "",
            color = Cyan,
            alignment = Alignment.BottomEnd,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 8.dp, bottom = HydrationBottomPadding)
        )
    }
}

@Composable
private fun ProgressMessageContent() {
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
                .background(Color(0xFFEFF6FF)),
            contentAlignment = Alignment.Center
        ) {
            TrendLineIcon(
                modifier = Modifier.size(20.dp),
                color = Blue
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = TextSecondary, fontWeight = FontWeight.Normal)) {
                        append("You’re ")
                    }
                    withStyle(SpanStyle(color = Success, fontWeight = FontWeight.SemiBold)) {
                        append("2.1%")
                    }
                    withStyle(SpanStyle(color = TextSecondary, fontWeight = FontWeight.Normal)) {
                        append(" better than your last scan")
                    }
                },
                fontSize = 13.sp,
                lineHeight = 16.sp,
                style = compactTextStyle()
            )
            Text(
                text = "Keep up the great work.",
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 15.sp,
                style = compactTextStyle()
            )
        }
    }
}

@Composable
private fun TrendLineIcon(
    modifier: Modifier,
    color: Color
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.2.dp.toPx()
        val points = listOf(
            Offset(size.width * .10f, size.height * .72f),
            Offset(size.width * .35f, size.height * .52f),
            Offset(size.width * .54f, size.height * .62f),
            Offset(size.width * .88f, size.height * .25f)
        )

        for (index in 0 until points.lastIndex) {
            drawLine(
                color = color,
                start = points[index],
                end = points[index + 1],
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
        drawLine(
            color = color,
            start = points.last(),
            end = Offset(size.width * .83f, size.height * .45f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = points.last(),
            end = Offset(size.width * .68f, size.height * .29f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

private data class SummarySegment(
    val label: String,
    val value: String,
    val color: Color,
    val amount: Float,
    val isLeft: Boolean
)

private fun compactTextStyle() = TextStyle(
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)

@Composable
private fun BodyMeasurementsPanel(
    measurements: Measurements,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")

    // 1. Pulsing halo size & opacity for markers
    val markerPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "markerPulseScale"
    )
    val markerPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.04f,
        targetValue = 0.20f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "markerPulseAlpha"
    )

    // 2. Flowing dash offset for dotted lines (crawling animation)
    val dashOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dashOffset"
    )

    // 3. Breathing grid opacity
    val gridAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gridAlpha"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BorderSoft),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                val widthDp = maxWidth
                val heightDp = maxHeight

                val bodySizeDp = 210.dp
                val density = androidx.compose.ui.platform.LocalDensity.current

                val widthPx = with(density) { widthDp.toPx() }
                val heightPx = with(density) { heightDp.toPx() }
                val bodySizePx = with(density) { bodySizeDp.toPx() }

                val leftPx = (widthPx - bodySizePx) / 2
                val topPx = (heightPx - bodySizePx) / 2

                // 1. Concentric scan circles in background (breathing opacity)
                Canvas(
                    modifier = Modifier
                        .size(bodySizeDp)
                        .align(Alignment.Center)
                ) {
                    val stroke = Stroke(
                        width = 0.8.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 8f), 0f)
                    )
                    val gridColor = Color(0xFFE2E8F0).copy(alpha = gridAlpha)

                    drawCircle(
                        color = gridColor,
                        radius = size.width * 0.44f,
                        style = stroke
                    )
                    drawCircle(
                        color = gridColor,
                        radius = size.width * 0.30f,
                        style = stroke
                    )
                    drawCircle(
                        color = gridColor,
                        radius = size.width * 0.16f,
                        style = stroke
                    )
                }

                // 2. Centered body image
                Box(
                    modifier = Modifier
                        .size(bodySizeDp)
                        .align(Alignment.Center)
                ) {
                    Image(
                        painter = painterResource(R.drawable.body),
                        contentDescription = "Body illustration",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                // 3. Canvas overlay for lines and markers
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val scale = bodySizePx / 100f

                    fun getCanvasCoords(vx: Float, vy: Float): Offset {
                        return Offset(
                            x = leftPx + vx * scale,
                            y = topPx + vy * scale
                        )
                    }

                    val leftLineEndX = 88.dp.toPx()
                    val rightLineEndX = widthPx - 88.dp.toPx()

                    // Separate dotted path effects to make the dots flow outwards from center
                    val dottedEffectRight = PathEffect.dashPathEffect(
                        floatArrayOf(5f, 5f), phase = dashOffset
                    )
                    val dottedEffectLeft = PathEffect.dashPathEffect(
                        floatArrayOf(5f, 5f), phase = -dashOffset
                    )

                    val markerColor = Blue // soft blue marker accent
                    val lineColor = Color(0xFF93C5FD).copy(alpha = 0.5f) // soft blue dotted lines

                    fun drawMarkerAndLine(
                        vx: Float,
                        vy: Float,
                        toRight: Boolean,
                        endVy: Float = vy
                    ) {
                        val markerPos = getCanvasCoords(vx, vy)
                        val endX = if (toRight) rightLineEndX else leftLineEndX
                        val endY = getCanvasCoords(vx, endVy).y
                        val lineEnd = Offset(endX, endY)
                        val dottedEffect = if (toRight) dottedEffectRight else dottedEffectLeft

                        // Thin dotted guide line (always full length, flowing animated dots)
                        drawLine(
                            color = lineColor,
                            start = markerPos,
                            end = lineEnd,
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = dottedEffect,
                            cap = StrokeCap.Round
                        )

                        // Pulsing Outer Halo
                        drawCircle(
                            color = markerColor.copy(alpha = markerPulseAlpha),
                            radius = 9.dp.toPx() * markerPulseScale,
                            center = markerPos
                        )

                        // Core Ring
                        drawCircle(
                            color = markerColor,
                            radius = 4.5.dp.toPx(),
                            center = markerPos
                        )

                        // Center dot
                        drawCircle(
                            color = Color.White,
                            radius = 2.dp.toPx(),
                            center = markerPos
                        )
                    }

                    // Neck: Left side
                    drawMarkerAndLine(50.0f, 14.5f, toRight = false)

                    // Shoulder: Right side
                    drawMarkerAndLine(62.0f, 22.5f, toRight = true, endVy = 16.0f)

                    // Chest: Left side
                    drawMarkerAndLine(50.0f, 28.0f, toRight = false)

                    // Bicep: Right side
                    drawMarkerAndLine(63.5f, 34.0f, toRight = true, endVy = 31.0f)

                    // Waist: Left side
                    drawMarkerAndLine(50.0f, 42.0f, toRight = false)

                    // Stomach: Right side
                    drawMarkerAndLine(50.0f, 48.0f, toRight = true, endVy = 46.0f)

                    // Thighs: Right side
                    drawMarkerAndLine(56.5f, 58.0f, toRight = true, endVy = 61.0f)

                    // Calf: Left side
                    drawMarkerAndLine(43.0f, 79.0f, toRight = false)
                }

                // 4. Position labels at side ends
                val scale = bodySizePx / 100f

                // Right side labels
                MeasurementLabel(
                    label = "Shoulder",
                    value = String.format(java.util.Locale.US, "%.1f", measurements.shoulderCm),
                    unit = "cm",
                    isLeft = false,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 10.dp)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = (topPx + 16.0f * scale - 15.dp.toPx()).toInt()
                            )
                        }
                )

                MeasurementLabel(
                    label = "Bicep",
                    value = String.format(java.util.Locale.US, "%.1f", measurements.bicepCm),
                    unit = "cm",
                    isLeft = false,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 10.dp)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = (topPx + 31.0f * scale - 15.dp.toPx()).toInt()
                            )
                        }
                )

                MeasurementLabel(
                    label = "Stomach",
                    value = String.format(java.util.Locale.US, "%.1f", measurements.stomachCm),
                    unit = "cm",
                    isLeft = false,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 10.dp)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = (topPx + 46.0f * scale - 15.dp.toPx()).toInt()
                            )
                        }
                )

                MeasurementLabel(
                    label = "Thighs",
                    value = String.format(java.util.Locale.US, "%.1f", measurements.thighCm),
                    unit = "cm",
                    isLeft = false,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 10.dp)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = (topPx + 61.0f * scale - 15.dp.toPx()).toInt()
                            )
                        }
                )

                // Left side labels
                MeasurementLabel(
                    label = "Neck",
                    value = String.format(java.util.Locale.US, "%.1f", measurements.neckCm),
                    unit = "cm",
                    isLeft = true,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 10.dp)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = (topPx + 14.5f * scale - 15.dp.toPx()).toInt()
                            )
                        }
                )

                MeasurementLabel(
                    label = "Chest",
                    value = String.format(java.util.Locale.US, "%.1f", measurements.chestCm),
                    unit = "cm",
                    isLeft = true,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 10.dp)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = (topPx + 28.0f * scale - 15.dp.toPx()).toInt()
                            )
                        }
                )

                MeasurementLabel(
                    label = "Waist",
                    value = String.format(java.util.Locale.US, "%.1f", measurements.waistCm),
                    unit = "cm",
                    isLeft = true,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 10.dp)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = (topPx + 42.0f * scale - 15.dp.toPx()).toInt()
                            )
                        }
                )

                MeasurementLabel(
                    label = "Calf",
                    value = String.format(java.util.Locale.US, "%.1f", measurements.calfCm),
                    unit = "cm",
                    isLeft = true,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 10.dp)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = (topPx + 79.0f * scale - 15.dp.toPx()).toInt()
                            )
                        }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Redesigned metrics panel showing ratios
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(16.dp))
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompactMetricItem(
                    label = "Shoulder\n÷ Waist",
                    value = if (measurements.waistCm > 0) String.format(java.util.Locale.US, "%.2f", measurements.shoulderCm / measurements.waistCm) else "-",
                    unit = "",
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(Color(0xFFE2E8F0))
                )
                CompactMetricItem(
                    label = "Chest\n÷ Waist",
                    value = if (measurements.waistCm > 0) String.format(java.util.Locale.US, "%.2f", measurements.chestCm / measurements.waistCm) else "-",
                    unit = "",
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(Color(0xFFE2E8F0))
                )
                CompactMetricItem(
                    label = "Bicep\n÷ Waist",
                    value = if (measurements.waistCm > 0) String.format(java.util.Locale.US, "%.2f", measurements.bicepCm / measurements.waistCm) else "-",
                    unit = "",
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(Color(0xFFE2E8F0))
                )
                CompactMetricItem(
                    label = "Thigh\n÷ Waist",
                    value = if (measurements.waistCm > 0) String.format(java.util.Locale.US, "%.2f", measurements.thighCm / measurements.waistCm) else "-",
                    unit = "",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

        }
    }
}

private val defaultWeightDataKg = listOf(
    80.3f, 80.0f, 79.6f, 79.5f, 79.4f,
    79.0f, 78.7f, 78.5f, 78.4f, 78.2f,
    78.8f, 78.6f, 78.2f, 78.1f, 78.0f,
    77.5f, 77.4f, 77.2f, 77.5f, 77.1f,
    76.9f, 76.7f, 76.7f, 76.6f, 76.1f,
    76.3f, 76.4f, 76.2f, 76.3f, 76.4f
)

@Composable
fun WeightDashboard(
    currentWeight: Float = 76.4f,
    unit: String = "kg",
    dateLabel: String = "May 20, 2024",
    weights: List<Float> = defaultWeightDataKg,
    averageWeight: Float = 77.8f,
    lowestWeight: Float = 76.1f,
    goalWeight: Float = 72.6f,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.92f),
        border = BorderStroke(1.dp, BorderSoft),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            CurrentWeightHeader(currentWeight, unit, dateLabel)
            WeightTrendChart(weights, unit, goalWeight)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    icon = Icons.Filled.AutoAwesome,
                    label = "Average",
                    valueText = String.format(java.util.Locale.US, "%.1f", averageWeight),
                    unit = unit,
                    caption = "Last 30d",
                    captionColor = TextSecondary,
                    modifier = Modifier.fillMaxWidth()
                )
                MetricCard(
                    icon = Icons.Filled.Person,
                    label = "Lowest",
                    valueText = String.format(java.util.Locale.US, "%.1f", lowestWeight),
                    unit = unit,
                    caption = "All time",
                    captionColor = TextSecondary,
                    modifier = Modifier.fillMaxWidth()
                )
                MetricCard(
                    icon = Icons.Filled.Check,
                    label = "Goal",
                    valueText = String.format(java.util.Locale.US, "%.1f", goalWeight),
                    unit = unit,
                    caption = "${String.format(java.util.Locale.US, "%.1f", currentWeight - goalWeight)} left",
                    captionColor = Success,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun CurrentWeightHeader(
    currentWeight: Float,
    unit: String,
    dateLabel: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Current Weight",
            style = TextStyle(
                fontSize = 13.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
        )
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = String.format(java.util.Locale.US, "%.1f", currentWeight),
                style = TextStyle(
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                ),
                modifier = Modifier.alignByBaseline()
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = unit,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier.alignByBaseline()
            )
        }
        Text(
            text = dateLabel,
            style = TextStyle(
                fontSize = 13.sp,
                color = TextSecondary
            )
        )
    }
}

@Composable
private fun WeightTrendChart(
    weights: List<Float>,
    unit: String,
    goalWeight: Float,
    modifier: Modifier = Modifier
) {
    val density = androidx.compose.ui.platform.LocalDensity.current

    val minY = 72f
    val maxY = 82f

    val yAxisLabels = listOf("82", "80", "78", "76", "74", "72")
    val xAxisLabels = listOf("Apr 21", "Apr 28", "May 5", "May 12", "May 19")

    val textPaint = remember(density) {
        android.graphics.Paint().apply {
            color = TextSecondary.toArgb()
            textSize = with(density) { 10.sp.toPx() }
            textAlign = android.graphics.Paint.Align.RIGHT
            typeface = android.graphics.Typeface.create(
                android.graphics.Typeface.DEFAULT,
                android.graphics.Typeface.NORMAL
            )
            isAntiAlias = true
        }
    }

    val xLabelPaint = remember(density) {
        android.graphics.Paint().apply {
            color = TextSecondary.toArgb()
            textSize = with(density) { 10.sp.toPx() }
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.create(
                android.graphics.Typeface.DEFAULT,
                android.graphics.Typeface.NORMAL
            )
            isAntiAlias = true
        }
    }

    val pillPaint = remember(density) {
        android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = with(density) { 12.sp.toPx() }
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.create(
                android.graphics.Typeface.DEFAULT,
                android.graphics.Typeface.BOLD
            )
            isAntiAlias = true
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        val leftPaddingPx = 34.dp.toPx()
        val rightPaddingPx = 16.dp.toPx()
        val topPaddingPx = 10.dp.toPx()
        val bottomPaddingPx = 34.dp.toPx()

        val plotLeft = leftPaddingPx
        val plotRight = size.width - rightPaddingPx
        val plotTop = topPaddingPx
        val plotBottom = size.height - bottomPaddingPx

        val plotWidth = plotRight - plotLeft
        val plotHeight = plotBottom - plotTop

        fun pointToOffset(index: Int, value: Float): Offset {
            val coercedValue = value.coerceIn(minY, maxY)
            val x = plotLeft + index * plotWidth / (weights.lastIndex.coerceAtLeast(1))
            val normalizedY = (coercedValue - minY) / (maxY - minY)
            val y = plotBottom - normalizedY * plotHeight
            return Offset(x, y)
        }

        fun smoothPath(points: List<Offset>): androidx.compose.ui.graphics.Path {
            val path = androidx.compose.ui.graphics.Path()
            if (points.isEmpty()) return path

            path.moveTo(points.first().x, points.first().y)
            if (points.size == 1) return path

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

                path.cubicTo(
                    control1.x,
                    control1.y,
                    control2.x,
                    control2.y,
                    p2.x,
                    p2.y
                )
            }

            return path
        }

        // 1. Gridlines and y-axis labels
        yAxisLabels.forEach { labelStr ->
            val value = labelStr.toFloatOrNull() ?: 72f
            val normalizedY = (value - minY) / (maxY - minY)
            val y = plotBottom - normalizedY * plotHeight

            drawLine(
                color = BorderSoft,
                start = Offset(plotLeft, y),
                end = Offset(plotRight, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
            )

            val textHeight = textPaint.fontMetrics.descent - textPaint.fontMetrics.ascent
            val textY = y + textHeight / 3f
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    labelStr,
                    plotLeft - 8.dp.toPx(),
                    textY,
                    textPaint
                )
            }
        }

        // 1.5 Goal Weight line
        val goalNormalizedY = (goalWeight.coerceIn(minY, maxY) - minY) / (maxY - minY)
        val goalY = plotBottom - goalNormalizedY * plotHeight

        drawLine(
            color = Success.copy(alpha = 0.8f),
            start = Offset(plotLeft, goalY),
            end = Offset(plotRight, goalY),
            strokeWidth = 1.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
        )

        drawIntoCanvas { canvas ->
            val textPaintGoal = android.graphics.Paint(textPaint).apply {
                color = Success.toArgb()
                textSize = with(density) { 10.sp.toPx() }
                typeface = android.graphics.Typeface.create(
                    android.graphics.Typeface.DEFAULT,
                    android.graphics.Typeface.BOLD
                )
            }
            canvas.nativeCanvas.drawText(
                "Goal",
                plotRight - 4.dp.toPx(),
                goalY - 8.dp.toPx(),
                android.graphics.Paint(textPaintGoal).apply { textAlign = android.graphics.Paint.Align.RIGHT }
            )
        }

        // 2. Area fill under the line
        if (weights.isNotEmpty()) {
            val points = weights.mapIndexed { i, value -> pointToOffset(i, value) }
            val path = smoothPath(points)
            val lastOffset = points.last()
            path.lineTo(lastOffset.x, plotBottom)
            path.lineTo(points.first().x, plotBottom)
            path.close()

            drawPath(
                path = path,
                color = Blue.copy(alpha = 0.10f)
            )
        }

        // 3. Trend line
        if (weights.size > 1) {
            val linePath = smoothPath(weights.mapIndexed { i, value -> pointToOffset(i, value) })
            drawPath(
                path = linePath,
                color = Blue,
                style = Stroke(
                    width = 2.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }

        // 4. Data point circles
        val radiusPx = 1.75.dp.toPx()
        weights.forEachIndexed { i, value ->
            val offset = pointToOffset(i, value)
            drawCircle(
                color = Blue.copy(alpha = 0.45f),
                radius = radiusPx,
                center = offset
            )
        }

        // 5. Final vertical guide line
        if (weights.isNotEmpty()) {
            val finalOffset = pointToOffset(weights.lastIndex, weights.last())
            drawLine(
                color = BorderSoft,
                start = Offset(finalOffset.x, plotTop),
                end = Offset(finalOffset.x, plotBottom),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )

            // 6. Final highlighted point
            drawCircle(
                color = Blue,
                radius = 5.dp.toPx(),
                center = finalOffset
            )
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = finalOffset
            )

            // 7. Current-value pill
            val pillText = String.format(java.util.Locale.US, "%.1f", weights.last())
            val textWidth = pillPaint.measureText(pillText)
            val textHeight = pillPaint.fontMetrics.descent - pillPaint.fontMetrics.ascent

            val pillPaddingHorizontal = 8.dp.toPx()
            val pillPaddingVertical = 5.dp.toPx()

            val pillWidth = textWidth + pillPaddingHorizontal * 2f
            val pillHeight = textHeight + pillPaddingVertical * 2f

            val pillRight = finalOffset.x - 8.dp.toPx()
            val pillLeft = pillRight - pillWidth
            val pillBottom = finalOffset.y - 8.dp.toPx()
            val pillTop = pillBottom - pillHeight

            val cornerRadiusPx = 8.dp.toPx()
            drawRoundRect(
                color = Blue,
                topLeft = Offset(pillLeft, pillTop),
                size = Size(pillWidth, pillHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                    cornerRadiusPx,
                    cornerRadiusPx
                )
            )

            val textX = pillLeft + pillWidth / 2f
            val textY =
                pillTop + pillHeight / 2f - (pillPaint.fontMetrics.ascent + pillPaint.fontMetrics.descent) / 2f

            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    pillText,
                    textX,
                    textY,
                    pillPaint
                )
            }
        }

        // 8. X-axis labels and unit label
        xAxisLabels.forEachIndexed { i, label ->
            val labelX = when (i) {
                0 -> {
                    xLabelPaint.textAlign = android.graphics.Paint.Align.LEFT
                    plotLeft + 4.dp.toPx()
                }

                xAxisLabels.lastIndex -> {
                    xLabelPaint.textAlign = android.graphics.Paint.Align.RIGHT
                    plotRight - 4.dp.toPx()
                }

                else -> {
                    xLabelPaint.textAlign = android.graphics.Paint.Align.CENTER
                    plotLeft + i * plotWidth / (xAxisLabels.size - 1)
                }
            }
            val labelY = size.height - 10.dp.toPx()
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    label,
                    labelX,
                    labelY,
                    xLabelPaint
                )
            }
        }

        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawText(
                unit,
                plotLeft - 8.dp.toPx(),
                size.height - 10.dp.toPx(),
                textPaint
            )
        }
    }
}


@Composable
private fun MetricCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    valueText: String,
    unit: String,
    caption: String,
    captionColor: Color,
    modifier: Modifier = Modifier
) {
    CustomizedCard(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Blue.copy(alpha = 0.10f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Blue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = label,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = caption,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = captionColor,
                        fontWeight = FontWeight.Normal
                    ),
                    maxLines = 1
                )
            }

            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = valueText,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    modifier = Modifier.alignByBaseline()
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = TextSecondary
                    ),
                    modifier = Modifier.alignByBaseline()
                )
            }
        }
    }
}

@Composable
private fun MeasurementLabel(
    label: String,
    value: String,
    unit: String,
    isLeft: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(75.dp),
        horizontalAlignment = if (isLeft) Alignment.End else Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        Text(
            text = label.uppercase(),
            color = Color(0xFF94A3B8), // slate-400
            fontSize = 8.5.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            style = compactTextStyle(),
            textAlign = if (isLeft) TextAlign.End else TextAlign.Start
        )
        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                ) {
                    append(value)
                }
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Normal,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                ) {
                    append(" $unit")
                }
            },
            style = compactTextStyle(),
            textAlign = if (isLeft) TextAlign.End else TextAlign.Start
        )
    }
}

@Composable
private fun CompactMetricItem(
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = label.uppercase(),
            color = Color(0xFF94A3B8),
            fontSize = 8.5.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.4.sp,
            style = compactTextStyle(),
            textAlign = TextAlign.Center
        )
        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 16.sp
                    )
                ) {
                    append(value)
                }
                if (unit.isNotEmpty()) {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    ) {
                        append(unit)
                    }
                }
            },
            style = compactTextStyle(),
            textAlign = TextAlign.Center
        )
    }
}

