package com.taroshimaya.app.core.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Semantic color roles that Material 3 does not cover (D10, DI-03).
 * Read via `TaroshimayaTheme.extendedColors`; values change with [TaroThemeVariant].
 *
 * @property accent Decorative gold for flourishes, star glints, streak / coin highlights. Not for body text.
 *   Stays ≥ 3:1 against every surface tier of its theme (WCAG 1.4.11, checked in `DesignTokensTest`).
 * @property cardBack Fill of the face-down tarot card (F04, F05 draw flows).
 * @property cardBackPattern Ornament lines drawn on top of [cardBack] (contrast is against [cardBack], not the surface).
 * @property cardFace Background behind card artwork and card detail panels.
 * @property cardBorder Thin frame around tarot cards in every list / spread. It is the card's visible boundary,
 *   so after alpha blending it stays ≥ 3:1 against every surface tier (checked in `DesignTokensTest`).
 * @property glow Light color of the theme: halo of [Modifier.taroGlow], sparkle haze and light washes in
 *   [TaroBackground]. Decorative only, never text.
 * @property nebula Soft haze color painted behind content by [TaroBackground]. Decorative only, never text.
 * @property cardInk Text and line work drawn on [cardFace] (≥ 4.5:1 against it, checked in `DesignTokensTest`).
 * @property cardArt Illustration fill on [cardFace] (≥ 3:1 against it).
 * @property success Positive confirmation (journal saved, streak kept). Pair with [onSuccess].
 * @property warning Caution that is not an error (streak at risk, accuracy pending). Pair with [onWarning].
 * @property elementFire Wands / Fire (F08 card details, F12 element charts).
 * @property elementWater Cups / Water.
 * @property elementAir Swords / Air.
 * @property elementEarth Pentacles / Earth.
 * @property majorArcana Highlight for Major Arcana tags and chart series.
 * @property minorArcana Highlight for Minor Arcana tags and chart series.
 * @property chartSeries Ordered palette for multi-series charts (F12 Vico); index wraps when more series exist.
 * @property backgroundGradient Top-to-bottom colors for hero / onboarding backdrops (use [backgroundBrush]).
 */
@Immutable
data class TaroExtendedColors(
    val accent: Color,
    val onAccent: Color,
    val cardBack: Color,
    val cardBackPattern: Color,
    val cardFace: Color,
    val cardBorder: Color,
    val glow: Color,
    val nebula: Color,
    val cardInk: Color,
    val cardArt: Color,
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val elementFire: Color,
    val elementWater: Color,
    val elementAir: Color,
    val elementEarth: Color,
    val majorArcana: Color,
    val minorArcana: Color,
    val chartSeries: List<Color>,
    val backgroundGradient: List<Color>,
) {
    /**
     * Vertical brush built from [backgroundGradient]. Every read creates a new, structurally equal [Brush]; that is
     * cheap, so wrap it in `remember(extendedColors)` only when it is read in a hot animation or draw path.
     */
    val backgroundBrush: Brush
        get() = Brush.verticalGradient(backgroundGradient)

    /** Color for chart series [index]; wraps around [chartSeries] so callers never go out of bounds. */
    fun chartColor(index: Int): Color = chartSeries[Math.floorMod(index, chartSeries.size)]
}

/**
 * Throws when read outside [TaroshimayaTheme] so a missing theme wrapper fails fast in previews/tests
 * instead of silently rendering with wrong colors.
 */
internal val LocalTaroExtendedColors = staticCompositionLocalOf<TaroExtendedColors> {
    error("TaroExtendedColors not provided: wrap the UI in TaroshimayaTheme { ... }")
}
