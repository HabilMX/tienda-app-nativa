# Lección 01 — Mi primera pantalla corre

**Por Dorian Chávez, fundador de Hábil y arquitecto de integración.**

**Antes de leer, siete palabras.** Una línea cada una; las demás de la serie llegan en la lección donde se usan.

- **Kotlin:** el lenguaje en que escribes la app. Sus archivos terminan en `.kt`.
- **Compose** (Jetpack Compose): la librería de Android para hacer pantallas escribiendo funciones de Kotlin.
- **Función composable:** una función marcada con `@Composable` que describe una parte de la pantalla.
- **Boceto:** el dibujo de cómo debe quedar la pantalla. Es contra lo que comparas.
- **Modificador:** lo que le dice a un elemento cuánto ocupa y cuánto margen deja.
- **Estado:** un dato que la pantalla recuerda y que puede cambiar, como «ya tocaron el botón».
- **Prueba simulada:** una prueba que dibuja tu pantalla en la memoria de tu computadora, sin emulador, y busca en
  ella lo que debería estar.

**Qué te llevas:** la pantalla de bienvenida de la tienda —nombre, lema y botón— escrita por ti, y saber leer una
pantalla de Compose línea por línea y decir qué cambia en pantalla cada una.

**Idea nueva de esta lección (la única que se evalúa):** en Compose, una pantalla es una función que describe lo que
se ve. No hay un archivo de diseño aparte: lees la función y sabes qué va a aparecer.

---

## La escena

Le pides la pantalla de bienvenida a Claude Code. En medio minuto tienes treinta líneas de Kotlin. Compila. Las
pruebas salen en verde. La instalas, tocas «Entrar» y aparece el saludo. Cierras la computadora.

Al día siguiente alguien abre la app junto al boceto y te pregunta: «¿por qué el saludo sale arriba del botón, si
aquí va abajo?». Abres el archivo. Ves `remember`, `mutableStateOf`, `Modifier`, un `if` en medio de la pantalla.
No sabes cuál de esas líneas mueve el saludo.

Ese código existe: es el listado del ejercicio 1.3, y sus pruebas salen 4 de 4 en verde con el saludo en el lugar
equivocado (medido el 18 de noviembre de 2026). El verde dice que el saludo aparece. No dice dónde.

Por eso la lección va en este orden: lees una pantalla, escribes una a mano, le pides un cambio a Claude Code y,
al final, explicas —sin ayuda— qué hace cada bloque de lo que generó.

---

## Antes de empezar

**Qué necesitas:**

| | Sirve | No sirve |
|---|---|---|
| Lección anterior | La [00](00-ambiente-listo.md) terminada: `ANDROID_HOME` definido y un emulador o tu teléfono | Saltártela: sin el SDK, el proyecto no compila |
| Saber | Programar en algún lenguaje y haber hecho una interfaz | No se supone Kotlin ni Compose |

**El proyecto de inicio.** Desde la raíz del repo de la serie:

```bash
git pull
cd lecciones/01-mi-primera-pantalla/inicio
```

Ahí trabajas. Junto está `solucion/`, con la pantalla resuelta: es para comparar al final, no para copiar.

**Lo que el proyecto ya trae (andamiaje).** No lo escribes tú y no se evalúa aquí, pero conviene saber por qué está:

| Qué trae | Dónde | Por qué |
|---|---|---|
| `org.gradle.jvmargs=-Xmx4g` | `gradle.properties` | Le da 4 GB de memoria a Gradle, la herramienta que compila. Sin ella, compilar con Firebase (lección 03) falló con «Java heap space» (medido el 13-nov-2026) |
| `gradlew` ya generado | raíz del proyecto | El guion que baja y corre la versión exacta de Gradle. Generarlo con `gradle wrapper` **dentro** de un proyecto con AGP 9.4 falló (medido el 13-nov). No lo regeneres |
| `sdk=36` | `app/src/test/resources/robolectric.properties` | Las pruebas simuladas usan una copia de Android 36. Sin fijarlo tomaban Android 37 y ninguna corrió (medido el 13-nov) |
| `jvmToolchain(25)` | `app/build.gradle.kts` | Con Java 17 las pruebas simuladas no arrancaron: Android 36 pidió Java 21 o más (medido el 13-nov) |

