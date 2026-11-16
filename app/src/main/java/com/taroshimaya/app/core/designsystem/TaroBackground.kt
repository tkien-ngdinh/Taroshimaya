package com.taroshimaya.app.core.designsystem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Night-sky backdrop of the app (D10, DI-03), drawn from the active theme:
 * a vertical surface gradient, a nebula wash top right, a glow wash bottom left, a faint astrological wheel behind
 * the content (rings, 12 houses, degree ticks, hexagram, eight-point star), a smaller wheel in the top right corner,
 * 4 glowing four-point sparkles and a few specks of light. Light themes use the same layout, fainter.
 *
 * It is placed once at the app root (`TaroshimayaApp`); feature packages do not add it again. Screens keep their
 * containers transparent to let it show; an opaque card covering it is the card's own choice.
 *
 * The drawing is static and cached by [drawWithCache] (rebuilt only when the size or theme changes), so it never
 * costs anything while lists scroll. Nothing here animates.
 */
@Composable
fun TaroBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = TaroshimayaTheme.colors
    val ext = TaroshimayaTheme.extendedColors
    val isDark = TaroshimayaTheme.variant.isDark
    Box(
        modifier = modifier
            .fillMaxSize()
            .drawWithCache {
                val w = size.width
                val h = size.height
                val minDim = size.minDimension
                val stroke = Stroke(width = 1.dp.toPx())
                val gradient = Brush.verticalGradient(
                    listOf(colors.surfaceContainerLow, colors.background, colors.surfaceContainerLowest),
                )
                val nebula = Brush.radialGradient(
                    listOf(ext.nebula.copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(w * 0.85f, h * 0.06f),
                    radius = minDim * 0.8f,
                )
                val glow = Brush.radialGradient(
                    listOf(ext.glow.copy(alpha = 0.2f), Color.Transparent),
                    center = Offset(w * 0.10f, h * 0.72f),
                    radius = minDim * 0.75f,
                )
                val wheelColor = if (isDark) colors.primary else colors.outline
                val wheelAlpha =
                    if (isDark) TaroBackgroundDefaults.WheelAlphaDark else TaroBackgroundDefaults.WheelAlphaLight
                val mainCenter = Offset(w * 0.5f, h * 0.58f)
                val mainRadius = minDim * 0.7f
                val mainWheel = wheelPath(mainCenter, mainRadius, detailed = true)
                val houseDots = List(12) { polar(mainCenter, mainRadius * 0.77f, 15f + it * 30f) }
                val cornerWheel = wheelPath(Offset(w * 0.94f, h * 0.06f), minDim * 0.38f, detailed = false)

                val sparkleColor = if (isDark) Color.White else ext.glow
                val sparkles = TaroBackgroundDefaults.Sparkles.map { spec ->
                    CachedSparkle(Offset(spec.x * w, spec.y * h), spec.sizeDp.dp.toPx(), ext.glow)
                }
                val speckRadius = 1.dp.toPx()

                onDrawBehind {
                    drawRect(gradient)
                    drawRect(nebula)
                    drawRect(glow)
                    drawPath(mainWheel, wheelColor, alpha = wheelAlpha, style = stroke)
                    houseDots.forEach { drawCircle(wheelColor, radius = speckRadius * 1.5f, center = it, alpha = wheelAlpha) }
                    drawPath(cornerWheel, wheelColor, alpha = wheelAlpha / 2f, style = stroke)
                    sparkles.forEach { it.draw(this, sparkleColor, ext.glow) }
                    TaroBackgroundDefaults.Specks.forEach {
                        drawCircle(ext.accent, radius = speckRadius, center = Offset(it.x * w, it.y * h), alpha = 0.8f)
                    }
                }
            },
        content = content,
    )
}

/** Tunable constants of [TaroBackground]; kept here so tests can hold the pattern to "subtle and on screen". */
internal object TaroBackgroundDefaults {
    /** Wheel line opacity on dark themes. Above 0.25 the wheel starts to compete with text. */
    const val WheelAlphaDark: Float = 0.16f

    /** Wheel line opacity on light themes (dark lines on light paper read stronger, so lower). */
    const val WheelAlphaLight: Float = 0.12f

    /**
     * The 4 glowing sparkles, placed where they stay visible around full-width cards without sitting behind text:
     * the empty right part of the top bar (right of the title, left of the actions), the thin left edge strip
     * outside the cards' 16dp margin, and the open band above the navigation bar.
     */
    val Sparkles: List<SparkleSpec> = listOf(
        SparkleSpec(x = 0.76f, y = 0.085f, sizeDp = 14f),
        SparkleSpec(x = 0.025f, y = 0.47f, sizeDp = 9f),
        SparkleSpec(x = 0.30f, y = 0.85f, sizeDp = 11f),
        SparkleSpec(x = 0.90f, y = 0.86f, sizeDp = 16f),
    )

