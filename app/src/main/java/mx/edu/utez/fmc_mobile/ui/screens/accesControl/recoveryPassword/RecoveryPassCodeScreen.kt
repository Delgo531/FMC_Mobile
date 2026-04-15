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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import mx.edu.utez.fmc_mobile.ui.components.OtpField
import mx.edu.utez.fmc_mobile.ui.components.PrimaryButton
import mx.edu.utez.fmc_mobile.ui.components.StepIndicator
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.FMC_MobileTheme
import mx.edu.utez.fmc_mobile.ui.theme.TextPrimary
import mx.edu.utez.fmc_mobile.ui.theme.TextSecondary

@Composable
fun RecoveryPassCodeScreen(
    navController: NavController,
    viewModel: RecoveryViewModel = viewModel()
) {
    val recoveryState by viewModel.recoveryState.collectAsState()
    var otpValue by remember { mutableStateOf("") }
    var showBlockedDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (viewModel.savedEmail.isBlank()) {
            viewModel.resumeRecoveryFlow()
        }
    }

    LaunchedEffect(recoveryState) {
        if (recoveryState is RecoveryState.CodeVerified) {
            viewModel.resetState()
            navController.navigate(Routes.PASSRECOVERYPASS)
        }

        if (recoveryState is RecoveryState.IdentityVerificationFailed) {
            showBlockedDialog = true
            viewModel.resetState()
        }

        if (recoveryState is RecoveryState.Idle && viewModel.savedEmail.isBlank()) {
            navController.navigate(Routes.PASSRECOVERYEMAIL) {
                popUpTo(Routes.PASSRECOVERYEMAIL) { inclusive = true }
            }
        }
    }

    if (showBlockedDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("No se pudo verificar su identidad") },
            text = { Text("Has superado el numero maximo de intentos permitidos. Por seguridad, vuelve a iniciar sesion.") },
            confirmButton = {
                TextButton(onClick = {
                    showBlockedDialog = false
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }) {
                    Text("Ir a login")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                "Verificar",
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
                painter = painterResource(R.drawable.emailicon),
                contentDescription = "email icon",
                modifier = Modifier.size(80.dp),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Revisa tu correo",
                style = AppTypography.Title.copy(fontWeight = FontWeight.SemiBold),
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Ingresa el código de 4 dígitos que enviamos a ${viewModel.savedEmail}",
                style = AppTypography.Body.copy(fontWeight = FontWeight.Medium),
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            OtpField(
                otpValue = otpValue,
                onOtpChange = { otpValue = it }
            )

            Spacer(modifier = Modifier.height(15.dp))

            PrimaryButton(
                text = if (recoveryState is RecoveryState.Loading) "Verificando..." else "Continuar >",
                onClick = {
                    if (recoveryState !is RecoveryState.Loading) {
                        viewModel.verifyCode(otpValue)
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

            StepIndicator(step = 2)

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview
@Composable
fun RecoveryPassCodeScreenPreview() {
    FMC_MobileTheme {
        RecoveryPassCodeScreen(navController = rememberNavController())
    }
}