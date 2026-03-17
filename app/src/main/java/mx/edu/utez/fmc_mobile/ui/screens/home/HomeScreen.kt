package mx.edu.utez.fmc_mobile.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.Background
import mx.edu.utez.fmc_mobile.ui.theme.CompletedText
import mx.edu.utez.fmc_mobile.ui.theme.InProgressText
import mx.edu.utez.fmc_mobile.ui.theme.PendingText
import mx.edu.utez.fmc_mobile.ui.theme.Primary
import mx.edu.utez.fmc_mobile.ui.theme.StatusCompleted
import mx.edu.utez.fmc_mobile.ui.theme.StatusInProgress
import mx.edu.utez.fmc_mobile.ui.theme.StatusPending
import mx.edu.utez.fmc_mobile.ui.theme.Surface
import mx.edu.utez.fmc_mobile.ui.theme.TextPrimary
import mx.edu.utez.fmc_mobile.ui.theme.TextSecondary

/** Estado de una denuncia según DFR (Registrada, Asignada, Pendiente, Aceptada, En atención, Cerrada, Rechazada). */
enum class ReportStatus(val label: String, val bg: Color, val text: Color) {
    PENDIENTE("PENDIENTE", StatusPending, PendingText),
    EN_PROCESO("EN PROCESO", StatusInProgress, InProgressText),
    COMPLETADO("COMPLETADO", StatusCompleted, CompletedText)
}

/** Modelo de tarjeta de reporte para el feed (Módulo 6 - Publicaciones / DFR). */
data class ReportFeedItem(
    val id: String,
    val title: String,
    val description: String,
    val location: String,
    val dateTime: String,
    val userName: String,
    val status: ReportStatus,
    val imageUrl: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    municipality: String = "Municipio",
    onSearchChange: (String) -> Unit = {},
    onReportClick: (ReportFeedItem) -> Unit = {},
    onCreateReportClick: () -> Unit = {},
    reports: List<ReportFeedItem> = emptyList()
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Reportes Morelos",
                    style = AppTypography.Subtitle.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = "Fix My City",
                    style = AppTypography.BodySmall,
                    color = Primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        onSearchChange(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Buscar reportes...",
                            style = AppTypography.Body,
                            color = TextSecondary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Primary
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f),
                        cursorColor = Primary,
                        focusedLeadingIconColor = Primary,
                        unfocusedLeadingIconColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = municipality,
                    style = AppTypography.Caption,
                    color = TextSecondary
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateReportClick,
                containerColor = Primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                content = {
                    Icon(Icons.Default.Add, contentDescription = "Crear Nuevo Reporte")
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Background)
        ) {
            if (reports.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No hay reportes en tu municipio",
                        style = AppTypography.Body,
                        color = TextSecondary
                    )
                    Text(
                        text = "Sé el primero en reportar una incidencia",
                        style = AppTypography.BodySmall,
                        color = TextSecondary
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 88.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(reports, key = { it.id }) { report ->
                        ReportCard(
                            report = report,
                            onClick = { onReportClick(report) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportCard(
    report: ReportFeedItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                // Placeholder para imagen; cuando la API esté lista se puede usar Coil/Glide
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = report.userName,
                        style = AppTypography.BodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = TextPrimary
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(report.status.bg)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = report.status.label,
                            style = AppTypography.Caption,
                            color = report.status.text
                        )
                    }
                }
                Text(
                    text = report.dateTime,
                    style = AppTypography.Caption,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = report.location,
                    style = AppTypography.Caption,
                    color = TextSecondary
                )
                Text(
                    text = report.description,
                    style = AppTypography.BodySmall,
                    color = TextPrimary,
                    maxLines = 2
                )
            }
        }
    }
}
