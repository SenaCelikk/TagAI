package com.tagai.presentation.notelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tagai.domain.model.Note
import com.tagai.domain.repository.NoteRepository
import com.tagai.domain.usecase.GetNotesUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteListViewModel(
    private val getNotesUseCase: GetNotesUseCase,
    private val repository: NoteRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NoteListState())
    val state = _state.asStateFlow()

    private val _effect = Channel<NoteListEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        observeNotes()
    }

    private fun observeNotes() {
        getNotesUseCase()
            .onEach { notes ->
                _state.update {
                    it.copy(
                        notes = notes,
                        availableTags = notes.flatMap { it.tags }.distinct(),
                        filteredNotes = filterNotes(notes, it.selectedTag),
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: NoteListEvent) {
        when (event) {
            is NoteListEvent.FilterByTag -> {
                _state.update {
                    it.copy(
                        selectedTag = event.tag,
                        filteredNotes = filterNotes(it.notes, event.tag)
                    )
                }
            }
            is NoteListEvent.DeleteNote -> {
                viewModelScope.launch {
                    repository.deleteNote(event.note)
                }
            }
            NoteListEvent.LoadNotes -> { /* Handled by init */ }
        }
    }

    fun onAddClick() {
        viewModelScope.launch {
            _effect.send(NoteListEffect.NavigateToAdd)
        }
    }

    fun onNoteClick(id: Long) {
        viewModelScope.launch {
            _effect.send(NoteListEffect.NavigateToEdit(id))
        }
    }

    private fun filterNotes(notes: List<Note>, tag: String?): List<Note> {
        return if (tag == null) notes else notes.filter { it.tags.contains(tag) }
    }
}
