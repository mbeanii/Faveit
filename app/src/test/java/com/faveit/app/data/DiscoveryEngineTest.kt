package com.faveit.app.data

import com.faveit.app.model.CatalogItem
import com.faveit.app.model.FaveCategory
import com.faveit.app.model.GemPalette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DiscoveryEngineTest {
    private fun item(
        id: String,
        facet: String,
        tags: Set<String>,
        popularity: Int,
    ) = CatalogItem(
        id = id,
        name = id,
        category = FaveCategory.BOOKS,
        emoji = "📚",
        palette = GemPalette.EMERALD,
        facet = facet,
        tags = tags + FaveCategory.BOOKS.wireName,
        popularity = popularity,
    )

    @Test fun initialBatchIsSmallPopularAndFacetDiverse() {
        val catalog = (1..20).map { index ->
            item(
                id = "book_$index",
                facet = "genre_$index",
                tags = setOf("genre_$index"),
                popularity = index,
            )
        }

        val initial = DiscoveryEngine.initial(catalog, FaveCategory.BOOKS)

        assertEquals(DiscoveryEngine.BATCH_SIZE, initial.size)
        assertEquals(DiscoveryEngine.BATCH_SIZE, initial.map { it.facet }.distinct().size)
        assertEquals((1..DiscoveryEngine.BATCH_SIZE).toList(), initial.map { it.popularity })
    }

    @Test fun adaptiveBatchIsHalfNeighborsHalfExplorationAndStable() {
        val favorite = item(
            id = "favorite",
            facet = "space opera",
            tags = setOf("science fiction", "space", "adventure"),
            popularity = 1,
        )
        val neighbors = (1..10).map { index ->
            item(
                id = "neighbor_$index",
                facet = "neighbor_facet_$index",
                tags = setOf("science fiction", "space"),
                popularity = index + 1,
            )
        }
        val exploration = (1..12).map { index ->
            item(
                id = "explore_$index",
                facet = "explore_facet_$index",
                tags = setOf("history", "biography"),
                popularity = index + 20,
            )
        }
        val catalog = listOf(favorite) + neighbors + exploration

        val first = DiscoveryEngine.next(
            items = catalog,
            category = FaveCategory.BOOKS,
            favorites = listOf(favorite),
            excludedIds = setOf(favorite.id),
            batch = 1,
        )
        val repeated = DiscoveryEngine.next(
            items = catalog,
            category = FaveCategory.BOOKS,
            favorites = listOf(favorite),
            excludedIds = setOf(favorite.id),
            batch = 1,
        )

        assertEquals(DiscoveryEngine.BATCH_SIZE, first.size)
        assertEquals(first.map { it.id }, repeated.map { it.id })
        assertEquals(
            6,
            first.count { DiscoveryEngine.similarity(it, favorite) > 0.0 },
        )
        assertEquals(
            6,
            first.count { DiscoveryEngine.similarity(it, favorite) == 0.0 },
        )
        assertTrue(first.none { it.id == favorite.id })
    }

    @Test fun compatibilityOnlyItemsNeverEnterDiscovery() {
        val visible = item("visible", "visible", setOf("history"), 1)
        val hidden = item("hidden", "hidden", setOf("history"), 2)
            .copy(discoverable = false)
        assertEquals(
            listOf("visible"),
            DiscoveryEngine.initial(listOf(hidden, visible), FaveCategory.BOOKS)
                .map { it.id },
        )
        assertTrue(
            DiscoveryEngine.next(
                items = listOf(hidden, visible),
                category = FaveCategory.BOOKS,
                favorites = emptyList(),
                excludedIds = setOf(visible.id),
                batch = 1,
            ).isEmpty(),
        )
    }

    @Test fun categoryMembershipAloneDoesNotCreateFalseNeighbors() {
        val fantasy = item("fantasy", "fantasy", setOf("magic"), 1)
        val history = item("history", "history", setOf("nonfiction"), 2)

        assertEquals(0.0, DiscoveryEngine.similarity(fantasy, history), 0.0)
    }
}
