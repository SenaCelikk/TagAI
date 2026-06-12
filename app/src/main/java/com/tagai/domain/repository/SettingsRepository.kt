package com.tagai.domain.repository

import kotlinx.coroutines.flow.Flow

enum class AiMode {
    CLOUD, LOCAL
}

interface SettingsRepository {
    fun getAiMode(): Flow<AiMode>
    suspend fun setAiMode(mode: AiMode)
}
