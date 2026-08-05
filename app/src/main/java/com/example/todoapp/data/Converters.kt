package com.example.todoapp.data

import androidx.room.TypeConverter
import com.example.todoapp.data.entities.Priority
import com.example.todoapp.data.entities.RecurrenceType

class Converters {

    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): Priority =
        Priority.values().firstOrNull { it.name == value } ?: Priority.NONE

    @TypeConverter
    fun fromRecurrenceType(type: RecurrenceType): String = type.name

    @TypeConverter
    fun toRecurrenceType(value: String): RecurrenceType =
        RecurrenceType.values().firstOrNull { it.name == value } ?: RecurrenceType.NONE
}
