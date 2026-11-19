package mx.habil.ejemplo.tienda

// Nuevo en la lección 07: la sesión. Firebase recuerda en el teléfono a quien
// ya entró, aunque cierres la app; desde la lección 04 lo hacía, pero la app
// nunca le preguntaba. Esta interfaz es lo que Tienda le pregunta y le pide.
interface Sesion {
    // Se llama una vez, al abrir la app. Regresa el nombre de quien sigue
    // adentro, o null si nadie.
    fun quienSigueAdentro(): String?

    // Al tocar «Cerrar sesión». Al terminar, nadie sigue adentro: la próxima
    // vez que se abra la app, se pide entrar otra vez.
    suspend fun cerrar()
}
