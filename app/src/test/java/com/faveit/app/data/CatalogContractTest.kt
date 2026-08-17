package com.faveit.app.data

import com.faveit.app.model.FaveCategory
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogContractTest {
    private val source by lazy {
        sequenceOf(
            File("src/main/assets/catalog.json"),
            File("app/src/main/assets/catalog.json"),
        ).first { it.isFile }.readText()
    }

    private val objects by lazy {
        source.lineSequence().filter { it.startsWith("{") }.toList()
    }

    @Test fun bundledCatalogHasTwoHundredTenUniqueSpecificItemsPerCategory() {
        val ids = Regex("\\\"id\\\":\\\"([^\\\"]+)\\\"").findAll(source)
            .map { it.groupValues[1] }
            .toList()
        assertEquals(1_680, ids.size)
        assertEquals(ids.size, ids.distinct().size)

        FaveCategory.entries.forEach { category ->
            val categoryObjects = objects.filter {
                it.contains("\"category\":\"${category.wireName}\"")
            }
            assertEquals(
                "Expected 210 bundled entries for ${category.wireName}",
                210,
                categoryObjects.size,
            )
            assertEquals(
                "Expected 21 useful facets for ${category.wireName}",
                21,
                categoryObjects.map { objectLine ->
                    Regex("\\\"facet\\\":\\\"([^\\\"]+)\\\"")
                        .find(objectLine)!!.groupValues[1]
                }.distinct().size,
            )
            assertEquals(
                (1..210).toList(),
                categoryObjects.map { objectLine ->
                    Regex("\\\"popularity\\\":(\\d+)")
                        .find(objectLine)!!.groupValues[1].toInt()
                }.sorted(),
            )
        }

        assertEquals(1_680, Regex("\\\"tags\\\":\\[").findAll(source).count())
        assertEquals(1_680, Regex("\\\"facet\\\":\\\"").findAll(source).count())
    }

    @Test fun shippedSpecificFavoriteIdsRemainStableAcrossCatalogUpgrade() {
        listOf(
            "movie_princess_bride",
            "movie_into_spider_verse",
            "movie_everything_everywhere",
            "tv_schitts_creek",
            "tv_only_murders",
            "tv_avatar_airbender",
            "tv_great_british_bake_off",
            "book_pride_prejudice",
            "book_hitchhikers_guide",
            "book_murderbot",
            "game_zelda_botw",
            "game_baldurs_gate_3",
            "activity_cooking_friends",
            "activity_museum",
            "food_dumplings",
            "food_mango",
        ).forEach { shippedId ->
            assertTrue(
                "Shipped specific favorite ID changed: $shippedId",
                source.contains("\"id\":\"$shippedId\""),
            )
        }
        assertFalse(source.contains("\"id\":\"activitie_"))
        assertTrue(
            source.contains(
                "\"id\":\"restaurant_in_n_out\",\"name\":\"In-N-Out\"," +
                    "\"category\":\"restaurants\",\"emoji\":\"🍔\"," +
                    "\"palette\":\"RUBY\"",
            ),
        )
    }

    @Test fun formerGenericFillersAreGone() {
        listOf(
            "The Sushi Spot",
            "The Taco Stand",
            "Corner Café",
            "Neighborhood Pizza",
            "Thai Kitchen",
            "Indian Table",
            "Late-night Jazz",
            "Classical Focus",
            "Indie Mix",
            "R&B Favorites",
            "Lo-fi Beats",
        ).forEach { filler ->
            assertFalse(
                "Generic filler remained: $filler",
                source.contains("\"name\":\"$filler\""),
            )
        }
    }

    @Test fun rapidAddExampleAndAliasesAreBundled() {
        assertTrue(source.contains("\"name\":\"In-N-Out\""))
        assertTrue(source.contains("\"in n out\""))
        assertTrue(source.contains("\"in and out\""))
    }
}
