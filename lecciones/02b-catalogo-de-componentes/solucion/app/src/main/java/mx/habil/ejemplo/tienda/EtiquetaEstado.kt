package mx.habil.ejemplo.tienda

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp

// Los tres estados de una orden, cada uno con el texto que se ve en la etiqueta.
enum class EstadoOrden(val texto: String) {
    RECIBIDA("Recibida"),
    ENVIADA("Enviada"),
    ENTREGADA("Entregada"),
}

// El color de cada estado sale de la paleta. Es el mismo en claro y en oscuro.
fun colorDe(estado: EstadoOrden): Color = when (estado) {
    EstadoOrden.RECIBIDA -> AzulRecibida
    EstadoOrden.ENVIADA -> AmbarEnviada
    EstadoOrden.ENTREGADA -> VerdeTienda
}

// Componente 3: la etiqueta del estado de una orden. Una píldora de color con letra blanca.
@Composable
fun EtiquetaEstado(estado: EstadoOrden) {
    Surface(shape = RoundedCornerShape(50), color = colorDe(estado)) {
        Text(
            estado.texto,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

// --- Sus variantes ----------------------------------------------------------------------------

data class VarianteEtiqueta(val nombre: String, val estado: EstadoOrden, val oscuro: Boolean)

class VariantesEtiqueta : PreviewParameterProvider<VarianteEtiqueta> {
    // Una lista por dentro, como recomienda la documentación: getDisplayName la lee por posición.
    private val lista = listOf(
        VarianteEtiqueta("recibida", EstadoOrden.RECIBIDA, oscuro = false),
        VarianteEtiqueta("enviada", EstadoOrden.ENVIADA, oscuro = false),
        VarianteEtiqueta("entregada", EstadoOrden.ENTREGADA, oscuro = false),
        VarianteEtiqueta("entregada en oscuro", EstadoOrden.ENTREGADA, oscuro = true),
    )
    override val values = lista.asSequence()

    // El nombre que el panel de previews le pone a cada variante, en vez de «variante 0, 1, 2…».
    override fun getDisplayName(index: Int): String? = lista.getOrNull(index)?.nombre
}

@Preview(name = "Etiqueta de estado", group = "Etiquetas")
@Composable
fun EtiquetaEstadoPreview(@PreviewParameter(VariantesEtiqueta::class) variante: VarianteEtiqueta) {
    TemaTienda(oscuro = variante.oscuro) {
        Box(Modifier.padding(16.dp)) {
            EtiquetaEstado(variante.estado)
        }
    }
}
