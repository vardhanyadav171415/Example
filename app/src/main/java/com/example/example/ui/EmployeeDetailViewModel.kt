package com.example.example.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.example.data.Employee
import com.example.example.data.EmployeeDetailRepositary
import kotlinx.coroutines.launch

class EmployeeDetailViewModel(context:Application):AndroidViewModel(context) {

    val repo: EmployeeDetailRepositary = EmployeeDetailRepositary(context)

    private val _employeeId=MutableLiveData<Long>()

    val employeeId:LiveData<Long>
    get() = _employeeId

    val employee:LiveData<Employee> =Transformations.switchMap(_employeeId){
        repo.getEmployee(it)
    }

    fun setEmployee(id:Long){
        if(_employeeId.value!=id)
        {
            _employeeId.value=id
        }
    }


    fun saveEmployee(employee: Employee){
        viewModelScope.launch {
            if(_employeeId.value==0L){
                _employeeId.value=repo.insertEmployee(employee)
            }else{
                repo.updateEmployee(employee)
            }
        }
    }


    fun deleteEmployee(){
        viewModelScope.launch {
             employee.value?.let { repo.deleteEmployee(it) }
        }
    }
}