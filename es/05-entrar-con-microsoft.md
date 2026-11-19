# Lección 05 — Entrar con Microsoft

**Antes de leer, cinco palabras.** Una línea cada una; las demás llegan en el paso donde se usan.

- **Microsoft Entra:** el servicio de Microsoft donde se dan de alta las apps que dejan entrar con una cuenta de
  Microsoft. Su consola está en `entra.microsoft.com`.
- **Directorio:** el espacio de Entra que pertenece a una persona o a una organización. Una app se registra
  dentro de un directorio. La documentación en inglés lo llama *tenant*.
- **Dirección de retorno:** la dirección web a la que Microsoft manda a la persona cuando termina de entrar. La
  documentación la llama *redirect URI*.
- **Clave de cliente:** una contraseña de la app, no de una persona. Con ella, quien la tiene se identifica ante
  Microsoft como esa app. La documentación la llama *client secret*.
- **Resultado pendiente:** la respuesta de una entrada que terminó cuando tu app ya no estaba ahí para
  recibirla.

**Qué te llevas:** el botón «Entrar con Microsoft» junto al de Google, escrito por ti, que recoge la entrada
aunque Android haya cerrado tu app a la mitad; una persona con el mismo correo en los dos proveedores que
termina con un solo usuario, porque ella lo confirmó; tu app registrada en Entra sin que su clave toque un solo
archivo; y una tabla tuya que dice qué es igual y qué cambia entre los dos proveedores.

**Idea nueva de esta lección:** un segundo proveedor que se registra fuera de Firebase, con una dirección de
retorno exacta y una clave que nunca toca la app. Lo demás —las librerías, el navegador, la tercera «puerta» y
el segundo botón en la pantalla— viene dado y explicado, y no se evalúa aquí.

---

## La escena

La página de Microsoft está abierta en el teléfono. La persona todavía no elige su cuenta. Detrás de esa página,
tu app ya no existe: Android la cerró.

La página sigue ahí, esperando.

Eso se provocó a propósito en el ensayo de esta serie, el 13 de noviembre de 2026, en un emulador. Con la
página de Microsoft al frente se cerró el proceso de la app desde la terminal, como lo haría Android si
necesitara memoria. Luego se eligió la cuenta. Android volvió a arrancar la app desde cero, sin nada de lo que
tenía en memoria.

La persona ya había entrado. La pregunta es si la app se entera, o si le pide que entre otra vez.

En el ensayo se enteró, porque al arrancar preguntaba una cosa antes que nada: ¿quedó una entrada a medias? Esa
pregunta es una línea, y esta lección la escribe. También escribe lo que va antes: registrar tu app con
Microsoft, que no se hace en Firebase, y cuidar una clave que solo se muestra una vez.

---

## Antes de empezar

**Qué necesitas:** la [04](04-entrar-con-google.md) terminada (su `verifica` en 20 de 20) y el ambiente de la
[00](00-ambiente-listo.md). Para el paso 8 basta un emulador abierto o tu teléfono conectado. Aplica lo de
«Cuenta y equipo de confianza» de la 00.

**Para los pasos 9 y 10, además:** una cuenta de Microsoft para entrar y un directorio de Entra donde puedas
registrar una app. Son dos cosas distintas, y la segunda es la que cuesta:

- La guía de Microsoft pide una cuenta de Azure con suscripción activa y el papel de *Application Developer*.
- En el ensayo del 13-nov-2026, una cuenta personal que **ya tenía directorio** registró la app por
  `entra.microsoft.com`, sin suscripción y sin tarjeta.
- Ese día, una cuenta **recién creada** no pudo: «Nuevo registro» abrió un panel sin formulario. Abrió después
  de activar la prueba gratuita de Azure, que pidió teléfono verificado, dirección de facturación y tarjeta.
- Si eres estudiante, revisa si calificas para Azure for Students. Su página dice que no pide tarjeta, que
  verifica con el correo de tu escuela y que no aplica a cursos masivos en línea ni a capacitaciones de
  organizaciones con fines de lucro. Esta lección no afirma que califiques.

**Si no tienes directorio,** haces completos los pasos 1 a 8, que no necesitan ninguna cuenta. Los pasos 9 y 10
quedan para cuando lo tengas.

**El proyecto de inicio.** Desde la raíz del repo de la serie:

```bash
git pull
cp lecciones/03-la-app-conoce-a-firebase/inicio/app/google-services.json lecciones/05-entrar-con-microsoft/inicio/app/
cd lecciones/05-entrar-con-microsoft/inicio
```

El segundo comando lleva tu archivo de la 03 al proyecto de esta lección. Ahí también queda ignorado por git.
Junto está `solucion/`: puedes leerla sin configurar nada, y **`./verifica solucion` sale en rojo en un clon
limpio**, porque sin tu archivo el proyecto no compila (medido el 18-nov-2026: 0 pruebas corridas). Tus
respuestas van en `respuestas/05.md`, en la raíz del repo.

**Lo que el proyecto ya trae (andamiaje).** No se evalúa aquí; el paso 3 lo recorre:

| Qué trae | Dónde | Por qué |
|---|---|---|
| Todo lo que dejó la 04: `EntradaConGoogle`, sus dos puertas, sus pruebas y sus dobles | Los mismos archivos, sin un solo cambio | El `verifica` los compara, byte por byte, contra la solución de la 04 |
| Un quinto resultado, `CuentaConOtroProveedor`; las interfaces `EntradaQueSeRetoma` y `Enlace`; y `ResultadoDeEnlace` | `Entrada.kt` | Con Microsoft hay una forma nueva de no entrar, una entrada que puede quedarse a medias y un usuario al que se le suma un proveedor |
| La tercera puerta, con su implementación de verdad | `PuertaDelNavegador.kt` | Por ahí sale tu código al navegador y a Firebase |
| El segundo botón, el mensaje de la colisión y la pantalla de confirmación | `PantallaEntrada.kt`, `Tienda.kt`, `PantallaEnlace.kt` | Los dos primeros **sí** cambiaron respecto a la 04 y el tercero es nuevo. Vienen dados: tú no tocas ninguna pantalla |
| `MainActivity` con `crearEntradaConMicrosoft` y `crearEnlaceConMicrosoft`, que por ahora regresan piezas que no hacen nada | `MainActivity.kt` | Es donde la app arma su segunda entrada y su enlace |
| Las pruebas de esta lección, sus dobles, un proyecto de mentira y una «app de pruebas» | `app/src/test`, `app/src/androidTest` | Son las que corre el `verifica`. No las cambies |
| Dos esqueletos | `EntradaConMicrosoft.kt`, `EnlaceConMicrosoft.kt` | Son los archivos que escribes tú |

El catálogo de versiones es el de la 04, sin librerías nuevas: Microsoft viene en Firebase Authentication.

**Lo que no hace esta lección.** No guarda la sesión ni la cierra (lección 06), no distingue la primera vez
(lección 07) y no deshace un enlace.

---

## Paso 1 · Sin IA: el mismo viaje, por otro camino

Este paso es tuyo, sin Claude Code. Es el ejercicio 5.3.

En la 04, tu app le pidió un token al teléfono y se lo llevó a Firebase. Con Microsoft, tu app no carga nada:
abre el navegador y espera.

