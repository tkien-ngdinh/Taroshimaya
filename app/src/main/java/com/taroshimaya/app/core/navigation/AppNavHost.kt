package com.taroshimaya.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.taroshimaya.app.features.auth_deck.ui.AuthDeckPlaceholderScreen
import com.taroshimaya.app.features.economy_shop.ui.EconomyShopPlaceholderScreen
import com.taroshimaya.app.features.journal_insights.ui.JournalInsightsPlaceholderScreen
import com.taroshimaya.app.features.reader_community.ui.ReaderCommunityPlaceholderScreen
import com.taroshimaya.app.features.session_ai.ui.SessionAiPlaceholderScreen

/**
 * Centralized graph for the five top-level destinations (DI-01, TA-05).
 * Supplies the shell with the selected route and a callback for tab selection.
 * Confirm auth guards, deep-route arguments, and modal sheet behavior
 * with the UI reviewer as the feature flows are specified.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    layout: @Composable (
        selectedRoute: AppRoute?,
        onSelectRoute: (AppRoute) -> Unit,
        destinationContent: @Composable (Modifier) -> Unit,
    ) -> Unit,
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val selectedRoute = AppRoute.topLevelRoutes.firstOrNull { currentDestination.isInTopLevelRoute(it) }

    layout(selectedRoute, navController::navigateToTopLevelRoute) { modifier ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.SessionAi.route,
            modifier = modifier,
        ) {
            composable(AppRoute.SessionAi.route) {
                SessionAiPlaceholderScreen()
            }
            composable(AppRoute.JournalInsights.route) {
                JournalInsightsPlaceholderScreen()
            }
            composable(AppRoute.AuthDeck.route) {
                AuthDeckPlaceholderScreen()
            }
            composable(AppRoute.ReaderCommunity.route) {
                ReaderCommunityPlaceholderScreen()
            }
            composable(AppRoute.EconomyShop.route) {
                EconomyShopPlaceholderScreen()
            }
        }
    }
}

private fun NavDestination?.isInTopLevelRoute(destination: AppRoute): Boolean =
    this?.hierarchy?.any { it.route == destination.route } == true

private fun NavController.navigateToTopLevelRoute(destination: AppRoute) {
    if (currentDestination.isInTopLevelRoute(destination)) return

    // Keep the start destination below the active tab for system Back.
    navigate(destination.route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
