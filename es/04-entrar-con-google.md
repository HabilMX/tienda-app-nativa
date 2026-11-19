# Lección 04 — Entrar con Google

**Antes de leer, cuatro palabras.** Una línea cada una; las demás llegan en el paso donde se usan.

- **Token de identidad:** un texto firmado por Google que dice quién eres y para quién se emitió. No es tu
  contraseña y caduca.
- **Audiencia:** el destinatario escrito dentro del token. Quien lo recibe revisa que diga su nombre.
- **Credential Manager:** la pieza de Android que muestra la ventana para elegir cuenta y le entrega a tu app
  lo que la persona eligió.
- **ID de cliente web:** el identificador que tu `google-services.json` trae en el bloque `"client_type": 3`. Lo
  revisaste en la lección 03.

**Qué te llevas:** el botón «Entrar con Google» de tu tienda, escrito por ti, con un mensaje distinto para cada
forma de no entrar, y saber decir qué viaja en cada paso entre tu app, Google y Firebase.

**Idea nueva de esta lección:** el token de identidad. Google entrega una prueba firmada de quién eres, hecha
para una audiencia, y Firebase la verifica. Tu app no comprueba nada: solo lleva el token de un lado al otro.
Lo demás —las librerías, las funciones que se pausan y las dos «puertas»— viene dado y explicado, y no se evalúa
aquí.

---

## La escena

El emulador tiene tu cuenta de Google agregada. Pones el botón, lo tocas y la app contesta que no hay ninguna
cuenta con la cual entrar.

La cuenta está ahí. La ves en Ajustes.

Eso pasó en el ensayo de esta serie, el 13 de noviembre de 2026, en una Mac. El código pedía la credencial con
`GetGoogleIdOption` y `setFilterByAuthorizedAccounts(false)`, y Credential Manager contestó
`NoCredentialException`; en el registro del sistema quedó el número `[28433]`. Se cambió una sola cosa, la opción
de la solicitud, por `GetSignInWithGoogleOption`: se abrió la ventana de cuentas y se entró.

Las dos opciones existen y las dos piden lo mismo, un token de identidad de Google. La documentación de Android
las separa por dónde se usan: `GetGoogleIdOption` es para la hoja inferior de Credential Manager, y
`GetSignInWithGoogleOption` es para un botón. Esta lección hace el botón. Primero ves qué viaja, luego lo
escribes, y al final lo rompes tú para reconocer ese error cuando te lo encuentres.

---

## Antes de empezar

**Qué necesitas:** la [03](03-la-app-conoce-a-firebase.md) terminada, con tu `google-services.json` completo (su
`verifica` en 18 de 18), y el ambiente de la [00](00-ambiente-listo.md). Para el paso 6 basta un emulador
abierto o tu teléfono conectado; para el paso 7, además, que la imagen sea Google APIs y tenga tu cuenta
agregada (ejercicio 0.4). Aplica lo de «Cuenta y equipo de
confianza» de la 00.

**El proyecto de inicio.** Desde la raíz del repo de la serie:

```bash
git pull
cp lecciones/03-la-app-conoce-a-firebase/inicio/app/google-services.json lecciones/04-entrar-con-google/inicio/app/
cd lecciones/04-entrar-con-google/inicio
```

El segundo comando lleva tu archivo de la 03 al proyecto de esta lección. Ahí también queda ignorado por git:
el proyecto ya trae su `.gitignore`. Junto está `solucion/`: puedes leerla sin configurar nada, y
**`./verifica solucion` sale en rojo en un clon limpio**, porque sin tu archivo el proyecto no compila (medido el
18-nov-2026: 0 pruebas corridas). Tus respuestas van en `respuestas/04.md`, en la raíz del repo.

**Lo que el proyecto ya trae (andamiaje).** No se evalúa aquí; el paso 2 lo recorre:

| Qué trae | Dónde | Por qué |
|---|---|---|
| Credential Manager 1.6.0 (dos librerías) y `googleid` 1.1.1, junto al BoM de Firebase 35.2.0 de la 03 | `gradle/libs.versions.toml` y `app/build.gradle.kts` | Son las piezas que piden el token. La serie está probada con esas versiones |
| `Resultado`, y `Entrada` convertida en función que se pausa | `Entrada.kt` | Entrar ya no contesta al instante ni con un simple «sí o no» |
| Las dos puertas, con su implementación de verdad | `Puertas.kt` | Por ahí sale tu código a hablar con el teléfono y con Firebase |
| Las pantallas, con el botón «Entrar con Google» y un mensaje por caso | `Tienda.kt`, `PantallaEntrada.kt` | Ya saben mostrar lo que tu entrada conteste |
| `MainActivity` con `crearEntrada`, que por ahora regresa una entrada que no deja pasar | `MainActivity.kt` | Es donde la app arma su entrada. Recibe las dos puertas para que una prueba pueda leer qué audiencia se pidió |
| Las pruebas simuladas y sus dobles | `app/src/test` | Son las que corre el `verifica`. No las cambies |
| Las pruebas instrumentadas, con una copia de los dobles, `androidx.test:runner` 1.7.0 y Espresso 3.7.0 | `app/src/androidTest` y el catálogo | Corren dentro de Android (paso 6). El Espresso que Compose trae por omisión no arrancó en Android 37: 0 de 4, con `NoSuchMethodException` (medido el 18-nov-2026) |
| Un esqueleto de `EntradaConGoogle` | `EntradaConGoogle.kt` | Es el archivo que escribes tú |

