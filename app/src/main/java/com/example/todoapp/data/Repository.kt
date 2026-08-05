package com.example.todoapp.data

import com.example.todoapp.data.entities.*
import kotlinx.coroutines.flow.Flow

class Repository(
    private val taskDao: TaskDao,
    private val noteDao: NoteDao,
    private val calendarNoteDao: CalendarNoteDao,
    private val tagDao: TagDao,
    private val subTaskDao: SubTaskDao,
    private val habitDao: HabitDao
) {
    // ─── Tasks ────────────────────────────────────────────────────
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()
    suspend fun insertTask(task: Task) = taskDao.insertTask(task)
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    // ─── SubTasks ─────────────────────────────────────────────────
    fun getSubTasks(taskId: Int): Flow<List<SubTask>> = subTaskDao.getSubTasks(taskId)
    suspend fun insertSubTask(s: SubTask) = subTaskDao.insertSubTask(s)
    suspend fun updateSubTask(s: SubTask) = subTaskDao.updateSubTask(s)
    suspend fun deleteSubTask(s: SubTask) = subTaskDao.deleteSubTask(s)

    // ─── Notes ────────────────────────────────────────────────────
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()
    suspend fun insertNote(n: Note) = noteDao.insertNote(n)
    suspend fun updateNote(n: Note) = noteDao.updateNote(n)
    suspend fun deleteNote(n: Note) = noteDao.deleteNote(n)

    // ─── Calendar Notes ───────────────────────────────────────────
    fun getNoteForDate(date: String): Flow<CalendarNote?> = calendarNoteDao.getNoteForDate(date)
    fun getAllCalendarNotes(): Flow<List<CalendarNote>> = calendarNoteDao.getAllCalendarNotes()
    suspend fun saveCalendarNote(cn: CalendarNote) = calendarNoteDao.insertOrUpdate(cn)
    suspend fun deleteCalendarNote(cn: CalendarNote) = calendarNoteDao.delete(cn)

    // ─── Tags ─────────────────────────────────────────────────────
    fun getAllTags(): Flow<List<Tag>> = tagDao.getAllTags()
    suspend fun insertTag(t: Tag) = tagDao.insertTag(t)
    suspend fun deleteTag(t: Tag) = tagDao.deleteTag(t)

    // ─── Habits ───────────────────────────────────────────────────
    fun getAllHabits(): Flow<List<Habit>> = habitDao.getAllHabits()
    suspend fun insertHabit(h: Habit) = habitDao.insertHabit(h)
    suspend fun deleteHabit(h: Habit) = habitDao.deleteHabit(h)
    fun getEntriesForHabit(habitId: Int): Flow<List<HabitEntry>> = habitDao.getEntriesForHabit(habitId)
    suspend fun insertHabitEntry(e: HabitEntry) = habitDao.insertHabitEntry(e)
    suspend fun deleteHabitEntry(e: HabitEntry) = habitDao.deleteHabitEntry(e)
}
