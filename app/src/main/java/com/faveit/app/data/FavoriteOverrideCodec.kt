package com.faveit.app.data

import com.faveit.app.model.FaveCategory
import com.faveit.app.model.FavoriteOverride
import com.faveit.app.model.GemPalette
import java.net.URLDecoder
import java.net.URLEncoder

object FavoriteOverrideCodec {
    private const val EMPTY = "~"

    fun encode(value: FavoriteOverride): String = listOf(
        encodePart(value.itemId),
        value.displayName?.takeIf { it.isNotBlank() }?.let(::encodePart) ?: EMPTY,
        value.category?.wireName ?: EMPTY,
        value.palette?.name ?: EMPTY,
    ).joinToString("\t")

    fun decode(value: String): FavoriteOverride? = runCatching {
        val parts = value.split('\t')
        require(parts.size == 4)
        FavoriteOverride(
            itemId = decodePart(parts[0]),
            displayName = parts[1].takeUnless { it == EMPTY }?.let(::decodePart),
            category = parts[2].takeUnless { it == EMPTY }?.let(FaveCategory::fromWire),
            palette = parts[3].takeUnless { it == EMPTY }?.let(GemPalette::valueOf),
        )
    }.getOrNull()

    private fun encodePart(value: String): String = URLEncoder.encode(value, "UTF-8")
    private fun decodePart(value: String): String = URLDecoder.decode(value, "UTF-8")
}
