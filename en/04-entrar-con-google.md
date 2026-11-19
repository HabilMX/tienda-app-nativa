# Lesson 04 — Signing in with Google

**Before you read, four words.** One line each; the rest arrive at the step where they are used.

- **Identity token:** a text signed by Google that says who you are and who it was issued for. It is not your
  password, and it expires.
- **Audience:** the recipient written inside the token. Whoever receives it checks that it carries their name.
- **Credential Manager:** the Android piece that shows the window for choosing an account and hands your app what
  the person chose.
- **Web client ID:** the identifier your `google-services.json` carries in the `"client_type": 3` block. You checked
  it in lesson 03.

**What you take away:** your store's «Sign in with Google» button, written by you, with a different message for each
way of not getting in, and being able to say what travels at each step between your app, Google and Firebase.

**This lesson's new idea:** the identity token. Google hands over a signed proof of who you are, made for an
audience, and Firebase verifies it. Your app checks nothing: it only carries the token from one side to the other.
The rest —the libraries, the functions that pause and the two «gates»— comes ready-made and explained, and is not
assessed here.

**A note on the app's texts.** As in lesson 03, the project ships its on-screen texts in Spanish, and the course's
automatic checks (the `verifica`) look for those exact texts. In code blocks, terminal outputs and on-screen
sketches they stay as the project has them; in the prose the lesson quotes them in English with the original in
parentheses the first time: the button «Sign in with Google» («Entrar con Google») and the screen «The store»
(«La tienda»).

---

## The scene

The emulator has your Google account added. You put the button in, tap it, and the app answers that there is no
account to sign in with.

The account is there. You can see it in Settings.

That happened in this series' trial, on November 13, 2026, on a Mac. The code asked for the credential with
`GetGoogleIdOption` and `setFilterByAuthorizedAccounts(false)`, and Credential Manager answered
`NoCredentialException`; the number `[28433]` was left in the system log. A single thing was changed, the option of
the request, to `GetSignInWithGoogleOption`: the account window opened and the sign-in went through.

Both options exist and both ask for the same thing, a Google identity token. Android's documentation tells them
apart by where they are used: `GetGoogleIdOption` is for Credential Manager's bottom sheet, and
`GetSignInWithGoogleOption` is for a button. This lesson builds the button. First you see what travels, then you
write it, and in the end you break it yourself so you recognize that error when you run into it.

---

## Before you start

**What you need:** [03](03-la-app-conoce-a-firebase.md) finished, with your `google-services.json` complete (its
`verifica` at 18 of 18), and the environment from [00](00-ambiente-listo.md). For step 6 an open emulator or your
connected phone is enough; for step 7, in addition, the image must be Google APIs and have your account added
(exercise 0.4). What is said under "Account and trusted machine" in 00 applies.

**The starter project.** From the root of the series repo:

```bash
git pull
cp lecciones/03-la-app-conoce-a-firebase/inicio/app/google-services.json lecciones/04-entrar-con-google/inicio/app/
cd lecciones/04-entrar-con-google/inicio
```

The second command takes your file from 03 to this lesson's project. There it is also ignored by git: the project
already carries its `.gitignore`. Next to it is `solucion/`: you can read it without configuring anything, and
**`./verifica solucion` comes out red on a clean clone**, because without your file the project does not build
(measured on Nov 18, 2026: 0 tests run). Your answers go in `respuestas/04.md`, at the repo root.

**What the project already carries (scaffolding).** It is not assessed here; step 2 walks through it:

| What it carries | Where | Why |
|---|---|---|
| Credential Manager 1.6.0 (two libraries) and `googleid` 1.1.1, next to the Firebase BoM 35.2.0 from 03 | `gradle/libs.versions.toml` and `app/build.gradle.kts` | They are the pieces that ask for the token. The series is tested with those versions |
| `Resultado` (the result), and `Entrada` (the sign-in) turned into a function that pauses | `Entrada.kt` | Signing in no longer answers instantly or with a plain "yes or no" |
| The two gates, with their real implementation | `Puertas.kt` | That is where your code goes out to talk to the phone and to Firebase |
| The screens, with the «Sign in with Google» button and one message per case | `Tienda.kt`, `PantallaEntrada.kt` | They already know how to show whatever your sign-in answers |
| `MainActivity` with `crearEntrada`, which for now returns a sign-in that lets nobody through | `MainActivity.kt` | It is where the app builds its sign-in. It receives the two gates so that a test can read which audience was requested |
| The simulated tests and their doubles | `app/src/test` | They are the ones the `verifica` runs. Do not change them |
| The instrumented tests, with a copy of the doubles, `androidx.test:runner` 1.7.0 and Espresso 3.7.0 | `app/src/androidTest` and the catalog | They run inside Android (step 6). The Espresso that Compose brings by default did not start on Android 37: 0 of 4, with `NoSuchMethodException` (measured on Nov 18, 2026) |
| A skeleton of `EntradaConGoogle` | `EntradaConGoogle.kt` | It is the file you write |

