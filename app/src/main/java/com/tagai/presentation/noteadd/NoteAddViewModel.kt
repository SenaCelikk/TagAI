package com.tagai.presentation.noteadd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tagai.domain.usecase.SaveNoteWithAiUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

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
            try {
                _state.update { it.copy(isLoading = true, error = null) }
                val result = saveNoteWithAiUseCase(content)

                result.onSuccess {
                    // Update state FIRST
                    _state.update { it.copy(isLoading = false) }
                    // Then send effect
                    _effect.send(NoteAddEffect.NavigateBack)
                }
                result.onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
            } catch (e: IOException) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
