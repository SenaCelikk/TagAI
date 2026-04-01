package com.tagai.domain.usecase

import com.tagai.domain.model.Note
import com.tagai.domain.model.NoteMetadata
import com.tagai.domain.repository.AIGenerationService
import com.tagai.domain.repository.NoteRepository

class SaveNoteWithAiUseCase(
    private val repository: NoteRepository,
    private val aiService: AIGenerationService
) {
    suspend operator fun invoke(content: String): Result<Unit> {
        val result = aiService.generateMetadata(content)
        
        val metadata = result.getOrElse {
            // Fallback: If AI fails (e.g. invalid API key), save the note with default title/no tags
            NoteMetadata(
                title = content.take(30).trim() + if (content.length > 30) "..." else "",
                tags = emptyList()
            )
        }

        val note = Note(
            title = metadata.title,
            content = content,
            tags = metadata.tags
        )
        repository.insertNote(note)
        return Result.success(Unit)
    }
}