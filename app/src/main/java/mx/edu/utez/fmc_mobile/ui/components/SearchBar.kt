package mx.edu.utez.fmc_mobile.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.Primary
import mx.edu.utez.fmc_mobile.ui.theme.Surface
import mx.edu.utez.fmc_mobile.ui.theme.TextPrimary
import mx.edu.utez.fmc_mobile.ui.theme.TextSecondary

@Composable
fun SearchBar(

    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier

) {

    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Busqueda...", color = TextSecondary, fontWeight = FontWeight.SemiBold) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = Primary
            )
        },
        singleLine = true,
        textStyle = AppTypography.Body,
        colors = TextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = Primary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = Surface,
            unfocusedContainerColor = Surface
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    )



}

@Preview
@Composable
fun SearchBarPreview() {
    var query by remember { mutableStateOf("") }
    SearchBar(
        value = query,
        onValueChange = { query = it }
    )
}