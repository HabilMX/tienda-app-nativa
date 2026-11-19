package mx.habil.ejemplo.tienda

// Una entrada de mentira para las pruebas: deja pasar siempre, con el nombre
// que le des, y cuenta cuántas veces le pidieron entrar.
// Vive en src/test: es código de pruebas y la app no puede usarla.
class EntradaFalsa(private val nombre: String) : Entrada {
    var veces = 0

    override fun entrar(): String? {
        veces++
        return nombre
    }
}
