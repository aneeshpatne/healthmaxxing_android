package com.aneesh.healthmaxxing.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aneesh.healthmaxxing.R

@Composable
fun LoginPage2() {
    val formaTeal = Color(0xFF008284)
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        FormaHeader()
        Spacer(modifier = Modifier.height(78.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Let's get to know you",
                    fontSize = 30.sp,
                    fontFamily = FontFamily(Font(R.font.cormorant_garamond_variablefont_wght)),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Enter email to create your account",
                    modifier = Modifier.widthIn(max = 200.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                FormaTextInput(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    icon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = formaTeal.copy(alpha = 0.14f),
                contentColor = formaTeal
            )
        ) {
            Text(
                text = "Continue",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.sp
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(18.dp)
            )
        }
    }
}

@Composable
fun FormaTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val formaTeal = Color(0xFF1BA7A7)
    val shape = RoundedCornerShape(20.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color.White, shape)
            .border(
                width = 1.dp,
                color = if (isFocused) formaTeal else Color(0xFFE5E7EB),
                shape = shape
            ),
        textStyle = TextStyle(
            color = Color(0xFF111827),
            fontSize = 16.sp,
            lineHeight = 20.sp
        ),
        cursorBrush = SolidColor(formaTeal),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        interactionSource = interactionSource,
        singleLine = true,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = formaTeal,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = label,
                            color = Color.Gray,
                            fontSize = 16.sp,
                            lineHeight = 20.sp
                        )
                    }
                    innerTextField()
                }
            }
        }
    )
}
