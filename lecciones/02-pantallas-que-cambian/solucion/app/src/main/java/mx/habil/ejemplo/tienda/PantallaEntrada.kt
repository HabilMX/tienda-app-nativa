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
// No decide si entra: avisa con alContinuar y muestra lo que le digan.
// rechazada llega en true cuando ya se intentó y no se pudo.
@Composable
fun PantallaEntrada(rechazada: Boolean, alContinuar: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Entra a tu cuenta", style = MaterialTheme.typography.headlineLarge)
        Button(onClick = alContinuar) {
            Text("Continuar")
        }
        if (rechazada) {
            Text("Todavía no se puede entrar")
        }
    }
}
