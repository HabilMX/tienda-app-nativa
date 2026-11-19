package mx.habil.ejemplo.tienda

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.airbnb.android.showkase.annotation.ShowkaseRoot
import com.airbnb.android.showkase.annotation.ShowkaseRootModule
import com.airbnb.android.showkase.models.Showkase

// Vive en src/debug: solo existe en la app de depuración, nunca en la que se publica.

// La raíz del catálogo. Showkase busca los @Preview del módulo y, al compilar, genera el
// código que los junta. Basta con que esta clase exista y lleve @ShowkaseRoot.
@ShowkaseRoot
class CatalogoTienda : ShowkaseRootModule

// Un segundo ícono, «Catálogo», que abre el navegador de Showkase y se cierra.
// getBrowserIntent lo genera Showkase al compilar: si Android Studio lo marca en rojo,
// compila una vez.
class CatalogoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Showkase.getBrowserIntent(this))
        finish()
    }
}
