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
import androidx.compose.material.icons.filled.Person
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
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = viewModel()
) {
    val loginState by viewModel.loginState.collectAsState()
    val usernameError by viewModel.usernameError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            // Al iniciar sesión correctamente, navegar al contenedor principal
            // con el bottom nav (Inicio, Cuadrilla, Mis Reportes, Mi Perfil).
            navController.navigate(Routes.MAIN) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar("Inicio de Sesión")
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

            Text(
                text = "¡ Bienvenido !",
                style = AppTypography.Title.copy(fontWeight = FontWeight.SemiBold),
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Mejorando Morelos Juntos",
                style = AppTypography.Body.copy(fontWeight = FontWeight.Medium),
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(30.dp))

            TxtField(
                value = username,
                onValueChange = { username = it },
                label = "Usuario",
                placeHolder = "Tu nombre de usuario",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Usuario",
                        tint = Primary
                    )
                },
                errorMessage = if (usernameError) "" else null
            )

            Spacer(modifier = Modifier.height(20.dp))

            PasswordTxtField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                placeHolder = "••••••••",
                errorMessage = if (passwordError) "" else null
            )

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                ClickableText(
                    text = "¿Olvidaste tu contraseña?",
                    onClick = { navController.navigate(Routes.PASSRECOVERYEMAIL) }
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            PrimaryButton(
                text = if (loginState is LoginState.Loading) "Iniciando..." else "Iniciar Sesión",
                onClick = {
                    if (loginState !is LoginState.Loading) {
                        viewModel.login(username, password)
                    }
                }
            )

            Spacer(modifier = Modifier.height(15.dp))

            when (loginState) {
                is LoginState.Loading -> CircularProgressIndicator()
                is LoginState.Error -> Text(
                    text = (loginState as LoginState.Error).message,
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
                    text = "¿No tienes una cuenta? ",
                    style = AppTypography.Body.copy(fontWeight = FontWeight.Normal),
                    color = TextPrimary
                )
                ClickableText(
                    text = "Crear cuenta",
                    onClick = { navController.navigate(Routes.REGISTER) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun LoginScreenPreview() {
    FMC_MobileTheme {
        LoginScreen(navController = rememberNavController())
    }
}