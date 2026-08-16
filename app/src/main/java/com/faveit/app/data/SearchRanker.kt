package com.faveit.app.data

import com.faveit.app.model.CatalogItem
import java.text.Normalizer

object SearchRanker {
    fun normalize(value: String): String = Normalizer.normalize(value, Normalizer.Form.NFKD)
        .replace(Regex("\\p{M}+"), "")
        .lowercase()
        .replace(Regex("[^\\p{L}\\p{N}\\p{S}]+"), " ")
        .trim()
        .replace(Regex("\\s+"), " ")

    fun search(query: String, items: List<CatalogItem>, limit: Int = 30): List<CatalogItem> {
        val needle = normalize(query)
        if (needle.isBlank()) return emptyList()

        return items.mapNotNull { item ->
            score(needle, item)?.let { score -> item to score }
        }.sortedWith(compareBy<Pair<CatalogItem, Int>> { it.second }.thenBy { it.first.name })
            .take(limit)
            .map { it.first }
    }

    private fun score(needle: String, item: CatalogItem): Int? {
        val name = normalize(item.name)
        val aliases = item.aliases.map(::normalize)
        val queryTokens = needle.split(' ')
        val candidateTokens = (listOf(name) + aliases).flatMap { it.split(' ') }

        return when {
            name == needle -> 0
            aliases.any { it == needle } -> 1
            name.startsWith(needle) -> 2
            aliases.any { it.startsWith(needle) } -> 3
            queryTokens.all { query -> candidateTokens.any { it.startsWith(query) } } -> 4
            name.contains(needle) || aliases.any { it.contains(needle) } -> 5
            else -> null
        }
    }
}
