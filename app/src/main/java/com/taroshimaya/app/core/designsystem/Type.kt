package com.taroshimaya.app.core.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.taroshimaya.app.R

/**
 * Typography tokens (D10, DI-03): full Material 3 type scale on two bundled fonts.
 *
 * - Titles (`display*`, `headline*`, `titleLarge`) use [TaroFontFamilies.Display], **Fraunces** (72pt optical size),
 *   a soft old-style serif close to the tarot widget reference. Display styles use Regular 400, headlines and
 *   titleLarge use SemiBold 600; both weights are bundled, and fake bold is off ([FontSynthesis.None]) so a weight
 *   that is not bundled never gets smeared. Line height is 1.3× font size or more, and that extra room is kept above
 *   the first line (no top trim): Fraunces capitals with stacked Vietnamese marks (Ẫ, Ữ) rise above the ascent.
 * - Everything functional (buttons, chips, inputs, body copy, labels) uses [TaroFontFamilies.Body],
 *   **Be Vietnam Pro** 400/500/600/700, a neutral sans built for Vietnamese.
 *
 * Licensing: both fonts are SIL OFL 1.1; the licences ship in `assets/licenses/` (Fraunces-OFL.txt,
 * BeVietnamPro-OFL.txt). To swap a font, replace its files in `res/font` and its family here; nothing else changes.
 *
 * Copy rule: UI text never joins words with a middle dot (·) or an em dash (—). Write a sentence or break the line.
 *
 * Read styles via `TaroshimayaTheme.typography.<role>` (or `TaroshimayaTheme.eyebrow`); never set fontSize or
 * fontFamily directly in features.
 */
object TaroFontFamilies {
    val Display: FontFamily = FontFamily(
        Font(R.font.fraunces_regular, FontWeight.Normal),
        Font(R.font.fraunces_semibold, FontWeight.SemiBold),
    )
    val Body: FontFamily = FontFamily(
        Font(R.font.be_vietnam_pro_regular, FontWeight.Normal),
        Font(R.font.be_vietnam_pro_medium, FontWeight.Medium),
        Font(R.font.be_vietnam_pro_semibold, FontWeight.SemiBold),
        Font(R.font.be_vietnam_pro_bold, FontWeight.Bold),
    )
}

private fun title(weight: FontWeight, size: TextUnit, lineHeight: TextUnit) = TextStyle(
    fontFamily = TaroFontFamilies.Display,
    fontWeight = weight,
    fontSynthesis = FontSynthesis.None,
    fontSize = size,
    lineHeight = lineHeight,
    // Marks on Fraunces capitals rise above its ascent; keep the extra line height above line 1 instead of trimming it.
    lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Proportional, LineHeightStyle.Trim.None),
    letterSpacing = 0.sp,
)

private fun body(weight: FontWeight, size: TextUnit, lineHeight: TextUnit, letterSpacing: TextUnit) = TextStyle(
    fontFamily = TaroFontFamilies.Body,
    fontWeight = weight,
    fontSize = size,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing,
)

internal val TaroTypography: Typography = Typography(
    displayLarge = title(FontWeight.Normal, 57.sp, 76.sp),
    displayMedium = title(FontWeight.Normal, 45.sp, 60.sp),
    displaySmall = title(FontWeight.Normal, 36.sp, 48.sp),
    headlineLarge = title(FontWeight.SemiBold, 32.sp, 42.sp),
    headlineMedium = title(FontWeight.SemiBold, 28.sp, 38.sp),
    headlineSmall = title(FontWeight.SemiBold, 24.sp, 32.sp),
    titleLarge = title(FontWeight.SemiBold, 22.sp, 30.sp),
    titleMedium = body(FontWeight.SemiBold, 16.sp, 24.sp, 0.15.sp),
    titleSmall = body(FontWeight.SemiBold, 14.sp, 20.sp, 0.1.sp),
    bodyLarge = body(FontWeight.Normal, 16.sp, 24.sp, 0.5.sp),
    bodyMedium = body(FontWeight.Normal, 14.sp, 20.sp, 0.25.sp),
    bodySmall = body(FontWeight.Normal, 12.sp, 16.sp, 0.4.sp),
    labelLarge = body(FontWeight.Medium, 14.sp, 20.sp, 0.1.sp),
    labelMedium = body(FontWeight.Medium, 12.sp, 16.sp, 0.5.sp),
    labelSmall = body(FontWeight.Medium, 11.sp, 16.sp, 0.5.sp),
)

/**
 * Eyebrow: a tiny, widely tracked section label above a title (the small-caps kicker style of harrypotter.com).
 * Callers uppercase the text themselves (`text.uppercase()`); the style only sets font, size and tracking.
 */
internal val TaroEyebrow: TextStyle = body(FontWeight.Medium, 10.sp, 14.sp, 3.2.sp)
