package mx.habil.ejemplo.tienda

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Pruebas SIMULADAS de tu esPrimeraVez (PrimeraVez.kt): le dan un resultado de
// inicio de sesión de mentira (ResultadoFalso, en DoblesDePrimeraVez.kt) y
// miran qué decide. No tocan Firebase ni la red.
// No cambies este archivo: el verifica revisa que siga igual al de solucion/.
class PrimeraVezTest {

    @Test
    fun conUnUsuarioNuevoEsLaPrimeraVez() {
        assertTrue(
            "Si isNewUser es true, es la primera vez",
            esPrimeraVez(ResultadoFalso(InformacionFalsa(nuevo = true)))
        )
    }

    @Test
    fun conUnUsuarioQueVuelveNoEsLaPrimeraVez() {
        assertFalse(
            "Si isNewUser es false, el usuario ya existía",
            esPrimeraVez(ResultadoFalso(InformacionFalsa(nuevo = false)))
        )
    }

    @Test
    fun sinInformacionAdicionalNoEsLaPrimeraVezYNoTruena() {
        assertFalse(
            "Si additionalUserInfo es null no se sabe si es nuevo: no hay bienvenida",
            esPrimeraVez(ResultadoFalso(informacion = null))
        )
    }
}
