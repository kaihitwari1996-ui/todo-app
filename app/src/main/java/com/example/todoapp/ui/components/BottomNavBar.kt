package com.example.todoapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Todo     : Screen("todo",     "Tasks",    Icons.Default.CheckCircle)
    object Calendar : Screen("calendar", "Calendar", Icons.Default.CalendarMonth)
    object Notes    : Screen("notes",    "Notes",    Icons.Default.Notes)
    object Habits   : Screen("habits",   "Habits",   Icons.Default.Loop)
    object Settings : Screen("settings", "Theme",    Icons.Default.Palette)
}

@Composable
fun BottomNavBar(currentRoute: String, onNavigate: (String) -> Unit) {
    NavigationBar {
        listOf(Screen.Todo, Screen.Calendar, Screen.Notes, Screen.Habits, Screen.Settings)
            .forEach { screen ->
                NavigationBarItem(
                    icon  = { Icon(screen.icon, screen.title) },
                    label = { Text(screen.title, style = MaterialTheme.typography.labelSmall) },
                    selected = currentRoute == screen.route,
                    onClick  = { onNavigate(screen.route) }
                )
            }
    }
}