**What this lesson does not do.** The session is not saved or closed (lesson 07), and the first time is not told
apart (lesson 08).

---

## Step 1 · Without AI: the token's journey

This step is yours, without Claude Code. Before writing code, look at what travels.

| # | From whom to whom | What travels |
|---|---|---|
| 1 | Your app → Credential Manager | A request: "I want a Google identity token, for this audience". The audience is your web client ID |
| 2 | Credential Manager → the person | The accounts window. Android draws it, not your app |
| 3 | Google → Credential Manager → your app | An identity token, signed by Google, with that audience inside |
| 4 | Your app → Firebase | The same token, untouched |
| 5 | Firebase → your app | A user, with their identifier in your project and, if the provider delivered it, their name. This app shows «cliente» («customer») if it is missing |

Your password is in no row. You type it, if needed, in a Google window; your app only receives the result.

**Open a token.** An identity token has three parts separated by dots: a header, a payload and a signature. The
payload is not encrypted: it is encoded, which is just a way of passing text through any channel. This payload is
**invented** for the lesson; Google did not issue it and it gets you into nothing:

```bash
printf '%s' 'eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhdWQiOiIxMDAwMDAwMDAwMDAtd2ViaW52ZW50YWRvLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMDAwMDAwMDAwMDAwMDAwMDAwMDAwIiwibmFtZSI6IkFuYSBJbnZlbnRhZGEiLCJleHAiOjE3OTUwNDY0MDB9' | base64 -d
```

On the series' Mac (macOS 27) both forms worked, `base64 -d` and `base64 -D` (measured on Nov 18, 2026). If your Mac
does not accept `-d`, use `-D`; on Linux it is `-d`. The example is built to decode this simply. A real token uses a
variant, Base64URL, and may need more steps. **Do not decode or paste a real token here:** the exercise is done with
the invented one.

**Predict** before running it: will you be able to read it without any key? This is what it answered on the Mac on
Nov 18, 2026:

```text
{"iss":"https://accounts.google.com","aud":"100000000000-webinventado.apps.googleusercontent.com","sub":"000000000000000000000","name":"Ana Inventada","exp":1795046400}
```

*(The decoded payload of the invented token: the issuer is Google, the audience is an invented web client ID, `sub`
is an invented account identifier, the name is "Ana Inventada" (invented Ana), and the expiry is a timestamp.)*

Five pieces of data, with the names Google's documentation uses:

- **`iss`**, who issued it. It must be Google.
- **`aud`**, the audience: who it is for. It must be a client ID of your project.
- **`sub`**, the account identifier. The documentation asks you to use this one, and not the email, to recognize a
  person.
- **`name`**, the name.
- **`exp`**, when it expires.

Anyone can read a token. What nobody else can do is **sign** it like Google. That is why whoever receives it checks
four things, according to that documentation: the signature, that `iss` is Google, that `aud` is theirs and that
`exp` has not passed. In this series Firebase does that: its documentation says you pass it the credentials and its
services verify them and answer you.

**Draw yours.** In `respuestas/04.md`, or on paper with a photo, draw the journey with five arrows and write on each
one what travels. Do it without looking at the table and then compare.

**What proves it:** your diagram, compared with the table (objective 4.1). No test reads it.

---

## Step 2 · What the project already carries

Four files changed with respect to 03. Open them; do not edit them yet. You will see new Kotlin words. For this
lesson three are enough for you, and the rest are given pieces:

- **`suspend`:** this function waits.
- **`try` / `catch`:** translates a problem into a result (step 4).
- **`sealed`:** forces you to cover all the results.

**`Entrada.kt`: the contract grew.** In 02, `entrar()` returned a name or `null`. Now it says:

```kotlin
interface Entrada {
    suspend fun entrar(): Resultado
}
```

- **`suspend`** marks a function that can be paused and continued later without blocking everything else,
  according to Kotlin's documentation. Signing in with Google opens a window, waits for a person and talks over the
  network: meanwhile the screen stays alive.
- **`Resultado`** is a closed list (`sealed`) of four cases: `Entro(nombre)` (signed in, with the name),
  `SinCuentas` (no accounts), `Cancelada` (canceled) and `Fallo` (failure). A `null` was not enough to say **why**
  the person did not get in. Since the list is closed, the compiler knows there is no fifth case.

**`Puertas.kt`: where your code goes out.** Two interfaces with a single function each:

```kotlin
interface SelectorDeCuentas {
    suspend fun pedir(solicitud: GetCredentialRequest): GetCredentialResponse
}

interface VerificadorDeToken {
    suspend fun verificar(tokenDeIdentidad: String): String
}
```

