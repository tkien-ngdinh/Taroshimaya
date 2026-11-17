package com.taroshimaya.app.features.session_ai.data

import com.taroshimaya.app.features.session_ai.domain.ArcanaType
import com.taroshimaya.app.features.session_ai.domain.CardOrientation
import com.taroshimaya.app.features.session_ai.domain.DailyDraw
import com.taroshimaya.app.features.session_ai.domain.DailyDrawRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap

/**
 * Phase 0–1 in-memory stub implementation of [DailyDrawRepository] (D4).
 * Pre-populated with 1 realistic sample daily draw (The Star) for F05 & Glance Widget F24.
 */
class FakeDailyDrawRepository : DailyDrawRepository {

    private val dailyDrawsMap = ConcurrentHashMap<String, DailyDraw>()
    private val stateFlow = MutableStateFlow<Map<String, DailyDraw>>(emptyMap())

    init {
        seedInitialDailyDraw()
    }

    private fun seedInitialDailyDraw() {
        val initialDraw = DailyDraw(
            id = "fake-user-001_2026-11-17",
            userId = "fake-user-001",
            dateLocalKey = "2026-11-17",
            deckId = "rider_waite",
            cardId = "major_17_star",
            cardNameVi = "Ngôi Sao",
            cardNameEn = "The Star",
            arcanaType = ArcanaType.MAJOR,
            suit = null,
            isReversed = false,
            orientation = CardOrientation.UPRIGHT,
            dailyMessageVi = "Hôm nay mang đến nguồn năng lượng phục hồi, hy vọng và niềm tin vào con đường phía trước. Hãy giữ tâm trí tĩnh lặng và lắng nghe trực giác.",
            dailyMessageEn = "Today radiates tranquil hope, spiritual renewal, and intuitive clarity. Trust your inner beacon.",
            streakCount = 7,
            coinReward = 20,
            coinTransactionId = "fake-user-001_daily_2026-11-17",
            shareImageUrl = "https://taroshimaya.app/shares/daily/2026-11-17-the-star.png",
            drawnAtUtcIso = "2026-11-17T01:30:00Z",
            createdAtUtcIso = "2026-11-17T01:30:00Z",
            updatedAtUtcIso = "2026-11-17T01:30:00Z",
        )
        dailyDrawsMap[initialDraw.id] = initialDraw
        stateFlow.value = dailyDrawsMap.toMap()
    }

    override suspend fun getTodayDailyDraw(userId: String, dateLocalKey: String): Result<DailyDraw?> {
        val deterministicKey = "${userId}_$dateLocalKey"
        val draw = dailyDrawsMap[deterministicKey]
        return Result.success(draw)
    }

    override fun getTodayDailyDrawStream(userId: String, dateLocalKey: String): Flow<DailyDraw?> {
        val deterministicKey = "${userId}_$dateLocalKey"
        return stateFlow.map { it[deterministicKey] }
    }

    override suspend fun saveDailyDraw(dailyDraw: DailyDraw): Result<Unit> {
        dailyDrawsMap[dailyDraw.id] = dailyDraw
        stateFlow.value = dailyDrawsMap.toMap()
        return Result.success(Unit)
    }
}
