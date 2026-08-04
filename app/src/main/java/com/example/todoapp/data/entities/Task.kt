package com.example.todoapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Priority { NONE, LOW, MEDIUM, HIGH }
enum class RecurrenceType { NONE, DAILY, WEEKLY, MONTHLY, YEARLY }

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val category: String = "General",
    val priority: Priority = Priority.NONE,
    val isCompleted: Boolean = false,
    val expiryDate: Long? = null,
    val recurrenceType: RecurrenceType = RecurrenceType.NONE,
    val tagIds: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
