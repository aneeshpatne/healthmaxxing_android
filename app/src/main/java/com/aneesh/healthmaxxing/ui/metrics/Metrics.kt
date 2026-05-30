package com.aneesh.healthmaxxing.ui.metrics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SurfaceWhite = Color(0xFFFFFFFF)
private val Ink = Color(0xFF172A35)
private val MutedInk = Color(0xFF6B7A86)
private val SoftInk = Color(0xFF8A98A5)
private val Teal = Color(0xFF087E8B)
private val Indigo = Color(0xFF4354B8)
private val PositiveAccent = Color(0xFF34A77B)
private val ChartTeal = Color(0xFF0D8A8A)
private val ChartIndigo = Color(0xFF4354B8)
private val ActiveFilterBlue = Color(0xFF4433FF)
private val ActiveFilterFill = Color(0xFFEBF8FF)
private val AxisGray = Color(0xFFA0AEC0)
private val CardStroke = Color(0xFFE6EEF2)

@Composable
fun MetricsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        DashboardHeader()
        WeightTrendCard()
        MetricCardsRow()
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun DashboardHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Composition Details",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.sp,
            color = Ink
        )
    }
}

@Composable
private fun WeightTrendCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardStroke, RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weight Trend",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Ink
                )
//                TrendFilterControl()
            }
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(194.dp)
            ) {
                YAxisLabels(modifier = Modifier.width(24.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    SmoothAreaTrendChart(modifier = Modifier.matchParentSize())
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 4.dp)
                            .offset(x = 9.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier
                                .background(ActiveFilterBlue, RoundedCornerShape(10.dp))
                                .padding(horizontal = 14.dp, vertical = 7.dp),
                            text = "83",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Canvas(modifier = Modifier.size(width = 10.dp, height = 6.dp)) {
                            val caret = Path().apply {
                                moveTo(size.width / 2f, size.height)
                                lineTo(0f, 0f)
                                lineTo(size.width, 0f)
                                close()
                            }
                            drawPath(caret, ActiveFilterBlue)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 34.dp, end = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("1 May", "15 May", "29 May", "12 Jun", "26 Jun").forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Normal,
                        color = AxisGray
                    )
                }
            }
        }
    }
}

@Composable
private fun TrendFilterControl() {
    Row(
        modifier = Modifier
            .border(1.dp, Color(0xFFE2E8F0), CircleShape)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf("1M", "3M", "6M", "1Y", "All").forEachIndexed { index, label ->
            val selected = index == 0
            Text(
                modifier = Modifier
                    .background(
                        color = if (selected) ActiveFilterFill else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (selected) ActiveFilterBlue else MutedInk
            )
        }
    }
}

@Composable
private fun YAxisLabels(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        listOf("84", "82", "80", "78", "76").forEach { label ->
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Normal,
                color = AxisGray.copy(alpha = .74f)
            )
        }
    }
}

@Composable
private fun MetricCardsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MetricCard(
            modifier = Modifier.weight(1f),
            title = "Weight",
            value = "78",
            valueColor = Teal,
            descriptor = "Stable",
            trend = "\u2191 5 pts"
        )
        MetricCard(
            modifier = Modifier.weight(1f),
            title = "Body Age",
            value = "28",
            valueColor = Indigo,
            descriptor = "Older",
            trend = "\u2193 2 years"
        )
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier,
    title: String,
    value: String,
    valueColor: Color,
    descriptor: String,
    trend: String
) {
    Box(
        modifier = modifier
            .height(184.dp)
            .border(1.dp, CardStroke, RoundedCornerShape(24.dp))
    ) {
        MetricComparisonColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 18.dp),
            title = title,
            value = value,
            valueColor = valueColor,
            descriptor = descriptor,
            trend = trend
        )
    }
}

@Composable
private fun DashboardCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.shadow(
            elevation = 12.dp,
            shape = RoundedCornerShape(24.dp),
            ambientColor = Color(0x140B2530),
            spotColor = Color(0x1A0B2530)
        ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = CardDefaults.outlinedCardBorder().copy(
            width = 1.dp,
            brush = SolidColor(CardStroke)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        content()
    }
}

@Composable
private fun SmoothAreaTrendChart(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val top = 24.dp.toPx()
        val bottom = size.height - 18.dp.toPx()
        val height = bottom - top
        val dashedGrid = PathEffect.dashPathEffect(floatArrayOf(7.dp.toPx(), 7.dp.toPx()))
        val left = 10.dp.toPx()
        val right = size.width - 14.dp.toPx()
        val chartWidth = right - left
        val y84 = top
        val y82 = top + height * .25f
        val y80 = top + height * .50f
        val y78 = top + height * .75f
        val y76 = bottom
        val points = listOf(
            Offset(left, y78),
            Offset(left + chartWidth * .24f, y78),
            Offset(left + chartWidth * .48f, top + height * .50f),
            Offset(left + chartWidth * .72f, top + height * .30f),
            Offset(right, top + height * .12f)
        )

        val linePath = Path().apply {
            moveTo(points[0].x, points[0].y)
            lineTo(points[1].x, points[1].y)
            cubicTo(
                left + chartWidth * .32f, y78,
                left + chartWidth * .40f, top + height * .52f,
                points[2].x, points[2].y
            )
            cubicTo(
                left + chartWidth * .58f, top + height * .48f,
                left + chartWidth * .65f, top + height * .34f,
                points[3].x, points[3].y
            )
            cubicTo(
                left + chartWidth * .82f, top + height * .30f,
                left + chartWidth * .92f, top + height * .12f,
                points[4].x, points[4].y
            )
        }

        listOf(y84, y82, y80, y78).forEach { y ->
            drawLine(
                color = AxisGray.copy(alpha = .22f),
                start = Offset(left, y),
                end = Offset(right, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = dashedGrid
            )
        }
        drawLine(
            color = Color(0xFF4A5568),
            start = Offset(left, y76),
            end = Offset(right, y76),
            strokeWidth = 1.dp.toPx()
        )

        val areaPath = Path().apply {
            addPath(linePath)
            lineTo(right, y76)
            lineTo(left, y76)
            close()
        }

        drawPath(
            path = areaPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    ActiveFilterBlue.copy(alpha = .12f),
                    ActiveFilterBlue.copy(alpha = 0f)
                ),
                startY = top,
                endY = y76
            )
        )
        drawPath(
            path = linePath,
            color = ActiveFilterBlue,
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
        points.dropLast(1).forEach { point ->
            drawCircle(
                color = ActiveFilterBlue,
                radius = 3.dp.toPx(),
                center = point
            )
        }
        drawCircle(
            color = ActiveFilterBlue,
            radius = 5.dp.toPx(),
            center = points.last()
        )
        drawCircle(color = Color.White, radius = 3.dp.toPx(), center = points.last())
    }
}

@Composable
private fun MetricComparisonColumn(
    modifier: Modifier,
    title: String,
    value: String,
    valueColor: Color,
    descriptor: String,
    trend: String
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = MutedInk
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontSize = 54.sp,
                lineHeight = 56.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.sp,
                color = valueColor
            )
            Text(
                text = descriptor,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = Ink
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                text = trend,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = PositiveAccent
            )
            Text(
                text = "vs last scan",
                style = MaterialTheme.typography.labelMedium,
                color = SoftInk
            )
        }
    }
}
