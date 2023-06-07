package com.example.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [Employee::class], version = 1, exportSchema = false)
abstract class EmployeeDatabase : RoomDatabase() {
    abstract fun employeeListDao():EmployeeListDao
    abstract fun employeeDetailDao():EmployeeDetailDao
    abstract fun employeeshowDao():EmployeeShowDao

    companion object{

        private var instance:EmployeeDatabase?=null

        fun getDtabase(context: Context):EmployeeDatabase = instance ?: synchronized(this){

            Room.databaseBuilder(context.applicationContext,EmployeeDatabase::class.java,"employee_database")
                .build().also { instance=it }
        }
    }
}
