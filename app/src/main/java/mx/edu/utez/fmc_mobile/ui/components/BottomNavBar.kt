package mx.edu.utez.fmc_mobile.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.fmc_mobile.navigation.Routes
import mx.edu.utez.fmc_mobile.ui.theme.*

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

private val items = listOf(
    BottomNavItem("Inicio",         Icons.Filled.Home,         Routes.HOME),
    BottomNavItem("Cuadrilla",      Icons.Filled.Groups,       Routes.TEAMS),
    BottomNavItem("Reportes",   Icons.Filled.Assignment,   Routes.REPORTS),
    BottomNavItem("Avisos", Icons.Filled.Notifications,Routes.NOTIFICATIONS),
    BottomNavItem("Mi perfil",      Icons.Filled.Person,       Routes.PROFILE)
)

@Composable
fun BottomNavBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(
        modifier = modifier,
        containerColor = Color.White
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute == item.route) return@NavigationBarItem
                    navController.navigate(item.route) {
                        popUpTo(Routes.HOME) {
                            inclusive = item.route == Routes.HOME
                            saveState = item.route != Routes.HOME
                        }
                        launchSingleTop = true
                        restoreState = item.route != Routes.HOME
                    }
                },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(text = item.label, style = AppTypography.Caption.copy(fontWeight = FontWeight.SemiBold)) },                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = Color.White
                )
            )
        }
    }
}

@Preview
@Composable
fun BottomNavBarPreview() {
    FMC_MobileTheme {
        BottomNavBar(navController = rememberNavController())
    }
}