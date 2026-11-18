package mx.habil.ejemplo.tienda

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Lección 01: la pantalla de bienvenida de la tienda.
// Así viene de inicio. En el paso 1 predices qué dibuja; en el paso 3 la
// cambias para que cumpla el boceto: nombre, lema y botón.
@Composable
fun Bienvenida() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Aquí va tu pantalla")
        Text("Lección 01")
    }
}
