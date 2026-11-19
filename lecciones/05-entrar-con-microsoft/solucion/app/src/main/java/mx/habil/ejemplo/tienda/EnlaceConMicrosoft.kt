package mx.habil.ejemplo.tienda

import com.google.firebase.auth.FirebaseAuthWebException
import com.google.firebase.auth.OAuthProvider
import kotlinx.coroutines.CancellationException

// Enlaza Microsoft al usuario que ya entró con otro proveedor.
// Usa la misma puerta que EntradaConMicrosoft, por otra de sus funciones: no
// crea un usuario ni cambia quién entró; le suma una forma de entrar.
class EnlaceConMicrosoft(
    private val navegador: NavegadorDeProveedor,
) : Enlace {

    override suspend fun enlazar(): ResultadoDeEnlace =
        try {
            val proveedor = OAuthProvider.newBuilder("microsoft.com")
                .addCustomParameter("prompt", "select_account")
                .build()
            navegador.enlazar(proveedor)
            ResultadoDeEnlace.Enlazado
        } catch (e: CancellationException) {
            throw e
        } catch (e: NadieHaEntrado) {
            // Sin usuario no hay a quién enlazarle nada.
            ResultadoDeEnlace.SinSesion
        } catch (e: Exception) {
            if (e is FirebaseAuthWebException && e.errorCode == "ERROR_WEB_CONTEXT_CANCELED") {
                ResultadoDeEnlace.Cancelado
            } else {
                ResultadoDeEnlace.Fallo
            }
        }
}
