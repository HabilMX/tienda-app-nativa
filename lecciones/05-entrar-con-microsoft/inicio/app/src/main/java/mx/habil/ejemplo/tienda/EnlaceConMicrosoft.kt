package mx.habil.ejemplo.tienda

// Enlaza Microsoft al usuario que ya entró con otro proveedor.
// No crea un usuario ni cambia quién entró: le suma una forma de entrar.
// Este archivo lo escribes en el paso 7. Por ahora solo trae la forma.
class EnlaceConMicrosoft(
    private val enlazador: EnlazadorDeProveedor,
) : Enlace {

    // Al tocar «Enlazar Microsoft».
    override suspend fun enlazar(): ResultadoDeEnlace {
        TODO("Lección 05, paso 7: aquí se enlaza Microsoft al usuario que ya entró")
    }

    // Al abrir la app: si Android la cerró a medio enlace, aquí se recoge
    // cómo terminó. Es un resultado de enlace, no una entrada.
    override suspend fun retomar(): EnlaceRetomado? {
        TODO("Lección 05, paso 7: aquí se retoma el enlace que quedó a medias")
    }
}
