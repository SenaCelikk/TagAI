package com.tagai.domain.repository

import com.tagai.domain.model.NoteMetadata

interface AIGenerationService {
    suspend fun generateMetadata(content: String): Result<NoteMetadata>
}