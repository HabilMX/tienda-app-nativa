# Lección 08 — La primera vez

**Antes de leer, tres palabras.** Una línea cada una.

- **Usuario nuevo:** el que Firebase acaba de crear en tu proyecto, en este mismo inicio de sesión.
- **Resultado del inicio de sesión:** lo que Firebase contesta cuando alguien entra. En el código se llama
  `AuthResult`.
- **`isNewUser`:** la pregunta que ese resultado sabe contestar: ¿este usuario es nuevo o ya existía?

**Qué te llevas:** una tienda que le da la bienvenida a quien entra por primera vez, con Google o con
Microsoft, y que no se la vuelve a dar cuando regresa. Y una cuenta honesta de lo que hizo Firebase por ti en
todo el tramo, de lo que tendrías que construir sin él y de lo que todavía no existe.

**Idea nueva de esta lección:** distinguir al usuario nuevo del que vuelve. Lo demás —las puertas que ahora
contestan también si es la primera vez, la línea de bienvenida en el inicio y el estado que la recuerda— viene
dado y explicado, y no se evalúa aquí.

---

## La escena

13 de noviembre de 2026, en el ensayo de esta serie. Una cuenta real de Google entra por primera vez a la app de
prueba. La pantalla dice: «Hola, … (primera vez)».

Alguien sale y vuelve a entrar con la misma cuenta. Ahora dice: «Hola, …». Sin el paréntesis.

Esa misma noche pasa lo mismo con Microsoft: la primera entrada trae «(primera vez)»; la segunda, ya no.

Y aquí está lo raro. Esa app no tenía base de datos. No guardaba una lista de quién ya había entrado. No escribía
nada en el teléfono. ¿Cómo supo que la segunda vez ya no era la primera?

No lo supo ella. Se lo dijo Firebase, en una sola palabra que viene escondida en la respuesta de cada inicio de
sesión. Tu app la ha estado recibiendo desde la lección 04, y la ha tirado a la basura cada vez: de esa
respuesta, tus puertas solo sacan el nombre. Esta lección la rescata.

---

## Antes de empezar

**Qué necesitas:** la [07](07-la-sesion-se-queda.md) terminada (su `verifica` en 82 de 82). Los pasos 1 a 3,
5 y 6 no piden ninguna cuenta. El paso 4 es en tu teléfono o emulador, con tu cuenta de Google de la 04 y, si
hiciste la parte opcional, tu registro de Microsoft de la 05.

**El proyecto de inicio.** Desde la raíz del repo de la serie:

```bash
git pull
cp lecciones/03-la-app-conoce-a-firebase/inicio/app/google-services.json lecciones/08-la-primera-vez/inicio/app/
cd lecciones/08-la-primera-vez/inicio
```

Parte de la solución de la 07, con el andamiaje de esta lección encima. `./verifica solucion` sale en rojo en un
clon limpio, porque sin tu archivo el proyecto no compila. Tus respuestas van en `respuestas/08.md`.

**Dos «bienvenidas» que no son la misma.** La pantalla `Bienvenida`, desde la lección 02, es la portada: la que ve
todo el que no ha entrado, con el botón «Entrar». La bienvenida de esta lección es otra cosa: una línea en el
**inicio**, debajo de «Hola, Ana», que solo ve el usuario nuevo. El texto dice: «Es tu primera vez aquí: te
damos la bienvenida.»

**Regla que sigue vigente.** Nunca pegues ni subas un token, ni capturas o registros que lo traigan. El
resultado de un inicio de sesión trae tokens; las puertas de esta app solo sacan de él el nombre y un sí o un no.

**Lo que no hace esta lección.** No crea un perfil de la tienda: no guarda dirección, órdenes ni preferencias.
No cambia lo que hacen las pantallas de la 04 a la 07: entrar, los dos botones, el enlace, reabrir y cerrar
sesión se portan igual, y sus 82 pruebas lo vigilan.

---

## Paso 1 · Predice: ¿cuándo aparece la bienvenida?

Es el primer paso de siempre: **predices** antes de pedirle nada a Claude Code. Todavía no leas el paso 2.

