package com.example.myapplication.ui



import android.R.id.tabs
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

// ============================================
// МОДЕЛЬ ДАННЫХ (заглушки)
// ============================================
data class MediaItemUi(
    val id: Int,
    val title: String,
    val rating: Double,
    val genre: String?,
    val status: String
)

// ============================================
// ГЛАВНОЙ ЭКРАН
// ============================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Игры", "Книги", "Фильмы", "Сериалы", "Музыка")

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Показывать кнопку "вверх" только если прокрутили вниз
    val showScrollTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 3 }
    }
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мой Топ") },
                actions = {
                    // Три точки — меню
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, "Меню")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Сортировка") },
                            onClick = { /* открыть диалог сортировки */ },
                            leadingIcon = { Icon(Icons.Default.Sort, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Фильтры") },
                            onClick = { /* открыть диалог фильтров */ },
                            leadingIcon = { Icon(Icons.Default.FilterList, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Экспорт") },
                            onClick = { /* JSON / TXT */ },
                            leadingIcon = { Icon(Icons.Default.Share, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Настройки") },
                            onClick = { /* открыть экран настроек */ },
                            leadingIcon = { Icon(Icons.Default.Settings, null) }
                        )
                    }
                }
            )
        }
    ,
        floatingActionButton = {
            // Кнопка "вверх" появляется при прокрутке
            AnimatedVisibility(
                visible = showScrollTop,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, "Наверх")
                }
            }
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = { /* иконка */ },
                        label = { Text(title, maxLines = 1) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Статистика
            StatsSection(
                category = tabs[selectedTab],
                totalItems = 42,
                averageRating = 8.7,
                completedCount = 15,
                modifier = Modifier.padding(16.dp)
            )

            Divider(thickness = 1.dp)

            // Список с кнопкой добавления внутри
            MediaList(
                items = getDummyData(selectedTab),
                listState = listState,
                onAddClick = { /* открыть диалог */ },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MediaList(
    items: List<MediaItemUi>,
    listState: LazyListState,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Кнопка добавления — ПЕРВЫЙ элемент, всегда видна
        item(key = "add_button") {
            OutlinedCard(
                onClick = onAddClick,
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                ),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Добавить",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        items(items, key = { it.id }) { item ->
            MediaCard(item = item)
        }
    }
}

// ============================================
// СЕКЦИЯ СТАТИСТИКИ
// ============================================
@Composable
fun StatsSection(
    category: String,
    totalItems: Int,
    averageRating: Double,
    completedCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Статистика: $category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Всего",
                    value = totalItems.toString()
                )
                StatItem(
                    label = "Средний балл",
                    value = String.format("%.1f", averageRating)
                )
                StatItem(
                    label = "Завершено",
                    value = completedCount.toString()
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// ============================================
// СПИСОК ЭЛЕМЕНТОВ
// ============================================
@Composable
fun MediaList(
    items: List<MediaItemUi>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            // Кнопка добавления как первый элемент списка
            OutlinedCard(
                onClick = { /* добавить */ },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Добавить ", fontWeight = FontWeight.Medium)
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        items(items) { item ->
            MediaCard(item = item)
        }
        items(items) { item ->
            MediaCard(item = item)
        }
    }
}

// ============================================
// КАРТОЧКА ЭЛЕМЕНТА
// ============================================
@Composable
fun MediaCard(item: MediaItemUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${item.genre ?: "Без жанра"} • ${item.status}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Рейтинг бейдж
            Surface(
                shape = MaterialTheme.shapes.small,
                color = when {
                    item.rating >= 9.0 -> MaterialTheme.colorScheme.primaryContainer
                    item.rating >= 7.0 -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.errorContainer
                }
            ) {
                Text(
                    text = String.format("%.1f", item.rating),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontWeight = FontWeight.Bold)
            }
        }
    }
}


// ============================================
// ЗАГЛУШКИ ДАННЫХ
// ============================================
fun getDummyData(tabIndex: Int): List<MediaItemUi> {
    val prefixes = listOf("Игра", "Книга", "Фильм", "Сериал", "Трек")
    val prefix = prefixes[tabIndex]

    return List(20) { index ->
        MediaItemUi(
            id = index,
            title = "$prefix #$index",
            rating = (5.0 + kotlin.random.Random.nextDouble() * 5.0),
            genre = listOf("RPG", "Фантастика", "Драма", "Рок").random(),
            status = listOf("Пройдено", "В процессе", "Заброшено").random()
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun MyTopAddPreview() {
    MyApplicationTheme {
        MainScreen()

    }
}




