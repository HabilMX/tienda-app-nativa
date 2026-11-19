# Lesson 05 — Signing in with Microsoft

**Before you read, five words.** One line each; the rest arrive at the step where they are used.

- **Microsoft Entra:** the Microsoft service where the apps that allow signing in with a Microsoft account are
  registered. Its console is at `entra.microsoft.com`.
- **Directory:** the Entra space that belongs to a person or to an organization. An app is registered inside a
  directory. The documentation calls it a *tenant*.
- **Return address:** the web address Microsoft sends the person to when they finish signing in. The documentation
  calls it a *redirect URI*.
- **Client secret:** a password for the app, not for a person. With it, whoever holds it identifies themselves to
  Microsoft as that app. The documentation calls it a *client secret*.
- **Pending result:** the answer to a sign-in that finished when your app was no longer there to receive it.

**What you take away:** the «Sign in with Microsoft» button next to Google's, written by you, which picks up the
sign-in even if Android closed your app halfway; your app registered in Entra without its secret touching a single
file; and a table of your own that says what is the same and what changes between the two providers.

**This lesson's new idea:** a second provider that is registered outside Firebase, with an exact return address and
a secret that never touches the app. The rest —the libraries, the browser, the third «gate» and the second button
on the screen— comes ready-made and explained, and is not assessed here.

**A note on the app's texts.** As in the previous lessons, the project ships its on-screen texts in Spanish, and the
`verifica` looks for those exact texts. In code blocks, terminal outputs and on-screen sketches they stay as the
project has them; in the prose the lesson quotes them in English with the original in parentheses the first time:
the button «Sign in with Microsoft» («Entrar con Microsoft») and the screen «The store» («La tienda»).

---

## The scene

Microsoft's page is open on the phone. The person has not yet chosen their account. Behind that page, your app no
longer exists: Android closed it.

The page is still there, waiting.

That was caused on purpose in this series' trial, on November 13, 2026, on an emulator. With Microsoft's page in
front, the app's process was killed from the terminal, as Android would do if it needed memory. Then the account was
chosen. Android started the app again from scratch, without anything it had in memory.

The person had already signed in. The question is whether the app finds out, or asks them to sign in again.

In the trial it found out, because on starting it asked one thing before anything else: was a sign-in left halfway?
That question is one line, and this lesson writes it. It also writes what comes before: registering your app with
Microsoft, which is not done in Firebase, and looking after a secret that is only shown once.

---

## Before you start

**What you need:** [04](04-entrar-con-google.md) finished (its `verifica` at 20 of 20) and the environment from
[00](00-ambiente-listo.md). For step 7 an open emulator or your connected phone is enough. What is said under
"Account and trusted machine" in 00 applies.

**For steps 8 and 9, in addition:** a Microsoft account to sign in with and an Entra directory where you can
register an app. They are two different things, and the second is the one that is hard to get:

- Microsoft's guide asks for an Azure account with an active subscription and the *Application Developer* role.
- In the Nov 13, 2026 trial, a personal account that **already had a directory** registered the app through
  `entra.microsoft.com`, without a subscription and without a card.
- That day, a **newly created** account could not: «New registration» (in the Spanish-language console, «Nuevo
  registro») opened a panel without a form. It opened after activating Azure's free trial, which asked for a
  verified phone, a billing address and a card.
- If you are a student, check whether you qualify for Azure for Students. Its page says it does not ask for a card,
  that it verifies with your school email and that it does not apply to massive online courses or to training from
  for-profit organizations. This lesson does not claim that you qualify.

**If you do not have a directory,** you do steps 1 to 7 in full, which need no account. Steps 8 and 9 wait until you
have one.

**The starter project.** From the root of the series repo:

```bash
git pull
cp lecciones/03-la-app-conoce-a-firebase/inicio/app/google-services.json lecciones/05-entrar-con-microsoft/inicio/app/
cd lecciones/05-entrar-con-microsoft/inicio
```

The second command takes your file from 03 to this lesson's project. There it is also ignored by git. Next to it
is `solucion/`: you can read it without configuring anything, and **`./verifica solucion` comes out red on a clean
clone**, because without your file the project does not build (measured on Nov 18, 2026: 0 tests run). Your answers
go in `respuestas/05.md`, at the repo root.

**What the project already carries (scaffolding).** It is not assessed here; step 3 walks through it:

| What it carries | Where | Why |
|---|---|---|
| Everything lesson 04 left: `EntradaConGoogle`, its two gates, its tests and its doubles | The same files, without a single change | The `verifica` compares them, byte by byte, against 04's solution |
| A fifth result, `CuentaConOtroProveedor` (account with another provider), and the `EntradaQueSeRetoma` (sign-in that can be resumed) interface | `Entrada.kt` | With Microsoft there is a new way of not getting in, and a sign-in that can be left halfway |
| The third gate, with its real implementation | `PuertaDelNavegador.kt` | That is where your code goes out to the browser and to Firebase |
| The second button and the collision message | `PantallaEntrada.kt`, `Tienda.kt` | These two **did** change with respect to 04. They come given: you touch no screen |
| `MainActivity`, which assembles the pieces and passes them to `Tienda` | `MainActivity.kt` | It comes given and is not changed: the `verifica` compares it against the lesson's |
| Where it is decided which piece gets assembled; for now, one that does nothing | `Armado.kt` | It is the file where you connect what you write |
| This lesson's tests, their doubles and a "test app" | `app/src/test`, `app/src/androidTest` | They are the ones the `verifica` runs. Do not change them |
| A skeleton of `EntradaConMicrosoft` | `EntradaConMicrosoft.kt` | It is the file you write |

The version catalog is 04's, with no new libraries: Microsoft comes inside Firebase Authentication.

