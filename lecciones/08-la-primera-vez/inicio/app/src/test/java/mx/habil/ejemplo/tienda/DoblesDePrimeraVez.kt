package mx.habil.ejemplo.tienda

import android.os.Parcel
import com.google.firebase.auth.AdditionalUserInfo
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.OAuthProvider

// Dobles de la lección 08, para las pruebas. Viven solo en src/test. Ninguno
// toca Firebase, Credential Manager, el navegador ni la red.

// --- El resultado de un inicio de sesión, de mentira --------------------------
// AuthResult y AdditionalUserInfo son interfaces de Firebase: aquí se llenan a
// mano. Las dos piden además dos funciones de Android para viajar entre
// procesos (Parcel); ninguna prueba las usa.

// La información adicional: solo importa isNewUser.
class InformacionFalsa(private val nuevo: Boolean) : AdditionalUserInfo {
    override fun isNewUser(): Boolean = nuevo
    override fun getProviderId(): String? = null
    override fun getUsername(): String? = null
    override fun getProfile(): Map<String, Any>? = null
    override fun describeContents(): Int = 0
    override fun writeToParcel(destino: Parcel, banderas: Int) = Unit
}

// El resultado: sin usuario y sin credencial (ninguna prueba los lee), con la
// información adicional que le digas, o null.
class ResultadoFalso(private val informacion: AdditionalUserInfo?) : AuthResult {
    override fun getAdditionalUserInfo(): AdditionalUserInfo? = informacion
    override fun getUser(): FirebaseUser? = null
    override fun getCredential(): AuthCredential? = null
    override fun describeContents(): Int = 0
    override fun writeToParcel(destino: Parcel, banderas: Int) = Unit
}

// --- Las puertas, contestando si es la primera vez -----------------------------

// La puerta 2 (Google). Contesta el nombre y si es nuevo. verificar() a secas
// contesta solo el nombre, como en la 04: si tu entrada sigue llamando ésa,
// nunca se entera de que es la primera vez.
class VerificadorConPrimeraVezFalso(
    private val nombre: String = "Ana",
    private val primeraVez: Boolean,
) : VerificadorDeToken {
    val tokens = mutableListOf<String>()

    override suspend fun verificar(tokenDeIdentidad: String): String {
        tokens += tokenDeIdentidad
        return nombre
    }

    override suspend fun verificarConPrimeraVez(tokenDeIdentidad: String): QuienEntro {
        tokens += tokenDeIdentidad
        return QuienEntro(nombre, primeraVez)
    }
}

// La puerta 3 (Microsoft). Igual: las funciones de la 05 contestan solo el
// nombre; las de la 08, además si es nuevo.
// - pendiente: quién quedó a medias (null si nadie).
class NavegadorConPrimeraVezFalso(
    private val nombre: String = "Luis",
    private val primeraVez: Boolean,
    private val pendiente: QuienEntro? = null,
) : NavegadorDeProveedor {
    val abiertos = mutableListOf<OAuthProvider>()

    override suspend fun pendiente(): String? = pendiente?.nombre

    override suspend fun abrir(proveedor: OAuthProvider): String {
        abiertos += proveedor
        return nombre
    }

    override suspend fun pendienteConPrimeraVez(): QuienEntro? = pendiente

    override suspend fun abrirConPrimeraVez(proveedor: OAuthProvider): QuienEntro {
        abiertos += proveedor
        return QuienEntro(nombre, primeraVez)
    }
}

// --- El proyecto de mentira de la lección 06, contestando si es nuevo ----------
// ProyectoFalso.kt no cambia: estas funciones lo envuelven. «Es nuevo» quiere
// decir lo mismo que en Firebase: esa entrada creó un usuario que no existía.

fun ProyectoFalso.verificadorConPrimeraVezDe(correo: String, nombre: String): VerificadorDeToken {
    val deLaLeccion06 = verificadorDe(correo, nombre)
    return object : VerificadorDeToken {
        override suspend fun verificar(tokenDeIdentidad: String): String = deLaLeccion06.verificar(tokenDeIdentidad)

        override suspend fun verificarConPrimeraVez(tokenDeIdentidad: String): QuienEntro {
            val antes = usuarios.size
            val quien = deLaLeccion06.verificar(tokenDeIdentidad)
            return QuienEntro(quien, primeraVez = usuarios.size > antes)
        }
    }
}

fun ProyectoFalso.navegadorConPrimeraVezDe(correo: String, nombre: String): NavegadorDeProveedor {
    val deLaLeccion06 = navegadorDe(correo, nombre)
    return object : NavegadorDeProveedor {
        override suspend fun pendiente(): String? = null

        override suspend fun abrir(proveedor: OAuthProvider): String = deLaLeccion06.abrir(proveedor)

        override suspend fun abrirConPrimeraVez(proveedor: OAuthProvider): QuienEntro {
            val antes = usuarios.size
            val quien = deLaLeccion06.abrir(proveedor)
            return QuienEntro(quien, primeraVez = usuarios.size > antes)
        }
    }
}
