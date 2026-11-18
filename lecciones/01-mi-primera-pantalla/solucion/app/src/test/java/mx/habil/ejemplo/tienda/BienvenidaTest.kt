package mx.habil.ejemplo.tienda

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Pruebas SIMULADAS de la lección 01: corren en tu computadora, sin emulador.
// Dibujan Bienvenida() en memoria y buscan lo que pide el boceto.
// No cambies este archivo: el verifica revisa que siga igual al de solucion/.
@RunWith(RobolectricTestRunner::class)
class BienvenidaTest {

    @get:Rule
    val pantalla = createComposeRule()

    @Test
    fun muestraElNombreDeLaTienda() {
        pantalla.setContent { Bienvenida() }
        pantalla.onNodeWithText("La tienda").assertIsDisplayed()
    }

    @Test
    fun muestraElLema() {
        pantalla.setContent { Bienvenida() }
        pantalla.onNodeWithText("Tus órdenes, en tu bolsillo").assertIsDisplayed()
    }

    @Test
    fun tieneElBotonEntrar() {
        pantalla.setContent { Bienvenida() }
        // Un texto «Entrar» suelto no basta: tiene que poder tocarse.
        pantalla.onNode(hasText("Entrar") and hasClickAction()).assertIsDisplayed()
    }

    @Test
    fun alTocarEntrarSaluda() {
        pantalla.setContent { Bienvenida() }
        // Antes de tocar, el saludo no está.
        pantalla.onNodeWithText("¡Hola!").assertDoesNotExist()
        pantalla.onNode(hasText("Entrar") and hasClickAction()).performClick()
        pantalla.onNodeWithText("¡Hola!").assertIsDisplayed()
    }
}
