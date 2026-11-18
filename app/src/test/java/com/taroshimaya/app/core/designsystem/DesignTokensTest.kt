package com.taroshimaya.app.core.designsystem

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontListFontFamily
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.ResourceFont
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.taroshimaya.app.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Guards the D10 design tokens (DI-03): every theme stays complete, readable (WCAG AA) and consistent.
 */
class DesignTokensTest {

    private val variants = TaroThemeVariant.entries

    // ------------------------------------------------------------------ theme variants

    @Test
    fun ships4Themes_2Dark2Light_withUniqueStorageKeys() {
        assertEquals(4, variants.size)
        assertEquals(2, variants.count { it.isDark })
        assertEquals(2, variants.count { !it.isDark })
        assertEquals(variants.size, variants.map { it.storageKey }.toSet().size)
    }

    @Test
    fun defaultVariant_followsDeviceDarkSetting() {
        assertEquals(TaroThemeVariant.MYSTIC_NIGHT, TaroThemeVariant.default(systemInDarkTheme = true))
        assertEquals(TaroThemeVariant.MOONLIGHT, TaroThemeVariant.default(systemInDarkTheme = false))
    }

    @Test
    fun fromStorageKey_roundTripsAndFallsBackToDefault() {
        variants.forEach { variant ->
            assertEquals(variant, TaroThemeVariant.fromStorageKey(variant.storageKey, systemInDarkTheme = false))
        }
        assertEquals(TaroThemeVariant.MOONLIGHT, TaroThemeVariant.fromStorageKey(null, systemInDarkTheme = false))
        assertEquals(TaroThemeVariant.MYSTIC_NIGHT, TaroThemeVariant.fromStorageKey("unknown", systemInDarkTheme = true))
    }

    @Test
    fun schemeBrightness_matchesVariantIsDark() {
        variants.forEach { variant ->
            val background = TaroColorSchemes.colorScheme(variant).background
            if (variant.isDark) {
                assertTrue("$variant background should be dark", luminance(background) < 0.1)
            } else {
                assertTrue("$variant background should be light", luminance(background) > 0.7)
            }
        }
    }

    // ------------------------------------------------------------------ color contrast (WCAG AA)

    @Test
    fun materialTextPairs_meetWcagAa() {
        variants.forEach { variant ->
            val scheme = TaroColorSchemes.colorScheme(variant)
            textPairs(scheme).forEach { (name, pair) ->
                assertContrast("$variant $name", pair.first, pair.second, minimum = 4.5)
            }
        }
    }

    @Test
    fun surfaceText_isReadableOnEverySurfaceContainer() {
        // onSurface / onSurfaceVariant are body text; primary / secondary / tertiary / error are also used as text
        // (text buttons, selected navigation label, error messages), so all of them must read on every tier.
        variants.forEach { variant ->
            val scheme = TaroColorSchemes.colorScheme(variant)
            val textRoles = listOf(
                "onSurface" to scheme.onSurface,
                "onSurfaceVariant" to scheme.onSurfaceVariant,
                "primary" to scheme.primary,
                "secondary" to scheme.secondary,
                "tertiary" to scheme.tertiary,
                "error" to scheme.error,
            )
            surfaceTiers(scheme).forEach { (tier, bg) ->
                textRoles.forEach { (role, fg) ->
                    assertContrast("$variant $role/$tier", fg, bg, minimum = 4.5)
                }
            }
        }
    }

    @Test
    fun outline_isVisibleAsControlBoundaryOnEverySurface() {
        // WCAG 1.4.11: `outline` is the boundary of OutlinedTextField (material3 1.4.0) and must be ≥ 3:1 in light
        // AND dark themes. OutlinedButton / OutlinedCard / AssistChip borders use the decorative `outlineVariant`
        // and are intentionally not held to 3:1 (those components are identified by their label, not the border).
        variants.forEach { variant ->
            val s = TaroColorSchemes.colorScheme(variant)
            listOf(s.background, s.surface, s.surfaceContainer, s.surfaceContainerHighest).forEachIndexed { i, bg ->
                assertContrast("$variant outline/surface#$i", s.outline, bg, minimum = 3.0)
            }
        }
    }

