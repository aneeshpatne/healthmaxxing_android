package com.aneesh.healthmaxxing.ui.metrics

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MetricTabContent(
    selectedTab: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        when (selectedTab) {
            0 -> {
                // Summary Page (existing content)
                Summary()
            }

            1 -> {
                MetricDetailPlaceholder(
                    title = "Fat Composition",
                    description = "Track body fat percentage, visceral fat level, and subcutaneous fat distribution over time."
                )
            }

            2 -> {
                MetricDetailPlaceholder(
                    title = "Muscle Analysis",
                    description = "Monitor skeletal muscle mass, growth trends, and balance across body segments."
                )
            }

            3 -> {
                MetricDetailPlaceholder(
                    title = "Lean Mass",
                    description = "Evaluate lean body mass changes to ensure healthy weight progression and fitness efficiency."
                )
            }

            4 -> {
                MetricDetailPlaceholder(
                    title = "Protein Status",
                    description = "View your protein levels to ensure proper nutrition, muscle repair, and recovery support."
                )
            }

            5 -> {
                MetricDetailPlaceholder(
                    title = "Hydration Level",
                    description = "Keep track of total body water percentage to maintain peak cellular performance and energy."
                )
            }
        }
    }
}

@Composable
private fun MetricDetailPlaceholder(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE6EEF2), RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF172A35),
                textAlign = TextAlign.Center
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF6B7A86),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}
