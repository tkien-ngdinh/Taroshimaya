package com.taroshimaya.app.core.designsystem

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Corner shape tokens (D10 · DI-03) for Material components:
 * extraSmall 4 · small 8 · medium 12 · large 16 · extraLarge 28 (dp).
 * Read via `TaroshimayaTheme.shapes.<size>`.
 */
internal val TaroMaterialShapes: Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

/**
 * Shapes Material does not name (D10 · DI-03). Read via `TaroshimayaTheme.taroShapes`.
 *
 * @property tarotCard Corners of every tarot card (front and back) so spreads look consistent.
 * @property pill Fully rounded chips, tags, streak badges.
 * @property bottomSheet Top-rounded sheet container.
 */
@Immutable
data class TaroShapes(
    val tarotCard: Shape = RoundedCornerShape(10.dp),
    val pill: Shape = CircleShape,
    val bottomSheet: Shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
)

internal val LocalTaroShapes = staticCompositionLocalOf { TaroShapes() }
