package mx.habil.ejemplo.tienda

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Tercera pantalla: a donde llega quien ya entró.
// Solo muestra el nombre que le pasan; no sabe de dónde salió.
// Nuevo en la lección 07: el botón «Cerrar sesión». Se dibuja solo si le pasan
// alCerrarSesion; con null, la pantalla es la de la lección 06.
@Composable
fun Inicio(nombre: String, alCerrarSesion: (() -> Unit)? = null) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Hola, $nombre", style = MaterialTheme.typography.headlineLarge)
        Text("Aquí verás tus órdenes")
        if (alCerrarSesion != null) {
            Button(onClick = alCerrarSesion) {
                Text("Cerrar sesión")
            }
        }
    }
}
