package com.example.todoapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todoapp.data.entities.*
import com.example.todoapp.ui.components.GlassCard
import com.example.todoapp.ui.theme.*
import com.example.todoapp.ui.viewmodel.AppViewModel
import com.example.todoapp.ui.viewmodel.TaskFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(vm: AppViewModel) {
    val tasks by vm.filteredTasks.collectAsState()
    val tags by vm.tags.collectAsState()
    val taskFilter by vm.taskFilter.collectAsState()
    val tagFilter by vm.tagFilter.collectAsState()
    val subTasks by vm.subTasks.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var expandedId by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Tasks",
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
                    "Add",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ---- Smart Filter Tabs ----
            ScrollableTabRow(
                selectedTabIndex = TaskFilter.values().indexOf(taskFilter),
                edgePadding = 12.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {}
            ) {
                TaskFilter.values().forEach { filter ->
                    Tab(
                        selected = taskFilter == filter,
                        onClick = { vm.setTaskFilter(filter) },
                        text = {
                            Text(
                                when (filter) {
                                    TaskFilter.ALL -> "All"
                                    TaskFilter.ACTIVE -> "Active"
                                    TaskFilter.TODAY -> "Today"
                                    TaskFilter.HIGH_PRIORITY -> "⚡ Priority"
                                },
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    )
                }
            }

            // ---- Tag Filter Chips ----
            if (tags.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = tagFilter == null,
                        onClick = { vm.setTagFilter(null) },
                        label = {
                            Text(
                                "All",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GlassPrimary.copy(alpha = 0.2f),
                            selectedLabelColor = GlassPrimary
                        )
                    )
                    tags.forEach { tag ->
                        FilterChip(
                            selected = tagFilter == tag.id,
                            onClick = {
                                vm.setTagFilter(if (tagFilter == tag.id) null else tag.id)
                            },
                            label = {
                                Text(
                                    "#${tag.name}",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            leadingIcon = {
                                Box(
                                    Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(tag.color))
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GlassPrimary.copy(alpha = 0.2f),
                                selectedLabelColor = GlassPrimary
                            )
                        )
                    }
                }
            }

            // ---- Task List ----
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = tasks,
                    key = { it.id }
                ) { task ->
                    GlassTaskCard(
                        task = task,
                        vm = vm,
                        expandedId = expandedId,
                        onExpand = { expandedId = if (expandedId == task.id) null else task.id }
                    )
                }
            }
        }
    }

    // ---- Add Task Dialog ----
    if (showAdd) {
        AddTaskDialog(
            onDismiss = { showAdd = false },
            onAdd = { title, description, priority, dueDate, tagId ->
                vm.addTask(title, description, priority, dueDate, tagId)
                showAdd = false
            },
            tags = tags
        )
    }
}

