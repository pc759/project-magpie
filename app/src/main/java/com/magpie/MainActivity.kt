package com.magpie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.magpie.ui.screens.Difficulty
import com.magpie.ui.screens.HuntScreen
import com.magpie.ui.screens.MainMenuScreen
import com.magpie.ui.screens.SetupScreen
import com.magpie.ui.theme.MagpieTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MagpieTheme {
                var currentScreen by remember { mutableStateOf("menu") }
                var huntParams by remember { mutableStateOf<HuntParams?>(null) }

                when (currentScreen) {
                    "menu" -> MainMenuScreen(
                        onStartNewHunt = { currentScreen = "setup" }
                    )
                    "setup" -> SetupScreen(
                        onBeginHunt = { location, difficulty, itemCount, huntId ->
                            huntParams = HuntParams(location, difficulty, itemCount, huntId)
                            currentScreen = "hunt"
                        },
                        onBack = { currentScreen = "menu" }
                    )
                    "hunt" -> huntParams?.let { params ->
                        HuntScreen(
                            location = params.location,
                            difficulty = params.difficulty,
                            itemCount = params.itemCount,
                            huntId = params.huntId,
                            onBack = {
                                currentScreen = "menu"
                                huntParams = null
                            }
                        )
                    }
                }
            }
        }
    }
}

data class HuntParams(
    val location: String,
    val difficulty: Difficulty,
    val itemCount: Int,
    val huntId: Int
)
