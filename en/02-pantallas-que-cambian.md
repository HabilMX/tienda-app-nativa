# Lesson 02 — Screens that change

**Before you read, seven words.** One line each; the series' other terms come in the lesson where they are used.

- **State:** a piece of data the screen remembers and that can change. You met it in 01; here it is the topic.
- **Recomposition:** when Compose re-runs your functions to update the screen because a piece of data changed.
- **Event:** something that happens and that the app finds out about, like a tap on a button.
- **Hoisting state:** taking state out of a screen and keeping it in the function that calls it.
- **Interface:** a list of what can be asked of something, without saying how it gets done.
- **Implementation:** a class that does carry out what the interface asks for. There can be several.
- **Test double:** an implementation made only for tests, which answers what the test needs.

**What you take away:** an app with three screens —welcome, sign-in and home— that moves from one to another, and
knowing how to say why the screen changed each time: which value changed, who changed it and who read it.

**This lesson's new idea:** state. When a state value that the screen read changes, Compose **schedules a
recomposition** to update what is needed, and the screen is drawn again. Moving from one screen to another is exactly
that: a value that changes. You also practice one supporting thing, which is also checked: separating the decision to
let someone in into a contract, with an implementation just for the tests (step 3).

**A note on the app's texts.** The course project ships its on-screen texts in Spanish, and the files in `lecciones/`
are not translated: the course's automatic checks (the `verifica`) look for those exact texts. So in code blocks,
terminal outputs and on-screen sketches the texts stay as the project has them, and in the prose the lesson quotes
them in English with the original in parentheses the first time: the screens «Sign in to your account» («Entra a tu
cuenta»), «Hello, Ana» («Hola, Ana») and «Your orders will show here» («Aquí verás tus órdenes»), the button
«Continue» («Continuar») and the notice «You cannot sign in yet» («Todavía no se puede entrar»). The ones from lesson
01 are the same: «The store» («La tienda»), «Enter» («Entrar») and «Hello!» («¡Hola!»).

---

## The scene

You have the three screens working. Before closing, you go over the file to leave it clean and one word gets in your
way: `remember`. The line reads the same without it. You delete it.

It compiles. You open the app: the welcome looks identical. You tap «Enter».

Nothing happens. There is no error message. The welcome is still there. You tap again. Nothing.

That change exists and has been measured: it is the "Break one on purpose" of step 4. With that single word deleted,
the lesson's simulated tests go from 7 of 7 green to 1 of 7, with 6 red and none with an error (November 18, 2026).
Nothing blew up. The screen stopped changing, and the code does not say why.

The answer is in a question: when you tap a button, where is what happened stored? First you predict what a state
value does, then you build the journey with it, and in the end you break it yourself to explain it.

---

## Before you start

**What you need:** [01](01-mi-primera-pantalla.md) finished —`Column`, `Text` and `Button` are not explained again
here— and the environment from [00](00-ambiente-listo.md): `ANDROID_HOME` defined and an emulator or your phone.

**The starter project.** From the root of the series repo:

```bash
git pull
cd lecciones/02-pantallas-que-cambian/inicio
```

That is where you work. Next to it is `solucion/`: at the end it helps you contrast your decisions and understand any
difference. Your predictions and explanations go in `respuestas/02.md`, at the repo root (`mkdir -p respuestas` if it
does not exist).

**What the project already carries (scaffolding).** It is not assessed here:

| What it carries | Where | Why |
|---|---|---|
| The welcome from 01 | `Bienvenida.kt` | You start from what you already wrote |
| The tests | `app/src/test/…/RecorridoTest.kt` and `ArranqueTest.kt` | They are the ones the `verifica` runs. Do not change them |
| Memory, `gradlew`, Android 36 for the tests and Java 25 | Same as in 01 | Explained in [01](01-mi-primera-pantalla.md) |

**Navigation is scaffolding too.** Android has a library for moving between screens, Navigation. It is not used here:
the journey is built with a state value and a `when`, which I give you already written in step 2.

**The sketch.** Three screens, one at a time:

```text
┌──────────────────────┐    ┌──────────────────────┐    ┌──────────────────────┐
│      La tienda       │    │  Entra a tu cuenta   │    │      Hola, Ana       │
│ Tus órdenes, en tu   │ ─▶ │    [ Continuar ]     │ ─▶ │ Aquí verás tus       │
│      bolsillo        │    │ Todavía no se puede  │    │ órdenes              │
│     [ Entrar ]       │    │ entrar (si no pasó)  │    │                      │
└──────────────────────┘    └──────────────────────┘    └──────────────────────┘
      welcome (bienvenida)         sign-in (entrada)           home (inicio)
```

*(In the sketch, "si no pasó" means "if it did not go through".)*

From sign-in to home you move by tapping «Continue», **only if someone decides you can pass**. Who decides is step 3.

---

## Step 1 · Predict what a state value does

This step is yours, without Claude Code. Open `app/src/main/java/mx/habil/ejemplo/tienda/Bienvenida.kt`. It is the
screen from 01, with these three pieces:

```kotlin
    var saludando by remember { mutableStateOf(false) }      // the state
    …
        Button(onClick = { saludando = true }) {             // the event changes it
            Text("Entrar")
        }
        if (saludando) {                                     // the screen reads it
            Text("¡Hola!")
        }
```

**Predict first.** Change `saludando = true` to `saludando = !saludando` (the `!` inverts: from `false` to `true`
and back). Write in `respuestas/02.md`, before running anything: what shows after the first tap, the second and the
third?

**Now check it.** With the emulator open or your phone connected:

```bash
./gradlew installDebug      # builds and installs; it does not open the app
```

Open the app, tap three times and compare. If your prediction failed, do not erase it: write below which line would
have given you the answer.

**What happened, in order.** It is the chain of the whole lesson:

1. You tap the button: that is the **event**.
2. The `onClick` changes the value. It draws nothing.
3. Compose watches that value because it was born from `mutableStateOf`. When it changes, it **schedules a
   recomposition**: re-running what read it and needs updating.
4. In this example, your function runs again. `remember` hands it back the stored value, not a new one.
5. The `if` reads the current value, and the screen's description comes out different.

Do not write code assuming how many times or in what order a `@Composable` function will run: according to the
documentation, Compose may skip the ones that did not change and run others many times.

Nobody "adds" or "removes" the greeting: the function describes the screen for the value of that moment.

Before continuing, put the button line back by hand to `Button(onClick = { saludando = true })`. That way you keep
any other change you may have made. To confirm the file ended up as it came, this command must show nothing (it only
looks, it does not change your file):

```bash
git diff -- app/src/main/java/mx/habil/ejemplo/tienda/Bienvenida.kt
```

**What proves it:** your written prediction, compared with the run (objective 2.1).

---

## Step 2 · By hand: a value decides which screen shows

Also without Claude Code. You are going to write two screens and the function that chooses which one is drawn.

**Predict first.** If "which screen am I on" is a state value, in which function should it live: in `Bienvenida`, in
the sign-in screen or in a third one that calls both? Write which one and why.

**1. The welcome stops keeping state and gives notice.** In `Bienvenida.kt`, delete the `saludando` line, the
`if (saludando) { … }` block and the four `androidx.compose.runtime` `import`s that are not `Composable`. Change two
lines:

```kotlin
fun Bienvenida(alEntrar: () -> Unit) {
    …
        Button(onClick = alEntrar) {
```

`alEntrar: () -> Unit` is a parameter whose value is a function: it receives nothing (`()`) and returns no data
(`Unit`). The screen runs it when the button is tapped, and does not know what happens next.

**2. The sign-in screen.** Create `PantallaEntrada.kt` in the same folder. It starts with `package
mx.habil.ejemplo.tienda` and the same `import`s as `Bienvenida.kt`:

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

It does not keep anything either: it receives a piece of data (`rechazada`) and gives notice of a tap
(`alContinuar`).

**3. The function that keeps the state.** Create `Tienda.kt`:

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

