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
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.MyLocation
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aneesh.healthmaxxing.R
import kotlinx.coroutines.delay
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
private val MuscleMassBottomPadding = 80.dp
private val LeanMassTopPadding = 30.dp
private val ProteinBottomPadding = 32.dp
private val HydrationBottomPadding = 44.dp
private val ChartLabelWidth = 100.dp
private val ChartLabelHorizontalPadding = 8.dp
private val MuscleMassElbowOffsetX = 20.dp

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
        BodyOverviewCard()

        BodyCompositionPanel()
        BodyMeasurementsPanel()
        WeightDashboard()
//        Stats()
    }
}

@Composable
private fun CustomizedCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.82f)
        ),
        border = BorderStroke(1.dp, BorderSoft),
        content = content
    )
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
private fun BodyOverviewCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.88f)
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
                    text = "You’re making excellent progress.",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Serif,
                    style = compactTextStyle()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your body composition has improved significantly over the past 8 weeks.",
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
private fun Stats() {
    Column() {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            maxItemsInEachRow = 2,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomizedCard(
                modifier = Modifier
                    .weight(1f)
                    .height(180.dp)
            ) { Text("Card1") }
            CustomizedCard(
                modifier = Modifier
                    .weight(1f)
                    .height(180.dp)
            ) { Text("Card1") }
            CustomizedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) { Text("Card1") }
        }
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

@Composable
private fun BodyMeasurementsPanel(
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

                    val markerColor = Color(0xFF3B82F6) // soft blue marker accent
                    val lineColor = Color(0xFF93C5FD).copy(alpha = 0.5f) // soft blue dotted lines

                    fun drawMarkerAndLine(
                        vx: Float,
                        vy: Float,
                        toRight: Boolean
                    ) {
                        val markerPos = getCanvasCoords(vx, vy)
                        val endX = if (toRight) rightLineEndX else leftLineEndX
                        val lineEnd = Offset(endX, markerPos.y)
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
                    drawMarkerAndLine(62.0f, 22.5f, toRight = true)

                    // Chest: Left side
                    drawMarkerAndLine(50.0f, 28.0f, toRight = false)

                    // Bicep: Right side
                    drawMarkerAndLine(63.5f, 34.0f, toRight = true)

                    // Waist: Left side
                    drawMarkerAndLine(50.0f, 42.0f, toRight = false)

                    // Thighs: Right side
                    drawMarkerAndLine(56.5f, 58.0f, toRight = true)

                    // Calf: Left side
                    drawMarkerAndLine(43.0f, 79.0f, toRight = false)
                }

                // 4. Position labels at side ends
                val scale = bodySizePx / 100f

                // Right side labels
                MeasurementLabel(
                    label = "Shoulder",
                    value = "114.6",
                    unit = "cm",
                    isLeft = false,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 10.dp)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = (topPx + 22.5f * scale - 15.dp.toPx()).toInt()
                            )
                        }
                )

                MeasurementLabel(
                    label = "Bicep",
                    value = "33.2",
                    unit = "cm",
                    isLeft = false,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 10.dp)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = (topPx + 34.0f * scale - 15.dp.toPx()).toInt()
                            )
                        }
                )

                MeasurementLabel(
                    label = "Thighs",
                    value = "58.3",
                    unit = "cm",
                    isLeft = false,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 10.dp)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = (topPx + 58.0f * scale - 15.dp.toPx()).toInt()
                            )
                        }
                )

                // Left side labels
                MeasurementLabel(
                    label = "Neck",
                    value = "38.2",
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
                    value = "102.6",
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
                    value = "82.1",
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
                    value = "38.5",
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
                    value = "1.40",
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
                    value = "1.25",
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
                    value = "0.40",
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
                    value = "0.71",
                    unit = "",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Body Type Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFFEFF6FF), Color(0xFFF0FDFA))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFFDBEAFE),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "BODY TYPE",
                        color = Color(0xFF3B82F6),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        style = compactTextStyle()
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Athletic V-Taper",
                        color = Color(0xFF0F172A),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = compactTextStyle()
                    )
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFF3B82F6).copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "V-SHAPE",
                        color = Color(0xFF3B82F6),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        style = compactTextStyle()
                    )
                }
            }
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
        color = Color.White.copy(alpha = 0.82f),
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
            WeightTrendChart(weights, unit)
            BottomMetricCards(averageWeight, lowestWeight, goalWeight, weights, unit)
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
                color = Color(0xFF64748B),
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
                    color = Color(0xFF0F172A)
                ),
                modifier = Modifier.alignByBaseline()
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = unit,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color(0xFF475569),
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier.alignByBaseline()
            )
        }
        Text(
            text = dateLabel,
            style = TextStyle(
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )
        )
    }
}