    /** Tiny 1dp specks of accent light. Only x and y are used. */
    val Specks: List<SparkleSpec> = listOf(
        SparkleSpec(x = 0.30f, y = 0.14f, sizeDp = 1f),
        SparkleSpec(x = 0.62f, y = 0.05f, sizeDp = 1f),
        SparkleSpec(x = 0.72f, y = 0.40f, sizeDp = 1f),
        SparkleSpec(x = 0.22f, y = 0.38f, sizeDp = 1f),
        SparkleSpec(x = 0.55f, y = 0.92f, sizeDp = 1f),
        SparkleSpec(x = 0.94f, y = 0.55f, sizeDp = 1f),
    )
}

/** One sparkle of [TaroBackground]: [x] and [y] are fractions (0..1) of the frame, [sizeDp] its full width. */
internal data class SparkleSpec(val x: Float, val y: Float, val sizeDp: Float)

/**
 * Four-point star with concave, curved sides that fits a [size] by [size] box (origin top left).
 * The control points sit slightly off the center so the arms stay slender but not hairline thin.
 */
internal fun sparklePath(size: Float): Path {
    val c = size / 2f
    val k = size * 0.06f
    return Path().apply {
        moveTo(c, 0f)
        quadraticTo(c + k, c - k, size, c)
        quadraticTo(c + k, c + k, c, size)
        quadraticTo(c - k, c + k, 0f, c)
        quadraticTo(c - k, c - k, c, 0f)
        close()
    }
}

/**
 * A sparkle with its paths and halo brush built once in the draw cache, so drawing allocates nothing.
 * Its fields avoid the names `center` and `size`: inside `with(scope)` those resolve to `DrawScope.center`
 * (the middle of the screen) and `DrawScope.size`, which once stacked every sparkle in the middle.
 */
private class CachedSparkle(private val position: Offset, private val starSize: Float, glow: Color) {
    private val haloRadius = starSize * 1.4f
    private val haloSize = starSize * 1.5f
    private val star = sparklePath(starSize)
    private val haloStar = sparklePath(haloSize)
    private val haloBrush = Brush.radialGradient(
        listOf(glow.copy(alpha = 0.45f), Color.Transparent),
        center = position,
        radius = haloRadius,
    )

    fun draw(scope: DrawScope, color: Color, glow: Color) = with(scope) {
        // Two glow layers of falling strength (soft disc, then a larger faint star), then the star itself.
        drawCircle(haloBrush, radius = haloRadius, center = position)
        translate(position.x - haloSize / 2f, position.y - haloSize / 2f) { drawPath(haloStar, glow, alpha = 0.25f) }
        translate(position.x - starSize / 2f, position.y - starSize / 2f) { drawPath(star, color) }
    }
}

private fun polar(center: Offset, radius: Float, degrees: Float): Offset {
    val rad = degrees * PI.toFloat() / 180f
    return Offset(center.x + radius * cos(rad), center.y + radius * sin(rad))
}

/**
 * Outline of an astrological wheel as one path (drawn with a single stroke call).
 * Detailed: rings at 0.98 / 0.84 / 0.70 / 0.30 of [r], 12 house spokes, 72 degree ticks (long every 30°),
 * a hexagram and an eight-point star in the middle. Simple: two rings and 36 ticks.
 */
private fun wheelPath(center: Offset, r: Float, detailed: Boolean): Path = Path().apply {
    fun ring(f: Float) = addOval(Rect(center, r * f))
    fun line(from: Offset, to: Offset) {
        moveTo(from.x, from.y)
        lineTo(to.x, to.y)
    }
    if (!detailed) {
        ring(0.98f)
        ring(0.80f)
        for (i in 0 until 36) line(polar(center, r * 0.98f, i * 10f), polar(center, r * 0.92f, i * 10f))
        return@apply
    }
    listOf(0.98f, 0.84f, 0.70f, 0.30f).forEach(::ring)
    for (i in 0 until 12) line(polar(center, r * 0.70f, i * 30f), polar(center, r * 0.84f, i * 30f))
    for (i in 0 until 72) {
        val inner = if (i % 6 == 0) 0.88f else 0.94f
        line(polar(center, r * 0.98f, i * 5f), polar(center, r * inner, i * 5f))
    }
    listOf(-90f, 90f).forEach { start ->
        val points = List(3) { polar(center, r * 0.70f, start + it * 120f) }
        moveTo(points[0].x, points[0].y)
        points.drop(1).forEach { lineTo(it.x, it.y) }
        close()
    }
    val star = List(16) { polar(center, r * if (it % 2 == 0) 0.30f else 0.12f, -90f + it * 22.5f) }
    moveTo(star[0].x, star[0].y)
    star.drop(1).forEach { lineTo(it.x, it.y) }
    close()
}
