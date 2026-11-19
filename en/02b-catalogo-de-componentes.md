# Practice 02-B — The store's component catalog

**Before you read, six words.** One line each.

- **Component:** a small `@Composable` function that is used on several screens, like a button or a card.
- **Variant:** the same component with other data or in another mode: disabled, in dark, with a long text.
- **Preview:** a function marked with `@Preview` that Android Studio draws in a panel, without an emulator.
- **Catalog:** a screen inside the debug app where you see all the components and their variants.
- **Screenshot test:** a test that draws a component, takes an image and compares it with a **reference image** saved
  in the repo. If a pixel changed by more than what is tolerated, it comes out red.
- **`src/debug`:** a code folder that only goes into the debug app, never into the one that is published.

**What you take away:** three components of the store —main button, product card and order-status label— reviewed in
isolation in their variants, placed on 02's home screen, with a catalog in the debug app and eleven screenshot
tests that turn red when someone changes a color.

**This practice's new idea:** a component is designed and reviewed **in isolation**, in all its variants, before it
is used on a screen. It is a practice: it comes after 02, outside the main sequence, and it is optional for the
cohort.

**A note on the app's texts.** As in the previous lessons, the project ships its on-screen texts in Spanish, and the
`verifica` looks for those exact texts. In code blocks, terminal outputs and on-screen sketches they stay as the
project has them; in the prose the practice quotes them in English with the original in parentheses the first time:
the button «See my orders» («Ver mis órdenes»), the label «Delivered» («Entregada») and the other two states
«Received» («Recibida») and «Sent» («Enviada»).

---

## The scene

Someone on the team changes one line: the store's green turns red, to try out a more eye-catching button. It
compiles. The seven tests from 02 are still green. Nobody opens the app in dark mode or looks at the card of a
product with a long name. It is pushed.

That change is measured. With `VerdeTienda` in red, 02's seven tests came out 7 of 7 green, and this practice's
eleven screenshot tests came out **6 of 11 red** (Nov 19, 2026). It was not only the button that changed: the card's
price and the «Entregada» label changed too, also in dark. 02's tests measure which screen is left after each tap,
and they do not look at colors.

The practice's question: how do you see all of a component's variants without walking through the app, and how do
you find out when one changes without anybody wanting it to?

---

## Before you start

**What you need:** [02](02-pantallas-que-cambian.md) finished, the environment from [00](00-ambiente-listo.md)
(`ANDROID_HOME` and an emulator or your phone) and Android Studio for the previews panel.

**The starter project** is 02's solution, with this practice's setup already done:

```bash
git pull
cd lecciones/02b-catalogo-de-componentes/inicio
```

Your answers go in `respuestas/02b.md`, at the root of the repo (`mkdir -p respuestas` if it does not exist).

**What the project already carries (scaffolding).** It is not assessed here:

| What it carries | Where | Why |
|---|---|---|
| The store's palette and typography | `Tema.kt` | The practice gives them: nobody designs them. Do not change it, except for one moment in step 6 |
| The dependencies and the plugins | `gradle/libs.versions.toml` and `app/build.gradle.kts` | See the versions table below |
| 02's tests | `RecorridoTest.kt` and `ArranqueTest.kt` | They must stay green with the components in place. Do not change them |
| Memory, `gradlew`, Android 36 for the tests and Java 25 | The same as in 02 | It was explained in [01](01-mi-primera-pantalla.md) |

`Tema.kt` defines eleven named colors (`VerdeTienda`, `AzulRecibida`, `AmbarEnviada`…), a light and a dark palette,
four type styles and a `TemaTienda(oscuro) { … }` function that wraps any content with all of that. Open it and read
it before step 1: you are going to use it in every step.

**Why Roborazzi for the screenshots.** It was chosen with a single criterion: that it keeps working the same for the
next cohort. Google's official tool does not meet it today (Nov 19, 2026): its page says the
`com.android.compose.screenshot` plugin "is no longer maintained", and its installation still asks for the
`android.experimental.enableScreenshotTest=true` flag. Google recommends migrating to AGP's *test suites*, which
ask for AGP 9.6.0-alpha03 or higher, a preview version; this series is pinned to AGP 9.4.1, and updating it is a separate decision. Paparazzi, the
other well-known option, is at 2.0.0-alpha05. Roborazzi has a stable version on Maven Central and runs on Robolectric,
which the series has used since 01.

