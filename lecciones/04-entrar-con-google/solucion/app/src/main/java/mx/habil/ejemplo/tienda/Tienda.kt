package mx.habil.ejemplo.tienda

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

// Las tres pantallas del recorrido. Un enum es una lista cerrada de valores.
enum class Pantalla { BIENVENIDA, ENTRADA, INICIO }

// Lo que la app le dice a la persona cuando no entró: un texto por caso.
// Si la persona entró no hay mensaje (null).
fun mensajeDe(resultado: Resultado): String? = when (resultado) {
    is Resultado.Entro -> null
    Resultado.SinCuentas ->
        "No encontré una cuenta de Google disponible para entrar. Agrega una o vuelve a iniciar sesión en Google y prueba otra vez."
    Resultado.Cancelada -> "Cerraste la ventana sin elegir cuenta. Toca el botón cuando quieras intentar otra vez."
    Resultado.Fallo -> "No se pudo entrar. Revisa tu conexión y vuelve a intentar."
}

// La raíz de la app: guarda el estado y decide qué pantalla se dibuja.
// Recibe la Entrada como parámetro: no sabe cuál implementación le tocó.
@Composable
fun Tienda(entrada: Entrada) {
    // Estado: en qué pantalla estamos. Cuando cambia, Compose vuelve a
    // ejecutar esta función y el when de abajo elige otra pantalla.
    var pantalla by remember { mutableStateOf(Pantalla.BIENVENIDA) }
    // Estado: quién entró (null mientras nadie haya entrado).
    var nombre by remember { mutableStateOf<String?>(null) }
    // Estado: qué decirle a la persona si ya intentó y no entró.
    var mensaje by remember { mutableStateOf<String?>(null) }
    // Donde corren las tareas que se pausan (suspend). Vive mientras esta
    // pantalla exista: si la pantalla se va, sus tareas se cancelan.
    val alcance = rememberCoroutineScope()

    when (pantalla) {
        Pantalla.BIENVENIDA -> Bienvenida(
            alEntrar = { pantalla = Pantalla.ENTRADA }
        )
        Pantalla.ENTRADA -> PantallaEntrada(
            mensaje = mensaje,
            alEntrarConGoogle = {
                // La decisión se pide aquí, en el evento: nunca al dibujar.
                // launch arranca la tarea y regresa de inmediato: la pantalla
                // sigue viva mientras la persona elige su cuenta.
                alcance.launch {
                    val resultado = entrada.entrar()
                    if (resultado is Resultado.Entro) {
                        nombre = resultado.nombre
                        pantalla = Pantalla.INICIO
                    } else {
                        mensaje = mensajeDe(resultado)
                    }
                }
            }
        )
        Pantalla.INICIO -> Inicio(nombre = nombre ?: "")
    }
}