    @Test
    fun extendedTextPairs_meetWcagAa() {
        variants.forEach { variant ->
            val ext = TaroColorSchemes.extendedColors(variant)
            listOf(
                "onAccent/accent" to (ext.onAccent to ext.accent),
                "onSuccess/success" to (ext.onSuccess to ext.success),
                "onSuccessContainer/successContainer" to (ext.onSuccessContainer to ext.successContainer),
                "onWarning/warning" to (ext.onWarning to ext.warning),
                "onWarningContainer/warningContainer" to (ext.onWarningContainer to ext.warningContainer),
            ).forEach { (name, pair) ->
                assertContrast("$variant $name", pair.first, pair.second, minimum = 4.5)
            }
        }
    }

    @Test
    fun chartAndElementColors_areVisibleOnSurface_andDistinct() {
        variants.forEach { variant ->
            val scheme = TaroColorSchemes.colorScheme(variant)
            val ext = TaroColorSchemes.extendedColors(variant)
            val elements = listOf(ext.elementFire, ext.elementWater, ext.elementAir, ext.elementEarth)

            assertEquals("$variant elements must differ", 4, elements.toSet().size)
            assertTrue("$variant needs ≥ 6 chart colors", ext.chartSeries.size >= 6)
            assertEquals("$variant chart colors must differ", ext.chartSeries.size, ext.chartSeries.toSet().size)
            // WCAG 1.4.11: graphical objects need ≥ 3:1 against their background (charts sit on either one).
            listOf("surface" to scheme.surface, "background" to scheme.background).forEach { (bgName, bg) ->
                (elements + ext.chartSeries + ext.majorArcana + ext.minorArcana).forEach { color ->
                    assertContrast("$variant graphic $color/$bgName", color, bg, minimum = 3.0)
                }
            }
        }
    }

    @Test
    fun accentAndCardBorder_areVisibleOnEverySurfaceTier() {
        // accent marks star glints / streak / coin highlights and cardBorder is the only edge of a tarot card,
        // so both are graphical objects (WCAG 1.4.11, ≥ 3:1) on whatever surface tier they are placed.
        // Modifier.border paints inside the card, on top of the card's own fill, so the border's real color is
        // cardBorder blended over cardFace / cardBack; that blended edge must stand out from the surface outside.
        variants.forEach { variant ->
            val ext = TaroColorSchemes.extendedColors(variant)
            val cardFills = listOf("cardFace" to ext.cardFace, "cardBack" to ext.cardBack)
            surfaceTiers(TaroColorSchemes.colorScheme(variant)).forEach { (tier, bg) ->
                assertContrast("$variant accent/$tier", ext.accent, bg, minimum = 3.0)
                cardFills.forEach { (fillName, fill) ->
                    val edge = ext.cardBorder.compositeOver(fill)
                    assertContrast("$variant cardBorder over $fillName/$tier", edge, bg, minimum = 3.0)
                }
            }
        }
    }

    @Test
    fun cardFaceContent_isReadable() {
        variants.forEach { v ->
            val ext = TaroColorSchemes.extendedColors(v)
            assertContrast("$v cardInk/cardFace", ext.cardInk, ext.cardFace, minimum = 4.5)
            assertContrast("$v cardArt/cardFace", ext.cardArt, ext.cardFace, minimum = 3.0)
        }
    }

    @Test
    fun refreshedThemes_matchTheReferencePalette() {
        val n = TaroColorSchemes.colorScheme(TaroThemeVariant.MYSTIC_NIGHT)
        assertEquals(Color(0xFF111B6B), n.background)
        assertEquals(Color(0xFFC8DEF6), n.primary)
        assertEquals(Color(0xFF84CCF0), TaroColorSchemes.extendedColors(TaroThemeVariant.MYSTIC_NIGHT).glow)
        val a = TaroColorSchemes.colorScheme(TaroThemeVariant.CELESTIAL_AURORA)
        assertEquals(Color(0xFF0B0A2E), a.background)
        assertEquals(Color(0xFFA79CFF), a.primary)
        val m = TaroColorSchemes.colorScheme(TaroThemeVariant.MOONLIGHT)
        assertEquals(Color(0xFFF5F7FD), m.background)
        assertEquals(Color(0xFF2F4FB8), m.primary)
    }

    @Test
    fun backgroundPattern_staysSubtleAndOnScreen() {
        assertTrue(TaroBackgroundDefaults.WheelAlphaDark in 0.1f..0.25f)
        assertTrue(TaroBackgroundDefaults.WheelAlphaLight in 0.08f..0.2f)
        assertEquals(4, TaroBackgroundDefaults.Sparkles.size)
        TaroBackgroundDefaults.Sparkles.forEach {
            assertTrue("sparkle $it must sit inside the frame", it.x in 0f..1f && it.y in 0f..1f)
            assertTrue("sparkle $it size", it.sizeDp in 6f..20f)
        }
    }

