package mx.edu.utez.fmc_mobile.ui.screens.accesControl.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.fmc_mobile.R
import mx.edu.utez.fmc_mobile.navigation.Routes
import mx.edu.utez.fmc_mobile.ui.components.AppTopBar
import mx.edu.utez.fmc_mobile.ui.components.ClickableText
import mx.edu.utez.fmc_mobile.ui.components.PasswordTxtField
import mx.edu.utez.fmc_mobile.ui.components.PrimaryButton
import mx.edu.utez.fmc_mobile.ui.components.TxtField
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.FMC_MobileTheme
import mx.edu.utez.fmc_mobile.ui.theme.Primary
import mx.edu.utez.fmc_mobile.ui.theme.TextPrimary
import mx.edu.utez.fmc_mobile.ui.theme.TextSecondary

@Composable
fun LoginScreen(navController: NavHostController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            AppTopBar( "Inicio de Sesión")
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Icon(
                painter = painterResource(R.drawable.loginicon),
                contentDescription = "Login icon",
                modifier = Modifier.size(80.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.height(5.dp))

            Text(text = "¡ Bienvenido !", style = AppTypography.Title.copy(fontWeight = FontWeight.SemiBold), color = TextPrimary)
            Spacer(modifier = Modifier.height(5.dp))

            Text(text = "Mejorando Morelos Juntos", style = AppTypography.Body.copy(fontWeight = FontWeight.Medium), color = TextSecondary)

            Spacer(modifier = Modifier.height(30.dp))

            TxtField(
                value =email,
                 onValueChange = {email = it},
                label = "Correo Electrónico",
                placeHolder = "tucorreo@dominio.com",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = Primary
                    )
                }
            )

            Spacer(modifier = Modifier.height(20.dp))


            PasswordTxtField(
                value = password,
                onValueChange = {password = it},
                label = "Contraseña",
                placeHolder = "••••••••"
            )

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                ClickableText(
                    text = "¿Olivadaste tu contraseña?",
                    onClick = { navController.navigate(Routes.PASSRECOVERYEMAIL)}
                )
            }


            Spacer(modifier = Modifier.height(15.dp))

            PrimaryButton(
                text = "Iniciar Sesión",
                onClick = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )

            Spacer(modifier = Modifier.height(15.dp))

            Row{
                Text(text = "¿No tienes una cuenta? ", style = AppTypography.Body.copy(fontWeight = FontWeight.Normal), color = TextPrimary)
                ClickableText(
                    text = "Crear cuenta",
                    onClick = { navController.navigate(Routes.REGISTER)}
                )
            }
        }

    }
}

@Preview
@Composable
fun prev(){
    FMC_MobileTheme() { LoginScreen(navController = rememberNavController()); }

}
