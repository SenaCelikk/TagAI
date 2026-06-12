package com.tagai.presentation.noteadd

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteAddScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoteAddViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Stable reference for the navigation lambda
    val currentOnNavigateBack by rememberUpdatedState(onNavigateBack)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                // Fixed: Executing the stable function call with ()
                is NoteAddEffect.NavigateBack -> currentOnNavigateBack()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            NoteAddTopBar(
                isLoading = state.isLoading,
                onNavigateBack = onNavigateBack,
                onSave = { viewModel.onEvent(NoteAddEvent.SaveNote) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.content,
                onValueChange = { viewModel.onEvent(NoteAddEvent.ContentChanged(it)) },
                label = { Text("Content") },
                modifier = Modifier.fillMaxWidth().weight(1f),
                placeholder = { Text("Type something to generate AI tags...") }
            )

            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "AI tags will be generated automatically on save.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteAddTopBar(
    isLoading: Boolean,
    onNavigateBack: () -> Unit,
    onSave: () -> Unit
) {
    TopAppBar(
        title = { Text("Add Note") },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else {
                IconButton(onClick = onSave) {
                    Icon(Icons.Default.Check, contentDescription = "Save")
                }
            }
        }
    )
}
