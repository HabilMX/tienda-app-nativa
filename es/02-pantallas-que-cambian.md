# Lección 02 — Pantallas que cambian

**Antes de leer, siete palabras.** Una línea cada una; las demás de la serie llegan en la lección donde se usan.

- **Estado:** un dato que la pantalla recuerda y que puede cambiar. Lo conociste en la 01; aquí es el tema.
- **Recomposición:** cuando Compose vuelve a ejecutar tus funciones para actualizar la pantalla porque un dato
  cambió.
- **Evento:** algo que pasa y de lo que la app se entera, como un toque en un botón.
- **Elevar el estado:** sacar un estado de una pantalla y guardarlo en la función que la llama.
- **Interfaz:** una lista de lo que se le puede pedir a algo, sin decir cómo lo resuelve.
- **Implementación:** una clase que sí resuelve lo que la interfaz pide. Puede haber varias.
- **Doble de prueba:** una implementación hecha solo para las pruebas, que responde lo que la prueba necesita.

**Qué te llevas:** una app con tres pantallas —bienvenida, entrada e inicio— que pasa de una a otra, y saber decir
por qué cambió la pantalla cada vez: qué valor cambió, quién lo cambió y quién lo leyó.

**Idea nueva de esta lección:** el estado. Cuando cambia un valor de estado que la pantalla leyó, Compose
**programa una recomposición** para actualizar lo necesario, y la pantalla se dibuja de nuevo. Pasar de una
pantalla a otra es eso mismo: un valor que cambia. Además practicas una cosa de apoyo, que también se comprueba:
separar la decisión de entrar en un contrato, con una implementación solo para las pruebas (paso 3).

---

## La escena

Tienes las tres pantallas funcionando. Antes de cerrar, repasas el archivo para dejarlo limpio y una palabra te
estorba: `remember`. La línea se lee igual sin ella. La borras.

Compila. Abres la app: la bienvenida se ve idéntica. Tocas «Entrar».

No pasa nada. No hay mensaje de error. La bienvenida sigue ahí. Tocas otra vez. Nada.

Ese cambio existe y está medido: es el «Rompe uno a propósito» del paso 4. Con esa sola palabra borrada, las
pruebas simuladas de la lección pasan de 7 de 7 en verde a 1 de 7, con 6 en rojo y ninguna con error (18 de
noviembre de 2026). Nada tronó. La pantalla dejó de cambiar, y el código no dice por qué.

La respuesta está en una pregunta: cuando tocas un botón, ¿dónde queda guardado lo que pasó? Primero predices qué
hace un valor de estado, luego construyes el recorrido con él, y al final lo rompes tú para explicarlo.

---

## Antes de empezar

**Qué necesitas:** la [01](01-mi-primera-pantalla.md) terminada —aquí no se vuelve a explicar `Column`, `Text` ni
`Button`— y el ambiente de la [00](00-ambiente-listo.md): `ANDROID_HOME` definido y un emulador o tu teléfono.

**El proyecto de inicio.** Desde la raíz del repo de la serie:

```bash
git pull
cd lecciones/02-pantallas-que-cambian/inicio
```

Ahí trabajas. Junto está `solucion/`: te sirve al final para contrastar tus decisiones y entender cualquier diferencia. Tus predicciones y explicaciones
van en `respuestas/02.md`, en la raíz del repo (`mkdir -p respuestas` si no existe).

**Lo que el proyecto ya trae (andamiaje).** No se evalúa aquí:

| Qué trae | Dónde | Por qué |
|---|---|---|
| La bienvenida de la 01 | `Bienvenida.kt` | Partes de lo que ya escribiste |
| Las pruebas | `app/src/test/…/RecorridoTest.kt` y `ArranqueTest.kt` | Son las que corre el `verifica`. No las cambies |
| Memoria, `gradlew`, Android 36 para las pruebas y Java 25 | Igual que en la 01 | Se explicó en la [01](01-mi-primera-pantalla.md) |

**La navegación también es andamiaje.** Android tiene una librería para moverse entre pantallas, Navigation. Aquí
no se usa: el recorrido se arma con un valor de estado y un `when`, que te doy escritos en el paso 2.

**El boceto.** Tres pantallas, una a la vez:

