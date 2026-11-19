# Lección 06 — Dos botones, una cuenta

**Antes de leer, tres palabras.** Una línea cada una.

- **Colisión:** lo que pasa cuando alguien entra con un proveedor y su correo ya tiene usuario en tu proyecto,
  creado con otro proveedor. La viste en la lección 05.
- **Enlazar:** sumarle a un usuario que ya existe otra forma de entrar. No crea un usuario nuevo.
- **Una cuenta por correo:** la opción de tu proyecto de Firebase que hace que la colisión exista.

**Qué te llevas:** una persona con el mismo correo en Google y en Microsoft que termina con **un** usuario en tu
tienda, al que llega con cualquiera de los dos botones, porque ella lo confirmó.

**Idea nueva de esta lección:** una persona, dos proveedores. Con «una cuenta por correo», el segundo se enlaza
a la cuenta que ya existe, y solo con su confirmación. Lo demás —la pantalla que pregunta, la puerta del enlace
y la nota que distingue un enlace de una entrada— viene dado y explicado, y no se evalúa aquí.

---

## La escena

Ana entró a la tienda con Google la semana pasada. Hoy toca el otro botón. Su cuenta de Microsoft tiene el mismo
correo.

Para ella es la misma persona entrando a la misma tienda. Para tu proyecto son dos credenciales distintas que
dicen tener el mismo correo.

Eso se probó en el ensayo de esta serie, el 13 de noviembre de 2026, con una cuenta de Microsoft cuyo correo ya
había entrado con Google. Firebase no la dejó pasar, y contestó: «An account already exists with the same email
address but different sign-in credentials».

Hay dos finales malos. En uno, la app crea un segundo usuario, y las órdenes de Ana quedan repartidas entre dos
cuentas que no se conocen. En el otro, la app junta las dos cuentas por su lado, sin preguntarle a nadie. La
lección 05 se quedó en decirle «entra con el otro botón». Esta escribe el final bueno: entra con el que ya
tenía, la app le pregunta, y si acepta, los dos botones llegan al mismo lugar.

---

## Antes de empezar

**Qué necesitas:** la [05](05-entrar-con-microsoft.md) terminada (su `verifica` en 44 de 44). Los pasos 1 a 4 no
piden ninguna cuenta de Microsoft. El paso 5 sí: tu registro en Entra de la 05, funcionando.

**El proyecto de inicio.** Desde la raíz del repo de la serie:

```bash
git pull
cp lecciones/03-la-app-conoce-a-firebase/inicio/app/google-services.json lecciones/06-dos-botones-una-cuenta/inicio/app/
cd lecciones/06-dos-botones-una-cuenta/inicio
```

Parte de la solución de la 05, con el andamiaje de esta lección encima. `./verifica solucion` sale en rojo en un
clon limpio, porque sin tu archivo el proyecto no compila. Tus respuestas van en `respuestas/06.md`.

**Lo que no hace esta lección.** No deshace un enlace, no guarda la sesión ni la cierra (lección 07) y no
distingue la primera vez (lección 08).

---

## Paso 1 · En la consola: la regla que hace existir la colisión

La colisión solo existe con una opción de tu proyecto de Firebase: «una cuenta por correo» (*One account per
email address*). La referencia de Firebase dice que ese error solo se lanza con ella activada, y la recomienda.
Con varias cuentas por correo, Firebase puede crear un segundo usuario: el primer final malo de la escena, y
nada que resolver.

Búscala en la configuración de Authentication de tu consola y comprueba que está activa. Anota en
`respuestas/06.md` esta línea, sin correos ni claves:

```text
una cuenta por correo: activa
```

El `verifica` no ve tu consola: solo busca esa línea. [pendiente: ensayo con cuenta — la ruta exacta de esa
opción en la consola].

**Lo demuestra:** tu revisión, a mano. Es parte de la evidencia del 6.1.

---

## Paso 2 · Lo que el proyecto ya trae

**El orden lo pone Firebase.** Su guía para enlazar cuentas pide que **primero** la persona entre con cualquier
proveedor y que **después**, a ese usuario que ya entró, se le sume el otro. Así, dice, la persona tiene el
mismo identificador de usuario sin importar con cuál entre. Para Microsoft, la guía de ese proveedor nombra la
llamada: `startActivityForLinkWithProvider`, sobre el usuario que ya entró.

