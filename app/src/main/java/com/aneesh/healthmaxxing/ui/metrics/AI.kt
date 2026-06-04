package com.aneesh.healthmaxxing.ui.metrics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aneesh.healthmaxxing.R
import com.aneesh.healthmaxxing.data.remote.EffortScore
import com.aneesh.healthmaxxing.data.remote.InsightSection
import com.aneesh.healthmaxxing.data.remote.InsightsResponse

private val AiTextPrimary = Color(0xFF172A35)
private val AiTextSecondary = Color(0xFF6B7A86)
private val AiCardBorder = Color(0xFFE6EEF2)
private val AiBlue = Color(0xFF4354B8)
private val AiGreen = Color(0xFF34A77B)
private val AiSurfaceSoft = Color(0xFFF8FAFC)

@Composable
fun AI(
    insightsResponse: InsightsResponse
) {
    val insights = insightsResponse.insights
    val effortScore = insightsResponse.effortScore

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        BodyOverviewCard(
            title = insights.overviewTitle,
            remarks = insights.overviewRemarks
        )
        HeroInsightCard(insightSection = insights.foundation)
        MomentumInsightCard(insightSection = insights.momentum)
        BiggestLeverInsightCard(insightSection = insights.biggestLever)
        PhysiqueArchetypeCard(archetype = insights.physiqueArchetype)
        EffortScoreCard(effortScore = effortScore)
    }
}

@Composable
fun HeroInsightCard(
    insightSection: InsightSection,
    modifier: Modifier = Modifier
) {
    AiInsightCard(
        modifier = modifier,
        label = "FOUNDATION",
        icon = {
            Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = null,
                tint = AiGreen,
                modifier = Modifier.size(14.dp)
            )
        },
        headline = buildAnnotatedString { append(insightSection.headline) },
        supportingText = insightSection.supportingDescription,
        footerText = insightSection.actionableInsight
    )
}

@Composable
fun MomentumInsightCard(
    insightSection: InsightSection,
    modifier: Modifier = Modifier
) {
    AiInsightCard(
        modifier = modifier,
        label = "MOMENTUM",
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                contentDescription = null,
                tint = AiGreen,
                modifier = Modifier.size(14.dp)
            )
        },
        headline = buildAnnotatedString { append(insightSection.headline) },
        supportingText = insightSection.supportingDescription,
        footerText = insightSection.actionableInsight
    ) {
        MomentumTrendGraph()
    }
}

@Composable
fun BiggestLeverInsightCard(
    insightSection: InsightSection,
    modifier: Modifier = Modifier
) {
    AiInsightCard(
        modifier = modifier,
        label = "BIGGEST LEVER",
        icon = {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = null,
                tint = AiGreen,
                modifier = Modifier.size(14.dp)
            )
        },
        headline = buildAnnotatedString { append(insightSection.headline) },
        supportingText = insightSection.supportingDescription,
        footerText = insightSection.actionableInsight
    )
}

