package com.example.todoapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String = "",
    val color: Long = 0xFF4CAF50,
    val targetDaysPerWeek: Int = 7,
    val createdAt: Long = System.currentTimeMillis()
)