**Lo que no hace esta lección.** No se guarda la sesión ni se cierra (lección 06), y no se distingue la primera
vez (lección 07).

---

## Paso 1 · Sin IA: el viaje del token

Este paso es tuyo, sin Claude Code. Antes de escribir código, mira qué viaja.

| # | De quién a quién | Qué viaja |
|---|---|---|
| 1 | Tu app → Credential Manager | Una solicitud: «quiero un token de identidad de Google, para esta audiencia». La audiencia es tu ID de cliente web |
| 2 | Credential Manager → la persona | La ventana de cuentas. La dibuja Android, no tu app |
| 3 | Google → Credential Manager → tu app | Un token de identidad, firmado por Google, con esa audiencia adentro |
| 4 | Tu app → Firebase | El mismo token, sin tocarlo |
| 5 | Firebase → tu app | Un usuario, con su identificador en tu proyecto y, si el proveedor lo entregó, su nombre. Esta app muestra «cliente» si falta |

Tu contraseña no está en ninguna fila. La escribes, si hace falta, en una ventana de Google; a tu app solo le
llega el resultado.

**Abre un token.** Un token de identidad tiene tres partes separadas por puntos: un encabezado, un contenido y
una firma. El contenido no está cifrado: está codificado, que es solo una forma de pasar texto por cualquier
canal. Este contenido es **inventado** para la lección; no lo emitió Google ni sirve para entrar a nada:

```bash
printf '%s' 'eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhdWQiOiIxMDAwMDAwMDAwMDAtd2ViaW52ZW50YWRvLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMDAwMDAwMDAwMDAwMDAwMDAwMDAwIiwibmFtZSI6IkFuYSBJbnZlbnRhZGEiLCJleHAiOjE3OTUwNDY0MDB9' | base64 -d
```

En la Mac de la serie (macOS 27) funcionaron las dos formas, `base64 -d` y `base64 -D` (medido el 18-nov-2026).
Si tu Mac no acepta `-d`, usa `-D`; en Linux es `-d`. El ejemplo está armado para decodificarse así de simple.
Un token real usa una variante, Base64URL, y puede pedir más pasos. **No decodifiques ni pegues aquí un token
real:** el ejercicio se hace con el inventado.

**Predice** antes de correrlo: ¿vas a poder leerlo sin ninguna clave? Esto contestó en la Mac el 18-nov-2026:

```text
{"iss":"https://accounts.google.com","aud":"100000000000-webinventado.apps.googleusercontent.com","sub":"000000000000000000000","name":"Ana Inventada","exp":1795046400}
```

Cinco datos, con los nombres que usa la documentación de Google:

- **`iss`**, quién lo emitió. Debe ser Google.
- **`aud`**, la audiencia: para quién es. Debe ser un ID de cliente de tu proyecto.
- **`sub`**, el identificador de la cuenta. La documentación pide usar este, y no el correo, para reconocer a una
  persona.
- **`name`**, el nombre.
- **`exp`**, cuándo caduca.

Cualquiera puede leer un token. Lo que nadie más puede hacer es **firmarlo** como Google. Por eso quien lo recibe
revisa cuatro cosas, según esa documentación: la firma, que `iss` sea Google, que `aud` sea suyo y que `exp` no
haya pasado. En esta serie eso lo hace Firebase: su documentación dice que le pasas las credenciales y que sus
servicios las verifican y te contestan.

**Dibuja el tuyo.** En `respuestas/04.md`, o en papel con una foto, dibuja el viaje con cinco flechas y escribe
sobre cada una qué viaja. Hazlo sin mirar la tabla y luego compáralo.

**Lo demuestra:** tu diagrama, comparado con la tabla (objetivo 4.1). Ninguna prueba lo lee.

---

## Paso 2 · Lo que el proyecto ya trae

Cuatro archivos cambiaron respecto a la 03. Ábrelos; todavía no los edites. Vas a ver palabras nuevas de
Kotlin. Para esta lección te bastan tres, y las demás son piezas dadas:

- **`suspend`:** esta función espera.
- **`try` / `catch`:** traduce un problema a un resultado (paso 4).
- **`sealed`:** obliga a cubrir todos los resultados.

**`Entrada.kt`: el contrato creció.** En la 02, `entrar()` regresaba un nombre o `null`. Ahora dice:

```kotlin
interface Entrada {
    suspend fun entrar(): Resultado
}
```

- **`suspend`** marca una función que se puede pausar y continuar después sin bloquear lo demás, según la
  documentación de Kotlin. Entrar con Google abre una ventana, espera a una persona y habla por la red: mientras
  tanto la pantalla sigue viva.
- **`Resultado`** es una lista cerrada (`sealed`) de cuatro casos: `Entro(nombre)`, `SinCuentas`, `Cancelada` y
  `Fallo`. Un `null` no alcanzaba para decir **por qué** no se entró. Como la lista es cerrada, el compilador
  sabe que no hay un quinto caso.

**`Puertas.kt`: por dónde sale tu código.** Dos interfaces de una sola función:

```kotlin
interface SelectorDeCuentas {
    suspend fun pedir(solicitud: GetCredentialRequest): GetCredentialResponse
}

interface VerificadorDeToken {
    suspend fun verificar(tokenDeIdentidad: String): String
}
```

