package com.aneesh.healthmaxxing.account

import com.aneesh.healthmaxxing.data.datastore.AccountPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AccountState {
    data object Loading : AccountState
    data object LoggedOut : AccountState
    data class LoggedIn(val accountId: String) : AccountState
}

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val prefs: AccountPreferences
) : ViewModel() {

    val accountState: StateFlow<AccountState> =
        prefs.selectedAccountId
            .map { accountId ->
                if (accountId == null) {
                    AccountState.LoggedOut
                } else {
                    AccountState.LoggedIn(accountId)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AccountState.Loading
            )

    fun selectAccount(id: String) {
        viewModelScope.launch {
            prefs.saveSelectedAccountId(id)
        }
    }
}