**El boceto.** Esto es lo que tiene que verse al terminar:

```text
┌────────────────────────────────┐
│                                │
│           La tienda            │  ← nombre, con letra grande
│  Tus órdenes, en tu bolsillo   │  ← lema
│          [ Entrar ]            │  ← botón
│            ¡Hola!              │  ← aparece solo después de tocar «Entrar»
│                                │
└────────────────────────────────┘
  Todo centrado, a media pantalla, con 24 dp de margen.
```

**dp** es la unidad de Android para tamaños: más o menos un pixel en una pantalla de densidad media, ajustado
para verse del mismo tamaño en pantallas más densas.

---

## Paso 1 · Lee una pantalla antes de correrla

Abre `app/src/main/java/mx/habil/ejemplo/tienda/Bienvenida.kt`. Así viene:

```kotlin
@Composable
fun Bienvenida() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Aquí va tu pantalla")
        Text("Lección 01")
    }
}
```

**Predice primero.** Crea el archivo `respuestas/01.md` en la raíz del repo y escribe, antes de correr nada, tres
líneas: ¿cuántos textos se van a ver?, ¿uno junto al otro o uno debajo del otro?, ¿pegados a la izquierda o
centrados?

Para predecir, lee la función de afuera hacia adentro:

- **`@Composable`** marca la función como una que describe pantalla.
- **`fun Bienvenida()`** es una función de Kotlin. No regresa nada: describe lo que debe verse.
- **`Column( … ) { … }`** acomoda lo que lleva dentro en secuencia vertical: uno debajo del otro.
- **`Text("…")`** muestra un texto. Cada llamada es un elemento.
- **`Modifier.fillMaxSize().padding(24.dp)`** dice que la columna ocupe todo el espacio que le den y deje 24 dp de
  margen.
- **`verticalArrangement = Arrangement.Center`** pone el contenido a media altura. De lo horizontal nadie dijo
  nada, y en una `Column` eso empieza, por omisión, en `Alignment.Start`: el lado donde empieza el texto.

**Ahora córrela.** Con el emulador abierto o tu teléfono conectado:

```bash
./gradlew installDebug      # compila e instala; no abre la app
```

Ábrela desde el cajón de apps y compara con tus tres líneas. Si una predicción falló, no la borres: escribe debajo
qué línea del código te habría dado la respuesta. [pendiente: captura de esta pantalla en el emulador]

**Segunda predicción.** ¿Y si quitas el `Column` y dejas los dos `Text` sueltos? Según la documentación de
Android, Compose encima un texto sobre el otro. Pruébalo, anota lo que ves y regresa el `Column`.

**¿Quién llama a `Bienvenida()`?** `MainActivity.kt`, en la misma carpeta: su línea `setContent { Bienvenida() }`
dice qué función dibuja la pantalla. No la cambias.

**Lo demuestra:** tu predicción escrita, comparada con la corrida (objetivo 1.1).

---

## Paso 2 · Kotlin: lo que ya conoces con otro nombre

Basta reconocer tres cosas que ya usas en otro lenguaje.

| En Kotlin | Qué es | Dónde lo ves |
|---|---|---|
| `val nombre = "La tienda"` | Una variable que se asigna una vez y ya no cambia | `val pantalla = …` en `BienvenidaTest.kt` |
| `var saludando = false` | Una variable que sí puede cambiar | La escribes en el paso 4 |
| `fun doble(n: Int): Int { return n * 2 }` | Una función: nombre, parámetros con su tipo y tipo de regreso | `fun Bienvenida()` |
| `fun doble(n: Int) = n * 2` | La misma, corta: el cuerpo es una expresión y el tipo de regreso se infiere | |
| `Bundle?` | Un tipo que **puede** ser nulo. Sin el `?`, no puede | `savedInstanceState: Bundle?` en `MainActivity.kt` |