La primera le pide una credencial al teléfono; su versión de verdad llama a Credential Manager. La segunda
entrega el token; su versión de verdad llama a Firebase con `GoogleAuthProvider.getCredential` y
`signInWithCredential`, que son las dos llamadas de la guía de Firebase. Es la misma idea de la 02: tu clase
recibe las puertas y no sabe cuáles le tocaron. En la app son las de verdad; en las pruebas, dos de mentira que
viven en `src/test`.

La puerta del teléfono es un recorte didáctico: le pasa la `Activity` directa a Credential Manager. La guía
vigente de Android recomienda envolverla en un `MutableContextWrapper`, para no retenerla si Android vuelve a
crear la pantalla con la ventana abierta. Según las notas de versión, la librería actualiza ese envoltorio por
su cuenta desde la 1.7.0-alpha03, y la serie fija la 1.6.0. El archivo lo dice en un comentario.

**`MainActivity.kt`: dónde se arma.** `onCreate` llama a `crearEntrada()` y le pasa el resultado a `Tienda`.
`crearEntrada` recibe las dos puertas, con las de verdad por omisión, y por ahora regresa
`EntradaSinProveedor()`, la que no deja pasar. Su cuerpo lo cambias en el paso 3.

**`Tienda.kt`: quién llama.** El botón ya no llama a `entrar()` directo. Lo hace dentro de `alcance.launch { … }`.
`rememberCoroutineScope` da un lugar donde correr tareas que se pausan, y la documentación de Compose dice que
ese lugar se cancela solo cuando la pantalla deja de existir. Ahí mismo está `mensajeDe`, con un texto por caso.

**Mide el punto de partida.** Desde la raíz del repo:

```bash
./lecciones/04-entrar-con-google/verifica
```

El 18-nov-2026, con un archivo inventado completo, la última línea fue
`Resultado: 6 de 20 pruebas en verde (14 en rojo, 0 con error, 0 saltadas)`. Las seis verdes son del andamiaje:
las pantallas, sus textos y el recurso que generó el plugin. Las catorce rojas esperan tu código.

---

## Paso 3 · Con Claude Code: el camino en que todo sale bien

Aquí entra la herramienta, y entra siempre igual: **predices → le pides → lees lo que generó → corres el
`verifica` → lo explicas con tus palabras.**

1. **Predice.** Escribe en `respuestas/04.md`: de las cinco filas del viaje, ¿cuáles ocurren dentro de
   `EntradaConGoogle` y cuáles detrás de una puerta?
2. **Pídele** a Claude Code, parado en `inicio/`: «En `EntradaConGoogle.kt`, escribe `entrar()` solo para el
   caso en que todo sale bien: arma un `GetSignInWithGoogleOption` con `idDeClienteWeb`, mételo en un
   `GetCredentialRequest`, pídelo con `selector.pedir`, saca el token de identidad con
   `GoogleIdTokenCredential.createFrom` y entrégalo a `verificador.verificar`; regresa `Resultado.Entro` con el
   nombre. Si la credencial no es del tipo de Google, regresa `Resultado.Fallo`. No atrapes excepciones todavía.
   En `MainActivity.kt`, cambia solo el cuerpo de `crearEntrada` para que regrese `EntradaConGoogle` con
   `getString(R.string.default_web_client_id)` y las dos puertas que recibe, `selector` y `verificador`; y
   borra `EntradaSinProveedor` de `Entrada.kt`. No cambies ningún otro archivo, no escribas ningún ID de
   cliente en el código y no imprimas ni guardes el token.»
3. **Lee** el cambio completo antes de aceptarlo. El centro debe parecerse a esto:

```kotlin
override suspend fun entrar(): Resultado {
    // Fila 1: la solicitud, con la audiencia.
    val opcion = GetSignInWithGoogleOption.Builder(idDeClienteWeb).build()
    val solicitud = GetCredentialRequest.Builder().addCredentialOption(opcion).build()
    // Filas 2 y 3: el teléfono muestra la ventana y regresa lo elegido.
    val credencial = selector.pedir(solicitud).credential
    if (credencial !is CustomCredential ||
        credencial.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
        return Resultado.Fallo
    }
    val token = GoogleIdTokenCredential.createFrom(credencial.data).idToken
    // Filas 4 y 5: Firebase lo verifica y contesta quién es.
    return Resultado.Entro(verificador.verificar(token))
}
```

   Seis cosas que revisar al leer:

   - **La opción es `GetSignInWithGoogleOption`.** Si aparece `GetGoogleIdOption`, compárala con lo que
     necesita un botón y pide el cambio explicando la diferencia: es la de la escena.
   - **La audiencia llega por parámetro.** En `EntradaConGoogle.kt` no debe haber ningún texto que termine en
     `.apps.googleusercontent.com`.
   - **`crearEntrada` lee `R.string.default_web_client_id` y se lo pasa a `EntradaConGoogle`.** Ese recurso lo
     generó el plugin con el cliente web de tu archivo (lección 03, paso 4). Y `onCreate` sigue llamando a
     `crearEntrada()`.
   - **El token va tal cual.** `idToken` entra a `verificar` sin recortes ni cambios.
   - **Regla de datos: nadie imprime, pega ni guarda el token.** Ni en el registro (`Log`, `println`), ni en un
     archivo o en las preferencias, ni en `respuestas/04.md`. En la app va directo de la credencial a Firebase.
     La documentación de Google dice que estos tokens son sensibles y se pueden usar mal si alguien los
     intercepta. Los tokens inventados de las pruebas son la única excepción, y es didáctica: los dobles sí
     los guardan en una lista, porque no son de nadie. A la lista de la lección 03 de lo que nunca se sube
     (claves de firma, contraseñas, la clave de Microsoft) súmale los tokens de identidad reales.
   - **Nadie tocó las pruebas, `Puertas.kt` ni tu `google-services.json`.**

