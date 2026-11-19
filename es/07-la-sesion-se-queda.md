# Lección 07 — La sesión se queda

**Antes de leer, cinco palabras.** Una línea cada una.

- **Sesión:** lo que permite que la app sepa quién eres sin preguntártelo otra vez.
- **Usuario actual:** el usuario que Firebase recuerda en este teléfono. En el código se llama `currentUser`.
- **Cerrar sesión:** pedirle al teléfono que olvide a ese usuario, para que la próxima vez se pida entrar.
- **Token:** un comprobante digital de la sesión. No es una contraseña, y tampoco se pega ni se publica.
- **Credential Manager:** la pieza de Android que coordina las credenciales y puede recordar qué cuenta elegiste
  para esta app.

**Qué te llevas:** una tienda que, al abrirla, te saluda si ya habías entrado y te pide entrar si no; con un
botón «Cerrar sesión» que de verdad te saca. Y la diferencia entre cosas que suelen confundirse: el usuario
actual, los tokens que guarda Firebase, las identidades enlazadas a tu usuario y lo que un proveedor recuerda por
su lado.

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
escrita el AAAA-MM-DD, antes del paso 2 y sin IA
```

En la primera: entraste con Google, cerraste la app desde la lista de apps recientes y la vuelves a abrir. ¿Qué
pantalla ves, y se abre la ventana para elegir cuenta? En la segunda: tocaste «Cerrar sesión» y la vuelves a
abrir. ¿Qué pantalla ves, y qué pasa al tocar «Entrar»?

**Mide el punto de partida.** Desde la raíz del repo:

```bash
./lecciones/07-la-sesion-se-queda/verifica
```

El 19-nov-2026, con un archivo inventado, la última línea del conteo fue
`Resultado: 68 de 82 pruebas en verde (14 en rojo, 0 con error, 0 saltadas)`. Son 82: las 66 que heredas de la
06, ya en verde, y 16 de la sesión. De esas, dos ya pasan con el andamiaje y catorce esperan tu código.

**Lo demuestra:** tu predicción, contra lo que veas en el paso 3 (las pruebas) y en el paso 4 (tu teléfono). El
`verifica` solo confirma que dejaste las líneas: no ve cuándo las escribiste, ni si usaste IA, ni si acertaste.
Por eso conserva tu predicción con su fecha, antes del paso 2, y declara debajo, en una línea, que la hiciste sin
IA. El verde no califica el contenido.

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
| `PuertaDeLaSesion.kt` (nuevo) | La puerta `RecuerdoDeLaSesion` con tres funciones: `usuarioActual()`, `olvidarEnFirebase()` y `olvidarEnElTelefono()`; su implementación de verdad, `RecuerdoConFirebase`; y aparte, el `LimpiadorDeCredenciales` que habla con Credential Manager |
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
- `olvidarEnElTelefono()` le pide a su limpiador que llame `clearCredentialState` de Credential Manager. La guía
  de Firebase pide hacerlo al cerrar sesión, además de `signOut`. La referencia de Credential Manager explica
  para qué: un proveedor de credenciales puede guardar una sesión activa de tu app y darle prioridad la próxima
  vez. Este aviso le pide que limpie ese estado; así puede dejar de priorizar la credencial anterior. Las
  opciones que aparecen al final las decide el proveedor, y se comprueban en el teléfono.
- El limpiador va aparte, en su propia puerta, por una razón de las pruebas: así una prueba puede darle a la
  puerta de verdad un limpiador de mentira y ver que sí lo llama al cerrar sesión.

**Una advertencia de la propia documentación.** La página de Firebase para administrar usuarios avisa de dos
casos. `getCurrentUser` puede regresar `null` porque Firebase todavía no termina de prepararse. Y puede regresar
un usuario cuyo token ya no es válido, por ejemplo si lo borraron desde otro dispositivo y el token del teléfono
no se ha renovado. Para reaccionar a eso ofrece un `AuthStateListener`, que avisa cada vez que cambia el estado
del token.

Esta lección hace una sola lectura al abrir, como la guía de Google, para estudiar el ciclo básico. Esa lectura
no garantiza que Firebase ya haya recuperado la sesión, ni que el token del teléfono siga siendo válido. Una app
de producción escucha el `AuthStateListener` o algo equivalente. Comprueba el resultado real en tu teléfono: un
82 de 82 no demuestra que la sesión se guarde de verdad.

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

   Revisa cuatro cosas: que `cerrar()` haga **las dos** cosas y en ese orden; que el `catch` no sea un
   `Exception` a secas que se trague la cancelación; que tu clase solo conozca `RecuerdoDeLaSesion`, sin
   preguntar qué puerta le tocó; y que nadie haya tocado las pruebas, las puertas ni las pantallas.

4. **Corre el `verifica`.** Las 82 pruebas pueden quedar en verde. La entrega todavía no está completa: faltan
   tus líneas del 7.1 y del 7.3 en `respuestas/07.md`, y el `verifica` lo dice al final.
5. **Explica**, en tres líneas: por qué Firebase olvida primero y el aviso al teléfono va después, dentro del
   `try`.

**Lo demuestra, y hasta dónde.** Las pruebas usan el proyecto de mentira de la 06 como si fuera la memoria del
teléfono. Ana entra con Google; la prueba «cierra y vuelve a abrir» la app, y Ana sigue adentro sin que se abra
la ventana de cuentas. Toca «Cerrar sesión», la app vuelve a la bienvenida, se reabre y pide entrar; al entrar
otra vez llega al **mismo** usuario. Otras pruebas le dan a tu clase una puerta de mentira y miran qué le pidió:
que olvide una vez, que avise una vez, que un aviso que falla no truene la app y que la cancelación no se trague.
Y con `MainActivity` miden que la sesión que arma la app es la tuya; que con la puerta de verdad, en tu
computadora, nadie sigue adentro; y que, con esa misma puerta de verdad y un limpiador de mentira, cerrar sesión
le pide a Credential Manager que limpie. Además, el `verifica` revisa como texto que `SesionDeLaTienda` solo
programe contra `RecuerdoDeLaSesion`: si nombra una puerta concreta o un doble, el nivel simulado queda
incompleto.

Los sabotajes se midieron sobre la solución el 19-nov-2026:

| Sabotaje | Resultado |
|---|---|
| `quienSigueAdentro()` que siempre contesta `null` | 78 de 82 |
| `cerrar()` sin `olvidarEnFirebase()` | 78 de 82 |
| `cerrar()` sin el aviso al teléfono | 78 de 82 |
| El aviso sin `try` | 81 de 82 |
| `catch (e: Exception)` que se traga la cancelación | 81 de 82 |
| `quienSigueAdentro()` que siempre contesta que hay alguien | 71 de 82, y dos de las rojas son heredadas de lecciones anteriores |
| El aviso al teléfono solo cuando la puerta **no** es la de verdad | 81 de 82, y la marca de «solo programa contra `RecuerdoDeLaSesion`» en rojo |

Cada uno deja además su marca en rojo en la salida, con el paso que lo arregla.

El último es el que justifica el limpiador aparte: sin una prueba que cierre sesión con la puerta de verdad, una
clase que se porta bien solo con los dobles saldría en verde.

Tres límites. «Reabrir» en las pruebas es dibujar la app desde cero con la misma memoria de mentira: ninguna
prueba mata el proceso ni lee el disco del teléfono. Ninguna ve un usuario real de Firebase: en tu computadora
nunca lo hay. Y el Credential Manager de verdad no lo llama ninguna prueba: en tu computadora no contesta
(medido el 19-nov-2026: diez segundos sin respuesta). Las pruebas ven que se le pide limpiar, no qué ofrece
después la ventana de cuentas: eso solo se ve en el teléfono.

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

Con Microsoft conviene saber algo. La guía de Firebase para Microsoft documenta el cierre en la app, con
`signOut`, y no dice nada del navegador. En el ensayo, al volver a entrar con Microsoft, su página ya mostraba
una sesión activa del navegador, con la cuenta lista para elegirla sin contraseña. Eso lo vimos en ese
navegador, ese día; no es una garantía para cualquier navegador. En un equipo compartido, revisa esa sesión y
ciérrala por separado.

**Y la deuda de la 06.** La prueba manual de su paso 5 pedía salir para volver a entrar con Microsoft, y
proponía borrar los datos de la app. Ahora tienes «Cerrar sesión»: es la forma de salir que la propia app
ofrece.

**Lo demuestra:** lo que tú observes. No hay marca en el `verifica` para este paso.

---

## Paso 5 · Cuatro cosas que no son lo mismo

Este es el ejercicio 7.3. Al reabrir con sesión y al cerrarla, intervienen cuatro cosas distintas, y cada una
tiene su fuente:

| | Qué es | Qué le hace «Cerrar sesión» en esta app | Fuente |
|---|---|---|---|
| **El usuario actual** | El usuario de tu proyecto que Firebase recuerda en este teléfono (`currentUser`) | `signOut()` cierra su sesión y lo borra del caché en disco: `currentUser` queda en `null` | Referencia de `FirebaseAuth` (`getCurrentUser`, `signOut`) |
| **Los tokens de Firebase** | El de identidad dura una hora. El de actualización sirve para pedir otro, y caduca si el usuario se borra o se desactiva, o ante un cambio mayor en su cuenta, como la contraseña o el correo. El servidor también puede revocarlo, y restablecer la contraseña lo revoca | `signOut()` limpia el estado en el teléfono. No es una revocación en el servidor | *Manage User Sessions* de Firebase |
| **Las identidades enlazadas** (`providerData`) | Las identidades de cada proveedor enlazadas a tu usuario de Firebase: con Google y con Microsoft, si las enlazaste en la 06 | Nada: ni `signOut` ni `clearCredentialState` las desvinculan | Referencia de `FirebaseUser` (`getProviderData`) |
| **Lo que un proveedor recuerda por su lado** | El estado de credencial que un proveedor del teléfono puede guardar para esta app. Aparte están tu cuenta de Google en el teléfono y tu sesión de Microsoft en el navegador | `clearCredentialState` solo pide limpiar ese estado local para esta app. No borra la cuenta de Google del teléfono ni la sesión del navegador de Microsoft | Referencia de `CredentialManager` (`clearCredentialState`) |

La página de sesiones es de la parte de servidor de Firebase, pero describe la sesión de cualquier usuario:
«las sesiones de Firebase Authentication son de larga duración». Por eso la app no te pide entrar cada hora: el
token de identidad caduca, y Firebase pide otro con el de actualización.

**Ahora tú.** Para cada afirmación, escribe en `respuestas/07.md` `verdadera` o `falsa`, así: `7.3-1: falsa`.

1. Cerrar sesión en la app borra tu cuenta de Google del teléfono.
2. El token de identidad de Firebase dura una hora; por eso, al día siguiente, la app te pide entrar otra vez.
3. En el código de esta lección, si al abrir Firebase ya tiene un usuario actual, la app saluda sin abrir la
   ventana de cuentas de Google.
4. Después de `signOut()`, `currentUser` es `null`.
5. Cerrar sesión en la app desvincula Microsoft de tu usuario de Firebase: desaparece de `providerData`.

Elige **una** y justifícala con la documentación: copia la dirección de la página oficial en una línea que
empiece `fuente 7.3: https://`, y debajo, con tus palabras, qué dice.

