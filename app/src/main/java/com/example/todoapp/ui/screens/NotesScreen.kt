package com.example.todoapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.todoapp.data.Tag

@Composable
fun NoteDialog(
    note: com.example.todoapp.data.Note? = null,
    tags: List<Tag> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (String, String, Set<Int>) -> Unit,
    onNewTag: (String, Int) -> Unit = { _, _ -> }
) {
    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }
    var selTags by remember { mutableStateOf(note?.tagIds ?: emptySet()) }
    var showTagField by remember { mutableStateOf(false) }
    var newTagName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (note == null) "New Note" else "Edit Note") },
        text = {
            Column(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Write your note…") },
                    modifier = Modifier.fillMaxWidth().height(240.dp),
                    maxLines = 20
                )
                Spacer(Modifier.height(8.dp))
                if (tags.isNotEmpty()) {
                    Text("Tags", style = MaterialTheme.typography.labelLarge)
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        tags.forEach { tag ->
                            FilterChip(
                                selected = selTags.contains(tag.id),
                                onClick = {
                                    selTags = if (selTags.contains(tag.id)) selTags - tag.id else selTags + tag.id
                                },
                                label = { Text("#${tag.name}", style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(tag.color).copy(0.2f),
                                    selectedLabelColor = Color(tag.color)
                                )
                            )
                        }
                    }
                }
                if (showTagField) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = newTagName,
                            onValueChange = { newTagName = it },
                            label = { Text("Tag name") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        IconButton(onClick = {
                            if (newTagName.isNotBlank()) {
                                onNewTag(newTagName.trim(), 0xFF4A90E2.toInt())
                                newTagName = ""
                                showTagField = false
                            }
                        }) {
                            Icon(Icons.Default.Check, contentDescription = null)
                        }
                    }
                } else {
                    TextButton(onClick = { showTagField = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("New tag", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title, content, selTags) },
                enabled = title.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