4. **Corre el `verifica`.** Con ese código dio `Resultado: 13 de 20 pruebas en verde (7 en rojo, …)` el
   18-nov-2026. Las siete rojas son los casos en que no se entra: el paso 4.
5. **Explica**, en tres líneas de `respuestas/04.md`: qué recibe `verificar`, de dónde salió y quién lo firmó.

**¿Por qué el ID de cliente web, y no el de Android?** Porque el token no es para tu app: es para quien lo va a
verificar. La guía de Firebase lo escribe así en su ejemplo: el ID de cliente de tu servidor, no el de Android.
En esta serie ese servidor es Firebase.

**¿Por qué ese ID no es una contraseña?** Por tres razones que puedes comprobar:

- Viaja dentro de tu app, como recurso. Cualquiera que tenga la app instalada puede leerlo.
- Viene de `google-services.json`, y la documentación de Firebase dice que ese archivo trae identificadores
  únicos pero no secretos.
- Solo dice **para quién** se pide el token. No prueba quién eres ni deja entrar a nadie: lo que prueba algo es
  la firma de Google, y esa no la tiene tu app.

**Lo demuestra, y hasta dónde.** En el nivel simulado, el `verifica` demuestra que tu clase pide una sola
opción y es la del botón, que su audiencia es el ID que recibió, y que el token pasa tal cual de una puerta a la
otra. Y una prueba abre `MainActivity`, le pasa las puertas de mentira a `crearEntrada` y lee la audiencia que
se pidió: debe ser el cliente web de tu archivo. Con otro recurso en su lugar, esa prueba se pone roja aunque
el nombre correcto siga escrito en el archivo (medido el 18-nov-2026: 19 de 20).

Las puertas eran de mentira: no se le pidió nada a Google ni a Firebase. Y queda un hueco que ninguna prueba ve:
que `onCreate` le pase a `Tienda` lo que regresa `crearEntrada()`. Eso el `verifica` solo lo busca como texto, y
lo llama así, «presencia textual». Con `onCreate` armando otra entrada, las 20 pruebas dan verde y lo único
rojo es esa marca (medido el 18-nov-2026).

---

## Paso 4 · Sin IA: las tres formas de no entrar

Este es el ejercicio 4.3 y se hace **sin Claude Code**. Son tres bloques `catch`, y los escribes tú.

Cuando la puerta del teléfono no puede regresar una credencial, lanza una excepción. La documentación de Android
lista las que importan aquí:

| Excepción | Cuándo, según la documentación | Tu entrada contesta |
|---|---|---|
| `NoCredentialException` | No hay una credencial disponible para la persona: por ejemplo, no hay cuentas en el dispositivo o necesitan volver a autenticarse. La excepción no dice cuál de las causas fue | `Resultado.SinCuentas` |
| `GetCredentialCancellationException` | La persona canceló. La misma guía avisa que muchas «cancelaciones» seguidas pueden esconder una mala configuración | `Resultado.Cancelada` |
| Cualquier otra | Una falla del teléfono, o Firebase rechazó el token | `Resultado.Fallo` |

**1. Predice.** Las dos primeras son hijas de una misma clase, `GetCredentialException`. Si pones primero un
`catch` general y después los dos específicos, ¿cuál de los tres mensajes verá siempre la persona?

**2. Escríbelo.** Envuelve lo que escribiste en el paso 3 con `return try { … }` y agrega, en este orden:

```kotlin
} catch (e: NoCredentialException) {
    Resultado.SinCuentas
} catch (e: GetCredentialCancellationException) {
    Resultado.Cancelada
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Resultado.Fallo
}
```

El tercero no es un caso de la entrada. `CancellationException` (de `kotlinx.coroutines`) es Kotlin avisando que
la tarea se canceló, por ejemplo porque la pantalla se cerró. La documentación de Kotlin pide que, si la
atrapas, la vuelvas a lanzar. Como el `catch` general de abajo también la atraparía, va antes y la deja pasar.

**3. Corre el `verifica`.** Ahora la línea es `Resultado: 20 de 20 pruebas en verde`. Queda una marca en rojo, la
de `respuestas/04.md`: se llena en el paso 5.

**4. Comprueba tu predicción.** Mueve el `catch (e: Exception)` arriba de los otros y corre el `verifica`: el
18-nov-2026 dio 16 de 20, con «sin cuentas» y «cancelación» en rojo. Regrésalo a su lugar.

Los textos que ve la persona ya están en `mensajeDe`, en `Tienda.kt`:

| Resultado | Mensaje |
|---|---|
| `SinCuentas` | «No encontré una cuenta de Google disponible para entrar. Agrega una o vuelve a iniciar sesión en Google y prueba otra vez.» |
| `Cancelada` | «Cerraste la ventana sin elegir cuenta. Toca el botón cuando quieras intentar otra vez.» |
| `Fallo` | «No se pudo entrar. Revisa tu conexión y vuelve a intentar.» |

