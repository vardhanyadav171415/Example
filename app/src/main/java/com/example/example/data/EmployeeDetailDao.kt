package com.example.example.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface EmployeeDetailDao {

    @Query("SELECT * FROM employee WHERE `id` = :id")
    fun getEmployee(id: Long): LiveData<Employee>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEmployee(employee: Employee): Long

    @Update
    suspend fun updateEmployee(employee: Employee)

    @Delete
    suspend fun deleteEmployee(employee: Employee)
}