Piensa en la app **terminada** de esta lección y en estos cuatro momentos, en orden:

1. Ana entra con Google por primera vez en su vida a tu tienda.
2. Ana cierra la app desde la lista de apps recientes y la vuelve a abrir (como en la 07).
3. Ana toca «Cerrar sesión» y vuelve a entrar con Google.
4. Luis entra con Microsoft por primera vez, y Android cierra la app mientras él está en la página de Microsoft.
   Al volver, la app retoma la entrada (como en la 05).

Escribe en `respuestas/08.md` una línea que empiece así, con los números de los momentos en que crees que aparece
la bienvenida y, en una frase, por qué:

```text
predicción 8.1: …
```

**Mide el punto de partida.** Desde la raíz del repo:

```bash
./lecciones/08-la-primera-vez/verifica
```

El 19-nov-2026, con un archivo inventado, la última línea del conteo fue
`Resultado: 84 de 97 pruebas en verde (13 en rojo, 0 con error, 0 saltadas)`. Son 97: las 82 que heredas de la
07, ya en verde, y 15 de la primera vez. De esas, dos ya pasan con el andamiaje (las de «el usuario que vuelve»:
hoy tu app nunca dice que es la primera vez, y en ese caso acierta) y trece esperan tu código.

**Lo demuestra:** tu predicción, contra lo que veas en el paso 3 (las pruebas) y en el paso 4 (tu teléfono). El
`verifica` solo busca la línea: no sabe si acertaste.

---

## Paso 2 · Lo que el proyecto ya trae

**Dónde vive la respuesta.** Cuando alguien entra, Firebase contesta con un `AuthResult`. Su referencia dice que
es el resultado «de operaciones que pueden afectar el estado de autenticación». Además del usuario, trae
`getAdditionalUserInfo()`: información adicional. Y la referencia de `AdditionalUserInfo` dice que contiene
información del usuario «como resultado de un inicio de sesión, un enlace o una reautenticación exitosos». Su
función `isNewUser()` «regresa si el usuario es nuevo o existente». En Kotlin se lee como una propiedad:
`resultado.additionalUserInfo?.isNewUser`.

**Dos detalles de la documentación que deciden cómo se escribe esa línea:**

- La referencia marca `getAdditionalUserInfo()` como `@Nullable`: puede venir `null`. Kotlin lo trae como un tipo
  que admite `null`, y el operador `?.` existe para eso: si lo de la izquierda es `null`, el resultado es `null`
  en lugar de tronar.
- La misma referencia dice que regresa información «si el proveedor es Facebook, GitHub, Google o Twitter».
  Microsoft no está en esa lista. Pero la página de `AdditionalUserInfo`, en `getProfile()`, sí nombra a
  Microsoft, y en el ensayo del 13-nov la entrada con Microsoft dijo «(primera vez)» la primera vez y no la
  segunda. La documentación no es pareja aquí, y por eso el código no supone nada: si no hay información,
  no hay bienvenida.

**Lo que cambió en la app.** Un archivo es nuevo y cinco cambiaron respecto a la solución de la 07; en las
pruebas llegan tres clases nuevas y sus dobles, `DoblesDePrimeraVez.kt`. Ábrelos; edita solo los tres que dice el
paso 3:

| Archivo | Qué trae |
|---|---|
| `PrimeraVez.kt` (nuevo) | La forma de tu función `esPrimeraVez(resultado: AuthResult)`, vacía. La escribes en el paso 3 |
| `Entrada.kt` | `Resultado.Entro` trae, además del nombre, `primeraVez`. Vale `false` si nadie lo dice: así todo lo de la 04 a la 07 sigue igual |
| `Puertas.kt` | `QuienEntro`: el nombre y un sí o un no. Y en la puerta de Google, una función nueva: `verificarConPrimeraVez()` |
| `PuertaDelNavegador.kt` | En la puerta de Microsoft, dos nuevas: `abrirConPrimeraVez()` y `pendienteConPrimeraVez()` |
| `Tienda.kt` | Guarda si quien entró es nuevo y se lo pasa al inicio. Al reabrir con sesión y al cerrar sesión, lo deja en `false` |
| `Inicio.kt` | Con `primeraVez` en `true`, agrega la línea de bienvenida. Con `false`, es el inicio de la 07 |

