package mx.edu.utez.fmc_mobile.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.fmc_mobile.ui.screens.accesControl.login.LoginScreen

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


    }
}