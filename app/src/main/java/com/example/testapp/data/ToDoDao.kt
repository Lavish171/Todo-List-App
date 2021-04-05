package com.example.testapp.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.testapp.data.models.ToDoData

@Dao
interface ToDoDao
{
    @Query("SELECT * FROM todo_table ORDER BY id ASC")
   fun getAllData():LiveData<List<ToDoData>>

   @Insert(onConflict = OnConflictStrategy.IGNORE)
   suspend fun insertData(toDoData: ToDoData)

   @Update
   suspend fun updateData(toDoData: ToDoData)

   @Delete
   suspend fun  deleteItem(toDoData: ToDoData)

   @Query("DELETE FROM todo_table")
   suspend fun  deleteAll()

   @Query("SELECT * FROM todo_table where title LIKE :searchQuery")
    fun searchDatabase(searchQuery: String):LiveData<List<ToDoData>>

    @Query("SELECT * FROM todo_table ORDER BY CASE WHEN priority LIKE 'H%' THEN 1 WHEN PRIORITY LIKE 'M%' THEN 2 WHEN PRIORITY LIKE 'L%' THEN 3 END")
    fun sortByHighPriority():LiveData<List<ToDoData>>

   @Query("SELECT * FROM todo_table ORDER BY CASE WHEN priority LIKE 'H%' THEN 3 WHEN PRIORITY LIKE 'M%' THEN 2 WHEN PRIORITY LIKE 'L%' THEN 1 END")
   fun sortByLowPriority():LiveData<List<ToDoData>>
}