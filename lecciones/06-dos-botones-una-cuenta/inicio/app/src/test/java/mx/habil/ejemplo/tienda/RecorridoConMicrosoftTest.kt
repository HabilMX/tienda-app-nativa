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
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Pruebas SIMULADAS del recorrido con los dos botones: corren en tu
// computadora, sin emulador. Dibujan Tienda en memoria con una entrada de
// mentira para Google (EntradaFalsa) y TU EntradaConMicrosoft con la puerta
// de mentira, tocan los botones y buscan los textos.
// No miden cómo se ve, ni el navegador, ni a Microsoft, ni a Firebase.
// No cambies este archivo: el verifica revisa que siga igual al de solucion/.
@RunWith(RobolectricTestRunner::class)
class RecorridoConMicrosoftTest {

    @get:Rule
    val pantalla = createComposeRule()

    private val sinCuentas =
        "No encontré una cuenta de Google disponible para entrar. Agrega una o vuelve a iniciar sesión en Google y prueba otra vez."
    private val cancelada = "Cerraste la ventana sin elegir cuenta. Toca el botón cuando quieras intentar otra vez."
    private val fallo = "No se pudo entrar. Revisa tu conexión y vuelve a intentar."
    private val colision = "Ese correo ya entró a la tienda con otro botón. Entra con el que usaste la primera vez."