    @Test
    fun sparkles_avoidTheTitleAndStayVisibleAroundCards() {
        // Edge-to-edge app: the top ~15% holds the status bar and the top-bar title, which runs from the left
        // edge to about 60% of the width. A sparkle there would sit behind the title glyphs.
        // To stay visible, every sparkle sits where full-width content cards (16dp side margin) do not reach:
        // the outer 3.5% edge strips, the empty right part of the top bar, or the bottom 20% above the nav bar.
        TaroBackgroundDefaults.Sparkles.forEach {
            assertTrue("sparkle $it sits behind the top-bar title", it.y >= 0.15f || it.x >= 0.7f)
            val visible = it.x <= 0.035f || it.x >= 0.965f || it.y < 0.15f || it.y >= 0.8f
            assertTrue("sparkle $it is hidden behind full-width cards", visible)
        }
    }

    @Test
    fun fixedAccentRoles_areThemedAndReadable() {
        // Material 3 "fixed" roles are a light tint with dark content in every theme. They must come from the
        // Taro palette (not the library baseline) and both content roles must stay readable on both fills.
        variants.forEach { variant ->
            val s = TaroColorSchemes.colorScheme(variant)
            val baseline = if (variant.isDark) darkColorScheme() else lightColorScheme()
            listOf(
                Triple(
                    "primary",
                    listOf(s.primaryFixed, s.primaryFixedDim, s.onPrimaryFixed, s.onPrimaryFixedVariant),
                    listOf(
                        baseline.primaryFixed,
                        baseline.primaryFixedDim,
                        baseline.onPrimaryFixed,
                        baseline.onPrimaryFixedVariant,
                    ),
                ),
                Triple(
                    "secondary",
                    listOf(s.secondaryFixed, s.secondaryFixedDim, s.onSecondaryFixed, s.onSecondaryFixedVariant),
                    listOf(
                        baseline.secondaryFixed,
                        baseline.secondaryFixedDim,
                        baseline.onSecondaryFixed,
                        baseline.onSecondaryFixedVariant,
                    ),
                ),
                Triple(
                    "tertiary",
                    listOf(s.tertiaryFixed, s.tertiaryFixedDim, s.onTertiaryFixed, s.onTertiaryFixedVariant),
                    listOf(
                        baseline.tertiaryFixed,
                        baseline.tertiaryFixedDim,
                        baseline.onTertiaryFixed,
                        baseline.onTertiaryFixedVariant,
                    ),
                ),
            ).forEach { (role, themed, defaults) ->
                val (fixed, fixedDim, onFixed, onFixedVariant) = themed
                themed.zip(defaults).forEachIndexed { index, (mine, default) ->
                    assertNotEquals("$variant $role fixed role #$index is still the Material baseline", default, mine)
                }
                listOf("Fixed" to fixed, "FixedDim" to fixedDim).forEach { (fillName, fill) ->
                    assertContrast("$variant $role onFixed/$fillName", onFixed, fill, minimum = 4.5)
                    assertContrast("$variant $role onFixedVariant/$fillName", onFixedVariant, fill, minimum = 4.5)
                }
                assertTrue(
                    "$variant $role fixed fills must be light tints (Fixed lighter than FixedDim)",
                    luminance(fixed) > luminance(fixedDim) && luminance(fixedDim) > 0.3,
                )
            }
        }
    }

    @Test
    fun chartColor_wrapsAroundForAnyIndex() {
        val ext = TaroColorSchemes.extendedColors(TaroThemeVariant.MYSTIC_NIGHT)
        val size = ext.chartSeries.size
        assertEquals(ext.chartSeries[0], ext.chartColor(size))
        assertEquals(ext.chartSeries[size - 1], ext.chartColor(-1))
    }

    // ------------------------------------------------------------------ spacing, typography, motion

    @Test
    fun spacingScale_isAscendingOn4dpGrid() {
        val scale = TaroSpacing().scale.map { it.value }
        assertEquals(scale.sorted(), scale)
        assertEquals(scale.size, scale.toSet().size)
        scale.filter { it != TaroSpacing().xxs.value }.forEach { value ->
            assertEquals("spacing $value must be a multiple of 4", 0f, value % 4f)
        }
    }

