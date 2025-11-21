package com.magpie.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3Api
import com.magpie.BuildConfig
import com.magpie.data.ai.HuntGenerator
import kotlinx.coroutines.launch

enum class Difficulty {
    TODDLER, EXPLORER, EXPERT
}

data class ItemCountOption(val count: Int, val gridSize: Int, val label: String)

val VALID_ITEM_COUNTS = listOf(
    ItemCountOption(4, 2, "2×2"),
    ItemCountOption(9, 3, "3×3"),
    ItemCountOption(16, 4, "4×4"),
    ItemCountOption(25, 5, "5×5"),
    ItemCountOption(32, 6, "6×6")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    onBeginHunt: (location: String, difficulty: Difficulty, itemCount: Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var location by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.EXPLORER) }
    var selectedItemCount by remember { mutableStateOf(9) }
    var isGenerating by remember { mutableStateOf(false) }
    var generationError by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val huntGenerator = remember { HuntGenerator(context, BuildConfig.GEMINI_API_KEY) }

    if (isGenerating) {
        LoadingScreen(location = location)
    } else if (generationError != null) {
        ErrorScreen(
            error = generationError!!,
            onRetry = {
                generationError = null
                isGenerating = true
                scope.launch {
                    val result = huntGenerator.generateHuntForLocation(location, selectedItemCount)
                    isGenerating = false
                    if (result.success) {
                        onBeginHunt(location, selectedDifficulty, selectedItemCount)
                    } else {
                        generationError = result.error ?: "Unknown error occurred"
                    }
                }
            },
            onBack = onBack
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Setup Hunt",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Location",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = { Text("e.g., Sheffield Park") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Difficulty",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ElevatedFilterChip(
                        selected = selectedDifficulty == Difficulty.TODDLER,
                        onClick = { selectedDifficulty = Difficulty.TODDLER },
                        label = { Text("Toddler") }
                    )
                    ElevatedFilterChip(
                        selected = selectedDifficulty == Difficulty.EXPLORER,
                        onClick = { selectedDifficulty = Difficulty.EXPLORER },
                        label = { Text("Explorer") }
                    )
                    ElevatedFilterChip(
                        selected = selectedDifficulty == Difficulty.EXPERT,
                        onClick = { selectedDifficulty = Difficulty.EXPERT },
                        label = { Text("Expert") }
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Item Count",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VALID_ITEM_COUNTS.forEach { option ->
                        ElevatedFilterChip(
                            selected = selectedItemCount == option.count,
                            onClick = { selectedItemCount = option.count },
                            label = { Text(option.label) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 12.dp)
                ) {
                    Text("Back")
                }
                Button(
                    onClick = {
                        isGenerating = true
                        scope.launch {
                            val result = huntGenerator.generateHuntForLocation(location, selectedItemCount)
                            isGenerating = false
                            if (result.success) {
                                onBeginHunt(location, selectedDifficulty, selectedItemCount)
                            } else {
                                generationError = result.error ?: "Unknown error occurred"
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 12.dp),
                    enabled = location.isNotBlank()
                ) {
                    Text("Begin Hunt")
                }
            }
        }
    }
}

@Composable
fun LoadingScreen(location: String) {
    val loadingMessages = listOf(
        "Summoning the Magpies...",
        "Polishing the sparkles...",
        "Teaching birds to find things...",
        "Sprinkling magic dust...",
        "Organizing the treasure...",
        "Consulting the ancient maps...",
        "Waking up the forest spirits...",
        "Counting the shinies...",
        "Tuning the treasure compass...",
        "Gathering the clues...",
        "Enchanting the items...",
        "Preparing the adventure..."
    )

    var currentMessageIndex by remember { mutableStateOf(0) }
    val infiniteTransition = rememberInfiniteTransition(label = "loading")

    // Cycle through messages every 2 seconds
    androidx.compose.runtime.LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(2000)
            currentMessageIndex = (currentMessageIndex + 1) % loadingMessages.size
        }
    }

    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🪄",
            fontSize = 80.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "Building your hunt in $location...",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = loadingMessages[currentMessageIndex],
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Animated dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            repeat(3) { index ->
                val delay = index * 200
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, delayMillis = delay, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "dotAlpha$index"
                )
                Text(
                    text = "●",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = alpha)
                )
            }
        }
    }
}

@Composable
fun ErrorScreen(
    error: String,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "😕",
            fontSize = 80.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "Oops! Something went wrong",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = error,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 32.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp)
            ) {
                Text("Back")
            }
            Button(
                onClick = onRetry,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp)
            ) {
                Text("Try Again")
            }
        }
    }
}
