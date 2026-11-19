package mx.habil.ejemplo.tienda

import com.google.firebase.auth.OAuthProvider

// Un proyecto de Firebase de mentira, para probar el enlace con más de un
// usuario. Vive solo en src/test. Guarda usuarios en una lista y sigue la
// regla «una cuenta por correo»: un correo que ya tiene usuario no puede
// crear otro entrando con un proveedor distinto. No es Firebase: es la regla
// escrita a mano, para que las pruebas puedan preguntar «¿quién entró?».
class ProyectoFalso {

    // accesos: con qué cuentas se llega a este usuario («proveedor|correo»).
    class Usuario(val id: String, val correo: String, val nombre: String, val accesos: MutableSet<String>)

    val usuarios = mutableListOf<Usuario>()
    var sesion: Usuario? = null
        private set
    var enlacesPedidos = 0
        private set

    fun salir() { sesion = null }

    private fun entra(proveedor: String, correo: String, nombre: String): String {
        val acceso = "$proveedor|$correo"
        val suyo = usuarios.firstOrNull { acceso in it.accesos }
        if (suyo != null) { sesion = suyo; return suyo.nombre }
        // Ese correo ya tiene usuario, creado con otro proveedor.
        if (usuarios.any { it.correo == correo }) throw colisionInventada()
        val nuevo = Usuario("usuario-${usuarios.size + 1}", correo, nombre, mutableSetOf(acceso))
        usuarios += nuevo
        sesion = nuevo
        return nuevo.nombre
    }

    // La puerta 2 (lección 04) para una persona que elige su cuenta de Google.
    fun verificadorDe(correo: String, nombre: String) = object : VerificadorDeToken {
        override suspend fun verificar(tokenDeIdentidad: String): String = entra("google.com", correo, nombre)
    }

    // La puerta 3 para una persona que usa su cuenta de Microsoft.
    fun navegadorDe(correo: String, nombre: String) = object : NavegadorDeProveedor {
        override suspend fun pendiente(): String? = null

        override suspend fun abrir(proveedor: OAuthProvider): String = entra(proveedor.providerId ?: "", correo, nombre)

        override suspend fun enlazar(proveedor: OAuthProvider) {
            enlacesPedidos++
            val actual = sesion ?: throw NadieHaEntrado()
            actual.accesos += "${proveedor.providerId}|$correo"
        }
    }
}
