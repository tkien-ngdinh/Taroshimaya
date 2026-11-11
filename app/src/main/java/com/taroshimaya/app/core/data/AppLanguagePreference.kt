package com.taroshimaya.app.core.data

import java.util.Locale

/**
 * In-app language selector choices mandated by C19 (DI-01).
 *
 * Resource placement:
 * - Default (Vietnamese + fallback for non-EN/non-VI OS locales): `res/values/strings.xml`
 * - English: `res/values-en/strings.xml`
 */
enum class AppLanguagePreference(val languageTag: String?) {
    SYSTEM(null),
    VIETNAMESE("vi"),
    ENGLISH("en"),
    ;

    companion object {
        const val DEFAULT_FALLBACK_TAG = "vi"
        val SUPPORTED_LANGUAGE_TAGS = setOf("vi", "en")

        fun fromLanguageTag(tag: String?): AppLanguagePreference {
            if (tag.isNullOrBlank()) return SYSTEM
            val primarySubtag = tag.substringBefore('-').substringBefore('_').lowercase(Locale.ROOT)
            return when (primarySubtag) {
                "vi" -> VIETNAMESE
                "en" -> ENGLISH
                else -> SYSTEM
            }
        }

        /**
         * Resolves effective locale tag (`"vi"` or `"en"`) given user preference and OS locale list.
         *
         * Rule (DI-01 / C19):
         * - Explicit preference (`VIETNAMESE` / `ENGLISH`) wins.
         * - When `SYSTEM`, first supported device language (`vi` or `en`) wins.
         * - Any other device language defaults to Vietnamese (`"vi"`).
         */
        fun resolveEffectiveLocaleTag(
            preference: AppLanguagePreference,
            deviceLanguageTags: List<String>,
        ): String {
            preference.languageTag?.let { return it }
            for (tag in deviceLanguageTags) {
                val primary = tag.substringBefore('-').substringBefore('_').lowercase(Locale.ROOT)
                if (primary in SUPPORTED_LANGUAGE_TAGS) {
                    return primary
                }
            }
            return DEFAULT_FALLBACK_TAG
        }
    }
}
