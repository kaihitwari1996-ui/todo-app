package com.example.todoapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.todoapp.data.entities.SubTask

@Dao
interface SubTaskDao {
    @Query("SELECT * FROM subtasks WHERE taskId = :taskId")
    fun getSubTasks(taskId: Int): Flow<List<SubTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTask(subTask: SubTask)

    @Update
    suspend fun updateSubTask(subTask: SubTask)

    @Delete
    suspend fun deleteSubTask(subTask: SubTask)
}
