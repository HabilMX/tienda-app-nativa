package mx.habil.ejemplo.tienda

// Aquí se decide con qué piezas se arma la app. MainActivity llama a estas
// funciones y le pasa a Tienda lo que regresen: MainActivity.kt viene dado y
// no se cambia; lo que cambias tú es el cuerpo de estas funciones.

// La segunda Entrada, la de Microsoft: la que escribiste en la lección 05.
fun armarEntradaConMicrosoft(navegador: NavegadorDeProveedor): EntradaQueSeRetoma =
    EntradaConMicrosoft(navegador = navegador)

// El Enlace con Microsoft: el que escribiste en la lección 06.
fun armarEnlaceConMicrosoft(enlazador: EnlazadorDeProveedor): Enlace =
    EnlaceConMicrosoft(enlazador = enlazador)

// La Sesión.
// Por ahora regresa la que no recuerda a nadie: la cambias en el paso 3.
fun armarSesion(recuerdo: RecuerdoDeLaSesion): Sesion =
    SesionSinConectar()

// La pieza que usa la app al empezar esta lección: no le pregunta nada al
// teléfono y no cierra nada. Así se portaba la app de la lección 06: abría
// siempre en la bienvenida. Al cambiar la función de arriba, bórrala.
class SesionSinConectar : Sesion {
    override fun quienSigueAdentro(): String? = null
    override suspend fun cerrar() {}
}
