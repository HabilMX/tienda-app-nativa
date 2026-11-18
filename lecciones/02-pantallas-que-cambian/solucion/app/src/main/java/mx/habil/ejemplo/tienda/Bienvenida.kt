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

// Primera pantalla del recorrido. Ya no guarda estado: solo avisa.
// alEntrar es una función que le pasa quien la llama (Tienda); la pantalla
// la ejecuta cuando alguien toca el botón y no sabe qué pasa después.
@Composable
fun Bienvenida(alEntrar: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("La tienda", style = MaterialTheme.typography.headlineLarge)
        Text("Tus órdenes, en tu bolsillo")
        Button(onClick = alEntrar) {
            Text("Entrar")
        }
    }
}
