package com.example.todoapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendar_notes")
data class CalendarNote(
    @PrimaryKey val date: String,
    val content: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