**Lo demuestra:** el `verifica` simulado, los tres casos (objetivo 4.3). Cada caso lleva pruebas de dos tipos:
unas le lanzan la excepción a tu clase y leen qué contestó; otra dibuja la pantalla con tu clase y busca su
mensaje. Las excepciones las lanzó un doble: ninguna vino de un teléfono.

---

## Paso 5 · Sin IA: rompe uno a propósito

También **sin Claude Code**. Vas a provocar el error de la escena.

**1. Predice.** Vas a cambiar la opción del botón por `GetGoogleIdOption`, la de la hoja de Credential Manager.
No es «la del inicio automático»: según la guía de Android, ese solo ocurre si además se activa
`setAutoSelectEnabled(true)` y se cumplen sus condiciones, y aquí no se activa. ¿Compila? ¿Cuántas pruebas se
ponen rojas, y cuáles?

**2. Rómpelo y mide.** En `EntradaConGoogle.kt`, cambia la línea de la opción por esta (y su `import`):

```kotlin
val opcion = GetGoogleIdOption.Builder()
    .setServerClientId(idDeClienteWeb)
    .setFilterByAuthorizedAccounts(false)
    .build()
```

Corre el `verifica`. Esto dio el 18-nov-2026 (recortado):

```text
❌ Algún .kt de la app nombra GetGoogleIdOption: es la opción de la hoja de Credential Manager, no la del …
❌ Tu entrada pidió otra opción: la solicitud llevó GetGoogleIdOption y debe llevar GetSignInWithGoogleOption
❌ La audiencia de la solicitud no es el ID de cliente que recibió EntradaConGoogle (o la opción no es …
❌ La Entrada que arma MainActivity.crearEntrada no pide el token con el cliente web de tu archivo como …
Resultado: 17 de 20 pruebas en verde (3 en rojo, 0 con error, 0 saltadas), leído de …
```

Copia a `respuestas/04.md` la línea que dice «Tu entrada pidió otra opción».

**3. Lo que esas pruebas no te dicen.** Compila, y 17 de 20 siguen en verde: el token inventado pasa igual por
las dos puertas de mentira. Las pruebas solo saben que pediste **otra** opción. Lo que esa opción hace en un
teléfono se midió aparte:

- El 13-nov-2026, en el emulador con una cuenta agregada: `NoCredentialException` y `[28433]` en el registro.
- El 18-nov-2026, en un emulador nuevo **sin** cuenta (Pixel 9, imagen `google_apis_playstore` de Android 37,
  con un archivo inventado): la app mostró el mensaje de «sin cuentas» y el registro dijo `[28433]`. Con la opción del botón, en ese mismo emulador, Android abrió la pantalla de Google para
  agregar una cuenta; al regresar con «atrás», la app mostró el mensaje de la cancelación.

Son observaciones de esos dos días, con esas versiones; no una regla que Google publique.

**4. Arréglalo.** Regresa la línea a `GetSignInWithGoogleOption.Builder(idDeClienteWeb).build()` y su `import`.
Corre el `verifica`: 20 de 20, y ahora también la marca de `respuestas/04.md`.

El verde confirma lo que revisan esas pruebas y que la línea está en tu archivo; tu explicación completa la
evidencia. El ejercicio está terminado cuando puedes marcar las tres:

- [ ] Tu predicción del punto 1 está escrita, con lo que viste debajo.
- [ ] Pegaste la línea del rojo.
- [ ] Dijiste por qué 17 pruebas siguieron en verde con un código que en el teléfono no deja entrar.

**Lo demuestra:** «Rompe uno a propósito». Es base del objetivo 4.2: saber por qué el botón lleva esa opción.

---

## Paso 6 · Dentro de Android: el nivel instrumentado

Las pruebas de los pasos anteriores corren en tu computadora, con una imitación de Android. Estas corren
**dentro** de Android, en tu emulador o en tu teléfono. Con el emulador abierto, desde la raíz del repo:

```bash
./lecciones/04-entrar-con-google/verifica inicio instrumentado
```

Repite el nivel simulado y después instala la app y su app de pruebas en el dispositivo. Ahí dibuja `Tienda`
con tu `EntradaConGoogle` y las mismas dos puertas de mentira, toca los botones y busca los textos. Con la
solución, en un emulador nuevo y sin cuenta (Pixel 9, imagen `google_apis_playstore` de Android 37), esto dio
el 18-nov-2026 (recortado):

```text
✅ Instrumentado: dentro de Android, con las puertas de mentira, tocar «Entrar con Google» lleva al inicio …
✅ Instrumentado: dentro de Android, cada uno de los tres casos muestra su mensaje sin pasar al inicio (3 de 3)
Resultado: 20 de 20 pruebas en verde (0 en rojo, 0 con error, 0 saltadas), leído de …
Instrumentado: 4 de 4 pruebas en verde (0 en rojo, 0 con error, 0 saltadas), leído de …
```

Sobre el proyecto de inicio sin tocar dio 0 de 4, y sin ningún dispositivo conectado el `verifica` sale en rojo
y dice que no se corrió.

