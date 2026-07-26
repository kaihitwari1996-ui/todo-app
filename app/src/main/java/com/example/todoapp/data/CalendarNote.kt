package com.example.todoapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendar_notes")
data class CalendarNote(
    @PrimaryKey
    val date: String, // "yyyy-MM-dd"
    val content: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
