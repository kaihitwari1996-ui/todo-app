package com.example.todoapp.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.todoapp.data.entities.Note
import com.example.todoapp.data.entities.Tag
import com.example.todoapp.ui.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(vm: AppViewModel) {
    val notes    by vm.notes.collectAsState()
    val tags     by vm.tags.collectAsState()
    val query    by vm.noteSearch.collectAsState()
    var editNote by remember { mutableStateOf<Note?>(null) }
    var showAdd  by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notes", style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { editNote = null; showAdd = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) { Icon(Icons.Default.Add, "Add", tint = MaterialTheme.colorScheme.onPrimary) }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { inner ->
        Column(Modifier.fillMaxSize().padding(inner)) {
            // Search
            OutlinedTextField(
                value = query,
                onValueChange = { vm.setNoteSearch(it) },
                placeholder = { Text("Search notes…", style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (query.isNotBlank()) IconButton({ vm.setNoteSearch("") }) {
                        Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp))
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            if (notes.isEmpty()) {
                EmptyState(Icons.Default.Notes, if (query.isBlank()) "Tap + to write your first note" else "No notes match \"$query\"")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(notes, key = { it.id }) { note ->
                        NoteCard(
                            note    = note,
                            tags    = tags,
                            onClick = { editNote = note; showAdd = true },
                            onPin   = { vm.togglePin(note) },
                            onDelete = { vm.deleteNote(note) }
                        )
                    }
                }
            }
        }
    }

    if (showAdd) {
        NoteEditorDialog(
            note      = editNote,
            tags      = tags,
            onDismiss = { showAdd = false; editNote = null },
            onSave    = { title, content, tagIds ->
                if (editNote != null) {
                    vm.saveNote(editNote!!.copy(title = title, content = content,
                        tagIds = tagIds.joinToString(",")))
                } else {
                    vm.addNote(title, content, tagIds)
                }
                showAdd = false; editNote = null
            },
            onNewTag = { name, color -> vm.addTag(name, color) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCard(note: Note, tags: List<Tag>, onClick: () -> Unit, onPin: () -> Unit, onDelete: () -> Unit) {
    val noteTags = tags.filter { note.tagIds.split(",").contains(it.id.toString()) }
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, if (note.isPinned) MaterialTheme.colorScheme.primary.copy(0.5f)
                                    else MaterialTheme.colorScheme.outline.copy(0.2f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                if (note.isPinned) {
                    Icon(Icons.Default.PushPin, null, Modifier.size(14.dp).padding(end = 4.dp),
                        tint = MaterialTheme.colorScheme.primary)
                }
                Text(note.title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                IconButton(onClick = onPin, modifier = Modifier.size(28.dp)) {
                    Icon(if (note.isPinned) Icons.Default.PushPin else Icons.Default.PushPin, null,
                        Modifier.size(14.dp),
                        tint = if (note.isPinned) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurfaceVariant.copy(0.4f))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, null, Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.4f))
                }
            }
            if (note.content.isNotBlank()) {
                Text(
                    note.content.let { if (it.length > 150) it.take(150) + "…" else it },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            if (noteTags.isNotEmpty()) {
                Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    noteTags.take(4).forEach { tag ->
                        Box(
                            Modifier.clip(RoundedCornerShape(3.dp))
                                .background(Color(tag.color).copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("#${tag.name}", style = MaterialTheme.typography.labelSmall, color = Color(tag.color))
                        }
                    }
                }
            }
            Text(
                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(note.updatedAt)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun NoteEditorDialog(
    note: Note?, tags: List<Tag>,
    onDismiss: () -> Unit,
    onSave: (String, String, List<Int>) -> Unit,
    onNewTag: (String, Long) -> Unit
) {
    var title    by remember { mutableStateOf(note?.title ?: "") }
    var content  by remember { mutableStateOf(note?.content ?: "") }
    var selTags  by remember { mutableStateOf(note?.tagIds?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList()) }
    var newTagName by remember { mutableStateOf("") }
    var showTagField by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title  = { Text(if (note == null) "New Note" else "Edit Note", style = MaterialTheme.typography.headlineMedium) },
        text   = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(title, { title = it }, label = { Text("Title *") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(content, { content = it }, label = { Text("Write your note…") },
                    modifier = Modifier.fillMaxWidth().height(240.dp), maxLines = 20)

                if (tags.isNotEmpty()) {
                    Text("Tags", style = MaterialTheme.typography.labelLarge)
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        tags.forEach { tag ->
                            FilterChip(
                                selected = selTags.contains(tag.id),
                                onClick  = { selTags = if (selTags.contains(tag.id)) selTags - tag.id else selTags + tag.id },
                                label    = { Text("#${tag.name}", style = MaterialTheme.typography.labelSmall) },
                                colors   = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(tag.color).copy(0.2f),
                                    selectedLabelColor = Color(tag.color))
                            )
                        }
                    }
                }
                if (showTagField) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(newTagName, { newTagName = it }, label = { Text("Tag name") },
                            modifier = Modifier.weight(1f), singleLine = true)
                        IconButton({
                            if (newTagName.isNotBlank()) { onNewTag(newTagName.trim(), 0xFF4A90E2); newTagName = ""; showTagField = false }
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
        confirmButton  = { Button({ onSave(title, content, selTags) }, enabled = title.isNotBlank()) { Text("Save") } },
        dismissButton  = { TextButton(onDismiss) { Text("Cancel") } }
    )
}    onSave: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (note == null) "New Note" else "Edit Note") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    maxLines = 10
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(title, content) },
                enabled = title.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
