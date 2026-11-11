package com.taroshimaya.app.core.ui

/**
 * Standard 4-state UI model aligned with DI-01 / T12 (Loading, Empty, Error, Success).
 */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>

    data object Empty : UiState<Nothing>

    data class Error(val messageResId: Int? = null, val fallbackMessage: String? = null) : UiState<Nothing>

    data class Success<T>(val data: T) : UiState<T>
}
