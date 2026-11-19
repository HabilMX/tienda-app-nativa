package mx.habil.ejemplo.tienda

// Lo que puede pasar cuando alguien pide entrar. Es una lista cerrada
// (sealed): el compilador sabe que no hay un sexto caso, y un when que
// los recorre no necesita «else».
sealed interface Resultado {
    // Entró: trae el nombre con el que la app saluda.
    data class Entro(val nombre: String) : Resultado

    // El teléfono no ofreció ninguna credencial con la cual entrar. No dice
    // por qué: puede no haber cuenta, o haber una que necesita volver a
    // iniciar sesión.
    data object SinCuentas : Resultado

    // La persona cerró la ventana sin elegir cuenta.
    data object Cancelada : Resultado

    // Cualquier otra falla: sin red, configuración incompleta, token rechazado.
    data object Fallo : Resultado

    // Nuevo en la lección 05: ese correo ya tiene usuario en la tienda, creado
    // con otro proveedor. Firebase no deja entrar con el segundo.
    data object CuentaConOtroProveedor : Resultado
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

// Nuevo en la lección 05: una Entrada que sale de la app para entrar (abre el
// navegador) y por eso puede quedarse a medias si Android cierra la app
// mientras tanto. Además de entrar(), sabe retomar.
interface EntradaQueSeRetoma : Entrada {
    // Se llama al abrir la app. Si quedó una entrada a medias, regresa cómo
    // terminó; si no quedó nada, regresa null.
    suspend fun retomar(): Resultado?
}

// Nuevo en la lección 05: lo que puede pasar cuando alguien que ya entró
// pide enlazar un segundo proveedor a su mismo usuario.
sealed interface ResultadoDeEnlace {
    // Quedó enlazado: desde ahora los dos botones llegan al mismo usuario.
    data object Enlazado : ResultadoDeEnlace

    // Nadie ha entrado todavía: no hay usuario al cual enlazarle nada.
    data object SinSesion : ResultadoDeEnlace

    // La persona cerró el navegador sin terminar.
    data object Cancelado : ResultadoDeEnlace

    // Cualquier otra falla.
    data object Fallo : ResultadoDeEnlace
}

// Enlazar un segundo proveedor al usuario que ya entró. La pantalla solo lo
// pide después de que la persona lo confirma.
interface Enlace {
    suspend fun enlazar(): ResultadoDeEnlace
}

// Las dos implementaciones que usa la app al empezar esta lección: el botón
// de Microsoft todavía no deja pasar a nadie, no hay nada que retomar y no se
// enlaza nada. Las cambias por EntradaConMicrosoft (paso 4) y por
// EnlaceConMicrosoft (paso 7), y entonces estas clases se borran.
class MicrosoftSinConectar : EntradaQueSeRetoma {
    override suspend fun retomar(): Resultado? = null
    override suspend fun entrar(): Resultado = Resultado.Fallo
}

class EnlaceSinConectar : Enlace {
    override suspend fun enlazar(): ResultadoDeEnlace = ResultadoDeEnlace.Fallo
}
