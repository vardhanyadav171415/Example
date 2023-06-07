package com.example.example.data

import android.app.Application
import androidx.lifecycle.LiveData

class EmployeeShowRepositary(context: Application) {
    private val employeeShowDao:EmployeeShowDao =EmployeeDatabase.getDtabase(context).employeeshowDao()

    fun getEmployee(id:Long):LiveData<Employee>{
        return employeeShowDao.getEmployee(id)
    }
}