| # | De quién a quién | Qué viaja |
|---|---|---|
| 1 | Tu app → Firebase (su librería) | Una solicitud: «quiero entrar con el proveedor `microsoft.com`» |
| 2 | Firebase → el navegador | Abre la página de Microsoft, con el ID de cliente de tu registro y tu dirección de retorno |
| 3 | La persona → Microsoft | Su cuenta y lo que Microsoft le pida. Ocurre en el navegador, fuera de tu app |
| 4 | Microsoft → Firebase (sus servidores) | Un código de un solo uso, mandado a tu dirección de retorno |
| 5 | Firebase → Microsoft | Ese código, junto con el ID de cliente **y la clave** que guardaste en la consola de Firebase |
| 6 | Firebase → tu app | Un usuario, con su identificador en tu proyecto y, si el proveedor lo entregó, su nombre |

La fila 5 es la que no existía con Google. La guía de Firebase lo dice así: Firebase maneja todo el flujo y el
intercambio del código usando el ID de cliente y la clave configurados en su consola. Y la documentación de
Microsoft explica por qué esa clave no va en la app: pide no usarla en una app nativa, porque en un dispositivo
no se puede guardar de forma confiable.

Por eso la fila 2 exige exactitud. La documentación de Microsoft dice que su servidor solo manda a la persona a
una dirección que esté agregada al registro de la app, y que la dirección de la solicitud debe coincidir
exactamente con una de las registradas. Si no coincide, responde con un error.

**Predice** antes de seguir, en `respuestas/05.md`: de las seis filas, ¿en cuáles participa el código que vas a
escribir tú?

**Haz tu tabla.** En `respuestas/05.md`, compara los dos proveedores con estas cinco preguntas. Llénala sin
mirar «Compara tus respuestas» y luego compárala:

1. ¿Quién registra la app con el proveedor, y en qué consola?
2. ¿Qué pieza le muestra a la persona dónde entrar?
3. ¿Qué recibe tu app del proveedor?
4. ¿Hay una clave? ¿Dónde vive?
5. ¿Qué tiene que coincidir exactamente para que funcione?

**Lo demuestra:** tu tabla, comparada con la de la lección (objetivo 5.3). Ninguna prueba la lee.

---

## Paso 2 · La regla de datos, antes de tocar nada

En la 04 la regla fue: nadie imprime, pega ni guarda el token. Aquí se extiende a tres cosas más.

- **La clave de cliente solo existe en la consola de Firebase.** No va en el código, en un archivo, en
  `respuestas/05.md`, en la terminal, en una captura ni en un mensaje. De ella solo se anota su **fecha de
  caducidad**.
- **El token de acceso de Microsoft no se lee.** El resultado de Firebase trae uno, con el que se podría llamar
  a servicios de Microsoft a nombre de la persona. De ese resultado solo sale el nombre.
- **El ID de cliente de Entra no va en el código.** No es un secreto, pero la app no lo necesita: lo usa
  Firebase. En tu tabla del paso 9 sí lo anotas.

Si la clave llega a tocar un archivo, la terminal o una captura, ya no es secreta: se elimina en Entra y se crea
otra. Borrar el archivo no alcanza. Súmala, con los tokens de acceso, a la lista de lo que nunca se sube.

---

## Paso 3 · Lo que el proyecto ya trae

Seis archivos de la app cambiaron o son nuevos respecto a la 04, además de los dos esqueletos. Ábrelos; todavía
no los edites. Vas a ver tres piezas de Kotlin nuevas:

- **`String?`:** un tipo que termina en `?` acepta `null`. `String?` es «un texto, o nada».
- **`a ?: b`:** si `a` no es `null`, regresa `a`; si lo es, regresa `b`.
- **`when { … }`:** una lista de condiciones; se queda con la primera que se cumple.

**`Entrada.kt`: dos cosas nuevas.** `Resultado` tiene un quinto caso, `CuentaConOtroProveedor`. Y hay una
interfaz más:

```kotlin
interface EntradaQueSeRetoma : Entrada {
    suspend fun retomar(): Resultado?
}
```

Es una `Entrada` que, además de `entrar()`, sabe contestar si quedó algo a medias. Regresa un `Resultado`, o
`null` si no quedó nada.

**`PuertaDelNavegador.kt`: la tercera puerta.**

```kotlin
interface NavegadorDeProveedor {
    suspend fun pendiente(): String?
    suspend fun abrir(proveedor: OAuthProvider): String
    suspend fun enlazar(proveedor: OAuthProvider)
}
```

`abrir` abre el navegador y regresa el nombre de quien entró; su versión de verdad llama a
`startActivityForSignInWithProvider`. `pendiente` pregunta por una entrada a medias; su versión de verdad lee
`pendingAuthResult`. Son las dos llamadas de la guía de Firebase. `enlazar` es del paso 7. La referencia de Firebase dice que ese
resultado pendiente existe cuando se inició una entrada por la web y el sistema recuperó la pantalla que la
esperaba, y que se entrega **una sola vez**.

Esta puerta solo entrega un nombre. Es la regla de datos del paso 2 hecha código: el token de acceso no sale de
ahí.

**`PantallaEntrada.kt` y `Tienda.kt`: el segundo botón.** `Tienda` recibe ahora una segunda entrada, opcional.
Si no se la pasan, no hay segundo botón y todo es como en la 04; por eso las pruebas de la 04 siguen pasando sin
tocarlas. Y tiene un bloque nuevo:

```kotlin
LaunchedEffect(entradaConMicrosoft) {
    val pendiente = entradaConMicrosoft?.retomar()
    if (pendiente != null) recibe(pendiente)
}
```

`LaunchedEffect` corre su bloque cuando esa parte de la pantalla aparece, no en cada redibujo, según la
documentación de Compose. Aquí significa: al abrirse la app, pregunta una vez si quedó algo a medias. Ahí mismo
está el mensaje nuevo de `mensajeDe`.

**`MainActivity.kt`.** `onCreate` arma las dos entradas y el enlace, y se los pasa a `Tienda`. Por ahora
`crearEntradaConMicrosoft` regresa `MicrosoftSinConectar()` y `crearEnlaceConMicrosoft` regresa
`EnlaceSinConectar()`: ninguna hace nada.

**Las pruebas.** En tu computadora, Firebase no se prepara solo como en un teléfono: sin ayuda, las pruebas
dieron «Default FirebaseApp is not initialized» (medido el 18-nov-2026). Por eso `src/test` trae `AppDePruebas`,
que lo prepara a mano. Preparar no es conectarse.

**Mide el punto de partida.** Desde la raíz del repo:

```bash
./lecciones/05-entrar-con-microsoft/verifica
```

El 18-nov-2026, con un archivo inventado completo, la última línea fue
`Resultado: 23 de 58 pruebas en verde (35 en rojo, 0 con error, 0 saltadas)`. Son 58 porque van las 20 de la
lección 04 y 38 nuevas. Las 20 de la 04 ya están en verde, y tres de las nuevas también: son del andamiaje. Las
otras 35 esperan tu código.

---

## Paso 4 · Con Claude Code: entrar y retomar

