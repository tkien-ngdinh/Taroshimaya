package com.taroshimaya.app.core.designsystem

import androidx.annotation.StringRes
import com.taroshimaya.app.R

/**
 * The 4 interface themes of F14 (D10 · DI-03). Every variant ships a full token set
 * (Material 3 color scheme + [TaroExtendedColors]); spacing, typography, shapes and motion are shared.
 *
 * - [MYSTIC_NIGHT]: default dark theme from the "Kedy Mail" illustration (cobalt sky, ice-white stars, cyan glow).
 * - [MOONLIGHT]: default light theme from the brand video (frost paper, ink navy, royal blue, coral).
 * - [CLASSIC_TAROT]: light theme inspired by classic card printing (cream, crimson, ultramarine, mustard).
 * - [CELESTIAL_AURORA]: dark theme from the tarot widget reference (indigo violet, lavender glow, gold line art).
 *
 * [storageKey] is a stable identifier for persisting the user's choice (DI-14 · user_settings);
 * never persist [ordinal] or [name].
 */
enum class TaroThemeVariant(
    val storageKey: String,
    val isDark: Boolean,
    @param:StringRes val labelRes: Int,
) {
    MYSTIC_NIGHT(
        storageKey = "mystic_night",
        isDark = true,
        labelRes = R.string.theme_variant_mystic_night,
    ),
    MOONLIGHT(
        storageKey = "moonlight",
        isDark = false,
        labelRes = R.string.theme_variant_moonlight,
    ),
    CLASSIC_TAROT(
        storageKey = "classic_tarot",
        isDark = false,
        labelRes = R.string.theme_variant_classic_tarot,
    ),
    CELESTIAL_AURORA(
        storageKey = "celestial_aurora",
        isDark = true,
        labelRes = R.string.theme_variant_celestial_aurora,
    ),
    ;

    companion object {
        /** Variant used when the user has not picked a theme yet: follow the device dark/light setting. */
        fun default(systemInDarkTheme: Boolean): TaroThemeVariant =
            if (systemInDarkTheme) MYSTIC_NIGHT else MOONLIGHT

        /** Restores a persisted choice; unknown or missing keys fall back to [default]. */
        fun fromStorageKey(key: String?, systemInDarkTheme: Boolean): TaroThemeVariant =
            entries.firstOrNull { it.storageKey == key } ?: default(systemInDarkTheme)
    }
}
