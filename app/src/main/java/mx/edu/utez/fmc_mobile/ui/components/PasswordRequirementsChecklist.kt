package mx.edu.utez.fmc_mobile.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.SuccessGreen
import mx.edu.utez.fmc_mobile.ui.theme.TextSecondary
import mx.edu.utez.fmc_mobile.utils.PasswordValidator

@Composable
fun PasswordRequirementsChecklist(
    password: String,
    modifier: Modifier = Modifier
) {
    val validation = remember(password) { PasswordValidator.evaluate(password) }

    Column(modifier = modifier.fillMaxWidth()) {
        RequirementItem(
            text = "Minimo 8 caracteres",
            isMet = validation.hasMinLength
        )
        RequirementItem(
            text = "La contrasena debe tener minimo una minuscula y una mayuscula.",
            isMet = validation.hasLowerAndUpper
        )
        RequirementItem(
            text = "La contrasena debe tener minimo un caracter especial.",
            isMet = validation.hasSpecialChar
        )
    }
}

@Composable
private fun RequirementItem(text: String, isMet: Boolean) {
    val color = if (isMet) SuccessGreen else TextSecondary

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = text,
            style = AppTypography.BodySmall.copy(fontWeight = FontWeight.Medium),
            color = color
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
}

