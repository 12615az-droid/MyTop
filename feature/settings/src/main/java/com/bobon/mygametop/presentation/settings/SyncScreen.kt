package com.bobon.mygametop.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobon.mygametop.core.ui.R
import com.bobon.mygametop.localization.s
import com.bobon.mygametop.domain.sync.SyncResult
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncScreen(onBack: () -> Unit, viewModel: SyncViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(topBar = {
        TopAppBar(title = { Text(s(R.string.sync)) }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, s(R.string.back)) }
        })
    }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(shape = MaterialTheme.shapes.large) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.CloudOff, null)
                    Text(s(R.string.sync_unavailable), style = MaterialTheme.typography.titleLarge)
                    Text(s(R.string.sync_client_note), style = MaterialTheme.typography.bodyMedium)
                }
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(Modifier.weight(1f)) {
                    Text(s(R.string.sync_enable), style = MaterialTheme.typography.titleMedium)
                    Text(s(if (state.saved.enabled) R.string.sync_enabled_pending else R.string.sync_disabled),
                        style = MaterialTheme.typography.bodySmall)
                }
                Switch(checked = state.saved.enabled, enabled = !state.busy, onCheckedChange = viewModel::setEnabled)
            }
            OutlinedTextField(value = state.serverUrl, onValueChange = viewModel::changeServerUrl,
                label = { Text(s(R.string.sync_server)) }, placeholder = { Text("https://example.com") },
                modifier = Modifier.fillMaxWidth(), singleLine = true, enabled = !state.busy, isError = state.invalidUrl,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                supportingText = { Text(s(if (state.invalidUrl) R.string.sync_invalid_url else R.string.sync_url_help)) })
            OutlinedButton(enabled = !state.busy, onClick = viewModel::saveSettings) { Text(s(R.string.sync_save)) }
            Button(enabled = state.canCheck, modifier = Modifier.fillMaxWidth(), onClick = viewModel::checkSynchronization) {
                Text(s(R.string.sync_now))
            }
            state.result?.let {
                Text(s(when (it) {
                    SyncResult.DISABLED -> R.string.sync_disabled
                    SyncResult.INVALID_SERVER -> R.string.sync_invalid_url
                    SyncResult.SERVER_NOT_AVAILABLE -> R.string.sync_unavailable
                }), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
