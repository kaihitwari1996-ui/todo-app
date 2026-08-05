package com.example.todoapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.todoapp.data.entities.Habit
import com.example.todoapp.data.entities.HabitEntry
import com.example.todoapp.ui.theme.HabitColors
import com.example.todoapp.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitScreen(vm: AppViewModel) {
    val habits  by vm.habits.collectAsState()
    val entries by vm.habitEntries.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    val last7   = vm.getLastNDates(7)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Habits", style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAdd = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Add", tint = MaterialTheme.colorScheme.onPrimary)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { inner ->
        if (habits.isEmpty()) {
            EmptyState(Icons.Default.Loop, "Build a new habit — tap +")
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(habits, key = { it.id }) { habit ->
                    HabitCard(
                        habit     = habit,
                        entries   = entries[habit.id] ?: emptyList(),
                        last7     = last7,
                        streak    = vm.getStreak(habit.id),
                        doneToday = vm.isHabitDoneToday(habit.id),
                        onToggle  = { vm.toggleHabit(habit.id, AppViewModel.todayString()) },
                        onDelete  = { vm.deleteHabit(habit) }
                    )
                }
            }
        }
    }

    if (showAdd) {
        AddHabitDialog(
            onDismiss = { showAdd = false },
            onConfirm = { name, desc, color, target ->
                vm.addHabit(name, desc, color, target)
                showAdd = false
            }
        )
    }
}

@Composable
fun HabitCard(
    habit: Habit,
    entries: List<HabitEntry>,
    last7: List<String>,
    streak: Int,
    doneToday: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val habitColor     = Color(habit.color)
    val completedDates = entries.map { it.date }.toSet()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border   = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(Modifier.padding(16.dp)) {

            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(habitColor)
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(habit.name, style = MaterialTheme.typography.titleLarge)
                    if (habit.description.isNotBlank()) {
                        Text(
                            habit.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Streak badge
                if (streak > 0) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("🔥", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "$streak days",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        Icons.Default.Delete, null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Last 7 days tracker dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                last7.forEach { date ->
                    val done    = date in completedDates
                    val isToday = date == AppViewModel.todayString()
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        done    -> habitColor
                                        isToday -> habitColor.copy(alpha = 0.2f)
                                        else    -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (done) {
                                Icon(
                                    Icons.Default.Check, null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.White
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            date.takeLast(2),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Today toggle button
            Button(
                onClick  = onToggle,
                modifier = Modifier.fillMaxWidth(),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = if (doneToday) habitColor
                                     else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor   = if (doneToday) Color.White
                                     else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(
                    if (doneToday) Icons.Default.CheckCircle
                    else Icons.Default.RadioButtonUnchecked,
                    null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    if (doneToday) "Done today!" else "Mark as done today",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun AddHabitDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Long, Int) -> Unit
) {
    var name       by remember { mutableStateOf("") }
    var desc       by remember { mutableStateOf("") }
    var colorIdx   by remember { mutableIntStateOf(0) }
    var targetDays by remember { mutableIntStateOf(7) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Habit", style = MaterialTheme.typography.headlineMedium) },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value         = name,
                    onValueChange = { name = it },
                    label         = { Text("Habit name *") },
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true
                )
                OutlinedTextField(
                    value         = desc,
                    onValueChange = { desc = it },
                    label         = { Text("Description") },
                    modifier      = Modifier.fillMaxWidth(),
                    maxLines      = 2
                )

                Text("Color", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HabitColors.forEachIndexed { i, color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { colorIdx = i },
                            contentAlignment = Alignment.Center
                        ) {
                            if (colorIdx == i) {
                                Icon(
                                    Icons.Default.Check, null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                Text(
                    "Target: $targetDays days/week",
                    style = MaterialTheme.typography.labelLarge
                )
                Slider(
                    value         = targetDays.toFloat(),
                    onValueChange = { targetDays = it.toInt() },
                    valueRange    = 1f..7f,
                    steps         = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick  = {
                    onConfirm(name, desc, HabitColors[colorIdx].value.toLong(), targetDays)
                },
                enabled  = name.isNotBlank()
            ) { Text("Add Habit") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
