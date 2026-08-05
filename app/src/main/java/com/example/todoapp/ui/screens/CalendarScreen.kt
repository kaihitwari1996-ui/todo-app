package com.example.todoapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todoapp.ui.components.GlassCard
import com.example.todoapp.ui.theme.*
import com.example.todoapp.ui.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(vm: AppViewModel) {
    val selectedDate by vm.selectedDate.collectAsState()
    val calendarNote by vm.calendarNote.collectAsState()
    val calendarNotes by vm.calendarNotes.collectAsState()
    var noteContent by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    // Load note for selected date
    LaunchedEffect(selectedDate) {
        vm.selectDate(selectedDate)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Calendar",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Date selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDate) ?: Date()
                    ),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(
                    onClick = { showDatePicker = true }
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Pick date",
                        tint = GlassPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Note for selected date
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Note for ${SimpleDateFormat("MMM d", Locale.getDefault()).format(
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDate) ?: Date()
                        )}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = calendarNote?.content ?: noteContent,
                        onValueChange = { noteContent = it },
                        label = { Text("Write a note...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GlassPrimary,
                            unfocusedBorderColor = GlassGray
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            vm.saveCalendarNote(noteContent)
                            noteContent = ""
                        },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GlassPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Save Note")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List of all calendar notes (optional)
            if (calendarNotes.isNotEmpty()) {
                Text(
                    text = "All Calendar Notes",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(calendarNotes.reversed()) { note ->
                        GlassCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(
                                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(note.date) ?: Date()
                                    ),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = GlassGray
                                )
                                Text(
                                    text = note.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Date picker placeholder
    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = { Text("Select Date") },
            text = {
                Column {
                    Text("Pick a date (placeholder)")
                    // Use a real date picker library for production
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val today = AppViewModel.getTodayString()
                        vm.selectDate(today)
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
