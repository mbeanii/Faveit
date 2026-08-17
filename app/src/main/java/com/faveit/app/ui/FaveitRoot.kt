package com.faveit.app.ui

import android.annotation.SuppressLint
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.faveit.app.FaveitViewModel
import com.faveit.app.model.DisplayItem
import com.faveit.app.model.FaveCategory
import com.faveit.app.notifications.FavoriteReminderWorker
import com.faveit.app.ui.components.DelightBanner
import com.faveit.app.ui.components.GemMark
import com.faveit.app.ui.components.favoriteSavedMessage
import com.faveit.app.ui.components.setupCompleteMessage
import com.faveit.app.ui.screens.CategoryScreen
import com.faveit.app.ui.screens.FavoriteEditorSheet
import com.faveit.app.ui.screens.HomeScreen
import com.faveit.app.ui.screens.ReminderDialog
import com.faveit.app.ui.screens.SetupScreen
import com.faveit.app.ui.theme.DeepInk
import com.faveit.app.ui.theme.Ink
import kotlinx.coroutines.launch

private const val HOME = "home"
private const val CATEGORY = "category"

internal fun canRequestNotificationPermission(
    isGranted: Boolean,
    hasDeniedPermission: Boolean,
    shouldShowRationale: Boolean,
): Boolean = !isGranted && (!hasDeniedPermission || shouldShowRationale)