The first asks the phone for a credential; its real version calls Credential Manager. The second hands over the
token; its real version calls Firebase with `GoogleAuthProvider.getCredential` and `signInWithCredential`, which are
the two calls in Firebase's guide. It is the same idea as in 02: your class receives the gates and does not know
which ones it got. In the app they are the real ones; in the tests, two fake ones that live in `src/test`.

The phone's gate is a didactic simplification: it hands the `Activity` straight to Credential Manager. Android's
current guide recommends wrapping it in a `MutableContextWrapper`, so as not to hold on to it if Android recreates
the screen with the window open. According to the release notes, the library updates that wrapper by itself since
1.7.0-alpha03, and the series pins 1.6.0. The file says so in a comment.

**`MainActivity.kt`: where it is assembled.** `onCreate` calls `crearEntrada()` and passes the result to `Tienda`.
`crearEntrada` receives the two gates, with the real ones by default, and for now returns
`EntradaSinProveedor()` (sign-in without a provider), the one that lets nobody through. You change its body in
step 3.

**`Tienda.kt`: who calls.** The button no longer calls `entrar()` directly. It does so inside
`alcance.launch { … }`. `rememberCoroutineScope` gives a place to run tasks that pause, and Compose's documentation
says that place is canceled by itself when the screen stops existing. Right there is `mensajeDe`, with one text per
case.

**Measure the starting point.** From the repo root:

```bash
./lecciones/04-entrar-con-google/verifica
```

On Nov 18, 2026, with a complete invented file, the last line was
`Resultado: 6 de 20 pruebas en verde (14 en rojo, 0 con error, 0 saltadas)`. The six greens belong to the
scaffolding: the screens, their texts and the resource the plugin generated. The fourteen reds wait for your code.

*(Result: 6 of 20 tests green: 14 red, 0 with error, 0 skipped.)*

---

## Step 3 · With Claude Code: the path where everything goes well

This is where the tool comes in, and it always comes in the same way: **you predict → you ask → you read what it
generated → you run the `verifica` → you explain it in your own words.**

1. **Predict.** Write in `respuestas/04.md`: of the journey's five rows, which ones happen inside
   `EntradaConGoogle` and which behind a gate?
2. **Ask** Claude Code, standing in `inicio/`: "In `EntradaConGoogle.kt`, write `entrar()` only for the case where
   everything goes well: build a `GetSignInWithGoogleOption` with `idDeClienteWeb`, put it in a
   `GetCredentialRequest`, ask for it with `selector.pedir`, take the identity token out with
   `GoogleIdTokenCredential.createFrom` and hand it to `verificador.verificar`; return `Resultado.Entro` with the
   name. If the credential is not of Google's type, return `Resultado.Fallo`. Do not catch exceptions yet. In
   `MainActivity.kt`, change only the body of `crearEntrada` so that it returns `EntradaConGoogle` with
   `getString(R.string.default_web_client_id)` and the two gates it receives, `selector` and `verificador`; and
   delete `EntradaSinProveedor` from `Entrada.kt`. Do not change any other file, do not write any client ID in the
   code, and do not print or save the token."
3. **Read** the whole change before accepting it. The core must look like this:

```kotlin
override suspend fun entrar(): Resultado {
    // Row 1: the request, with the audience.
    val opcion = GetSignInWithGoogleOption.Builder(idDeClienteWeb).build()
    val solicitud = GetCredentialRequest.Builder().addCredentialOption(opcion).build()
    // Rows 2 and 3: the phone shows the window and returns what was chosen.
    val credencial = selector.pedir(solicitud).credential
    if (credencial !is CustomCredential ||
        credencial.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
        return Resultado.Fallo
    }
    val token = GoogleIdTokenCredential.createFrom(credencial.data).idToken
    // Rows 4 and 5: Firebase verifies it and answers who it is.
    return Resultado.Entro(verificador.verificar(token))
}
```

   Six things to check as you read:

   - **The option is `GetSignInWithGoogleOption`.** If `GetGoogleIdOption` shows up, compare it with what a button
     needs and ask for the change explaining the difference: it is the one from the scene.
   - **The audience arrives as a parameter.** In `EntradaConGoogle.kt` there must be no text that ends in
     `.apps.googleusercontent.com`.
   - **`crearEntrada` reads `R.string.default_web_client_id` and passes it to `EntradaConGoogle`.** The plugin
     generated that resource from the web client in your file (lesson 03, step 4). And `onCreate` still calls
     `crearEntrada()`.
   - **The token goes as it is.** `idToken` enters `verificar` with no trimming or changes.
   - **Data rule: nobody prints, pastes or saves the token.** Not in the log (`Log`, `println`), not in a file or in
     the preferences, not in `respuestas/04.md`. In the app it goes straight from the credential to Firebase.
     Google's documentation says these tokens are sensitive and can be misused if someone intercepts them. The
     invented tokens in the tests are the only exception, and it is didactic: the doubles do keep them in a list,
     because they belong to nobody. To lesson 03's list of what is never uploaded (signing keys, passwords, the
     Microsoft key), add real identity tokens.
   - **Nobody touched the tests, `Puertas.kt` or your `google-services.json`.**

