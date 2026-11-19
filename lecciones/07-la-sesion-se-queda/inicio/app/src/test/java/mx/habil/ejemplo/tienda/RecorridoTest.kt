package mx.habil.ejemplo.tienda

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialUnknownException
import androidx.credentials.exceptions.NoCredentialException
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Pruebas SIMULADAS del recorrido: corren en tu computadora, sin emulador.
// Dibujan Tienda(entrada) en memoria, tocan los botones y buscan los textos.
// Las primeras usan EntradaFalsa: miden las pantallas (andamiaje de la 02,
// adaptado al botón de Google). Las últimas cuatro usan TU EntradaConGoogle
// con las dos puertas de mentira: miden el botón de punta a punta, hasta
// donde llega una prueba sin teléfono.
// No miden cómo se ve, ni la ventana de cuentas de Android, ni a Google, ni
// a Firebase.
// No cambies este archivo: el verifica revisa que siga igual al de solucion/.
@RunWith(RobolectricTestRunner::class)
class RecorridoTest {

    @get:Rule
    val pantalla = createComposeRule()

    // Los tres textos que pide el objetivo 4.3, uno por caso.
    private val sinCuentas =
        "No encontré una cuenta de Google disponible para entrar. Agrega una o vuelve a iniciar sesión en Google y prueba otra vez."
    private val cancelada = "Cerraste la ventana sin elegir cuenta. Toca el botón cuando quieras intentar otra vez."
    private val fallo = "No se pudo entrar. Revisa tu conexión y vuelve a intentar."

    // «El botón X»: un elemento con ese texto, que se puede tocar y que se
    // declara botón (Role.Button). Un Text con un toque agregado no cuenta.
    private fun boton(texto: String) =
        hasText(texto) and
            hasClickAction() and
            SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)

    private fun noHayNingun(texto: String) =
        pantalla.onAllNodes(hasText(texto, substring = true)).assertCountEquals(0)

    private fun tocaEntrarConGoogle(entrada: Entrada) {
        pantalla.setContent { Tienda(entrada = entrada) }
        pantalla.onNode(boton("Entrar")).performClick()
        pantalla.onNode(boton("Entrar con Google")).performClick()
        pantalla.waitForIdle()
    }

    // --- Las pantallas, con EntradaFalsa -----------------------------------

    @Test
    fun empiezaEnLaBienvenida() {
        pantalla.setContent { Tienda(entrada = EntradaFalsa(Resultado.Entro("Ana"))) }
        pantalla.onNodeWithText("La tienda").assertIsDisplayed()
        pantalla.onNode(boton("Entrar")).assertIsDisplayed()
        noHayNingun("Entra a tu cuenta")
        noHayNingun("Hola")
    }

    @Test
    fun laEntradaTieneElBotonDeGoogleYNingunMensaje() {
        pantalla.setContent { Tienda(entrada = EntradaFalsa(Resultado.Entro("Ana"))) }
        pantalla.onNode(boton("Entrar")).performClick()
        pantalla.onNodeWithText("Entra a tu cuenta").assertIsDisplayed()
        pantalla.onNode(boton("Entrar con Google")).assertIsDisplayed()
        noHayNingun("La tienda")
        noHayNingun("No encontré")
        noHayNingun("Cerraste")
        noHayNingun("No se pudo")
    }

    @Test
    fun laEntradaSeConsultaSoloAlTocarElBoton() {
        val entrada = EntradaFalsa(Resultado.Entro("Ana"))
        pantalla.setContent { Tienda(entrada = entrada) }
        pantalla.onNode(boton("Entrar")).performClick()
        pantalla.onNodeWithText("Entra a tu cuenta").assertIsDisplayed()
        assertEquals("Antes de tocar «Entrar con Google» nadie debe pedir entrar", 0, entrada.veces)
        pantalla.onNode(boton("Entrar con Google")).performClick()
        pantalla.onNodeWithText("Hola, Ana").assertIsDisplayed()
        assertEquals("Un toque en «Entrar con Google» pide entrar una vez", 1, entrada.veces)
    }

    @Test
    fun cadaResultadoTieneSuPropioMensaje() {
        val textos = listOf(Resultado.SinCuentas, Resultado.Cancelada, Resultado.Fallo).map { mensajeDe(it) }
        assertEquals("Cada caso debe tener su texto", listOf<String?>(sinCuentas, cancelada, fallo), textos)
        assertEquals("Los tres textos deben ser distintos entre sí", 3, textos.toSet().size)
        assertEquals("Quien entró no recibe mensaje", null, mensajeDe(Resultado.Entro("Ana")))
    }

    // --- El botón, con tu EntradaConGoogle y las dos puertas de mentira ----

    private fun conGoogle(selector: SelectorDeCuentas, verificador: VerificadorDeToken = VerificadorFalso("Ana")) =
        EntradaConGoogle("id-de-cliente-de-la-prueba.apps.googleusercontent.com", selector, verificador)

    @Test
    fun conUnaCuentaElegidaLlegaAlInicioConElNombre() {
        tocaEntrarConGoogle(conGoogle(SelectorFalso(credencial = credencialDeGoogleInventada())))
        pantalla.onNodeWithText("Hola, Ana").assertIsDisplayed()
        noHayNingun("Entra a tu cuenta")
    }

    @Test
    fun sinCuentasMuestraSuMensajeYNoPasa() {
        tocaEntrarConGoogle(conGoogle(SelectorFalso(falla = NoCredentialException("sin cuentas, en la prueba"))))
        pantalla.onNodeWithText(sinCuentas).assertIsDisplayed()
        pantalla.onNodeWithText("Entra a tu cuenta").assertIsDisplayed()
        noHayNingun("Cerraste"); noHayNingun("No se pudo"); noHayNingun("Hola")
    }

    @Test
    fun laCancelacionMuestraSuMensajeYNoPasa() {
        tocaEntrarConGoogle(conGoogle(SelectorFalso(falla = GetCredentialCancellationException("cancelada, en la prueba"))))
        pantalla.onNodeWithText(cancelada).assertIsDisplayed()
        pantalla.onNodeWithText("Entra a tu cuenta").assertIsDisplayed()
        noHayNingun("No encontré"); noHayNingun("No se pudo"); noHayNingun("Hola")
    }

    @Test
    fun elErrorMuestraSuMensajeYNoPasa() {
        tocaEntrarConGoogle(conGoogle(SelectorFalso(falla = GetCredentialUnknownException("falla, en la prueba"))))
        pantalla.onNodeWithText(fallo).assertIsDisplayed()
        pantalla.onNodeWithText("Entra a tu cuenta").assertIsDisplayed()
        noHayNingun("No encontré"); noHayNingun("Cerraste"); noHayNingun("Hola")
    }
}
