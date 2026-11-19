package mx.habil.ejemplo.tienda

import android.app.Activity
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

// La tercera puerta de la app: la que sale al navegador para entrar con un
// proveedor que Firebase atiende por la web, como Microsoft.
// Es una interfaz de tres funciones, con su implementación de verdad aquí
// abajo. Las pruebas usan otra, de mentira (DoblesDeMicrosoft.kt): así
// EntradaConMicrosoft se prueba sin navegador, sin cuenta y sin red.
// Este archivo viene dado (andamiaje): no lo cambies.

interface NavegadorDeProveedor {
    // ¿Quedó una entrada a medias? Pasa cuando Android cerró la app mientras
    // la persona estaba en el navegador. Si quedó, regresa el nombre de quien
    // entró, o lanza la excepción con la que esa entrada terminó. Si no
    // quedó nada, regresa null. Firebase entrega ese resultado una sola vez.
    suspend fun pendiente(): String?

    // Abre el navegador con la página del proveedor y espera a que la
    // persona termine. Regresa su nombre, o lanza una excepción de Firebase.
    suspend fun abrir(proveedor: OAuthProvider): String

    // Abre el navegador para sumarle ese proveedor al usuario que YA entró.
    // Si nadie ha entrado, lanza NadieHaEntrado sin abrir nada. Si la persona
    // no termina, lanza una excepción de Firebase.
    suspend fun enlazar(proveedor: OAuthProvider)
}

// Lo que lanza la puerta cuando le piden enlazar y no hay ningún usuario.
class NadieHaEntrado : Exception("Nadie ha entrado: no hay usuario al cual enlazarle un proveedor")

// La puerta de verdad: Firebase Authentication. Necesita la Activity porque
// el navegador se abre encima de ella.
// Regla de datos: de lo que contesta Firebase solo sale el nombre. El
// resultado trae además un token de acceso de Microsoft; aquí no se lee, no
// se imprime y no se guarda.
// Recorte didáctico: la guía de Firebase pide no usar la Activity dentro de
// los avisos de éxito y de falla, porque se sueltan cuando el navegador toma
// la pantalla. Estos avisos no la usan: solo despiertan a la función que
// estaba esperando. Si Android cierra la app, esa espera se pierde con ella,
// y el resultado se recoge después con pendiente().
class NavegadorConFirebase(private val actividad: Activity) : NavegadorDeProveedor {

    override suspend fun pendiente(): String? {
        val tarea = FirebaseAuth.getInstance().pendingAuthResult ?: return null
        return suspendCancellableCoroutine { continuacion ->
            tarea
                .addOnSuccessListener { resultado -> continuacion.resume(nombreDe(resultado)) }
                .addOnFailureListener { falla -> continuacion.resumeWithException(falla) }
        }
    }

    override suspend fun abrir(proveedor: OAuthProvider): String =
        suspendCancellableCoroutine { continuacion ->
            FirebaseAuth.getInstance().startActivityForSignInWithProvider(actividad, proveedor)
                .addOnSuccessListener { resultado -> continuacion.resume(nombreDe(resultado)) }
                .addOnFailureListener { falla -> continuacion.resumeWithException(falla) }
        }

    override suspend fun enlazar(proveedor: OAuthProvider) {
        val usuario = FirebaseAuth.getInstance().currentUser ?: throw NadieHaEntrado()
        suspendCancellableCoroutine { continuacion ->
            usuario.startActivityForLinkWithProvider(actividad, proveedor)
                .addOnSuccessListener { continuacion.resume(Unit) }
                .addOnFailureListener { falla -> continuacion.resumeWithException(falla) }
        }
    }

    private fun nombreDe(resultado: AuthResult): String = resultado.user?.displayName ?: "cliente"
}
