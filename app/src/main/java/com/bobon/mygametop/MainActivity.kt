package com.bobon.mygametop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bobon.mygametop.presentation.add.AddGameScreen
import com.bobon.mygametop.presentation.main.MainScreen
import com.bobon.mygametop.ui.theme.MyApplicationTheme

private object Routes { const val MAIN = "main"; const val ADD_GAME = "add_game" }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Routes.MAIN) {
                    composable(Routes.MAIN) {
                        MainScreen(onAddClick = { navController.navigate(Routes.ADD_GAME) })
                    }
                    composable(Routes.ADD_GAME) {
                        AddGameScreen(
                            onBack = { navController.popBackStack() },
                            onSaved = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
