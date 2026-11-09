package com.zut.chama_contribution_tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zut.chama_contribution_tracker.data.ChamaRepository
import com.zut.chama_contribution_tracker.data.Member
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class GroupSetupUiState(
    val groupName: String = "",
    val memberName: String = "",
    val memberPhone: String = "",
    val addedMembers: List<Member> = emptyList(),
    val errorMessage: String? = null
)

class GroupSetupViewModel(
    private val repository: ChamaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupSetupUiState())
    val uiState: StateFlow<GroupSetupUiState> = _uiState

    fun onGroupNameChange(value: String) {
        _uiState.value = _uiState.value.copy(groupName = value, errorMessage = null)
    }

    fun onMemberNameChange(value: String) {
        _uiState.value = _uiState.value.copy(memberName = value, errorMessage = null)
    }

    fun onMemberPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(memberPhone = value, errorMessage = null)
    }

    fun addMember() {
        val state = _uiState.value
        val error = validateMember(state.memberName, state.memberPhone, state.addedMembers)
        if (error != null) {
            _uiState.value = state.copy(errorMessage = error)
            return
        }
        viewModelScope.launch {
            repository.insertMember(
                Member(name = state.memberName.trim(), phone = state.memberPhone.trim())
            )
            _uiState.value = _uiState.value.copy(
                memberName = "",
                memberPhone = "",
                errorMessage = null
            )
        }
    }

    private fun validateMember(name: String, phone: String, existing: List<Member>): String? {
        if (name.isBlank()) return "Member name is required"
        if (phone.isBlank()) return "Phone number is required"
        if (!phone.matches(Regex("^(09|\\+260)[0-9]{8}$"))) {
            return "Enter a valid Zambian phone number"
        }
        if (existing.any { it.phone == phone.trim() }) {
            return "This member is already in the group"
        }
        return null
    }
}