@Composable
fun PhysiqueArchetypeCard(
    archetype: String,
    modifier: Modifier = Modifier
) {
    AiInsightCard(
        modifier = modifier,
        label = "PHYSIQUE ARCHETYPE",
        icon = {
            Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = null,
                tint = AiGreen,
                modifier = Modifier.size(14.dp)
            )
        },
        headline = buildAnnotatedString { append(archetype) },
        supportingText = "Your structure reads wide and solid, with the foundation to carry more definition as the waistline tightens.",
        footerText = "Keep building around shoulders, back, and upper chest while trimming gradually."
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(AiSurfaceSoft)
                .border(1.dp, AiCardBorder, RoundedCornerShape(18.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.physique2),
                contentDescription = "$archetype physique archetype",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun EffortScoreCard(
    effortScore: EffortScore,
    modifier: Modifier = Modifier
) {
    AiInsightCard(
        modifier = modifier,
        label = "EFFORT SCORE",
        icon = {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = null,
                tint = AiGreen,
                modifier = Modifier.size(14.dp)
            )
        },
        headline = buildAnnotatedString {},
        supportingText = "",
        footerText = ""
    ) {
        EffortGauge(
            value = effortScore.score.toFloat(),
            modifier = Modifier.padding(vertical = 4.dp)
        )

        HorizontalDivider(
            color = AiCardBorder,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        Text(
            text = effortScore.remark,
            color = AiTextSecondary,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun EffortGauge(
    value: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(34.dp)
        ) {
            val trackHeight = 14.dp.toPx()
            val trackY = size.height / 2f
            val thumbRadius = 10.dp.toPx()
            val startX = thumbRadius + 2.dp.toPx()
            val endX = size.width - thumbRadius - 2.dp.toPx()
            val trackWidth = endX - startX
            val progressX = startX + (value / 100f) * trackWidth

            // Track shadow (subtle depth)
            drawRoundRect(
                color = Color(0x14000000),
                topLeft = Offset(startX, trackY - trackHeight / 2f + 1.5.dp.toPx()),
                size = Size(trackWidth, trackHeight),
                cornerRadius = CornerRadius(trackHeight / 2f, trackHeight / 2f)
            )

            // Track background
            drawRoundRect(
                color = Color(0xFFE4E9EE),
                topLeft = Offset(startX, trackY - trackHeight / 2f),
                size = Size(trackWidth, trackHeight),
                cornerRadius = CornerRadius(trackHeight / 2f, trackHeight / 2f)
            )

            // Progress fill — 5-stop gradient for a rich spectrum
            val progressBrush = Brush.horizontalGradient(
                colorStops = arrayOf(
                    0.00f to Color(0xFFE53935),
                    0.25f to Color(0xFFFF7043),
                    0.50f to Color(0xFFFFA726),
                    0.75f to Color(0xFF66BB6A),
                    1.00f to Color(0xFF2E9E6E)
                ),
                startX = startX,
                endX = endX
            )
            if (progressX > startX) {
                drawRoundRect(
                    brush = progressBrush,
                    topLeft = Offset(startX, trackY - trackHeight / 2f),
                    size = Size(progressX - startX, trackHeight),
                    cornerRadius = CornerRadius(trackHeight / 2f, trackHeight / 2f)
                )
            }

            // Glass highlight — thin bright strip across the top of the bar
            val highlightHeight = 4.dp.toPx()
            val highlightBrush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.45f),
                    Color.White.copy(alpha = 0f)
                ),
                startY = trackY - trackHeight / 2f,
                endY = trackY - trackHeight / 2f + highlightHeight
            )
            drawRoundRect(
                brush = highlightBrush,
                topLeft = Offset(startX, trackY - trackHeight / 2f),
                size = Size(
                    if (progressX > startX) progressX - startX else trackWidth,
                    highlightHeight
                ),
                cornerRadius = CornerRadius(highlightHeight / 2f, highlightHeight / 2f)
            )

            // Thumb — soft shadow
            drawCircle(
                color = Color(0x20000000),
                radius = thumbRadius + 2.dp.toPx(),
                center = Offset(progressX + 0.5.dp.toPx(), trackY + 1.dp.toPx())
            )
            // Thumb — outer glow
            drawCircle(
                color = Color(0xFF2E9E6E).copy(alpha = 0.22f),
                radius = thumbRadius + 3.dp.toPx(),
                center = Offset(progressX, trackY)
            )
            // Thumb — main fill with gradient
            val thumbBrush = Brush.radialGradient(
                colors = listOf(Color(0xFF3AB885), Color(0xFF2E9E6E)),
                center = Offset(progressX, trackY),
                radius = thumbRadius
            )
            drawCircle(
                brush = thumbBrush,
                radius = thumbRadius,
                center = Offset(progressX, trackY)
            )
            // Thumb — inner ring
            drawCircle(
                color = Color.White.copy(alpha = 0.25f),
                radius = thumbRadius - 1.5.dp.toPx(),
                center = Offset(progressX, trackY),
                style = Stroke(width = 1.dp.toPx())
            )
            // Thumb — white center dot
            drawCircle(
                color = Color.White,
                radius = 3.5.dp.toPx(),
                center = Offset(progressX, trackY)
            )
        }

        // Score pill badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(AiGreen.copy(alpha = 0.10f))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${value.toInt()}",
                color = AiGreen,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                style = compactAiTextStyle()
            )
        }
    }
}

@Composable
private fun AiInsightCard(
    label: String,
    icon: @Composable () -> Unit,
    headline: androidx.compose.ui.text.AnnotatedString,
    supportingText: String,
    footerText: String,
    modifier: Modifier = Modifier,
    extraContent: @Composable ColumnScope.() -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.92f)
        ),
        border = BorderStroke(1.dp, AiCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AiGreen.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }

                Text(
                    text = label,
                    color = AiBlue,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    lineHeight = 12.sp,
                    style = compactAiTextStyle()
                )
            }

            if (headline.isNotEmpty()) {
                Text(
                    text = headline,
                    color = AiTextPrimary,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = compactAiTextStyle()
                )
            }

            if (supportingText.isNotEmpty()) {
                Text(
                    text = supportingText,
                    color = AiTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Normal,
                    style = compactAiTextStyle()
                )
            }

            extraContent()

            if (footerText.isNotEmpty()) {
                HorizontalDivider(
                    color = AiCardBorder,
                    thickness = 1.dp
                )

                Text(
                    text = footerText,
                    color = AiTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Normal,
                    style = compactAiTextStyle()
                )
            }
        }
    }
}

