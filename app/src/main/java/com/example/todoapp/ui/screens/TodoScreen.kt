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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todoapp.data.entities.*
import com.example.todoapp.ui.theme.*
import com.example.todoapp.ui.viewmodel.AppViewModel
import com.example.todoapp.ui.viewmodel.TaskFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(vm: AppViewModel) {
    val tasks        by vm.filteredTasks.collectAsState()
    val tags         by vm.tags.collectAsState()
    val taskFilter   by vm.taskFilter.collectAsState()
    val tagFilter    by vm.tagFilter.collectAsState()
    val subTasks     by vm.subTasks.collectAsState()
    var showAdd      by remember { mutableStateOf(false) }
    var expandedId   by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tasks", style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAdd = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) { Icon(Icons.Default.Add, "Add", tint = MaterialTheme.colorScheme.onPrimary) }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { inner ->
        Column(Modifier.fillMaxSize().padding(inner)) {

            // Smart filter tabs
            ScrollableTabRow(
                selectedTabIndex = TaskFilter.values().indexOf(taskFilter),
                edgePadding = 12.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {}
            ) {
                TaskFilter.values().forEach { f ->
                    Tab(
                        selected = taskFilter == f,
                        onClick  = { vm.setTaskFilter(f) },
                        text     = {
                            Text(
                                when (f) {
                                    TaskFilter.ALL           -> "All"
                                    TaskFilter.ACTIVE        -> "Active"
                                    TaskFilter.TODAY         -> "Today"
                                    TaskFilter.HIGH_PRIORITY -> "⚡ Priority"
                                },
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    )
                }
            }

            // Tag filter chips
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
                        onClick  = { vm.setTagFilter(null) },
                        label    = { Text("All", style = MaterialTheme.typography.labelSmall) }
                    )
                    tags.forEach { tag ->
                        FilterChip(
                            selected = tagFilter == tag.id,
                            onClick  = { vm.setTagFilter(if (tagFilter == tag.id) null else tag.id) },
                            label    = { Text("#${tag.name}", style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = {
                                Box(Modifier.size(8.dp).clip(CircleShape).background(Color(tag.color)))
                            }
                        )
                    }
                }
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            }

            if (tasks.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.CheckCircleOutline,
                    text = when (taskFilter) {
                        TaskFilter.TODAY         -> "Nothing due today 🎉"
                        TaskFilter.HIGH_PRIORITY -> "No high priority tasks"
                        TaskFilter.ACTIVE        -> "All done!"
                        else                     -> "Tap + to add your first task"
                    }
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        val expanded = expandedId == task.id
                        if (expanded) vm.loadSubTasks(task.id)
                        TaskCard(
                            task         = task,
                            tags         = tags,
                            subTasks     = subTasks[task.id] ?: emptyList(),
                            isExpanded   = expanded,
                            onToggle     = { vm.toggleTask(task) },
                            onDelete     = { vm.deleteTask(task) },
                            onExpand     = { expandedId = if (expanded) null else task.id },
                            onAddSub     = { vm.addSubTask(task.id, it) },
                            onToggleSub  = { vm.toggleSubTask(it) },
                            onDeleteSub  = { vm.deleteSubTask(it) }
                        )
                    }
                }
            }
        }
    }

    if (showAdd) {
        AddTaskSheet(
            tags      = tags,
            onDismiss = { showAdd = false },
            onConfirm = { title, desc, cat, pri, exp, rec, tIds ->
                vm.addTask(title, desc, cat, pri, exp, rec, tIds)
                showAdd = false
            },
            onNewTag  = { name, color -> vm.addTag(name, color) }
        )
    }
}