Aquí entra la herramienta, y entra siempre igual: **predices → le pides → lees lo que generó → corres el
`verifica` → lo explicas con tus palabras.**

1. **Predice.** Escribe en `respuestas/05.md`: si al abrirse la app no quedó ninguna entrada a medias, ¿qué debe
   regresar `retomar()`, y qué debe pasar en la pantalla?
2. **Pídele** a Claude Code, parado en `inicio/`, la segunda entrada tomando la primera como referencia. Este
   es el ejercicio 5.5, y el encargo es corto a propósito: «Toma `EntradaConGoogle.kt` como referencia y escribe
   `EntradaConMicrosoft.kt` solo para el caso en que todo sale bien, con `retomar()` y `entrar()`, usando la
   puerta `navegador`. No atrapes excepciones todavía. En `MainActivity.kt`, cambia solo el cuerpo de
   `crearEntradaConMicrosoft` para que regrese `EntradaConMicrosoft` con la puerta que recibe, y borra
   `MicrosoftSinConectar` de `Entrada.kt`. No cambies ningún otro archivo.»
3. **Lee** el cambio completo antes de aceptarlo y **lista lo que no copió bien.** La primera entrada es una
   buena referencia para el estilo y una mala para el fondo: Google entrega un token y Microsoft no. Contesta
   sí o no a las ocho preguntas de «Compara tus respuestas», ejercicio 5.5, y anota en `respuestas/05.md`
   cada «no» con el archivo y la línea. Luego pide la corrección de cada uno, diciendo por qué. El centro debe
   quedar parecido a esto:

```kotlin
override suspend fun retomar(): Resultado? {
    val nombre = navegador.pendiente()
    return if (nombre == null) null else Resultado.Entro(nombre)
}

override suspend fun entrar(): Resultado {
    // Filas 1 y 2: la solicitud, con el proveedor.
    val proveedor = OAuthProvider.newBuilder("microsoft.com")
        .addCustomParameter("prompt", "select_account")
        .build()
    // Primero, lo pendiente. Solo si no hay (null) se abre el navegador.
    val nombre = navegador.pendiente() ?: navegador.abrir(proveedor)
    return Resultado.Entro(nombre)
}
```

4. **Corre el `verifica`.** Con ese código dio `Resultado: 33 de 58 pruebas en verde (25 en rojo, …)` el
   18-nov-2026. Las rojas son las formas de no entrar, del paso 5, y el enlace, del paso 7.
5. **Explica**, en tres líneas de `respuestas/05.md`: por qué `entrar()` pregunta por lo pendiente si
   `retomar()` ya lo hizo al abrir la app.

**¿Qué hace `select_account`?** Según la documentación de Microsoft, interrumpe el inicio de sesión automático
y muestra la lista de cuentas para elegir. Es el valor que usó el ensayo del 13-nov-2026. Ninguna prueba lee
ese parámetro: el `verifica` solo busca que la palabra esté en tu archivo.

**Lo demuestra, y hasta dónde.** En el nivel simulado, el `verifica` demuestra que un toque abre el navegador
una sola vez y con el proveedor `microsoft.com`; que con algo a medias no se vuelve a abrir; y que `retomar()`
regresa quién entró o `null`. Otra prueba abre `MainActivity`, le pasa la puerta de mentira a
`crearEntradaConMicrosoft` y lee qué proveedor se pidió.

La puerta era de mentira: no se abrió ningún navegador. «Quedó algo a medias» lo dijo el doble; ninguna prueba
cerró la app. Y queda el mismo hueco de la 04: que `onCreate` le pase a `Tienda` lo que regresa
`crearEntradaConMicrosoft()` solo se busca como texto. Con `onCreate` armando otra entrada, las 58 pruebas dan
verde y lo único rojo es esa marca (medido el 18-nov-2026). Si no le pasa ninguna, sí hay una prueba que se pone
roja, porque el botón desaparece (57 de 58).

---

## Paso 5 · Sin IA: las tres formas de no entrar

Este paso se hace **sin Claude Code**. Es una función de tres ramas, y la escribes tú.

Cuando la puerta del navegador no puede entregar un nombre, lanza una excepción de Firebase. La referencia de
Firebase lista las de esta llamada. Tres grupos importan aquí:

| Excepción | Cuándo, según la referencia | Tu entrada contesta |
|---|---|---|
| `FirebaseAuthUserCollisionException` | El correo de quien está entrando ya está en uso. Con el código `ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL`, ese correo ya tiene usuario con otra forma de entrar; solo ocurre con la opción «una cuenta por correo» activada | `Resultado.CuentaConOtroProveedor` |
| `FirebaseAuthWebException` con el código `ERROR_WEB_CONTEXT_CANCELED` | La persona canceló la operación pendiente | `Resultado.Cancelada` |
| Cualquier otra, incluida una `FirebaseAuthWebException` con otro código | La referencia nombra otra operación web en curso, un navegador sin soporte, una falla dentro de la página y el proveedor mal configurado. Aquí entra también todo lo demás que falle | `Resultado.Fallo` |

En el ensayo del 13-nov-2026 se entró con una cuenta de Microsoft cuyo
correo ya había entrado con Google, y Firebase contestó con esa excepción y este texto: «An account already
exists with the same email address but different sign-in credentials».

**1. Predice.** Las dos primeras filas nombran clases distintas. Si en la segunda quitas la condición del
código y dejas solo «es una `FirebaseAuthWebException`», ¿qué mensaje verá una persona a la que le falló la
página, sin haber cancelado nada?

**2. Escríbelo.** Agrega esta función dentro de tu clase:

```kotlin
private fun resultadoDe(falla: Exception): Resultado = when {
    falla is FirebaseAuthUserCollisionException -> Resultado.CuentaConOtroProveedor
    falla is FirebaseAuthWebException && falla.errorCode == "ERROR_WEB_CONTEXT_CANCELED" ->
        Resultado.Cancelada
    else -> Resultado.Fallo
}
```

Y envuelve el cuerpo de `retomar()` y el de `entrar()` con `try { … }` y estos dos `catch`, en este orden:

```kotlin
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    resultadoDe(e)
}
```

El primero es el de la 04: `CancellationException` no es una falla de la entrada, es Kotlin avisando que la
tarea se canceló, y se deja pasar. `retomar()` lleva los mismos `catch` porque, según la referencia, el
resultado pendiente trae también las excepciones con las que la entrada hubiera terminado.

**3. Corre el `verifica`.** Ahora la línea es `Resultado: 48 de 58 pruebas en verde`. Las diez rojas son del
enlace, el paso 7.

**4. Comprueba tu predicción.** Quita `&& falla.errorCode == "ERROR_WEB_CONTEXT_CANCELED"` y corre el
`verifica`: se ponen rojas dos pruebas más, las dos del caso «error» (medido el 18-nov-2026 sobre la
solución: 56 de 58). Regrésalo.

Los textos que ve la persona ya están en `mensajeDe`:

| Resultado | Mensaje |
|---|---|
| `Cancelada` | «Cerraste la ventana sin elegir cuenta. Toca el botón cuando quieras intentar otra vez.» |
| `Fallo` | «No se pudo entrar. Revisa tu conexión y vuelve a intentar.» |
| `CuentaConOtroProveedor` | «Ese correo ya entró a la tienda con otro botón. Entra con el que usaste la primera vez.» |

