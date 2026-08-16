package com.faveit.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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

        if (composeRule.onAllNodesWithText("Skip").fetchSemanticsNodes().isNotEmpty()) {
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
        composeRule.onNodeWithContentDescription("Already a favorite").assertIsDisplayed()

        composeRule.onNodeWithTag("global_search").performTextClearance()
        composeRule.onNodeWithTag("category_restaurants").performClick()
        composeRule.onNodeWithTag("favorite_restaurant_in_n_out").assertIsDisplayed()
    }
}
