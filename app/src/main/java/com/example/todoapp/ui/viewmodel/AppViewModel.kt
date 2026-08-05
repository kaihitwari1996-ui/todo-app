package com.example.todoapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
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

    private val repository: Repository

    // ─── Raw data ─────────────────────────────────────────────────
    private val _allTasks      = MutableStateFlow<List<Task>>(emptyList())
    private val _tags          = MutableStateFlow<List<Tag>>(emptyList())
    private val _subTasks      = MutableStateFlow<Map<Int, List<SubTask>>>(emptyMap())
    private val _notes         = MutableStateFlow<List<Note>>(emptyList())
    private val _habits        = MutableStateFlow<List<Habit>>(emptyList())
    private val _habitEntries  = MutableStateFlow<Map<Int, List<HabitEntry>>>(emptyMap())
    private val _calendarNote  = MutableStateFlow<CalendarNote?>(null)
    private val _calendarNotes = MutableStateFlow<List<CalendarNote>>(emptyList())
    private val _selectedDate  = MutableStateFlow(getTodayString())
    private val _taskFilter    = MutableStateFlow(TaskFilter.ALL)
    private val _tagFilter     = MutableStateFlow<Int?>(null)
    private val _theme         = MutableStateFlow(ThemeMode.TICKTICK)

    // ─── Public StateFlows ────────────────────────────────────────
    val tags:          StateFlow<List<Tag>>               = _tags.asStateFlow()
    val subTasks:      StateFlow<Map<Int, List<SubTask>>>  = _subTasks.asStateFlow()
    val notes:         StateFlow<List<Note>>              = _notes.asStateFlow()
    val habits:        StateFlow<List<Habit>>             = _habits.asStateFlow()
    val habitEntries:  StateFlow<Map<Int, List<HabitEntry>>> = _habitEntries.asStateFlow()
    val calendarNote:  StateFlow<CalendarNote?>           = _calendarNote.asStateFlow()
    val calendarNotes: StateFlow<List<CalendarNote>>      = _calendarNotes.asStateFlow()
    val selectedDate:  StateFlow<String>                  = _selectedDate.asStateFlow()
    val taskFilter:    StateFlow<TaskFilter>              = _taskFilter.asStateFlow()
    val tagFilter:     StateFlow<Int?>                    = _tagFilter.asStateFlow()
    val theme:         StateFlow<ThemeMode>               = _theme.asStateFlow()

    // ─── Filtered + sorted tasks ──────────────────────────────────
    val filteredTasks: StateFlow<List<Task>> = combine(
        _allTasks, _taskFilter, _tagFilter
    ) { tasks, filter, tagId ->
        val today = getTodayString()
        tasks.filter { task ->
            when (filter) {
                TaskFilter.ALL           -> true
                TaskFilter.ACTIVE        -> !task.isCompleted
                TaskFilter.TODAY         -> task.expiryDate?.let {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it)) == today
                } ?: false
                TaskFilter.HIGH_PRIORITY -> task.priority == Priority.HIGH
            }
        }.filter { task ->
            tagId == null || task.tagIds.split(",").contains(tagId.toString())
        }.sortedWith(
            compareBy<Task> { it.isCompleted }
                .thenByDescending {
                    when (it.priority) {
                        Priority.HIGH   -> 3
                        Priority.MEDIUM -> 2
                        Priority.LOW    -> 1
                        Priority.NONE   -> 0
                    }
                }
                .thenByDescending { it.createdAt }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ─── Init ─────────────────────────────────────────────────────
    init {
        val db = AppDatabase.getDatabase(application)
        repository = Repository(
            taskDao         = db.taskDao(),
            noteDao         = db.noteDao(),
            calendarNoteDao = db.calendarNoteDao(),
            tagDao          = db.tagDao(),
            subTaskDao      = db.subTaskDao(),
            habitDao        = db.habitDao()
        )
        viewModelScope.launch { repository.getAllTasks().collect { _allTasks.value = it } }
        viewModelScope.launch { repository.getAllTags().collect { _tags.value = it } }
        viewModelScope.launch { repository.getAllNotes().collect { _notes.value = it } }
        viewModelScope.launch { repository.getAllCalendarNotes().collect { _calendarNotes.value = it } }
        viewModelScope.launch {
            repository.getAllHabits().collect { habitList ->
                _habits.value = habitList
                habitList.forEach { habit ->
                    launch {
                        repository.getEntriesForHabit(habit.id).collect { entries ->
                            _habitEntries.value = _habitEntries.value + (habit.id to entries)
                        }
                    }
                }
            }
        }
    }

    // ─── Theme ────────────────────────────────────────────────────
    fun setTheme(mode: ThemeMode) { _theme.value = mode }

    // ─── Filter controls ──────────────────────────────────────────
    fun setTaskFilter(filter: TaskFilter) { _taskFilter.value = filter }
    fun setTagFilter(tagId: Int?)         { _tagFilter.value  = tagId  }

    // ─── Task ─────────────────────────────────────────────────────
    fun addTask(
        title: String,
        description: String = "",
        category: String = "General",
        priority: Priority = Priority.NONE,
        expiryDate: Long? = null,
        recurrenceType: RecurrenceType = RecurrenceType.NONE,
        tagIds: List<Int> = emptyList()
    ) {
        viewModelScope.launch {
            repository.insertTask(
                Task(title = title, description = description, category = category,
                    priority = priority, expiryDate = expiryDate,
                    recurrenceType = recurrenceType, tagIds = tagIds.joinToString(","))
            )
        }
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch { repository.updateTask(task.copy(isCompleted = !task.isCompleted)) }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch { repository.deleteTask(task) }
    }

    // ─── SubTask ──────────────────────────────────────────────────
    fun loadSubTasks(taskId: Int) {
        viewModelScope.launch {
            repository.getSubTasks(taskId).collect { list ->
                _subTasks.value = _subTasks.value + (taskId to list)
            }
        }
    }

    fun addSubTask(taskId: Int, title: String) {
        viewModelScope.launch { repository.insertSubTask(SubTask(taskId = taskId, title = title)) }
    }

    fun toggleSubTask(subTask: SubTask) {
        viewModelScope.launch { repository.updateSubTask(subTask.copy(isCompleted = !subTask.isCompleted)) }
    }

    fun deleteSubTask(subTask: SubTask) {
        viewModelScope.launch { repository.deleteSubTask(subTask) }
    }

    // ─── Tags ─────────────────────────────────────────────────────
    fun addTag(name: String, color: Long) {
        viewModelScope.launch { repository.insertTag(Tag(name = name, color = color)) }
    }

    fun deleteTag(tag: Tag) {
        viewModelScope.launch { repository.deleteTag(tag) }
    }

    // ─── Notes ────────────────────────────────────────────────────
    fun addNote(title: String, content: String, tagIds: Set<Int> = emptySet()) {
        viewModelScope.launch {
            repository.insertNote(Note(title = title, content = content,
                tagIds = tagIds.joinToString(",")))
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch { repository.updateNote(note.copy(updatedAt = System.currentTimeMillis())) }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch { repository.deleteNote(note) }
    }

    // ─── Calendar ─────────────────────────────────────────────────
    fun selectDate(date: String) {
        _selectedDate.value = date
        viewModelScope.launch {
            repository.getNoteForDate(date).collect { _calendarNote.value = it }
        }
    }

    fun saveCalendarNote(content: String) {
        viewModelScope.launch {
            repository.saveCalendarNote(CalendarNote(date = _selectedDate.value, content = content))
        }
    }

    // ─── Habits ───────────────────────────────────────────────────
    fun addHabit(name: String, description: String, color: Long, targetDaysPerWeek: Int) {
        viewModelScope.launch {
            repository.insertHabit(Habit(name = name, description = description,
                color = color, targetDaysPerWeek = targetDaysPerWeek))
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch { repository.deleteHabit(habit) }
    }

    fun toggleHabit(habitId: Int, date: String) {
        viewModelScope.launch {
            val entries = _habitEntries.value[habitId] ?: emptyList()
            val existing = entries.find { it.date == date }
            if (existing != null) repository.deleteHabitEntry(existing)
            else repository.insertHabitEntry(HabitEntry(habitId = habitId, date = date))
        }
    }

    fun getStreak(habitId: Int): Int {
        val sdf     = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal     = Calendar.getInstance()
        val doneSet = (_habitEntries.value[habitId] ?: emptyList()).map { it.date }.toSet()
        var streak  = 0
        repeat(365) { i ->
            cal.time = Date()
            cal.add(Calendar.DAY_OF_YEAR, -i)
            if (sdf.format(cal.time) in doneSet) streak++ else return streak
        }
        return streak
    }

    fun isHabitDoneToday(habitId: Int): Boolean {
        val today = getTodayString()
        return (_habitEntries.value[habitId] ?: emptyList()).any { it.date == today }
    }

    fun getLastNDates(n: Int): List<String> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        return (n - 1 downTo 0).map { i ->
            cal.time = Date()
            cal.add(Calendar.DAY_OF_YEAR, -i)
            sdf.format(cal.time)
        }
    }

    // ─── Companion ────────────────────────────────────────────────
    companion object {
        fun getTodayString(): String =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        fun todayString(): String = getTodayString()

        fun formatDate(timestamp: Long): String =
            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))

        fun formatDateShort(timestamp: Long): String =
            SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))

        fun parseDate(dateString: String): Date? =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString)
    }
}
