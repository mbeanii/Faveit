package com.faveit.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.faveit.app.model.CatalogItem
import com.faveit.app.model.DisplayItem
import com.faveit.app.model.FaveCategory
import com.faveit.app.model.GemPalette
import com.faveit.app.ui.screens.HomeScreen
import com.faveit.app.ui.screens.SetupScreen
import com.faveit.app.ui.theme.FaveitTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FaveitSemanticsTest {
    @get:Rule val composeRule = createComposeRule()

    private val restaurant = CatalogItem(
        id = "restaurant_shake_shack",
        name = "Shake Shack",
        category = FaveCategory.RESTAURANTS,
        emoji = "🍔",
        palette = GemPalette.EMERALD,
    )
    private val music = CatalogItem(
        id = "music_jazz",
        name = "Jazz",
        category = FaveCategory.MUSIC,
        emoji = "🎷",
        palette = GemPalette.AMETHYST,
    )

    @Test fun setupSelectionIsAToggleAndProgressesByCategory() {
        var page by mutableIntStateOf(0)
        var items by mutableStateOf(
            listOf(restaurant, music).map { source ->
                DisplayItem(source, source.name, source.category, source.palette, false)
            },
        )

        composeRule.setContent {
            FaveitTheme {
                SetupScreen(
                    page = page,
                    items = items,
                    favoriteCount = items.count { it.isFavorite },
                    onPage = { page = it },
                    onToggle = { selectedId ->
                        items = items.map { item ->
                            if (item.id == selectedId) item.copy(isFavorite = !item.isFavorite)
                            else item
                        }
                    },
                    onFinish = {},
                )
            }
        }

        composeRule.onNodeWithTag("setup_item_restaurant_shake_shack")
            .assertIsOff()
            .performClick()
            .assertIsOn()
        composeRule.onNodeWithTag("next_setup").performClick()
        composeRule.onNodeWithTag("setup_grid_music").assertIsDisplayed()
        composeRule.onNodeWithTag("setup_item_music_jazz").assertIsOff()
    }

    @Test fun rapidAddAndReminderControlsAnnounceTheirState() {
        var favorite by mutableStateOf(false)
        var remindersEnabled by mutableStateOf(false)

        composeRule.setContent {
            val result = DisplayItem(
                source = restaurant,
                displayName = restaurant.name,
                category = restaurant.category,
                palette = restaurant.palette,
                isFavorite = favorite,
            )
            FaveitTheme {
                HomeScreen(
                    query = "Shake Shack",
                    onQueryChange = {},
                    results = listOf(result),
                    favoriteCounts = emptyMap(),
                    remindersEnabled = remindersEnabled,
                    onCategory = {},
                    onAdd = { favorite = true },
                    onReminders = { remindersEnabled = true },
                )
            }
        }

        composeRule.onNodeWithTag("search_result_restaurant_shake_shack").assert(
            SemanticsMatcher.expectValue(
                SemanticsProperties.StateDescription,
                "Not a favorite",
            ),
        )
        composeRule.onNodeWithTag("add_restaurant_shake_shack").performClick()
        composeRule.onNodeWithTag("search_result_restaurant_shake_shack").assert(
            SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Favorite"),
        )
        composeRule.onNodeWithContentDescription("Favorite reminders, off").performClick()
        composeRule.onNodeWithContentDescription("Favorite reminders, on").assertIsDisplayed()
    }
}
