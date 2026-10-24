package com.bobon.mygametop.presentation.main

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.bobon.mygametop.core.ui.R
import com.bobon.mygametop.localization.s

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobon.mygametop.domain.model.*
import com.bobon.mygametop.domain.transfer.GameExportFormat
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onAddClick: () -> Unit, onEditClick: (Long) -> Unit, onSettingsClick: () -> Unit, viewModel: MainViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val busy by viewModel.busy.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val context = LocalContext.current
    val clipboard = remember(context) { context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }
    var filesExpanded by remember { mutableStateOf(false) }
    var sortingExpanded by remember { mutableStateOf(false) }
    var showFilters by rememberSaveable { mutableStateOf(false) }
    var showImport by rememberSaveable { mutableStateOf(false) }
    var showExport by rememberSaveable { mutableStateOf(false) }
    var selectedGame by remember { mutableStateOf<Game?>(null) }
    var gameToDelete by remember { mutableStateOf<Game?>(null) }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) { showImport = false; viewModel.import(uri.toString()) }
    }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        viewModel.finishExport(uri?.toString())
    }
    val textExportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
        viewModel.finishExport(uri?.toString())
    }
    LaunchedEffect(viewModel) { viewModel.messages.collect { snackbar.showSnackbar(it) } }
    val clipboardLabel = s(R.string.app_name)
    LaunchedEffect(viewModel, clipboard, clipboardLabel) {
        viewModel.clipboard.collect { clipboard.setPrimaryClip(ClipData.newPlainText(clipboardLabel, it)) }
    }
    val filtersActive = state.query.status != null || state.query.genre != null || state.query.minimumRating > 0

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text(s(R.string.app_name), fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                actions = {
                    IconButton(onClick = onSettingsClick) { Icon(Icons.Default.Settings, s(R.string.settings)) }
                    Box {
                        IconButton(onClick = { filesExpanded = true }, enabled = !busy && !state.loading) {
                            Icon(Icons.Default.MoreVert, contentDescription = s(R.string.transfer))
                        }
                        DropdownMenu(expanded = filesExpanded, onDismissRequest = { filesExpanded = false }) {
                            DropdownMenuItem(text = { Text(s(R.string.import_json)) }, leadingIcon = { Icon(Icons.Default.FileDownload, null) },
                                onClick = { filesExpanded = false; showImport = true })
                            DropdownMenuItem(text = { Text(s(R.string.export_collection)) }, leadingIcon = { Icon(Icons.Default.FileUpload, null) },
                                onClick = { filesExpanded = false; showExport = true })
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onAddClick, icon = { Icon(Icons.Default.Add, null) }, text = { Text(s(R.string.add_game)) })
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            if (busy) LinearProgressIndicator(Modifier.fillMaxWidth())
            Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedTextField(
                    value = state.query.search, onValueChange = { viewModel.updateQuery(state.query.copy(search = it)) },
                    modifier = Modifier.fillMaxWidth(), placeholder = { Text(s(R.string.search_hint)) },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        if (state.query.search.isNotEmpty()) IconButton(onClick = { viewModel.updateQuery(state.query.copy(search = "")) }) {
                            Icon(Icons.Default.Close, s(R.string.clear_search))
                        }
                    }, singleLine = true, shape = MaterialTheme.shapes.large
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f)) {
                        AssistChip(onClick = { sortingExpanded = true }, label = { Text(state.query.sort.label(), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.Sort, null, Modifier.size(18.dp)) })
                        DropdownMenu(expanded = sortingExpanded, onDismissRequest = { sortingExpanded = false }) {
                            GameSort.entries.forEach { sort ->
                                DropdownMenuItem(text = { Text(sort.label()) }, onClick = {
                                    viewModel.updateQuery(state.query.copy(sort = sort)); sortingExpanded = false
                                }, trailingIcon = { if (sort == state.query.sort) Icon(Icons.Default.Check, null) })
                            }
                        }
                    }
                    FilterChip(selected = filtersActive, onClick = { showFilters = true }, label = { Text(s(R.string.filters)) }, leadingIcon = { Icon(Icons.Default.Tune, null, Modifier.size(18.dp)) })
                }
                Row(Modifier.fillMaxWidth().padding(bottom = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(s(R.string.shown_count, state.games.size, state.total), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(s(R.string.average_compact, state.averageRating?.let { formatRating(it.toFloat()) } ?: "—"), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
            when {
                state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                state.games.isEmpty() -> Column(
                    Modifier.weight(1f).fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.SportsEsports, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(16.dp))
                    Text(if (state.total == 0) s(R.string.empty_title) else s(R.string.no_results), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(if (state.total == 0) s(R.string.empty_help) else s(R.string.no_results_help), style = MaterialTheme.typography.bodyMedium)
                    if (state.total > 0) TextButton(onClick = { viewModel.updateQuery(GameQuery(sort = state.query.sort)) }) { Text(s(R.string.reset_search)) }
                }
                else -> LazyColumn(
                    Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(state.games, key = { _, game -> game.id }) { index, game ->
                        GameCard(game, index + 1, actionsEnabled = !busy,
                            onClick = { selectedGame = game }, onEdit = { onEditClick(game.id) }, onDelete = { gameToDelete = game })
                    }
                }
            }
        }
    }
    if (showFilters) {
        ModalBottomSheet(onDismissRequest = { showFilters = false }) {
            FilterPanel(state, viewModel::updateQuery, onDone = { showFilters = false })
        }
    }
    if (showImport) ImportWindow(
        onDismiss = { showImport = false },
        onSelectFile = { importLauncher.launch(arrayOf("application/json", "text/*", "application/octet-stream")) }
    )
    if (showExport) ExportWindow(
        totalCount = state.total, filteredCount = state.games.size,
        onDismiss = { showExport = false },
        onExport = { options, destination ->
            showExport = false
            if (destination == ExportDestination.CLIPBOARD) {
                viewModel.copyExport(options)
            } else {
                viewModel.prepareExport(options)
                when (options.format) {
                    GameExportFormat.JSON -> exportLauncher.launch("my-game-top.json")
                    GameExportFormat.TEXT -> textExportLauncher.launch("my-game-top.txt")
                }
            }
        }
    )
    selectedGame?.let { game ->
        GameDetails(game, actionsEnabled = !busy, onDismiss = { selectedGame = null },
            onEdit = { selectedGame = null; onEditClick(game.id) },
            onDelete = { selectedGame = null; gameToDelete = game })
    }
    gameToDelete?.let { game ->
        AlertDialog(
            onDismissRequest = { gameToDelete = null }, title = { Text(s(R.string.delete_question)) },
            text = { Text(s(R.string.delete_confirmation, game.title)) },
            confirmButton = {
                TextButton(enabled = !busy, onClick = { viewModel.deleteGame(game.id); gameToDelete = null }) {
                    Text(s(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { gameToDelete = null }) { Text(s(R.string.cancel)) } }
        )
    }
}
