package com.aneesh.healthmaxxing

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aneesh.healthmaxxing.ui.HomeScreen
import com.aneesh.healthmaxxing.ui.theme.HealthMaxxingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthMaxxingTheme {
                HomeScreen()
            }
        }
    }
}
