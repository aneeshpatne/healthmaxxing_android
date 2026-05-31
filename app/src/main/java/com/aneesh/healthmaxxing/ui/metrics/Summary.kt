package com.aneesh.healthmaxxing.ui.metrics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val TextPrimary = Color(0xFF111827)
private val TextSecondary = Color(0xFF667085)
private val BorderSoft = Color(0xFFE5E7EB)
private val SurfaceSoft = Color(0xFFF8FAFC)
private val Blue = Color(0xFF3B82F6)
private val Purple = Color(0xFF7C3AED)
private val Cyan = Color(0xFF06B6D4)
private val Green = Color(0xFF22C55E)
private val Orange = Color(0xFFF59E0B)
private val Success = Color(0xFF16A34A)

private val segments = listOf(
    SummarySegment("Lean Mass", "27.5%", Blue, 27.5f),
    SummarySegment("Protein", "6.3%", Purple, 6.3f),
    SummarySegment("Hydration", "53.2%", Cyan, 53.2f),
    SummarySegment("Muscle Mass", "38.7%", Green, 38.7f),
    SummarySegment("Body Fat", "24.3%", Orange, 24.3f)
)

@Composable
fun Summary(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(horizontal = 2.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SummaryHeader()
        BodyCompositionPanel()
        ProgressMessage()
    }
}

@Composable
private fun SummaryHeader() {
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
                text = "May 12, 2024 • 8:15 AM",
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
private fun BodyCompositionPanel() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = .82f),
        border = BorderStroke(1.dp, BorderSoft),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val chartSize = maxWidth.coerceAtMost(292.dp)
                DonutChart(chartSize = chartSize)
            }
        }
    }
}

@Composable
private fun DonutChart(chartSize: Dp) {
    val textMeasurer = rememberTextMeasurer()
    Box(
        modifier = Modifier.size(chartSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(chartSize)) {
            val donutRadius = this.size.minDimension * 0.20f
            val strokeWidth = donutRadius * 0.28f
            val arcSize = Size(donutRadius * 2, donutRadius * 2)
            val topLeft = Offset(center.x - donutRadius, center.y - donutRadius)
            val total = segments.sumOf { it.amount.toDouble() }.toFloat()
            var startAngle = -90f

            val R_outer = donutRadius + strokeWidth / 2f
            val R_start = R_outer + 4.dp.toPx()

            drawArc(
                color = Color(0xFFEFF2F6),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )

            segments.forEach { segment ->
                val sweep = (segment.amount / total) * 360f
                drawArc(
                    brush = Brush.linearGradient(
                        colors = listOf(segment.color.copy(alpha = .72f), segment.color),
                        start = topLeft,
                        end = Offset(this.size.width, this.size.height)
                    ),
                    startAngle = startAngle,
                    sweepAngle = sweep - 1.4f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )

                val midAngle = startAngle + sweep / 2f
                val angleRad = midAngle * (PI.toFloat() / 180f)
                val cosA = cos(angleRad)
                val sinA = sin(angleRad)

                val isRight = cosA >= 0

                val startPoint = Offset(
                    center.x + cosA * R_start,
                    center.y + sinA * R_start
                )

                val textAnchorX = if (isRight) {
                    center.x + donutRadius + 18.dp.toPx()
                } else {
                    center.x - donutRadius - 18.dp.toPx()
                }
                
                // End Y matches startPoint.y to keep the line perfectly horizontal
                val textAnchor = Offset(textAnchorX, startPoint.y)

                // Draw straight, perfectly horizontal pointer line
                drawLine(
                    color = segment.color.copy(alpha = 0.5f),
                    start = startPoint,
                    end = textAnchor,
                    strokeWidth = 1.2.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Draw indicator dot
                drawCircle(
                    color = segment.color,
                    radius = 3.dp.toPx(),
                    center = startPoint
                )

                // Render labels text next to the connector lines
                val labelText = buildAnnotatedString {
                    withStyle(SpanStyle(color = segment.color, fontWeight = FontWeight.Bold, fontSize = 12.sp)) {
                        append(segment.value)
                    }
                    append("\n")
                    withStyle(SpanStyle(color = TextSecondary, fontWeight = FontWeight.SemiBold, fontSize = 10.sp)) {
                        append(segment.label)
                    }
                }

                val textLayoutResult = textMeasurer.measure(
                    text = labelText,
                    style = TextStyle(
                        lineHeight = 13.sp,
                        textAlign = if (isRight) TextAlign.Start else TextAlign.End,
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    )
                )

                val textWidth = textLayoutResult.size.width
                val textHeight = textLayoutResult.size.height
                val textX = if (isRight) {
                    textAnchorX + 5.dp.toPx()
                } else {
                    textAnchorX - 5.dp.toPx() - textWidth
                }
                val textY = startPoint.y - textHeight / 2f

                drawText(
                    textMeasurer = textMeasurer,
                    text = labelText,
                    topLeft = Offset(textX, textY),
                    style = TextStyle(
                        lineHeight = 13.sp,
                        textAlign = if (isRight) TextAlign.Start else TextAlign.End,
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    )
                )

                startAngle += sweep
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "Body Score",
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                style = compactTextStyle()
            )
            Text(
                text = "86",
                color = TextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 36.sp,
                style = compactTextStyle()
            )
            Text(
                text = "Balanced",
                color = Success,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 14.sp,
                style = compactTextStyle()
            )
        }
    }
}



@Composable
private fun ProgressMessage() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color.White.copy(alpha = .78f),
        border = BorderStroke(1.dp, BorderSoft),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEFF6FF)),
                contentAlignment = Alignment.Center
            ) {
                TrendLineIcon(
                    modifier = Modifier.size(24.dp),
                    color = Blue
                )
            }
            Spacer(modifier = Modifier.width(13.dp))
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
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
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    style = compactTextStyle()
                )
                Text(
                    text = "Keep up the great work.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                    style = compactTextStyle()
                )
            }
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

private fun pointOnCircle(
    center: Offset,
    radius: Float,
    angleDegrees: Float
): Offset {
    val angle = angleDegrees * (PI.toFloat() / 180f)
    return Offset(
        x = center.x + cos(angle) * radius,
        y = center.y + sin(angle) * radius
    )
}

private data class SummarySegment(
    val label: String,
    val value: String,
    val color: Color,
    val amount: Float
)

private fun compactTextStyle() = TextStyle(
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)