**Lo demuestra, y hasta dónde.** Demuestra que las pantallas, el toque y tu clase funcionan dentro de Android,
con las librerías de Credential Manager y de `googleid` cargadas en el dispositivo. **No** demuestra nada de tu
proyecto de Firebase: las puertas siguen siendo de mentira, no se abre la ventana de cuentas y no hace falta
cuenta ni red hacia Google. Por eso pasó en un emulador sin cuenta.

---

## Paso 7 · En el teléfono: el nivel real

Hasta aquí nada habló con Google. Este paso sí, una vez, a mano. Es el único que comprueba lo que la lección 03
dejó escrito en tu archivo: que la huella está registrada, que Google está habilitado y que el ID de cliente es
de tu proyecto.

Con el emulador abierto y tu cuenta agregada, parado en `inicio/`:

```bash
./gradlew installDebug
```

Abre «La tienda», toca «Entrar» y luego «Entrar con Google».

**Lo que se vio en el ensayo del 13-nov-2026** (Mac, emulador con imagen `google_apis_playstore` y una cuenta
agregada): se abrió la ventana de cuentas de Android, se eligió la cuenta y la app saludó con el nombre de esa
cuenta. Ese ensayo usó un código de prueba con las mismas llamadas, no el de `solucion/`:
[pendiente: repetir la entrada real con el código de esta lección; la clase `VerificadorConFirebase` solo se ha
compilado, no se ha ejecutado contra Firebase].

**Si tu cuenta usa llave de acceso (passkey).** En el ensayo, Google pidió escanear un código QR con el
teléfono, y eso no completó en el emulador. Se tocó **«Try another way»** y se entró con la contraseña o con el
aviso en el teléfono (lección 00, ejercicio 0.4).

**El cuadro de permisos.** Según la ayuda de Google, al elegir «Sign in with Google» aceptas compartir con esa
app tres datos: nombre, correo y foto de perfil. Con ellos la app puede crear tu cuenta.
[pendiente: el texto exacto del cuadro, con captura; del ensayo no quedó registrado].

Este es el ejercicio 4.4. Clasifica cada dato en `respuestas/04.md` antes de mirar la tabla de «Compara tus
respuestas»: **qué es, para qué se pide y si tu app lo recibe.**

1. Tu nombre.
2. Tu correo.
3. Tu foto de perfil.
4. Tu contraseña de Google.

**Al terminar, en un equipo que no es tuyo:** saca tu cuenta del emulador y borra sus datos (lección 00).

**Lo demuestra:** la entrada real, con tu cuenta, demuestra que huella, ID de cliente y proveedor están bien
configurados. No demuestra los tres casos de no entrar: esos los cubre el nivel simulado.

---

## Tus ejercicios

Los pasos 1, 4 y 5 se hacen **sin IA**.

| # | Haces | Está hecho cuando |
|---|---|---|
| 4.1 | Dibujas el viaje con cinco flechas y qué viaja en cada una | Tu diagrama coincide con la tabla del paso 1. Lo comparas tú |
| 4.2 | Implementas `EntradaConGoogle` con la opción del botón y el ID de cliente web, y explicas por qué ese ID no es una contraseña | Tres evidencias. Simulado: el `verifica` da 20 de 20, que valida lo que tu clase pide y a dónde lleva el token, con puertas de mentira; sus marcas «Texto» son presencia textual. Instrumentado: `./verifica inicio instrumentado` da 4 de 4, que valida lo mismo dentro de Android. Real: entras con tu cuenta en el paso 7, que es lo único que valida tu proyecto. Tu explicación está en `respuestas/04.md` |
| 4.3 | Manejas sin cuentas, cancelación y error, cada uno con su mensaje | Las tres marcas de casos del `verifica` están en verde. Las excepciones las lanza un doble |
| 4.4 | Clasificas los cuatro datos del paso 7 | 4 de 4 contra la tabla de «Compara tus respuestas» |

**Tiempo:** calcula unas tres horas y media, con el «Rompe uno a propósito», el nivel instrumentado y la entrada
real. Es un estimado: lo
ajustamos con lo que tarde la primera cohorte.

---

## ¿No te corre?

