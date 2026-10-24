package com.bobon.mygametop

import com.bobon.mygametop.domain.sync.*
import com.bobon.mygametop.data.sync.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class SyncClientTest {
    @Test fun defaultsAndBrokenPreferencesKeepSyncOff() {
        listOf(null, "", "bad json", "{}", "{\"enabled\":true}").forEach {
            assertFalse(SyncSettingsCodec.decode(it).enabled)
        }
    }

    @Test fun preferencesRoundTripAndNormalizeUrl() {
        val settings = SyncSettings(true, "  https://example.com/api/  ")
        assertEquals(SyncSettings(true, "https://example.com/api"), SyncSettingsCodec.decode(SyncSettingsCodec.encode(settings)))
    }

    @Test fun serverValidationRejectsMalformedOrCredentialBearingUrls() {
        listOf("", "http://example.com", "https://", "ftp://example.com", "https://user:secret@example.com",
            "https://example.com?q=1", "https://example.com/#test", "https://example.com:99999", "https://bad host").forEach {
            assertFalse(it, SyncSettings(true, it).validServerUrl)
        }
        assertTrue(SyncSettings(serverUrl = "https://example.com:8443/api").validServerUrl)
    }

    @Test fun disabledOrInvalidClientNeverCallsGateway() = runBlocking {
        var calls = 0
        val client = SyncClient(object : SyncGateway {
            override suspend fun synchronize(serverUrl: String): SyncResult {
                calls++
                return SyncResult.SERVER_NOT_AVAILABLE
            }
        })
        assertEquals(SyncResult.DISABLED, client.synchronize(SyncSettings()))
        assertEquals(SyncResult.INVALID_SERVER, client.synchronize(SyncSettings(true, "invalid")))
        assertEquals(0, calls)
        assertEquals(SyncResult.SERVER_NOT_AVAILABLE, client.synchronize(SyncSettings(true, "https://example.com")))
        assertEquals(1, calls)
    }

    @Test fun unconfiguredAdapterDoesNotReportSuccess() = runBlocking {
        assertEquals(SyncResult.SERVER_NOT_AVAILABLE,
            SyncClient(UnconfiguredSyncGateway()).synchronize(SyncSettings(true, "https://example.com")))
    }

    @Test(expected = CancellationException::class)
    fun cancellationPropagates() = runBlocking {
        SyncClient(object : SyncGateway {
            override suspend fun synchronize(serverUrl: String): SyncResult = throw CancellationException()
        }).synchronize(SyncSettings(true, "https://example.com"))
        Unit
    }
}
