package com.taroshimaya.app

import com.taroshimaya.app.core.auth.UserRole
import com.taroshimaya.app.core.data.DataSourceMode
import com.taroshimaya.app.core.di.DefaultAppContainer
import com.taroshimaya.app.core.navigation.AppRoute
import com.taroshimaya.app.features.auth_deck.data.FakeAuthDeckDataSource
import com.taroshimaya.app.features.economy_shop.data.FakeEconomyShopDataSource
import com.taroshimaya.app.features.journal_insights.data.FakeJournalInsightsDataSource
import com.taroshimaya.app.features.reader_community.data.FakeReaderCommunityDataSource
import com.taroshimaya.app.features.session_ai.data.FakeSessionAiDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppContainerAndNavigationTest {

    @Test
    fun topLevelRoutes_coversAllFiveFeaturePackagesWithUniqueRoutes() {
        val routes = AppRoute.topLevelRoutes
        assertEquals(5, routes.size)
        assertEquals(5, routes.map { it.route }.toSet().size)
        assertEquals(
            listOf(
                "session_ai",
                "journal_insights",
                "auth_deck",
                "reader_community",
                "economy_shop",
            ),
            routes.map { it.route },
        )
    }

    @Test
    fun defaultAppContainer_defaultsToFakeModeAndSupportsAllRoles() = runTest {
        val container = DefaultAppContainer()
        assertEquals(DataSourceMode.FAKE, container.dataSourceMode)

        val initialSession = container.authRepository.currentSession.value
        assertNotNull(initialSession)
        assertFalse(initialSession!!.isSignedIn)
        assertEquals(UserRole.USER, initialSession.role)

        for (role in UserRole.entries) {
            container.authRepository.signInWithRole(role)
            val updated = container.authRepository.currentSession.value
            assertNotNull(updated)
            assertTrue(updated!!.isSignedIn)
            assertEquals(role, updated.role)
        }

        container.authRepository.signOut()
        val signedOut = container.authRepository.currentSession.value
        assertNotNull(signedOut)
        assertFalse(signedOut!!.isSignedIn)
    }

    @Test
    fun featureFakeDataSources_initializeWithoutAndroidContext() {
        assertEquals(3, FakeAuthDeckDataSource().availableDeckCount())
        assertEquals(3, FakeSessionAiDataSource().supportedSpreadCount())
        assertEquals(0, FakeJournalInsightsDataSource().sampleEntryCount())
        assertEquals(0, FakeReaderCommunityDataSource().samplePostCount())
        assertEquals(0, FakeEconomyShopDataSource().starterCoinBalance())
    }
}
