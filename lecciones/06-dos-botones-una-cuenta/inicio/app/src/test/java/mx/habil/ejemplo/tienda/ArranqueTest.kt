package mx.habil.ejemplo.tienda

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import java.io.File
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Prueba SIMULADA del arranque: abre MainActivity, la misma clase que Android
// abre al tocar el ícono de la app, con lo que ella misma ponga en setContent.
// Mide dos cosas:
// - que por ese camino se llega a la pantalla de entrada, con su botón, sin
//   que nadie haya entrado;
// - que la Entrada que arma MainActivity.crearEntrada pide el token con el
//   cliente web de tu google-services.json como audiencia. Para leerlo, la
//   prueba le pasa a crearEntrada las dos puertas de mentira.
// NO toca «Entrar con Google»: en la app de verdad ese botón abre la ventana
// de cuentas de Android, que una prueba en tu computadora no tiene. Y no ve
// si onCreate usa crearEntrada(): eso lo busca el verifica como texto.
// No cambies este archivo: el verifica revisa que siga igual al de solucion/.
@RunWith(RobolectricTestRunner::class)
class ArranqueTest {

    @get:Rule
    val app = createAndroidComposeRule<MainActivity>()

    private fun boton(texto: String) =
        hasText(texto) and
            hasClickAction() and
            SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)

    private fun noHayNingun(texto: String) =
        app.onAllNodes(hasText(texto, substring = true)).assertCountEquals(0)

    @Test
    fun laAppArrancadaLlegaALaEntradaSinQueNadieHayaEntrado() {
        app.onNodeWithText("La tienda").assertIsDisplayed()
        noHayNingun("Hola")
        app.onNode(boton("Entrar")).performClick()
        app.onNodeWithText("Entra a tu cuenta").assertIsDisplayed()
        app.onNode(boton("Entrar con Google")).assertIsDisplayed()
        noHayNingun("Hola")
    }

    @Test
    fun laAppArmaSuEntradaConElClienteWebDeTuArchivo() {
        val archivo = File("google-services.json")
        if (!archivo.isFile) { fail("No existe google-services.json en la carpeta app/"); return }
        val delArchivo = clienteWeb(archivo.readText(), "mx.habil.ejemplo.tienda")
        if (delArchivo == null) { fail("Tu archivo no trae un cliente de OAuth web (tipo 3)"); return }

        val selector = SelectorFalso(credencial = credencialDeGoogleInventada())
        val entrada = app.activity.crearEntrada(selector = selector, verificador = VerificadorFalso())
        runBlocking { entrada.entrar() }

        if (selector.solicitudes.size != 1) {
            fail("La Entrada que arma MainActivity.crearEntrada no le pidió nada a la puerta del teléfono"); return
        }
        val opcion = selector.solicitudes.single().credentialOptions.single()
        if (opcion !is GetSignInWithGoogleOption) {
            fail("La opción no es GetSignInWithGoogleOption: no puedo leerle la audiencia"); return
        }
        assertEquals(
            "La audiencia que pide la app no es el cliente web (tipo 3) de tu archivo",
            delArchivo, opcion.serverClientId
        )
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
}
