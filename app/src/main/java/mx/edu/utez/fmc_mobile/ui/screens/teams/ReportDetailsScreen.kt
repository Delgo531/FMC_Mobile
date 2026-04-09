package mx.edu.utez.fmc_mobile.ui.screens.teams

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import mx.edu.utez.fmc_mobile.ui.components.*
import mx.edu.utez.fmc_mobile.ui.theme.*

// ── Status display metadata ────────────────────────────────────────────────────

private data class StatusDisplayInfo(
    val label: String,
    val icon: ImageVector,
    val color: Color,
    val description: String
)

private fun statusDisplayInfo(status: String): StatusDisplayInfo = when (status) {
    "ACCEPTED"    -> StatusDisplayInfo("Pendiente",   Icons.Filled.Schedule,      Color(0xFFF59E0B), "El reporte ha sido aceptado por la cuadrilla")
    "ON_THE_WAY"  -> StatusDisplayInfo("En Camino",   Icons.Filled.DirectionsCar, Color(0xFF3B82F6), "La cuadrilla se dirige al lugar del reporte")
    "IN_PROGRESS" -> StatusDisplayInfo("En Progreso", Icons.Filled.Build,          Color(0xFF8B5CF6), "La cuadrilla está trabajando en el reporte")
    "CLOSED"      -> StatusDisplayInfo("Cerrado",     Icons.Filled.CheckCircle,   Color(0xFF10B981), "El reporte ha sido cerrado exitosamente")
    else          -> StatusDisplayInfo("Rechazado",   Icons.Filled.Close,         Color(0xFFEF4444), "El reporte no pudo ser atendido")
}

// Fixed linear progression — no going back, no skipping
private fun nextStatusOf(current: String): String? = when (current) {
    "ACCEPTED"   -> "ON_THE_WAY"
    "ON_THE_WAY" -> "IN_PROGRESS"
    else         -> null   // IN_PROGRESS closes via closeWithEvidence; CLOSED/REJECTED are terminal
}

private fun advanceButtonLabel(current: String): String = when (current) {
    "ACCEPTED"   -> "Marcar como En Camino"
    "ON_THE_WAY" -> "Marcar como En Progreso"
    else         -> "Avanzar estado"
}

// ── Screen ─────────────────────────────────────────────────────────────────────

