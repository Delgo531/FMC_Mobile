package mx.edu.utez.fmc_mobile.ui.screens.teams.joinRequest

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.fmc_mobile.navigation.Routes
import mx.edu.utez.fmc_mobile.ui.components.*
import mx.edu.utez.fmc_mobile.ui.theme.*

// "NONE"
// "PENDING"
// "MEMBER"
private enum class UserStatus { NONE, PENDING, MEMBER }

@Composable
fun TeamsScreen(navController: NavController) {

    var userStatus by remember { mutableStateOf(UserStatus.MEMBER) }
    var showJoinSheet by remember { mutableStateOf(false) }
    var showCancelSheet by remember { mutableStateOf(false) }
    var showLeaveSheet by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            when (userStatus) {
                UserStatus.MEMBER -> AppTopBar(
                    title = "Mi Cuadrilla",
                    subtitle = "Fix My City",
                    leadingIcon = Icons.Default.LocationOn,
                    trailingIcon = Icons.Default.Close,
                    trailingIconTint = Color(0xFFC62828),
                    onTrailingClick = { showLeaveSheet = true }
                )
                else -> AppTopBar(
                    title = "Mi Cuadrilla",
                    subtitle = "Fix My City",
                    trailingIcon = if (userStatus == UserStatus.NONE) Icons.Default.GroupAdd else null,
                    onTrailingClick = { showJoinSheet = true }
                )
            }
        },
        bottomBar = { BottomNavBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            when (userStatus) {
                UserStatus.NONE, UserStatus.PENDING -> {
                    InfoCard(
                        title = "Información sobre voluntarios:",
                        message = "Los ciudadanos voluntarios son personas que ayudan a mantener esta aplicación funcionando, si gustas unirte como voluntario lo puedes hacer mediante el botón de \"Unirme\" en la esquina superior derecha o en el siguiente botón.",
                        icon = {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Primary
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (userStatus == UserStatus.PENDING) {
                        Text(
                            text = "Solicitud Pendiente",
                            style = AppTypography.Body.copy(fontWeight = FontWeight.SemiBold),
                            color = Primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        PrimaryButton(
                            text = "Cancelar solicitud",
                            onClick = { showCancelSheet = true }
                        )
                    } else {
                        PrimaryButton(
                            text = "¡Quiero ser voluntario!",
                            onClick = { showJoinSheet = true }
                        )
                    }
                }

                UserStatus.MEMBER -> {
                    SquadCard(
                        squadName = "Cuadrilla 1 Temixco",
                        municipality = "Temixco, Morelos",
                        role = "Líder"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Pendientes", "Resueltos").forEachIndexed { index, label ->
                            val selected = selectedTab == index
                            Button(
                                onClick = { selectedTab = index },
                                shape = RoundedCornerShape(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selected) Primary else Color.White,
                                    contentColor = if (selected) Color.White else TextSecondary
                                ),
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = AppTypography.BodySmall.copy(fontWeight = FontWeight.SemiBold)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (selectedTab == 0) {
                            item {
                                AssignedReportCard(
                                    username = "JEMB1432",
                                    createdAt = "2026-03-15T17:26:25.299875",
                                    status = "REGISTERED",
                                    title = "Ubicacion proporcionada",
                                    address = "Calle Principal #123, Colonia Centro",
                                    description = "Descripción de la denuncia...",
                                    currentVotes = 1,
                                    totalVotes = 5,
                                    imageUrls = emptyList(),
                                    onClick = { navController.navigate(Routes.REPORTDETAILS) },
                                    onAccept = {},
                                    onReject = {}
                                )
                            }
                        } else {
                            item {
                                AssignedReportCard(
                                    username = "JEMB1432",
                                    createdAt = "2026-03-15T17:26:25.299875",
                                    status = "COMPLETED",
                                    title = "Bache resuelto",
                                    address = "Calle Principal #123, Colonia Centro",
                                    description = "Descripción de la denuncia...",
                                    currentVotes = 5,
                                    totalVotes = 5,
                                    imageUrls = emptyList(),
                                    onClick = { navController.navigate(Routes.REPORTDETAILS) },
                                    onAccept = {},
                                    onReject = {}
                                )
                            }
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }

    if (showJoinSheet) {
        SuccessBottomSheet(
            title = "¿Quieres postularte como voluntario?",
            message = "Un administrador revisará tu solicitud para asignarte a una cuadrilla oficial. Recibirás una notificación cuando tu perfil sea aprobado.",
            buttonText = "Sí, confirmar postulación",
            secondaryButtonText = "No, cancelar",
            icon = {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(14.dp)
                )
            },
            onButtonClick = {
                userStatus = UserStatus.PENDING
                showJoinSheet = false
            },
            onSecondaryButtonClick = { showJoinSheet = false },
            onDismiss = { showJoinSheet = false }
        )
    }

    if (showCancelSheet) {
        SuccessBottomSheet(
            title = "¿Deseas cancelar tu solicitud?",
            message = "Si cancelas tu solicitud, deberás volver a postularte para unirte a una cuadrilla.",
            buttonText = "Sí, cancelar",
            secondaryButtonText = "No, conservar solicitud",
            icon = {
                Icon(
                    imageVector = Icons.Default.PersonRemove,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(14.dp)
                )
            },
            onButtonClick = {
                userStatus = UserStatus.NONE
                showCancelSheet = false
            },
            onSecondaryButtonClick = { showCancelSheet = false },
            onDismiss = { showCancelSheet = false }
        )
    }

    if (showLeaveSheet) {
        SuccessBottomSheet(
            title = "¿Deseas salir de la cuadrilla?",
            message = "",
            buttonText = "Sí, abandonar",
            secondaryButtonText = "No, permanecer en mi cuadrilla",
            icon = {
                Icon(
                    imageVector = Icons.Default.PersonRemove,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(14.dp)
                )
            },
            onButtonClick = {
                userStatus = UserStatus.NONE
                showLeaveSheet = false
            },
            onSecondaryButtonClick = { showLeaveSheet = false },
            onDismiss = { showLeaveSheet = false }
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun TeamsScreenPreview() {
    FMC_MobileTheme {
        TeamsScreen(navController = rememberNavController())
    }
}