    private fun boton(texto: String) =
        hasText(texto) and
            hasClickAction() and
            SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)

    private fun noHayNingun(texto: String) =
        pantalla.onAllNodes(hasText(texto, substring = true)).assertCountEquals(0)

    private fun tocaEntrarConMicrosoft(navegador: NavegadorDeProveedor) {
        pantalla.setContent {
            Tienda(entrada = EntradaFalsa(Resultado.Fallo), entradaConMicrosoft = EntradaConMicrosoft(navegador))
        }
        pantalla.onNode(boton("Entrar")).performClick()
        pantalla.onNode(boton("Entrar con Microsoft")).performClick()
        pantalla.waitForIdle()
    }

    // --- Los dos botones conviven ------------------------------------------

    @Test
    fun losDosBotonesEstanYCadaUnoLlamaASuEntrada() {
        val google = EntradaFalsa(Resultado.Cancelada)
        val navegador = NavegadorFalso(fallaAlAbrir = colisionInventada())
        pantalla.setContent { Tienda(entrada = google, entradaConMicrosoft = EntradaConMicrosoft(navegador)) }
        pantalla.onNode(boton("Entrar")).performClick()
        pantalla.onNode(boton("Entrar con Google")).assertIsDisplayed()
        pantalla.onNode(boton("Entrar con Microsoft")).assertIsDisplayed()
        assertEquals("Antes de tocar un botón nadie debe pedir entrar", 0, google.veces)
        assertEquals("Antes de tocar un botón no se abre el navegador", 0, navegador.abiertos.size)

        pantalla.onNode(boton("Entrar con Microsoft")).performClick()
        pantalla.waitForIdle()
        assertEquals("El botón de Microsoft no llama a la entrada de Google", 0, google.veces)
        assertEquals("El botón de Microsoft abre el navegador una vez", 1, navegador.abiertos.size)
        pantalla.onNodeWithText(colision).assertIsDisplayed()

        pantalla.onNode(boton("Entrar con Google")).performClick()
        pantalla.waitForIdle()
        assertEquals("El botón de Google llama a su entrada una vez", 1, google.veces)
        assertEquals("El botón de Google no abre el navegador", 1, navegador.abiertos.size)
        pantalla.onNodeWithText(cancelada).assertIsDisplayed()
    }

    @Test
    fun sinEntradaDeMicrosoftLaPantallaEsLaDeLaLeccion04() {
        pantalla.setContent { Tienda(entrada = EntradaFalsa(Resultado.Entro("Ana"))) }
        pantalla.onNode(boton("Entrar")).performClick()
        pantalla.onNode(boton("Entrar con Google")).assertIsDisplayed()
        noHayNingun("Entrar con Microsoft")
    }

    // --- El botón de Microsoft, con tu clase y la puerta de mentira --------

    @Test
    fun conMicrosoftLlegaAlInicioConElNombre() {
        tocaEntrarConMicrosoft(NavegadorFalso(nombre = "Ana"))
        pantalla.onNodeWithText("Hola, Ana").assertIsDisplayed()
        noHayNingun("Entra a tu cuenta")
    }

    @Test
    fun laCancelacionMuestraSuMensajeYNoPasa() {
        tocaEntrarConMicrosoft(NavegadorFalso(fallaAlAbrir = cancelacionInventada()))
        pantalla.onNodeWithText(cancelada).assertIsDisplayed()
        pantalla.onNodeWithText("Entra a tu cuenta").assertIsDisplayed()
        noHayNingun("No se pudo"); noHayNingun("Ese correo"); noHayNingun("Hola")
    }

    @Test
    fun elErrorMuestraSuMensajeYNoPasa() {
        tocaEntrarConMicrosoft(NavegadorFalso(fallaAlAbrir = otraFallaWebInventada()))
        pantalla.onNodeWithText(fallo).assertIsDisplayed()
        pantalla.onNodeWithText("Entra a tu cuenta").assertIsDisplayed()
        noHayNingun("Cerraste"); noHayNingun("Ese correo"); noHayNingun("Hola")
    }

    @Test
    fun laColisionMuestraSuMensajeYNoPasa() {
        tocaEntrarConMicrosoft(NavegadorFalso(fallaAlAbrir = colisionInventada()))
        pantalla.onNodeWithText(colision).assertIsDisplayed()
        pantalla.onNodeWithText("Entra a tu cuenta").assertIsDisplayed()
        noHayNingun("Cerraste"); noHayNingun("No se pudo"); noHayNingun("Hola")
    }

    @Test
    fun elMensajeDeLaColisionEsElDeLaLeccionYDistintoDeLosOtrosTres() {
        val textos = listOf(
            Resultado.SinCuentas, Resultado.Cancelada, Resultado.Fallo, Resultado.CuentaConOtroProveedor
        ).map { mensajeDe(it) }
        assertEquals(listOf<String?>(sinCuentas, cancelada, fallo, colision), textos)
        assertEquals("Los cuatro textos deben ser distintos entre sí", 4, textos.toSet().size)
    }

    // --- Al abrir la app: retomar lo que quedó a medias --------------------

    @Test
    fun alAbrirConUnaEntradaAMediasLlegaAlInicioSinTocarNada() {
        val navegador = NavegadorFalso(pendiente = "Luis")
        pantalla.setContent {
            Tienda(entrada = EntradaFalsa(Resultado.Fallo), entradaConMicrosoft = EntradaConMicrosoft(navegador))
        }
        pantalla.waitForIdle()
        pantalla.onNodeWithText("Hola, Luis").assertIsDisplayed()
        noHayNingun("La tienda")
        assertEquals("Retomar no abre el navegador", 0, navegador.abiertos.size)
    }

    @Test
    fun alAbrirSinNadaAMediasSeQuedaEnLaBienvenida() {
        val navegador = NavegadorFalso(pendiente = null)
        pantalla.setContent {
            Tienda(entrada = EntradaFalsa(Resultado.Fallo), entradaConMicrosoft = EntradaConMicrosoft(navegador))
        }
        pantalla.waitForIdle()
        pantalla.onNodeWithText("La tienda").assertIsDisplayed()
        noHayNingun("Hola"); noHayNingun("Entra a tu cuenta")
        assertEquals("Al abrir, la app debe preguntar una vez por lo pendiente", 1, navegador.preguntasPorPendiente)
        assertEquals("Sin tocar el botón no se abre el navegador", 0, navegador.abiertos.size)
    }

    @Test
    fun alAbrirConUnaEntradaAMediasQueTerminoEnColisionMuestraSuMensaje() {
        pantalla.setContent {
            Tienda(
                entrada = EntradaFalsa(Resultado.Fallo),
                entradaConMicrosoft = EntradaConMicrosoft(NavegadorFalso(fallaPendiente = colisionInventada())),
            )
        }
        pantalla.waitForIdle()
        pantalla.onNodeWithText(colision).assertIsDisplayed()
        pantalla.onNodeWithText("Entra a tu cuenta").assertIsDisplayed()
        noHayNingun("Hola")
    }
}
