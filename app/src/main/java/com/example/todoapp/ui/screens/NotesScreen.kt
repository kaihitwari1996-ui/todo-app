package com.example.todoapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.todoapp.data.entities.Note
import com.example.todoapp.data.entities.Tag
import com.example.todoapp.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(vm: AppViewModel) {
    val notes by vm.notes.collectAsState()
    val tags  by vm.tags.collectAsState()
    var showAdd   by remember { mutableStateOf(false) }
    var editNote  by remember { mutableStateOf<Note?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(notes, searchQuery) {
        if (searchQuery.isBlank()) notes
        else notes.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.content.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notes", style = MaterialTheme.typography.headlineMedium) },
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
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search notes…") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                singleLine = true
            )
            if (filtered.isEmpty()) {
                EmptyState(Icons.Default.Notes, "No notes yet — tap + to create one")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered, key = { it.id }) { note ->
                        NoteCard(
                            note     = note,
                            tags     = tags,
                            onClick  = { editNote = note },
                            onDelete = { vm.deleteNote(note) }
                        )
                    }
                }
            }
        }
    }

    if (showAdd || editNote != null) {
        NoteDialog(
            note      = editNote,
            tags      = tags,
            onDismiss = { showAdd = false; editNote = null },
            onSave    = { title, content, selTags ->
                val existing = editNote
                if (existing != null)
                    vm.updateNote(existing.copy(title = title, content = content,
                        tagIds = selTags.joinToString(",")))
                else
                    vm.addNote(title, content, selTags)
                showAdd = false; editNote = null
            },
            onNewTag  = { name, color -> vm.addTag(name, color.toLong()) }
        )
    }
}

@Composable
fun NoteCard(note: Note, tags: List<Tag>, onClick: () -> Unit, onDelete: () -> Unit) {
    val noteTags = tags.filter { tag ->
        note.tagIds.split(",").filter { it.isNotBlank() }.contains(tag.id.toString())
    }
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border   = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(note.title, style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f))
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.4f))
                }
            }
            if (note.content.isNotBlank()) {
                Text(note.content,
                    style    = MaterialTheme.typography.bodyMedium,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp))
            }
            if (noteTags.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(top = 8.dp).horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    noteTags.take(4).forEach { tag ->
                        Box(
                            Modifier.clip(RoundedCornerShape(3.dp))
                                .background(Color(tag.color).copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("#${tag.name}", style = MaterialTheme.typography.labelSmall,
                                color = Color(tag.color))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteDialog(
    note: Note? = null,
    tags: List<Tag> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (String, String, Set<Int>) -> Unit,
    onNewTag: (String, Int) -> Unit = { _, _ -> }
) {
    var title       by remember { mutableStateOf(note?.title ?: "") }
    var content     by remember { mutableStateOf(note?.content ?: "") }
    var selTags     by remember {
        mutableStateOf<Set<Int>>(
            note?.tagIds?.split(",")?.filter { it.isNotBlank() }
                ?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
        )
    }
    var showTagField by remember { mutableStateOf(false) }
    var newTagName   by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (note == null) "New Note" else "Edit Note") },
        text  = {
            Column(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value         = title,
                    onValueChange = { title = it },
                    label         = { Text("Title") },
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value         = content,
                    onValueChange = { content = it },
                    label         = { Text("Write your note…") },
                    modifier      = Modifier.fillMaxWidth().height(200.dp),
                    maxLines      = 20
                )
                Spacer(Modifier.height(8.dp))
                if (tags.isNotEmpty()) {
                    Text("Tags", style = MaterialTheme.typography.labelLarge)
                    Row(Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        tags.forEach { tag ->
                            FilterChip(
                                selected = selTags.contains(tag.id),
                                onClick  = {
                                    selTags = if (selTags.contains(tag.id))
                                        selTags - tag.id else selTags + tag.id
                                },
                                label  = { Text("#${tag.name}",
                                    style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(tag.color).copy(0.2f),
                                    selectedLabelColor     = Color(tag.color))
                            )
                        }
                    }
                }
                if (showTagField) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(newTagName, { newTagName = it },
                            label    = { Text("Tag name") },
                            modifier = Modifier.weight(1f), singleLine = true)
                        IconButton(onClick = {
                            if (newTagName.isNotBlank()) {
                                onNewTag(newTagName.trim(), 0xFF4A90E2.toInt())
                                newTagName   = ""
                                showTagField = false
                            }
                        }) { Icon(Icons.Default.Check, null) }
                    }
                } else {
                    TextButton(onClick = { showTagField = true }) {
                        Icon(Icons.Default.Add, null, Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("New tag", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(title, content, selTags) }, enabled = title.isNotBlank()) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
