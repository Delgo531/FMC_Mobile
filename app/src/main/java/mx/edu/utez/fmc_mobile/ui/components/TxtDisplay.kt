package mx.edu.utez.fmc_mobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.Background
import mx.edu.utez.fmc_mobile.ui.theme.Primary
import mx.edu.utez.fmc_mobile.ui.theme.StatusInProgress
import mx.edu.utez.fmc_mobile.ui.theme.Surface
import mx.edu.utez.fmc_mobile.ui.theme.TextPrimary
import mx.edu.utez.fmc_mobile.ui.theme.TextSecondary

@Composable
fun TxtDisplay(
    label: String,
    value: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
    ){

        Column(modifier = Modifier.padding(15.dp)) {
            Text(
                text = label,
                style = AppTypography.Body.copy(fontWeight = FontWeight.SemiBold),
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 0.dp,
                color = Surface,
                border = BorderStroke(1.dp, StatusInProgress)
            ) {
                TextField(
                    value = value,
                    onValueChange = {},
                    textStyle = AppTypography.Body,
                    leadingIcon = leadingIcon,
                    readOnly = true,
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        disabledTextColor = TextPrimary,
                        focusedIndicatorColor = Background,
                        unfocusedIndicatorColor = Background,
                        focusedContainerColor = Background,
                        unfocusedContainerColor = Background,
                        disabledContainerColor = Background
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

    }

}

@Preview(showBackground = false,)
@Composable
fun TxtDisplayPreview() {
    TxtDisplay(
        label = "Correo electrónico",
        value = "usuario@correo.com",
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Email",
                tint = Primary
            )
        }
    )
}