**What this lesson does not do.** It does not link Google and Microsoft into a single user: here the app recognizes
the collision and says which button to sign in with; linking is [06](06-dos-botones-una-cuenta.md). It does not
save or close the session (lesson 07), and it does not tell the first time apart (lesson 08).

---

## Step 1 · Without AI: the same journey, another way

This step is yours, without Claude Code. It is exercise 5.3.

In 04, your app asked the phone for a token and took it to Firebase. With Microsoft, your app carries nothing: it
opens the browser and waits.

| # | From whom to whom | What travels |
|---|---|---|
| 1 | Your app → Firebase (its library) | A request: "I want to sign in with the `microsoft.com` provider" |
| 2 | Firebase → the browser | It opens Microsoft's page, with your registration's client ID and your return address |
| 3 | The person → Microsoft | Their account and whatever Microsoft asks of them. It happens in the browser, outside your app |
| 4 | Microsoft → Firebase (its servers) | A single-use code, sent to your return address |
| 5 | Firebase → Microsoft | That code, together with the client ID **and the secret** you saved in Firebase's console |
| 6 | Firebase → your app | A user, with their identifier in your project and, if the provider delivered it, their name |

Row 5 is the one that did not exist with Google. Firebase's guide says it like this: Firebase handles the whole flow
and the exchange of the code using the client ID and secret configured in its console. And Microsoft's
documentation explains why that secret does not go in the app: it asks not to use it in a native app, because on a
device it cannot be stored reliably.

That is why row 2 demands exactness. Microsoft's documentation says its server only sends the person to an address
that is added to the app's registration, and that the request's address must match one of the registered ones
exactly. If it does not match, it answers with an error.

**Predict** before going on, in `respuestas/05.md`: of the six rows, in which does the code you are going to write
take part?

**Make your table.** In `respuestas/05.md`, compare the two providers with these five questions. Fill it in without
looking at "Compare your answers" and then compare:

1. Who registers the app with the provider, and in which console?
2. Which piece shows the person where to sign in?
3. What does your app receive from the provider?
4. Is there a secret? Where does it live?
5. What has to match exactly for it to work?

**What proves it:** your table, compared with the lesson's (objective 5.3). No test reads it.

---

## Step 2 · The data rule, before touching anything

In 04 the rule was: nobody prints, pastes or saves the token. Here it extends to three more things.

- **The client secret is born in Entra and is pasted straight into Firebase.** It does not pass through the code, a
  file, `respuestas/05.md`, the terminal, a screenshot or a message. Firebase uses it on the side of its service,
  never on the phone. Of it, only its **expiry date** is written down.
- **Microsoft's access token is not read.** Firebase's result carries one, with which Microsoft services could be
  called on the person's behalf. From that result only the name comes out.
- **The Entra client ID does not go in the code.** It is not a secret, but the app does not need it: Firebase uses
  it. In your step 8 table you do write it down.

If the secret ever touches a file, the terminal or a screenshot, it is no longer secret: it is deleted in Entra and
another one is created. Deleting the file is not enough. Add it, with the access tokens, to the list of what is never
uploaded.

---

## Step 3 · What the project already carries

Open these files; do not edit them yet. You will see three new Kotlin pieces:

- **`String?`:** a type that ends in `?` accepts `null`. `String?` is "a text, or nothing".
- **`a ?: b`:** if `a` is not `null`, it returns `a`; if it is, it returns `b`.
- **`when { … }`:** a list of conditions; it takes the first one that holds.

**`Entrada.kt`: two new things.** `Resultado` has a fifth case, `CuentaConOtroProveedor`. And there is one more
interface:

```kotlin
interface EntradaQueSeRetoma : Entrada {
    suspend fun retomar(): Resultado?
}
```

It is an `Entrada` that, besides `entrar()`, knows how to answer whether something was left halfway. It returns a
`Resultado`, or `null` if nothing was left.

**`PuertaDelNavegador.kt`: the third gate.**

```kotlin
interface NavegadorDeProveedor {
    suspend fun pendiente(): String?
    suspend fun abrir(proveedor: OAuthProvider): String
}
```

`abrir` opens the browser and returns the name of whoever signed in; its real version calls
`startActivityForSignInWithProvider`. `pendiente` asks about a sign-in left halfway; its real version reads
`pendingAuthResult`. They are the two calls in Firebase's guide. According to the reference, that result exists when
a sign-in was started through the web and the system cleaned up the screen that was waiting for it; it carries the
same success or the same exception the sign-in would have given; and for a sign-in it is delivered **only once**.

This gate only delivers a name. It is step 2's data rule turned into code: the access token does not leave there.

**`PantallaEntrada.kt` and `Tienda.kt`: the second button.** `Tienda` now receives a second, optional sign-in. If it
is not passed one, there is no second button and everything is as in 04; that is why 04's tests keep passing without
touching them. And when it opens it asks whether something was left halfway:

```kotlin
val pendiente = entradaConMicrosoft?.retomar()
if (pendiente != null) recibe(pendiente)
```

Those lines go inside a `LaunchedEffect`, which is Compose's way of saying "do this once on appearing, not every
time the screen is redrawn". It carries a key: if the key changes, Compose cancels the work and starts it again,
according to its documentation. Here the key never changes, so the question is asked only once, when the app opens.

**`MainActivity.kt` and `Armado.kt`.** `onCreate` assembles the pieces and passes them to `Tienda`. Which piece is
assembled for Microsoft is decided by a function in `Armado.kt`, which for now returns `MicrosoftSinConectar()`
(Microsoft not connected): it lets nobody through. You change `Armado.kt`; `MainActivity.kt` is not touched.

