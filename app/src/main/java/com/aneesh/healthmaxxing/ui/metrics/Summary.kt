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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
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

private val BodyFatTopPadding = 36.dp
private val MuscleMassBottomPadding = 92.dp
private val LeanMassTopPadding = 30.dp
private val ProteinBottomPadding = 32.dp
private val HydrationBottomPadding = 44.dp

private val segments = listOf(
    SummarySegment("Lean Mass", "27.5%", Blue, 27.5f, isLeft = false),
    SummarySegment("Protein", "6.3%", Purple, 6.3f, isLeft = false),
    SummarySegment("Hydration", "53.2%", Cyan, 53.2f, isLeft = false),
    SummarySegment("Muscle Mass", "38.7%", Green, 38.7f, isLeft = true),
    SummarySegment("Body Fat", "24.3%", Orange, 24.3f, isLeft = true)
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val chartSize = maxWidth.coerceAtMost(340.dp)
                DonutChart(chartSize = chartSize)
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
            .width(100.dp)
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
private fun DonutChart(chartSize: Dp) {
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

            val total = segments.sumOf { it.amount.toDouble() }.toFloat()
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

            // Assume standard label height is 32dp for Y center calculation
            val labelHeight = 32.dp.toPx()

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
                val elbowX = centerX + cos(midAngleRad) * extendedRadius

                // Determine target Y based on the label's manual layout position
                val targetY = when (segment.label) {
                    "Body Fat" -> BodyFatTopPadding.toPx() + labelHeight / 2f
                    "Lean Mass" -> LeanMassTopPadding.toPx() + labelHeight / 2f
                    "Protein" -> (height / 2f) - ProteinBottomPadding.toPx()
                    "Hydration" -> height - HydrationBottomPadding.toPx() - labelHeight / 2f
                    "Muscle Mass" -> height - MuscleMassBottomPadding.toPx() - labelHeight / 2f
                    else -> centerY
                }

                // Horizontal end point
                val horizontalLength = 32.dp.toPx()
                val endX =
                    if (segment.isLeft) elbowX - horizontalLength else elbowX + horizontalLength
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

                // Draw end point dot
                drawCircle(
                    color = segment.color,
                    radius = 3.5.dp.toPx(),
                    center = Offset(endX, endY)
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
                text = "84",
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
            value = "24.3%",
            color = Orange,
            alignment = Alignment.TopStart,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = BodyFatTopPadding)
        )

        ChartLabel(
            label = "Muscle Mass",
            value = "38.7%",
            color = Green,
            alignment = Alignment.BottomStart,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 8.dp, bottom = MuscleMassBottomPadding)
        )

        ChartLabel(
            label = "Lean Mass",
            value = "27.5%",
            color = Blue,
            alignment = Alignment.TopEnd,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 8.dp, top = LeanMassTopPadding)
        )

        ChartLabel(
            label = "Protein",
            value = "6.3%",
            color = Purple,
            alignment = Alignment.CenterEnd,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp, bottom = ProteinBottomPadding)
        )

        ChartLabel(
            label = "Hydration",
            value = "53.2%",
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
