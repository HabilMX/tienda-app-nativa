package mx.habil.ejemplo.tienda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

// La puerta de entrada de la app. Android crea esta clase al abrir la app
// y setContent le dice qué función dibuja la pantalla.
// En la lección 02 cambias una sola línea de este archivo: la de setContent,
// en el paso 2 y otra vez en el paso 3.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Bienvenida() }
    }
}
