package mx.habil.ejemplo.tienda

import com.google.firebase.auth.AuthResult

// Nuevo en la lección 08: ¿es la primera vez que esta persona entra?
// Lo decide el resultado del inicio de sesión que contesta Firebase
// (AuthResult). Lo llaman las puertas de verdad (Puertas.kt y
// PuertaDelNavegador.kt), con Google y con Microsoft.
// Lo escribes tú en el paso 3.
fun esPrimeraVez(resultado: AuthResult): Boolean =
    TODO("Lección 08, paso 3")
