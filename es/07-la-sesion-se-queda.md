# Lección 07 — La sesión se queda

**Antes de leer, tres palabras.** Una línea cada una.

- **Sesión:** lo que permite que la app sepa quién eres sin preguntártelo otra vez.
- **Usuario actual:** el usuario que Firebase recuerda en este teléfono. En el código se llama `currentUser`.
- **Cerrar sesión:** pedirle al teléfono que olvide a ese usuario, para que la próxima vez se pida entrar.

**Qué te llevas:** una tienda que, al abrirla, te saluda si ya habías entrado y te pide entrar si no; con un
botón «Cerrar sesión» que de verdad te saca. Y la diferencia entre tres cosas que suelen confundirse: el usuario
actual, los tokens que Firebase guarda en el teléfono y lo que sabe de ti cada proveedor.

**Idea nueva de esta lección:** el ciclo de la sesión: recordar, cerrar y caducar. Lo demás —la pregunta que la
app hace al abrirse, el botón en el inicio y la puerta que habla con Firebase y con Credential Manager— viene
dado y explicado, y no se evalúa aquí.

---

## La escena

Es el 13 de noviembre de 2026, en el ensayo de esta serie. La app de prueba acaba de saludar a una cuenta real
de Google: «Hola, … (primera vez)». Alguien la cierra y la vuelve a abrir.

La app saluda otra vez. Antes de que nadie toque el botón.

En las lecciones 04, 05 y 06 tu app no hace eso: abre siempre en la bienvenida y te pide entrar, aunque hayas
entrado hace un minuto. La lección 06 lo dejó anotado: «esta app todavía no tiene cómo cerrar sesión».

Las dos cosas son ciertas a la vez, y el misterio tiene una sola respuesta. Firebase **sí** te recordaba desde la
lección 04: lo guarda en el teléfono por su cuenta. La app del ensayo le preguntaba al abrirse; la tuya nunca le
ha preguntado. Esta lección le enseña a preguntar, y a pedir que olvide.

---

## Antes de empezar

**Qué necesitas:** la [06](06-dos-botones-una-cuenta.md) terminada (su `verifica` en 66 de 66). Los pasos 1 a 3
y el 5 no piden ninguna cuenta. El paso 4 es en tu teléfono o emulador, con tu cuenta de Google de la 04.

**El proyecto de inicio.** Desde la raíz del repo de la serie:

```bash
git pull
cp lecciones/03-la-app-conoce-a-firebase/inicio/app/google-services.json lecciones/07-la-sesion-se-queda/inicio/app/
cd lecciones/07-la-sesion-se-queda/inicio
```

Parte de la solución de la 06, con el andamiaje de esta lección encima. `./verifica solucion` sale en rojo en un
clon limpio, porque sin tu archivo el proyecto no compila. Tus respuestas van en `respuestas/07.md`.

**Regla que sigue vigente.** Nunca pegues ni subas un token, ni capturas o registros que lo traigan. Esta
lección habla mucho de tokens, y por eso mismo no imprime ninguno: la app solo lee el nombre del usuario.

**Lo que no hace esta lección.** No distingue la primera vez (lección 08). No borra tu cuenta de Google del
teléfono ni tu usuario del proyecto de Firebase: cerrar sesión no es nada de eso, y el paso 5 explica por qué.

---

## Paso 1 · Sin IA: ¿a dónde llega la app al reabrirla?

Este es el ejercicio 7.1 y se hace **sin Claude Code**. Todavía no leas el paso 2.

Piensa en la app **terminada** de esta lección: la que ya le pregunta a Firebase al abrirse. Escribe en
`respuestas/07.md` dos líneas que empiecen así:

```text
predicción 7.1 con sesión: …
predicción 7.1 sin sesión: …
```

En la primera: entraste con Google, cerraste la app desde la lista de apps recientes y la vuelves a abrir. ¿Qué
pantalla ves, y se abre la ventana para elegir cuenta? En la segunda: tocaste «Cerrar sesión» y la vuelves a
abrir. ¿Qué pantalla ves, y qué pasa al tocar «Entrar»?

