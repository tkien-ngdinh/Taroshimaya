package com.taroshimaya.app.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.taroshimaya.app.R
import com.taroshimaya.app.core.designsystem.TaroshimayaTheme

/**
 * Mandatory disclaimer block (Tab 3.6 · S06) rendered using design tokens & string resources.
 */
@Composable
fun DisclaimerBanner(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = TaroshimayaTheme.elevation.level1,
    ) {
        Column(modifier = Modifier.padding(TaroshimayaTheme.spacing.md)) {
            Text(
                text = stringResource(R.string.disclaimer_s06_title),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(TaroshimayaTheme.spacing.xs))
            Text(
                text = stringResource(R.string.disclaimer_s06_body),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