**The versions**, pinned and tested with this practice on Nov 19, 2026. Those of Roborazzi, Showkase, KSP and
`material-icons-core` were the latest published that day; AGP 9.4.1 and the series' BOM 2026.09.00 were not (there
was already AGP 9.5.1 and BOM 2026.11.01), and they stay as they are so as not to move the series mid-cohort:

| Piece | Version | What for | Source |
|---|---|---|---|
| Roborazzi (`roborazzi`, `roborazzi-compose` and its plugin) | 1.79.0 | Screenshot tests | Maven Central |
| Showkase (`showkase` and `showkase-processor`) | 1.0.5 | The catalog | Maven Central and its README |
| KSP (Kotlin Symbol Processing) | 2.3.14 | Runs Showkase's generator at compile time | Maven Central and its *releases* |
| `material-icons-core` | 1.7.8, the one in BOM 2026.09.00 | Icons that Showkase uses inside | The Compose BOM |
| Robolectric | 4.18 | It already came from 02 | — |

Robolectric 4.18.1 appeared on Maven Central on Nov 18, 2026. This practice stays with the series' 4.18: everything was measured with it.

**The sketch of the home screen**, with the three components below 02's greeting:

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

*(The arrows say: product card, status label, main button.)*

---

## Step 1 · By hand: the main button

Without Claude Code. Create `BotonPrincipal.kt` next to `Inicio.kt`, in `app/src/main/java/mx/habil/ejemplo/tienda/`:

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

- **`habilitado: Boolean = true`** is a parameter with a default value: whoever does not pass it gets `true`.
- **`MaterialTheme.typography.labelLarge`** does not say which font to use: it asks for the `labelLarge` style of
  whatever theme surrounds it. Inside `TemaTienda`, it is the one from `Tema.kt`.
- **The button does not say its color.** `Button` takes the theme's primary color, which in the light palette is
  `VerdeTienda`.

**Predict.** Write in `respuestas/02b.md`, before going on: what changes on screen with `habilitado = false`? And with
a text that does not fit on one line?

You cannot see it yet: there is no screen that uses it. You will see it in step 3's panel.

**What proves it:** the component compiles and the `verifica` finds "fun BotonPrincipal(" (part of objective 2B.1).

---

## Step 2 · With Claude Code: the card, the label and the home screen

This is where the tool comes in, always the same way: **you predict → you ask → you read what it generated → you
run the `verifica` → you explain it in your own words.**

1. **Predict.** Write in `respuestas/02b.md`: which of these files are going to change? `Inicio.kt`,
   `MainActivity.kt`, `Tienda.kt`, `Tema.kt`.
2. **Ask** Claude Code, standing in `inicio/`: "Create `TarjetaProducto.kt` with a composable
   `TarjetaProducto(nombre: String, precio: String)`: a full-width `Card`, 16 dp corners and the theme's surface
   color; inside, the name with the `titleMedium` style on at most two lines and with "…" if it does not fit, and
   below it the price in bold with the theme's primary color. Create `EtiquetaEstado.kt` with an
   `enum class EstadoOrden(val texto: String)` with `RECIBIDA("Recibida")`, `ENVIADA("Enviada")` and
   `ENTREGADA("Entregada")`; a `colorDe(estado)` function that returns `AzulRecibida`, `AmbarEnviada` and
   `VerdeTienda` from `Tema.kt`; and a composable `EtiquetaEstado(estado: EstadoOrden)`: a pill of that color with
   the text in white and the `labelSmall` style. In `Inicio.kt`, below the two texts, add a `TarjetaProducto` of
   "Audífonos inalámbricos" at "$899.00", an `EtiquetaEstado(EstadoOrden.ENVIADA)` and a
   `BotonPrincipal("Ver mis órdenes")` that does nothing for now. In `MainActivity`, wrap `Tienda` with
   `TemaTienda(oscuro = isSystemInDarkTheme())`. Do not change `Tema.kt`, `Tienda.kt` or the tests."