Tres archivos son nuevos y cuatro cambiaron respecto a la solución de la 05. Ábrelos; no los edites:

| Archivo | Qué trae |
|---|---|
| `Enlace.kt` (nuevo) | `ResultadoDeEnlace` (enlazado, sin sesión, cancelado, falla), `EnlaceRetomado` y la interfaz `Enlace`, con `enlazar()` y `retomar()` |
| `PuertaDelEnlace.kt` (nuevo) | La puerta `EnlazadorDeProveedor`. `enlazar` hace la llamada de Firebase; si nadie ha entrado, lanza `NadieHaEntrado` sin abrir nada. `pendiente` recoge un enlace que quedó a medias |
| `PantallaEnlace.kt` (nuevo) | La confirmación: «¿También con Microsoft?», con «Enlazar Microsoft» y «Ahora no» |
| `Tienda.kt` | Recuerda si hubo una colisión. Cuando esa persona entra después con el botón correcto, muestra la confirmación en lugar del inicio. Solo «Enlazar Microsoft» llama a tu código |
| `PuertaDelNavegador.kt` | Una línea: no toma lo pendiente si lo que quedó a medias fue un enlace |
| `MainActivity.kt`, `Armado.kt` | Arman también el enlace. Por ahora `Armado.kt` regresa `EnlaceSinConectar()` |

Lo demás es idéntico a la solución de la 05, y el `verifica` lo compara archivo por archivo.

**Un enlace a medias no es una entrada a medias.** Firebase entrega lo que quedó pendiente por el mismo lugar,
`pendingAuthResult`, sea una entrada o un enlace: su referencia lo dice para las dos llamadas. Y no dice cuál
fue. Si la app lo leyera siempre como entrada, un enlace cancelado se vería como «no pudiste entrar». Por eso
la puerta deja una nota en el teléfono antes de abrir el navegador para enlazar: «hay un enlace en curso». Es
un sí o un no; no guarda cuentas, tokens ni claves. Al abrirse la app, la puerta de entrar no toma lo pendiente
si esa nota está puesta, y `Tienda` le pregunta primero a tu enlace.

**Mide el punto de partida.** Desde la raíz del repo:

```bash
./lecciones/06-dos-botones-una-cuenta/verifica
```

El 18-nov-2026, con un archivo inventado, la última línea fue
`Resultado: 46 de 66 pruebas en verde (20 en rojo, 0 con error, 0 saltadas)`. Son 66: las 44 que heredas de la
05, ya en verde, y 22 del enlace. De esas, dos ya pasan con el andamiaje y veinte esperan tu código.

---

## Paso 3 · Con Claude Code: enlazar y retomar

Entra igual que siempre: **predices → le pides → lees lo que generó → corres el `verifica` → lo explicas.**

1. **Predice**, en `respuestas/06.md`: en el momento de la colisión, antes de que la persona entre con Google,
   ¿a qué usuario se le podría enlazar Microsoft?
2. **Pídele** a Claude Code, parado en `inicio/`: «En `EnlaceConMicrosoft.kt`, toma `EntradaConMicrosoft.kt`
   como referencia. `enlazar()` arma el mismo proveedor, llama a `enlazador.enlazar` y regresa
   `ResultadoDeEnlace.Enlazado`. `retomar()` llama a `enlazador.pendiente()`: si regresa `null`, regresa `null`;
   si no, regresa un `EnlaceRetomado` con su nombre y con `Enlazado` si su `falla` es `null`, o con la falla
   traducida. Las fallas se traducen en una función aparte: `NadieHaEntrado` es `SinSesion`; una
   `FirebaseAuthWebException` con `ERROR_WEB_CONTEXT_CANCELED`, `Cancelado`; cualquier otra, `Fallo`.
   `CancellationException` se deja pasar. En `Armado.kt`, cambia solo el cuerpo de `armarEnlaceConMicrosoft` y
   borra ahí `EnlaceSinConectar`. No cambies ningún otro archivo.»
3. **Lee.** El centro debe parecerse a esto:

```kotlin
override suspend fun enlazar(): ResultadoDeEnlace =
    try {
        val proveedor = OAuthProvider.newBuilder("microsoft.com")
            .addCustomParameter("prompt", "select_account")
            .build()
        enlazador.enlazar(proveedor)
        ResultadoDeEnlace.Enlazado
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        resultadoDe(e)
    }

override suspend fun retomar(): EnlaceRetomado? {
    val pendiente = enlazador.pendiente()
    if (pendiente == null) return null
    val resultado = if (pendiente.falla == null) ResultadoDeEnlace.Enlazado else resultadoDe(pendiente.falla)
    return EnlaceRetomado(pendiente.nombre, resultado)
}

private fun resultadoDe(falla: Exception): ResultadoDeEnlace = when {
    falla is NadieHaEntrado -> ResultadoDeEnlace.SinSesion
    falla is FirebaseAuthWebException && falla.errorCode == "ERROR_WEB_CONTEXT_CANCELED" ->
        ResultadoDeEnlace.Cancelado
    else -> ResultadoDeEnlace.Fallo
}
```

   Revisa tres cosas: que `enlazar()` llame a la puerta del enlace y no vuelva a entrar; que `retomar()` no
   conteste siempre «enlazado»; y que nadie haya tocado las pruebas, las puertas ni las pantallas.

4. **Corre el `verifica`:** `Resultado: 66 de 66 pruebas en verde`. Quedan en rojo las marcas de
   `respuestas/06.md` que te falten.
5. **Explica**, en tres líneas: por qué el enlace se hace sobre el usuario que ya entró, y no en la colisión.

**Lo demuestra, y hasta dónde.** El `verifica` usa un proyecto de mentira (`ProyectoFalso.kt`) con dos
personas: Ana, con el mismo correo en Google y en Microsoft, y Luis, con otro correo. Demuestra que antes de
enlazar Ana choca y no se crea un segundo usuario; que después de enlazar sus dos accesos llegan al mismo
usuario; y que Luis sigue siendo otro. Demuestra la confirmación, con tus clases dibujadas en `Tienda`: en la
colisión no se ofrece enlazar, tras entrar se pregunta sin enlazar nada, y «Ahora no» no enlaza. Y demuestra
que un enlace a medias se retoma como enlace: la app abre en la confirmación, con «Listo» o con su mensaje.

Los sabotajes se midieron sobre la solución el 18-nov-2026: no reconocer `NadieHaEntrado`, 63 de 66;
`retomar()` que siempre regresa `null`, 60; uno que siempre contesta «enlazado», 63; leer el enlace a medias
como entrada, 64.

Tres límites. «El mismo usuario» es el del proyecto de mentira, que sigue «una cuenta por correo» escrita a
mano: **no** demuestra que Firebase enlace tus cuentas. «Quedó a medias» lo dijo un doble: ninguna prueba cerró
la app a medio enlace, y la nota del teléfono solo se ejercitó sin nada pendiente. Y que la puerta de entrar
respete esa nota no lo ve ninguna prueba (sin esa línea, las 66 dan verde): lo respalda que el archivo sea
idéntico en `inicio/` y en `solucion/`.

---

## Paso 4 · Sin IA: ¿y si la app enlazara sola?

Este es el ejercicio 6.2 y se hace **sin Claude Code**. La confirmación es un criterio de esta serie: enlazar le
da a otra cuenta la entrada a este usuario, y eso lo decide su dueño, ya adentro. Aquí lo compruebas quitándola.

**1. Predice**, en `respuestas/06.md`: si la app enlazara en cuanto la persona entra, sin preguntar, ¿qué vería
Ana? ¿Y qué podría pasar si la cuenta de Microsoft que chocó no fuera la suya?

**2. Rómpelo y mide.** En `Tienda.kt`, dentro de `recibe`, cambia esta línea:

```kotlin
pantalla = if (huboColision && enlace != null) Pantalla.CONFIRMAR_ENLACE else Pantalla.INICIO
```

por estas dos:

```kotlin
if (huboColision && enlace != null) alcance.launch { enlace.enlazar() }
pantalla = Pantalla.INICIO
```

Corre el `verifica`. Esto dio el 18-nov-2026 (recortado):

