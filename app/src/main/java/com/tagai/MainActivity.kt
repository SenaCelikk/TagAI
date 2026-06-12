package com.tagai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tagai.presentation.noteadd.NoteAddScreen
import com.tagai.presentation.noteedit.NoteEditScreen
import com.tagai.presentation.notelist.NoteListScreen
import com.tagai.presentation.splash.SplashScreen
import com.tagai.ui.theme.TagAiTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TagAiTheme {
                TagAINavHost()
            }
        }
    }
}

@Composable
fun TagAINavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate("note_list") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        composable("note_list") {
            NoteListScreen(
                onNavigateToAdd = {
                    navController.navigate("note_add")
                },
                onNavigateToEdit = { noteId ->
                    navController.navigate("note_edit/$noteId")
                }
            )
        }
        composable("note_add") {
            NoteAddScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = "note_edit/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
            NoteEditScreen(
                noteId = noteId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
