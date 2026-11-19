package mx.habil.ejemplo.tienda

// Aquí se decide con qué piezas se arma la app. MainActivity llama a estas
// dos funciones y le pasa a Tienda lo que regresen: MainActivity.kt viene
// dado y no se cambia; lo que cambias tú es el cuerpo de estas funciones.

// La segunda Entrada, la de Microsoft.
fun armarEntradaConMicrosoft(navegador: NavegadorDeProveedor): EntradaQueSeRetoma =
    EntradaConMicrosoft(navegador = navegador)

// El Enlace con Microsoft.
fun armarEnlaceConMicrosoft(enlazador: EnlazadorDeProveedor): Enlace =
    EnlaceConMicrosoft(enlazador = enlazador)
