package com.aneesh.healthmaxxing.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "account_prefs")

class AccountPreferences(private val context: Context) {
    private val SELECTED_ACCOUNT_ID = stringPreferencesKey("selected_account_id")
    val selectedAccountId = context.dataStore.data.map { prefs ->
        prefs[SELECTED_ACCOUNT_ID]
    }

    suspend fun saveSelectedAccountId(id: String) {
        context.dataStore.edit { prefs ->
            prefs[SELECTED_ACCOUNT_ID] = id
        }
    }
}