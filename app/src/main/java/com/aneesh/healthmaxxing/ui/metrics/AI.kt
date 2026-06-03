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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aneesh.healthmaxxing.R

private val AiTextPrimary = Color(0xFF172A35)
private val AiTextSecondary = Color(0xFF6B7A86)
private val AiCardBorder = Color(0xFFE6EEF2)
private val AiBlue = Color(0xFF4354B8)
private val AiGreen = Color(0xFF34A77B)
private val AiSurfaceSoft = Color(0xFFF8FAFC)

@Composable
fun AI() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        BodyOverviewCard()
        HeroInsightCard()
        MomentumInsightCard()
        BiggestLeverInsightCard()
        PhysiqueArchetypeCard()
    }
}

@Composable
fun HeroInsightCard(
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
        headline = buildAnnotatedString {
            append("You've built ")
            withStyle(style = SpanStyle(color = AiGreen)) {
                append("55kg")
            }
            append(" of quality muscle.")
        },
        supportingText = "A sturdy base for body recomposition, recovery, and better training output.",
        footerText = "Consistency is paying off. Keep protein intake steady and progress load gradually."
    )
}

@Composable
fun MomentumInsightCard(
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
        headline = buildAnnotatedString {
            append("Body fat is edging down while ")
            withStyle(style = SpanStyle(color = AiGreen)) {
                append("muscle trends up")
            }
            append(".")
        },
        supportingText = "A quiet but steady recomposition pattern is forming across your recent check-ins.",
        footerText = "Keep the current rhythm: consistent training, steady protein, and measured calorie control."
    ) {
        MomentumTrendGraph()
    }
}

@Composable
fun BiggestLeverInsightCard(
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
        headline = buildAnnotatedString {
            append("Dial in a ")
            withStyle(style = SpanStyle(color = AiGreen)) {
                append("slight calorie deficit")
            }
            append(" with higher protein.")
        },
        supportingText = "That combination will trim the waistline and reveal your shape without compromising the muscle base you've built.",
        footerText = "Aim for small, repeatable adjustments rather than aggressive cuts."
    )
}

@Composable
fun PhysiqueArchetypeCard(
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
        headline = buildAnnotatedString {
            append("Broad ")
            withStyle(style = SpanStyle(color = AiGreen)) {
                append("Strong")
            }
            append(" Frame")
        },
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
                painter = painterResource(id = R.drawable.physique),
                contentDescription = "Broad Strong Frame physique archetype",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
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

            Text(
                text = headline,
                color = AiTextPrimary,
                fontSize = 20.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold,
                style = compactAiTextStyle()
            )

            Text(
                text = supportingText,
                color = AiTextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Normal,
                style = compactAiTextStyle()
            )

            extraContent()

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
