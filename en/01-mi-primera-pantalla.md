# Lesson 01 — My first screen runs

**Before you read, seven words.** One line each; the series' other terms come in the lesson where they are used.

- **Kotlin:** the language you write the app in. Its files end in `.kt`.
- **Compose** (Jetpack Compose): Android's library for building screens by writing Kotlin functions.
- **Composable function:** a function marked with `@Composable` that describes a part of the screen.
- **Sketch:** the drawing of how the screen should look. It is what you compare against.
- **Modifier:** what tells an element how much room it takes and how much margin it leaves.
- **State:** a piece of data the screen remembers and that can change, like "the button was already tapped".
- **Simulated test:** a test that draws your screen in your computer's memory, without an emulator, and looks in it
  for what should be there.

**What you take away:** the store's welcome screen —name, tagline and button— written by you, and knowing how to read
a Compose screen line by line and say what each one changes on screen.

**This lesson's new idea (the only one that is assessed):** in Compose, a screen is a function that describes what
shows. There is no separate layout file: you read the function and you know what is going to appear.

**A note on the app's texts.** The course project ships its on-screen texts in Spanish, and the files in `lecciones/`
are not translated: the course's automatic checks (the `verifica`) look for those exact texts. So in code blocks,
terminal outputs and on-screen sketches the texts stay as the project has them, and in the prose the lesson quotes
them in English with the original in parentheses the first time: the name «The store» (in the project, «La tienda»),
the tagline «Your orders, in your pocket» («Tus órdenes, en tu bolsillo»), the button «Enter» («Entrar») and the
greeting «Hello!» («¡Hola!»).

---

## The scene

You ask Claude Code for the welcome screen. In a short time you have a short Kotlin file. It compiles. The tests come
out green. You install it, tap "Enter" and the greeting appears. You close the computer.

The next day someone opens the app next to the sketch and asks you: "why does the greeting come out above the button,
when here it goes below?" You open the file. You see `remember`, `mutableStateOf`, `Modifier`, an `if` in the middle
of the screen. You do not know which of those lines moves the greeting.

That code exists: it is the listing from exercise 1.3, and its tests come out 4 of 4 green with the greeting in the
wrong place (measured on November 18, 2026). Green says the greeting appears. It does not say where.

That is why the lesson goes in this order: you read a screen, you write one by hand, you ask Claude Code for a change
and, at the end, you explain —without help— what each block of what it generated does.

---

## Before you start

**What you need:**

| | Works | Does not work |
|---|---|---|
| Previous lesson | [00](00-ambiente-listo.md) finished: `ANDROID_HOME` defined and an emulator or your phone | Skipping it: without the SDK, the project does not build |
| Knowledge | Programming in some language and having built an interface | Kotlin and Compose are not assumed |

**The starter project.** From the root of the series repo:

```bash
git pull
cd lecciones/01-mi-primera-pantalla/inicio
```

That is where you work. Next to it is `solucion/`, with the screen solved: it is for comparing at the end, not for
copying.

**What the project already carries (scaffolding).** You do not write it and it is not assessed here, but it is worth
knowing why it is there:

| What it carries | Where | Why |
|---|---|---|
| `org.gradle.jvmargs=-Xmx4g` | `gradle.properties` | Gives 4 GB of memory to Gradle, the tool that builds. Without it, building with Firebase (lesson 03) failed with "Java heap space" (measured on Nov 13, 2026) |
| `gradlew` already generated | project root | The script that downloads and runs the exact Gradle version. Generating it with `gradle wrapper` **inside** a project with AGP 9.4 failed (measured on Nov 13). Do not regenerate it |
| `sdk=36` | `app/src/test/resources/robolectric.properties` | The simulated tests use a copy of Android 36. Without pinning it they took Android 37 and none ran (measured on Nov 13) |
| `jvmToolchain(25)` | `app/build.gradle.kts` | In the Nov 13 trial, Robolectric, when loading its Android 36, did not start with Java 17 and asked for Java 21 or higher. That is why the project pins 25 |

**The sketch.** This is what has to show when you finish:

```text
┌────────────────────────────────┐
│                                │
│           La tienda            │  ← name, in large type
│  Tus órdenes, en tu bolsillo   │  ← tagline
│          [ Entrar ]            │  ← button
│            ¡Hola!              │  ← appears only after tapping «Entrar»
│                                │
└────────────────────────────────┘
  Everything centered, mid-screen, with 24 dp of margin.
```

