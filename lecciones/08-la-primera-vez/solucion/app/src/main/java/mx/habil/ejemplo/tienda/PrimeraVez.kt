package mx.habil.ejemplo.tienda

import com.google.firebase.auth.AuthResult

// Nuevo en la lección 08: ¿es la primera vez que esta persona entra?
// Lo decide el resultado del inicio de sesión que contesta Firebase
// (AuthResult). Lo llaman las puertas de verdad (Puertas.kt y
// PuertaDelNavegador.kt), con Google y con Microsoft.
// - additionalUserInfo puede venir null: la referencia lo marca @Nullable.
//   Sin esa información no se sabe si es nuevo, y entonces no hay bienvenida.
// - isNewUser dice si el usuario es nuevo o ya existía.
fun esPrimeraVez(resultado: AuthResult): Boolean =
    resultado.additionalUserInfo?.isNewUser == true
