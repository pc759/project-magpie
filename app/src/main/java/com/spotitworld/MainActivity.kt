package com.spotitworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.spotitworld.ui.screens.Difficulty
import com.spotitworld.ui.screens.HuntScreen
import com.spotitworld.ui.screens.MainMenuScreen
import com.spotitworld.ui.screens.SetupScreen
import com.spotitworld.ui.theme.MagpieTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MagpieTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.MainMenu) }
                var huntParams by remember { mutableStateOf<HuntParams?>(null) }

                when (currentScreen) {
                    Screen.MainMenu -> {
                        MainMenuScreen(
                            onStartNewHunt = { currentScreen = Screen.Setup }
                        )
                    }
                    Screen.Setup -> {
                        SetupScreen(
                            onBeginHunt = { location, difficulty, itemCount ->
                                huntParams = HuntParams(location, difficulty, itemCount)
                                currentScreen = Screen.Hunt
                            },
                            onBack = { currentScreen = Screen.MainMenu }
                        )
                    }
                    Screen.Hunt -> {
                        huntParams?.let { params ->
                            HuntScreen(
                                location = params.location,
                                difficulty = params.difficulty,
                                itemCount = params.itemCount,
                                onBack = { currentScreen = Screen.MainMenu }
                            )
                        }
                    }
                }
            }
        }
    }
}

sealed class Screen {
    object MainMenu : Screen()
    object Setup : Screen()
    object Hunt : Screen()
}

data class HuntParams(
    val location: String,
    val difficulty: Difficulty,
    val itemCount: Int
)
