package com.example.example.ui

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.audiofx.EnvironmentalReverb
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.example.R

const val ACTION_ALARM_START="ACTION_ALARM_START"
   const val ACTION_ALARM_STOP="ACTION_ALARM_STOP"
class AlarmService:Service() {
    private val CHANNEL_ID="1000"
    private var player:MediaPlayer?=null
    override fun onBind(p0: Intent?): IBinder? {
         return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            ACTION_ALARM_START->{
                startAlarm()
            }
            ACTION_ALARM_STOP->{
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun startAlarm() {
         showNotification()
        playAlarm()
    }

    private fun playAlarm() {
         if(player?.isPlaying==true){
             player?.stop()
         }
        player = MediaPlayer.create(this,Settings.System.DEFAULT_ALARM_ALERT_URI)
        player?.isLooping=true
        player?.start()
    }

    private fun showNotification() {
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){
            createChannel()
        }
        val notificationIntent=Intent(this,AlarmService::class.java).apply {
            action= ACTION_ALARM_START

        }
        val pendingIntent=PendingIntent.getService(this,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        val notification=NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle("Employee Alarm")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(pendingIntent)
            .setContentText("Employee Example")
            .build()
        startForeground(1,notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {

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