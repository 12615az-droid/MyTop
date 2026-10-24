package com.bobon.mygametop.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bobon.mygametop.presentation.add.AddGameScreen
import com.bobon.mygametop.presentation.main.MainScreen
import com.bobon.mygametop.presentation.settings.SettingsScreen
import com.bobon.mygametop.presentation.settings.AppearanceScreen
import com.bobon.mygametop.presentation.settings.SyncScreen

private object Routes { const val MAIN = "main"; const val ADD_GAME = "add_game"; const val EDIT_GAME = "edit_game/{gameId}"; const val SETTINGS = "settings"; const val APPEARANCE = "appearance"; const val SYNC = "sync" }

@Composable
fun MyTopNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.MAIN) {
        composable(Routes.MAIN) {
            MainScreen(
                onAddClick = { navController.navigate(Routes.ADD_GAME) },
                onEditClick = { id -> navController.navigate("edit_game/$id") },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() },
                onAppearance = { navController.navigate(Routes.APPEARANCE) }, onSync = { navController.navigate(Routes.SYNC) })
        }
        composable(Routes.SYNC) {
            SyncScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.APPEARANCE) {
            AppearanceScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.EDIT_GAME, arguments = listOf(navArgument("gameId") { type = NavType.LongType })) { entry ->
            AddGameScreen(
                gameId = requireNotNull(entry.arguments).getLong("gameId"),
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
        composable(Routes.ADD_GAME) {
            AddGameScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
    }
}
