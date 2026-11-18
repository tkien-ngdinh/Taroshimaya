package com.taroshimaya.app.core.designsystem

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.provider.Settings
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.core.view.WindowCompat

/** Active theme variant, so a composable can react to it (e.g. pick card artwork per theme). */
internal val LocalTaroThemeVariant = staticCompositionLocalOf { TaroThemeVariant.MYSTIC_NIGHT }

/**
 * Root theme of Taroshimaya (D10 · DI-03). Applied once at the app root (`TaroshimayaApp`).
 *
 * Feature screens must NOT wrap themselves again: a nested call without `variant` falls back to the device
 * default and re-syncs the system bars, which would override the theme the user picked (DI-14).
 * Wrap again only where there is no app root: `@Preview` functions and UI tests.
 *
 * Provides the Material 3 theme (colors, typography, shapes) plus Taroshimaya-specific tokens through
 * CompositionLocal: extended colors, spacing, sizing, elevation, extra shapes and motion.
 * Read everything through the [TaroshimayaTheme] object, e.g. `TaroshimayaTheme.spacing.lg`.
 *
 * Brand colors are intentional, so Android 12+ dynamic color (wallpaper-based) is not used.
 *
 * @param variant One of the 4 F14 themes. Defaults to [TaroThemeVariant.default] (follow device dark/light)
 *   until DI-14 wires the user's saved choice.
 */
@Composable
fun TaroshimayaTheme(
    variant: TaroThemeVariant = TaroThemeVariant.default(isSystemInDarkTheme()),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    // Read once per Activity: toggling "Remove animations" while the app is open applies after the
    // Activity is recreated (rotation, process restart). Cheap and good enough for v1.
    val motion = remember(context) {
        if (context.animationsDisabled()) TaroMotion.Reduced else TaroMotion.Default
    }

    SyncSystemBarAppearance(isDarkTheme = variant.isDark)

    CompositionLocalProvider(
        LocalTaroThemeVariant provides variant,
        LocalTaroExtendedColors provides TaroColorSchemes.extendedColors(variant),
        LocalTaroSpacing provides TaroSpacing(),
        LocalTaroSizing provides TaroSizing(),
        LocalTaroElevation provides TaroElevation(),
        LocalTaroShapes provides TaroShapes(),
        LocalTaroMotion provides motion,
    ) {
        MaterialTheme(
            colorScheme = TaroColorSchemes.colorScheme(variant),
            typography = TaroTypography,
            shapes = TaroMaterialShapes,
            content = content,
        )
    }
}

/**
 * Single access point for all design tokens (D10). Usable from any feature package.
 *
 * ```
 * Text(
 *     text = stringResource(R.string.journal_title),
 *     style = TaroshimayaTheme.typography.titleLarge,
 *     color = TaroshimayaTheme.colors.onSurface,
 *     modifier = Modifier.padding(TaroshimayaTheme.spacing.lg),
 * )
 * ```
 */
object TaroshimayaTheme {
    /** Material 3 color roles of the active theme (primary, surface, onSurface, error, …). */
    val colors: ColorScheme
        @Composable @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

    /** Taroshimaya roles beyond Material 3: card colors, tarot elements, success/warning, chart series. */
    val extendedColors: TaroExtendedColors
        @Composable @ReadOnlyComposable
        get() = LocalTaroExtendedColors.current

    val typography: Typography
        @Composable @ReadOnlyComposable
        get() = MaterialTheme.typography

    /** Small uppercase section label (kicker) above a title. Callers pass `text.uppercase()`. */
    val eyebrow: TextStyle
        @Composable @ReadOnlyComposable
        get() = TaroEyebrow

    /** Material corner sizes (extraSmall … extraLarge). */
    val shapes: Shapes
        @Composable @ReadOnlyComposable
        get() = MaterialTheme.shapes

    /** Extra shapes: tarot card, pill, bottom sheet. */
    val taroShapes: TaroShapes
        @Composable @ReadOnlyComposable
        get() = LocalTaroShapes.current

    val spacing: TaroSpacing
        @Composable @ReadOnlyComposable
        get() = LocalTaroSpacing.current

    val sizing: TaroSizing
        @Composable @ReadOnlyComposable
        get() = LocalTaroSizing.current

    val elevation: TaroElevation
        @Composable @ReadOnlyComposable
        get() = LocalTaroElevation.current

    val motion: TaroMotion
        @Composable @ReadOnlyComposable
        get() = LocalTaroMotion.current

    val variant: TaroThemeVariant
        @Composable @ReadOnlyComposable
        get() = LocalTaroThemeVariant.current
}

/** Keeps status/navigation bar icons readable when the app theme differs from the device setting. */
@Composable
private fun SyncSystemBarAppearance(isDarkTheme: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    SideEffect {
        val window = view.context.findActivity()?.window ?: return@SideEffect
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !isDarkTheme
            isAppearanceLightNavigationBars = !isDarkTheme
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

/** True when the user turned animations off (Developer options / Accessibility "Remove animations"). */
private fun Context.animationsDisabled(): Boolean = runCatching {
    Settings.Global.getFloat(contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
}.getOrDefault(false)
