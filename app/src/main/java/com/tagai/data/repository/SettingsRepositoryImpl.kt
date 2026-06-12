package com.tagai.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tagai.domain.repository.AiMode
import com.tagai.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    private val aiModeKey = stringPreferencesKey("ai_mode")

    override fun getAiMode(): Flow<AiMode> {
        return context.dataStore.data.map { preferences ->
            val modeString = preferences[aiModeKey] ?: AiMode.CLOUD.name
            AiMode.valueOf(modeString)
        }
    }

    override suspend fun setAiMode(mode: AiMode) {
        context.dataStore.edit { preferences ->
            preferences[aiModeKey] = mode.name
        }
    }
}
