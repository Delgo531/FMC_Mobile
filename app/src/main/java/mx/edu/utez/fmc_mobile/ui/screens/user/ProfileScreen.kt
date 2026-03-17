package mx.edu.utez.fmc_mobile.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.fmc_mobile.R
import mx.edu.utez.fmc_mobile.navigation.Routes
import mx.edu.utez.fmc_mobile.ui.components.AppTopBar
import mx.edu.utez.fmc_mobile.ui.components.BottomNavBar
import mx.edu.utez.fmc_mobile.ui.components.InfoCard
import mx.edu.utez.fmc_mobile.ui.components.LogoutButton
import mx.edu.utez.fmc_mobile.ui.components.TxtDisplay
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.FMC_MobileTheme
import mx.edu.utez.fmc_mobile.ui.theme.Primary
import mx.edu.utez.fmc_mobile.ui.theme.TextPrimary
import mx.edu.utez.fmc_mobile.ui.theme.TextSecondary

@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        topBar = { AppTopBar(title = "Mi perfil", leadingIcon = Icons.Default.AddLocation, trailingIcon = Icons.Default.Create, onTrailingClick = {navController.navigate(
            Routes.UPDATEPROFILE)}) },
        bottomBar = { BottomNavBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            Icon(
                painter = painterResource(R.drawable.usericon),
                contentDescription = "Login icon",
                modifier = Modifier.size(80.dp),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Jane Doe",
                style = AppTypography.Title.copy(fontWeight = FontWeight.SemiBold),
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "example@domain.com",
                style = AppTypography.Body.copy(fontWeight = FontWeight.Medium),
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            TxtDisplay(
                label = "Usuario",
                value = "Jane Doe",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = Primary
                    )
                }
            )

            Spacer(modifier = Modifier.height(15.dp))

            TxtDisplay(
                label = "Municipio",
                value = "Municipio Actual",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = Primary
                    )
                }
            )
            Spacer(modifier = Modifier.height(15.dp))
            LogoutButton(onClick = {

                navController.navigate(Routes.LOGIN){
                    popUpTo(0) {inclusive = true}
                }

            })
            Spacer(modifier = Modifier.height(15.dp))

            InfoCard(
                title = "Aviso de Privacidad:",
                message = "Tu privacidad es nuestra prioridad. No recolectamos datos personales adicionales. Tus reportes son anónimos ante otros ciudadanos.",
                icon = {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = Primary
                    )
                }
            )


        }
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    FMC_MobileTheme {
        ProfileScreen(navController = rememberNavController())
    }
}