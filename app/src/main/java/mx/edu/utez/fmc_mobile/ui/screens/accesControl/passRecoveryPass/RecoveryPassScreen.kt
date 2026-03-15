package mx.edu.utez.fmc_mobile.ui.screens.accesControl.passRecoveryPass

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.fmc_mobile.R
import mx.edu.utez.fmc_mobile.navigation.Routes
import mx.edu.utez.fmc_mobile.ui.components.AppTopBar
import mx.edu.utez.fmc_mobile.ui.components.ClickableText
import mx.edu.utez.fmc_mobile.ui.components.OtpField
import mx.edu.utez.fmc_mobile.ui.components.PasswordTxtField
import mx.edu.utez.fmc_mobile.ui.components.PrimaryButton
import mx.edu.utez.fmc_mobile.ui.components.StepIndicator
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.FMC_MobileTheme
import mx.edu.utez.fmc_mobile.ui.theme.TextPrimary
import mx.edu.utez.fmc_mobile.ui.theme.TextSecondary

@Composable
fun RecoveryPassScreen(navController: NavController) {

    var password1 by remember { mutableStateOf("") }
    var password2 by remember { mutableStateOf("") }


    Scaffold(

        topBar = {
            AppTopBar(
                "Recuperar Contraseña",
                leadingIcon = Icons.Default.ArrowBackIosNew,
                onLeadingClick = { navController.popBackStack() }
            )
        }
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
                painter = painterResource(R.drawable.lockicon),
                contentDescription = "email icon",
                modifier = Modifier.size(80.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.height(5.dp))

            Text(text = "Nueva Contraseña", style = AppTypography.Title.copy(fontWeight = FontWeight.SemiBold), color = TextPrimary)
            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Crea una nueva clave segura para acceder a tu " +
                        "cuenta y seguir reportando incidentes en Morelos.",
                style = AppTypography.Body.copy(fontWeight = FontWeight.Medium),
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))


            PasswordTxtField(
                value = password1,
                onValueChange = {password1 = it},
                label = "Nueva contraseña",
                placeHolder = "••••••••"
            )

            Spacer(modifier = Modifier.height(20.dp))

            PasswordTxtField(
                value = password2,
                onValueChange = {password2 = it},
                label = "Confirmar Nueva contraseña",
                placeHolder = "••••••••"
            )


            Spacer(modifier = Modifier.height(15.dp))

            PrimaryButton(
                "Actualizar contraseña >",
                onClick = {navController.navigate(Routes.PASSRECOVERYSUCCES)}
            )

            Spacer(modifier = Modifier.height(15.dp))




            Spacer(modifier = Modifier.weight(1f))

            StepIndicator(step = 3)

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview
@Composable
fun RecoveryPassScreenPreview() {
    FMC_MobileTheme {
        RecoveryPassScreen(navController = rememberNavController())
    }
}