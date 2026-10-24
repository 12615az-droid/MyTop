package com.bobon.mygametop.presentation.add

import com.bobon.mygametop.core.ui.R
import com.bobon.mygametop.localization.s

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobon.mygametop.domain.model.GameStatus
import com.bobon.mygametop.presentation.common.GameCover
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddGameScreen(onBack: () -> Unit, onSaved: () -> Unit, gameId: Long? = null, viewModel: AddGameViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var statusExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(gameId) { viewModel.initialize(gameId) }

    LaunchedEffect(state.message) {
        state.message?.let { snackbarHostState.showSnackbar(it); viewModel.consumeMessage() }
    }
    LaunchedEffect(state.saved) {
        if (state.saved) { viewModel.consumeSaved(); onSaved() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) s(R.string.edit_game) else s(R.string.new_game), maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s(R.string.back)) }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { if (!state.isSaving && !state.isLoading && !state.loadFailed) viewModel.save() },
                expanded = state.title.isNotBlank(),
                icon = { Icon(Icons.Default.Check, contentDescription = null) },
                text = { Text(if (state.isSaving) s(R.string.saving) else s(R.string.save)) }
            )
        }
    ) { padding ->
        if (state.isLoading || state.loadFailed) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                if (state.isLoading) CircularProgressIndicator() else Text(s(R.string.game_unavailable))
            }
            return@Scaffold
        }
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionTitle(s(R.string.basics))
            OutlinedTextField(state.title, viewModel::onTitleChange, Modifier.fillMaxWidth(), label = { Text(s(R.string.title_required)) }, singleLine = true)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(s(R.string.completions_count, state.completions), style = MaterialTheme.typography.titleMedium)
                Row {
                    IconButton(enabled = state.completions > 0, onClick = { viewModel.onCompletionsChange(state.completions - 1) }) {
                        Icon(Icons.Default.Remove, contentDescription = s(R.string.decrease))
                    }
                    IconButton(onClick = { viewModel.onCompletionsChange(state.completions + 1) }) {
                        Icon(Icons.Default.Add, contentDescription = s(R.string.increase))
                    }
                }
            }

            ExposedDropdownMenuBox(expanded = statusExpanded, onExpandedChange = { statusExpanded = !statusExpanded }) {
                OutlinedTextField(
                    value = state.status.label(), onValueChange = {}, readOnly = true,
                    modifier = Modifier.fillMaxWidth().menuAnchor(), label = { Text(s(R.string.status_required)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) }
                )
                ExposedDropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                    GameStatus.entries.forEach { status ->
                        DropdownMenuItem(text = { Text(status.label()) }, onClick = { viewModel.onStatusChange(status); statusExpanded = false })
                    }
                }
            }

            Text(s(R.string.rating_value, formatRating(state.rating)), style = MaterialTheme.typography.titleMedium)
            Slider(
                value = state.rating,
                onValueChange = { raw -> viewModel.onRatingChange((raw * 10f).roundToInt() / 10f) },
                valueRange = 0f..10f,
                steps = 99
            )

            SectionTitle(s(R.string.additional))
            CoverEditor(state.coverUri, viewModel::onCoverUriChange)
            CompletionDatesEditor(state.completionDates, viewModel::addCompletionDate, viewModel::removeCompletionDate)
            OutlinedTextField(state.series, viewModel::onSeriesChange, Modifier.fillMaxWidth(), label = { Text(s(R.string.series)) }, singleLine = true)
            OutlinedTextField(state.universe, viewModel::onUniverseChange, Modifier.fillMaxWidth(), label = { Text(s(R.string.universe)) }, singleLine = true)
            StringListEditor(s(R.string.genres), state.genres, viewModel::onGenresChange)
            StringListEditor(s(R.string.developers), state.developers, viewModel::onDevelopersChange)
            OutlinedTextField(
                state.releaseYear, viewModel::onReleaseYearChange, Modifier.fillMaxWidth(), label = { Text(s(R.string.release_year)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true
            )
            StringListEditor(s(R.string.platforms), state.platforms, viewModel::onPlatformsChange)
            StringListEditor(s(R.string.tags), state.tags, viewModel::onTagsChange)
            OutlinedTextField(state.opinion, viewModel::onOpinionChange, Modifier.fillMaxWidth(), label = { Text(s(R.string.opinion)) }, minLines = 4, maxLines = 10)
            OutlinedTextField(
                state.playtimeHours, viewModel::onPlaytimeHoursChange, Modifier.fillMaxWidth(), label = { Text(s(R.string.playtime)) }, suffix = { Text(s(R.string.hours)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true
            )
            Spacer(Modifier.height(96.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun CoverEditor(coverUri: String?, onCoverChange: (String?) -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            runCatching { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            onCoverChange(uri.toString())
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(s(R.string.cover), style = MaterialTheme.typography.titleMedium)
        GameCover(uriString = coverUri, modifier = Modifier.fillMaxWidth().height(180.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilledTonalButton(onClick = {
                launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }) { Text(if (coverUri == null) s(R.string.select) else s(R.string.replace)) }
            if (coverUri != null) TextButton(onClick = { onCoverChange(null) }) { Text(s(R.string.remove)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun CompletionDatesEditor(dates: List<String>, onAdd: (String) -> Unit, onRemove: (String) -> Unit) {
    var showPicker by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(s(R.string.completion_dates), style = MaterialTheme.typography.titleMedium)
        if (dates.isNotEmpty()) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                dates.forEach { date ->
                    InputChip(selected = false, onClick = { onRemove(date) }, label = { Text(date) }, trailingIcon = { Icon(Icons.Default.Close, contentDescription = s(R.string.delete)) })
                }
            }
        }
        OutlinedButton(onClick = { showPicker = true }) {
            Icon(Icons.Default.Add, contentDescription = null); Spacer(Modifier.width(8.dp)); Text(s(R.string.add_date))
        }
    }
    if (showPicker) {
        val pickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                Button(enabled = pickerState.selectedDateMillis != null, onClick = {
                    pickerState.selectedDateMillis?.let { onAdd(formatDate(it)) }; showPicker = false
                }) { Text(s(R.string.add)) }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text(s(R.string.cancel)) } }
        ) { DatePicker(state = pickerState) }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StringListEditor(title: String, values: List<String>, onValuesChange: (List<String>) -> Unit) {
    var input by rememberSaveable { mutableStateOf("") }
    fun addValue() {
        val clean = input.trim(); if (clean.isBlank()) return
        if (values.none { it.equals(clean, ignoreCase = true) }) onValuesChange(values + clean)
        input = ""
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = input, onValueChange = { input = it }, modifier = Modifier.fillMaxWidth(), label = { Text(title) },
            trailingIcon = { IconButton(enabled = input.isNotBlank(), onClick = ::addValue) { Icon(Icons.Default.Add, contentDescription = s(R.string.add)) } },
            singleLine = true
        )
        if (values.isNotEmpty()) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                values.forEach { value ->
                    InputChip(selected = false, onClick = { onValuesChange(values - value) }, label = { Text(value) }, trailingIcon = { Icon(Icons.Default.Close, contentDescription = s(R.string.delete)) })
                }
            }
        }
    }
}

@Composable
private fun GameStatus.label(): String = when (this) {
    GameStatus.PLAYING -> s(R.string.playing)
    GameStatus.COMPLETED -> s(R.string.completed)
    GameStatus.REPLAYING -> s(R.string.replaying)
    GameStatus.DROPPED -> s(R.string.dropped)
}
@Composable
private fun formatRating(value: Float): String = String.format(androidx.compose.ui.platform.LocalConfiguration.current.locales[0], "%.1f", value)
private fun formatDate(millis: Long): String = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}.format(Date(millis))