Los dos primeros son los de la 04. El tercero no dice «Google» a propósito: la excepción no trae con qué
proveedor se creó el usuario.

**Lo demuestra:** el `verifica` simulado, los tres casos. Cada caso lleva pruebas de dos tipos: unas le lanzan
la excepción a tu clase y leen qué contestó; otra dibuja la pantalla con tu clase y busca su mensaje. Las
excepciones las armó un doble, con códigos escritos a mano: ninguna vino de Firebase. Qué excepción da en un
teléfono cerrar la pestaña del navegador está [pendiente: ensayo con cuenta].

---

## Paso 6 · Sin IA: rompe uno a propósito

También **sin Claude Code**. Vas a provocar el final malo de la escena.

**1. Predice.** Vas a hacer que `retomar()` conteste siempre `null`, como si nunca quedara nada a medias.
¿Compila? El botón de Microsoft, ¿sigue funcionando? ¿Cuántas pruebas se ponen rojas?

**2. Rómpelo y mide.** En `EntradaConMicrosoft.kt`, cambia las dos líneas del cuerpo de `retomar()` por un
`null` a secas, dentro del `try`. Corre el `verifica`. Esto dio el 18-nov-2026 (recortado):

```text
❌ Tu entrada no retoma: quedó una entrada a medias y retomar() no la recogió (paso 4)
❌ Retomar: 1 de 4 pruebas en verde. Revisa retomar(), que entrar() pregunte primero por lo pendiente, y …
❌ Al abrir la app: 0 de 3 pruebas en verde. Tienda.kt viene dado y llama a retomar(): revisa lo que …
Resultado: 41 de 58 pruebas en verde (17 en rojo, 0 con error, 0 saltadas), leído de …
```

Son siete rojas más que antes de romperlo. Copia a `respuestas/05.md` la línea que dice «Tu entrada no
retoma».

**3. Lo que esas pruebas no te dicen.** Compila, y las pruebas del botón siguen en verde: tocar «Entrar con
Microsoft» lleva al inicio igual. El defecto solo existe si Android cierra la app a media entrada, y eso no pasa
en una prueba que dura un segundo. En un teléfono se midió aparte, el 13-nov-2026, con un código de ensayo:

- Con la página de Microsoft al frente se cerró el proceso de la app con `adb shell am kill` y luego se eligió
  la cuenta: Android arrancó la app otra vez y el resultado pendiente entregó la entrada.
- La opción de desarrollador «No conservar actividades» **no** sirvió para probarlo: destruye la pantalla, pero
  el proceso sigue vivo y termina la entrada por su cuenta. Para ver el caso hay que cerrar el proceso, no la
  pantalla.

**4. Arréglalo.** Regresa las dos líneas de `retomar()`. Corre el `verifica`: 48 de 58 otra vez, y ahora en
verde la marca de `respuestas/05.md`.

El ejercicio está terminado cuando puedes marcar las tres:

- [ ] Tu predicción del punto 1 está escrita, con lo que viste debajo.
- [ ] Pegaste la línea del rojo.
- [ ] Dijiste por qué las pruebas del botón siguieron en verde con un código que, en un teléfono con poca
      memoria, haría entrar dos veces a la persona.

**Lo demuestra:** «Rompe uno a propósito». Es base del objetivo 5.2: saber qué recoge `retomar()`.

---

## Paso 7 · Con Claude Code: enlazar, con confirmación

Es el ejercicio 5.4. Hasta aquí, a quien tiene el mismo correo en los dos proveedores la app le dice «entra con
el otro botón». Ahora, además, le ofrece que los dos botones lleguen a su mismo usuario.

La guía de Firebase para enlazar cuentas pide un orden: **primero** la persona entra con cualquier proveedor, y
**después**, a ese usuario que ya entró, se le suma el otro. Así, dice, la persona tiene el mismo identificador
de usuario sin importar con cuál entre. Para Microsoft, la guía de ese proveedor nombra la llamada:
`startActivityForLinkWithProvider`, sobre el usuario que ya entró. Es lo que hace `enlazar` en la puerta del
navegador; si nadie ha entrado, lanza `NadieHaEntrado` sin abrir nada.

**La confirmación viene dada.** `Tienda.kt` recuerda si hubo una colisión. Cuando esa persona entra después
con el botón correcto, en lugar del inicio ve `PantallaEnlace`: «¿También con Microsoft?», con los botones
«Enlazar Microsoft» y «Ahora no». Solo el primero llama a tu código. El criterio es de esta serie: enlazar le da
a otra cuenta la llave de este usuario, y eso lo decide su dueño, ya adentro.

1. **Predice**, en `respuestas/05.md`: en el momento de la colisión, antes de que la persona entre con Google,
   ¿a qué usuario se le podría enlazar Microsoft?
2. **Pídele** a Claude Code: «En `EnlaceConMicrosoft.kt`, toma `EntradaConMicrosoft.kt` como referencia y escribe
   `enlazar()`: arma el mismo proveedor, llama a `navegador.enlazar` y regresa `ResultadoDeEnlace.Enlazado`. Si
   la puerta lanza `NadieHaEntrado`, regresa `SinSesion`; si es una `FirebaseAuthWebException` con
   `ERROR_WEB_CONTEXT_CANCELED`, `Cancelado`; cualquier otra, `Fallo`; `CancellationException` se deja pasar. En
   `MainActivity.kt`, cambia solo el cuerpo de `crearEnlaceConMicrosoft`, y borra `EnlaceSinConectar` de
   `Entrada.kt`. No llames a `navegador.abrir` y no cambies ningún otro archivo.»
3. **Lee.** El centro debe parecerse a esto:

```kotlin
override suspend fun enlazar(): ResultadoDeEnlace =
    try {
        val proveedor = OAuthProvider.newBuilder("microsoft.com")
            .addCustomParameter("prompt", "select_account")
            .build()
        navegador.enlazar(proveedor)
        ResultadoDeEnlace.Enlazado
    } catch (e: CancellationException) {
        throw e
    } catch (e: NadieHaEntrado) {
        ResultadoDeEnlace.SinSesion
    } catch (e: Exception) {
        if (e is FirebaseAuthWebException && e.errorCode == "ERROR_WEB_CONTEXT_CANCELED") {
            ResultadoDeEnlace.Cancelado
        } else {
            ResultadoDeEnlace.Fallo
        }
    }
```

   Revisa dos cosas. Que llame a `enlazar` y no a `abrir`: entrar otra vez no enlaza nada, solo vuelve a chocar
   (con `abrir`, 49 de 58, medido el 18-nov-2026). Y que el `catch` de `NadieHaEntrado` vaya antes del general.

4. **Corre el `verifica`:** `Resultado: 58 de 58 pruebas en verde`.
5. **Explica**, en tres líneas: por qué el enlace se hace sobre el usuario que ya entró, y no en la colisión.

