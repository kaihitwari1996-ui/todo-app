package com.example.todoapp.ui.screens

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
                key = { it.id ?: 0 }
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
            onAdd = { name, description, color, targetDays ->
                vm.addHabit(name, description, color, targetDays)
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
    val streak = remember(habit.id) { vm.getStreak(habit.id) }
    val doneToday = remember(habit.id) { vm.isHabitDoneToday(habit.id) }
    val today = AppViewModel.getTodayString()

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
                // Color indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(habit.color.toInt()))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Streak
                if (streak > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Whatshot,
                            contentDescription = "Streak",
                            tint = GlassWarning,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "$streak",
                            style = MaterialTheme.typography.labelMedium,
                            color = GlassWarning
                        )
                    }
                }

                // Check-in button
                IconButton(
                    onClick = { vm.toggleHabit(habit.id, today) }
                ) {
                    Icon(
                        if (doneToday) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = if (doneToday) "Completed" else "Check in",
                        tint = if (doneToday) GlassSuccess else GlassGray
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))

                if (habit.description.isNotBlank()) {
                    Text(
                        text = habit.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GlassTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Target days per week
                Text(
                    text = "Target: ${habit.targetDaysPerWeek} days/week",
                    style = MaterialTheme.typography.bodySmall,
                    color = GlassGray
                )

                // Simple progress: show last 7 days
                val last7Days = vm.getLastNDates(7)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    last7Days.forEach { date ->
                        val done = vm.habitEntries.value[habit.id]?.any { it.date == date } ?: false
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    if (done) Color(habit.color.toInt()) else GlassGray.copy(alpha = 0.3f)
                                )
                        ) {
                            if (done) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Delete button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { vm.deleteHabit(habit) },
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
fun AddHabitDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, description: String, color: Long, targetDaysPerWeek: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(HabitColors.first().hashCode().toLong()) }
    var targetDays by remember { mutableStateOf(7) }

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

                Text(
                    "Target Days Per Week",
                    style = MaterialTheme.typography.labelMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (1..7).forEach { days ->
                        FilterChip(
                            selected = targetDays == days,
                            onClick = { targetDays = days },
                            label = {
                                Text(
                                    "$days",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GlassPrimary.copy(alpha = 0.2f),
                                selectedLabelColor = GlassPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

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
                                    selectedColor = color.hashCode().toLong()
                                }
                                .then(
                                    if (selectedColor == color.hashCode().toLong()) {
                                        Modifier.border(3.dp, Color.White, CircleShape)
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
                        onAdd(name, description, selectedColor, targetDays)
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