4. **Run the `verifica`.** With that code it gave `Resultado: 13 de 20 pruebas en verde (7 en rojo, …)` on
   Nov 18, 2026. The seven reds are the cases where the person does not get in: step 4.
5. **Explain**, in three lines of `respuestas/04.md`: what `verificar` receives, where it came from and who signed
   it.

*(Result: 13 of 20 tests green, 7 red.)*

**Why the web client ID, and not the Android one?** Because the token is not for your app: it is for whoever is
going to verify it. Firebase's guide writes it that way in its example: your server's client ID, not the Android
one. In this series that server is Firebase.

**Why is that ID not a password?** For three reasons you can check:

- It travels inside your app, as a resource. Anyone who has the app installed can read it.
- It comes from `google-services.json`, and Firebase's documentation says that file carries unique but non-secret
  identifiers.
- It only says **who** the token is requested for. It proves nothing about who you are and lets nobody in: what
  proves something is Google's signature, and your app does not have that.

**What it proves, and how far.** At the simulated level, the `verifica` proves that your class asks for a single
option and it is the button's, that its audience is the ID it received, and that the token passes as it is from one
gate to the other. And one test opens `MainActivity`, hands the fake gates to `crearEntrada` and reads the audience
that was requested: it must be the web client in your file. With another resource in its place, that test goes red
even though the right name is still written in the file (measured on Nov 18, 2026: 19 of 20).

The gates were fake: nothing was asked of Google or Firebase. And one gap remains that no test sees: that
`onCreate` passes to `Tienda` what `crearEntrada()` returns. The `verifica` only looks for that as text, and calls
it that, "presencia textual" (textual presence). With `onCreate` building another sign-in, all 20 tests are green
and the only red is that mark (measured on Nov 18, 2026).

---

## Step 4 · Without AI: the three ways of not getting in

This is exercise 4.3 and it is done **without Claude Code**. They are three `catch` blocks, and you write them.

When the phone's gate cannot return a credential, it throws an exception. Android's documentation lists the ones
that matter here:

| Exception | When, according to the documentation | Your sign-in answers |
|---|---|---|
| `NoCredentialException` | There is no credential available for the person: for example, there are no accounts on the device or they need to authenticate again. The exception does not say which of the causes it was | `Resultado.SinCuentas` |
| `GetCredentialCancellationException` | The person canceled. The same guide warns that many "cancellations" in a row can hide a bad configuration | `Resultado.Cancelada` |
| Any other | A failure of the phone, or Firebase rejected the token | `Resultado.Fallo` |

**1. Predict.** The first two are children of the same class, `GetCredentialException`. If you put a general `catch`
first and then the two specific ones, which of the three messages will the person always see?

**2. Write it.** Wrap what you wrote in step 3 with `return try { … }` and add, in this order:

```kotlin
} catch (e: NoCredentialException) {
    Resultado.SinCuentas
} catch (e: GetCredentialCancellationException) {
    Resultado.Cancelada
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Resultado.Fallo
}
```

The third one is not a case of the sign-in. `CancellationException` (from `kotlinx.coroutines`) is Kotlin telling you
the task was canceled, for example because the screen closed. Kotlin's documentation asks that, if you catch it,
you throw it again. Since the general `catch` below would also catch it, it goes before and lets it through.

**3. Run the `verifica`.** Now the line is `Resultado: 20 de 20 pruebas en verde` (result: 20 of 20 tests green). One
mark stays red, the one for `respuestas/04.md`: it gets filled in step 5.

**4. Check your prediction.** Move the `catch (e: Exception)` above the others and run the `verifica`: on
Nov 18, 2026 it gave 16 of 20, with «sin cuentas» (no accounts) and «cancelación» (cancellation) in red. Put it
back in its place.

The texts the person sees are already in `mensajeDe`, in `Tienda.kt`:

| Result | Message |
|---|---|
| `SinCuentas` | «No encontré una cuenta de Google disponible para entrar. Agrega una o vuelve a iniciar sesión en Google y prueba otra vez.» (I could not find a Google account available to sign in. Add one or sign in to Google again and try again.) |
| `Cancelada` | «Cerraste la ventana sin elegir cuenta. Toca el botón cuando quieras intentar otra vez.» (You closed the window without choosing an account. Tap the button whenever you want to try again.) |
| `Fallo` | «No se pudo entrar. Revisa tu conexión y vuelve a intentar.» (Could not sign in. Check your connection and try again.) |

**What proves it:** the simulated `verifica`, the three cases (objective 4.3). Each case carries tests of two
kinds: some throw the exception at your class and read what it answered; another draws the screen with your class
and looks for its message. The exceptions were thrown by a double: none came from a phone.

