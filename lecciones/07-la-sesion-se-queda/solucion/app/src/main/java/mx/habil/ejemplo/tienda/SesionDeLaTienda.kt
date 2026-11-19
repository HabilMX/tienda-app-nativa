package mx.habil.ejemplo.tienda

import androidx.credentials.exceptions.ClearCredentialException
import kotlinx.coroutines.CancellationException

// La sesión de la tienda: le pregunta al teléfono a quién recuerda y, al
// cerrar, le pide que lo olvide.
// Recibe una sola cosa y no sabe de dónde sale:
// - recuerdo: la puerta hacia Firebase y Credential Manager.
class SesionDeLaTienda(
    private val recuerdo: RecuerdoDeLaSesion,
) : Sesion {

    // Al abrir la app: lo que diga el usuario actual de Firebase. No abre
    // ninguna ventana ni habla por la red.
    override fun quienSigueAdentro(): String? = recuerdo.usuarioActual()

    // Al tocar «Cerrar sesión». El orden es el de la guía de Firebase.
    override suspend fun cerrar() {
        // 1. Firebase olvida al usuario: desde aquí, nadie sigue adentro.
        recuerdo.olvidarEnFirebase()
        // 2. Se les avisa a los proveedores del teléfono, para que la próxima
        //    vez ofrezcan todas las cuentas. Si el aviso falla, la sesión ya
        //    quedó cerrada en el paso 1: no se truena la app por eso.
        try {
            recuerdo.olvidarEnElTelefono()
        } catch (e: CancellationException) {
            // No es una falla del aviso: es Kotlin cancelando la tarea. Se deja pasar.
            throw e
        } catch (e: ClearCredentialException) {
            // El aviso no se pudo dar. La sesión de Firebase ya está cerrada.
        }
    }
}
