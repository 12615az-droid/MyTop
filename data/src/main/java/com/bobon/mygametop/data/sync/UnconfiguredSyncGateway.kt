package com.bobon.mygametop.data.sync

import com.bobon.mygametop.domain.sync.SyncGateway
import com.bobon.mygametop.domain.sync.SyncResult

class UnconfiguredSyncGateway : SyncGateway {
    override suspend fun synchronize(serverUrl: String) = SyncResult.SERVER_NOT_AVAILABLE
}
