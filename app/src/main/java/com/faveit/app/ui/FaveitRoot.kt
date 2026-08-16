package com.faveit.app.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.faveit.app.FaveitViewModel
import com.faveit.app.model.DisplayItem
import com.faveit.app.model.FaveCategory
import com.faveit.app.ui.screens.CategoryScreen
import com.faveit.app.ui.screens.FavoriteEditorSheet
import com.faveit.app.ui.screens.HomeScreen
import com.faveit.app.ui.screens.ReminderDialog
import com.faveit.app.ui.screens.SetupScreen
import com.faveit.app.ui.theme.DeepInk
import com.faveit.app.ui.theme.Ink

private const val HOME = "home"
private const val CATEGORY = "category"

@Composable
fun FaveitRoot(viewModel: FaveitViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var destination by rememberSaveable { mutableStateOf(HOME) }
    var selectedCategoryName by rememberSaveable { mutableStateOf(FaveCategory.RESTAURANTS.name) }
    var setupPage by rememberSaveable { mutableIntStateOf(0) }
    var query by rememberSaveable { mutableStateOf("") }
    var managedItemId by rememberSaveable { mutableStateOf<String?>(null) }
    var showReminderDialog by rememberSaveable { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) viewModel.setRemindersEnabled(true)
        showReminderDialog = false
    }

    val notificationPermissionGranted = Build.VERSION.SDK_INT < 33 ||
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED

    val background = Brush.verticalGradient(listOf(Ink, DeepInk, Ink))
    Box(
        Modifier.fillMaxSize().background(background).systemBarsPadding().imePadding(),
    ) {
        if (!uiState.loaded) {
            Text(
                "Faveit",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.align(Alignment.Center),
            )
            return@Box
        }

        val snapshot = uiState.snapshot
        if (!snapshot.setupComplete) {
            SetupScreen(
                page = setupPage,
                items = snapshot.items,
                favoriteCount = snapshot.favorites.size,
                onPage = { setupPage = it.coerceIn(FaveCategory.entries.indices) },
                onToggle = viewModel::toggleFavorite,
                onFinish = {
                    viewModel.completeSetup()
                    destination = HOME
                },
            )
        } else if (destination == CATEGORY) {
            val category = FaveCategory.valueOf(selectedCategoryName)
            CategoryScreen(
                category = category,
                items = snapshot.items,
                onBack = { destination = HOME },
                onAdd = viewModel::addFavorite,
                onManage = { managedItemId = it.id },
            )
            BackHandler { destination = HOME }
        } else {
            val results = remember(query, snapshot.items) {
                viewModel.search(query, snapshot)
            }
            HomeScreen(
                query = query,
                onQueryChange = { query = it },
                results = results,
                favoriteCounts = snapshot.favorites.groupingBy { it.category }.eachCount(),
                remindersEnabled = snapshot.remindersEnabled && notificationPermissionGranted,
                onCategory = {
                    selectedCategoryName = it.name
                    destination = CATEGORY
                },
                onAdd = viewModel::addFavorite,
                onReminders = { showReminderDialog = true },
            )
        }

        val managedItem: DisplayItem? = managedItemId?.let { id ->
            snapshot.items.firstOrNull { it.id == id && it.isFavorite }
        }
        if (managedItem != null) FavoriteEditorSheet(
            item = managedItem,
            onDismiss = { managedItemId = null },
            onSave = viewModel::saveOverride,
            onReset = viewModel::resetOverride,
            onRemove = viewModel::removeFavorite,
        )

        if (showReminderDialog) ReminderDialog(
            enabled = snapshot.remindersEnabled,
            permissionGranted = notificationPermissionGranted,
            onDismiss = { showReminderDialog = false },
            onDisable = {
                viewModel.setRemindersEnabled(false)
                showReminderDialog = false
            },
            onEnable = {
                if (Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS,
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    viewModel.setRemindersEnabled(true)
                    showReminderDialog = false
                } else {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            },
            onRecoverPermission = {
                val intent = if (Build.VERSION.SDK_INT >= 26) {
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(
                        Settings.EXTRA_APP_PACKAGE,
                        context.packageName,
                    )
                } else {
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:${context.packageName}"),
                    )
                }
                context.startActivity(intent)
                showReminderDialog = false
            },
        )
    }
}