**Mide el punto de partida.** Desde la raíz del repo:

```bash
./lecciones/07-la-sesion-se-queda/verifica
```

El 19-nov-2026, con un archivo inventado, la última línea del conteo fue
`Resultado: 68 de 81 pruebas en verde (13 en rojo, 0 con error, 0 saltadas)`. Son 81: las 66 que heredas de la
06, ya en verde, y 15 de la sesión. De esas, dos ya pasan con el andamiaje y trece esperan tu código.

**Lo demuestra:** tu predicción, contra lo que veas en el paso 3 (las pruebas) y en el paso 4 (tu teléfono). El
`verifica` solo busca las dos líneas: no sabe si acertaste.

---

## Paso 2 · Lo que el proyecto ya trae

**Firebase ya decía cómo preguntar.** Su guía para entrar con Google en Android dice: «al iniciar tu Activity,
revisa si el usuario ya inició sesión», y lo hace leyendo `currentUser`. La referencia de `FirebaseAuth` lo
resume: `getCurrentUser()` regresa el usuario que inició sesión, o `null` si no hay ninguno.

En la app, tres archivos son nuevos y cuatro cambiaron respecto a la solución de la 06; en las pruebas llegan tres
clases nuevas y su doble, `DoblesDeSesion.kt`. Ábrelos; edita solo los dos que dice el paso 3:

| Archivo | Qué trae |
|---|---|
| `Sesion.kt` (nuevo) | La interfaz `Sesion`: `quienSigueAdentro()` y `cerrar()` |
| `PuertaDeLaSesion.kt` (nuevo) | La puerta `RecuerdoDeLaSesion` con tres funciones: `usuarioActual()`, `olvidarEnFirebase()` y `olvidarEnElTelefono()`, y su implementación de verdad |
| `SesionDeLaTienda.kt` (nuevo) | La forma de tu clase, vacía. La escribes en el paso 3 |
| `Tienda.kt` | Al abrirse le pregunta **una vez** a la sesión quién sigue adentro. Si alguien, abre en el inicio con su nombre; si nadie, en la bienvenida |
| `Inicio.kt` | El botón «Cerrar sesión». Se dibuja solo si hay sesión: sin ella, el inicio es el de la 06 |
| `MainActivity.kt`, `Armado.kt` | Arman también la sesión. Por ahora `Armado.kt` regresa `SesionSinConectar()`, que no recuerda a nadie: así se porta tu app de la 06 |

Lo demás es idéntico a la solución de la 06, y el `verifica` lo compara archivo por archivo. Es una restricción
de esta actividad, no del objetivo.

**Las tres funciones de la puerta, cada una con su fuente:**

- `usuarioActual()` lee `FirebaseAuth.getInstance().currentUser` y regresa solo su nombre.
- `olvidarEnFirebase()` llama `signOut()`. La referencia dice que cierra la sesión del usuario actual **y lo
  borra del caché en disco**. Esa frase resuelve el misterio de la escena: Firebase guarda al usuario en el
  disco del teléfono, y por eso sobrevive a cerrar la app.
- `olvidarEnElTelefono()` llama `clearCredentialState` de Credential Manager. La guía de Firebase pide hacerlo al
  cerrar sesión, además de `signOut`. La referencia de Credential Manager explica para qué: un proveedor de
  credenciales puede guardar una sesión activa de tu app y usarla para limitar las opciones la próxima vez;
  este aviso le pide que la borre, para que se ofrezcan todas.

**Una advertencia de la propia documentación.** La página de Firebase para administrar usuarios dice que
`getCurrentUser` también puede regresar `null` porque Firebase todavía no termina de prepararse, y ofrece otra
forma de enterarse: un `AuthStateListener`, que avisa cada vez que cambia el estado. Esta lección pregunta una
sola vez al abrir, como la guía de Google. En el ensayo del 13-nov ese «una vez» alcanzó para saludar; si a ti
la app te pide entrar y luego descubres que sí había sesión, cuéntanos.

