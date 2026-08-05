package com.example.todoapp.data

import com.example.todoapp.data.entities.CalendarNote

@Dao
interface CalendarNoteDao {
    @Query("SELECT * FROM calendar_notes WHERE date = :date")
    fun getNoteForDate(date: String): Flow<CalendarNote?>

    @Query("SELECT * FROM calendar_notes WHERE content != ''")
    fun getAllCalendarNotes(): Flow<List<CalendarNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(calendarNote: CalendarNote)

    @Delete
    suspend fun delete(calendarNote: CalendarNote)
}
