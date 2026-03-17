package mx.edu.utez.fmc_mobile.ui.screens.accesControl.register

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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Map
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
import mx.edu.utez.fmc_mobile.ui.components.DropDownField
import mx.edu.utez.fmc_mobile.ui.components.PasswordTxtField
import mx.edu.utez.fmc_mobile.ui.components.PrimaryButton
import mx.edu.utez.fmc_mobile.ui.components.TxtField
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.FMC_MobileTheme
import mx.edu.utez.fmc_mobile.ui.theme.Primary
import mx.edu.utez.fmc_mobile.ui.theme.TextPrimary
import mx.edu.utez.fmc_mobile.ui.theme.TextSecondary
import mx.edu.utez.fmc_mobile.utils.Constants

@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel = viewModel()) {

    val registerState by viewModel.registerState.collectAsState()
    val usernameError by viewModel.usernameError.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val municipioError by viewModel.municipioError.collectAsState()

    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var municipio by remember { mutableStateOf("") }

    LaunchedEffect(registerState) {
        if (registerState is RegisterState.Success) {
            navController.navigate(Routes.LOGIN)
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                "Registro",
                leadingIcon = Icons.Default.ArrowBackIosNew,
                onLeadingClick = { navController.popBackStack() }
            )
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
                painter = painterResource(R.drawable.usericon),
                contentDescription = "Login icon",
                modifier = Modifier.size(80.dp),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Crear Cuenta",
                style = AppTypography.Title.copy(fontWeight = FontWeight.SemiBold),
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Únete para reportar y mejorar la\ninfrastructura de Morelos.",
                style = AppTypography.Body.copy(fontWeight = FontWeight.Medium),
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            TxtField(
                value = userName,
                onValueChange = { userName = it },
                label = "Nombre de Usuario *",
                placeHolder = "Ej. Jane Doe",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Nombre de Usuario",
                        tint = Primary
                    )
                },
                errorMessage = if (usernameError) "" else null
            )

            Spacer(modifier = Modifier.height(20.dp))

            TxtField(
                value = email,
                onValueChange = { email = it },
                label = "Correo Electrónico *",
                placeHolder = "tucorreo@dominio.com",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = Primary
                    )
                },
                errorMessage = if (emailError) "" else null
            )

            Spacer(modifier = Modifier.height(20.dp))

            PasswordTxtField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña *",
                placeHolder = "Mínimo 8 caracteres",
                errorMessage = if (passwordError) "" else null
            )

            Spacer(modifier = Modifier.height(20.dp))

            DropDownField(
                label = "Municipio *",
                options = Constants.municipiosMorelos,
                selectedOption = municipio,
                onOptionSelected = { municipio = it },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Municipio",
                        tint = Primary
                    )
                },
                errorMessage = if (municipioError) "" else null
            )

            Spacer(modifier = Modifier.height(15.dp))

            PrimaryButton(
                text = if (registerState is RegisterState.Loading) "Registrando..." else "Registrarse",
                onClick = {
                    if (registerState !is RegisterState.Loading) {
                        viewModel.register(
                            username = userName,
                            email = email,
                            password = password,
                            municipality = municipio
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(15.dp))

            when (registerState) {
                is RegisterState.Loading -> CircularProgressIndicator()
                is RegisterState.Error -> Text(
                    text = (registerState as RegisterState.Error).message,
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
                    text = "¿Ya tienes una cuenta? ",
                    style = AppTypography.Body.copy(fontWeight = FontWeight.Normal),
                    color = TextPrimary
                )
                ClickableText(
                    text = "Inicia Sesión",
                    onClick = { navController.popBackStack() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun RegisterScreenPreview() {
    FMC_MobileTheme {
        RegisterScreen(navController = rememberNavController())
    }
}