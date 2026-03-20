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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.fmc_mobile.navigation.Routes
import mx.edu.utez.fmc_mobile.ui.components.*
import mx.edu.utez.fmc_mobile.ui.screens.teams.TeamsViewModel
import mx.edu.utez.fmc_mobile.ui.theme.*

@Composable
fun TeamsScreen(navController: NavController, viewModel: TeamsViewModel = viewModel()) {

    val userStatus by viewModel.userStatus.collectAsState()
    val squadInfo by viewModel.squadInfo.collectAsState()
    val assignedReports by viewModel.assignedReports.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val actionSuccess by viewModel.actionSuccess.collectAsState()

    var showJoinSheet by remember { mutableStateOf(false) }
    var showCancelSheet by remember { mutableStateOf(false) }
    var showLeaveSheet by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    // Show snackbar for messages
    LaunchedEffect(actionSuccess) {
        if (actionSuccess != null) {
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            when (userStatus) {
                "MEMBER" -> AppTopBar(
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
                    trailingIcon = if (userStatus == "NONE") Icons.Default.GroupAdd else null,
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

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                when (userStatus) {
                    "NONE", "PENDING" -> {
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

                        if (userStatus == "PENDING") {
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

                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = errorMessage ?: "",
                                color = Color.Red,
                                style = AppTypography.BodySmall
                            )
                        }
                    }

                    "MEMBER" -> {
                        val squadName = squadInfo?.get("name")?.toString() ?: "Mi Cuadrilla"
                        val squadMunicipality = squadInfo?.get("municipality")?.toString() ?: ""
                        val userRole = squadInfo?.get("userRole")?.toString() ?: "MEMBER"

                        SquadCard(
                            squadName = squadName,
                            municipality = squadMunicipality.ifBlank { "Morelos" },
                            role = when (userRole) {
                                "LEADER" -> "Líder"
                                "MEMBER" -> "Miembro"
                                else -> userRole
                            }
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

                        val filteredReports = if (selectedTab == 0) {
                            assignedReports.filter {
                                it.assignmentStatus != "COMPLETED" && it.assignmentStatus != "REJECTED"
                            }
                        } else {
                            assignedReports.filter {
                                it.assignmentStatus == "COMPLETED" || it.assignmentStatus == "REJECTED"
                            }
                        }

                        if (filteredReports.isEmpty()) {
                            Text(
                                text = if (selectedTab == 0) "No hay reportes pendientes" else "No hay reportes resueltos",
                                color = TextSecondary,
                                style = AppTypography.BodySmall
                            )
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(filteredReports) { report ->
                                    AssignedReportCard(
                                        username = report.citizenUsername,
                                        createdAt = report.reportCreatedAt,
                                        status = report.reportStatus,
                                        title = report.title,
                                        address = report.address,
                                        description = report.description,
                                        currentVotes = 0,
                                        totalVotes = 0,
                                        imageUrls = report.photos.map { it.filePath },
                                        onClick = { navController.navigate("${Routes.REPORTDETAILS}/${report.assignmentId}") },
                                        onAccept = { viewModel.voteReport(report.assignmentId, "ACCEPT") },
                                        onReject = { viewModel.voteReport(report.assignmentId, "REJECT") }
                                    )
                                }
                                item { Spacer(modifier = Modifier.height(16.dp)) }
                            }
                        }
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
                viewModel.applyAsVolunteer()
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
                viewModel.leaveSquad("") // TODO: ask for password
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