```text
┌──────────────────────┐    ┌──────────────────────┐    ┌──────────────────────┐
│      La tienda       │    │  Entra a tu cuenta   │    │      Hola, Ana       │
│ Tus órdenes, en tu   │ ─▶ │    [ Continuar ]     │ ─▶ │ Aquí verás tus       │
│      bolsillo        │    │ Todavía no se puede  │    │ órdenes              │
│     [ Entrar ]       │    │ entrar (si no pasó)  │    │                      │
└──────────────────────┘    └──────────────────────┘    └──────────────────────┘
      bienvenida                   entrada                      inicio
```

De la entrada al inicio se pasa tocando «Continuar», **solo si alguien decide que puedes pasar**. Quién decide es
el paso 3.

---

## Paso 1 · Predice qué hace un valor de estado

Este paso es tuyo, sin Claude Code. Abre `app/src/main/java/mx/habil/ejemplo/tienda/Bienvenida.kt`. Es la pantalla
de la 01, con estas tres piezas:

```kotlin
    var saludando by remember { mutableStateOf(false) }      // el estado
    …
        Button(onClick = { saludando = true }) {             // el evento lo cambia
            Text("Entrar")
        }
        if (saludando) {                                     // la pantalla lo lee
            Text("¡Hola!")
        }
```

**Predice primero.** Cambia `saludando = true` por `saludando = !saludando` (el `!` invierte: de `false` a `true`
y de regreso). Escribe en `respuestas/02.md`, antes de correr nada: ¿qué se ve después del primer toque, del
segundo y del tercero?

**Ahora compruébalo.** Con el emulador abierto o tu teléfono conectado:

```bash
./gradlew installDebug      # compila e instala; no abre la app
```

Abre la app, toca tres veces y compara. Si tu predicción falló, no la borres: anota debajo qué línea te habría
dado la respuesta.

**Qué pasó, en orden.** Es la cadena de toda la lección:

1. Tocas el botón: ese es el **evento**.
2. El `onClick` cambia el valor. No dibuja nada.
3. Compose vigila ese valor porque nació de `mutableStateOf`. Al cambiar, **programa una recomposición**: volver a
   ejecutar lo que lo leyó y haga falta actualizar.
4. En este ejemplo, tu función corre otra vez. `remember` le regresa el valor guardado, no uno nuevo.
5. El `if` lee el valor de ahora, y la descripción de la pantalla sale distinta.

No escribas código suponiendo cuántas veces ni en qué orden se ejecutará una función `@Composable`: según la
documentación, Compose puede saltarse las que no cambiaron y ejecutar otras muchas veces.

Nadie «agrega» ni «quita» el saludo: la función describe la pantalla para el valor de ese momento.

Antes de seguir, regresa a mano la línea del botón a `Button(onClick = { saludando = true })`. Así conservas
cualquier otro cambio que hayas hecho. Para confirmar que el archivo quedó como venía, este comando no debe
mostrar nada (solo mira, no cambia tu archivo):

```bash
git diff -- app/src/main/java/mx/habil/ejemplo/tienda/Bienvenida.kt
```

**Lo demuestra:** tu predicción escrita, comparada con la corrida (objetivo 2.1).

---

## Paso 2 · A mano: un valor decide qué pantalla se ve

También sin Claude Code. Vas a escribir dos pantallas y la función que elige cuál se dibuja.

**Predice primero.** Si «en qué pantalla estoy» es un valor de estado, ¿en cuál función debe vivir: en
`Bienvenida`, en la pantalla de entrada o en una tercera que llame a las dos? Escribe cuál y por qué.

**1. La bienvenida deja de guardar estado y avisa.** En `Bienvenida.kt`, borra la línea de `saludando`, el bloque
`if (saludando) { … }` y los cuatro `import` de `androidx.compose.runtime` que no sean `Composable`. Cambia dos
líneas:

```kotlin
fun Bienvenida(alEntrar: () -> Unit) {
    …
        Button(onClick = alEntrar) {
```

`alEntrar: () -> Unit` es un parámetro cuyo valor es una función: no recibe nada (`()`) y no regresa un dato
(`Unit`). La pantalla la ejecuta al tocar el botón, y no sabe qué pasa después.

