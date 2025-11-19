package com.spotitworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.spotitworld.ui.screens.Difficulty
import com.spotitworld.ui.screens.MainMenuScreen
import com.spotitworld.ui.screens.SetupScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier,
                    color = MaterialTheme.colorScheme.background
                ) {
                    MagpieApp()
                }
            }
        }
    }
}

@Composable
fun MagpieApp() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.MainMenu) }

    when (currentScreen) {
        Screen.MainMenu -> {
            MainMenuScreen(
                onStartNewHunt = {
                    currentScreen = Screen.Setup
                }
            )
        }
        Screen.Setup -> {
            SetupScreen(
                onBeginHunt = { location, difficulty, itemCount ->
                    // TODO: Navigate to hunt screen with these parameters
                    println("Hunt started: $location, $difficulty, $itemCount items")
                },
                onBack = {
                    currentScreen = Screen.MainMenu
                }
            )
        }
    }
}

sealed class Screen {
    object MainMenu : Screen()
    object Setup : Screen()
}

@Preview(showBackground = true)
@Composable
fun MainMenuScreenPreview() {
    MaterialTheme {
        MainMenuScreen(
            onStartNewHunt = {}
        )
    }
}