**Lo demuestra, y hasta dónde.** El `verifica` simulado usa un proyecto de mentira (`ProyectoFalso.kt`) con dos
personas: Ana, con el mismo correo en Google y en Microsoft, y Luis, con otro correo. Demuestra que antes de
enlazar Ana choca y no se crea un segundo usuario; que después de enlazar sus dos accesos llegan al mismo
usuario; y que Luis sigue siendo otro. Y demuestra la confirmación, con tus tres clases dibujadas en `Tienda`:
en la colisión no se ofrece enlazar, tras entrar se pregunta sin enlazar nada, y «Ahora no» no enlaza. Una
prueba más usa la puerta de verdad sin nadie adentro, y tu enlace contesta «sin sesión».

Los sabotajes se midieron sobre la solución el 18-nov-2026: enlazar sin preguntar, 55 de 58; ofrecerlo en la
colisión, antes de entrar, 54; no reconocer `NadieHaEntrado`, 55.

«El mismo usuario» es el del proyecto de mentira, que sigue la regla «una cuenta por correo» escrita a mano.
**No** demuestra que Firebase enlace tus cuentas: eso es la prueba manual del paso 10. Queda un hueco que ninguna
prueba ve: que `onCreate` le pase el enlace a `Tienda` solo se busca como texto (sin él, 58 de 58). Y si Android
cierra la app a medio enlace, la referencia de Firebase dice que el desenlace se recoge con el mismo resultado
pendiente; no se midió.

---

## Paso 8 · Dentro de Android: el nivel instrumentado

Con el emulador abierto, desde la raíz del repo:

```bash
./lecciones/05-entrar-con-microsoft/verifica inicio instrumentado
```

Repite el nivel simulado y después instala la app y su app de pruebas en el dispositivo. Ahí dibuja `Tienda` con
tu `EntradaConMicrosoft` y la misma puerta de mentira, toca los botones y busca los textos. Con la solución, en
un emulador nuevo y sin cuenta (Pixel 9, imagen `google_apis_playstore` de Android 37), esto dio el 18-nov-2026
(recortado):

```text
✅ Instrumentado: dentro de Android, con la puerta de mentira, tocar «Entrar con Microsoft» lleva al inicio …
✅ Instrumentado: dentro de Android, cancelación, error y colisión muestran cada uno su mensaje sin pasar …
✅ Instrumentado: dentro de Android, con una entrada a medias que contesta el doble, la app llega al inicio …
✅ Instrumentado: dentro de Android, las 4 pruebas del botón de Google de la lección 04 siguen en verde
Resultado: 58 de 58 pruebas en verde (0 en rojo, 0 con error, 0 saltadas), leído de …
Instrumentado: 9 de 9 pruebas en verde (0 en rojo, 0 con error, 0 saltadas), leído de …
```

Son nueve: cinco de esta lección y las cuatro de la 04. Sobre el proyecto de inicio sin tocar dio 4 de 9, las
cuatro de la 04. Sin ningún dispositivo conectado, el `verifica` sale en rojo y dice que no se corrió.

**Lo demuestra, y hasta dónde.** Demuestra que las pantallas, el toque y tu clase funcionan dentro de Android,
con la librería de Firebase Authentication del dispositivo armando el proveedor. **No** demuestra nada de tu
registro en Entra ni de tu proyecto: la puerta sigue siendo de mentira, no se abre el navegador y no hace falta
cuenta ni red hacia Microsoft. Y la prueba de «retomar» es el doble contestando que quedó algo a medias: Android
no cerró ninguna app. El enlace no tiene prueba instrumentada: se mide en el nivel simulado y en el real.

**Mira el botón de verdad, sin registro.** En ese mismo emulador, con un archivo inventado, se instaló la app y
se tocó «Entrar con Microsoft» (18-nov-2026). No se abrió ninguna página. La app mostró «No se pudo entrar.
Revisa tu conexión y vuelve a intentar.», y el registro del sistema dijo
`[GetAuthDomainTask] Error getting project config. Failed with API key not valid`. Es lo esperado: el archivo
era inventado. Con tu archivo de verdad y Microsoft todavía sin habilitar en Firebase, el resultado está
[pendiente: ensayo con cuenta].

---

## Paso 9 · En las consolas: registrar la app sin que la clave toque un archivo

Este paso y el siguiente piden el directorio de «Antes de empezar». Aquí no se escribe código. Abre las dos
consolas en dos pestañas: vas a llevar un dato de Firebase a Entra, y dos de Entra a Firebase.

Los menús y botones van en inglés, como en la documentación. En español, el ensayo del 13-nov-2026 vio «Registros de aplicaciones», «Nuevo registro» y «Registrar una
aplicación». [pendiente: ensayo con cuenta — recorrer el «Nuevo registro» de punta a punta y anotar cada rótulo
en español, con capturas].

**1. En Firebase, copia tu dirección de retorno.** En Authentication, pestaña *Sign-in method*, elige Microsoft
y habilítalo. Todavía no guardes. En el ensayo, la dirección se copió de ese mismo panel. Tiene esta forma:

```text
https://<tu-proyecto>.firebaseapp.com/__/auth/handler
```

La guía de Firebase sobre redirecciones dice que el final `/__/auth/handler` importa.

**2. En Entra, entra por `entra.microsoft.com`.** No por `portal.azure.com`: en el ensayo, la cuenta sin
suscripción dio ahí el error `AADSTS16000`, y por Entra abrió su propio directorio.

**3. Registra la app.** Según la guía de Microsoft: *Entra ID* › *App registrations* › *New registration*.

- **Name:** uno que reconozcas. La guía avisa que las personas que entren pueden verlo.
- **Supported account types:** *Any Entra ID Tenant + Personal Microsoft accounts*, para cuentas de
  organizaciones y personales. Es la que usó el ensayo.
- *Register*.

La página *Overview* muestra el **Application (client) ID**. Ese sí se anota.

**4. Agrega la dirección de retorno.** Según la guía: *Manage* › *Authentication* › *Add Redirect URI* ›
plataforma **Web** › pega la dirección del punto 1 › *Configure*. Pégala; no la escribas a mano. Microsoft pide
que empiece con `https` y distingue mayúsculas de minúsculas.

**5. Crea la clave.** Según la guía: *Certificates & secrets* › *Client secrets* › *New client secret*. Ponle
una descripción y elige una caducidad. La guía dice que no puede pasar de 24 meses y recomienda menos de 12.
*Add*.

La clave aparece en la columna **Value**, y la guía avisa que no se vuelve a mostrar después de salir de la
página. Con esa página abierta:

- Cópiala con el botón de copiar y pégala **directo** en el campo de la clave del panel de Microsoft, en la
  pestaña de Firebase. Pega también el *Application (client) ID*. *Save*.
- No la pegues en ningún otro lugar. Después copia cualquier otro texto, para sacarla del portapapeles
  (recomendación de esta serie).
- Anota la **fecha de caducidad**. Cuando llegue, la entrada con Microsoft deja de funcionar hasta que crees
  otra clave y la lleves a Firebase.
- No tomes ninguna captura mientras la columna *Value* muestre la clave.

La misma guía dice que una clave de cliente no debe usarse en producción. Esta es una app de curso.

**6. Deja la evidencia.** En `respuestas/05.md`:

