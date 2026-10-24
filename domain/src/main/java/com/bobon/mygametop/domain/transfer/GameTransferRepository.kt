package com.bobon.mygametop.domain.transfer

/** Document identifiers are opaque strings; Android URI handling belongs to the data layer. */
interface GameTransferRepository {
    suspend fun import(documentUri: String): Int
    suspend fun export(documentUri: String, request: GameExportRequest): Int
    suspend fun render(request: GameExportRequest): Pair<String, Int>
}
