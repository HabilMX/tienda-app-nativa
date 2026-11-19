package mx.habil.ejemplo.tienda

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Prueba SIMULADA del enlace que arma la app: abre MainActivity y le pide su
// enlace a crearEnlaceConMicrosoft. Mide tres cosas:
// - con la puerta de mentira, que ese enlace pida microsoft.com;
// - con la puerta de VERDAD, que sin nadie adentro conteste «sin sesión» sin
//   abrir nada (en tu computadora Firebase no tiene ningún usuario);
// - con las dos puertas de VERDAD, que la nota «hay un enlace en curso» la
//   borre solo la puerta del enlace: la de entrar la deja como estaba.
// En tu computadora nunca hay un resultado pendiente de Firebase, así que
// esta prueba NO puede ver si la puerta de entrar respeta la nota (medido:
// sin esa línea sigue en verde). Eso lo respalda que PuertaDelNavegador.kt
// sea idéntico al de la lección, y al final, el nivel real.
// No cambies este archivo: el verifica revisa que siga igual al de solucion/.
@RunWith(RobolectricTestRunner::class)
class ArranqueConEnlaceTest {

    @get:Rule
    val app = createAndroidComposeRule<MainActivity>()

    @Test
    fun laAppArmaSuEnlacePidiendoEseProveedor() {
        val enlazador = EnlazadorFalso()
        val resultado = runBlocking { app.activity.crearEnlaceConMicrosoft(enlazador = enlazador).enlazar() }
        assertEquals(ResultadoDeEnlace.Enlazado, resultado)
        assertEquals("microsoft.com", enlazador.enlazados.single().providerId)
    }

    @Test
    fun conLaPuertaDeVerdadYSinNadieAdentroElEnlaceContestaSinSesion() {
        // Con límite de tiempo: si el enlace abriera el navegador en lugar de
        // preguntar primero por el usuario, aquí nadie contestaría nunca.
        val resultado = runBlocking { withTimeoutOrNull(5_000) { app.activity.crearEnlaceConMicrosoft().enlazar() } }
        assertEquals(
            "Sin nadie adentro, el enlace debe contestar «sin sesión» de inmediato (null = no contestó en 5 segundos)",
            ResultadoDeEnlace.SinSesion, resultado
        )
        assertFalse("Sin usuario no se abre el navegador ni se deja la nota", hayEnlaceEnCurso(app.activity))
    }

    @Test
    fun laNotaDeEnlaceLaBorraSoloLaPuertaDelEnlace() {
        anotaEnlaceEnCurso(app.activity, true)
        val comoEntrada = runBlocking { NavegadorConFirebase(app.activity).pendiente() }
        assertEquals("En tu computadora no hay nada pendiente", null, comoEntrada)
        assertTrue("La puerta de entrar no borra la nota: no es suya", hayEnlaceEnCurso(app.activity))
        runBlocking { EnlazadorConFirebase(app.activity).pendiente() }
        assertFalse("La puerta del enlace sí la borra al recogerla", hayEnlaceEnCurso(app.activity))
    }
}
