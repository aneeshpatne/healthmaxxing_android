package com.aneesh.healthmaxxing.ui.metrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aneesh.healthmaxxing.data.datastore.AccountPreferences
import com.aneesh.healthmaxxing.data.remote.ProfileEssentialsResponse
import com.aneesh.healthmaxxing.repository.EssentialsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EssentialsViewModel @Inject constructor(
    private val essentialsRepository: EssentialsRepository,
    private val accountPreferences: AccountPreferences
) : ViewModel() {
    private val _essentials = MutableStateFlow<ProfileEssentialsResponse?>(null)
    val essentials = _essentials.asStateFlow()

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
                getEssentials(profileId)
            } else {
                _error.value = "Profile ID is missing."
                _isLoading.value = false
            }
            if (isUserInitiated) _isRefreshing.value = false
        }
    }

    private suspend fun getEssentials(profileId: String) {
        _isLoading.value = true
        _error.value = null
        try {
            val response = essentialsRepository.getEssentials(profileId)
            if (response.isSuccessful) {
                _essentials.value = response.body()
            } else {
                _error.value = "Failed to load essentials: ${response.message()}"
            }
        } catch (e: Exception) {
            _error.value = e.localizedMessage ?: "An error occurred"
        } finally {
            _isLoading.value = false
        }
    }
}
