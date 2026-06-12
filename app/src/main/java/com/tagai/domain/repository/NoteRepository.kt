package com.tagai.domain.repository

import com.tagai.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    suspend fun getNoteById(id: Long): Note?
    suspend fun insertNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(note: Note)
}
