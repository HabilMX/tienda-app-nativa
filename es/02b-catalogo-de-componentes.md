# Práctica 02-B — Catálogo de componentes de la tienda

**Antes de leer, seis palabras.** Una línea cada una.

- **Componente:** una función `@Composable` pequeña que se usa en varias pantallas, como un botón o una tarjeta.
- **Variante:** el mismo componente con otros datos o en otro modo: deshabilitado, en oscuro, con un texto largo.
- **Preview:** una función marcada con `@Preview` que Android Studio dibuja en un panel, sin emulador.
- **Catálogo:** una pantalla dentro de la app de depuración donde ves todos los componentes y sus variantes.
- **Prueba de captura:** una prueba que dibuja un componente, toma una imagen y la compara con una **imagen de
  referencia** guardada en el repo. Si un pixel cambió más de lo tolerado, queda en rojo.
- **`src/debug`:** una carpeta de código que solo entra en la app de depuración, nunca en la que se publica.

**Qué te llevas:** tres componentes de la tienda —botón principal, tarjeta de producto y etiqueta del estado de una
orden— revisados aislados en sus variantes, puestos en la pantalla de inicio de la 02, con un catálogo en la app de
depuración y once pruebas de captura que se ponen en rojo cuando alguien cambia un color.

**Idea nueva de esta práctica:** un componente se diseña y se revisa **aislado**, en todas sus variantes, antes de
usarse en una pantalla. Es una práctica: va después de la 02, fuera de la secuencia principal, y es opcional para la
cohorte.

---

## La escena

Alguien del equipo cambia una línea: el verde de la tienda pasa a rojo, para probar un botón más llamativo. Compila.
Las siete pruebas de la 02 siguen en verde. Nadie abre la app en modo oscuro ni mira la tarjeta de un producto con
nombre largo. Se sube.

Ese cambio está medido. Con `VerdeTienda` en rojo, las siete pruebas de la 02 dieron 7 de 7 en verde, y las once
pruebas de captura de esta práctica dieron **6 de 11 en rojo** (19 de noviembre de 2026). No solo cambió el botón:
cambiaron el precio de la tarjeta y la etiqueta «Entregada», también en oscuro. Las pruebas de la 02 miden qué pantalla
queda después de cada toque, y no miran colores.

La pregunta de la práctica: ¿cómo ves todas las variantes de un componente sin recorrer la app, y cómo te enteras
cuando una cambia sin que nadie lo quisiera?

---

## Antes de empezar

**Qué necesitas:** la [02](02-pantallas-que-cambian.md) terminada, el ambiente de la [00](00-ambiente-listo.md)
(`ANDROID_HOME` y un emulador o tu teléfono) y Android Studio para el panel de previews.

**El proyecto de inicio** es la solución de la 02, con lo de esta práctica ya configurado:

```bash
git pull
cd lecciones/02b-catalogo-de-componentes/inicio
```

Tus respuestas van en `respuestas/02b.md`, en la raíz del repo (`mkdir -p respuestas` si no existe).

**Lo que el proyecto ya trae (andamiaje).** No se evalúa aquí:

| Qué trae | Dónde | Por qué |
|---|---|---|
| La paleta y la tipografía de la tienda | `Tema.kt` | Las da la práctica: nadie las diseña. No lo cambies, salvo un momento en el paso 6 |
| Las dependencias y los plugins | `gradle/libs.versions.toml` y `app/build.gradle.kts` | Ver la tabla de versiones de abajo |
| Las pruebas de la 02 | `RecorridoTest.kt` y `ArranqueTest.kt` | Deben seguir en verde con los componentes puestos. No las cambies |
| Memoria, `gradlew`, Android 36 para las pruebas y Java 25 | Igual que en la 02 | Se explicó en la [01](01-mi-primera-pantalla.md) |

`Tema.kt` define once colores con nombre (`VerdeTienda`, `AzulRecibida`, `AmbarEnviada`…), una paleta clara y una
oscura, cuatro estilos de letra y una función `TemaTienda(oscuro) { … }` que envuelve cualquier contenido con todo eso.
Ábrelo y léelo antes del paso 1: lo vas a usar en cada paso.

