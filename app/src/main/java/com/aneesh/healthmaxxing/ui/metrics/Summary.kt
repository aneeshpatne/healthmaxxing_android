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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
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

            CompositionGrid()
        }
    }
}

@Composable
private fun DonutChart(chartSize: Dp) {
    Box(
        modifier = Modifier.size(chartSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(chartSize)) {
            val strokeWidth = this.size.minDimension * .16f
            val padding = strokeWidth / 2f
            val arcSize = Size(this.size.width - padding * 2f, this.size.height - padding * 2f)
            val topLeft = Offset(padding, padding)
            val total = segments.sumOf { it.amount.toDouble() }.toFloat()
            var startAngle = -90f

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

                val markerAngle = startAngle + sweep / 2f
                val radius = (this.size.minDimension - strokeWidth) / 2f
                val markerCenter = pointOnCircle(center, radius, markerAngle)
                drawCircle(
                    color = Color.White,
                    radius = strokeWidth * .18f,
                    center = markerCenter
                )
                drawCircle(
                    color = segment.color,
                    radius = strokeWidth * .11f,
                    center = markerCenter
                )

                startAngle += sweep
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "Body Score",
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                style = compactTextStyle()
            )
            Text(
                text = "86",
                color = TextPrimary,
                fontSize = 46.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 50.sp,
                style = compactTextStyle()
            )
            Text(
                text = "Balanced",
                color = Success,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 16.sp,
                style = compactTextStyle()
            )
        }
    }
}

@Composable
private fun CompositionGrid() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        segments.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { segment ->
                    CompositionMetric(
                        segment = segment,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CompositionMetric(
    segment: SummarySegment,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceSoft)
            .border(1.dp, BorderSoft, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(segment.color)
        )
        Spacer(modifier = Modifier.width(9.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = segment.label,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                maxLines = 1,
                style = compactTextStyle()
            )
            Text(
                text = segment.value,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 20.sp,
                maxLines = 1,
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
