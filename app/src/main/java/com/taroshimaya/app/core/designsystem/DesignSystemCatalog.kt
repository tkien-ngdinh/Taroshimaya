package com.taroshimaya.app.core.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview

/**
 * Developer-only catalog of D10 tokens (DI-03) for Android Studio previews and PR screenshots.
 * Not reachable from app navigation. Labels are token names (code identifiers), not user-facing copy.
 * It shows the theme on [TaroBackground]: eyebrow and Fraunces titles, a stacked Vietnamese diacritic check,
 * three cards with the middle one in [taroGlow], chips and buttons in Be Vietnam Pro, and the key swatches.
 */
@Composable
internal fun DesignSystemCatalog(modifier: Modifier = Modifier) {
    val spacing = TaroshimayaTheme.spacing
    val colors = TaroshimayaTheme.colors
    val ext = TaroshimayaTheme.extendedColors
    val type = TaroshimayaTheme.typography

    TaroBackground(modifier = modifier) {
        Column(
            modifier = Modifier.padding(spacing.screenHorizontal),
            verticalArrangement = Arrangement.spacedBy(spacing.sectionGap),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                Text("eyebrow".uppercase(), style = TaroshimayaTheme.eyebrow, color = colors.onSurfaceVariant)
                Text(
                    text = stringResource(TaroshimayaTheme.variant.labelRes),
                    style = type.headlineMedium,
                    color = colors.onBackground,
                )
                // Stacked marks must render in full under Fraunces (no clipping at the top of the line).
                Text("ẢỘỮẰỶ ẤẬỨỰ", style = type.displaySmall, color = colors.onBackground)
            }

            Row(
                modifier = Modifier.padding(vertical = spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TarotCardFaceSample(numeral = "XVIII")
                TarotCardBackSample(modifier = Modifier.taroGlow())
                TarotCardFaceSample(numeral = "XIX")
            }

            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                FilterChip(selected = true, onClick = {}, label = { Text("selected") })
                FilterChip(selected = false, onClick = {}, label = { Text("chip") })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                Button(onClick = {}) { Text("primary") }
                OutlinedButton(onClick = {}) { Text("outlineVariant") }
            }

            SwatchRow(
                listOf(
                    "primary" to colors.primary,
                    "secondary" to colors.secondary,
                    "tertiary" to colors.tertiary,
                    "glow" to ext.glow,
                    "nebula" to ext.nebula,
                ),
            )
            SwatchRow(
                listOf(
                    "fire" to ext.elementFire,
                    "water" to ext.elementWater,
                    "air" to ext.elementAir,
                    "earth" to ext.elementEarth,
                ),
            )
            Text("bodyLarge", style = type.bodyLarge, color = colors.onSurfaceVariant)
        }
    }
}

@Composable
private fun SwatchRow(swatches: List<Pair<String, Color>>) {
    val spacing = TaroshimayaTheme.spacing
    Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
        swatches.forEach { (name, color) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                Box(
                    modifier = Modifier
                        .size(TaroshimayaTheme.sizing.avatarMedium)
                        .clip(TaroshimayaTheme.shapes.medium)
                        .background(color),
                )
                Text(
                    text = name,
                    style = TaroshimayaTheme.typography.labelSmall,
                    color = TaroshimayaTheme.colors.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun TarotCardFaceSample(numeral: String) {
    val ext = TaroshimayaTheme.extendedColors
    val sizing = TaroshimayaTheme.sizing
    val shape = TaroshimayaTheme.taroShapes.tarotCard
    Column(
        modifier = Modifier
            .width(sizing.tarotCardSmall)
            .aspectRatio(sizing.tarotCardAspectRatio)
            .clip(shape)
            .background(ext.cardFace)
            .border(sizing.borderThin, ext.cardBorder, shape)
            .padding(TaroshimayaTheme.spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(numeral, style = TaroshimayaTheme.typography.labelSmall, color = ext.cardInk)
        Canvas(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
            val star = size.minDimension * 0.8f
            translate((size.width - star) / 2f, (size.height - star) / 2f) {
                drawPath(sparklePath(star), ext.cardArt)
            }
        }
        Text("cardInk", style = TaroshimayaTheme.typography.labelSmall, color = ext.cardInk)
    }
}

@Composable
private fun TarotCardBackSample(modifier: Modifier = Modifier) {
    val ext = TaroshimayaTheme.extendedColors
    val sizing = TaroshimayaTheme.sizing
    val shape = TaroshimayaTheme.taroShapes.tarotCard
    Box(
        modifier = modifier
            .width(sizing.tarotCardSmall)
            .aspectRatio(sizing.tarotCardAspectRatio)
            .clip(shape)
            .background(ext.cardBack)
            .padding(TaroshimayaTheme.spacing.sm),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .border(sizing.borderThin, ext.cardBackPattern, TaroshimayaTheme.taroShapes.pill),
        )
    }
}

@Preview(name = "Mystic Night", widthDp = 360, heightDp = 800)
@Composable
private fun CatalogMysticNightPreview() {
    TaroshimayaTheme(variant = TaroThemeVariant.MYSTIC_NIGHT) { DesignSystemCatalog() }
}

@Preview(name = "Moonlight", widthDp = 360, heightDp = 800)
@Composable
private fun CatalogMoonlightPreview() {
    TaroshimayaTheme(variant = TaroThemeVariant.MOONLIGHT) { DesignSystemCatalog() }
}

@Preview(name = "Classic Tarot", widthDp = 360, heightDp = 800)
@Composable
private fun CatalogClassicTarotPreview() {
    TaroshimayaTheme(variant = TaroThemeVariant.CLASSIC_TAROT) { DesignSystemCatalog() }
}

@Preview(name = "Celestial Aurora", widthDp = 360, heightDp = 800)
@Composable
private fun CatalogCelestialAuroraPreview() {
    TaroshimayaTheme(variant = TaroThemeVariant.CELESTIAL_AURORA) { DesignSystemCatalog() }
}
