package mx.edu.utez.fmc_mobile.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.Background
import mx.edu.utez.fmc_mobile.ui.theme.CompletedText
import mx.edu.utez.fmc_mobile.ui.theme.FondoError
import mx.edu.utez.fmc_mobile.ui.theme.Primary
import mx.edu.utez.fmc_mobile.ui.theme.Surface
import mx.edu.utez.fmc_mobile.ui.theme.TextPrimary
import mx.edu.utez.fmc_mobile.ui.theme.TextSecondary

@Composable
fun TxtField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeHolder: String = "",

    leadingIcon: @Composable (() -> Unit)? = null,
    errorMessage: String? = null,
    readOnly: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Label
        Text(
            text = label,
            style = AppTypography.Body.copy(fontWeight = FontWeight.SemiBold),
            color = if (errorMessage != null) Color.Red else TextSecondary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 4.dp,
            color = Background
        ) {
            TextField(
                textStyle = AppTypography.Body,
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeHolder, color = TextSecondary) },
                leadingIcon = leadingIcon,
                singleLine = true,
                readOnly = readOnly,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = if (readOnly) TextSecondary else TextPrimary,
                    unfocusedTextColor = if (readOnly) TextSecondary else TextPrimary,
                    cursorColor = if (errorMessage != null) CompletedText else Primary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = if (readOnly) Background else if (errorMessage != null) FondoError else Surface,
                    unfocusedContainerColor = if (readOnly) Background else if (errorMessage != null) FondoError else Surface
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }


    }
}

@Composable
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
fun TxtFieldPreview() {
    var email by remember { mutableStateOf("test@correo.com") }

    TxtField(
        value = email,
        onValueChange = { email = it },
        label = "Correo electrónico",
        placeHolder = "usuario@correo.com",
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Email",
                tint = Primary
            )
        }
    )
}