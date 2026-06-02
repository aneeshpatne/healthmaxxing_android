package com.aneesh.healthmaxxing.ui.metrics

import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aneesh.healthmaxxing.R
import kotlinx.coroutines.delay

object MuscleSnapshotColors {
    val Background = Color(0xFFFFFFFF)

    val PrimaryText = Color(0xFF111827)
    val SecondaryText = Color(0xFF5F6672)
    val MutedText = Color(0xFF8A9099)

    val AccentBlue = Color(0xFF4A90FF)
    val MuscleBlue = Color(0xFF4F8DFF)
    val MuscleBlueDark = Color(0xFF3C78F0)

    val SuccessGreen = Color(0xFF34C759)
    val SuccessBackground = Color(0xFFEAF8EE)

    val IconGray = Color(0xFFA4ABB5)

    val BodyGray = Color(0xFFE6E8EC)
    val BodyGrayDark = Color(0xFFD5D9DF)
}

private val MuscleCardBorder = Color(0xFFE6EEF2)

object MuscleVsBodyColors {
    val Background = Color(0xFFFFFFFF)

    val MuscleBlue = Color(0xFF3E7BFA)
    val MuscleBlueDark = Color(0xFF2F67E8)

    val RestGreen = Color(0xFF5BB26B)

    val PrimaryText = Color(0xFF1B1F24)
    val SecondaryText = Color(0xFF4E5561)
    val TertiaryText = Color(0xFF7C8591)
}

@Composable
fun Muscle() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MuscleSnapshotCard()
        MuscleVsRestOfBodyCard()
        MuscleStats()
    }
}

@Composable
private fun MuscleStats() {
    var activeSheet by rememberSaveable {
        mutableStateOf<MuscleSheetType?>(null)
    }

    MuscleMetricBottomSheet(
        activeSheet = activeSheet,
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
            MuscleMetricStatsCard(
                label = "Bone Mass",
                valueText = "2.8",
                unit = "kg",
                caption = "Healthy range",
                onClick = { activeSheet = MuscleSheetType.BONE_MASS },
                modifier = Modifier
                    .weight(1f)
                    .height(150.dp)
            )

            MuscleMetricStatsCard(
                label = "Muscle Ratio",
                valueText = "57.3",
                unit = "%",
                caption = "Strong range",
                onClick = { activeSheet = MuscleSheetType.MUSCLE_RATIO },
                modifier = Modifier
                    .weight(1f)
                    .height(150.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MuscleMetricStatsCard(
                label = "Skeleton Muscle Mass",
                valueText = "24.8",
                unit = "kg",
                caption = "Healthy range",
                onClick = { activeSheet = MuscleSheetType.SKELETON_MUSCLE_MASS },
                modifier = Modifier
                    .weight(1f)
                    .height(150.dp)
            )

            MuscleMetricStatsCard(
                label = "Skeleton Muscle Ratio",
                valueText = "33.6",
                unit = "%",
                caption = "Normal range",
                onClick = { activeSheet = MuscleSheetType.SKELETON_MUSCLE_RATIO },
                modifier = Modifier
                    .weight(1f)
                    .height(150.dp)
            )
        }
    }
}

private enum class MuscleSheetType {
    BONE_MASS,
    MUSCLE_RATIO,
    SKELETON_MUSCLE_MASS,
    SKELETON_MUSCLE_RATIO
}

@Composable
private fun MuscleMetricStatsCard(
    label: String,
    valueText: String,
    unit: String,
    caption: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MuscleSnapshotColors.Background
        ),
        border = BorderStroke(1.dp, MuscleCardBorder)
    ) {
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
                Text(
                    text = label,
                    color = MuscleSnapshotColors.SecondaryText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 13.sp,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF8FAFC)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Info",
                        tint = MuscleSnapshotColors.SecondaryText.copy(alpha = 0.68f),
                        modifier = Modifier.size(13.dp)
                    )
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
                        color = MuscleSnapshotColors.PrimaryText,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 32.sp,
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                        modifier = Modifier.alignByBaseline()
                    )
                    if (unit.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = unit,
                            color = MuscleSnapshotColors.SecondaryText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                            modifier = Modifier.alignByBaseline()
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(MuscleSnapshotColors.SuccessGreen.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = caption,
                        color = MuscleSnapshotColors.SuccessGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MuscleMetricBottomSheet(
    activeSheet: MuscleSheetType?,
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
                    color = MuscleCardBorder
                )
            }
        ) {
            val title: String
            val headerText: String
            val explanation: String

            when (activeSheet) {
                MuscleSheetType.BONE_MASS -> {
                    title = "BONE MASS INFO"
                    headerText = "Understanding Bone Mass"
                    explanation = "Bone mass estimates the weight of mineral content in your bones.\n\nIt is a useful body-composition marker because stronger bones support posture, training capacity, and long-term mobility. Changes are usually gradual, so trends matter more than single readings."
                }
                MuscleSheetType.MUSCLE_RATIO -> {
                    title = "MUSCLE RATIO INFO"
                    headerText = "Understanding Muscle Ratio"
                    explanation = "Muscle ratio is the percentage of your total body weight represented by muscle mass.\n\nA higher healthy muscle ratio usually reflects better strength potential, metabolic health, and body composition, especially when tracked alongside body weight and fat ratio."
                }
                MuscleSheetType.SKELETON_MUSCLE_MASS -> {
                    title = "SKELETON MUSCLE MASS"
                    headerText = "Understanding Skeletal Muscle Mass"
                    explanation = "Skeletal muscle mass is the muscle attached to your bones that helps you move, lift, stabilize joints, and maintain posture.\n\nThis metric is especially useful for strength and fitness tracking because it focuses on the muscles most directly affected by resistance training."
                }
                MuscleSheetType.SKELETON_MUSCLE_RATIO -> {
                    title = "SKELETON MUSCLE RATIO"
                    headerText = "Understanding Skeletal Muscle Ratio"
                    explanation = "Skeletal muscle ratio shows skeletal muscle mass as a percentage of total body weight.\n\nIt helps compare muscular development independent of body size, and is best interpreted together with total muscle mass, fat ratio, and recent training trends."
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    color = MuscleSnapshotColors.AccentBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = headerText,
                    color = MuscleSnapshotColors.PrimaryText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = MuscleCardBorder,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = explanation,
                        color = MuscleSnapshotColors.SecondaryText,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun MuscleVsRestOfBodyCard(
    modifier: Modifier = Modifier,
    musclePercentage: Float = 92f,
    restPercentage: Float = 8f
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.04f),
                spotColor = Color.Black.copy(alpha = 0.04f)
        ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MuscleVsBodyColors.Background),
        border = BorderStroke(1.dp, MuscleCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MuscleVsBodyDonutChart(
                musclePercentage = musclePercentage,
                restPercentage = restPercentage
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Muscle vs Rest of Body",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MuscleVsBodyColors.PrimaryText,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )

                Spacer(modifier = Modifier.height(12.dp))

                MuscleVsBodyLegendRow(
                    dotColor = MuscleVsBodyColors.MuscleBlue,
                    label = "Muscle Mass",
                    value = "${musclePercentage.toInt()}%"
                )

                Spacer(modifier = Modifier.height(8.dp))

                MuscleVsBodyLegendRow(
                    dotColor = MuscleVsBodyColors.RestGreen,
                    label = "Rest of Body",
                    value = "${restPercentage.toInt()}%"
                )
            }
        }
    }
}

