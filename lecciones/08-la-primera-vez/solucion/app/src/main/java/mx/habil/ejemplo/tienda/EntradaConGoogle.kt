package mx.habil.ejemplo.tienda

import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CancellationException

// La Entrada de verdad: el botón «Entrar con Google».
// Recibe tres cosas y no sabe de dónde salen:
// - idDeClienteWeb: para quién se pide el token (la audiencia).
// - selector: la puerta hacia Credential Manager.
// - verificador: la puerta hacia Firebase.
class EntradaConGoogle(
    private val idDeClienteWeb: String,
    private val selector: SelectorDeCuentas,
    private val verificador: VerificadorDeToken,
) : Entrada {

    override suspend fun entrar(): Resultado {
        // 1. La solicitud: «quiero un token de identidad de Google, hecho
        //    para este ID de cliente». Es la opción del botón: abre la
        //    ventana para elegir cuenta.
        val opcion = GetSignInWithGoogleOption.Builder(idDeClienteWeb).build()
        val solicitud = GetCredentialRequest.Builder().addCredentialOption(opcion).build()

        return try {
            // 2. El teléfono muestra la ventana y regresa lo que se eligió.
            val credencial = selector.pedir(solicitud).credential
            if (credencial !is CustomCredential ||
                credencial.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                return Resultado.Fallo
            }
            // 3. De la respuesta se saca el token de identidad.
            val token = GoogleIdTokenCredential.createFrom(credencial.data).idToken
            // 4. Firebase lo verifica y contesta quién es, y si es la primera vez.
            val quien = verificador.verificarConPrimeraVez(token)
            Resultado.Entro(quien.nombre, quien.primeraVez)
        } catch (e: NoCredentialException) {
            // El teléfono no ofreció ninguna cuenta.
            Resultado.SinCuentas
        } catch (e: GetCredentialCancellationException) {
            // La persona cerró la ventana.
            Resultado.Cancelada
        } catch (e: CancellationException) {
            // No es una falla de la entrada: es Kotlin avisando que esta
            // tarea se canceló (por ejemplo, se cerró la pantalla). Se deja pasar.
            throw e
        } catch (e: Exception) {
            // Todo lo demás, venga del teléfono o de Firebase.
            Resultado.Fallo
        }
    }
}
