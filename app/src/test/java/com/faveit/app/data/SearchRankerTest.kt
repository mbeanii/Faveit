package com.faveit.app.data

import com.faveit.app.model.CatalogItem
import com.faveit.app.model.FaveCategory
import com.faveit.app.model.GemPalette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

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

    @Test fun irrelevantQueriesReturnNothing() {
        assertTrue(SearchRanker.search("volleyball", items).isEmpty())
    }
}
