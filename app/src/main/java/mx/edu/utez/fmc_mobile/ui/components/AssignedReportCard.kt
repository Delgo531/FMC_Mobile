package mx.edu.utez.fmc_mobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import mx.edu.utez.fmc_mobile.ui.theme.*
import mx.edu.utez.fmc_mobile.utils.formatApiDate

private fun computeRemainingMinutes(assignedAt: String): Long {
    return try {
        val assigned = LocalDateTime.parse(assignedAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSS][.SSS]"))
        val deadline = assigned.plusMinutes(30)
        // Use device local time — the backend stores LocalDateTime.now() in server-local time,
        // so both sides are naive datetimes in the same timezone and are directly comparable.
        val now = LocalDateTime.now()
        ChronoUnit.MINUTES.between(now, deadline).coerceAtLeast(0)
    } catch (_: Exception) { -1L }
}

@Composable
fun AssignedReportCard(
    username: String,
    createdAt: String,
    status: String,
    assignmentStatus: String = "",
    assignedAt: String = "",
    title: String,
    address: String,
    description: String,
    currentVotes: Int,
    totalVotes: Int,
    leaderAccepted: Boolean = false,
    imageUrls: List<String> = emptyList(),
    isLeader: Boolean = false,
    onClick: () -> Unit = {},
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPendingVote = assignmentStatus.equals("PENDING_VOTE", ignoreCase = true)
    val isAccepted = assignmentStatus.equals("ACCEPTED", ignoreCase = true)
    val remainingMinutes = remember(assignedAt) {
        if (isPendingVote && assignedAt.isNotBlank()) computeRemainingMinutes(assignedAt) else -1L
    }
    val (badgeBackground, badgeTextColor) = when (status.uppercase()) {
        "REGISTERED"   -> StatusPending           to PendingText
        "PENDING_VOTE" -> StatusPending           to PendingText
        "ACCEPTED"     -> Color(0xFFD1FAE5)       to Color(0xFF065F46)
        "ON_THE_WAY"   -> StatusInProgress        to InProgressText
        "IN_PROGRESS"  -> StatusInProgress        to InProgressText
        "CLOSED"       -> Color(0xFFD1FAE5)       to Color(0xFF065F46)
        "REJECTED"     -> FondoError              to Color(0xFFC62828)
        "COMPLETED"    -> StatusCompleted         to Color(0xFF6B21A8)
        else           -> Color(0xFFEEEEEE)       to TextSecondary
    }

    val statusLabel = when (status.uppercase()) {
        "REGISTERED"   -> "REGISTRADO"
        "PENDING_VOTE" -> "EN VOTACIÓN"
        "ACCEPTED"     -> "ACEPTADO"
        "ON_THE_WAY"   -> "EN CAMINO"
        "IN_PROGRESS"  -> "EN PROCESO"
        "CLOSED"       -> "CERRADO"
        "REJECTED"     -> "RECHAZADO"
        "COMPLETED"    -> "COMPLETADO"
        else           -> status
    }

    var currentImage by remember { mutableIntStateOf(0) }
    var showRejectDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(Light),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = username,
                        style = AppTypography.Body.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.AccessTime,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatApiDate(createdAt),
                            style = AppTypography.Caption.copy(color = TextSecondary)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(badgeBackground)
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = statusLabel,
                        style = AppTypography.Overline.copy(fontWeight = FontWeight.Bold, color = badgeTextColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.Center
            ) {
                if (imageUrls.isEmpty()) {
                    Text(text = "Sin imágenes", style = AppTypography.BodySmall.copy(color = TextSecondary))
                } else {
                    AsyncImage(
                        model = imageUrls[currentImage],
                        contentDescription = "Foto del reporte",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                if (imageUrls.size > 1) {
                    Icon(
                        imageVector = Icons.Filled.ChevronLeft,
                        contentDescription = "Anterior",
                        tint = TextSecondary,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 8.dp)
                            .size(28.dp)
                            .clickable { if (currentImage > 0) currentImage-- }
                    )
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Siguiente",
                        tint = TextSecondary,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                            .size(28.dp)
                            .clickable { if (currentImage < imageUrls.size - 1) currentImage++ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = title, style = AppTypography.BodySmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
            Text(text = address, style = AppTypography.Caption.copy(color = Primary))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, style = AppTypography.BodySmall.copy(color = TextSecondary), maxLines = 3)

            Spacer(modifier = Modifier.height(12.dp))

            if (isPendingVote && remainingMinutes >= 0) {
                val timerColor = if (remainingMinutes <= 5) Color(0xFFC62828) else Primary
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.HourglassEmpty,
                        contentDescription = null,
                        tint = timerColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (remainingMinutes == 0L) "¡Tiempo agotado!"
                               else "Tiempo para votar: ${remainingMinutes} min",
                        style = AppTypography.Caption.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = timerColor
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (isPendingVote) {
                val volunteerVotes = (if (leaderAccepted) currentVotes - 1 else currentVotes).coerceAtLeast(0)

                // ── Voto del Líder ───────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = if (leaderAccepted) Color(0xFFF59E0B) else TextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Voto del Líder",
                            style = AppTypography.Caption.copy(color = TextSecondary)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (leaderAccepted) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                        }
                        Text(
                            text = if (leaderAccepted) "Aprobado" else "Pendiente",
                            style = AppTypography.Caption.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (leaderAccepted) Color(0xFF059669) else Color(0xFFF59E0B)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // ── Votos de Voluntarios ──────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Group,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Votos de voluntarios",
                            style = AppTypography.Caption.copy(color = TextSecondary)
                        )
                    }
                    Text(
                        text = "$volunteerVotes / 2",
                        style = AppTypography.Caption.copy(fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    )
                }
            }

            if (isPendingVote) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isLeader) {
                        OutlinedButton(
                            onClick = { showRejectDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, TextSecondary),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Surface, contentColor = TextSecondary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Filled.ThumbDown, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Rechazar", style = AppTypography.BodySmall.copy(fontWeight = FontWeight.SemiBold))
                        }
                    }

                    Button(
                        onClick = onAccept,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = if (isLeader) Modifier.weight(1f) else Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Filled.ThumbUp, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Aceptar", style = AppTypography.BodySmall.copy(fontWeight = FontWeight.SemiBold))
                    }
                }

                if (showRejectDialog) {
                    AlertDialog(
                        onDismissRequest = { showRejectDialog = false },
                        title = {
                            Text(
                                text = "¿Rechazar reporte?",
                                style = AppTypography.Body.copy(fontWeight = FontWeight.Bold)
                            )
                        },
                        text = {
                            Text(
                                text = "Esta acción rechazará el reporte \"$title\". ¿Estás seguro de que deseas continuar?",
                                style = AppTypography.BodySmall
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showRejectDialog = false
                                    onReject()
                                }
                            ) {
                                Text("Sí, rechazar", color = Color(0xFFC62828))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showRejectDialog = false }) {
                                Text("Cancelar", color = TextSecondary)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun AssignedReportCardPreview() {
    FMC_MobileTheme {
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
            onAccept = {},
            onReject = {}
        )
    }
}