package mx.edu.utez.fmc_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.edu.utez.fmc_mobile.ui.theme.*
import mx.edu.utez.fmc_mobile.utils.formatApiDate

@Composable
fun NotificationCard(
    type: String,
    message: String,
    createdAt: String,
    read: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (read) Color.White else Light.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (read) Light.copy(alpha = 0.4f) else Light),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = type,
                    style = AppTypography.BodySmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = message,
                    style = AppTypography.Caption,
                    color = TextSecondary,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatApiDate(createdAt),
                    style = AppTypography.Overline,
                    color = TextSecondary
                )
            }

            if (!read) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Primary)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun NotificationCardPreview() {
    FMC_MobileTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            NotificationCard(
                type = "Reporte Asignado",
                message = "Se te ha asignado un nuevo reporte en Temixco.",
                createdAt = "2026-03-15T17:26:25.299875",
                read = false
            )
            NotificationCard(
                type = "Estado Actualizado",
                message = "Tu denuncia ha cambiado de estado a En Proceso.",
                createdAt = "2026-03-15T17:26:25.299875",
                read = true
            )
        }
    }
}