**The tests.** On your computer, Firebase does not prepare itself as it does on a phone: without help, the tests gave
"Default FirebaseApp is not initialized" (measured on Nov 18, 2026). That is why `src/test` carries `AppDePruebas`
(test app), which prepares it by hand. Preparing is not connecting.

**Measure the starting point.** From the repo root:

```bash
./lecciones/05-entrar-con-microsoft/verifica
```

On Nov 18, 2026, with a complete invented file, the last line was
`Resultado: 23 de 44 pruebas en verde (21 en rojo, 0 con error, 0 saltadas)`. There are 44 because the 20 from
lesson 04 are included, plus 24 new ones. The 20 from 04 are already green, and three of the new ones too: they
belong to the scaffolding. The other 21 wait for your code.

*(Result: 23 of 44 tests green: 21 red, 0 with error, 0 skipped.)*

---

## Step 4 · With Claude Code: sign in and resume

This is where the tool comes in, and it always comes in the same way: **you predict → you ask → you read what it
generated → you run the `verifica` → you explain it in your own words.**

1. **Predict.** Write in `respuestas/05.md`: if when the app opens no sign-in was left halfway, what must
   `retomar()` return, and what must happen on the screen?
2. **Ask** Claude Code, standing in `inicio/`, for the second sign-in taking the first as a reference. This is
   exercise 5.4, and the request is short on purpose: "Take `EntradaConGoogle.kt` as a reference and write
   `EntradaConMicrosoft.kt` only for the case where everything goes well, with `retomar()` and `entrar()`, using
   the `navegador` gate. Do not catch exceptions yet. In `Armado.kt`, change only the body of
   `armarEntradaConMicrosoft` so that it returns `EntradaConMicrosoft` with the gate it receives, and delete
   `MicrosoftSinConectar` there. Do not change any other file."
3. **Read** the whole change before accepting it and **list what it did not copy well.** The first sign-in is a good
   reference for the style and a bad one for the substance: Google hands over a token and Microsoft does not. Answer
   yes or no to the eight questions in "Compare your answers", exercise 5.4, and write in `respuestas/05.md` each
   "no" with its file and line. Then ask for the correction of each one, saying why. If all eight are "yes", write
   "No encontré desvíos en las ocho revisiones" (I found no deviations in the eight checks) and keep the table: do
   not invent an error. The core must end up looking like this:

```kotlin
override suspend fun retomar(): Resultado? {
    val nombre = navegador.pendiente()
    return if (nombre == null) null else Resultado.Entro(nombre)
}

override suspend fun entrar(): Resultado {
    // Rows 1 and 2: the request, with the provider.
    val proveedor = OAuthProvider.newBuilder("microsoft.com")
        .addCustomParameter("prompt", "select_account")
        .build()
    // First, what is pending. Only if there is none (null) is the browser opened.
    val nombre = navegador.pendiente() ?: navegador.abrir(proveedor)
    return Resultado.Entro(nombre)
}
```

4. **Run the `verifica`.** With that code it gave `Resultado: 32 de 44 pruebas en verde` (32 of 44 tests green) on
   Nov 18, 2026. The reds are the ways of not getting in: step 5.
5. **Explain**, in three lines of `respuestas/05.md`: why `entrar()` asks about what is pending if `retomar()`
   already did it when the app opened.

**What does `select_account` do?** According to Microsoft's documentation, it interrupts automatic sign-in and shows
the list of accounts to choose from. Microsoft marks it as optional: this lesson recommends it, and it is the value
the Nov 13, 2026 trial used. It is not a condition for passing: no test reads that parameter, and if it is missing,
the `verifica` only warns.

**What it proves, and how far.** At the simulated level, the `verifica` proves that a tap opens the browser a single
time and with the `microsoft.com` provider; that with something halfway it is not opened again; and that
`retomar()` returns who signed in or `null`. Another test opens `MainActivity`, hands the fake gate to
`crearEntradaConMicrosoft` and reads which provider was requested.

The gate was fake: no browser was opened. "Something was left halfway" was said by the double; no test closed the
app.

**And does the installed app use your piece?** No test sees it from end to end. There are two backups:

- `MainActivity.kt` has to be identical to the lesson's. If you change it, the `verifica` marks it red even though
  all 44 tests are green (measured on Nov 18, 2026).
- If `Armado.kt` assembles something else, a test turns red (43 of 44).

The «Texto:» marks only look for words in your files. The complete proof is the real level, in step 9.

---

## Step 5 · Without AI: the three ways of not getting in

This step is done **without Claude Code**. It is a function with three branches, and you write it.

When the browser's gate cannot deliver a name, it throws a Firebase exception. Firebase's reference lists the ones
for this call. Three groups matter here:

| Exception | When, according to the reference | Your sign-in answers |
|---|---|---|
| `FirebaseAuthUserCollisionException` with the code `ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL` | That email already has a user with another way of signing in. It only happens with the "one account per email address" option on (lesson 06) | `Resultado.CuentaConOtroProveedor` |
| `FirebaseAuthWebException` with the code `ERROR_WEB_CONTEXT_CANCELED` | The person canceled the pending operation | `Resultado.Cancelada` |
| Any other, including those two classes with another code | The reference names another web operation in progress, an unsupported browser, a failure inside the page and a misconfigured provider. Everything else that fails also lands here | `Resultado.Fallo` |

In the Nov 13, 2026 trial, someone signed in with a Microsoft account whose email had already signed in with Google,
and Firebase answered with that exception and this text: "An account already exists with the same email address but
different sign-in credentials".

The class is not enough. The same exception carries two other codes, and the reference asks you to check the code to
know the cause: only this one means "sign in with the other button". Another collision is shown as a failure,
without inventing a cause for it.

**1. Predict.** The first two rows name different classes. If in the second you remove the code condition and leave
only "it is a `FirebaseAuthWebException`", which message will a person see whose page failed, without having
canceled anything?

