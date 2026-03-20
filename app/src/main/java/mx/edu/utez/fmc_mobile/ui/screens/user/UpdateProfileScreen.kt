package mx.edu.utez.fmc_mobile.ui.screens.user

import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
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
import mx.edu.utez.fmc_mobile.ui.components.AppTopBar
import mx.edu.utez.fmc_mobile.ui.components.DropDownField
import mx.edu.utez.fmc_mobile.ui.components.PasswordTxtField
import mx.edu.utez.fmc_mobile.ui.components.PrimaryButton
import mx.edu.utez.fmc_mobile.ui.components.SuccessBottomSheet
import mx.edu.utez.fmc_mobile.ui.components.TxtField
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.FMC_MobileTheme
import mx.edu.utez.fmc_mobile.ui.theme.Primary
import mx.edu.utez.fmc_mobile.ui.theme.TextPrimary
import mx.edu.utez.fmc_mobile.ui.theme.TextSecondary
import mx.edu.utez.fmc_mobile.utils.Constants
import mx.edu.utez.fmc_mobile.utils.SessionManager

@Composable
fun UpdateProfileScreen(navController: NavController, viewModel: ProfileViewModel = viewModel()) {

    var userName by remember { mutableStateOf(SessionManager.getUsername()) }
    var email by remember { mutableStateOf(SessionManager.getEmail()) }
    var password by remember { mutableStateOf("") }
    var municipio by remember { mutableStateOf(SessionManager.getMunicipality()) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val updateState by viewModel.updateState.collectAsState()

    LaunchedEffect(updateState) {
        if (updateState is UpdateProfileState.Success) {
            showBottomSheet = true
        }
    }

    Scaffold(
        topBar = { AppTopBar(title = "Mi perfil", leadingIcon = Icons.Default.ArrowBackIosNew, onLeadingClick = {navController.popBackStack()}) },

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
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = SessionManager.getUsername().ifBlank { "Usuario" },
                style = AppTypography.Title.copy(fontWeight = FontWeight.SemiBold),
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = SessionManager.getEmail().ifBlank { "correo@dominio.com" },
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
                        contentDescription = null,
                        tint = Primary
                    )
                }
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
                        contentDescription = null,
                        tint = Primary
                    )
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            PasswordTxtField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña actual *",
                placeHolder = "Ingresa tu contraseña para confirmar"
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
                        contentDescription = null,
                        tint = Primary
                    )
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            when (updateState) {
                is UpdateProfileState.Loading -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(12.dp))
                }
                is UpdateProfileState.Error -> {
                    Text(
                        text = (updateState as UpdateProfileState.Error).message,
                        color = Color.Red,
                        style = AppTypography.BodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                else -> {}
            }

            PrimaryButton(
                text = if (updateState is UpdateProfileState.Loading) "Guardando..." else "Guardar",
                onClick = {
                    if (updateState !is UpdateProfileState.Loading) {
                        viewModel.updateProfile(userName, email, password, municipio)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
    if (showBottomSheet) {
        SuccessBottomSheet(
            title = "¡Perfil Actualizado!",
            message = "Tus cambios han sido guardados exitosamente en tu perfil de ciudadano de Morelos.",
            buttonText = "Continuar",
            icon = {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(14.dp)
                )
            },
            onButtonClick = {
                showBottomSheet = false
                viewModel.resetUpdateState()
                navController.popBackStack()
            },
            onDismiss = {
                showBottomSheet = false
                viewModel.resetUpdateState()
            }
        )
    }
}

@Preview()
@Composable
fun UpdateProfileScreenPreview() {
    FMC_MobileTheme {
        UpdateProfileScreen(navController = rememberNavController())
    }
}