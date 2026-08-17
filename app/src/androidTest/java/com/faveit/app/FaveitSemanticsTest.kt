package com.faveit.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.faveit.app.model.CatalogItem
import com.faveit.app.model.DisplayItem
import com.faveit.app.model.FaveCategory
import com.faveit.app.model.GemPalette
import com.faveit.app.ui.components.DelightBanner
import com.faveit.app.ui.screens.HomeScreen
import com.faveit.app.ui.screens.SetupScreen
import com.faveit.app.ui.theme.FaveitTheme
import org.junit.Assert.assertTrue
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
        var finished by mutableStateOf(false)
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
                    onAdd = { selectedId ->
                        items = items.map { item ->
                            if (item.id == selectedId) item.copy(isFavorite = true) else item
                        }
                    },
                    onRemove = { selectedId ->
                        items = items.map { item ->
                            if (item.id == selectedId) item.copy(isFavorite = false) else item
                        }
                    },
                    onManage = {},
                    onFinish = { finished = true },
                )
            }
        }

        composeRule.onNodeWithTag("setup_item_restaurant_shake_shack")
            .assertIsOff()
            .performClick()
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.StateDescription,
                    "Favorite",
                ),
            )
        composeRule.onNodeWithTag("finish_setup_early").assertIsDisplayed().performClick()
        assertTrue(finished)
        composeRule.onNodeWithTag("next_setup").performClick()
        composeRule.onNodeWithTag("setup_grid_music").assertIsDisplayed()
        composeRule.onNodeWithTag("setup_item_music_jazz").assertIsOff()
    }

    @Test fun expandedSetupBatchSurvivesCategoryRoundTripAndIgnoresHiddenFavorites() {
        var page by mutableIntStateOf(0)
        val restaurants = (1..13).map { index ->
            CatalogItem(
                id = "restaurant_test_$index",
                name = "Restaurant $index",
                category = FaveCategory.RESTAURANTS,
                emoji = "🍽️",
                palette = GemPalette.RUBY,
                facet = "facet_$index",
                tags = setOf("restaurants", "facet_$index"),
                popularity = index,
            )
        }
        val musicItem = music.copy(id = "music_test", facet = "jazz", popularity = 1)
        val hiddenUpgradeFavorite = restaurant.copy(
            id = "restaurant_hidden_upgrade",
            name = "Retired favorite",
            discoverable = false,
        )
        val items = (restaurants + musicItem + hiddenUpgradeFavorite).map { source ->
            DisplayItem(
                source,
                source.name,
                source.category,
                source.palette,
                source.id == hiddenUpgradeFavorite.id,
            )
        }
        composeRule.setContent {
            FaveitTheme {
                SetupScreen(
                    page = page,
                    items = items,
                    favoriteCount = items.count(DisplayItem::isFavorite),
                    onPage = { page = it },
                    onAdd = {},
                    onRemove = {},
                    onManage = {},
                    onFinish = {},
                )
            }
        }
        composeRule.onNodeWithTag("setup_item_restaurant_hidden_upgrade")
            .performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("more_setup_restaurants").performClick()
        composeRule.onNodeWithTag("setup_item_restaurant_test_13")
            .performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("next_setup").performClick()
        composeRule.onNodeWithContentDescription("Previous category").performClick()
        composeRule.onNodeWithTag("setup_item_restaurant_test_13")
            .performScrollTo().assertIsDisplayed()
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
                    onRemove = { favorite = false },
                    onManage = {},
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
        composeRule.onNodeWithTag("favorite_toggle_restaurant_shake_shack").performClick()
        composeRule.onNodeWithTag("search_result_restaurant_shake_shack").assert(
            SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Favorite"),
        )
        composeRule.onNodeWithContentDescription("Remove Shake Shack from favorites").performClick()
        composeRule.onNodeWithTag("search_result_restaurant_shake_shack").assert(
            SemanticsMatcher.expectValue(
                SemanticsProperties.StateDescription,
                "Not a favorite",
            ),
        )
        composeRule.onNodeWithContentDescription("Favorite reminders, off").performClick()
        composeRule.onNodeWithContentDescription("Favorite reminders, on").assertIsDisplayed()
    }

    @Test fun delightBannerAnnouncesPersistedSuccess() {
        composeRule.setContent {
            FaveitTheme {
                DelightBanner("In-N-Out is in your favorites")
            }
        }

        composeRule.onNodeWithTag("delight_banner").assert(
            SemanticsMatcher.expectValue(
                SemanticsProperties.LiveRegion,
                LiveRegionMode.Polite,
            ),
        )
        composeRule.onNodeWithText("In-N-Out is in your favorites").assertIsDisplayed()
        composeRule.onNodeWithText("Yours, right when you need it.").assertIsDisplayed()
    }
}
