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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.edu.utez.fmc_mobile.ui.theme.*
import mx.edu.utez.fmc_mobile.utils.formatApiDate

private fun computeRemainingMinutes(assignedAt: String): Long {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
            .withZone(java.time.ZoneOffset.UTC)
        val assigned = LocalDateTime.parse(assignedAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSS][.SSS]"))
        val deadline = assigned.plusMinutes(30)
        val now = LocalDateTime.now(java.time.ZoneOffset.UTC)
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
    imageUrls: List<String> = emptyList(),
    onClick: () -> Unit = {},
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPendingVote = assignmentStatus.equals("PENDING_VOTE", ignoreCase = true)
    val remainingMinutes = remember(assignedAt) {
        if (isPendingVote && assignedAt.isNotBlank()) computeRemainingMinutes(assignedAt) else -1L
    }
    val (badgeBackground, badgeTextColor) = when (status.uppercase()) {
        "COMPLETED"  -> StatusCompleted  to CompletedText
        "REGISTERED" -> StatusPending    to PendingText
        "IN_PROCESS" -> StatusInProgress to InProgressText
        "REJECTED"   -> FondoError       to Color(0xFFC62828)
        else         -> Color(0xFFEEEEEE) to TextSecondary
    }

    val statusLabel = when (status.uppercase()) {
        "COMPLETED"  -> "COMPLETADO"
        "REGISTERED" -> "REGISTRADO"
        "IN_PROCESS" -> "EN PROCESO"
        "REJECTED"   -> "RECHAZADO"
        else         -> status
    }

    var currentImage by remember { mutableIntStateOf(0) }

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Progreso de votación", style = AppTypography.Caption.copy(color = TextSecondary))
                Text(
                    text = "$currentVotes / $totalVotes Votos",
                    style = AppTypography.Caption.copy(fontWeight = FontWeight.SemiBold, color = TextSecondary)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, TextSecondary),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Surface, contentColor = TextSecondary),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Filled.ThumbDown, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Rechazar", style = AppTypography.BodySmall.copy(fontWeight = FontWeight.SemiBold))
                }

                Button(
                    onClick = onAccept,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Filled.ThumbUp, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Aceptar", style = AppTypography.BodySmall.copy(fontWeight = FontWeight.SemiBold))
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