---

## Paso 3 · Con Claude Code: recordar y cerrar

Entra igual que siempre: **predices → le pides → lees lo que generó → corres el `verifica` → lo explicas.**

1. **Predice**, en `respuestas/07.md`: si `cerrar()` le pidiera a Firebase que olvide y el aviso al teléfono
   fallara, ¿la persona queda adentro o afuera?
2. **Pídele** a Claude Code, parado en `inicio/`: «En `SesionDeLaTienda.kt`, `quienSigueAdentro()` regresa lo
   que diga `recuerdo.usuarioActual()`. `cerrar()` primero llama `recuerdo.olvidarEnFirebase()` y después
   `recuerdo.olvidarEnElTelefono()` dentro de un `try`: `CancellationException` se deja pasar y
   `ClearCredentialException` se atrapa sin hacer nada más, porque la sesión ya quedó cerrada. En `Armado.kt`
   cambia solo el cuerpo de `armarSesion` para que regrese `SesionDeLaTienda`, y borra ahí `SesionSinConectar`.
   No cambies ningún otro archivo.»
3. **Lee.** El centro debe parecerse a esto:

```kotlin
override fun quienSigueAdentro(): String? = recuerdo.usuarioActual()

override suspend fun cerrar() {
    recuerdo.olvidarEnFirebase()
    try {
        recuerdo.olvidarEnElTelefono()
    } catch (e: CancellationException) {
        throw e
    } catch (e: ClearCredentialException) {
        // El aviso no se pudo dar. La sesión de Firebase ya está cerrada.
    }
}
```

   Revisa tres cosas: que `cerrar()` haga **las dos** cosas y en ese orden; que el `catch` no sea un
   `Exception` a secas que se trague la cancelación; y que nadie haya tocado las pruebas, las puertas ni las
   pantallas.

4. **Corre el `verifica`.** Las 81 pruebas pueden quedar en verde. La entrega todavía no está completa: faltan
   tus líneas del 7.1 y del 7.3 en `respuestas/07.md`, y el `verifica` lo dice al final.
5. **Explica**, en tres líneas: por qué Firebase olvida primero y el aviso al teléfono va después, dentro del
   `try`.

**Lo demuestra, y hasta dónde.** Las pruebas usan el proyecto de mentira de la 06 como si fuera la memoria del
teléfono. Ana entra con Google; la prueba «cierra y vuelve a abrir» la app, y Ana sigue adentro sin que se abra
la ventana de cuentas. Toca «Cerrar sesión», la app vuelve a la bienvenida, se reabre y pide entrar; al entrar
otra vez llega al **mismo** usuario. Otras pruebas le dan a tu clase una puerta de mentira y miran qué le pidió:
que olvide una vez, que avise una vez, que un aviso que falla no truene la app y que la cancelación no se trague.
Y con `MainActivity` miden que la sesión que arma la app es la tuya, y que con la puerta de verdad, en tu
computadora, nadie sigue adentro.

Los sabotajes se midieron sobre la solución el 19-nov-2026:

| Sabotaje | Resultado |
|---|---|
| `quienSigueAdentro()` que siempre contesta `null` | 77 de 81 |
| `cerrar()` sin `olvidarEnFirebase()` | 77 de 81 |
| `cerrar()` sin el aviso al teléfono | 78 de 81 |
| El aviso sin `try` | 80 de 81 |
| `catch (e: Exception)` que se traga la cancelación | 80 de 81 |
| `quienSigueAdentro()` que siempre contesta que hay alguien | 71 de 81, y dos de las rojas son heredadas de lecciones anteriores |

Cada uno deja además su marca en rojo en la salida, con el paso que lo arregla.

