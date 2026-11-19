package mx.habil.ejemplo.tienda

// Aquí se decide con qué piezas se arma la app. MainActivity llama a estas
// dos funciones y le pasa a Tienda lo que regresen: MainActivity.kt viene
// dado y no se cambia; lo que cambias tú es el cuerpo de estas funciones.

// La segunda Entrada, la de Microsoft: la que escribiste en la lección 05.
fun armarEntradaConMicrosoft(navegador: NavegadorDeProveedor): EntradaQueSeRetoma =
    EntradaConMicrosoft(navegador = navegador)

// El Enlace con Microsoft.
// Por ahora regresa el que no enlaza: lo cambias en el paso 3.
fun armarEnlaceConMicrosoft(enlazador: EnlazadorDeProveedor): Enlace =
    EnlaceSinConectar()

// La pieza que usa la app al empezar esta lección: no enlaza nada y no tiene
// nada que retomar. Al cambiar la función de arriba, bórrala.
class EnlaceSinConectar : Enlace {
    override suspend fun enlazar(): ResultadoDeEnlace = ResultadoDeEnlace.Fallo
    override suspend fun retomar(): EnlaceRetomado? = null
}
