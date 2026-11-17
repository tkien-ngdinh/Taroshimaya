package com.taroshimaya.app.features.session_ai.domain

import kotlinx.coroutines.flow.Flow

/**
 * Interface contract D4 for Daily Draw (F05 & F24 Glance Widget).
 */
interface DailyDrawRepository {
    /** Get today's daily draw for the given user and local date key (YYYY-MM-DD) */
    suspend fun getTodayDailyDraw(userId: String, dateLocalKey: String): Result<DailyDraw?>

    /** Real-time stream of today's draw */
    fun getTodayDailyDrawStream(userId: String, dateLocalKey: String): Flow<DailyDraw?>

    /** Save today's daily draw */
    suspend fun saveDailyDraw(dailyDraw: DailyDraw): Result<Unit>
}
