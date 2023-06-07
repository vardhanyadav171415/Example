package com.example.example.ui

import android.annotation.SuppressLint
import android.app.Application
import androidx.constraintlayout.widget.Constraints
import androidx.work.*
import java.util.concurrent.TimeUnit

class EmployeeApplication: Application() {
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate() {
        super.onCreate()

        val constraints=androidx.work.Constraints.Builder(
        ).setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresCharging(true)
            .build()

        val work=PeriodicWorkRequest.Builder(EmployeeOfTheDayWorker::class.java,24,TimeUnit.HOURS)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR,PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,TimeUnit.MICROSECONDS).build()

          WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork("EmployeeOfTheDay",
          ExistingPeriodicWorkPolicy.KEEP,work)
    }
}