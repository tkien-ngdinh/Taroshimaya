package com.taroshimaya.app.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.Style
import androidx.compose.ui.graphics.vector.ImageVector
import com.taroshimaya.app.R

/**
 * Top-level route contract for the 5 independent feature packages (KI-03 -> TA-05).
 * Only scalar route strings / IDs cross module boundaries (DI-01).
 */
sealed class AppRoute(
    val route: String,
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    data object SessionAi : AppRoute(
        route = "session_ai",
        labelRes = R.string.nav_session,
        icon = Icons.Outlined.AutoAwesome,
    )

    data object JournalInsights : AppRoute(
        route = "journal_insights",
        labelRes = R.string.nav_journal,
        icon = Icons.AutoMirrored.Outlined.MenuBook,
    )

    data object AuthDeck : AppRoute(
        route = "auth_deck",
        labelRes = R.string.nav_decks,
        icon = Icons.Outlined.Style,
    )

    data object ReaderCommunity : AppRoute(
        route = "reader_community",
        labelRes = R.string.nav_community,
        icon = Icons.Outlined.Forum,
    )

    data object EconomyShop : AppRoute(
        route = "economy_shop",
        labelRes = R.string.nav_shop,
        icon = Icons.Outlined.Storefront,
    )

    companion object {
        val topLevelRoutes: List<AppRoute> = listOf(
            SessionAi,
            JournalInsights,
            AuthDeck,
            ReaderCommunity,
            EconomyShop,
        )
    }
}
