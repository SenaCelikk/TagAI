package com.tagai.data.remote

import com.tagai.domain.model.NoteMetadata
import com.tagai.domain.repository.AIGenerationService
import kotlinx.coroutines.delay

class MockAIGenerationService : AIGenerationService {
    override suspend fun generateMetadata(content: String): Result<NoteMetadata> {
        delay(1000) // Simulate network delay
        val commonWords = listOf("work", "personal", "todo", "idea", "important", "shopping", "market")
        val tags = commonWords.filter { content.contains(it, ignoreCase = true) }.take(3).ifEmpty {
            listOf("general")
        }
        val title = content.take(20).ifEmpty { "Untitled" }
        return Result.success(NoteMetadata(title, tags))
    }
}
