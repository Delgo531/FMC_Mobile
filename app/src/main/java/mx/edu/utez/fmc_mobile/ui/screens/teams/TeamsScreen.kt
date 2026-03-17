package mx.edu.utez.fmc_mobile.ui.screens.teams

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.fmc_mobile.ui.components.AppTopBar
import mx.edu.utez.fmc_mobile.ui.components.BottomNavBar
import mx.edu.utez.fmc_mobile.ui.components.InfoCard
import mx.edu.utez.fmc_mobile.ui.components.PrimaryButton
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.FMC_MobileTheme
import mx.edu.utez.fmc_mobile.ui.theme.Primary
import mx.edu.utez.fmc_mobile.ui.theme.TextPrimary

@Composable
fun TeamsScreen(navController: NavController) {
    Scaffold(
        topBar = { AppTopBar(title = "Cuadrillas", subtitle = "Fis My City") },
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

            InfoCard(
                title = "Información sobre voluntarios:",
                message = "Los ciudadanos voluntarios son personas que ayudan a mantener esta aplicación funcionando, si gustas unirte como voluntario lo puedes hacer mediante el botón de \"Unirme\" en la esquina superior derecha o en el siguiente botón.",
                icon = {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Primary
                    )
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = "Quiero ser voluntario",
                onClick = {}
            )


        }
    }
}

@Preview
@Composable
fun TeamsScreenPreview() {
    FMC_MobileTheme {
        TeamsScreen(navController = rememberNavController())
    }
}