    @Test
    fun touchTarget_meetsAccessibilityMinimum() {
        assertTrue(TaroSizing().minTouchTarget.value >= 48f)
    }

    @Test
    fun typography_usesFrauncesForTitlesAndBeVietnamProForEverythingElse() {
        titleStyles().forEach { assertEquals(TaroFontFamilies.Display, it.fontFamily) }
        val t = TaroTypography
        listOf(
            t.titleMedium, t.titleSmall, t.bodyLarge, t.bodyMedium, t.bodySmall,
            t.labelLarge, t.labelMedium, t.labelSmall,
        ).forEach { assertEquals(TaroFontFamilies.Body, it.fontFamily) }
        val sizes = listOf(t.displayLarge, t.headlineLarge, t.titleLarge, t.bodyLarge, t.labelSmall)
            .map { it.fontSize.value }
        assertEquals("type scale must shrink from display to label", sizes.sortedDescending(), sizes)
    }

    @Test
    fun fontFamilies_bundleTheExpectedFiles() {
        fun ids(f: FontFamily) = (f as FontListFontFamily).fonts.map { (it as ResourceFont).resId to it.weight.weight }
        assertEquals(
            listOf(R.font.fraunces_regular to 400, R.font.fraunces_semibold to 600),
            ids(TaroFontFamilies.Display),
        )
        assertEquals(
            listOf(
                R.font.be_vietnam_pro_regular to 400,
                R.font.be_vietnam_pro_medium to 500,
                R.font.be_vietnam_pro_semibold to 600,
                R.font.be_vietnam_pro_bold to 700,
            ),
            ids(TaroFontFamilies.Body),
        )
    }

    @Test
    fun titleStyles_leaveRoomForStackedVietnameseMarks_andUseOnlyBundledWeights() {
        // Fraunces 72pt: ascent + descent = 1.233 em and the tallest Vietnamese capitals (Ẫ, Ữ) rise 0.037 em above
        // the ascent, so the line needs ≥ 1.28 em; 1.3 leaves a small margin.
        titleStyles().forEach {
            assertTrue("${it.fontSize} line height too tight", it.lineHeight.value >= it.fontSize.value * 1.3f)
            assertEquals(FontSynthesis.None, it.fontSynthesis)
        }
        TaroTypography.run {
            listOf(displayLarge, displayMedium, displaySmall).forEach { assertEquals(FontWeight.Normal, it.fontWeight) }
            listOf(headlineLarge, headlineMedium, headlineSmall, titleLarge).forEach {
                assertEquals(FontWeight.SemiBold, it.fontWeight)
            }
        }
    }

    @Test
    fun uiStrings_neverJoinWordsWithMiddleDotsOrEmDashes() {
        // Copy rule (T12): UI text is written as sentences or line breaks, never "a · b" or "a — b".
        // Unit tests run from the module directory; XML comments are skipped, only string values are checked.
        val stringValue = Regex("<string[^>]*>(.*?)</string>", RegexOption.DOT_MATCHES_ALL)
        val files = java.io.File("src/main/res").walk().filter { it.isFile && it.name.startsWith("strings") && it.extension == "xml" }.toList()
        assertTrue("no strings files found", files.isNotEmpty())
        files.forEach { file ->
            stringValue.findAll(file.readText()).forEach { match ->
                val text = match.groupValues[1]
                assertTrue("${file.parentFile?.name ?: ""}/${file.name}: \"$text\"", '·' !in text && '—' !in text)
            }
        }
    }

    @Test
    fun bundledFonts_shipTheirOpenFontLicenses() {
        // Unit tests run from the module directory, so the asset path is relative to app/.
        listOf("BeVietnamPro-OFL.txt", "Fraunces-OFL.txt").forEach { name ->
            val license = java.io.File("src/main/assets/licenses/$name")
            assertTrue("$name missing", license.isFile)
            assertTrue("$name is not the SIL OFL", license.readText().contains("SIL Open Font License"))
        }
    }

    @Test
    fun titleStyles_keepExtraLineHeightAboveTheFirstLine() {
        // Fraunces SemiBold capitals with stacked marks reach 0.037 em above the font ascent, so the default
        // trim of the first line's top padding would clip marks such as Ẫ or Ữ on line 1.
        val keepTopRoom = LineHeightStyle(LineHeightStyle.Alignment.Proportional, LineHeightStyle.Trim.None)
        titleStyles().forEach { assertEquals("${it.fontSize}", keepTopRoom, it.lineHeightStyle) }
    }