@Composable
fun GlassTaskCard(
    task: Task,
    vm: AppViewModel,
    expandedId: Long?,
    onExpand: () -> Unit
) {
    val isExpanded = expandedId == task.id

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ---- Checkbox ----
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { vm.toggleTaskCompletion(task.id) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = GlassPrimary,
                        uncheckedColor = GlassGray
                    )
                )

                // ---- Task Title ----
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (task.isCompleted) {
                        GlassGray
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    },
                    textDecoration = if (task.isCompleted) {
                        TextDecoration.LineThrough
                    } else {
                        null
                    },
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // ---- Priority Badge ----
                if (task.priority != Priority.NONE) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                when (task.priority) {
                                    Priority.HIGH -> PriorityHigh
                                    Priority.MEDIUM -> PriorityMedium
                                    Priority.LOW -> PriorityLow
                                    Priority.NONE -> PriorityNone
                                }
                            )
                    )
                }

                // ---- Expand Button ----
                IconButton(onClick = onExpand) {
                    Icon(
                        if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = GlassGray
                    )
                }
            }

            // ---- Expanded Content ----
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    // Description
                    if (!task.description.isNullOrBlank()) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = GlassTextSecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    // Due Date
                    if (task.dueDate != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = "Due Date",
                                modifier = Modifier.size(16.dp),
                                tint = GlassGray
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Due: ${java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(task.dueDate)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = GlassGray
                            )
                        }
                    }

                    // Tag
                    task.tagId?.let { tagId ->
                        val tag = vm.tags.value.find { it.id == tagId }
                        if (tag != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 4.dp)
                            ) {
                                Box(
                                    Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(tag.color))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "#${tag.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GlassGray
                                )
                            }
                        }
                    }

                    // ---- Subtasks ----
                    val taskSubTasks = vm.subTasks.value.filter { it.taskId == task.id }
                    if (taskSubTasks.isNotEmpty()) {
                        Text(
                            text = "Subtasks",
                            style = MaterialTheme.typography.labelMedium,
                            color = GlassTextSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        )
                        taskSubTasks.forEach { subTask ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = subTask.isCompleted,
                                    onCheckedChange = { vm.toggleSubTaskCompletion(subTask.id) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = GlassPrimary,
                                        uncheckedColor = GlassGray
                                    )
                                )
                                Text(
                                    text = subTask.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (subTask.isCompleted) {
                                        GlassGray
                                    } else {
                                        MaterialTheme.colorScheme.onBackground
                                    },
                                    textDecoration = if (subTask.isCompleted) {
                                        TextDecoration.LineThrough
                                    } else {
                                        null
                                    }
                                )
                            }
                        }
                    }

                    // ---- Actions ----
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { /* Edit action */ },
                            colors = TextButtonDefaults.textButtonColors(
                                contentColor = GlassPrimary
                            )
                        ) {
                            Text("Edit")
                        }
                        TextButton(
                            onClick = { vm.deleteTask(task.id) },
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
}

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String?, Priority, Long?, Long?) -> Unit,
    tags: List<Tag>
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }
    var selectedTagId by remember { mutableStateOf<Long?>(null) }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Add Task",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
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

                // Priority
                Text(
                    "Priority",
                    style = MaterialTheme.typography.labelMedium
                )
                Row {
                    Priority.values().forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = {
                                Text(
                                    p.name,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GlassPrimary.copy(alpha = 0.2f),
                                selectedLabelColor = GlassPrimary
                            ),
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tag
                if (tags.isNotEmpty()) {
                    Text(
                        "Tag",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        FilterChip(
                            selected = selectedTagId == null,
                            onClick = { selectedTagId = null },
                            label = {
                                Text(
                                    "None",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GlassPrimary.copy(alpha = 0.2f),
                                selectedLabelColor = GlassPrimary
                            ),
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        tags.forEach { tag ->
                            FilterChip(
                                selected = selectedTagId == tag.id,
                                onClick = { selectedTagId = tag.id },
                                label = {
                                    Text(
                                        "#${tag.name}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                leadingIcon = {
                                    Box(
                                        Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(tag.color))
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GlassPrimary.copy(alpha = 0.2f),
                                    selectedLabelColor = GlassPrimary
                                ),
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Due Date
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Due Date: ",
                        style = MaterialTheme.typography.labelMedium
                    )
                    TextButton(
                        onClick = { showDatePicker = true },
                        colors = TextButtonDefaults.textButtonColors(
                            contentColor = GlassPrimary
                        )
                    ) {
                        Text(
                            if (dueDate != null) {
                                java.text.SimpleDateFormat(
                                    "MMM dd, yyyy",
                                    java.util.Locale.getDefault()
                                ).format(java.util.Date(dueDate!!))
                            } else {
                                "Set Date"
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(title, description.takeIf { it.isNotBlank() }, priority, dueDate, selectedTagId)
                    }
                },
                enabled = title.isNotBlank(),
                colors = TextButtonDefaults.textButtonColors(
                    contentColor = if (title.isNotBlank()) GlassPrimary else GlassGray
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

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { timestamp ->
                dueDate = timestamp
                showDatePicker = false
            }
        )
    }
}

@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    // Simple date picker implementation
    // For a production app, use a proper date picker library
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select Date") },
        text = {
            Column {
                Text("Date picker placeholder")
                Text("Use a library like 'com.maxkeppeler.sheets:calendar' for a full implementation")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Placeholder - set today's date
                    onDateSelected(System.currentTimeMillis())
                },
                colors = TextButtonDefaults.textButtonColors(
                    contentColor = GlassPrimary
                )
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                colors = TextButtonDefaults.textButtonColors(
                    contentColor = GlassGray
                )
            ) {
                Text("Cancel")
            }
        }
    )
}
