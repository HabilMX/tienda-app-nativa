package mx.habil.ejemplo.tienda

// La sesión de la tienda: le pregunta al teléfono a quién recuerda y, al
// cerrar, le pide que lo olvide.
// Este archivo lo escribes en el paso 3. Por ahora solo trae la forma.
class SesionDeLaTienda(
    private val recuerdo: RecuerdoDeLaSesion,
) : Sesion {

    // Al abrir la app.
    override fun quienSigueAdentro(): String? {
        TODO("Lección 07, paso 3: aquí se pregunta quién sigue adentro")
    }

    // Al tocar «Cerrar sesión».
    override suspend fun cerrar() {
        TODO("Lección 07, paso 3: aquí se cierra la sesión")
    }
}
