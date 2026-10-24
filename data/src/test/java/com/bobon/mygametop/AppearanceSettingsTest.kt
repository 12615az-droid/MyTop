package com.bobon.mygametop

import com.bobon.mygametop.domain.settings.*
import com.bobon.mygametop.data.settings.*
import org.junit.Assert.*
import org.junit.Test

class AppearanceSettingsTest {
    @Test fun languagePreferenceSurvivesSerializationAndUnsupportedLanguagesAreRejected() {
        com.bobon.mygametop.domain.localization.AppLanguages.supported.forEach { language ->
            val setting = AppearanceSettings(languageTag = language.tag)
            assertEquals(setting, AppearanceCodec.decode(AppearanceCodec.encode(setting)))
        }
        assertEquals("", AppearanceSettings(languageTag = "ja").sanitized().languageTag)
        assertEquals("", AppearanceSettings(languageTag = "ar").sanitized().languageTag)
        assertEquals("", AppearanceCodec.decode("{}").languageTag)
    }
    @Test fun preferencesRoundTripPreservesAllChoices() {
        val original = AppearanceSettings(themeMode = ThemeMode.DARK, dynamicColors = false, accentHex = "#2E7D60",
            font = AppFont.CUSTOM, customFontPath = "/private/fonts/sample.font", customFontName = "Мой шрифт.ttf",
            textScale = 1.15f, corners = CornerStyle.SOFT, compactCards = true, showRanks = false)
        assertEquals(original, AppearanceCodec.decode(AppearanceCodec.encode(original)))
    }

    @Test fun malformedPreferencesFallBackToUsableDefaults() {
        listOf(null, "", "broken", "[]", """{"themeMode":"UNKNOWN"}""").forEach {
            assertEquals(AppearanceSettings(), AppearanceCodec.decode(it))
        }
        assertEquals(AppearanceSettings(), AppearanceCodec.decode("{}"))
    }

    @Test fun savedPreferencesAcceptFutureFields() {
        val decoded = AppearanceCodec.decode("""{"themeMode":"LIGHT","futureSetting":true}""")
        assertEquals(ThemeMode.LIGHT, decoded.themeMode)
    }

    @Test fun customAccentAcceptsOnlyFullRgbAndNormalizesCase() {
        assertEquals("#7C4DFF", normalizeAccent("  7c4dff  "))
        listOf("", "#123", "#12345678", "purple", "#xx1122", "##123456").forEach { assertNull(normalizeAccent(it)) }
        assertEquals("#6650A4", AppearanceSettings(accentHex = "invalid").sanitized().accentHex)
    }

    @Test fun invalidTextSizeAndMissingCustomFontAreRepaired() {
        assertEquals(1f, AppearanceSettings(textScale = Float.NaN).sanitized().textScale, 0f)
        assertEquals(1.2f, AppearanceSettings(textScale = 4f).sanitized().textScale, 0f)
        assertEquals(0.9f, AppearanceSettings(textScale = 0f).sanitized().textScale, 0f)
        assertEquals(AppFont.SYSTEM, AppearanceSettings(font = AppFont.CUSTOM).sanitized().font)
    }

    @Test fun fontPickerRejectsNonFontFileHeaders() {
        assertTrue(isSupportedFontHeader(byteArrayOf(0, 1, 0, 0)))
        assertTrue(isSupportedFontHeader("OTTO".toByteArray()))
        assertFalse(isSupportedFontHeader("%PDF".toByteArray()))
        assertFalse(isSupportedFontHeader(byteArrayOf()))
        assertFalse(isSupportedFontHeader("OTTOjunk".toByteArray()))
    }
}
