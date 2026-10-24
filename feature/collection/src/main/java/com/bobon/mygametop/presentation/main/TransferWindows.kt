package com.bobon.mygametop.presentation.main

import com.bobon.mygametop.core.ui.R
import com.bobon.mygametop.localization.s

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bobon.mygametop.domain.transfer.GameExportFormat
import com.bobon.mygametop.domain.transfer.GameExportContent
import com.bobon.mygametop.domain.transfer.GameExportOptions
import com.bobon.mygametop.domain.transfer.GameExportScope

internal enum class ExportDestination { FILE, CLIPBOARD }

internal val importExample = """
    {
      "version": 1,
      "games": [
        {
          "title": "Portal 2",
          "rating": 9.5,
          "status": "COMPLETED",
          "completions": 1,
          "genres": ["Puzzle"],
          "platforms": ["PC"],
          "releaseYear": 2011
        }
      ]
    }
""".trimIndent()

@Composable
internal fun ImportWindow(onDismiss: () -> Unit, onSelectFile: () -> Unit) {
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    TransferWindow(s(R.string.import_json), s(R.string.choose_json), onDismiss, onSelectFile) {
        Text(s(R.string.import_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(s(R.string.import_intro))
        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh) {
            SelectionContainer {
                Text(importExample, Modifier.fillMaxWidth().padding(16.dp), fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodySmall)
            }
        }
        OutlinedButton(onClick = { clipboard.setText(AnnotatedString(importExample)); copied = true }) {
            Text(if (copied) s(R.string.copied) else s(R.string.copy_example))
        }
        Text(s(R.string.required_fields), style = MaterialTheme.typography.titleMedium)
        Text(s(R.string.required_help))
        Text(s(R.string.statuses), style = MaterialTheme.typography.titleMedium)
        Text(s(R.string.statuses_help))
        Text(s(R.string.optional_fields), style = MaterialTheme.typography.titleMedium)
        Text(s(R.string.optional_help))
        Text(s(R.string.import_how), style = MaterialTheme.typography.titleMedium)
        Text(s(R.string.import_behavior))
        Text(s(R.string.import_limits),
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
internal fun ExportWindow(totalCount: Int, filteredCount: Int, onDismiss: () -> Unit,
    onExport: (GameExportOptions, ExportDestination) -> Unit) {
    var format by rememberSaveable { mutableStateOf(GameExportFormat.JSON) }
    var scope by rememberSaveable { mutableStateOf(GameExportScope.ALL) }
    var destination by rememberSaveable { mutableStateOf(ExportDestination.FILE) }
    var content by rememberSaveable { mutableStateOf(GameExportContent.FULL) }
    var metadata by rememberSaveable { mutableStateOf(true) }
    var history by rememberSaveable { mutableStateOf(true) }
    var opinion by rememberSaveable { mutableStateOf(true) }
    var cover by rememberSaveable { mutableStateOf(true) }
    val count = if (scope == GameExportScope.ALL) totalCount else filteredCount
    val action = if (destination == ExportDestination.FILE) s(R.string.save_file_count, count) else s(R.string.copy_count, count)
    TransferWindow(s(R.string.export_settings), action, onDismiss, {
        onExport(GameExportOptions(format, scope, metadata, history, opinion, cover, content), destination)
    }) {
        Text(s(R.string.export_destination), style = MaterialTheme.typography.titleLarge)
        ChoiceRow(s(R.string.to_file), s(R.string.to_file_help), destination == ExportDestination.FILE) { destination = ExportDestination.FILE }
        ChoiceRow(s(R.string.to_clipboard), s(R.string.to_clipboard_help), destination == ExportDestination.CLIPBOARD) { destination = ExportDestination.CLIPBOARD }
        HorizontalDivider()
        Text(s(R.string.file_format), style = MaterialTheme.typography.titleLarge)
        ChoiceRow("JSON", s(R.string.json_purpose), format == GameExportFormat.JSON) {
            format = GameExportFormat.JSON
            content = GameExportContent.FULL
        }
        ChoiceRow("TXT", s(R.string.txt_purpose), format == GameExportFormat.TEXT) { format = GameExportFormat.TEXT }
        HorizontalDivider()
        Text(s(R.string.which_games), style = MaterialTheme.typography.titleLarge)
        ChoiceRow(s(R.string.whole_collection), s(R.string.games_count, totalCount), scope == GameExportScope.ALL) { scope = GameExportScope.ALL }
        ChoiceRow(s(R.string.current_selection), s(R.string.filtered_count, filteredCount), scope == GameExportScope.FILTERED) { scope = GameExportScope.FILTERED }
        Text(s(R.string.export_order), style = MaterialTheme.typography.bodySmall)
        HorizontalDivider()
        Text(s(R.string.export_content), style = MaterialTheme.typography.titleLarge)
        ChoiceRow(s(R.string.full_report), s(R.string.full_report_help), content == GameExportContent.FULL) { content = GameExportContent.FULL }
        ChoiceRow(s(R.string.top_only), s(R.string.top_only_help), content == GameExportContent.TOP_ONLY) {
            content = GameExportContent.TOP_ONLY
            format = GameExportFormat.TEXT
        }
        if (content == GameExportContent.FULL) {
        Text(s(R.string.which_fields), style = MaterialTheme.typography.titleLarge)
        Text(s(R.string.required_export), style = MaterialTheme.typography.bodyMedium)
        FieldRow(s(R.string.game_details), s(R.string.metadata_help), metadata) { metadata = it }
        FieldRow(s(R.string.history), s(R.string.history_help), history) { history = it }
        FieldRow(s(R.string.opinion), s(R.string.opinion_help), opinion) { opinion = it }
        FieldRow(s(R.string.cover_links), s(R.string.cover_links_help), cover) { cover = it }
        if (format == GameExportFormat.JSON) {
            Text(s(R.string.export_warning),
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        }
    }
}

@Composable
private fun ChoiceRow(title: String, subtitle: String, selected: Boolean, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().selectable(selected, role = Role.RadioButton, onClick = onClick).padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        RadioButton(selected = selected, onClick = null)
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun FieldRow(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().toggleable(checked, role = Role.Checkbox, onValueChange = onChange).padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Checkbox(checked = checked, onCheckedChange = null)
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransferWindow(title: String, action: String, onDismiss: () -> Unit, onAction: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
        val lightBars = MaterialTheme.colorScheme.surface.luminance() > 0.5f
        SideEffect {
            dialogWindow?.let { window ->
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = lightBars
                    isAppearanceLightNavigationBars = lightBars
                }
            }
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(title = { Text(title) }, navigationIcon = {
                    IconButton(onClick = onDismiss) { Icon(Icons.AutoMirrored.Filled.ArrowBack, s(R.string.back)) }
                })
            },
            bottomBar = {
                Surface(tonalElevation = 3.dp) {
                    Button(onClick = onAction, modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) { Text(action) }
                }
            }
        ) { padding ->
            Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
        }
    }
}
