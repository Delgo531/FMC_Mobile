package mx.edu.utez.fmc_mobile.ui.screens.cuadrilla

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import mx.edu.utez.fmc_mobile.ui.components.PrimaryButton
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.Primary
import mx.edu.utez.fmc_mobile.ui.theme.TextPrimary
import mx.edu.utez.fmc_mobile.ui.theme.TextSecondary

/**
 * Contenido de la pestaña Cuadrilla para usuarios con rol CITIZEN que aún no están en una cuadrilla.
 * Muestra información sobre voluntarios y el botón "¡Quiero ser voluntario!".
 * La postulación real no se implementa hasta que el backend esté listo.
 */
@Composable
fun CitizenSquadContent(
    onWantToBeVolunteerClick: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Mi Cuadrilla",
                style = AppTypography.Subtitle,
                color = TextPrimary
            )

            Text(
                text = "Información sobre voluntarios: Los ciudadanos voluntarios son personas que ayudan a mantener esta aplicación funcionando. Si gustas unirte como voluntario, lo puedes hacer mediante el botón de \"Unirme\" en la esquina superior derecha o en el siguiente botón.",
                style = AppTypography.Body,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            PrimaryButton(
                text = "¡Quiero ser voluntario!",
                onClick = {
                    // Postulación no implementada aún (pendiente del backend)
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            "La postulación como voluntario estará disponible próximamente.",
                            withDismissAction = true
                        )
                    }
                }
            )
        }
    }
}
