package com.example.todoapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.todoapp.data.entities.Tag

@Dao
interface TagDao {
    @Query("SELECT * FROM tags")
    fun getAllTags(): Flow<List<Tag>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: Tag)

    @Delete
    suspend fun deleteTag(tag: Tag)
}
