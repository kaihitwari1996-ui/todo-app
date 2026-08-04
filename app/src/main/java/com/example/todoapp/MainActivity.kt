package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.ui.components.BottomNavBar
import com.example.todoapp.ui.screens.*
import com.example.todoapp.ui.theme.AppTheme
import com.example.todoapp.ui.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: AppViewModel = viewModel()
            val theme by vm.theme.collectAsState()

            AppTheme(themeMode = theme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val backStack by navController.currentBackStackEntryAsState()
                    val currentRoute = backStack?.destination?.route ?: "todo"

                    Scaffold(
                        bottomBar = {
                            BottomNavBar(currentRoute = currentRoute) { route ->
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController    = navController,
                            startDestination = "todo",
                            modifier         = Modifier.padding(innerPadding)
                        ) {
                            composable("todo")     { TodoScreen(vm) }
                            composable("calendar") { CalendarScreen(vm) }
                            composable("notes")    { NotesScreen(vm) }
                            composable("habits")   { HabitScreen(vm) }
                            composable("settings") { SettingsScreen(vm) }
                        }
                    }
                }
            }
        }
    }
}
