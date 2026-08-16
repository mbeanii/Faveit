package com.faveit.app.data

import com.faveit.app.model.CatalogItem
import com.faveit.app.model.FaveCategory
import com.faveit.app.model.FavoriteOverride
import com.faveit.app.model.GemPalette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FaveitRepositoryProjectionTest {
    private val burger = CatalogItem(
        id = "restaurant_in_n_out",
        name = "In-N-Out",
        category = FaveCategory.RESTAURANTS,
        emoji = "🍔",
        palette = GemPalette.RUBY,
    )

    @Test fun catalogDefaultsRemainAuthoritativeWithoutLocalPreferences() {
        val item = resolveDisplayItems(listOf(burger), UserPreferences()).single()

        assertEquals("In-N-Out", item.displayName)
        assertEquals(FaveCategory.RESTAURANTS, item.category)
        assertEquals(GemPalette.RUBY, item.palette)
        assertFalse(item.isFavorite)
    }

    @Test fun localFavoriteAndEverySupportedOverrideAreProjected() {
        val preferences = UserPreferences(
            favoriteIds = setOf(burger.id),
            overrides = mapOf(
                burger.id to FavoriteOverride(
                    itemId = burger.id,
                    displayName = "Friday Burgers",
                    category = FaveCategory.FOODS,
                    palette = GemPalette.AMETHYST,
                ),
            ),
        )

        val item = resolveDisplayItems(listOf(burger), preferences).single()

        assertEquals("Friday Burgers", item.displayName)
        assertEquals(FaveCategory.FOODS, item.category)
        assertEquals(GemPalette.AMETHYST, item.palette)
        assertTrue(item.isFavorite)
    }

    @Test fun unknownStoredIdsCannotCreatePhantomCatalogItems() {
        val preferences = UserPreferences(
            favoriteIds = setOf("retired_item"),
            overrides = mapOf(
                "retired_item" to FavoriteOverride(
                    itemId = "retired_item",
                    displayName = "Phantom",
                ),
            ),
        )

        val items = resolveDisplayItems(listOf(burger), preferences)

        assertEquals(listOf(burger.id), items.map { it.id })
        assertFalse(items.single().isFavorite)
    }
}