Tres detalles que sí cambian respecto a otros lenguajes:

- **El tipo va después del nombre:** `n: Int`. En una variable casi nunca lo escribes: en `val x = 5`, Kotlin
  deduce que es `Int`.
- **Los nulos se declaran.** Una variable `String` no puede guardar `null`. Si puede faltar, el tipo es `String?`,
  y entonces `lema.length` marca error al compilar; `lema?.length` regresa `null` si `lema` es nulo; y
  `lema?.length ?: 0` regresa `0` en ese caso.
- **Las llaves al final son una función que le pasas a otra.** En `Column( … ) { … }`, lo que va entre llaves es
  una función sin nombre (una *lambda*). Si el último parámetro de una función es otra función, Kotlin deja
  escribirla afuera de los paréntesis. Por eso Compose se ve como bloques anidados.

**Lo demuestra:** el repaso 1.4, en «Tus ejercicios».

---

## Paso 3 · Escribe la bienvenida, a mano

Este paso es tuyo, sin Claude Code: son unas diez líneas.

**Predice primero.** ¿Cuántas llamadas a funciones composables vas a necesitar dentro del `Column`, y cuál de
ellas va a llevar otra adentro?

En `Bienvenida.kt`:

1. **Agrega tres líneas al bloque de `import`.** Un `import` le dice a Kotlin de qué librería sale cada nombre:

```kotlin
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
```

2. **Centra lo horizontal.** Pon una coma después de `Arrangement.Center` y, debajo, el parámetro que faltaba:

```kotlin
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
```

3. **Cambia los dos textos por los tres elementos del boceto:**

```kotlin
        Text("La tienda", style = MaterialTheme.typography.headlineLarge)
        Text("Tus órdenes, en tu bolsillo")
        Button(onClick = { }) {
            Text("Entrar")
        }
```

`style = MaterialTheme.typography.headlineLarge` le pone al nombre uno de los tamaños de letra de Material, el
juego de componentes visuales de Android. `Button` lleva dos cosas: `onClick`, la función que se llama cuando
alguien lo toca —por ahora vacía—, y entre llaves lo que se ve dentro del botón, que es otro `Text`.

**Corre el `verifica`.** Desde la raíz del repo:

```bash
./lecciones/01-mi-primera-pantalla/verifica
```

No cambia tu código: corre las pruebas simuladas (`./gradlew testDebugUnitTest`) y te traduce el resultado. La
primera vez necesita red y tarda, porque descarga la librería de pruebas (Robolectric) y una copia de Android: en
el ensayo del 13-nov fue 1 minuto 21 segundos en una Mac; las siguientes, segundos.

**Vas bien** si ves tres marcas en verde y una en rojo:

```text
✅ Simulado: la pantalla muestra el nombre «La tienda»
✅ Simulado: la pantalla muestra el lema «Tus órdenes, en tu bolsillo»
✅ Simulado: hay un botón «Entrar» que se puede tocar
❌ Al tocar «Entrar» no aparece «¡Hola!» (o ya estaba desde antes): ese cambio lo pides en el paso 4
Resultado: 3 de 4 pruebas en verde (1 en rojo, 0 con error, 0 saltadas), leído de …
```

Ese rojo es el esperado: el botón todavía no hace nada. También sale en rojo el ejercicio 1.3, que aún no escribes.

**Qué demuestra cada marca, y qué no.** «Simulado» significa que la prueba dibujó tu función en memoria y encontró
ese texto, y que el botón se puede tocar. No demuestra el tamaño de la letra, el margen ni el orden en que se ven
las cosas: eso lo ves tú, con `./gradlew installDebug` y el boceto al lado.

**Lo demuestra:** el `verifica` simulado encuentra los tres elementos (objetivo 1.2).

---