**Por qué Roborazzi para las capturas.** Se eligió con un solo criterio: que siga funcionando igual para la siguiente
cohorte. La herramienta oficial de Google no lo cumple hoy (19-nov-2026): su página dice que el plugin
`com.android.compose.screenshot` «ya no se mantiene», y su instalación todavía pide la bandera
`android.experimental.enableScreenshotTest=true`. Google recomienda migrar a las *test suites* de AGP, que piden AGP 9.6.0-alpha03 o
más, una versión preliminar; esta serie está fijada en AGP 9.4.1, y actualizarla es una decisión aparte. Paparazzi, la otra opción conocida, va en
2.0.0-alpha05. Roborazzi tiene versión estable en Maven Central y corre sobre Robolectric, que la serie ya usa desde la
01.

**Las versiones**, fijadas y probadas con esta práctica el 19-nov-2026. Las de Roborazzi, Showkase, KSP y
`material-icons-core` eran las últimas publicadas ese día; AGP 9.4.1 y el BOM 2026.09.00 de la serie no (ya había
AGP 9.5.1 y el BOM 2026.11.01), y se quedan para no mover la serie a media cohorte:

| Pieza | Versión | Para qué | Fuente |
|---|---|---|---|
| Roborazzi (`roborazzi`, `roborazzi-compose` y su plugin) | 1.79.0 | Pruebas de captura | Maven Central |
| Showkase (`showkase` y `showkase-processor`) | 1.0.5 | El catálogo | Maven Central y su README |
| KSP (Kotlin Symbol Processing) | 2.3.14 | Corre el generador de Showkase al compilar | Maven Central y sus *releases* |
| `material-icons-core` | 1.7.8, la del BOM 2026.09.00 | Íconos que usa Showkase por dentro | El BOM de Compose |
| Robolectric | 4.18 | Ya venía de la 02 | — |

La 4.18.1 de Robolectric apareció en Maven Central el 18-nov-2026. Esta práctica sigue con la 4.18 de la serie: con ella se midió todo.

**El boceto de la pantalla de inicio**, con los tres componentes debajo del saludo de la 02:

```text
┌──────────────────────────┐
│       Hola, Ana          │
│  Aquí verás tus órdenes  │
│ ┌──────────────────────┐ │
│ │ Audífonos            │ │  ← tarjeta de producto
│ │ $899.00              │ │
│ └──────────────────────┘ │
│        ( Enviada )       │  ← etiqueta de estado
│ [   Ver mis órdenes    ] │  ← botón principal
└──────────────────────────┘
```

---

## Paso 1 · A mano: el botón principal

Sin Claude Code. Crea `BotonPrincipal.kt` junto a `Inicio.kt`, en `app/src/main/java/mx/habil/ejemplo/tienda/`:

```kotlin
package mx.habil.ejemplo.tienda

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun BotonPrincipal(texto: String, alTocar: () -> Unit, habilitado: Boolean = true) {
    Button(
        onClick = alTocar,
        enabled = habilitado,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(texto, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
    }
}
```

- **`habilitado: Boolean = true`** es un parámetro con valor por omisión: quien no lo pasa recibe `true`.
- **`MaterialTheme.typography.labelLarge`** no dice qué letra usar: pide el estilo `labelLarge` del tema que esté
  alrededor. Dentro de `TemaTienda`, es el de `Tema.kt`.
- **El botón no dice su color.** `Button` toma el color primario del tema, que en la paleta clara es `VerdeTienda`.

**Predice.** Escribe en `respuestas/02b.md`, antes de seguir: ¿qué cambia en pantalla con `habilitado = false`? ¿Y con
un texto que no cabe en un renglón?

Todavía no lo puedes ver: no hay pantalla que lo use. Lo verás en el panel del paso 3.

