package com.zut.chama_contribution_tracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zut.chama_contribution_tracker.data.ChamaRepository
import com.zut.chama_contribution_tracker.viewmodel.DashboardViewModel

@Composable
fun AppNavigation(repository: ChamaRepository) {
    val viewModel = remember { DashboardViewModel(repository) }
    val groupTotal by viewModel.groupTotal.collectAsState()
    val recentContributions by viewModel.recentContributions.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "Chama Contribution Tracker", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Group Total", style = MaterialTheme.typography.titleMedium)
                    Text(text = "ZMW $groupTotal", style = MaterialTheme.typography.headlineLarge)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Recent Contributions", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            if (recentContributions.isEmpty()) {
                Text(text = "No contributions yet.")
            } else {
                for (contribution in recentContributions) {
                    Text(text = "Amount: ZMW ${contribution.amount}")
                }
            }
        }
    }
}
