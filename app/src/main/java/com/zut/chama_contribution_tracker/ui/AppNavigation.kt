package com.zut.chama_contribution_tracker.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.zut.chama_contribution_tracker.data.ChamaRepository
import com.zut.chama_contribution_tracker.viewmodel.AddContributionViewModel
import com.zut.chama_contribution_tracker.viewmodel.DashboardViewModel
import com.zut.chama_contribution_tracker.viewmodel.GroupSetupViewModel

enum class Screen {
    Startup,
    Dashboard,
    AddContribution
}

@Composable
fun AppNavigation(repository: ChamaRepository) {
    var currentScreen by remember { mutableStateOf(Screen.Startup) }

    val dashboardViewModel = remember { DashboardViewModel(repository) }
    val groupSetupViewModel = remember { GroupSetupViewModel(repository) }
    val addContributionViewModel = remember { AddContributionViewModel(repository) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (currentScreen) {
            Screen.Startup -> {
                GroupSetupScreen(
                    viewModel = groupSetupViewModel,
                    onContinue = { currentScreen = Screen.Dashboard }
                )
            }
            Screen.Dashboard -> {
                BackHandler {
                    currentScreen = Screen.Startup
                }
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onNavigateToAddContribution = { currentScreen = Screen.AddContribution },
                    onNavigateToGroupSetup = { currentScreen = Screen.Startup }
                )
            }
            Screen.AddContribution -> {
                BackHandler {
                    currentScreen = Screen.Dashboard
                }
                AddContributionScreen(
                    viewModel = addContributionViewModel,
                    onSaved = { currentScreen = Screen.Dashboard },
                    onBack = { currentScreen = Screen.Dashboard }
                )
            }
        }
    }
}
