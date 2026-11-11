package com.taroshimaya.app.features.journal_insights.domain

/**
 * Package 3 contract stub (D10 Design tokens & shared UI live in core/designsystem & core/ui, owned by Diệp).
 * Reads D4 (readings & daily_draws) and D5 (reminders) from Package 2 via interfaces.
 */
interface JournalRepositoryContract {
    fun sampleEntryCount(): Int
}
