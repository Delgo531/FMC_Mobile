package mx.edu.utez.fmc_mobile.ui.screens.reports

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.fmc_mobile.ui.components.AppTopBar
import mx.edu.utez.fmc_mobile.ui.components.LabelBadge
import mx.edu.utez.fmc_mobile.ui.components.LabelWithBadge
import mx.edu.utez.fmc_mobile.ui.components.PhotoPicker
import mx.edu.utez.fmc_mobile.ui.components.PrimaryButton
import mx.edu.utez.fmc_mobile.ui.components.SuccessBottomSheet
import mx.edu.utez.fmc_mobile.ui.theme.FMC_MobileTheme
import mx.edu.utez.fmc_mobile.ui.theme.Primary
import mx.edu.utez.fmc_mobile.ui.theme.Surface

@Composable
fun NewReportScreen(navController: NavController) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var images by remember { mutableStateOf<List<Uri>>(emptyList()) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Nuevo Reporte",
                leadingIcon = Icons.Default.ArrowBackIosNew,
                onLeadingClick = { navController.popBackStack() }
            )
        }
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

            LabelBadge(label = "Fotos", badgeText = "Max. 3")

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                PhotoPicker(
                    images = images,
                    onImagesSelected = { images = it }
                )
            }


            Spacer(modifier = Modifier.height(25.dp))

            LabelWithBadge(
                label = "Descripción del problema",
                value = "Describe brevemente el problema..."
            )

            Spacer(modifier = Modifier.height(25.dp))

            LabelWithBadge(
                label = "Ubicación",
                value = "Ej. Av. Plan de Ayala 123, Cuernavaca, CP 62000",
                badgeText = "Municipio Registrado",
                badgeIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationCity,
                        contentDescription = null,
                        tint = Primary
                    )
                }
            )

            Spacer(modifier = Modifier.height(25.dp))

            PrimaryButton(
                text = "Enviar Reporte",
                onClick = { showBottomSheet = true },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        tint = Surface
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
    if (showBottomSheet) {
        SuccessBottomSheet(
            title = "¡ Reporte Enviado !",
            message = "Tu reporte ha sido registrado exitosamente. Las autoridades y las brigadas cercanas en Morelos han sido notificadas para su revisión inmediata.",
            buttonText = "Volver al inicio",
            secondaryButtonText = "Ver mis reportes",
            icon = {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(14.dp)
                )
            },
            onButtonClick = { showBottomSheet = false },
            onSecondaryButtonClick = { showBottomSheet = false },
            onDismiss = { showBottomSheet = false }
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun NewReportScreenPreview() {
    FMC_MobileTheme {
        NewReportScreen(navController = rememberNavController())
    }
}