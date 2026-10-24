package com.bobon.mygametop.domain.localization

import java.util.Locale

data class AppLanguage(val tag: String, val nativeName: String)

object AppLanguages {
    val supported = listOf(
        AppLanguage("en", "English"), AppLanguage("ru", "Русский"), AppLanguage("be", "Беларуская"),
        AppLanguage("uk", "Українська"), AppLanguage("de", "Deutsch"), AppLanguage("fr", "Français"),
        AppLanguage("es", "Español"), AppLanguage("it", "Italiano"), AppLanguage("pt", "Português"),
        AppLanguage("nl", "Nederlands"), AppLanguage("pl", "Polski"), AppLanguage("cs", "Čeština"), AppLanguage("ro", "Română")
    )
    fun normalize(tag: String) = tag.takeIf { value -> supported.any { it.tag == value } }.orEmpty()
    fun resolve(systemLocales: List<Locale>, tag: String): Locale {
        if (normalize(tag).isNotEmpty()) return Locale.forLanguageTag(tag)
        return systemLocales.firstOrNull { locale -> supported.any { it.tag == locale.language } } ?: Locale.ENGLISH
    }
}
