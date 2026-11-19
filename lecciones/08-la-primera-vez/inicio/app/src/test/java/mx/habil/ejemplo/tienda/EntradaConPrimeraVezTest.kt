package mx.habil.ejemplo.tienda

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Pruebas SIMULADAS de tus dos entradas con la primera vez: les dan puertas de
// mentira que contestan si el usuario es nuevo (DoblesDePrimeraVez.kt) y miran
// si el Resultado lo trae. No hablan con Google, Microsoft ni Firebase.
// No cambies este archivo: el verifica revisa que siga igual al de solucion/.
@RunWith(RobolectricTestRunner::class)
class EntradaConPrimeraVezTest {

    private val selector = SelectorFalso(credencial = credencialDeGoogleInventada())

    // --- Google ------------------------------------------------------------

    @Test
    fun conGoogleUnUsuarioNuevoEntraPorPrimeraVez() {
        val verificador = VerificadorConPrimeraVezFalso(nombre = "Ana", primeraVez = true)
        val resultado = runBlocking { EntradaConGoogle("id-de-la-prueba", selector, verificador).entrar() }
        assertEquals(Resultado.Entro("Ana", primeraVez = true), resultado)
        assertEquals("El token llega una sola vez a la puerta", listOf(TOKEN_INVENTADO), verificador.tokens)
    }

    @Test
    fun conGoogleUnUsuarioQueVuelveNoEsLaPrimeraVez() {
        val verificador = VerificadorConPrimeraVezFalso(nombre = "Ana", primeraVez = false)
        val resultado = runBlocking { EntradaConGoogle("id-de-la-prueba", selector, verificador).entrar() }
        assertEquals(Resultado.Entro("Ana", primeraVez = false), resultado)
    }

    // --- Microsoft -----------------------------------------------------------

    @Test
    fun conMicrosoftUnUsuarioNuevoEntraPorPrimeraVez() {
        val navegador = NavegadorConPrimeraVezFalso(nombre = "Luis", primeraVez = true)
        val resultado = runBlocking { EntradaConMicrosoft(navegador).entrar() }
        assertEquals(Resultado.Entro("Luis", primeraVez = true), resultado)
        assertEquals("Un toque abre el navegador una sola vez", 1, navegador.abiertos.size)
    }

    @Test
    fun conMicrosoftUnUsuarioQueVuelveNoEsLaPrimeraVez() {
        val navegador = NavegadorConPrimeraVezFalso(nombre = "Luis", primeraVez = false)
        val resultado = runBlocking { EntradaConMicrosoft(navegador).entrar() }
        assertEquals(Resultado.Entro("Luis", primeraVez = false), resultado)
    }

    @Test
    fun alRetomarConMicrosoftUnUsuarioNuevoEntraPorPrimeraVez() {
        val navegador = NavegadorConPrimeraVezFalso(primeraVez = false, pendiente = QuienEntro("Luis", primeraVez = true))
        val resultado = runBlocking { EntradaConMicrosoft(navegador).retomar() }
        assertEquals(Resultado.Entro("Luis", primeraVez = true), resultado)
        assertEquals("Retomar nunca abre el navegador", 0, navegador.abiertos.size)
    }

    @Test
    fun alTocarElBotonConUnaEntradaPendienteSeRespetaSuPrimeraVez() {
        val navegador = NavegadorConPrimeraVezFalso(primeraVez = false, pendiente = QuienEntro("Luis", primeraVez = true))
        val resultado = runBlocking { EntradaConMicrosoft(navegador).entrar() }
        assertEquals(Resultado.Entro("Luis", primeraVez = true), resultado)
        assertEquals("Con una entrada a medias no se abre otra vez el navegador", 0, navegador.abiertos.size)
    }
}
