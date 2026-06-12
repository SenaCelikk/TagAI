package com.tagai.presentation.noteedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tagai.domain.model.Note
import com.tagai.domain.repository.NoteRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteEditViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NoteEditState())
    val state = _state.asStateFlow()

    private val _effect = Channel<NoteEditEffect>()
    val effect = _effect.receiveAsFlow()

    private var currentNoteId: Long = -1

    fun onEvent(event: NoteEditEvent) {
        when (event) {
            is NoteEditEvent.LoadNote -> loadNote(event.id)
            is NoteEditEvent.TitleChanged -> {
                _state.update { it.copy(title = event.title) }
            }
            is NoteEditEvent.ContentChanged -> {
                _state.update { it.copy(content = event.content) }
            }
            is NoteEditEvent.TagAdded -> {
                if (event.tag.isNotBlank() && !_state.value.tags.contains(event.tag)) {
                    _state.update { it.copy(tags = it.tags + event.tag) }
                }
            }
            is NoteEditEvent.TagRemoved -> {
                _state.update { it.copy(tags = it.tags - event.tag) }
            }
            NoteEditEvent.SaveNote -> saveNote()
        }
    }

    private fun loadNote(id: Long) {
        currentNoteId = id
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val note = repository.getNoteById(id)
            if (note != null) {
                _state.update {
                    it.copy(
                        title = note.title,
                        content = note.content,
                        tags = note.tags,
                        isLoading = false
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false, error = "Note not found") }
            }
        }
    }

    private fun saveNote() {
        val currentState = _state.value
        if (currentState.content.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val updatedNote = Note(
                id = currentNoteId,
                title = currentState.title,
                content = currentState.content,
                tags = currentState.tags,
                timestamp = System.currentTimeMillis()
            )
            repository.updateNote(updatedNote)
            _effect.send(NoteEditEffect.NavigateBack)
        }
    }
}