**2. La pantalla de entrada.** Crea `PantallaEntrada.kt` en la misma carpeta. Empieza con `package
mx.habil.ejemplo.tienda` y los mismos `import` que `Bienvenida.kt`:

```kotlin
@Composable
fun PantallaEntrada(rechazada: Boolean, alContinuar: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Entra a tu cuenta", style = MaterialTheme.typography.headlineLarge)
        Button(onClick = alContinuar) {
            Text("Continuar")
        }
        if (rechazada) {
            Text("Todavía no se puede entrar")
        }
    }
}
```

Tampoco guarda nada: recibe un dato (`rechazada`) y avisa de un toque (`alContinuar`).

**3. La función que guarda el estado.** Crea `Tienda.kt`:

```kotlin
package mx.habil.ejemplo.tienda

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

enum class Pantalla { BIENVENIDA, ENTRADA, INICIO }

@Composable
fun Tienda() {
    var pantalla by remember { mutableStateOf(Pantalla.BIENVENIDA) }

    when (pantalla) {
        Pantalla.BIENVENIDA -> Bienvenida(alEntrar = { pantalla = Pantalla.ENTRADA })
        Pantalla.ENTRADA -> PantallaEntrada(rechazada = false, alContinuar = { })
        Pantalla.INICIO -> Text("Aquí va el inicio")
    }
}
```

- **`enum class Pantalla { … }`** es una lista cerrada: una `Pantalla` solo puede ser una de esas tres.
- **`when (pantalla) { … }`** ejecuta la rama de ese valor. Si te falta una de las tres, no compila.
- **`alEntrar = { pantalla = Pantalla.ENTRADA }`** es la función que le pasas a la bienvenida. Ella avisa; aquí se
  cambia el estado.

**4. Que la app arranque en `Tienda`.** En `MainActivity.kt`, la línea de `setContent` queda
`setContent { Tienda() }`.

**Compruébalo en la app.** `./gradlew installDebug`, ábrela y toca «Entrar»: debe quedar la pantalla «Entra a tu
cuenta» y la bienvenida ya no debe verse. «Continuar» todavía no hace nada.
Con la solución terminada, en el emulador de la prueba (Pixel 9, Android 37, 18-nov-2026) se vio esto: la bienvenida
con «La tienda», «Tus órdenes, en tu bolsillo» y «Entrar»; al tocar «Entrar», «Entra a tu cuenta» y «Continuar»; y al
tocar «Continuar», el aviso «Todavía no se puede entrar». La pantalla de inicio no aparece en la app instalada: solo
llegan a ella las pruebas.

**Qué acabas de hacer.** El estado no quedó en ninguna pantalla: lo subiste a la función que las llama. Eso es
**elevar el estado**, y la documentación de Android lo recomienda: el estado vive en el ancestro común más cercano
de quienes lo leen y lo cambian; hacia abajo viajan datos, hacia arriba viajan eventos.

**El `verifica` todavía no puede correr las pruebas.** Desde la raíz del repo:

```bash
./lecciones/02-pantallas-que-cambian/verifica
```

Este rojo es el esperado **solo** al terminar el paso 2, y solo porque todavía faltan `Entrada`, `EntradaFalsa` y
el parámetro `entrada` de `Tienda`. **Vas bien** si entre las líneas ves estas (medido el 18-nov-2026; las de
Gradle se repiten varias veces):

```text
❌ No encontré «interface Entrada» en src/main: sin ella las pruebas no compilan (paso 3)
✅ Texto: en src/main hay un «fun Tienda(»
❌ No encontré «class EntradaFalsa» en src/test: la escribes tú en EntradaFalsa.kt (paso 3)
ℹ️  Estado guiado del paso 2: arriba faltan piezas del paso 3, y mientras falten las pruebas no compilan.
❌ Las pruebas no llegaron a correr. Esto dijo Gradle:
     e: RecorridoTest.kt:45:38 No parameter with name 'entrada' found.
     e: RecorridoTest.kt:45:48 Unresolved reference 'EntradaFalsa'.
Resultado: 0 pruebas corridas.
```