**2. Write it.** Add this function inside your class:

```kotlin
private fun resultadoDe(falla: Exception): Resultado = when {
    falla is FirebaseAuthUserCollisionException &&
        falla.errorCode == "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" ->
        Resultado.CuentaConOtroProveedor
    falla is FirebaseAuthWebException && falla.errorCode == "ERROR_WEB_CONTEXT_CANCELED" ->
        Resultado.Cancelada
    else -> Resultado.Fallo
}
```

And wrap the body of `retomar()` and the one of `entrar()` with `try { … }` and these two `catch` blocks, in this
order:

```kotlin
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    resultadoDe(e)
}
```

The first is the one from 04: `CancellationException` is not a failure of the sign-in, it is Kotlin telling you the
task was canceled, and it is let through. `retomar()` carries the same `catch` blocks because, according to the
reference, the pending result also carries the exceptions the sign-in would have ended with.

**3. Run the `verifica`.** Now the line is `Resultado: 44 de 44 pruebas en verde` (result: 44 of 44 tests green).
One mark stays red, the one for `respuestas/05.md`: it gets filled in step 6.

**4. Check your prediction.** Remove `&& falla.errorCode == "ERROR_WEB_CONTEXT_CANCELED"` and run the `verifica`:
two more tests turn red, the two for the «error» case (measured on Nov 18, 2026 on the solution: 42 of 44). Put it
back. If what you remove is the collision's code condition, one turns red (43 of 44).

The texts the person sees are already in `mensajeDe`:

| Result | Message |
|---|---|
| `Cancelada` | «Cerraste la ventana sin elegir cuenta. Toca el botón cuando quieras intentar otra vez.» (You closed the window without choosing an account. Tap the button whenever you want to try again.) |
| `Fallo` | «No se pudo entrar. Revisa tu conexión y vuelve a intentar.» (Could not sign in. Check your connection and try again.) |
| `CuentaConOtroProveedor` | «Ese correo ya entró a la tienda con otro botón. Entra con el que usaste la primera vez.» (That email already signed in to the store with another button. Sign in with the one you used the first time.) |

The first two are those from 04. The third does not say "Google" on purpose: the exception does not carry which
provider the user was created with.

**What proves it:** the simulated `verifica`, the three cases. Each case carries tests of two kinds: some throw the
exception at your class and read what it answered; another draws the screen with your class and looks for its
message. The exceptions were assembled by a double, with codes written by hand: none came from Firebase. Which
exception a real phone gives when the browser tab is closed we have not yet measured with an account.

---

## Step 6 · Without AI: break one on purpose

Also **without Claude Code**. You are going to cause the scene's bad ending.

**1. Predict.** You are going to make `retomar()` always answer `null`, as if nothing was ever left halfway. Does it
compile? Does the Microsoft button keep working? How many tests turn red?

**2. Break it and measure.** In `EntradaConMicrosoft.kt`, change the two lines of the body of `retomar()` to a
plain `null`, inside the `try`. Run the `verifica`. This is what it gave on Nov 18, 2026 (trimmed):

```text
❌ Tu entrada no retoma: quedó una entrada a medias y retomar() no la recogió (paso 4)
❌ Retomar: 1 de 4 pruebas en verde. Revisa retomar(), que entrar() pregunte primero por lo pendiente, y …
❌ Al abrir la app: 0 de 3 pruebas en verde. Tienda.kt viene dado y llama a retomar(): revisa lo que …
Resultado: 37 de 44 pruebas en verde (7 en rojo, 0 con error, 0 saltadas), leído de …
```

*(In English the marks say: your sign-in does not resume: a sign-in was left halfway and `retomar()` did not pick it
up (step 4); Resume: 1 of 4 tests green, check `retomar()`, and that `entrar()` asks first about what is pending;
On opening the app: 0 of 3 tests green, `Tienda.kt` comes given and calls `retomar()`. Result: 37 of 44 tests green,
7 red.)*

Copy into `respuestas/05.md` the line that says «Tu entrada no retoma» (Your sign-in does not resume).

**3. What those tests do not tell you.** It compiles, and the button's tests stay green: tapping «Sign in with
Microsoft» leads to the home screen all the same. The defect only exists if Android closes the app mid-sign-in, and
that does not happen in a test that lasts one second. On a phone it was measured separately, on Nov 13, 2026, with
trial code:

- With Microsoft's page in front, the app's process was killed with `adb shell am kill` and then the account was
  chosen: Android started the app again and the pending result delivered the sign-in.
- The developer option "Don't keep activities" did **not** work for testing it: it destroys the screen, but the
  process stays alive and finishes the sign-in on its own. To see the case you have to kill the process, not the
  screen.

**4. Fix it.** Put the two lines of `retomar()` back. Run the `verifica`: 44 of 44, and now also the
`respuestas/05.md` mark.

The exercise is finished when you can tick all three:

- [ ] Your prediction from point 1 is written, with what you saw below it.
- [ ] You pasted the line from the red.
- [ ] You said why the button's tests stayed green with code that, on a phone with little memory, would make the
      person sign in twice.

**What proves it:** "Break one on purpose". It is the basis of objective 5.2: knowing what `retomar()` picks up.

---

## Step 7 · Inside Android: the instrumented level

With the emulator open, from the repo root:

```bash
./lecciones/05-entrar-con-microsoft/verifica inicio instrumentado
```

It repeats the simulated level and then installs the app and its test app on the device. There it draws `Tienda`
with your `EntradaConMicrosoft` and the same fake gate, taps the buttons and looks for the texts. With the solution,
on a new emulator without an account (Pixel 9, `google_apis_playstore` image of Android 37), this gave on
Nov 18, 2026 (trimmed):

