package com.faveit.app.data

import com.faveit.app.model.CatalogItem
import com.faveit.app.model.FaveCategory
import com.faveit.app.model.GemPalette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.system.measureNanoTime

class SearchRankerTest {
    private val items = listOf(
        CatalogItem(
            id = "in_n_out",
            name = "In-N-Out",
            category = FaveCategory.RESTAURANTS,
            emoji = "🍔",
            palette = GemPalette.RUBY,
            aliases = listOf("in and out", "innout"),
        ),
        CatalogItem(
            id = "inside_out",
            name = "Inside Out",
            category = FaveCategory.MOVIES,
            emoji = "🎬",
            palette = GemPalette.SAPPHIRE,
        ),
        CatalogItem(
            id = "cafe",
            name = "Corner Café",
            category = FaveCategory.RESTAURANTS,
            emoji = "☕",
            palette = GemPalette.TOPAZ,
        ),
        CatalogItem(
            id = "sushi",
            name = "寿司",
            category = FaveCategory.RESTAURANTS,
            emoji = "🍣",
            palette = GemPalette.AMETHYST,
        ),
        CatalogItem(
            id = "gem",
            name = "💎",
            category = FaveCategory.GAMES,
            emoji = "💎",
            palette = GemPalette.SAPPHIRE,
        ),
    )

    @Test fun punctuationDoesNotBlockRapidLookup() {
        assertEquals("in_n_out", SearchRanker.search("In N Out", items).first().id)
    }

    @Test fun aliasesAreSearchable() {
        assertEquals("in_n_out", SearchRanker.search("in and out", items).first().id)
    }

    @Test fun diacriticsAreSearchableWithoutAccents() {
        assertEquals("cafe", SearchRanker.search("cafe", items).single().id)
    }

    @Test fun nonLatinDisplayNamesRemainSearchable() {
        assertEquals("sushi", SearchRanker.search("寿司", items).single().id)
    }

    @Test fun symbolOnlyDisplayNamesRemainSearchable() {
        assertEquals("gem", SearchRanker.search("💎", items).single().id)
    }

    @Test fun irrelevantQueriesReturnNothing() {
        assertTrue(SearchRanker.search("volleyball", items).isEmpty())
    }

    @Test fun reusableIndexSearchesLocalCustomNamesAndOriginalNames() {
        val index = CatalogSearchIndex(items)

        assertEquals(
            "in_n_out",
            index.search(
                query = "Friday Burgers",
                customNames = mapOf("in_n_out" to "Friday Burgers"),
            ).first().id,
        )
        assertEquals("in_n_out", index.search("In N Out").first().id)
    }

    @Test fun hiddenCompatibilityItemsAreSearchableOnlyForTheirPreviousOwner() {
        val hidden = CatalogItem(
            id = "music_jazz",
            name = "Late-night Jazz",
            category = FaveCategory.MUSIC,
            emoji = "🎷",
            palette = GemPalette.SAPPHIRE,
            aliases = listOf("smooth jazz"),
            discoverable = false,
        )
        val index = CatalogSearchIndex(items + hidden)

        assertTrue(index.search("late night jazz").isEmpty())
        assertEquals(
            hidden.id,
            index.search(
                query = "smooth jazz",
                eligibleUndiscoverableIds = setOf(hidden.id),
            ).single().id,
        )
    }

    @Test fun fullBundledCatalogSizedIndexStaysFastAcrossTypingWorkload() {
        val largeCatalog = (1..1_680).map { index ->
            CatalogItem(
                id = "catalog_$index",
                name = "Catalog Choice $index",
                category = FaveCategory.entries[index % FaveCategory.entries.size],
                emoji = "💎",
                palette = GemPalette.SAPPHIRE,
                aliases = listOf("choice number $index"),
                popularity = index,
            )
        }
        val index = CatalogSearchIndex(largeCatalog)
        repeat(20) { index.search("choice 1599") }

        val elapsedNanos = measureNanoTime {
            repeat(250) { queryIndex ->
                index.search("choice ${1 + (queryIndex % 1_680)}")
            }
        }

        assertTrue(
            "250 indexed searches took ${elapsedNanos / 1_000_000} ms",
            elapsedNanos < 1_500_000_000L,
        )
        assertEquals("catalog_1599", index.search("choice number 1599").first().id)
    }

}
