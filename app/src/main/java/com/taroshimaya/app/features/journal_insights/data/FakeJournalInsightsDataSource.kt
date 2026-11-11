package com.taroshimaya.app.features.journal_insights.data

import com.taroshimaya.app.features.journal_insights.domain.JournalRepositoryContract

/**
 * Phase 0–1 Fake starter implementation for Package 3.
 */
class FakeJournalInsightsDataSource : JournalRepositoryContract {
    override fun sampleEntryCount(): Int = 0
}
