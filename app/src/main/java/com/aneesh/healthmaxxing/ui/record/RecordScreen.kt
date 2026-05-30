package com.aneesh.healthmaxxing.ui.record

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

private val Ink = Color(0xFF173238)
private val MutedInk = Color(0xFF66767A)
private val Teal = Color(0xFF006D73)
private val DeepTeal = Color(0xFF063C45)
private val BrightTeal = Color(0xFF2FE0D0)
private val PaleTeal = Color(0xFFE7F2F0)
private val CardStroke = Color(0xFFDCE7EA)

@Composable
fun RecordScreen(
    viewModel: RecordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants.values.all { it }) {
            viewModel.startReading()
        } else {
            viewModel.onPermissionDenied()
        }
    }

    val measurement = uiState.measurement
    val weightText = measurement.weightKg?.let { "%.2f".format(it) } ?: "--"
    val heartRateText = measurement.heartRate?.toString() ?: "--"
    val impedanceText = measurement.impedanceOhms?.let { "%.0f".format(it) } ?: "--"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        RecordHeader(status = uiState.status, isReading = uiState.isReading)

        WeightHeroCard(
            weightText = weightText,
            isReading = uiState.isReading
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricChartCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.FavoriteBorder,
                title = "Heart",
                value = heartRateText,
                unit = "bpm",
                points = listOf(.30f, .42f, .34f, .56f, .48f, .66f, .52f, .72f)
            )
            MetricChartCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Speed,
                title = "Impedance",
                value = impedanceText,
                unit = "ohms",
                points = listOf(.64f, .45f, .56f, .38f, .50f, .31f, .43f, .35f)
            )
        }

        uiState.error?.let { error ->
            ErrorNotice(message = error)
        }

        ReadScaleButton(
            isReading = uiState.isReading,
            onReadClick = {
                val permissions = requiredBluetoothPermissions()
                if (permissions.isEmpty()) {
                    viewModel.startReading()
                } else {
                    permissionLauncher.launch(permissions)
                }
            },
            onStopClick = viewModel::stopReading
        )
    }
}

@Composable
private fun RecordHeader(status: String, isReading: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = "Record",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Text(
                text = status,
                style = MaterialTheme.typography.bodyMedium,
                color = MutedInk
            )
        }
        Surface(
            shape = CircleShape,
            color = if (isReading) Color(0xFFFFF1D7) else PaleTeal
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                text = if (isReading) "Live" else "Ready",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (isReading) Color(0xFF8A5400) else Teal
            )
        }
    }
}

@Composable
private fun WeightHeroCard(weightText: String, isReading: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(198.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF075A63), Color(0xFF062B35)),
                        start = Offset.Zero,
                        end = Offset.Infinite
                    )
                )
                .padding(18.dp)
        ) {
            SubtleWaveLines(modifier = Modifier.matchParentSize())

            if (isReading) {
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = .12f)
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                        text = "Reading",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC8FFF8)
                    )
                }
            }

            Column(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconTile(icon = Icons.Outlined.MonitorWeight, dark = true)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "WEIGHT",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF93ECE5)
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = weightText,
                            fontSize = 48.sp,
                            lineHeight = 50.sp,
                            fontWeight = FontWeight.Light,
                            color = Color.White
                        )
                        Text(
                            modifier = Modifier.padding(start = 6.dp, bottom = 7.dp),
                            text = "kg",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White.copy(alpha = .82f)
                        )
                    }
                }
            }

            TrendChart(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun TrendChart(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Canvas(
            modifier = Modifier
                .size(width = 80.dp, height = 40.dp)
        ) {
            val path = Path().apply {
                moveTo(0f, size.height * 0.7f)
                cubicTo(
                    size.width * 0.2f, size.height * 0.3f,
                    size.width * 0.4f, size.height * 0.9f,
                    size.width * 0.6f, size.height * 0.5f
                )
                cubicTo(
                    size.width * 0.75f, size.height * 0.2f,
                    size.width * 0.85f, size.height * 0.7f,
                    size.width * 0.95f, size.height * 0.4f
                )
            }
            drawPath(
                path = path,
                color = BrightTeal,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
            drawCircle(Color.White, radius = 4.dp.toPx(), center = Offset(size.width * 0.95f, size.height * 0.4f))
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "7d",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "trend",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.72f)
            )
        }
    }
}

