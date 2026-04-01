package com.tagai.presentation.note_edit

import com.tagai.domain.model.Note

data class NoteEditState(
    val title: String = "",
    val content: String = "",
    val tags: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface NoteEditEvent {
    data class LoadNote(val id: Long) : NoteEditEvent
    data class TitleChanged(val title: String) : NoteEditEvent
    data class ContentChanged(val content: String) : NoteEditEvent
    data class TagAdded(val tag: String) : NoteEditEvent
    data class TagRemoved(val tag: String) : NoteEditEvent
    object SaveNote : NoteEditEvent
}

sealed interface NoteEditEffect {
    object NavigateBack : NoteEditEffect
}