@Composable
fun TaskCard(
    task: Task, tags: List<Tag>, subTasks: List<SubTask>,
    isExpanded: Boolean,
    onToggle: () -> Unit, onDelete: () -> Unit, onExpand: () -> Unit,
    onAddSub: (String) -> Unit, onToggleSub: (SubTask) -> Unit, onDeleteSub: (SubTask) -> Unit
) {
    val priColor = when (task.priority) {
        Priority.HIGH   -> PriorityHigh
        Priority.MEDIUM -> PriorityMedium
        Priority.LOW    -> PriorityLow
        Priority.NONE   -> Color.Transparent
    }
    val taskTags = tags.filter { task.tagIds.split(",").contains(it.id.toString()) }
    var newSub by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onExpand() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = if (task.priority != Priority.NONE) 2.dp else 1.dp,
            color = if (task.priority != Priority.NONE) priColor.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        )
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Priority strip
                if (task.priority != Priority.NONE) {
                    Box(
                        Modifier.width(4.dp).height(52.dp).clip(RoundedCornerShape(2.dp))
                            .background(priColor)
                    )
                    Spacer(Modifier.width(10.dp))
                }

                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggle() },
                    modifier = Modifier.padding(top = 2.dp)
                )
                Spacer(Modifier.width(6.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (task.isCompleted) MaterialTheme.colorScheme.onSurface.copy(0.4f)
                                else MaterialTheme.colorScheme.onSurface,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (task.description.isNotBlank()) {
                        Text(
                            task.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    // Meta chips
                    Row(
                        Modifier.padding(top = 6.dp).horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        task.expiryDate?.let { ts ->
                            val overdue = ts < System.currentTimeMillis() && !task.isCompleted
                            MiniChip(AppViewModel.formatDateShort(ts), Icons.Default.CalendarToday,
                                if (overdue) PriorityHigh else MaterialTheme.colorScheme.secondary)
                        }
                        if (task.recurrenceType != RecurrenceType.NONE)
                            MiniChip(task.recurrenceType.name.lowercase().replaceFirstChar { it.uppercase() },
                                Icons.Default.Repeat, MaterialTheme.colorScheme.secondary)
                        if (task.category == "Target")
                            MiniChip("Target", Icons.Default.TrackChanges, MaterialTheme.colorScheme.primary)
                    }
                    // Tags
                    if (taskTags.isNotEmpty()) {
                        Row(
                            Modifier.padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            taskTags.take(4).forEach { tag ->
                                Box(
                                    Modifier.clip(RoundedCornerShape(3.dp))
                                        .background(Color(tag.color).copy(alpha = 0.18f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("#${tag.name}", style = MaterialTheme.typography.labelSmall,
                                        color = Color(tag.color))
                                }
                            }
                        }
                    }
                    // Subtask summary
                    if (subTasks.isNotEmpty()) {
                        val done = subTasks.count { it.isCompleted }
                        Text("$done/${subTasks.size} subtasks",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp))
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp)
                    )
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
                            modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Expanded subtasks panel
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    Modifier.fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    if (subTasks.isNotEmpty()) {
                        Text("Subtasks", style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(4.dp))
                        subTasks.forEach { st ->
                            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = st.isCompleted, onCheckedChange = { onToggleSub(st) },
                                    modifier = Modifier.size(28.dp))
                                Text(st.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textDecoration = if (st.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                    modifier = Modifier.weight(1f).padding(horizontal = 6.dp))
                                IconButton(onClick = { onDeleteSub(st) }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                    }
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = newSub,
                            onValueChange = { newSub = it },
                            placeholder = { Text("Add subtask…", style = MaterialTheme.typography.bodySmall) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                        IconButton(onClick = {
                            if (newSub.isNotBlank()) { onAddSub(newSub.trim()); newSub = "" }
                        }) { Icon(Icons.Default.Add, null) }
                    }
                }
            }
        }
    }
}

@Composable
fun MiniChip(text: String, icon: ImageVector, color: Color) {
    Row(
        Modifier.clip(RoundedCornerShape(4.dp)).background(color.copy(alpha = 0.1f))
            .padding(horizontal = 5.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(icon, null, Modifier.size(9.dp), tint = color)
        Text(text, style = MaterialTheme.typography.labelSmall, color = color)
    }
}

@Composable
fun EmptyState(icon: ImageVector, text: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
            Text(text, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskSheet(
    tags: List<Tag>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Priority, Long?, RecurrenceType, List<Int>) -> Unit,
    onNewTag: (String, Long) -> Unit
) {
    var title       by remember { mutableStateOf("") }
    var desc        by remember { mutableStateOf("") }
    var category    by remember { mutableStateOf("General") }
    var priority    by remember { mutableStateOf(Priority.NONE) }
    var recurrence  by remember { mutableStateOf(RecurrenceType.NONE) }
    var expiryDate  by remember { mutableStateOf<Long?>(null) }
    var selectedTags by remember { mutableStateOf<List<Int>>(emptyList()) }
    var showDate    by remember { mutableStateOf(false) }
    var newTagName  by remember { mutableStateOf("") }
    var showTagField by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Task", style = MaterialTheme.typography.headlineMedium) },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(title, { title = it }, label = { Text("Title *") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true)

                OutlinedTextField(desc, { desc = it }, label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(), maxLines = 3)

                // Category
                Text("Category", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("General", "Target").forEach { cat ->
                        FilterChip(cat == category, { category = cat }, label = { Text(cat) })
                    }
                }

                // Priority
                Text("Priority", style = MaterialTheme.typography.labelLarge)
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Priority.values().forEach { p ->
                        val pColor = when (p) {
                            Priority.HIGH   -> PriorityHigh
                            Priority.MEDIUM -> PriorityMedium
                            Priority.LOW    -> PriorityLow
                            Priority.NONE   -> MaterialTheme.colorScheme.outline
                        }
                        FilterChip(
                            selected = priority == p,
                            onClick  = { priority = p },
                            label    = { Text(p.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall) },
                            colors   = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = pColor.copy(alpha = 0.18f),
                                selectedLabelColor = pColor
                            )
                        )
                    }
                }

                // Due date (Target only)
                if (category == "Target") {
                    TextButton(onClick = { showDate = true }) {
                        Icon(Icons.Default.CalendarToday, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            if (expiryDate != null) "Due ${AppViewModel.formatDateShort(expiryDate!!)}"
                            else "Set due date",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                if (showDate) {
                    val pickerState = rememberDatePickerState()
                    DatePickerDialog(
                        onDismissRequest = { showDate = false },
                        confirmButton = {
                            TextButton(onClick = {
                                expiryDate = pickerState.selectedDateMillis
                                showDate = false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDate = false }) { Text("Cancel") }
                        }
                    ) { DatePicker(state = pickerState) }
                }

                // Recurrence
                Text("Recurrence", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RecurrenceType.values().forEach { r ->
                        FilterChip(
                            selected = recurrence == r,
                            onClick  = { recurrence = r },
                            label    = { Text(r.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                // Tags
                if (tags.isNotEmpty()) {
                    Text("Tags", style = MaterialTheme.typography.labelLarge)
                    Row(
                        Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        tags.forEach { tag ->
                            FilterChip(
                                selected = selectedTags.contains(tag.id),
                                onClick  = {
                                    selectedTags = if (selectedTags.contains(tag.id))
                                        selectedTags - tag.id else selectedTags + tag.id
                                },
                                label    = { Text("#${tag.name}", style = MaterialTheme.typography.labelSmall) },
                                colors   = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(tag.color).copy(0.2f),
                                    selectedLabelColor = Color(tag.color)
                                )
                            )
                        }
                    }
                }

                if (showTagField) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(newTagName, { newTagName = it }, label = { Text("Tag name") },
                            modifier = Modifier.weight(1f), singleLine = true)
                        IconButton({
                            if (newTagName.isNotBlank()) {
                                onNewTag(newTagName.trim(), 0xFF4A90E2)
                                newTagName = ""; showTagField = false
                            }
                        }) { Icon(Icons.Default.Check, null) }
                    }
                } else {
                    TextButton({ showTagField = true }) {
                        Icon(Icons.Default.Add, null, Modifier.size(14.dp)); Spacer(Modifier.width(4.dp))
                        Text("New tag", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, desc, category, priority, expiryDate, recurrence, selectedTags) },
                enabled = title.isNotBlank()
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onDismiss) { Text("Cancel") }
        }
    )
}