```text
✅ Instrumentado: dentro de Android, con la puerta de mentira, tocar «Entrar con Microsoft» lleva al inicio …
✅ Instrumentado: dentro de Android, cancelación, error y colisión muestran cada uno su mensaje sin pasar …
✅ Instrumentado: dentro de Android, con una entrada a medias que contesta el doble, la app llega al inicio …
✅ Instrumentado: dentro de Android, las 4 pruebas del botón de Google de la lección 04 siguen en verde
Resultado: 44 de 44 pruebas en verde (0 en rojo, 0 con error, 0 saltadas), leído de …
Instrumentado: 9 de 9 pruebas en verde (0 en rojo, 0 con error, 0 saltadas), leído de …
```

*(Instrumented: inside Android, with the fake gate, tapping «Entrar con Microsoft» leads to the home screen; inside
Android, cancellation, error and collision each show their message without moving on; inside Android, with a
sign-in left halfway that the double answers, the app reaches the home screen; inside Android, the 4 tests for the
Google button from lesson 04 are still green. Result: 44 of 44 tests green. Instrumented: 9 of 9 tests green.)*

There are nine: five from this lesson and the four from 04. On the untouched starter project it gave 4 of 9, the
four from 04. With no device connected, the `verifica` comes out red and says it did not run.

**What it proves, and how far.** It proves that the screens, the tap and your class work inside Android, with the
device's Firebase Authentication library assembling the provider. It proves **nothing** about your registration in
Entra or your project: the gate is still fake, the browser is not opened, and no account or network toward
Microsoft is needed. And the "resume" test is the double answering that something was left halfway: Android did not
close any app.

**See the real button, without a registration.** On that same emulator, with an invented file, the app was
installed and «Sign in with Microsoft» («Entrar con Microsoft») was tapped (Nov 18, 2026). No page was opened. The
app showed «No se pudo entrar. Revisa tu conexión y vuelve a intentar.» (Could not sign in. Check your connection
and try again.), and the system log said `[GetAuthDomainTask] Error getting project config. Failed with API key not
valid`. It is what is expected: the file was invented. With your real file and Microsoft not yet enabled in
Firebase, we have not yet measured the result; if you see it, tell us which message it gave you.

---

## Step 8 · In the consoles: registering the app without the secret touching a file

This step and the next one require the directory from "Before you start". No code is written here. Open the two
consoles in two tabs, because three pieces of data are going to cross from one to the other:

| Data | Born in | Pasted into |
|---|---|---|
| The return address | Firebase | Entra |
| The *Application (client) ID* | Entra | Firebase |
| The client secret | Entra | Firebase, and nowhere else |

Menus and buttons are written in English, as in the documentation. In the Spanish-language console, the Nov 13,
2026 trial saw «Registros de aplicaciones» («App registrations»), «Nuevo registro» («New registration») and
«Registrar una aplicación» («Register an application»). We did not walk through the rest of the Spanish labels from
end to end: if your console says something else, go by the English version of each one and tell us how it appears.

**1. In Firebase, copy your return address.** In Authentication, the *Sign-in method* tab, choose Microsoft and
enable it. Do not save yet: the panel asks for the ID and the secret, which you do not have yet. Leave that tab open
like that, halfway; you come back to it at point 5. In the trial, the address was copied from that same panel. It
has this form:

```text
https://<tu-proyecto>.firebaseapp.com/__/auth/handler
```

*(`<tu-proyecto>` means `<your-project>`.)*

Firebase's guide on redirects says the `/__/auth/handler` ending matters.

**2. In Entra, go in through `entra.microsoft.com`.** Not through `portal.azure.com`: in the trial, the account
without a subscription got the error `AADSTS16000` there, and through Entra it opened its own directory.

**3. Register the app.** According to Microsoft's guide: *Entra ID* › *App registrations* › *New registration*.

- **Name:** one you recognize. The guide warns that the people who sign in can see it.
- **Supported account types:** *Any Entra ID Tenant + Personal Microsoft accounts*, for organization and personal
  accounts. It is the one the trial used.
- *Register*.

The *Overview* page shows the **Application (client) ID**. That one is written down, and you can already paste it
into Firebase's tab. It is not the secret: the secret does not exist yet.

**4. Add the return address.** According to the guide: *Manage* › *Authentication* › *Add Redirect URI* › **Web**
platform › paste the address from point 1 › *Configure*. Paste it; do not type it by hand. Microsoft asks that it
start with `https` and it is case-sensitive.

**5. Create the secret.** According to the guide: *Certificates & secrets* › *Client secrets* › *New client
secret*. Give it a description and choose an expiry. The guide says it cannot go beyond 24 months and recommends
less than 12. *Add*.

The secret appears in the **Value** column, and the guide warns that it is not shown again after you leave the page.
With that page open:

- Copy it with the copy button and paste it **straight** into the secret field of the Microsoft panel, in Firebase's
  tab. Check that the *Application (client) ID* from point 3 is already in its field. *Save*.
- Do not paste it anywhere else. Afterwards copy any other text, to clear it from the clipboard (a recommendation of
  this series).
- Write down the **expiry date**. When it arrives, signing in with Microsoft stops working until you create another
  secret and take it to Firebase.
- Take no screenshot while the *Value* column shows the secret.

A boundary, so as not to draw an architecture from here: Firebase asks for that ID and that secret for this
provider, and Microsoft's same guide recommends certificates instead of secrets before taking an app to production.
For a real product, that is reviewed with whoever handles security: the Entra policy, the secret's rotation and the
type of credential.

**6. Leave the evidence.** In `respuestas/05.md`:

