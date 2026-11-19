package mx.habil.ejemplo.tienda

import android.app.Activity
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

// La puerta por la que la app sale al navegador para ENLAZAR: sumarle un
// proveedor al usuario que ya entró. Es otra puerta, no la de entrar
// (PuertaDelNavegador.kt), porque contesta otra cosa.
// Este archivo viene dado (andamiaje): no lo cambies.

interface EnlazadorDeProveedor {
    // Abre el navegador para sumarle ese proveedor al usuario que YA entró.
    // Si nadie ha entrado, lanza NadieHaEntrado sin abrir nada. Si la persona
    // no termina, lanza una excepción de Firebase.
    suspend fun enlazar(proveedor: OAuthProvider)

    // ¿Quedó un enlace a medias? Pasa cuando Android cerró la app mientras
    // la persona estaba en el navegador, enlazando. Si quedó, regresa de
    // quién era y, si terminó mal, con qué excepción. Si no, regresa null.
    suspend fun pendiente(): EnlacePendiente?
}

// falla es null cuando el enlace que quedó a medias terminó bien.
class EnlacePendiente(val nombre: String, val falla: Exception?)

// Lo que lanza la puerta cuando le piden enlazar y no hay ningún usuario.
class NadieHaEntrado : Exception("Nadie ha entrado: no hay usuario al cual enlazarle un proveedor")

// La nota que distingue las dos operaciones. Firebase entrega lo que quedó a
// medias por el mismo lugar (pendingAuthResult), sea una entrada o un enlace,
// y no dice cuál fue. Por eso, antes de abrir el navegador para enlazar, la
// app deja anotado en el teléfono «hay un enlace en curso». La nota es un sí
// o un no: no guarda cuentas, tokens ni claves.
private const val NOTAS = "puerta-del-enlace"
private const val ENLACE_EN_CURSO = "enlace_en_curso"

fun hayEnlaceEnCurso(contexto: Context): Boolean =
    contexto.getSharedPreferences(NOTAS, Context.MODE_PRIVATE).getBoolean(ENLACE_EN_CURSO, false)

fun anotaEnlaceEnCurso(contexto: Context, enCurso: Boolean) {
    // commit() escribe antes de seguir: la nota tiene que estar guardada
    // cuando el navegador tome la pantalla.
    contexto.getSharedPreferences(NOTAS, Context.MODE_PRIVATE).edit().putBoolean(ENLACE_EN_CURSO, enCurso).commit()
}

// La puerta de verdad: Firebase Authentication.
class EnlazadorConFirebase(private val actividad: Activity) : EnlazadorDeProveedor {

    override suspend fun enlazar(proveedor: OAuthProvider) {
        val usuario = FirebaseAuth.getInstance().currentUser ?: throw NadieHaEntrado()
        anotaEnlaceEnCurso(actividad, true)
        try {
            suspendCancellableCoroutine { continuacion ->
                usuario.startActivityForLinkWithProvider(actividad, proveedor)
                    .addOnSuccessListener { continuacion.resume(Unit) }
                    .addOnFailureListener { falla -> continuacion.resumeWithException(falla) }
            }
        } finally {
            // Si la app sigue viva, el enlace ya terminó aquí mismo: se borra
            // la nota. Si Android cerró la app, esta línea no llega a correr
            // y la nota se queda para pendiente().
            anotaEnlaceEnCurso(actividad, false)
        }
    }

    override suspend fun pendiente(): EnlacePendiente? {
        if (!hayEnlaceEnCurso(actividad)) return null
        anotaEnlaceEnCurso(actividad, false)
        val firebase = FirebaseAuth.getInstance()
        val tarea = firebase.pendingAuthResult ?: return null
        val nombre = firebase.currentUser?.displayName ?: "cliente"
        return suspendCancellableCoroutine { continuacion ->
            tarea
                .addOnSuccessListener { continuacion.resume(EnlacePendiente(nombre, null)) }
                .addOnFailureListener { falla -> continuacion.resume(EnlacePendiente(nombre, falla)) }
        }
    }
}
