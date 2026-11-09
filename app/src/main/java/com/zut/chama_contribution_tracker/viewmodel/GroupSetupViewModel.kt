package com.zut.chama_contribution_tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zut.chama_contribution_tracker.data.ChamaRepository
import com.zut.chama_contribution_tracker.data.Member
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class GroupSetupUiState(
    val groupName: String = "",
    val memberName: String = "",
    val memberPhone: String = "",
    val addedMembers: List<Member> = emptyList(), // This holds the list
    val errorMessage: String? = null
)

class GroupSetupViewModel(
    private val repository: ChamaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupSetupUiState())
    val uiState: StateFlow<GroupSetupUiState> = _uiState

    init {
        // This listens to the database. Every time a member is added, this updates automatically!
        viewModelScope.launch {
            repository.getAllMembers().collectLatest { membersFromDb ->
                _uiState.value = _uiState.value.copy(addedMembers = membersFromDb)
            }
        }
    }

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

        // 1. Validate
        val error = validateMember(state.memberName, state.memberPhone)
        if (error != null) {
            _uiState.value = state.copy(errorMessage = error)
            return
        }

        // 2. Save to database
        viewModelScope.launch {
            repository.insertMember(
                Member(name = state.memberName.trim(), phone = state.memberPhone.trim())
            )

            // 3. Clear the form fields (the list updates automatically via the init block above)
            _uiState.value = _uiState.value.copy(
                memberName = "",
                memberPhone = "",
                errorMessage = null
            )
        }
    }

    private fun validateMember(name: String, phone: String): String? {
        if (name.isBlank()) return "Member name is required"
        if (phone.isBlank()) return "Phone number is required"
        if (!phone.matches(Regex("^(09|\\+260)[0-9]{8}$"))) {
            return "Enter a valid Zambian phone number"
        }
        return null
    }
}