package mx.habil.ejemplo.tienda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text

// Lección 00: solo comprueba que el ambiente compila y corre la app.
// La primera pantalla de verdad la escribes en la lección 01.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Text("La tienda corre en tu ambiente") }
    }
}
