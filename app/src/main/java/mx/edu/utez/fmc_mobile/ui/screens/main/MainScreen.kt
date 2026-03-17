package mx.edu.utez.fmc_mobile.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.fmc_mobile.navigation.Routes
import mx.edu.utez.fmc_mobile.ui.screens.cuadrilla.CuadrillaScreen
import mx.edu.utez.fmc_mobile.ui.screens.home.HomeScreen
import mx.edu.utez.fmc_mobile.ui.screens.myReports.MyReportsScreen
import mx.edu.utez.fmc_mobile.ui.screens.profile.ProfileScreen
import mx.edu.utez.fmc_mobile.ui.theme.Primary
import mx.edu.utez.fmc_mobile.ui.theme.TextSecondary

private data class TabItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val tabs = listOf(
        TabItem(Routes.HOME_TAB, "Inicio", Icons.Filled.Home, Icons.Outlined.Home),
        TabItem(Routes.SQUAD_TAB, "Cuadrilla", Icons.Outlined.Groups, Icons.Outlined.Groups),
        TabItem(Routes.MY_REPORTS_TAB, "Mis Reportes", Icons.Filled.Report, Icons.Outlined.Report),
        TabItem(Routes.PROFILE_TAB, "Mi Perfil", Icons.Filled.Person, Icons.Outlined.Person)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = backStackEntry?.destination
                tabs.forEach { tab ->
                    val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(tab.label) },
                        selected = selected,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Primary,
                            selectedTextColor = Primary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME_TAB,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.HOME_TAB) {
                HomeScreen(
                    municipality = "Municipio",
                    onCreateReportClick = { /* TODO: navegar a Crear Reporte cuando exista la pantalla */ }
                )
            }
            composable(Routes.SQUAD_TAB) {
                CuadrillaScreen()
            }
            composable(Routes.MY_REPORTS_TAB) {
                MyReportsScreen()
            }
            composable(Routes.PROFILE_TAB) {
                ProfileScreen()
            }
        }
    }
}
