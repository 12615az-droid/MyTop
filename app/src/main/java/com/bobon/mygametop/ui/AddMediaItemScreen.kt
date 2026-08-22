package com.bobon.mygametop.ui

import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bobon.mygametop.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs
import kotlin.math.roundToInt


/**
 * UI-затычка экрана добавления игры.
 *
 * ViewModel и база пока не нужны: все значения живут локально в Compose-state.
 * Позже эти же поля можно напрямую перенести в GameUiState / ViewModel.
 */
enum class GameStatus(val title: String) {
    PLAYING("Прохожу"),
    COMPLETED("Пройдено"),
    REPLAYING("Перепрохожу"),
    DROPPED("Брошено")
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun AddGameScreen() {
    // -----------------------------
    // Обязательные поля
    // -----------------------------
    var title by rememberSaveable { mutableStateOf("") }
    var completions by rememberSaveable { mutableIntStateOf(0) }
    var status by rememberSaveable { mutableStateOf(GameStatus.PLAYING) }
    var rating by rememberSaveable { mutableFloatStateOf(0f) }

    // -----------------------------
    // Необязательные поля
    // -----------------------------
    var coverUri by rememberSaveable { mutableStateOf<String?>(null) }
    val completionDates = remember { mutableStateListOf<String>() }

    var series by rememberSaveable { mutableStateOf("") }
    var universe by rememberSaveable { mutableStateOf("") }

    val genres = remember { mutableStateListOf<String>() }
    val tags = remember { mutableStateListOf<String>() }
    val developers = remember { mutableStateListOf<String>() }
    val platforms = remember { mutableStateListOf<String>() }

    var releaseYear by rememberSaveable { mutableStateOf("") }
    var opinion by rememberSaveable { mutableStateOf("") }
    var playtimeHours by rememberSaveable { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    GameEditorContent(
        title = title,
        onTitleChange = { title = it },

        completions = completions,
        onCompletionsChange = { completions = it.coerceAtLeast(0) },

        status = status,
        onStatusChange = { status = it },

        rating = rating,
        onRatingChange = { rating = it.coerceIn(0f, 10f) },

        coverUri = coverUri,
        onCoverUriChange = { coverUri = it },

        completionDates = completionDates,
        onCompletionDateAdd = { date ->
            if (date !in completionDates) {
                completionDates.add(date)
                completionDates.sort()
            }
        },
        onCompletionDateRemove = { completionDates.remove(it) },

        series = series,
        onSeriesChange = { series = it },

        universe = universe,
        onUniverseChange = { universe = it },

        genres = genres,
        onGenresChange = {
            genres.clear()
            genres.addAll(it)
        },

        tags = tags,
        onTagsChange = {
            tags.clear()
            tags.addAll(it)
        },

        developers = developers,
        onDevelopersChange = {
            developers.clear()
            developers.addAll(it)
        },

        platforms = platforms,
        onPlatformsChange = {
            platforms.clear()
            platforms.addAll(it)
        },

        releaseYear = releaseYear,
        onReleaseYearChange = { input ->
            if (input.length <= 4 && input.all(Char::isDigit)) {
                releaseYear = input
            }
        },

        opinion = opinion,
        onOpinionChange = { opinion = it },

        playtimeHours = playtimeHours,
        onPlaytimeHoursChange = { input ->
            val normalized = input.replace(',', '.')
            if (
                normalized.isEmpty() ||
                normalized.matches(Regex("""\d{0,6}(\.\d{0,2})?"""))
            ) {
                playtimeHours = input
            }
        },

        snackbarHostState = snackbarHostState,

        onBackClicked = {
            scope.launch {
                snackbarHostState.showSnackbar("Назад — пока UI-затычка")
            }
        },

        onSaveClicked = {
            scope.launch {
                snackbarHostState.showSnackbar(
                    if (title.isBlank()) {
                        "Для сохранения нужно название"
                    } else {
                        "UI готов: позже здесь будет запись в базу"
                    }
                )
            }
        }
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
private fun GameEditorContent(
    title: String,
    onTitleChange: (String) -> Unit,

    completions: Int,
    onCompletionsChange: (Int) -> Unit,

    status: GameStatus,
    onStatusChange: (GameStatus) -> Unit,

    rating: Float,
    onRatingChange: (Float) -> Unit,

    coverUri: String?,
    onCoverUriChange: (String?) -> Unit,

    completionDates: List<String>,
    onCompletionDateAdd: (String) -> Unit,
    onCompletionDateRemove: (String) -> Unit,

    series: String,
    onSeriesChange: (String) -> Unit,

    universe: String,
    onUniverseChange: (String) -> Unit,

    genres: List<String>,
    onGenresChange: (List<String>) -> Unit,

    tags: List<String>,
    onTagsChange: (List<String>) -> Unit,

    developers: List<String>,
    onDevelopersChange: (List<String>) -> Unit,

    platforms: List<String>,
    onPlatformsChange: (List<String>) -> Unit,

    releaseYear: String,
    onReleaseYearChange: (String) -> Unit,

    opinion: String,
    onOpinionChange: (String) -> Unit,

    playtimeHours: String,
    onPlaytimeHoursChange: (String) -> Unit,

    snackbarHostState: SnackbarHostState,
    onBackClicked: () -> Unit,
    onSaveClicked: () -> Unit
) {
    var statusExpanded by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (title.isBlank()) "Новая игра" else title,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onSaveClicked,
                expanded = title.isNotBlank(),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                },
                text = {
                    Text("Сохранить")
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionHeader(
                title = "Основное",
                subtitle = "Поля, которые формируют базовую запись игры."
            )

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Название *") },
                placeholder = { Text("Fallout: New Vegas") },
                supportingText = {
                    Text("Можно писать официальное или своё название.")
                },
                singleLine = true
            )

            CompletionCounterCard(
                completions = completions,
                onValueChange = onCompletionsChange
            )

            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = {
                    statusExpanded = !statusExpanded
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = status.title,
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    label = { Text("Статус *") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = statusExpanded
                        )
                    }
                )

                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    GameStatus.values().forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.title) },
                            onClick = {
                                onStatusChange(item)
                                statusExpanded = false
                            }
                        )
                    }
                }
            }

            RatingCard(
                rating = rating,
                onRatingChange = onRatingChange
            )

            Spacer(Modifier.height(4.dp))
            HorizontalDivider()
            Spacer(Modifier.height(4.dp))

            SectionHeader(
                title = "Дополнительно",
                subtitle = "Всё ниже можно оставить пустым и заполнить когда угодно."
            )

            CoverPickerCard(
                coverUri = coverUri,
                onCoverUriChange = onCoverUriChange
            )

            CompletionDatesEditor(
                dates = completionDates,
                onAdd = onCompletionDateAdd,
                onRemove = onCompletionDateRemove
            )

            OutlinedTextField(
                value = series,
                onValueChange = onSeriesChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Серия") },
                placeholder = { Text("Например: Assassin's Creed") },
                supportingText = { Text("Для отдельных топов по серии.") },
                singleLine = true
            )

            OutlinedTextField(
                value = universe,
                onValueChange = onUniverseChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Вселенная / мир") },
                placeholder = { Text("Например: Star Wars") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null
                    )
                },
                singleLine = true
            )

            StringListEditor(
                title = "Жанры",
                placeholder = "RPG",
                values = genres,
                onValuesChange = onGenresChange,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = null
                    )
                }
            )

            StringListEditor(
                title = "Разработчики",
                placeholder = "Obsidian Entertainment",
                values = developers,
                onValuesChange = onDevelopersChange
            )

            OutlinedTextField(
                value = releaseYear,
                onValueChange = onReleaseYearChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Год выпуска") },
                placeholder = { Text("2010") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true
            )

            StringListEditor(
                title = "Платформы",
                placeholder = "PC",
                values = platforms,
                onValuesChange = onPlatformsChange
            )

            StringListEditor(
                title = "Дополнительные теги",
                placeholder = "Ностальгия",
                values = tags,
                onValuesChange = onTagsChange,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Tag,
                        contentDescription = null
                    )
                }
            )

            OutlinedTextField(
                value = opinion,
                onValueChange = onOpinionChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Моё мнение") },
                placeholder = {
                    Text("Что понравилось, что не понравилось, почему такая оценка...")
                },
                minLines = 4,
                maxLines = 10
            )

            OutlinedTextField(
                value = playtimeHours,
                onValueChange = onPlaytimeHoursChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Время в игре") },
                suffix = { Text("ч") },
                supportingText = {
                    Text("Необязательно. Можно не заполнять вообще.")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                singleLine = true
            )

            Spacer(Modifier.height(96.dp))
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CompletionCounterCard(
    completions: Int,
    onValueChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Пройдено раз *",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "0 и больше",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    enabled = completions > 0,
                    onClick = {
                        if (completions > 0) {
                            onValueChange(completions - 1)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Уменьшить"
                    )
                }

                Text(
                    text = completions.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                IconButton(
                    onClick = {
                        onValueChange(completions + 1)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Увеличить"
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingCard(
    rating: Float,
    onRatingChange: (Float) -> Unit
) {
    var ratingText by rememberSaveable {
        mutableStateOf(formatRating(rating))
    }

    LaunchedEffect(rating) {
        val current = ratingText
            .replace(',', '.')
            .toFloatOrNull()

        if (current == null || abs(current - rating) > 0.001f) {
            ratingText = formatRating(rating)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Оценка *",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "От 0 до 10 с шагом 0,1",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = formatRating(rating),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Slider(
                value = rating,
                onValueChange = { raw ->
                    val rounded = (raw * 10f).roundToInt() / 10f
                    ratingText = formatRating(rounded)
                    onRatingChange(rounded)
                },
                valueRange = 0f..10f,
                steps = 99,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = ratingText,
                onValueChange = { input ->
                    if (input.matches(Regex("""\d{0,2}([.,]\d?)?"""))) {
                        ratingText = input

                        input
                            .replace(',', '.')
                            .toFloatOrNull()
                            ?.takeIf { it in 0f..10f }
                            ?.let {
                                onRatingChange(
                                    (it * 10f).roundToInt() / 10f
                                )
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Ввести оценку вручную") },
                suffix = { Text("/ 10") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                singleLine = true
            )
        }
    }
}

@Composable
private fun CoverPickerCard(
    coverUri: String?,
    onCoverUriChange: (String?) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        onCoverUriChange(uri?.toString())
    }

    val bitmap = rememberUriBitmap(coverUri)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Обложка",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Необязательно",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (coverUri != null) {
                    TextButton(
                        onClick = { onCoverUriChange(null) }
                    ) {
                        Text("Убрать")
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Обложка игры",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Без обложки",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            FilledTonalButton(
                onClick = {
                    launcher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (coverUri == null) {
                        "Выбрать изображение"
                    } else {
                        "Заменить изображение"
                    }
                )
            }
        }
    }
}

@Suppress("DEPRECATION")
@Composable
private fun rememberUriBitmap(
    uriString: String?
): ImageBitmap? {
    val context = LocalContext.current
    var imageBitmap by remember(uriString) {
        mutableStateOf<ImageBitmap?>(null)
    }

    LaunchedEffect(uriString) {
        imageBitmap = null

        if (uriString == null) {
            return@LaunchedEffect
        }

        imageBitmap = withContext(Dispatchers.IO) {
            runCatching {
                val uri = android.net.Uri.parse(uriString)

                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(
                        context.contentResolver,
                        uri
                    )
                    ImageDecoder.decodeBitmap(source)
                } else {
                    MediaStore.Images.Media.getBitmap(
                        context.contentResolver,
                        uri
                    )
                }

                bitmap.asImageBitmap()
            }.getOrNull()
        }
    }

    return imageBitmap
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
private fun CompletionDatesEditor(
    dates: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Даты прохождений",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "Можно знать не все даты. Количество прохождений хранится отдельно.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (dates.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                dates.forEach { date ->
                    InputChip(
                        selected = false,
                        onClick = { onRemove(date) },
                        label = { Text(date) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Удалить дату",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }
        }

        OutlinedButton(
            onClick = { showDatePicker = true }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Text("Добавить дату прохождения")
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                Button(
                    enabled = datePickerState.selectedDateMillis != null,
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onAdd(formatDate(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Добавить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalLayoutApi::class)
@Composable
private fun StringListEditor(
    title: String,
    placeholder: String,
    values: List<String>,
    onValuesChange: (List<String>) -> Unit,
    leadingIcon: (@Composable (() -> Unit))? = null
) {
    var input by rememberSaveable {
        mutableStateOf("")
    }

    fun addValue() {
        val clean = input.trim()

        if (clean.isBlank()) {
            return
        }

        val exists = values.any {
            it.equals(clean, ignoreCase = true)
        }

        if (!exists) {
            onValuesChange(values + clean)
        }

        input = ""
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(title) },
            placeholder = { Text(placeholder) },
            leadingIcon = leadingIcon,
            trailingIcon = {
                IconButton(
                    enabled = input.isNotBlank(),
                    onClick = { addValue() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Добавить"
                    )
                }
            },
            singleLine = true
        )

        if (values.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                values.forEach { value ->
                    InputChip(
                        selected = false,
                        onClick = {
                            onValuesChange(values - value)
                        },
                        label = {
                            Text(value)
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Удалить",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}

private fun formatRating(value: Float): String {
    return String.format(
        Locale.US,
        "%.1f",
        value
    ).replace('.', ',')
}

private fun formatDate(millis: Long): String {
    return SimpleDateFormat(
        "dd.MM.yyyy",
        Locale.getDefault()
    ).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date(millis))
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun AddGameScreenPreview() {
    MyApplicationTheme {
        val snackbarHostState = remember { SnackbarHostState() }

        GameEditorContent(
            title = "Fallout: New Vegas",
            onTitleChange = {},

            completions = 3,
            onCompletionsChange = {},

            status = GameStatus.REPLAYING,
            onStatusChange = {},

            rating = 9.6f,
            onRatingChange = {},

            coverUri = null,
            onCoverUriChange = {},

            completionDates = listOf(
                "14.08.2021",
                "20.01.2024"
            ),
            onCompletionDateAdd = {},
            onCompletionDateRemove = {},

            series = "Fallout",
            onSeriesChange = {},

            universe = "Fallout",
            onUniverseChange = {},

            genres = listOf(
                "RPG",
                "Action"
            ),
            onGenresChange = {},

            tags = listOf(
                "Ностальгия",
                "Лучший сюжет"
            ),
            onTagsChange = {},

            developers = listOf(
                "Obsidian Entertainment"
            ),
            onDevelopersChange = {},

            platforms = listOf(
                "PC"
            ),
            onPlatformsChange = {},

            releaseYear = "2010",
            onReleaseYearChange = {},

            opinion = "Одна из моих любимых RPG.",
            onOpinionChange = {},

            playtimeHours = "",
            onPlaytimeHoursChange = {},

            snackbarHostState = snackbarHostState,
            onBackClicked = {},
            onSaveClicked = {}
        )
    }
}