@Composable
private fun MetricChartCard(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    value: String,
    unit: String,
    points: List<Float>
) {
    Card(
        modifier = modifier.aspectRatio(.88f),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder()
            .copy(width = 1.dp, brush = SolidColor(CardStroke)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(13.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                IconTile(icon = icon, dark = false)
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Teal
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Ink
                    )
                    Text(
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                        text = unit,
                        style = MaterialTheme.typography.labelLarge,
                        color = MutedInk
                    )
                }
            }
            MiniLineChart(
                points = points,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
            )
        }
    }
}

@Composable
private fun MiniLineChart(points: List<Float>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val step = size.width / points.lastIndex.coerceAtLeast(1)
        val verticalPadding = 6.dp.toPx()
        val chartHeight = size.height - verticalPadding * 2
        val path = Path()
        points.forEachIndexed { index, point ->
            val x = step * index
            val y = verticalPadding + chartHeight * (1f - point)
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                val previousX = step * (index - 1)
                val previousY = verticalPadding + chartHeight * (1f - points[index - 1])
                path.cubicTo((previousX + x) / 2f, previousY, (previousX + x) / 2f, y, x, y)
            }
        }
        drawPath(
            path = path,
            color = Teal,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
        val endY = verticalPadding + chartHeight * (1f - points.last())
        drawCircle(BrightTeal, radius = 4.dp.toPx(), center = Offset(size.width, endY))
    }
}

@Composable
private fun IconTile(icon: ImageVector, dark: Boolean) {
    Box(
        modifier = Modifier
            .size(if (dark) 46.dp else 38.dp)
            .clip(RoundedCornerShape(if (dark) 14.dp else 11.dp))
            .background(if (dark) Color.White.copy(alpha = .14f) else PaleTeal),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (dark) Color.White else Teal,
            modifier = Modifier.size(if (dark) 27.dp else 22.dp)
        )
    }
}

@Composable
private fun ErrorNotice(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFECE8), RoundedCornerShape(14.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = null,
            tint = Color(0xFFB3261E),
            modifier = Modifier.size(21.dp)
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF7A271A)
        )
    }
}

@Composable
private fun ReadScaleButton(
    isReading: Boolean,
    onReadClick: () -> Unit,
    onStopClick: () -> Unit
) {
    if (isReading) {
        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            onClick = onStopClick
        ) {
            Icon(imageVector = Icons.Outlined.Stop, contentDescription = null)
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = "Stop reading",
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Teal),
            onClick = onReadClick
        ) {
            Icon(
                imageVector = Icons.Outlined.MonitorWeight,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = "Read from scale",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SubtleWaveLines(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        repeat(7) { index ->
            val baseY = size.height * (.68f + index * .038f)
            val path = Path().apply {
                moveTo(size.width * .32f, baseY)
                cubicTo(
                    size.width * .48f,
                    baseY - 30.dp.toPx(),
                    size.width * .57f,
                    baseY - 64.dp.toPx(),
                    size.width * .72f,
                    baseY - 18.dp.toPx()
                )
                cubicTo(
                    size.width * .86f,
                    baseY + 26.dp.toPx(),
                    size.width * .92f,
                    baseY - 26.dp.toPx(),
                    size.width,
                    baseY - 48.dp.toPx()
                )
            }
            drawPath(
                path = path,
                color = Color.White.copy(alpha = .055f),
                style = Stroke(width = 1.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

private fun requiredBluetoothPermissions(): Array<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        )
    } else {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}
