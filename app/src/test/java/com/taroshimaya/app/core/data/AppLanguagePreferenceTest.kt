package com.taroshimaya.app.core.data

import org.junit.Assert.assertEquals
import org.junit.Test

class AppLanguagePreferenceTest {

    @Test
    fun fromLanguageTag_resolvesSupportedTagsAndFallbacks() {
        assertEquals(AppLanguagePreference.SYSTEM, AppLanguagePreference.fromLanguageTag(null))
        assertEquals(AppLanguagePreference.SYSTEM, AppLanguagePreference.fromLanguageTag(""))
        assertEquals(AppLanguagePreference.SYSTEM, AppLanguagePreference.fromLanguageTag("   "))
        assertEquals(AppLanguagePreference.VIETNAMESE, AppLanguagePreference.fromLanguageTag("vi"))
        assertEquals(AppLanguagePreference.VIETNAMESE, AppLanguagePreference.fromLanguageTag("vi-VN"))
        assertEquals(AppLanguagePreference.VIETNAMESE, AppLanguagePreference.fromLanguageTag("vi_VN"))
        assertEquals(AppLanguagePreference.ENGLISH, AppLanguagePreference.fromLanguageTag("en"))
        assertEquals(AppLanguagePreference.ENGLISH, AppLanguagePreference.fromLanguageTag("en-US"))
        assertEquals(AppLanguagePreference.ENGLISH, AppLanguagePreference.fromLanguageTag("EN_GB"))
        assertEquals(AppLanguagePreference.SYSTEM, AppLanguagePreference.fromLanguageTag("fr-FR"))
    }

    @Test
    fun resolveEffectiveLocaleTag_explicitPreferenceOverridesDeviceLocale() {
        assertEquals(
            "vi",
            AppLanguagePreference.resolveEffectiveLocaleTag(
                preference = AppLanguagePreference.VIETNAMESE,
                deviceLanguageTags = listOf("en-US"),
            ),
        )
        assertEquals(
            "en",
            AppLanguagePreference.resolveEffectiveLocaleTag(
                preference = AppLanguagePreference.ENGLISH,
                deviceLanguageTags = listOf("vi-VN"),
            ),
        )
    }

    @Test
    fun resolveEffectiveLocaleTag_systemFollowsDeviceOrDefaultsToVietnamese() {
        assertEquals(
            "en",
            AppLanguagePreference.resolveEffectiveLocaleTag(
                preference = AppLanguagePreference.SYSTEM,
                deviceLanguageTags = listOf("en-US"),
            ),
        )
        assertEquals(
            "vi",
            AppLanguagePreference.resolveEffectiveLocaleTag(
                preference = AppLanguagePreference.SYSTEM,
                deviceLanguageTags = listOf("vi-VN"),
            ),
        )
        assertEquals(
            "vi",
            AppLanguagePreference.resolveEffectiveLocaleTag(
                preference = AppLanguagePreference.SYSTEM,
                deviceLanguageTags = listOf("fr-FR", "ja-JP"),
            ),
        )
        assertEquals(
            "vi",
            AppLanguagePreference.resolveEffectiveLocaleTag(
                preference = AppLanguagePreference.SYSTEM,
                deviceLanguageTags = emptyList(),
            ),
        )
    }
}