**El truco de las funciones nuevas.** Las tres traen un cuerpo por omisión que contesta «no es la primera vez».
Por eso los dobles de la 04 a la 07, que no saben nada de esto, siguen compilando y sus pruebas siguen en verde.
Las puertas de verdad reemplazan ese cuerpo: leen el `AuthResult` de Firebase y le preguntan a **tu**
`esPrimeraVez`. Hacia afuera de la puerta solo sale `QuienEntro`: el nombre y un `Boolean`. El resultado completo,
con sus tokens, se queda adentro.

Lo demás es idéntico a la solución de la 07, y el `verifica` lo compara archivo por archivo. Es una restricción de
esta actividad, no del objetivo.

---

## Paso 3 · Con Claude Code: decidir la primera vez

Entra igual que siempre: **predices → le pides → lees lo que generó → corres el `verifica` → lo explicas.**

1. **Predice**, en `respuestas/08.md`, en una línea libre: si `additionalUserInfo` viene `null` y escribes
   `resultado.additionalUserInfo!!.isNewUser`, ¿qué le pasa a la persona que intenta entrar?
2. **Pídele** a Claude Code, parado en `inicio/`: «En `PrimeraVez.kt`, `esPrimeraVez` regresa `true` solo si
   `resultado.additionalUserInfo?.isNewUser` es `true`; con `null` regresa `false`. En `EntradaConGoogle.kt`
   cambia `verificador.verificar(token)` por `verificador.verificarConPrimeraVez(token)` y pasa su nombre y su
   `primeraVez` a `Resultado.Entro`. En `EntradaConMicrosoft.kt`, en `retomar()` y en `entrar()`, usa
   `navegador.pendienteConPrimeraVez()` y `navegador.abrirConPrimeraVez(proveedor)` en lugar de `pendiente()` y
   `abrir(proveedor)`, y pasa igual el nombre y la `primeraVez`. No cambies ningún otro archivo.»
3. **Lee.** El centro debe parecerse a esto:

```kotlin
// PrimeraVez.kt
fun esPrimeraVez(resultado: AuthResult): Boolean =
    resultado.additionalUserInfo?.isNewUser == true

// EntradaConGoogle.kt, en el paso 4 de entrar()
val quien = verificador.verificarConPrimeraVez(token)
Resultado.Entro(quien.nombre, quien.primeraVez)

// EntradaConMicrosoft.kt, en entrar()
val quien = navegador.pendienteConPrimeraVez() ?: navegador.abrirConPrimeraVez(proveedor)
Resultado.Entro(quien.nombre, quien.primeraVez)
```

   Revisa tres cosas. Que diga `== true` y no `!!`: con `?.`, un `null` se vuelve `null`, y `null == true` es
   `false`. Que en `EntradaConMicrosoft.kt` cambien **las dos** funciones, `retomar()` y `entrar()`: si solo
   cambia una, la otra sigue tirando la primera vez. Y que nadie haya tocado las pruebas, las puertas ni las
   pantallas.

4. **Corre el `verifica`.** Las 97 pruebas pueden quedar en verde. La entrega todavía no está completa: faltan tus
   líneas del 8.1, del 8.2 y del 8.3 en `respuestas/08.md`, y el `verifica` lo dice al final.
5. **Explica**, en tres líneas: por qué la decisión vive dentro de la puerta y hacia las pantallas solo sale un
   `Boolean`.

**Lo demuestra, y hasta dónde.** Unas pruebas le dan a tu `esPrimeraVez` un `AuthResult` de mentira y miran qué
decide: con `isNewUser` en `true`, en `false` y sin información adicional. Otras le dan a tus dos entradas puertas
que contestan «es nuevo» o «ya existía» y miran si el `Resultado` lo trae, también al retomar con Microsoft. Y las
del recorrido dibujan la tienda con el proyecto de mentira de la 06: Ana entra con Google y ve la bienvenida;
cierra sesión, vuelve a entrar y ya no la ve, y sigue siendo el mismo usuario. Lo mismo con Luis y Microsoft. Al
reabrir con sesión, sin bienvenida. Al retomar con Microsoft a un usuario nuevo, con bienvenida.

