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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.fmc_mobile.navigation.Routes
import mx.edu.utez.fmc_mobile.ui.components.AppTopBar
import mx.edu.utez.fmc_mobile.ui.components.LabelBadge
import mx.edu.utez.fmc_mobile.ui.components.PhotoPicker
import mx.edu.utez.fmc_mobile.ui.components.PrimaryButton
import mx.edu.utez.fmc_mobile.ui.components.SuccessBottomSheet
import mx.edu.utez.fmc_mobile.ui.components.TxtField
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.FMC_MobileTheme
import mx.edu.utez.fmc_mobile.ui.theme.Primary
import mx.edu.utez.fmc_mobile.ui.theme.Surface
import mx.edu.utez.fmc_mobile.ui.theme.TextSecondary

@Composable
fun NewReportScreen(navController: NavController, viewModel: NewReportViewModel = viewModel()) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var images by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    val createState by viewModel.createState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(createState) {
        if (createState is CreateReportState.Success) {
            showBottomSheet = true
        }
    }

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

            TxtField(
                value = title,
                onValueChange = { title = it },
                label = "Título del reporte *",
                placeHolder = "Ej. Bache en avenida principal",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Title,
                        contentDescription = null,
                        tint = Primary
                    )
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            TxtField(
                value = description,
                onValueChange = { description = it },
                label = "Descripción del problema *",
                placeHolder = "Describe el problema con detalle",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = Primary
                    )
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            TxtField(
                value = address,
                onValueChange = { address = it },
                label = "Ubicación *",
                placeHolder = "Ej. Av. Plan de Ayala 123, Cuernavaca",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Primary
                    )
                }
            )

            Spacer(modifier = Modifier.height(25.dp))

            when (createState) {
                is CreateReportState.Loading -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = (createState as CreateReportState.Loading).message,
                        style = AppTypography.BodySmall,
                        color = TextSecondary
                    )
                }
                is CreateReportState.Error -> {
                    Text(
                        text = (createState as CreateReportState.Error).message,
                        color = Color.Red,
                        style = AppTypography.BodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                else -> {}
            }

            PrimaryButton(
                text = if (createState is CreateReportState.Loading) "Enviando..." else "Enviar Reporte",
                onClick = {
                    if (createState !is CreateReportState.Loading) {
                        viewModel.createReport(context, title, description, address, images)
                    }
                },
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
            onButtonClick = {
                showBottomSheet = false
                viewModel.resetState()
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.HOME) { inclusive = true }
                    launchSingleTop = true
                }
            },
            onSecondaryButtonClick = {
                showBottomSheet = false
                viewModel.resetState()
                navController.navigate(Routes.REPORTS) {
                    popUpTo(Routes.HOME) { inclusive = false }
                    launchSingleTop = true
                }
            },
            onDismiss = {
                showBottomSheet = false
                viewModel.resetState()
            }
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