**dp** is Android's unit for sizes: roughly one pixel on a medium-density screen, adjusted to look the same size on
denser screens.

---

## Step 1 · Read a screen before running it

Open `app/src/main/java/mx/habil/ejemplo/tienda/Bienvenida.kt`. This is how it comes:

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

**Predict first.** At the repo root, create the answers folder if it does not exist yet (`mkdir -p respuestas`)
and, inside it, the file `respuestas/01.md`. That is where your predictions and explanations go. Before running
anything, write three lines: how many texts will show?, side by side or one below the other?, flush left or
centered?

To predict, read the function from the outside in:

- **`@Composable`** marks the function as one that describes screen.
- **`fun Bienvenida()`** is a Kotlin function. It returns nothing: it describes what should show.
- **`Column( … ) { … }`** arranges what it holds in vertical sequence: one below the other.
- **`Text("…")`** shows a text. Each call is one element.
- **`Modifier.fillMaxSize().padding(24.dp)`** says the column takes all the space it is given and leaves 24 dp of
  margin.
- **`verticalArrangement = Arrangement.Center`** puts the content at mid-height. Nobody said anything about the
  horizontal, and in a `Column` that starts, by default, at `Alignment.Start`: the side where text begins.

**Now run it.** With the emulator open or your phone connected:

```bash
./gradlew installDebug      # builds and installs; it does not open the app
```

Open it from the app drawer and compare with your three lines. If a prediction failed, do not erase it: write below
which line of the code would have given you the answer. On the trial emulator (Pixel 9, Android 37, Nov 18, 2026)
two texts were seen, «Aquí va tu pantalla» (Your screen goes here) and below it «Lección 01» (Lesson 01), at
mid-height and flush left.

**Second prediction.** What if you remove the `Column` and leave the two `Text` loose? According to Android's
documentation, Compose stacks one text on top of the other. Try it, write down what you see and put the `Column`
back.

**Who calls `Bienvenida()`?** `MainActivity.kt`, in the same folder: its line `setContent { Bienvenida() }` says
which function draws the screen. You do not change it.

**What proves it:** your written prediction, compared with the run (objective 1.1).

---

## Step 2 · Kotlin: what you already know under another name

It is enough to recognize three things you already use in another language.

| In Kotlin | What it is | Where you see it |
|---|---|---|
| `val nombre = "La tienda"` | A variable that is assigned once and never changes | `val pantalla = …` in `BienvenidaTest.kt` |
| `var saludando = false` | A variable that can change | You write it in step 4 |
| `fun doble(n: Int): Int { return n * 2 }` | A function: name, parameters with their type and return type | `fun Bienvenida()` |
| `fun doble(n: Int) = n * 2` | The same, short: the body is an expression and the return type is inferred | |
| `Bundle?` | A type that **can** be null. Without the `?`, it cannot | `savedInstanceState: Bundle?` in `MainActivity.kt` |

Three details that do change compared with other languages:

- **The type goes after the name:** `n: Int`. On a variable you almost never write it: in `val x = 5`, Kotlin
  deduces it is `Int`.
- **Nulls are declared.** A `String` variable cannot hold `null`. If it can be missing, the type is `String?`,
  and then `lema.length` is a compile error; `lema?.length` returns `null` if `lema` is null; and
  `lema?.length ?: 0` returns `0` in that case.
- **The braces at the end are a function you pass to another.** In `Column( … ) { … }`, what goes between braces is
  a function without a name (a *lambda*). If the last parameter of a function is another function, Kotlin lets you
  write it outside the parentheses. That is why Compose looks like nested blocks.

**What proves it:** review 1.4, in "Your exercises".

---

## Step 3 · Write the welcome, by hand

This step is yours, without Claude Code: it is about ten lines.

**Predict first.** How many calls to composable functions will you need inside the `Column`, and which of them is
going to hold another one inside?

In `Bienvenida.kt`:

1. **Add three lines to the `import` block.** An `import` tells Kotlin which library each name comes from:

```kotlin
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
```

2. **Center the horizontal.** Put a comma after `Arrangement.Center` and, below it, the parameter that was missing:

```kotlin
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
```

3. **Replace the two texts with the three elements of the sketch:**

```kotlin
        Text("La tienda", style = MaterialTheme.typography.headlineLarge)
        Text("Tus órdenes, en tu bolsillo")
        Button(onClick = { }) {
            Text("Entrar")
        }
```

