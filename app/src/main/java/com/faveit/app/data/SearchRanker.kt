package com.faveit.app.data

import com.faveit.app.model.CatalogItem
import java.text.Normalizer
import java.util.Locale

internal data class IndexedSearchText(val whole: String, val tokens: List<String>)

/** Immutable search index built once when the bundled catalog loads. */
class CatalogSearchIndex(items: List<CatalogItem>) {
    private data class IndexedItem(
        val item: CatalogItem,
        val name: IndexedSearchText,
        val aliases: List<IndexedSearchText>,
    )

    private data class Match(val item: CatalogItem, val textScore: Int)

    private val indexed = items.map { item ->
        IndexedItem(
            item = item,
            name = SearchRanker.index(item.name),
            aliases = item.aliases.map(SearchRanker::index),
        )
    }

    /**
     * Searches source names and aliases without re-normalizing the master catalog.
     * Only the typically tiny map of locally customized names is normalized per query.
     */
    fun search(
        query: String,
        customNames: Map<String, String> = emptyMap(),
        eligibleUndiscoverableIds: Set<String> = emptySet(),
        limit: Int = 30,
    ): List<CatalogItem> {
        val needle = SearchRanker.index(query)
        if (needle.whole.isBlank() || limit <= 0) return emptyList()

        return indexed.mapNotNull { entry ->
            if (!entry.item.discoverable && entry.item.id !in eligibleUndiscoverableIds) {
                return@mapNotNull null
            }
            val sourceScore = SearchRanker.score(needle, entry.name, entry.aliases)
            val customScore = customNames[entry.item.id]
                ?.let(SearchRanker::index)
                ?.let { SearchRanker.score(needle, it, listOf(entry.name)) }
            listOfNotNull(sourceScore, customScore).minOrNull()?.let { Match(entry.item, it) }
        }.sortedWith(
            compareBy<Match> { it.textScore }
                .thenBy { it.item.popularity }
                .thenBy { it.item.name.lowercase(Locale.ROOT) },
        ).take(limit).map { it.item }
    }
}

object SearchRanker {
    private val combiningMarks = Regex("\\p{M}+")
    private val separators = Regex("[^\\p{L}\\p{N}\\p{S}]+")
    private val whitespace = Regex("\\s+")

    fun normalize(value: String): String = Normalizer.normalize(value, Normalizer.Form.NFKD)
        .replace(combiningMarks, "")
        .lowercase(Locale.ROOT)
        .replace(separators, " ")
        .trim()
        .replace(whitespace, " ")

    internal fun index(value: String): IndexedSearchText {
        val normalized = normalize(value)
        return IndexedSearchText(
            whole = normalized,
            tokens = normalized.split(' ').filter(String::isNotBlank),
        )
    }

    internal fun score(
        needle: IndexedSearchText,
        name: IndexedSearchText,
        aliases: List<IndexedSearchText>,
    ): Int? {
        val candidates = listOf(name) + aliases
        return when {
            name.whole == needle.whole -> 0
            aliases.any { it.whole == needle.whole } -> 1
            name.whole.startsWith(needle.whole) -> 2
            aliases.any { it.whole.startsWith(needle.whole) } -> 3
            needle.tokens.all { query -> candidates.any { candidate ->
                candidate.tokens.any { it.startsWith(query) }
            } } -> 4
            candidates.any { it.whole.contains(needle.whole) } -> 5
            else -> null
        }
    }

    /** Convenience for tests and small callers. Production holds a [CatalogSearchIndex]. */
    fun search(query: String, items: List<CatalogItem>, limit: Int = 30): List<CatalogItem> =
        CatalogSearchIndex(items).search(query, limit = limit)
}
