package com.taroshimaya.app.core.data

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages persisted per-app language preference (C19 / DI-01) via AndroidX AppCompat
 * per-app locales (`AppLocalesMetadataHolderService` on API 24–32, system `LocaleManager` on API 33+).
 */
class LanguageManager {
    private val _preference = MutableStateFlow(AppLanguagePreference.SYSTEM)
    val preference: StateFlow<AppLanguagePreference> = _preference.asStateFlow()

    /**
     * Synchronizes [preference] with AndroidX per-app locale state after `Activity.onCreate`.
     */
    fun syncOnLaunch() {
        val currentAppCompat = AppCompatDelegate.getApplicationLocales()
        val resolved = if (!currentAppCompat.isEmpty) {
            AppLanguagePreference.fromLanguageTag(currentAppCompat[0]?.toLanguageTag())
        } else {
            AppLanguagePreference.SYSTEM
        }
        _preference.value = resolved
    }

    /**
     * Applies user's explicit choice (`SYSTEM`, `VIETNAMESE`, or `ENGLISH`).
     * Passing `SYSTEM` resets per-app locale to empty so the device OS locale takes effect
     * (with unsupported device languages falling back to default `res/values/strings.xml`, Vietnamese).
     */
    fun selectLanguage(choice: AppLanguagePreference) {
        _preference.value = choice
        val desiredLocales = when (val tag = choice.languageTag) {
            null -> LocaleListCompat.getEmptyLocaleList()
            else -> LocaleListCompat.forLanguageTags(tag)
        }
        if (AppCompatDelegate.getApplicationLocales().toLanguageTags() != desiredLocales.toLanguageTags()) {
            AppCompatDelegate.setApplicationLocales(desiredLocales)
        }
    }
}