| Ves | Causa probable | Qué haces |
|---|---|---|
| `File google-services.json is missing.` | No copiaste tu archivo a `inicio/app/` | El `cp` de «Antes de empezar». Sobre `solucion/` es lo esperado en un clon limpio |
| `Unresolved reference 'default_web_client_id'` | Tu archivo no trae cliente web (tipo 3), y el plugin no generó el recurso. Medido el 18-nov-2026 con un archivo inventado sin clientes | Descárgalo otra vez después de la huella y de habilitar Google (lección 03, paso 3) |
| ❌ «sin cuentas» y «cancelación» en rojo, con los `catch` escritos | El `catch (e: Exception)` está antes que los específicos | Paso 4: los específicos van primero |
| ❌ «Tu entrada pidió otra opción» | Quedó `GetGoogleIdOption` | Paso 5, punto 4 |
| En el teléfono, «No encontré una cuenta de Google disponible…» con la cuenta agregada | En el ensayo del 13-nov-2026 la opción era `GetGoogleIdOption`. Según la guía, también pasa si la cuenta necesita volver a iniciar sesión | Revisa que la opción sea `GetSignInWithGoogleOption`, y que tu cuenta siga iniciada en Ajustes |
| ❌ «Instrumentado: no hay ningún emulador abierto ni teléfono conectado» | `adb devices` no lista ningún dispositivo en estado `device` | Abre el emulador (lección 00) y espera a que termine de arrancar |
| En las pruebas instrumentadas, `NoSuchMethodException: android.hardware.input.InputManager.getInstance` | Se quitó la línea de Espresso 3.7.0 del andamiaje y quedó el que Compose trae por omisión (medido el 18-nov-2026 en Android 37) | Regresa `androidTestImplementation(libs.espresso.core)` |
| En el teléfono se abre una pantalla de Google para iniciar sesión | El emulador no tiene ninguna cuenta (medido el 18-nov-2026) | Agrega tu cuenta ahí, o en Ajustes (lección 00, ejercicio 0.4) |
| Google pide escanear un código QR | Tu cuenta usa llave de acceso | «Try another way» |
| En el teléfono, «No se pudo entrar…» | [pendiente: medir qué causa da este mensaje con un proyecto real: huella sin registrar, Google sin habilitar o sin red] | Revisa la lección 03, paso 3, y que el emulador tenga red |
| `GetCredentialProviderConfigurationException` | Según la guía de Android, falta la librería `credentials-play-services-auth`. No se midió aquí | Revisa que `app/build.gradle.kts` siga trayendo las tres líneas del andamiaje |
| Cambiaste de computadora y no tienes la huella de depuración de ésta | Sin `google-services.json` el proyecto no compila, y el almacén de depuración lo crea la primera compilación: un círculo (medido en Linux Mint 22.3, 18-nov-2026) | Copia a `inicio/app/` tu archivo de la 03 aunque traiga la huella de la otra computadora, corre `./gradlew assembleDebug` (compila igual), saca la huella con `./gradlew signingReport`, regístrala en Firebase y vuelve a bajar tu archivo |

En Linux Mint 22.3 (x86_64), el `verifica` de esta lección salió igual que en la Mac (18-nov-2026), en el nivel
simulado y con un archivo de prueba que traía la huella de esa computadora: sobre `inicio/`, 6 de 20 en verde; sobre
`solucion/`, 20 de 20. Sin archivo, «0 pruebas corridas», como en la Mac. El nivel instrumentado no se midió en Linux.
Las dependencias de esta lección le sumaron unos 300 MB a `~/.gradle`.

---

## Para cerrar

**Contesta sin volver a leer:**

1. ¿Qué es un token de identidad, quién lo firma y quién lo verifica en esta serie?
2. ¿Qué dice la audiencia, y de dónde saca tu app el valor que pone ahí?
3. Tu `verifica` da 20 de 20. Di una cosa que eso demuestra y una que no.
4. ¿Por qué el `catch` general va al final?

**Lo que sigue:** en la L05 agregas un segundo proveedor, Microsoft, sin cambiar las pantallas.

---

## Compara tus respuestas

**Paso 1.** Sí se puede leer sin clave: el contenido de un token solo está codificado. Lo que protege al token es
la firma, no un cifrado. Tu diagrama debe traer las cinco flechas de la tabla, con la audiencia en la primera,
el token en la tercera y en la cuarta, y ninguna flecha con tu contraseña hacia la app.

**Paso 3.** Dentro de `EntradaConGoogle` ocurre armar la solicitud (parte de la fila 1) y pasar el token de una
puerta a la otra. Detrás de la puerta del teléfono, las filas 2 y 3; detrás de la del verificador, la 4 y la 5.
`verificar` recibe el token de identidad, que salió de la credencial que entregó el teléfono y que firmó Google.

**Paso 4.** Siempre el de `Fallo`: el `catch` general atrapa también a las dos hijas, y los específicos de abajo
nunca se alcanzan.

**Paso 5.** Compila, y se ponen rojas tres pruebas: la que revisa la opción y las dos que le leen la audiencia,
a tu clase y a la entrada que arma la app. Las otras 17 siguen en verde porque usan puertas de mentira, que entregan el token inventado sin importar qué opción
se pidió. Por eso el nivel simulado no alcanza para decir «en el teléfono entra».

**Ejercicio 4.4.**

| Dato | Qué es | Para qué se pide | ¿La app lo recibe? |
|---|---|---|---|
| Nombre | El nombre de tu cuenta de Google | Para crear tu cuenta en la app | Sí, en este flujo de Google, si la cuenta lo tiene. Llega por Firebase y se ve en «Hola, …»; si falta, la app dice «cliente» |
| Correo | La dirección de tu cuenta | Para crear tu cuenta en la app | Sí: la credencial lo trae, y el usuario de Firebase tiene un correo principal. Esta app no lo muestra |
| Foto de perfil | La dirección de tu foto | Para crear tu cuenta en la app | Sí: la credencial trae su dirección. Esta app no la usa |
| Contraseña | La clave de tu cuenta de Google | No se pide para la app | No. La ayuda de Google no la lista entre lo compartido, y la credencial que recibe la app no tiene un campo para ella |

**Para cerrar:**

1. Un texto firmado por Google que dice quién eres y para quién se emitió. Lo firma Google y lo verifica
   Firebase; tu app solo lo lleva.
2. Para quién es el token. Tu app lo saca del recurso `default_web_client_id`, que el plugin generó con el
   cliente web de tu `google-services.json`.
