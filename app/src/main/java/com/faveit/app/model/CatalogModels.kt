package com.faveit.app.model

enum class FaveCategory(
    val wireName: String,
    val title: String,
    val singular: String,
    val emoji: String,
    val defaultPalette: GemPalette,
) {
    RESTAURANTS("restaurants", "Restaurants", "Restaurant", "🍽️", GemPalette.RUBY),
    MUSIC("music", "Music", "Music", "🎵", GemPalette.AMETHYST),
    MOVIES("movies", "Movies", "Movie", "🎬", GemPalette.SAPPHIRE),
    TV("tv", "TV", "TV show", "📺", GemPalette.TOPAZ),
    BOOKS("books", "Books", "Book", "📚", GemPalette.EMERALD),
    GAMES("games", "Games", "Game", "🎮", GemPalette.AQUAMARINE),
    ACTIVITIES("activities", "Activities", "Activity", "🛼", GemPalette.CITRINE),
    FOODS("foods", "Foods", "Food", "🍓", GemPalette.GARNET);

    companion object {
        fun fromWire(value: String): FaveCategory = entries.first { it.wireName == value }
    }
}

enum class GemPalette(val label: String) {
    EMERALD("Emerald"),
    RUBY("Ruby"),
    AMETHYST("Amethyst"),
    SAPPHIRE("Sapphire"),
    TOPAZ("Topaz"),
    AQUAMARINE("Aquamarine"),
    CITRINE("Citrine"),
    GARNET("Garnet"),
}

data class CatalogItem(
    val id: String,
    val name: String,
    val category: FaveCategory,
    val emoji: String,
    val palette: GemPalette,
    val aliases: List<String> = emptyList(),
)

data class FavoriteOverride(
    val itemId: String,
    val displayName: String? = null,
    val category: FaveCategory? = null,
    val palette: GemPalette? = null,
) {
    val isDefault: Boolean
        get() = displayName.isNullOrBlank() && category == null && palette == null
}

data class DisplayItem(
    val source: CatalogItem,
    val displayName: String,
    val category: FaveCategory,
    val palette: GemPalette,
    val isFavorite: Boolean,
) {
    val id: String get() = source.id
    val emoji: String get() = source.emoji
}