Tres límites. «Reabrir» en las pruebas es dibujar la app desde cero con la misma memoria de mentira: ninguna
prueba mata el proceso ni lee el disco del teléfono. Ninguna ve un usuario real de Firebase: en tu computadora
nunca lo hay. Y el aviso de verdad a Credential Manager no lo llama ninguna prueba: en tu computadora no
contesta (medido el 19-nov-2026: diez segundos sin respuesta), así que su efecto solo se ve en el teléfono.

---

## Paso 4 · En el teléfono: reabrir y cerrar

Instala y abre la app con tu `google-services.json`, como en la 04:

```bash
./gradlew installDebug
```

**Con sesión.** Entra con Google. Cierra la app desde la lista de apps recientes (deslízala fuera) y ábrela otra
vez desde el cajón. Compara con tu primera predicción.

**Sin sesión.** Toca «Cerrar sesión». Cierra la app de la misma forma y ábrela. Toca «Entrar» y «Entrar con
Google». Compara con tu segunda predicción.

Lo que ya se vio en el ensayo del 13-nov-2026, con un código de prueba: al reabrir, la app saludó sin tocar el
botón. Lo que **todavía no capturamos con una cuenta real** es este recorrido con el código de esta lección: el
botón «Cerrar sesión», la app que al reabrir pide entrar y lo que muestra después la ventana de cuentas. Si te
aparece distinto, cuéntanos.

Con Microsoft pasa algo que conviene saber. La guía de Firebase para Microsoft solo dice que para salir se llama
`signOut`; no dice nada del navegador. En el ensayo, al volver a entrar con Microsoft, su página ya mostraba la
cuenta como iniciada para elegirla sin contraseña: esa sesión es de Microsoft, vive en el navegador, y cerrar
sesión en tu app no la toca. En un equipo que no es tuyo, cierra también esa sesión en el navegador.

**Y la deuda de la 06.** La prueba manual de su paso 5 pedía salir para volver a entrar con Microsoft, y
proponía borrar los datos de la app. Ahora tienes «Cerrar sesión»: es la forma de salir que la propia app
ofrece.

**Lo demuestra:** lo que tú observes. No hay marca en el `verifica` para este paso.

---

## Paso 5 · Tres cosas que no son lo mismo

Este es el ejercicio 7.3. Al reabrir con sesión y al cerrarla, intervienen tres cosas distintas:

| | El usuario actual | Los tokens que guarda Firebase | Los datos del proveedor |
|---|---|---|---|
| **Qué es** | El usuario de tu proyecto que Firebase recuerda en este teléfono (`currentUser`) | Los que forman la sesión: uno de identidad de Firebase y uno de actualización | Lo que cada proveedor enlazado le dio a Firebase (`providerData`), y la sesión que el proveedor tiene por su lado: tu cuenta de Google en el teléfono o tu sesión de Microsoft en el navegador |
| **Cuánto dura** | Hasta que se cierre la sesión | El de identidad, una hora; el de actualización sirve para pedir otro, y solo caduca si el usuario se borra, se desactiva o tiene un cambio mayor en su cuenta | Lo decide cada proveedor |
| **Qué le hace «Cerrar sesión»** | `signOut()` lo cierra y lo borra del caché en disco: `currentUser` queda en `null` | Ya no hay usuario actual para el cual pedirlos | `clearCredentialState` le pide a los proveedores del teléfono que borren la sesión que guardaban **para tu app**. La cuenta de Google sigue en Ajustes, y el usuario sigue en tu proyecto |
| **Fuente** | Referencias de `FirebaseAuth` (`getCurrentUser`, `signOut`) | *Manage User Sessions* de Firebase | Referencia de `FirebaseUser` (`getProviderData`) y de `CredentialManager` (`clearCredentialState`) |

La página de sesiones es de la parte de servidor de Firebase, pero describe la sesión de cualquier usuario:
«las sesiones de Firebase Authentication son de larga duración». Por eso la app no te pide entrar cada hora: el
token de identidad caduca, y Firebase pide otro con el de actualización.

**Ahora tú.** Para cada afirmación, escribe en `respuestas/07.md` `verdadera` o `falsa`, así: `7.3-1: falsa`.

