package com.taroshimaya.app.features.economy_shop.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.taroshimaya.app.R
import com.taroshimaya.app.core.data.AppLanguagePreference
import com.taroshimaya.app.core.data.DataSourceMode
import com.taroshimaya.app.core.ui.ModulePlaceholderScreen

@Composable
fun EconomyShopPlaceholderScreen(
    dataSourceMode: DataSourceMode,
    languagePreference: AppLanguagePreference,
    modifier: Modifier = Modifier,
) {
    ModulePlaceholderScreen(
        titleRes = R.string.module_shop_title,
        descriptionRes = R.string.module_shop_desc,
        ownerRes = R.string.module_shop_owner,
        featuresRes = R.string.module_shop_features,
        packageName = "features/economy_shop",
        dataSourceMode = dataSourceMode,
        languagePreference = languagePreference,
        modifier = modifier,
    )
}
