package com.taroshimaya.app.features.session_ai

import com.taroshimaya.app.core.di.DefaultAppContainer
import com.taroshimaya.app.features.session_ai.data.FakeDailyDrawRepository
import com.taroshimaya.app.features.session_ai.data.FakeSessionReadingRepository
import com.taroshimaya.app.features.session_ai.domain.ArcanaType
import com.taroshimaya.app.features.session_ai.domain.CardOrientation
import com.taroshimaya.app.features.session_ai.domain.DailyDraw
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

class D4ContractJsonTest {

    private lateinit var readingRepository: FakeSessionReadingRepository
    private lateinit var dailyDrawRepository: FakeDailyDrawRepository

    @Before
    fun setup() {
        readingRepository = FakeSessionReadingRepository()
        dailyDrawRepository = FakeDailyDrawRepository()
    }

    @Test
    fun testFakeSessionReadingRepository_initialSeeds_contains3ValidSessions() = runTest {
        val result = readingRepository.getRecentReadings("fake-user-001", limit = 10)
        assertTrue(result.isSuccess)
        val list = result.getOrThrow()
        assertEquals(3, list.size)

        // Session 1: Love spread with complete AI interpretation
        val s1 = list.first { it.id == "reading_mock_001_love" }
        assertEquals("spread_love_3", s1.spreadId)
        assertEquals(3, s1.cards.size)
        assertNotNull(s1.aiInterpretation)
        assertEquals("gemini-2.5-flash", s1.aiInterpretation?.model)
        assertEquals(3, s1.aiInterpretation?.cardInterpretations?.size)
        assertTrue(s1.isFavorite)

        // Session 2: Career spread without AI yet, contains reversed card
        val s2 = list.first { it.id == "reading_mock_002_career" }
        assertEquals("spread_career_3", s2.spreadId)
        assertEquals(3, s2.cards.size)
        assertNull(s2.aiInterpretation)
        assertTrue(s2.cards.any { it.isReversed && it.orientation == CardOrientation.REVERSED })
        assertFalse(s2.isFavorite)

        // Session 3: Quick Yes/No spread with accuracy rating after 1 week
        val s3 = list.first { it.id == "reading_mock_003_decision" }
        assertEquals("spread_yes_no_1", s3.spreadId)
        assertEquals(1, s3.cards.size)
        assertEquals(ArcanaType.MAJOR, s3.cards[0].arcanaType)
        assertEquals(5, s3.accuracyRating)
        assertEquals("2026-11-17T04:00:00Z", s3.ratedAtUtcIso)
    }

    @Test
    fun testFiltering_bySpreadAndTags() = runTest {
        val userId = "fake-user-001"

        // Filter by spreadId
        val loveResult = readingRepository.getReadingsFiltered(userId, spreadId = "spread_love_3")
        assertTrue(loveResult.isSuccess)
        assertEquals(1, loveResult.getOrThrow().size)

        // Filter by tag
        val tagResult = readingRepository.getReadingsFiltered(userId, tag = "hoc_tap")
        assertTrue(tagResult.isSuccess)
        val tagList = tagResult.getOrThrow()
        assertEquals(1, tagList.size)
        assertEquals("reading_mock_003_decision", tagList[0].id)

        // Filter by non-existent tag
        val nonExistentResult = readingRepository.getReadingsFiltered(userId, tag = "non_existent_tag")
        assertTrue(nonExistentResult.isSuccess)
        assertTrue(nonExistentResult.getOrThrow().isEmpty())
    }