Los sabotajes se midieron sobre la solución el 19-nov-2026:

| Sabotaje | Resultado |
|---|---|
| `esPrimeraVez` con `!= false`: un `null` cuenta como nuevo | 96 de 97 |
| `esPrimeraVez` con `additionalUserInfo!!` | 96 de 97 |
| `esPrimeraVez` al revés (`isNewUser == false`) | 95 de 97 |
| `EntradaConGoogle` que no pasa la `primeraVez` | 93 de 97 |
| `EntradaConMicrosoft` que no la pasa en `entrar()` | 93 de 97 |
| `EntradaConMicrosoft` que no la pasa en `retomar()` | 95 de 97 |
| `EntradaConGoogle` que siempre dice que es la primera vez | 92 de 97, y tres de las rojas son heredadas de lecciones anteriores |
| `EntradaConGoogle` que falla solo cuando la puerta es `VerificadorConFirebase` | 97 de 97: las pruebas no lo ven, porque solo le pasan dobles. Lo atrapa la marca de la puerta concreta |

Cada uno deja además su marca en rojo en la salida, con el paso que lo arregla.

Tres límites. «Es nuevo» en las pruebas lo decide un doble: el proyecto de mentira dice «nuevo» cuando esa entrada
creó un usuario, que es lo que Firebase promete, pero no es Firebase. Ninguna prueba ve un `AuthResult` de verdad.
Y ninguna mira qué contesta `isNewUser` al enlazar: esta app no usa ese resultado para la bienvenida (el paso 4
dice por qué).

Esta lección no tiene nivel instrumentado, como la 06 y la 07: la bienvenida es una línea de texto en una pantalla
que ya se probó dentro de Android en la 04, y lo nuevo es una decisión, que se prueba en tu computadora.

---

## Paso 4 · En el teléfono: la primera vez de verdad

Instala y abre la app con tu `google-services.json`, como en la 04:

```bash
./gradlew installDebug
```

**El problema de tu cuenta.** Tu cuenta de Google ya entró a tu proyecto en la 04. Para Firebase ya no es nueva,
así que con ella **no** vas a ver la bienvenida. Es justo lo que esta lección promete, y es tu primera
comprobación: entra con tu cuenta y mira que diga «Hola, …» sin la línea de bienvenida.

Para verla, Firebase tiene que crear un usuario. Tienes dos caminos:

- **Otra cuenta de Google** que nunca haya entrado a tu proyecto, si tienes una de pruebas.
- **Borrar tu usuario** en la consola de Firebase, en Authentication › Users. Es tu proyecto de pruebas, pero
  borrar un usuario no se deshace, y con él se va cualquier enlace que hayas hecho en la 06.

Entra. Debe salir la bienvenida. Toca «Cerrar sesión», vuelve a entrar con la misma cuenta, y ya no debe salir.
Compara con tu predicción.

Lo que ya se vio en el ensayo del 13-nov-2026, con un código de prueba: `isNewUser` valió `true` la primera vez y
`false` al volver, con Google y con Microsoft. Lo que **todavía no capturamos con una cuenta real** es este
recorrido con el código de esta lección: la línea de bienvenida en tu inicio y que no regrese al volver a entrar.
Si te aparece distinto, cuéntanos.

**Dos casos que la documentación no cierra.** Se buscaron en las páginas de Firebase el 19-nov-2026:

- **Al enlazar** (lección 06). `AdditionalUserInfo` dice que también llega después de un enlace, pero ninguna
  página dice qué vale `isNewUser` en ese caso: ni la referencia, ni la guía de enlazar cuentas, ni la de
  Microsoft. **No está documentado.** Esta app no le pregunta al enlace: quien enlaza ya entró antes con el otro
  botón, y esa entrada ya decidió si había bienvenida.
