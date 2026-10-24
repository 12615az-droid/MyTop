package com.bobon.mygametop.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bobon.mygametop.domain.settings.AppearanceSettings
import com.bobon.mygametop.domain.settings.CornerStyle
import com.bobon.mygametop.domain.settings.ThemeMode

val LocalAppearance = staticCompositionLocalOf { AppearanceSettings() }

@Composable
fun MyApplicationTheme(settings: AppearanceSettings = AppearanceSettings(), content: @Composable () -> Unit) {
    val dark = when (settings.themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val context = LocalContext.current
    val colors = if (settings.dynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else remember(settings.accentHex, dark) { customColorScheme(settings.accentHex, dark) }
    val font = remember(settings.font, settings.customFontPath) { appFontFamily(settings) }
    val typography = remember(font, settings.textScale) { appTypography(font, settings.textScale) }
    val shapes = remember(settings.corners) {
        val radius = when (settings.corners) { CornerStyle.SUBTLE -> 12; CornerStyle.BALANCED -> 20; CornerStyle.SOFT -> 28 }
        Shapes(
            extraSmall = RoundedCornerShape(4.dp), small = RoundedCornerShape((radius / 2).dp),
            medium = RoundedCornerShape((radius * 0.7f).dp), large = RoundedCornerShape(radius.dp),
            extraLarge = RoundedCornerShape((radius + 4).dp)
        )
    }
    CompositionLocalProvider(LocalAppearance provides settings) {
        MaterialTheme(colorScheme = colors, typography = typography, shapes = shapes, content = content)
    }
}
