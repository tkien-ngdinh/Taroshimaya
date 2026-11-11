package com.taroshimaya.app.features.auth_deck.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.taroshimaya.app.R
import com.taroshimaya.app.core.ui.ModulePlaceholderScreen

@Composable
fun AuthDeckPlaceholderScreen(
    modifier: Modifier = Modifier,
) {
    ModulePlaceholderScreen(
        titleRes = R.string.module_auth_deck_title,
        descriptionRes = R.string.module_auth_deck_desc,
        ownerRes = R.string.module_auth_deck_owner,
        featuresRes = R.string.module_auth_deck_features,
        packageName = "features/auth_deck",
        modifier = modifier,
    )
}
