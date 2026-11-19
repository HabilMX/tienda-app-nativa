package mx.habil.ejemplo.tienda

// Dobles de la puerta de la sesión (PuertaDeLaSesion.kt), para las pruebas.
// Viven solo en src/test. Ninguno toca Firebase, Credential Manager ni la red.

// Doble suelto: recuerda a quien le digas y cuenta qué le pidieron.
// - usuario: a quién recuerda «el teléfono» (null si a nadie).
// - fallaAlAvisar: la excepción con la que termina el aviso a los proveedores.
class RecuerdoFalso(
    var usuario: String? = null,
    private val fallaAlAvisar: Throwable? = null,
) : RecuerdoDeLaSesion {
    var preguntas = 0
    var olvidosEnFirebase = 0
    var avisosAlTelefono = 0

    override fun usuarioActual(): String? {
        preguntas++
        return usuario
    }

    override fun olvidarEnFirebase() {
        olvidosEnFirebase++
        usuario = null
    }

    override suspend fun olvidarEnElTelefono() {
        avisosAlTelefono++
        if (fallaAlAvisar != null) throw fallaAlAvisar
    }
}

// La misma puerta, pero conectada al proyecto de mentira de la lección 06
// (ProyectoFalso.kt): «el teléfono» recuerda a quien haya entrado ahí, y
// olvidar es salir de ese proyecto. Así una prueba puede entrar con Google,
// «cerrar la app», volver a abrirla y preguntar a quién recuerda.
class RecuerdoDelProyecto(private val proyecto: ProyectoFalso) : RecuerdoDeLaSesion {
    var avisosAlTelefono = 0

    override fun usuarioActual(): String? = proyecto.sesion?.nombre

    override fun olvidarEnFirebase() = proyecto.salir()

    override suspend fun olvidarEnElTelefono() {
        avisosAlTelefono++
    }
}