3. **Read** the change before accepting it. The names above are this practice's contract: the previews, the catalog
   and the screenshots call them that way. The piece that is hardest to read is the label:

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

   Three things to check as you read:

   - **`enum class EstadoOrden(val texto: String)`** is 02's closed list, now with one piece of data per value:
     `EstadoOrden.ENVIADA.texto` is `"Enviada"`.
   - **`RoundedCornerShape(50)`** rounds the corners to 50 % of the height: that is why it looks like a pill.
   - **`"$899.00"` brings in no variable.** In Kotlin, `$` only builds a template if a name or a brace follows it;
     followed by a number, it is the dollar sign.

4. **Run the `verifica`** from the repo root:

```bash
./lecciones/02b-catalogo-de-componentes/verifica
```

   02's seven tests must stay green: 02's home screen still says «Hola, Ana» and has three more things. Measured on
   Nov 19, 2026 at this point: the marks for the components, for `Inicio.kt` and for `MainActivity.kt` green,
   `Resultado: 7 de 7 pruebas en verde`, and red only those from steps 3 to 6 (previews, `@ShowkaseRoot`, images,
   screenshots, sabotage and `respuestas/02b.md`). That red is the expected one now.
5. **Explain** in two lines of `respuestas/02b.md`: why does `TarjetaProducto` receive no color, and where does it get
   it from?

*(Result: 7 of 7 tests green.)*

**What proves it:** the three components compile and 02's home screen uses them, with 02's tests green (objective
2B.1).

---

## Step 3 · Without AI: the variants in the previews panel

This is exercise 2B.2 and it is done **without Claude Code**. Android Studio's previews panel draws a `@Preview`
function without compiling the whole app or opening the emulator.

**1. Write the button's.** At the end of `BotonPrincipal.kt` add this, with its `import`s (`Box`, `padding`,
`Preview`, `PreviewParameter` and `PreviewParameterProvider`; Android Studio offers them to you):

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

- **`data class`** is a class for holding data: Kotlin writes for it how to compare and how to print itself.
- **`PreviewParameterProvider`** is an interface, like 02's `Entrada`: it promises a sequence of values. Android's
  documentation says **one preview is drawn per value**.
- **`getDisplayName`** names each one. Without it, according to the documentation, they are called by their position
  (0, 1, 2…). The list inside is that same page's recommendation, to find the name by position.
- **`oscuro` goes in the variant.** The preview's dark mode does not depend on your computer or on the phone: each
  variant decides it. That is why step 5's screenshots come out the same on any machine that draws the same.

**2. Write the other two with the same mold.** In `TarjetaProducto.kt`, `VarianteTarjeta(nombre, producto, precio,
oscuro)`, `VariantesTarjeta` and `TarjetaProductoPreview`, with `group = "Tarjetas"`. In `EtiquetaEstado.kt`,
`VarianteEtiqueta(nombre, estado, oscuro)`, `VariantesEtiqueta` and `EtiquetaEstadoPreview`, with
`group = "Etiquetas"` and no `widthDp`. These are the variants, in this order:

| Component | Variants (`nombre`) |
|---|---|
| Card | «claro» and «oscuro» (light and dark), with «Audífonos inalámbricos» at «$899.00»; «texto largo» (long text), with «Juego de seis tazas de cerámica esmaltada hechas a mano en Dolores Hidalgo, Guanajuato» at «$1,249.00» |
| Label | «recibida», «enviada» and «entregada», in light; «entregada en oscuro» |

**3. Predict.** Before opening the panel, write: how many previews are you going to see in total? Which card is
going to end in "…"?

**4. Look at it.** Open `BotonPrincipal.kt` in Android Studio and choose the **Split** or **Design** view, at the top
right of the editor; if it asks you to compile, accept. According to the documentation, the panel draws one preview
per value of the provider, with the name that `getDisplayName` gives. This practice did not measure the panel in
Android Studio: if it appears different to you, tell us.

