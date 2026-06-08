package com.aneesh.healthmaxxing.ui.metrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aneesh.healthmaxxing.data.datastore.AccountPreferences
import com.aneesh.healthmaxxing.data.remote.PerformanceResponse
import com.aneesh.healthmaxxing.repository.PerformanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerformanceViewModel @Inject constructor(
    private val performanceRepository: PerformanceRepository,
    private val accountPreferences: AccountPreferences
) : ViewModel() {
    private val _performance = MutableStateFlow<PerformanceResponse?>(null)
    val performance = _performance.asStateFlow()

    private val _isLoading = MutableStateFlow<Boolean>(true)
    val isLoading = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        refresh()
    }

    fun refresh(isUserInitiated: Boolean = false) {
        viewModelScope.launch {
            if (isUserInitiated) _isRefreshing.value = true
            val profileId = accountPreferences.selectedPrimaryProfileId.first()
            if (!profileId.isNullOrBlank()) {
                getPerformance(profileId)
            } else {
                _error.value = "Profile ID is missing."
                _isLoading.value = false
            }
            if (isUserInitiated) _isRefreshing.value = false
        }
    }

    private suspend fun getPerformance(profileId: String) {
        _isLoading.value = true
        _error.value = null
        try {
            val response = performanceRepository.getPerformance(profileId)
            if (response.isSuccessful) {
                _performance.value = response.body()
            } else {
                _error.value = "Failed to load performance: ${response.message()}"
            }
        } catch (e: Exception) {
            _error.value = e.localizedMessage ?: "An error occurred"
        } finally {
            _isLoading.value = false
        }
    }
}
