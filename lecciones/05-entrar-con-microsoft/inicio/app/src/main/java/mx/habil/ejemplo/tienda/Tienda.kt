package mx.habil.ejemplo.tienda

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    Resultado.CuentaConOtroProveedor ->
        "Ese correo ya entró a la tienda con otro botón. Entra con el que usaste la primera vez."
}

// La raíz de la app: guarda el estado y decide qué pantalla se dibuja.
// Recibe la Entrada como parámetro: no sabe cuál implementación le tocó.
// Nuevo en la lección 05: puede recibir una segunda entrada, la de Microsoft.
// Si no la recibe (null), no hay segundo botón y todo es como en la 04.
@Composable
fun Tienda(entrada: Entrada, entradaConMicrosoft: EntradaQueSeRetoma? = null) {
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

    // Qué hacer con lo que conteste cualquiera de las dos entradas.
    fun recibe(resultado: Resultado) {
        if (resultado is Resultado.Entro) {
            nombre = resultado.nombre
            pantalla = Pantalla.INICIO
        } else {
            mensaje = mensajeDe(resultado)
            pantalla = Pantalla.ENTRADA
        }
    }

    // Una sola vez, al abrirse la app: ¿quedó una entrada con Microsoft a
    // medias? LaunchedEffect corre su bloque cuando esta función aparece en
    // pantalla, no en cada redibujo.
    LaunchedEffect(entradaConMicrosoft) {
        val pendiente = entradaConMicrosoft?.retomar()
        if (pendiente != null) recibe(pendiente)
    }

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
                alcance.launch { recibe(entrada.entrar()) }
            },
            alEntrarConMicrosoft = entradaConMicrosoft?.let { conMicrosoft ->
                { alcance.launch { recibe(conMicrosoft.entrar()) } }
            }
        )
        Pantalla.INICIO -> Inicio(nombre = nombre ?: "")
    }
}
