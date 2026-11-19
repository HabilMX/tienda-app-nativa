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
// Es una interfaz de dos funciones, con su implementación de verdad aquí
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

    // Nuevas en la lección 08: las mismas dos, y además si es la primera vez
    // (QuienEntro vive en Puertas.kt). Por omisión contestan «no es la
    // primera vez»: así los dobles de la 05 a la 07 siguen sirviendo. La
    // puerta de verdad (abajo) las reemplaza y sí lo averigua.
    suspend fun pendienteConPrimeraVez(): QuienEntro? =
        pendiente()?.let { QuienEntro(it, primeraVez = false) }

    suspend fun abrirConPrimeraVez(proveedor: OAuthProvider): QuienEntro =
        QuienEntro(abrir(proveedor), primeraVez = false)
}

// La puerta de verdad: Firebase Authentication. Necesita la Activity porque
// el navegador se abre encima de ella.
// Regla de datos: de lo que contesta Firebase solo sale el nombre y, desde la
// lección 08, si es la primera vez. El resultado trae además un token de
// acceso de Microsoft; aquí no se lee, no se imprime y no se guarda.
// Recorte didáctico: la guía de Firebase pide no usar la Activity dentro de
// los avisos de éxito y de falla, porque se sueltan cuando el navegador toma
// la pantalla. Estos avisos no la usan: solo despiertan a la función que
// estaba esperando. Si Android cierra la app, esa espera se pierde con ella,
// y el resultado se recoge después con pendiente().
class NavegadorConFirebase(private val actividad: Activity) : NavegadorDeProveedor {

    override suspend fun pendiente(): String? = resultadoPendiente()?.let(::nombreDe)

    override suspend fun abrir(proveedor: OAuthProvider): String = nombreDe(abrirEnFirebase(proveedor))

    // Lección 08: el resultado que Firebase entrega al retomar es un AuthResult,
    // como el de abrir(); a los dos se les pregunta lo mismo con TU esPrimeraVez.
    override suspend fun pendienteConPrimeraVez(): QuienEntro? =
        resultadoPendiente()?.let { QuienEntro(nombreDe(it), esPrimeraVez(it)) }

    override suspend fun abrirConPrimeraVez(proveedor: OAuthProvider): QuienEntro {
        val resultado = abrirEnFirebase(proveedor)
        return QuienEntro(nombreDe(resultado), esPrimeraVez(resultado))
    }

    private suspend fun resultadoPendiente(): AuthResult? {
        // Si lo que quedó a medias fue un enlace, no es una entrada: lo
        // recoge la otra puerta (PuertaDelEnlace.kt).
        if (hayEnlaceEnCurso(actividad)) return null
        val tarea = FirebaseAuth.getInstance().pendingAuthResult ?: return null
        return suspendCancellableCoroutine { continuacion ->
            tarea
                .addOnSuccessListener { resultado -> continuacion.resume(resultado) }
                .addOnFailureListener { falla -> continuacion.resumeWithException(falla) }
        }
    }

    private suspend fun abrirEnFirebase(proveedor: OAuthProvider): AuthResult =
        suspendCancellableCoroutine { continuacion ->
            FirebaseAuth.getInstance().startActivityForSignInWithProvider(actividad, proveedor)
                .addOnSuccessListener { resultado -> continuacion.resume(resultado) }
                .addOnFailureListener { falla -> continuacion.resumeWithException(falla) }
        }

    private fun nombreDe(resultado: AuthResult): String = resultado.user?.displayName ?: "cliente"
}
