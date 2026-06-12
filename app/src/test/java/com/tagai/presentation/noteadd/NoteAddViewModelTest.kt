package com.tagai.presentation.noteadd

import app.cash.turbine.test
import com.tagai.domain.usecase.SaveNoteWithAiUseCase
import com.tagai.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class NoteAddViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val saveNoteWithAiUseCase = mockk<SaveNoteWithAiUseCase>()
    private lateinit var viewModel: NoteAddViewModel

    @Before
    fun setup() {
        viewModel = NoteAddViewModel(saveNoteWithAiUseCase)
    }

    @Test
    fun `initial state should be idle`() = runTest {
        assertEquals(NoteAddState(), viewModel.state.value)
    }

    @Test
    fun `ContentChanged event should update content in state`() = runTest {
        val newContent = "New note content"
        viewModel.onEvent(NoteAddEvent.ContentChanged(newContent))
        assertEquals(newContent, viewModel.state.value.content)
    }

    @Test
    fun `SaveNote event should trigger use case and navigate back on success`() = runTest {
        val content = "Test content"
        viewModel.onEvent(NoteAddEvent.ContentChanged(content))

        coEvery { saveNoteWithAiUseCase(content) } returns Result.success(Unit)

        viewModel.effect.test {
            viewModel.onEvent(NoteAddEvent.SaveNote)
            assertEquals(NoteAddEffect.NavigateBack, awaitItem())
        }

        assertEquals(false, viewModel.state.value.isLoading)
        assertEquals(null, viewModel.state.value.error)
    }

    @Test
    fun `SaveNote event should update state with error on failure`() = runTest {
        val content = "Test content"
        val errorMessage = "Failed to save"
        viewModel.onEvent(NoteAddEvent.ContentChanged(content))

        coEvery { saveNoteWithAiUseCase(content) } returns Result.failure(Exception(errorMessage))

        viewModel.onEvent(NoteAddEvent.SaveNote)

        assertEquals(false, viewModel.state.value.isLoading)
        assertEquals(errorMessage, viewModel.state.value.error)
    }
}
