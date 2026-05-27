package com.aneesh.healthmaxxing.account

import com.aneesh.healthmaxxing.data.datastore.AccountPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val prefs: AccountPreferences
) : ViewModel() {

    val selectedAccountId: StateFlow<String?> =
        prefs.selectedAccountId.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun selectAccount(id: String) {
        viewModelScope.launch {
            prefs.saveSelectedAccountId(id)
        }
    }
}
