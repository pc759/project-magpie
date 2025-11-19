package com.magpie.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.magpie.data.HuntItem
import com.magpie.data.HuntRepository

@Composable
fun HuntScreen(
    location: String,
    difficulty: Difficulty,
    itemCount: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember { HuntRepository(context) }
    val hunt = remember { repository.getHuntByDifficulty(difficulty) }
    val huntItems = hunt.items.take(itemCount)

    var selectedItems by remember { mutableStateOf(setOf<Int>()) }
    var selectedItem by remember { mutableStateOf<HuntItem?>(null) }
    var showGrayscale by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with hunt info
        Column {
            Text(
                text = hunt.id,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = hunt.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${hunt.location} • ${difficulty.name}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = { showGrayscale = !showGrayscale }) {
                Text(if (showGrayscale) "Show Color" else "Show B&W")
            }
        }

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
                    isFound = item.id in selectedItems,
                    isGrayscale = showGrayscale && item.id !in selectedItems,
                    onClick = { selectedItem = item }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Menu")
        }
    }

    selectedItem?.let { item ->
        HuntItemDetailModal(
            item = item,
            isFound = item.id in selectedItems,
            onDismiss = { selectedItem = null },
            onMarkFound = {
                selectedItems = selectedItems + item.id
                selectedItem = null
            }
        )
    }
}

@Composable
fun HuntItemCard(
    item: HuntItem,
    isFound: Boolean,
    isGrayscale: Boolean,
    onClick: () -> Unit,
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

            if (isFound) {
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
    isFound: Boolean,
    onDismiss: () -> Unit,
    onMarkFound: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showFact by remember { mutableStateOf(false) }
    val factAlpha by animateFloatAsState(
        targetValue = if (showFact) 1f else 0f,
        animationSpec = tween(durationMillis = 600)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
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

                // Fun fact only shown when found
                if (isFound || showFact) {
                    Text(
                        text = item.funFact,
                        fontSize = 14.sp,
                        modifier = Modifier.alpha(factAlpha)
                    )
                } else {
                    Text(
                        text = "Mark as found to reveal the fun fact!",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                }

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
                    if (!isFound) {
                        Button(
                            onClick = {
                                onMarkFound()
                                showFact = true
                            },
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