`style = MaterialTheme.typography.headlineLarge` gives the name one of the type sizes of Material, Android's set of
visual components. `Button` takes two things: `onClick`, the function called when someone taps it —empty for
now—, and, between braces, what shows inside the button, which is another `Text`.

**Run the `verifica`.** From the repo root:

```bash
./lecciones/01-mi-primera-pantalla/verifica
```

It does not change your code: it runs the simulated tests (`./gradlew testDebugUnitTest`) and translates the result
for you. The first time it needs network and takes a while, because it downloads the testing library (Robolectric)
and a copy of Android: in the Nov 13 trial it was 1 minute 21 seconds on a Mac; the following ones, seconds.

**You are on track** if you see three green marks and one red:

```text
✅ Simulado: la pantalla muestra el nombre «La tienda»
✅ Simulado: la pantalla muestra el lema «Tus órdenes, en tu bolsillo»
✅ Simulado: hay un elemento «Entrar» con acción de toque y rol de botón
❌ Al tocar el botón «Entrar» no aparece «¡Hola!» (o ya estaba desde antes): ese cambio lo pides en el paso 4
Resultado: 3 de 4 pruebas en verde (1 en rojo, 0 con error, 0 saltadas), leído de …
```

*(That is the `verifica`'s output, exactly as the script prints it, in Spanish. In English it says: the screen shows
the name; the screen shows the tagline; there is an "Enter" element with a tap action and a button role; and, red,
"tapping the 'Enter' button does not show 'Hello!' (or it was already there): you ask for that change in step 4";
and the result, 3 of 4 tests green.)*

That red is the expected one: the button does nothing yet. The mark for `respuestas/01.md`, which you have not filled
in yet, also comes out red.

**What each mark proves, and what it does not.** "Simulado" (simulated) means the test drew your function in memory
and found that text, and that "Enter" can be tapped and is declared a button (a `Text` to which you only add a tap
does not pass). It does not prove the type size, the margin, the centering or the order: you see that yourself, with
`./gradlew installDebug` and the sketch beside it.

**What proves it:** the simulated `verifica` finds the three elements **and** you compared the installed app with the
sketch (objective 1.2).

---

## Step 4 · With Claude Code: make the button greet

This is where the tool comes in, and it always comes in the same way: **you predict → you ask → you read what it
generated → you run the `verifica` → you explain it in your own words.**

1. **Predict.** Write in `respuestas/01.md`: for "Hello!" to appear only after tapping the button, what does the
   screen have to remember? Which of the lines you already wrote is going to change?
2. **Ask** Claude Code, standing in `inicio/`: "In `Bienvenida.kt`, make it so that tapping 'Entrar' shows the text
   '¡Hola!' below the button. Before tapping it must not show. Do not change anything else or touch the tests."
3. **Read** the whole change before accepting it. It does not have to be identical to this, but it must have these
   three pieces:

```kotlin
    var saludando by remember { mutableStateOf(false) }      // 1. the state
    …
        Button(onClick = { saludando = true }) {             // 2. the event
            Text("Entrar")
        }
        if (saludando) {                                     // 3. the screen reads the state
            Text("¡Hola!")
        }
```

4. **Run the `verifica`.** Now the four tests must come out green.
5. **Explain**, in two lines of `respuestas/01.md`, what happens between tapping the button and the greeting
   appearing.

Today you do not design state from scratch: you only trace the change Claude Code generated. Which state is worth
keeping and how to preserve it is the central idea of L02. To trace it you need to know what each piece does:

- **`mutableStateOf(false)`** creates a value Compose watches. When it changes, Compose schedules the update of the
  part of the screen that read it. That is called **recomposition**.
- **`remember { … }`** keeps that value for as long as this screen stays alive. It is not saving to disk: if Android
  recreates the screen, it goes back to `false`.
- **`by`** lets you use `saludando` as a normal variable. It asks for two `import`s:
  `androidx.compose.runtime.getValue` and `androidx.compose.runtime.setValue`.
- **`onClick = { saludando = true }`** is the event: it only changes the state. It draws nothing.
- **`if (saludando) { Text("¡Hola!") }`** is normal Kotlin inside the screen: if the state is `true`, that text
  exists; if not, it does not.

The lesson's idea, complete: you tap → the state changes → Compose updates what read that state, which here is the
`if` → the screen's description now includes the greeting. Nobody "adds" the text. And Compose can re-run only what
is needed: do not write code assuming the whole function will run.

