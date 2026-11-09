package com.zut.chama_contribution_tracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zut.chama_contribution_tracker.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onBackToMembers: () -> Unit,
    onOpenReminderSettings: () -> Unit
) {
    val total by viewModel.groupTotal.collectAsState()
    val recent by viewModel.recentContributions.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Chama Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Group Total Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Group Total", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "ZMW $total",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Recent Contributions", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        if (recent.isEmpty()) {
            // EMPTY STATE (Required by the brief)
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No contributions yet.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recent) { contribution ->
                    val dateString = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        .format(Date(contribution.date))

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Member ID: ${contribution.memberId}")
                            Text("ZMW ${contribution.amount} ($dateString)")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
// Scheduled reminder settings
        OutlinedButton(
            onClick = onOpenReminderSettings,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Scheduled Reminder Settings")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onBackToMembers,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Members")
        }
    }
}