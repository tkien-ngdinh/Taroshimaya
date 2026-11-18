package com.taroshimaya.app.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.taroshimaya.app.R
import com.taroshimaya.app.core.designsystem.TaroshimayaTheme

/**
 * Shared 4-state container (Loading / Empty / Error / Success) ready for DI-06 & T12 screens.
 */
@Composable
fun <T> UiStateHost(
    state: UiState<T>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit,
) {
    when (state) {
        UiState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(TaroshimayaTheme.spacing.xl),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(TaroshimayaTheme.spacing.md),
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = stringResource(R.string.state_loading),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        UiState.Empty -> {
            OutlinedCard(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(TaroshimayaTheme.spacing.lg),
            ) {
                Column(
                    modifier = Modifier.padding(TaroshimayaTheme.spacing.cardPadding),
                    verticalArrangement = Arrangement.spacedBy(TaroshimayaTheme.spacing.sm),
                ) {
                    Text(
                        text = stringResource(R.string.state_empty_title),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        text = stringResource(R.string.state_empty_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        is UiState.Error -> {
            val errorText = when {
                state.messageResId != null -> stringResource(state.messageResId)
                !state.fallbackMessage.isNullOrBlank() -> state.fallbackMessage
                else -> stringResource(R.string.state_error_generic)
            }
            OutlinedCard(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(TaroshimayaTheme.spacing.lg),
            ) {
                Column(
                    modifier = Modifier.padding(TaroshimayaTheme.spacing.cardPadding),
                    verticalArrangement = Arrangement.spacedBy(TaroshimayaTheme.spacing.sm),
                ) {
                    Text(
                        text = errorText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Spacer(modifier = Modifier.height(TaroshimayaTheme.spacing.xs))
                    Button(onClick = onRetry) {
                        Text(text = stringResource(R.string.action_retry))
                    }
                }
            }
        }

        is UiState.Success -> content(state.data)
    }
}

@Preview(showBackground = true)
@Composable
private fun UiStateHostLoadingPreview() {
    TaroshimayaTheme {
        UiStateHost<String>(state = UiState.Loading, onRetry = {}) {}
    }
}

@Preview(showBackground = true)
@Composable
private fun UiStateHostErrorPreview() {
    TaroshimayaTheme {
        UiStateHost<String>(state = UiState.Error(), onRetry = {}) {}
    }
}
