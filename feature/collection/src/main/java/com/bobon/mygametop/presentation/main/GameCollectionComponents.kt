package com.bobon.mygametop.presentation.main

import com.bobon.mygametop.core.ui.R
import com.bobon.mygametop.localization.s

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bobon.mygametop.domain.model.*
import com.bobon.mygametop.presentation.common.GameCover
import com.bobon.mygametop.ui.theme.LocalAppearance

@Composable
internal fun GameCard(game: Game, position: Int, actionsEnabled: Boolean, onClick: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    val appearance = LocalAppearance.current
    var menuExpanded by remember { mutableStateOf(false) }
    Card(onClick = onClick, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow), shape = MaterialTheme.shapes.large) {
        Row(Modifier.fillMaxWidth().padding(if (appearance.compactCards) 10.dp else 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.size(if (appearance.compactCards) 60.dp else 76.dp)) {
                GameCover(uriString = game.coverUri, modifier = Modifier.fillMaxSize())
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text((if (appearance.showRanks) "#${position.toString().padStart(2, '0')}  ·  " else "") + game.status.label(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(game.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Icon(Icons.Default.Star, null, Modifier.size(17.dp), tint = MaterialTheme.colorScheme.primary)
                    Text(formatRating(game.rating), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text("/ 10", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    game.releaseYear?.let { Text(" · $it", style = MaterialTheme.typography.bodySmall) }
                }
            }
            Box {
                IconButton(enabled = actionsEnabled, onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, s(R.string.game_actions, game.title))
                }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    DropdownMenuItem(text = { Text(s(R.string.edit)) }, leadingIcon = { Icon(Icons.Default.Edit, null) },
                        onClick = { menuExpanded = false; onEdit() })
                    DropdownMenuItem(text = { Text(s(R.string.delete), color = MaterialTheme.colorScheme.error) },
                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) },
                        onClick = { menuExpanded = false; onDelete() })
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun FilterPanel(state: MainUiState, onChange: (GameQuery) -> Unit, onDone: () -> Unit) {
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(s(R.string.filter_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(s(R.string.status), style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = state.query.status == null, onClick = { onChange(state.query.copy(status = null)) }, label = { Text(s(R.string.all)) })
            GameStatus.entries.forEach { status ->
                FilterChip(selected = state.query.status == status, onClick = { onChange(state.query.copy(status = status)) }, label = { Text(status.label()) })
            }
        }
        if (state.genres.isNotEmpty()) {
            Text(s(R.string.genre), style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = state.query.genre == null, onClick = { onChange(state.query.copy(genre = null)) }, label = { Text(s(R.string.all_genres)) })
                state.genres.forEach { genre ->
                    FilterChip(selected = state.query.genre == genre, onClick = { onChange(state.query.copy(genre = genre)) }, label = { Text(genre) })
                }
            }
        }
        Text(s(R.string.minimum_rating, formatRating(state.query.minimumRating)), style = MaterialTheme.typography.titleSmall)
        Slider(value = state.query.minimumRating, onValueChange = { onChange(state.query.copy(minimumRating = it)) }, valueRange = 0f..10f, steps = 19)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = { onChange(state.query.copy(status = null, genre = null, minimumRating = 0f)) }) { Text(s(R.string.reset)) }
            Button(onClick = onDone) { Text(s(R.string.show_count, state.games.size)) }
        }
    }
}

@Composable
internal fun GameDetails(game: Game, actionsEnabled: Boolean, onDismiss: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text(game.title) }, confirmButton = { TextButton(enabled = actionsEnabled, onClick = onEdit) { Text(s(R.string.edit)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(s(R.string.close)) } }, text = {
        Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("${formatRating(game.rating)} / 10 · ${game.status.label()}", fontWeight = FontWeight.Bold)
            Text(s(R.string.completions_count, game.completions))
            TextButton(enabled = actionsEnabled, onClick = onDelete) {
                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                Spacer(Modifier.width(8.dp))
                Text(s(R.string.delete_game), color = MaterialTheme.colorScheme.error)
            }
            listOf(
                s(R.string.series) to game.series, s(R.string.universe) to game.universe, s(R.string.release_year) to game.releaseYear?.toString(),
                s(R.string.genres) to game.genres.joinToString(), s(R.string.platforms) to game.platforms.joinToString(),
                s(R.string.developers) to game.developers.joinToString(), s(R.string.tags) to game.tags.joinToString(),
                s(R.string.completion_dates) to game.completionDates.joinToString(), s(R.string.playtime) to game.playtimeHours?.let { s(R.string.hours_value, it.toString()) },
                s(R.string.opinion) to game.opinion
            ).forEach { (label, value) -> if (!value.isNullOrBlank()) Text("$label: $value") }
        }
    })
}

@Composable
private fun GameStatus.label() = when (this) {
    GameStatus.PLAYING -> s(R.string.playing)
    GameStatus.COMPLETED -> s(R.string.completed)
    GameStatus.REPLAYING -> s(R.string.replaying)
    GameStatus.DROPPED -> s(R.string.dropped)
}
@Composable
internal fun GameSort.label() = when (this) {
    GameSort.RATING_DESC -> s(R.string.sort_best)
    GameSort.RATING_ASC -> s(R.string.sort_rating_asc)
    GameSort.TITLE -> s(R.string.sort_title)
    GameSort.NEWEST -> s(R.string.sort_newest)
    GameSort.RELEASE_YEAR -> s(R.string.sort_year)
}
@Composable
internal fun formatRating(value: Float): String = String.format(androidx.compose.ui.platform.LocalConfiguration.current.locales[0], "%.1f", value)
