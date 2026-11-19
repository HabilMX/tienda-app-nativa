package mx.habil.ejemplo.tienda

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp

// Componente 2: la tarjeta de un producto. El nombre ocupa a lo más dos renglones y, si no cabe,
// termina en «…»; el precio va en el color primario. Recibe el precio ya escrito como texto.
@Composable
fun TarjetaProducto(nombre: String, precio: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                nombre,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                precio,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// --- Sus variantes ----------------------------------------------------------------------------

data class VarianteTarjeta(
    val nombre: String, val producto: String, val precio: String, val oscuro: Boolean
)

class VariantesTarjeta : PreviewParameterProvider<VarianteTarjeta> {
    // Una lista por dentro, como recomienda la documentación: getDisplayName la lee por posición.
    private val lista = listOf(
        VarianteTarjeta("claro", "Audífonos inalámbricos", "$899.00", oscuro = false),
        VarianteTarjeta("oscuro", "Audífonos inalámbricos", "$899.00", oscuro = true),
        VarianteTarjeta(
            "texto largo",
            "Juego de seis tazas de cerámica esmaltada hechas a mano en Dolores Hidalgo, " +
                "Guanajuato",
            "$1,249.00",
            oscuro = false
        ),
    )
    override val values = lista.asSequence()

    // El nombre que el panel de previews le pone a cada variante, en vez de «variante 0, 1, 2…».
    override fun getDisplayName(index: Int): String? = lista.getOrNull(index)?.nombre
}

@Preview(name = "Tarjeta de producto", group = "Tarjetas", widthDp = 320)
@Composable
fun TarjetaProductoPreview(@PreviewParameter(VariantesTarjeta::class) variante: VarianteTarjeta) {
    TemaTienda(oscuro = variante.oscuro) {
        Box(Modifier.padding(16.dp)) {
            TarjetaProducto(variante.producto, variante.precio)
        }
    }
}
