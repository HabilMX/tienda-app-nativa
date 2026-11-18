package mx.habil.ejemplo.tienda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

// La puerta de entrada de la app. Android crea esta clase al abrir la app
// y setContent le dice qué función dibuja la pantalla.
// En la lección 01 no cambias este archivo: tu trabajo va en Bienvenida.kt.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Bienvenida() }
    }
}
