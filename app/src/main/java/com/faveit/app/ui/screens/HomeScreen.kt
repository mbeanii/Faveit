package com.faveit.app.ui.screens

import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faveit.app.model.DisplayItem
import com.faveit.app.model.FaveCategory
import com.faveit.app.ui.components.GemTile
import com.faveit.app.ui.theme.EmeraldAccent
import com.faveit.app.ui.theme.SoftInk
import com.faveit.app.ui.theme.colors

@Composable
fun HomeScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    results: List<DisplayItem>,
    favoriteCounts: Map<FaveCategory, Int>,
    remindersEnabled: Boolean,
    onCategory: (FaveCategory) -> Unit,
    onAdd: (String) -> Unit,
    onReminders: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        val categoryScrollState = rememberScrollState()
        val useScrollableCategories = query.isBlank() &&
            (maxHeight < 520.dp || LocalDensity.current.fontScale > 1.15f)
        val constrainedGridHeight = if (maxWidth > maxHeight) 240.dp else 480.dp
        Column(
            Modifier.fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = 12.dp)
                .then(
                    if (useScrollableCategories) Modifier.verticalScroll(categoryScrollState)
                    else Modifier,
                ),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Faveit", style = MaterialTheme.typography.displaySmall)
                    Text(
                        "Your taste. On demand.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                IconButton(onClick = onReminders, modifier = Modifier.testTag("reminder_button")) {
                    Icon(
                        if (remindersEnabled) {
                            Icons.Rounded.NotificationsActive
                        } else {
                            Icons.Rounded.Notifications
                        },
                        contentDescription = if (remindersEnabled) {
                            "Favorite reminders, on"
                        } else {
                            "Favorite reminders, off"
                        },
                        tint = if (remindersEnabled) {
                            EmeraldAccent
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth().testTag("global_search"),
                placeholder = { Text("What do I love?") },
                leadingIcon = { Icon(Icons.Rounded.Search, null) },
                trailingIcon = {
                    if (query.isNotEmpty()) IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Rounded.Clear, "Clear search")
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(22.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {}),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SoftInk,
                    unfocusedContainerColor = SoftInk,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            )
            Spacer(Modifier.height(12.dp))

            if (query.isBlank()) {
                Text(
                    "FIND A FAVORITE",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                    letterSpacing = 1.sp,
                )
                Spacer(Modifier.height(8.dp))
                CategoryGrid(
                    favoriteCounts,
                    onCategory,
                    if (useScrollableCategories) {
                        Modifier.fillMaxWidth().height(constrainedGridHeight)
                    } else {
                        Modifier.weight(1f)
                    },
                )
            } else {
                SearchResults(query, results, onAdd, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CategoryGrid(
    favoriteCounts: Map<FaveCategory, Int>,
    onCategory: (FaveCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    val categories = FaveCategory.entries
    BoxWithConstraints(modifier) {
        val columnCount = if (maxWidth > maxHeight) 4 else 2
        val rowCount = (categories.size + columnCount - 1) / columnCount
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(rowCount) { rowIndex ->
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(columnCount) { columnIndex ->
                        val itemIndex = rowIndex * columnCount + columnIndex
                        if (itemIndex >= categories.size) {
                            Spacer(Modifier.weight(1f))
                        } else {
                            val category = categories[itemIndex]
                            val count = favoriteCounts[category] ?: 0
                            GemTile(
                                title = category.title,
                                subtitle = if (count == 1) "1 favorite" else "$count favorites",
                                emoji = category.emoji,
                                palette = category.defaultPalette,
                                onClick = { onCategory(category) },
                                compact = true,
                                modifier = Modifier.weight(1f).fillMaxSize()
                                    .testTag("category_${category.wireName}"),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResults(
    query: String,
    results: List<DisplayItem>,
    onAdd: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (results.isEmpty()) {
        Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No match for “$query”", style = MaterialTheme.typography.titleLarge)
                Text(
                    "The bundled catalog stays intentionally focused for this prototype.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
        return
    }

    val resultListState = key(query) { rememberLazyListState() }
    LazyColumn(
        modifier = modifier,
        state = resultListState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(
                if (results.size == 1) "1 MATCH" else "${results.size} MATCHES",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 2.dp),
            )
        }
        items(results, key = { it.id }) { item ->
            SearchResultRow(item = item, onAdd = { onAdd(item.id) })
        }
    }
}

@Composable
private fun SearchResultRow(item: DisplayItem, onAdd: () -> Unit) {
    val gems = item.palette.colors()
    val haptics = LocalHapticFeedback.current
    val view = LocalView.current
    val addWithFeedback = {
        haptics.performHapticFeedback(HapticFeedbackType.Confirm)
        view.playSoundEffect(SoundEffectConstants.CLICK)
        onAdd()
    }
    Surface(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).clickable(
            enabled = !item.isFavorite,
            onClick = addWithFeedback,
        ).semantics {
            stateDescription = if (item.isFavorite) "Favorite" else "Not a favorite"
            if (item.isFavorite) liveRegion = LiveRegionMode.Polite
        }.testTag("search_result_${item.id}"),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(18.dp),
    ) {
        Row(
            Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                Modifier.size(52.dp).clip(RoundedCornerShape(15.dp)).background(
                    Brush.linearGradient(listOf(gems.light, gems.base, gems.dark)),
                ),
                contentAlignment = Alignment.Center,
            ) { Text(item.emoji, fontSize = 25.sp) }
            Column(Modifier.weight(1f)) {
                Text(
                    item.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    item.category.singular,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            FilledIconButton(
                onClick = addWithFeedback,
                enabled = !item.isFavorite,
                modifier = Modifier.testTag("add_${item.id}"),
            ) {
                Icon(
                    if (item.isFavorite) Icons.Rounded.Check else Icons.Rounded.Add,
                    if (item.isFavorite) "Already a favorite" else "Add favorite",
                )
            }
        }
    }
}
