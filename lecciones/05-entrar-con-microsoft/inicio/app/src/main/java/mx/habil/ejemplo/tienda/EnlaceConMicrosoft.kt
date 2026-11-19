package mx.habil.ejemplo.tienda

// Enlaza Microsoft al usuario que ya entró con otro proveedor.
// Usa la misma puerta que EntradaConMicrosoft, por otra de sus funciones: no
// crea un usuario ni cambia quién entró; le suma una forma de entrar.
// Este archivo lo escribes en el paso 7. Por ahora solo trae la forma.
class EnlaceConMicrosoft(
    private val navegador: NavegadorDeProveedor,
) : Enlace {

    override suspend fun enlazar(): ResultadoDeEnlace {
        TODO("Lección 05, paso 7: aquí se enlaza Microsoft al usuario que ya entró")
    }
}
