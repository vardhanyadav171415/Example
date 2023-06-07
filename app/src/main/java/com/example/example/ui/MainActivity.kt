package com.example.example.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.example.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun createFile(context: Context, folder:String, ext:String): File {

     val timeStamp:String=SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date( ))
    val filesDir:File?=context.getExternalFilesDir(folder)
    val newFile=File(filesDir,"$timeStamp.$ext")
    newFile.createNewFile()
    return newFile
}

fun Activity.showToast(msg: String, duration: Int = Toast.LENGTH_SHORT){
    Toast.makeText(this, msg, duration).show()
}


class MainActivity : AppCompatActivity() {
//    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
//    private lateinit var appBarConfiguration:AppBarConfiguration
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout=findViewById(R.id.drawer_layout)
        runWorker()
       // toolbar=findViewById(R.id.toolbar)
//        supportActionBar!!.setDisplayShowHomeEnabled(true)
//        supportActionBar!!.setIcon(R.mipmap.ic_launcher_round)
//         setSupportActionBar(toolbar)
//        supportActionBar?.title=""
//        val navController=findNavController(R.id.nav_host_fragment)
//        appBarConfiguration= AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController,appBarConfiguration)

    }

    private fun runWorker() {
         val work=OneTimeWorkRequest.Builder(EmployeeOfTheDayWorker::class.java).build()
        WorkManager.getInstance(this).enqueue(work)
    }

    //    override fun onSupportNavigateUp(): Boolean {
//        val navController=findNavController(R.id.nav_host_fragment)
//        return navController.navigateUp()
//    }
    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}