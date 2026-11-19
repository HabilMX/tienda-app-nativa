package mx.habil.ejemplo.tienda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme

// La puerta de entrada de la app. Android crea esta clase al abrir la app
// y setContent le dice qué función dibuja la pantalla.
// Aquí se elige la implementación de Entrada que usa la app de verdad.
// Es el único lugar de la app que la nombra.
// Práctica 02-B: TemaTienda pone la paleta y la tipografía; claro u oscuro, según el teléfono.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TemaTienda(oscuro = isSystemInDarkTheme()) {
                Tienda(entrada = EntradaSinProveedor())
            }
        }
    }
}
