package mx.edu.utez.fmc_mobile.ui.screens.accesControl.recoveryPassword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.fmc_mobile.R
import mx.edu.utez.fmc_mobile.navigation.Routes
import mx.edu.utez.fmc_mobile.ui.components.AppTopBar
import mx.edu.utez.fmc_mobile.ui.components.PasswordTxtField
import mx.edu.utez.fmc_mobile.ui.components.PasswordRequirementsChecklist
import mx.edu.utez.fmc_mobile.ui.components.PrimaryButton
import mx.edu.utez.fmc_mobile.ui.components.StepIndicator
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.FMC_MobileTheme
import mx.edu.utez.fmc_mobile.ui.theme.TextPrimary
import mx.edu.utez.fmc_mobile.ui.theme.TextSecondary

@Composable
fun RecoveryPassScreen(
    navController: NavController,
    viewModel: RecoveryViewModel = viewModel()
) {
    val recoveryState by viewModel.recoveryState.collectAsState()
    var password1 by remember { mutableStateOf("") }
    var password2 by remember { mutableStateOf("") }
    val hasError = recoveryState is RecoveryState.Error


    LaunchedEffect(recoveryState) {
        if (recoveryState is RecoveryState.PasswordReset) {
            viewModel.resetState()
            navController.navigate(Routes.PASSRECOVERYSUCCES)
        }
    }

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
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Icon(
                painter = painterResource(R.drawable.lockicon),
                contentDescription = "lock icon",
                modifier = Modifier.size(80.dp),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Nueva Contraseña",
                style = AppTypography.Title.copy(fontWeight = FontWeight.SemiBold),
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Crea una nueva clave segura para acceder a tu cuenta y seguir reportando incidentes en Morelos.",
                style = AppTypography.Body.copy(fontWeight = FontWeight.Medium),
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            PasswordTxtField(
                value = password1,
                onValueChange = { password1 = it },
                label = "Nueva contraseña",
                placeHolder = "••••••••",
                errorMessage = if (hasError) "" else null
            )

            Spacer(modifier = Modifier.height(8.dp))

            PasswordRequirementsChecklist(password = password1)

            Spacer(modifier = Modifier.height(20.dp))

            PasswordTxtField(
                value = password2,
                onValueChange = { password2 = it },
                label = "Confirmar nueva contraseña",
                placeHolder = "••••••••",
                errorMessage = if (hasError) "" else null
            )

            Spacer(modifier = Modifier.height(15.dp))

            PrimaryButton(
                text = if (recoveryState is RecoveryState.Loading) "Actualizando..." else "Actualizar contraseña >",
                onClick = {
                    if (recoveryState !is RecoveryState.Loading) {
                        viewModel.resetPassword(password1, password2)
                    }
                }
            )

            Spacer(modifier = Modifier.height(15.dp))

            when (recoveryState) {
                is RecoveryState.Loading -> CircularProgressIndicator()
                is RecoveryState.Error -> Text(
                    text = (recoveryState as RecoveryState.Error).message,
                    color = Color.Red,
                    style = AppTypography.Body,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                else -> {}
            }

            Spacer(modifier = Modifier.height(32.dp))

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