**Lo demuestra:** el componente compila y el `verifica` encuentra «fun BotonPrincipal(» (parte del objetivo 2B.1).

---

## Paso 2 · Con Claude Code: la tarjeta, la etiqueta y la pantalla de inicio

Aquí entra la herramienta, siempre igual: **predices → le pides → lees lo que generó → corres el `verifica` → lo
explicas con tus palabras.**

1. **Predice.** Escribe en `respuestas/02b.md`: ¿cuáles de estos archivos van a cambiar? `Inicio.kt`,
   `MainActivity.kt`, `Tienda.kt`, `Tema.kt`.
2. **Pídele** a Claude Code, parado en `inicio/`: «Crea `TarjetaProducto.kt` con un composable
   `TarjetaProducto(nombre: String, precio: String)`: una `Card` de ancho completo, esquinas de 16 dp y color de
   superficie del tema; adentro, el nombre con el estilo `titleMedium` en a lo más dos renglones y con «…» si no cabe,
   y debajo el precio en negritas con el color primario del tema. Crea `EtiquetaEstado.kt` con un
   `enum class EstadoOrden(val texto: String)` con `RECIBIDA("Recibida")`, `ENVIADA("Enviada")` y
   `ENTREGADA("Entregada")`; una función `colorDe(estado)` que regrese `AzulRecibida`, `AmbarEnviada` y `VerdeTienda`
   de `Tema.kt`; y un composable `EtiquetaEstado(estado: EstadoOrden)`: una píldora de ese color con el texto en blanco
   y el estilo `labelSmall`. En `Inicio.kt`, debajo de los dos textos, agrega una `TarjetaProducto` de "Audífonos
   inalámbricos" a "$899.00", una `EtiquetaEstado(EstadoOrden.ENVIADA)` y un `BotonPrincipal("Ver mis órdenes")`
   que por ahora no haga nada. En `MainActivity`, envuelve `Tienda` con `TemaTienda(oscuro = isSystemInDarkTheme())`.
   No cambies `Tema.kt`, `Tienda.kt` ni las pruebas.»
3. **Lee** el cambio antes de aceptarlo. Los nombres de arriba son el contrato de esta práctica: las previews, el
   catálogo y las capturas los llaman así. La pieza que más cuesta leer es la etiqueta:

```kotlin
enum class EstadoOrden(val texto: String) {
    RECIBIDA("Recibida"),
    ENVIADA("Enviada"),
    ENTREGADA("Entregada"),
}

fun colorDe(estado: EstadoOrden): Color = when (estado) {
    EstadoOrden.RECIBIDA -> AzulRecibida
    EstadoOrden.ENVIADA -> AmbarEnviada
    EstadoOrden.ENTREGADA -> VerdeTienda
}

@Composable
fun EtiquetaEstado(estado: EstadoOrden) {
    Surface(shape = RoundedCornerShape(50), color = colorDe(estado)) {
        Text(
            estado.texto,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}
```

   Tres cosas que revisar al leer:

   - **`enum class EstadoOrden(val texto: String)`** es la lista cerrada de la 02, ahora con un dato por valor:
     `EstadoOrden.ENVIADA.texto` es `"Enviada"`.
   - **`RoundedCornerShape(50)`** redondea las esquinas al 50 % del alto: por eso se ve como píldora.
   - **`"$899.00"` no mete ninguna variable.** En Kotlin, `$` solo arma una plantilla si le sigue un nombre o una
     llave; seguido de un número, es el signo de pesos.

4. **Corre el `verifica`** desde la raíz del repo:

```bash
./lecciones/02b-catalogo-de-componentes/verifica
```

   Las siete pruebas de la 02 deben seguir en verde: el inicio de la 02 sigue diciendo «Hola, Ana» y tiene tres cosas
   más. Medido el 19-nov-2026 en este punto: las marcas de los componentes, de `Inicio.kt` y de `MainActivity.kt` en
   verde, `Resultado: 7 de 18 pruebas esperadas en verde (solo corrieron 7…` (las once de captura todavía no existen), y en rojo solo las de los pasos 3 a 6 (previews, `@ShowkaseRoot`,
   imágenes, capturas, sabotaje y `respuestas/02b.md`). Ese rojo es el esperado ahora.
5. **Explica** en dos líneas de `respuestas/02b.md`: ¿por qué `TarjetaProducto` no recibe ningún color, y de dónde lo
   saca?

**Lo demuestra:** los tres componentes compilan y la pantalla de inicio de la 02 los usa, con las pruebas de la 02 en
verde (objetivo 2B.1).

---

## Paso 3 · Sin IA: las variantes en el panel de previews

Este es el ejercicio 2B.2 y se hace **sin Claude Code**. El panel de previews de Android Studio dibuja una función
`@Preview` sin compilar la app completa ni abrir el emulador.

**1. Escribe la del botón.** Al final de `BotonPrincipal.kt` agrega esto, con sus `import` (`Box`, `padding`,
`Preview`, `PreviewParameter` y `PreviewParameterProvider`; Android Studio te los ofrece):

```kotlin
data class VarianteBoton(
    val nombre: String, val texto: String, val habilitado: Boolean, val oscuro: Boolean
)

class VariantesBoton : PreviewParameterProvider<VarianteBoton> {
    private val lista = listOf(
        VarianteBoton("habilitado", "Comprar", habilitado = true, oscuro = false),
        VarianteBoton("deshabilitado", "Comprar", habilitado = false, oscuro = false),
        VarianteBoton("oscuro", "Comprar", habilitado = true, oscuro = true),
        VarianteBoton(
            "texto largo", "Agregar al carrito y seguir viendo la tienda",
            habilitado = true, oscuro = false
        ),
    )
    override val values = lista.asSequence()
    override fun getDisplayName(index: Int): String? = lista.getOrNull(index)?.nombre
}

@Preview(name = "Botón principal", group = "Botones", widthDp = 320)
@Composable
fun BotonPrincipalPreview(@PreviewParameter(VariantesBoton::class) variante: VarianteBoton) {
    TemaTienda(oscuro = variante.oscuro) {
        Box(Modifier.padding(16.dp)) {
            BotonPrincipal(variante.texto, alTocar = {}, habilitado = variante.habilitado)
        }
    }
}
```

- **`data class`** es una clase para guardar datos: Kotlin le escribe solo cómo compararse y cómo imprimirse.
- **`PreviewParameterProvider`** es una interfaz, como la `Entrada` de la 02: promete una secuencia de valores. La
  documentación de Android dice que se dibuja **una preview por cada valor**.
- **`getDisplayName`** le pone nombre a cada una. Sin él, según la documentación, se llaman por su posición (0, 1,
  2…). La lista por dentro es la recomendación de esa misma página, para encontrar el nombre por posición.
- **`oscuro` va en la variante.** El modo oscuro de la preview no depende de tu computadora ni del teléfono: lo
  decide cada variante. Por eso las capturas del paso 5 salen iguales en cualquier máquina que dibuje igual.

**2. Escribe las otras dos con el mismo molde.** En `TarjetaProducto.kt`, `VarianteTarjeta(nombre, producto, precio,
oscuro)`, `VariantesTarjeta` y `TarjetaProductoPreview`, con `group = "Tarjetas"`. En `EtiquetaEstado.kt`,
`VarianteEtiqueta(nombre, estado, oscuro)`, `VariantesEtiqueta` y `EtiquetaEstadoPreview`, con `group = "Etiquetas"` y
sin `widthDp`. Estas son las variantes, en este orden:

| Componente | Variantes (`nombre`) |
|---|---|
| Tarjeta | «claro» y «oscuro», con «Audífonos inalámbricos» a «$899.00»; «texto largo», con «Juego de seis tazas de cerámica esmaltada hechas a mano en Dolores Hidalgo, Guanajuato» a «$1,249.00» |
| Etiqueta | «recibida», «enviada» y «entregada», en claro; «entregada en oscuro» |

**3. Predice.** Antes de abrir el panel, escribe: ¿cuántas previews vas a ver en total? ¿Qué tarjeta va a terminar en
«…»?

**4. Míralo.** Abre `BotonPrincipal.kt` en Android Studio y elige la vista **Split** o **Design**, arriba a la derecha
del editor; si te pide compilar, acepta. Según la documentación, el panel dibuja una preview por cada valor del
proveedor, con el nombre que da `getDisplayName`. Esta práctica no midió el panel en Android Studio: si te aparece
distinto, cuéntanos.

Toma una captura del panel con las cuatro variantes del botón y guárdala como `respuestas/02b-panel.png`. Debajo de tu
predicción del paso 1, anota qué acertaste.

**Lo demuestra:** tu captura del panel y tu predicción, comparadas (objetivo 2B.2). El `verifica` solo comprueba que
cada componente trae su `@Preview` con `@PreviewParameter`: no ve el panel. Y que lo hayas hecho sin IA es una regla de
trabajo que declaras en tu respuesta: ni una captura ni el `verifica` lo pueden demostrar.

---

## Paso 4 · Con Claude Code: el catálogo con Showkase

El panel vive en tu Android Studio. El catálogo vive **en la app**: cualquiera del equipo lo abre en su teléfono, sin
el proyecto abierto. Showkase, de Airbnb, lo arma con las mismas `@Preview`: al compilar, KSP (un generador de código
que corre junto al compilador de Kotlin) busca las previews y escribe el código que las junta.

1. **Predice.** ¿Por qué crees que el catálogo debe ir en `src/debug` y no en `src/main`?
2. **Pídele** a Claude Code, parado en `inicio/`: «Crea `app/src/debug/java/mx/habil/ejemplo/tienda/Catalogo.kt` con
   una clase `CatalogoTienda` marcada con `@ShowkaseRoot` que implemente `ShowkaseRootModule`, y una
   `CatalogoActivity` que en `onCreate` abra `Showkase.getBrowserIntent(this)` y se cierre con `finish()`. Crea
   `app/src/debug/AndroidManifest.xml` que registre `CatalogoActivity` con su propio ícono de inicio y la etiqueta
   «Catálogo». No toques nada de `src/main`.»
3. **Lee** el cambio. Debe traer estas piezas:

```kotlin
@ShowkaseRoot
class CatalogoTienda : ShowkaseRootModule

class CatalogoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Showkase.getBrowserIntent(this))
        finish()
    }
}
```

   - **`@ShowkaseRoot`** marca la raíz del catálogo. El README de Showkase pide una por módulo.
   - **`getBrowserIntent`** no existe hasta que compilas: lo escribe KSP. Si Android Studio lo pinta en rojo, compila
     una vez.
   - **Las previews deben ser públicas o `internal`.** El README lo dice: una `private` no entra al catálogo.

4. **Míralo en el emulador.** `./gradlew installDebug`. En el teléfono aparecen dos íconos: «La tienda» y «Catálogo».
   Abre «Catálogo».

   En el emulador de la prueba (Pixel 9, Android 37, 19-nov-2026) se vio esto: una lista titulada «Components» (los
   textos de Showkase vienen en inglés) con tres grupos, «Botones (1)», «Etiquetas (1)» y «Tarjetas (1)». El «(1)»
   cuenta componentes, no variantes. Al tocar «Botones» y luego «Botón principal» aparecieron las cuatro variantes:
   verde con «Comprar», gris apagado, verde claro sobre fondo oscuro, y el texto largo en dos renglones. Las cuatro
   llevan el mismo título, «Botón principal»: Showkase no usó los nombres de `getDisplayName`. En las etiquetas se
   vieron «Recibida» en azul, «Enviada» en ámbar y dos «Entregada» en verde, la segunda sobre fondo oscuro.

5. **Explica** en `respuestas/02b.md` qué viste distinto entre el panel del paso 3 y el catálogo.

**Lo demuestra:** el catálogo abre en el emulador con los tres grupos y sus variantes (objetivo 2B.3). El `verifica`
comprueba lo que Showkase generó al compilar: la raíz del catálogo y sus tres grupos, que una actividad de `src/debug`
lo abre con su ícono propio, y que `src/main` no lo nombra. Una cadena que diga «@ShowkaseRoot» no genera nada y queda
en rojo. Lo que se ve en el emulador lo cuentas tú, y tu captura es la evidencia.

---

## Paso 5 · Con Claude Code: una captura por preview

Ver las variantes sirve mientras alguien las mira. Una prueba de captura las mira cada vez que corren las pruebas.

1. **Predice.** ¿Cuántas imágenes de referencia vas a tener al terminar?
2. **Pídele** a Claude Code, parado en `inicio/`: «Crea `app/src/test/java/mx/habil/ejemplo/tienda/CapturasTest.kt`:
   una prueba de Robolectric con `ParameterizedRobolectricTestRunner` y `@GraphicsMode(GraphicsMode.Mode.NATIVE)`,
   con un caso por cada variante de `VariantesBoton`, `VariantesTarjeta` y `VariantesEtiqueta`, llamados
   `boton_<nombre>`, `tarjeta_<nombre>` y `etiqueta_<nombre>` con los espacios cambiados por guion bajo. Cada caso
   dibuja su preview con `createComposeRule()` y llama a `onRoot().captureRoboImage("src/test/capturas/<caso>.png")`.
   La lista de casos debe salir de los mismos proveedores, no escrita a mano. No toques nada más.»
3. **Lee** el cambio. El corazón es este:

```kotlin
@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CapturasTest(private val archivo: String, private val preview: @Composable () -> Unit) {
    // … previews(): un caso por variante, sacado de los proveedores …

    @get:Rule
    val regla = createComposeRule()

    @Test
    fun captura() {
        regla.setContent { preview() }
        regla.onRoot().captureRoboImage("src/test/capturas/$archivo.png")
    }
}
```

   - **`ParameterizedRobolectricTestRunner`** corre la misma prueba una vez por cada caso de la lista: once casos,
     once pruebas.
   - **`@GraphicsMode(NATIVE)`** le pide a Robolectric dibujar pixeles de verdad. El README de Roborazzi lo exige.
   - **`captureRoboImage`** toma la imagen. Qué hace con ella depende de la tarea de Gradle que corras.

4. **Graba las referencias.** Parado en `inicio/`:

```bash
./gradlew recordRoborazziDebug
```

   Quedan once archivos `.png` en `app/src/test/capturas/`. Ábrelos: son tus componentes, cada uno en su variante.
   Esas imágenes **se suben al repo**: son lo que la siguiente corrida va a comparar.
5. **Corre el `verifica`.** Ahora corre `verifyRoborazziDebug`, que compara cada captura con su referencia. Con la
   solución, el 19-nov-2026 dio `Resultado: 18 de 18 pruebas en verde`: las 7 de la 02 y las 11 capturas.

**Cuidado con la tarea que corres.** Según el README de Roborazzi, `recordRoborazziDebug` graba (o vuelve a grabar) las
referencias y `verifyRoborazziDebug` compara y falla si algo cambió. `./gradlew testDebugUnitTest` a secas corre las
pruebas sin comparar nada: con el color roto del paso 6 dio 18 de 18 en verde (medido el 19-nov-2026). El `verifica`
usa `verify`.

**Las referencias de `solucion/` se grabaron en una Mac.** En Linux Mint 22.3 coincidieron las once (19-nov-2026): la
solución dio 18 de 18 y el sabotaje del color puso 6 de 11 en rojo, como en la Mac. Si en tu máquina no coinciden,
graba las tuyas con `recordRoborazziDebug`, como en el punto 4.

**Lo demuestra:** once pruebas de captura en verde contra sus referencias (primera parte del objetivo 2B.4).

---

## Paso 6 · Sin IA: rompe un color a propósito

Este es el cierre del 2B.4 y se hace **sin Claude Code**. Vas a provocar la escena del principio.

**1. Predice.** En `Tema.kt`, `VerdeTienda` va a pasar de `0xFF0B6E4F` a `0xFFC62828`, un rojo. Escribe en
`respuestas/02b.md` cuáles de las once capturas crees que se van a poner en rojo, y por qué. Pista: busca en tus
archivos quién usa `VerdeTienda` directamente y quién usa el color primario del tema, y en cuál paleta.

**2. Rómpelo y mide.** Cambia la línea:

```kotlin
val VerdeTienda = Color(0xFFC62828)
```

y corre, parado en `inicio/`:

```bash
./gradlew verifyRoborazziDebug
```

El 19-nov-2026, con la solución, terminó así (el número de línea depende de tu archivo):

```text
CapturasTest > [boton_habilitado] > captura[boton_habilitado] FAILED
    java.lang.AssertionError at CapturasTest.kt:53
…
18 tests completed, 6 failed
FAILURE: Build failed with an exception.
```

Las seis en rojo fueron `boton_habilitado`, `boton_texto_largo`, `tarjeta_claro`, `tarjeta_texto_largo`,
`etiqueta_entregada` y `etiqueta_entregada_en_oscuro`. Cada una deja una imagen de diferencia en
`app/build/outputs/roborazzi/`, por ejemplo `boton_habilitado_compare.png`.

**3. Lee la diferencia.** Abre `boton_habilitado_compare.png`. Trae tres paneles: **Reference** (la referencia, el
botón verde), **Diff** (lo que cambió, marcado en rojo) y **New** (lo que se dibujó ahora, el botón rojo). Pega en
`respuestas/02b.md` la ruta de esa imagen y escribe qué ves en el panel del medio.

**4. Explica por qué esas seis y no las otras cinco**, antes de leer «Compara tus respuestas».

**5. Arréglalo.** Regresa la línea a `val VerdeTienda = Color(0xFF0B6E4F)` y corre otra vez
`./gradlew verifyRoborazziDebug`: las once vuelven a verde. Luego corre el `verifica`. Él hace lo mismo por su cuenta
en una copia de tu proyecto, sin tocar tu carpeta: cambia el color, exige al menos una captura en rojo con su imagen de
diferencia, lo regresa y exige las once en verde. Si tus capturas no detectan el cambio, esa marca queda en rojo aunque
las once pasen.

**Un límite, medido.** Un cambio de una sola unidad en el azul, de `0xFF0B6E4F` a `0xFF0B6E4E`, dio 11 de 11 en verde
(19-nov-2026). Estas capturas usan la tolerancia por omisión de Roborazzi 1.79.0: `CapturasTest` no le pasa ninguna
opción de comparación. Su README describe esa distancia máxima por pixel (0.001 por omisión) y las opciones para
tolerar más, pensadas para el suavizado de bordes. Por eso el criterio de esta práctica es el cambio de verde a rojo
que se ve, no detectar cada número.

El 2B.4 está terminado cuando puedes marcar las cuatro:

- [ ] Tu predicción del punto 1 está escrita, con lo que salió debajo.
- [ ] Pegaste la ruta de una imagen `_compare.png` y describiste su panel del medio.
- [ ] Explicaste por qué se pusieron en rojo esas capturas y no las demás.
- [ ] El `verifica` da 18 de 18, con las marcas del sabotaje en verde.

**Lo demuestra:** corrida en verde, corrida en rojo con la diferencia de imagen, corrida en verde otra vez (objetivo
2B.4).

---

## Tus ejercicios

El paso 1, el 2B.2 (paso 3) y el paso 6 se hacen **sin IA**.

| # | Haces | Está hecho cuando |
|---|---|---|
| 2B.1 | Escribes el botón y revisas la tarjeta y la etiqueta que generó Claude Code; la pantalla de inicio los usa | Las marcas «Texto» de los componentes y de `Inicio.kt` en verde, y las 7 pruebas de la 02 en verde |
| 2B.2 | Escribes las previews con `@PreviewParameter` y lees el panel sin emulador | Tienes `respuestas/02b-panel.png` y tu predicción comparada |
| 2B.3 | Armas el catálogo en `src/debug` y lo abres en el emulador | Las marcas «Compilado» del catálogo en verde, y escribiste qué viste en el catálogo y en qué difiere del panel |
| 2B.4 | Conviertes cada preview en una captura y rompes un color | Las cuatro casillas del paso 6, y el `verifica` en 18 de 18 |

**Tiempo:** calcula unas dos horas y media, con el emulador ya instalado. Es un estimado: lo ajustamos con lo que tarde
la primera cohorte.

---

## ¿No te corre?

| Ves | Causa probable | Qué haces |
|---|---|---|
| Al abrir «Catálogo», la app se cierra; en `adb logcat` aparece `ClassNotFoundException: Didn't find class "androidx.compose.material.icons.Icons$Filled"` | Showkase 1.0.5 usa íconos que las versiones de Compose de esta serie ya no traen solas (medido el 19-nov-2026) | Revisa que `app/build.gradle.kts` siga trayendo `debugImplementation(libs.compose.material.icons.core)`; viene en el proyecto de inicio |
| `Unresolved reference 'getBrowserIntent'` | KSP todavía no genera el código de Showkase | Compila una vez (`./gradlew assembleDebug`) |
| Un componente no aparece en el catálogo | Su preview es `private` | Quítale `private`: el README de Showkase pide previews públicas o `internal` |
| ❌ «CapturasTest corrió 10 pruebas y esperaba 11» | Falta una variante en un proveedor, o la lista de casos se escribió a mano | Compara tus proveedores con la tabla del paso 3 |
| ❌ «N de 11 capturas no coinciden» sin que hayas roto nada | Cambiaste un componente a propósito, o tus referencias son de otra máquina | Si el cambio es el que querías, vuelve a grabar con `./gradlew recordRoborazziDebug` y revisa las imágenes nuevas antes de subirlas |
| ❌ «Tema.kt cambió» | El color del paso 6 no regresó | Regresa la línea a mano y mira `git diff` |
| ❌ «Las pruebas no llegaron a correr» y las líneas que muestra no bastan | El `verifica` solo enseña las primeras líneas del error | Desde la carpeta del proyecto corre `./gradlew verifyRoborazziDebug --stacktrace > fallo.txt 2>&1` y lee las últimas 40 líneas de `fallo.txt` antes de concluir que es un error de compilación |

---

## Para cerrar

**Contesta sin volver a leer:**

1. ¿Qué ganas al ver un componente aislado, en todas sus variantes, antes de ponerlo en una pantalla?
2. ¿Por qué el catálogo vive en `src/debug`?
3. Tus once capturas están en verde. Di una cosa que eso demuestra y una que no.
4. ¿Por qué las siete pruebas de la 02 no se dieron cuenta del color rojo?

**Lo que sigue:** regresas a la secuencia principal, en la L03.

---

## Compara tus respuestas

**Paso 1.** Deshabilitado, el botón se pinta gris apagado y no responde al toque; el texto largo se parte en dos
renglones, centrado (medido en las capturas `boton_deshabilitado` y `boton_texto_largo`, 19-nov-2026).

**Paso 2.** Cambian `Inicio.kt` y `MainActivity.kt`; `Tienda.kt` y `Tema.kt` no. La tarjeta no recibe colores porque
los pide al tema que la rodea con `MaterialTheme.colorScheme`: dentro de `TemaTienda`, son los de `Tema.kt`.

**Paso 3.** Once previews: cuatro del botón, tres de la tarjeta y cuatro de la etiqueta. Termina en «…» la tarjeta de
las tazas: su nombre no cabe en dos renglones a 320 dp de ancho.

**Paso 4.** `src/main` viaja en la app que se publica; `src/debug`, según la documentación de Android, solo en la de
depuración. Un catálogo con todos los componentes no le sirve al cliente. Entre el panel y el catálogo: el panel
muestra los nombres de `getDisplayName` y vive en Android Studio; el catálogo repite el título de la preview en cada
variante y vive en el teléfono.

**Paso 5.** Once imágenes, una por variante.

**Paso 6.** Se ponen en rojo las que pintan `VerdeTienda`. En la paleta clara, `VerdeTienda` es el color primario: lo
usan el botón habilitado, el de texto largo y el precio de las dos tarjetas claras. La etiqueta «Entregada» lo usa
directo con `colorDe`, sin pasar por el tema: por eso también cambia en oscuro. No cambian el botón deshabilitado, que
usa un gris del tema; el botón y la tarjeta en oscuro, cuyo primario es `VerdeClaro`; y «Recibida» y «Enviada», que
tienen su propio color.

**Para cerrar:**

1. Ves de una vez los casos que en la app cuestan trabajo encontrar —deshabilitado, oscuro, texto largo— y corriges el
   componente antes de que llegue a varias pantallas.
2. Para que viaje solo en la app de depuración y no en la que se publica.
3. Demuestra que cada variante se dibuja igual que su referencia y que un cambio visible de color las pone en rojo. No
   demuestra que el diseño sea bueno, ni detecta un cambio de color muy pequeño, ni cómo se ve en un teléfono real.
4. Porque miden qué pantalla queda y qué texto aparece después de cada toque; ninguna mira pixeles.

---

## Fuentes de la lección

Abiertas el 19-nov-2026.

- Android Developers. *Preview your UI with composable previews* (`@PreviewParameter`, una preview por valor,
  `getDisplayName`). https://developer.android.com/develop/ui/compose/tooling/previews
- Android Developers. *Compose Preview Screenshot Testing* (el plugin ya no se mantiene; bandera experimental).
  https://developer.android.com/studio/preview/compose-screenshot-testing
- Android Developers. *Test suites in the Android Gradle plugin* (experimental; AGP 9.6.0-alpha03 o más).
  https://developer.android.com/studio/preview/test-suites
- Android Developers. *Material Design 3 in Compose* (`MaterialTheme`, paleta clara y oscura).
  https://developer.android.com/develop/ui/compose/designsystems/material3
- Android Developers. *Configure build variants* (la carpeta `src/debug`). https://developer.android.com/build/build-variants
- Android Developers. *Android Gradle plugin release notes* (versiones vigentes de AGP).
  https://developer.android.com/build/releases/about-agp
- Kotlin. *Kotlin Symbol Processing API*. https://kotlinlang.org/docs/ksp-overview.html
- Airbnb. *Showkase* (README: instalación, `@ShowkaseRoot`, previews públicas). https://github.com/airbnb/Showkase
- Roborazzi (README: tareas `record` y `verify`, `@GraphicsMode`, comparador). https://github.com/takahirom/roborazzi
- Maven Central (versiones de Roborazzi, Showkase, KSP, Paparazzi y Robolectric). https://repo1.maven.org/maven2/io/github/takahirom/roborazzi/roborazzi/
- Medición propia: Mac con Apple silicon, 19-nov-2026 (el `verifica` sobre `inicio/` y `solucion/`, un sabotaje por
  marca, el color en rojo y en una unidad, el catálogo en un emulador Pixel 9 con Android 37, y la falla de los
  íconos).
- Medición en Linux Mint 22.3, 19-nov-2026: el `verifica` sobre `solucion/` desde un clon nuevo, 18 de 18, las once
  capturas iguales a sus referencias y el sabotaje del color con 6 de 11 en rojo.
