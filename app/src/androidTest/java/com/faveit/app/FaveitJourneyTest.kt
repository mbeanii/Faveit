package com.faveit.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FaveitJourneyTest {
    @get:Rule val composeRule = createAndroidComposeRule<MainActivity>()

    @Test fun setupSearchAddAndRecallRestaurant() {
        val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
            as FaveitApplication
        runBlocking { app.repository.removeFavorite("restaurant_in_n_out") }
        composeRule.waitForIdle()

        var selectedDuringSetup = false
        if (composeRule.onAllNodesWithText("Skip").fetchSemanticsNodes().isNotEmpty()) {
            composeRule.onNodeWithTag("setup_item_restaurant_shake_shack")
                .assertIsOff()
                .performClick()
            composeRule.waitUntil(timeoutMillis = 10_000) {
                "restaurant_shake_shack" in runBlocking {
                    app.repository.currentPreferences().favoriteIds
                }
            }
            composeRule.onNodeWithTag("setup_item_restaurant_shake_shack").assertIsOn()
            selectedDuringSetup = true
            composeRule.onNodeWithText("Skip").performClick()
        }

        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithTag("global_search").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithTag("global_search").performTextInput("In N Out")
        composeRule.onNodeWithText("In-N-Out").assertIsDisplayed()
        composeRule.onNodeWithText("Restaurant").assertIsDisplayed()
        if (composeRule.onAllNodesWithContentDescription("Add favorite").fetchSemanticsNodes().isNotEmpty()) {
            composeRule.onNodeWithContentDescription("Add favorite").performClick()
        }
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithContentDescription("Already a favorite").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithContentDescription("Already a favorite").assertIsDisplayed()

        composeRule.onNodeWithTag("global_search").performTextClearance()
        composeRule.onNodeWithTag("category_restaurants").performClick()
        composeRule.onNodeWithTag("favorite_restaurant_in_n_out").assertIsDisplayed()
        if (selectedDuringSetup) {
            composeRule.waitUntil(timeoutMillis = 10_000) {
                composeRule.onAllNodesWithTag("favorite_restaurant_shake_shack")
                    .fetchSemanticsNodes().isNotEmpty()
            }
            composeRule.onNodeWithTag("favorite_restaurant_shake_shack").assertIsDisplayed()
        }
    }

    @Test fun customizeMoveRestoreRemoveAndRediscoverFavorite() {
        val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
            as FaveitApplication
        runBlocking {
            app.repository.completeSetup()
            app.repository.removeFavorite("restaurant_in_n_out")
            app.repository.addFavorite("restaurant_in_n_out")
        }

        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithTag("global_search").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("category_restaurants").performClick()
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithTag("favorite_restaurant_in_n_out")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("favorite_restaurant_in_n_out").performClick()

        composeRule.onNodeWithTag("custom_name").performTextClearance()
        composeRule.onNodeWithTag("custom_name").performTextInput("Friday Burgers")
        composeRule.onNodeWithTag("category_option_music").performClick()
        composeRule.onNodeWithTag("gem_style_amethyst").performClick()
        composeRule.onNodeWithTag("save_customization").performScrollTo().performClick()

        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithTag("favorite_restaurant_in_n_out")
                .fetchSemanticsNodes().isEmpty()
        }
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithTag("category_music").performClick()
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithText("Friday Burgers").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Friday Burgers").assertIsDisplayed()
        composeRule.onNodeWithTag("favorite_restaurant_in_n_out").performClick()
        composeRule.onNodeWithTag("reset_customization").performScrollTo().performClick()

        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithTag("favorite_restaurant_in_n_out")
                .fetchSemanticsNodes().isEmpty()
        }
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithTag("category_restaurants").performClick()
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithText("In-N-Out").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("In-N-Out").assertIsDisplayed()
        composeRule.onNodeWithTag("favorite_restaurant_in_n_out").performClick()
        composeRule.onNodeWithTag("remove_favorite").performScrollTo().performClick()
        composeRule.onNodeWithTag("confirm_remove").performClick()

        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithTag("discover_restaurant_in_n_out")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("discover_restaurant_in_n_out").assertIsDisplayed()
    }
}
