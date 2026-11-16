package com.taroshimaya.app.core.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.taroshimaya.app.R
import com.taroshimaya.app.core.designsystem.TaroshimayaTheme

/**
 * Clean reusable placeholder screen for the five feature packages.
 * All UI copy comes from string resources (C19); colors, type and spacing come from D10 tokens (DI-03).
 * UI never branches on DataSourceMode or holds runtime data source state (DI-01).
 */
@Composable
fun ModulePlaceholderScreen(
    @StringRes titleRes: Int,
    @StringRes descriptionRes: Int,
    @StringRes ownerRes: Int,
    @StringRes featuresRes: Int,
    packageName: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = TaroshimayaTheme.spacing.screenHorizontal,
                vertical = TaroshimayaTheme.spacing.screenVertical,
            ),
        verticalArrangement = Arrangement.spacedBy(TaroshimayaTheme.spacing.listItemGap),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.85f),
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            Column(
                modifier = Modifier.padding(TaroshimayaTheme.spacing.cardPadding),
                verticalArrangement = Arrangement.spacedBy(TaroshimayaTheme.spacing.sm),
            ) {
                Text(
                    text = stringResource(titleRes),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(descriptionRes),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(TaroshimayaTheme.spacing.xs))
                Text(
                    text = stringResource(
                        R.string.placeholder_owner_label,
                        stringResource(ownerRes),
                    ),
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = stringResource(R.string.placeholder_package_label, packageName),
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    text = stringResource(
                        R.string.placeholder_features_label,
                        stringResource(featuresRes),
                    ),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }

        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
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

        DisclaimerBanner()
    }
}
