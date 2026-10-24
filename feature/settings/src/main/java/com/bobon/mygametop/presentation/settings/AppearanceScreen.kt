package com.bobon.mygametop.presentation.settings

import com.bobon.mygametop.core.ui.R
import com.bobon.mygametop.localization.s

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bobon.mygametop.domain.settings.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.core.graphics.toColorInt
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val accents = listOf(
    R.string.iris to "#6650A4", R.string.ocean to "#2563A6", R.string.forest to "#2E7D60",
    R.string.rose to "#AE426A", R.string.amber to "#A76B16", R.string.graphite to "#62656C"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AppearanceScreen(onBack: () -> Unit, viewModel: SettingsViewModel = org.koin.androidx.compose.koinViewModel()) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val resources = androidx.compose.ui.platform.LocalContext.current.resources
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var reset by rememberSaveable { mutableStateOf(false) }
    var importing by remember { mutableStateOf(false) }
    var hex by rememberSaveable(settings.accentHex) { mutableStateOf(settings.accentHex) }
    val normalizedHex = normalizeAccent(hex)
    val supportsDynamic = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val fontPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) scope.launch {
            importing = true
            try {
                viewModel.importFont(uri.toString())
                snackbar.showSnackbar(resources.getString(R.string.font_applied))
            } catch (error: CancellationException) { throw error }
            catch (error: Exception) { snackbar.showSnackbar(resources.getString(R.string.font_error)) }
            finally { importing = false }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(title = { Text(s(R.string.appearance)) }, navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, s(R.string.back)) }
            }, actions = {
                IconButton(enabled = !importing, onClick = { reset = true }) { Icon(Icons.Default.RestartAlt, s(R.string.reset_appearance)) }
            })
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text(s(R.string.your_style), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(s(R.string.auto_save), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            PreviewCard(settings)
            SettingSection(s(R.string.theme)) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeMode.entries.forEach { mode ->
                        FilterChip(selected = settings.themeMode == mode, onClick = { viewModel.update { it.copy(themeMode = mode) } },
                            label = { Text(mode.label()) }, leadingIcon = {
                                Icon(when (mode) { ThemeMode.SYSTEM -> Icons.Default.BrightnessAuto; ThemeMode.LIGHT -> Icons.Default.LightMode; ThemeMode.DARK -> Icons.Default.DarkMode }, null, Modifier.size(18.dp))
                            })
                    }
                }
            }
            SettingSection(s(R.string.colors)) {
                ToggleRow(s(R.string.system_colors), if (supportsDynamic) s(R.string.system_colors_help) else s(R.string.system_colors_unavailable),
                    checked = settings.dynamicColors && supportsDynamic, enabled = supportsDynamic) { checked -> viewModel.update { it.copy(dynamicColors = checked) } }
                if (!settings.dynamicColors || !supportsDynamic) {
                    Text(s(R.string.accent_palette), style = MaterialTheme.typography.titleSmall)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        accents.forEach { (name, value) ->
                            val selected = settings.accentHex == value
                            Column(Modifier.width(72.dp).selectable(selected, role = Role.RadioButton, onClick = {
                                viewModel.update { it.copy(accentHex = value, dynamicColors = false) }
                            }).padding(vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(Modifier.size(44.dp).background(Color(value.toColorInt()), CircleShape)
                                    .then(if (selected) Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape) else Modifier), contentAlignment = Alignment.Center) {
                                    if (selected) Icon(Icons.Default.Check, null, Modifier.size(22.dp), tint = Color.White)
                                }
                                Text(s(name), style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                    OutlinedTextField(hex, { if (it.length <= 9) hex = it }, Modifier.fillMaxWidth(), label = { Text(s(R.string.custom_hex)) },
                        supportingText = { Text(if (normalizedHex == null) s(R.string.hex_help) else s(R.string.tonal_help)) },
                        isError = normalizedHex == null, singleLine = true)
                    FilledTonalButton(enabled = normalizedHex != null && normalizedHex != settings.accentHex, onClick = {
                        normalizedHex?.let { value -> viewModel.update { it.copy(accentHex = value, dynamicColors = false) } }
                    }) { Text(s(R.string.apply_accent)) }
                }
            }
            SettingSection(s(R.string.font)) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppFont.entries.filter { it != AppFont.CUSTOM }.forEach { font ->
                        FilterChip(selected = settings.font == font, onClick = { viewModel.update { it.copy(font = font) } }, label = { Text(font.label()) })
                    }
                }
                if (settings.customFontPath != null) {
                    FilterChip(selected = settings.font == AppFont.CUSTOM, onClick = { viewModel.update { it.copy(font = AppFont.CUSTOM) } },
                        label = { Text(settings.customFontName ?: s(R.string.custom_font), maxLines = 2, overflow = TextOverflow.Ellipsis) },
                        leadingIcon = { Icon(Icons.Default.TextFields, null, Modifier.size(18.dp)) })
                }
                OutlinedButton(enabled = !importing, onClick = { fontPicker.launch(arrayOf("*/*")) }) {
                    Icon(Icons.Default.UploadFile, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp))
                    Text(if (importing) s(R.string.loading) else s(R.string.import_font))
                }
                Text(s(R.string.font_help),
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                HorizontalDivider()
                Text(s(R.string.text_scale, (settings.textScale * 100).roundToInt()), style = MaterialTheme.typography.titleSmall)
                Slider(value = settings.textScale, onValueChange = { value ->
                    viewModel.update { it.copy(textScale = (value * 20).roundToInt() / 20f) }
                }, valueRange = 0.9f..1.2f, steps = 5)
                Text(s(R.string.text_scale_help), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            SettingSection(s(R.string.cards)) {
                Text(s(R.string.corners), style = MaterialTheme.typography.titleSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CornerStyle.entries.forEach { style ->
                        FilterChip(selected = settings.corners == style, onClick = { viewModel.update { it.copy(corners = style) } }, label = { Text(style.label()) })
                    }
                }
                ToggleRow(s(R.string.compact_cards), s(R.string.compact_help), settings.compactCards) { value -> viewModel.update { it.copy(compactCards = value) } }
                ToggleRow(s(R.string.show_ranks), s(R.string.ranks_help), settings.showRanks) { value -> viewModel.update { it.copy(showRanks = value) } }
            }
            Text(s(R.string.material_note), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
        }
    }
    if (reset) AlertDialog(onDismissRequest = { reset = false }, title = { Text(s(R.string.reset_appearance_question)) },
        text = { Text(s(R.string.reset_appearance_help)) },
        confirmButton = { TextButton(onClick = { viewModel.reset(); reset = false }) { Text(s(R.string.reset)) } },
        dismissButton = { TextButton(onClick = { reset = false }) { Text(s(R.string.cancel)) } })
}

@Composable
private fun SettingSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}

