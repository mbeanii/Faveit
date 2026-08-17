package com.faveit.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.faveit.app.ui.theme.DeepInk
import com.faveit.app.ui.theme.EmeraldAccent

internal fun favoriteSavedMessage(displayName: String): String =
    "$displayName is in your favorites"

internal fun setupCompleteMessage(favoriteCount: Int): String =
    if (favoriteCount == 0) "Faveit is ready when inspiration strikes"
    else "Your favorites are ready"

@Composable
fun DelightBanner(
    message: String,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(22.dp)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("delight_banner")
            .semantics { liveRegion = LiveRegionMode.Polite },
        shape = shape,
        color = DeepInk,
        tonalElevation = 12.dp,
        shadowElevation = 16.dp,
        border = BorderStroke(1.dp, EmeraldAccent.copy(alpha = 0.72f)),
    ) {
        Box(
            Modifier.background(
                Brush.linearGradient(
                    colors = listOf(
                        EmeraldAccent.copy(alpha = 0.22f),
                        Color.Transparent,
                        Color.White.copy(alpha = 0.07f),
                    ),
                    start = Offset.Zero,
                    end = Offset.Infinite,
                ),
            ),
        ) {
            Row(
                Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                GemMark(Modifier.size(44.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        message,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        "Yours, right when you need it.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
