package mx.habil.ejemplo.tienda

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

// Las tres pantallas del recorrido. Un enum es una lista cerrada de valores.
enum class Pantalla { BIENVENIDA, ENTRADA, INICIO }

// La raíz de la app: guarda el estado y decide qué pantalla se dibuja.
// Recibe la Entrada como parámetro: no sabe cuál implementación le tocó.
@Composable
fun Tienda(entrada: Entrada) {
    // Estado: en qué pantalla estamos. Cuando cambia, Compose vuelve a
    // ejecutar esta función y el when de abajo elige otra pantalla.
    var pantalla by remember { mutableStateOf(Pantalla.BIENVENIDA) }
    // Estado: quién entró (null mientras nadie haya entrado).
    var nombre by remember { mutableStateOf<String?>(null) }
    // Estado: ya se intentó entrar y no se pudo.
    var rechazada by remember { mutableStateOf(false) }

    when (pantalla) {
        Pantalla.BIENVENIDA -> Bienvenida(
            alEntrar = { pantalla = Pantalla.ENTRADA }
        )
        Pantalla.ENTRADA -> PantallaEntrada(
            rechazada = rechazada,
            alContinuar = {
                // La decisión se pide aquí, en el evento: nunca al dibujar.
                val quien = entrada.entrar()
                if (quien != null) {
                    nombre = quien
                    pantalla = Pantalla.INICIO
                } else {
                    rechazada = true
                }
            }
        )
        Pantalla.INICIO -> Inicio(nombre = nombre ?: "")
    }
}
