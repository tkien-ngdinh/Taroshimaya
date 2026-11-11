package com.taroshimaya.app.features.journal_insights.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.taroshimaya.app.R
import com.taroshimaya.app.core.data.AppLanguagePreference
import com.taroshimaya.app.core.data.DataSourceMode
import com.taroshimaya.app.core.ui.ModulePlaceholderScreen

@Composable
fun JournalInsightsPlaceholderScreen(
    dataSourceMode: DataSourceMode,
    languagePreference: AppLanguagePreference,
    modifier: Modifier = Modifier,
) {
    ModulePlaceholderScreen(
        titleRes = R.string.module_journal_title,
        descriptionRes = R.string.module_journal_desc,
        ownerRes = R.string.module_journal_owner,
        featuresRes = R.string.module_journal_features,
        packageName = "features/journal_insights",
        dataSourceMode = dataSourceMode,
        languagePreference = languagePreference,
        modifier = modifier,
    )
}
