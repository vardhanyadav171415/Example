package com.example.example.ui
import android.app.ActivityManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation

import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.example.R
import com.example.example.data.Employee
import com.example.example.data.Gender
import com.example.example.data.Role
import com.example.example.databinding.ActivityMainBinding
import com.example.example.databinding.FragmentEmployeeShowBinding
import com.google.android.material.appbar.CollapsingToolbarLayout

class EmployeeShowFragment : Fragment() {
    private lateinit var employeeRole:TextView
    private lateinit var employeeAge:TextView
    private lateinit var employeeGender:TextView
    private lateinit var employeeEducation:TextView
    private lateinit var employeeResponsibility:TextView
    private lateinit var employeeExperinece:TextView
    private lateinit var employeePhone:TextView
    private lateinit var employeeAddress:TextView
    private lateinit var employeeEmail:TextView
    private lateinit var employeePhoto:ImageView
    private lateinit var collapsingtoobar:CollapsingToolbarLayout
    private lateinit var toolBarShow:androidx.appcompat.widget.Toolbar
    private lateinit var viewmodel:EmployeeShowViewModel
    private lateinit var binding:FragmentEmployeeShowBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding= FragmentEmployeeShowBinding.inflate(layoutInflater)
        toolBarShow=binding.toolbarShow
        collapsingtoobar=binding.collapsingToolbar
        employeePhoto=binding.employeePhoto
        employeeRole=binding.employeeRole
        employeeAddress=binding.employeeAddress
        employeeExperinece=binding.employeeExperience
        employeeEducation=binding.employeeEducation
        employeeResponsibility=binding.employeeResponsibilities
        employeeGender=binding.employeeGender
        employeeEmail=binding.employeeEmail
        employeePhone=binding.employeePhone
        employeeAge=binding.employeeAge
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel=ViewModelProviders.of(this).get(EmployeeShowViewModel::class.java)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
       // val navHostFragment = requireActivity().findViewById<NavHostFragment>(R.id.nav_host_fragment)
        val navController= Navigation.findNavController(requireActivity(),R.id.nav_host_fragment)
        val appBarConfiguration=AppBarConfiguration(navController.graph)
        toolBarShow.setupWithNavController(navController,appBarConfiguration)
        employeePhoto.setImageResource(R.drawable.blankphoto)
        employeePhoto.tag=""
       val id=EmployeeShowFragmentArgs.fromBundle(requireArguments()).id
        viewmodel.setEmployeeId(id)
        viewmodel.employee.observe(viewLifecycleOwner, Observer {
            it?.let { setData(it) }
        })
    }

    private fun setData(employee: Employee) {
          with(employee.photo){
              if(isNotEmpty()){
                  employeePhoto.setImageURI(Uri.parse(this))
                  employeePhoto.tag=this
              }else{
                  employeePhoto.setImageResource(R.drawable.blankphoto)
                  employeePhoto.tag=""
              }
          }
        collapsingtoobar.title=employee.name
        run loop@{
            Role.values().forEach {
                if(it.ordinal==employee.role){
                    employeeRole.text=it.name
                    return@loop
                }
            }
        }
        employeeAge.text="${(employee.Age)} years"
        when(employee.gender){
            Gender.Male.ordinal->{
                employeeGender.text=Gender.Male.name
            }
            Gender.Female.ordinal->{
                employeeGender.text=Gender.Female.name
            }else->{
                employeeGender.text=Gender.Other.name
            }
        }
        employeeResponsibility.text=employee.responsibility
        employeeExperinece.text=employee.responsibility
        employeeEducation.text=employee.education
        if(employee.phone>0){
            employeePhone.text=employee.phone.toString()
        }
        employeeEmail.text=employee.email
        employeeAddress.text=employee.address
    }
}