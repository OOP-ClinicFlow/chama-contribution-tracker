package com.zut.chama_contribution_tracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zut.chama_contribution_tracker.data.ChamaRepository
import com.zut.chama_contribution_tracker.viewmodel.DashboardViewModel
import com.zut.chama_contribution_tracker.viewmodel.AddContributionViewModel

@Composable
fun AppNavigation(repository: ChamaRepository) {

    var showAddContribution by remember { mutableStateOf(false) }
    var showReminderSettings by remember { mutableStateOf(false) }

    val addContributionViewModel = remember {
        AddContributionViewModel(repository)
    }

    val viewModel = remember {
        DashboardViewModel(repository)
    }

    val groupTotal by viewModel.groupTotal.collectAsState()
    val recentContributions by viewModel.recentContributions.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when {
            showAddContribution -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    TextButton(onClick = { showAddContribution = false }) {
                        Text("Back to Dashboard")
                    }

                    AddContributionScreen(
                        viewModel = addContributionViewModel,
                        onSaved = { showAddContribution = false }
                    )
                }
            }

            showReminderSettings -> {
                ReminderSettingsScreen(
                    onBack = { showReminderSettings = false }
                )
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Chama Contribution Tracker",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Group Total",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "ZMW $groupTotal",
                                style = MaterialTheme.typography.headlineLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showAddContribution = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Contribution")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showReminderSettings = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Reminder Settings")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Recent Contributions",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (recentContributions.isEmpty()) {
                        Text("No contributions yet.")
                    } else {
                        for (contribution in recentContributions) {
                            Text("Amount: ZMW ${contribution.amount}")
                        }
                    }
                }
            }
        }
    }
}
