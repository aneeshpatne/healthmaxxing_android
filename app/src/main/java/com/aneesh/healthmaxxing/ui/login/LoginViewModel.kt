package com.aneesh.healthmaxxing.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aneesh.healthmaxxing.data.datastore.AccountPreferences
import com.aneesh.healthmaxxing.data.remote.ApiService
import com.aneesh.healthmaxxing.data.remote.RegisterProfileRequest
import com.aneesh.healthmaxxing.data.remote.RegisterRequest
import com.aneesh.healthmaxxing.data.remote.RegisterResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ApiService,
    private val accountPreferences: AccountPreferences
) : ViewModel() {
    var loading by mutableStateOf(false)
        private set

    var pendingAccountId by mutableStateOf<String?>(null)
        private set

    var pendingProfileId by mutableStateOf<String?>(null)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var success by mutableStateOf(false)
        private set

    var registeredUser by mutableStateOf<RegisterResponse?>(null)
        private set

    fun registerProfile(name: String) {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) {
            error = "Name is required"
            success = false
            return
        }

        val accountId = pendingAccountId
        if (accountId.isNullOrBlank()) {
            error = "Account ID is required"
            success = false
            return
        }

        viewModelScope.launch {
            loading = true
            error = null
            success = false

            try {
                val response = apiService.registerProfile(
                    RegisterProfileRequest(
                        accountId = accountId,
                        name = trimmedName,
                        isPrimary = "true"
                    )
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.ok == true) {
                        success = true
                        pendingProfileId = body.id
                    } else {
                        error = "Profile registration failed"
                    }
                } else {
                    error = response.errorBody()?.string()
                        ?: "Profile registration failed with code ${response.code()}"
                }
            } catch (e: IOException) {
                error = "Could not connect to server"
            } catch (e: Exception) {
                error = e.message ?: "Something went wrong"
            } finally {
                loading = false
            }
        }
    }

    fun register(mailAddress: String) {
        val trimmedMailAddress = mailAddress.trim()
        if (trimmedMailAddress.isBlank()) {
            error = "Email is required"
            success = false
            registeredUser = null
            return
        }

        viewModelScope.launch {
            loading = true
            error = null
            success = false
            registeredUser = null

            try {
                val response = apiService.register(
                    RegisterRequest(mailAddress = trimmedMailAddress)
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.ok == true) {
                        success = true
                        registeredUser = body
                        pendingAccountId = body.id
                    } else {
                        error = "Registration failed"
                    }
                } else {
                    error = response.errorBody()?.string()
                        ?: "Registration failed with code ${response.code()}"
                }
            } catch (e: IOException) {
                error = "Could not connect to server"
            } catch (e: Exception) {
                error = e.message ?: "Something went wrong"
            } finally {
                loading = false
            }
        }
    }
}