---

## Step 5 · Without AI: break one on purpose

Also **without Claude Code**. You are going to cause the error from the scene.

**1. Predict.** You are going to change the button's option to `GetGoogleIdOption`, the one for Credential
Manager's sheet. It is not "the automatic sign-in one": according to Android's guide, that only happens if
`setAutoSelectEnabled(true)` is also turned on and its conditions are met, and here it is not turned on. Does it
compile? How many tests turn red, and which?

**2. Break it and measure.** In `EntradaConGoogle.kt`, change the option line to this (and its `import`):

```kotlin
val opcion = GetGoogleIdOption.Builder()
    .setServerClientId(idDeClienteWeb)
    .setFilterByAuthorizedAccounts(false)
    .build()
```

Run the `verifica`. This is what it gave on Nov 18, 2026 (trimmed):

```text
❌ Algún .kt de la app nombra GetGoogleIdOption: es la opción de la hoja de Credential Manager, no la del …
❌ Tu entrada pidió otra opción: la solicitud llevó GetGoogleIdOption y debe llevar GetSignInWithGoogleOption
❌ La audiencia de la solicitud no es el ID de cliente que recibió EntradaConGoogle (o la opción no es …
❌ La Entrada que arma MainActivity.crearEntrada no pide el token con el cliente web de tu archivo como …
Resultado: 17 de 20 pruebas en verde (3 en rojo, 0 con error, 0 saltadas), leído de …
```

*(In English the marks say: some .kt in the app names `GetGoogleIdOption`, which is Credential Manager's sheet
option, not the button's; your sign-in asked for another option: the request carried `GetGoogleIdOption` and must
carry `GetSignInWithGoogleOption`; the request's audience is not the client ID that `EntradaConGoogle` received; the
sign-in `MainActivity.crearEntrada` builds does not ask for the token with your file's web client. Result: 17 of 20
tests green, 3 red.)*

Copy into `respuestas/04.md` the line that says «Tu entrada pidió otra opción» (Your sign-in asked for another
option).

**3. What those tests do not tell you.** It compiles, and 17 of 20 stay green: the invented token passes through
the two fake gates all the same. The tests only know that you asked for **another** option. What that option does
on a phone was measured separately:

- On Nov 13, 2026, on the emulator with an account added: `NoCredentialException` and `[28433]` in the log.
- On Nov 18, 2026, on a new emulator **without** an account (Pixel 9, `google_apis_playstore` image of Android 37,
  with an invented file): the app showed the «no accounts» message and the log said `[28433]`. With the button's
  option, on that same emulator, Android opened Google's screen to add an account; on coming back with "back", the
  app showed the cancellation message.

They are observations from those two days, with those versions; not a rule Google publishes.

**4. Fix it.** Put the line back to `GetSignInWithGoogleOption.Builder(idDeClienteWeb).build()` and its `import`.
Run the `verifica`: 20 of 20, and now also the `respuestas/04.md` mark.

The green confirms what those tests check and that the line is in your file; your explanation completes the
evidence. The exercise is finished when you can tick all three:

- [ ] Your prediction from point 1 is written, with what you saw below it.
- [ ] You pasted the line from the red.
- [ ] You said why 17 tests stayed green with code that, on a phone, lets nobody in.

**What proves it:** "Break one on purpose". It is the basis of objective 4.2: knowing why the button carries that
option.

---

## Step 6 · Inside Android: the instrumented level

The tests from the previous steps run on your computer, with an imitation of Android. These run **inside** Android,
on your emulator or your phone. With the emulator open, from the repo root:

```bash
./lecciones/04-entrar-con-google/verifica inicio instrumentado
```

It repeats the simulated level and then installs the app and its test app on the device. There it draws `Tienda`
with your `EntradaConGoogle` and the same two fake gates, taps the buttons and looks for the texts. With the
solution, on a new emulator without an account (Pixel 9, `google_apis_playstore` image of Android 37), this gave on
Nov 18, 2026 (trimmed):

```text
✅ Instrumentado: dentro de Android, con las puertas de mentira, tocar «Entrar con Google» lleva al inicio …
✅ Instrumentado: dentro de Android, cada uno de los tres casos muestra su mensaje sin pasar al inicio (3 de 3)
Resultado: 20 de 20 pruebas en verde (0 en rojo, 0 con error, 0 saltadas), leído de …
Instrumentado: 4 de 4 pruebas en verde (0 en rojo, 0 con error, 0 saltadas), leído de …
```

*(Instrumented: inside Android, with the fake gates, tapping «Entrar con Google» leads to the home screen; inside
Android, each of the three cases shows its message without moving on to the home screen (3 of 3). Result: 20 of 20
tests green. Instrumented: 4 of 4 tests green.)*