```text
❌ Estos archivos ya no son iguales en inicio/ y en solucion/, y no se tocan: …/Tienda.kt
❌ La confirmación: 2 de 4 pruebas en verde. Nada se enlaza sin el toque de la persona, y solo se le ofrece …
❌ Al tocar «Enlazar Microsoft» no se enlaza, o después Microsoft no llega al mismo usuario (paso 3)
Resultado: 63 de 66 pruebas en verde (3 en rojo, 0 con error, 0 saltadas), leído de …
```

Copia a `respuestas/06.md` la línea que dice «La confirmación».

**3. Arréglalo.** Regresa la línea original. Corre el `verifica`: 66 de 66, y `Tienda.kt` otra vez igual al de
la lección.

**4. Explica**, sin volver a leer: por qué la app pide confirmación antes de enlazar, y qué pasaría si enlazara
sola. Compara con «Compara tus respuestas».

**Lo demuestra:** tu explicación (objetivo 6.2). El `verifica` solo busca la línea pegada: no sabe si lo
rompiste ni califica lo que escribiste. Ofrecer el enlace antes de entrar, en la colisión misma, se midió
aparte: 62 de 66.

---

## Paso 5 · En el teléfono: la prueba manual

Pide el nivel real de la 05: tu registro en Entra funcionando y «una cuenta por correo» activa (paso 1).

Con una cuenta de Microsoft cuyo correo ya entró con Google: toca «Entrar con Microsoft» y lee el mensaje;
entra con Google; acepta «Enlazar Microsoft». Después, los dos botones deberían llegar al mismo usuario, y tu
proyecto debería tener un solo usuario con ese correo. Para comprobar el botón de Microsoft hay que salir
primero, y esta app todavía no cierra sesión (lección 07).

[pendiente: ensayo con cuenta — hacer esta prueba con el código de esta lección: cómo salir entre un intento y
otro, dónde muestra la consola los dos proveedores de un usuario, y cerrar el proceso a medio enlace con
`adb shell am kill` para ver la nota trabajar].

En el ensayo del 13-nov-2026, con un código de prueba, se vio el camino corto: ya adentro con Google se enlazó
Microsoft, el mismo usuario quedó con los dos proveedores, y al salir y entrar con Microsoft se llegó a él.

**Lo demuestra:** es lo único que demuestra que Firebase enlazó tus cuentas.

---

## Tus ejercicios

El paso 4 se hace **sin IA**.

| # | Haces | Está hecho cuando |
|---|---|---|
| 6.1 | Resuelves la colisión: la persona entra con el proveedor que ya tenía y enlaza el otro, con confirmación | Tres evidencias. La línea «una cuenta por correo: activa» en `respuestas/06.md`, después de revisarla en tu consola. Simulado: el `verifica` da 66 de 66, con un proyecto de mentira de dos personas; valida el orden, la confirmación, el enlace a medias y que los dos accesos llegan al mismo usuario **de mentira**. Real: la prueba manual del paso 5, que es lo único que valida que Firebase enlazó tus cuentas |
| 6.2 | Explicas por qué la app pide confirmación antes de enlazar y qué pasaría si enlazara sola | Tu predicción, el rojo pegado y tu explicación están en `respuestas/06.md`, y coinciden con «Compara tus respuestas». La explicación la comparas tú |

**Tiempo:** calcula una hora y media sin la prueba manual, y media hora más con ella. Es un estimado: lo
ajustamos con lo que tarde la primera cohorte.

---

## ¿No te corre?

| Ves | Causa probable | Qué haces |
|---|---|---|
| `File google-services.json is missing.` | No copiaste tu archivo a `inicio/app/` | El `cp` de «Antes de empezar» |
| ❌ «Estos archivos ya no son los de la solución de la lección 05» | Cambió algo heredado | `git diff -- <archivo>` y deshazlo a mano |
| ❌ «Estos archivos ya no son iguales en inicio/ y en solucion/» con `Tienda.kt` | Vienes del paso 4 | Regresa la línea original |
| ❌ «Enlazar sin haber entrado primero» | Tu `resultadoDe` no reconoce `NadieHaEntrado` | Paso 3 |
| ❌ «Retomar el enlace» | `retomar()` no usa lo que regresa `enlazador.pendiente()`, o contesta siempre lo mismo | Paso 3 |
| Entraste con Microsoft con un correo que ya había entrado con Google, y no salió el mensaje de la colisión | Tu proyecto permite varias cuentas por correo: Firebase creó otro usuario | Paso 1. No enlaces dos usuarios creados por separado como si fuera el caso de la lección |
| «Primero entra con el botón que ya usabas…» | Se pidió enlazar sin nadie adentro | Entra primero; el enlace es sobre un usuario que ya entró |

