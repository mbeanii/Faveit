package com.faveit.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.faveit.app.data.FaveitSnapshot
import com.faveit.app.data.SearchRanker
import com.faveit.app.model.DisplayItem
import com.faveit.app.model.FavoriteOverride
import com.faveit.app.notifications.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FaveitUiState(
    val loaded: Boolean = false,
    val snapshot: FaveitSnapshot,
)

class FaveitViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as FaveitApplication).repository
    private val initialSnapshot = FaveitSnapshot(
        catalog = repository.catalog,
        items = repository.catalog.map { source ->
            DisplayItem(source, source.name, source.category, source.palette, false)
        },
        setupComplete = false,
        remindersEnabled = false,
        notificationPermissionRequested = false,
    )

    val uiState: StateFlow<FaveitUiState> = repository.snapshot
        .map { FaveitUiState(loaded = true, snapshot = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FaveitUiState(snapshot = initialSnapshot),
        )

    fun search(query: String, snapshot: FaveitSnapshot): List<DisplayItem> {
        val searchableCatalog = snapshot.items.map { item ->
            item.source.copy(
                name = item.displayName,
                aliases = item.source.aliases + item.source.name,
            )
        }
        val rankedIds = SearchRanker.search(query, searchableCatalog).map { it.id }
        val byId = snapshot.items.associateBy { it.id }
        return rankedIds.mapNotNull(byId::get)
    }

    fun toggleFavorite(itemId: String) = viewModelScope.launch {
        repository.toggleFavorite(itemId)
    }

    fun addFavorite(itemId: String) = viewModelScope.launch {
        repository.addFavorite(itemId)
    }

    fun removeFavorite(itemId: String) = viewModelScope.launch {
        repository.removeFavorite(itemId)
    }

    fun saveOverride(override: FavoriteOverride) = viewModelScope.launch {
        repository.saveOverride(override)
    }

    fun resetOverride(itemId: String) = viewModelScope.launch {
        repository.resetOverride(itemId)
    }

    fun completeSetup() = viewModelScope.launch {
        repository.completeSetup()
    }

    fun setRemindersEnabled(enabled: Boolean) = viewModelScope.launch {
        repository.setRemindersEnabled(enabled)
        if (enabled) ReminderScheduler.schedule(getApplication())
        else ReminderScheduler.cancel(getApplication())
    }

    fun markNotificationPermissionRequested() = viewModelScope.launch {
        repository.markNotificationPermissionRequested()
    }
}