- **Al retomar** (lección 05). `getPendingAuthResult()` entrega un `AuthResult`, el mismo tipo que una entrada
  normal, y la app lo lee igual. Pero ninguna página dice de forma explícita que ahí `isNewUser` se porte igual:
  **no está documentado**. En el ensayo se retomó una entrada con una cuenta que ya existía, y no se probó con
  una nueva. Si lo pruebas y ves otra cosa, cuéntanos.

**Lo demuestra:** lo que tú observes. No hay marca en el `verifica` para este paso.

---

## Paso 5 · Sin IA: lo que Firebase hizo por ti

Este es el ejercicio 8.2, el de cierre del tramo, y se hace **sin Claude Code**. Tampoco vuelvas a leer las
lecciones anteriores mientras lo contestas: primero escribe lo que recuerdes; después, si quieres, revisa.

Ocho lecciones, una app que te deja entrar con dos proveedores, te recuerda y sabe si es tu primera vez. Ahora la
cuenta honesta. Escribe en `respuestas/08.md` tres líneas que empiecen así:

```text
8.2 Firebase hizo: …
8.2 sin Firebase: …
8.2 todavía no hay: …
```

1. **Firebase hizo:** al menos tres cosas que Firebase hizo por ti en el tramo. Piensa en lo que pasa entre que
   eliges tu cuenta y la app dice «Hola».
2. **Sin Firebase:** para cada una, qué pieza tendrías que construir tú. No basta «un servidor»: di qué tendría
   que hacer ese servidor.
3. **Todavía no hay:** algo que una tienda de verdad necesita saber de ti y que tu app, hoy, no tiene dónde
   guardar.

**Lo demuestra:** tus tres líneas, contra «Compara tus respuestas». El `verifica` solo busca las tres: no las
califica.

---

## Paso 6 · Juzgar una respuesta de Claude Code

Este es el ejercicio 8.3. Imagina que, al terminar el paso 3, le preguntas a Claude Code: «¿Qué tan seguro es el
inicio de sesión de mi app?». Una respuesta **como ésta** es verosímil. No es una cita de una conversación real:
está escrita para este ejercicio.

> Revisé el inicio de sesión de tu app. Tres cosas:
>
> **A.** Tu app no valida el token de identidad de Google: se lo entrega a Firebase, y son los servidores de
> Firebase los que lo verifican antes de dejar entrar a nadie.
>
> **B.** Con Firebase, tu inicio de sesión ya es completamente seguro: nadie podrá entrar a la tienda con una
> cuenta que no sea suya, así que no hace falta revisar nada más.
>
> **C.** Firebase guarda el token de actualización cifrado en el teléfono, así que aunque alguien saque una copia
> de los datos de la app, no le sirve.

Suena bien. Las tres frases están dichas con la misma seguridad. Tu trabajo es separarlas.

**Ahora tú.** Para cada afirmación, escribe en `respuestas/08.md` uno de tres juicios —`correcta`, `exagerada` o
`sin comprobar`— al principio de la línea y, después, por qué. Por ejemplo, `8.3-A: correcta porque …`:

```text
8.3-A: <juicio> porque …
8.3-B: <juicio> porque …
8.3-C: <juicio> porque …
8.3 comprobé: …
fuente 8.3: https://…
```

En `8.3 comprobé` di dos cosas: qué de la respuesta comprobó la propia respuesta (¿citó un archivo, una línea, una
página?) y qué comprobaste tú, y cómo. En `fuente 8.3`, la dirección de una página oficial que usaste para
comprobar algo.

**Lo demuestra:** tus tres juicios contra «Compara tus respuestas», y tu comprobación. El `verifica` solo busca las
líneas y la fuente: no las califica.

---

## Tus ejercicios

El paso 5 se hace **sin IA**.

