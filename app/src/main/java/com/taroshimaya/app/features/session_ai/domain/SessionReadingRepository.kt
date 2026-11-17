package com.taroshimaya.app.features.session_ai.domain

import kotlinx.coroutines.flow.Flow

/**
 * Interface contract D4 between Package 2 (Kiên - provider) and Package 3 (Diệp - consumer).
 * Implemented as FakeSessionReadingRepository in Phase 1 (KI-05), Firestore in Phase 2 (KI-12).
 */
interface SessionReadingRepository {
    /** Get a single reading by its ID (F09, F10, F11) */
    suspend fun getReadingById(readingId: String): Result<TarotReading>

    /** Real-time stream of all readings for a specific user (F11) */
    fun getReadingsStream(userId: String): Flow<List<TarotReading>>

    /** Get recent readings up to limit count (F11 History list) */
    suspend fun getRecentReadings(userId: String, limit: Int = 20): Result<List<TarotReading>>

    /** Filter readings by date range, deckId, spreadId, or tag (F11) */
    suspend fun getReadingsFiltered(
        userId: String,
        startDateUtcIso: String? = null,
        endDateUtcIso: String? = null,
        deckId: String? = null,
        spreadId: String? = null,
        tag: String? = null,
    ): Result<List<TarotReading>>

    /** Save a new reading session (F04 - Kiên) */
    suspend fun saveReading(reading: TarotReading): Result<String>

    /** Update personal reflection: notes, tags, mood, accuracy rating (F09, F10 - Diệp) */
    suspend fun updateReadingReflection(
        readingId: String,
        userNotes: String?,
        tags: List<String>?,
        moodTag: String?,
        accuracyRating: Int?,
    ): Result<Unit>

    /** Toggle favorite bookmark (F11 - Diệp) */
    suspend fun toggleFavorite(readingId: String, isFavorite: Boolean): Result<Unit>
}
