package com.example.todoapp.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.Repository
import com.example.todoapp.data.entities.*
import com.example.todoapp.ui.theme.ThemeMode
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class TaskFilter { ALL, ACTIVE, TODAY, HIGH_PRIORITY }

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: Repository
    private val prefs = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    // ─── Theme ────────────────────────────────────────────────────────────────
    private val _theme = MutableStateFlow(
        ThemeMode.valueOf(prefs.getString("theme", ThemeMode.TICKTICK.name)!!)
    )
    val theme: StateFlow<ThemeMode> = _theme.asStateFlow()

    fun setTheme(mode: ThemeMode) {
        _theme.value = mode
        prefs.edit().putString("theme", mode.name).apply()
    }

    // ─── Tasks ────────────────────────────────────────────────────────────────
    private val _allTasks   = MutableStateFlow<List<Task>>(emptyList())
    private val _taskFilter = MutableStateFlow(TaskFilter.ALL)
    val taskFilter: StateFlow<TaskFilter> = _taskFilter.asStateFlow()

    private val _tagFilter  = MutableStateFlow<Int?>(null)
    val tagFilter: StateFlow<Int?> = _tagFilter.asStateFlow()

    val filteredTasks: StateFlow<List<Task>> = combine(_allTasks, _taskFilter, _tagFilter) { all, filter, tagId ->
        var list = when (filter) {
            TaskFilter.ALL          -> all
            TaskFilter.ACTIVE       -> all.filter { !it.isCompleted }
            TaskFilter.HIGH_PRIORITY -> all.filter { it.priority == Priority.HIGH }
            TaskFilter.TODAY        -> {
                val today = todayString()
                all.filter { t ->
                    t.expiryDate?.let { sdf.format(Date(it)) == today } ?: false
                }
            }
        }
        tagId?.let { id -> list = list.filter { id.toString() in it.tagIds.split(",") } }
        list
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun setTaskFilter(f: TaskFilter) { _taskFilter.value = f }
    fun setTagFilter(id: Int?)       { _tagFilter.value = id  }

    fun addTask(
        title: String, description: String = "", category: String = "General",
        priority: Priority = Priority.NONE, expiryDate: Long? = null,
        recurrence: RecurrenceType = RecurrenceType.NONE, tagIds: List<Int> = emptyList()
    ) = viewModelScope.launch {
        repo.insertTask(Task(title = title, description = description, category = category,
            priority = priority, expiryDate = expiryDate, recurrenceType = recurrence,
            tagIds = tagIds.joinToString(",")))
    }

    fun toggleTask(task: Task) = viewModelScope.launch {
        repo.updateTask(task.copy(isCompleted = !task.isCompleted,
            completedAt = if (!task.isCompleted) System.currentTimeMillis() else null))
    }

    fun deleteTask(task: Task) = viewModelScope.launch { repo.deleteTask(task) }
    fun updateTask(task: Task) = viewModelScope.launch { repo.updateTask(task) }

    // ─── SubTasks ─────────────────────────────────────────────────────────────
    private val _subTasks = MutableStateFlow<Map<Int, List<SubTask>>>(emptyMap())
    val subTasks: StateFlow<Map<Int, List<SubTask>>> = _subTasks.asStateFlow()

    fun loadSubTasks(taskId: Int) = viewModelScope.launch {
        repo.getSubTasksForTask(taskId).collect { list ->
            _subTasks.value = _subTasks.value + (taskId to list)
        }
    }

    fun addSubTask(taskId: Int, title: String) = viewModelScope.launch {
        repo.insertSubTask(SubTask(taskId = taskId, title = title))
    }

    fun toggleSubTask(st: SubTask) = viewModelScope.launch {
        repo.updateSubTask(st.copy(isCompleted = !st.isCompleted))
    }

    fun deleteSubTask(st: SubTask) = viewModelScope.launch { repo.deleteSubTask(st) }

    // ─── Notes ────────────────────────────────────────────────────────────────
    private val _noteSearch = MutableStateFlow("")
    val noteSearch: StateFlow<String> = _noteSearch.asStateFlow()

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    fun setNoteSearch(q: String) {
        _noteSearch.value = q
        viewModelScope.launch {
            if (q.isBlank()) repo.getAllNotes().take(1).collect { _notes.value = it }
            else repo.searchNotes(q).take(1).collect { _notes.value = it }
        }
    }

    fun addNote(title: String, content: String, tagIds: List<Int> = emptyList()) = viewModelScope.launch {
        repo.insertNote(Note(title = title, content = content, tagIds = tagIds.joinToString(",")))
    }

    fun saveNote(note: Note) = viewModelScope.launch {
        repo.updateNote(note.copy(updatedAt = System.currentTimeMillis()))
    }

    fun deleteNote(note: Note) = viewModelScope.launch { repo.deleteNote(note) }

    fun togglePin(note: Note) = viewModelScope.launch {
        repo.updateNote(note.copy(isPinned = !note.isPinned, updatedAt = System.currentTimeMillis()))
    }

    // ─── Calendar ─────────────────────────────────────────────────────────────
    private val _selectedDate = MutableStateFlow(todayString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _calendarNote = MutableStateFlow<CalendarNote?>(null)
    val calendarNote: StateFlow<CalendarNote?> = _calendarNote.asStateFlow()

    private val _calendarNotes = MutableStateFlow<List<CalendarNote>>(emptyList())
    val calendarNotes: StateFlow<List<CalendarNote>> = _calendarNotes.asStateFlow()

    fun selectDate(date: String) {
        _selectedDate.value = date
        viewModelScope.launch {
            repo.getNoteForDate(date).take(1).collect { _calendarNote.value = it }
        }
    }

    fun saveCalendarNote(content: String) = viewModelScope.launch {
        if (content.isBlank()) {
            _calendarNote.value?.let { repo.deleteCalendarNote(it) }
        } else {
            repo.saveCalendarNote(CalendarNote(date = _selectedDate.value, content = content))
        }
    }

    // ─── Tags ─────────────────────────────────────────────────────────────────
    private val _tags = MutableStateFlow<List<Tag>>(emptyList())
    val tags: StateFlow<List<Tag>> = _tags.asStateFlow()

    fun addTag(name: String, color: Long) = viewModelScope.launch {
        repo.insertTag(Tag(name = name, color = color))
    }

    fun deleteTag(tag: Tag) = viewModelScope.launch { repo.deleteTag(tag) }

    // ─── Habits ───────────────────────────────────────────────────────────────
    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    private val _habitEntries = MutableStateFlow<Map<Int, List<HabitEntry>>>(emptyMap())
    val habitEntries: StateFlow<Map<Int, List<HabitEntry>>> = _habitEntries.asStateFlow()

    fun addHabit(name: String, description: String, color: Long, targetDays: Int) = viewModelScope.launch {
        repo.insertHabit(Habit(name = name, description = description, color = color, targetDaysPerWeek = targetDays))
    }

    fun deleteHabit(habit: Habit) = viewModelScope.launch { repo.deleteHabit(habit) }

    fun toggleHabit(habitId: Int, date: String) = viewModelScope.launch {
        val existing = repo.getEntryForDate(habitId, date)
        if (existing != null) repo.deleteHabitEntry(existing)
        else repo.insertHabitEntry(HabitEntry(habitId = habitId, date = date))
    }

    fun getStreak(habitId: Int): Int {
        val entries = _habitEntries.value[habitId]?.map { it.date }?.toSet() ?: return 0
        var streak = 0
        val cal = Calendar.getInstance()
        while (sdf.format(cal.time) in entries) {
            streak++
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        return streak
    }

    fun isHabitDoneToday(habitId: Int): Boolean {
        val today = todayString()
        return _habitEntries.value[habitId]?.any { it.date == today } ?: false
    }

    fun getLastNDates(n: Int): List<String> {
        val cal = Calendar.getInstance()
        return (0 until n).map {
            sdf.format(cal.time).also { cal.add(Calendar.DAY_OF_YEAR, -1) }
        }.reversed()
    }

    // ─── Init ─────────────────────────────────────────────────────────────────
    init {
        val db = AppDatabase.getDatabase(application)
        repo = Repository(db.taskDao(), db.subTaskDao(), db.noteDao(),
            db.calendarNoteDao(), db.tagDao(), db.habitDao())

        viewModelScope.launch { repo.getAllTasks().collect { _allTasks.value = it } }
        viewModelScope.launch { repo.getAllNotes().collect { _notes.value = it } }
        viewModelScope.launch { repo.getAllCalendarNotes().collect { _calendarNotes.value = it } }
        viewModelScope.launch { repo.getAllTags().collect { _tags.value = it } }
        viewModelScope.launch {
            repo.getAllHabits().collect { habits ->
                _habits.value = habits
                habits.forEach { h ->
                    viewModelScope.launch {
                        repo.getEntriesForHabit(h.id).collect { entries ->
                            _habitEntries.value = _habitEntries.value + (h.id to entries)
                        }
                    }
                }
            }
        }
        selectDate(todayString())
    }

    companion object {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        fun todayString(): String = sdf.format(Date())
        fun formatDate(ts: Long) = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(ts))
        fun formatDateShort(ts: Long) = SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(ts))
    }
}
