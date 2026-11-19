package mx.habil.ejemplo.tienda

import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWebException
import com.google.firebase.auth.OAuthProvider

// Doble de la tercera puerta (PuertaDelNavegador.kt), para las pruebas. Vive
// en src/test, con una copia idéntica en src/androidTest para las pruebas
// instrumentadas: la app no puede usarlo. No abre ningún navegador, no toca
// ninguna cuenta y no sale a la red.
// Aquí no hay ningún token ni ninguna clave, ni inventados: esta puerta solo
// entrega un nombre, igual que la de verdad.

// Guarda con qué proveedor le pidieron abrir el navegador y cuántas veces le
// preguntaron por una entrada a medias, y contesta lo que le digas:
// - nombre: quién entra si se abre el navegador.
// - pendiente: quién quedó a medias (null si nadie).
// - fallaAlAbrir / fallaPendiente: la excepción con la que termina cada una.
class NavegadorFalso(
    private val nombre: String = "Ana",
    private val pendiente: String? = null,
    private val fallaAlAbrir: Exception? = null,
    private val fallaPendiente: Exception? = null,
) : NavegadorDeProveedor {
    val abiertos = mutableListOf<OAuthProvider>()
    var preguntasPorPendiente = 0

    override suspend fun pendiente(): String? {
        preguntasPorPendiente++
        if (fallaPendiente != null) throw fallaPendiente
        return pendiente
    }

    override suspend fun abrir(proveedor: OAuthProvider): String {
        abiertos += proveedor
        if (fallaAlAbrir != null) throw fallaAlAbrir
        return nombre
    }
}

// Las excepciones de Firebase con las que termina una entrada por la web,
// armadas a mano con textos de prueba. Ninguna vino de Firebase.

// La persona cerró el navegador sin terminar.
fun cancelacionInventada(): Exception =
    FirebaseAuthWebException("ERROR_WEB_CONTEXT_CANCELED", "cancelada, en la prueba")

// Otra falla del navegador que no es una cancelación.
fun otraFallaWebInventada(): Exception =
    FirebaseAuthWebException("ERROR_WEB_INTERNAL_ERROR", "falla web, en la prueba")

// Ese correo ya tiene usuario, creado con otro proveedor.
fun colisionInventada(): Exception =
    FirebaseAuthUserCollisionException(
        "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL", "ya existe con otro proveedor, en la prueba"
    )
