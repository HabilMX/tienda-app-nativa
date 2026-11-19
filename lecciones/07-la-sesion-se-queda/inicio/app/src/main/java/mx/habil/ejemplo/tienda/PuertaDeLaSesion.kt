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
    // Manager) que la persona salió de esta app. Puede lanzar una excepción
    // de Credential Manager si el aviso no se pudo dar.
    suspend fun olvidarEnElTelefono()
}

// La puerta de verdad.
// Regla de datos: del usuario actual solo sale el nombre. Firebase guarda
// además sus tokens; aquí no se leen, no se imprimen y no se guardan.
class RecuerdoConFirebase(private val actividad: Activity) : RecuerdoDeLaSesion {

    override fun usuarioActual(): String? {
        // currentUser: el usuario que Firebase recuerda en este teléfono.
        val usuario = FirebaseAuth.getInstance().currentUser ?: return null
        return usuario.displayName ?: "cliente"
    }

    override fun olvidarEnFirebase() {
        // Según la referencia de Firebase, signOut() cierra la sesión del
        // usuario actual y lo borra del caché en disco.
        FirebaseAuth.getInstance().signOut()
    }

    override suspend fun olvidarEnElTelefono() {
        // Según la referencia de Credential Manager, borra el estado de la
        // credencial del usuario en todos los proveedores, para que la próxima
        // vez ofrezcan todas las opciones para entrar.
        CredentialManager.create(actividad).clearCredentialState(ClearCredentialStateRequest())
    }
}
