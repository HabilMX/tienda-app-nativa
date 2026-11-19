package mx.habil.ejemplo.tienda

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Tercera pantalla: a donde llega quien ya entró.
// Solo muestra el nombre que le pasan; no sabe de dónde salió.
@Composable
fun Inicio(nombre: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Hola, $nombre", style = MaterialTheme.typography.headlineLarge)
        Text("Aquí verás tus órdenes")
    }
}
