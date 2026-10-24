package com.bobon.mygametop.localization

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import com.bobon.mygametop.domain.localization.AppLanguages

object AndroidLocales {
    fun wrap(context: Context, tag: String): Context {
        val config = Configuration(context.resources.configuration)
        val systemLocales = (0 until config.locales.size()).map { config.locales[it] }
        config.setLocales(LocaleList(AppLanguages.resolve(systemLocales, tag)))
        return context.createConfigurationContext(config)
    }
}
