package mx.habil.ejemplo.tienda

// Aquí se decide con qué pieza se arma la app. MainActivity llama a esta
// función y le pasa a Tienda lo que regrese: MainActivity.kt viene dado y no
// se cambia; lo que cambias tú es el cuerpo de esta función.

// La segunda Entrada, la de Microsoft.
// Por ahora regresa la que no deja pasar: la cambias en el paso 4.
fun armarEntradaConMicrosoft(navegador: NavegadorDeProveedor): EntradaQueSeRetoma =
    MicrosoftSinConectar()

// La pieza que usa la app al empezar esta lección: el botón de Microsoft
// todavía no deja pasar a nadie y no hay nada que retomar. Al cambiar la
// función de arriba, bórrala.
class MicrosoftSinConectar : EntradaQueSeRetoma {
    override suspend fun retomar(): Resultado? = null
    override suspend fun entrar(): Resultado = Resultado.Fallo
}
