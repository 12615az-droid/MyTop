package com.bobon.mygametop.domain.sync

/** Result of a user-initiated synchronization. No success is reported by the unconfigured client. */
enum class SyncResult { DISABLED, INVALID_SERVER, SERVER_NOT_AVAILABLE }

/**
 * Boundary for the future server adapter. Implementations must propagate cancellation.
 * Account authorization, remote identity, revisions, deletions and conflict resolution must be
 * agreed with the backend before an adapter can write either local or remote game records.
 */
interface SyncGateway {
    suspend fun synchronize(serverUrl: String): SyncResult
}

class SyncClient(private val gateway: SyncGateway) {
    suspend fun synchronize(settings: SyncSettings): SyncResult {
        val normalized = settings.normalized()
        if (!normalized.enabled) return SyncResult.DISABLED
        if (!normalized.validServerUrl) return SyncResult.INVALID_SERVER
        return gateway.synchronize(normalized.serverUrl)
    }
}
