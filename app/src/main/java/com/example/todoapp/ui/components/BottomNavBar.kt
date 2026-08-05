package com.example.todoapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.todoapp.ui.theme.GlassPrimary
import com.example.todoapp.ui.theme.GlassSurface

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        containerColor = GlassSurface.copy(alpha = 0.15f),
        tonalElevation = 0.dp
    ) {
        val items = listOf(
            NavItem("todo", "Tasks", Icons.Default.Home),
            NavItem("calendar", "Calendar", Icons.Default.DateRange),
            NavItem("notes", "Notes", Icons.Default.Note),
            NavItem("habits", "Habits", Icons.Default.Favorite),
            NavItem("settings", "Settings", Icons.Default.Settings)
        )

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GlassPrimary,
                    selectedTextColor = GlassPrimary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    indicatorColor = GlassPrimary.copy(alpha = 0.2f)
                )
            )
        }
    }
}

data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)
