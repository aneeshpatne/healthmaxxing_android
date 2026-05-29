package com.aneesh.healthmaxxing.ui.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aneesh.healthmaxxing.data.bluetooth.ScaleManager
import com.aneesh.healthmaxxing.data.bluetooth.ScaleMeasurement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecordUiState(
    val measurement: ScaleMeasurement = ScaleMeasurement(),
    val isReading: Boolean = false,
    val status: String = "Ready to read from scale",
    val error: String? = null
)

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val scaleManager: ScaleManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecordUiState())
    val uiState: StateFlow<RecordUiState> = _uiState.asStateFlow()

    private var readJob: Job? = null

    fun startReading() {
        if (readJob?.isActive == true) return

        _uiState.value = RecordUiState(
            isReading = true,
            status = "Connecting to Cult Smart Scale"
        )

        readJob = viewModelScope.launch {
            scaleManager.measurements()
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isReading = false,
                            status = "Unable to read from scale",
                            error = throwable.message ?: "Bluetooth read failed"
                        )
                    }
                }
                .collect { measurement ->
                    _uiState.update {
                        it.copy(
                            measurement = measurement,
                            isReading = !measurement.isFinal,
                            status = if (measurement.isFinal) "Measurement complete" else "Reading measurement",
                            error = null
                        )
                    }
                }
        }.also { job ->
            job.invokeOnCompletion { throwable ->
                if (throwable != null) {
                    _uiState.update {
                        it.copy(
                            isReading = false,
                            status = "Unable to read from scale",
                            error = throwable.message
                        )
                    }
                } else {
                    _uiState.update {
                        if (it.measurement.isFinal) it else it.copy(isReading = false)
                    }
                }
            }
        }
    }

    fun stopReading() {
        readJob?.cancel()
        readJob = null
        _uiState.update {
            it.copy(isReading = false, status = "Reading stopped")
        }
    }

    fun onPermissionDenied() {
        _uiState.update {
            it.copy(
                isReading = false,
                status = "Bluetooth permission needed",
                error = "Allow Bluetooth permissions to read from the scale."
            )
        }
    }
}