| Data | You write down |
|---|---|
| *Application (client) ID* | Yes, in full |
| Account types | Yes, the name of the option |
| Return address | Yes, in full |
| Secret's expiry | Yes, the date |
| Secret's value | **Never** |

And a screenshot of *Certificates & secrets* taken **after** leaving that page and coming back in. If any part of
the value is visible in the screenshot, cover it before saving it.

**7. Ask the `verifica` to look.** Run it: the two «Clave:» (Secret:) marks must be green. To know that this search
does see, plant an **invented** secret. This command builds it in parts, so it is not written anywhere, and appends
it as a comment at the end of a file:

```bash
printf '// %s%s%s\n' 'Inv8' 'Q~' "$(printf 'x%.0s' $(seq 34))" >> lecciones/05-entrar-con-microsoft/inicio/app/src/main/java/mx/habil/ejemplo/tienda/Armado.kt
```

Run the `verifica`. On Nov 18, 2026, on the untouched starter project, it answered with the file and the line
number, without printing the text (your line number may be another):

```text
❌ Hay un texto con forma de clave de cliente de Entra en: inicio/app/src/main/java/…/Armado.kt:19. No la imprimo. …
```

*(In English: there is a text shaped like an Entra client secret in: …/Armado.kt:19. I do not print it.)*

Delete that last line in your editor and run the `verifica` again: green.

**What it proves, and how far.** The «Clave:» marks prove that no text file in `inicio/` or `respuestas/05.md`
carries a text shaped like those secrets or names a client secret. The shape is inspired by a public gitleaks rule,
but the `verifica` does not run gitleaks and does not replace a secrets scan: it is simpler, it can flag innocent
text, and it misses other shapes, binaries, environment variables and git's history. It also does not see your
terminal, your screenshots or your clipboard. That part you look after yourself, and you compare your table and your
screenshot yourself.

---

## Step 9 · On the phone: the real level

This step does talk to Microsoft, once, by hand. It is the only one that checks step 8: that the return address is
the exact one, that the ID is your registration's and that the secret you saved is the current one.

With the emulator open, standing in `inicio/`:

```bash
./gradlew installDebug
```

Open «The store» («La tienda»), tap «Enter» («Entrar») and then «Sign in with Microsoft» («Entrar con Microsoft»).

**What was seen in the Nov 13, 2026 trial** (Mac, emulator, personal accounts). That trial used test code with the
same calls, not the one in `solucion/`:

| Case | What was seen |
|---|---|
| First time | The browser opened with Microsoft's page; the account was typed in; the app greeted with the name |
| The permission | A screen "Let this app access your info?", with the **unverified** mark next to the app's name, "Read your profile" and the *Deny* and *Accept* buttons. It is what is expected in a sample app without a verified publisher. Microsoft saved the acceptance: it did not appear again |
| Signing in again | Microsoft showed the account already signed in, to choose it without a password |
| An email that already signed in with Google | The collision exception from step 5 |
| The app closed mid-sign-in | What step 6 shows: the pending result delivered the sign-in |

This lesson's `NavegadorConFirebase` class we only ran up to the error of step 7's invented file; the complete
sign-in with a real account we have not yet done. If you see something different when you try it, tell us what
showed up.

**If your account uses a passkey.** In the trial, the "Sign in with your passkey" screen stayed on "Verifying…" on
the emulator. *Cancel* was tapped and the sign-in went through with the password.

**If the browser offers to save the password:** on a machine that is not yours, "No".

**Try the scene's ending.** With Microsoft's page in front and before choosing an account, in another terminal:

```bash
adb shell am kill mx.habil.ejemplo.tienda
adb shell pidof mx.habil.ejemplo.tienda
```

The first kills the app's processes that are safe to kill, according to the `adb` documentation. The second must
print nothing: the process no longer exists. Now choose your account. The app must start and greet you without your
tapping the button again.

**When you finish, on a machine that is not yours:** sign out of Microsoft in the emulator's browser and delete its
data (lesson 00).

**What proves it:** the real sign-in proves that the registration, the return address, the ID and the secret are
right. It does not prove the cases of not getting in: the simulated level covers those.

---

## Your exercises

Steps 1, 5 and 6 are done **without AI**.

| # | You do | It is done when |
|---|---|---|
| 5.1 | You register the app in Entra with the exact return address and take its ID and secret to Firebase | Three pieces of evidence. Your step 8 table and your screenshot with no secret in view: you compare them yourself. The `verifica`'s two «Clave:» marks in green, which only say that no **file** in `inicio/` or `respuestas/05.md` carries that shape or that name. And the real sign-in from step 9, which is the only thing that validates the registration |
| 5.2 | You implement `EntradaConMicrosoft` without changing the screens, and you resume the pending result | Three pieces of evidence. Simulated: its `verifica` marks in green; it validates what your class asks for and what it answers, with a fake gate, and that the 20 tests from 04 stay green with their files identical. It does **not** by itself certify that the installed app uses your sign-in: what backs that is `MainActivity.kt` identical to the lesson's and the test of what `Armado.kt` assembles. Instrumented: 9 of 9, the same inside Android. Real: you sign in at step 9 and kill the process mid-sign-in; it is the only thing that validates that Android really returns you to the app |
| 5.3 | You compare Google and Microsoft with the five questions from step 1 | Your table matches the one in "Compare your answers". You compare it yourself |
| 5.4 | You ask Claude Code for the second sign-in taking the first as a reference, and list what it did not copy well | Your list of yes and no from step 4, with file and line on each "no", against the eight questions in "Compare your answers". If all eight are "yes", your delivery is that table and the phrase "No encontré desvíos en las ocho revisiones". You grade it yourself; the `verifica` does not read it |

