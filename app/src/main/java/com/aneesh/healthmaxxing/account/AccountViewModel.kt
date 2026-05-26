package com.aneesh.healthmaxxing.account

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AccountViewModel : ViewModel() {
    private val _selectedAccoundId = MutableStateFlow<String?>(null)
    val selectedAccountId: StateFlow<String?> = _selectedAccoundId.asStateFlow()
    fun selectAccount(id: String) {
        _selectedAccoundId.value = id
    }
}