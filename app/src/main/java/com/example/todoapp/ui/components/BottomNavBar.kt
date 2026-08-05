package com.example.todoapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

private data class NavItem(val route: String, val icon: ImageVector, val label: String)

private val navItems = listOf(
    NavItem("todo",     Icons.Default.CheckCircle,    "Tasks"),
    NavItem("calendar", Icons.Default.CalendarMonth,  "Calendar"),
    NavItem("notes",    Icons.Default.Notes,          "Notes"),
    NavItem("habits",   Icons.Default.Loop,           "Habits"),
    NavItem("settings", Icons.Default.Settings,       "Settings")
)

@Composable
fun BottomNavBar(currentRoute: String, onNavigate: (String) -> Unit) {
    NavigationBar {
        navItems.forEach { item ->
            NavigationBarItem(
                selected  = currentRoute == item.route,
                onClick   = { onNavigate(item.route) },
                icon      = { Icon(item.icon, contentDescription = item.label) },
                label     = { Text(item.label) }
            )
        }
    }
}
