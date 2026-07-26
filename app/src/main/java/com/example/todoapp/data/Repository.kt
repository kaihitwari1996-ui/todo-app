package com.example.todoapp.data

import kotlinx.coroutines.flow.Flow

class Repository(
    private val taskDao: TaskDao,
    private val noteDao: NoteDao,
    private val calendarNoteDao: CalendarNoteDao
) {
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()
    fun getTasksByCategory(category: String): Flow<List<Task>> = taskDao.getTasksByCategory(category)
    suspend fun insertTask(task: Task) = taskDao.insertTask(task)
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()
    suspend fun insertNote(note: Note) = noteDao.insertNote(note)
    suspend fun updateNote(note: Note) = noteDao.updateNote(note)
    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)

    fun getNoteForDate(date: String): Flow<CalendarNote?> = calendarNoteDao.getNoteForDate(date)
    fun getAllCalendarNotes(): Flow<List<CalendarNote>> = calendarNoteDao.getAllCalendarNotes()
    suspend fun saveCalendarNote(calendarNote: CalendarNote) = calendarNoteDao.insertOrUpdate(calendarNote)
}
