package com.tagai.presentation.notelist

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Surface
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import com.tagai.domain.model.Note
import com.tagai.ui.theme.TagAiTheme
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import kotlin.random.Random

// Curated pastel note colors – soft and harmonious
private val NoteColors = listOf(
    Color(0xFFFFF9C4), // Soft Yellow
    Color(0xFFFFCDD2), // Soft Pink
    Color(0xFFC8E6C9), // Soft Green
    Color(0xFFBBDEFB), // Soft Blue
    Color(0xFFE1BEE7), // Soft Lavender
    Color(0xFFFFE0B2), // Soft Orange
    Color(0xFFB2EBF2), // Soft Cyan
    Color(0xFFD1C4E9), // Soft Purple
    Color(0xFFF8BBD0), // Soft Rose
    Color(0xFFDCEDC8), // Soft Lime
    Color(0xFFB3E5FC), // Soft Sky
    Color(0xFFFFCCBC), // Soft Peach
)

// Height tiers for visual variety in the staggered grid
private val HeightTiers = listOf(140.dp, 170.dp, 200.dp, 160.dp, 190.dp, 150.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoteListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Stable references for lambdas to prevent LaunchedEffect restarts
    val currentOnNavigateToAdd by rememberUpdatedState(onNavigateToAdd)
    val currentOnNavigateToEdit by rememberUpdatedState(onNavigateToEdit)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is NoteListEffect.NavigateToAdd -> currentOnNavigateToAdd()
                is NoteListEffect.NavigateToEdit -> currentOnNavigateToEdit(effect.noteId)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { NoteListTopBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onAddClick() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TagFilterRow(
                tags = state.availableTags,
                selectedTag = state.selectedTag,
                onTagClick = { viewModel.onEvent(NoteListEvent.FilterByTag(it)) }
            )

            if (state.isLoading) {
                ShimmerLoadingGrid()
            } else {
                NotesStaggeredGrid(
                    notes = state.filteredNotes,
                    onNoteClick = viewModel::onNoteClick,
                    onDeleteNote = { viewModel.onEvent(NoteListEvent.DeleteNote(it)) }
                )
            }
        }
    }
}


// ─── Top Bar ──────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteListTopBar() {
    androidx.compose.material3.CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.tagai.R.drawable.ic_tagai_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(50.dp).padding(end = 8.dp)
                )
                Text(
                    "TagAI",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

// ─── Shimmer Loading Animation ─────────────────────────────────────────

@Composable
internal fun ShimmerLoadingGrid() {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerTranslateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFE0E0E0),
            Color(0xFFF5F5F5),
            Color(0xFFE0E0E0),
        ),
        start = Offset(shimmerTranslateAnim - 200f, shimmerTranslateAnim - 200f),
        end = Offset(shimmerTranslateAnim, shimmerTranslateAnim)
    )

    // Simulate staggered grid placeholders
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        repeat(3) { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Left placeholder
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(if (rowIndex % 2 == 0) 160.dp else 130.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(shimmerBrush)
                )
                // Right placeholder
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(if (rowIndex % 2 == 0) 130.dp else 180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(shimmerBrush)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

// ─── Staggered Notes Grid ──────────────────────────────────────────────

@Composable
internal fun NotesStaggeredGrid(
    notes: List<Note>,
    onNoteClick: (Long) -> Unit,
    onDeleteNote: (Note) -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalItemSpacing = 10.dp
    ) {
        itemsIndexed(
            items = notes,
            key = { _, note -> note.id }
        ) { index, note ->
            // Deterministic random based on note.id for stable color & size
            val noteRandom = remember(note.id) { Random(note.id) }
            val cardColor = remember(note.id) { NoteColors[noteRandom.nextInt(NoteColors.size)] }
            val cardHeight = remember(note.id) { HeightTiers[noteRandom.nextInt(HeightTiers.size)] }

            // Entrance animation: scale + fade
            val animatedAlpha = remember { Animatable(0f) }
            val animatedScale = remember { Animatable(0.8f) }

            LaunchedEffect(note.id) {
                // Stagger the animation by index for a cascade effect
                delay(index * 50L)
                animatedAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 400)
                )
            }
            LaunchedEffect(note.id) {
                delay(index * 50L)
                animatedScale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f)
                )
            }

            NoteItem(
                note = note,
                cardColor = cardColor,
                cardHeight = cardHeight,
                onClick = { onNoteClick(note.id) },
                onDelete = { onDeleteNote(note) },
                modifier = Modifier
                    .graphicsLayer {
                        alpha = animatedAlpha.value
                        scaleX = animatedScale.value
                        scaleY = animatedScale.value
                    }
            )
        }
    }
}

