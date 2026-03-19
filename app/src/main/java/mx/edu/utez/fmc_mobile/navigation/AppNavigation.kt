package mx.edu.utez.fmc_mobile.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.fmc_mobile.ui.screens.accesControl.login.LoginScreen
import mx.edu.utez.fmc_mobile.ui.screens.accesControl.recoveryPassword.RecoveryEmailScreen
import mx.edu.utez.fmc_mobile.ui.screens.accesControl.recoveryPassword.RecoveryPassCodeScreen
import mx.edu.utez.fmc_mobile.ui.screens.accesControl.recoveryPassword.RecoveryPassScreen
import mx.edu.utez.fmc_mobile.ui.screens.accesControl.recoveryPassword.RecoveryPassSucces
import mx.edu.utez.fmc_mobile.ui.screens.accesControl.recoveryPassword.RecoveryViewModel
import mx.edu.utez.fmc_mobile.ui.screens.accesControl.register.RegisterScreen
import mx.edu.utez.fmc_mobile.ui.screens.home.Home
import mx.edu.utez.fmc_mobile.ui.screens.notifications.NotificationsScreen
import mx.edu.utez.fmc_mobile.ui.screens.profile.ProfileScreen
import mx.edu.utez.fmc_mobile.ui.screens.reports.NewReportScreen
import mx.edu.utez.fmc_mobile.ui.screens.reports.ReportsScreen
import mx.edu.utez.fmc_mobile.ui.screens.teams.ReportDetailsScreen
import mx.edu.utez.fmc_mobile.ui.screens.teams.joinRequest.TeamsScreen
import mx.edu.utez.fmc_mobile.ui.screens.user.UpdateProfileScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val recoveryViewModel: RecoveryViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ){
        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }

        composable(Routes.REGISTER) {
            RegisterScreen(navController)
        }

        composable(Routes.PASSRECOVERYEMAIL) {
            RecoveryEmailScreen(navController, recoveryViewModel)
        }

        composable(Routes.PASSRECOVERYCODE) {
            RecoveryPassCodeScreen(navController, recoveryViewModel)
        }

        composable(Routes.PASSRECOVERYPASS) {
            RecoveryPassScreen(navController, recoveryViewModel)
        }

        composable(Routes.PASSRECOVERYSUCCES) {
            RecoveryPassSucces(navController)
        }

        composable(Routes.HOME) {
            Home(navController)
        }

        composable(Routes.TEAMS) {
            TeamsScreen(navController)
        }

        composable(Routes.REPORTS) {
            ReportsScreen(navController)
        }

        composable(Routes.PROFILE) {
            ProfileScreen(navController)
        }

        composable(Routes.UPDATEPROFILE) {
            UpdateProfileScreen(navController)
        }

        composable(Routes.CREATEREPORT) {
            NewReportScreen(navController)
        }

        composable(Routes.REPORTDETAILS) {
            ReportDetailsScreen(navController)
        }

        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(navController)
        }

    }
}