3. Demuestra qué pide tu clase (la opción y la audiencia), que el token pasa de una puerta a la otra, y qué
   resultado y qué mensaje da cada caso. No demuestra que tu proyecto de Firebase esté bien configurado ni que
   alguien pueda entrar: las puertas eran de mentira.
4. Porque atrapa todo, incluidas las excepciones específicas. Si va primero, los otros nunca corren.

---

## Fuentes de la lección

Abiertas el 18-nov-2026.

- Android Developers. *Implement Sign in with Google* (el flujo del botón con `GetSignInWithGoogleOption`; el de la hoja con `GetGoogleIdOption`; las condiciones del inicio automático; el `MutableContextWrapper`; `GoogleIdTokenCredential.createFrom`; validar el token en el servidor). https://developer.android.com/identity/sign-in/credential-manager-siwg-implementation
- Android Developers. *Sign in with Google overview* (por qué un botón además de la hoja; con el botón se puede agregar una cuenta). https://developer.android.com/identity/sign-in/credential-manager-siwg
- Android Developers. *Troubleshoot Credential Manager* (`NoCredentialException`, `GetCredentialCancellationException`, `GetCredentialProviderConfigurationException`). https://developer.android.com/identity/sign-in/credential-manager-troubleshooting-guide
- Android Developers. *androidx.credentials.exceptions* (la descripción de cada excepción). https://developer.android.com/reference/kotlin/androidx/credentials/exceptions/package-summary
- Android Developers. *Credentials*, notas de versión (1.6.0 es la estable, del 8-abr-2026; el cambio automático de contexto llegó en la 1.7.0-alpha03). https://developer.android.com/jetpack/androidx/releases/credentials
- Android Developers. *Side-effects in Compose* (`rememberCoroutineScope`). https://developer.android.com/develop/ui/compose/side-effects
- Android Developers. Guía de pruebas de Compose (las dependencias de las pruebas y `app/src/androidTest`). https://developer.android.com/develop/ui/compose/testing
- Google for Developers. *OpenID Connect* (los tokens de identidad son sensibles; se validan antes de usar sus datos; sus valores van en Base64URL). https://developers.google.com/identity/openid-connect/openid-connect
- Google for Developers. *GoogleIdTokenCredential* (los datos que trae la credencial). https://developers.google.com/identity/android-credential-manager/android/reference/com/google/android/libraries/identity/googleid/GoogleIdTokenCredential
- Google for Developers. *Verify the Google ID token on your server side* (firma, `aud`, `iss`, `exp`; usar `sub` y no el correo). https://developers.google.com/identity/gsi/web/guides/verify-google-id-token
- Ayuda de Cuentas de Google. *Sign in with Google* (qué se comparte: nombre, correo y foto de perfil). https://support.google.com/accounts/answer/12849458
- Firebase. *Authenticate with Google on Android* (las librerías, `default_web_client_id`, `GoogleAuthProvider.getCredential` y `signInWithCredential`; su ejemplo usa `GetGoogleIdOption` y Credential Manager 1.3.0). https://firebase.google.com/docs/auth/android/google-signin
- Firebase. *Firebase Authentication* (le pasas las credenciales y sus servicios las verifican). https://firebase.google.com/docs/auth
- Firebase. *Users in Firebase projects* (identificador, correo, nombre y foto del usuario). https://firebase.google.com/docs/auth/users
- Kotlin. *Coroutines basics* (`suspend`, `launch`), *Cancellation and timeouts* (volver a lanzar `CancellationException`) y *Sealed classes and interfaces*. https://kotlinlang.org/docs/coroutines-basics.html · https://kotlinlang.org/docs/coroutines-cancellation.html · https://kotlinlang.org/docs/sealed-classes.html
- Repositorio Maven de Google: sus índices listan `credentials` 1.6.0, `googleid` 1.1.1 y `firebase-bom` 35.2.0. De `googleid` existe una versión más nueva; la serie fija la 1.1.1, que es la de la guía de Firebase y la del ensayo. Los mismos índices listan `androidx.test:runner` 1.7.0 y `espresso-core` 3.7.0 como las estables más recientes. https://dl.google.com/dl/android/maven2/androidx/credentials/credentials/maven-metadata.xml · https://dl.google.com/dl/android/maven2/com/google/android/libraries/identity/googleid/googleid/maven-metadata.xml · https://dl.google.com/dl/android/maven2/com/google/firebase/firebase-bom/maven-metadata.xml · https://dl.google.com/dl/android/maven2/androidx/test/runner/maven-metadata.xml · https://dl.google.com/dl/android/maven2/androidx/test/espresso/espresso-core/maven-metadata.xml
- Ensayo de la serie, 13-nov-2026, Mac con Apple silicon: la entrada real con una cuenta; `GetGoogleIdOption` con `NoCredentialException` y `[28433]`; la llave de acceso y «Try another way».
- Medición propia: Mac con Apple silicon, 18-nov-2026. El `verifica` sobre `inicio/` y `solucion/`, cada paso de la lección y cada sabotaje, con archivos inventados y sin tocar la consola de Firebase ni ninguna cuenta. Y la app en un emulador nuevo, sin cuenta, que se borró al terminar: las dos opciones, y la cancelación con «atrás». Las pruebas instrumentadas se midieron en otro emulador nuevo, sin cuenta, también borrado.

Revisión externa (18-nov-2026): CON CAMBIOS, 3 de prioridad alta, 5 media y 2 baja; aplicada.
