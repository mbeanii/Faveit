package com.faveit.app.data

import com.faveit.app.model.FaveCategory
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogContractTest {
    private val source by lazy {
        sequenceOf(
            File("src/main/assets/catalog.json"),
            File("app/src/main/assets/catalog.json"),
        ).first { it.isFile }.readText()
    }

    @Test fun bundledCatalogHasTwelveUniqueItemsPerCategory() {
        val ids = Regex("\\\"id\\\":\\\"([^\\\"]+)\\\"").findAll(source)
            .map { it.groupValues[1] }
            .toList()
        assertEquals(96, ids.size)
        assertEquals(ids.size, ids.distinct().size)

        FaveCategory.entries.forEach { category ->
            assertEquals(
                "Expected 12 bundled entries for ${category.wireName}",
                12,
                Regex("\\\"category\\\":\\\"${category.wireName}\\\"").findAll(source).count(),
            )
        }
    }

    @Test fun rapidAddExampleAndAliasesAreBundled() {
        assertTrue(source.contains("\"name\":\"In-N-Out\""))
        assertTrue(source.contains("\"in n out\""))
        assertTrue(source.contains("\"in and out\""))
    }
}
