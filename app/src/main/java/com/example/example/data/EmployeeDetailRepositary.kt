package com.example.example.data

import android.app.Application
import androidx.lifecycle.LiveData

class EmployeeDetailRepositary(context:Application) {
    private val employeeDetailDao:EmployeeDetailDao = EmployeeDatabase.getDtabase(context).employeeDetailDao()

    fun getEmployee(id:Long):LiveData<Employee> = employeeDetailDao.getEmployee(id)

    suspend fun insertEmployee(employee: Employee):Long{
      return  employeeDetailDao.insertEmployee(employee)
    }

    suspend fun updateEmployee(employee: Employee){
        employeeDetailDao.updateEmployee(employee)
    }

    suspend fun deleteEmployee(employee: Employee){
        employeeDetailDao.deleteEmployee(employee)
    }
}