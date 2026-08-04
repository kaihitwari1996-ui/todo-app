package com.example.todoapp.data

import com.example.todoapp.data.dao.*
import com.example.todoapp.data.entities.*

class Repository(
    private val taskDao: TaskDao,
    private val subTaskDao: SubTaskDao,
    private val noteDao: NoteDao,
    private val calendarNoteDao: CalendarNoteDao,
    private val tagDao: TagDao,
    private val habitDao: HabitDao
) {
    fun getAllTasks() = taskDao.getAllTasks()
    fun getActiveTasks() = taskDao.getActiveTasks()
    suspend fun insertTask(task: Task) = taskDao.insertTask(task)
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    fun getSubTasksForTask(id: Int) = subTaskDao.getSubTasksForTask(id)
    suspend fun insertSubTask(s: SubTask) = subTaskDao.insertSubTask(s)
    suspend fun updateSubTask(s: SubTask) = subTaskDao.updateSubTask(s)
    suspend fun deleteSubTask(s: SubTask) = subTaskDao.deleteSubTask(s)

    fun getAllNotes() = noteDao.getAllNotes()
    fun searchNotes(q: String) = noteDao.searchNotes(q)
    suspend fun insertNote(n: Note) = noteDao.insertNote(n)
    suspend fun updateNote(n: Note) = noteDao.updateNote(n)
    suspend fun deleteNote(n: Note) = noteDao.deleteNote(n)

    fun getNoteForDate(date: String) = calendarNoteDao.getNoteForDate(date)
    fun getAllCalendarNotes() = calendarNoteDao.getAllCalendarNotes()
    suspend fun saveCalendarNote(cn: CalendarNote) = calendarNoteDao.insertOrUpdate(cn)
    suspend fun deleteCalendarNote(cn: CalendarNote) = calendarNoteDao.delete(cn)

    fun getAllTags() = tagDao.getAllTags()
    suspend fun insertTag(t: Tag) = tagDao.insertTag(t)
    suspend fun deleteTag(t: Tag) = tagDao.deleteTag(t)

    fun getAllHabits() = habitDao.getAllHabits()
    suspend fun insertHabit(h: Habit) = habitDao.insertHabit(h)
    suspend fun updateHabit(h: Habit) = habitDao.updateHabit(h)
    suspend fun deleteHabit(h: Habit) = habitDao.deleteHabit(h)
    fun getEntriesForHabit(id: Int) = habitDao.getEntriesForHabit(id)
    suspend fun getEntryForDate(habitId: Int, date: String) = habitDao.getEntryForDate(habitId, date)
    suspend fun insertHabitEntry(e: HabitEntry) = habitDao.insertEntry(e)
    suspend fun deleteHabitEntry(e: HabitEntry) = habitDao.deleteEntry(e)
}