@SuppressLint("InlinedApi")
@Composable
fun FaveitRoot(viewModel: FaveitViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = LocalActivity.current
    var destination by rememberSaveable { mutableStateOf(HOME) }
    var selectedCategoryName by rememberSaveable { mutableStateOf(FaveCategory.RESTAURANTS.name) }
    var setupPage by rememberSaveable { mutableIntStateOf(0) }
    var query by rememberSaveable { mutableStateOf("") }
    var managedItemId by rememberSaveable { mutableStateOf<String?>(null) }
    var showReminderDialog by rememberSaveable { mutableStateOf(false) }
    var notificationSettingsRevision by remember { mutableIntStateOf(0) }
    var pendingFavoriteNoticeId by rememberSaveable { mutableStateOf<String?>(null) }
    var setupNoticePending by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val noticeScope = rememberCoroutineScope()

    LifecycleResumeEffect(Unit) {
        notificationSettingsRevision += 1
        onPauseOrDispose {}
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            viewModel.setRemindersEnabled(true)
        } else if (activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    it,
                    Manifest.permission.POST_NOTIFICATIONS,
                )
            } == true
        ) {
            viewModel.markNotificationPermissionDenied()
        }
        showReminderDialog = false
        notificationSettingsRevision += 1
    }

    val notificationPermissionGranted = remember(notificationSettingsRevision) {
        Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }
    val notificationPermissionCanBeRequested = canRequestNotificationPermission(
        isGranted = notificationPermissionGranted,
        hasDeniedPermission = uiState.snapshot.notificationPermissionDenied,
        shouldShowRationale = activity?.let {
            ActivityCompat.shouldShowRequestPermissionRationale(
                it,
                Manifest.permission.POST_NOTIFICATIONS,
            )
        } == true,
    )
    val appNotificationsEnabled = remember(notificationSettingsRevision) {
        NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
    val reminderChannelEnabled = remember(notificationSettingsRevision) {
        FavoriteReminderWorker.isChannelEnabled(context)
    }
    val reminderDeliveryAvailable = notificationPermissionGranted &&
        appNotificationsEnabled && reminderChannelEnabled

    val openNotificationSettings = {
        val intent = if (
            Build.VERSION.SDK_INT >= 26 &&
            notificationPermissionGranted &&
            appNotificationsEnabled &&
            !reminderChannelEnabled
        ) {
            Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                .putExtra(Settings.EXTRA_CHANNEL_ID, FavoriteReminderWorker.CHANNEL_ID)
        } else if (Build.VERSION.SDK_INT >= 26) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(
                Settings.EXTRA_APP_PACKAGE,
                context.packageName,
            )
        } else {
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                "package:${context.packageName}".toUri(),
            )
        }
        context.startActivity(intent)
        showReminderDialog = false
    }

    LaunchedEffect(uiState.loaded) {
        if (uiState.loaded) activity?.reportFullyDrawn()
    }

    val background = Brush.verticalGradient(listOf(Ink, DeepInk, Ink))
    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
        Box(
            Modifier.fillMaxSize().background(background).systemBarsPadding().imePadding(),
        ) {
            if (!uiState.loaded) {
                Column(
                    modifier = Modifier.align(Alignment.Center).testTag("loading_brand"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    GemMark(Modifier.size(68.dp))
                    Text(
                        "Faveit",
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier.padding(top = 14.dp),
                    )
                    Text(
                        "Your taste. On demand.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                return@Box
            }

            val snapshot = uiState.snapshot
            LaunchedEffect(pendingFavoriteNoticeId, snapshot.items) {
                val pendingId = pendingFavoriteNoticeId ?: return@LaunchedEffect
                val savedItem = snapshot.items.firstOrNull {
                    it.id == pendingId && it.isFavorite
                } ?: return@LaunchedEffect
                pendingFavoriteNoticeId = null
                noticeScope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(favoriteSavedMessage(savedItem.displayName))
                }
            }
            LaunchedEffect(setupNoticePending, snapshot.setupComplete) {
                if (setupNoticePending && snapshot.setupComplete) {
                    setupNoticePending = false
                    noticeScope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(
                            setupCompleteMessage(snapshot.favorites.size),
                        )
                    }
                }
            }
            val addFavoriteWithNotice: (String) -> Unit = { itemId ->
                val item = snapshot.items.firstOrNull { it.id == itemId }
                if (item != null && !item.isFavorite) {
                    pendingFavoriteNoticeId = itemId
                    viewModel.addFavorite(itemId)
                }
            }

            if (!snapshot.setupComplete) {
                SetupScreen(
                    page = setupPage,
                    items = snapshot.items,
                    favoriteCount = snapshot.favorites.size,
                    onPage = { setupPage = it.coerceIn(FaveCategory.entries.indices) },
                    onToggle = viewModel::toggleFavorite,
                    onFinish = {
                        setupNoticePending = true
                        viewModel.completeSetup()
                        destination = HOME
                    },
                )
                BackHandler(enabled = setupPage > 0) { setupPage -= 1 }
            } else if (destination == CATEGORY) {
                val category = FaveCategory.valueOf(selectedCategoryName)
                CategoryScreen(
                    category = category,
                    items = snapshot.items,
                    onBack = { destination = HOME },
                    onAdd = addFavoriteWithNotice,
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
                    remindersEnabled = snapshot.remindersEnabled && reminderDeliveryAvailable,
                    onCategory = {
                        selectedCategoryName = it.name
                        destination = CATEGORY
                    },
                    onAdd = addFavoriteWithNotice,
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
                deliveryAvailable = reminderDeliveryAvailable,
                onDismiss = { showReminderDialog = false },
                onDisable = {
                    viewModel.setRemindersEnabled(false)
                    showReminderDialog = false
                },
                onEnable = {
                    when {
                        !notificationPermissionGranted -> {
                            if (notificationPermissionCanBeRequested) permissionLauncher.launch(
                                Manifest.permission.POST_NOTIFICATIONS,
                            ) else openNotificationSettings()
                        }
                        !appNotificationsEnabled || !reminderChannelEnabled ->
                            openNotificationSettings()
                        else -> {
                            viewModel.setRemindersEnabled(true)
                            showReminderDialog = false
                        }
                    }
                },
                onRecoverNotifications = openNotificationSettings,
            )

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) { data ->
                DelightBanner(message = data.visuals.message)
            }
        }
    }
}
