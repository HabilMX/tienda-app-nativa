package mx.habil.ejemplo.tienda

// Una entrada de mentira para las pruebas: contesta siempre el resultado
// que le des y cuenta cuántas veces le pidieron entrar.
// Vive en src/test: es código de pruebas y la app no puede usarla.
class EntradaFalsa(private val resultado: Resultado) : Entrada {
    var veces = 0

    override suspend fun entrar(): Resultado {
        veces++
        return resultado
    }
}
