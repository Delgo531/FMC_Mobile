package mx.edu.utez.fmc_mobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.Light
import mx.edu.utez.fmc_mobile.ui.theme.Primary
import mx.edu.utez.fmc_mobile.ui.theme.TextPrimary

@Composable
fun InfoCard(
    title: String,
    message: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Light.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Primary.copy(alpha = 0.4f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            icon()

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    style = AppTypography.BodySmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = message,
                    style = AppTypography.BodySmall,
                    color = TextPrimary
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun InfoCardPreview() {
    InfoCard(
        title = "Aviso de Privacidad:",
        message = "Tu privacidad es nuestra prioridad. No recolectamos datos personales adicionales. Tus reportes son anónimos ante otros ciudadanos.",
        icon = {
            Icon(
                imageVector = Icons.Default.VerifiedUser,
                contentDescription = null,
                tint = Primary
            )
        }
    )
}