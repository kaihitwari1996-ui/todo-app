package com.example.todoapp.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.todoapp.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: Repository

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _selectedDate = MutableStateFlow(getTodayString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _calendarNote = MutableStateFlow<CalendarNote?>(null)
    val calendarNote: StateFlow<CalendarNote?> = _calendarNote.asStateFlow()

    private val _calendarNotes = MutableStateFlow<List<CalendarNote>>(emptyList())
    val calendarNotes: StateFlow<List<CalendarNote>> = _calendarNotes.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = Repository(db.taskDao(), db.noteDao(), db.calendarNoteDao())

        viewModelScope.launch {
            repository.getAllTasks().collect { _tasks.value = it }
        }
        viewModelScope.launch {
            repository.getAllNotes().collect { _notes.value = it }
        }
        viewModelScope.launch {
            repository.getAllCalendarNotes().collect { _calendarNotes.value = it }
        }
    }

    fun addTask(title: String, category: String, expiryDate: Long? = null) {
        viewModelScope.launch {
            repository.insertTask(Task(title = title, category = category, expiryDate = expiryDate))
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            repository.insertNote(Note(title = title, content = content))
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun selectDate(date: String) {
        _selectedDate.value = date
        viewModelScope.launch {
            repository.getNoteForDate(date).collect { _calendarNote.value = it }
        }
    }

    fun saveCalendarNote(content: String) {
        viewModelScope.launch {
            repository.saveCalendarNote(
                CalendarNote(date = _selectedDate.value, content = content)
            )
        }
    }

    companion object {
        fun getTodayString(): String {
            return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        }

        fun formatDate(timestamp: Long): String {
            return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
        }

        fun parseDate(dateString: String): Date? {
            return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString)
        }
    }
}
