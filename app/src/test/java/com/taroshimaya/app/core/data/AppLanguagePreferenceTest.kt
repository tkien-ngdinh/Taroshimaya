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
        assertEquals(AppLanguagePreference.SYSTEM, AppLanguagePreference.fromLanguageTag("ja-JP"))
        assertEquals(AppLanguagePreference.SYSTEM, AppLanguagePreference.fromLanguageTag("de"))
    }
}