@Composable
private fun MomentumTrendGraph(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AiSurfaceSoft)
            .border(1.dp, AiCardBorder, RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GraphLegend(label = "Body Fat", color = AiGreen)
            GraphLegend(label = "Muscle Mass", color = AiBlue)
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(144.dp)
        ) {
            val left = 28.dp.toPx()
            val right = 12.dp.toPx()
            val top = 10.dp.toPx()
            val bottom = 24.dp.toPx()
            val chartWidth = size.width - left - right
            val chartHeight = size.height - top - bottom
            val bottomY = size.height - bottom

            val labelPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.rgb(107, 122, 134)
                textSize = 9.sp.toPx()
                isAntiAlias = true
            }

            repeat(4) { index ->
                val ratio = index / 3f
                val y = top + ratio * chartHeight
                drawLine(
                    color = AiCardBorder,
                    start = Offset(left, y),
                    end = Offset(size.width - right, y),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                )
            }

            val dates = listOf("Apr 20", "Apr 27", "May 4", "May 11", "May 18")
            dates.forEachIndexed { index, date ->
                val x = left + (index / (dates.lastIndex).toFloat()) * chartWidth
                drawIntoCanvas { canvas ->
                    labelPaint.textAlign = android.graphics.Paint.Align.CENTER
                    canvas.nativeCanvas.drawText(date, x, size.height - 4.dp.toPx(), labelPaint)
                }
            }

            val bodyFat = listOf(20.3f, 19.8f, 19.4f, 19.0f, 18.7f)
            val muscleMass = listOf(39.4f, 39.6f, 39.9f, 40.1f, 40.3f)

            fun points(values: List<Float>, min: Float, max: Float): List<Offset> {
                return values.mapIndexed { index, value ->
                    val x = left + (index / values.lastIndex.toFloat()) * chartWidth
                    val y = bottomY - ((value - min) / (max - min)).coerceIn(0f, 1f) * chartHeight
                    Offset(x, y)
                }
            }

            fun smoothPath(points: List<Offset>): Path {
                return Path().apply {
                    if (points.isEmpty()) return@apply
                    moveTo(points.first().x, points.first().y)
                    for (index in 0 until points.lastIndex) {
                        val start = points[index]
                        val end = points[index + 1]
                        val controlDistance = (end.x - start.x) / 2f
                        cubicTo(
                            start.x + controlDistance,
                            start.y,
                            end.x - controlDistance,
                            end.y,
                            end.x,
                            end.y
                        )
                    }
                }
            }

            fun drawTrend(points: List<Offset>, color: Color) {
                drawPath(
                    path = smoothPath(points),
                    color = color,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
                points.forEach { point ->
                    drawCircle(color = Color.White, radius = 5.dp.toPx(), center = point)
                    drawCircle(color = color, radius = 3.5.dp.toPx(), center = point)
                }
            }

            drawTrend(points(bodyFat, 16f, 24f), AiGreen)
            drawTrend(points(muscleMass, 38f, 42f), AiBlue)
        }
    }
}

@Composable
private fun GraphLegend(
    label: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color)
        )
        Text(
            text = label,
            color = AiTextSecondary,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight.SemiBold,
            style = compactAiTextStyle()
        )
    }
}

private fun compactAiTextStyle() = TextStyle(
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)

@Composable
fun AiShimmerPlaceholder() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // BodyOverviewCard skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(24.dp))
                .shimmerEffect()
        )
        
        // 3 Insight Card skeletons
        repeat(3) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
                border = BorderStroke(1.dp, Color(0xFFE6EEF2))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header (Icon + Label)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .shimmerEffect()
                        )
                        Box(
                            modifier = Modifier
                                .size(width = 80.dp, height = 12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .shimmerEffect()
                        )
                    }
                    
                    // Headline
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                    
                    // Supporting text
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .shimmerEffect()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .shimmerEffect()
                        )
                    }
                }
            }
        }
    }
}

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "shimmer")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFF3F6F8),
                Color(0xFFE2E9ED),
                Color(0xFFF3F6F8),
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    ).onGloballyPositioned {
        size = it.size
    }
}
