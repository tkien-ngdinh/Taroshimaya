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

        /**
         * Maps an Android locale BCP-47 tag to an [AppLanguagePreference].
         * Non-supported tags or empty/null resolve to [SYSTEM].
         */
        fun fromLanguageTag(tag: String?): AppLanguagePreference {
            if (tag.isNullOrBlank()) return SYSTEM
            val primarySubtag = tag.substringBefore('-').substringBefore('_').lowercase(Locale.ROOT)
            return when (primarySubtag) {
                "vi" -> VIETNAMESE
                "en" -> ENGLISH
                else -> SYSTEM
            }
        }
    }
}
