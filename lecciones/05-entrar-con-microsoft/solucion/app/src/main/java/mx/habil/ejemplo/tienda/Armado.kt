package mx.habil.ejemplo.tienda

// Aquí se decide con qué pieza se arma la app. MainActivity llama a esta
// función y le pasa a Tienda lo que regrese: MainActivity.kt viene dado y no
// se cambia; lo que cambias tú es el cuerpo de esta función.

// La segunda Entrada, la de Microsoft.
fun armarEntradaConMicrosoft(navegador: NavegadorDeProveedor): EntradaQueSeRetoma =
    EntradaConMicrosoft(navegador = navegador)
