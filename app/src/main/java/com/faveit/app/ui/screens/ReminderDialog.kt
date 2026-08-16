package com.faveit.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReminderDialog(
    enabled: Boolean,
    onDismiss: () -> Unit,
    onEnable: () -> Unit,
    onDisable: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Rounded.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary) },
        title = { Text(if (enabled) "Gentle reminders are on" else "Remember an old favorite") },
        text = {
            Column {
                Text("Faveit can send one simple local reminder each week, starting in a few days.")
                Text(
                    "Example: “Remember In-N-Out?”",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 10.dp),
                )
                Text(
                    "No tracking, recommendations, or sponsored content.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
        },
        confirmButton = {
            Button(onClick = if (enabled) onDisable else onEnable) {
                Text(if (enabled) "Turn off" else "Turn on")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Not now") } },
    )
}
