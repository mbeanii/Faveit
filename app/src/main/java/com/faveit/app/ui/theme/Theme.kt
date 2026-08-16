package com.faveit.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FaveitColors = darkColorScheme(
    primary = EmeraldAccent,
    onPrimary = Ink,
    primaryContainer = Color(0xFF0B6746),
    onPrimaryContainer = Ivory,
    secondary = Color(0xFFFFCA5C),
    onSecondary = Ink,
    background = Ink,
    onBackground = Ivory,
    surface = DeepInk,
    onSurface = Ivory,
    surfaceVariant = SoftInk,
    onSurfaceVariant = Mist,
    outline = Color(0xFF4A5268),
    error = Color(0xFFFF6B7F),
)

@Composable
fun FaveitTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FaveitColors,
        typography = FaveitTypography,
        content = content,
    )
}
