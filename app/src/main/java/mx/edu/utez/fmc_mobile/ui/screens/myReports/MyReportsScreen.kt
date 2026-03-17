package mx.edu.utez.fmc_mobile.ui.screens.myReports

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.utez.fmc_mobile.data.remote.dto.response.ReportResponse
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

@Composable
fun MyReportsScreen(
    viewModel: MyReportsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val selectedReport by viewModel.selectedReport.collectAsState()
    var query by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Mis Reportes",
                    style = AppTypography.Subtitle.copy(fontWeight = FontWeight.SemiBold),
                    color = TextPrimary
                )
                Text(
                    text = "Historial",
                    style = AppTypography.BodySmall,
                    color = Primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Buscar mis reportes...",
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
                        cursorColor = Primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Background)
        ) {
            when (val s = state) {
                is MyReportsUiState.Idle, is MyReportsUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Primary)
                    }
                }

                is MyReportsUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = s.message,
                            style = AppTypography.Body,
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                is MyReportsUiState.Loaded -> {
                    val filtered = s.reports
                        .filter { it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }
                        .sortedByDescending { it.createdAt }

                    if (filtered.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Aún no has realizado alguna denuncia",
                                style = AppTypography.Body,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 88.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filtered, key = { it.id }) { report ->
                                MyReportCard(
                                    report = report,
                                    onClick = { viewModel.openReport(report) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedReport != null) {
        ReportDetailDialog(
            report = selectedReport!!,
            onDismiss = { viewModel.closeReportDialog() }
        )
    }
}

@Composable
private fun MyReportCard(
    report: ReportResponse,
    onClick: () -> Unit
) {
    val uiStatus = report.status.toUiStatus()

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
            verticalAlignment = androidx.compose.ui.Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        text = report.citizenUsername,
                        style = AppTypography.BodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = TextPrimary
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(uiStatus.bg)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = uiStatus.label,
                            style = AppTypography.Caption,
                            color = uiStatus.text
                        )
                    }
                }

                Text(
                    text = report.createdAt,
                    style = AppTypography.Caption,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = report.address,
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

private data class UiStatus(val label: String, val bg: Color, val text: Color)

private fun String.toUiStatus(): UiStatus {
    val normalized = trim().uppercase()
    return when (normalized) {
        "REGISTERED", "PENDING", "PENDIENTE", "PENDING_ACCEPTANCE" ->
            UiStatus("PENDIENTE", StatusPending, PendingText)
        "IN_PROGRESS", "EN_PROCESO", "EN ATENCIÓN", "IN_ATTENTION" ->
            UiStatus("EN PROCESO", StatusInProgress, InProgressText)
        "COMPLETED", "COMPLETADO", "CLOSED", "CERRADA" ->
            UiStatus("COMPLETADO", StatusCompleted, CompletedText)
        else ->
            UiStatus(normalized, StatusInProgress, InProgressText)
    }
}

@Composable
private fun ReportDetailDialog(
    report: ReportResponse,
    onDismiss: () -> Unit
) {
    val uiStatus = report.status.toUiStatus()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        },
        title = {
            Column {
                Text(
                    text = report.title,
                    style = AppTypography.Subtitle.copy(fontWeight = FontWeight.SemiBold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(uiStatus.bg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(uiStatus.label, style = AppTypography.Caption, color = uiStatus.text)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Fecha: ${report.createdAt}", style = AppTypography.Caption, color = TextSecondary)
                Text("Ubicación: ${report.address}", style = AppTypography.Caption, color = TextSecondary)
                Text(report.description, style = AppTypography.Body, color = TextPrimary)

                if (report.photos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Evidencia fotográfica",
                        style = AppTypography.BodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = TextPrimary
                    )
                    report.photos.take(3).forEach { photo ->
                        Text(
                            text = "• ${photo.filePath}",
                            style = AppTypography.BodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        },
        containerColor = Surface,
        tonalElevation = 2.dp
    )
}
