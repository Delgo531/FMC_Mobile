package mx.edu.utez.fmc_mobile.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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
import mx.edu.utez.fmc_mobile.ui.components.BottomNavBar
import mx.edu.utez.fmc_mobile.ui.components.LogoutButton
import mx.edu.utez.fmc_mobile.ui.components.TxtDisplay
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import mx.edu.utez.fmc_mobile.ui.screens.user.DeactivateState
import mx.edu.utez.fmc_mobile.ui.screens.user.LogoutState
import mx.edu.utez.fmc_mobile.ui.screens.user.ProfileViewModel
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.FMC_MobileTheme
import mx.edu.utez.fmc_mobile.ui.theme.Primary
import mx.edu.utez.fmc_mobile.ui.theme.TextPrimary
import mx.edu.utez.fmc_mobile.ui.theme.TextSecondary

@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileViewModel = viewModel()) {

    val username by viewModel.username.collectAsState()
    val email by viewModel.email.collectAsState()
    val municipality by viewModel.municipality.collectAsState()
    val logoutState by viewModel.logoutState.collectAsState()
    val deactivateState by viewModel.deactivateState.collectAsState()

    var showDeactivateDialog by remember { mutableStateOf(false) }
    var deactivatePassword by remember { mutableStateOf("") }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    LaunchedEffect(logoutState) {
        if (logoutState is LogoutState.Success) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    LaunchedEffect(deactivateState) {
        if (deactivateState is DeactivateState.Success) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshProfile()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
                text = username.ifBlank { "Usuario" },
                style = AppTypography.Title.copy(fontWeight = FontWeight.SemiBold),
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = email.ifBlank { "correo@dominio.com" },
                style = AppTypography.Body.copy(fontWeight = FontWeight.Medium),
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            TxtDisplay(
                label = "Usuario",
                value = username.ifBlank { "No disponible" },
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
                value = municipality.ifBlank { "No disponible" },
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
                viewModel.logout()
            })
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = { showDeactivateDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Desactivar mi cuenta",
                    style = AppTypography.BodySmall,
                    color = Color(0xFFC62828)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { showPrivacyDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Ver Aviso de Privacidad",
                    style = AppTypography.BodySmall,
                    color = Primary
                )
            }


        }
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = Primary
                )
            },
            title = {
                Text(
                    text = "Aviso de Privacidad",
                    style = AppTypography.Body.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "Tu privacidad es nuestra prioridad. No recolectamos datos personales adicionales a los estrictamente necesarios para el funcionamiento de la aplicación. Tus reportes son anónimos ante otros ciudadanos. La información proporcionada se utiliza únicamente para gestionar incidencias municipales y mejorar los servicios públicos de tu comunidad. No compartimos tus datos con terceros sin tu consentimiento.",
                    style = AppTypography.BodySmall,
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Entendido", color = Primary)
                }
            }
        )
    }

    if (showDeactivateDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeactivateDialog = false
                deactivatePassword = ""
                viewModel.resetDeactivateState()
            },
            title = {
                Text(
                    text = "¿Desactivar tu cuenta?",
                    style = AppTypography.Body.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    Text(
                        text = "Esta acción desactivará tu cuenta. Tus reportes no serán eliminados. Ingresa tu contraseña para confirmar.",
                        style = AppTypography.BodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = deactivatePassword,
                        onValueChange = { deactivatePassword = it },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = deactivateState is DeactivateState.Error
                    )
                    if (deactivateState is DeactivateState.Error) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = (deactivateState as DeactivateState.Error).message,
                            color = Color(0xFFC62828),
                            style = AppTypography.BodySmall
                        )
                    }
                    if (deactivateState is DeactivateState.Loading) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deactivateAccount(deactivatePassword)
                    },
                    enabled = deactivatePassword.isNotBlank() && deactivateState !is DeactivateState.Loading
                ) {
                    Text("Desactivar", color = Color(0xFFC62828))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeactivateDialog = false
                    deactivatePassword = ""
                    viewModel.resetDeactivateState()
                }) {
                    Text("Cancelar", color = TextSecondary)
                }
            }
        )
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    FMC_MobileTheme {
        ProfileScreen(navController = rememberNavController())
    }
}