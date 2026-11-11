package com.taroshimaya.app.features.session_ai.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.taroshimaya.app.R
import com.taroshimaya.app.core.data.AppLanguagePreference
import com.taroshimaya.app.core.data.DataSourceMode
import com.taroshimaya.app.core.ui.ModulePlaceholderScreen

@Composable
fun SessionAiPlaceholderScreen(
    dataSourceMode: DataSourceMode,
    languagePreference: AppLanguagePreference,
    modifier: Modifier = Modifier,
) {
    ModulePlaceholderScreen(
        titleRes = R.string.module_session_title,
        descriptionRes = R.string.module_session_desc,
        ownerRes = R.string.module_session_owner,
        featuresRes = R.string.module_session_features,
        packageName = "features/session_ai",
        dataSourceMode = dataSourceMode,
        languagePreference = languagePreference,
        modifier = modifier,
    )
}
