package com.taroshimaya.app.core.designsystem

import androidx.compose.ui.graphics.Color

/**
 * Primitive color palette (D10 · DI-03): raw values only.
 *
 * Feature code must NOT import these directly: read semantic roles from
 * `TaroshimayaTheme.colors` (Material 3 roles) or `TaroshimayaTheme.extendedColors`
 * so every screen follows the active theme (G02, G08, T12).
 *
 * Sources: Mystic Night follows the "Kedy Mail" illustration reference (cobalt sky, ice-white stars, cyan sparks,
 * lilac haze); Moonlight follows the Taroshimaya brand video (frost paper, royal blue, coral, rose); Celestial Aurora follows the "Tarot Card Stream Widget" reference (indigo violet,
 * lavender glow, gold line art). Reference values: `docs/design/tokens.json`.
 *
 * Naming: within one hue family a higher number is always darker (50 lightest, 950 darkest).
 * A tint that does not fit that ramp gets a descriptive suffix instead of borrowing a number (`<Hue>Variant`,
 * `<Hue>Mist`, `<Hue>Ink`), so the scale stays trustworthy when picking colors.
 */
internal object TaroPalette {
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)

    // Error roles are one shared set (Material Theme Builder error tones), so error states read the same in every
    // theme. They are NOT the library defaults of lightColorScheme()/darkColorScheme(), so the explicit error
    // arguments in ColorSchemes.kt are required.
    val ErrorLight = Color(0xFFBA1A1A)
    val ErrorContainerLight = Color(0xFFFFDAD6)
    val OnErrorContainerLight = Color(0xFF410002)
    val ErrorDark = Color(0xFFFFB4AB)
    val OnErrorDark = Color(0xFF690005)
    val ErrorContainerDark = Color(0xFF93000A)
    val OnErrorContainerDark = Color(0xFFFFDAD6)

    // ---- Hue families of the brand video: Moonlight accents and the *Fixed roles shared by Mystic Night and Moonlight ----
    // Ice: icy blue glow.
    val Ice100 = Color(0xFFD9E6FF)
    val Ice300 = Color(0xFF8FB8FF)
    val Ice700 = Color(0xFF1F3D8A)
    val Ice950 = Color(0xFF06163F)
    // Coral: Moonlight secondary and card art.
    val Coral50 = Color(0xFFFFDBD2)
    val Coral100 = Color(0xFFFFDAD1)
    val Coral300 = Color(0xFFF4A58F)
    val Coral500 = Color(0xFFD9573C)
    val Coral700 = Color(0xFF983823)
    val Coral800 = Color(0xFF6E2A1B)
    val Coral900 = Color(0xFF4A1408)
    val Coral950 = Color(0xFF3D0C02)
    // Rose: Moonlight nebula and tertiary.
    val Rose100 = Color(0xFFFFD8EC)
    val Rose150 = Color(0xFFF2C6D8)
    val Rose300 = Color(0xFFF0A8D2)
    val Rose800 = Color(0xFF6F2350)
    val Rose900 = Color(0xFF4D1036)

    // ---- Mystic Night (dark: cobalt night sky of the "Kedy Mail" reference) ----
    // Nearly monochrome blue-violet; contrast comes from value, not from warm hues.
    // Cobalt: saturated ground, a shade deeper than the reference so long reading stays calm.
    // CobaltInk is the cat-silhouette navy used for text on light fills (card face, primary, accent).
    val Cobalt950 = Color(0xFF0A1048)
    val Cobalt900 = Color(0xFF111B6B)
    val Cobalt850 = Color(0xFF152173)
    val Cobalt800 = Color(0xFF1A287E)
    val Cobalt750 = Color(0xFF202F89)
    val Cobalt700 = Color(0xFF25358F)
    val Cobalt650 = Color(0xFF2A3B96)
    val Cobalt600 = Color(0xFF2F53B8)
    val Cobalt500 = Color(0xFF4F63C7)
    val CobaltVariant = Color(0xFF4152BE)
    val CobaltMist = Color(0xFF9DA9E8)
    val CobaltHaze = Color(0xFFD2DAF8)
    val CobaltInk = Color(0xFF0F1862)
    // Starlight: ice-white stars and their pale blue shading.
    val Starlight50 = Color(0xFFFDFDFE)
    val Starlight100 = Color(0xFFF4F6FF)
    val Starlight150 = Color(0xFFF0F6FF)
    val Starlight200 = Color(0xFFE3E6F6)
    val Starlight300 = Color(0xFFC8DEF6)
    // Orchid: the thin lilac haze on the horizon.
    val Orchid100 = Color(0xFFF3EAFF)
    val Orchid200 = Color(0xFFDCC8F4)
    val Orchid500 = Color(0xFF906CD8)
    val Orchid700 = Color(0xFF5A42AE)
    val Orchid950 = Color(0xFF2A1766)
    // Spark: cyan strokes and the glow around stars.
    val Spark100 = Color(0xFFE0F5FF)
    val Spark200 = Color(0xFF9EE0FF)
    val Spark300 = Color(0xFF84CCF0)
    val Spark700 = Color(0xFF1C5BA8)
    val Spark950 = Color(0xFF062F4D)

    // ---- Moonlight (light: frost paper, ink navy, royal blue) ----
    val Mist50 = Color(0xFFF5F7FD)
    val Mist100 = Color(0xFFEEF1FA)
    val Mist150 = Color(0xFFE8ECF7)
    val Mist200 = Color(0xFFE2E7F3)
    val Mist250 = Color(0xFFDCE2F0)
    val Mist300 = Color(0xFFD3DAEA)
    val Mist400 = Color(0xFFC5CCE0)
    val Slate600 = Color(0xFF6B7392)
    val Slate700 = Color(0xFF434C6E)
    val Slate900 = Color(0xFF121A3D)
    val Royal100 = Color(0xFFDCE4FF)
    val Royal300 = Color(0xFF7FA6FF)
    val Royal500 = Color(0xFF3D63D6)
    val Royal600 = Color(0xFF2F4FB8)
    val Royal700 = Color(0xFF1F3A8F)
    val Royal800 = Color(0xFF16286B)
    val Royal950 = Color(0xFF0A1B5C)
    val Berry100 = Color(0xFFFFD7EE)
    val Berry600 = Color(0xFF8A3570)
    val Berry950 = Color(0xFF3A0730)

    // ---- Classic Tarot (light: cream, crimson, ultramarine, mustard) ----
    val Cream50 = Color(0xFFFFFBF2)
    val Cream100 = Color(0xFFFBF5E6)
    val Cream150 = Color(0xFFF9F0DE)
    val Cream200 = Color(0xFFF7EFDC)
    val Cream250 = Color(0xFFF3E9D5)
    val Cream300 = Color(0xFFEDE3CF)
    val Cream350 = Color(0xFFE7DDC9)
    val Cream400 = Color(0xFFE3D8C3)
    val CreamVariant = Color(0xFFEFE0C8)
    val CreamGlow = Color(0xFFEFD9B0)
    val Cream600 = Color(0xFFD7C4A8)
    val Sepia600 = Color(0xFF857561)
    val Sepia700 = Color(0xFF524534)
    val Sepia800 = Color(0xFF3A2E25)
    val Sepia900 = Color(0xFF2B1D14)
    val SepiaLight = Color(0xFFFBEFE3)
    val Crimson600 = Color(0xFF9E1B1B)
    val Crimson800 = Color(0xFF7A1212)
    val Crimson900 = Color(0xFF5C0B1B)
    val Crimson100 = Color(0xFFFFDAD4)
    val Crimson200 = Color(0xFFFFB4A8)
    val Crimson950 = Color(0xFF410001)
    val Ultramarine600 = Color(0xFF1F3F8F)
    val Ultramarine100 = Color(0xFFDCE1FF)
    val Ultramarine200 = Color(0xFFB6C4FF)
    val Ultramarine950 = Color(0xFF001550)
    val Mustard500 = Color(0xFF8C6400)
    val Mustard600 = Color(0xFF7A5900)
    val Mustard800 = Color(0xFF5A4200)
    val Mustard100 = Color(0xFFFFDF9E)
    val Mustard200 = Color(0xFFF0C25A)
    val Mustard300 = Color(0xFFE0B860)
    val Mustard950 = Color(0xFF261A00)

    // ---- Celestial Aurora (dark: indigo violet, lavender glow, gold line art) ----
    val Aurora950 = Color(0xFF07061F)
    val Aurora900 = Color(0xFF0B0A2E)
    val Aurora850 = Color(0xFF110F3A)
    val Aurora800 = Color(0xFF171447)
    val Aurora750 = Color(0xFF1F1B57)
    val Aurora700 = Color(0xFF282367)
    val Aurora650 = Color(0xFF2F2A74)
    val AuroraVariant = Color(0xFF3A3470)
    val AuroraMist = Color(0xFF8A82BE)
    val AuroraHaze = Color(0xFFB9B2E0)
    val AuroraStar = Color(0xFFEEEAFF)
    val Violet50 = Color(0xFFF6F2FF)
    val Violet100 = Color(0xFFE6E0FF)
    val Violet300 = Color(0xFFA79CFF)
    val Violet500 = Color(0xFF7C6CF0)
    val Violet600 = Color(0xFF4A3FC4)
    val Violet700 = Color(0xFF3C30B4)
    val Violet750 = Color(0xFF3A2FA8)
    val Violet800 = Color(0xFF2E2390)
    val Violet950 = Color(0xFF1B1066)
    val Lilac100 = Color(0xFFECE4FF)
    val Lilac300 = Color(0xFFCCC0E4)
    val Lilac800 = Color(0xFF433A66)
    val Lilac950 = Color(0xFF2C2347)
    val Honey100 = Color(0xFFFFE3B5)
    val Honey300 = Color(0xFFF2C98A)
    val Honey800 = Color(0xFF5B3F0A)
    val Honey950 = Color(0xFF3D2700)

    // ---- Tarot elements (F08 encyclopedia, F12 statistics) ----
    val FireLight = Color(0xFFB3261E)
    val WaterLight = Color(0xFF1F5FA8)
    val AirLight = Color(0xFF7A6A00)
    val EarthLight = Color(0xFF2E6B30)
    val FireDark = Color(0xFFFF8A73)
    val WaterDark = Color(0xFF8CC2FF)
    val AirDark = Color(0xFFF2DC6B)
    val EarthDark = Color(0xFF8FD48F)

    // ---- Status (journal saved, streak kept, accuracy warnings) ----
    val SuccessLight = Color(0xFF256B2A)
    val SuccessContainerLight = Color(0xFFC6EFC0)
    val OnSuccessContainerLight = Color(0xFF002204)
    val WarningLight = Color(0xFF8A5100)
    val WarningContainerLight = Color(0xFFFFDDB8)
    val OnWarningContainerLight = Color(0xFF2C1600)
    val SuccessDark = Color(0xFF9DD69A)
    val OnSuccessDark = Color(0xFF00390A)
    val SuccessContainerDark = Color(0xFF0E5216)
    val OnSuccessContainerDark = Color(0xFFC6EFC0)
    val WarningDark = Color(0xFFFFB86E)
    val OnWarningDark = Color(0xFF4A2800)
    val WarningContainerDark = Color(0xFF693C00)
    val OnWarningContainerDark = Color(0xFFFFDDB8)
}
