package mx.edu.utez.fmc_mobile.ui.screens.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.fmc_mobile.ui.components.AppTopBar
import mx.edu.utez.fmc_mobile.ui.components.BottomNavBar
import mx.edu.utez.fmc_mobile.ui.components.NotificationCard
import mx.edu.utez.fmc_mobile.ui.theme.FMC_MobileTheme

@Composable
fun NotificationsScreen(navController: NavController) {

    Scaffold(
        topBar = { AppTopBar(title = "Avisos", subtitle = "Fix My City") },
        bottomBar = { BottomNavBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    NotificationCard(
                        type = "Reporte Asignado",
                        message = "Se te ha asignado un nuevo reporte en Temixco.",
                        createdAt = "2026-03-15T17:26:25.299875",
                        read = false
                    )
                }
                item {
                    NotificationCard(
                        type = "Estado Actualizado",
                        message = "Tu denuncia ha cambiado de estado a En Proceso.",
                        createdAt = "2026-03-15T17:26:25.299875",
                        read = true
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun NotificationsScreenPreview() {
    FMC_MobileTheme {
        NotificationsScreen(navController = rememberNavController())
    }
}