## Paso 4 · Con Claude Code: que el botón salude

Aquí entra la herramienta, y entra siempre igual: **predices → le pides → lees lo que generó → corres el
`verifica` → lo explicas con tus palabras.**

1. **Predice.** Escribe en `respuestas/01.md`: para que «¡Hola!» aparezca solo después de tocar el botón, ¿qué
   tiene que recordar la pantalla? ¿Qué línea de las que ya escribiste va a cambiar?
2. **Pídele** a Claude Code, parado en `inicio/`: «En `Bienvenida.kt`, haz que al tocar "Entrar" aparezca el texto
   "¡Hola!" debajo del botón. Antes de tocar no debe verse. No cambies nada más ni toques las pruebas.»
3. **Lee** el cambio completo antes de aceptarlo. No tiene que ser idéntico a esto, pero debe traer estas tres
   piezas:

```kotlin
    var saludando by remember { mutableStateOf(false) }      // 1. el estado
    …
        Button(onClick = { saludando = true }) {             // 2. el evento
            Text("Entrar")
        }
        if (saludando) {                                     // 3. la pantalla lee el estado
            Text("¡Hola!")
        }
```

4. **Corre el `verifica`.** Ahora las cuatro pruebas deben salir en verde.
5. **Explica**, en dos líneas de `respuestas/01.md`, qué pasa entre que tocas el botón y que aparece el saludo.

Para explicarlo necesitas saber qué hace cada pieza:

- **`mutableStateOf(false)`** crea un valor que Compose vigila. Cuando cambia, Compose vuelve a correr las funciones
  que lo leyeron. A eso se le llama **recomposición**.
- **`remember { … }`** guarda ese valor entre una corrida de la función y la siguiente.
- **`by`** deja usar `saludando` como una variable normal. Pide dos `import`: `androidx.compose.runtime.getValue` y
  `androidx.compose.runtime.setValue`.
- **`onClick = { saludando = true }`** es el evento: solo cambia el estado. No dibuja nada.
- **`if (saludando) { Text("¡Hola!") }`** es Kotlin normal dentro de la pantalla: si el estado es `true`, ese texto
  existe; si no, no.

La idea de la lección, completa: tocas → cambia el estado → Compose vuelve a correr la función → la función
describe una pantalla que ahora incluye el saludo. Nadie «agrega» el texto: la función se vuelve a leer.

**Si no puedes explicar una línea,** detente: pregúntale a Claude Code qué hace, compruébalo cambiándola y
corriendo el `verifica`, y sigue hasta que la explicación sea tuya.

---

## Paso 5 · Sin IA: explica cinco bloques y corrige uno

Este es el ejercicio 1.3 y se hace **sin Claude Code**, sobre tu propio `Bienvenida.kt`. Si el tuyo ya cumple el
boceto, usa para la corrección este listado, que es una respuesta posible a la misma petición:

```kotlin
19  @Composable
20  fun Bienvenida() {
21      var saludando by remember { mutableStateOf(false) }
22
23      Column(
24          modifier = Modifier.fillMaxSize().padding(24.dp),
25          verticalArrangement = Arrangement.Center,
26          horizontalAlignment = Alignment.CenterHorizontally
27      ) {
28          Text("La tienda", style = MaterialTheme.typography.headlineLarge)
29          Text("Tus órdenes, en tu bolsillo")
30          if (saludando) {
31              Text("¡Hola!")
32          }
33          Button(onClick = { saludando = true }) {
34              Text("Entrar")
35          }
36      }
37  }
```

Con este listado el `verifica` da 4 de 4 en verde (medido el 18-nov-2026). Y aun así no cumple el boceto.

**Haces dos cosas, por escrito, en `respuestas/01.md`:**

1. **Explica cinco bloques,** uno de cada tipo: **estado**, **texto**, **modificador**, **evento** y **función**.
   De cada uno escribe su línea con el formato `Bienvenida.kt:21` y qué cambia **en la pantalla** si lo quitas o lo
   alteras. «Guarda un booleano» no es un efecto visible; «sin esta línea el saludo nunca aparece» sí.
