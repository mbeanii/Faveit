package com.faveit.app.data

import com.faveit.app.model.CatalogItem
import com.faveit.app.model.FaveCategory
import kotlin.math.absoluteValue

/**
 * Transparent, deterministic, on-device content-based nearest-neighbor discovery.
 * The user's favorites are the only personalization signal; there is no tracking.
 */
object DiscoveryEngine {
    const val BATCH_SIZE = 12

    fun initial(
        items: List<CatalogItem>,
        category: FaveCategory,
        limit: Int = BATCH_SIZE,
    ): List<CatalogItem> {
        val candidates = items.asSequence()
            .filter { it.category == category }
            .sortedBy { it.popularity }
            .toList()
        val diverse = candidates.distinctBy { it.facet }.take(limit)
        return (diverse + candidates.filterNot { candidate -> diverse.any { it.id == candidate.id } })
            .take(limit)
    }

    fun next(
        items: List<CatalogItem>,
        category: FaveCategory,
        favorites: List<CatalogItem>,
        excludedIds: Set<String>,
        batch: Int,
        limit: Int = BATCH_SIZE,
    ): List<CatalogItem> {
        if (limit <= 0) return emptyList()
        val candidates = items.filter {
            it.category == category && it.id !in excludedIds && favorites.none { favorite ->
                favorite.id == it.id
            }
        }
        if (candidates.isEmpty()) return emptyList()

        val relatedQuota = if (favorites.isEmpty()) 0 else (limit + 1) / 2
        val scoredCandidates = candidates.map { candidate ->
            candidate to (favorites.maxOfOrNull { favorite ->
                similarity(candidate, favorite)
            } ?: 0.0)
        }
        val related = scoredCandidates.asSequence()
            .filter { it.second > 0.0 }
            .sortedWith(compareByDescending<Pair<CatalogItem, Double>> { it.second }
                .thenBy { it.first.popularity })
            .map { it.first }
            .takeBalanced(relatedQuota, maxPerFacet = 2)

        val relatedIds = related.mapTo(mutableSetOf()) { it.id }
        val exploration = scoredCandidates.asSequence()
            .filterNot { it.first.id in relatedIds }
            .sortedWith(
                compareBy<Pair<CatalogItem, Double>> { if (it.second == 0.0) 0 else 1 }
                    .thenBy { (it.first.popularity - 1) / 40 }
                    .thenBy { stableShuffleKey(it.first.id, batch) }
                    .thenBy { it.first.popularity },
            )
            .map { it.first }
            .takeBalanced(limit - related.size, maxPerFacet = 1)

        val result = ArrayList<CatalogItem>(limit)
        val relatedIterator = related.iterator()
        val explorationIterator = exploration.iterator()
        while (result.size < limit && (relatedIterator.hasNext() || explorationIterator.hasNext())) {
            if (relatedIterator.hasNext()) result += relatedIterator.next()
            if (result.size < limit && explorationIterator.hasNext()) result += explorationIterator.next()
        }
        if (result.size < limit) {
            result += candidates.filterNot { candidate -> result.any { it.id == candidate.id } }
                .sortedBy { it.popularity }
                .take(limit - result.size)
        }
        return result
    }

    fun similarity(left: CatalogItem, right: CatalogItem): Double {
        if (left.id == right.id) return 1.0
        val leftFeatures = left.tags - left.category.wireName
        val rightFeatures = right.tags - right.category.wireName
        val intersection = leftFeatures.intersect(rightFeatures).size
        val union = leftFeatures.union(rightFeatures).size
        return if (union == 0) 0.0 else intersection.toDouble() / union
    }

    private fun Sequence<CatalogItem>.takeBalanced(
        count: Int,
        maxPerFacet: Int,
    ): List<CatalogItem> {
        if (count <= 0) return emptyList()
        val result = mutableListOf<CatalogItem>()
        val facetCounts = mutableMapOf<String, Int>()
        val deferred = mutableListOf<CatalogItem>()
        for (item in this) {
            if ((facetCounts[item.facet] ?: 0) < maxPerFacet) {
                result += item
                facetCounts[item.facet] = (facetCounts[item.facet] ?: 0) + 1
                if (result.size == count) break
            } else {
                deferred += item
            }
        }
        if (result.size < count) result += deferred.take(count - result.size)
        return result
    }

    private fun stableShuffleKey(id: String, batch: Int): Int =
        (id.hashCode() xor (batch * -0x61c88647)).absoluteValue
}
