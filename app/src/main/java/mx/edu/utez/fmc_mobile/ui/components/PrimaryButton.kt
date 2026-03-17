package mx.edu.utez.fmc_mobile.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
    onClick: () -> Unit,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        leadingIcon?.invoke()
        if (leadingIcon != null) Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = AppTypography.Body, fontWeight = FontWeight.SemiBold)
        if (trailingIcon != null) Spacer(modifier = Modifier.width(8.dp))
        trailingIcon?.invoke()
    }
}

@Preview
@Composable
fun PrimaryButtonPreview() {
    FMC_MobileTheme {
        PrimaryButton(
            text = "Iniciar Sesión",
            onClick = {}
        )
    }
}