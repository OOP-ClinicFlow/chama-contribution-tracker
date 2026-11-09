package com.zut.chama_contribution_tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zut.chama_contribution_tracker.data.ChamaRepository
import com.zut.chama_contribution_tracker.data.Member
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MemberListViewModel(repository: ChamaRepository) : ViewModel() {
    val members: StateFlow<List<Member>> = repository.getAllMembers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}