**Lo demuestra:** cinco de cinco contra «Compara tus respuestas», y tu justificación. El `verifica` solo
confirma que dejaste las cinco líneas y la fuente: compáralas tú con esa sección. El verde no califica el
contenido.

---

## Tus ejercicios

El paso 1 se hace **sin IA**.

| # | Haces | Está hecho cuando |
|---|---|---|
| 7.1 | Predices a qué pantalla llega la app al reabrirla con y sin sesión, y lo compruebas | Tus dos líneas `predicción 7.1` están en `respuestas/07.md`, con su fecha, escritas antes del paso 2 y declaradas sin IA, y las comparaste con las pruebas del paso 3 y con tu teléfono en el paso 4. El `verifica` solo ve que las líneas existen |
| 7.2 | Implementas cerrar sesión de forma que al volver se pida entrar otra vez | El `verifica` dice «Nivel simulado: completo», con las 82 pruebas en verde. Es el nivel simulado; el paso 4 es tu prueba en el teléfono |
| 7.3 | Distingues el usuario actual, los tokens de Firebase, las identidades enlazadas y lo que recuerda un proveedor | Cinco de cinco contra «Compara tus respuestas», y una justificada con su fuente oficial. Eso lo comparas tú: el `verifica` no lo califica |

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
| Al volver a entrar con Microsoft no te pide contraseña | Puede ser la sesión de Microsoft en el navegador, que no es la de tu app | Paso 4. En un equipo compartido, revísala y ciérrala en el navegador |
| La app abre en la bienvenida, y la sesión aparece después | Firebase pudo no terminar de prepararse cuando la app hizo su única lectura | No borres datos ni des la prueba por buena: anota el caso. Esta lección hace una lectura única; una versión robusta escucha el `AuthStateListener` (paso 2) |
| ❌ «SesionDeLaTienda solo programa contra RecuerdoDeLaSesion…» | Tu clase nombra una puerta concreta o un doble | Paso 3: tu clase solo conoce la interfaz |

