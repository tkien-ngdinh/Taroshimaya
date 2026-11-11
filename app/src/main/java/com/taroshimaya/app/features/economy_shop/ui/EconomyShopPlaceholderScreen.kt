package com.taroshimaya.app.features.economy_shop.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.taroshimaya.app.R
import com.taroshimaya.app.core.ui.ModulePlaceholderScreen

@Composable
fun EconomyShopPlaceholderScreen(
    modifier: Modifier = Modifier,
) {
    ModulePlaceholderScreen(
        titleRes = R.string.module_shop_title,
        descriptionRes = R.string.module_shop_desc,
        ownerRes = R.string.module_shop_owner,
        featuresRes = R.string.module_shop_features,
        packageName = "features/economy_shop",
        modifier = modifier,
    )
}
