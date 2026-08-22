package com.bobon.mygametop.presentation.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobon.mygametop.domain.model.Game
import com.bobon.mygametop.domain.model.GameStatus
import com.bobon.mygametop.presentation.common.GameCover
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onAddClick: () -> Unit, viewModel: MainViewModel = koinViewModel()) {
    val games by viewModel.games.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Мой Топ") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Добавить игру")
            }
        }
    ) { padding ->
        if (games.isEmpty()) {
            EmptyState(Modifier.padding(padding).fillMaxSize())
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = games, key = Game::id) { game -> GameCard(game) }
                item { Spacer(Modifier.height(72.dp)) }
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Пока нет игр", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Text(
            "Нажми +, заполни карточку — после сохранения она появится здесь.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GameCard(game: Game) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (game.coverUri != null) {
                Surface(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().height(180.dp)) {
                    GameCover(uriString = game.coverUri, modifier = Modifier.fillMaxSize())
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(game.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                    Text(formatRating(game.rating), modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), fontWeight = FontWeight.Bold)
                }
            }

            Text("${game.status.label()} • прохождений: ${game.completions}", style = MaterialTheme.typography.bodyMedium)
            MetadataLine("Серия", game.series)
            MetadataLine("Вселенная", game.universe)
            MetadataLine("Год", game.releaseYear?.toString())
            MetadataLine("Время", game.playtimeHours?.let { "${String.format(Locale.US, "%.1f", it)} ч" })
            MetadataLine("Даты прохождений", game.completionDates.takeIf { it.isNotEmpty() }?.joinToString())
            MetadataLine("Разработчики", game.developers.takeIf { it.isNotEmpty() }?.joinToString())
            MetadataLine("Платформы", game.platforms.takeIf { it.isNotEmpty() }?.joinToString())

            if (game.genres.isNotEmpty() || game.tags.isNotEmpty()) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    (game.genres + game.tags).distinct().forEach { value ->
                        AssistChip(onClick = {}, label = { Text(value) })
                    }
                }
            }
            game.opinion?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
        }
    }
}

@Composable
private fun MetadataLine(label: String, value: String?) {
    if (!value.isNullOrBlank()) {
        Text("$label: $value", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun GameStatus.label(): String = when (this) {
    GameStatus.PLAYING -> "Прохожу"
    GameStatus.COMPLETED -> "Пройдено"
    GameStatus.REPLAYING -> "Перепрохожу"
    GameStatus.DROPPED -> "Брошено"
}

private fun formatRating(value: Float): String = String.format(Locale.US, "%.1f", value).replace('.', ',')
