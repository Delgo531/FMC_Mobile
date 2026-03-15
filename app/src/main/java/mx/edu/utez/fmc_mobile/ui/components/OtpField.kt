package mx.edu.utez.fmc_mobile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.Background
import mx.edu.utez.fmc_mobile.ui.theme.Primary
import mx.edu.utez.fmc_mobile.ui.theme.TextPrimary
import mx.edu.utez.fmc_mobile.ui.theme.Surface as SurfaceColor
import mx.edu.utez.fmc_mobile.ui.theme.TextSecondary

@Composable
fun OtpField(
    otpValue: String,
    onOtpChange: (String) -> Unit,
    length: Int = 4,
    modifier: Modifier = Modifier
) {
    val focusRequesters = remember { List(length) { FocusRequester() } }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(length) { index ->
            val char = if (index < otpValue.length) otpValue[index].toString() else ""

            Surface(
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 4.dp,
                color = Background
            ) {
                TextField(

                    value = char,
                    onValueChange = { newValue ->
                        val digit = newValue.filter { it.isDigit() }.take(1)
                        val newOtp = otpValue.toMutableList().apply {
                            while (size < length) add(' ')
                        }
                        if (digit.isEmpty()) {
                            newOtp[index] = ' '
                            if (index > 0) focusRequesters[index - 1].requestFocus()
                        } else {
                            newOtp[index] = digit[0]
                            if (index < length - 1) focusRequesters[index + 1].requestFocus()
                        }
                        onOtpChange(newOtp.joinToString("").trimEnd())
                    },
                    singleLine = true,
                    textStyle = AppTypography.Body.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = Primary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedContainerColor = SurfaceColor,
                        unfocusedContainerColor = SurfaceColor
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .size(60.dp)
                        .focusRequester(focusRequesters[index])
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OtpFieldPreview() {
    var otp by remember { mutableStateOf("") }
    OtpField(
        otpValue = otp,
        onOtpChange = { otp = it }
    )
}