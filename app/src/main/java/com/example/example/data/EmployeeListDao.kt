package com.example.example.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EmployeeListDao {

    @Query("SELECT * FROM employee ORDER BY name")
    fun getEmployee():LiveData<List<Employee>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEmployees(employee: List<Employee>)

    @Query("SELECT * FROM employee ORDER BY name")
    suspend fun getEmployeeList():List<Employee>
}