1. Cerrar sesión en la app borra tu cuenta de Google del teléfono.
2. El token de identidad de Firebase dura una hora; por eso, al día siguiente, la app te pide entrar otra vez.
3. Al reabrir con sesión, la app sabe quién eres sin abrir la ventana de cuentas de Google.
4. Después de `signOut()`, `currentUser` es `null`.
5. El token de actualización solo caduca si el usuario se borra, se desactiva o tiene un cambio mayor en su
   cuenta.

Elige **una** y justifícala con la documentación: copia la dirección de la página oficial en una línea que
empiece `fuente 7.3: https://`, y debajo, con tus palabras, qué dice.

**Lo demuestra:** cinco de cinco contra «Compara tus respuestas», y tu justificación. El `verifica` solo busca
las cinco líneas y la fuente: no las califica.

---

## Tus ejercicios

El paso 1 se hace **sin IA**.

| # | Haces | Está hecho cuando |
|---|---|---|
| 7.1 | Predices a qué pantalla llega la app al reabrirla con y sin sesión, y lo compruebas | Tus dos líneas `predicción 7.1` están en `respuestas/07.md`, escritas antes del paso 2, y las comparaste con las pruebas del paso 3 y con tu teléfono en el paso 4 |
| 7.2 | Implementas cerrar sesión de forma que al volver se pida entrar otra vez | El `verifica` dice «Nivel simulado: completo», con las 81 pruebas en verde. Es el nivel simulado; el paso 4 es tu prueba en el teléfono |
| 7.3 | Distingues el usuario actual, los tokens que guarda Firebase y los datos del proveedor | Cinco de cinco contra «Compara tus respuestas», y una justificada con su fuente oficial |

**Tiempo:** calcula una hora y cuarto sin el paso 4, y veinte minutos más con él. Es un estimado: lo ajustamos
con lo que tarde la primera cohorte.

---

## ¿No te corre?

| Ves | Causa probable | Qué haces |
|---|---|---|
| `File google-services.json is missing.` | No copiaste tu archivo a `inicio/app/` | El `cp` de «Antes de empezar» |
| `NotImplementedError` en las pruebas de la sesión | `SesionDeLaTienda.kt` sigue con sus `TODO` | Paso 3 |
| ❌ «Restricción de esta actividad… ya no son iguales en inicio/ y en solucion/» | Cambió un archivo dado | `git diff -- <archivo>` y deshazlo a mano |
| ❌ «Cuando el aviso falla» | Tu `catch` atrapa `Exception` y se traga la cancelación, o no hay `try` | Paso 3: `CancellationException` primero, y se vuelve a lanzar |
| ❌ «De las 66 pruebas heredadas…» y varias de la sesión en rojo | Tu sesión dice que siempre hay alguien adentro, y la app ya no abre en la bienvenida | `quienSigueAdentro()` contesta lo que diga la puerta, nada más |
| En el teléfono, la app abre en la bienvenida aunque entraste | Instalaste la de `inicio/` antes del paso 3, o borraste los datos de la app | Termina el paso 3 y vuelve a instalar |
| Al volver a entrar con Microsoft no te pide contraseña | Es la sesión de Microsoft en el navegador, no la de tu app | Paso 4. Ciérrala en el navegador si el equipo no es tuyo |

El `verifica` de esta lección se midió en una Mac con Apple silicon. No se ha medido en Linux.

---

## Para cerrar

**Contesta sin volver a leer:**

1. ¿Por qué la app de la 06 abría en la bienvenida aunque Firebase te recordaba?
2. ¿Qué dos cosas hace «Cerrar sesión», y por qué en ese orden?
3. Tu `verifica` da 81 de 81. Di una cosa que eso demuestra y una que no.
4. Si el token de identidad dura una hora, ¿por qué la app no te pide entrar cada hora?

**Lo que sigue:** en la L08, la primera vez: la app distingue al usuario nuevo del que vuelve.

