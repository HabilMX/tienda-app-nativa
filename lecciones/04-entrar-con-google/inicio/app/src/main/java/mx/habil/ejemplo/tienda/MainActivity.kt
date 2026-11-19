package mx.habil.ejemplo.tienda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

// La puerta de entrada de la app. Android crea esta clase al abrir la app
// y setContent le dice qué función dibuja la pantalla.
// Aquí se arma la Entrada que usa la app de verdad.
// Es el único lugar de la app que la nombra.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val entrada = crearEntrada()
        setContent { Tienda(entrada = entrada) }
    }

    // Arma la Entrada de la app. Las dos puertas llegan por parámetro con su
    // valor de verdad por omisión: la app llama crearEntrada() a secas, y una
    // prueba puede pasarle puertas de mentira para leer qué audiencia se pidió.
    // Por ahora regresa la entrada que no deja pasar: la cambias en el paso 3.
    fun crearEntrada(
        selector: SelectorDeCuentas = SelectorConCredentialManager(this),
        verificador: VerificadorDeToken = VerificadorConFirebase(),
    ): Entrada = EntradaSinProveedor()
}