Si las líneas de Gradle mencionan otro archivo —`Tienda.kt`, `Bienvenida.kt`, `PantallaEntrada.kt`—, o si ya
empezaste el paso 3, no es este rojo: es un error que hay que corregir antes de continuar. Por ejemplo, con
`recuerda` escrito en lugar de `remember`, Gradle dijo `e: Tienda.kt:14:21 Unresolved reference 'recuerda'.`
(medido el 18-nov-2026). Y como las pruebas aún no corren, este paso se comprueba en la app instalada.

**Lo demuestra:** en la app instalada, «Entrar» te lleva a la pantalla de entrada (primera mitad del objetivo
2.2).

---

## Paso 3 · Con Claude Code: quién decide si entras

Falta el inicio, y falta decidir quién puede llegar a él. Hoy no hay proveedor (Google llega en la lección 04), y
aun así las pruebas necesitan recorrer las tres pantallas.

Por eso esa decisión no se escribe dentro de ninguna pantalla. Se declara aparte, como **interfaz**: «a esto se le
puede pedir entrar, y contesta con el nombre de quien entró o con nada». Y se escriben dos **implementaciones**:

| Implementación | Dónde vive | Qué contesta | Quién la usa |
|---|---|---|---|
| `EntradaSinProveedor` | `app/src/main` (la app) | Siempre «no»: `null` | La app instalada |
| `EntradaFalsa` | `app/src/test` (las pruebas) | Siempre «sí», con el nombre que le des | Solo las pruebas |

`EntradaFalsa` es un **doble de prueba**. Para que la app real no deje pasar a nadie sin proveedor, mantenemos
esta regla: **la app no trae ninguna forma de entrar sin proveedor.** Según la documentación de Android, un doble
que vive en el código de pruebas no viaja con la app.

Aquí entra la herramienta, y entra siempre igual: **predices → le pides → lees lo que generó → corres el
`verifica` → lo explicas con tus palabras.**

1. **Predice.** Escribe en `respuestas/02.md`: de los archivos `Bienvenida.kt`, `PantallaEntrada.kt`, `Tienda.kt` y
   `MainActivity.kt`, ¿cuáles van a cambiar y cuáles no?
2. **Pídele** a Claude Code, parado en `inicio/`: «Crea `Entrada.kt` con una interfaz `Entrada` que tenga
   `fun entrar(): String?` y una clase `EntradaSinProveedor` que regrese `null`. Crea `Inicio.kt` con un
   composable `Inicio(nombre: String)` que muestre "Hola, " seguido del nombre y, debajo, "Aquí verás tus
   órdenes". Haz que `Tienda` reciba `entrada: Entrada`: al tocar "Continuar" llama a `entrada.entrar()`; si
   regresa un nombre, pasa al inicio con ese nombre; si regresa `null`, se queda y pone `rechazada` en `true`.
   En `MainActivity` usa `EntradaSinProveedor()`. En `app/src/test`, junto a `RecorridoTest.kt`, crea
   `EntradaFalsa.kt`: recibe un nombre, lo regresa siempre y cuenta en `veces` cuántas veces le pidieron entrar.
   No cambies `RecorridoTest.kt` ni las dos pantallas que ya existen.»
3. **Lee** el cambio completo antes de aceptarlo. El cuerpo de las funciones puede variar. Lo que las pruebas
   llaman por su nombre sí es parte del contrato de esta lección y tiene que quedar tal cual: `Entrada` con
   `fun entrar(): String?`, `EntradaSinProveedor()`, `Tienda(entrada = …)`, `EntradaFalsa(nombre)` con su
   contador `veces`, y los textos de las pantallas. Con otros nombres tu diseño puede ser igual de bueno, pero
   estas pruebas no compilan. Debe traer estas cuatro piezas:

```kotlin
// Entrada.kt — el contrato y la implementación de la app
interface Entrada {
    fun entrar(): String?            // el nombre de quien entró, o null si no pudo
}

class EntradaSinProveedor : Entrada {
    override fun entrar(): String? = null
}
```

```kotlin
// EntradaFalsa.kt — en app/src/test, no en app/src/main
class EntradaFalsa(private val nombre: String) : Entrada {
    var veces = 0

    override fun entrar(): String? {
        veces++
        return nombre
    }
}
```

