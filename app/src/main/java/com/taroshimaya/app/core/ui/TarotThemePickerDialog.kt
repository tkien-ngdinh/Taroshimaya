package com.taroshimaya.app.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
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
import com.taroshimaya.app.R
import com.taroshimaya.app.core.designsystem.TaroThemeVariant
import com.taroshimaya.app.core.designsystem.TaroshimayaTheme

/**
 * Dialog allowing users to switch between the 4 mystical themes or follow system default (F14 & DI-03).
 */
@Composable
fun TarotThemePickerDialog(
    currentVariant: TaroThemeVariant?,
    onSelectVariant: (TaroThemeVariant?) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.settings_theme_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.settings_theme_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(TaroshimayaTheme.spacing.md))

                // Option: System default
                ThemeChoiceRow(
                    title = stringResource(R.string.theme_option_system),
                    description = stringResource(R.string.theme_option_system_desc),
                    selected = currentVariant == null,
                    onClick = { onSelectVariant(null) },
                )

                // 4 Theme Variants
                TaroThemeVariant.entries.forEach { variant ->
                    ThemeChoiceRow(
                        title = stringResource(variant.labelRes),
                        description = stringResource(variant.descriptionRes()),
                        selected = currentVariant == variant,
                        onClick = { onSelectVariant(variant) },
                    )
                }
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
private fun ThemeChoiceRow(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = TaroshimayaTheme.sizing.minTouchTarget)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(vertical = TaroshimayaTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
        )
        Spacer(modifier = Modifier.width(TaroshimayaTheme.spacing.sm))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun TaroThemeVariant.descriptionRes(): Int = when (this) {
    TaroThemeVariant.MYSTIC_NIGHT -> R.string.theme_variant_mystic_night_desc
    TaroThemeVariant.MOONLIGHT -> R.string.theme_variant_moonlight_desc
    TaroThemeVariant.CLASSIC_TAROT -> R.string.theme_variant_classic_tarot_desc
    TaroThemeVariant.CELESTIAL_AURORA -> R.string.theme_variant_celestial_aurora_desc
}
