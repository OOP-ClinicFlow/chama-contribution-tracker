
package com.zut.chama_contribution_tracker.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.Calendar
import java.util.concurrent.TimeUnit

private const val CHANNEL_ID = "contribution_reminders"
private const val WORK_NAME = "chama_contribution_reminder"

private data class ReminderOccurrence(
    val time: Long,
    val isDayBefore: Boolean
)

object ContributionReminderScheduler {

    fun scheduleNext(
        context: Context,
        policy: ExistingWorkPolicy = ExistingWorkPolicy.REPLACE
    ) {
        val appContext = context.applicationContext
        val prefs = appContext.getSharedPreferences(
            "reminder_settings",
            Context.MODE_PRIVATE
        )

        if (!prefs.getBoolean("enabled", true)) {
            WorkManager.getInstance(appContext).cancelUniqueWork(WORK_NAME)
            return
        }

        val next = calculateNextReminder(prefs)

        if (next == null) {
            WorkManager.getInstance(appContext).cancelUniqueWork(WORK_NAME)
            return
        }

        val delay = (next.time - System.currentTimeMillis())
            .coerceAtLeast(0L)

        val input = Data.Builder()
            .putBoolean("is_day_before", next.isDayBefore)
            .build()

        val request = OneTimeWorkRequestBuilder<ContributionReminderWorker>()
            .setInputData(input)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(appContext).enqueueUniqueWork(
            WORK_NAME,
            policy,
            request
        )
    }

    private fun calculateNextReminder(
        prefs: android.content.SharedPreferences
    ): ReminderOccurrence? {

        val contributionDate = prefs.getLong("contribution_date", 0L)

        if (contributionDate <= 0L) return null

        val mode = prefs.getString("mode", "On the day")
            ?: "On the day"

        val hour = prefs.getInt("hour", 8)
        val minute = prefs.getInt("minute", 0)

        val contributionCalendar = Calendar.getInstance().apply {
            timeInMillis = contributionDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val now = System.currentTimeMillis()

        val reminderOffsets = when (mode) {
            "One day before" -> listOf(-1)
            "Both" -> listOf(-1, 0)
            else -> listOf(0)
        }

        var next: ReminderOccurrence? = null

        for (daysBefore in reminderOffsets) {

            val reminderCalendar =
                contributionCalendar.clone() as Calendar

            reminderCalendar.add(Calendar.DAY_OF_YEAR, daysBefore)

            reminderCalendar.set(Calendar.HOUR_OF_DAY, hour)
            reminderCalendar.set(Calendar.MINUTE, minute)
            reminderCalendar.set(Calendar.SECOND, 0)
            reminderCalendar.set(Calendar.MILLISECOND, 0)

            val reminderTime = reminderCalendar.timeInMillis

            if (
                reminderTime > now &&
                (next == null || reminderTime < next.time)
            ) {
                next = ReminderOccurrence(
                    time = reminderTime,
                    isDayBefore = daysBefore == -1
                )
            }
        }

        return next
    }
}

class ContributionReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val prefs = applicationContext.getSharedPreferences(
            "reminder_settings",
            Context.MODE_PRIVATE
        )

        if (!prefs.getBoolean("enabled", true)) {
            return Result.success()
        }

        val isDayBefore =
            inputData.getBoolean("is_day_before", false)

        val hasPermission = Build.VERSION.SDK_INT < 33 ||
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {

            val manager = applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Contribution Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                )

                manager.createNotificationChannel(channel)
            }

            val notificationMessage = if (isDayBefore) {
                "Your chama contribution is due tomorrow. Remember to prepare!"
            } else {
                "Today is your chama contribution day. Remember to record it!"
            }

            val notification = NotificationCompat.Builder(
                applicationContext,
                CHANNEL_ID
            )
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Chama Tracker")
                .setContentText(notificationMessage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(applicationContext)
                .notify(1001, notification)
        }

        // Schedule the other reminder if Both was selected.
        // If no future reminder remains, the scheduler cancels the work.
        ContributionReminderScheduler.scheduleNext(
            applicationContext,
            ExistingWorkPolicy.APPEND_OR_REPLACE
        )

        return Result.success()
    }
}
