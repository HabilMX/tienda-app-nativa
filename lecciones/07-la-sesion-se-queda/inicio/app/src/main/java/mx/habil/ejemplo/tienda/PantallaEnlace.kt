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

// Pantalla nueva de la lección 06: la confirmación. Aparece solo cuando la
// persona chocó con «ese correo ya entró con otro botón» y después entró con
// el botón correcto. No enlaza nada por su cuenta: pregunta y avisa.
// - enlazado: ya se hizo; solo queda continuar.
// - mensaje: llega con texto si se intentó y no se pudo.
@Composable
fun PantallaEnlace(
    enlazado: Boolean,
    mensaje: String?,
    alEnlazar: () -> Unit,
    alContinuar: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (enlazado) {
            Text("Listo", style = MaterialTheme.typography.headlineLarge)
            Text("Ya puedes entrar a esta misma cuenta con Google o con Microsoft.")
            Button(onClick = alContinuar) { Text("Continuar") }
        } else {
            Text("¿También con Microsoft?", style = MaterialTheme.typography.headlineLarge)
            Text("Tu correo ya tenía cuenta en la tienda. Si aceptas, vas a poder entrar a esta misma cuenta con cualquiera de los dos botones.")
            Button(onClick = alEnlazar) { Text("Enlazar Microsoft") }
            Button(onClick = alContinuar) { Text("Ahora no") }
            if (mensaje != null) {
                Text(mensaje)
            }
        }
    }
}
