package com.magpie.ui.screens

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3Api
import com.magpie.R

enum class Difficulty {
    TODDLER, EXPLORER, EXPERT
}

// Square numbers: 4, 9, 16, 25, 32
val VALID_ITEM_COUNTS = listOf(4, 9, 16, 25, 32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    onBeginHunt: (location: String, difficulty: Difficulty, itemCount: Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var location by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.EXPLORER) }
    var selectedItemCount by remember { mutableStateOf(9) } // Default to 3x3 grid

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
                ElevatedFilterChip(
                    selected = selectedDifficulty == Difficulty.TODDLER,
                    onClick = { selectedDifficulty = Difficulty.TODDLER },
                    label = { Text(stringResource(R.string.difficulty_toddler)) }
                )
                ElevatedFilterChip(
                    selected = selectedDifficulty == Difficulty.EXPLORER,
                    onClick = { selectedDifficulty = Difficulty.EXPLORER },
                    label = { Text(stringResource(R.string.difficulty_explorer)) }
                )
                ElevatedFilterChip(
                    selected = selectedDifficulty == Difficulty.EXPERT,
                    onClick = { selectedDifficulty = Difficulty.EXPERT },
                    label = { Text(stringResource(R.string.difficulty_expert)) }
                )
            }
        }

        // Item Count Selector (Square Numbers Only)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.item_count),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VALID_ITEM_COUNTS.forEach { count ->
                    val gridSize = kotlin.math.sqrt(count.toDouble()).toInt()
                    ElevatedFilterChip(
                        selected = selectedItemCount == count,
                        onClick = { selectedItemCount = count },
                        label = { Text("$gridSize×$gridSize") }
                    )
                }
            }
        }

        // Spacer to push buttons to bottom
        Spacer(modifier = Modifier.weight(1f))

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
                    onBeginHunt(location, selectedDifficulty, selectedItemCount)
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
