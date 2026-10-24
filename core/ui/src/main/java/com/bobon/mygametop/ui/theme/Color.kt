package com.bobon.mygametop.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt
import com.bobon.mygametop.domain.settings.normalizeAccent

/** Tonal surfaces and contrasting content colors retain Material 3's semantic roles. */
fun customColorScheme(accentHex: String, dark: Boolean): ColorScheme {
    val seed = (normalizeAccent(accentHex) ?: "#6650A4").toColorInt()
    val hsl = FloatArray(3).also { ColorUtils.colorToHSL(seed, it) }
    fun tone(lightness: Float, saturation: Float = hsl[1].coerceAtMost(0.75f), hue: Float = hsl[0]): Color =
        Color(ColorUtils.HSLToColor(floatArrayOf(hue % 360f, saturation, lightness)))
    fun neutral(lightness: Float) = tone(lightness, 0.025f)
    fun on(color: Color): Color = if (ColorUtils.calculateContrast(android.graphics.Color.WHITE, color.toArgb()) >=
        ColorUtils.calculateContrast(android.graphics.Color.BLACK, color.toArgb())) Color.White else Color.Black
    fun accessibleAccent(saturation: Float = hsl[1].coerceAtMost(0.75f), hue: Float = hsl[0]): Color {
        var lightness = if (dark) 0.80f else 0.38f
        val background = neutral(if (dark) 0.22f else 0.90f).toArgb()
        var result = tone(lightness, saturation, hue)
        while (ColorUtils.calculateContrast(result.toArgb(), background) < 4.5 && lightness in 0.02f..0.98f) {
            lightness += if (dark) 0.01f else -0.01f
            result = tone(lightness.coerceIn(0f, 1f), saturation, hue)
        }
        return result
    }
    val primary = accessibleAccent()
    val primaryContainer = tone(if (dark) 0.25f else 0.90f)
    val secondary = accessibleAccent(hsl[1] * 0.35f)
    val secondaryContainer = tone(if (dark) 0.25f else 0.90f, hsl[1] * 0.25f)
    val tertiary = accessibleAccent(hsl[1] * 0.65f, hsl[0] + 60f)
    val tertiaryContainer = tone(if (dark) 0.25f else 0.90f, hsl[1] * 0.55f, hsl[0] + 60f)
    val base = if (dark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = primary, onPrimary = on(primary), primaryContainer = primaryContainer, onPrimaryContainer = on(primaryContainer),
        inversePrimary = tone(if (dark) 0.38f else 0.80f),
        secondary = secondary, onSecondary = on(secondary), secondaryContainer = secondaryContainer, onSecondaryContainer = on(secondaryContainer),
        tertiary = tertiary, onTertiary = on(tertiary), tertiaryContainer = tertiaryContainer, onTertiaryContainer = on(tertiaryContainer),
        background = neutral(if (dark) 0.06f else 0.985f), onBackground = neutral(if (dark) 0.92f else 0.10f),
        surface = neutral(if (dark) 0.06f else 0.985f), onSurface = neutral(if (dark) 0.92f else 0.10f),
        surfaceVariant = tone(if (dark) 0.22f else 0.90f, 0.05f), onSurfaceVariant = neutral(if (dark) 0.80f else 0.30f),
        surfaceDim = neutral(if (dark) 0.06f else 0.87f), surfaceBright = neutral(if (dark) 0.24f else 0.985f),
        surfaceContainerLowest = neutral(if (dark) 0.04f else 1f),
        surfaceContainerLow = neutral(if (dark) 0.10f else 0.96f),
        surfaceContainer = neutral(if (dark) 0.12f else 0.94f),
        surfaceContainerHigh = neutral(if (dark) 0.17f else 0.92f),
        surfaceContainerHighest = neutral(if (dark) 0.22f else 0.90f),
        outline = neutral(if (dark) 0.60f else 0.46f), outlineVariant = neutral(if (dark) 0.30f else 0.79f),
        inverseSurface = neutral(if (dark) 0.90f else 0.20f), inverseOnSurface = neutral(if (dark) 0.20f else 0.95f),
        surfaceTint = primary
    )
}
