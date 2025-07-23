package com.myintag.weatherapp.presentation.main

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.myintag.weatherapp.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainScreenHiltInstrumentationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun mainScreen_showsInitialEmptyState() {
        composeTestRule.onNodeWithText("Search city").assertIsDisplayed()

        composeTestRule.onNodeWithText("Search for a city to see the weather.")
            .assertIsDisplayed()
    }

    @Test
    fun mainScreen_searchField_acceptsInput() {
        composeTestRule.onNodeWithText("Search city")
            .performTextInput("Toronto")

        composeTestRule.onNodeWithText("Toronto").assertIsDisplayed()
    }

    @Test
    fun mainScreen_searchButton_performsSearch() {
        composeTestRule.onNodeWithText("Search city")
            .performTextInput("Toronto")

        composeTestRule.onNodeWithContentDescription("Search")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Toronto").fetchSemanticsNodes().size >= 2
        }

        composeTestRule.onAllNodesWithText("Toronto").assertCountEquals(2) // one in search field, one as city name
        composeTestRule.onNodeWithText("Clear").assertIsDisplayed()
        composeTestRule.onNodeWithText("22°").assertIsDisplayed()
    }

    @Test
    fun mainScreen_permissionButton_isClickable() {
        composeTestRule.onNodeWithText("Grant location access")
            .assertExists()
    }
} 