```kotlin
// Tienda.kt — recibe la entrada y guarda tres valores de estado
@Composable
fun Tienda(entrada: Entrada) {
    var pantalla by remember { mutableStateOf(Pantalla.BIENVENIDA) }
    var nombre by remember { mutableStateOf<String?>(null) }
    var rechazada by remember { mutableStateOf(false) }

    when (pantalla) {
        Pantalla.BIENVENIDA -> Bienvenida(alEntrar = { pantalla = Pantalla.ENTRADA })
        Pantalla.ENTRADA -> PantallaEntrada(
            rechazada = rechazada,
            alContinuar = {
                val quien = entrada.entrar()
                if (quien != null) {
                    nombre = quien
                    pantalla = Pantalla.INICIO
                } else {
                    rechazada = true
                }
            }
        )
        Pantalla.INICIO -> Inicio(nombre = nombre ?: "")
    }
}
```

```kotlin
// MainActivity.kt — el único lugar de la app que nombra una implementación
        setContent { Tienda(entrada = EntradaSinProveedor()) }
```

   Cuatro cosas que revisar al leer:

   - **`: Entrada`** después del nombre de la clase dice «esta clase cumple esa interfaz», y **`override`** marca
     la función que la cumple. Sin `override`, no compila.
   - **`entrada.entrar()` está dentro de `alContinuar`**, o sea, dentro del evento. Suelta en el cuerpo de
     `Tienda` se ejecutaría cada vez que la función se dibuja, y Compose puede ejecutarla muchas veces.
   - **`Tienda` no dice cuál entrada le tocó.** Solo conoce la interfaz.
   - En `Inicio.kt` vas a ver `"Hola, $nombre"`: el signo `$` mete el valor de la variable dentro del texto.

4. **Corre el `verifica`.** Ahora sí corren las pruebas: siete marcas «Simulado» en verde —seis del recorrido y
   una del arranque— y la línea `Resultado: 7 de 7 pruebas en verde (0 en rojo, 0 con error, 0 saltadas)`. Queda
   una marca en rojo, la de `respuestas/02.md`: se llena en el paso 4.
5. **Explica**, en tres líneas de `respuestas/02.md`: qué pasa entre que tocas «Continuar» y que aparece «Hola,
   Ana», nombrando los valores de estado que cambian.

**Muestra que cambiar la entrada no toca las pantallas.** Las pruebas ya usaron `EntradaFalsa("Ana")`,
`EntradaFalsa("Luis")` y `EntradaSinProveedor()` con las mismas tres pantallas. Ahora enséñalo tú. Parado en
`inicio/`, corre esto y pega la salida en `respuestas/02.md`:

```bash
grep -rlE "EntradaSinProveedor|EntradaFalsa" app/src | sort
```

En `solucion/` la salida son cuatro archivos (medido el 18-nov-2026): `Entrada.kt` y `MainActivity.kt` en
`src/main`, y `EntradaFalsa.kt` y `RecorridoTest.kt` en `src/test`. Ninguna pantalla aparece. Escribe debajo qué
archivos tocarías para estrenar otra forma de entrar, y cuáles no.

**Qué demuestra cada marca, y qué no.**

- **«Simulado», las seis del recorrido:** dibujaron `Tienda` en memoria con la entrada que la prueba escogió,
  tocaron los botones y buscaron los textos. Demuestran qué pantalla queda después de cada toque, con dos
  implementaciones.
- **«Simulado», la del arranque:** abrió `MainActivity`, la clase que Android abre al tocar el ícono, y tocó
  «Entrar» y «Continuar». Demuestra que por ese camino la app no deja pasar.
- **«Texto»:** solo buscó palabras en tus archivos, sin contar los comentarios. No demuestra que el código haga
  lo que esas palabras sugieren.

Ninguna demuestra que no exista **otra** forma de entrar sin proveedor: una segunda entrada con otro nombre en
`src/main`, usada desde otro lugar, pasa en verde. Eso lo revisas tú: lee `MainActivity.kt` y confirma que llama a
`Tienda(entrada = EntradaSinProveedor())`, y que en `src/main` no hay otra clase que cumpla `Entrada`. Tampoco
demuestran cómo se ven las pantallas, qué hace el botón «atrás» ni nada de Google.

**En la app instalada** llegas hasta la pantalla de entrada, y «Continuar» debe contestar «Todavía no se puede
entrar»: la app usa `EntradaSinProveedor`. Al inicio solo llegan las pruebas.

