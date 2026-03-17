package mx.edu.utez.fmc_mobile.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import mx.edu.utez.fmc_mobile.navigation.Routes
import mx.edu.utez.fmc_mobile.ui.theme.*

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

private val items = listOf(
    BottomNavItem("Inicio",       Icons.Filled.Home,       Routes.HOME),
    BottomNavItem("Cuadrilla",    Icons.Filled.Groups,     Routes.TEAMS),
    BottomNavItem("Mis reportes", Icons.Filled.Assignment, Routes.REPORTS),
    BottomNavItem("Mi perfil",    Icons.Filled.Person,     Routes.PROFILE)
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
                    navController.navigate(item.route) {
                        popUpTo(Routes.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
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