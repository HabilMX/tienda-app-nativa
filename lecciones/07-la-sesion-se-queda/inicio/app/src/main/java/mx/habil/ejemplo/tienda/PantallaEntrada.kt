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

// Segunda pantalla: donde la persona pide entrar.
// No decide si entra: avisa con alEntrarConGoogle y muestra lo que le digan.
// mensaje llega con texto cuando ya se intentó y no se pudo; si no, es null.
// Nuevo en la lección 05: el segundo botón. Se dibuja solo si le pasan
// alEntrarConMicrosoft; con null, la pantalla es la de la lección 04.
@Composable
fun PantallaEntrada(
    mensaje: String?,
    alEntrarConGoogle: () -> Unit,
    alEntrarConMicrosoft: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Entra a tu cuenta", style = MaterialTheme.typography.headlineLarge)
        Button(onClick = alEntrarConGoogle) {
            Text("Entrar con Google")
        }
        if (alEntrarConMicrosoft != null) {
            Button(onClick = alEntrarConMicrosoft) {
                Text("Entrar con Microsoft")
            }
        }
        if (mensaje != null) {
            Text(mensaje)
        }
    }
}
