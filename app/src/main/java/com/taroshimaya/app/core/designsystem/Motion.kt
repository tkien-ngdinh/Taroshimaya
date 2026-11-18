package com.taroshimaya.app.core.designsystem

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Motion tokens (D10 · DI-03). Read via `TaroshimayaTheme.motion`.
 *
 * Rules (T12):
 * - Ask first whether it should animate at all. Things users do tens of times a day (tab switch,
 *   list scroll, chip toggle) get no movement or only [durationPress] feedback.
 * - Enter and exit both use [easingOut] (starts fast, feels responsive). Never use an ease-in curve for UI:
 *   it delays the very first frames the user is watching. Exits are faster than entries
 *   ([durationShort] vs [durationMedium]).
 * - Things already on screen that move or morph (expand card, reorder) use [easingInOut].
 * - UI stays under 300 ms. Only rare signature moments (card flip F04, daily reveal F05) use
 *   [durationCardFlip].
 * - Pressable custom surfaces scale to [pressScale] for [durationPress], applied with
 *   `Modifier.graphicsLayer { scaleX = …; scaleY = … }` so the layout never shifts. Never animate from scale 0;
 *   start at [enterScale] together with alpha 0.
 * - Items entering together are staggered by [staggerDelay].
 * - When the user turned system animations off (animator duration scale 0), Compose itself finishes every
 *   animation instantly, fades included. [TaroshimayaTheme] then provides [Reduced] so nothing jumps:
 *   [pressScale] / [enterScale] become 1, and movement durations and [staggerDelay] become 0 (a coroutine
 *   `delay()` is not scaled by Compose). [durationFade] keeps its value but does not play in that state.
 *   Features read the tokens and never need their own accessibility check.
 */
@Immutable
data class TaroMotion(
    val durationPress: Int = 120,
    val durationShort: Int = 150,
    val durationMedium: Int = 220,
    val durationLong: Int = 300,
    val durationCardFlip: Int = 600,
    val durationFade: Int = 150,
    val staggerDelay: Int = 50,
    val pressScale: Float = 0.97f,
    val enterScale: Float = 0.95f,
    val easingOut: Easing = CubicBezierEasing(0.23f, 1f, 0.32f, 1f),
    val easingInOut: Easing = CubicBezierEasing(0.77f, 0f, 0.175f, 1f),
    val easingSheet: Easing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f),
    val isReduced: Boolean = false,
) {
    companion object {
        val Default = TaroMotion()
        val Reduced = TaroMotion(
            durationPress = 0,
            durationShort = 0,
            durationMedium = 0,
            durationLong = 0,
            durationCardFlip = 0,
            staggerDelay = 0,
            pressScale = 1f,
            enterScale = 1f,
            isReduced = true,
        )
    }
}

internal val LocalTaroMotion = staticCompositionLocalOf { TaroMotion.Default }
