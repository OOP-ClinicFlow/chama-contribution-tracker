package com.zut.chama_contribution_tracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zut.chama_contribution_tracker.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToAddContribution: () -> Unit = {},
    onNavigateToGroupSetup: () -> Unit = {}
) {
    val groupTotal by viewModel.groupTotal.collectAsState()
    val recentContributions by viewModel.recentContributions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    TextButton(onClick = onNavigateToGroupSetup) {
                        Text("Group Setup")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddContribution
            ) {
                Text("+ Add Contribution")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Chama Contribution Tracker",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Group Total", style = MaterialTheme.typography.titleMedium)
                    Text(text = "ZMW $groupTotal", style = MaterialTheme.typography.headlineLarge)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ContributionReminderButton()

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Recent Contributions", style = MaterialTheme.typography.titleLarge)
                Button(onClick = onNavigateToAddContribution) {
                    Text("+ Add")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (recentContributions.isEmpty()) {
                Text(
                    text = "No contributions yet.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recentContributions) { contribution ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Contribution",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "ZMW ${contribution.amount}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