**If a line is still not clear,** pause here: ask Claude Code to explain it, check it with a small change and the
`verifica`, and continue when you can say it in your own words.

---

## Step 5 · Without AI: explain five blocks and fix one

This is exercise 1.3 and it is done **without Claude Code**, on your own `Bienvenida.kt`. If yours already meets the
sketch, use this listing for the correction, which is one possible answer to the same request:

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

With this listing the `verifica` gives 4 of 4 green (measured on Nov 18, 2026). And even so it does not meet the
sketch.

**You do two things, in writing, in `respuestas/01.md`:**

1. **Explain five blocks,** one of each kind: **state**, **text**, **modifier**, **event** and **function**.
   For each one write its line in the format `Bienvenida.kt:21` and what changes **on screen** if you remove or
   alter it. "Stores a boolean" is not a visible effect; "without this line the greeting never appears" is.
2. **Fix the block that does not meet the sketch.** Say which one it is and why the tests do not detect it, fix it,
   run the `verifica` and install the app again.

The `verifica` only checks that you wrote down at least five references in the format `Bienvenida.kt:N`; it does not
assess your explanations or the fix. And a 4 of 4 confirms behavior, not how it looks. 1.3 is finished when you can
tick all four:

- [ ] All five kinds are there: state, text, modifier, event and function.
- [ ] Each one states an effect that shows on screen.
- [ ] You explained why the greeting came out above and why the tests did not detect it.
- [ ] After moving it, the 4 of 4 is still green **and** in the installed app you saw «¡Hola!» below «Entrar». Write
      that observation down next to the fix.

**What proves it:** exercise 1.3 (objective 1.3).

---

## Your exercises

1.2 and 1.3 are done **without AI**.

| # | You do | It is done when |
|---|---|---|
| 1.1 | You predict what the step 1 function draws and run it | You have your three lines and what you saw |
| 1.2 | You write name, tagline and button from the sketch | The `verifica` marks the three elements green **and** you compared the installed app with the sketch: group centered and name in large type |
| 1.3 | You explain five blocks and fix the one that does not meet the sketch | You ticked the four boxes of step 5: the 4 of 4 is still green **and** you saw the greeting below the button in the installed app |
| 1.4 | You answer the Kotlin review | Four of five correct, **each one with its why** |

**Review 1.4.** Answer without running the code. The `verifica` does not grade your predictions or this review:
compare them yourself with "Compare your answers" and keep what you wrote.

1. `val nombre = "La tienda"` and, on the next line, `nombre = "Otra"`. Does it compile?
2. `var saludando = false`. Nobody wrote the type. What is it and who decided it?
3. `fun doble(n: Int) = n * 2`. What does `doble(4)` return and of what type?
4. In `MainActivity.kt` it says `savedInstanceState: Bundle?`. What does that `?` tell you?
5. `val lema: String? = null`. What happens with `lema.length`, with `lema?.length` and with `lema?.length ?: 0`?

**Time:** allow about two hours, with the build and your predictions. It is an estimate: we adjust it with how long
the first cohort takes.

---

## Not running?

The messages in English are the ones the series' compiler gave (Kotlin 2.4.21) on Nov 18, 2026.

| You see | Probable cause | What you do |
|---|---|---|
| `Unresolved reference 'Button'` | The `import` for that name is missing, or misspelled | Add the step 3 `import` and check capitalization |
| `'val' cannot be reassigned` | You declared the state with `val` and then change it | It is `var saludando by remember { … }` |
| `Property delegate must have a 'getValue(…)' method` | You used `by` without its two `import`s | Add `getValue` and `setValue` (step 4) |
| «Las pruebas no llegaron a correr» (the tests did not get to run) | The code does not compile | Read the line that starts with `e:`: it carries file, line and column |
| ❌ on the button, with «Entrar» visible | It is a `Text`, loose or with `clickable`: it is not declared a button | It has to be a `Button` with the `Text` inside |
| ❌ on the greeting, even though «¡Hola!» is visible | The greeting is always there, not only after tapping | It goes inside the `if (saludando)` |
| `BienvenidaTest.kt` changed | Someone (you or Claude Code) touched the tests | Recover it with the command the message gives and repeat the request asking it not to touch them |

On Linux Mint 22.3 (x86_64), this lesson's `verifica` came out green on the solution: 4 of 4, in 64 seconds counting
the first build (Nov 18, 2026). If your SDK does not carry `build-tools`, Gradle downloads it by itself the first
time.