    @Test
    fun testUpdateReflection_updatesNotesAndAccuracyWithDynamicTimestamp() = runTest {
        val readingId = "reading_mock_002_career"
        val before = Instant.now()

        val updateResult = readingRepository.updateReadingReflection(
            readingId = readingId,
            userNotes = "Đã thảo luận với team lead, quyết định nhận thêm module kiến trúc!",
            tags = listOf("su_nghiep", "quyet_dinh_moi"),
            moodTag = "Tự tin",
            accuracyRating = 4,
        )
        val after = Instant.now()
        assertTrue(updateResult.isSuccess)

        val updated = readingRepository.getReadingById(readingId).getOrThrow()
        assertEquals("Đã thảo luận với team lead, quyết định nhận thêm module kiến trúc!", updated.userNotes)
        assertEquals(listOf("su_nghiep", "quyet_dinh_moi"), updated.tags)
        assertEquals("Tự tin", updated.moodTag)
        assertEquals(4, updated.accuracyRating)
        assertNotNull(updated.ratedAtUtcIso)
        assertNotEquals("2026-11-17T14:00:00Z", updated.updatedAtUtcIso)
        assertNotEquals("2026-11-17T14:00:00Z", updated.ratedAtUtcIso)

        val updatedInstant = Instant.parse(updated.updatedAtUtcIso)
        val ratedInstant = Instant.parse(updated.ratedAtUtcIso)
        assertFalse(updatedInstant.isBefore(before))
        assertFalse(updatedInstant.isAfter(after))
        assertEquals(updatedInstant, ratedInstant)
    }

    @Test
    fun testToggleFavorite_updatesFlagAndDynamicTimestamp() = runTest {
        val readingId = "reading_mock_002_career"
        assertFalse(readingRepository.getReadingById(readingId).getOrThrow().isFavorite)

        val before = Instant.now()
        readingRepository.toggleFavorite(readingId, isFavorite = true)
        val after = Instant.now()

        val toggled = readingRepository.getReadingById(readingId).getOrThrow()
        assertTrue(toggled.isFavorite)
        assertNotEquals("2026-11-17T14:00:00Z", toggled.updatedAtUtcIso)
        val updatedInstant = Instant.parse(toggled.updatedAtUtcIso)
        assertFalse(updatedInstant.isBefore(before))
        assertFalse(updatedInstant.isAfter(after))

        readingRepository.toggleFavorite(readingId, isFavorite = false)
        assertFalse(readingRepository.getReadingById(readingId).getOrThrow().isFavorite)
    }

    @Test
    fun testDeleteReading_softDeletesAndExcludesFromQueriesAndStream() = runTest {
        val userId = "fake-user-001"
        val readingId = "reading_mock_002_career"

        val deleteResult = readingRepository.deleteReading(readingId)
        assertTrue(deleteResult.isSuccess)

        // Cannot retrieve soft-deleted reading
        assertTrue(readingRepository.getReadingById(readingId).isFailure)

        // Excluded from recent readings, filtered queries, and live stream
        val recent = readingRepository.getRecentReadings(userId, limit = 10).getOrThrow()
        assertEquals(2, recent.size)
        assertTrue(recent.none { it.id == readingId })

        val filtered = readingRepository.getReadingsFiltered(userId, spreadId = "spread_career_3").getOrThrow()
        assertTrue(filtered.isEmpty())

        val streamList = readingRepository.getReadingsStream(userId).first()
        assertEquals(2, streamList.size)
        assertTrue(streamList.none { it.id == readingId })

        // Re-deleting or mutating a soft-deleted reading returns failure
        assertTrue(readingRepository.deleteReading(readingId).isFailure)
        assertTrue(readingRepository.deleteReading("non_existent_id").isFailure)
        assertTrue(readingRepository.toggleFavorite(readingId, isFavorite = true).isFailure)
        assertTrue(
            readingRepository.updateReadingReflection(
                readingId = readingId,
                userNotes = "Should fail",
                tags = null,
                moodTag = null,
                accuracyRating = null,
            ).isFailure,
        )
    }

    @Test
    fun testReadingsStream_emitsLiveUpdates() = runTest {
        val stream = readingRepository.getReadingsStream("fake-user-001")
        val initialList = stream.first()
        assertEquals(3, initialList.size)
    }