2. **Corrige el bloque que no cumple el boceto.** Di cuál es y por qué las pruebas no lo detectan, arréglalo,
   corre el `verifica` y vuelve a instalar la app.

El `verifica` solo revisa que haya cinco bloques ligados a su línea; la explicación la comparas tú al final.

**Lo demuestra:** ejercicio 1.3 (objetivo 1.3).

---

## Tus ejercicios

El 1.2 y el 1.3 se hacen **sin IA**.

| # | Haces | Está hecho cuando |
|---|---|---|
| 1.1 | Predices qué dibuja la función del paso 1 y la corres | Tienes tus tres líneas y lo que viste |
| 1.2 | Escribes nombre, lema y botón a partir del boceto | El `verifica` marca en verde los tres elementos |
| 1.3 | Explicas cinco bloques y corriges el que no cumple el boceto | Cada bloque tiene su `Bienvenida.kt:N` y su efecto visible; el saludo queda debajo del botón; el `verifica` sigue en verde |
| 1.4 | Contestas el repaso de Kotlin | Cuatro de cinco correctas, **cada una con su porqué** |

**Repaso 1.4.** Contesta sin correr el código; después compruébalo si quieres.

1. `val nombre = "La tienda"` y, en la línea siguiente, `nombre = "Otra"`. ¿Compila?
2. `var saludando = false`. Nadie escribió el tipo. ¿Cuál es y quién lo decidió?
3. `fun doble(n: Int) = n * 2`. ¿Qué regresa `doble(4)` y de qué tipo es?
4. En `MainActivity.kt` dice `savedInstanceState: Bundle?`. ¿Qué te avisa ese `?`?
5. `val lema: String? = null`. ¿Qué pasa con `lema.length`, con `lema?.length` y con `lema?.length ?: 0`?

**Tiempo:** [pendiente: medir el tiempo de la lección con un alumno]

---

## ¿No te corre?

Los mensajes en inglés son los que dio el compilador de la serie (Kotlin 2.4.21) el 18-nov-2026.

| Ves | Causa probable | Qué haces |
|---|---|---|
| `Unresolved reference 'Button'` | Falta el `import` de ese nombre, o está mal escrito | Agrega el `import` del paso 3 y revisa mayúsculas |
| `'val' cannot be reassigned` | Declaraste el estado con `val` y luego lo cambias | Es `var saludando by remember { … }` |
| `Property delegate must have a 'getValue(…)' method` | Usaste `by` sin sus dos `import` | Agrega `getValue` y `setValue` (paso 4) |
| «Las pruebas no llegaron a correr» | El código no compila | Lee la línea que empieza con `e:`: trae archivo, línea y columna |
| ❌ en el botón, con «Entrar» visible | Lo escribiste como `Text("Entrar")` suelto | Tiene que ser un `Button` con el `Text` adentro |
| ❌ en el saludo, aunque se ve «¡Hola!» | El saludo está siempre, no solo después de tocar | Va dentro del `if (saludando)` |
| `BienvenidaTest.kt` cambió | Alguien (tú o Claude Code) tocó las pruebas | Recupéralo con el comando que da el mensaje y repite la petición pidiendo que no las toque |

En Ubuntu, esta lección está [pendiente: medir en Ubuntu].

---

## Para cerrar

**Contesta sin volver a leer:**

1. ¿Qué es una pantalla en Compose, en una frase?
2. Tocas «Entrar» y aparece «¡Hola!». ¿Qué pasó en medio, en orden?
3. Tus cuatro pruebas simuladas están en verde. Di una cosa que eso demuestra y una que no.

**Lo que sigue:** en la L02 la app tiene más de una pantalla y pasa de una a otra.

---

## Compara tus respuestas

**Paso 1.** Dos textos, uno debajo del otro (`Column`), a media altura (`Arrangement.Center`) y del lado izquierdo
(nadie indicó la alineación horizontal).

