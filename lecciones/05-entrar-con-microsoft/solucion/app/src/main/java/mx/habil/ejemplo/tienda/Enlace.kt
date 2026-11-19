package mx.habil.ejemplo.tienda

// Lo que puede pasar cuando alguien que ya entró pide enlazar un segundo
// proveedor a su mismo usuario.
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

// Un enlace que quedó a medias y se recogió al abrir la app: de quién era
// y cómo terminó.
data class EnlaceRetomado(val nombre: String, val resultado: ResultadoDeEnlace)

// Enlazar un segundo proveedor al usuario que ya entró. La pantalla solo lo
// pide después de que la persona lo confirma.
interface Enlace {
    suspend fun enlazar(): ResultadoDeEnlace

    // Se llama al abrir la app. Si Android la cerró a medio enlace, regresa
    // cómo terminó; si no quedó ningún enlace a medias, regresa null.
    suspend fun retomar(): EnlaceRetomado?
}