    @Test
    fun testDailyDrawRepository_deterministicKeyAndRetrieval() = runTest {
        val userId = "fake-user-001"
        val dateKey = "2026-11-17"

        val result = dailyDrawRepository.getTodayDailyDraw(userId, dateKey)
        assertTrue(result.isSuccess)
        val dailyDraw = result.getOrThrow()
        assertNotNull(dailyDraw)
        assertEquals("rider_waite", dailyDraw?.deckId)
        assertEquals("major_17_star", dailyDraw?.cardId)
        assertEquals("Ngôi Sao", dailyDraw?.cardNameVi)
        assertEquals(7, dailyDraw?.streakCount)
        assertEquals(20, dailyDraw?.coinReward)
        assertEquals("${userId}_$dateKey", dailyDraw?.id)

        // Non-existent date
        val absentResult = dailyDrawRepository.getTodayDailyDraw(userId, "2026-11-18")
        assertTrue(absentResult.isSuccess)
        assertNull(absentResult.getOrThrow())
    }

    @Test
    fun testDailyDrawRepository_getDailyDrawsHistory_sortedDescendingAndRespectsLimit() = runTest {
        val userId = "fake-user-001"

        // Initial seed has 1 draw (2026-11-17)
        val initialHistory = dailyDrawRepository.getDailyDrawsHistory(userId).getOrThrow()
        assertEquals(1, initialHistory.size)
        assertEquals("2026-11-17", initialHistory[0].dateLocalKey)

        // Save additional dates, another user's draw, and a soft-deleted draw
        val extraDraws = listOf(
            createSampleDailyDraw(userId = userId, dateKey = "2026-11-15", cardId = "major_00_fool", streak = 5),
            createSampleDailyDraw(userId = userId, dateKey = "2026-11-18", cardId = "major_19_sun", streak = 8),
            createSampleDailyDraw(userId = userId, dateKey = "2026-11-16", cardId = "major_01_magician", streak = 6),
            createSampleDailyDraw(userId = "other-user-999", dateKey = "2026-11-18", cardId = "major_06_lovers", streak = 1),
            createSampleDailyDraw(
                userId = userId,
                dateKey = "2026-11-14",
                cardId = "major_02_high_priestess",
                streak = 4,
                isDeleted = true,
            ),
        )
        for (draw in extraDraws) {
            assertTrue(dailyDrawRepository.saveDailyDraw(draw).isSuccess)
        }

        val fullHistory = dailyDrawRepository.getDailyDrawsHistory(userId, limit = 30).getOrThrow()
        assertEquals(
            listOf("2026-11-18", "2026-11-17", "2026-11-16", "2026-11-15"),
            fullHistory.map { it.dateLocalKey },
        )

        val limitedHistory = dailyDrawRepository.getDailyDrawsHistory(userId, limit = 2).getOrThrow()
        assertEquals(listOf("2026-11-18", "2026-11-17"), limitedHistory.map { it.dateLocalKey })
    }

    private fun createSampleDailyDraw(
        userId: String,
        dateKey: String,
        cardId: String,
        streak: Int,
        isDeleted: Boolean = false,
    ): DailyDraw {
        return DailyDraw(
            id = "${userId}_$dateKey",
            userId = userId,
            dateLocalKey = dateKey,
            deckId = "rider_waite",
            cardId = cardId,
            cardNameVi = "Lá mẫu $dateKey",
            cardNameEn = "Sample $dateKey",
            arcanaType = ArcanaType.MAJOR,
            suit = null,
            isReversed = false,
            orientation = CardOrientation.UPRIGHT,
            dailyMessageVi = "Thông điệp ngày $dateKey",
            dailyMessageEn = "Daily message $dateKey",
            streakCount = streak,
            coinReward = 10,
            drawnAtUtcIso = "${dateKey}T01:30:00Z",
            createdAtUtcIso = "${dateKey}T01:30:00Z",
            updatedAtUtcIso = "${dateKey}T01:30:00Z",
            isDeleted = isDeleted,
            deletedAtUtcIso = if (isDeleted) "${dateKey}T02:00:00Z" else null,
        )
    }

    @Test
    fun testAppContainer_providesD4Repositories() {
        val container = DefaultAppContainer()
        assertNotNull(container.sessionReadingRepository)
        assertNotNull(container.dailyDrawRepository)
    }
}
