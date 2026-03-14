package mx.edu.utez.fmc_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import mx.edu.utez.fmc_mobile.navigation.AppNavigation
import mx.edu.utez.fmc_mobile.ui.theme.FMC_MobileTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            FMC_MobileTheme(darkTheme = false) {
                AppNavigation()
            }
        }
    }
}