package com.taroshimaya.app.features.session_ai.domain

/**
 * Finite State Machine contracts and state models for F04 Tarot Session (RR05 / KI-09).
 * Solves the high-state complexity risk by strictly defining state transitions and guards.
 */

enum class F04SessionStep {
    IDLE,
    SETUP_QUESTION,
    SELECT_DECK,
    SELECT_SPREAD,
    CONFIRMED,
    SHUFFLING,
    DECK_FAN_OUT,
    DRAWING_CARD,
    CARDS_PLACED,
    FLIPPING_CARD,
    ALL_REVEALED,
    SAVING_READING,
    READING_READY,
}

data class F04SessionUiState(
    val step: F04SessionStep = F04SessionStep.IDLE,
    val question: String = "",
    val questionError: String? = null,
    val selectedDeckId: String? = null,
    val spreadConfig: SpreadConfig? = null,
    val drawnCards: List<PlacedCard> = emptyList(),
    val currentFlippingIndex: Int? = null,
    val readingId: String? = null,
    val saveError: String? = null,
    val isOfflineSaved: Boolean = false,
) {
    val isDeckSelected: Boolean get() = selectedDeckId != null
    val isSpreadSelected: Boolean get() = spreadConfig != null
    val expectedCardCount: Int get() = spreadConfig?.cardCount ?: 0
    val drawnCardCount: Int get() = drawnCards.size
    val hasDrawnAllCards: Boolean get() = expectedCardCount > 0 && drawnCardCount == expectedCardCount
    val revealedCardCount: Int get() = drawnCards.count { it.isRevealed }
    val isAllRevealed: Boolean get() = hasDrawnAllCards && revealedCardCount == expectedCardCount
}

sealed interface F04SessionEvent {
    data object InitSession : F04SessionEvent
    data class UpdateQuestion(val text: String) : F04SessionEvent
    data object SubmitQuestion : F04SessionEvent
    data class SelectDeck(val deckId: String, val isUnlocked: Boolean) : F04SessionEvent
    data class SelectSpread(val config: SpreadConfig) : F04SessionEvent
    data object StartShuffling : F04SessionEvent
    data object FinishShuffling : F04SessionEvent
    data class DrawCard(val cardId: String, val orientation: CardOrientation = CardOrientation.UPRIGHT) : F04SessionEvent
    data class FlipCard(val positionIndex: Int) : F04SessionEvent
    data class FinishFlip(val positionIndex: Int) : F04SessionEvent
    data object TriggerAutoSave : F04SessionEvent
    data class SaveReadingSuccess(val readingId: String) : F04SessionEvent
    data class SaveReadingFailure(val errorMessage: String, val fallbackOfflineSaved: Boolean = false) : F04SessionEvent
    data object RetrySave : F04SessionEvent
    data object ResetSession : F04SessionEvent
}

/**
 * Pure Kotlin Reducer for F04 Finite State Machine (RR05).
 * Enforces guards on every state transition and handles error/recovery scenarios.
 */
class F04StateMachine {

