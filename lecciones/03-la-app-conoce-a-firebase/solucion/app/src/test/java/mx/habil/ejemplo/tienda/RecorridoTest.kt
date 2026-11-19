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

// Pruebas SIMULADAS de la lección 02: corren en tu computadora, sin emulador.
// Dibujan Tienda(entrada) en memoria, tocan los botones y buscan los textos
// de cada pantalla. Usan dos implementaciones de Entrada: EntradaFalsa (de
// pruebas, deja pasar) y EntradaSinProveedor (la de la app, no deja pasar).
// Miden qué pantalla queda después de cada toque; no miden cómo se ve, ni el
// botón «atrás» del teléfono, ni qué pasa al girarlo.
// No cambies este archivo: el verifica revisa que siga igual al de solucion/.
// EntradaFalsa.kt, junto a este archivo, sí lo escribes tú (paso 3).
@RunWith(RobolectricTestRunner::class)
class RecorridoTest {

    @get:Rule
    val pantalla = createComposeRule()

    // «El botón X»: un elemento con ese texto, que se puede tocar y que se
    // declara botón (Role.Button). Un Text con un toque agregado no cuenta.
    private fun boton(texto: String) =
        hasText(texto) and
            hasClickAction() and
            SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)

    private fun noHayNingun(texto: String) =
        pantalla.onAllNodes(hasText(texto, substring = true)).assertCountEquals(0)

    @Test
    fun empiezaEnLaBienvenida() {
        pantalla.setContent { Tienda(entrada = EntradaFalsa("Ana")) }
        pantalla.onNodeWithText("La tienda").assertIsDisplayed()
        pantalla.onNode(boton("Entrar")).assertIsDisplayed()
        noHayNingun("Entra a tu cuenta")
        noHayNingun("Hola")
    }

    @Test
    fun deLaBienvenidaPasaALaEntrada() {
        pantalla.setContent { Tienda(entrada = EntradaFalsa("Ana")) }
        pantalla.onNode(boton("Entrar")).performClick()
        pantalla.onNodeWithText("Entra a tu cuenta").assertIsDisplayed()
        pantalla.onNode(boton("Continuar")).assertIsDisplayed()
        // La bienvenida ya no está: se cambió de pantalla, no se encimó otra.
        noHayNingun("La tienda")
    }

    @Test
    fun conLaEntradaFalsaLlegaAlInicio() {
        pantalla.setContent { Tienda(entrada = EntradaFalsa("Ana")) }
        pantalla.onNode(boton("Entrar")).performClick()
        pantalla.onNode(boton("Continuar")).performClick()
        pantalla.onNodeWithText("Hola, Ana").assertIsDisplayed()
        noHayNingun("Entra a tu cuenta")
    }

    @Test
    fun elNombreLoPoneLaEntrada() {
        // Otra EntradaFalsa, con otro nombre: el saludo cambia sin tocar
        // ninguna pantalla.
        pantalla.setContent { Tienda(entrada = EntradaFalsa("Luis")) }
        pantalla.onNode(boton("Entrar")).performClick()
        pantalla.onNode(boton("Continuar")).performClick()
        pantalla.onNodeWithText("Hola, Luis").assertIsDisplayed()
        noHayNingun("Ana")
    }

    @Test
    fun conLaEntradaDeLaAppNoSePasa() {
        // La implementación que usa la app de verdad: no deja pasar.
        pantalla.setContent { Tienda(entrada = EntradaSinProveedor()) }
        pantalla.onNode(boton("Entrar")).performClick()
        noHayNingun("Todavía no se puede entrar")
        pantalla.onNode(boton("Continuar")).performClick()
        pantalla.onNodeWithText("Entra a tu cuenta").assertIsDisplayed()
        pantalla.onNodeWithText("Todavía no se puede entrar").assertIsDisplayed()
        noHayNingun("Hola")
    }

    @Test
    fun laEntradaSeConsultaSoloAlTocarContinuar() {
        val entrada = EntradaFalsa("Ana")
        pantalla.setContent { Tienda(entrada = entrada) }
        pantalla.onNode(boton("Entrar")).performClick()
        pantalla.onNodeWithText("Entra a tu cuenta").assertIsDisplayed()
        assertEquals("Antes de tocar «Continuar» nadie debe pedir entrar", 0, entrada.veces)
        pantalla.onNode(boton("Continuar")).performClick()
        pantalla.onNodeWithText("Hola, Ana").assertIsDisplayed()
        assertEquals("Un toque en «Continuar» pide entrar una vez", 1, entrada.veces)
    }
}