Take a screenshot of the panel with the button's four variants and save it as `respuestas/02b-panel.png`. Below your
prediction from step 1, note what you got right.

**What proves it:** your screenshot of the panel and your prediction, compared (objective 2B.2). The `verifica` only
checks that each component carries its `@Preview` with `@PreviewParameter`: it does not see the panel. And that you
did it without AI is a working rule that you declare in your answer: neither a screenshot nor the `verifica` can
prove it.

---

## Step 4 · With Claude Code: the catalog with Showkase

The panel lives in your Android Studio. The catalog lives **in the app**: anyone on the team opens it on their phone,
without the project open. Showkase, from Airbnb, builds it from the same `@Preview`s: at compile time, KSP (a code
generator that runs alongside the Kotlin compiler) looks for the previews and writes the code that gathers them.

1. **Predict.** Why do you think the catalog must go in `src/debug` and not in `src/main`?
2. **Ask** Claude Code, standing in `inicio/`: "Create `app/src/debug/java/mx/habil/ejemplo/tienda/Catalogo.kt` with
   a class `CatalogoTienda` marked with `@ShowkaseRoot` that implements `ShowkaseRootModule`, and a
   `CatalogoActivity` that in `onCreate` opens `Showkase.getBrowserIntent(this)` and closes itself with `finish()`.
   Create `app/src/debug/AndroidManifest.xml` that registers `CatalogoActivity` with its own launcher icon and the
   label "Catálogo". Do not touch anything in `src/main`."
3. **Read** the change. It must carry these pieces:

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

   - **`@ShowkaseRoot`** marks the catalog's root. Showkase's README asks for one per module.
   - **`getBrowserIntent`** does not exist until you compile: KSP writes it. If Android Studio paints it red, compile
     once.
   - **The previews must be public or `internal`.** The README says so: a `private` one does not get into the
     catalog.

4. **Look at it in the emulator.** `./gradlew installDebug`. On the phone two icons appear: «La tienda» and
   «Catálogo». Open «Catálogo».

   In the trial emulator (Pixel 9, Android 37, Nov 19, 2026) this was seen: a list titled «Components» (Showkase's
   texts come in English) with three groups, «Botones (1)», «Etiquetas (1)» and «Tarjetas (1)». The "(1)" counts
   components, not variants. On tapping «Botones» and then «Botón principal» the four variants appeared: green with
   «Comprar», dimmed gray, light green on a dark background, and the long text on two lines. All four carry the same
   title, «Botón principal»: Showkase did not use the names from `getDisplayName`. In the labels, «Recibida» in blue,
   «Enviada» in amber and two «Entregada» in green were seen, the second one on a dark background.

5. **Explain** in `respuestas/02b.md` what you saw differently between step 3's panel and the catalog.

**What proves it:** the catalog opens in the emulator with the three groups and their variants (objective 2B.3). The
`verifica` checks what Showkase generated at compile time: the catalog's root and its three groups, that an activity
in `src/debug` opens it with its own icon, and that `src/main` does not name it. A string that says "@ShowkaseRoot"
generates nothing and comes out red. What is seen in the emulator you tell, and your screenshot is the evidence.

---

## Step 5 · With Claude Code: one screenshot per preview

Seeing the variants is useful while someone is looking at them. A screenshot test looks at them every time the tests
run.

1. **Predict.** How many reference images are you going to have at the end?
2. **Ask** Claude Code, standing in `inicio/`: "Create `app/src/test/java/mx/habil/ejemplo/tienda/CapturasTest.kt`: a
   Robolectric test with `ParameterizedRobolectricTestRunner` and `@GraphicsMode(GraphicsMode.Mode.NATIVE)`, with
   one case for each variant of `VariantesBoton`, `VariantesTarjeta` and `VariantesEtiqueta`, called
   `boton_<nombre>`, `tarjeta_<nombre>` and `etiqueta_<nombre>` with the spaces changed to underscores. Each case
   draws its preview with `createComposeRule()` and calls `onRoot().captureRoboImage("src/test/capturas/<caso>.png")`.
   The list of cases must come from the same providers, not written by hand. Do not touch anything else."
