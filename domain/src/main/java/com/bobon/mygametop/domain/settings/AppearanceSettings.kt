package com.bobon.mygametop.domain.settings

import java.util.Locale
import com.bobon.mygametop.domain.localization.AppLanguages

enum class ThemeMode { SYSTEM, LIGHT, DARK }
enum class AppFont { SYSTEM, SANS, SERIF, CONDENSED, MONO, CUSTOM }
enum class CornerStyle { SUBTLE, BALANCED, SOFT }

data class AppearanceSettings(
    val languageTag: String = "",
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColors: Boolean = true,
    val accentHex: String = "#6650A4",
    val font: AppFont = AppFont.SYSTEM,
    val textScale: Float = 1f,
    val corners: CornerStyle = CornerStyle.BALANCED,
    val compactCards: Boolean = false,
    val showRanks: Boolean = true,
    val customFontPath: String? = null,
    val customFontName: String? = null
) {
    fun sanitized(): AppearanceSettings = copy(
        languageTag = AppLanguages.normalize(languageTag),
        accentHex = normalizeAccent(accentHex) ?: "#6650A4",
        textScale = if (textScale.isFinite()) textScale.coerceIn(0.9f, 1.2f) else 1f,
        font = if (font == AppFont.CUSTOM && customFontPath.isNullOrBlank()) AppFont.SYSTEM else font
    )
}

fun normalizeAccent(input: String): String? {
    val value = input.trim().removePrefix("#")
    return if (value.matches(Regex("[0-9a-fA-F]{6}"))) "#${value.uppercase(Locale.ROOT)}" else null
}
