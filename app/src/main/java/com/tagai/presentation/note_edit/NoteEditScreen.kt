package com.tagai.presentation.note_edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    noteId: Long,
    onNavigateBack: () -> Unit,
    viewModel: NoteEditViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var newTag by remember { mutableStateOf("") }

    LaunchedEffect(noteId) {
        viewModel.onEvent(NoteEditEvent.LoadNote(noteId))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is NoteEditEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Note") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        IconButton(onClick = { viewModel.onEvent(NoteEditEvent.SaveNote) }) {
                            Icon(Icons.Default.Check, contentDescription = "Save")
                        }
                    }
                }
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
                onValueChange = { viewModel.onEvent(NoteEditEvent.ContentChanged(it)) },
                label = { Text("Content") },
                modifier = Modifier.fillMaxWidth().weight(1f)
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Tags", style = MaterialTheme.typography.titleMedium)
            
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.tags) { tag ->
                    InputChip(
                        selected = true,
                        onClick = { viewModel.onEvent(NoteEditEvent.TagRemoved(tag)) },
                        label = { Text(tag) },
                        trailingIcon = { Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = newTag,
                    onValueChange = { newTag = it },
                    placeholder = { Text("Add manual tag") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    if (newTag.isNotBlank()) {
                        viewModel.onEvent(NoteEditEvent.TagAdded(newTag))
                        newTag = ""
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Tag")
                }
            }
        }
    }
}