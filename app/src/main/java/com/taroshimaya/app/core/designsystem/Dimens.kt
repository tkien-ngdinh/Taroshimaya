package com.taroshimaya.app.core.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Spacing tokens (D10 · DI-03) on a 4dp grid (only [xxs] = 2dp breaks it, for hairline gaps).
 * Read via `TaroshimayaTheme.spacing`; do not write raw `.dp` paddings in feature screens.
 *
 * Rule of thumb (T12): [sm] between related items, [lg] for screen gutters and card padding,
 * [xl] between sections.
 */
@Immutable
data class TaroSpacing(
    val none: Dp = 0.dp,
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 48.dp,
) {
    /** Horizontal padding of every screen (left/right gutter). */
    val screenHorizontal: Dp get() = lg

    /** Top/bottom padding of scrollable screen content. */
    val screenVertical: Dp get() = lg

    /** Inner padding of cards, sheets and dialogs. */
    val cardPadding: Dp get() = lg

    /** Gap between items in a list or grid. */
    val listItemGap: Dp get() = md

    /** Gap between two content sections on the same screen. */
    val sectionGap: Dp get() = xl

    /** Ordered scale (ascending), handy for tests and the design-system catalog. */
    val scale: List<Dp> get() = listOf(none, xxs, xs, sm, md, lg, xl, xxl, xxxl)
}

/**
 * Size tokens (D10 · DI-03): touch targets, icons, avatars, tarot cards, borders.
 *
 * @property tarotCardAspectRatio width / height of a standard tarot card (70 × 120 mm ≈ 0.58).
 */
@Immutable
data class TaroSizing(
    val minTouchTarget: Dp = 48.dp,
    val iconSmall: Dp = 16.dp,
    val iconMedium: Dp = 24.dp,
    val iconLarge: Dp = 32.dp,
    val avatarSmall: Dp = 32.dp,
    val avatarMedium: Dp = 48.dp,
    val avatarLarge: Dp = 72.dp,
    val tarotCardSmall: Dp = 72.dp,
    val tarotCardMedium: Dp = 120.dp,
    val tarotCardLarge: Dp = 200.dp,
    val tarotCardAspectRatio: Float = 0.58f,
    val borderThin: Dp = 1.dp,
    val borderThick: Dp = 2.dp,
    val contentMaxWidth: Dp = 600.dp,
)

/**
 * Elevation tokens (D10 · DI-03), aligned with Material 3 levels 0–5.
 * Prefer tonal surfaces (`surfaceContainer*`) over shadows; dark themes barely show shadows.
 */
@Immutable
data class TaroElevation(
    val level0: Dp = 0.dp,
    val level1: Dp = 1.dp,
    val level2: Dp = 3.dp,
    val level3: Dp = 6.dp,
    val level4: Dp = 8.dp,
    val level5: Dp = 12.dp,
)

internal val LocalTaroSpacing = staticCompositionLocalOf { TaroSpacing() }
internal val LocalTaroSizing = staticCompositionLocalOf { TaroSizing() }
internal val LocalTaroElevation = staticCompositionLocalOf { TaroElevation() }
