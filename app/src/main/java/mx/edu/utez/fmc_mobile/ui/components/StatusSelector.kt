package mx.edu.utez.fmc_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.edu.utez.fmc_mobile.ui.theme.*

private data class StatusOption(
    val key: String,
    val label: String,
    val icon: ImageVector
)

private val statusOptions = listOf(
    StatusOption("PENDING",    "Pendiente",  Icons.Filled.Schedule),
    StatusOption("ON_THE_WAY", "En Camino",  Icons.Filled.DirectionsCar),
    StatusOption("WORKING",    "Trabajando", Icons.Filled.Build),
    StatusOption("COMPLETED",  "Completado", Icons.Filled.CheckCircle)
)

@Composable
fun StatusSelector(
    selectedStatus: String,
    onStatusSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        statusOptions.forEach { option ->
            val isSelected = selectedStatus == option.key
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) Primary else Light.copy(alpha = 0.3f))
                    .clickable { onStatusSelected(option.key) }
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = option.label,
                    tint = if (isSelected) Color.White else Primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = option.label,
                    style = AppTypography.Overline.copy(fontWeight = FontWeight.SemiBold),
                    color = if (isSelected) Color.White else Primary
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun StatusSelectorPreview() {
    FMC_MobileTheme {
        var selected by remember { mutableStateOf("PENDING") }
        StatusSelector(
            selectedStatus = selected,
            onStatusSelected = { selected = it }
        )
    }
}