| Dato | Lo anotas |
|---|---|
| *Application (client) ID* | Sí, completo |
| Tipo de cuentas | Sí, el nombre de la opción |
| Dirección de retorno | Sí, completa |
| Caducidad de la clave | Sí, la fecha |
| Valor de la clave | **Nunca** |

Y una captura de *Certificates & secrets* tomada **después** de salir de esa página y volver a entrar. Si en la
captura queda a la vista cualquier parte del valor, tápala antes de guardarla.

**7. Pídele al `verifica` que busque.** Córrelo: las dos marcas «Clave:» deben estar en verde. Para saber que
esa búsqueda sí ve, siembra una clave **inventada**. Este comando la arma por partes, para que no quede escrita
en ningún lado, y la agrega como comentario al final de un archivo:

```bash
printf '// %s%s%s\n' 'Inv8' 'Q~' "$(printf 'x%.0s' $(seq 34))" >> lecciones/05-entrar-con-microsoft/inicio/app/src/main/java/mx/habil/ejemplo/tienda/MainActivity.kt
```

Corre el `verifica`. El 18-nov-2026, sobre el proyecto de inicio sin tocar, contestó con el archivo y el número
de línea, sin imprimir el texto (tu número de línea puede ser otro):

```text
❌ Hay un texto con forma de clave de cliente de Entra en: inicio/app/src/main/java/…/MainActivity.kt:48. No la imprimo. …
```

Borra esa última línea en tu editor y corre el `verifica` otra vez: verde.

**Lo demuestra, y hasta dónde.** Las marcas «Clave:» demuestran que ningún archivo de texto de `inicio/` ni
`respuestas/05.md` trae un texto con la forma de esas claves ni nombra un secreto de cliente. La forma es la de
una herramienta pública de búsqueda de secretos; Microsoft no la publica como formato, y una clave con otra
forma se le pasaría. Solo lee archivos: **no** ve tu terminal, tus capturas, tu portapapeles ni el historial de
git. Esa parte la cuidas tú, y tu tabla y tu captura las comparas tú.

---

## Paso 10 · En el teléfono: el nivel real

Este paso sí habla con Microsoft, una vez, a mano. Es el único que comprueba el paso 9: que la dirección de
retorno es la exacta, que el ID es el de tu registro y que la clave que guardaste es la vigente.

Con el emulador abierto, parado en `inicio/`:

```bash
./gradlew installDebug
```

Abre «La tienda», toca «Entrar» y luego «Entrar con Microsoft».

**Lo que se vio en el ensayo del 13-nov-2026** (Mac, emulador, cuentas personales). Ese ensayo usó un código de
prueba con las mismas llamadas, no el de `solucion/`:

| Caso | Qué se vio |
|---|---|
| Primera vez | Se abrió el navegador con la página de Microsoft; se escribió la cuenta; la app saludó con el nombre |
| El permiso | Una pantalla «Let this app access your info?», con la marca **unverified** junto al nombre de la app, «Read your profile» y los botones *Deny* y *Accept*. Es lo esperado en una app de ejemplo sin editor verificado. Microsoft guardó la aceptación: no volvió a aparecer |
| Volver a entrar | Microsoft mostró la cuenta ya iniciada, para elegirla sin contraseña |
| Un correo que ya entró con Google | La excepción de colisión del paso 5 |
| Enlazar después de entrar con Google | Ya adentro con Google se enlazó Microsoft: el mismo usuario quedó con los dos proveedores. Al salir y entrar con Microsoft, se llegó a ese mismo usuario |
| La app cerrada a media entrada | Lo del paso 6: el resultado pendiente entregó la entrada |

[pendiente: ensayo con cuenta — repetir la entrada real con el código de esta lección; la clase
`NavegadorConFirebase` solo se ha ejecutado hasta el error del archivo inventado del paso 8].

**Si tu cuenta usa llave de acceso (passkey).** En el ensayo, la pantalla «Sign in with your passkey» se quedó
en «Verifying…» en el emulador. Se tocó *Cancel* y se entró con la contraseña.

**Si el navegador ofrece guardar la contraseña:** en un equipo que no es tuyo, «No».

**Prueba el final de la escena.** Con la página de Microsoft al frente y antes de elegir cuenta, en otra
terminal:

```bash
adb shell am kill mx.habil.ejemplo.tienda
adb shell pidof mx.habil.ejemplo.tienda
```

El primero cierra los procesos de la app que es seguro cerrar, según la documentación de `adb`. El segundo no
debe imprimir nada: el proceso ya no existe. Ahora elige tu cuenta. La app debe arrancar y saludarte sin que
toques el botón otra vez.

**La prueba manual del enlace (ejercicio 5.4).** Con una cuenta de Microsoft cuyo correo ya entró con Google:
toca «Entrar con Microsoft» y lee el mensaje; entra con Google; acepta «Enlazar Microsoft». Después, los dos
botones deberían llegar al mismo usuario, y tu proyecto debería tener un solo usuario con ese correo. Para
comprobar el botón de Microsoft hay que salir primero, y esta app todavía no cierra sesión (lección 06).
[pendiente: ensayo con cuenta — hacer esta prueba con el código de esta lección: cómo salir entre un intento y
otro, y dónde muestra la consola de Firebase los dos proveedores de un usuario].

**Al terminar, en un equipo que no es tuyo:** cierra la sesión de Microsoft en el navegador del emulador y borra
sus datos (lección 00).

**Lo demuestra:** la entrada real demuestra que el registro, la dirección de retorno, el ID y la clave están
bien. No demuestra los casos de no entrar: esos los cubre el nivel simulado.

---

## Tus ejercicios

Los pasos 1, 5 y 6 se hacen **sin IA**.

| # | Haces | Está hecho cuando |
|---|---|---|
| 5.1 | Registras la app en Entra con la dirección de retorno exacta y llevas su ID y su clave a Firebase | Tres evidencias. Tu tabla del paso 9 y tu captura sin clave a la vista: las comparas tú. Las dos marcas «Clave:» del `verifica` en verde, que solo dicen que ningún **archivo** de `inicio/` ni `respuestas/05.md` trae esa forma o ese nombre. Y la entrada real del paso 10, que es lo único que valida el registro |
| 5.2 | Implementas `EntradaConMicrosoft` sin cambiar las pantallas, y retomas el resultado pendiente | Tres evidencias. Simulado: sus marcas del `verifica` en verde (al terminar el paso 7, 58 de 58); valida qué pide tu clase y qué contesta, con una puerta de mentira, y que las 20 pruebas de la 04 siguen en verde con sus archivos idénticos. Instrumentado: 9 de 9, lo mismo dentro de Android. Real: entras en el paso 10 y cierras el proceso a media entrada; es lo único que valida que Android de verdad te regresa a la app |
| 5.3 | Comparas Google y Microsoft con las cinco preguntas del paso 1 | Tu tabla coincide con la de «Compara tus respuestas». La comparas tú |
| 5.4 | Resuelves la colisión: la persona entra con el proveedor que ya tenía y enlaza el otro, con confirmación | Dos evidencias. Simulado: las marcas del enlace en verde, con un proyecto de mentira de dos personas; valida el orden, la confirmación y que los dos accesos llegan al mismo usuario **de mentira**. Real: la prueba manual del paso 10, que es lo único que valida que Firebase enlazó tus cuentas |
| 5.5 | Le pides a Claude Code la segunda entrada tomando la primera como referencia, y listas lo que no copió bien | Tu lista de sí y no del paso 4, con archivo y línea en cada «no», contra las ocho preguntas de «Compara tus respuestas». La calificas tú; el `verifica` no la lee |

