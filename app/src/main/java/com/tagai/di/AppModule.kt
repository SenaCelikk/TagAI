package com.tagai.di

import androidx.room.Room
import com.tagai.data.local.NoteDatabase
import com.tagai.data.remote.MockAIGenerationService
import com.tagai.data.repository.NoteRepositoryImpl
import com.tagai.domain.repository.AIGenerationService
import com.tagai.domain.repository.NoteRepository
import com.tagai.domain.usecase.GetNotesUseCase
import com.tagai.domain.usecase.SaveNoteWithAiUseCase
import com.tagai.presentation.noteadd.NoteAddViewModel
import com.tagai.presentation.noteedit.NoteEditViewModel
import com.tagai.presentation.notelist.NoteListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            NoteDatabase::class.java,
            NoteDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<NoteDatabase>().noteDao }

    single<NoteRepository> { NoteRepositoryImpl(get()) }

    single<AIGenerationService> { MockAIGenerationService() }

    factory { GetNotesUseCase(get()) }
    factory { SaveNoteWithAiUseCase(get(), get()) }

    viewModel { NoteListViewModel(get(), get()) }
    viewModel { NoteAddViewModel(get()) }
    viewModel { NoteEditViewModel(get()) }
}
