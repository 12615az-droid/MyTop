package com.bobon.mygametop.data.transfer

import android.content.ContentResolver
import android.net.Uri
import com.bobon.mygametop.domain.transfer.*
import com.bobon.mygametop.domain.repository.GameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class GameFileTransfer(private val resolver: ContentResolver, private val repository: GameRepository, private val strings: TextExportStrings) : GameTransferRepository {
    override suspend fun import(documentUri: String): Int = withContext(Dispatchers.IO) {
        val uri = Uri.parse(documentUri)
        val bytes = resolver.openInputStream(uri)?.use { input ->
            val output = java.io.ByteArrayOutputStream()
            val buffer = ByteArray(8192)
            var count = input.read(buffer)
            while (count != -1) {
                require(output.size() + count <= 10 * 1024 * 1024) { "Файл больше 10 МБ" }
                output.write(buffer, 0, count)
                count = input.read(buffer)
            }
            output.toByteArray()
        } ?: error("Не удалось открыть файл")
        val games = GameJson.decode(bytes.toString(Charsets.UTF_8).removePrefix("\uFEFF"))
        repository.importGames(games)
        games.size
    }

    override suspend fun export(documentUri: String, request: GameExportRequest): Int = withContext(Dispatchers.IO) {
        val uri = Uri.parse(documentUri)
        val (text, count) = render(request)
        val bytes = text.toByteArray(Charsets.UTF_8)
        if (request.options.format == GameExportFormat.JSON) require(bytes.size <= 10 * 1024 * 1024 && count <= 10_000) { "Коллекция превышает лимит JSON: 10 МБ / 10 000 игр" }
        resolver.openOutputStream(uri, "wt")?.use { it.write(bytes) } ?: error("Не удалось записать файл")
        count
    }

    override suspend fun render(request: GameExportRequest): Pair<String, Int> = withContext(Dispatchers.IO) {
        val options = request.options
        val games = options.select(repository.observeGames().first(), request.query)
        val text = when {
            options.content == GameExportContent.TOP_ONLY -> GameText.encodeTop(games, strings.locale)
            options.format == GameExportFormat.JSON -> GameJson.encode(games, options.omittedFields)
            else -> GameText.encode(games, options, { strings.get(it) }, strings.locale)
        }
        text to games.size
    }
}
