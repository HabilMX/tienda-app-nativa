package mx.habil.ejemplo.tienda

import androidx.credentials.Credential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

// Dobles de las dos puertas (Puertas.kt), para las pruebas. Viven en
// src/test: la app no puede usarlos. Ninguno abre una ventana, ni toca una
// cuenta, ni sale a la red.

// Un token inventado. No es un token de Google: es un texto cualquiera que
// sirve para seguirle la pista de una puerta a la otra.
const val TOKEN_INVENTADO = "token-inventado-de-la-prueba"

// La credencial que entregaría el teléfono si alguien eligiera su cuenta:
// del tipo de Google, con el token inventado adentro.
fun credencialDeGoogleInventada(): Credential =
    GoogleIdTokenCredential.Builder()
        .setId("persona-inventada")
        .setIdToken(TOKEN_INVENTADO)
        .build()

// Doble de la puerta 1. Guarda la solicitud que recibió, para que la prueba
// la revise, y contesta lo que le digas: una credencial o una excepción.
class SelectorFalso(
    private val credencial: Credential? = null,
    private val falla: Exception? = null,
) : SelectorDeCuentas {
    val solicitudes = mutableListOf<GetCredentialRequest>()

    override suspend fun pedir(solicitud: GetCredentialRequest): GetCredentialResponse {
        solicitudes += solicitud
        if (falla != null) throw falla
        return GetCredentialResponse(credencial!!)
    }
}

// Doble de la puerta 2. Guarda los tokens que le entregaron y contesta el
// nombre que le digas, o lanza la excepción que le digas.
class VerificadorFalso(
    private val nombre: String = "Ana",
    private val falla: Exception? = null,
) : VerificadorDeToken {
    val tokens = mutableListOf<String>()

    override suspend fun verificar(tokenDeIdentidad: String): String {
        tokens += tokenDeIdentidad
        if (falla != null) throw falla
        return nombre
    }
}
