package com.example.todoapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.todoapp.data.entities.Habit
import com.example.todoapp.data.entities.HabitEntry

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits")
    fun getAllHabits(): Flow<List<Habit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitEntry(entry: HabitEntry)
}
