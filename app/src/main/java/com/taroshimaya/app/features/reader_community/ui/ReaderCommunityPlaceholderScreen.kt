package com.taroshimaya.app.features.reader_community.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.taroshimaya.app.R
import com.taroshimaya.app.core.data.AppLanguagePreference
import com.taroshimaya.app.core.data.DataSourceMode
import com.taroshimaya.app.core.ui.ModulePlaceholderScreen

@Composable
fun ReaderCommunityPlaceholderScreen(
    dataSourceMode: DataSourceMode,
    languagePreference: AppLanguagePreference,
    modifier: Modifier = Modifier,
) {
    ModulePlaceholderScreen(
        titleRes = R.string.module_community_title,
        descriptionRes = R.string.module_community_desc,
        ownerRes = R.string.module_community_owner,
        featuresRes = R.string.module_community_features,
        packageName = "features/reader_community",
        dataSourceMode = dataSourceMode,
        languagePreference = languagePreference,
        modifier = modifier,
    )
}
