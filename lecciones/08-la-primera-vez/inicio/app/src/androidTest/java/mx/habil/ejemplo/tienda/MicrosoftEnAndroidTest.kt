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

// Pruebas INSTRUMENTADAS del botón de Microsoft: corren DENTRO de Android,
// en tu emulador o en tu teléfono (./gradlew connectedDebugAndroidTest).
// Dibujan Tienda con TU EntradaConMicrosoft y la puerta de mentira
// (DoblesDeMicrosoft.kt), tocan los botones de verdad y buscan los textos.
// Demuestran que las pantallas, el toque y tu clase funcionan dentro de
// Android, con la librería de Firebase Authentication del dispositivo
// armando el proveedor.
// No abren el navegador, no le piden nada a Microsoft y no le entregan nada
// a Firebase: la puerta es un doble. No demuestran que tu registro en Entra
// esté bien hecho ni que alguien pueda entrar. Y «retomar» aquí es el doble
// contestando que quedó algo a medias: Android no cerró ninguna app.
// No cambies este archivo: el verifica revisa que siga igual al de solucion/.
class MicrosoftEnAndroidTest {

    @get:Rule
    val pantalla = createComposeRule()

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
            Tienda(entrada = EntradaConGoogle("id-de-la-prueba", SelectorFalso(), VerificadorFalso()),
                entradaConMicrosoft = EntradaConMicrosoft(navegador))
        }
        pantalla.onNode(boton("Entrar")).performClick()
        pantalla.onNode(boton("Entrar con Google")).assertIsDisplayed()
        pantalla.onNode(boton("Entrar con Microsoft")).performClick()
        pantalla.waitForIdle()
    }

    @Test
    fun enAndroidConMicrosoftLlegaAlInicioConElNombre() {
        val navegador = NavegadorFalso(nombre = "Ana")
        tocaEntrarConMicrosoft(navegador)
        pantalla.onNodeWithText("Hola, Ana").assertIsDisplayed()
        noHayNingun("Entra a tu cuenta")
        assertEquals("microsoft.com", navegador.abiertos.single().providerId)
    }

    @Test
    fun enAndroidConMicrosoftLaCancelacionMuestraSuMensajeYNoPasa() {
        tocaEntrarConMicrosoft(NavegadorFalso(fallaAlAbrir = cancelacionInventada()))
        pantalla.onNodeWithText(cancelada).assertIsDisplayed()
        noHayNingun("No se pudo"); noHayNingun("Ese correo"); noHayNingun("Hola")
    }

    @Test
    fun enAndroidConMicrosoftElErrorMuestraSuMensajeYNoPasa() {
        tocaEntrarConMicrosoft(NavegadorFalso(fallaAlAbrir = otraFallaWebInventada()))
        pantalla.onNodeWithText(fallo).assertIsDisplayed()
        noHayNingun("Cerraste"); noHayNingun("Ese correo"); noHayNingun("Hola")
    }

    @Test
    fun enAndroidConMicrosoftLaColisionMuestraSuMensajeYNoPasa() {
        tocaEntrarConMicrosoft(NavegadorFalso(fallaAlAbrir = colisionInventada()))
        pantalla.onNodeWithText(colision).assertIsDisplayed()
        noHayNingun("Cerraste"); noHayNingun("No se pudo"); noHayNingun("Hola")
    }

    @Test
    fun enAndroidAlAbrirConUnaEntradaAMediasLlegaAlInicioSinTocarNada() {
        val navegador = NavegadorFalso(pendiente = "Luis")
        pantalla.setContent {
            Tienda(entrada = EntradaConGoogle("id-de-la-prueba", SelectorFalso(), VerificadorFalso()),
                entradaConMicrosoft = EntradaConMicrosoft(navegador))
        }
        pantalla.waitForIdle()
        pantalla.onNodeWithText("Hola, Luis").assertIsDisplayed()
        assertEquals("Retomar no abre el navegador", 0, navegador.abiertos.size)
    }
}
