package com.zut.chama_contribution_tracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zut.chama_contribution_tracker.viewmodel.GroupSetupViewModel

@Composable
fun GroupSetupScreen(
    viewModel: GroupSetupViewModel,
    onContinue: () -> Unit = {}
) {
    // This is how you read StateFlow in Compose
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Group Setup",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Group Name - from your ViewModel
        OutlinedTextField(
            value = uiState.groupName,
            onValueChange = { viewModel.onGroupNameChange(it) },
            label = { Text("Group Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Member Name
        OutlinedTextField(
            value = uiState.memberName,
            onValueChange = { viewModel.onMemberNameChange(it) },
            label = { Text("Member Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Member Phone
        OutlinedTextField(
            value = uiState.memberPhone,
            onValueChange = { viewModel.onMemberPhoneChange(it) },
            label = { Text("Member Phone") },
            placeholder = { Text("e.g. 0977123456 or +260977123456") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Error from ViewModel
        uiState.errorMessage?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = { viewModel.addMember() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Member")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Members (${uiState.addedMembers.size})",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Real-time list from ViewModel
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.addedMembers) { member ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = member.name, style = MaterialTheme.typography.bodyLarge)
                        Text(text = member.phone, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Continue - only enabled after at least 1 member
        Button(
            onClick = { onContinue() },
            enabled = uiState.addedMembers.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue")
        }
    }
}