package com.taroshimaya.app.features.session_ai

import com.taroshimaya.app.features.session_ai.domain.CardOrientation
import com.taroshimaya.app.features.session_ai.domain.F04SessionEvent
import com.taroshimaya.app.features.session_ai.domain.F04SessionStep
import com.taroshimaya.app.features.session_ai.domain.F04SessionUiState
import com.taroshimaya.app.features.session_ai.domain.F04StateMachine
import com.taroshimaya.app.features.session_ai.domain.PresetSpreads
import com.taroshimaya.app.features.session_ai.domain.SpreadCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class F04StateMachineTest {

    private lateinit var stateMachine: F04StateMachine

    @Before
    fun setUp() {
        stateMachine = F04StateMachine()
    }

    @Test
    fun testPresets_coversRequiredSpreadsWithNormalizedCoordinates() {
        val presets = PresetSpreads.allPresets
        assertTrue(presets.size >= 5)

        val categories = presets.map { it.category }.toSet()
        assertTrue(categories.contains(SpreadCategory.LOVE))
        assertTrue(categories.contains(SpreadCategory.CAREER))
        assertTrue(categories.contains(SpreadCategory.FINANCE))
        assertTrue(categories.contains(SpreadCategory.HEALING))
        assertTrue(categories.contains(SpreadCategory.QUICK_DECISION))

        // Check Love 3 cards
        val love = PresetSpreads.LOVE_3
        assertEquals(3, love.cardCount)
        assertEquals(3, love.positions.size)
        love.positions.forEach { pos ->
            assertTrue(pos.normalizedX in 0.0f..1.0f)
            assertTrue(pos.normalizedY in 0.0f..1.0f)
        }
    }

    @Test
    fun testCustomSpread_createsValidOneThreeFiveCardLayouts() {
        val custom1 = PresetSpreads.createCustomSpread(1, listOf("Trọng tâm"), listOf("Focus"))
        assertEquals(1, custom1.cardCount)
        assertEquals("Trọng tâm", custom1.positions[0].nameVi)

        val custom3 = PresetSpreads.createCustomSpread(3)
        assertEquals(3, custom3.cardCount)
        assertEquals(3, custom3.positions.size)

        val custom5 = PresetSpreads.createCustomSpread(5)
        assertEquals(5, custom5.cardCount)
        assertEquals(5, custom5.positions.size)
        assertTrue(custom5.isCustom)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCustomSpread_invalidCount_throwsException() {
        PresetSpreads.createCustomSpread(4)
    }

    @Test
    fun testHappyPath_fullLifecycle_fromIdleToReadingReady() {
        var state = F04SessionUiState()
        assertEquals(F04SessionStep.IDLE, state.step)

        // 1. Init
        state = stateMachine.reduce(state, F04SessionEvent.InitSession)
        assertEquals(F04SessionStep.SETUP_QUESTION, state.step)

        // 2. Question
        state = stateMachine.reduce(state, F04SessionEvent.UpdateQuestion("Học kỳ này công việc của tôi thế nào?"))
        state = stateMachine.reduce(state, F04SessionEvent.SubmitQuestion)
        assertEquals(F04SessionStep.SELECT_DECK, state.step)
        assertNull(state.questionError)

        // 3. Select Deck
        state = stateMachine.reduce(state, F04SessionEvent.SelectDeck(deckId = "rider_waite", isUnlocked = true))
        assertEquals(F04SessionStep.SELECT_SPREAD, state.step)
        assertEquals("rider_waite", state.selectedDeckId)

        // 4. Select Spread (Love 3 cards)
        state = stateMachine.reduce(state, F04SessionEvent.SelectSpread(PresetSpreads.LOVE_3))
        assertEquals(F04SessionStep.CONFIRMED, state.step)
        assertEquals(3, state.expectedCardCount)

        // 5. Shuffling
        state = stateMachine.reduce(state, F04SessionEvent.StartShuffling)
        assertEquals(F04SessionStep.SHUFFLING, state.step)

        state = stateMachine.reduce(state, F04SessionEvent.FinishShuffling)
        assertEquals(F04SessionStep.DECK_FAN_OUT, state.step)

        // 6. Drawing 3 cards
        state = stateMachine.reduce(state, F04SessionEvent.DrawCard("major_00_fool"))
        assertEquals(F04SessionStep.DRAWING_CARD, state.step)
        assertEquals(1, state.drawnCardCount)

        state = stateMachine.reduce(state, F04SessionEvent.DrawCard("major_01_magician"))
        assertEquals(F04SessionStep.DRAWING_CARD, state.step)
        assertEquals(2, state.drawnCardCount)

        state = stateMachine.reduce(state, F04SessionEvent.DrawCard("major_02_high_priestess", CardOrientation.REVERSED))
        assertEquals(F04SessionStep.CARDS_PLACED, state.step)
        assertEquals(3, state.drawnCardCount)
        assertTrue(state.hasDrawnAllCards)
        assertFalse(state.isAllRevealed)

        // 7. Flipping cards
        state = stateMachine.reduce(state, F04SessionEvent.FlipCard(0))
        assertEquals(F04SessionStep.FLIPPING_CARD, state.step)
        state = stateMachine.reduce(state, F04SessionEvent.FinishFlip(0))
        assertEquals(F04SessionStep.CARDS_PLACED, state.step)
        assertEquals(1, state.revealedCardCount)

        state = stateMachine.reduce(state, F04SessionEvent.FlipCard(1))
        state = stateMachine.reduce(state, F04SessionEvent.FinishFlip(1))
        assertEquals(F04SessionStep.CARDS_PLACED, state.step)

        state = stateMachine.reduce(state, F04SessionEvent.FlipCard(2))
        state = stateMachine.reduce(state, F04SessionEvent.FinishFlip(2))
        assertEquals(F04SessionStep.ALL_REVEALED, state.step)
        assertTrue(state.isAllRevealed)

        // 8. Auto-save trigger
        state = stateMachine.reduce(state, F04SessionEvent.TriggerAutoSave)
        assertEquals(F04SessionStep.SAVING_READING, state.step)

        // 9. Save success
        val sampleReadingId = "reading_20261115_001"
        state = stateMachine.reduce(state, F04SessionEvent.SaveReadingSuccess(sampleReadingId))
        assertEquals(F04SessionStep.READING_READY, state.step)
        assertEquals(sampleReadingId, state.readingId)
        assertNull(state.saveError)
        assertFalse(state.isOfflineSaved)
    }

    @Test
    fun testQuestionValidation_lengthGuards() {
        var state = F04SessionUiState(step = F04SessionStep.SETUP_QUESTION)

        // Too short (< 5 chars)
        state = stateMachine.reduce(state, F04SessionEvent.UpdateQuestion("Abc"))
        state = stateMachine.reduce(state, F04SessionEvent.SubmitQuestion)
        assertEquals(F04SessionStep.SETUP_QUESTION, state.step)
        assertNotNull(state.questionError)

        // Valid length
        state = stateMachine.reduce(state, F04SessionEvent.UpdateQuestion("Tương lai công việc của tôi"))
        state = stateMachine.reduce(state, F04SessionEvent.SubmitQuestion)
        assertEquals(F04SessionStep.SELECT_DECK, state.step)
        assertNull(state.questionError)
    }

    @Test
    fun testDeckSelection_lockedDeck_rejected() {
        var state = F04SessionUiState(step = F04SessionStep.SELECT_DECK)

        state = stateMachine.reduce(state, F04SessionEvent.SelectDeck("pop_art_deck", isUnlocked = false))
        assertEquals(F04SessionStep.SELECT_DECK, state.step)
        assertNotNull(state.saveError)

        state = stateMachine.reduce(state, F04SessionEvent.SelectDeck("rider_waite", isUnlocked = true))
        assertEquals(F04SessionStep.SELECT_SPREAD, state.step)
        assertNull(state.saveError)
    }

    @Test
    fun testSaveFailure_handlesOfflineFallback() {
        var state = F04SessionUiState(step = F04SessionStep.SAVING_READING)

        state = stateMachine.reduce(
            state,
            F04SessionEvent.SaveReadingFailure(
                errorMessage = "Network timeout",
                fallbackOfflineSaved = true,
            ),
        )
        assertEquals(F04SessionStep.READING_READY, state.step)
        assertTrue(state.isOfflineSaved)
        assertEquals("Network timeout", state.saveError)
    }

    @Test
    fun testResetSession_returnsToIdle() {
        val state = F04SessionUiState(step = F04SessionStep.READING_READY, readingId = "xyz")
        val reset = stateMachine.reduce(state, F04SessionEvent.ResetSession)
        assertEquals(F04SessionStep.IDLE, reset.step)
        assertNull(reset.readingId)
    }
}
