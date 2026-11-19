package mx.habil.ejemplo.tienda

import com.google.firebase.auth.FirebaseAuthWebException
import com.google.firebase.auth.OAuthProvider
import kotlinx.coroutines.CancellationException

// Enlaza Microsoft al usuario que ya entró con otro proveedor.
// No crea un usuario ni cambia quién entró: le suma una forma de entrar.
class EnlaceConMicrosoft(
    private val enlazador: EnlazadorDeProveedor,
) : Enlace {

    // Al tocar «Enlazar Microsoft».
    override suspend fun enlazar(): ResultadoDeEnlace =
        try {
            val proveedor = OAuthProvider.newBuilder("microsoft.com")
                .addCustomParameter("prompt", "select_account")
                .build()
            enlazador.enlazar(proveedor)
            ResultadoDeEnlace.Enlazado
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            resultadoDe(e)
        }

    // Al abrir la app: si Android la cerró a medio enlace, aquí se recoge
    // cómo terminó. Es un resultado de enlace, no una entrada.
    override suspend fun retomar(): EnlaceRetomado? {
        val pendiente = enlazador.pendiente()
        if (pendiente == null) return null
        val resultado = if (pendiente.falla == null) ResultadoDeEnlace.Enlazado else resultadoDe(pendiente.falla)
        return EnlaceRetomado(pendiente.nombre, resultado)
    }

    private fun resultadoDe(falla: Exception): ResultadoDeEnlace = when {
        // Sin usuario no hay a quién enlazarle nada.
        falla is NadieHaEntrado -> ResultadoDeEnlace.SinSesion
        falla is FirebaseAuthWebException && falla.errorCode == "ERROR_WEB_CONTEXT_CANCELED" ->
            ResultadoDeEnlace.Cancelado
        else -> ResultadoDeEnlace.Fallo
    }
}
