package mx.habil.ejemplo.tienda

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// La paleta de la tienda. La da la lección: nadie la diseña.
// No cambies este archivo: el verifica revisa que siga igual al de solucion/. Para el «rompe un
// color» (paso 6) lo cambias tú un momento y lo regresas; el verifica hace lo mismo en una copia.
// Un color se escribe 0xAARRGGBB: opacidad, rojo, verde y azul, dos cifras hexadecimales cada uno.
val VerdeTienda = Color(0xFF0B6E4F)
val VerdeClaro = Color(0xFF7FD8B5)
val Tinta = Color(0xFF1A1C1A)
val Papel = Color(0xFFF7F8F5)
val Noche = Color(0xFF111412)
val NocheTarjeta = Color(0xFF1E2421)
val Niebla = Color(0xFFE3E6E2)
val GrisTenue = Color(0xFF5E6660)
val GrisNiebla = Color(0xFFB8C0BA)
val AzulRecibida = Color(0xFF1F5FAD)
val AmbarEnviada = Color(0xFF8A5300)

// Claro y oscuro: los mismos papeles (primario, fondo, superficie, texto), con otros colores.
private val PaletaClara = lightColorScheme(
    primary = VerdeTienda,
    onPrimary = Color.White,
    background = Papel,
    onBackground = Tinta,
    surface = Color.White,
    onSurface = Tinta,
    onSurfaceVariant = GrisTenue,
)

private val PaletaOscura = darkColorScheme(
    primary = VerdeClaro,
    onPrimary = Noche,
    background = Noche,
    onBackground = Niebla,
    surface = NocheTarjeta,
    onSurface = Niebla,
    onSurfaceVariant = GrisNiebla,
)

// La tipografía de la tienda: cuatro estilos, con la letra que trae Android.
val TipografiaTienda = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 34.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 22.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 20.sp),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp
    ),
)

// Envuelve cualquier contenido con la paleta y la tipografía de la tienda.
// oscuro lo decide quien llama: la app lo toma del teléfono; cada preview, de su variante.
@Composable
fun TemaTienda(oscuro: Boolean, contenido: @Composable () -> Unit) {
    val colores = if (oscuro) PaletaOscura else PaletaClara
    MaterialTheme(colorScheme = colores, typography = TipografiaTienda) {
        Surface(color = colores.background, content = contenido)
    }
}
