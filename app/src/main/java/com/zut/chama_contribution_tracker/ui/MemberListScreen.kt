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
import androidx.compose.ui.unit.dp
import com.zut.chama_contribution_tracker.viewmodel.MemberListViewModel

@Composable
fun MemberListScreen(
    viewModel: MemberListViewModel,
    onAddContributionClick: () -> Unit,
    onOpenDashboard: () -> Unit
) {
    val members by viewModel.members.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Group Members",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (members.isEmpty()) {
            // EMPTY STATE (Required by the brief)
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No members yet. Go back to Setup to add members.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(members) { member ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = member.name, style = MaterialTheme.typography.titleLarge)
                            Text(text = member.phone, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onAddContributionClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = members.isNotEmpty()
        ) {
            Text("Add a Contribution")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onOpenDashboard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go to Dashboard")
        }
    }
}