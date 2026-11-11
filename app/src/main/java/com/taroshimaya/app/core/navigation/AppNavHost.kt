package com.taroshimaya.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.taroshimaya.app.core.data.AppLanguagePreference
import com.taroshimaya.app.core.data.DataSourceMode
import com.taroshimaya.app.features.auth_deck.ui.AuthDeckPlaceholderScreen
import com.taroshimaya.app.features.economy_shop.ui.EconomyShopPlaceholderScreen
import com.taroshimaya.app.features.journal_insights.ui.JournalInsightsPlaceholderScreen
import com.taroshimaya.app.features.reader_community.ui.ReaderCommunityPlaceholderScreen
import com.taroshimaya.app.features.session_ai.ui.SessionAiPlaceholderScreen

/**
 * Centralized navigation graph at app level (DI-01).
 * Expanded in TA-05 (12/11) with full auth guard, deep routes, and modal sheets.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    dataSourceMode: DataSourceMode,
    languagePreference: AppLanguagePreference,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.SessionAi.route,
        modifier = modifier,
    ) {
        composable(AppRoute.SessionAi.route) {
            SessionAiPlaceholderScreen(
                dataSourceMode = dataSourceMode,
                languagePreference = languagePreference,
            )
        }
        composable(AppRoute.JournalInsights.route) {
            JournalInsightsPlaceholderScreen(
                dataSourceMode = dataSourceMode,
                languagePreference = languagePreference,
            )
        }
        composable(AppRoute.AuthDeck.route) {
            AuthDeckPlaceholderScreen(
                dataSourceMode = dataSourceMode,
                languagePreference = languagePreference,
            )
        }
        composable(AppRoute.ReaderCommunity.route) {
            ReaderCommunityPlaceholderScreen(
                dataSourceMode = dataSourceMode,
                languagePreference = languagePreference,
            )
        }
        composable(AppRoute.EconomyShop.route) {
            EconomyShopPlaceholderScreen(
                dataSourceMode = dataSourceMode,
                languagePreference = languagePreference,
            )
        }
    }
}
