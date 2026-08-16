package com.faveit.app.ui.theme

import androidx.compose.ui.graphics.Color
import com.faveit.app.model.GemPalette

val Ink = Color(0xFF090B12)
val DeepInk = Color(0xFF111522)
val SoftInk = Color(0xFF191E2D)
val Ivory = Color(0xFFF8F7F2)
val Mist = Color(0xFFBFC6D8)
val EmeraldAccent = Color(0xFF47E69C)

data class GemColors(
    val dark: Color,
    val base: Color,
    val light: Color,
    val glint: Color,
)

fun GemPalette.colors(): GemColors = when (this) {
    GemPalette.EMERALD -> GemColors(Color(0xFF06462F), Color(0xFF0B9A62), Color(0xFF48E6A1), Color(0xFFBDFFE1))
    GemPalette.RUBY -> GemColors(Color(0xFF590E2B), Color(0xFFC21F58), Color(0xFFFF5F8A), Color(0xFFFFC4D5))
    GemPalette.AMETHYST -> GemColors(Color(0xFF35115E), Color(0xFF7C35BD), Color(0xFFC678F2), Color(0xFFF0D1FF))
    GemPalette.SAPPHIRE -> GemColors(Color(0xFF092D68), Color(0xFF1766C2), Color(0xFF58A6FF), Color(0xFFC9E5FF))
    GemPalette.TOPAZ -> GemColors(Color(0xFF6A3510), Color(0xFFD47819), Color(0xFFFFB64E), Color(0xFFFFE0A8))
    GemPalette.AQUAMARINE -> GemColors(Color(0xFF075058), Color(0xFF159AA3), Color(0xFF63E1DA), Color(0xFFC7FFF8))
    GemPalette.CITRINE -> GemColors(Color(0xFF664706), Color(0xFFD09A13), Color(0xFFFFD550), Color(0xFFFFF0AB))
    GemPalette.GARNET -> GemColors(Color(0xFF4C1021), Color(0xFF922542), Color(0xFFE35874), Color(0xFFFFC0CA))
}
