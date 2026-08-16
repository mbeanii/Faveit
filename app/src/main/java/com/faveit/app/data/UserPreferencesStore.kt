package com.faveit.app.data

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.faveit.app.model.FavoriteOverride
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.userDataStore by preferencesDataStore(
    name = "faveit_preferences",
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
)

data class UserPreferences(
    val favoriteIds: Set<String> = emptySet(),
    val overrides: Map<String, FavoriteOverride> = emptyMap(),
    val setupComplete: Boolean = false,
    val remindersEnabled: Boolean = false,
    val notificationPermissionDenied: Boolean = false,
)

class UserPreferencesStore(context: Context) {
    private val dataStore = context.applicationContext.userDataStore

    val preferences: Flow<UserPreferences> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map(::decode)

    suspend fun current(): UserPreferences = preferences.first()

    suspend fun toggleFavorite(itemId: String) {
        dataStore.edit { values ->
            val current = values[FAVORITES].orEmpty()
            values[FAVORITES] = if (itemId in current) current - itemId else current + itemId
        }
    }

    suspend fun addFavorite(itemId: String) {
        dataStore.edit { values -> values[FAVORITES] = values[FAVORITES].orEmpty() + itemId }
    }

    suspend fun removeFavorite(itemId: String) {
        dataStore.edit { values ->
            values[FAVORITES] = values[FAVORITES].orEmpty() - itemId
            values[OVERRIDES] = values[OVERRIDES].orEmpty().filterNot {
                FavoriteOverrideCodec.decode(it)?.itemId == itemId
            }.toSet()
        }
    }

    suspend fun saveOverride(override: FavoriteOverride) {
        dataStore.edit { values ->
            val withoutItem = values[OVERRIDES].orEmpty().filterNot {
                FavoriteOverrideCodec.decode(it)?.itemId == override.itemId
            }.toMutableSet()
            if (!override.isDefault) withoutItem += FavoriteOverrideCodec.encode(override)
            values[OVERRIDES] = withoutItem
        }
    }

    suspend fun resetOverride(itemId: String) {
        dataStore.edit { values ->
            values[OVERRIDES] = values[OVERRIDES].orEmpty().filterNot {
                FavoriteOverrideCodec.decode(it)?.itemId == itemId
            }.toSet()
        }
    }

    suspend fun completeSetup() {
        dataStore.edit { it[SETUP_COMPLETE] = true }
    }

    suspend fun setRemindersEnabled(enabled: Boolean) {
        dataStore.edit { it[REMINDERS_ENABLED] = enabled }
    }

    suspend fun markNotificationPermissionDenied() {
        dataStore.edit { it[NOTIFICATION_PERMISSION_DENIED] = true }
    }

    private fun decode(values: Preferences): UserPreferences {
        val overrides = values[OVERRIDES].orEmpty().mapNotNull(FavoriteOverrideCodec::decode)
            .associateBy { it.itemId }
        return UserPreferences(
            favoriteIds = values[FAVORITES].orEmpty(),
            overrides = overrides,
            setupComplete = values[SETUP_COMPLETE] ?: false,
            remindersEnabled = values[REMINDERS_ENABLED] ?: false,
            notificationPermissionDenied = values[NOTIFICATION_PERMISSION_DENIED] ?: false,
        )
    }

    private companion object {
        val FAVORITES = stringSetPreferencesKey("favorite_ids")
        val OVERRIDES = stringSetPreferencesKey("favorite_overrides")
        val SETUP_COMPLETE = booleanPreferencesKey("setup_complete")
        val REMINDERS_ENABLED = booleanPreferencesKey("reminders_enabled")
        val NOTIFICATION_PERMISSION_DENIED =
            booleanPreferencesKey("notification_permission_denied")
    }
}
