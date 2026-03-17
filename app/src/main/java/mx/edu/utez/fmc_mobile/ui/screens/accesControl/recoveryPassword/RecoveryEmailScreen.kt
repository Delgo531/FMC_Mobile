package mx.edu.utez.fmc_mobile.ui.screens.accesControl.recoveryPassword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Email
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
import mx.edu.utez.fmc_mobile.ui.components.ClickableText
import mx.edu.utez.fmc_mobile.ui.components.PrimaryButton
import mx.edu.utez.fmc_mobile.ui.components.StepIndicator
import mx.edu.utez.fmc_mobile.ui.components.TxtField
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.FMC_MobileTheme
import mx.edu.utez.fmc_mobile.ui.theme.Primary
import mx.edu.utez.fmc_mobile.ui.theme.TextPrimary
import mx.edu.utez.fmc_mobile.ui.theme.TextSecondary

@Composable
fun RecoveryEmailScreen(
    navController: NavController,
    viewModel: RecoveryViewModel = viewModel()
) {
    val recoveryState by viewModel.recoveryState.collectAsState()
    var email by remember { mutableStateOf("") }

    LaunchedEffect(recoveryState) {
        if (recoveryState is RecoveryState.EmailSent) {
            viewModel.resetState()
            navController.navigate(Routes.PASSRECOVERYCODE)
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Icon(
                painter = painterResource(R.drawable.recoveyicon),
                contentDescription = "Login icon",
                modifier = Modifier.size(80.dp),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Introduce el correo electrónico asociado a tu cuenta para recibir las instrucciones de recuperación.",
                style = AppTypography.Body.copy(fontWeight = FontWeight.Medium),
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            TxtField(
                value = email,
                onValueChange = { email = it },
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

            Spacer(modifier = Modifier.height(15.dp))

            PrimaryButton(
                text = if (recoveryState is RecoveryState.Loading) "Enviando..." else "Continuar >",
                onClick = {
                    if (recoveryState !is RecoveryState.Loading) {
                        viewModel.forgotPassword(email)
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

            Spacer(modifier = Modifier.height(15.dp))

            Row {
                Text(
                    text = "¿Ya la recordaste? ",
                    style = AppTypography.Body.copy(fontWeight = FontWeight.Normal),
                    color = TextPrimary
                )
                ClickableText(
                    text = "Volver a login",
                    onClick = { navController.navigate(Routes.LOGIN) }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            StepIndicator(step = 1)

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview
@Composable
fun RecoveryEmailScreenPreview() {
    FMC_MobileTheme {
        RecoveryEmailScreen(navController = rememberNavController())
    }
}