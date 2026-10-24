package com.bobon.mygametop.ui.theme

import android.graphics.Typeface
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import com.bobon.mygametop.domain.settings.AppFont
import com.bobon.mygametop.domain.settings.AppearanceSettings
import java.io.File

internal fun appFontFamily(settings: AppearanceSettings): FontFamily = when (settings.font) {
    AppFont.SYSTEM -> FontFamily.Default
    AppFont.SANS -> FontFamily.SansSerif
    AppFont.SERIF -> FontFamily.Serif
    AppFont.MONO -> FontFamily.Monospace
    AppFont.CONDENSED -> FontFamily(Typeface.create("sans-serif-condensed", Typeface.NORMAL))
    AppFont.CUSTOM -> runCatching {
        FontFamily(Typeface.createFromFile(File(requireNotNull(settings.customFontPath))))
    }.getOrDefault(FontFamily.Default)
}

internal fun appTypography(family: FontFamily, scale: Float): Typography {
    val base = Typography()
    fun TextStyle.adapt() = copy(fontFamily = family, fontSize = fontSize * scale, lineHeight = lineHeight * scale)
    return base.copy(
        displayLarge = base.displayLarge.adapt(), displayMedium = base.displayMedium.adapt(), displaySmall = base.displaySmall.adapt(),
        headlineLarge = base.headlineLarge.adapt(), headlineMedium = base.headlineMedium.adapt(), headlineSmall = base.headlineSmall.adapt(),
        titleLarge = base.titleLarge.adapt(), titleMedium = base.titleMedium.adapt(), titleSmall = base.titleSmall.adapt(),
        bodyLarge = base.bodyLarge.adapt(), bodyMedium = base.bodyMedium.adapt(), bodySmall = base.bodySmall.adapt(),
        labelLarge = base.labelLarge.adapt(), labelMedium = base.labelMedium.adapt(), labelSmall = base.labelSmall.adapt()
    )
}
