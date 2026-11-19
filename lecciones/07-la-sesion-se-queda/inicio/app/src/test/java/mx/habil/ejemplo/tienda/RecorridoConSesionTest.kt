package mx.habil.ejemplo.tienda

import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Pruebas SIMULADAS del recorrido de la sesión: dibujan Tienda en memoria con
// tus clases (EntradaConGoogle y SesionDeLaTienda) conectadas al proyecto de
// mentira de la lección 06, entran, «cierran y vuelven a abrir» la app y
// buscan los textos.
// «Volver a abrir» aquí es dibujar Tienda desde cero, con todo su estado
// nuevo, y la misma memoria del «teléfono» (el proyecto de mentira). No es
// matar el proceso: eso no lo hace ninguna prueba en tu computadora.
// No miden Firebase, Credential Manager ni la red.
// No cambies este archivo: el verifica revisa que siga igual al de solucion/.
@RunWith(RobolectricTestRunner::class)
class RecorridoConSesionTest {

    @get:Rule
    val pantalla = createComposeRule()

    private val correo = "ana@correo.invalid"
    private val proyecto = ProyectoFalso()
    private val selector = SelectorFalso(credencial = credencialDeGoogleInventada())
    private val google = EntradaConGoogle("id-de-la-prueba", selector, proyecto.verificadorDe(correo, "Ana"))
    private val recuerdo = RecuerdoDelProyecto(proyecto)
    private var vez by mutableIntStateOf(0)

    private fun boton(texto: String) =
        hasText(texto) and
            hasClickAction() and
            SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)

    private fun noHayNingun(texto: String) =
        pantalla.onAllNodes(hasText(texto, substring = true)).assertCountEquals(0)

    private fun toca(texto: String) {
        pantalla.onNode(boton(texto)).performClick()
        pantalla.waitForIdle()
    }

    // Abre la app: dibuja Tienda con tu SesionDeLaTienda.
    private fun abre(conSesion: Boolean = true) {
        pantalla.setContent {
            key(vez) {
                Tienda(entrada = google, sesion = if (conSesion) SesionDeLaTienda(recuerdo) else null)
            }
        }
        pantalla.waitForIdle()
    }

    // «Cierra y vuelve a abrir» la app: Tienda se dibuja otra vez desde cero.
    private fun reabre() {
        vez++
        pantalla.waitForIdle()
    }

    @Test
    fun alAbrirSinSesionLlegaALaBienvenida() {
        abre()
        pantalla.onNodeWithText("La tienda").assertIsDisplayed()
        noHayNingun("Hola")
    }

    @Test
    fun alAbrirConUnaSesionGuardadaLlegaDirectoAlInicioSinPedirNada() {
        // Ana entró en una vez anterior y nunca cerró sesión.
        runBlocking { google.entrar() }
        selector.solicitudes.clear()
        abre()
        pantalla.onNodeWithText("Hola, Ana").assertIsDisplayed()
        noHayNingun("La tienda")
        assertEquals("Con la sesión guardada no se abre la ventana de cuentas", 0, selector.solicitudes.size)
    }

    @Test
    fun trasEntrarYReabrirLaAppSigueAdentro() {
        abre()
        toca("Entrar")
        toca("Entrar con Google")
        pantalla.onNodeWithText("Hola, Ana").assertIsDisplayed()
        reabre()
        pantalla.onNodeWithText("Hola, Ana").assertIsDisplayed()
        noHayNingun("La tienda")
        assertEquals("Reabrir no vuelve a pedir la cuenta", 1, selector.solicitudes.size)
    }

    @Test
    fun alCerrarSesionYReabrirSePideEntrarOtraVez() {
        abre()
        toca("Entrar")
        toca("Entrar con Google")
        val usuario = proyecto.sesion!!.id
        toca("Cerrar sesión")
        pantalla.onNodeWithText("La tienda").assertIsDisplayed()
        noHayNingun("Hola")
        assertNull("Después de cerrar sesión, el proyecto no tiene a nadie adentro", proyecto.sesion)

        reabre()
        pantalla.onNodeWithText("La tienda").assertIsDisplayed()
        noHayNingun("Hola")
        toca("Entrar")
        pantalla.onNode(boton("Entrar con Google")).assertIsDisplayed()

        toca("Entrar con Google")
        pantalla.onNodeWithText("Hola, Ana").assertIsDisplayed()
        assertEquals("Al volver a entrar se llega al mismo usuario", usuario, proyecto.sesion!!.id)
        assertEquals(1, proyecto.usuarios.size)
    }

    @Test
    fun cerrarSesionAvisaUnaVezALosProveedoresDelTelefono() {
        abre()
        toca("Entrar")
        toca("Entrar con Google")
        toca("Cerrar sesión")
        assertEquals(1, recuerdo.avisosAlTelefono)
    }

    @Test
    fun sinSesionElInicioEsElDeLaLeccion06() {
        abre(conSesion = false)
        toca("Entrar")
        toca("Entrar con Google")
        pantalla.onNodeWithText("Hola, Ana").assertIsDisplayed()
        noHayNingun("Cerrar sesión")
    }
}