@Composable
private fun WeightTrendChart(
    weights: List<Float>,
    unit: String,
    modifier: Modifier = Modifier
) {
    val density = androidx.compose.ui.platform.LocalDensity.current

    val minY = 72f
    val maxY = 82f

    val yAxisLabels = listOf("82", "80", "78", "76", "74", "72")
    val xAxisLabels = listOf("Apr 21", "Apr 28", "May 5", "May 12", "May 19")

    val textPaint = remember(density) {
        android.graphics.Paint().apply {
            color = Color(0xFF64748B).toArgb()
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
            color = Color(0xFF64748B).toArgb()
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

        // 1. Gridlines and y-axis labels
        yAxisLabels.forEach { labelStr ->
            val value = labelStr.toFloatOrNull() ?: 72f
            val normalizedY = (value - minY) / (maxY - minY)
            val y = plotBottom - normalizedY * plotHeight

            drawLine(
                color = Color(0xFFE5E7EB),
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

        // 2. Area fill under the line
        if (weights.isNotEmpty()) {
            val path = androidx.compose.ui.graphics.Path()
            val firstOffset = pointToOffset(0, weights[0])
            path.moveTo(firstOffset.x, plotBottom)
            path.lineTo(firstOffset.x, firstOffset.y)
            for (i in 1..weights.lastIndex) {
                val offset = pointToOffset(i, weights[i])
                path.lineTo(offset.x, offset.y)
            }
            val lastOffset = pointToOffset(weights.lastIndex, weights.last())
            path.lineTo(lastOffset.x, plotBottom)
            path.close()

            drawPath(
                path = path,
                color = Color(0xFF3B82F6).copy(alpha = 0.12f)
            )
        }

        // 3. Trend line
        if (weights.size > 1) {
            val linePath = androidx.compose.ui.graphics.Path()
            val firstOffset = pointToOffset(0, weights[0])
            linePath.moveTo(firstOffset.x, firstOffset.y)
            for (i in 1..weights.lastIndex) {
                val offset = pointToOffset(i, weights[i])
                linePath.lineTo(offset.x, offset.y)
            }
            drawPath(
                path = linePath,
                color = Color(0xFF3B82F6),
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }

        // 4. Data point circles
        val radiusPx = 2.5.dp.toPx()
        weights.forEachIndexed { i, value ->
            val offset = pointToOffset(i, value)
            drawCircle(
                color = Color(0xFF3B82F6),
                radius = radiusPx,
                center = offset
            )
        }

        // 5. Final vertical guide line
        if (weights.isNotEmpty()) {
            val finalOffset = pointToOffset(weights.lastIndex, weights.last())
            drawLine(
                color = Color(0xFFE5E7EB),
                start = Offset(finalOffset.x, plotTop),
                end = Offset(finalOffset.x, plotBottom),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )

            // 6. Final highlighted point
            drawCircle(
                color = Color(0xFF3B82F6),
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
                color = Color(0xFF3B82F6),
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
private fun BottomMetricCards(
    averageWeight: Float,
    lowestWeight: Float,
    goalWeight: Float,
    weights: List<Float>,
    unit: String,
    modifier: Modifier = Modifier
) {
    val computedAvg = if (weights.isNotEmpty()) weights.average().toFloat() else averageWeight
    val computedLowest =
        if (weights.isNotEmpty()) (weights.minOrNull() ?: lowestWeight) else lowestWeight
    val computedCurrent = if (weights.isNotEmpty()) weights.last() else averageWeight

    val avgDiff = computedAvg - computedCurrent
    val averageCaption = if (avgDiff >= 0) {
        String.format(java.util.Locale.US, "↓ %.1f %s vs last 30D", avgDiff, unit)
    } else {
        String.format(java.util.Locale.US, "↑ %.1f %s vs last 30D", -avgDiff, unit)
    }

    val goalDiff = computedCurrent - goalWeight
    val goalCaption = if (goalDiff > 0) {
        String.format(java.util.Locale.US, "%.1f %s to go", goalDiff, unit)
    } else {
        "Goal reached!"
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MetricCard(
            icon = Icons.AutoMirrored.Filled.ShowChart,
            label = "Average",
            valueText = String.format(java.util.Locale.US, "%.1f", computedAvg),
            unit = unit,
            caption = averageCaption,
            captionColor = Color(0xFF3B82F6),
            modifier = Modifier
                .weight(1f)
                .height(124.dp)
        )
        MetricCard(
            icon = Icons.Default.ArrowDownward,
            label = "Lowest",
            valueText = String.format(java.util.Locale.US, "%.1f", computedLowest),
            unit = unit,
            caption = "May 18, 2024",
            captionColor = Color(0xFF64748B),
            modifier = Modifier
                .weight(1f)
                .height(124.dp)
        )
        MetricCard(
            icon = Icons.Default.MyLocation,
            label = "Goal",
            valueText = String.format(java.util.Locale.US, "%.1f", goalWeight),
            unit = unit,
            caption = goalCaption,
            captionColor = Color(0xFF3B82F6),
            modifier = Modifier
                .weight(1f)
                .height(124.dp)
        )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(Color(0xFFEFF6FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF3B82F6),
                    modifier = Modifier.size(16.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                Text(
                    text = label,
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium
                    )
                )
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = valueText,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        ),
                        modifier = Modifier.alignByBaseline()
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = unit,
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        ),
                        modifier = Modifier.alignByBaseline()
                    )
                }
            }

            Text(
                text = caption,
                style = TextStyle(
                    fontSize = 9.5.sp,
                    lineHeight = 12.sp,
                    color = captionColor,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 2
            )
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

