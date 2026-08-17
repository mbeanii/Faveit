package com.faveit.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.faveit.app.model.DisplayItem
import com.faveit.app.model.FaveCategory
import com.faveit.app.ui.components.GemMark
import com.faveit.app.ui.components.GemTile

@Composable
fun SetupScreen(
    page: Int,
    items: List<DisplayItem>,
    favoriteCount: Int,
    onPage: (Int) -> Unit,
    onToggle: (String) -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val category = FaveCategory.entries[page]
    val pageItems = items.filter { it.source.category == category }
    val pageSelected = pageItems.count { it.isFavorite }
    val isLast = page == FaveCategory.entries.lastIndex
    val gridState = key(page) { rememberLazyGridState() }
    val haptics = LocalHapticFeedback.current
    val finishWithDelight = {
        haptics.performHapticFeedback(HapticFeedbackType.Confirm)
        onFinish()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                tonalElevation = 6.dp,
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            if (pageSelected == 0) "Choose any that spark"
                            else "$pageSelected chosen here",
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "$favoriteCount total favorites",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    if (!isLast && favoriteCount > 0) {
                        TextButton(
                            onClick = finishWithDelight,
                            modifier = Modifier.testTag("finish_setup_early"),
                        ) { Text("Start now") }
                    }
                    Button(
                        onClick = {
                            if (isLast) finishWithDelight() else onPage(page + 1)
                        },
                        modifier = Modifier.testTag(if (isLast) "finish_setup" else "next_setup"),
                    ) {
                        Text(if (isLast) "Start Faveit" else "Next")
                        Icon(
                            if (isLast) Icons.Rounded.Done
                            else Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.padding(start = 7.dp),
                        )
                    }
                }
            }
        },
    ) { scaffoldPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .testTag("setup_grid_${category.wireName}"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(9.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (page > 0) {
                        IconButton(onClick = { onPage(page - 1) }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Previous category")
                        }
                    } else {
                        TextButton(onClick = finishWithDelight) { Text("Skip") }
                    }
                    LinearProgressIndicator(
                        progress = { (page + 1f) / FaveCategory.entries.size },
                        modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                    )
                    Text(
                        "${page + 1}/${FaveCategory.entries.size}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(Modifier.padding(top = 5.dp, bottom = 5.dp)) {
                    Text("Pick what you love", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        "Tap your very favorite ${category.title.lowercase()}. Fast instincts win.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                    if (page == 0) {
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.82f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.38f),
                            ),
                        ) {
                            Row(
                                Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                GemMark(Modifier.size(52.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("For the blank-mind moment", fontWeight = FontWeight.Black)
                                    Text(
                                        "Pick one favorite and you can start immediately.",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            }
                        }
                    }
                }
            }
            items(pageItems, key = { it.id }) { item ->
                GemTile(
                    title = item.displayName,
                    subtitle = item.category.singular,
                    emoji = item.emoji,
                    palette = item.palette,
                    selected = item.isFavorite,
                    selectionMode = true,
                    onClick = { onToggle(item.id) },
                    modifier = Modifier.fillMaxWidth().height(142.dp).testTag("setup_item_${item.id}"),
                )
            }
        }
    }
}
