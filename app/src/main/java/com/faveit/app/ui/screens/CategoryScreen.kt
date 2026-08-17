package com.faveit.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faveit.app.model.DisplayItem
import com.faveit.app.model.FaveCategory
import com.faveit.app.ui.components.GemTile

@Composable
fun CategoryScreen(
    category: FaveCategory,
    items: List<DisplayItem>,
    onBack: () -> Unit,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit,
    onManage: (DisplayItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val liveFavorites = items.filter { it.isFavorite && it.category == category }
    val liveFavoriteIds = liveFavorites.map { it.id }
    var pinnedIds by remember(category) { mutableStateOf(liveFavoriteIds) }
    LaunchedEffect(category, liveFavoriteIds) {
        val additions = liveFavoriteIds.filterNot { it in pinnedIds }
        if (additions.isNotEmpty()) pinnedIds = (pinnedIds + additions).distinct()
    }
    val byId = items.associateBy { it.id }
    val pinnedItems = pinnedIds.mapNotNull(byId::get).filter {
        !it.isFavorite || it.category == category
    }
    val discover = items.filter {
        !it.isFavorite && it.source.category == category && it.id !in pinnedIds
    }

    Column(modifier.fillMaxSize()) {
        androidx.compose.foundation.layout.Row(
            Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back") }
            Column {
                Text(category.title, style = MaterialTheme.typography.headlineMedium)
                Text(
                    if (liveFavorites.size == 1) "1 favorite" else "${liveFavorites.size} favorites",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
                .testTag("category_grid_${category.wireName}"),
            horizontalArrangement = Arrangement.spacedBy(9.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            sectionHeader("YOUR FAVORITES")
            if (pinnedItems.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(
                        Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(category.emoji, fontSize = 38.sp)
                        Text("Nothing here yet", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Tap a gem below to add one.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else {
                items(pinnedItems, key = { "favorite_${it.id}" }) { item ->
                    GemTile(
                        title = item.displayName,
                        subtitle = if (item.isFavorite) {
                            "Tap to customize"
                        } else {
                            "Removed · tap to add"
                        },
                        emoji = item.emoji,
                        palette = item.palette,
                        selected = item.isFavorite,
                        editable = item.isFavorite,
                        muted = !item.isFavorite,
                        selectionMode = true,
                        onClick = {
                            if (item.isFavorite) onManage(item) else onAdd(item.id)
                        },
                        onRemove = if (item.isFavorite) {
                            { onRemove(item.id) }
                        } else {
                            null
                        },
                        removeLabel = "Remove ${item.displayName} from favorites",
                        modifier = Modifier.fillMaxWidth().height(146.dp)
                            .testTag("favorite_${item.id}"),
                    )
                }
            }

            sectionHeader("DISCOVER MORE", topPadding = 20)
            items(discover, key = { "discover_${it.id}" }) { item ->
                GemTile(
                    title = item.displayName,
                    subtitle = "Tap to add",
                    emoji = item.emoji,
                    palette = item.palette,
                    muted = true,
                    onClick = { onAdd(item.id) },
                    modifier = Modifier.fillMaxWidth().height(132.dp)
                        .testTag("discover_${item.id}"),
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) { Spacer(Modifier.height(12.dp)) }
        }
    }
}

private fun LazyGridScope.sectionHeader(text: String, topPadding: Int = 0) {
    item(span = { GridItemSpan(maxLineSpan) }) {
        Text(
            text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(top = topPadding.dp, bottom = 2.dp),
        )
    }
}
