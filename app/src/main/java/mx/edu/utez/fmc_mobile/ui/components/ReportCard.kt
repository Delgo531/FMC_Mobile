package mx.edu.utez.fmc_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.edu.utez.fmc_mobile.ui.theme.*
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

private fun formatApiDate(raw: String): String {
    return try {
        val (datePart, timePart) = raw.split("T")
        val (y, m, d) = datePart.split("-")
        val hhmm = timePart.substring(0, 5)
        "$d-$m-$y - $hhmm"
    } catch (e: Exception) { raw }
}

@Composable
fun ReportCard(
    username: String,
    createdAt: String,
    status: String,
    title: String,
    address: String,
    description: String,
    imageUrls: List<String> = emptyList(),
    modifier: Modifier = Modifier
) {
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
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Header ──────────────────────────────────────────────
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
                        style = AppTypography.Body.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
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
                        style = AppTypography.Overline.copy(
                            fontWeight = FontWeight.Bold,
                            color = badgeTextColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Título + Dirección + Descripción ─────────────────────
            Text(
                text = title,
                style = AppTypography.BodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Text(
                text = address,
                style = AppTypography.Caption.copy(color = Primary)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = AppTypography.BodySmall.copy(color = TextSecondary),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Carrusel ─────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.Center
            ) {
                if (imageUrls.isEmpty()) {
                    Text(
                        text = "Sin imágenes",
                        style = AppTypography.BodySmall.copy(color = TextSecondary)
                    )
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
        }
    }
}