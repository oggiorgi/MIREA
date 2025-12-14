package com.example.firstapplication.presentation.ui.screen

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.firstapplication.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationListDetailTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun навигация_List_Detail_List() {
        composeTestRule.onNodeWithText("Список задач")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Купить молоко")
            .assertIsDisplayed()
            .performClick()

        // вместо "Детали задачи"
        composeTestRule.onNodeWithTag("detailScreen")
            .assertIsDisplayed()

        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }

        composeTestRule.onNodeWithText("Список задач")
            .assertIsDisplayed()
    }
}