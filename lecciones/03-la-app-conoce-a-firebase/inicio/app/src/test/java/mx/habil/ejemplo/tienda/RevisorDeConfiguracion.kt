package mx.habil.ejemplo.tienda

import java.io.File
import java.security.KeyStore
import java.security.MessageDigest
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

// Lee el texto de un google-services.json y dice qué le falta para esta app.
// Vive en src/test: lo usan solo las pruebas, no viaja en la app instalada.
// No abre la consola de Firebase ni la red: solo mira el texto que le das.
// No cambies este archivo: el verifica revisa que siga igual al de solucion/.
object RevisorDeConfiguracion {

    enum class Falta {
        NO_ES_JSON,            // el texto no se pudo leer como JSON
        OTRO_PAQUETE,          // ninguna app del archivo tiene el paquete pedido
        SIN_CLIENTE_ANDROID,   // la app no trae un cliente de OAuth tipo 1 (Android)
        HUELLA_DISTINTA,       // hay cliente de Android, pero ninguno con la huella pedida
        SIN_CLIENTE_WEB        // la app no trae un cliente de OAuth tipo 3 (web)
    }

    // huella: el SHA-1 con o sin «:», en mayúsculas o minúsculas.
    fun revisar(texto: String, paquete: String, huella: String): Set<Falta> {
        val app = try {
            appDelPaquete(JSONObject(texto), paquete)
        } catch (e: JSONException) {
            return setOf(Falta.NO_ES_JSON)
        } ?: return setOf(Falta.OTRO_PAQUETE)

        val faltas = mutableSetOf<Falta>()
        val clientes = app.optJSONArray("oauth_client") ?: JSONArray()
        val deAndroid = (0 until clientes.length())
            .map { clientes.getJSONObject(it) }
            .filter { it.optInt("client_type") == 1 }
        val huellas = deAndroid.mapNotNull { it.optJSONObject("android_info") }
            .filter { it.optString("package_name") == paquete }
            .map { limpia(it.optString("certificate_hash")) }
        if (deAndroid.isEmpty()) {
            faltas += Falta.SIN_CLIENTE_ANDROID
        } else if (limpia(huella) !in huellas) {
            faltas += Falta.HUELLA_DISTINTA
        }
        val hayWeb = (0 until clientes.length())
            .any { clientes.getJSONObject(it).optInt("client_type") == 3 }
        if (!hayWeb) faltas += Falta.SIN_CLIENTE_WEB
        return faltas
    }

    // El project_id y el identificador de la app que el archivo declara para ese paquete.
    fun proyecto(texto: String): String =
        JSONObject(texto).getJSONObject("project_info").getString("project_id")

    fun idDeApp(texto: String, paquete: String): String? =
        appDelPaquete(JSONObject(texto), paquete)
            ?.getJSONObject("client_info")?.optString("mobilesdk_app_id")

    private fun appDelPaquete(raiz: JSONObject, paquete: String): JSONObject? {
        val apps = raiz.optJSONArray("client") ?: return null
        return (0 until apps.length()).map { apps.getJSONObject(it) }.firstOrNull {
            it.optJSONObject("client_info")
                ?.optJSONObject("android_client_info")
                ?.optString("package_name") == paquete
        }
    }

    private fun limpia(huella: String) = huella.replace(":", "").trim().lowercase()

    // El SHA-1 del certificado de depuración de ESTA computadora, leído del
    // almacén que Android crea en tu carpeta personal (.android/debug.keystore,
    // o la carpeta que diga ANDROID_USER_HOME). Es el mismo dato que imprime
    // ./gradlew signingReport en la variante debug. Regresa null si no existe.
    fun huellaDeDepuracion(): String? {
        val carpeta = System.getenv("ANDROID_USER_HOME")
            ?: (System.getProperty("user.home") + "/.android")
        val almacen = File(carpeta, "debug.keystore")
        if (!almacen.isFile) return null
        val certificado = KeyStore.getInstance(almacen, "android".toCharArray())
            .getCertificate("androiddebugkey") ?: return null
        return MessageDigest.getInstance("SHA-1").digest(certificado.encoded)
            .joinToString("") { "%02x".format(it) }
    }
}
