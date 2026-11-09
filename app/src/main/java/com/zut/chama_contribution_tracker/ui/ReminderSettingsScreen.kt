
package com.zut.chama_contribution_tracker.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReminderSettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    val prefs = remember {
        context.getSharedPreferences("reminder_settings", Context.MODE_PRIVATE)
    }

    var enabled by remember {
        mutableStateOf(prefs.getBoolean("enabled", true))
    }

    var contributionDate by remember {
        mutableStateOf(prefs.getLong("contribution_date", 0L))
    }

    var reminderMode by remember {
        mutableStateOf(prefs.getString("mode", "On the day") ?: "On the day")
    }

    var hour by remember {
        mutableStateOf(prefs.getInt("hour", 8).toString())
    }

    var minute by remember {
        mutableStateOf(prefs.getInt("minute", 0).toString())
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    val dateFormatter = remember {
        SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            ContributionReminderScheduler.scheduleNext(context)
            message = "Permission granted. Your reminder has been scheduled."
        } else {
            message = "Notification permission was denied. Enable notifications in your phone settings."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "Reminder Settings",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Enable reminders")
            Switch(
                checked = enabled,
                onCheckedChange = { enabled = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Next contribution date",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (contributionDate == 0L) {
                    "Select contribution date"
                } else {
                    dateFormatter.format(Date(contributionDate))
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "When should we remind you?",
            style = MaterialTheme.typography.titleMedium
        )

        listOf("One day before", "On the day", "Both").forEach { option ->
            Row {
                RadioButton(
                    selected = reminderMode == option,
                    onClick = { reminderMode = option }
                )
                Text(option, modifier = Modifier.padding(top = 12.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Preferred reminder time (24-hour clock)")

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = hour,
                onValueChange = { hour = it },
                label = { Text("Hour (0 to 23)") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            OutlinedTextField(
                value = minute,
                onValueChange = { minute = it },
                label = { Text("Minute (0 to 59)") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (message.isNotBlank()) {
            Text(message, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                val selectedHour = hour.toIntOrNull()
                val selectedMinute = minute.toIntOrNull()

                if (contributionDate == 0L) {
                    message = "Please select the contribution date."
                } else if (contributionDate <= System.currentTimeMillis()) {
                    message = "Please select a future contribution date."
                } else if (
                    selectedHour == null || selectedHour !in 0..23 ||
                    selectedMinute == null || selectedMinute !in 0..59
                ) {
                    message = "Enter a valid reminder time."
                } else {
                    prefs.edit()
                        .putBoolean("enabled", enabled)
                        .putLong("contribution_date", contributionDate)
                        .putString("mode", reminderMode)
                        .putInt("hour", selectedHour)
                        .putInt("minute", selectedMinute)
                        .apply()

                    if (!enabled) {
                        ContributionReminderScheduler.scheduleNext(context)
                        message = "Reminders have been turned off."
                    } else if (
                        Build.VERSION.SDK_INT >= 33 &&
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissionLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                        message = "Please allow notifications to receive reminders."
                    } else {
                        ContributionReminderScheduler.scheduleNext(context)
                        message = "Your reminder settings have been saved."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Reminder Settings")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                val hasPermission = Build.VERSION.SDK_INT < 33 ||
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED

                if (!hasPermission) {
                    permissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                    message = "Please allow notifications to send a demo notification."
                } else {
                    val manager = context.getSystemService(
                        Context.NOTIFICATION_SERVICE
                    ) as NotificationManager

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val channel = NotificationChannel(
                            "contribution_reminders",
                            "Contribution Reminders",
                            NotificationManager.IMPORTANCE_DEFAULT
                        )
                        manager.createNotificationChannel(channel)
                    }

                    val notification = NotificationCompat.Builder(
                        context,
                        "contribution_reminders"
                    )
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("Chama Tracker (Demo)")
                        .setContentText("Demo: Today is your chama contribution day. Remember to record it!")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .build()

                    NotificationManagerCompat.from(context)
                        .notify(1002, notification)
                    message = "Demo notification sent successfully."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Demo Notification")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Dashboard")
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = if (contributionDate > 0L) {
                contributionDate
            } else {
                System.currentTimeMillis()
            }
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        contributionDate =
                            datePickerState.selectedDateMillis ?: 0L
                        showDatePicker = false
                    }
                ) {
                    Text("Select")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
