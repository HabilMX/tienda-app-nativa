package mx.habil.ejemplo.tienda

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.firebase.auth.AuthResult
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

// Nuevo en la lección 08: lo que una puerta contesta cuando alguien entró.
// El nombre, y si Firebase acaba de crear a ese usuario. Nada más: ni tokens
// ni datos del proveedor salen de la puerta.
data class QuienEntro(val nombre: String, val primeraVez: Boolean)

// Puerta 2: entregarle el token de identidad a quien lo verifica.
interface VerificadorDeToken {
    // Regresa el nombre del usuario, o lanza una excepción si el token
    // no se aceptó.
    suspend fun verificar(tokenDeIdentidad: String): String

    // Nuevo en la lección 08: lo mismo, y además si es la primera vez.
    // Trae un cuerpo por omisión que contesta «no es la primera vez»: así los
    // dobles de la 04 a la 07, que solo saben verificar(), siguen sirviendo.
    // La puerta de verdad (abajo) lo reemplaza y sí lo averigua.
    suspend fun verificarConPrimeraVez(tokenDeIdentidad: String): QuienEntro =
        QuienEntro(verificar(tokenDeIdentidad), primeraVez = false)
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
// Desde la lección 08 sabe contestar de dos formas, y las dos piden lo mismo
// a Firebase: verificar() solo el nombre, como en la 04; verificarConPrimeraVez()
// además le pregunta a TU función esPrimeraVez (PrimeraVez.kt) si el usuario es nuevo.
class VerificadorConFirebase : VerificadorDeToken {
    override suspend fun verificar(tokenDeIdentidad: String): String =
        nombreDe(entrarEnFirebase(tokenDeIdentidad))

    override suspend fun verificarConPrimeraVez(tokenDeIdentidad: String): QuienEntro {
        val resultado = entrarEnFirebase(tokenDeIdentidad)
        return QuienEntro(nombreDe(resultado), esPrimeraVez(resultado))
    }

    private fun nombreDe(resultado: AuthResult): String = resultado.user?.displayName ?: "cliente"

    private suspend fun entrarEnFirebase(tokenDeIdentidad: String): AuthResult =
        // Firebase contesta después, por la red. Estas líneas convierten su
        // aviso («ya terminé» o «falló») en una función que se pausa y continúa.
        suspendCancellableCoroutine { continuacion ->
            val credencial = GoogleAuthProvider.getCredential(tokenDeIdentidad, null)
            FirebaseAuth.getInstance().signInWithCredential(credencial)
                .addOnSuccessListener { resultado -> continuacion.resume(resultado) }
                .addOnFailureListener { falla -> continuacion.resumeWithException(falla) }
        }
}
