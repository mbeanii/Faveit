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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.faveit.app.data.DiscoveryEngine
import com.faveit.app.model.DisplayItem
import com.faveit.app.model.FaveCategory
import com.faveit.app.ui.components.GemMark
import com.faveit.app.ui.components.GemTile

private val StringListSaver = Saver<List<String>, ArrayList<String>>(
    save = { ArrayList(it) },
    restore = { it.toList() },
)

@Composable
fun SetupScreen(
    page: Int,
    items: List<DisplayItem>,
    favoriteCount: Int,
    onPage: (Int) -> Unit,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit,
    onManage: (DisplayItem) -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val category = FaveCategory.entries[page]
    val catalog = remember(items.size) { items.map { it.source } }
    val initialIds = remember(category, catalog) {
        DiscoveryEngine.initial(catalog, category).map { it.id }
    }
    var shownIds by rememberSaveable(category.name, stateSaver = StringListSaver) {
        mutableStateOf(initialIds)
    }
    var removedIds by rememberSaveable(category.name, stateSaver = StringListSaver) {
        mutableStateOf(emptyList())
    }
    val byId = items.associateBy { it.id }
    val pageFavorites = items.filter { it.isFavorite && it.source.category == category }
    val pageFavoriteIds = pageFavorites.map { it.id }
    LaunchedEffect(category, pageFavoriteIds) {
        val missing = pageFavoriteIds.filterNot { it in shownIds }
        if (missing.isNotEmpty()) shownIds = (shownIds + missing).distinct()
    }
    val pageItems = shownIds.mapNotNull(byId::get)
    val pageSelected = pageFavorites.size
    val totalInCategory = catalog.count { it.category == category }
    val hasMore = shownIds.size < totalInCategory
    val isLast = page == FaveCategory.entries.lastIndex
    val gridState = key(page) { rememberLazyGridState() }
    val haptics = LocalHapticFeedback.current
    val finishWithDelight = {
        haptics.performHapticFeedback(HapticFeedbackType.Confirm)
        onFinish()
    }
    val showMore = {
        val next = DiscoveryEngine.next(
            items = catalog,
            category = category,
            favorites = pageFavorites.map { it.source },
            excludedIds = shownIds.toSet(),
            batch = shownIds.size / DiscoveryEngine.BATCH_SIZE,
        )
        shownIds = (shownIds + next.map { it.id }).distinct()
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
                        "Popular, varied picks first. More picks adapt to what you choose.",
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
                val wasRemoved = item.id in removedIds && !item.isFavorite
                GemTile(
                    title = item.displayName,
                    subtitle = when {
                        item.isFavorite -> "Tap to customize"
                        wasRemoved -> "Removed · tap to add"
                        else -> item.category.singular
                    },
                    emoji = item.emoji,
                    palette = item.palette,
                    selected = item.isFavorite,
                    editable = item.isFavorite,
                    muted = wasRemoved,
                    selectionMode = true,
                    onClick = {
                        if (item.isFavorite) {
                            onManage(item)
                        } else {
                            removedIds = removedIds.filterNot { it == item.id }
                            onAdd(item.id)
                        }
                    },
                    onRemove = if (item.isFavorite) {
                        {
                            removedIds = (removedIds + item.id).distinct()
                            onRemove(item.id)
                        }
                    } else {
                        null
                    },
                    removeLabel = "Remove ${item.displayName} from favorites",
                    modifier = Modifier.fillMaxWidth().height(142.dp)
                        .testTag("setup_item_${item.id}"),
                )
            }
            if (hasMore) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    OutlinedButton(
                        onClick = showMore,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                            .testTag("more_setup_${category.wireName}"),
                    ) {
                        Text("More picks · ${shownIds.size} of $totalInCategory shown")
                    }
                }
            }
        }
    }
}