3. **Read** the change. The heart of it is this:

```kotlin
@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CapturasTest(private val archivo: String, private val preview: @Composable () -> Unit) {
    // … previews(): one case per variant, taken from the providers …

    @get:Rule
    val regla = createComposeRule()

    @Test
    fun captura() {
        regla.setContent { preview() }
        regla.onRoot().captureRoboImage("src/test/capturas/$archivo.png")
    }
}
```

   - **`ParameterizedRobolectricTestRunner`** runs the same test once for each case in the list: eleven cases,
     eleven tests.
   - **`@GraphicsMode(NATIVE)`** asks Robolectric to draw real pixels. Roborazzi's README requires it.
   - **`captureRoboImage`** takes the image. What it does with it depends on the Gradle task you run.

4. **Record the references.** Standing in `inicio/`:

```bash
./gradlew recordRoborazziDebug
```

   Eleven `.png` files are left in `app/src/test/capturas/`. Open them: they are your components, each in its
   variant. Those images **are pushed to the repo**: they are what the next run is going to compare.
5. **Run the `verifica`.** Now it runs `verifyRoborazziDebug`, which compares each screenshot with its reference.
   With the solution, on Nov 19, 2026 it gave `Resultado: 18 de 18 pruebas en verde`: 02's 7 and the 11 screenshots.

*(Result: 18 of 18 tests green.)*

**Careful with the task you run.** According to Roborazzi's README, `recordRoborazziDebug` records (or records
again) the references and `verifyRoborazziDebug` compares and fails if something changed. A bare
`./gradlew testDebugUnitTest` runs the tests without comparing anything: with step 6's broken color it gave 18 of 18
green (measured on Nov 19, 2026). The `verifica` uses `verify`.

**The references in `solucion/` were recorded on a Mac.** It has not been measured whether they match on Linux. If you
use Linux, record yours on your machine with `recordRoborazziDebug`, as in point 4.

**What proves it:** eleven screenshot tests green against their references (first part of objective 2B.4).

---

## Step 6 · Without AI: break a color on purpose

This is the close of 2B.4 and it is done **without Claude Code**. You are going to provoke the scene from the
beginning.

**1. Predict.** In `Tema.kt`, `VerdeTienda` is going to go from `0xFF0B6E4F` to `0xFFC62828`, a red. Write in
`respuestas/02b.md` which of the eleven screenshots you think are going to turn red, and why. Hint: look in your
files for who uses `VerdeTienda` directly and who uses the theme's primary color, and in which palette.

**2. Break it and measure.** Change the line:

```kotlin
val VerdeTienda = Color(0xFFC62828)
```

and run, standing in `inicio/`:

```bash
./gradlew verifyRoborazziDebug
```

On Nov 19, 2026, with the solution, it ended like this (the line number depends on your file):

```text
CapturasTest > [boton_habilitado] > captura[boton_habilitado] FAILED
    java.lang.AssertionError at CapturasTest.kt:53
…
18 tests completed, 6 failed
FAILURE: Build failed with an exception.
```

*(That is: the `boton_habilitado` test failed with an assertion error; 18 tests completed, 6 failed; the build
failed.)*

The six in red were `boton_habilitado`, `boton_texto_largo`, `tarjeta_claro`, `tarjeta_texto_largo`,
`etiqueta_entregada` and `etiqueta_entregada_en_oscuro`. Each one leaves a difference image in
`app/build/outputs/roborazzi/`, for example `boton_habilitado_compare.png`.

**3. Read the difference.** Open `boton_habilitado_compare.png`. It carries three panels: **Reference** (the
reference, the green button), **Diff** (what changed, marked in red) and **New** (what was drawn now, the red
button). Paste into `respuestas/02b.md` the path of that image and write what you see in the middle panel.

**4. Explain why those six and not the other five**, before reading "Compare your answers".

