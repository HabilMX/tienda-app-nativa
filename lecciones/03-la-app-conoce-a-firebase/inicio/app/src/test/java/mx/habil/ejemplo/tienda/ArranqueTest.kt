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

// Prueba SIMULADA del arranque: no dibuja Tienda con una entrada escogida por
// la prueba, sino que abre MainActivity, la misma clase que Android abre al
// tocar el ícono de la app, con lo que ella misma ponga en setContent.
// Mide una sola cosa: que por ese camino, tocando «Entrar» y «Continuar»,
// no se llega al inicio. No mide otros caminos que la app pudiera tener.
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
    fun laAppArrancadaNoDejaPasar() {
        // Al abrir: la bienvenida, y nada del inicio.
        app.onNodeWithText("La tienda").assertIsDisplayed()
        noHayNingun("Hola")
        app.onNode(boton("Entrar")).performClick()
        app.onNode(boton("Continuar")).performClick()
        app.onNodeWithText("Todavía no se puede entrar").assertIsDisplayed()
        noHayNingun("Hola")
    }
}