On the untouched starter project it gave 0 of 4, and with no device connected the `verifica` comes out red and
says it did not run.

**What it proves, and how far.** It proves that the screens, the tap and your class work inside Android, with the
Credential Manager and `googleid` libraries loaded on the device. It proves **nothing** about your Firebase project:
the gates are still fake, the accounts window is not opened, and no account or network toward Google is needed.
That is why it passed on an emulator without an account.

---

## Step 7 · On the phone: the real level

Up to here nothing has talked to Google. This step does, once, by hand. It is the only one that checks what lesson
03 left written in your file: that the fingerprint is registered, that Google is enabled and that the client ID
belongs to your project.

With the emulator open and your account added, standing in `inicio/`:

```bash
./gradlew installDebug
```

Open «The store» («La tienda»), tap «Enter» («Entrar») and then «Sign in with Google» («Entrar con Google»).

**What was seen in the Nov 13, 2026 trial** (Mac, emulator with a `google_apis_playstore` image and an account
added): Android's accounts window opened, the account was chosen and the app greeted with that account's name. That
trial used test code with the same calls, not the one in `solucion/`: this lesson's `VerificadorConFirebase` class
compiles, but we have not yet run it against a real Firebase project. If, when you try it, you see something
different from what this step describes, tell us what showed up.

**If your account uses a passkey.** In the trial, Google asked to scan a QR code with the phone, and that did not
complete on the emulator. **"Try another way"** was tapped and the sign-in went through with the password or with
the prompt on the phone (lesson 00, exercise 0.4).

**The permissions box.** According to Google's help, when you choose "Sign in with Google" you agree to share three
pieces of data with that app: name, email and profile photo. With them the app can create your account. We did not
capture the exact text of the box in the trial; if yours says something else, tell us how it reads.

This is exercise 4.4. Classify each piece of data in `respuestas/04.md` before looking at the table in "Compare
your answers": **what it is, what it is requested for and whether your app receives it.**

1. Your name.
2. Your email.
3. Your profile photo.
4. Your Google password.

**When you finish, on a machine that is not yours:** remove your account from the emulator and delete its data
(lesson 00).

**What proves it:** the real sign-in, with your account, proves that fingerprint, client ID and provider are well
configured. It does not prove the three cases of not getting in: the simulated level covers those.

---

## Your exercises

Steps 1, 4 and 5 are done **without AI**.

| # | You do | It is done when |
|---|---|---|
| 4.1 | You draw the journey with five arrows and what travels on each | Your diagram matches the table in step 1. You compare it yourself |
| 4.2 | You implement `EntradaConGoogle` with the button's option and the web client ID, and explain why that ID is not a password | Three pieces of evidence. Simulated: the `verifica` gives 20 of 20, which validates what your class asks for and where it takes the token, with fake gates; its «Texto» marks are textual presence. Instrumented: `./verifica inicio instrumentado` gives 4 of 4, which validates the same inside Android. Real: you sign in with your account in step 7, which is the only thing that validates your project. Your explanation is in `respuestas/04.md` |
| 4.3 | You handle no accounts, cancellation and error, each with its message | The `verifica`'s three case marks are green. The exceptions are thrown by a double |
| 4.4 | You classify the four pieces of data from step 7 | 4 of 4 against the table in "Compare your answers" |

**Time:** allow about three and a half hours, with the "Break one on purpose", the instrumented level and the real
sign-in. It is an estimate: we adjust it with how long the first cohort takes.

---

## Not running?