**Time:** allow about four hours, with the Entra registration, the "Break one on purpose", the instrumented level and
the real sign-in; without steps 8 and 9, about two and a half. It is an estimate: we adjust it with how long the
first cohort takes. Getting the directory is not counted: in the trial, the trial subscription took about 15
minutes to show up as active.

---

## Not running?

| You see | Probable cause | What you do |
|---|---|---|
| `File google-services.json is missing.` | You did not copy your file to `inicio/app/` | The `cp` from "Before you start". On `solucion/` it is what you expect on a clean clone |
| ❌ «Estos archivos ya no son los de la lección 04» (These files are no longer lesson 04's) | Someone, you or Claude Code, changed a test, a gate or a screen from 04 | `git diff -- <file>` and undo it by hand |
| ❌ «Tu entrada no retoma» (Your sign-in does not resume) | `retomar()` does not use what `pendiente()` returns | Step 4. If you come from the "Break one", step 6, point 4 |
| ❌ The «error» case in red, with `resultadoDe` written | Every `FirebaseAuthWebException` is being counted as a cancellation | Step 5: the code condition is missing |
| ❌ «Hay un texto con forma de clave de cliente de Entra» (There is a text shaped like an Entra client secret) | A secret, or something shaped like one, is in a file | If it is your secret: delete it from the file, delete it in Entra and create another (step 8). If it is the invented one from the control: delete that line |
| In the tests, `Default FirebaseApp is not initialized` | `robolectric.properties` or `AppDePruebas.kt` was changed (measured on Nov 18, 2026 without them) | Put them back: they are scaffolding |
| In the instrumented tests, `NoSuchMethodException: android.hardware.input.InputManager.getInstance` | The Espresso 3.7.0 line was removed and the one Compose brings by default was left. Measured on Nov 18, 2026 on Android 37: 0 of 9 | Put `androidTestImplementation(libs.espresso.core)` back |
| On the phone, «No se pudo entrar…» (Could not sign in…) with no page opening | With an invented file, the log said `API key not valid` (measured on Nov 18, 2026). With your file, what it gives with Microsoft not enabled or with an expired secret we have not yet measured | Check that you copied your `google-services.json` and step 8 |
| In the browser, an `AADSTS50011` error | According to Microsoft: the request's return address does not match any of the registered ones. It was not measured here | Step 8, point 4: paste it again, complete |
| On `portal.azure.com`, `AADSTS16000` | Measured on Nov 13, 2026 with an account without a subscription | Go in through `entra.microsoft.com` |
| «New registration» («Nuevo registro», in the Spanish-language console) opens a panel without a form | Your account has no directory (measured on Nov 13, 2026 with a newly created account) | "Before you start": you need a directory first |
| «Ese correo ya entró a la tienda con otro botón…» (That email already signed in to the store with another button…) | That email already has a user in your project, created with another provider; in this series, Google | Sign in with Google. Making both buttons arrive at the same user is lesson 06 |
| The passkey screen stays on "Verifying…" | Measured on Nov 13, 2026 on the emulator | *Cancel* and sign in with the password |

On Linux Mint 22.3 (x86_64), this lesson's `verifica` came out the same as on the Mac (Nov 18, 2026), at the
simulated level and with a test file that carried that computer's fingerprint: on `inicio/`, 23 of 44 green; on
`solucion/`, 44 of 44. The instrumented level was not measured on Linux.

---

## To wrap up

**Answer without reading again:**

1. Where does the client secret live, who uses it and why does it not go in the app?
2. What has to match exactly between Firebase and Entra?
3. Your `verifica` gives 44 of 44 and the «Clave:» marks are green. Say one thing that proves and two that it does
   not.
4. Why is "Don't keep activities" no good for testing `retomar()`?

**What comes next:** in [06](06-dos-botones-una-cuenta.md), the person with the same email in both providers ends up
with a single user, and only if they confirm it.

---

## Compare your answers

**Step 1.** Your code takes part in row 1, when it builds the request, and receives row 6. Rows 2 to 5 happen between
Firebase, the browser and Microsoft. Your table:

| Question | Google (lesson 04) | Microsoft (this lesson) |
|---|---|---|
| Who registers the app, and where? | You register the app in Firebase and add the fingerprint; you do not create Google's clients by hand: they arrive in your `google-services.json` after enabling Google (lesson 03) | You, by hand, in Entra's console, which is outside Firebase |
| What shows the person where to sign in? | Credential Manager, with Android's accounts window | The browser, with Microsoft's page |
| What does your app receive from the provider? | An identity token, which the app itself carries to Firebase | Nothing: the code travels from Microsoft to Firebase. Your app receives the user from Firebase |
| Is there a secret? Where does it live? | You handle none: only the web client ID goes in the app, and it is not secret | Yes, the client secret. You create it in Entra and it lives in Firebase's console |
| What has to match? | The package name and the fingerprint | The return address, character by character |

**What is the same is the pattern:** an `Entrada` with its gate, a `Resultado` for each way of ending, the tests with
a double, and Firebase delivering a user at the end. The screens did not know there was a new provider.

**Step 4.** `retomar()` returns `null` and the screen stays on the welcome, without a message. `entrar()` asks again
because that is what Firebase's guide asks before opening another flow: whoever taps the button must not sign in
twice if there is already an answer waiting.

**Exercise 5.4.** Eight yes-or-no questions about what Claude Code generated. What is expected is eight "yes"; each
"no" is something it did not copy well and goes in your list with file and line. The list says what to check, not
what will fail: what the tool generates was not measured.

| # | Question | Why |
|---|---|---|
| 1 | Is the provider the text `"microsoft.com"`, without `https://`? | That is how Firebase's guide writes it |
| 2 | Did it leave out what belonged to Google: `GetCredentialRequest`, `GetSignInWithGoogleOption`, `GoogleIdTokenCredential`? | Here there is no token to ask the phone for |
| 3 | Does the class receive only the `navegador` gate, without `idDeClienteWeb` or another identifier? | The ID and the secret live in the consoles |
| 4 | Does `entrar()` ask `pendiente()` before `abrir`? | It is what the guide asks, so as not to sign in twice |
| 5 | Does `retomar()` never call `abrir`? | It is called when the app opens, without anyone tapping a button |
| 6 | Is there no client ID, no secret and no directory identifier written anywhere? | Data rule from step 2. The `tenant` parameter with the value `common` is not data of yours: it is the default value |
| 7 | Does nobody name `accessToken`, `idToken` or `OAuthCredential`? | Data rule: only the name comes out of Firebase |
| 8 | Were the tests, the gates, the screens and your `google-services.json` left untouched? | The `verifica` compares those files |

**Step 5.** They would see the cancellation one, «Cerraste la ventana…» (You closed the window…), even though they
closed nothing. Without the condition, every browser failure is counted as a cancellation.

**Step 6.** It compiles, and the button keeps working. Seven tests turn red: those of `retomar()` and the three that
open the app with something halfway. The button's stay green because none closes the app: in them, the fake browser
answers instantly.

**To wrap up:**

1. It is born in Entra and lives in Firebase's console. Firebase's servers use it to exchange Microsoft's code for
   the person's data. It does not go in the app because anyone who has the app installed could pull it out:
   Microsoft asks not to use it in native apps.
2. The return address: the one Firebase uses and the one you registered in Entra.
3. It proves what your class asks for and what it answers in each case, with a fake gate, and that no checked file
   carries a text shaped like a secret. It does not prove that your Entra registration works, nor that the secret is
   not in your terminal, in a screenshot or in git's history.
4. Because it destroys the screen but leaves the process alive, which finishes the sign-in on its own. `retomar()`
   exists for when the process is no longer there.

---

## Lesson sources

Opened on Nov 18, 2026.

- Firebase. *Authenticate Using Microsoft on Android* (the provider, the pending result, the sign-in and the exchange of the code with the console's secret). https://firebase.google.com/docs/auth/android/microsoft-oauth
- Firebase. `FirebaseAuth` reference (`getPendingAuthResult` and the exceptions of `startActivityForSignInWithProvider`). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth
- Firebase. `FirebaseAuthUserCollisionException` reference (its three codes; `ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL` only with "one account per email address", which it recommends; check the code). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuthUserCollisionException
- Firebase. `FirebaseAuthWebException` reference (its four codes; `ERROR_WEB_CONTEXT_CANCELED`). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuthWebException
- Firebase. *Best practices for using signInWithRedirect* (the address `https://<project>.firebaseapp.com/__/auth/handler`). https://firebase.google.com/docs/auth/web/redirect-best-practices
- Microsoft Learn. *How to register an app in Microsoft Entra ID* (requirements, steps and account types). https://learn.microsoft.com/en-us/entra/identity-platform/quickstart-register-app
- Microsoft Learn. *How to add a redirect URI to your application* (the steps and the Web platform). https://learn.microsoft.com/en-us/entra/identity-platform/how-to-add-redirect-uri
- Microsoft Learn. *Redirect URI (reply URL) best practices and limitations* (registered addresses, `https`, case and `AADSTS50011`). https://learn.microsoft.com/en-us/entra/identity-platform/reply-url
- Microsoft Learn. *Add and manage app credentials in Microsoft Entra ID* (the secret: 24 months at most and a value that is not shown again). https://learn.microsoft.com/en-us/entra/identity-platform/how-to-add-credentials
- Microsoft Learn. *Microsoft identity platform and OAuth 2.0 authorization code flow* (`select_account`, the exact match and the secret kept out of native apps). https://learn.microsoft.com/en-us/entra/identity-platform/v2-oauth2-auth-code-flow
- Microsoft Azure. *Azure for Students* (the offer's conditions). https://azure.microsoft.com/en-us/pricing/offers/ms-azr-0170p
- Android Developers. *Side-effects in Compose* (`LaunchedEffect`). https://developer.android.com/develop/ui/compose/side-effects
- Android Developers. *Android Debug Bridge* (`am kill`). https://developer.android.com/tools/adb
- Kotlin. *Null safety* (the types with `?` and the `?:` operator). https://kotlinlang.org/docs/null-safety.html
- gitleaks, a public secrets-search tool: its `azure-ad-client-secret` rule inspired the shape the `verifica` looks for, which does not run gitleaks or apply its delimiters or its entropy threshold. It is not a format published by Microsoft. https://github.com/gitleaks/gitleaks/blob/master/config/gitleaks.toml
- Google's Maven repository: Firebase's BoM 35.2.0 is still the most recent one and carries `firebase-auth` 25.0.0. https://dl.google.com/dl/android/maven2/com/google/firebase/firebase-bom/maven-metadata.xml · https://dl.google.com/dl/android/maven2/com/google/firebase/firebase-bom/35.2.0/firebase-bom-35.2.0.pom
- Series trial, Nov 13, 2026, Mac with Apple silicon, with test code: the Entra registration through `entra.microsoft.com`; `AADSTS16000` in the Azure portal; the newly created account and the free trial; the real sign-in, the permission, the collision and the pending result after `adb shell am kill`; the passkey on the emulator.
- Our own measurement: Mac with Apple silicon, Nov 18, 2026. The `verifica` on `inicio/` and `solucion/`, each step of the lesson and each sabotage, with invented files and without touching the Entra, Azure or Firebase consoles, or any account. The instrumented tests and the Microsoft button with an invented file, on a new emulator, without an account, which was deleted when finished. The four codes of `FirebaseAuthWebException` were also checked inside the `firebase-auth` 25.0.0 library.
