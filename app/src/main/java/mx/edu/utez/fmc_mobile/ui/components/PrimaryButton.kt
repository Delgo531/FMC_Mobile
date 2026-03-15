package mx.edu.utez.fmc_mobile.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.FMC_MobileTheme

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit
) {


    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp)
        ) {
        Text(text, style = AppTypography.Body, fontWeight = FontWeight.SemiBold)
    }
}

@Preview
@Composable
fun Preview(){
    FMC_MobileTheme {
        PrimaryButton(
            text = "Iniciar Sesión",
            onClick = {}
        )
    }
}
