package com.faveit.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.faveit.app.model.DisplayItem
import com.faveit.app.model.FaveCategory
import com.faveit.app.model.FavoriteOverride
import com.faveit.app.model.GemPalette
import com.faveit.app.ui.theme.colors

private const val MAX_DISPLAY_NAME_CODE_POINTS = 60

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteEditorSheet(
    item: DisplayItem,
    onDismiss: () -> Unit,
    onSave: (FavoriteOverride) -> Unit,
    onReset: (String) -> Unit,
    onRemove: (String) -> Unit,
) {
    var displayName by rememberSaveable(item.id) { mutableStateOf(item.displayName) }
    var category by rememberSaveable(item.id) { mutableStateOf(item.category) }
    var palette by rememberSaveable(item.id) { mutableStateOf(item.palette) }
    var confirmRemove by rememberSaveable(item.id) { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
        ) {
            Text("Make it yours", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Saved on this device; Android backup may transfer changes.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp, bottom = 16.dp),
            )
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it.takeCodePoints(MAX_DISPLAY_NAME_CODE_POINTS) },
                modifier = Modifier.fillMaxWidth().testTag("custom_name"),
                label = { Text("Display name") },
                singleLine = true,
            )
            Text(
                "CATEGORY",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 18.dp, bottom = 8.dp),
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(FaveCategory.entries) { option ->
                    FilterChip(
                        selected = category == option,
                        onClick = { category = option },
                        modifier = Modifier.testTag("category_option_${option.wireName}"),
                        label = { Text("${option.emoji} ${option.title}") },
                    )
                }
            }
            Text(
                "GEM STYLE",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 18.dp, bottom = 8.dp),
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(GemPalette.entries) { option ->
                    GemStyleOption(option, selected = palette == option) { palette = option }
                }
            }
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    onSave(
                        FavoriteOverride(
                            itemId = item.id,
                            displayName = displayName.trim().takeIf {
                                it.isNotBlank() && it != item.source.name
                            },
                            category = category.takeIf { it != item.source.category },
                            palette = palette.takeIf { it != item.source.palette },
                        ),
                    )
                    onDismiss()
                },
                enabled = displayName.isNotBlank(),
                modifier = Modifier.fillMaxWidth().testTag("save_customization"),
            ) { Text("Save changes") }
            OutlinedButton(
                onClick = { onReset(item.id); onDismiss() },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp).testTag("reset_customization"),
            ) {
                androidx.compose.material3.Icon(Icons.Rounded.RestartAlt, null)
                Text("Restore catalog defaults", modifier = Modifier.padding(start = 8.dp))
            }
            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            TextButton(
                onClick = { confirmRemove = true },
                modifier = Modifier.align(Alignment.CenterHorizontally).testTag("remove_favorite"),
            ) {
                androidx.compose.material3.Icon(
                    Icons.Rounded.DeleteOutline,
                    null,
                    tint = MaterialTheme.colorScheme.error,
                )
                Text(
                    "Remove from favorites",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 6.dp),
                )
            }
        }
    }

    if (confirmRemove) AlertDialog(
        onDismissRequest = { confirmRemove = false },
        title = { Text("Remove ${item.displayName}?") },
        text = { Text("You can always find it again in Discover More or search.") },
        confirmButton = {
            TextButton(
                onClick = { onRemove(item.id); confirmRemove = false; onDismiss() },
                modifier = Modifier.testTag("confirm_remove"),
            ) {
                Text("Remove", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = { TextButton(onClick = { confirmRemove = false }) { Text("Keep it") } },
    )
}

@Composable
private fun GemStyleOption(palette: GemPalette, selected: Boolean, onClick: () -> Unit) {
    val colors = palette.colors()
    Column(
        Modifier
            .clip(RoundedCornerShape(14.dp))
            .testTag("gem_style_${palette.name.lowercase()}")
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
            )
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier.size(48.dp).clip(CircleShape).background(
                Brush.linearGradient(listOf(colors.light, colors.base, colors.dark)),
            ).then(if (selected) Modifier.background(Color.White.copy(alpha = 0.18f)) else Modifier),
        )
        Text(
            palette.label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.Black else FontWeight.Medium,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

internal fun String.takeCodePoints(maxCodePoints: Int): String {
    require(maxCodePoints >= 0) { "maxCodePoints must be non-negative" }
    if (codePointCount(0, length) <= maxCodePoints) return this
    val endIndex = offsetByCodePoints(0, maxCodePoints)
    return substring(0, endIndex)
}
