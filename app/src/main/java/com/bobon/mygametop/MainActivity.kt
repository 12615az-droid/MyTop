package com.bobon.mygametop

import android.os.Bundle
import android.content.Context
import androidx.compose.runtime.LaunchedEffect
import com.bobon.mygametop.localization.AndroidLocales
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.android.ext.android.inject
import com.bobon.mygametop.domain.settings.AppearanceRepository
import com.bobon.mygametop.domain.settings.ThemeMode
import com.bobon.mygametop.ui.theme.MyApplicationTheme
import com.bobon.mygametop.navigation.MyTopNavHost


class MainActivity : ComponentActivity() {
    private val appearanceStore: AppearanceRepository by inject()
    private var appliedLanguage = ""
    override fun attachBaseContext(newBase: Context) {
        appliedLanguage = appearanceStore.settings.value.languageTag
        val localized = AndroidLocales.wrap(newBase, appliedLanguage)
        android.os.LocaleList.setDefault(localized.resources.configuration.locales)
        super.attachBaseContext(localized)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by appearanceStore.settings.collectAsStateWithLifecycle()
            LaunchedEffect(settings.languageTag) {
                if (settings.languageTag != appliedLanguage) recreate()
            }
            val dark = when (settings.themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            SideEffect {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !dark
                    isAppearanceLightNavigationBars = !dark
                }
            }
            MyApplicationTheme(settings) {
                MyTopNavHost()
            }
        }
    }
}
