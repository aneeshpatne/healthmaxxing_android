package com.aneesh.healthmaxxing.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aneesh.healthmaxxing.navigation.Destination

@Composable
fun FormaBottomBar(
    selectedDestination: Int,
    onDestinationSelected: (Int, Destination) -> Unit
) {
    val barColor = Color(0xFFFFFFFF)
    val barBorder = Color(0xFFE8EDF2)
    val selectedColor = Color(0xFF26323F)
    val inactiveColor = Color(0xFF9AA3AD)
    val activeIndicatorColor = Color(0xFF2F7DF6)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(barColor)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            color = barColor,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, barBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Destination.entries.forEachIndexed { index, destination ->
                    val selected = selectedDestination == index
                    val indicatorWidth = animateDpAsState(
                        targetValue = if (selected) 26.dp else 0.dp,
                        animationSpec = tween(durationMillis = 180),
                        label = "bottomBarIndicatorWidth"
                    )
                    val indicatorColor = animateColorAsState(
                        targetValue = if (selected) activeIndicatorColor else Color.Transparent,
                        animationSpec = tween(durationMillis = 180),
                        label = "bottomBarIndicatorColor"
                    )
                    val iconColor = animateColorAsState(
                        targetValue = if (selected) selectedColor else inactiveColor,
                        animationSpec = tween(durationMillis = 180),
                        label = "bottomBarIconColor"
                    )
                    val labelColor = animateColorAsState(
                        targetValue = if (selected) selectedColor else inactiveColor,
                        animationSpec = tween(durationMillis = 180),
                        label = "bottomBarLabelColor"
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                onDestinationSelected(index, destination)
                            }
                            .padding(vertical = 3.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .height(27.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                modifier = Modifier.size(21.dp),
                                imageVector = destination.icon,
                                contentDescription = destination.contentDescription,
                                tint = iconColor.value
                            )
                        }

                        Text(
                            text = destination.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                            color = labelColor.value
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Box(
                            modifier = Modifier
                                .height(2.dp)
                                .width(indicatorWidth.value)
                                .clip(RoundedCornerShape(1.dp))
                                .background(indicatorColor.value)
                        )
                    }
                }
            }
        }
    }
}
