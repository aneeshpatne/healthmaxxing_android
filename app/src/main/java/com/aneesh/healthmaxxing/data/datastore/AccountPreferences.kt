package com.aneesh.healthmaxxing.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore by preferencesDataStore(name = "account_prefs")

class AccountPreferences @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val selectedAccountIdKey = stringPreferencesKey("selected_account_id")
    private val selectedPrimaryProfileIdKey = stringPreferencesKey("selected_primary_profile_id")

    val selectedAccountId = context.dataStore.data.map { prefs ->
        prefs[selectedAccountIdKey]
    }

    val selectedPrimaryProfileId = context.dataStore.data.map { prefs ->
        prefs[selectedPrimaryProfileIdKey]
    }

    suspend fun saveSelectedAccountId(id: String) {
        context.dataStore.edit { prefs ->
            prefs[selectedAccountIdKey] = id
        }
    }

    suspend fun saveSelectedPrimaryProfileId(id: String) {
        context.dataStore.edit { prefs ->
            prefs[selectedPrimaryProfileIdKey] = id
        }
    }
}
