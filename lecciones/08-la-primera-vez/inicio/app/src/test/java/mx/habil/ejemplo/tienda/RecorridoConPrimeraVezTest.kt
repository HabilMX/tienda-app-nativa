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
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Pruebas SIMULADAS del recorrido de la primera vez: dibujan Tienda en memoria
// con tus clases conectadas al proyecto de mentira de la lección 06, entran,
// salen, vuelven a entrar y buscan la bienvenida.
// «Es nuevo» aquí lo decide el proyecto de mentira: esa entrada creó un
// usuario. No miden Firebase, Credential Manager, el navegador ni la red.
// No cambies este archivo: el verifica revisa que siga igual al de solucion/.
@RunWith(RobolectricTestRunner::class)
class RecorridoConPrimeraVezTest {

    @get:Rule
    val pantalla = createComposeRule()

    private val proyecto = ProyectoFalso()
    private val selector = SelectorFalso(credencial = credencialDeGoogleInventada())
    private val google = EntradaConGoogle(
        "id-de-la-prueba", selector, proyecto.verificadorConPrimeraVezDe("ana@correo.invalid", "Ana")
    )
    private val microsoft = EntradaConMicrosoft(proyecto.navegadorConPrimeraVezDe("luis@correo.invalid", "Luis"))
    private val recuerdo = RecuerdoDelProyecto(proyecto)
    private var vez by mutableIntStateOf(0)

    private fun boton(texto: String) =
        hasText(texto) and
            hasClickAction() and
            SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)

    private fun hayBienvenida() = pantalla.onNodeWithText(TEXTO_DE_LA_PRIMERA_VEZ).assertIsDisplayed()

    private fun noHayBienvenida() =
        pantalla.onAllNodes(hasText(TEXTO_DE_LA_PRIMERA_VEZ)).assertCountEquals(0)

    private fun toca(texto: String) {
        pantalla.onNode(boton(texto)).performClick()
        pantalla.waitForIdle()
    }

    private fun abre(conMicrosoft: EntradaQueSeRetoma? = microsoft) {
        pantalla.setContent {
            key(vez) {
                Tienda(entrada = google, entradaConMicrosoft = conMicrosoft, sesion = SesionDeLaTienda(recuerdo))
            }
        }
        pantalla.waitForIdle()
    }

    private fun reabre() {
        vez++
        pantalla.waitForIdle()
    }

    @Test
    fun conGoogleLaPrimeraVezHayBienvenida() {
        abre()
        toca("Entrar")
        toca("Entrar con Google")
        pantalla.onNodeWithText("Hola, Ana").assertIsDisplayed()
        hayBienvenida()
    }

    @Test
    fun conGoogleAlVolverAEntrarYaNoHayBienvenida() {
        abre()
        toca("Entrar")
        toca("Entrar con Google")
        hayBienvenida()
        toca("Cerrar sesión")
        toca("Entrar")
        toca("Entrar con Google")
        pantalla.onNodeWithText("Hola, Ana").assertIsDisplayed()
        noHayBienvenida()
        assertEquals("Las dos entradas llegan al mismo usuario", 1, proyecto.usuarios.size)
    }

    @Test
    fun conMicrosoftLaPrimeraVezHayBienvenida() {
        abre()
        toca("Entrar")
        toca("Entrar con Microsoft")
        pantalla.onNodeWithText("Hola, Luis").assertIsDisplayed()
        hayBienvenida()
    }

    @Test
    fun conMicrosoftAlVolverAEntrarYaNoHayBienvenida() {
        abre()
        toca("Entrar")
        toca("Entrar con Microsoft")
        hayBienvenida()
        toca("Cerrar sesión")
        toca("Entrar")
        toca("Entrar con Microsoft")
        pantalla.onNodeWithText("Hola, Luis").assertIsDisplayed()
        noHayBienvenida()
    }

    @Test
    fun alReabrirConSesionNoHayBienvenida() {
        abre()
        toca("Entrar")
        toca("Entrar con Google")
        hayBienvenida()
        reabre()
        pantalla.onNodeWithText("Hola, Ana").assertIsDisplayed()
        noHayBienvenida()
    }

    @Test
    fun alRetomarConMicrosoftUnUsuarioNuevoHayBienvenida() {
        val pendiente = NavegadorConPrimeraVezFalso(primeraVez = false, pendiente = QuienEntro("Luis", primeraVez = true))
        abre(conMicrosoft = EntradaConMicrosoft(pendiente))
        pantalla.onNodeWithText("Hola, Luis").assertIsDisplayed()
        hayBienvenida()
    }
}
