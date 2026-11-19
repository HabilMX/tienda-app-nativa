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
//   a nadie) nadie sigue adentro, que pedirle a Firebase que olvide no truena,
//   y que al cerrar sesión esa puerta le pide a Credential Manager que limpie.
// NO ve un usuario real de Firebase: en tu computadora nunca lo hay.
// NO llama al Credential Manager de verdad: en tu computadora no contesta
// (medido el 19-nov-2026: 10 segundos sin respuesta). En su lugar va un
// limpiador de mentira. Lo que el teléfono ofrece después solo se ve ahí.
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

    @Test
    fun conLaPuertaDeVerdadCerrarLeAvisaACredentialManager() {
        // La puerta de verdad, con un limpiador de mentira en lugar de
        // Credential Manager (que en tu computadora no contesta).
        val limpiador = LimpiadorFalso()
        val sesion = app.activity.crearSesion(recuerdo = RecuerdoConFirebase(app.activity, limpiador = limpiador))
        runBlocking { sesion.cerrar() }
        assertEquals(
            "Con la puerta de verdad, cerrar sesión debe pedirle a Credential Manager, una vez, que limpie",
            1, limpiador.limpiezas
        )
        assertNull(sesion.quienSigueAdentro())
    }
}
