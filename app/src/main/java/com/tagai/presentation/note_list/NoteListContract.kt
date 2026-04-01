package com.tagai.presentation.note_list

import com.tagai.domain.model.Note

data class NoteListState(
    val notes: List<Note> = emptyList(),
    val filteredNotes: List<Note> = emptyList(),
    val availableTags: List<String> = emptyList(),
    val selectedTag: String? = null,
    val isLoading: Boolean = false
)

sealed interface NoteListEvent {
    object LoadNotes : NoteListEvent
    data class FilterByTag(val tag: String?) : NoteListEvent
    data class DeleteNote(val note: Note) : NoteListEvent
}

sealed interface NoteListEffect {
    data class NavigateToEdit(val noteId: Long) : NoteListEffect
    object NavigateToAdd : NoteListEffect
}