**Tiempo:** calcula unas cuatro horas y media, con el registro en Entra, el «Rompe uno a propósito», el nivel
instrumentado y la entrada real; sin los pasos 9 y 10, unas tres. Es un estimado: lo ajustamos con lo que
tarde la primera cohorte. Conseguir el directorio no está contado: en el ensayo, la suscripción de prueba tardó
unos 15 minutos en aparecer como activa.

---

## ¿No te corre?

| Ves | Causa probable | Qué haces |
|---|---|---|
| `File google-services.json is missing.` | No copiaste tu archivo a `inicio/app/` | El `cp` de «Antes de empezar». Sobre `solucion/` es lo esperado en un clon limpio |
| ❌ «Estos archivos ya no son los de la lección 04» | Alguien, tú o Claude Code, cambió una prueba, una puerta o una pantalla de la 04 | `git diff -- <archivo>` y deshazlo a mano |
| ❌ «Tu entrada no retoma» | `retomar()` no usa lo que regresa `pendiente()` | Paso 4. Si vienes del «Rompe uno», paso 6, punto 4 |
| ❌ El caso «error» en rojo, con `resultadoDe` escrita | Toda `FirebaseAuthWebException` se está contando como cancelación | Paso 5: falta la condición del código |
| ❌ «Hay un texto con forma de clave de cliente de Entra» | Una clave, o algo con su forma, está en un archivo | Si es tu clave: bórrala del archivo, elimínala en Entra y crea otra (paso 9). Si es la inventada del control: borra esa línea |
| En las pruebas, `Default FirebaseApp is not initialized` | Se cambió `robolectric.properties` o `AppDePruebas.kt` (medido el 18-nov-2026 sin ellos) | Regrésalos: son andamiaje |
| En las pruebas instrumentadas, `NoSuchMethodException: android.hardware.input.InputManager.getInstance` | Se quitó la línea de Espresso 3.7.0 y quedó el que Compose trae por omisión. Medido el 18-nov-2026 en Android 37: 0 de 9 | Regresa `androidTestImplementation(libs.espresso.core)` |
| En el teléfono, «No se pudo entrar…» sin que se abra ninguna página | Con un archivo inventado, el registro dijo `API key not valid` (medido el 18-nov-2026). Con tu archivo: [pendiente: ensayo con cuenta — medir qué da con Microsoft sin habilitar o con la clave vencida] | Revisa que copiaste tu `google-services.json` y el paso 9 |
| En el navegador, un error `AADSTS50011` | Según Microsoft: la dirección de retorno de la solicitud no coincide con ninguna de las registradas. No se midió aquí | Paso 9, punto 4: pégala otra vez, completa |
| En `portal.azure.com`, `AADSTS16000` | Medido el 13-nov-2026 con una cuenta sin suscripción | Entra por `entra.microsoft.com` |
| «Nuevo registro» abre un panel sin formulario | Tu cuenta no tiene directorio (medido el 13-nov-2026 con una cuenta recién creada) | «Antes de empezar»: necesitas un directorio primero |
| «Ese correo ya entró a la tienda con otro botón…» | Ese correo ya tiene usuario en tu proyecto, creado con otro proveedor; en esta serie, Google | Entra con Google y acepta «Enlazar Microsoft» (paso 7) |
| «Primero entra con el botón que ya usabas…» | Se pidió enlazar sin nadie adentro | Entra primero; el enlace es sobre un usuario que ya entró |
| La pantalla de la llave de acceso se queda en «Verifying…» | Medido el 13-nov-2026 en el emulador | *Cancel* y entra con la contraseña |

En Linux, esta lección está [pendiente: medir en Linux Mint 22.3].

---

## Para cerrar

**Contesta sin volver a leer:**

1. ¿Dónde vive la clave de cliente, quién la usa y por qué no va en la app?
2. ¿Qué tiene que coincidir exactamente entre Firebase y Entra?
3. Tu `verifica` da 58 de 58 y las marcas «Clave:» están en verde. Di una cosa que eso demuestra y dos que no.
4. ¿Por qué «No conservar actividades» no sirve para probar `retomar()`?
5. ¿Por qué el enlace se hace después de entrar, y con confirmación?

**Lo que sigue:** en la L06 la sesión se queda: la app te recuerda al volver, y aprendes a cerrarla.

---

## Compara tus respuestas

**Paso 1.** Tu código participa en la fila 1, cuando arma la solicitud, y recibe la fila 6. Las filas 2 a 5
ocurren entre Firebase, el navegador y Microsoft. Tu tabla:

| Pregunta | Google (lección 04) | Microsoft (esta lección) |
|---|---|---|
| ¿Quién registra la app, y dónde? | Tú registras la app en Firebase y agregas la huella; los clientes de Google no los das de alta a mano: llegan en tu `google-services.json` después de habilitar Google (lección 03) | Tú, a mano, en la consola de Entra, que está fuera de Firebase |
| ¿Qué le muestra a la persona dónde entrar? | Credential Manager, con la ventana de cuentas de Android | El navegador, con la página de Microsoft |
| ¿Qué recibe tu app del proveedor? | Un token de identidad, que ella misma lleva a Firebase | Nada: el código viaja de Microsoft a Firebase. Tu app recibe de Firebase el usuario |
| ¿Hay una clave? ¿Dónde vive? | Tú no manejas ninguna: en la app solo va el ID de cliente web, que no es secreto | Sí, la clave de cliente. La creas en Entra y vive en la consola de Firebase |
| ¿Qué tiene que coincidir? | El nombre de paquete y la huella | La dirección de retorno, carácter por carácter |

**Lo que es igual es el patrón:** una `Entrada` con su puerta, un `Resultado` por cada forma de terminar, las
pruebas con un doble, y Firebase entregando al final un usuario. Las pantallas no supieron que había un
proveedor nuevo.

**Paso 4.** `retomar()` regresa `null` y la pantalla se queda en la bienvenida, sin mensaje. `entrar()` pregunta
otra vez porque Firebase entrega el resultado pendiente una sola vez y solo si es reciente, y porque es lo que
pide su guía: quien toca el botón no debe entrar dos veces si ya hay una respuesta esperando.

**Ejercicio 5.5.** Ocho preguntas de sí o no sobre lo que generó Claude Code. Lo esperado son ocho «sí»; cada
«no» es algo que no copió bien y va en tu lista con archivo y línea. La lista dice qué revisar, no qué va a
fallar: no se midió qué genera la herramienta.