El `verifica` de esta lección se midió en una Mac con Apple silicon. No se ha medido en Linux.

---

## Para cerrar

**Contesta sin volver a leer:**

1. ¿Por qué la app de la 06 abría en la bienvenida aunque Firebase te recordaba?
2. ¿Qué dos cosas hace «Cerrar sesión», y por qué en ese orden?
3. Tu `verifica` da 82 de 82. Di una cosa que eso demuestra y una que no.
4. Si el token de identidad dura una hora, ¿por qué la app no te pide entrar cada hora?

**Lo que sigue:** en la L08, la primera vez: la app distingue al usuario nuevo del que vuelve.

---

## Compara tus respuestas

**Paso 1.** Con sesión: si Firebase ya tiene al usuario actual cuando la app hace su lectura, la app abre
directo en el inicio, con tu nombre, y no se abre la ventana de cuentas. Es lo que miden las pruebas, y lo que
vio el ensayo con una cuenta real; con una sola lectura, no es una garantía (paso 2). Sin sesión: abre en la
bienvenida; al tocar «Entrar» llegas a la pantalla con los botones, y al entrar con Google, sí se abre la
ventana.

**Paso 3.** Afuera. Firebase olvidó primero, y eso es lo que decide si alguien sigue adentro. El aviso al
teléfono solo cambia qué opciones se ofrecen la próxima vez; si falla, no deja a nadie adentro.

