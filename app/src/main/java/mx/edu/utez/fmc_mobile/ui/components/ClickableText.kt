package mx.edu.utez.fmc_mobile.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.Primary

@Composable
fun ClickableText(
    text: String,
    onClick: ()-> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = AppTypography.Body.copy(fontWeight = FontWeight.SemiBold),
        color = Primary,
        modifier = modifier.clickable { onClick() }
    )

}


@Preview(showBackground = true)
@Composable
fun ClickableTextPreview() {
    ClickableText(
        text = "¿Olvidaste tu contraseña?",
        onClick = {}
    )
}