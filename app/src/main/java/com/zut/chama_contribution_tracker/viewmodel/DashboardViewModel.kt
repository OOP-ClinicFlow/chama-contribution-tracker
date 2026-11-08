package com.zut.chama_contribution_tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zut.chama_contribution_tracker.data.ChamaRepository
import com.zut.chama_contribution_tracker.data.Contribution
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: ChamaRepository
) : ViewModel() {

    private val _groupTotal = MutableStateFlow(0.0)
    val groupTotal: StateFlow<Double> = _groupTotal.asStateFlow()

    private val _recentContributions = MutableStateFlow<List<Contribution>>(emptyList())
    val recentContributions: StateFlow<List<Contribution>> = _recentContributions.asStateFlow()

    init {
        viewModelScope.launch {
            _groupTotal.value = repository.getGroupTotal()
        }
        viewModelScope.launch {
            repository.getAllContributions().collect { list ->
                _recentContributions.value = list.take(5)
            }
        }
    }
}
