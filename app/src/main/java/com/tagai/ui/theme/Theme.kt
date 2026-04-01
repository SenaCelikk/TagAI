package com.tagai.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Senior Tip: Create a custom class for attributes Material 3 doesn't have
@Immutable
data class TagAiCustomColors(
    val aiGlow: Color,
    val tagChipBackground: Color
)
// Theme.kt

private val VintagePaperScheme = lightColorScheme(
    primary = VintagePurple,
    onPrimary = Color.White,
    primaryContainer = VintagePurpleContainer,

    background = PaperBase,        // The desk/background
    onBackground = InkBlack,

    surface = PaperDarker,         // The actual note paper
    onSurface = InkBlack,

    surfaceVariant = PaperBase,    // Inside the note
    outline = PaperBorder          // The paper edge
)


val LocalTagAiColors = staticCompositionLocalOf {
    TagAiCustomColors(aiGlow = Color.Unspecified, tagChipBackground = Color.Unspecified)
}

@Composable
fun TagAiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) VintagePaperScheme else VintagePaperScheme

    val customColors = TagAiCustomColors(
        aiGlow = if (darkTheme) VintagePurple else VintagePurple,
        tagChipBackground = if (darkTheme) Color(0xFF333333) else Color(0xFFE9ECEF)
    )

    CompositionLocalProvider(LocalTagAiColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// Accessor for the UI
object TagAiTheme {
    val customColors: TagAiCustomColors
        @Composable
        get() = LocalTagAiColors.current
}