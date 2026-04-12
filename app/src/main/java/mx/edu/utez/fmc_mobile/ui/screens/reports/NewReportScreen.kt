package mx.edu.utez.fmc_mobile.ui.screens.reports

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocalPostOffice
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
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

    // Campos de ubicación (solo lectura, se llenan con GPS)
    var gpsMunicipality by remember { mutableStateOf("") }
    var gpsColony by remember { mutableStateOf("") }
    var gpsStreet by remember { mutableStateOf("") }
    var gpsPostalCode by remember { mutableStateOf("") }

    // Detalles adicionales de ubicación (opcional, editable)
    var locationDetails by remember { mutableStateOf("") }

    val createState by viewModel.createState.collectAsState()
    val locationState by viewModel.locationState.collectAsState()
    val titleError by viewModel.titleError.collectAsState()
    val descriptionError by viewModel.descriptionError.collectAsState()
    val locationError by viewModel.locationError.collectAsState()
    val imagesError by viewModel.imagesError.collectAsState()
    val context = LocalContext.current

    // Cuando el GPS obtiene la dirección, rellena los campos y limpia el error de ubicación
    LaunchedEffect(locationState) {
        if (locationState is LocationFetchState.Success) {
            val ls = locationState as LocationFetchState.Success
            gpsMunicipality = ls.municipality
            gpsColony = ls.colony
            gpsStreet = ls.street
            gpsPostalCode = ls.postalCode
            viewModel.clearLocationError()
        }
    }

    LaunchedEffect(createState) {
        if (createState is CreateReportState.Success) {
            showBottomSheet = true
        }
    }

    // Lanzador de permisos de ubicación
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.fetchLocation(context)
        } else {
            viewModel.resetLocationState()
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
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            LabelBadge(label = "Fotos", badgeText = "Max. 3")

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                PhotoPicker(
                    images = images,
                    onImagesSelected = {
                        images = it
                        if (it.isNotEmpty()) viewModel.clearImagesError()
                    }
                )
            }

            if (imagesError) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Debes agregar al menos una foto del problema",
                    color = Color.Red,
                    style = AppTypography.Caption,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            TxtField(
                value = title,
                onValueChange = {
                    title = it
                    if (it.length >= 5) viewModel.clearTitleError()
                },
                label = "Título del reporte *",
                placeHolder = "Ej. Bache en avenida principal",
                errorMessage = if (titleError) "" else null,
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
                onValueChange = {
                    description = it
                    if (it.length >= 20) viewModel.clearDescriptionError()
                },
                label = "Descripción del problema *",
                placeHolder = "Describe el problema con detalle",
                errorMessage = if (descriptionError) "" else null,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = Primary
                    )
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Sección de ubicación ───────────────────────────────────────

            TxtField(
                value = gpsMunicipality,
                onValueChange = {
                    gpsMunicipality = it
                    if (it.isNotBlank()) viewModel.clearLocationError()
                },
                label = "Municipio *",
                placeHolder = "Ej. Cuernavaca",
                errorMessage = if (locationError) "" else null,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationCity,
                        contentDescription = null,
                        tint = Primary
                    )
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            TxtField(
                value = gpsColony,
                onValueChange = { gpsColony = it },
                label = "Colonia",
                placeHolder = "Ej. Centro",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = Primary
                    )
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            TxtField(
                value = gpsStreet,
                onValueChange = { gpsStreet = it },
                label = "Calle",
                placeHolder = "Ej. Av. Morelos 123",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        tint = Primary
                    )
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            TxtField(
                value = gpsPostalCode,
                onValueChange = { gpsPostalCode = it },
                label = "Código postal",
                placeHolder = "Ej. 62000",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocalPostOffice,
                        contentDescription = null,
                        tint = Primary
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botón "Obtener dirección"
            OutlinedButton(
                onClick = {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },
                enabled = locationState !is LocationFetchState.Loading,
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (locationState is LocationFetchState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Obteniendo ubicación...",
                        style = AppTypography.BodySmall
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Obtener dirección",
                        style = AppTypography.BodySmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            // Feedback del GPS
            when (val ls = locationState) {
                is LocationFetchState.Success -> {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "✓ Dirección obtenida por GPS",
                        style = AppTypography.Caption,
                        color = Color(0xFF10B981),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                is LocationFetchState.Error -> {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ls.message,
                        style = AppTypography.Caption,
                        color = Color.Red,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Detalles adicionales de ubicación (opcional) ───────────────
            TxtField(
                value = locationDetails,
                onValueChange = { locationDetails = it },
                label = "Detalles de la ubicación (opcional)",
                placeHolder = "Ej. Frente al parque, entre calles...",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.EditNote,
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
                        viewModel.createReport(
                            context, title, description,
                            gpsMunicipality, gpsColony, gpsStreet, gpsPostalCode,
                            locationDetails, images
                        )
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
