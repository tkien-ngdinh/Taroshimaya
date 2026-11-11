package com.taroshimaya.app.features.session_ai.data

import com.taroshimaya.app.features.session_ai.domain.SessionReadingContract

/**
 * Phase 0–1 Fake starter implementation for Package 2.
 */
class FakeSessionAiDataSource : SessionReadingContract {
    override fun supportedSpreadCount(): Int = 3
}
