package com.faveit.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.faveit.app.model.GemPalette
import com.faveit.app.ui.theme.colors

@Composable
fun GemMark(
    modifier: Modifier = Modifier,
    palette: GemPalette = GemPalette.EMERALD,
) {
    val gems = palette.colors()
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier
            .clip(shape)
            .background(Brush.linearGradient(listOf(gems.light, gems.base, gems.dark)))
            .border(1.dp, Color.White.copy(alpha = 0.55f), shape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Rounded.AutoAwesome,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.fillMaxSize(0.52f),
        )
    }
}
