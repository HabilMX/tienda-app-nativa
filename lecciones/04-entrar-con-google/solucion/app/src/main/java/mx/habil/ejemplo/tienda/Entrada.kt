package mx.habil.ejemplo.tienda

// Lo que puede pasar cuando alguien pide entrar. Es una lista cerrada
// (sealed): el compilador sabe que no hay un quinto caso, y un when que
// los recorre no necesita «else».
sealed interface Resultado {
    // Entró: trae el nombre con el que la app saluda.
    data class Entro(val nombre: String) : Resultado

    // El teléfono no ofreció ninguna cuenta con la cual entrar.
    data object SinCuentas : Resultado

    // La persona cerró la ventana sin elegir cuenta.
    data object Cancelada : Resultado

    // Cualquier otra falla: sin red, configuración incompleta, token rechazado.
    data object Fallo : Resultado
}

// La decisión de entrar, separada de las pantallas (lección 02).
// Cambió en dos cosas respecto a la 02:
// - suspend: entrar ya no contesta al instante. Abre una ventana, espera a
//   que la persona elija y habla por la red; la función se pausa sin
//   congelar la pantalla y continúa cuando llega la respuesta.
// - Regresa un Resultado en lugar de «nombre o null»: un null no alcanzaba
//   para distinguir por qué no se entró.
interface Entrada {
    suspend fun entrar(): Resultado
}
