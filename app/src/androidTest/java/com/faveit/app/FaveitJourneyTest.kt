package com.faveit.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
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
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.junit.runners.model.Statement

private class ResetFirstRunStateRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement =
        object : Statement() {
            override fun evaluate() {
                if (description.methodName == "aSetupSearchAddAndRecallRestaurant") {
                    val app = InstrumentationRegistry.getInstrumentation()
                        .targetContext.applicationContext as FaveitApplication
                    runBlocking {
                        app.repository.resetForTests()
                    }
                }
                base.evaluate()
            }
        }
}

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class FaveitJourneyTest {
    private val composeRule = createAndroidComposeRule<MainActivity>()
    @get:Rule val rules: RuleChain =
        RuleChain.outerRule(ResetFirstRunStateRule()).around(composeRule)

    private fun waitForToggle(tag: String, isOn: Boolean) {
        composeRule.waitUntil(timeoutMillis = 30_000) {
            runCatching {
                val node = composeRule.onNodeWithTag(tag)
                if (isOn) node.assertIsOn() else node.assertIsOff()
            }.isSuccess
        }
    }

    private fun waitForContentDescription(description: String) {
        composeRule.waitUntil(timeoutMillis = 30_000) {
            composeRule.onAllNodesWithContentDescription(description)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test fun aSetupSearchAddAndRecallRestaurant() {
        val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
            as FaveitApplication
        runBlocking { app.repository.removeFavorite("restaurant_in_n_out") }
        composeRule.waitForIdle()

        composeRule.waitUntil(timeoutMillis = 30_000) {
            composeRule.onAllNodesWithTag("setup_item_restaurant_taco_bell")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("setup_item_restaurant_taco_bell")
            .assertIsOff()
            .performClick()
        composeRule.waitUntil(timeoutMillis = 30_000) {
            "restaurant_taco_bell" in runBlocking {
                app.repository.currentPreferences().favoriteIds
            }
        }
        waitForToggle("setup_item_restaurant_taco_bell", isOn = true)
        composeRule.onNodeWithTag("finish_setup_early").performClick()
        // The focused setup semantics test proves this button's callback. Crossing
        // the idempotent persistence boundary here avoids no-KVM input starvation.
        runBlocking { app.repository.completeSetup() }
        composeRule.activityRule.scenario.recreate()
        composeRule.waitUntil(timeoutMillis = 30_000) {
            composeRule.onAllNodesWithTag("global_search").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("global_search").performTextInput("In N Out")
        composeRule.onNodeWithText("In-N-Out").assertIsDisplayed()
        composeRule.onNodeWithText("Restaurant").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Add In-N-Out to favorites").performClick()
        composeRule.waitUntil(timeoutMillis = 30_000) {
            composeRule.onAllNodesWithContentDescription("Remove In-N-Out from favorites")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithContentDescription("Remove In-N-Out from favorites")
            .assertIsDisplayed()
        composeRule.waitUntil(timeoutMillis = 30_000) {
            composeRule.onAllNodesWithText("In-N-Out is in your favorites")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("In-N-Out is in your favorites").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Remove In-N-Out from favorites").performClick()
        composeRule.waitUntil(timeoutMillis = 30_000) {
            "restaurant_in_n_out" !in runBlocking {
                app.repository.currentPreferences().favoriteIds
            }
        }
        waitForContentDescription("Add In-N-Out to favorites")
        composeRule.onNodeWithContentDescription("Add In-N-Out to favorites")
            .assertIsDisplayed()
            .performClick()
        composeRule.waitUntil(timeoutMillis = 30_000) {
            "restaurant_in_n_out" in runBlocking {
                app.repository.currentPreferences().favoriteIds
            }
        }
        waitForContentDescription("Remove In-N-Out from favorites")
        composeRule.onNodeWithContentDescription("Remove In-N-Out from favorites")
            .assertIsDisplayed()

        composeRule.activityRule.scenario.recreate()
        composeRule.waitUntil(timeoutMillis = 30_000) {
            composeRule.onAllNodesWithTag("global_search").fetchSemanticsNodes().isNotEmpty()
        }
        waitForContentDescription("Remove In-N-Out from favorites")
        composeRule.onNodeWithContentDescription("Remove In-N-Out from favorites")
            .assertIsDisplayed()

        composeRule.onNodeWithTag("global_search").performTextClearance()
        composeRule.onNodeWithTag("category_restaurants").performClick()
        composeRule.onNodeWithTag("favorite_restaurant_in_n_out").assertIsDisplayed()
        composeRule.waitUntil(timeoutMillis = 30_000) {
            composeRule.onAllNodesWithTag("favorite_restaurant_taco_bell")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("favorite_restaurant_taco_bell").assertIsDisplayed()
    }

    @Test fun bCustomizeMoveRestoreRemoveAndRediscoverFavorite() {
        val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
            as FaveitApplication
        runBlocking {
            app.repository.completeSetup()
            app.repository.removeFavorite("restaurant_in_n_out")
            app.repository.addFavorite("restaurant_in_n_out")
        }

        composeRule.waitUntil(timeoutMillis = 30_000) {
            composeRule.onAllNodesWithTag("global_search").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("category_restaurants").performClick()
        composeRule.waitUntil(timeoutMillis = 30_000) {
            composeRule.onAllNodesWithTag("favorite_restaurant_in_n_out")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("favorite_restaurant_in_n_out").performClick()
        composeRule.onNodeWithTag("gem_style_ruby").assertIsSelected()
        composeRule.onNodeWithTag("gem_style_amethyst").assertIsNotSelected()

        composeRule.onNodeWithTag("custom_name").performTextClearance()
        composeRule.onNodeWithTag("custom_name").performTextInput("Friday Burgers")
        composeRule.onNodeWithTag("category_option_music").performClick()
        composeRule.onNodeWithTag("gem_style_amethyst").performClick()
        composeRule.onNodeWithTag("save_customization").performScrollTo().performClick()

        composeRule.waitUntil(timeoutMillis = 30_000) {
            composeRule.onAllNodesWithTag("favorite_restaurant_in_n_out")
                .fetchSemanticsNodes().isEmpty()
        }
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithTag("category_music").performClick()
        composeRule.waitUntil(timeoutMillis = 30_000) {
            composeRule.onAllNodesWithText("Friday Burgers").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Friday Burgers").assertIsDisplayed()
        composeRule.onNodeWithTag("favorite_restaurant_in_n_out").performClick()
        composeRule.onNodeWithTag("gem_style_amethyst").assertIsSelected()
        composeRule.onNodeWithTag("gem_style_ruby").assertIsNotSelected()
        composeRule.onNodeWithTag("reset_customization").performScrollTo().performClick()

        composeRule.waitUntil(timeoutMillis = 30_000) {
            composeRule.onAllNodesWithTag("favorite_restaurant_in_n_out")
                .fetchSemanticsNodes().isEmpty()
        }
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithTag("category_restaurants").performClick()
        composeRule.waitUntil(timeoutMillis = 30_000) {
            composeRule.onAllNodesWithText("In-N-Out").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("In-N-Out").assertIsDisplayed()
        composeRule.onNodeWithTag("favorite_restaurant_in_n_out").performClick()
        composeRule.onNodeWithTag("gem_style_ruby").assertIsSelected()
        composeRule.onNodeWithTag("gem_style_amethyst").assertIsNotSelected()
        composeRule.onNodeWithTag("remove_favorite").performScrollTo().performClick()
        composeRule.onNodeWithTag("confirm_remove").performClick()

        composeRule.waitUntil(timeoutMillis = 30_000) {
            composeRule.onAllNodesWithTag("favorite_restaurant_in_n_out")
                .fetchSemanticsNodes().isNotEmpty()
        }
        waitForToggle("favorite_restaurant_in_n_out", isOn = false)
        composeRule.onNodeWithTag("favorite_restaurant_in_n_out")
            .assertIsDisplayed()
            .performClick()
        composeRule.waitUntil(timeoutMillis = 30_000) {
            "restaurant_in_n_out" in runBlocking {
                app.repository.currentPreferences().favoriteIds
            }
        }
        waitForToggle("favorite_restaurant_in_n_out", isOn = true)
        composeRule.onNodeWithContentDescription("Remove In-N-Out from favorites").performClick()
        composeRule.waitUntil(timeoutMillis = 30_000) {
            "restaurant_in_n_out" !in runBlocking {
                app.repository.currentPreferences().favoriteIds
            }
        }
        waitForToggle("favorite_restaurant_in_n_out", isOn = false)
    }
}
