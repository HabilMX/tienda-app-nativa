package mx.habil.ejemplo.tienda

// Aquí se decide con qué piezas se arma la app. MainActivity llama a estas
// dos funciones y le pasa a Tienda lo que regresen: MainActivity.kt viene
// dado y no se cambia; lo que cambias tú es el cuerpo de estas funciones.

// La segunda Entrada, la de Microsoft.
// Por ahora regresa la que no deja pasar: la cambias en el paso 4.
fun armarEntradaConMicrosoft(navegador: NavegadorDeProveedor): EntradaQueSeRetoma =
    MicrosoftSinConectar()

// El Enlace con Microsoft.
// Por ahora regresa el que no enlaza: lo cambias en el paso 7.
fun armarEnlaceConMicrosoft(enlazador: EnlazadorDeProveedor): Enlace =
    EnlaceSinConectar()

// Las dos piezas que usa la app al empezar esta lección: el botón de
// Microsoft todavía no deja pasar a nadie, no hay nada que retomar y no se
// enlaza nada. Al cambiar cada función de arriba, borra la suya.
class MicrosoftSinConectar : EntradaQueSeRetoma {
    override suspend fun retomar(): Resultado? = null
    override suspend fun entrar(): Resultado = Resultado.Fallo
}

class EnlaceSinConectar : Enlace {
    override suspend fun enlazar(): ResultadoDeEnlace = ResultadoDeEnlace.Fallo
    override suspend fun retomar(): EnlaceRetomado? = null
}
