package com.aneesh.healthmaxxing.ui.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aneesh.healthmaxxing.data.bluetooth.ScaleManager
import com.aneesh.healthmaxxing.data.bluetooth.ScaleMeasurement
import com.aneesh.healthmaxxing.data.datastore.AccountPreferences
import com.aneesh.healthmaxxing.data.remote.AddMeasurementRequest
import com.aneesh.healthmaxxing.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
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
    private val scaleManager: ScaleManager,
    private val apiService: ApiService,
    private val accountPreferences: AccountPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecordUiState())
    val uiState: StateFlow<RecordUiState> = _uiState.asStateFlow()

    private var readJob: Job? = null
    private var postedMeasurement = false

    fun startReading() {
        if (readJob?.isActive == true) return

        postedMeasurement = false
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
                            status = if (measurement.isFinal) "Measurement complete. Saving..." else "Reading measurement",
                            error = null
                        )
                    }
                    if (measurement.isFinal) {
                        postMeasurement(measurement)
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

    private suspend fun postMeasurement(measurement: ScaleMeasurement) {
        if (postedMeasurement) return
        postedMeasurement = true

        val weight = measurement.weightKg
        if (weight == null) {
            _uiState.update {
                it.copy(
                    isReading = false,
                    status = "Measurement complete",
                    error = "Weight is missing, so the measurement was not saved."
                )
            }
            return
        }

        val profileId = accountPreferences.selectedPrimaryProfileId.first()
        if (profileId.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    isReading = false,
                    status = "Measurement complete",
                    error = "Profile ID is missing, so the measurement was not saved."
                )
            }
            return
        }

        try {
            val response = apiService.addMeasurement(
                AddMeasurementRequest(
                    profileId = profileId,
                    weight = weight.toDouble(),
                    heartbeat = measurement.heartRate,
                    impedance = measurement.impedanceOhms?.toDouble()
                )
            )

            _uiState.update {
                if (response.isSuccessful && response.body()?.ok == true) {
                    it.copy(isReading = false, status = "Measurement saved", error = null)
                } else {
                    it.copy(
                        isReading = false,
                        status = "Measurement complete",
                        error = response.errorBody()?.string()
                            ?: "Measurement save failed with code ${response.code()}"
                    )
                }
            }
        } catch (exception: IOException) {
            _uiState.update {
                it.copy(
                    isReading = false,
                    status = "Measurement complete",
                    error = "Could not connect to server to save measurement."
                )
            }
        } catch (exception: Exception) {
            _uiState.update {
                it.copy(
                    isReading = false,
                    status = "Measurement complete",
                    error = exception.message ?: "Measurement save failed."
                )
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
