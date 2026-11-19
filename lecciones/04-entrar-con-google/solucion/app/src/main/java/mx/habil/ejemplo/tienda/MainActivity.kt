package mx.habil.ejemplo.tienda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

// La puerta de entrada de la app. Android crea esta clase al abrir la app
// y setContent le dice qué función dibuja la pantalla.
// Aquí se arma la Entrada que usa la app de verdad, con sus tres piezas.
// Es el único lugar de la app que las nombra.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val entrada = EntradaConGoogle(
            // La audiencia: el ID de cliente web que el plugin google-services
            // sacó de tu google-services.json al compilar.
            idDeClienteWeb = getString(R.string.default_web_client_id),
            selector = SelectorConCredentialManager(this),
            verificador = VerificadorConFirebase(),
        )
        setContent { Tienda(entrada = entrada) }
    }
}
