package com.zut.chama_contribution_tracker.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zut.chama_contribution_tracker.data.ChamaRepository
import com.zut.chama_contribution_tracker.viewmodel.AddContributionViewModel
import com.zut.chama_contribution_tracker.viewmodel.DashboardViewModel
import com.zut.chama_contribution_tracker.viewmodel.GroupSetupViewModel
import com.zut.chama_contribution_tracker.viewmodel.MemberListViewModel

@Composable
fun AppNavigation(repository: ChamaRepository) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "setup"
    ) {

        // 1. Group Setup
        composable("setup") {

            val setupViewModel: GroupSetupViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        GroupSetupViewModel(repository)
                    }
                }
            )

            GroupSetupScreen(
                viewModel = setupViewModel,
                onContinue = {
                    navController.navigate("members")
                }
            )
        }

        // 2. Member List
        composable("members") {

            val memberListViewModel: MemberListViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        MemberListViewModel(repository)
                    }
                }
            )

            MemberListScreen(
                viewModel = memberListViewModel,
                onAddContributionClick = {
                    navController.navigate("add_contribution")
                },
                onOpenDashboard = {
                    navController.navigate("dashboard")
                }
            )
        }

        // 3. Add Contribution
        composable("add_contribution") {

            val addContributionViewModel: AddContributionViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        AddContributionViewModel(repository)
                    }
                }
            )

            AddContributionScreen(
                viewModel = addContributionViewModel,
                onSaved = {
                    navController.popBackStack()
                }
            )
        }

        // 4. Dashboard
        composable("dashboard") {

            val dashboardViewModel: DashboardViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        DashboardViewModel(repository)
                    }
                }
            )

            DashboardScreen(
                viewModel = dashboardViewModel,
                onBackToMembers = {
                    navController.popBackStack()
                },
                onOpenReminderSettings = {
                    navController.navigate("reminder_settings")
                }
            )
        }

        // 5. Scheduled Reminder Settings
        composable("reminder_settings") {

            ReminderSettingsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}