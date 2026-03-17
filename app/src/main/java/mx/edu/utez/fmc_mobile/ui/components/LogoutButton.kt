package mx.edu.utez.fmc_mobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.CompletedText
import mx.edu.utez.fmc_mobile.ui.theme.FondoError
import mx.edu.utez.fmc_mobile.ui.theme.Primary

@Composable
fun LogoutButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = FondoError,
            contentColor = CompletedText
        ),
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, CompletedText),

        ) {
        Icon(
            imageVector = Icons.Default.Logout,
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp),
            tint = CompletedText
        )
        Text(
            text = "Cerrar sesión",
            style = AppTypography.Body.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun LogoutButtonPreview() {
    LogoutButton(onClick = {})
}