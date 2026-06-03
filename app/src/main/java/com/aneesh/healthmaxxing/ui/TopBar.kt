package com.aneesh.healthmaxxing.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aneesh.healthmaxxing.R

@Composable
fun TopBar() {
    val primaryAccent = MaterialTheme.colorScheme.primary
    val brandFont = FontFamily(Font(R.font.cormorant_garamond_variablefont_wght))
    val manropeFont = FontFamily(Font(R.font.manrope_variablefont_wght))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),

        ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {


                    Column {
                        Text(
                            text = "Forma",
                            fontSize = 35.sp,
                            fontFamily = brandFont,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.sp,
                            lineHeight = 35.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Good morning, Aneesh",
                                fontSize = 14.sp,
                                fontFamily = manropeFont,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.58f),
                                letterSpacing = 0.1.sp,
                                lineHeight = 18.sp
                            )
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = primaryAccent.copy(alpha = 0.72f),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                }


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    IconButton(
                        onClick = {},
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = primaryAccent.copy(alpha = 0.12f),
                            contentColor = primaryAccent
                        ),
                        modifier = Modifier
                            .size(42.dp)
                            .background(primaryAccent.copy(alpha = 0.04f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account",
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

        }
    }
}
