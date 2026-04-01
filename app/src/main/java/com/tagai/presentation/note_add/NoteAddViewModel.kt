package com.tagai.presentation.note_add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tagai.domain.usecase.SaveNoteWithAiUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteAddViewModel(
    private val saveNoteWithAiUseCase: SaveNoteWithAiUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(NoteAddState())
    val state = _state.asStateFlow()

    private val _effect = Channel<NoteAddEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: NoteAddEvent) {
        when (event) {
            is NoteAddEvent.ContentChanged -> {
                _state.update { it.copy(content = event.content) }
            }
            NoteAddEvent.SaveNote -> saveNote()
        }
    }

    private fun saveNote() {
        val content = _state.value.content
        if (content.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            saveNoteWithAiUseCase(content)
                .onSuccess {
                    _effect.send(NoteAddEffect.NavigateBack)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }
}