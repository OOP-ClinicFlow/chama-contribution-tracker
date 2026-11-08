package com.zut.chama_contribution_tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zut.chama_contribution_tracker.data.ChamaRepository
import com.zut.chama_contribution_tracker.data.Contribution
import com.zut.chama_contribution_tracker.data.Member
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AddContributionUiState(
    val selectedMemberId: Long? = null,
    val amount: String = "",
    val date: Long = System.currentTimeMillis(),
    val errorMessage: String? = null,
    val saved: Boolean = false
)

class AddContributionViewModel(
    private val repository: ChamaRepository
) : ViewModel() {

    val members = repository.getAllMembers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(AddContributionUiState())
    val uiState: StateFlow<AddContributionUiState> = _uiState

    fun onMemberSelected(id: Long) {
        _uiState.value = _uiState.value.copy(selectedMemberId = id, errorMessage = null)
    }

    fun onAmountChange(value: String) {
        _uiState.value = _uiState.value.copy(amount = value, errorMessage = null)
    }

    fun onDateChange(value: Long) {
        _uiState.value = _uiState.value.copy(date = value)
    }

    fun save() {
        val state = _uiState.value
        if (state.selectedMemberId == null) {
            _uiState.value = state.copy(errorMessage = "Please select a member")
            return
        }
        val amountValue = state.amount.toDoubleOrNull()
        if (amountValue == null || amountValue <= 0) {
            _uiState.value = state.copy(errorMessage = "Amount must be greater than zero")
            return
        }
        viewModelScope.launch {
            repository.insertContribution(
                Contribution(
                    memberId = state.selectedMemberId,
                    amount = amountValue,
                    date = state.date
                )
            )
            _uiState.value = AddContributionUiState(saved = true)
        }
    }
}
