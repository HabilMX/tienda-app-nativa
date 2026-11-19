package mx.habil.ejemplo.tienda

// La Entrada de verdad: el botón «Entrar con Google».
// Recibe tres cosas y no sabe de dónde salen:
// - idDeClienteWeb: para quién se pide el token (la audiencia).
// - selector: la puerta hacia Credential Manager.
// - verificador: la puerta hacia Firebase.
// Este es el archivo que escribes en la lección 04. Por ahora solo trae la
// forma, para que el proyecto compile y las pruebas puedan correr en rojo.
class EntradaConGoogle(
    private val idDeClienteWeb: String,
    private val selector: SelectorDeCuentas,
    private val verificador: VerificadorDeToken,
) : Entrada {

    override suspend fun entrar(): Resultado {
        TODO("Lección 04, paso 3: aquí va la entrada con Google")
    }
}
