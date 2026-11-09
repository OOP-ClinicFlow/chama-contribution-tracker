package com.zut.chama_contribution_tracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zut.chama_contribution_tracker.data.ChamaRepository
import com.zut.chama_contribution_tracker.viewmodel.AddContributionViewModel
import com.zut.chama_contribution_tracker.viewmodel.DashboardViewModel
import com.zut.chama_contribution_tracker.viewmodel.GroupSetupViewModel

object Screen {
    const val GroupSetup = "group_setup"
    const val Dashboard = "dashboard"
    const val AddContribution = "add_contribution"
}

@Composable
fun AppNavigation(repository: ChamaRepository) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.GroupSetup
    ) {
        composable(Screen.GroupSetup) {
            val viewModel = remember(repository) { GroupSetupViewModel(repository) }
            GroupSetupScreen(
                viewModel = viewModel,
                onContinue = {
                    navController.navigate(Screen.Dashboard)
                }
            )
        }

        composable(Screen.Dashboard) {
            val viewModel = remember(repository) { DashboardViewModel(repository) }
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToAddContribution = {
                    navController.navigate(Screen.AddContribution)
                },
                onNavigateToGroupSetup = {
                    navController.navigate(Screen.GroupSetup)
                }
            )
        }

        composable(Screen.AddContribution) {
            val viewModel = remember(repository) { AddContributionViewModel(repository) }
            AddContributionScreen(
                viewModel = viewModel,
                onSaved = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
