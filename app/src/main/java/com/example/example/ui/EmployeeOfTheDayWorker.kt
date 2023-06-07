package com.example.example.ui

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build

import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.example.R


class EmployeeOfTheDayWorker(ctx: Context, params:WorkerParameters) : Worker(ctx,params) {
    private val CHANNEL_ID = "1000"
    override fun doWork(): Result {
        if (runAttemptCount > 5) {
            return Result.failure()
        }
        try {
            val name = getEmployeOfTheDay()
            showNotification(name)
        } catch (e: java.lang.Exception) {
            return Result.retry()
        }
      return Result.success()
    }
    private fun getEmployeOfTheDay():String{
        return "Sara Jones"
    }
    private fun showNotification(name:String){
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){
            createChannel()
        }
        val intent=Intent(applicationContext,MainActivity::class.java)
        val uniqueInt =(System.currentTimeMillis() and 0xfffffff).toInt()
        val pendingIntent=PendingIntent.getActivity(applicationContext,uniqueInt,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        val builder=NotificationCompat.Builder(applicationContext,CHANNEL_ID)
            .setContentTitle("Employee of the day")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentText("YAY! ${name} is choosen one")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(applicationContext)){
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(100,builder.build())
        }
    }
    @androidx.annotation.RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(){
        val notificationManager=applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val name:CharSequence="Employee Example"
        val description="Service example notification channel"
        val importance=NotificationManager.IMPORTANCE_DEFAULT
        val channel=NotificationChannel(CHANNEL_ID,name,importance)
        channel.description=description
        channel.setShowBadge(false)
        channel.lockscreenVisibility=Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(channel)
    }
}