@Composable
private fun ToggleRow(title: String, subtitle: String, checked: Boolean, enabled: Boolean = true, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().toggleable(checked, enabled = enabled, role = Role.Switch, onValueChange = onChange).padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = null, enabled = enabled)
    }
}

@Composable
private fun PreviewCard(settings: AppearanceSettings) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(s(R.string.preview), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Card(shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Row(Modifier.fillMaxWidth().padding(if (settings.compactCards) 12.dp else 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Surface(Modifier.size(if (settings.compactCards) 52.dp else 68.dp), shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.primary) {
                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.SportsEsports, null, Modifier.size(32.dp), tint = MaterialTheme.colorScheme.onPrimary) }
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(if (settings.showRanks) "#01 · " + s(R.string.completed) else s(R.string.completed), style = MaterialTheme.typography.labelSmall)
                    Text(s(R.string.favorite_game), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("★ " + java.text.NumberFormat.getNumberInstance(androidx.compose.ui.platform.LocalConfiguration.current.locales[0]).format(9.5) + " / 10", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun ThemeMode.label() = when (this) { ThemeMode.SYSTEM -> s(R.string.follow_system); ThemeMode.LIGHT -> s(R.string.light_theme); ThemeMode.DARK -> s(R.string.dark_theme) }
@Composable
private fun AppFont.label() = when (this) { AppFont.SYSTEM -> s(R.string.system_font); AppFont.SANS -> s(R.string.sans); AppFont.SERIF -> s(R.string.serif); AppFont.CONDENSED -> s(R.string.condensed); AppFont.MONO -> s(R.string.mono); AppFont.CUSTOM -> s(R.string.custom) }
@Composable
private fun CornerStyle.label() = when (this) { CornerStyle.SUBTLE -> s(R.string.subtle); CornerStyle.BALANCED -> s(R.string.balanced); CornerStyle.SOFT -> s(R.string.soft) }
