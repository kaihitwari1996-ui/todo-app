package com.example.todoapp.data

import android.content.Context
import androidx.room.*
import com.example.todoapp.data.entities.Task
import com.example.todoapp.data.entities.SubTask
import com.example.todoapp.data.entities.Note
import com.example.todoapp.data.entities.CalendarNote
import com.example.todoapp.data.entities.Tag
import com.example.todoapp.data.entities.Habit
import com.example.todoapp.data.entities.HabitEntry

@Database(
    entities = [
        Task::class,
        SubTask::class,
        Note::class,
        CalendarNote::class,
        Tag::class,
        Habit::class,
        HabitEntry::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun subTaskDao(): SubTaskDao
    abstract fun noteDao(): NoteDao
    abstract fun calendarNoteDao(): CalendarNoteDao
    abstract fun tagDao(): TagDao
    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "todo_db"
                ).build().also { INSTANCE = it }
            }
    }
}