@Composable
private fun MuscleVsBodyDonutChart(
    musclePercentage: Float,
    restPercentage: Float,
    modifier: Modifier = Modifier
) {
    val total = (musclePercentage + restPercentage).coerceAtLeast(1f)
    val muscleSweep = musclePercentage / total * 360f
    val restSweep = restPercentage / total * 360f

    Box(
        modifier = modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 10.dp.toPx()
            val arcSize = size.copy(
                width = size.width - strokeWidth,
                height = size.height - strokeWidth
            )
            val topLeft = androidx.compose.ui.geometry.Offset(strokeWidth / 2f, strokeWidth / 2f)

            drawArc(
                color = MuscleVsBodyColors.MuscleBlue,
                startAngle = -90f,
                sweepAngle = muscleSweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )
            drawArc(
                color = MuscleVsBodyColors.RestGreen,
                startAngle = -90f + muscleSweep,
                sweepAngle = restSweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${musclePercentage.toInt()}%",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MuscleVsBodyColors.PrimaryText,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
            )
            Text(
                text = "of body weight",
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = MuscleVsBodyColors.TertiaryText,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
            )
        }
    }
}

@Composable
private fun MuscleVsBodyLegendRow(
    dotColor: Color,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(dotColor, RoundedCornerShape(50))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            color = MuscleVsBodyColors.SecondaryText,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MuscleVsBodyColors.PrimaryText,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuscleSnapshotCard(
    modifier: Modifier = Modifier,
    muscleMass: String = "42.3",
    unit: String = "kg",
    delta: String = "+1.8 kg",
    comparisonLabel: String = "vs last scan"
) {
    var showInfoSheet by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.04f),
                spotColor = Color.Black.copy(alpha = 0.04f)
        ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MuscleSnapshotColors.Background),
        border = BorderStroke(1.dp, MuscleCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Section — Muscle Illustration
            Box(
                modifier = Modifier
                    .height(170.dp)
                    .aspectRatio(70f / 150f),
                contentAlignment = Alignment.Center
            ) {
                val image = AnimatedImageVector.animatedVectorResource(R.drawable.muscle_body)
                var atEnd by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(150)
                    atEnd = true
                }
                Image(
                    painter = rememberAnimatedVectorPainter(image, atEnd),
                    contentDescription = "Muscle distribution illustration",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right Section — Metrics Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Header Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Your Muscle Snapshot",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MuscleSnapshotColors.SecondaryText,
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                    )
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Info",
                        tint = MuscleSnapshotColors.IconGray,
                        modifier = Modifier
                            .size(14.dp)
                            .clickable { showInfoSheet = true }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Primary Metric Row (using baseline alignment)
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = muscleMass,
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Bold,
                        color = MuscleSnapshotColors.PrimaryText,
                        modifier = Modifier.alignByBaseline(),
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = unit,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MuscleSnapshotColors.MutedText,
                        modifier = Modifier.alignByBaseline(),
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Metric Label
                Text(
                    text = "Total Muscle Mass",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MuscleSnapshotColors.AccentBlue,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Trend Badge
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = MuscleSnapshotColors.SuccessBackground
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = "Trending Up",
                            tint = MuscleSnapshotColors.SuccessGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "↗ $delta $comparisonLabel",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MuscleSnapshotColors.SuccessGreen,
                            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                        )
                    }
                }
            }
        }
    }

    if (showInfoSheet) {
        MuscleInfoBottomSheet(onDismiss = { showInfoSheet = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuscleInfoBottomSheet(
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = Color(0xFFE6EEF2)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MUSCLE MASS INFO",
                color = MuscleSnapshotColors.AccentBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Understanding Muscle Mass",
                color = MuscleSnapshotColors.PrimaryText,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFFE6EEF2),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = "Total Muscle Mass includes skeletal muscles, smooth muscles, and the water contained in them.\n\nIncreasing muscle mass boosts your metabolic rate, helps burn more calories even at rest, improves strength, and protects joints and bone density as you age.",
                    color = MuscleSnapshotColors.SecondaryText,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