**5. Fix it.** Put the line back to `val VerdeTienda = Color(0xFF0B6E4F)` and run `./gradlew verifyRoborazziDebug`
again: the eleven go back to green. Then run the `verifica`. It does the same on its own, in a copy of your project,
without touching your folder: it changes the color, demands at least one screenshot in red with its difference
image, puts it back and demands the eleven in green. If your screenshots do not detect the change, that mark stays
red even if the eleven pass.

**A limit, measured.** A change of a single unit in the blue, from `0xFF0B6E4F` to `0xFF0B6E4E`, gave 11 of 11 green
(Nov 19, 2026). These screenshots use Roborazzi 1.79.0's default tolerance: `CapturasTest` passes it no comparison
option. Its README describes that maximum distance per pixel (0.001 by default) and the options for tolerating more,
meant for edge anti-aliasing. That is why this practice's criterion is the green-to-red change that you can see, not
detecting every number.

2B.4 is done when you can check all four:

- [ ] Your prediction from point 1 is written, with what came out below it.
- [ ] You pasted the path of a `_compare.png` image and described its middle panel.
- [ ] You explained why those screenshots turned red and not the others.
- [ ] The `verifica` gives 18 of 18, with the sabotage marks green.

**What proves it:** a green run, a red run with the image difference, a green run again (objective 2B.4).

---

## Your exercises

Step 1, 2B.2 (step 3) and step 6 are done **without AI**.

| # | You do | It is done when |
|---|---|---|
| 2B.1 | You write the button and review the card and the label that Claude Code generated; the home screen uses them | The «Texto» marks of the components and of `Inicio.kt` green, and 02's 7 tests green |
| 2B.2 | You write the previews with `@PreviewParameter` and read the panel without an emulator | You have `respuestas/02b-panel.png` and your prediction compared |
| 2B.3 | You build the catalog in `src/debug` and open it in the emulator | The catalog's «Compilado» marks green, and you wrote what you saw in the catalog and how it differs from the panel |
| 2B.4 | You turn each preview into a screenshot and break a color | The four checkboxes of step 6, and the `verifica` at 18 of 18 |

**Time:** allow about two and a half hours, with the emulator already installed. It is an estimate: we adjust it with
how long the first cohort takes.

---

## Not running?

| You see | Probable cause | What you do |
|---|---|---|
| On opening «Catálogo», the app closes; in `adb logcat` there is `ClassNotFoundException: Didn't find class "androidx.compose.material.icons.Icons$Filled"` | Showkase 1.0.5 uses icons that this series' Compose versions no longer bring on their own (measured on Nov 19, 2026) | Check that `app/build.gradle.kts` still carries `debugImplementation(libs.compose.material.icons.core)`; it comes in the starter project |
| `Unresolved reference 'getBrowserIntent'` | KSP has not generated Showkase's code yet | Compile once (`./gradlew assembleDebug`) |
| A component does not appear in the catalog | Its preview is `private` | Remove the `private`: Showkase's README asks for public or `internal` previews |
| ❌ «CapturasTest corrió 10 pruebas y esperaba 11» (CapturasTest ran 10 tests and expected 11) | A variant is missing in a provider, or the list of cases was written by hand | Compare your providers with step 3's table |
| ❌ «N de 11 capturas no coinciden» (N of 11 screenshots do not match) without your having broken anything | You changed a component on purpose, or your references are from another machine | If the change is the one you wanted, record again with `./gradlew recordRoborazziDebug` and review the new images before pushing them |
| ❌ «Tema.kt cambió» (Tema.kt changed) | Step 6's color did not go back | Put the line back by hand and look at `git diff` |
| ❌ «Las pruebas no llegaron a correr» (The tests did not run) and the lines it shows are not enough | The `verifica` only shows the first lines of the error | From the project folder run `./gradlew verifyRoborazziDebug --stacktrace > fallo.txt 2>&1` and read the last 40 lines of `fallo.txt` before concluding it is a compile error |

---

## To wrap up

**Answer without reading again:**

