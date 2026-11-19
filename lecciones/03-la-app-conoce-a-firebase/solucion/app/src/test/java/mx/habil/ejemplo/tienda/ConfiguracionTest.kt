package mx.habil.ejemplo.tienda

import java.io.File
import mx.habil.ejemplo.tienda.RevisorDeConfiguracion.Falta
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

// Pruebas SIMULADAS de la lección 03: corren en tu computadora, sin emulador
// y sin red. Leen el TEXTO de tu app/google-services.json y lo que el plugin
// google-services generó con él al compilar.
// No hablan con Firebase: un archivo bien formado de un proyecto que ya no
// existe pasa igual. Que Google te deje entrar se comprueba en la lección 04.
// No cambies este archivo: el verifica revisa que siga igual al de solucion/.
@RunWith(RobolectricTestRunner::class)
class ConfiguracionTest {

    private val paquete = "mx.habil.ejemplo.tienda"
    private val huellaDelFalso = "AA:AA:AA:AA:AA:AA:AA:AA:AA:AA:AA:AA:AA:AA:AA:AA:AA:AA:AA:AA"

    private fun falso(nombre: String): String =
        javaClass.getResource("/configuraciones/$nombre")!!.readText()

    // Las pruebas corren paradas en la carpeta app/ del proyecto.
    private val tuArchivo = File("google-services.json")

    private fun tuTexto(): String {
        if (!tuArchivo.isFile) fail("No existe ${tuArchivo.absoluteFile.name} en la carpeta app/")
        return tuArchivo.readText()
    }

    // --- El revisor, contra archivos inventados de src/test/resources -------
    // Si estas cuatro pasan, el revisor sí distingue un archivo completo de
    // uno al que le falta algo. Sin ellas, un «no falta nada» no diría nada.

    @Test
    fun elRevisorNoLePoneFaltasAUnArchivoCompleto() {
        assertEquals(emptySet<Falta>(), RevisorDeConfiguracion.revisar(falso("falso-completo.json"), paquete, huellaDelFalso))
    }

    @Test
    fun elRevisorVeQueFaltanLosClientesDeOAuth() {
        assertEquals(
            setOf(Falta.SIN_CLIENTE_ANDROID, Falta.SIN_CLIENTE_WEB),
            RevisorDeConfiguracion.revisar(falso("falso-sin-clientes.json"), paquete, huellaDelFalso)
        )
    }

    @Test
    fun elRevisorVeUnArchivoDeOtroPaquete() {
        assertEquals(
            setOf(Falta.OTRO_PAQUETE),
            RevisorDeConfiguracion.revisar(falso("falso-otro-paquete.json"), paquete, huellaDelFalso)
        )
    }

    @Test
    fun elRevisorVeUnaHuellaQueNoEsLaPedida() {
        val otra = "BB:BB:BB:BB:BB:BB:BB:BB:BB:BB:BB:BB:BB:BB:BB:BB:BB:BB:BB:BB"
        assertEquals(
            setOf(Falta.HUELLA_DISTINTA),
            RevisorDeConfiguracion.revisar(falso("falso-completo.json"), paquete, otra)
        )
    }

    // --- Tu archivo --------------------------------------------------------

    @Test
    fun tuConfiguracionEsDeEstePaquete() {
        val faltas = RevisorDeConfiguracion.revisar(tuTexto(), paquete, "")
        assertTrue("Tu archivo no se pudo leer como JSON", Falta.NO_ES_JSON !in faltas)
        assertTrue("Ninguna app de tu archivo es del paquete $paquete", Falta.OTRO_PAQUETE !in faltas)
    }

    @Test
    fun tuConfiguracionTraeUnClienteDeAndroid() {
        val faltas = RevisorDeConfiguracion.revisar(tuTexto(), paquete, "")
        assertTrue("Tu archivo no es de este paquete", Falta.OTRO_PAQUETE !in faltas && Falta.NO_ES_JSON !in faltas)
        assertTrue("Tu archivo no trae un cliente de OAuth de Android (tipo 1)", Falta.SIN_CLIENTE_ANDROID !in faltas)
    }

