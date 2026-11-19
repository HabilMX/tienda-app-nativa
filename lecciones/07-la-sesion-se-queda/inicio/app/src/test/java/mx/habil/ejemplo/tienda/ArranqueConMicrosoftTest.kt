package mx.habil.ejemplo.tienda

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Prueba SIMULADA del arranque con los dos botones: abre MainActivity, la
// misma clase que Android abre al tocar el ícono, con lo que ella misma
// ponga en setContent. Mide dos cosas:
// - que por ese camino la pantalla de entrada muestra los dos botones;
// - que la entrada que arma MainActivity.crearEntradaConMicrosoft, con la
//   puerta de mentira que le pasa la prueba, pide el proveedor microsoft.com.
// NO toca «Entrar con Microsoft»: en la app de verdad ese botón abre el
// navegador, que una prueba en tu computadora no tiene. Y no ve si onCreate
// le pasa a Tienda lo que regresa crearEntradaConMicrosoft(): eso lo busca
// el verifica como texto.
// Al abrirse, la app sí pregunta por una entrada a medias con la puerta de
// verdad: en tu computadora Firebase contesta que no hay ninguna.
// No cambies este archivo: el verifica revisa que siga igual al de solucion/.
@RunWith(RobolectricTestRunner::class)
class ArranqueConMicrosoftTest {

    @get:Rule
    val app = createAndroidComposeRule<MainActivity>()

    private fun boton(texto: String) =
        hasText(texto) and
            hasClickAction() and
            SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)

    @Test
    fun laAppArrancadaMuestraLosDosBotones() {
        app.onNode(boton("Entrar")).performClick()
        app.onNode(boton("Entrar con Google")).assertIsDisplayed()
        app.onNode(boton("Entrar con Microsoft")).assertIsDisplayed()
    }

    @Test
    fun laAppArmaSuEntradaConMicrosoftPidiendoEseProveedor() {
        val navegador = NavegadorFalso()
        val entrada = app.activity.crearEntradaConMicrosoft(navegador = navegador)
        runBlocking { entrada.entrar() }
        assertEquals(
            "La entrada que arma MainActivity.crearEntradaConMicrosoft no abrió el navegador una vez",
            1, navegador.abiertos.size
        )
        assertEquals("microsoft.com", navegador.abiertos.single().providerId)
    }
}
