package com.taroshimaya.app.core.designsystem

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * Glowing frame around a shape (D10, DI-03), by default the tarot card shape: a soft halo in
 * `extendedColors.glow` that spreads outward, then the content, then a thin `extendedColors.cardBorder` edge.
 * Use it for the selected or highlighted card.
 *
 * - [enabled] switches the glow on and off instantly. When false the modifier only draws the content.
 * - [shape] outlines the glow; `null` uses `TaroshimayaTheme.taroShapes.tarotCard`.
 *
 * This is a pure drawing effect: it never animates. Fading the glow in, pulsing it or lifting the card (as in the
 * design mockup) belongs to the component that uses it (DI-06), driven by `TaroshimayaTheme.motion`.
 * The halo is drawn outside the bounds, so leave room around the card (or do not clip its parent).
 * It uses layered strokes instead of `BlurMaskFilter`, which hardware acceleration ignores before API 28.
 */
fun Modifier.taroGlow(enabled: Boolean = true, shape: Shape? = null): Modifier =
    this then TaroGlowElement(enabled, shape)

/** Halo layers: stroke width in dp to glow alpha, widest and faintest last. */
private val GlowLayers = listOf(2f to 0.55f, 6f to 0.3f, 12f to 0.15f, 20f to 0.07f)
private const val BORDER_WIDTH_DP = 1.5f

private data class TaroGlowElement(val enabled: Boolean, val shape: Shape?) : ModifierNodeElement<TaroGlowNode>() {
    override fun create() = TaroGlowNode(enabled, shape)

    override fun update(node: TaroGlowNode) = node.update(enabled, shape)

    override fun InspectorInfo.inspectableProperties() {
        name = "taroGlow"
        properties["enabled"] = enabled
        properties["shape"] = shape
    }
}

private class TaroGlowNode(
    private var enabled: Boolean,
    private var shape: Shape?,
) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {

    // Outline cache: rebuilt only when the size, direction, density or shape changes.
    private var cachedOutline: Outline? = null
    private var cachedSize = Size.Unspecified
    private var cachedDirection: LayoutDirection? = null
    private var cachedDensity = 0f
    private var cachedShape: Shape? = null
    private var glowStrokes: List<Pair<Stroke, Float>> = emptyList()
    private var borderStroke = Stroke()

    fun update(enabled: Boolean, shape: Shape?) {
        if (this.enabled == enabled && this.shape == shape) return
        this.enabled = enabled
        this.shape = shape
        invalidateDraw()
    }

    override fun ContentDrawScope.draw() {
        if (!enabled) {
            drawContent()
            return
        }
        // Reading CompositionLocals here re-runs draw when the theme changes.
        val colors = currentValueOf(LocalTaroExtendedColors)
        val outlineShape = shape ?: currentValueOf(LocalTaroShapes).tarotCard
        val outline = outlineFor(outlineShape)
        glowStrokes.forEach { (stroke, alpha) -> drawOutline(outline, colors.glow, alpha = alpha, style = stroke) }
        drawContent()
        drawOutline(outline, colors.cardBorder, style = borderStroke)
    }

    private fun ContentDrawScope.outlineFor(outlineShape: Shape): Outline {
        val cached = cachedOutline
        if (cached != null && cachedSize == size && cachedDirection == layoutDirection &&
            cachedDensity == density && cachedShape == outlineShape
        ) {
            return cached
        }
        cachedSize = size
        cachedDirection = layoutDirection
        cachedDensity = density
        cachedShape = outlineShape
        glowStrokes = GlowLayers.map { (width, alpha) -> Stroke(width = width.dp.toPx()) to alpha }
        borderStroke = Stroke(width = BORDER_WIDTH_DP.dp.toPx())
        return outlineShape.createOutline(size, layoutDirection, this).also { cachedOutline = it }
    }
}
