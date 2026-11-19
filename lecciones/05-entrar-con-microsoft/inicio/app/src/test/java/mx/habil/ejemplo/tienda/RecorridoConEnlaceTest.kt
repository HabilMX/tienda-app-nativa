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
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Pruebas SIMULADAS del recorrido del enlace: dibujan Tienda en memoria con
// tus tres clases (EntradaConGoogle, EntradaConMicrosoft y EnlaceConMicrosoft)
// conectadas a un proyecto de mentira (ProyectoFalso.kt) donde Ana ya tiene
// usuario creado con Google, tocan los botones y buscan los textos.
// Miden la confirmación: que nada se enlace sin el toque de la persona, y que
// solo se le ofrezca a quien ya entró.
// No miden el navegador, ni a Microsoft, ni a Firebase.
// No cambies este archivo: el verifica revisa que siga igual al de solucion/.
@RunWith(RobolectricTestRunner::class)
class RecorridoConEnlaceTest {

    @get:Rule
    val pantalla = createComposeRule()

    private val colision = "Ese correo ya entró a la tienda con otro botón. Entra con el que usaste la primera vez."
    private val correo = "ana@correo.invalid"

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

    // Ana ya entró alguna vez con Google y salió. Regresa el proyecto.
    private fun tiendaDeAna(): ProyectoFalso {
        val p = ProyectoFalso()
        val google = EntradaConGoogle(
            "id-de-la-prueba", SelectorFalso(credencial = credencialDeGoogleInventada()), p.verificadorDe(correo, "Ana")
        )
        runBlocking { google.entrar() }
        p.salir()
        val navegador = p.navegadorDe(correo, "Ana")
        pantalla.setContent {
            Tienda(
                entrada = google,
                entradaConMicrosoft = EntradaConMicrosoft(navegador),
                enlace = EnlaceConMicrosoft(p.enlazadorDe(correo)),
            )
        }
        toca("Entrar")
        return p
    }

    @Test
    fun enLaColisionTodaviaNoSeOfreceEnlazar() {
        val p = tiendaDeAna()
        toca("Entrar con Microsoft")
        pantalla.onNodeWithText(colision).assertIsDisplayed()
        noHayNingun("Enlazar Microsoft")
        assertEquals("Sin haber entrado, nadie debe pedir un enlace", 0, p.enlacesPedidos)
    }

    @Test
    fun trasLaColisionYEntrarConGoogleSePideConfirmacionSinEnlazar() {
        val p = tiendaDeAna()
        toca("Entrar con Microsoft")
        toca("Entrar con Google")
        pantalla.onNode(boton("Enlazar Microsoft")).assertIsDisplayed()
        pantalla.onNode(boton("Ahora no")).assertIsDisplayed()
        noHayNingun("Hola")
        assertEquals("Antes de que la persona confirme no se enlaza nada", 0, p.enlacesPedidos)
    }

    @Test
    fun alConfirmarSeEnlazaYMicrosoftLlegaAlMismoUsuario() {
        val p = tiendaDeAna()
        toca("Entrar con Microsoft")
        toca("Entrar con Google")
        val conGoogle = p.sesion!!.id
        toca("Enlazar Microsoft")
        pantalla.onNodeWithText("Listo").assertIsDisplayed()
        assertEquals("Un toque en «Enlazar Microsoft» pide el enlace una vez", 1, p.enlacesPedidos)
        toca("Continuar")
        pantalla.onNodeWithText("Hola, Ana").assertIsDisplayed()

        p.salir()
        val otraVez = runBlocking { EntradaConMicrosoft(p.navegadorDe(correo, "Ana")).entrar() }
        assertEquals(Resultado.Entro("Ana"), otraVez)
        assertEquals("Con Microsoft debe llegar al usuario que creó Google", conGoogle, p.sesion!!.id)
        assertEquals(1, p.usuarios.size)
    }

    @Test
    fun conAhoraNoEntraSinEnlazarYMicrosoftSigueChocando() {
        val p = tiendaDeAna()
        toca("Entrar con Microsoft")
        toca("Entrar con Google")
        toca("Ahora no")
        pantalla.onNodeWithText("Hola, Ana").assertIsDisplayed()
        assertEquals("«Ahora no» no enlaza", 0, p.enlacesPedidos)
        p.salir()
        val otraVez = runBlocking { EntradaConMicrosoft(p.navegadorDe(correo, "Ana")).entrar() }
        assertEquals(Resultado.CuentaConOtroProveedor, otraVez)
    }

    @Test
    fun sinColisionPreviaNoSePreguntaNada() {
        val p = tiendaDeAna()
        toca("Entrar con Google")
        pantalla.onNodeWithText("Hola, Ana").assertIsDisplayed()
        noHayNingun("Enlazar Microsoft")
        assertEquals(0, p.enlacesPedidos)
    }

    // --- Al abrir la app: un ENLACE que quedó a medias no es una entrada ---

    private fun abreConEnlaceAMedias(pendiente: EnlacePendiente) {
        // La puerta de entrar no tiene nada a medias: lo pendiente es del enlace.
        pantalla.setContent {
            Tienda(
                entrada = EntradaFalsa(Resultado.Fallo),
                entradaConMicrosoft = EntradaConMicrosoft(NavegadorFalso(pendiente = null)),
                enlace = EnlaceConMicrosoft(EnlazadorFalso(pendiente = pendiente)),
            )
        }
        pantalla.waitForIdle()
    }

    @Test
    fun alAbrirConUnEnlaceAMediasQueTerminoBienAvisaQueQuedoEnlazado() {
        abreConEnlaceAMedias(EnlacePendiente("Ana", null))
        pantalla.onNodeWithText("Listo").assertIsDisplayed()
        noHayNingun("La tienda"); noHayNingun("Hola")
        toca("Continuar")
        pantalla.onNodeWithText("Hola, Ana").assertIsDisplayed()
    }

    @Test
    fun alAbrirConUnEnlaceAMediasQueSeCanceloLoDiceYVuelveAPreguntar() {
        abreConEnlaceAMedias(EnlacePendiente("Ana", cancelacionInventada()))
        pantalla.onNodeWithText("Cerraste la ventana antes de terminar. No se enlazó nada.").assertIsDisplayed()
        pantalla.onNode(boton("Enlazar Microsoft")).assertIsDisplayed()
        noHayNingun("Listo"); noHayNingun("Hola"); noHayNingun("Entra a tu cuenta")
    }
}
