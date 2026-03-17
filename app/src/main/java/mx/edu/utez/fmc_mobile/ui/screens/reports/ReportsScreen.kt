package mx.edu.utez.fmc_mobile.ui.screens.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.fmc_mobile.ui.components.AppTopBar
import mx.edu.utez.fmc_mobile.ui.components.BottomNavBar
import mx.edu.utez.fmc_mobile.ui.components.ReportCard
import mx.edu.utez.fmc_mobile.ui.components.SearchBar
import mx.edu.utez.fmc_mobile.ui.theme.FMC_MobileTheme

@Composable
fun ReportsScreen(navController: NavController) {

    var busqueda by remember { mutableStateOf("") }

    Scaffold(
        topBar = { AppTopBar(title = "Mis Reportes", subtitle = "Historial",leadingIcon = Icons.Default.AddLocation, trailingIcon = Icons.Default.AddCircle) },
        bottomBar = { BottomNavBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            SearchBar(busqueda, onValueChange = { busqueda = it })

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    ReportCard(
                        username = "JEMB1432",
                        createdAt = "2026-03-15T17:26:25.299875",
                        status = "REGISTERED",
                        title = "Bache en la calle principal",
                        address = "Calle Principal #123, Colonia Centro",
                        description = "Se reporta un bache de aproximadamente 50cm de diámetro en la calle principal.",
                        imageUrls = emptyList()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun ReportsScreenPreview() {
    FMC_MobileTheme {
        ReportsScreen(navController = rememberNavController())
    }
}