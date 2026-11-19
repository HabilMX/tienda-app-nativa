package mx.habil.ejemplo.tienda

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialUnknownException
import androidx.credentials.exceptions.NoCredentialException
import org.junit.Rule
import org.junit.Test

// Pruebas INSTRUMENTADAS: corren DENTRO de Android, en tu emulador o en tu
// teléfono (./gradlew connectedDebugAndroidTest), no en tu computadora.
// Dibujan Tienda con TU EntradaConGoogle y las dos puertas de mentira
// (Dobles.kt), tocan los botones de verdad y buscan los textos.
// Demuestran que las pantallas, el toque y tu clase funcionan dentro de
// Android, con las librerías de Credential Manager del dispositivo.
// No abren la ventana de cuentas, no le piden nada a Google y no le entregan
// nada a Firebase: las puertas son dobles. No demuestran que tu proyecto
// esté bien configurado ni que alguien pueda entrar.
// No cambies este archivo: el verifica revisa que siga igual al de solucion/.
class BotonEnAndroidTest {

    @get:Rule
    val pantalla = createComposeRule()

    private val sinCuentas =
        "No encontré una cuenta de Google disponible para entrar. Agrega una o vuelve a iniciar sesión en Google y prueba otra vez."
    private val cancelada = "Cerraste la ventana sin elegir cuenta. Toca el botón cuando quieras intentar otra vez."
    private val fallo = "No se pudo entrar. Revisa tu conexión y vuelve a intentar."

    private fun boton(texto: String) =
        hasText(texto) and
            hasClickAction() and
            SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)

    private fun noHayNingun(texto: String) =
        pantalla.onAllNodes(hasText(texto, substring = true)).assertCountEquals(0)

    private fun tocaEntrarConGoogle(selector: SelectorDeCuentas) {
        val entrada = EntradaConGoogle(
            "id-de-cliente-de-la-prueba.apps.googleusercontent.com", selector, VerificadorFalso("Ana")
        )
        pantalla.setContent { Tienda(entrada = entrada) }
        pantalla.onNode(boton("Entrar")).performClick()
        pantalla.onNode(boton("Entrar con Google")).performClick()
        pantalla.waitForIdle()
    }

    @Test
    fun enAndroidConUnaCuentaElegidaLlegaAlInicioConElNombre() {
        tocaEntrarConGoogle(SelectorFalso(credencial = credencialDeGoogleInventada()))
        pantalla.onNodeWithText("Hola, Ana").assertIsDisplayed()
        noHayNingun("Entra a tu cuenta")
    }

    @Test
    fun enAndroidSinCuentasMuestraSuMensajeYNoPasa() {
        tocaEntrarConGoogle(SelectorFalso(falla = NoCredentialException("sin cuentas, en la prueba")))
        pantalla.onNodeWithText(sinCuentas).assertIsDisplayed()
        noHayNingun("Cerraste"); noHayNingun("No se pudo"); noHayNingun("Hola")
    }

    @Test
    fun enAndroidLaCancelacionMuestraSuMensajeYNoPasa() {
        tocaEntrarConGoogle(SelectorFalso(falla = GetCredentialCancellationException("cancelada, en la prueba")))
        pantalla.onNodeWithText(cancelada).assertIsDisplayed()
        noHayNingun("No encontré"); noHayNingun("No se pudo"); noHayNingun("Hola")
    }

    @Test
    fun enAndroidElErrorMuestraSuMensajeYNoPasa() {
        tocaEntrarConGoogle(SelectorFalso(falla = GetCredentialUnknownException("falla, en la prueba")))
        pantalla.onNodeWithText(fallo).assertIsDisplayed()
        noHayNingun("No encontré"); noHayNingun("Cerraste"); noHayNingun("Hola")
    }
}
