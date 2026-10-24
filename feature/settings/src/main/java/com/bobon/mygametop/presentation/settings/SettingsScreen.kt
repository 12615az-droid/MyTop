package com.bobon.mygametop.presentation.settings

import com.bobon.mygametop.core.ui.R
import com.bobon.mygametop.localization.s

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import com.bobon.mygametop.domain.localization.AppLanguages
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, onAppearance: () -> Unit, onSync: () -> Unit, viewModel: SettingsViewModel = koinViewModel()) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    var chooseLanguage by rememberSaveable { mutableStateOf(false) }
    Scaffold(topBar = {
        TopAppBar(title = { Text(s(R.string.settings)) }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, s(R.string.back)) }
        })
    }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(s(R.string.make_it_yours), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(s(R.string.settings_intro), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Card(onClick = onAppearance, shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Icon(Icons.Default.Palette, null, Modifier.size(32.dp))
                    Column(Modifier.weight(1f)) {
                        Text(s(R.string.appearance), style = MaterialTheme.typography.titleLarge)
                        Text(s(R.string.appearance_summary), style = MaterialTheme.typography.bodyMedium)
                    }
                    Icon(Icons.Default.ChevronRight, null)
                }
            }
            Card(onClick = { chooseLanguage = true }, shape = MaterialTheme.shapes.large) {
                ListItem(headlineContent = { Text(s(R.string.language)) }, supportingContent = {
                    Text(AppLanguages.supported.find { it.tag == settings.languageTag }?.nativeName ?: s(R.string.system_language))
                }, leadingContent = { Icon(Icons.Default.Language, null) }, trailingContent = { Icon(Icons.Default.ChevronRight, null) })
            }
            Text(s(R.string.other_settings), style = MaterialTheme.typography.titleMedium)
            Card(shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
                ListItem(headlineContent = { Text(s(R.string.notifications)) }, supportingContent = { Text(s(R.string.notifications_soon)) },
                    leadingContent = { Icon(Icons.Default.NotificationsNone, null) }, colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow))
                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                Card(onClick = onSync, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
                    ListItem(headlineContent = { Text(s(R.string.sync)) }, supportingContent = { Text(s(R.string.sync_client_note)) },
                        leadingContent = { Icon(Icons.Default.CloudQueue, null) }, trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow))
                }
            }
            Text(s(R.string.settings_soon),
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    if (chooseLanguage) AlertDialog(
        onDismissRequest = { chooseLanguage = false }, title = { Text(s(R.string.language)) },
        confirmButton = { TextButton(onClick = { chooseLanguage = false }) { Text(s(R.string.close)) } },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text(s(R.string.language_note), style = MaterialTheme.typography.bodySmall)
                (listOf(com.bobon.mygametop.domain.localization.AppLanguage("", s(R.string.system_language))) + AppLanguages.supported).forEach { language ->
                    TextButton(onClick = { chooseLanguage = false; viewModel.update { it.copy(languageTag = language.tag) } }, modifier = Modifier.fillMaxWidth()) {
                        Text(language.nativeName, Modifier.weight(1f))
                        if (settings.languageTag == language.tag) Icon(Icons.Default.Check, null)
                    }
                }
            }
        }
    )
}