// ─── Tag Filter Row ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagFilterRow(
    tags: List<String>,
    selectedTag: String?,
    onTagClick: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            com.tagai.presentation.components.TagView(
                text = "All",
                isSelected = selectedTag == null,
                onClick = { onTagClick(null) }
            )
        }
        items(tags) { tag ->
            com.tagai.presentation.components.TagView(
                text = "#$tag",
                isSelected = selectedTag == tag,
                onClick = { onTagClick(tag) }
            )
        }
    }
}

// ─── Note Item Card ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteItem(
    note: Note,
    cardColor: Color,
    cardHeight: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFF2C2924),
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFF2C2924).copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF5D574D),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Tags at the bottom
            if (note.tags.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    note.tags.take(2).forEach { tag ->
                        com.tagai.presentation.components.TagView(
                            text = tag,
                            isSelected = true,
                            onClick = null
                        )
                    }
                    if (note.tags.size > 2) {
                        Text(
                            text = "+${note.tags.size - 2}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF5D574D),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─── Previews ──────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun NoteItemPreview() {
    TagAiTheme {
        Surface {
            NoteItem(
                note = Note(
                    id = 1L,
                    title = "Meeting Notes",
                    content = "Discuss Q3 roadmap, assign tasks to the team, and review the design mockups.",
                    tags = listOf("work", "meeting")
                ),
                cardColor = Color(0xFFFFF9C4),
                cardHeight = 170.dp,
                onClick = {},
                onDelete = {},
                modifier = Modifier.width(200.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoteItemLongContentPreview() {
    TagAiTheme {
        Surface {
            NoteItem(
                note = Note(
                    id = 2L,
                    title = "A Very Long Title That Should Definitely Overflow and Be Truncated",
                    content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
                    tags = listOf("personal", "ideas", "long", "overflow")
                ),
                cardColor = Color(0xFFE1BEE7),
                cardHeight = 160.dp,
                onClick = {},
                onDelete = {},
                modifier = Modifier.width(200.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TagFilterRowPreview() {
    TagAiTheme {
        Surface {
            TagFilterRow(
                tags = listOf("work", "personal", "ideas", "shopping", "health"),
                selectedTag = "ideas",
                onTagClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoteListLoadingPreview() {
    TagAiTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ShimmerLoadingGrid()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoteListContentPreview() {
    val sampleNotes = listOf(
        Note(id = 1L, title = "Grocery List", content = "Eggs, milk, bread, avocados, and coffee beans.", tags = listOf("shopping")),
        Note(id = 2L, title = "Workout Plan", content = "Monday: Chest & Triceps\nWednesday: Back & Biceps\nFriday: Legs & Shoulders", tags = listOf("health", "fitness")),
        Note(id = 3L, title = "Book Recommendations", content = "1. Atomic Habits\n2. Deep Work\n3. The Pragmatic Programmer", tags = listOf("reading", "personal")),
        Note(id = 4L, title = "Sprint Retro", content = "What went well: CI pipeline. What to improve: code reviews.", tags = listOf("work")),
        Note(id = 5L, title = "Travel Ideas", content = "Japan in autumn, Portugal in spring, Iceland northern lights.", tags = listOf("travel", "ideas")),
        Note(id = 6L, title = "Recipe: Pasta", content = "Garlic, olive oil, cherry tomatoes, basil, parmesan.", tags = listOf("cooking"))
    )

    TagAiTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            NotesStaggeredGrid(
                notes = sampleNotes,
                onNoteClick = {},
                onDeleteNote = {}
            )
        }
    }
}
