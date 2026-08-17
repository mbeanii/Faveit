package com.faveit.app.data

import com.faveit.app.model.CatalogItem
import com.faveit.app.model.FaveCategory
import com.faveit.app.model.GemPalette

/**
 * Compatibility-only records for selections shipped before the researched catalog.
 *
 * They never appear in search or discovery, but remain resolvable so an app update
 * cannot silently erase an existing favorite. Removing one remains reversible.
 */
object LegacyCatalogItems {
    val entries: List<CatalogItem> = listOf(
        legacy("restaurant_sushi_spot", "The Sushi Spot", "🍣", GemPalette.SAPPHIRE, listOf("sushi", "japanese")),
        legacy("restaurant_taco_stand", "The Taco Stand", "🍴", GemPalette.CITRINE, listOf("tacos", "taco shop")),
        legacy("restaurant_corner_cafe", "Corner Café", "☕", GemPalette.TOPAZ, listOf("coffee shop", "corner cafe")),
        legacy("restaurant_pizza_night", "Neighborhood Pizza", "🍕", GemPalette.RUBY, listOf("pizza place", "pizzeria")),
        legacy("restaurant_thai_kitchen", "Thai Kitchen", "🍜", GemPalette.AMETHYST, listOf("thai food", "noodles")),
        legacy("restaurant_indian_table", "Indian Table", "🍛", GemPalette.GARNET, listOf("indian food", "curry")),
        legacy("restaurant_bbq_house", "Backyard BBQ", "🍖", GemPalette.TOPAZ, listOf("barbecue", "bbq")),
        legacy("restaurant_garden_bistro", "Garden Bistro", "🌿", GemPalette.EMERALD, listOf("salad", "vegetarian")),
        legacyMusic("music_jazz", "Late-night Jazz", "🎷", GemPalette.SAPPHIRE, listOf("jazz", "smooth jazz")),
        legacyMusic("music_classical", "Classical Focus", "🎻", GemPalette.EMERALD, listOf("classical music", "orchestra")),
        legacyMusic("music_indie", "Indie Mix", "🎸", GemPalette.AMETHYST, listOf("indie music", "alternative")),
        legacyMusic("music_rnb", "R&B Favorites", "💿", GemPalette.RUBY, listOf("rnb", "rhythm and blues")),
        legacyMusic("music_house", "House Music", "🎵", GemPalette.AQUAMARINE, listOf("dance music", "edm")),
        legacyMusic("music_lofi", "Lo-fi Beats", "🎧", GemPalette.EMERALD, listOf("lofi", "chill beats")),
    )

    private fun legacy(
        id: String, name: String, emoji: String, palette: GemPalette, aliases: List<String>,
    ) = compatibilityItem(id, name, FaveCategory.RESTAURANTS, emoji, palette, aliases)

    private fun legacyMusic(
        id: String, name: String, emoji: String, palette: GemPalette, aliases: List<String>,
    ) = compatibilityItem(id, name, FaveCategory.MUSIC, emoji, palette, aliases)

    private fun compatibilityItem(
        id: String,
        name: String,
        category: FaveCategory,
        emoji: String,
        palette: GemPalette,
        aliases: List<String>,
    ) = CatalogItem(
        id = id,
        name = name,
        category = category,
        emoji = emoji,
        palette = palette,
        aliases = aliases,
        facet = "legacy compatibility",
        tags = setOf(category.wireName),
        discoverable = false,
    )
}
