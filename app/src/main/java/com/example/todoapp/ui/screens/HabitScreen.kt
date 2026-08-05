package com.example.todoapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todoapp.data.entities.Habit
import com.example.todoapp.ui.components.GlassCard
import com.example.todoapp.ui.theme.*
import com.example.todoapp.ui.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitScreen(vm: AppViewModel) {
    val habits by vm.habits.collectAsState()
    var showAdd by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Habits",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAdd = true },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Add,
                    "Add Habit",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = habits,
                key = { it.id }
            ) { habit ->
                HabitCard(
                    habit = habit,
                    vm = vm
                )
            }
        }
    }

    if (showAdd) {
        AddHabitDialog(
            onDismiss = { showAdd = false },
            onAdd = { name, description, color ->
                vm.addHabit(name, description, color)
                showAdd = false
            }
        )
    }
}

@Composable
fun HabitCard(
    habit: Habit,
    vm: AppViewModel
) {
    var expanded by remember { mutableStateOf(false) }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ---- Color Indicator ----
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(habit.color))
                )

                Spacer(modifier = Modifier.width(12.dp))

                // ---- Habit Name ----
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // ---- Streak Counter ----
                if (habit.streak > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Whatshot,
                            contentDescription = "Streak",
                            tint = GlassWarning,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${habit.streak}",
                            style = MaterialTheme.typography.labelMedium,
                            color = GlassWarning
                        )
                    }
                }

                // ---- Check-in Button ----
                IconButton(
                    onClick = { vm.toggleHabitCompletion(habit.id) }
                ) {
                    Icon(
                        if (habit.completedToday) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = if (habit.completedToday) "Completed" else "Check in",
                        tint = if (habit.completedToday) GlassSuccess else GlassGray
                    )
                }
            }

            // ---- Expanded Content ----
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))

                // Description
                if (!habit.description.isNullOrBlank()) {
                    Text(
                        text = habit.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GlassTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // ---- Statistics ----
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        label = "Total",
                        value = "${habit.totalCompletions}"
                    )
                    StatItem(
                        label = "Current Streak",
                        value = "${habit.streak}"
                    )
                    StatItem(
                        label = "Best Streak",
                        value = "${habit.bestStreak}"
                    )
                }

                // ---- Progress Bar (Simple) ----
                val progress = if (habit.goal > 0) {
                    habit.totalCompletions.toFloat() / habit.goal
                } else {
                    0f
                }
                LinearProgressIndicator(
                    progress = progress.coerceIn(0f, 1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    color = Color(habit.color),
                    trackColor = GlassGray.copy(alpha = 0.2f)
                )

                // ---- Delete Button ----
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { vm.deleteHabit(habit.id) },
                        colors = TextButtonDefaults.textButtonColors(
                            contentColor = GlassError
                        )
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = GlassGray
        )
    }
}

@Composable
fun AddHabitDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String?, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(HabitColors.first().hashCode()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "New Habit",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Habit Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GlassPrimary,
                        unfocusedBorderColor = GlassGray
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GlassPrimary,
                        unfocusedBorderColor = GlassGray
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Color Picker
                Text(
                    "Choose Color",
                    style = MaterialTheme.typography.labelMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HabitColors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable {
                                    selectedColor = color.hashCode()
                                }
                                .then(
                                    if (selectedColor == color.hashCode()) {
                                        Modifier.border(
                                            3.dp,
                                            Color.White,
                                            CircleShape
                                        )
                                    } else {
                                        Modifier
                                    }
                                )
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(name, description.takeIf { it.isNotBlank() }, selectedColor)
                    }
                },
                enabled = name.isNotBlank(),
                colors = TextButtonDefaults.textButtonColors(
                    contentColor = if (name.isNotBlank()) GlassPrimary else GlassGray
                )
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = TextButtonDefaults.textButtonColors(
                    contentColor = GlassGray
                )
            ) {
                Text("Cancel")
            }
        }
    )
}
