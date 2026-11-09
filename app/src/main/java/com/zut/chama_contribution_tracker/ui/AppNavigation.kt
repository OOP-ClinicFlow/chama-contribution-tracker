package com.zut.chama_contribution_tracker.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zut.chama_contribution_tracker.data.ChamaRepository
import com.zut.chama_contribution_tracker.viewmodel.*

@Composable
fun AppNavigation(repository: ChamaRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "setup") {

        composable("setup") {
            val vm: GroupSetupViewModel = viewModel(factory = viewModelFactory { initializer { GroupSetupViewModel(repository) } })
            GroupSetupScreen(
                viewModel = vm,
                onContinue = { navController.navigate("list") }
            )
        }

        composable("list") {
            val vm: MemberListViewModel = viewModel(factory = viewModelFactory { initializer { MemberListViewModel(repository) } })
            MemberListScreen(
                viewModel = vm,
                onAddContributionClick = { navController.navigate("add") },
                onOpenDashboard = { navController.navigate("dashboard") }
            )
        }

        composable("add") {
            val vm: AddContributionViewModel = viewModel(factory = viewModelFactory { initializer { AddContributionViewModel(repository) } })
            AddContributionScreen(
                viewModel = vm,
                onSaved = { navController.popBackStack() }
            )
        }

        composable("dashboard") {
            val vm: DashboardViewModel = viewModel(factory = viewModelFactory { initializer { DashboardViewModel(repository) } })
            DashboardScreen(
                viewModel = vm,
                onBackToMembers = { navController.popBackStack() }
            )
        }
    }
}