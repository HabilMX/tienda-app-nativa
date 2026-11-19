package mx.habil.ejemplo.tienda

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Prueba SIMULADA de la sesión que arma la app: abre MainActivity y le pide
// su sesión a crearSesion. Mide dos cosas:
// - con la puerta de mentira, que esa sesión sea la tuya: le pregunta al
//   teléfono quién sigue adentro;
// - con la puerta de VERDAD, que en tu computadora (donde Firebase no tiene
//   a nadie) nadie sigue adentro, y que pedirle a Firebase que olvide no truena.
// NO ve un usuario real de Firebase: en tu computadora nunca lo hay.
// NO llama al aviso de verdad a Credential Manager: en tu computadora no
// contesta (medido el 19-nov-2026: 10 segundos sin respuesta). Ese aviso solo
// se ve en el teléfono.
// No cambies este archivo: el verifica revisa que siga igual al de solucion/.
@RunWith(RobolectricTestRunner::class)
class ArranqueConSesionTest {

    @get:Rule
    val app = createAndroidComposeRule<MainActivity>()

    @Test
    fun laAppArmaSuSesionPreguntandoleAlTelefono() {
        val recuerdo = RecuerdoFalso(usuario = "Ana")
        val sesion = app.activity.crearSesion(recuerdo = recuerdo)
        assertEquals("La sesión que arma la app debe contestar a quién recuerda el teléfono", "Ana", sesion.quienSigueAdentro())
        runBlocking { sesion.cerrar() }
        assertEquals("Y al cerrar, pedirle a Firebase que lo olvide", 1, recuerdo.olvidosEnFirebase)
    }

    @Test
    fun conLaPuertaDeVerdadYSinNadieAdentroNadieSigueAdentro() {
        assertNull("En tu computadora Firebase no recuerda a nadie", app.activity.crearSesion().quienSigueAdentro())
        // La parte de Firebase de la puerta de verdad: olvidar sin nadie adentro no truena.
        val puerta = RecuerdoConFirebase(app.activity)
        puerta.olvidarEnFirebase()
        assertNull(puerta.usuarioActual())
    }
}
