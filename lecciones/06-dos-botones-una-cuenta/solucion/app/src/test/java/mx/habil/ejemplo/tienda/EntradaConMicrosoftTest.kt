package mx.habil.ejemplo.tienda

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Pruebas SIMULADAS de EntradaConMicrosoft: corren en tu computadora, sin
// emulador, sin navegador, sin cuenta y sin red. Le dan a tu clase una
// puerta de mentira (DoblesDeMicrosoft.kt) y miran dos cosas: qué le pidió y
// qué contestó.
// No hablan con Microsoft ni con Firebase: no demuestran que tu registro en
// Entra esté bien hecho, que la clave sea la correcta ni que alguien pueda
// entrar. Eso es el nivel real.
// No cambies este archivo: el verifica revisa que siga igual al de solucion/.
@RunWith(RobolectricTestRunner::class)
class EntradaConMicrosoftTest {

    // --- 5.2 · Qué le pide al navegador ------------------------------------

    @Test
    fun pideElProveedorMicrosoft() {
        val navegador = NavegadorFalso()
        runBlocking { EntradaConMicrosoft(navegador).entrar() }
        assertEquals("Un toque debe abrir el navegador una sola vez", 1, navegador.abiertos.size)
        assertEquals(
            "El proveedor debe ser microsoft.com y fue ${navegador.abiertos.single().providerId}",
            "microsoft.com", navegador.abiertos.single().providerId
        )
    }

    @Test
    fun elNombreLoPoneElNavegador() {
        val resultado = runBlocking { EntradaConMicrosoft(NavegadorFalso(nombre = "Luis")).entrar() }
        assertEquals(Resultado.Entro("Luis"), resultado)
    }

    // --- 5.2 · Retomar lo que quedó a medias -------------------------------

    @Test
    fun alTocarElBotonPrimeroPreguntaPorLoPendiente() {
        val navegador = NavegadorFalso(nombre = "Ana", pendiente = "Luis")
        val resultado = runBlocking { EntradaConMicrosoft(navegador).entrar() }
        assertEquals("Con una entrada a medias, entra quien quedó pendiente", Resultado.Entro("Luis"), resultado)
        assertEquals("Con una entrada a medias no se abre otra vez el navegador", 0, navegador.abiertos.size)
    }

    @Test
    fun retomarConPendienteContestaQuienEntro() {
        val navegador = NavegadorFalso(pendiente = "Luis")
        val resultado = runBlocking { EntradaConMicrosoft(navegador).retomar() }
        assertEquals(Resultado.Entro("Luis"), resultado)
        assertEquals("Retomar nunca abre el navegador", 0, navegador.abiertos.size)
    }

    @Test
    fun retomarSinPendienteContestaNullYNoAbreNada() {
        val navegador = NavegadorFalso(pendiente = null)
        val resultado = runBlocking { EntradaConMicrosoft(navegador).retomar() }
        assertEquals("Sin nada a medias, retomar regresa null", null, resultado)
        assertEquals("Retomar debe preguntar por lo pendiente una vez", 1, navegador.preguntasPorPendiente)
        assertEquals("Retomar nunca abre el navegador", 0, navegador.abiertos.size)
    }

    @Test
    fun loPendienteQueTerminoEnColisionSeRetomaComoColision() {
        val navegador = NavegadorFalso(fallaPendiente = colisionInventada())
        val resultado = runBlocking { EntradaConMicrosoft(navegador).retomar() }
        assertEquals(Resultado.CuentaConOtroProveedor, resultado)
    }

    @Test
    fun loPendienteQueTerminoEnFallaSeRetomaComoFallo() {
        val navegador = NavegadorFalso(fallaPendiente = IllegalStateException("falla, en la prueba"))
        val resultado = runBlocking { EntradaConMicrosoft(navegador).retomar() }
        assertEquals(Resultado.Fallo, resultado)
    }

    // --- 5.2 · Las formas de no entrar -------------------------------------

    @Test
    fun laCancelacionContestaCancelada() {
        val resultado = runBlocking {
            EntradaConMicrosoft(NavegadorFalso(fallaAlAbrir = cancelacionInventada())).entrar()
        }
        assertEquals(Resultado.Cancelada, resultado)
    }

    @Test
    fun laColisionContestaCuentaConOtroProveedor() {
        val resultado = runBlocking {
            EntradaConMicrosoft(NavegadorFalso(fallaAlAbrir = colisionInventada())).entrar()
        }
        assertEquals(Resultado.CuentaConOtroProveedor, resultado)
    }

    @Test
    fun otraColisionQueNoEsDeProveedorContestaFallo() {
        val resultado = runBlocking {
            EntradaConMicrosoft(NavegadorFalso(fallaAlAbrir = otraColisionInventada())).entrar()
        }
        assertEquals(
            "Una colisión con otro código no es «ese correo ya entró con otro botón»", Resultado.Fallo, resultado
        )
    }

    @Test
    fun otraFallaDelNavegadorContestaFalloYNoCancelada() {
        val resultado = runBlocking {
            EntradaConMicrosoft(NavegadorFalso(fallaAlAbrir = otraFallaWebInventada())).entrar()
        }
        assertEquals("Una falla web que no es cancelación es un error", Resultado.Fallo, resultado)
    }

    @Test
    fun cualquierOtraFallaContestaFallo() {
        val resultado = runBlocking {
            EntradaConMicrosoft(NavegadorFalso(fallaAlAbrir = IllegalStateException("falla, en la prueba"))).entrar()
        }
        assertEquals(Resultado.Fallo, resultado)
    }
}
