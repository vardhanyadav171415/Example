package com.example.example.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R
import com.example.example.data.Employee
import com.example.example.data.Gender
import com.example.example.data.Role

class EmployeeAdapter(private val listener: (Boolean,Long) -> Unit) :
    ListAdapter<Employee, EmployeeAdapter.ViewHolder>(
        DiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var employeeeditbtn:ImageButton =itemView.findViewById<ImageButton>(R.id.editemployee)
//        private  var employeeResponsibility: TextView?=itemView.findViewById(R.id.employeeresponsibility)
         val employee_name:TextView=itemView.findViewById(R.id.emp_name)
        val employee_role:TextView=itemView.findViewById(R.id.employee_role)
        private val employeeRole: TextView = itemView.findViewById(R.id.employee_role)
        private val employeeAge: TextView = itemView.findViewById(R.id.employee_age)
        private val employeeGender: TextView = itemView.findViewById(R.id.employee_gender)
        private val employeImage:ImageView=itemView.findViewById(R.id.employee_photo)
        init {
            itemView.setOnClickListener {
                listener.invoke(true,getItem(adapterPosition).id)
            }
            employeeeditbtn.setOnClickListener {
                listener.invoke(false,getItem(adapterPosition).id)
            }
        }

        fun bind(employee: Employee) {
            with(employee) {
                employee_name.text=employee.name

                employee_role.text = Role.values()[employee.role].name
                employeeAge.text =this.Age.toString()
                employeeGender.text = Gender.values()[employee.gender].name
                with(photo){
                    if(isNotEmpty()){
                        employeImage.setImageURI(Uri.parse(this))
                    }
                    else{
                        employeImage.setImageResource(R.drawable.blankphoto)
                    }
                }
//                employeeResponsibility?.text=responsibility
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<Employee>() {
    override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean {
        return oldItem == newItem
    }
}