| # | Haces | Está hecho cuando |
|---|---|---|
| 8.1 | Decides con `isNewUser` si se muestra la bienvenida y demuestras que aparece solo la primera vez | El `verifica` dice «Nivel simulado: completo», con las 97 pruebas en verde, y tu `predicción 8.1` está comparada con lo que viste. Es el nivel simulado; el paso 4 es tu prueba en el teléfono |
| 8.2 | Explicas qué hizo Firebase por ti, qué construirías sin él y qué todavía no hay | Tus tres líneas `8.2` cubren lo de «Compara tus respuestas», escritas sin IA |
| 8.3 | Juzgas una respuesta de Claude Code sobre la seguridad del inicio de sesión | Tus tres juicios coinciden con «Compara tus respuestas», cada uno con su porqué, y hay una comprobación con fuente oficial |

**Tiempo:** calcula una hora y media sin el paso 4, y veinte minutos más con él. Es un estimado: lo ajustamos con
lo que tarde la primera cohorte.

---

## ¿No te corre?

| Ves | Causa probable | Qué haces |
|---|---|---|
| `File google-services.json is missing.` | No copiaste tu archivo a `inicio/app/` | El `cp` de «Antes de empezar» |
| `NotImplementedError` en las pruebas de la primera vez | `PrimeraVez.kt` sigue con su `TODO` | Paso 3 |
| ❌ «Con additionalUserInfo en null tu esPrimeraVez truena o dice que sí» | Usaste `!!`, o `!= false` | Paso 3: `?.` y `== true` |
| ❌ «Retomar con Microsoft» y lo demás de Microsoft en verde | Cambiaste `entrar()` pero no `retomar()`, o al revés | Paso 3: las dos funciones |
| ❌ «De las 82 pruebas heredadas…» | Tu entrada siempre dice que es la primera vez, y el `Resultado` ya no es el que esperan las pruebas de antes | Pasa la `primeraVez` que contestó la puerta, nada más |
| ❌ «Restricción de esta actividad… ya no son iguales en inicio/ y en solucion/» | Cambió un archivo dado | `git diff -- <archivo>` y deshazlo a mano |
| En el teléfono, nunca sale la bienvenida | Tu cuenta ya existía en tu proyecto, desde la 04 | Paso 4: otra cuenta, o borra tu usuario |
| En el teléfono, sale la bienvenida cada vez que entras | Borraste el usuario en la consola entre una entrada y otra, o algo distinto que no hemos visto | Revisa Authentication › Users; si el usuario sigue ahí y sale cada vez, cuéntanos |
| ❌ «EntradaConGoogle.kt o EntradaConMicrosoft.kt nombra una puerta concreta» | Una de tus entradas nombra una puerta de verdad (`VerificadorConFirebase`, `NavegadorConFirebase`) o un doble de las pruebas. Las pruebas solo les pasan dobles: si tu código pregunta cuál le tocó, el verde no dice qué hace con la de verdad | Que tus entradas hablen solo con las interfaces de sus puertas. La puerta de verdad la escoge `MainActivity.kt`, que viene dado (paso 3) |

El `verifica` de esta lección se midió en una Mac con Apple silicon. No se ha medido en Linux.

---

## Para cerrar

**Contesta sin volver a leer:**

1. ¿Quién sabe si es la primera vez: tu app o Firebase? ¿Por qué tu app no lo puede saber sola, hoy?
2. ¿Por qué `== true` y no `!!`?
3. Tu `verifica` da 97 de 97. Di una cosa que eso demuestra y una que no.
4. Al reabrir con sesión, ¿por qué no hay bienvenida, aunque sea la segunda vez que abres la app ese día?

**Cierre del tramo.** Empezaste con un ambiente vacío. Hoy tienes una app Android nativa, en Kotlin y Compose, de
una tienda donde entras con Google o con Microsoft; donde una persona con el mismo correo en los dos llega a un
solo usuario, porque ella lo confirmó; que te recuerda al volver a abrirla y te olvida cuando se lo pides; y que te
da la bienvenida solo la primera vez. Cada pieza pasó por la misma vuelta: predecir, pedir, leer, correr y
explicar. Y en el paso 5 dijiste, con tus palabras, qué parte puso Firebase y qué parte pusiste tú.