**Si una línea todavía no te queda clara,** antes de seguir intenta explicarla con tus palabras. Si aún no sale,
vuelve a ese fragmento o pide una aclaración.

**Lo demuestra:** el `verifica` simulado recorre las tres pantallas (objetivo 2.2) y lo hace con dos
implementaciones de `Entrada`, más tu salida de `grep` y tu lectura de `MainActivity.kt` (objetivo 2.4).

---

## Paso 4 · Sin IA: rompe uno a propósito

Este es el ejercicio 2.3 y se hace **sin Claude Code**. Con las siete pruebas en verde, vas a provocar la escena
del principio.

**1. Predice.** En `Tienda.kt`, la línea del estado va a quedar así, sin `remember`:

```kotlin
    var pantalla by mutableStateOf(Pantalla.BIENVENIDA)
```

Escribe en `respuestas/02.md`: ¿compila?, ¿qué se ve al abrir la app?, ¿qué pasa al tocar «Entrar»?

**2. Rómpelo y mide.** Haz el cambio y corre el `verifica`. Esto es lo que dio el 18-nov-2026:

```text
✅ Simulado: la app empieza en la bienvenida, con «La tienda» y el botón «Entrar», y sin nada de las otras dos
❌ Al tocar el botón «Entrar» no aparece la pantalla de entrada con «Entra a tu cuenta» y un botón «Continuar» …
❌ conLaEntradaFalsaLlegaAlInicio quedó en rojo, pero depende de la marca anterior …
   (y cuatro más iguales)
Resultado: 1 de 7 pruebas en verde (6 en rojo, 0 con error, 0 saltadas), leído de …
```

Copia a `respuestas/02.md` la línea roja que empieza con «Al tocar el botón «Entrar» no aparece la pantalla de
entrada». Luego instala la app y míralo tú.

**3. Explica por qué**, con tus palabras, antes de leer lo que sigue.

Lo que dice la documentación de Android: `remember` guarda un valor en la memoria de la pantalla y, en cada
recomposición, regresa **ese mismo** valor. Sin `remember`, la línea vuelve a ejecutarse completa. La cadena,
deducida de esa documentación (el `verifica` mide el resultado, no cada paso), queda así:

1. Tocas «Entrar». El evento cambia el valor a `ENTRADA`.
2. Compose se entera, porque el valor sigue naciendo de `mutableStateOf`, y vuelve a ejecutar `Tienda`.
3. `Tienda` corre desde arriba y la primera línea crea un valor **nuevo**, en `BIENVENIDA`.
4. El `when` lee ese valor nuevo. Se dibuja la bienvenida otra vez.

El valor sí cambió. Lo que se perdió fue el lugar donde se guardaba.

**4. Otra forma de romperlo.** Deja la línea como una variable común: `var pantalla = Pantalla.BIENVENIDA`. El
`verifica` da el mismo 1 de 7 (medido el 18-nov-2026), por otra razón: según la documentación, Compose solo se
entera de los cambios en valores que puede observar. El valor cambia y nadie vuelve a dibujar.

**5. Arréglalo.** Regresa la línea a `var pantalla by remember { mutableStateOf(Pantalla.BIENVENIDA) }` y corre el
`verifica`: siete de siete, y ahora también la marca de `respuestas/02.md`.

El `verifica` solo comprueba que pegaste la línea roja: no sabe si lo rompiste ni califica tu explicación. El 2.3
está terminado cuando puedes marcar las cuatro:

- [ ] Tu predicción del punto 1 está escrita, con lo que viste debajo.
- [ ] Pegaste la línea roja y el «Resultado» que te dio a ti.
- [ ] Explicaste, paso por paso, por qué la pantalla deja de actualizarse sin `remember`.
- [ ] Dijiste en qué se parece y en qué no la segunda forma de romperlo.

**Un límite.** Según la documentación de Android, `remember` no conserva el valor en un cambio de configuración,
como girar el teléfono; para eso existe `rememberSaveable`. Esta lección no lo usa ni lo prueba.
En el emulador de la prueba (18-nov-2026), al girar el teléfono estando en «Entra a tu cuenta», la app regresó a la
bienvenida: el estado se perdió, como dice la documentación. Y «atrás», desde cualquiera de las dos pantallas, cerró la
app y regresó al inicio del teléfono: el recorrido de esta lección no maneja «atrás».

