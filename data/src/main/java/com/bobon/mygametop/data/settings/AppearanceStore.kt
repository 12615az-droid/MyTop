package com.bobon.mygametop.data.settings

import com.bobon.mygametop.domain.settings.*

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class AppearanceStore(private val context: Context) : AppearanceRepository {
    private val preferences = context.getSharedPreferences("appearance", Context.MODE_PRIVATE)
    private val fontDirectory = File(context.filesDir, "fonts")
    private val initial = AppearanceCodec.decode(preferences.getString("settings", null)).let {
        if (it.customFontPath != null && !isOwnedFont(it.customFontPath)) {
            it.copy(font = if (it.font == AppFont.CUSTOM) AppFont.SYSTEM else it.font, customFontPath = null, customFontName = null)
        } else it
    }
    private val _settings = MutableStateFlow(initial)
    override val settings = _settings.asStateFlow()

    @Synchronized
    override fun update(transform: (AppearanceSettings) -> AppearanceSettings) {
        val next = transform(_settings.value).sanitized()
        preferences.edit { putString("settings", AppearanceCodec.encode(next)) }
        _settings.value = next
    }

    override fun reset() {
        val previous = _settings.value.customFontPath
        update { AppearanceSettings(languageTag = it.languageTag) }
        if (isOwnedFont(previous)) File(requireNotNull(previous)).delete()
    }

    override suspend fun importFont(documentUri: String): Unit = withContext(Dispatchers.IO) {
        val uri = Uri.parse(documentUri)
        check(fontDirectory.isDirectory || fontDirectory.mkdirs()) { "Не удалось создать папку шрифтов" }
        val destination = File(fontDirectory, "${UUID.randomUUID()}.font")
        try {
            val resolver = context.contentResolver
            val displayName = resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0) else null
            }?.take(120) ?: "Свой шрифт"
            resolver.openInputStream(uri)?.use { input ->
                destination.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    var total = 0
                    while (true) {
                        val count = input.read(buffer)
                        if (count == -1) break
                        total += count
                        require(total <= 5 * 1024 * 1024) { "Шрифт больше 5 МБ" }
                        output.write(buffer, 0, count)
                    }
                }
            } ?: error("Не удалось открыть шрифт")
            val header = destination.inputStream().use { input -> ByteArray(4).also { require(input.read(it) == 4) { "Пустой файл шрифта" } } }
            require(isSupportedFontHeader(header)) { "Выберите шрифт TTF или OTF" }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requireNotNull(Typeface.Builder(destination).build()) { "Повреждённый или неподдерживаемый шрифт" }
            } else {
                Typeface.createFromFile(destination)
            }
            val previous = _settings.value.customFontPath
            update { it.copy(font = AppFont.CUSTOM, customFontPath = destination.absolutePath, customFontName = displayName) }
            if (previous != destination.absolutePath && isOwnedFont(previous)) File(requireNotNull(previous)).delete()
        } catch (error: Throwable) {
            destination.delete()
            throw error
        }
    }

    private fun isOwnedFont(path: String?): Boolean = path != null && runCatching {
        val file = File(path)
        file.isFile && file.canonicalFile.parentFile == fontDirectory.canonicalFile
    }.getOrDefault(false)
}