| You see | Probable cause | What you do |
|---|---|---|
| `File google-services.json is missing.` | You did not copy your file to `inicio/app/` | The `cp` from "Before you start". On `solucion/` it is what you expect on a clean clone |
| `Unresolved reference 'default_web_client_id'` | Your file carries no web client (type 3), and the plugin did not generate the resource. Measured on Nov 18, 2026 with an invented file without clients | Download it again after the fingerprint and after enabling Google (lesson 03, step 3) |
| ❌ «sin cuentas» and «cancelación» in red (no accounts, cancellation), with the `catch` blocks written | The `catch (e: Exception)` is before the specific ones | Step 4: the specific ones go first |
| ❌ «Tu entrada pidió otra opción» (Your sign-in asked for another option) | `GetGoogleIdOption` was left in | Step 5, point 4 |
| On the phone, «No encontré una cuenta de Google disponible…» (I could not find a Google account available…) with the account added | In the Nov 13, 2026 trial the option was `GetGoogleIdOption`. According to the guide, it also happens if the account needs to sign in again | Check that the option is `GetSignInWithGoogleOption`, and that your account is still signed in under Settings |
| ❌ «Instrumentado: no hay ningún emulador abierto ni teléfono conectado» (Instrumented: there is no emulator open or phone connected) | `adb devices` lists no device in the `device` state | Open the emulator (lesson 00) and wait for it to finish starting |
| In the instrumented tests, `NoSuchMethodException: android.hardware.input.InputManager.getInstance` | The Espresso 3.7.0 line was removed from the scaffolding and the one Compose brings by default was left (measured on Nov 18, 2026 on Android 37) | Put `androidTestImplementation(libs.espresso.core)` back |
| On the phone, a Google screen to sign in opens | The emulator has no account (measured on Nov 18, 2026) | Add your account there, or in Settings (lesson 00, exercise 0.4) |
| Google asks to scan a QR code | Your account uses a passkey | "Try another way" |
| On the phone, «No se pudo entrar…» (Could not sign in…) | With a real project we have not yet measured which of the causes produces it. The three most probable: the fingerprint not registered, Google not enabled in Firebase, or the emulator without network | Check lesson 03, step 3, and that the emulator has network |
| `GetCredentialProviderConfigurationException` | According to Android's guide, the `credentials-play-services-auth` library is missing. It was not measured here | Check that `app/build.gradle.kts` still carries the three lines of the scaffolding |
| You changed computers and do not have the debug fingerprint of this one | Without `google-services.json` the project does not build, and the debug store is created by the first build: a circle (measured on Linux Mint 22.3, Nov 18, 2026) | Copy your file from 03 into `inicio/app/` even if it carries the other computer's fingerprint, run `./gradlew assembleDebug` (it builds anyway), get the fingerprint with `./gradlew signingReport`, register it in Firebase and download your file again |
| ❌ «EntradaConGoogle.kt nombra una puerta concreta» (EntradaConGoogle.kt names a specific gate) | Your code names the real gate (`VerificadorConFirebase`, `SelectorConCredentialManager`) or a test double. The tests only hand it doubles: if your code asks which one it got, the green does not say what it does with the real one | Make your code talk only to `SelectorDeCuentas` and `VerificadorDeToken`. The real gate is chosen by `MainActivity.kt`, which comes given (step 3) |

On Linux Mint 22.3 (x86_64), this lesson's `verifica` came out the same as on the Mac (Nov 18, 2026), at the
simulated level and with a test file that carried that computer's fingerprint: on `inicio/`, 6 of 20 green; on
`solucion/`, 20 of 20. Without a file, "0 pruebas corridas" (0 tests run), as on the Mac. The instrumented level was
not measured on Linux. This lesson's dependencies added about 300 MB to `~/.gradle`.

---

## To wrap up

**Answer without reading again:**

1. What is an identity token, who signs it and who verifies it in this series?
2. What does the audience say, and where does your app get the value it puts there?
3. Your `verifica` gives 20 of 20. Say one thing that proves and one thing it does not.
4. Why does the general `catch` go last?

**What comes next:** in L05 you add a second provider, Microsoft, without changing the screens.

---

## Compare your answers

**Step 1.** Yes, it can be read without a key: a token's payload is only encoded. What protects the token is the
signature, not encryption. Your diagram must carry the table's five arrows, with the audience in the first, the
token in the third and the fourth, and no arrow with your password going to the app.

**Step 3.** Inside `EntradaConGoogle` what happens is building the request (part of row 1) and passing the token
from one gate to the other. Behind the phone's gate, rows 2 and 3; behind the verifier's, 4 and 5. `verificar`
receives the identity token, which came out of the credential the phone handed over and which Google signed.

**Step 4.** Always the one for `Fallo`: the general `catch` also catches the two children, and the specific ones
below are never reached.

**Step 5.** It compiles, and three tests turn red: the one that checks the option and the two that read the
audience, from your class and from the sign-in the app builds. The other 17 stay green because they use fake
gates, which hand over the invented token no matter which option was requested. That is why the simulated level is
not enough to say "on the phone it gets in".

**Exercise 4.4.**

| Data | What it is | What it is requested for | Does the app receive it? |
|---|---|---|---|
| Name | The name of your Google account | To create your account in the app | Yes, in this Google flow, if the account has one. It arrives through Firebase and shows in «Hola, …» (Hi, …); if it is missing, the app says «cliente» (customer) |
| Email | Your account's address | To create your account in the app | Yes: the credential carries it, and the Firebase user has a primary email. This app does not show it |
| Profile photo | The address of your photo | To create your account in the app | Yes: the credential carries its address. This app does not use it |
| Password | Your Google account's key | It is not requested for the app | No. Google's help does not list it among what is shared, and the credential the app receives has no field for it |

**To wrap up:**

1. A text signed by Google that says who you are and who it was issued for. Google signs it and Firebase verifies
   it; your app only carries it.
2. Who the token is for. Your app takes it from the `default_web_client_id` resource, which the plugin generated
   from the web client in your `google-services.json`.
3. It proves what your class asks for (the option and the audience), that the token passes from one gate to the
   other, and what result and what message each case gives. It does not prove that your Firebase project is well
   configured or that anyone can sign in: the gates were fake.
