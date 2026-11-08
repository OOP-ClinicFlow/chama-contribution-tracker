package com.zut.chama_contribution_tracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GroupSetupScreen(
    viewModel: GroupSetupViewModel = viewModel(),
    onContinue: () -> Unit = {}
) {
    var memberName by remember { mutableStateOf("") }
    var memberPhone by remember { mutableStateOf("") }

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

        // Group Name
        OutlinedTextField(
            value = viewModel.groupName,
            onValueChange = { viewModel.onGroupNameChange(it) },
            label = { Text("Group Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Member Name
        OutlinedTextField(
            value = memberName,
            onValueChange = { memberName = it },
            label = { Text("Member Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Member Phone
        OutlinedTextField(
            value = memberPhone,
            onValueChange = { memberPhone = it },
            label = { Text("Member Phone") },
            placeholder = { Text("e.g. 0977123456 or +260977123456") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Exact error messages from spec
        viewModel.errorMessage?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                viewModel.addMember(memberName, memberPhone)
                // Clear fields only if no error
                if (viewModel.errorMessage == null) {
                    memberName = ""
                    memberPhone = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Member")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Members (${viewModel.members.size})",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Real-time list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.members) { member ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = member.name, style = MaterialTheme.typography.bodyLarge)
                        Text(text = member.phone, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Continue button - only enabled after at least 1 member
        Button(
            onClick = { onContinue() },
            enabled = viewModel.members.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue")
        }
    }
}
