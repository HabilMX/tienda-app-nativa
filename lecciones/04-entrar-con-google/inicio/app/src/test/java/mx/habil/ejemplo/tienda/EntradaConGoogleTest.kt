package mx.habil.ejemplo.tienda

import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialUnknownException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import java.io.File
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

// Pruebas SIMULADAS de EntradaConGoogle: corren en tu computadora, sin
// emulador, sin cuenta y sin red. Le dan a tu clase dos puertas de mentira
// (Dobles.kt) y miran dos cosas: qué les pidió y qué contestó.
// No hablan con Google ni con Firebase: no demuestran que tu proyecto esté
// bien configurado ni que alguien pueda entrar. Eso es el nivel real.
// No cambies este archivo: el verifica revisa que siga igual al de solucion/.
@RunWith(RobolectricTestRunner::class)
class EntradaConGoogleTest {

    private val idDePrueba = "id-de-cliente-de-la-prueba.apps.googleusercontent.com"

    private fun entrada(selector: SelectorDeCuentas, verificador: VerificadorDeToken) =
        EntradaConGoogle(idDeClienteWeb = idDePrueba, selector = selector, verificador = verificador)

    // --- 4.2 · Qué le pide al teléfono -------------------------------------

    @Test
    fun pideLaOpcionDelBoton() {
        val selector = SelectorFalso(credencial = credencialDeGoogleInventada())
        runBlocking { entrada(selector, VerificadorFalso()).entrar() }
        assertEquals("Un toque debe pedir una sola vez", 1, selector.solicitudes.size)
        val opciones = selector.solicitudes.single().credentialOptions
        assertEquals("La solicitud debe llevar una sola opción", 1, opciones.size)
        assertTrue(
            "La opción debe ser GetSignInWithGoogleOption (la del botón) y fue ${opciones.single().javaClass.simpleName}",
            opciones.single() is GetSignInWithGoogleOption
        )
    }

    @Test
    fun laAudienciaEsElIdQueRecibio() {
        val selector = SelectorFalso(credencial = credencialDeGoogleInventada())
        runBlocking { entrada(selector, VerificadorFalso()).entrar() }
        val opcion = selector.solicitudes.single().credentialOptions.single()
        if (opcion !is GetSignInWithGoogleOption) {
            fail("La opción no es GetSignInWithGoogleOption: no puedo leerle la audiencia"); return
        }
        assertEquals("La audiencia del token no es el ID que recibió EntradaConGoogle", idDePrueba, opcion.serverClientId)
    }

    // El plugin google-services convierte el cliente web (tipo 3) de tu
    // archivo en el recurso default_web_client_id. Aquí se compara el recurso
    // de la app compilada contra el texto de tu archivo.
    // No revisa que MainActivity use ese recurso: eso lo busca el verifica
    // como texto.
    @Test
    fun elRecursoDeLaAppEsElClienteWebDeTuArchivo() {
        val archivo = File("google-services.json")
        if (!archivo.isFile) { fail("No existe google-services.json en la carpeta app/"); return }
        val delArchivo = clienteWeb(archivo.readText(), "mx.habil.ejemplo.tienda")
        if (delArchivo == null) { fail("Tu archivo no trae un cliente de OAuth web (tipo 3)"); return }
        val app = RuntimeEnvironment.getApplication()
        val id = app.resources.getIdentifier("default_web_client_id", "string", app.packageName)
        if (id == 0) { fail("La app compilada no trae el recurso default_web_client_id"); return }
        assertEquals("El recurso default_web_client_id no es el cliente web de tu archivo", delArchivo, app.getString(id))
    }

    private fun clienteWeb(texto: String, paquete: String): String? {
        val clientes = JSONObject(texto).getJSONArray("client")
        for (i in 0 until clientes.length()) {
            val c = clientes.getJSONObject(i)
            val suPaquete = c.getJSONObject("client_info").getJSONObject("android_client_info").getString("package_name")
            if (suPaquete != paquete) continue
            val oauth = c.optJSONArray("oauth_client") ?: continue
            for (j in 0 until oauth.length()) {
                val o = oauth.getJSONObject(j)
                if (o.optInt("client_type") == 3) return o.getString("client_id")
            }
        }
        return null
    }

    // --- 4.2 · El token pasa de una puerta a la otra -----------------------

    @Test
    fun elTokenQueEntregaElTelefonoLlegaAlVerificador() {
        val verificador = VerificadorFalso(nombre = "Ana")
        val resultado = runBlocking {
            entrada(SelectorFalso(credencial = credencialDeGoogleInventada()), verificador).entrar()
        }
        assertEquals("El verificador debe recibir el token tal cual, una vez", listOf(TOKEN_INVENTADO), verificador.tokens)
        assertEquals("El nombre lo pone el verificador", Resultado.Entro("Ana"), resultado)
    }

    @Test
    fun elNombreLoPoneElVerificador() {
        val resultado = runBlocking {
            entrada(SelectorFalso(credencial = credencialDeGoogleInventada()), VerificadorFalso(nombre = "Luis")).entrar()
        }
        assertEquals(Resultado.Entro("Luis"), resultado)
    }

    // --- 4.3 · Los tres casos en que no se entra ---------------------------

    @Test
    fun sinCuentasContestaSinCuentas() {
        val verificador = VerificadorFalso()
        val resultado = runBlocking {
            entrada(SelectorFalso(falla = NoCredentialException("sin cuentas, en la prueba")), verificador).entrar()
        }
        assertEquals(Resultado.SinCuentas, resultado)
        assertEquals("Sin token no hay nada que verificar", emptyList<String>(), verificador.tokens)
    }

    @Test
    fun laCancelacionContestaCancelada() {
        val verificador = VerificadorFalso()
        val resultado = runBlocking {
            entrada(SelectorFalso(falla = GetCredentialCancellationException("cancelada, en la prueba")), verificador).entrar()
        }
        assertEquals(Resultado.Cancelada, resultado)
        assertEquals("Sin token no hay nada que verificar", emptyList<String>(), verificador.tokens)
    }

    @Test
    fun otraFallaDelTelefonoContestaFallo() {
        val resultado = runBlocking {
            entrada(SelectorFalso(falla = GetCredentialUnknownException("falla, en la prueba")), VerificadorFalso()).entrar()
        }
        assertEquals(Resultado.Fallo, resultado)
    }

    @Test
    fun siElVerificadorRechazaElTokenContestaFallo() {
        val verificador = VerificadorFalso(falla = IllegalStateException("token rechazado, en la prueba"))
        val resultado = runBlocking {
            entrada(SelectorFalso(credencial = credencialDeGoogleInventada()), verificador).entrar()
        }
        assertEquals(Resultado.Fallo, resultado)
    }

    @Test
    fun unaCredencialQueNoEsDeGoogleContestaFallo() {
        val verificador = VerificadorFalso()
        val resultado = runBlocking {
            entrada(SelectorFalso(credencial = PasswordCredential("alguien", "una-clave-inventada")), verificador).entrar()
        }
        assertEquals(Resultado.Fallo, resultado)
        assertEquals("Una credencial que no es de Google no trae token que verificar", emptyList<String>(), verificador.tokens)
    }
}