    @Test
    fun elClienteDeAndroidTraeTuHuella() {
        val texto = tuTexto()
        val huella = RevisorDeConfiguracion.huellaDeDepuracion()
        assertNotNull("No encontré tu almacén de depuración: corre ./gradlew signingReport", huella)
        assertEquals(
            "Tu archivo no trae un cliente de Android con la huella de depuración de esta computadora ($huella)",
            emptySet<Falta>(),
            RevisorDeConfiguracion.revisar(texto, paquete, huella!!) - Falta.SIN_CLIENTE_WEB
        )
    }

    @Test
    fun tuConfiguracionTraeElClienteWeb() {
        val faltas = RevisorDeConfiguracion.revisar(tuTexto(), paquete, "")
        assertTrue("Tu archivo no es de este paquete", Falta.OTRO_PAQUETE !in faltas && Falta.NO_ES_JSON !in faltas)
        assertTrue("Tu archivo no trae un cliente de OAuth web (tipo 3)", Falta.SIN_CLIENTE_WEB !in faltas)
    }

    // --- Lo que el plugin y la librería hicieron con tu archivo -----------

    // El plugin google-services convierte tu archivo en recursos de la app al
    // compilar. Aquí se leen esos recursos, como los leería la app.
    private fun recurso(nombre: String): String? {
        val app = RuntimeEnvironment.getApplication()
        val id = app.resources.getIdentifier(nombre, "string", app.packageName)
        return if (id == 0) null else app.getString(id)
    }

    @Test
    fun laAppCompiladaLlevaLosDatosDeTuArchivo() {
        val texto = tuTexto()
        assertEquals(
            "El recurso project_id de la app no es el de tu archivo (¿está aplicado el plugin google-services?)",
            RevisorDeConfiguracion.proyecto(texto), recurso("project_id")
        )
        assertEquals(
            "El recurso google_app_id de la app no es el de tu archivo",
            RevisorDeConfiguracion.idDeApp(texto, paquete), recurso("google_app_id")
        )
    }

    // Aquí la prueba le pide a Firebase que arranque (initializeApp) con los
    // recursos que generó el plugin, y lee con qué proyecto quedó. En la app
    // instalada ese arranque lo hace la librería sola al abrir; eso no se mide
    // aquí. Se pregunta por nombre para que este archivo compile también antes
    // de que agregues Firebase al proyecto.
    @Test
    fun firebaseArrancaConTuProyecto() {
        val clase = try {
            Class.forName("com.google.firebase.FirebaseApp")
        } catch (e: ClassNotFoundException) {
            fail("La app no trae la librería de Firebase entre sus dependencias"); return
        }
        val firebase = clase.getMethod("initializeApp", android.content.Context::class.java)
            .invoke(null, RuntimeEnvironment.getApplication())
        assertNotNull("Firebase no encontró su configuración entre los recursos de la app", firebase)
        val opciones = clase.getMethod("getOptions").invoke(firebase)
        val proyecto = opciones.javaClass.getMethod("getProjectId").invoke(opciones)
        assertEquals("Firebase arrancó con otro proyecto", RevisorDeConfiguracion.proyecto(tuTexto()), proyecto)
    }

    // El BoM solo elige versiones; la librería de Authentication es una
    // dependencia aparte (firebase-auth). Aquí se pregunta si su clase
    // principal viaja en la app. No se usa: entrar es la lección 04.
    @Test
    fun laAppTraeLaLibreriaDeAuthentication() {
        try {
            Class.forName("com.google.firebase.auth.FirebaseAuth")
        } catch (e: ClassNotFoundException) {
            fail("La app no trae firebase-auth entre sus dependencias")
        }
    }
}
