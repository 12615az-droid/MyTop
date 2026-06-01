package com.example.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.ui.theme.MyApplicationTheme
import org.koin.androidx.compose.koinViewModel

/**
 * Основной экран добавления/редактирования медиа-объекта.
 * Связан с ViewModel для управления состоянием.
 */
@Composable
fun AddMediaItemScreen(
    viewModel: ViewModel = koinViewModel()
) {
    // Подписываемся на состояния из ViewModel
    val title by viewModel.title.collectAsStateWithLifecycle()
    val completions by viewModel.completions.collectAsStateWithLifecycle()
    val type by viewModel.type.collectAsStateWithLifecycle()
    val status by viewModel.status.collectAsStateWithLifecycle()
    val rating by viewModel.rating.collectAsStateWithLifecycle()
    val universe by viewModel.universe.collectAsStateWithLifecycle()
    val genre by viewModel.genre.collectAsStateWithLifecycle()
    val comment by viewModel.comment.collectAsStateWithLifecycle()
    val customTag by viewModel.customTag.collectAsStateWithLifecycle()

    AddMediaItemContent(
        title = title,
        completions = completions,
        type = type,
        status = status,
        rating = rating,
        universe = universe,
        genre = genre,
        comment = comment,
        customTag = customTag,
        onTitleChange = { viewModel.updateTitle(it) },
        onCompletionsChange = { viewModel.updateCompletions(it) },
        onTypeChange = { viewModel.updateType(it) },
        onStatusChange = { viewModel.updateStatus(it) },
        onRatingChange = { viewModel.updateRating(it) },
        onUniverseChange = { viewModel.updateUniverse(it) },
        onGenreChange = { viewModel.updateGenre(it) },
        onCommentChange = { viewModel.updateComment(it) },
        onCustomTagChange = { viewModel.updateCustomTag(it) },
        onBackClicked = { viewModel.onBackClicked() },
        onAddClicked = { viewModel.onAddClicked() },
        onSaveClicked = { viewModel.onSaveClicked() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMediaItemContent(
    title: String,
    completions: Int,
    type: String,
    status: String,
    rating: Float,
    universe: String,
    genre: String,
    comment: String,
    customTag: String,
    onTitleChange: (String) -> Unit,
    onCompletionsChange: (Int) -> Unit,
    onTypeChange: (String) -> Unit,
    onStatusChange: (String) -> Unit,
    onRatingChange: (Float) -> Unit,
    onUniverseChange: (String) -> Unit,
    onGenreChange: (String) -> Unit,
    onCommentChange: (String) -> Unit,
    onCustomTagChange: (String) -> Unit,
    onBackClicked: () -> Unit,
    onAddClicked: () -> Unit,
    onSaveClicked: () -> Unit
) {
    val types = listOf("Игра", "Книга", "Музыка", "Сериал", "Аниме", "Фильм", "Манга", "Настолка", "Визуальная новелла")
    val statuses = listOf("В процессе", "Пройдено", "Заброшено", "Перепрохожу")

    var typeExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новая запись") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = onAddClicked) {
                        Icon(Icons.Default.Add, "Добавить")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onSaveClicked,
                expanded = title.isNotBlank(),
                icon = { Icon(Icons.Default.Check, null) },
                text = { Text("Сохранить") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Название
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Название") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Fallout: New Vegas") }
            )

            // Счётчик прохождений
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Раз пройдено / Просмотрено")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (completions > 0) onCompletionsChange(completions - 1) }) {
                            Icon(Icons.Default.Remove, "Меньше")
                        }
                        Text(
                            text = completions.toString(),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(onClick = { onCompletionsChange(completions + 1) }) {
                            Icon(Icons.Default.Add, "Больше")
                        }
                    }
                }
            }

            // Тип и Статус в ряд
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Тип") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(typeExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                        types.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t) },
                                onClick = { onTypeChange(t); typeExpanded = false }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Статус") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(statusExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                        statuses.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s) },
                                onClick = { onStatusChange(s); statusExpanded = false }
                            )
                        }
                    }
                }
            }

            // Рейтинг
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Твоя оценка")
                        Text(
                            text = "%.1f".format(rating),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = rating,
                        onValueChange = onRatingChange,
                        valueRange = 0f..10f,
                    )
                }
            }

            // Остальные поля
            OutlinedTextField(
                value = universe,
                onValueChange = onUniverseChange,
                label = { Text("Вселенная / Мир") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Public, null) }
            )

            OutlinedTextField(
                value = genre,
                onValueChange = onGenreChange,
                label = { Text("Жанры") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Category, null) }
            )

            OutlinedTextField(
                value = comment,
                onValueChange = onCommentChange,
                label = { Text("Экспертное мнение") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            OutlinedTextField(
                value = customTag,
                onValueChange = onCustomTagChange,
                label = { Text("Собственный тег (для Топов)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Tag, null) }
            )

            Spacer(Modifier.height(100.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyTopAddPreview() {
    MyApplicationTheme {
        AddMediaItemContent(
            title = "Fallout: New Vegas",
            completions = 1,
            type = "Игра",
            status = "В процессе",
            rating = 8.5f,
            universe = "",
            genre = "",
            comment = "",
            customTag = "",
            onTitleChange = {},
            onCompletionsChange = {},
            onTypeChange = {},
            onStatusChange = {},
            onRatingChange = {},
            onUniverseChange = {},
            onGenreChange = {},
            onCommentChange = {},
            onCustomTagChange = {},
            onBackClicked = {},
            onAddClicked = {},
            onSaveClicked = {}
        )
    }
}