    fun reduce(currentState: F04SessionUiState, event: F04SessionEvent): F04SessionUiState {
        return when (event) {
            is F04SessionEvent.InitSession -> {
                F04SessionUiState(step = F04SessionStep.SETUP_QUESTION)
            }

            is F04SessionEvent.UpdateQuestion -> {
                currentState.copy(
                    question = event.text,
                    questionError = null,
                )
            }

            is F04SessionEvent.SubmitQuestion -> {
                val trimmed = currentState.question.trim()
                if (trimmed.length < 5) {
                    currentState.copy(questionError = "Câu hỏi phải có ít nhất 5 ký tự.")
                } else if (trimmed.length > 200) {
                    currentState.copy(questionError = "Câu hỏi không vượt quá 200 ký tự.")
                } else {
                    currentState.copy(
                        question = trimmed,
                        questionError = null,
                        step = F04SessionStep.SELECT_DECK,
                    )
                }
            }

            is F04SessionEvent.SelectDeck -> {
                if (!event.isUnlocked) {
                    currentState.copy(
                        saveError = "Bộ bài chưa được mở khóa (D7). Vui lòng chọn bộ khác hoặc mở khóa trong cửa hàng.",
                    )
                } else {
                    currentState.copy(
                        selectedDeckId = event.deckId,
                        saveError = null,
                        step = F04SessionStep.SELECT_SPREAD,
                    )
                }
            }

            is F04SessionEvent.SelectSpread -> {
                currentState.copy(
                    spreadConfig = event.config,
                    drawnCards = emptyList(),
                    step = F04SessionStep.CONFIRMED,
                )
            }

            is F04SessionEvent.StartShuffling -> {
                if (currentState.step == F04SessionStep.CONFIRMED) {
                    currentState.copy(
                        step = F04SessionStep.SHUFFLING,
                        saveError = null,
                    )
                } else {
                    currentState
                }
            }

            is F04SessionEvent.FinishShuffling -> {
                if (currentState.step == F04SessionStep.SHUFFLING) {
                    currentState.copy(step = F04SessionStep.DECK_FAN_OUT)
                } else {
                    currentState
                }
            }

            is F04SessionEvent.DrawCard -> {
                val config = currentState.spreadConfig ?: return currentState
                if (currentState.step != F04SessionStep.DECK_FAN_OUT && currentState.step != F04SessionStep.DRAWING_CARD) {
                    return currentState
                }
                if (currentState.drawnCards.size >= config.cardCount) {
                    return currentState
                }

                val nextIndex = currentState.drawnCards.size
                val newPlacedCard = PlacedCard(
                    positionIndex = nextIndex,
                    cardId = event.cardId,
                    orientation = event.orientation,
                    isRevealed = false,
                )
                val updatedCards = currentState.drawnCards + newPlacedCard

                if (updatedCards.size == config.cardCount) {
                    currentState.copy(
                        drawnCards = updatedCards,
                        step = F04SessionStep.CARDS_PLACED,
                    )
                } else {
                    currentState.copy(
                        drawnCards = updatedCards,
                        step = F04SessionStep.DRAWING_CARD,
                    )
                }
            }

            is F04SessionEvent.FlipCard -> {
                if (currentState.step != F04SessionStep.CARDS_PLACED && currentState.step != F04SessionStep.FLIPPING_CARD) {
                    return currentState
                }
                val targetCard = currentState.drawnCards.getOrNull(event.positionIndex) ?: return currentState
                if (targetCard.isRevealed) {
                    return currentState
                }

                currentState.copy(
                    currentFlippingIndex = event.positionIndex,
                    step = F04SessionStep.FLIPPING_CARD,
                )
            }

            is F04SessionEvent.FinishFlip -> {
                if (currentState.step != F04SessionStep.FLIPPING_CARD) {
                    return currentState
                }
                val updatedList = currentState.drawnCards.map { card ->
                    if (card.positionIndex == event.positionIndex) {
                        card.copy(isRevealed = true, revealedAtUtc = System.currentTimeMillis())
                    } else {
                        card
                    }
                }
                val allFlipped = updatedList.isNotEmpty() && updatedList.all { it.isRevealed }
                currentState.copy(
                    drawnCards = updatedList,
                    currentFlippingIndex = null,
                    step = if (allFlipped) F04SessionStep.ALL_REVEALED else F04SessionStep.CARDS_PLACED,
                )
            }

            is F04SessionEvent.TriggerAutoSave -> {
                if (currentState.step == F04SessionStep.ALL_REVEALED) {
                    currentState.copy(step = F04SessionStep.SAVING_READING, saveError = null)
                } else {
                    currentState
                }
            }

            is F04SessionEvent.SaveReadingSuccess -> {
                currentState.copy(
                    readingId = event.readingId,
                    saveError = null,
                    isOfflineSaved = false,
                    step = F04SessionStep.READING_READY,
                )
            }

            is F04SessionEvent.SaveReadingFailure -> {
                currentState.copy(
                    saveError = event.errorMessage,
                    isOfflineSaved = event.fallbackOfflineSaved,
                    step = if (event.fallbackOfflineSaved) F04SessionStep.READING_READY else F04SessionStep.SAVING_READING,
                )
            }

            is F04SessionEvent.RetrySave -> {
                if (currentState.step == F04SessionStep.SAVING_READING) {
                    currentState.copy(saveError = null)
                } else {
                    currentState
                }
            }

            is F04SessionEvent.ResetSession -> {
                F04SessionUiState(step = F04SessionStep.IDLE)
            }
        }
    }
}
