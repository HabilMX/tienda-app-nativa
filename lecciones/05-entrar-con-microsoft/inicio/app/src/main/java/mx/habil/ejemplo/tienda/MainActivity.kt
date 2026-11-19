package mx.habil.ejemplo.tienda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

// La puerta de entrada de la app. Android crea esta clase al abrir la app
// y setContent le dice qué función dibuja la pantalla.
// Aquí se arman las dos Entradas que usa la app de verdad, cada una con sus
// piezas. Es el único lugar de la app que las nombra.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val entrada = crearEntrada()
        val entradaConMicrosoft = crearEntradaConMicrosoft()
        setContent { Tienda(entrada = entrada, entradaConMicrosoft = entradaConMicrosoft) }
    }

    // Arma la Entrada de la app. Las dos puertas llegan por parámetro con su
    // valor de verdad por omisión: la app llama crearEntrada() a secas, y una
    // prueba puede pasarle puertas de mentira para leer qué audiencia se pidió.
    fun crearEntrada(
        selector: SelectorDeCuentas = SelectorConCredentialManager(this),
        verificador: VerificadorDeToken = VerificadorConFirebase(),
    ): Entrada = EntradaConGoogle(
        // La audiencia: el ID de cliente web que el plugin google-services
        // sacó de tu google-services.json al compilar.
        idDeClienteWeb = getString(R.string.default_web_client_id),
        selector = selector,
        verificador = verificador,
    )

    // Arma la segunda Entrada, la de Microsoft. Igual que arriba: la puerta
    // llega por parámetro con su valor de verdad por omisión, y una prueba
    // puede pasarle una de mentira para leer qué proveedor se pidió.
    // Por ahora regresa la que no deja pasar: la cambias en el paso 4.
    fun crearEntradaConMicrosoft(
        navegador: NavegadorDeProveedor = NavegadorConFirebase(this),
    ): EntradaQueSeRetoma = MicrosoftSinConectar()
}
