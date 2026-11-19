package mx.habil.ejemplo.tienda

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Pruebas SIMULADAS de EnlaceConMicrosoft: corren en tu computadora, sin
// navegador, sin cuenta y sin red. Las primeras le dan a tu clase la puerta
// de mentira y miran qué le pidió y qué contestó. Las últimas usan un
// proyecto de mentira (ProyectoFalso.kt) con dos personas: Ana, con el mismo
// correo en Google y en Microsoft, y Luis, con otro correo.
// «El mismo usuario» aquí es el del proyecto de mentira, que sigue la regla
// «una cuenta por correo» escrita a mano. No demuestra que Firebase enlace
// tus cuentas de verdad: eso es el nivel real.
// No cambies este archivo: el verifica revisa que siga igual al de solucion/.
@RunWith(RobolectricTestRunner::class)
class EnlaceConMicrosoftTest {

    // --- 5.4 · Qué le pide al navegador y qué contesta ---------------------

    @Test
    fun enlazaConElProveedorMicrosoftSinAbrirOtraEntrada() {
        val navegador = NavegadorFalso()
        val resultado = runBlocking { EnlaceConMicrosoft(navegador).enlazar() }
        assertEquals(ResultadoDeEnlace.Enlazado, resultado)
        assertEquals("Enlazar pide el enlace una sola vez", 1, navegador.enlazados.size)
        assertEquals("microsoft.com", navegador.enlazados.single().providerId)
        assertEquals("Enlazar no es entrar: no debe abrir una entrada nueva", 0, navegador.abiertos.size)
    }

    @Test
    fun sinHaberEntradoContestaSinSesion() {
        val resultado = runBlocking { EnlaceConMicrosoft(NavegadorFalso(fallaAlEnlazar = NadieHaEntrado())).enlazar() }
        assertEquals(ResultadoDeEnlace.SinSesion, resultado)
    }

    @Test
    fun laCancelacionContestaCancelado() {
        val resultado = runBlocking { EnlaceConMicrosoft(NavegadorFalso(fallaAlEnlazar = cancelacionInventada())).enlazar() }
        assertEquals(ResultadoDeEnlace.Cancelado, resultado)
    }

    @Test
    fun cualquierOtraFallaContestaFallo() {
        val resultado = runBlocking { EnlaceConMicrosoft(NavegadorFalso(fallaAlEnlazar = otraFallaWebInventada())).enlazar() }
        assertEquals(ResultadoDeEnlace.Fallo, resultado)
    }

    // --- 5.4 · Dos personas, un proyecto de mentira ------------------------

    private val correoDeAna = "ana@correo.invalid"
    private val correoDeLuis = "luis@correo.invalid"

    private fun conGoogle(p: ProyectoFalso, correo: String, nombre: String) =
        EntradaConGoogle("id-de-la-prueba", SelectorFalso(credencial = credencialDeGoogleInventada()), p.verificadorDe(correo, nombre))

    @Test
    fun antesDeEnlazarElMismoCorreoChocaYNoCreaOtroUsuario() {
        val p = ProyectoFalso()
        assertEquals(Resultado.Entro("Ana"), runBlocking { conGoogle(p, correoDeAna, "Ana").entrar() })
        p.salir()
        val conMicrosoft = runBlocking { EntradaConMicrosoft(p.navegadorDe(correoDeAna, "Ana")).entrar() }
        assertEquals(Resultado.CuentaConOtroProveedor, conMicrosoft)
        assertEquals("La colisión no crea un segundo usuario", 1, p.usuarios.size)
    }

    @Test
    fun despuesDeEnlazarLosDosAccesosLleganAlMismoUsuario() {
        val p = ProyectoFalso()
        val microsoftDeAna = p.navegadorDe(correoDeAna, "Ana")
        runBlocking { conGoogle(p, correoDeAna, "Ana").entrar() }
        val conGoogle = p.sesion!!.id
        assertEquals(ResultadoDeEnlace.Enlazado, runBlocking { EnlaceConMicrosoft(microsoftDeAna).enlazar() })
        p.salir()
        assertEquals(Resultado.Entro("Ana"), runBlocking { EntradaConMicrosoft(microsoftDeAna).entrar() })
        assertEquals("Con Microsoft debe llegar al usuario que creó Google", conGoogle, p.sesion!!.id)
        p.salir()
        runBlocking { conGoogle(p, correoDeAna, "Ana").entrar() }
        assertEquals("Con Google sigue llegando al mismo usuario", conGoogle, p.sesion!!.id)
        assertEquals("Enlazar no crea usuarios", 1, p.usuarios.size)
    }

    @Test
    fun otraPersonaSigueSiendoOtroUsuario() {
        val p = ProyectoFalso()
        runBlocking { conGoogle(p, correoDeAna, "Ana").entrar() }
        val ana = p.sesion!!.id
        runBlocking { EnlaceConMicrosoft(p.navegadorDe(correoDeAna, "Ana")).enlazar() }
        p.salir()
        assertEquals(Resultado.Entro("Luis"), runBlocking { EntradaConMicrosoft(p.navegadorDe(correoDeLuis, "Luis")).entrar() })
        assertNotEquals("Luis es otra persona: no debe llegar al usuario de Ana", ana, p.sesion!!.id)
        assertEquals(2, p.usuarios.size)
    }

    @Test
    fun enlazarSinHaberEntradoNoEnlazaNada() {
        val p = ProyectoFalso()
        val microsoftDeAna = p.navegadorDe(correoDeAna, "Ana")
        runBlocking { conGoogle(p, correoDeAna, "Ana").entrar() }
        p.salir()
        assertEquals(ResultadoDeEnlace.SinSesion, runBlocking { EnlaceConMicrosoft(microsoftDeAna).enlazar() })
        assertEquals(
            "Sin haber entrado no quedó enlazado: Microsoft debe seguir chocando",
            Resultado.CuentaConOtroProveedor, runBlocking { EntradaConMicrosoft(microsoftDeAna).entrar() }
        )
    }
}
