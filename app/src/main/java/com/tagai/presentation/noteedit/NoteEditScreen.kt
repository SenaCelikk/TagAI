package com.tagai.presentation.noteedit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    noteId: Long,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoteEditViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var newTag by remember { mutableStateOf("") }

    val currentOnNavigateBack by rememberUpdatedState(onNavigateBack)

    LaunchedEffect(noteId) {
        viewModel.onEvent(NoteEditEvent.LoadNote(noteId))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is NoteEditEffect.NavigateBack -> currentOnNavigateBack()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            NoteEditTopBar(
                isLoading = state.isLoading,
                onNavigateBack = onNavigateBack,
                onSave = { viewModel.onEvent(NoteEditEvent.SaveNote) }
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

            TagRow(
                tags = state.tags,
                onRemoveTag = { viewModel.onEvent(NoteEditEvent.TagRemoved(it)) }
            )

            AddTagSection(
                newTag = newTag,
                onNewTagChange = { newTag = it },
                onAddTag = {
                    if (newTag.isNotBlank()) {
                        viewModel.onEvent(NoteEditEvent.TagAdded(newTag))
                        newTag = ""
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteEditTopBar(
    isLoading: Boolean,
    onNavigateBack: () -> Unit,
    onSave: () -> Unit
) {
    TopAppBar(
        title = { Text("Edit Note") },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                IconButton(onClick = onSave) {
                    Icon(Icons.Default.Check, contentDescription = "Save")
                }
            }
        }
    )
}

@Composable
private fun TagRow(
    tags: List<String>,
    onRemoveTag: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tags) { tag ->
            InputChip(
                selected = true,
                onClick = { onRemoveTag(tag) },
                label = { Text(tag) },
                trailingIcon = {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            )
        }
    }
}

@Composable
private fun AddTagSection(
    newTag: String,
    onNewTagChange: (String) -> Unit,
    onAddTag: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = newTag,
            onValueChange = onNewTagChange,
            placeholder = { Text("Add manual tag") },
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onAddTag) {
            Icon(Icons.Default.Add, contentDescription = "Add Tag")
        }
    }
}
