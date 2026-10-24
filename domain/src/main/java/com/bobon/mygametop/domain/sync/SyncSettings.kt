package com.bobon.mygametop.domain.sync

import java.net.URI

data class SyncSettings(val enabled: Boolean = false, val serverUrl: String = "") {
    fun normalized() = copy(serverUrl = serverUrl.trim().trimEnd('/'))
    val validServerUrl: Boolean get() = runCatching {
        val uri = URI(serverUrl)
        uri.scheme.equals("https", ignoreCase = true) && !uri.host.isNullOrBlank() &&
            uri.userInfo == null && uri.query == null && uri.fragment == null &&
            (uri.port == -1 || uri.port in 1..65535)
    }.getOrDefault(false)
}
