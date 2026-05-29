package com.aneesh.healthmaxxing.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aneesh.healthmaxxing.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage4() {
    val formaTeal = Color(0xFF008284)
    var name by rememberSaveable { mutableStateOf("") }
    var heightCm by rememberSaveable { mutableStateOf(170) }
    var dateOfBirth by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        FormaHeader()
        Spacer(modifier = Modifier.height(40.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Tell us a bit about yourself",
                fontSize = 30.sp,
                fontFamily = FontFamily(Font(R.font.cormorant_garamond_variablefont_wght)),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            FormaTextInput(
                value = name,
                onValueChange = { name = it },
                label = "Name",
                icon = Icons.Default.Person
            )
            HeightStepper(
                heightCm = heightCm,
                onHeightChange = { heightCm = it }
            )
            DateOfBirthPicker(
                value = dateOfBirth,
                onValueChange = { dateOfBirth = it }
            )
            GenderDropdown(
                value = gender,
                onValueChange = { gender = it }
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { },
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
                text = "Start your fitness journey",
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
private fun GenderDropdown(
    value: String,
    onValueChange: (String) -> Unit
) {
    val formaTeal = Color(0xFF1BA7A7)
    val shape = RoundedCornerShape(20.dp)
    var expanded by rememberSaveable { mutableStateOf(false) }
    val borderColor = if (expanded) formaTeal else Color(0xFFE5E7EB)

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.White, shape)
                .border(1.dp, borderColor, shape)
                .clickable { expanded = true }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = formaTeal,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.size(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Gender",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                )
                Text(
                    text = value.ifEmpty { "Select" },
                    color = if (value.isEmpty()) Color.Gray else Color(0xFF111827),
                    fontSize = 16.sp,
                    fontWeight = if (value.isEmpty()) FontWeight.Normal else FontWeight.SemiBold,
                    lineHeight = 20.sp
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = formaTeal
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            DropdownMenuItem(
                text = { Text("Male") },
                onClick = {
                    onValueChange("Male")
                    expanded = false
                },
                colors = MenuDefaults.itemColors(textColor = Color(0xFF111827))
            )
            DropdownMenuItem(
                text = { Text("Female") },
                onClick = {
                    onValueChange("Female")
                    expanded = false
                },
                colors = MenuDefaults.itemColors(textColor = Color(0xFF111827))
            )
        }
    }
}

@Composable
private fun HeightStepper(
    heightCm: Int,
    onHeightChange: (Int) -> Unit
) {
    val formaTeal = Color(0xFF1BA7A7)
    val shape = RoundedCornerShape(20.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color.White, shape)
            .border(1.dp, Color(0xFFE5E7EB), shape)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onHeightChange((heightCm - 1).coerceAtLeast(80)) }) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease height",
                tint = formaTeal
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Height (cm)",
                color = Color.Gray,
                fontSize = 13.sp,
                lineHeight = 16.sp
            )
            Text(
                text = heightCm.toString(),
                color = Color(0xFF111827),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 22.sp
            )
        }
        IconButton(onClick = { onHeightChange((heightCm + 1).coerceAtMost(250)) }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase height",
                tint = formaTeal
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateOfBirthPicker(
    value: String,
    onValueChange: (String) -> Unit
) {
    val formaTeal = Color(0xFF1BA7A7)
    val shape = RoundedCornerShape(20.dp)
    var showPicker by rememberSaveable { mutableStateOf(false) }
    val borderColor = if (showPicker) formaTeal else Color(0xFFE5E7EB)
    val datePickerState = androidx.compose.material3.rememberDatePickerState()
    val formatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color.White, shape)
            .border(1.dp, borderColor, shape)
            .clickable { showPicker = true }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = null,
            tint = formaTeal,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.size(12.dp))
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Date of birth",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                )
                Text(
                    text = value.ifEmpty { "Select date" },
                    color = if (value.isEmpty()) Color.Gray else Color(0xFF111827),
                    fontSize = 16.sp,
                    fontWeight = if (value.isEmpty()) FontWeight.Normal else FontWeight.SemiBold,
                    lineHeight = 20.sp
                )
            }
        }
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null,
            tint = formaTeal
        )
    }

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedMillis ->
                            onValueChange(formatter.format(Date(selectedMillis)))
                        }
                        showPicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
