package com.bobon.mygametop.localization

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.bobon.mygametop.domain.settings.AppearanceRepository
import java.util.Locale

@Composable
fun s(@StringRes id: Int, vararg args: Any): String =
    if (args.isEmpty()) stringResource(id) else stringResource(id, *args)

class AppStrings(private val context: Context, private val settings: AppearanceRepository) {
    private fun localized() = AndroidLocales.wrap(context, settings.settings.value.languageTag)
    val locale: Locale get() = localized().resources.configuration.locales[0]
    fun get(@StringRes id: Int, vararg args: Any): String {
        val context = localized()
        return if (args.isEmpty()) context.getString(id) else context.getString(id, *args)
    }
}
