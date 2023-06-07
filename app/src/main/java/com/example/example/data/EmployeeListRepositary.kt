package com.example.example.data

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Query

class EmployeeListRepositary(context:Application)
{
     private val employeeListDao:EmployeeListDao = EmployeeDatabase.getDtabase(context).employeeListDao()

    fun getEmployees():LiveData<List<Employee>> =
        employeeListDao.getEmployee()

    suspend fun insertEmployees(employee:List<Employee>)=employeeListDao.insertEmployees(employee)

    suspend fun getEmployeesList():List<Employee>  = employeeListDao.getEmployeeList()
}