**Lo demuestra:** «Rompe uno a propósito» (objetivo 2.3).

---

## Tus ejercicios

El 2.1, el paso 2 y el 2.3 se hacen **sin IA**.

| # | Haces | Está hecho cuando |
|---|---|---|
| 2.1 | Predices qué hace el estado del paso 1 y lo corres | Tienes tu predicción y lo que viste |
| 2.2 | Construyes bienvenida → entrada → inicio | El `verifica` da 7 de 7 **y** en la app instalada viste «Todavía no se puede entrar» |
| 2.3 | Rompes el estado y explicas por qué la pantalla deja de actualizarse | Tu evidencia son las cuatro casillas del paso 4, escritas por ti. El `verifica` solo confirma que el proyecto quedó reparado (7 de 7) y que pegaste el rojo |
| 2.4 | Separas la decisión de entrar en `Entrada` y muestras que cambiarla no toca las pantallas | Las pruebas con las dos implementaciones y la del arranque están en verde, pegaste tu salida de `grep` **y** leíste que `src/main` no trae otra entrada |

**Tiempo:** [pendiente: medir el tiempo de la lección con un alumno]

---

## ¿No te corre?

Los mensajes en inglés son los que dio el compilador de la serie (Kotlin 2.4.21) el 18-nov-2026.

| Ves | Causa probable | Qué haces |
|---|---|---|
| `No value passed for parameter 'alEntrar'` | Alguien llama a `Bienvenida()` sin la función | Pásasela: `Bienvenida(alEntrar = { … })` |
| `'when' expression must be exhaustive. Add the 'INICIO' branch or an 'else' branch.` | Al `when` le falta una de las tres pantallas | Agrega la rama que dice el mensaje |
| `'entrar' hides member of supertype 'Entrada' and needs an 'override' modifier.` | La clase cumple la interfaz sin decirlo | Escribe `override fun entrar()` |
| `Smart cast to 'String' is impossible, because 'nombre' is a delegated property.` | Preguntaste `if (nombre != null)` sobre un estado y lo usaste como `String` | Guarda primero el resultado en un `val`, como `val quien = entrada.entrar()` |
| ❌ «EntradaFalsa aparece en src/main» | La entrada que deja pasar quedó dentro de la app | Muévela a `app/src/test/java/mx/habil/ejemplo/tienda/` |
| ❌ en «solo al tocar Continuar» | `entrada.entrar()` quedó fuera del evento | Va dentro de `alContinuar = { … }` |
| `RecorridoTest.kt` o `ArranqueTest.kt` cambió | Alguien (tú o Claude Code) tocó las pruebas | Mira qué cambió con el `git diff` que da el mensaje y deshazlo a mano. Restaurar el archivo con git descarta todo lo que tenga: hazlo solo después de revisar ese diff. Luego repite la petición pidiendo que no las toque |

En Linux Mint 22.3 (x86_64), el `verifica` de esta lección salió en verde sobre la solución: 7 de 7 (18-nov-2026).

---

## Para cerrar

**Contesta sin volver a leer:**

1. Tocas «Entrar» y cambia la pantalla. ¿Qué pasó en medio, en orden?
2. ¿Qué hace `remember`, y qué se ve en la app si lo quitas?
3. Tus siete pruebas simuladas están en verde. Di una cosa que eso demuestra y una que no.
4. ¿Por qué `EntradaFalsa` vive en `src/test` y no en `src/main`?

**Lo que sigue:** en la L03 la app conoce a Firebase, y en la L04 entras con Google.

---

## Compara tus respuestas

**Paso 1.** Tras el primer toque se ve «¡Hola!», tras el segundo desaparece y tras el tercero vuelve (medido el
18-nov-2026 con una prueba simulada).

**Paso 2.** En una tercera función que llame a las dos, `Tienda`. Si el valor viviera en `Bienvenida`, la pantalla
de entrada no podría leerlo, y al dejar de dibujarse la bienvenida se perdería con ella.

