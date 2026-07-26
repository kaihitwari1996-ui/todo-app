package com.example.todoapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.todoapp.data.CalendarNote
import com.example.todoapp.ui.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(viewModel: AppViewModel) {
    val calendarNotes by viewModel.calendarNotes.collectAsState()
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var showNoteDialog by remember { mutableStateOf(false) }

    val selectedDate by viewModel.selectedDate.collectAsState()
    val calendarNote by viewModel.calendarNote.collectAsState()

    val daysInMonth = getDaysInMonth(currentMonth)
    val monthYearText = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentMonth.time)

    LaunchedEffect(showNoteDialog) {
        if (showNoteDialog) {
            viewModel.selectDate(selectedDate)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            currentMonth.add(Calendar.MONTH, -1)
                            currentMonth = Calendar.getInstance().apply { time = currentMonth.time }
                        }) {
                            Icon(Icons.Default.ChevronLeft, "Previous")
                        }
                        Text(
                            text = monthYearText,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        IconButton(onClick = {
                            currentMonth.add(Calendar.MONTH, 1)
                            currentMonth = Calendar.getInstance().apply { time = currentMonth.time }
                        }) {
                            Icon(Icons.Default.ChevronRight, "Next")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                days.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(daysInMonth) { dayInfo ->
                    val dateStr = dayInfo?.let {
                        String.format(
                            "%04d-%02d-%02d",
                            currentMonth.get(Calendar.YEAR),
                            currentMonth.get(Calendar.MONTH) + 1,
                            it
                        )
                    }
                    val hasNote = calendarNotes.any { it.date == dateStr && it.content.isNotBlank() }
                    val isSelected = dateStr == selectedDate
                    val isToday = dateStr == AppViewModel.getTodayString()

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .background(
                                when {
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    isToday -> MaterialTheme.colorScheme.primaryContainer
                                    else -> Color.Transparent
                                },
                                shape = MaterialTheme.shapes.small
                            )
                            .clickable(enabled = dayInfo != null) {
                                dateStr?.let {
                                    viewModel.selectDate(it)
                                    showNoteDialog = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (dayInfo != null) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dayInfo.toString(),
                                    color = when {
                                        isSelected -> MaterialTheme.colorScheme.onPrimary
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                                if (hasNote) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(
                                                if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                                                shape = MaterialTheme.shapes.small
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showNoteDialog) {
        CalendarNoteDialog(
            date = selectedDate,
            note = calendarNote,
            onDismiss = { showNoteDialog = false },
            onSave = { content ->
                viewModel.saveCalendarNote(content)
                showNoteDialog = false
            }
        )
    }
}

@Composable
fun CalendarNoteDialog(
    date: String,
    note: CalendarNote?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var content by remember(note) { mutableStateOf(note?.content ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Note for $date") },
        text = {
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Your note...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 6
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(content) }) {
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

fun getDaysInMonth(calendar: Calendar): List<Int?> {
    val tempCal = calendar.clone() as Calendar
    tempCal.set(Calendar.DAY_OF_MONTH, 1)

    val firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK) - 1
    val maxDays = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val days = mutableListOf<Int?>()
    repeat(firstDayOfWeek) { days.add(null) }
    for (i in 1..maxDays) {
        days.add(i)
    }
    return days
}