| # | Pregunta | Por qué |
|---|---|---|
| 1 | ¿El proveedor es el texto `"microsoft.com"`, sin `https://`? | Así lo escribe la guía de Firebase |
| 2 | ¿Dejó fuera lo que era de Google: `GetCredentialRequest`, `GetSignInWithGoogleOption`, `GoogleIdTokenCredential`? | Aquí no hay token que pedirle al teléfono |
| 3 | ¿La clase recibe solo la puerta `navegador`, sin `idDeClienteWeb` ni otro identificador? | El ID y la clave viven en las consolas |
| 4 | ¿`entrar()` pregunta `pendiente()` antes de `abrir`? | Es lo que pide la guía, para no hacer entrar dos veces |
| 5 | ¿`retomar()` nunca llama a `abrir`? | Se llama al abrirse la app, sin que nadie toque un botón |
| 6 | ¿No hay escritos ningún ID de cliente, ninguna clave y ningún `tenant`? | Regla de datos del paso 2 |
| 7 | ¿Nadie nombra `accessToken`, `idToken` ni `OAuthCredential`? | Regla de datos: de Firebase solo sale el nombre |
| 8 | ¿Quedaron sin tocar las pruebas, las puertas, las pantallas y tu `google-services.json`? | El `verifica` compara esos archivos |

**Paso 5.** Vería el de la cancelación, «Cerraste la ventana…», aunque no cerró nada. Sin la condición, toda
falla del navegador se cuenta como cancelación.

**Paso 6.** Compila, y el botón sigue funcionando. Se ponen rojas siete pruebas: las de `retomar()` y las tres
que abren la app con algo a medias. Las del botón siguen en verde porque ninguna cierra la app: en ellas, el
navegador de mentira contesta al instante.

**Paso 7.** A ninguno: en la colisión nadie ha entrado, así que no hay usuario al cual sumarle nada. Por eso el
orden es entrar primero y enlazar después, y por eso la puerta lanza `NadieHaEntrado`.

**Para cerrar:**

1. En la consola de Firebase. La usan los servidores de Firebase para cambiar el código de Microsoft por los
   datos de la persona. No va en la app porque cualquiera que tenga la app instalada podría sacarla: Microsoft
   pide no usarla en apps nativas.
2. La dirección de retorno: la que usa Firebase y la que registraste en Entra.
3. Demuestra qué pide tu clase y qué contesta en cada caso, con una puerta de mentira, y que ningún archivo
   revisado trae un texto con forma de clave. No demuestra que tu registro en Entra funcione, ni que la clave no
   esté en tu terminal, en una captura o en el historial de git.
4. Porque destruye la pantalla pero deja vivo el proceso, que termina la entrada por su cuenta. `retomar()`
   existe para cuando el proceso ya no está.
5. Después, porque un enlace se hace sobre un usuario que ya entró: antes no hay a quién sumarle el proveedor.
   Con confirmación, porque le da a otra cuenta la entrada a este usuario, y eso lo decide su dueño.

---

## Fuentes de la lección

Abiertas el 18-nov-2026.

- Firebase. *Authenticate Using Microsoft on Android* (el proveedor, el resultado pendiente, la entrada, el enlace y el intercambio del código con la clave de la consola). https://firebase.google.com/docs/auth/android/microsoft-oauth
- Firebase. Referencia de `FirebaseAuth` (`getPendingAuthResult` y las excepciones de `startActivityForSignInWithProvider`). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth
- Firebase. *Link Multiple Auth Providers to an Account on Android* (primero se entra y después se enlaza; el mismo identificador con cualquier proveedor. Su ejemplo usa `linkWithCredential`; para Microsoft, la guía de ese proveedor indica `startActivityForLinkWithProvider`). https://firebase.google.com/docs/auth/android/account-linking
- Firebase. Referencia de `FirebaseUser` (`startActivityForLinkWithProvider` y su resultado pendiente). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseUser
- Firebase. Referencia de `FirebaseAuthUserCollisionException` (`ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL` y «una cuenta por correo»). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuthUserCollisionException
- Firebase. Referencia de `FirebaseAuthWebException` (sus cuatro códigos; `ERROR_WEB_CONTEXT_CANCELED`). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuthWebException
- Firebase. *Best practices for using signInWithRedirect* (la dirección `https://<project>.firebaseapp.com/__/auth/handler`). https://firebase.google.com/docs/auth/web/redirect-best-practices
- Microsoft Learn. *How to register an app in Microsoft Entra ID* (requisitos, pasos y tipos de cuenta). https://learn.microsoft.com/en-us/entra/identity-platform/quickstart-register-app
- Microsoft Learn. *How to add a redirect URI to your application* (los pasos y la plataforma Web). https://learn.microsoft.com/en-us/entra/identity-platform/how-to-add-redirect-uri
- Microsoft Learn. *Redirect URI (reply URL) best practices and limitations* (direcciones registradas, `https`, mayúsculas y `AADSTS50011`). https://learn.microsoft.com/en-us/entra/identity-platform/reply-url
- Microsoft Learn. *Add and manage app credentials in Microsoft Entra ID* (la clave: 24 meses como máximo y un valor que no se vuelve a mostrar). https://learn.microsoft.com/en-us/entra/identity-platform/how-to-add-credentials
- Microsoft Learn. *Microsoft identity platform and OAuth 2.0 authorization code flow* (`select_account`, la coincidencia exacta y la clave fuera de las apps nativas). https://learn.microsoft.com/en-us/entra/identity-platform/v2-oauth2-auth-code-flow
- Microsoft Azure. *Azure for Students* (condiciones de la oferta). https://azure.microsoft.com/en-us/pricing/offers/ms-azr-0170p
- Android Developers. *Side-effects in Compose* (`LaunchedEffect`). https://developer.android.com/develop/ui/compose/side-effects
- Android Developers. *Android Debug Bridge* (`am kill`). https://developer.android.com/tools/adb
- Kotlin. *Null safety* (los tipos con `?` y el operador `?:`). https://kotlinlang.org/docs/null-safety.html
- gitleaks, herramienta pública de búsqueda de secretos: su regla `azure-ad-client-secret` es la forma que busca el `verifica`. No es un formato publicado por Microsoft. https://github.com/gitleaks/gitleaks/blob/master/config/gitleaks.toml
- Repositorio Maven de Google: el BoM de Firebase 35.2.0 sigue siendo el más reciente y trae `firebase-auth` 25.0.0. https://dl.google.com/dl/android/maven2/com/google/firebase/firebase-bom/maven-metadata.xml · https://dl.google.com/dl/android/maven2/com/google/firebase/firebase-bom/35.2.0/firebase-bom-35.2.0.pom
- Ensayo de la serie, 13-nov-2026, Mac con Apple silicon, con un código de prueba: el registro en Entra por `entra.microsoft.com`; `AADSTS16000` en el portal de Azure; la cuenta recién creada y la prueba gratuita; la entrada real, el permiso, la colisión, el enlace después de entrar con Google y el resultado pendiente tras `adb shell am kill`; la llave de acceso en el emulador.
- Medición propia: Mac con Apple silicon, 18-nov-2026. El `verifica` sobre `inicio/` y `solucion/`, cada paso de la lección y cada sabotaje, con archivos inventados y sin tocar la consola de Entra, la de Azure ni la de Firebase, ni ninguna cuenta. Las pruebas instrumentadas y el botón de Microsoft con un archivo inventado, en un emulador nuevo, sin cuenta, que se borró al terminar. Los cuatro códigos de `FirebaseAuthWebException` se cotejaron además dentro de la librería `firebase-auth` 25.0.0.
