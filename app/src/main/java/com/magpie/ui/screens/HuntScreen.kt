package com.magpie.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.magpie.data.HuntItem
import com.magpie.data.HuntRepository
import kotlin.math.sqrt

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
    var showReportMenu by remember { mutableStateOf(false) }
    
    val isComplete = selectedItems.size == huntItems.size
    val gridSize = sqrt(itemCount.toDouble()).toInt()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with location and report button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = location,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = hunt.name,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
            IconButton(
                onClick = { showReportMenu = !showReportMenu },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = "Report unsafe content",
                    tint = Color.Red.copy(alpha = 0.6f)
                )
            }
        }

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
                    onClick = { selectedItem = item }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer with key
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "${hunt.id} • ${difficulty.name} • ${selectedItems.size}/${huntItems.size} found",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Menu")
            }
        }
    }

    // Completion banner
    AnimatedVisibility(
        visible = isComplete,
        enter = scaleIn(animationSpec = tween(500)) + fadeIn(animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable(enabled = false, onClick = {}),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "🎉",
                        fontSize = 64.sp
                    )
                    Text(
                        text = "Hunt Complete!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "You found all $itemCount items in ${hunt.name}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text("Return to Menu")
                    }
                }
            }
        }
    }

    selectedItem?.let { item ->
        HuntItemDetailModal(
            item = item,
            isFound = item.id in selectedItems,
            onDismiss = { selectedItem = null },
            onMarkFound = {
                selectedItems = selectedItems + item.id
            }
        )
    }
}

@Composable
fun HuntItemCard(
    item: HuntItem,
    isFound: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick)
            .border(2.dp, Color.Gray.copy(alpha = 0.3f))
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
                alpha = if (isFound) 1f else 0.5f
            )

            if (isFound) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        color = Color.Green,
                        fontSize = 60.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.alpha(0.8f)
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
    var revealFact by remember { mutableStateOf(isFound) }
    
    // Auto-reveal fact when item becomes found
    LaunchedEffect(isFound) {
        if (isFound && !revealFact) {
            revealFact = true
        }
    }

    val imageAlpha by animateFloatAsState(
        targetValue = if (revealFact) 0.3f else 1f,
        animationSpec = tween(durationMillis = 600)
    )
    
    val factAlpha by animateFloatAsState(
        targetValue = if (revealFact) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 300)
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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(imageAlpha),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Fact overlay
                    if (revealFact) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.6f))
                                .alpha(factAlpha),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.funFact,
                                fontSize = 14.sp,
                                color = Color.White,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Info text
                if (!revealFact) {
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
