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

// Lección 02: así viene de inicio. Es la bienvenida que dejó la lección 01.
// En el paso 1 predices qué hace su estado; en el paso 2 le quitas el saludo
// y le das un parámetro, para que avise cuando alguien toca «Entrar».
@Composable
fun Bienvenida() {
    // Estado: un dato que la pantalla recuerda y que puede cambiar.
    var saludando by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("La tienda", style = MaterialTheme.typography.headlineLarge)
        Text("Tus órdenes, en tu bolsillo")
        Button(onClick = { saludando = true }) {
            Text("Entrar")
        }
        if (saludando) {
            Text("¡Hola!")
        }
    }
}
