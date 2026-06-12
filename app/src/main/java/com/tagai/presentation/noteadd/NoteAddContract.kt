package com.tagai.presentation.noteadd

data class NoteAddState(
    val content: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface NoteAddEvent {
    data class ContentChanged(val content: String) : NoteAddEvent
    object SaveNote : NoteAddEvent
}

sealed interface NoteAddEffect {
    object NavigateBack : NoteAddEffect
}
