package com.aneesh.healthmaxxing.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import com.aneesh.healthmaxxing.model.StartRequest
import com.aneesh.healthmaxxing.network.RetrofitClient

@Composable
fun HomeScreen() {
    var result by remember {
        mutableStateOf("No response")
    }

    val scope = rememberCoroutineScope()

    Column {
        Button(
            onClick = {
                scope.launch {
                    try {
                        val response = RetrofitClient.api.test(
                            StartRequest(
                                profileId = "019e1610-f254-715c-8900-ca1a98c823da"
                            )
                        )

                        result = """
                            ok: ${response.ok}
                            id: ${response.id}
                            profileId: ${response.profileId}
                        """.trimIndent()
                    } catch (e: Exception) {
                        result = e.message ?: "Error"
                    }
                }
            }
        ) {
            Text("POST")
        }

        Text(result)
    }
}
