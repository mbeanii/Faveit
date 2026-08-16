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
                add(
                    CatalogItem(
                        id = item.getString("id"),
                        name = item.getString("name"),
                        category = FaveCategory.fromWire(item.getString("category")),
                        emoji = item.getString("emoji"),
                        palette = item.optString("palette").takeIf { it.isNotBlank() }
                            ?.let(GemPalette::valueOf)
                            ?: FaveCategory.fromWire(item.getString("category")).defaultPalette,
                        aliases = buildList {
                            if (aliasesJson != null) repeat(aliasesJson.length()) {
                                add(aliasesJson.getString(it))
                            }
                        },
                    ),
                )
            }
        }.also { items ->
            require(items.map { it.id }.distinct().size == items.size) { "Catalog IDs must be unique" }
            require(FaveCategory.entries.all { category -> items.any { it.category == category } }) {
                "Every category must have catalog items"
            }
        }
    }
}