    @Test
    fun eyebrow_isSmallSpacedBodyLabel() {
        assertEquals(TaroFontFamilies.Body, TaroEyebrow.fontFamily)
        assertEquals(10.sp, TaroEyebrow.fontSize)
        assertEquals(3.2.sp, TaroEyebrow.letterSpacing)
        assertEquals(FontWeight.Medium, TaroEyebrow.fontWeight)
    }

    private fun titleStyles() = TaroTypography.run {
        listOf(displayLarge, displayMedium, displaySmall, headlineLarge, headlineMedium, headlineSmall, titleLarge)
    }

    @Test
    fun reducedMotion_removesMovementAndScaleOffsets() {
        val reduced = TaroMotion.Reduced
        assertTrue(reduced.isReduced)
        listOf(
            reduced.durationPress,
            reduced.durationShort,
            reduced.durationMedium,
            reduced.durationLong,
            reduced.durationCardFlip,
            reduced.staggerDelay,
        ).forEach { assertEquals(0, it) }
        assertEquals(1f, reduced.pressScale)
        assertEquals(1f, reduced.enterScale)
    }

    @Test
    fun defaultMotion_staysSnappy() {
        val motion = TaroMotion.Default
        assertTrue("exits must not be slower than entries", motion.durationShort <= motion.durationMedium)
        assertTrue("UI animations stay ≤ 300 ms", motion.durationLong <= 300)
        assertTrue("press feedback is subtle", motion.pressScale in 0.95f..0.98f)
        assertTrue("never enter from scale 0", motion.enterScale >= 0.9f)
        assertTrue("stagger stays 30–80 ms", motion.staggerDelay in 30..80)
    }

    // ------------------------------------------------------------------ helpers

    private fun surfaceTiers(s: ColorScheme): List<Pair<String, Color>> = listOf(
        "background" to s.background,
        "surface" to s.surface,
        "surfaceContainerLowest" to s.surfaceContainerLowest,
        "surfaceContainerLow" to s.surfaceContainerLow,
        "surfaceContainer" to s.surfaceContainer,
        "surfaceContainerHigh" to s.surfaceContainerHigh,
        "surfaceContainerHighest" to s.surfaceContainerHighest,
        "surfaceBright" to s.surfaceBright,
        "surfaceDim" to s.surfaceDim,
    )

    private fun textPairs(s: ColorScheme): List<Pair<String, Pair<Color, Color>>> = listOf(
        "onBackground/background" to (s.onBackground to s.background),
        "onSurface/surface" to (s.onSurface to s.surface),
        "onSurfaceVariant/surfaceVariant" to (s.onSurfaceVariant to s.surfaceVariant),
        "onPrimary/primary" to (s.onPrimary to s.primary),
        "onPrimaryContainer/primaryContainer" to (s.onPrimaryContainer to s.primaryContainer),
        "onSecondary/secondary" to (s.onSecondary to s.secondary),
        "onSecondaryContainer/secondaryContainer" to (s.onSecondaryContainer to s.secondaryContainer),
        "onTertiary/tertiary" to (s.onTertiary to s.tertiary),
        "onTertiaryContainer/tertiaryContainer" to (s.onTertiaryContainer to s.tertiaryContainer),
        "onError/error" to (s.onError to s.error),
        "onErrorContainer/errorContainer" to (s.onErrorContainer to s.errorContainer),
        "inverseOnSurface/inverseSurface" to (s.inverseOnSurface to s.inverseSurface),
        "primary/surface (text buttons)" to (s.primary to s.surface),
    )

    private fun assertContrast(label: String, foreground: Color, background: Color, minimum: Double) {
        val ratio = contrastRatio(foreground, background)
        assertTrue("$label contrast %.2f < %.1f".format(ratio, minimum), ratio >= minimum)
    }

    /** WCAG 2.x contrast ratio, computed by hand so the test does not depend on Android framework classes. */
    private fun contrastRatio(a: Color, b: Color): Double {
        val la = luminance(a)
        val lb = luminance(b)
        return (max(la, lb) + 0.05) / (min(la, lb) + 0.05)
    }

    private fun luminance(color: Color): Double {
        fun channel(c: Float): Double {
            val v = c.toDouble()
            return if (v <= 0.04045) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)
        }
        return 0.2126 * channel(color.red) + 0.7152 * channel(color.green) + 0.0722 * channel(color.blue)
    }
}