1. What do you gain by seeing a component in isolation, in all its variants, before putting it on a screen?
2. Why does the catalog live in `src/debug`?
3. Your eleven screenshots are green. Say one thing that proves and one thing it does not.
4. Why did 02's seven tests not notice the red color?

**What comes next:** you go back to the main sequence, in L03.

---

## Compare your answers

**Step 1.** Disabled, the button is painted dimmed gray and does not respond to touch; the long text is split into
two lines, centered (measured in the `boton_deshabilitado` and `boton_texto_largo` screenshots, Nov 19, 2026).

**Step 2.** `Inicio.kt` and `MainActivity.kt` change; `Tienda.kt` and `Tema.kt` do not. The card receives no colors
because it asks the theme around it for them with `MaterialTheme.colorScheme`: inside `TemaTienda`, they are those
of `Tema.kt`.

**Step 3.** Eleven previews: four of the button, three of the card and four of the label. The card that ends in "…" is
the one with the cups: its name does not fit on two lines at 320 dp of width.

**Step 4.** `src/main` travels in the app that is published; `src/debug`, according to Android's documentation, only
in the debug one. A catalog with all the components is of no use to the customer. Between the panel and the
catalog: the panel shows the names from `getDisplayName` and lives in Android Studio; the catalog repeats the
preview's title in each variant and lives on the phone.

**Step 5.** Eleven images, one per variant.

**Step 6.** The ones that paint `VerdeTienda` turn red. In the light palette, `VerdeTienda` is the primary color: the
enabled button, the long-text one and the price on the two light cards use it. The «Entregada» label uses it
directly with `colorDe`, without going through the theme: that is why it also changes in dark. The ones that do not
change are the disabled button, which uses a gray from the theme; the button and the card in dark, whose primary is
`VerdeClaro`; and «Recibida» and «Enviada», which have their own color.

**To wrap up:**

1. You see at once the cases that are hard to find in the app —disabled, dark, long text— and you fix the component
   before it reaches several screens.
2. So that it travels only in the debug app and not in the one that is published.
3. It proves that each variant is drawn the same as its reference and that a visible color change turns them red. It
   does not prove that the design is good, nor does it detect a very small color change, nor how it looks on a real
   phone.
4. Because they measure which screen is left and which text appears after each tap; none of them looks at pixels.

---

## Lesson sources

Opened on Nov 19, 2026.

- Android Developers. *Preview your UI with composable previews* (`@PreviewParameter`, one preview per value, `getDisplayName`). https://developer.android.com/develop/ui/compose/tooling/previews
- Android Developers. *Compose Preview Screenshot Testing* (the plugin is no longer maintained; experimental flag). https://developer.android.com/studio/preview/compose-screenshot-testing
- Android Developers. *Test suites in the Android Gradle plugin* (experimental; AGP 9.6.0-alpha03 or more). https://developer.android.com/studio/preview/test-suites
- Android Developers. *Material Design 3 in Compose* (`MaterialTheme`, light and dark palette). https://developer.android.com/develop/ui/compose/designsystems/material3
- Android Developers. *Configure build variants* (the `src/debug` folder). https://developer.android.com/build/build-variants
- Android Developers. *Android Gradle plugin release notes* (current AGP versions). https://developer.android.com/build/releases/about-agp
- Kotlin. *Kotlin Symbol Processing API*. https://kotlinlang.org/docs/ksp-overview.html
- Airbnb. *Showkase* (README: installation, `@ShowkaseRoot`, public previews). https://github.com/airbnb/Showkase
- Roborazzi (README: `record` and `verify` tasks, `@GraphicsMode`, comparator). https://github.com/takahirom/roborazzi
- Maven Central (versions of Roborazzi, Showkase, KSP, Paparazzi and Robolectric). https://repo1.maven.org/maven2/io/github/takahirom/roborazzi/roborazzi/
- Our own measurement: Mac with Apple silicon, Nov 19, 2026 (the `verifica` on `inicio/` and `solucion/`, one sabotage per mark, the color in red and by one unit, the catalog on a Pixel 9 emulator with Android 37, and the icons failure).