**Paso 5.**

1. Falsa. `clearCredentialState` solo pide limpiar el estado que un proveedor guardaba para tu app; la cuenta
   de Google sigue en el teléfono.
2. Falsa. El token de identidad dura una hora, pero Firebase pide otro con el de actualización: las sesiones son
   de larga duración. El de actualización caduca o se revoca por otras razones (el usuario se borra o se
   desactiva, cambia la contraseña o el correo, o el servidor lo revoca), no por el paso del tiempo.
3. Verdadera. La app lee `currentUser` una vez al abrir, y con un usuario actual va directo al inicio; las
   pruebas miden que no se abre la ventana. Que Firebase ya lo tenga listo al abrir es otra pregunta (paso 2).
4. Verdadera. La referencia de `signOut()` dice que cierra la sesión del usuario actual y lo borra del caché en
   disco; `getCurrentUser()` regresa `null` si no hay ninguno.
5. Falsa. `providerData` son las identidades enlazadas a tu usuario de Firebase, y cerrar sesión no las
   desvincula: ni `signOut` ni `clearCredentialState` tocan ese enlace.

**Para cerrar:**

1. Porque la app nunca le preguntaba: abría siempre en la bienvenida. Firebase sí guardaba al usuario.
2. Le pide a Firebase que olvide al usuario y les avisa a los proveedores del teléfono. Primero Firebase, porque
   eso es lo que cierra la sesión; el aviso puede fallar sin dejar a nadie adentro.
3. Demuestra qué decide tu sesión al abrir, qué pide al cerrar (también con la puerta de verdad) y a qué
   pantalla llega la app, con una memoria de mentira. No demuestra que Firebase recuerde a tu usuario en tu
   teléfono, ni qué ofrece la ventana de cuentas después de cerrar.
4. Porque Firebase usa el token de actualización para conseguir un token de identidad nuevo. Cerrar sesión en
   la app no revoca ese token en el servidor: limpia el estado del teléfono.

---

## Fuentes de la lección

Abiertas el 19-nov-2026.

- Firebase. *Authenticate with Google on Android* (revisar `currentUser` al iniciar la Activity; para salir, `signOut` y `clearCredentialState`, con su `ClearCredentialException`). https://firebase.google.com/docs/auth/android/google-signin
- Firebase. *Manage Users in Firebase* (Android) (`getCurrentUser` puede regresar `null` si Firebase no termina de prepararse, o un usuario cuyo token ya no es válido; `AuthStateListener`; `getProviderData`). https://firebase.google.com/docs/auth/android/manage-users
- Firebase. *Manage User Sessions* (sesiones de larga duración; token de identidad de una hora; cuándo caduca el de actualización; restablecer la contraseña lo revoca; `revokeRefreshTokens` desde el servidor). https://firebase.google.com/docs/auth/admin/manage-sessions
- Firebase. Referencia de `FirebaseAuth` (`getCurrentUser`; `signOut` cierra la sesión y borra al usuario del caché en disco; `addAuthStateListener`). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth
- Firebase. Referencia de `FirebaseUser` (`getProviderData`: las identidades enlazadas al usuario; `getDisplayName`). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseUser
- Firebase. *Authenticate Using Microsoft on Android* (para salir, `signOut`; no menciona la sesión del navegador). https://firebase.google.com/docs/auth/android/microsoft-oauth
- Android Developers. Referencia de `CredentialManager` (`clearCredentialState`: cuándo llamarlo y para qué). https://developer.android.com/reference/kotlin/androidx/credentials/CredentialManager
- Ensayo de la serie, 13-nov-2026, Mac con Apple silicon, con un código de prueba y una cuenta real: al reabrir, la app saludó antes de tocar el botón; al volver a entrar con Microsoft, su página ya mostraba la cuenta iniciada.
- Medición propia: Mac con Apple silicon, 19-nov-2026. El `verifica` sobre `inicio/` y `solucion/`, dos veces seguidas cada uno, cada paso y cada sabotaje, con archivos inventados y sin tocar la consola de Firebase ni ninguna cuenta.
