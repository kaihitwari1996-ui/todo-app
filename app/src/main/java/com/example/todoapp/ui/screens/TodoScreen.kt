package com.example.todoapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todoapp.data.entities.Priority
import com.example.todoapp.data.entities.RecurrenceType
import com.example.todoapp.data.entities.Task
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
    var showAdd by remember { mutableStateOf(false) }
    var expandedTaskId by remember { mutableStateOf<Int?>(null) }

    // Load subtasks for expanded tasks
    LaunchedEffect(expandedTaskId) {
        expandedTaskId?.let { vm.loadSubTasks(it) }
    }

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
            // ---- Filter Tabs ----
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
                                        .background(Color(tag.color.toInt()))
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
                    key = { it.id ?: 0 }
                ) { task ->
                    GlassTaskCard(
                        task = task,
                        vm = vm,
                        expandedTaskId = expandedTaskId,
                        onExpand = { expandedTaskId = if (expandedTaskId == task.id) null else task.id }
                    )
                }
            }
        }
    }

    // ---- Add Task Dialog ----
    if (showAdd) {
        AddTaskDialog(
            onDismiss = { showAdd = false },
            onAdd = { title, description, category, priority, expiryDate, recurrenceType, tagIds ->
                vm.addTask(title, description, category, priority, expiryDate, recurrenceType, tagIds)
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
    expandedTaskId: Int?,
    onExpand: () -> Unit
) {
    val isExpanded = expandedTaskId == task.id
    val subTasks = if (isExpanded) vm.subTasks.value[task.id] ?: emptyList() else emptyList()

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
                    onCheckedChange = { vm.toggleTask(task) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = GlassPrimary,
                        uncheckedColor = GlassGray
                    )
                )

                // ---- Title ----
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (task.isCompleted) GlassGray else MaterialTheme.colorScheme.onBackground,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // ---- Priority Dot ----
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

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    // Description
                    if (task.description.isNotBlank()) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = GlassTextSecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    // Due date
                    task.expiryDate?.let { timestamp ->
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
                                text = "Due: ${AppViewModel.formatDate(timestamp)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = GlassGray
                            )
                        }
                    }

                    // Category
                    if (task.category.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = "📂 ${task.category}",
                                style = MaterialTheme.typography.bodySmall,
                                color = GlassGray
                            )
                        }
                    }

                    // Tags
                    val tagIds = task.tagIds.split(",").mapNotNull { it.toIntOrNull() }
                    if (tagIds.isNotEmpty()) {
                        Row(
                            modifier = Modifier.padding(bottom = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            tagIds.forEach { tagId ->
                                val tag = vm.tags.value.find { it.id == tagId }
                                if (tag != null) {
                                    Box(
                                        Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(tag.color.toInt()).copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            "#${tag.name}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(tag.color.toInt())
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ---- Subtasks ----
                    if (subTasks.isNotEmpty()) {
                        Text(
                            text = "Subtasks",
                            style = MaterialTheme.typography.labelMedium,
                            color = GlassTextSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        )
                        subTasks.forEach { sub ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = sub.isCompleted,
                                    onCheckedChange = { vm.toggleSubTask(sub) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = GlassPrimary,
                                        uncheckedColor = GlassGray
                                    )
                                )
                                Text(
                                    text = sub.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (sub.isCompleted) GlassGray else MaterialTheme.colorScheme.onBackground,
                                    textDecoration = if (sub.isCompleted) TextDecoration.LineThrough else null
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
                            onClick = { /* Edit – we'll implement later */ },
                            colors = TextButtonDefaults.textButtonColors(
                                contentColor = GlassPrimary
                            )
                        ) {
                            Text("Edit")
                        }
                        TextButton(
                            onClick = { vm.deleteTask(task) },
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
    onAdd: (
        title: String,
        description: String,
        category: String,
        priority: Priority,
        expiryDate: Long?,
        recurrenceType: RecurrenceType,
        tagIds: List<Int>
    ) -> Unit,
    tags: List<com.example.todoapp.data.entities.Tag>
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("General") }
    var priority by remember { mutableStateOf(Priority.NONE) }
    var expiryDate by remember { mutableStateOf<Long?>(null) }
    var recurrenceType by remember { mutableStateOf(RecurrenceType.NONE) }
    var selectedTagIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
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
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
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
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
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

                // Recurrence
                Text(
                    "Recurrence",
                    style = MaterialTheme.typography.labelMedium
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    RecurrenceType.values().forEach { r ->
                        FilterChip(
                            selected = recurrenceType == r,
                            onClick = { recurrenceType = r },
                            label = {
                                Text(
                                    r.name,
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

                // Tags
                if (tags.isNotEmpty()) {
                    Text(
                        "Tags",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        tags.forEach { tag ->
                            FilterChip(
                                selected = selectedTagIds.contains(tag.id),
                                onClick = {
                                    selectedTagIds = if (selectedTagIds.contains(tag.id)) {
                                        selectedTagIds - tag.id
                                    } else {
                                        selectedTagIds + tag.id
                                    }
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
                                            .background(Color(tag.color.toInt()))
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
                            if (expiryDate != null) {
                                AppViewModel.formatDate(expiryDate!!)
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
                        onAdd(
                            title,
                            description,
                            category,
                            priority,
                            expiryDate,
                            recurrenceType,
                            selectedTagIds.toList()
                        )
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

    // ---- Date Picker (simplified placeholder) ----
    if (showDatePicker) {
        // For production, use a proper date picker library like `com.maxkeppeler.sheets:calendar`
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = { Text("Select Date") },
            text = {
                Column {
                    Text("Pick a date (placeholder)")
                    // You can add a simple date picker here
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        expiryDate = System.currentTimeMillis()
                        showDatePicker = false
                    },
                    colors = TextButtonDefaults.textButtonColors(
                        contentColor = GlassPrimary
                    )
                ) {
                    Text("Today")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false },
                    colors = TextButtonDefaults.textButtonColors(
                        contentColor = GlassGray
                    )
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
