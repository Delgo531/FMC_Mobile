package mx.edu.utez.fmc_mobile.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    onLeadingClick: () -> Unit = {},
    trailingIcon: ImageVector? = null,
    onTrailingClick: () -> Unit = {},
    leadingIconTint: Color = Primary,
    trailingIconTint: Color = Primary,
) {
    TopAppBar(
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = AppTypography.Subtitle.copy(fontWeight = FontWeight.SemiBold)
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = AppTypography.Body.copy(fontWeight = FontWeight.Medium),
                        color = Primary
                    )
                }
            }
        },
        navigationIcon = {
            if (leadingIcon != null) {
                IconButton(onClick = onLeadingClick) {
                    Icon(imageVector = leadingIcon, contentDescription = title, tint = leadingIconTint)
                }
            } else {
                Box(modifier = Modifier.size(48.dp))
            }
        },
        actions = {
            if (trailingIcon != null) {
                IconButton(onClick = onTrailingClick) {
                    Icon(imageVector = trailingIcon, contentDescription = null, tint = trailingIconTint)
                }
            } else {
                Box(modifier = Modifier.size(48.dp))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AppTopBarPreview() {
    AppTopBar(
        title = "Inicio",
        subtitle = "Bienvenido",
        leadingIcon = Icons.Default.ArrowBackIosNew,
        onLeadingClick = {},
        trailingIcon = Icons.Default.Notifications,
        onTrailingClick = {}
    )
}