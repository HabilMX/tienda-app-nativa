package mx.habil.ejemplo.tienda

import com.google.firebase.auth.AdditionalUserInfo
import com.google.firebase.auth.AuthResult
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Pruebas SIMULADAS de tu esPrimeraVez (PrimeraVez.kt): le dan un resultado de
// inicio de sesión de mentira (ResultadoFalso, en DoblesDePrimeraVez.kt) y
// miran qué decide. No tocan Firebase ni la red.
// Cada caso se prueba dos veces: con ResultadoFalso y con el mismo resultado
// envuelto en otra clase, como uno de Firebase, que tampoco es ResultadoFalso.
// Tu decisión tiene que salir igual con los dos: si dependiera de qué clase le
// llegó, el verde no diría nada de lo que pasa con Firebase de verdad.
// No cambies este archivo: el verifica revisa que siga igual al de solucion/.
class PrimeraVezTest {

    // Los mismos datos, en una clase que no es ningún doble con nombre.
    private fun enOtraClase(nuevo: Boolean?): AuthResult {
        val informacion: AdditionalUserInfo? =
            nuevo?.let { object : AdditionalUserInfo by InformacionFalsa(nuevo = it) {} }
        return object : AuthResult by ResultadoFalso(informacion) {}
    }

    @Test
    fun conUnUsuarioNuevoEsLaPrimeraVez() {
        assertTrue(
            "Si isNewUser es true, es la primera vez",
            esPrimeraVez(ResultadoFalso(InformacionFalsa(nuevo = true)))
        )
        assertTrue(
            "Si isNewUser es true, es la primera vez, venga en la clase que venga",
            esPrimeraVez(enOtraClase(nuevo = true))
        )
    }

    @Test
    fun conUnUsuarioQueVuelveNoEsLaPrimeraVez() {
        assertFalse(
            "Si isNewUser es false, el usuario ya existía",
            esPrimeraVez(ResultadoFalso(InformacionFalsa(nuevo = false)))
        )
        assertFalse(
            "Si isNewUser es false, el usuario ya existía, venga en la clase que venga",
            esPrimeraVez(enOtraClase(nuevo = false))
        )
    }

    @Test
    fun sinInformacionAdicionalNoEsLaPrimeraVezYNoTruena() {
        assertFalse(
            "Si additionalUserInfo es null no se sabe si es nuevo: no hay bienvenida",
            esPrimeraVez(ResultadoFalso(informacion = null))
        )
        assertFalse(
            "Si additionalUserInfo es null no hay bienvenida, venga en la clase que venga",
            esPrimeraVez(enOtraClase(nuevo = null))
        )
    }
}
