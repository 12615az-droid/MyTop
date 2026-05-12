package com.example.myapplication.ui

import android.media.browse.MediaBrowser
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

/**
 * Основной экран добавления/редактирования медиа-объекта.
 * Использует стабильные компоненты Material 3.
 */


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMediaItemScreen(
    viewModel: ViewModel = koinViewModel(), // Используем Koin
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    val types = listOf("Игра", "Книга", "Музыка", "Сериал", "Аниме", "Фильм", "Манга", "Настолка", "Визуальная новелла")
    val statuses = listOf("В процессе", "Пройдено", "Заброшено", "Перепрохожу")

    var typeExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    // Закрываем экран после сохранения
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Новая запись", fontWeight = FontWeight.Bold) })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.saveItem() },
                expanded = state.title.isNotBlank(),
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
                value = state.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Название") },
                modifier = Modifier.fillMaxWidth()
            )

            // Счётчик прохождений
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Раз пройдено / Просмотрено", style = MaterialTheme.typography.bodyLarge)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.updateCompletions(-1) }) {
                            Icon(Icons.Default.Remove, "Меньше")
                        }
                        Text(
                            text = state.completions.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(onClick = { viewModel.updateCompletions(1) }) {
                            Icon(Icons.Default.Add, "Больше")
                        }
                    }
                }
            }

            // Тип и Статус в ряд
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = state.type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Тип") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(typeExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                        types.forEach { type ->
                            DropdownMenuItem(text = { Text(type) }, onClick = { viewModel.onTypeChange(type); typeExpanded = false })
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = state.status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Статус") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(statusExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                        statuses.forEach { status ->
                            DropdownMenuItem(text = { Text(status) }, onClick = { viewModel.onStatusChange(status); statusExpanded = false })
                        }
                    }
                }
            }

            // Рейтинг
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Твоя оценка")
                        Text("%.1f".format(state.rating), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = state.rating,
                        onValueChange = viewModel::onRatingChange,
                        valueRange = 0f..10f
                    )
                }
            }

            OutlinedTextField(value = state.universe, onValueChange = viewModel::onUniverseChange, label = { Text("Вселенная / Мир") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = state.genre, onValueChange = viewModel::onGenreChange, label = { Text("Жанры") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = state.comment, onValueChange = viewModel::onCommentChange, label = { Text("Экспертное мнение") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            OutlinedTextField(value = state.customTag, onValueChange = viewModel::onTagChange, label = { Text("Собственный тег") }, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(100.dp))
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun TrainingScreenPreview() {
    MaterialTheme {
        AddMediaItemScreen({},{})
    }
    }

