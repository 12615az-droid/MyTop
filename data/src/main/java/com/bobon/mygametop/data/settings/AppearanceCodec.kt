package com.bobon.mygametop.data.settings

import com.bobon.mygametop.domain.settings.*

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** Invalid or older preference data must never prevent the app from starting. */
internal object AppearanceCodec {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    fun encode(settings: AppearanceSettings): String = json.encodeToString(AppearancePreferences.from(settings.sanitized()))
    fun decode(value: String?): AppearanceSettings = runCatching {
        json.decodeFromString<AppearancePreferences>(value ?: "{}").toDomain().sanitized()
    }.getOrDefault(AppearanceSettings())
}

/** Storage schema stays compatible with existing preferences, independently of domain models. */
@Serializable
private data class AppearancePreferences(
    val languageTag: String = "",
    val themeMode: String = "SYSTEM",
    val dynamicColors: Boolean = true,
    val accentHex: String = "#6650A4",
    val font: String = "SYSTEM",
    val textScale: Float = 1f,
    val corners: String = "BALANCED",
    val compactCards: Boolean = false,
    val showRanks: Boolean = true,
    val customFontPath: String? = null,
    val customFontName: String? = null
) {
    fun toDomain() = AppearanceSettings(languageTag, ThemeMode.valueOf(themeMode), dynamicColors, accentHex,
        AppFont.valueOf(font), textScale, CornerStyle.valueOf(corners), compactCards, showRanks, customFontPath, customFontName)

    companion object {
        fun from(value: AppearanceSettings) = with(value) {
            AppearancePreferences(languageTag, themeMode.name, dynamicColors, accentHex, font.name,
                textScale, corners.name, compactCards, showRanks, customFontPath, customFontName)
        }
    }
}

internal fun isSupportedFontHeader(header: ByteArray): Boolean =
    header.contentEquals(byteArrayOf(0, 1, 0, 0)) ||
        (header.size == 4 && header.toString(Charsets.US_ASCII) in listOf("OTTO", "true", "typ1"))
