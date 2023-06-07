package com.example.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.example.data.Employee
import com.example.example.data.EmployeeShowRepositary

class EmployeeShowViewModel(application: Application):AndroidViewModel(application) {
    private val repo:EmployeeShowRepositary = EmployeeShowRepositary(application)

    private val _employeeId=MutableLiveData<Long>(0)

   val employee:LiveData<Employee> = Transformations.switchMap(_employeeId){
       repo.getEmployee(it)
   }

    fun setEmployeeId(id:Long){
        if(_employeeId.value!=id){
            _employeeId.value=id
        }
    }
}