package mx.habil.ejemplo.tienda

// La segunda Entrada de verdad: el botón «Entrar con Microsoft».
// Recibe una sola cosa y no sabe de dónde sale:
// - navegador: la puerta hacia el navegador y Firebase.
// No recibe ningún ID de cliente ni ninguna clave: los dos viven en la
// consola de Firebase, que es quien habla con Microsoft.
// Este es el archivo que escribes en la lección 05. Por ahora solo trae la
// forma, para que el proyecto compile y las pruebas puedan correr en rojo.
class EntradaConMicrosoft(
    private val navegador: NavegadorDeProveedor,
) : EntradaQueSeRetoma {

    // Al abrir la app: si Android la cerró a media entrada, aquí se recoge
    // cómo terminó. Si no había nada a medias, regresa null y no abre nada.
    override suspend fun retomar(): Resultado? {
        TODO("Lección 05, paso 4: aquí se retoma la entrada que quedó a medias")
    }

    // Al tocar el botón.
    override suspend fun entrar(): Resultado {
        TODO("Lección 05, paso 4: aquí va la entrada con Microsoft")
    }
}