4. Because it catches everything, including the specific exceptions. If it goes first, the others never run.

---

## Lesson sources

Opened on Nov 18, 2026.

- Android Developers. *Implement Sign in with Google* (the button flow with `GetSignInWithGoogleOption`; the sheet's with `GetGoogleIdOption`; the conditions of automatic sign-in; the `MutableContextWrapper`; `GoogleIdTokenCredential.createFrom`; validating the token on the server). https://developer.android.com/identity/sign-in/credential-manager-siwg-implementation
- Android Developers. *Sign in with Google overview* (why a button in addition to the sheet; with the button an account can be added). https://developer.android.com/identity/sign-in/credential-manager-siwg
- Android Developers. *Troubleshoot Credential Manager* (`NoCredentialException`, `GetCredentialCancellationException`, `GetCredentialProviderConfigurationException`). https://developer.android.com/identity/sign-in/credential-manager-troubleshooting-guide
- Android Developers. *androidx.credentials.exceptions* (the description of each exception). https://developer.android.com/reference/kotlin/androidx/credentials/exceptions/package-summary
- Android Developers. *Credentials*, release notes (1.6.0 is the stable one, from Apr 8, 2026; the automatic context change arrived in 1.7.0-alpha03). https://developer.android.com/jetpack/androidx/releases/credentials
- Android Developers. *Side-effects in Compose* (`rememberCoroutineScope`). https://developer.android.com/develop/ui/compose/side-effects
- Android Developers. Compose testing guide (the test dependencies and `app/src/androidTest`). https://developer.android.com/develop/ui/compose/testing
- Google for Developers. *OpenID Connect* (identity tokens are sensitive; they are validated before using their data; their values go in Base64URL). https://developers.google.com/identity/openid-connect/openid-connect
- Google for Developers. *GoogleIdTokenCredential* (the data the credential carries). https://developers.google.com/identity/android-credential-manager/android/reference/com/google/android/libraries/identity/googleid/GoogleIdTokenCredential
- Google for Developers. *Verify the Google ID token on your server side* (signature, `aud`, `iss`, `exp`; use `sub` and not the email). https://developers.google.com/identity/gsi/web/guides/verify-google-id-token
- Google Account Help. *Sign in with Google* (what is shared: name, email and profile photo). https://support.google.com/accounts/answer/12849458
- Firebase. *Authenticate with Google on Android* (the libraries, `default_web_client_id`, `GoogleAuthProvider.getCredential` and `signInWithCredential`; its example uses `GetGoogleIdOption` and Credential Manager 1.3.0). https://firebase.google.com/docs/auth/android/google-signin
- Firebase. *Firebase Authentication* (you pass it the credentials and its services verify them). https://firebase.google.com/docs/auth
- Firebase. *Users in Firebase projects* (the user's identifier, email, name and photo). https://firebase.google.com/docs/auth/users
- Kotlin. *Coroutines basics* (`suspend`, `launch`), *Cancellation and timeouts* (rethrowing `CancellationException`) and *Sealed classes and interfaces*. https://kotlinlang.org/docs/coroutines-basics.html · https://kotlinlang.org/docs/coroutines-cancellation.html · https://kotlinlang.org/docs/sealed-classes.html
- Google's Maven repository: its indexes list `credentials` 1.6.0, `googleid` 1.1.1 and `firebase-bom` 35.2.0. A newer version of `googleid` exists; the series pins 1.1.1, which is the one in Firebase's guide and in the trial. The same indexes list `androidx.test:runner` 1.7.0 and `espresso-core` 3.7.0 as the most recent stable ones. https://dl.google.com/dl/android/maven2/androidx/credentials/credentials/maven-metadata.xml · https://dl.google.com/dl/android/maven2/com/google/android/libraries/identity/googleid/googleid/maven-metadata.xml · https://dl.google.com/dl/android/maven2/com/google/firebase/firebase-bom/maven-metadata.xml · https://dl.google.com/dl/android/maven2/androidx/test/runner/maven-metadata.xml · https://dl.google.com/dl/android/maven2/androidx/test/espresso/espresso-core/maven-metadata.xml
- Series trial, Nov 13, 2026, Mac with Apple silicon: the real sign-in with an account; `GetGoogleIdOption` with `NoCredentialException` and `[28433]`; the passkey and "Try another way".
- Our own measurement: Mac with Apple silicon, Nov 18, 2026. The `verifica` on `inicio/` and `solucion/`, each step of the lesson and each sabotage, with invented files and without touching the Firebase console or any account. And the app on a new emulator, without an account, which was deleted when finished: the two options, and the cancellation with "back". The instrumented tests were measured on another new emulator, without an account, also deleted.

External review (Nov 18, 2026): WITH CHANGES, 3 high priority, 5 medium and 2 low; applied.
