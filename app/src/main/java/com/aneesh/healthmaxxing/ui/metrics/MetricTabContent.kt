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

import com.aneesh.healthmaxxing.data.remote.InsightsResponse
import com.aneesh.healthmaxxing.data.remote.PerformanceResponse
import com.aneesh.healthmaxxing.data.remote.ProfileEssentialsResponse
import com.aneesh.healthmaxxing.data.remote.TrendPoint

@Composable
fun MetricTabContent(
    selectedTab: Int,
    insightsResponse: InsightsResponse?,
    isLoading: Boolean,
    error: String?,
    momentumTrends: Map<String, List<TrendPoint>> = emptyMap(),
    momentumTrendsLoading: Boolean = false,
    essentialsResponse: ProfileEssentialsResponse? = null,
    essentialsLoading: Boolean = false,
    essentialsError: String? = null,
    performanceResponse: PerformanceResponse? = null,
    performanceLoading: Boolean = false,
    performanceError: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        when (selectedTab) {
            0 -> {
                // Summary Page (existing content)
                if (isLoading && insightsResponse == null) {
                    AiShimmerPlaceholder()
                } else if (error != null && insightsResponse == null) {
                    Text(
                        text = error,
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        textAlign = TextAlign.Center
                    )
                } else if (insightsResponse != null) {
                    AI(
                        insightsResponse = insightsResponse,
                        momentumTrends = momentumTrends,
                        momentumTrendsLoading = momentumTrendsLoading
                    )
                } else {
                    Text(
                        text = "No insights available.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            1 -> {
                Summary(
                    essentialsResponse = essentialsResponse,
                    isLoading = essentialsLoading,
                    error = essentialsError
                )
            }

            2 -> {
                Performance(
                    performanceResponse = performanceResponse,
                    isLoading = performanceLoading,
                    error = performanceError
                )
            }

            3 -> {
                Fat()
            }

            4 -> {
                Muscle()
            }

            5 -> {
                MetricDetailPlaceholder(
                    title = "Lean Mass Status",
                    description = "View your protein levels to ensure proper nutrition, muscle repair, and recovery support."
                )
            }

            6 -> {
                MetricDetailPlaceholder(
                    title = "Protein Level",
                    description = "Keep track of total body water percentage to maintain peak cellular performance and energy."
                )

            }

            7 -> {
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

