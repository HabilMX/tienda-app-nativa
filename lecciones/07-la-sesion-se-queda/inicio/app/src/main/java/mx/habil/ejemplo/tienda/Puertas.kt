package mx.habil.ejemplo.tienda

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

// Las dos puertas por las que la app sale a hablar con alguien de fuera.
// Cada una es una interfaz de una sola función, con una implementación de
// verdad aquí abajo. Las pruebas usan otras, de mentira, que viven en
// src/test: así EntradaConGoogle se prueba sin teléfono, sin cuenta y sin red.
// Este archivo viene dado (andamiaje): no lo cambies.

// Puerta 1: pedirle una credencial al teléfono.
interface SelectorDeCuentas {
    // Regresa lo que la persona eligió, o lanza una excepción de
    // Credential Manager (sin cuentas, cancelación u otra falla).
    suspend fun pedir(solicitud: GetCredentialRequest): GetCredentialResponse
}

// Puerta 2: entregarle el token de identidad a quien lo verifica.
interface VerificadorDeToken {
    // Regresa el nombre del usuario, o lanza una excepción si el token
    // no se aceptó.
    suspend fun verificar(tokenDeIdentidad: String): String
}

// La puerta 1 de verdad: Credential Manager, la pieza de Android que muestra
// la ventana para elegir cuenta. Necesita la Activity porque esa ventana se
// dibuja encima de ella.
// Recorte didáctico: la guía vigente de Android recomienda envolver la
// Activity en un MutableContextWrapper, para no retenerla si Android la
// vuelve a crear (por ejemplo, al girar el teléfono) mientras la ventana
// está abierta. Según las notas de versión, Credential Manager actualiza ese
// envoltorio por su cuenta desde la 1.7.0-alpha03; la serie fija la 1.6.0,
// así que aquí se le pasa la Activity directa.
class SelectorConCredentialManager(private val actividad: Activity) : SelectorDeCuentas {
    override suspend fun pedir(solicitud: GetCredentialRequest): GetCredentialResponse =
        CredentialManager.create(actividad).getCredential(context = actividad, request = solicitud)
}

// La puerta 2 de verdad: Firebase Authentication. Recibe el token de
// identidad de Google, lo manda a Firebase y, si Firebase lo acepta, regresa
// el nombre del usuario.
class VerificadorConFirebase : VerificadorDeToken {
    override suspend fun verificar(tokenDeIdentidad: String): String =
        // Firebase contesta después, por la red. Estas líneas convierten su
        // aviso («ya terminé» o «falló») en una función que se pausa y continúa.
        suspendCancellableCoroutine { continuacion ->
            val credencial = GoogleAuthProvider.getCredential(tokenDeIdentidad, null)
            FirebaseAuth.getInstance().signInWithCredential(credencial)
                .addOnSuccessListener { resultado ->
                    continuacion.resume(resultado.user?.displayName ?: "cliente")
                }
                .addOnFailureListener { falla -> continuacion.resumeWithException(falla) }
        }
}
