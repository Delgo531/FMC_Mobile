package mx.edu.utez.fmc_mobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.edu.utez.fmc_mobile.ui.theme.*

@Composable
fun SquadCard(
    squadName: String,
    municipality: String,
    role: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, TextSecondary.copy(alpha = 0.2f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = squadName,
                    style = AppTypography.BodySmall.copy(fontWeight = FontWeight.Bold),
                    color = TextSecondary
                )
                Text(
                    text = municipality,
                    style = AppTypography.Caption.copy(fontWeight = FontWeight.SemiBold),
                    color = Primary
                )
            }

            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(start = 8.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(50.dp),
                    colors = CardDefaults.cardColors(containerColor = Light.copy(alpha = 0.4f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    border = BorderStroke(1.dp, Primary.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = role.uppercase(),
                        style = AppTypography.Overline.copy(fontWeight = FontWeight.Bold),
                        color = Primary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun SquadCardPreview() {
    FMC_MobileTheme {
        SquadCard(
            squadName = "Nombre Cuadrilla",
            municipality = "Municipio, Morelos",
            role = "Líder"
        )
    }
}