package mx.habil.ejemplo.tienda

import com.google.firebase.auth.OAuthProvider

// Doble de la puerta del enlace (PuertaDelEnlace.kt), para las pruebas. Vive
// solo en src/test. No abre ningún navegador ni toca ninguna cuenta.
// Guarda con qué proveedor le pidieron enlazar y contesta lo que le digas:
// - fallaAlEnlazar: la excepción con la que termina el enlace.
// - pendiente: el enlace que quedó a medias (null si ninguno).
class EnlazadorFalso(
    private val fallaAlEnlazar: Exception? = null,
    private val pendiente: EnlacePendiente? = null,
) : EnlazadorDeProveedor {
    val enlazados = mutableListOf<OAuthProvider>()
    var preguntasPorPendiente = 0

    override suspend fun enlazar(proveedor: OAuthProvider) {
        enlazados += proveedor
        if (fallaAlEnlazar != null) throw fallaAlEnlazar
    }

    override suspend fun pendiente(): EnlacePendiente? {
        preguntasPorPendiente++
        return pendiente
    }
}