En Linux, esta lección está [pendiente: medir en Linux Mint 22.3].

---

## Para cerrar

**Contesta sin volver a leer:**

1. ¿Qué opción de tu proyecto hace que exista la colisión, y qué pasa sin ella?
2. ¿Por qué se entra primero y se enlaza después?
3. Tu `verifica` da 66 de 66. Di una cosa que eso demuestra y una que no.
4. ¿Por qué un enlace a medias no se puede leer como una entrada a medias?

**Lo que sigue:** en la L07 la sesión se queda: la app te recuerda al volver, y aprendes a cerrarla.

---

## Compara tus respuestas

**Paso 3.** A ninguno: en la colisión nadie ha entrado, así que no hay usuario al cual sumarle nada. Por eso el
orden es entrar primero y enlazar después, y por eso la puerta lanza `NadieHaEntrado`.

**Paso 4.** Ana no vería nada: entraría al inicio y, por detrás, se le abriría el navegador para enlazar sin
haberlo pedido. Y si la cuenta de Microsoft que chocó fuera de otra persona con acceso a ese correo, esa cuenta
quedaría con entrada al usuario de Ana sin que ella lo decidiera. La confirmación pone la decisión en quien ya
demostró ser la dueña: está adentro, con el proveedor con el que creó la cuenta.

**Para cerrar:**

1. «Una cuenta por correo». Sin ella, Firebase puede crear un segundo usuario con el mismo correo, y no hay
   colisión que resolver.
2. Porque enlazar es sumarle un proveedor a un usuario que ya entró: antes de entrar no hay a quién.
3. Demuestra qué pide tu enlace y qué contesta, que nada se enlaza sin el toque de la persona y que, en un
   proyecto de mentira, los dos accesos llegan al mismo usuario. No demuestra que Firebase enlace tus cuentas.
4. Porque Firebase los entrega por el mismo lugar y no dice cuál fue. Leído como entrada, un enlace cancelado
   se vería como «no pudiste entrar».

---

## Fuentes de la lección

Abiertas el 18-nov-2026.

- Firebase. *Link Multiple Auth Providers to an Account on Android* (primero se entra y después se enlaza; el mismo identificador con cualquier proveedor. Su ejemplo usa `linkWithCredential`; para Microsoft, la guía de ese proveedor indica `startActivityForLinkWithProvider`). https://firebase.google.com/docs/auth/android/account-linking
- Firebase. *Authenticate Using Microsoft on Android* (enlazar Microsoft a un usuario que ya entró). https://firebase.google.com/docs/auth/android/microsoft-oauth
- Firebase. *Authenticate Using Microsoft with JavaScript* (el caso Microsoft y Google con «una cuenta por correo»: entrar con el proveedor que ya existe y enlazar el otro). https://firebase.google.com/docs/auth/web/microsoft-oauth
- Firebase. Referencia de `FirebaseUser` (`startActivityForLinkWithProvider` y su resultado pendiente). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseUser
- Firebase. Referencia de `FirebaseAuth` (`getPendingAuthResult`). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth
- Firebase. Referencia de `FirebaseAuthUserCollisionException` (`ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL` solo con «una cuenta por correo», que recomienda). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuthUserCollisionException
- Ensayo de la serie, 13-nov-2026, Mac con Apple silicon, con un código de prueba: la colisión con una cuenta real, y el enlace después de entrar con Google.
- Medición propia: Mac con Apple silicon, 18-nov-2026. El `verifica` sobre `inicio/` y `solucion/`, cada paso y cada sabotaje, con archivos inventados y sin tocar la consola de Firebase, la de Entra ni ninguna cuenta.