---

## Compara tus respuestas

**Paso 1.** Con sesión: la app abre directo en el inicio, con tu nombre, y no se abre la ventana de cuentas.
Sin sesión: abre en la bienvenida; al tocar «Entrar» llegas a la pantalla con los botones, y al entrar con
Google, sí se abre la ventana.

**Paso 3.** Afuera. Firebase olvidó primero, y eso es lo que decide si alguien sigue adentro. El aviso al
teléfono solo cambia qué opciones se ofrecen la próxima vez; si falla, no deja a nadie adentro.

**Paso 5.**

1. Falsa. `clearCredentialState` borra la sesión que los proveedores guardaban para tu app; la cuenta de Google
   sigue en el teléfono.
2. Falsa. El token de identidad dura una hora, pero Firebase pide otro con el de actualización: las sesiones son
   de larga duración.
3. Verdadera. La app lee `currentUser`, que Firebase guarda en el teléfono; las pruebas miden que no se abre la
   ventana, y el ensayo lo vio con una cuenta real.
4. Verdadera. La referencia de `signOut()` dice que cierra la sesión del usuario actual y lo borra del caché en
   disco; `getCurrentUser()` regresa `null` si no hay ninguno.
5. Verdadera, según *Manage User Sessions*: se borra, se desactiva o hay un cambio mayor en la cuenta.

**Para cerrar:**

1. Porque la app nunca le preguntaba: abría siempre en la bienvenida. Firebase sí guardaba al usuario.
2. Le pide a Firebase que olvide al usuario y les avisa a los proveedores del teléfono. Primero Firebase, porque
   eso es lo que cierra la sesión; el aviso puede fallar sin dejar a nadie adentro.
3. Demuestra qué decide tu sesión al abrir, qué pide al cerrar y a qué pantalla llega la app, con una memoria de
   mentira. No demuestra que Firebase recuerde a tu usuario en tu teléfono, ni qué ofrece la ventana de cuentas
   después de cerrar.
4. Porque Firebase usa el token de actualización para conseguir un token de identidad nuevo.

---

## Fuentes de la lección

Abiertas el 19-nov-2026.

- Firebase. *Authenticate with Google on Android* (revisar `currentUser` al iniciar la Activity; para salir, `signOut` y `clearCredentialState`, con su `ClearCredentialException`). https://firebase.google.com/docs/auth/android/google-signin
- Firebase. *Manage Users in Firebase* (Android) (`getCurrentUser` puede regresar `null` si Firebase no termina de prepararse; `AuthStateListener`; `getProviderData`). https://firebase.google.com/docs/auth/android/manage-users
- Firebase. *Manage User Sessions* (sesiones de larga duración; token de identidad de una hora; cuándo caduca el de actualización). https://firebase.google.com/docs/auth/admin/manage-sessions
- Firebase. Referencia de `FirebaseAuth` (`getCurrentUser`; `signOut` cierra la sesión y borra al usuario del caché en disco; `addAuthStateListener`). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth
- Firebase. Referencia de `FirebaseUser` (`getProviderData`, `getDisplayName`). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseUser
- Firebase. *Authenticate Using Microsoft on Android* (para salir, `signOut`; no menciona la sesión del navegador). https://firebase.google.com/docs/auth/android/microsoft-oauth
- Android Developers. Referencia de `CredentialManager` (`clearCredentialState`: cuándo llamarlo y para qué). https://developer.android.com/reference/kotlin/androidx/credentials/CredentialManager
- Ensayo de la serie, 13-nov-2026, Mac con Apple silicon, con un código de prueba y una cuenta real: al reabrir, la app saludó antes de tocar el botón; al volver a entrar con Microsoft, su página ya mostraba la cuenta iniciada.
- Medición propia: Mac con Apple silicon, 19-nov-2026. El `verifica` sobre `inicio/` y `solucion/`, cada paso y cada sabotaje, con archivos inventados y sin tocar la consola de Firebase ni ninguna cuenta.
