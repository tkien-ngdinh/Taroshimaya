package com.taroshimaya.app.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.taroshimaya.app.R
import com.taroshimaya.app.core.data.AppLanguagePreference

/**
 * Bilingual C19 picker exposing the exact 3 choices proposed by DI-01:
 * Theo thiết bị / Tiếng Việt / English.
 */
@Composable
fun LanguagePickerDialog(
    currentPreference: AppLanguagePreference,
    onSelectPreference: (AppLanguagePreference) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.settings_language_title),
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.settings_language_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(12.dp))
                LanguageChoiceRow(
                    label = stringResource(R.string.language_option_system),
                    selected = currentPreference == AppLanguagePreference.SYSTEM,
                    onClick = { onSelectPreference(AppLanguagePreference.SYSTEM) },
                )
                LanguageChoiceRow(
                    label = stringResource(R.string.language_option_vi),
                    selected = currentPreference == AppLanguagePreference.VIETNAMESE,
                    onClick = { onSelectPreference(AppLanguagePreference.VIETNAMESE) },
                )
                LanguageChoiceRow(
                    label = stringResource(R.string.language_option_en),
                    selected = currentPreference == AppLanguagePreference.ENGLISH,
                    onClick = { onSelectPreference(AppLanguagePreference.ENGLISH) },
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.action_close))
            }
        },
    )
}

@Composable
private fun LanguageChoiceRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
