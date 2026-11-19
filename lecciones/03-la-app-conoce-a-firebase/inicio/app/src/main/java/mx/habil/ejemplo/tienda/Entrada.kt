package mx.habil.ejemplo.tienda

// La decisión de entrar, separada de las pantallas.
// Una interfaz dice QUÉ se puede pedir, no cómo se resuelve.
interface Entrada {
    // Regresa el nombre de quien entró, o null si no pudo entrar.
    fun entrar(): String?
}

// La única implementación que trae la app en esta lección: no deja pasar a
// nadie, porque todavía no hay un proveedor (Google llega en la lección 04).
// La que sí deja pasar, EntradaFalsa, vive solo en el código de pruebas.
class EntradaSinProveedor : Entrada {
    override fun entrar(): String? = null
}
