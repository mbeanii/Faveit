package com.faveit.app.data

import android.content.Context
import com.faveit.app.model.CatalogItem
import com.faveit.app.model.FaveCategory
import com.faveit.app.model.GemPalette
import org.json.JSONArray

class CatalogLoader(private val context: Context) {
    fun load(): List<CatalogItem> {
        val source = context.assets.open("catalog.json").bufferedReader().use { it.readText() }
        val array = JSONArray(source)
        return buildList(array.length()) {
            repeat(array.length()) { index ->
                val item = array.getJSONObject(index)
                val aliasesJson = item.optJSONArray("aliases")
                val tagsJson = item.optJSONArray("tags")
                val category = FaveCategory.fromWire(item.getString("category"))
                add(
                    CatalogItem(
                        id = item.getString("id"),
                        name = item.getString("name"),
                        category = category,
                        emoji = item.getString("emoji"),
                        palette = item.optString("palette").takeIf { it.isNotBlank() }
                            ?.let(GemPalette::valueOf)
                            ?: category.defaultPalette,
                        aliases = buildList {
                            if (aliasesJson != null) repeat(aliasesJson.length()) {
                                add(aliasesJson.getString(it))
                            }
                        },
                        facet = item.optString("facet").ifBlank { category.wireName },
                        tags = buildSet {
                            add(category.wireName)
                            if (tagsJson != null) repeat(tagsJson.length()) {
                                add(tagsJson.getString(it))
                            }
                        },
                        popularity = item.optInt("popularity", Int.MAX_VALUE),
                    ),
                )
            }
        }.also { items ->
            require(items.map { it.id }.distinct().size == items.size) { "Catalog IDs must be unique" }
            require(FaveCategory.entries.all { category -> items.any { it.category == category } }) {
                "Every category must have catalog items"
            }
            require(items.all { it.facet.isNotBlank() && it.tags.isNotEmpty() }) {
                "Every catalog item must provide recommendation features"
            }
            FaveCategory.entries.forEach { category ->
                val ranks = items.filter { it.category == category }.map { it.popularity }
                require(ranks.toSet().size == ranks.size && ranks.minOrNull() == 1) {
                    "Popularity ranks must be unique and begin at one for ${category.wireName}"
                }
            }
        }
    }
}
