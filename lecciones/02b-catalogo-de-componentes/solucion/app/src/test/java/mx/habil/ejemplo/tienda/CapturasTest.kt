package mx.habil.ejemplo.tienda

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

// Pruebas de CAPTURA de la práctica 02-B: una por cada variante de cada preview (11 en total).
// Corren en la JVM con Robolectric, sin emulador: dibujan la preview, toman una imagen y
// la comparan con la de referencia, que vive en app/src/test/capturas/ y se sube al repo.
//   ./gradlew recordRoborazziDebug   graba (o vuelve a grabar) las imágenes de referencia
//   ./gradlew verifyRoborazziDebug   compara; si un pixel cambió, la prueba queda en rojo y
//                                    deja la diferencia en app/build/outputs/roborazzi/
// Con ./gradlew testDebugUnitTest a secas, estas pruebas corren pero no comparan nada.
@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE) // sin esto, Robolectric no dibuja pixeles de verdad
class CapturasTest(private val archivo: String, private val preview: @Composable () -> Unit) {

    companion object {
        // La lista sale de los mismos proveedores de variantes que usan @PreviewParameter y
        // Showkase: una variante nueva en un proveedor es una prueba nueva aquí.
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun previews(): List<Array<Any>> {
            val botones = VariantesBoton().values.map { v ->
                caso("boton_${v.nombre}") { BotonPrincipalPreview(v) }
            }
            val tarjetas = VariantesTarjeta().values.map { v ->
                caso("tarjeta_${v.nombre}") { TarjetaProductoPreview(v) }
            }
            val etiquetas = VariantesEtiqueta().values.map { v ->
                caso("etiqueta_${v.nombre}") { EtiquetaEstadoPreview(v) }
            }
            return (botones + tarjetas + etiquetas).toList()
        }

        // «texto largo» → «texto_largo»: sin espacios en el nombre del archivo.
        private fun caso(nombre: String, preview: @Composable () -> Unit): Array<Any> =
            arrayOf(nombre.replace(' ', '_'), preview)
    }

    @get:Rule
    val regla = createComposeRule()

    @Test
    fun captura() {
        regla.setContent { preview() }
        regla.onRoot().captureRoboImage("src/test/capturas/$archivo.png")
    }
}