**Paso 3.** Cambian `Tienda.kt` y `MainActivity.kt`; `Bienvenida.kt` y `PantallaEntrada.kt` no: ya solo recibían
datos y avisaban de toques. Entre el toque y «Hola, Ana»: `alContinuar` llama a `entrada.entrar()`, que regresa
`"Ana"`; se guarda en `nombre` y `pantalla` pasa a `INICIO`; Compose vuelve a ejecutar `Tienda` y el `when` elige
`Inicio`. Para estrenar otra forma de entrar, con lo que hay en esta lección, escribes una implementación nueva y
cambias la línea de `MainActivity.kt` que la elige; las pantallas y `Tienda.kt` no se tocan. Cómo queda con
Google de verdad se ve en la L04.

**Paso 4.** Sí compila, y las pruebas simuladas encuentran la bienvenida al abrir; al tocar «Entrar» no cambia de
pantalla. La segunda forma se parece en el conteo (1 de 7) y no en la causa: sin `remember`, Compose vuelve a
dibujar con un valor recién creado; con una variable común, nunca se entera.

**Para cerrar:**

1. El evento cambió un valor de estado; Compose volvió a ejecutar la función que lo leyó; el `when` eligió otra
   pantalla.
2. Guarda el valor entre una recomposición y la siguiente. Sin él, la app se queda en la bienvenida.
3. Demuestra qué pantalla queda tras cada toque, con una entrada que deja pasar y con una que no, y que la app
   abierta desde `MainActivity` no deja pasar. No demuestra cómo se ven, qué hace el botón «atrás», que no haya
   otra forma de entrar por otro camino, ni que Google funcione.
4. Porque deja pasar a cualquiera. En `src/test` solo la usan las pruebas y no viaja en la app instalada.

---

## Fuentes de la lección

Abiertas el 18-nov-2026.

- Android Developers. *State and Jetpack Compose*. https://developer.android.com/develop/ui/compose/state
- Android Developers. *Thinking in Compose* (recomposición; los efectos van en eventos). https://developer.android.com/develop/ui/compose/mental-model
- Android Developers. *Lifecycle of composables* (qué dispara una recomposición). https://developer.android.com/develop/ui/compose/lifecycle
- Android Developers. *Where to hoist state* (el ancestro común más cercano). https://developer.android.com/develop/ui/compose/state-hoisting
- Android Developers. *Navigation* (la librería que esta lección no usa). https://developer.android.com/guide/navigation
- Android Developers. *Use test doubles in Android* (qué es un doble; dice que el que vive en el código de pruebas no viaja con la app). https://developer.android.com/training/testing/fundamentals/test-doubles
- Android Developers. *Android build structure* (diferencia entre `src/main` y `src/test`). https://developer.android.com/build/android-build-structure
- Android Developers. *Build local unit tests* y *Test in Android Studio*. https://developer.android.com/training/testing/local-tests · https://developer.android.com/studio/test/test-in-android-studio
- Android Developers. *Test your Compose layout*. https://developer.android.com/develop/ui/compose/testing
- Android Developers. *SemanticsMatcher* y *Role* (rol de botón en las pruebas). https://developer.android.com/reference/kotlin/androidx/compose/ui/test/SemanticsMatcher · https://developer.android.com/reference/kotlin/androidx/compose/ui/semantics/Role
- Kotlin. *Interfaces*. https://kotlinlang.org/docs/interfaces.html
- Kotlin. *Enum classes*. https://kotlinlang.org/docs/enum-classes.html
- Kotlin. *Conditions and loops* (`when`). https://kotlinlang.org/docs/control-flow.html
- Kotlin. *Higher-order functions and lambdas* (tipos de función como `() -> Unit`) y *Functions* (`Unit`). https://kotlinlang.org/docs/lambdas.html · https://kotlinlang.org/docs/functions.html
- Kotlin. *Strings* (plantillas con `$`). https://kotlinlang.org/docs/strings.html
- Medición propia: Mac con Apple silicon, 18-nov-2026 (el `verifica` sobre `inicio/` y `solucion/`; las dos formas de romper el estado; la predicción del paso 1; el estado intermedio del paso 2, con y sin una errata; mensajes del compilador).

Revisión externa (18-nov-2026): CON CAMBIOS, 3 de prioridad alta, 4 media y 2 baja; aplicada.