**Paso 3.** Cuatro llamadas: `Text`, `Text`, `Button` y, dentro del botón, otro `Text`.

**Ejercicio 1.3**, sobre el listado del paso 5:

| Bloque | Línea | Efecto visible |
|---|---|---|
| Estado | `Bienvenida.kt:21` | Sin ella el saludo no puede aparecer después del toque |
| Texto | `Bienvenida.kt:28` | Es el nombre; sin `style` se vería del mismo tamaño que el lema |
| Modificador | `Bienvenida.kt:24` | Sin `fillMaxSize()` la columna solo ocupa lo que mide su contenido: ya no hay espacio donde centrar |
| Evento | `Bienvenida.kt:33` | Con `onClick = { }` el botón se puede tocar, pero no pasa nada |
| Función | `Bienvenida.kt:20` | Es la pantalla completa; `MainActivity` la llama |

El bloque que no cumple es el de las líneas 30 a 32: está **antes** del botón, así que el saludo sale arriba de
«Entrar» y el boceto lo pide abajo. Se corrige moviendo esas tres líneas después de la 35. Las pruebas no lo
detectan porque buscan que el texto exista y se vea, no en qué posición está.

**Repaso 1.4:**

1. No. Un `val` no se puede reasignar; el compilador dice `'val' cannot be reassigned`.
2. `Boolean`. Lo dedujo Kotlin a partir del valor `false`.
3. Regresa `8`, de tipo `Int`: el cuerpo es una expresión y el tipo de regreso se infiere.
4. Que ese parámetro puede llegar en `null`, y Kotlin no te deja usarlo sin considerar ese caso.
5. `lema.length` no compila; `lema?.length` da `null`; `lema?.length ?: 0` da `0`.

**Para cerrar:**

1. Una función marcada con `@Composable` que describe lo que se ve.
2. El `onClick` cambió el estado a `true`; Compose volvió a correr la función; el `if` ahora sí incluye el texto.
3. Demuestra que los tres elementos y el saludo existen y que el botón responde. No demuestra cómo se ven ni en qué
   orden están.

---

## Fuentes de la lección

Abiertas el 18-nov-2026.

- Android Developers. *Thinking in Compose*. https://developer.android.com/develop/ui/compose/mental-model
- Android Developers. *Jetpack Compose Tutorial*. https://developer.android.com/develop/ui/compose/tutorial
- Android Developers. *Compose layout basics*. https://developer.android.com/develop/ui/compose/layouts/basics
- Android Developers. *Column* (valores por omisión). https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/Column.composable
- Android Developers. *Compose modifiers*. https://developer.android.com/develop/ui/compose/modifiers
- Android Developers. *State and Jetpack Compose*. https://developer.android.com/develop/ui/compose/state
- Android Developers. *Button*. https://developer.android.com/develop/ui/compose/components/button
- Android Developers. *Style text* y *Material Design 3 in Compose*. https://developer.android.com/develop/ui/compose/text/style-text · https://developer.android.com/develop/ui/compose/designsystems/material3
- Android Developers. *Support different pixel densities* (dp). https://developer.android.com/training/multiscreen/screendensities
- Android Developers. *Test your Compose layout*. https://developer.android.com/develop/ui/compose/testing
- Android Developers. *Robolectric strategies*. https://developer.android.com/training/testing/local-tests/robolectric
- Kotlin. *Basic syntax*. https://kotlinlang.org/docs/basic-syntax.html
- Kotlin. *Null safety*. https://kotlinlang.org/docs/null-safety.html
- Kotlin. *Higher-order functions and lambdas*. https://kotlinlang.org/docs/lambdas.html
- Medición propia: ensayo en Mac con Apple silicon, 13-nov-2026 (memoria de Gradle, `gradle wrapper`, Java y Android de las pruebas simuladas) y 18-nov-2026 (el `verifica` sobre `inicio/`, `solucion/` y el listado del 1.3; mensajes del compilador).