---

## To wrap up

**Answer without reading again:**

1. What is a screen in Compose, in one sentence?
2. You tap «Entrar» and «¡Hola!» appears. What happened in between, in order?
3. Your four simulated tests are green. Say one thing that proves and one thing it does not.

**What comes next:** in L02 the app has more than one screen and moves from one to another.

---

## Compare your answers

**Step 1.** Two texts, one below the other (`Column`), at mid-height (`Arrangement.Center`) and on the left side
(nobody specified the horizontal alignment).

**Step 3.** Four calls: `Text`, `Text`, `Button` and, inside the button, another `Text`.

**Exercise 1.3**, on the step 5 listing:

| Block | Line | Visible effect |
|---|---|---|
| State | `Bienvenida.kt:21` | Without it the greeting cannot appear after the tap |
| Text | `Bienvenida.kt:28` | It is the name; without `style` it would look the same size as the tagline |
| Modifier | `Bienvenida.kt:24` | Without `fillMaxSize()` the column takes only what its content measures: there is no space left to center in |
| Event | `Bienvenida.kt:33` | With `onClick = { }` the button can be tapped, but nothing happens |
| Function | `Bienvenida.kt:20` | It is the whole screen; `MainActivity` calls it |

The block that does not meet the sketch is the one on lines 30 to 32: it is **before** the button, so the greeting
comes out above «Entrar» and the sketch asks for it below. It is fixed by moving those three lines after line 35. The
tests do not detect it because they look for the text to exist and be visible, not for what position it is in.

**Review 1.4:**

1. No. A `val` cannot be reassigned; the compiler says `'val' cannot be reassigned`.
2. `Boolean`. Kotlin deduced it from the value `false`.
3. It returns `8`, of type `Int`: the body is an expression and the return type is inferred.
4. That that parameter can arrive as `null`, and Kotlin does not let you use it without considering that case.
5. `lema.length` does not compile; `lema?.length` gives `null`; `lema?.length ?: 0` gives `0`.

**To wrap up:**

1. A function marked with `@Composable` that describes what shows.
2. The `onClick` changed the state to `true`; Compose ran the function again; the `if` now does include the text.
3. It proves that the three elements and the greeting exist and that the button responds. It does not prove how they
   look or in what order they are.

---

## Lesson sources

Opened on Nov 18, 2026.

- Android Developers. *Thinking in Compose*. https://developer.android.com/develop/ui/compose/mental-model
- Android Developers. *Jetpack Compose Tutorial*. https://developer.android.com/develop/ui/compose/tutorial
- Android Developers. *Compose layout basics*. https://developer.android.com/develop/ui/compose/layouts/basics
- Android Developers. *Column* (default values). https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/Column.composable
- Android Developers. *Compose modifiers*. https://developer.android.com/develop/ui/compose/modifiers
- Android Developers. *State and Jetpack Compose*. https://developer.android.com/develop/ui/compose/state
- Android Developers. *Button*. https://developer.android.com/develop/ui/compose/components/button
- Android Developers. *Style text* and *Material Design 3 in Compose*. https://developer.android.com/develop/ui/compose/text/style-text · https://developer.android.com/develop/ui/compose/designsystems/material3
- Android Developers. *Support different pixel densities* (dp). https://developer.android.com/training/multiscreen/screendensities
- Android Developers. *Test your Compose layout*. https://developer.android.com/develop/ui/compose/testing
- Android Developers. *Role* and *SemanticsMatcher* (button role in tests). https://developer.android.com/reference/kotlin/androidx/compose/ui/semantics/Role · https://developer.android.com/reference/kotlin/androidx/compose/ui/test/SemanticsMatcher
- Android Developers. *Robolectric strategies*. https://developer.android.com/training/testing/local-tests/robolectric
- Kotlin. *Basic syntax*. https://kotlinlang.org/docs/basic-syntax.html
- Kotlin. *Null safety*. https://kotlinlang.org/docs/null-safety.html
- Kotlin. *Higher-order functions and lambdas*. https://kotlinlang.org/docs/lambdas.html
- Our own measurement: trial on a Mac with Apple silicon, Nov 13, 2026 (Gradle memory, `gradle wrapper`, Java and Android for the simulated tests) and Nov 18, 2026 (the `verifica` on `inicio/`, `solucion/` and the 1.3 listing; compiler messages).

External review (Nov 18, 2026): WITH CHANGES, 3 high priority, 5 medium and 2 low; applied.
