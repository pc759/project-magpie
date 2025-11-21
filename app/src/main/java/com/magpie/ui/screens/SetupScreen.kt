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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3Api

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
                placeholder = { Text("e.g., Central Park") },
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
                    onBeginHunt(location, selectedDifficulty, selectedItemCount)
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
