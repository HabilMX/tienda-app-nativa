package mx.habil.ejemplo.tienda

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp

// Componente 1: el botón principal de la tienda. Ocupa el ancho disponible y toma el color
// primario de la paleta. Deshabilitado, Material lo pinta apagado y no responde al toque.
@Composable
fun BotonPrincipal(texto: String, alTocar: () -> Unit, habilitado: Boolean = true) {
    Button(
        onClick = alTocar,
        enabled = habilitado,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(texto, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
    }
}

// --- Sus variantes, para el panel de previews, el catálogo y las capturas ---------------------

data class VarianteBoton(
    val nombre: String, val texto: String, val habilitado: Boolean, val oscuro: Boolean
)

class VariantesBoton : PreviewParameterProvider<VarianteBoton> {
    // Una lista por dentro, como recomienda la documentación: getDisplayName la lee por posición.
    private val lista = listOf(
        VarianteBoton("habilitado", "Comprar", habilitado = true, oscuro = false),
        VarianteBoton("deshabilitado", "Comprar", habilitado = false, oscuro = false),
        VarianteBoton("oscuro", "Comprar", habilitado = true, oscuro = true),
        VarianteBoton(
            "texto largo", "Agregar al carrito y seguir viendo la tienda",
            habilitado = true, oscuro = false
        ),
    )
    override val values = lista.asSequence()

    // El nombre que el panel de previews le pone a cada variante, en vez de «variante 0, 1, 2…».
    override fun getDisplayName(index: Int): String? = lista.getOrNull(index)?.nombre
}

@Preview(name = "Botón principal", group = "Botones", widthDp = 320)
@Composable
fun BotonPrincipalPreview(@PreviewParameter(VariantesBoton::class) variante: VarianteBoton) {
    TemaTienda(oscuro = variante.oscuro) {
        Box(Modifier.padding(16.dp)) {
            BotonPrincipal(variante.texto, alTocar = {}, habilitado = variante.habilitado)
        }
    }
}
