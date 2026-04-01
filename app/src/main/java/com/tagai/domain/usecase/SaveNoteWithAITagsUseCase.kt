package com.tagai.domain.usecase

import com.tagai.domain.model.Note
import com.tagai.domain.repository.AIGenerationService
import com.tagai.domain.repository.NoteRepository

class SaveNoteWithAITagsUseCase(
    private val repository: NoteRepository,
    private val aiService: AIGenerationService
) {
    suspend operator fun invoke(content: String): Result<Unit> {
        if (content.isBlank()) return Result.failure(Exception("Content cannot be empty"))

        return try {
            aiService.generateMetadata(content).mapCatching { metadata ->
                val note = Note(
                    // Ensure ID is generated if your Room DB doesn't do it automatically
                    title = metadata.title.ifBlank { "Untitled Note" },
                    content = content,
                    tags = metadata.tags
                )
                repository.insertNote(note)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
