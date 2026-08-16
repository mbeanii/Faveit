package com.faveit.app.data

import android.content.Context
import com.faveit.app.model.CatalogItem
import com.faveit.app.model.DisplayItem
import com.faveit.app.model.FavoriteOverride
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class FaveitSnapshot(
    val catalog: List<CatalogItem>,
    val items: List<DisplayItem>,
    val setupComplete: Boolean,
    val remindersEnabled: Boolean,
    val notificationPermissionDenied: Boolean,
) {
    val favorites: List<DisplayItem> get() = items.filter { it.isFavorite }
}

internal fun resolveDisplayItems(
    catalog: List<CatalogItem>,
    preferences: UserPreferences,
): List<DisplayItem> = catalog.map { source ->
    val override = preferences.overrides[source.id]
    DisplayItem(
        source = source,
        displayName = override?.displayName ?: source.name,
        category = override?.category ?: source.category,
        palette = override?.palette ?: source.palette,
        isFavorite = source.id in preferences.favoriteIds,
    )
}

class FaveitRepository(context: Context) {
    val catalog: List<CatalogItem> = CatalogLoader(context).load()
    private val preferencesStore = UserPreferencesStore(context)

    val snapshot: Flow<FaveitSnapshot> = preferencesStore.preferences.map { preferences ->
        FaveitSnapshot(
            catalog = catalog,
            items = resolveDisplayItems(catalog, preferences),
            setupComplete = preferences.setupComplete,
            remindersEnabled = preferences.remindersEnabled,
            notificationPermissionDenied = preferences.notificationPermissionDenied,
        )
    }

    fun search(query: String): List<CatalogItem> = SearchRanker.search(query, catalog)
    suspend fun toggleFavorite(itemId: String) = preferencesStore.toggleFavorite(itemId)
    suspend fun addFavorite(itemId: String) = preferencesStore.addFavorite(itemId)
    suspend fun removeFavorite(itemId: String) = preferencesStore.removeFavorite(itemId)
    suspend fun saveOverride(override: FavoriteOverride) = preferencesStore.saveOverride(override)
    suspend fun resetOverride(itemId: String) = preferencesStore.resetOverride(itemId)
    suspend fun completeSetup() = preferencesStore.completeSetup()
    suspend fun setRemindersEnabled(enabled: Boolean) = preferencesStore.setRemindersEnabled(enabled)
    suspend fun markNotificationPermissionDenied() =
        preferencesStore.markNotificationPermissionDenied()
    suspend fun currentPreferences(): UserPreferences = preferencesStore.current()
}
