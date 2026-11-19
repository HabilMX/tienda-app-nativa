package mx.habil.ejemplo.tienda

import androidx.credentials.exceptions.ClearCredentialUnknownException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Test

// Pruebas SIMULADAS de tu SesionDeLaTienda: le dan una puerta de mentira
// (RecuerdoFalso, en DoblesDeSesion.kt) y miran qué le preguntó, qué le pidió
// y qué contestó. No tocan Firebase, Credential Manager ni la red.
// No cambies este archivo: el verifica revisa que siga igual al de solucion/.
class SesionDeLaTiendaTest {

    @Test
    fun conAlguienAdentroDiceQuienEs() {
        val sesion = SesionDeLaTienda(RecuerdoFalso(usuario = "Ana"))
        assertEquals("Si el teléfono recuerda a Ana, sigue adentro Ana", "Ana", sesion.quienSigueAdentro())
    }

    @Test
    fun sinNadieAdentroContestaNull() {
        assertNull("Si el teléfono no recuerda a nadie, nadie sigue adentro", SesionDeLaTienda(RecuerdoFalso()).quienSigueAdentro())
    }

    @Test
    fun preguntarNoOlvidaANadie() {
        val recuerdo = RecuerdoFalso(usuario = "Ana")
        val sesion = SesionDeLaTienda(recuerdo)
        sesion.quienSigueAdentro()
        sesion.quienSigueAdentro()
        assertEquals("Preguntar quién sigue adentro no cierra nada", 0, recuerdo.olvidosEnFirebase + recuerdo.avisosAlTelefono)
        assertEquals("Ana", recuerdo.usuario)
    }

    @Test
    fun cerrarHaceQueFirebaseOlvideAlUsuario() {
        val recuerdo = RecuerdoFalso(usuario = "Ana")
        val sesion = SesionDeLaTienda(recuerdo)
        runBlocking { sesion.cerrar() }
        assertEquals("Cerrar le pide a Firebase, una vez, que olvide al usuario", 1, recuerdo.olvidosEnFirebase)
        assertNull("Después de cerrar, nadie sigue adentro", sesion.quienSigueAdentro())
    }

    @Test
    fun cerrarTambienAvisaALosProveedoresDelTelefono() {
        val recuerdo = RecuerdoFalso(usuario = "Ana")
        runBlocking { SesionDeLaTienda(recuerdo).cerrar() }
        assertEquals("Cerrar avisa una vez a los proveedores del teléfono (Credential Manager)", 1, recuerdo.avisosAlTelefono)
    }

    @Test
    fun siElAvisoAlTelefonoFallaLaSesionIgualQuedaCerrada() {
        val recuerdo = RecuerdoFalso(usuario = "Ana", fallaAlAvisar = ClearCredentialUnknownException("aviso inventado"))
        val sesion = SesionDeLaTienda(recuerdo)
        try {
            runBlocking { sesion.cerrar() }
        } catch (e: Exception) {
            fail("Si el aviso a los proveedores falla, cerrar no debe tronar la app (lanzó ${e::class.simpleName})")
        }
        assertEquals("Aunque el aviso falle, Firebase olvida al usuario", 1, recuerdo.olvidosEnFirebase)
        assertNull(sesion.quienSigueAdentro())
    }

    @Test
    fun laCancelacionNoSeTraga() {
        val recuerdo = RecuerdoFalso(usuario = "Ana", fallaAlAvisar = CancellationException("tarea cancelada"))
        try {
            runBlocking { SesionDeLaTienda(recuerdo).cerrar() }
            fail("CancellationException no es una falla del aviso: es Kotlin cancelando la tarea, y se deja pasar")
        } catch (e: CancellationException) {
            // Es lo esperado.
        }
    }
}
