package mx.edu.utez.fmc_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import mx.edu.utez.fmc_mobile.data.session.LocalUserSession
import mx.edu.utez.fmc_mobile.data.session.UserRole
import mx.edu.utez.fmc_mobile.data.session.UserSession
import mx.edu.utez.fmc_mobile.navigation.AppNavigation
import mx.edu.utez.fmc_mobile.ui.theme.FMC_MobileTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Sesión por defecto: ciudadano sin cuadrilla. Al integrar login con API,
        // actualizar con role (y squad si aplica) desde AuthResponse o GET /me.
        val defaultSession = UserSession(role = UserRole.ADMIN, squad = null)

        setContent {
            FMC_MobileTheme(darkTheme = false) {
                CompositionLocalProvider(LocalUserSession provides defaultSession) {
                    AppNavigation()
                }
            }
        }
    }
}