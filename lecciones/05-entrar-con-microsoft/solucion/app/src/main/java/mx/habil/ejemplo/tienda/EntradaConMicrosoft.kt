package mx.habil.ejemplo.tienda

import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWebException
import com.google.firebase.auth.OAuthProvider
import kotlinx.coroutines.CancellationException

// La segunda Entrada de verdad: el botón «Entrar con Microsoft».
// Recibe una sola cosa y no sabe de dónde sale:
// - navegador: la puerta hacia el navegador y Firebase.
// No recibe ningún ID de cliente ni ninguna clave: los dos viven en la
// consola de Firebase, que es quien habla con Microsoft.
class EntradaConMicrosoft(
    private val navegador: NavegadorDeProveedor,
) : EntradaQueSeRetoma {

    // Al abrir la app: si Android la cerró a media entrada, aquí se recoge
    // cómo terminó. Si no había nada a medias, regresa null y no abre nada.
    override suspend fun retomar(): Resultado? =
        try {
            val nombre = navegador.pendiente()
            if (nombre == null) null else Resultado.Entro(nombre)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            resultadoDe(e)
        }

    // Al tocar el botón.
    override suspend fun entrar(): Resultado =
        try {
            // 1. La solicitud: el proveedor «microsoft.com», pidiendo que la
            //    persona elija con qué cuenta entra.
            val proveedor = OAuthProvider.newBuilder("microsoft.com")
                .addCustomParameter("prompt", "select_account")
                .build()
            // 2. Primero se pregunta si ya hay una respuesta esperando. Solo
            //    si no la hay (null) se abre el navegador.
            val nombre = navegador.pendiente() ?: navegador.abrir(proveedor)
            Resultado.Entro(nombre)
        } catch (e: CancellationException) {
            // No es una falla de la entrada: es Kotlin avisando que esta
            // tarea se canceló. Se deja pasar.
            throw e
        } catch (e: Exception) {
            resultadoDe(e)
        }

    // Traduce cada forma de no entrar a su resultado. El orden importa: las
    // específicas van antes que el «todo lo demás».
    private fun resultadoDe(falla: Exception): Resultado = when {
        // Ese correo ya tiene usuario en tu proyecto, creado con otro proveedor.
        falla is FirebaseAuthUserCollisionException -> Resultado.CuentaConOtroProveedor
        // La persona cerró el navegador sin terminar.
        falla is FirebaseAuthWebException && falla.errorCode == "ERROR_WEB_CONTEXT_CANCELED" ->
            Resultado.Cancelada
        // Todo lo demás: sin red, proveedor mal configurado, otra falla web.
        else -> Resultado.Fallo
    }
}
