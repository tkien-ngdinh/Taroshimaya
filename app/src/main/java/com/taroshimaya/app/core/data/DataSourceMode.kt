package com.taroshimaya.app.core.data

/**
 * Application-wide data source selector configured at app startup (AppContainer).
 * Per DI-01: UI Composables must never branch on FAKE vs FIREBASE directly.
 */
enum class DataSourceMode {
    FAKE,
    FIREBASE,
}