**Lo que sigue: el tramo 2.** La misma tienda, en iPhone, con SwiftUI. No se traduce el código de Android línea
por línea: la app que terminaste hoy es la **especificación** de lo que la de iPhone tiene que hacer, y tus 97
pruebas dicen, caso por caso, qué es «hacerlo». Para compilar una app de iPhone hace falta una Mac con Xcode, así
que ese tramo es solo en Mac. Se publica cuando esté probado de punta a punta, como éste.

---

## Compara tus respuestas

**Paso 1.** Solo en el 1 y en el 4. En el 1, Firebase crea a Ana y `isNewUser` vale `true`. En el 2 no hubo inicio
de sesión: la app le pregunta a la sesión quién sigue adentro, y eso no dice si es nuevo. En el 3, Ana ya existe:
`isNewUser` vale `false`. En el 4, Luis es nuevo y la entrada retomada trae un `AuthResult` como cualquier otra:
las pruebas lo miden con un doble; con una cuenta real, no está documentado ni capturado (paso 4).

**Paso 3.** No entra, aunque Firebase sí la dejó entrar. `!!` convierte el `null` en una excepción; tu entrada la
atrapa con su `catch (e: Exception)`, como cualquier otra falla, y la persona ve «No se pudo entrar. Revisa tu
conexión y vuelve a intentar.». La conexión estaba bien. Y como Firebase ya había guardado la sesión, al reabrir la
app (lección 07) la saludaría como si nada: lo razonamos del código, no lo medimos en un teléfono. Con `?.` y
`== true`, un `null` se vuelve «no es la primera vez», y la persona entra sin bienvenida.

**Paso 5.** Lo que se espera, con estas ideas o las tuyas:

- **Firebase hizo:** verificó el token de identidad de Google en sus servidores; habló con Microsoft usando el ID
  de cliente y la clave que guardaste en su consola, sin que la clave tocara tu app; creó al usuario la primera
  vez que entraste, con un identificador único; aplicó «una cuenta por correo» y enlazó el segundo proveedor al
  mismo usuario; guardó la sesión en el teléfono y renueva el token de identidad cada hora con el de
  actualización; y te dijo si el usuario era nuevo.
- **Sin Firebase:** un servidor tuyo que revise que cada token es auténtico, que se emitió para tu app y que no ha
  caducado; el intercambio con Microsoft hecho desde ese servidor, que es donde viviría la clave; una tabla de
  usuarios con su identificador y con qué cuentas se llega a cada uno; la regla del correo repetido y el enlace;
  tus propios tokens de sesión, cómo se renuevan y dónde se guardan en el teléfono; y la marca de «usuario nuevo»,
  que tendrías que calcular tú al crear la fila.
- **Todavía no hay:** un perfil de la tienda. El usuario de autenticación tiene un conjunto fijo de datos: un
  identificador, un correo, un nombre y una foto. La propia documentación dice que no se le pueden agregar otros y
  que van en otro almacenamiento. Tu dirección, tus órdenes y tus preferencias no tienen dónde vivir todavía: eso
  llega cuando el curso introduzca almacenamiento.

**Paso 6.**

- **A: correcta.** La página de Firebase Authentication dice que la app le pasa las credenciales al SDK y que sus
  servicios las verifican y contestan. Y se comprueba en tu código: `VerificadorConFirebase` entrega el token a
  `signInWithCredential` y nunca lo revisa por su cuenta.
- **B: exagerada.** «Completamente» y «no hace falta revisar nada más» no los sostiene ninguna fuente. En el
  propio tramo hay contraejemplos: una sesión abierta sigue abierta en un teléfono prestado (lección 07); la clave
  de Microsoft se puede filtrar en una captura (lección 05); y si tu app enlazara sin preguntar, juntaría cuentas
  sin permiso (lección 06). Firebase resuelve partes; la seguridad del inicio de sesión también depende de lo que
  haces tú.
- **C: sin comprobar.** Puede ser cierta o no: ninguna de las páginas de esta serie dice cómo guarda Firebase los
  tokens en el teléfono. La referencia de `signOut` solo dice que borra al usuario «del caché en disco». La
  respuesta no cita nada. Para darla por buena, alguien tendría que mostrar la fuente.
