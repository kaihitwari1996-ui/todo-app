package com.example.todoapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.todoapp.data.entities.CalendarNote
import com.example.todoapp.ui.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(vm: AppViewModel) {
    val calendarNotes by vm.calendarNotes.collectAsState()
    val selectedDate  by vm.selectedDate.collectAsState()
    val calendarNote  by vm.calendarNote.collectAsState()

    var currentYear  by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var currentMonth by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var showNoteDialog by remember { mutableStateOf(false) }

    val monthLabel = remember(currentYear, currentMonth) {
        val c = Calendar.getInstance().apply { set(currentYear, currentMonth, 1) }
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(c.time)
    }
    val dayGrid = remember(currentYear, currentMonth) { buildDayGrid(currentYear, currentMonth) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        IconButton(onClick = {
                            if (currentMonth == 0) { currentMonth = 11; currentYear-- }
                            else currentMonth--
                        }) { Icon(Icons.Default.ChevronLeft, "Prev") }
                        Text(monthLabel, style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        IconButton(onClick = {
                            if (currentMonth == 11) { currentMonth = 0; currentYear++ }
                            else currentMonth++
                        }) { Icon(Icons.Default.ChevronRight, "Next") }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { inner ->
        Column(Modifier.fillMaxSize().padding(inner)) {
            // Day-of-week headers
            Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                listOf("S", "M", "T", "W", "T", "F", "S").forEach { d ->
                    Text(d, modifier = Modifier.weight(1f), textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(4.dp))

            // Calendar grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                userScrollEnabled = false
            ) {
                items(dayGrid) { day ->
                    val dateStr = day?.let {
                        String.format("%04d-%02d-%02d", currentYear, currentMonth + 1, it)
                    }
                    val hasNote = calendarNotes.any { it.date == dateStr && it.content.isNotBlank() }
                    val isSelected = dateStr == selectedDate
                    val isToday   = dateStr == AppViewModel.todayString()

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(when {
                                isSelected -> MaterialTheme.colorScheme.primary
                                isToday    -> MaterialTheme.colorScheme.primaryContainer
                                else       -> Color.Transparent
                            })
                            .clickable(enabled = day != null) {
                                dateStr?.let {
                                    vm.selectDate(it)
                                    showNoteDialog = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (day != null) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    day.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = when {
                                        isSelected -> MaterialTheme.colorScheme.onPrimary
                                        isToday    -> MaterialTheme.colorScheme.primary
                                        else       -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                                if (hasNote) {
                                    Box(
                                        Modifier.size(4.dp).clip(CircleShape)
                                            .background(if (isSelected) MaterialTheme.colorScheme.onPrimary
                                                        else MaterialTheme.colorScheme.primary)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Divider(Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline.copy(0.3f))

            // Selected date panel
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Row(
                    Modifier.fillMaxWidth().clickable {
                        vm.selectDate(selectedDate)
                        showNoteDialog = true
                    }.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Notes, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(selectedDate, style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            calendarNote?.content?.let {
                                if (it.length > 80) it.take(80) + "…" else it
                            } ?: "Tap to add a note for this day",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (calendarNote != null) MaterialTheme.colorScheme.onSurface
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    if (showNoteDialog) {
        CalendarNoteDialog(
            date    = selectedDate,
            initial = calendarNote?.content ?: "",
            onDismiss = { showNoteDialog = false },
            onSave    = { content -> vm.saveCalendarNote(content); showNoteDialog = false }
        )
    }
}

@Composable
fun CalendarNoteDialog(date: String, initial: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by remember(initial) { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title  = { Text(date, style = MaterialTheme.typography.titleLarge) },
        text   = {
            OutlinedTextField(text, { text = it }, label = { Text("Note for this day") },
                modifier = Modifier.fillMaxWidth().height(160.dp), maxLines = 8)
        },
        confirmButton  = { Button({ onSave(text) }) { Text("Save") } },
        dismissButton  = { TextButton(onDismiss) { Text("Cancel") } }
    )
}

fun buildDayGrid(year: Int, month: Int): List<Int?> {
    val cal = Calendar.getInstance().apply { set(year, month, 1) }
    val firstDow = cal.get(Calendar.DAY_OF_WEEK) - 1
    val maxDay   = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    return buildList {
        repeat(firstDow) { add(null) }
        for (d in 1..maxDay) add(d)
    }
}
