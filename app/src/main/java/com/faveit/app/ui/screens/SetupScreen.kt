package com.faveit.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.faveit.app.model.DisplayItem
import com.faveit.app.model.FaveCategory
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

    Column(
        modifier.fillMaxSize().padding(horizontal = 16.dp).padding(top = 8.dp, bottom = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (page > 0) {
                IconButton(onClick = { onPage(page - 1) }) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Previous category")
                }
            } else {
                TextButton(onClick = onFinish) { Text("Skip") }
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
        Spacer(Modifier.height(14.dp))
        Text("Pick what you love", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Tap your very favorite ${category.title.lowercase()}. Fast instincts win.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f).testTag("setup_grid_${category.wireName}"),
            horizontalArrangement = Arrangement.spacedBy(9.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            items(pageItems, key = { it.id }) { item ->
                GemTile(
                    title = item.displayName,
                    subtitle = item.category.singular,
                    emoji = item.emoji,
                    palette = item.palette,
                    selected = item.isFavorite,
                    onClick = { onToggle(item.id) },
                    modifier = Modifier.fillMaxWidth().height(142.dp).testTag("setup_item_${item.id}"),
                )
            }
        }

        Row(
            Modifier.fillMaxWidth().padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    if (pageSelected == 0) "Choose any that spark" else "$pageSelected chosen here",
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "$favoriteCount total favorites",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Button(
                onClick = { if (isLast) onFinish() else onPage(page + 1) },
                modifier = Modifier.testTag(if (isLast) "finish_setup" else "next_setup"),
            ) {
                Text(if (isLast) "Start Faveit" else "Next")
                Icon(
                    if (isLast) Icons.Rounded.Done else Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.padding(start = 7.dp),
                )
            }
        }
    }
}