@Composable
fun ReportDetailsScreen(
    navController: NavController,
    assignmentId: Long = -1L,
    reportStatus: String = "ACCEPTED",
    userRole: String = "MEMBER",
    viewModel: ReportDetailsViewModel = viewModel()
) {
    val context = LocalContext.current
    val actionState by viewModel.actionState.collectAsState()

    // Evidence state — only used when closing (IN_PROGRESS)
    var images by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var comments by remember { mutableStateOf("") }
    var showSuccessSheet by remember { mutableStateOf(false) }

    val isLeader   = userRole == "LEADER"
    val isClosing  = reportStatus == "IN_PROGRESS"
    val isFinished = reportStatus == "CLOSED" || reportStatus == "REJECTED"

    val currentInfo = statusDisplayInfo(reportStatus)
    val nextStatus  = nextStatusOf(reportStatus)
    val nextInfo    = when {
        nextStatus != null -> statusDisplayInfo(nextStatus)
        isClosing          -> statusDisplayInfo("CLOSED")
        else               -> null
    }

    LaunchedEffect(actionState) {
        if (actionState is ReportActionState.Success) showSuccessSheet = true
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
                .verticalScroll(rememberScrollState())
        ) {
            // ── Status banner ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(currentInfo.color.copy(alpha = 0.1f))
                    .padding(vertical = 20.dp, horizontal = 24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(currentInfo.color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = currentInfo.icon,
                            contentDescription = null,
                            tint = currentInfo.color,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Estado actual",
                            style = AppTypography.Caption,
                            color = TextSecondary
                        )
                        Text(
                            text = currentInfo.label,
                            style = AppTypography.Body.copy(fontWeight = FontWeight.Bold),
                            color = currentInfo.color
                        )
                        Text(
                            text = currentInfo.description,
                            style = AppTypography.Caption,
                            color = TextSecondary
                        )
                    }
                }
            }

            // ── Body ───────────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Member notice (read-only)
                if (!isLeader && !isFinished) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "Solo el líder de la cuadrilla puede actualizar el estado del reporte.",
                                style = AppTypography.BodySmall,
                                color = TextPrimary
                            )
                        }
                    }
                }

                // Terminal state card (CLOSED / REJECTED)
                if (isFinished) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = currentInfo.color.copy(alpha = 0.08f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = currentInfo.icon,
                                contentDescription = null,
                                tint = currentInfo.color,
                                modifier = Modifier.size(56.dp)
                            )
                            Text(
                                text = currentInfo.label,
                                style = AppTypography.Body.copy(fontWeight = FontWeight.Bold),
                                color = currentInfo.color,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = currentInfo.description,
                                style = AppTypography.BodySmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // ── Transition card (ACCEPTED → ON_THE_WAY, ON_THE_WAY → IN_PROGRESS) ──
                if (isLeader && !isClosing && !isFinished && nextInfo != null) {
                    Text(
                        text = "Avanzar estado del reporte",
                        style = AppTypography.BodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = TextPrimary
                    )

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Current state
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .background(currentInfo.color.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = currentInfo.icon,
                                            contentDescription = null,
                                            tint = currentInfo.color,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                    Text(
                                        text = currentInfo.label,
                                        style = AppTypography.Caption.copy(fontWeight = FontWeight.SemiBold),
                                        color = currentInfo.color,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.ArrowForwardIos,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )

                                // Next state
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .background(nextInfo.color.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = nextInfo.icon,
                                            contentDescription = null,
                                            tint = nextInfo.color,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                    Text(
                                        text = nextInfo.label,
                                        style = AppTypography.Caption.copy(fontWeight = FontWeight.SemiBold),
                                        color = nextInfo.color,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Text(
                                text = nextInfo.description,
                                style = AppTypography.BodySmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    if (actionState is ReportActionState.Error) {
                        Text(
                            text = (actionState as ReportActionState.Error).message,
                            color = Color.Red,
                            style = AppTypography.BodySmall
                        )
                    }

                    if (actionState is ReportActionState.Loading) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Primary)
                        }
                    } else {
                        PrimaryButton(
                            text = advanceButtonLabel(reportStatus),
                            onClick = {
                                if (nextStatus != null && assignmentId > 0) {
                                    viewModel.changeStatus(assignmentId, nextStatus, null)
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowForwardIos,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // ── Close report form (IN_PROGRESS, leader only) ───────────────
                if (isLeader && isClosing) {
                    Text(
                        text = "Cierre de Reporte",
                        style = AppTypography.Body.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "Para cerrar el reporte debes subir exactamente 3 fotos de evidencia del trabajo realizado.",
                        style = AppTypography.BodySmall,
                        color = TextSecondary
                    )

                    LabelBadge(
                        label = "Evidencia Fotográfica",
                        badgeText = "${images.size}/3 requeridas"
                    )

                    PhotoPicker(
                        images = images,
                        onImagesSelected = { images = it }
                    )

                    Text(
                        text = "Comentarios del cierre",
                        style = AppTypography.BodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = TextPrimary
                    )

                    OutlinedTextField(
                        value = comments,
                        onValueChange = { comments = it },
                        placeholder = {
                            Text(
                                text = "Describa las acciones realizadas, materiales usados o cualquier observación...",
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

                    if (images.size < 3) {
                        Text(
                            text = "Faltan ${3 - images.size} foto(s) para poder cerrar el reporte",
                            style = AppTypography.Caption,
                            color = Color(0xFFF59E0B)
                        )
                    }

                    if (actionState is ReportActionState.Error) {
                        Text(
                            text = (actionState as ReportActionState.Error).message,
                            color = Color.Red,
                            style = AppTypography.BodySmall
                        )
                    }

                    if (actionState is ReportActionState.Loading) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Primary)
                        }
                    } else {
                        PrimaryButton(
                            text = if (images.size == 3) "Cerrar Reporte con Evidencia"
                                   else "Sube ${3 - images.size} foto(s) más para continuar",
                            onClick = {
                                if (images.size == 3 && assignmentId > 0) {
                                    viewModel.closeWithEvidence(
                                        context, assignmentId, images, comments.ifBlank { null }
                                    )
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (images.size == 3) Icons.Default.Check else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showSuccessSheet) {
        SuccessBottomSheet(
            title = if (isClosing) "¡Reporte Cerrado Exitosamente!" else "¡Estado Actualizado!",
            message = if (isClosing)
                "Has contribuido a mejorar la infraestructura de Morelos. ¡Excelente labor!"
            else
                "El estado del reporte ha sido actualizado correctamente.",
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