- **`enum class Pantalla { … }`** is a closed list: a `Pantalla` can only be one of those three.
- **`when (pantalla) { … }`** runs the branch for that value. If you are missing one of the three, it does not
  compile.
- **`alEntrar = { pantalla = Pantalla.ENTRADA }`** is the function you hand to the welcome. It gives notice; here the
  state is changed.

**4. Make the app start in `Tienda`.** In `MainActivity.kt`, the `setContent` line becomes
`setContent { Tienda() }`.

**Check it in the app.** `./gradlew installDebug`, open it and tap «Enter»: the screen «Sign in to your account»
(«Entra a tu cuenta») must remain and the welcome must no longer show. «Continue» does nothing yet.
With the finished solution, on the trial emulator (Pixel 9, Android 37, Nov 18, 2026) this was seen: the welcome
with «La tienda», «Tus órdenes, en tu bolsillo» and «Entrar»; tapping «Entrar», «Entra a tu cuenta» and «Continuar»;
and tapping «Continuar», the notice «Todavía no se puede entrar». The home screen does not appear in the installed
app: only the tests get to it.

**What you just did.** The state did not stay in any screen: you raised it to the function that calls them. That is
**hoisting state**, and Android's documentation recommends it: state lives in the closest common ancestor of whoever
reads it and whoever changes it; data travels down, events travel up.

**The `verifica` cannot run the tests yet.** From the repo root:

```bash
./lecciones/02-pantallas-que-cambian/verifica
```

This red is the expected one **only** when you finish step 2, and only because `Entrada`, `EntradaFalsa` and
the `entrada` parameter of `Tienda` are still missing. **You are on track** if among the lines you see these
(measured on Nov 18, 2026; the Gradle ones repeat several times):

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

