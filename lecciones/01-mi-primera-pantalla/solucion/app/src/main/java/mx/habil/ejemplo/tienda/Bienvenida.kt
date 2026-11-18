package mx.habil.ejemplo.tienda

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Lección 01, resuelta: la pantalla de bienvenida de la tienda.
// Una pantalla es una función que describe lo que se ve.
@Composable
fun Bienvenida() {
    // Estado: un dato que la pantalla recuerda. Cuando cambia, Compose
    // vuelve a correr la función y la pantalla se redibuja.
    var saludando by remember { mutableStateOf(false) }

    // Column acomoda lo que lleva dentro, uno debajo del otro.
    Column(
        // Modificador: ocupa toda la pantalla y deja 24 dp de margen.
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Texto: el nombre, con la letra grande del tema.
        Text("La tienda", style = MaterialTheme.typography.headlineLarge)
        Text("Tus órdenes, en tu bolsillo")
        // Evento: lo que pasa al tocar. Aquí solo cambia el estado.
        Button(onClick = { saludando = true }) {
            Text("Entrar")
        }
        if (saludando) {
            Text("¡Hola!")
        }
    }
}
