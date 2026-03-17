package mx.edu.utez.fmc_mobile.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.TextPrimary
import mx.edu.utez.fmc_mobile.ui.theme.TextSecondary

@Composable
fun SimpleLabel(
    text: String,
    modifier: Modifier
) {

    Column(modifier = modifier) {
        Text(
            text = text,
            style = AppTypography.Body.copy(fontWeight = FontWeight.SemiBold),
            color = TextSecondary
        )
        Spacer(modifier = modifier.padding(vertical = 2.dp))
        Divider(
            color = TextSecondary,
            thickness = 2.dp
        )
    }


}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun SimpleLabelPreview() {
    SimpleLabel(
        text = "Reportes recientes",
        modifier = Modifier.fillMaxWidth()
    )
}