*(That is the `verifica`'s output, exactly as the script prints it, in Spanish. In English it says: "I did not find
«interface Entrada» in src/main: without it the tests do not compile (step 3)"; "Text: in src/main there is a
«fun Tienda(»"; "I did not find «class EntradaFalsa» in src/test: you write it in EntradaFalsa.kt (step 3)"; "Guided
state of step 2: pieces from step 3 are missing above, and while they are missing the tests do not compile"; "The
tests did not get to run. This is what Gradle said"; and "Result: 0 tests run".)*

If the Gradle lines mention another file —`Tienda.kt`, `Bienvenida.kt`, `PantallaEntrada.kt`—, or if you have already
started step 3, it is not this red: it is an error to fix before continuing. For example, with `recuerda` written
instead of `remember`, Gradle said `e: Tienda.kt:14:21 Unresolved reference 'recuerda'.` (measured on Nov 18, 2026).
And since the tests do not run yet, this step is checked in the installed app.

**What proves it:** in the installed app, «Enter» takes you to the sign-in screen (first half of objective 2.2).

---

## Step 3 · With Claude Code: who decides whether you get in

Home is missing, and so is deciding who can reach it. Today there is no provider (Google arrives in lesson 04), and
even so the tests need to go through the three screens.

That is why that decision is not written inside any screen. It is declared separately, as an **interface**: "this can
be asked to sign in, and it answers with the name of whoever got in, or with nothing". And two **implementations** are
written:

| Implementation | Where it lives | What it answers | Who uses it |
|---|---|---|---|
| `EntradaSinProveedor` | `app/src/main` (the app) | Always "no": `null` | The installed app |
| `EntradaFalsa` | `app/src/test` (the tests) | Always "yes", with the name you give it | Only the tests |

`EntradaFalsa` is a **test double**. So that the real app lets nobody through without a provider, we keep this rule:
**the app carries no way of signing in without a provider.** According to Android's documentation, a double that
lives in the test code does not travel with the app.

This is where the tool comes in, and it always comes in the same way: **you predict → you ask → you read what it
generated → you run the `verifica` → you explain it in your own words.**

1. **Predict.** Write in `respuestas/02.md`: of the files `Bienvenida.kt`, `PantallaEntrada.kt`, `Tienda.kt` and
   `MainActivity.kt`, which are going to change and which are not?
2. **Ask** Claude Code, standing in `inicio/`: "Create `Entrada.kt` with an interface `Entrada` that has
   `fun entrar(): String?` and a class `EntradaSinProveedor` that returns `null`. Create `Inicio.kt` with a
   composable `Inicio(nombre: String)` that shows "Hola, " followed by the name and, below, "Aquí verás tus
   órdenes". Make `Tienda` receive `entrada: Entrada`: when "Continuar" is tapped it calls `entrada.entrar()`; if it
   returns a name, go to home with that name; if it returns `null`, stay and set `rechazada` to `true`.
   In `MainActivity` use `EntradaSinProveedor()`. In `app/src/test`, next to `RecorridoTest.kt`, create
   `EntradaFalsa.kt`: it receives a name, always returns it and counts in `veces` how many times it was asked to sign
   in. Do not change `RecorridoTest.kt` or the two screens that already exist."
3. **Read** the whole change before accepting it. The body of the functions may vary. What the tests call by name is
   part of this lesson's contract and has to stay as is: `Entrada` with `fun entrar(): String?`,
   `EntradaSinProveedor()`, `Tienda(entrada = …)`, `EntradaFalsa(nombre)` with its `veces` counter, and the screens'
   texts. With other names your design may be just as good, but these tests will not compile. It must carry these
   four pieces:

```kotlin
// Entrada.kt — the contract and the app's implementation
interface Entrada {
    fun entrar(): String?            // the name of whoever got in, or null if they could not
}

class EntradaSinProveedor : Entrada {
    override fun entrar(): String? = null
}
```

```kotlin
// EntradaFalsa.kt — in app/src/test, not in app/src/main
class EntradaFalsa(private val nombre: String) : Entrada {
    var veces = 0

    override fun entrar(): String? {
        veces++
        return nombre
    }
}
```

```kotlin
// Tienda.kt — receives the sign-in and keeps three state values
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
// MainActivity.kt — the only place in the app that names an implementation
        setContent { Tienda(entrada = EntradaSinProveedor()) }
```

   Four things to check as you read:

   - **`: Entrada`** after the class name says "this class meets that interface", and **`override`** marks the
     function that meets it. Without `override`, it does not compile.
   - **`entrada.entrar()` is inside `alContinuar`**, that is, inside the event. Loose in the body of `Tienda`, it
     would run every time the function is drawn, and Compose may run it many times.
   - **`Tienda` does not say which sign-in it got.** It only knows the interface.
   - In `Inicio.kt` you will see `"Hola, $nombre"`: the `$` sign puts the variable's value inside the text.

4. **Run the `verifica`.** Now the tests do run: seven "Simulado" marks in green —six from the journey and one from
   startup— and the line `Resultado: 7 de 7 pruebas en verde (0 en rojo, 0 con error, 0 saltadas)` (result: 7 of 7
   tests green, 0 red, 0 with error, 0 skipped). One mark stays red, the one for `respuestas/02.md`: it gets filled in
   step 4.
5. **Explain**, in three lines of `respuestas/02.md`: what happens between tapping «Continue» and «Hello, Ana»
   appearing, naming the state values that change.

**Show that changing the sign-in does not touch the screens.** The tests already used `EntradaFalsa("Ana")`,
`EntradaFalsa("Luis")` and `EntradaSinProveedor()` with the same three screens. Now you show it. Standing in
`inicio/`, run this and paste the output into `respuestas/02.md`:

```bash
grep -rlE "EntradaSinProveedor|EntradaFalsa" app/src | sort
```

In `solucion/` the output is four files (measured on Nov 18, 2026): `Entrada.kt` and `MainActivity.kt` in
`src/main`, and `EntradaFalsa.kt` and `RecorridoTest.kt` in `src/test`. No screen appears. Write below which files you
would touch to introduce another way of signing in, and which you would not.

**What each mark proves, and what it does not.**

- **"Simulado", the six from the journey:** they drew `Tienda` in memory with the sign-in the test chose, tapped the
  buttons and looked for the texts. They prove which screen remains after each tap, with two implementations.
- **"Simulado", the one from startup:** it opened `MainActivity`, the class Android opens when you tap the icon, and
  tapped «Enter» and «Continue». It proves that along that path the app lets nobody through.
- **"Texto":** it only looked for words in your files, not counting comments. It does not prove that the code does
  what those words suggest.

None of them proves that there is no **other** way of signing in without a provider: a second sign-in with another
name in `src/main`, used from somewhere else, passes green. That you check yourself: read `MainActivity.kt` and
confirm that it calls `Tienda(entrada = EntradaSinProveedor())`, and that in `src/main` there is no other class that
meets `Entrada`. Nor do they prove how the screens look, what the back button does, or anything about Google.

**In the installed app** you get as far as the sign-in screen, and «Continue» must answer «You cannot sign in yet»:
the app uses `EntradaSinProveedor`. Only the tests reach home.

**If a line is still not clear,** before continuing try to explain it in your own words. If it still does not come,
go back to that fragment or ask for a clarification.

**What proves it:** the simulated `verifica` goes through the three screens (objective 2.2) and does so with two
implementations of `Entrada`, plus your `grep` output and your reading of `MainActivity.kt` (objective 2.4).

---

## Step 4 · Without AI: break one on purpose

This is exercise 2.3 and it is done **without Claude Code**. With the seven tests green, you are going to cause the
scene from the beginning.

**1. Predict.** In `Tienda.kt`, the state line is going to end up like this, without `remember`:

```kotlin
    var pantalla by mutableStateOf(Pantalla.BIENVENIDA)
```

Write in `respuestas/02.md`: does it compile?, what shows when you open the app?, what happens when you tap «Enter»?

**2. Break it and measure.** Make the change and run the `verifica`. This is what it gave on Nov 18, 2026:

```text
✅ Simulado: la app empieza en la bienvenida, con «La tienda» y el botón «Entrar», y sin nada de las otras dos
❌ Al tocar el botón «Entrar» no aparece la pantalla de entrada con «Entra a tu cuenta» y un botón «Continuar» …
❌ conLaEntradaFalsaLlegaAlInicio quedó en rojo, pero depende de la marca anterior …
   (y cuatro más iguales)
Resultado: 1 de 7 pruebas en verde (6 en rojo, 0 con error, 0 saltadas), leído de …
```

*(Spanish is the `verifica`'s output, as printed. In English: "Simulated: the app starts on the welcome, with «La
tienda» and the «Entrar» button, and nothing from the other two"; "Tapping the «Entrar» button does not show the
sign-in screen with «Entra a tu cuenta» and a «Continuar» button …"; "… came out red, but depends on the previous
mark …"; "(and four more the same)"; "Result: 1 of 7 tests green (6 red, 0 with error, 0 skipped), read from …".)*

Copy into `respuestas/02.md` the red line that starts with «Al tocar el botón «Entrar» no aparece la pantalla de
entrada». Then install the app and see it yourself.

**3. Explain why**, in your own words, before reading what follows.

What Android's documentation says: `remember` stores a value in the screen's memory and, on each recomposition,
returns **that same** value. Without `remember`, the line runs again in full. The chain, deduced from that
documentation (the `verifica` measures the result, not each step), goes like this:

1. You tap «Enter». The event changes the value to `ENTRADA`.
2. Compose finds out, because the value is still born from `mutableStateOf`, and runs `Tienda` again.
3. `Tienda` runs from the top and the first line creates a **new** value, at `BIENVENIDA`.
4. The `when` reads that new value. The welcome is drawn again.

The value did change. What was lost was the place where it was kept.

**4. Another way to break it.** Leave the line as an ordinary variable: `var pantalla = Pantalla.BIENVENIDA`. The
`verifica` gives the same 1 of 7 (measured on Nov 18, 2026), for another reason: according to the documentation,
Compose only finds out about changes in values it can observe. The value changes and nobody draws again.

**5. Fix it.** Put the line back to `var pantalla by remember { mutableStateOf(Pantalla.BIENVENIDA) }` and run the
`verifica`: seven of seven, and now also the `respuestas/02.md` mark.

The `verifica` only checks that you pasted the red line: it does not know whether you broke it or grade your
explanation. 2.3 is finished when you can tick all four:

- [ ] Your prediction from point 1 is written, with what you saw below it.
- [ ] You pasted the red line and the "Resultado" it gave you.
- [ ] You explained, step by step, why the screen stops updating without `remember`.
- [ ] You said how the second way of breaking it is alike and how it is not.

**A limit.** According to Android's documentation, `remember` does not keep the value across a configuration change,
such as rotating the phone; `rememberSaveable` exists for that. This lesson neither uses nor tests it.
On the trial emulator (Nov 18, 2026), when rotating the phone while on «Entra a tu cuenta», the app went back to the
welcome: the state was lost, as the documentation says. And "back", from either of the two screens, closed the app and
returned to the phone's home screen: this lesson's journey does not handle "back".

**What proves it:** "Break one on purpose" (objective 2.3).

---

## Your exercises

2.1, step 2 and 2.3 are done **without AI**.

| # | You do | It is done when |
|---|---|---|
| 2.1 | You predict what the step 1 state does and run it | You have your prediction and what you saw |
| 2.2 | You build welcome → sign-in → home | The `verifica` gives 7 of 7 **and** in the installed app you saw «Todavía no se puede entrar» |
| 2.3 | You break the state and explain why the screen stops updating | Your evidence is the four boxes of step 4, written by you. The `verifica` only confirms that the project was repaired (7 of 7) and that you pasted the red |
| 2.4 | You separate the decision to sign in into `Entrada` and show that changing it does not touch the screens | The tests with the two implementations and the startup one are green, you pasted your `grep` output **and** you read that `src/main` carries no other sign-in |

**Time:** allow about three hours, with the "Break one on purpose". It is an estimate: we adjust it with how long the
first cohort takes.

---

## Not running?

The messages in English are the ones the series' compiler gave (Kotlin 2.4.21) on Nov 18, 2026.

| You see | Probable cause | What you do |
|---|---|---|
| `No value passed for parameter 'alEntrar'` | Someone calls `Bienvenida()` without the function | Pass it: `Bienvenida(alEntrar = { … })` |
| `'when' expression must be exhaustive. Add the 'INICIO' branch or an 'else' branch.` | The `when` is missing one of the three screens | Add the branch the message names |
| `'entrar' hides member of supertype 'Entrada' and needs an 'override' modifier.` | The class meets the interface without saying so | Write `override fun entrar()` |
| `Smart cast to 'String' is impossible, because 'nombre' is a delegated property.` | You asked `if (nombre != null)` on a state and used it as a `String` | First store the result in a `val`, like `val quien = entrada.entrar()` |
| ❌ «EntradaFalsa aparece en src/main» (EntradaFalsa appears in src/main) | The sign-in that lets people through ended up inside the app | Move it to `app/src/test/java/mx/habil/ejemplo/tienda/` |
| ❌ on «solo al tocar Continuar» (only when tapping Continuar) | `entrada.entrar()` ended up outside the event | It goes inside `alContinuar = { … }` |
| `RecorridoTest.kt` or `ArranqueTest.kt` changed | Someone (you or Claude Code) touched the tests | See what changed with the `git diff` the message gives and undo it by hand. Restoring the file with git discards everything it has: do it only after reviewing that diff. Then repeat the request asking it not to touch them |

On Linux Mint 22.3 (x86_64), this lesson's `verifica` came out green on the solution: 7 of 7 (Nov 18, 2026).

---

## To wrap up

**Answer without reading again:**

1. You tap «Enter» and the screen changes. What happened in between, in order?
2. What does `remember` do, and what shows in the app if you remove it?
3. Your seven simulated tests are green. Say one thing that proves and one thing it does not.
4. Why does `EntradaFalsa` live in `src/test` and not in `src/main`?

**What comes next:** in L03 the app meets Firebase, and in L04 you sign in with Google.

---

## Compare your answers

**Step 1.** After the first tap «¡Hola!» shows, after the second it disappears and after the third it comes back
(measured on Nov 18, 2026 with a simulated test).

**Step 2.** In a third function that calls both, `Tienda`. If the value lived in `Bienvenida`, the sign-in screen
could not read it, and when the welcome stopped being drawn it would be lost along with it.

**Step 3.** `Tienda.kt` and `MainActivity.kt` change; `Bienvenida.kt` and `PantallaEntrada.kt` do not: they only
received data and gave notice of taps. Between the tap and «Hola, Ana»: `alContinuar` calls `entrada.entrar()`, which
returns `"Ana"`; it is stored in `nombre` and `pantalla` becomes `INICIO`; Compose runs `Tienda` again and the `when`
chooses `Inicio`. To introduce another way of signing in, with what is in this lesson, you write a new implementation
and change the line in `MainActivity.kt` that chooses it; the screens and `Tienda.kt` are not touched. How it looks
with real Google is seen in L04.

**Step 4.** Yes it compiles, and the simulated tests find the welcome on opening; when «Entrar» is tapped, the screen
does not change. The second way is alike in the count (1 of 7) and not in the cause: without `remember`, Compose draws
again with a freshly created value; with an ordinary variable, it never finds out.

**To wrap up:**

1. The event changed a state value; Compose ran again the function that read it; the `when` chose another screen.
2. It keeps the value between one recomposition and the next. Without it, the app stays on the welcome.
3. It proves which screen remains after each tap, with a sign-in that lets people through and one that does not, and
   that the app opened from `MainActivity` lets nobody through. It does not prove how they look, what the back button
   does, that there is no other way of signing in by another path, or that Google works.
4. Because it lets anyone through. In `src/test` only the tests use it and it does not travel in the installed app.

---

## Lesson sources

Opened on Nov 18, 2026.

- Android Developers. *State and Jetpack Compose*. https://developer.android.com/develop/ui/compose/state
- Android Developers. *Thinking in Compose* (recomposition; effects go in events). https://developer.android.com/develop/ui/compose/mental-model
- Android Developers. *Lifecycle of composables* (what triggers a recomposition). https://developer.android.com/develop/ui/compose/lifecycle
- Android Developers. *Where to hoist state* (the closest common ancestor). https://developer.android.com/develop/ui/compose/state-hoisting
- Android Developers. *Navigation* (the library this lesson does not use). https://developer.android.com/guide/navigation
- Android Developers. *Use test doubles in Android* (what a double is; says the one living in the test code does not travel with the app). https://developer.android.com/training/testing/fundamentals/test-doubles
- Android Developers. *Android build structure* (difference between `src/main` and `src/test`). https://developer.android.com/build/android-build-structure
- Android Developers. *Build local unit tests* and *Test in Android Studio*. https://developer.android.com/training/testing/local-tests · https://developer.android.com/studio/test/test-in-android-studio
- Android Developers. *Test your Compose layout*. https://developer.android.com/develop/ui/compose/testing
- Android Developers. *SemanticsMatcher* and *Role* (button role in tests). https://developer.android.com/reference/kotlin/androidx/compose/ui/test/SemanticsMatcher · https://developer.android.com/reference/kotlin/androidx/compose/ui/semantics/Role
- Kotlin. *Interfaces*. https://kotlinlang.org/docs/interfaces.html
- Kotlin. *Enum classes*. https://kotlinlang.org/docs/enum-classes.html
- Kotlin. *Conditions and loops* (`when`). https://kotlinlang.org/docs/control-flow.html
- Kotlin. *Higher-order functions and lambdas* (function types like `() -> Unit`) and *Functions* (`Unit`). https://kotlinlang.org/docs/lambdas.html · https://kotlinlang.org/docs/functions.html
- Kotlin. *Strings* (templates with `$`). https://kotlinlang.org/docs/strings.html
- Our own measurement: Mac with Apple silicon, Nov 18, 2026 (the `verifica` on `inicio/` and `solucion/`; the two ways of breaking the state; the step 1 prediction; the intermediate state of step 2, with and without an erratum; compiler messages).

External review (Nov 18, 2026): WITH CHANGES, 3 high priority, 4 medium and 2 low; applied.
