package com.aneesh.healthmaxxing.ui.metrics

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun FatRatioCard(
    modifier: Modifier = Modifier,
    fatRatio: String = "24.3",
    delta: String = "-1.6%",
    comparisonDate: String = "Apr 14"
) {
    Box(
        modifier = modifier
            .width(330.dp)
            .height(185.dp)
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

                HealthyPill()

                Text(
                    text = "Your fat ratio is within the\nhealthy range for your age\nand gender.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(top = 10.dp),
                    style = compactTextStyle()
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
private fun HealthyPill() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(PillBackground)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Success)
        )

        Spacer(Modifier.width(5.dp))

        Text(
            text = "Healthy",
            color = Success,
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
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
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
                .padding(horizontal = 16.dp, vertical = 17.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label.uppercase(),
                    color = Blue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.1.sp,
                    style = compactTextStyle()
                )
                if (showInfoIcon) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Info",
                        tint = TextSecondary.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = valueText,
                        color = TextPrimary,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = compactTextStyle(),
                        modifier = Modifier.alignByBaseline()
                    )
                    if (unit.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = unit,
                            color = TextSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            style = compactTextStyle(),
                            modifier = Modifier.alignByBaseline()
                        )
                    }
                }

                Text(
                    text = caption,
                    color = captionColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    style = compactTextStyle(),
                    modifier = Modifier.padding(top = 4.dp)
                )
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
private fun Stats() {
    var activeSheet by rememberSaveable {
        mutableStateOf<SheetType?>(null)
    }

    BottomSheetScreen(
        activeSheet = activeSheet,
        onDismissRequest = { activeSheet = null }
    )

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MetricStatsCard(
                label = "Fat Mass",
                valueText = "17.4",
                unit = "kg",
                caption = "Healthy range",
                captionColor = Success,
                onClick = { activeSheet = SheetType.FAT_MASS },
                showInfoIcon = true,
                modifier = Modifier
                    .weight(1f)
                    .height(150.dp)
            )

            MetricStatsCard(
                label = "Visceral Fat",
                valueText = "6",
                unit = "",
                caption = "Healthy index",
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
                valueText = "14.2",
                unit = "%",
                caption = "Normal limits",
                captionColor = Success,
                onClick = { activeSheet = SheetType.SUBCUTANEOUS_FAT_RATIO },
                showInfoIcon = true,
                modifier = Modifier
                    .weight(1f)
                    .height(150.dp)
            )

            MetricStatsCard(
                label = "Subcutaneous Mass",
                valueText = "10.7",
                unit = "kg",
                caption = "Healthy range",
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
fun Fat() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        FatRatioCard()
    }
    Stats()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetScreen(
    activeSheet: SheetType?,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
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

            when (activeSheet) {
                SheetType.FAT_MASS -> {
                    title = "FAT MASS INFO"
                    headerText = "Understanding Fat Mass"
                    explanation = "Fat mass represents the actual weight of fat tissue in your body (measured in kg or lbs), whereas fat ratio is the percentage of your total weight that is fat.\n\nMaintaining a healthy amount of fat mass is vital for hormone regulation, joint cushioning, and protecting internal organs."
                }
                SheetType.VISCERAL_FAT -> {
                    title = "VISCERAL FAT INFO"
                    headerText = "Understanding Visceral Fat"
                    explanation = "Visceral fat is the body fat that is stored within the abdominal cavity, surrounding your internal organs (like the liver, pancreas, and kidneys).\n\nUnlike subcutaneous fat, high levels of visceral fat are strongly linked to cardiovascular disease, type 2 diabetes, and other metabolic issues. An index between 1 and 9 is considered healthy."
                }
                SheetType.SUBCUTANEOUS_FAT_RATIO -> {
                    title = "SUBCUTANEOUS FAT RATIO"
                    headerText = "Understanding Subcutaneous Ratio"
                    explanation = "Subcutaneous fat is the visible fat layer located directly beneath your skin. It is the type of fat you can pinch.\n\nWhile subcutaneous fat is less metabolically active and less dangerous than visceral fat, keeping its ratio within healthy bounds supports overall fitness and aesthetic health."
                }
                SheetType.SUBCUTANEOUS_FAT_MASS -> {
                    title = "SUBCUTANEOUS FAT MASS"
                    headerText = "Understanding Subcutaneous Mass"
                    explanation = "Subcutaneous fat mass represents the absolute weight of the fat layer stored directly under your skin (measured in kg or lbs).\n\nMeasuring this mass helps track real changes in physical fat loss and muscle definition, which percentage calculations alone might not fully reflect."
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
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
                        text = explanation,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        style = compactTextStyle()
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