- **Lo que comprobó la respuesta:** nada que se vea. Dice «revisé el inicio de sesión de tu app», pero no nombra un
  archivo, una línea ni una página. Lo que tú puedes comprobar: A, con la documentación y con `Puertas.kt`.

**Para cerrar:**

1. Firebase. Tu app no guarda una lista de quién ya entró; Firebase sí tiene a sus usuarios, y al crear uno lo
   dice en `isNewUser`.
2. Porque `additionalUserInfo` puede venir `null`, y `!!` convierte ese `null` en una excepción: la persona vería
   «No se pudo entrar» sin que nada hubiera fallado. Con `== true`, `null` es «no».
3. Demuestra qué decide tu `esPrimeraVez`, que tus dos entradas pasan esa decisión y cuándo el inicio da la
   bienvenida, con dobles. No demuestra qué contesta `isNewUser` con una cuenta real, ni al enlazar.
4. Porque al reabrir no hubo inicio de sesión: no hay `AuthResult` y no hay a quién preguntarle si es nuevo. La
   app lo deja en «no».

---

## Fuentes de la lección

Abiertas el 19-nov-2026.

- Firebase. Referencia de `AdditionalUserInfo` (llega tras un inicio de sesión, un enlace o una reautenticación;
  `isNewUser` dice si el usuario es nuevo o existente; `getProfile` nombra a Microsoft entre sus proveedores).
  https://firebase.google.com/docs/reference/android/com/google/firebase/auth/AdditionalUserInfo
- Firebase. Referencia de `AuthResult` (resultado de operaciones que afectan la autenticación;
  `getAdditionalUserInfo` es `@Nullable` y su descripción nombra a Facebook, GitHub, Google y Twitter).
  https://firebase.google.com/docs/reference/android/com/google/firebase/auth/AuthResult
- Firebase. Referencia de `FirebaseAuth` (`signInWithCredential` crea la cuenta si no existía; `getPendingAuthResult`
  entrega la entrada que quedó a medias). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth
- Firebase. Referencia de `FirebaseUser` (`startActivityForLinkWithProvider` regresa un `AuthResult`; no dice qué
  vale `isNewUser` al enlazar). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseUser
- Firebase. *Authenticate Using Microsoft on Android* (revisar primero la respuesta pendiente; los datos del
  proveedor en `getAdditionalUserInfo()`; no menciona `isNewUser`). https://firebase.google.com/docs/auth/android/microsoft-oauth
- Firebase. *Authenticate with Google on Android* (la primera vez que el usuario entra se crea una cuenta en tu
  proyecto). https://firebase.google.com/docs/auth/android/google-signin
- Firebase. *Link Multiple Auth Providers to an Account on Android* (el mismo identificador sin importar el
  proveedor; no menciona `isNewUser`). https://firebase.google.com/docs/auth/android/account-linking
- Firebase. *Users in Firebase projects* (propiedades fijas del usuario; no se le agregan otras: van en otro
  almacenamiento). https://firebase.google.com/docs/auth/users
- Firebase. *Firebase Authentication* («How does it work?»: la app pasa las credenciales y los servicios de
  Firebase las verifican). https://firebase.google.com/docs/auth
- Kotlin. *Null safety* (el operador `?.` regresa `null` si lo de la izquierda es `null`).
  https://kotlinlang.org/docs/null-safety.html
- Ensayo de la serie, 13-nov-2026, Mac con Apple silicon, con un código de prueba y cuentas reales: «(primera vez)»
  salió solo en la primera entrada, con Google y con Microsoft.
- Medición propia: Mac con Apple silicon, 19-nov-2026. El `verifica` sobre `inicio/` y `solucion/`, cada paso y cada
  sabotaje, con archivos inventados y sin tocar la consola de Firebase ni ninguna cuenta. Versiones del proyecto:
  las de la serie (AGP 9.4.1, Kotlin 2.4.21, Firebase BOM 35.2.0, que trae `firebase-auth` 25.0.0).
