package mx.habil.ejemplo.tienda

import android.app.Application
import com.google.firebase.FirebaseApp

// La «app» que usan las pruebas simuladas en lugar de la de verdad (lo dice
// src/test/resources/robolectric.properties).
// En un teléfono, Firebase se prepara solo al abrir la app, con los datos de
// tu google-services.json. En las pruebas de tu computadora eso no ocurrió
// (medido el 18-nov-2026: «Default FirebaseApp is not initialized»), así que
// aquí se prepara a mano. Preparar no es conectarse: no sale a la red.
class AppDePruebas : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
