package mx.habil.ejemplo.tienda

import android.app.Activity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.google.firebase.auth.FirebaseAuth

// La puerta por la que la app le pregunta al teléfono a quién recuerda, y le
// pide que lo olvide. Es una interfaz de tres funciones, con su implementación
// de verdad aquí abajo. Las pruebas usan otra, de mentira (DoblesDeSesion.kt):
// así tu SesionDeLaTienda se prueba sin cuenta, sin Firebase y sin red.
// Este archivo viene dado (andamiaje): no lo cambies.

interface RecuerdoDeLaSesion {
    // El nombre del usuario actual de Firebase en este teléfono, o null si
    // Firebase no recuerda a nadie.
    fun usuarioActual(): String?

    // Que Firebase olvide al usuario actual: después, usuarioActual() da null.
    fun olvidarEnFirebase()

    // Avisarle a los proveedores de credenciales del teléfono (Credential
    // Manager) que la persona salió de esta app, para que limpien el estado
    // que hayan guardado para ella. Puede lanzar una excepción de Credential
    // Manager si el aviso no se pudo dar.
    suspend fun olvidarEnElTelefono()
}

// La puerta de verdad.
// Regla de datos: del usuario actual solo sale el nombre. Firebase guarda
// además sus tokens; aquí no se leen, no se imprimen y no se guardan.
// Recibe por parámetro con quién limpia lo que guarda Credential Manager, con
// su valor de verdad por omisión: la app la arma a secas, y una prueba puede
// pasarle un limpiador de mentira para ver que esta puerta sí lo llama.
class RecuerdoConFirebase(
    private val actividad: Activity,
    private val limpiador: LimpiadorDeCredenciales = LimpiadorConCredentialManager(actividad),
) : RecuerdoDeLaSesion {

    override fun usuarioActual(): String? {
        // currentUser: el usuario que Firebase recuerda en este teléfono.
        val usuario = FirebaseAuth.getInstance().currentUser ?: return null
        return usuario.displayName ?: "cliente"
    }

    override fun olvidarEnFirebase() {
        // Según la referencia de Firebase, signOut() cierra la sesión del
        // usuario actual y lo borra del caché en disco. Es una limpieza en el
        // teléfono: no revoca nada en el servidor de Firebase.
        FirebaseAuth.getInstance().signOut()
    }

    override suspend fun olvidarEnElTelefono() = limpiador.limpiar()
}

// La puerta hacia Credential Manager, por separado: así se puede ver, en una
// prueba, que la puerta de verdad de arriba la llama al cerrar sesión.
interface LimpiadorDeCredenciales {
    // Puede lanzar ClearCredentialException si el aviso no se pudo dar.
    suspend fun limpiar()
}

// El limpiador de verdad. Según la referencia de Credential Manager, pide a
// los proveedores de credenciales que borren el estado que hayan guardado para
// esta app; así pueden dejar de priorizar una credencial anterior. Qué
// opciones se muestran después lo decide cada proveedor.
class LimpiadorConCredentialManager(private val actividad: Activity) : LimpiadorDeCredenciales {
    override suspend fun limpiar() {
        CredentialManager.create(actividad).clearCredentialState(ClearCredentialStateRequest())
    }
}
