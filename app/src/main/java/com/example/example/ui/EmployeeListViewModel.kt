package com.example.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.example.data.Employee
import com.example.example.data.EmployeeListRepositary

class EmployeeListViewModel(application: Application):AndroidViewModel(application) {
    val repo:EmployeeListRepositary= EmployeeListRepositary(application)

    val employees:LiveData<List<Employee>> =
        repo.getEmployees()

    suspend fun insertEmployees(employee:List<Employee>) = repo.insertEmployees(employee)

    suspend fun getEmployeeList():List<Employee> =repo.getEmployeesList()
 }
