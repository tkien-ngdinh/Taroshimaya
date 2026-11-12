package com.taroshimaya.app

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.taroshimaya.app.core.data.AppLanguagePreference
import com.taroshimaya.app.core.designsystem.TaroshimayaTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppNavigationTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: NavHostController

    @Before
    fun showShell() {
        composeRule.setContent {
            val controller = rememberNavController()
            navController = controller
            TaroshimayaTheme {
                TaroshimayaShell(
                    languagePreference = AppLanguagePreference.SYSTEM,
                    onSelectLanguage = {},
                    navController = controller,
                )
            }
        }
    }

    @Test
    fun eachTabSelectsItsOwnRouteAndScreen() {
        assertCurrentScreen(R.string.nav_session, "session_ai", R.string.module_session_title)

        val tabs = listOf(
            Triple(R.string.nav_journal, "journal_insights", R.string.module_journal_title),
            Triple(R.string.nav_decks, "auth_deck", R.string.module_auth_deck_title),
            Triple(R.string.nav_community, "reader_community", R.string.module_community_title),
            Triple(R.string.nav_shop, "economy_shop", R.string.module_shop_title),
            Triple(R.string.nav_session, "session_ai", R.string.module_session_title),
        )
        for ((labelRes, route, titleRes) in tabs) {
            tab(labelRes).performClick()
            assertCurrentScreen(labelRes, route, titleRes)
        }
    }

    @Test
    fun reselectingCurrentTabKeepsItsEntryAndBackReturnsToReading() {
        tab(R.string.nav_journal).performClick()
        tab(R.string.nav_community).performClick()
        val entry = composeRule.runOnIdle { checkNotNull(navController.currentBackStackEntry) }

        repeat(3) { tab(R.string.nav_community).performClick() }

        composeRule.runOnIdle {
            assertSame(entry, navController.currentBackStackEntry)
        }
        pressBack()
        assertCurrentScreen(R.string.nav_session, "session_ai", R.string.module_session_title)
    }

    private fun tab(@StringRes labelRes: Int) =
        composeRule.onNode(isSelectable() and hasText(composeRule.activity.getString(labelRes)))

    private fun assertCurrentScreen(
        @StringRes labelRes: Int,
        expectedRoute: String,
        @StringRes titleRes: Int,
    ) {
        tab(labelRes).assertIsSelected()
        composeRule.onAllNodes(isSelected()).assertCountEquals(1)
        composeRule.onNodeWithText(composeRule.activity.getString(titleRes)).assertIsDisplayed()
        composeRule.runOnIdle {
            assertEquals(expectedRoute, navController.currentDestination?.route)
        }
    }
}
