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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.fmc_mobile.ui.components.*
import mx.edu.utez.fmc_mobile.ui.theme.*

@Composable
fun ReportDetailsScreen(navController: NavController) {

    var selectedStatus by remember { mutableStateOf("PENDING") }
    var images by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var observations by remember { mutableStateOf("") }
    var showSuccessSheet by remember { mutableStateOf(false) }

    val isCompleted = selectedStatus == "COMPLETED"

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Reporte #1",
                subtitle = "Calle Principal #123, Temixco",
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
                text = "Finalizar Reporte",
                onClick = { if (isCompleted) showSuccessSheet = true },
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
                navController.popBackStack()
            },
            onDismiss = { showSuccessSheet = false }
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