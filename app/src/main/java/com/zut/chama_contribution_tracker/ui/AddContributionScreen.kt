package com.zut.chama_contribution_tracker.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zut.chama_contribution_tracker.viewmodel.AddContributionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContributionScreen(
    viewModel: AddContributionViewModel,
    onSaved: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val members by viewModel.members.collectAsState()
    val context = LocalContext.current

    var expanded by remember { mutableStateOf(false) }

    val selectedMember = members.find {
        it.id == state.selectedMemberId
    }

    LaunchedEffect(state.saved) {
        if (state.saved) onSaved()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Add Contribution",
            style = MaterialTheme.typography.headlineMedium
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedMember?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Member") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                members.forEach { member ->
                    DropdownMenuItem(
                        text = { Text(member.name) },
                        onClick = {
                            viewModel.onMemberSelected(member.id)
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = state.amount,
            onValueChange = viewModel::onAmountChange,
            label = { Text("Amount (ZMW)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        val dateText = SimpleDateFormat(
            "dd MMMM yyyy",
            Locale.getDefault()
        ).format(Date(state.date))

        OutlinedButton(
            onClick = {
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = state.date
                }

                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        val selectedDate = Calendar.getInstance().apply {
                            set(year, month, day, 12, 0, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        viewModel.onDateChange(
                            selectedDate.timeInMillis
                        )
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Date: $dateText")
        }

        state.errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = viewModel::save,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Contribution")
        }
    }
}