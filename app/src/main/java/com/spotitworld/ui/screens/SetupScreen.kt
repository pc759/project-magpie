package com.spotitworld.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spotitworld.R

enum class Difficulty {
    TODDLER, EXPLORER, EXPERT
}

@Composable
fun SetupScreen(
    onBeginHunt: (location: String, difficulty: Difficulty, itemCount: Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var location by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.EXPLORER) }
    var itemCount by remember { mutableStateOf(10f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Text(
            text = stringResource(R.string.setup_hunt),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // Location Input
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.location),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                placeholder = { Text("e.g., Central Park") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        // Difficulty Selector
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.difficulty),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedDifficulty == Difficulty.TODDLER,
                    onClick = { selectedDifficulty = Difficulty.TODDLER },
                    label = { Text(stringResource(R.string.difficulty_toddler)) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedDifficulty == Difficulty.EXPLORER,
                    onClick = { selectedDifficulty = Difficulty.EXPLORER },
                    label = { Text(stringResource(R.string.difficulty_explorer)) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedDifficulty == Difficulty.EXPERT,
                    onClick = { selectedDifficulty = Difficulty.EXPERT },
                    label = { Text(stringResource(R.string.difficulty_expert)) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Item Count Slider
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.item_count),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = itemCount.toInt().toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = itemCount,
                onValueChange = { itemCount = it },
                valueRange = 5f..50f,
                steps = 44,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Spacer to push buttons to bottom
        Column(modifier = Modifier.weight(1f))

        // Action Buttons
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
                    onBeginHunt(location, selectedDifficulty, itemCount.toInt())
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp),
                enabled = location.isNotBlank()
            ) {
                Text(stringResource(R.string.begin_hunt))
            }
        }
    }
}
