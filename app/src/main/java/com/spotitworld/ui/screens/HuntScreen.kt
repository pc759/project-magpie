package com.spotitworld.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.spotitworld.data.HuntItem

@Composable
fun HuntScreen(
    location: String,
    difficulty: Difficulty,
    itemCount: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Mock hunt items
    val mockItems = (1..itemCount).map { index ->
        HuntItem(
            id = index,
            name = "Item $index",
            imageUrl = "https://via.placeholder.com/200?text=Item+$index",
            funFact = "This is a fun fact about item $index!",
            isFound = false
        )
    }

    var huntItems by remember { mutableStateOf(mockItems) }
    var selectedItem by remember { mutableStateOf<HuntItem?>(null) }
    var showGrayscale by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hunt in $location",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Difficulty: ${difficulty.name}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Button(onClick = { showGrayscale = !showGrayscale }) {
                Text(if (showGrayscale) "Show Color" else "Show B&W")
            }
        }

        // Item Grid
        val gridSize = kotlin.math.sqrt(itemCount.toDouble()).toInt()
        LazyVerticalGrid(
            columns = GridCells.Fixed(gridSize),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(huntItems) { item ->
                HuntItemCard(
                    item = item,
                    isGrayscale = showGrayscale && !item.isFound,
                    onClick = { selectedItem = item },
                    onMarkFound = {
                        huntItems = huntItems.map {
                            if (it.id == item.id) it.copy(isFound = true) else it
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Back Button
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Menu")
        }
    }

    // Detail Modal
    selectedItem?.let { item ->
        HuntItemDetailModal(
            item = item,
            onDismiss = { selectedItem = null },
            onMarkFound = {
                huntItems = huntItems.map {
                    if (it.id == item.id) it.copy(isFound = true) else it
                }
                selectedItem = null
            }
        )
    }
}

@Composable
fun HuntItemCard(
    item: HuntItem,
    isGrayscale: Boolean,
    onClick: () -> Unit,
    onMarkFound: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = if (isGrayscale) 0.5f else 1f
            )

            if (item.isFound) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Green.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓ Found",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun HuntItemDetailModal(
    item: HuntItem,
    onDismiss: () -> Unit,
    onMarkFound: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .clickable(enabled = false, onClick = {})
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = item.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = item.funFact,
                    fontSize = 14.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Close")
                    }
                    if (!item.isFound) {
                        Button(
                            onClick = onMarkFound,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Mark Found")
                        }
                    }
                }
            }
        }
    }
}
