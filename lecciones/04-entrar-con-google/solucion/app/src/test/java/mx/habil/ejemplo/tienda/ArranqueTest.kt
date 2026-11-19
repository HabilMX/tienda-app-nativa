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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Prueba SIMULADA del arranque: abre MainActivity, la misma clase que Android
// abre al tocar el ícono de la app, con lo que ella misma ponga en setContent.
// Mide una sola cosa: que por ese camino se llega a la pantalla de entrada,
// con su botón, sin que nadie haya entrado.
// NO toca «Entrar con Google»: en la app de verdad ese botón abre la ventana
// de cuentas de Android, que una prueba en tu computadora no tiene.
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
}
