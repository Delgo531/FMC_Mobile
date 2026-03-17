package mx.edu.utez.fmc_mobile.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.fmc_mobile.ui.screens.accesControl.login.LoginScreen
import mx.edu.utez.fmc_mobile.ui.screens.accesControl.passRecoveryCode.RecoveryPassCode
import mx.edu.utez.fmc_mobile.ui.screens.accesControl.passRecoveryEmail.RecoveryEmailScreen
import mx.edu.utez.fmc_mobile.ui.screens.accesControl.passRecoveryPass.RecoveryPassScreen
import mx.edu.utez.fmc_mobile.ui.screens.accesControl.passRecoverySucces.RecoveryPassSucces
import mx.edu.utez.fmc_mobile.ui.screens.accesControl.register.RegisterScreen
import mx.edu.utez.fmc_mobile.ui.screens.main.MainScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

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
            RecoveryEmailScreen(navController)
        }

        composable(Routes.PASSRECOVERYCODE) {
            RecoveryPassCode(navController)
        }

        composable(Routes.PASSRECOVERYSUCCES) {
            RecoveryPassSucces(navController)
        }

        composable(Routes.PASSRECOVERYPASS) {
            RecoveryPassScreen(navController)
        }

        composable(Routes.MAIN) {
            MainScreen()
        }
    }
}