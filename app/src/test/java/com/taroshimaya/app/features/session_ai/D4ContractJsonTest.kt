package com.taroshimaya.app.features.session_ai

import com.taroshimaya.app.core.di.DefaultAppContainer
import com.taroshimaya.app.features.session_ai.data.FakeDailyDrawRepository
import com.taroshimaya.app.features.session_ai.data.FakeSessionReadingRepository
import com.taroshimaya.app.features.session_ai.domain.ArcanaType
import com.taroshimaya.app.features.session_ai.domain.CardOrientation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

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
        val result = readingRepository.getRecentReadings("usr_glueck_kien_01", limit = 10)
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
        val userId = "usr_glueck_kien_01"

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
    fun testUpdateReflection_updatesNotesAndAccuracy() = runTest {
        val readingId = "reading_mock_002_career"

        val updateResult = readingRepository.updateReadingReflection(
            readingId = readingId,
            userNotes = "Đã thảo luận với team lead, quyết định nhận thêm module kiến trúc!",
            tags = listOf("su_nghiep", "quyet_dinh_moi"),
            moodTag = "Tự tin",
            accuracyRating = 4,
        )
        assertTrue(updateResult.isSuccess)

        val updated = readingRepository.getReadingById(readingId).getOrThrow()
        assertEquals("Đã thảo luận với team lead, quyết định nhận thêm module kiến trúc!", updated.userNotes)
        assertEquals(listOf("su_nghiep", "quyet_dinh_moi"), updated.tags)
        assertEquals("Tự tin", updated.moodTag)
        assertEquals(4, updated.accuracyRating)
        assertNotNull(updated.ratedAtUtcIso)
    }

    @Test
    fun testToggleFavorite() = runTest {
        val readingId = "reading_mock_002_career"
        assertFalse(readingRepository.getReadingById(readingId).getOrThrow().isFavorite)

        readingRepository.toggleFavorite(readingId, isFavorite = true)
        assertTrue(readingRepository.getReadingById(readingId).getOrThrow().isFavorite)

        readingRepository.toggleFavorite(readingId, isFavorite = false)
        assertFalse(readingRepository.getReadingById(readingId).getOrThrow().isFavorite)
    }

    @Test
    fun testReadingsStream_emitsLiveUpdates() = runTest {
        val stream = readingRepository.getReadingsStream("usr_glueck_kien_01")
        val initialList = stream.first()
        assertEquals(3, initialList.size)
    }

    @Test
    fun testDailyDrawRepository_deterministicKeyAndRetrieval() = runTest {
        val userId = "usr_glueck_kien_01"
        val dateKey = "2026-11-17"

        val result = dailyDrawRepository.getTodayDailyDraw(userId, dateKey)
        assertTrue(result.isSuccess)
        val dailyDraw = result.getOrThrow()
        assertNotNull(dailyDraw)
        assertEquals("major_17_the_star", dailyDraw?.cardId)
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
    fun testAppContainer_providesD4Repositories() {
        val container = DefaultAppContainer()
        assertNotNull(container.sessionReadingRepository)
        assertNotNull(container.dailyDrawRepository)
    }
}
