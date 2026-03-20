package mx.edu.utez.fmc_mobile.ui.screens.teams

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.fmc_mobile.ui.components.*
import mx.edu.utez.fmc_mobile.ui.theme.*

@Composable
fun ReportDetailsScreen(
    navController: NavController,
    assignmentId: Long = -1L,
    viewModel: ReportDetailsViewModel = viewModel()
) {

    var selectedStatus by remember { mutableStateOf("PENDING") }
    var images by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var observations by remember { mutableStateOf("") }
    var showSuccessSheet by remember { mutableStateOf(false) }

    val actionState by viewModel.actionState.collectAsState()
    val context = LocalContext.current

    val isCompleted = selectedStatus == "COMPLETED"

    LaunchedEffect(actionState) {
        if (actionState is ReportActionState.Success) {
            showSuccessSheet = true
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Reporte #$assignmentId",
                subtitle = "Detalle de reporte asignado",
                leadingIcon = Icons.Default.ArrowBackIosNew,
                onLeadingClick = { navController.popBackStack() },
                leadingIconTint = Primary
            )
        },
        bottomBar = { BottomNavBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Selecciona el estado de la denuncia",
                style = AppTypography.BodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = TextPrimary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            StatusSelector(
                selectedStatus = selectedStatus,
                onStatusSelected = { selectedStatus = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            LabelBadge(
                label = "Evidencia Fotográfica",
                badgeText = "Máx. 3",
            )

            Spacer(modifier = Modifier.height(12.dp))

            PhotoPicker(
                images = images,
                onImagesSelected = { images = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Observaciones finales",
                style = AppTypography.BodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = TextPrimary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = observations,
                onValueChange = { observations = it },
                placeholder = {
                    Text(
                        text = "Describa las acciones realizadas, materiales usados o cualquier inconveniente...",
                        style = AppTypography.BodySmall,
                        color = TextSecondary
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                maxLines = 6
            )

            Spacer(modifier = Modifier.height(12.dp))

            when (actionState) {
                is ReportActionState.Loading -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(12.dp))
                }
                is ReportActionState.Error -> {
                    Text(
                        text = (actionState as ReportActionState.Error).message,
                        color = Color.Red,
                        style = AppTypography.BodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                else -> {}
            }

            if (!isCompleted) {
                Text(
                    text = "Complete el reporte para habilitar la finalización",
                    style = AppTypography.Caption,
                    color = TextSecondary,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            PrimaryButton(
                text = if (actionState is ReportActionState.Loading) "Procesando..." else if (isCompleted) "Finalizar Reporte" else "Actualizar Estado",
                onClick = {
                    if (actionState !is ReportActionState.Loading && assignmentId > 0) {
                        if (isCompleted && images.size == 3) {
                            viewModel.closeWithEvidence(context, assignmentId, images, observations.ifBlank { null })
                        } else if (!isCompleted) {
                            viewModel.changeStatus(assignmentId, selectedStatus, observations.ifBlank { null })
                        }
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        tint = if (isCompleted) Color.White else TextSecondary
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showSuccessSheet) {
        SuccessBottomSheet(
            title = "¡Trabajo Completado Exitosamente!",
            message = "Has contribuido a mejorar la infraestructura de Morelos. ¡Excelente labor!",
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
                showSuccessSheet = false
                viewModel.resetState()
                navController.popBackStack()
            },
            onDismiss = {
                showSuccessSheet = false
                viewModel.resetState()
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun ReportDetailsScreenPreview() {
    FMC_MobileTheme {
        ReportDetailsScreen(navController = rememberNavController())
    }
}