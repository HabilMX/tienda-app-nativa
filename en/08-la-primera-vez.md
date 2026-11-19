# Lesson 08 — The first time

**Before you read, three words.** One line each.

- **New user:** the one that Firebase has just created in your project, in this very sign-in.
- **Sign-in result:** what Firebase answers when someone signs in. In the code it is called `AuthResult`.
- **`isNewUser`:** the question that result knows how to answer: is this user new, or did it already exist?

**What you take away:** a store that welcomes whoever signs in for the first time, with Google or with Microsoft,
and does not welcome them again when they return. And an honest account of what Firebase did for you across the
whole stage, of what you would have to build without it and of what does not exist yet.

**This lesson's new idea:** telling the new user apart from the one who returns. The rest —the gates that now also
answer whether it is the first time, the welcome line on the home screen and the state that remembers it— comes
ready-made and explained, and is not assessed here.

**A note on the app's texts.** As in the previous lessons, the project ships its on-screen texts in Spanish, and the
`verifica` looks for those exact texts. In code blocks, terminal outputs and on-screen sketches they stay as the
project has them; in the prose the lesson quotes them in English with the original in parentheses the first time:
the line "Hola, … (primera vez)" ("Hello, … (first time)"), the welcome «Es tu primera vez aquí: te damos la
bienvenida.» ("It is your first time here: we welcome you."), and the buttons «Sign in» («Entrar») and «Sign out»
(«Cerrar sesión»).

---

## The scene

November 13, 2026, in this series' trial. A real Google account signs in for the first time to the test app. The
screen says: "Hola, … (primera vez)".

Someone signs out and signs in again with the same account. Now it says: "Hola, …". Without the parenthesis.

That same night the same thing happens with Microsoft: the first sign-in brings "(primera vez)"; the second one,
not.

And here is the strange part. That app had no database. It did not keep a list of who had already signed in. It
wrote nothing on the phone. How did it know that the second time was no longer the first?

The app did not know it on its own. Firebase told it, in a single word that can come hidden in the sign-in's response. Your app
has been receiving it since lesson 04, and has thrown it in the trash every time: of that response, your gates
only take out the name. This lesson rescues it. Since that word may not come, your app will ask in a safe way: if
it is missing, there is no welcome.

---

## Before you start

**What you need:** [07](07-la-sesion-se-queda.md) finished (its `verifica` at 82 of 82). Steps 1 to 3, 5 and 6 ask
for no account. Step 4 is on your phone or emulator, with your Google account from 04 and, if you did the optional
part, your Microsoft registration from 05.

**The starter project.** From the root of the series repo:

```bash
git pull
cp lecciones/03-la-app-conoce-a-firebase/inicio/app/google-services.json lecciones/08-la-primera-vez/inicio/app/
cd lecciones/08-la-primera-vez/inicio
```

It starts from 07's solution, with this lesson's scaffolding on top. `./verifica solucion` comes out red on a clean
clone, because without your file the project does not build. Your answers go in `respuestas/08.md`.

**Two "welcomes" that are not the same.** The `Bienvenida` screen, since lesson 02, is the cover: the one that
everybody who has not signed in sees, with the «Entrar» button. This lesson's welcome is something else: a line on
the **home screen**, below «Hola, Ana», that only the new user sees. The text says: «Es tu primera vez aquí: te
damos la bienvenida.»

**A rule that is still in force.** Never paste or upload a token, nor screenshots or logs that carry one. A sign-in
result carries tokens; this app's gates only take out of it the name and a yes or a no.

**What this lesson does not do.** It does not create a store profile: it saves no address, orders or preferences.
It does not change what the screens from 04 to 07 do: signing in, the two buttons, the link, reopening and signing
out behave the same, and their 82 tests watch over it.

---

## Step 1 · Predict: when does the welcome appear?

It is the usual first step: you **predict** before asking Claude Code for anything. Do not read step 2 yet.

Think about this lesson's **finished** app and about these four moments, in order:

1. Ana signs in with Google for the first time in her life to your store.
2. Ana closes the app from the recent apps list and opens it again (as in 07).
3. Ana taps «Cerrar sesión» and signs in with Google again.
4. Luis signs in with Microsoft for the first time, and Android closes the app while he is on Microsoft's page. On
   coming back, the app resumes the sign-in (as in 05).

Write in `respuestas/08.md` a line that starts like this, with the numbers of the moments in which you think the
welcome appears and, in one sentence, why:

```text
predicción 8.1: …
```

*(That is: "prediction 8.1".)*

**Measure the starting point.** From the repo root:

```bash
./lecciones/08-la-primera-vez/verifica
```

On Nov 19, 2026, with an invented file, the last line of the count was
`Resultado: 84 de 97 pruebas en verde (13 en rojo, 0 con error, 0 saltadas)`. There are 97: the 82 you inherit from
07, already green, and 15 for the first time. Of those, two already pass with the scaffolding (the ones for "the
user who returns": today your app never says it is the first time, and in that case it gets it right) and thirteen
wait for your code.

*(Result: 84 of 97 tests green: 13 red, 0 with error, 0 skipped.)*

**What proves it:** your prediction, against what you see in step 3 (the tests) and in step 4 (your phone). What
the `verifica` reviews of your answers is in "Your exercises".

---

## Step 2 · What the project already carries

**Where the answer lives.** When someone signs in, Firebase answers with an `AuthResult`. Its reference says it is
the result "of operations that can affect the authentication state". Besides the user, it carries
`getAdditionalUserInfo()`: additional information. And the `AdditionalUserInfo` reference says it holds the user's
information "as a result of a successful sign-in, link or reauthentication". Its `isNewUser()` function "returns
whether the user is new or existing". In Kotlin it reads like a property: `resultado.additionalUserInfo?.isNewUser`.

**Two details of the documentation that decide how that line is written:**

- The reference marks `getAdditionalUserInfo()` as `@Nullable`: it can come `null`. Kotlin brings it as a type
  that admits `null`, and the `?.` operator exists for that: if what is on the left is `null`, the result is `null`
  instead of blowing up.
- The same reference says it returns information "if the provider is Facebook, GitHub, Google or Twitter".
  Microsoft is not on that list. But the `AdditionalUserInfo` page, in `getProfile()`, does name Microsoft, and in
  the Nov 13 trial the sign-in with Microsoft said "(primera vez)" the first time and not the second. The
  documentation is not consistent here, and that is why the code assumes nothing: if there is no information, there is
  no welcome.

**What changed in the app.** One file is new and five changed with respect to 07's solution; in the tests, three new
classes and their doubles, `DoblesDePrimeraVez.kt`, arrive. Open them; edit only the three that step 3 names:

| File | What it carries |
|---|---|
| `PrimeraVez.kt` (new) | The shape of your `esPrimeraVez(resultado: AuthResult)` function, empty. You write it in step 3 |
| `Entrada.kt` | `Resultado.Entro` carries, besides the name, `primeraVez`. It is `false` if nobody says otherwise: that way everything from 04 to 07 stays the same |
| `Puertas.kt` | `QuienEntro`: the name and a yes or a no. And, in Google's gate, a new function: `verificarConPrimeraVez()` |
| `PuertaDelNavegador.kt` | In Microsoft's gate, two new ones: `abrirConPrimeraVez()` and `pendienteConPrimeraVez()` |
| `Tienda.kt` | It keeps whether the person who signed in is new and hands it to the home screen. On reopening with a session and on signing out, it leaves it at `false` |
| `Inicio.kt` | With `primeraVez` at `true`, it adds the welcome line. With `false`, it is 07's home screen |

**The trick of the new functions.** All three carry a default body that answers "it is not the first time". That is
why the doubles from 04 to 07, which know nothing about this, still compile and their tests stay green. The real
gates replace that body: they read Firebase's `AuthResult` and ask **your** `esPrimeraVez`. Out of the gate only
`QuienEntro` comes out: the name and a `Boolean`. The complete result, with its tokens, stays inside.

Everything else is identical to 07's solution, and the `verifica` compares it file by file. It is a restriction of
this activity, not of the objective: if you changed one of those files and your app does do what the lesson asks,
that red does not say you did not understand 8.1. Put it back as it came, or ask someone to review your change.

---

## Step 3 · With Claude Code: decide the first time

It comes in the same way as always: **you predict → you ask → you read what it generated → you run the `verifica` →
you explain.**

1. **Predict**, in `respuestas/08.md`, in a free line: if `additionalUserInfo` comes `null` and you write
   `resultado.additionalUserInfo!!.isNewUser`, what happens to the person who tries to sign in?
2. **Ask** Claude Code, standing in `inicio/`: "In `PrimeraVez.kt`, `esPrimeraVez` returns `true` only if
   `resultado.additionalUserInfo?.isNewUser` is `true`; with `null` it returns `false`. In `EntradaConGoogle.kt`
   change `verificador.verificar(token)` to `verificador.verificarConPrimeraVez(token)` and pass its name and its
   `primeraVez` to `Resultado.Entro`. In `EntradaConMicrosoft.kt`, in `retomar()` and in `entrar()`, use
   `navegador.pendienteConPrimeraVez()` and `navegador.abrirConPrimeraVez(proveedor)` instead of `pendiente()` and
   `abrir(proveedor)`, and likewise pass the name and the `primeraVez`. Do not change any other file."
3. **Read.** The core must look like this:

```kotlin
// PrimeraVez.kt
fun esPrimeraVez(resultado: AuthResult): Boolean =
    resultado.additionalUserInfo?.isNewUser == true

// EntradaConGoogle.kt, in step 4 of entrar()
val quien = verificador.verificarConPrimeraVez(token)
Resultado.Entro(quien.nombre, quien.primeraVez)

// EntradaConMicrosoft.kt, in entrar()
val quien = navegador.pendienteConPrimeraVez() ?: navegador.abrirConPrimeraVez(proveedor)
Resultado.Entro(quien.nombre, quien.primeraVez)
```

   Check three things. That it says `== true` and not `!!`: with `?.`, a `null` stays `null`, and `null == true` is
   `false`. That in `EntradaConMicrosoft.kt` **both** functions change, `retomar()` and `entrar()`: if only one
   changes, the other keeps throwing away the first time. And that nobody touched the tests, the gates or the
   screens.

4. **Run the `verifica`.** All 97 tests can end up green. The delivery is still not complete: your lines for 8.1,
   8.2 and 8.3 in `respuestas/08.md` are missing, and the `verifica` says so at the end.
5. **Explain**, in three lines: why the decision lives inside the gate and only a `Boolean` comes out toward the
   screens.

**What it proves, and how far.** Some tests give your `esPrimeraVez` a fake `AuthResult` and look at what it
decides: with `isNewUser` at `true`, at `false` and with no additional information. Others give your two sign-ins
gates that answer "it is new" or "it already existed" and look at whether the `Resultado` carries it, also when
resuming with Microsoft. And the walkthrough ones draw the store with 06's fake project: Ana signs in with Google
and sees the welcome; she signs out, signs in again and no longer sees it, and she is still the same user. The same
with Luis and Microsoft. On reopening with a session, no welcome. On resuming a new user with Microsoft, with
welcome.

The sabotages were measured on the solution on Nov 19, 2026:

| Sabotage | Result |
|---|---|
| `esPrimeraVez` with `!= false`: a `null` counts as new | 96 of 97 |
| `esPrimeraVez` with `additionalUserInfo!!` | 96 of 97 |
| `esPrimeraVez` backwards (`isNewUser == false`) | 95 of 97 |
| `EntradaConGoogle` that does not pass the `primeraVez` | 93 of 97 |
| `EntradaConMicrosoft` that does not pass it in `entrar()` | 93 of 97 |
| `EntradaConMicrosoft` that does not pass it in `retomar()` | 95 of 97 |
| `EntradaConGoogle` that always says it is the first time | 92 of 97, and three of the red ones are inherited from earlier lessons |
| `esPrimeraVez` that only reads `isNewUser` when the result is the tests' double | 96 of 97: each case is also tested with the result in another class |
| `EntradaConGoogle` that fails only when the gate is `VerificadorConFirebase` | 97 of 97: the tests do not see it, because they only hand it doubles. The specific-gate mark catches it |

Each one also leaves its mark in red in the output, with the step that fixes it.

Three limits. "It is new" in the tests is decided by a double: the fake project says "new" when that sign-in created
a user, which is what Firebase promises, but it is not Firebase. No test sees a real `AuthResult`. And none looks at
what `isNewUser` answers on linking: this app does not use that result for the welcome (step 4 says why).

This lesson has no instrumented level, like 06 and 07: the welcome is a line of text on a screen that was already
tested inside Android in 04, and what is new is a decision, which is tested on your computer.

---

## Step 4 · On the phone: the first time for real

Install and open the app with your `google-services.json`, as in 04:

```bash
./gradlew installDebug
```

**The problem with your account.** Your Google account already signed in to your project in 04. For Firebase it is
no longer new, so with it you will **not** see the welcome. That is exactly what this lesson promises, and it is
your first check: sign in with your account and see that it says «Hola, …» without the welcome line.

To see it, Firebase has to create a user. You have two paths:

- **Another Google account** that has never signed in to your project, if you have a test one.
- **Delete your user** in the Firebase console, in Authentication › Users. It is your test project, but deleting a
  user cannot be undone, and with it goes any link you made in 06.

Sign in. The welcome should appear. Tap «Cerrar sesión», sign in again with the same account, and it should no
longer appear. Compare with your prediction.

What was already seen in the Nov 13, 2026 trial, with test code: `isNewUser` was `true` the first time and `false`
on coming back, with Google and with Microsoft. What we have **not yet captured with a real account** is this
walkthrough with this lesson's code: the welcome line on your home screen and its not returning on signing in
again. If it appears different to you, tell us.

**Two cases the documentation does not settle.** They were looked up on Firebase's pages on Nov 19, 2026:

- **On linking** (lesson 06). `AdditionalUserInfo` says it also arrives after a link, but no page says what
  `isNewUser` is in that case: neither the reference, nor the account-linking guide, nor the Microsoft one. **It is
  not documented.** This app does not ask the link: whoever links already signed in before with the other button,
  and that sign-in already decided whether there was a welcome.
- **On resuming** (lesson 05). `getPendingAuthResult()` delivers an `AuthResult`, the same type as a normal
  sign-in, and the app reads it the same way. But no page says explicitly that `isNewUser` behaves the same
  there: **it is not documented**. In the trial a sign-in was resumed with an account that already existed, and it
  was not tried with a new one. If you try it and see something else, tell us.

**What proves it:** what you observe. There is no mark in the `verifica` for this step.

---

## Step 5 · Without AI: what Firebase did for you

This is exercise 8.2, the one that closes the stage, and it is done **without Claude Code**. Do not read the
earlier lessons again while you answer it either: first write what you remember; afterwards, if you want, check.

Eight lessons, an app that lets you sign in with two providers, remembers you and knows whether it is your first
time. Now the honest account. Write in `respuestas/08.md` three lines that start like this:

```text
8.2 Firebase hizo: …
8.2 sin Firebase: …
8.2 todavía no hay: …
```

*(That is: "Firebase did", "without Firebase" and "there is not yet".)*

1. **Firebase did:** at least three things that Firebase did for you in the stage. Think about what happens between
   choosing your account and the app saying «Hola».
2. **Without Firebase:** for each one, what piece you would have to build. "A server" is not enough: say what that
   server would have to do.
3. **There is not yet:** something that a real store needs to know about you and that your app, today, has nowhere
   to save.

**What proves it:** your three lines, against "Compare your answers" (what the `verifica` reviews, in "Your
exercises").

---

## Step 6 · Judging a Claude Code answer

This is exercise 8.3. Imagine that, on finishing step 3, you ask Claude Code: "How secure is my app's sign-in?". An
answer **like this one** is plausible. It is not a quotation from a real conversation: it is written for this
exercise.

> I reviewed your app's sign-in. Three things:
>
> **A.** Your app does not validate Google's identity token: it hands it to Firebase, and it is Firebase's servers
> that verify it before letting anybody in.
>
> **B.** With Firebase, your sign-in is already completely secure: nobody will be able to enter the store with an
> account that is not theirs, so there is no need to review anything else.
>
> **C.** Firebase stores the refresh token encrypted on the phone, so even if someone pulls a copy of the app's
> data, it is useless to them.

It sounds good. The three sentences are said with the same confidence. Your job is to tell them apart.

**Now you.** For each statement, write in `respuestas/08.md` one of three verdicts —`correcta`, `exagerada` or
`sin comprobar` ("correct", "exaggerated" or "unchecked")— at the start of the line and, afterwards, why. For
example, `8.3-A: correcta porque …`:

```text
8.3-A: <juicio> porque …
8.3-B: <juicio> porque …
8.3-C: <juicio> porque …
8.3 comprobé: …
fuente 8.3: https://…
```

*(`juicio` is "verdict", `porque` is "because", `comprobé` is "I checked" and `fuente` is "source".)*

In `8.3 comprobé` say two things: what of the answer the answer itself checked (did it cite a file, a line, a
page?) and what you checked, and how. In `fuente 8.3`, the address of an official page that you used to check
something.

**What proves it:** your three verdicts against "Compare your answers", and your check (what the `verifica`
reviews, in "Your exercises").

---

## Your exercises

Step 5 is done **without AI**. Of your answers in `respuestas/08.md`, the `verifica` only reviews the format: that
the lines, the verdicts and the source are there. It does not know whether you got it right or whether you wrote
without AI; you close that yourself by comparing with "Compare your answers".

| # | You do | It is done when |
|---|---|---|
| 8.1 | You decide with `isNewUser` whether the welcome shows and you demonstrate that it appears only the first time | The `verifica` says «Nivel simulado: completo» (simulated level: complete), with the 97 tests green, and your `predicción 8.1` is compared with what you saw. It is the simulated level; step 4 is your test on the phone |
| 8.2 | You explain what Firebase did for you, what you would build without it and what does not exist yet | Your three `8.2` lines, written without AI, name the verification of the token, the creation of the user, the session that stays and what does not exist yet (the profile) |
| 8.3 | You judge a Claude Code answer about the sign-in's security | Your three verdicts match "Compare your answers", each with its why and with what backs it: the official page, or the file and the line |

**Time:** allow an hour and a half without step 4, and twenty minutes more with it. It is an estimate: we adjust it
with how long the first cohort takes.

---

## Not running?

| You see | Probable cause | What you do |
|---|---|---|
| `File google-services.json is missing.` | You did not copy your file to `inicio/app/` | The `cp` from "Before you start" |
| `NotImplementedError` in the first-time tests | `PrimeraVez.kt` still has its `TODO` | Step 3 |
| ❌ «Con additionalUserInfo en null tu esPrimeraVez truena o dice que sí» (With additionalUserInfo at null your esPrimeraVez blows up or says yes) | You used `!!`, or `!= false` | Step 3: `?.` and `== true` |
| ❌ «Retomar con Microsoft» (Resume with Microsoft) and the rest of Microsoft's in green | You changed `entrar()` but not `retomar()`, or the other way around | Step 3: both functions |
| ❌ «De las 82 pruebas heredadas…» (Of the 82 inherited tests…) | Your sign-in always says it is the first time, and the `Resultado` is no longer the one the earlier tests expect | Pass the `primeraVez` that the gate answered, nothing more |
| ❌ «Restricción de esta actividad… ya no son iguales en inicio/ y en solucion/» (Restriction of this activity… they are no longer equal in inicio/ and in solucion/) | A given file changed | `git diff -- <file>` and undo it by hand |
| On the phone, the welcome never shows | Your account already existed in your project, since 04 | Step 4: another account, or delete your user |
| On the phone, the welcome shows every time you sign in | You deleted the user in the console between one sign-in and the other, or something different that we have not seen | Check Authentication › Users; if the user is still there and it shows every time, tell us |
| ❌ «EntradaConGoogle.kt o EntradaConMicrosoft.kt nombra una puerta concreta» (EntradaConGoogle.kt or EntradaConMicrosoft.kt names a specific gate) | One of your sign-ins names a real gate (`VerificadorConFirebase`, `NavegadorConFirebase`) or a test double. The tests only hand them doubles: if your code asks which one it got, the green does not say what it does with the real one | Make your sign-ins talk only to their gates' interfaces. The real gate is chosen by `MainActivity.kt`, which comes given (step 3) |
| ❌ «Las pruebas no llegaron a correr» (The tests did not run) and what follows does not say the cause | Gradle failed for something the `verifica` does not recognize; it shows you its last lines | Run `./inicio/gradlew -p inicio testDebugUnitTest --stacktrace` and read the end. If you share it, remove tokens and your computer's paths first |

This lesson's `verifica` was measured on a Mac with Apple silicon and on Linux Mint 22.3, on Nov 19, 2026: with the solution, 97 of 97 on both.

---

## To wrap up

**Answer without reading again:**

1. Who knows whether it is the first time: your app or Firebase? Why can your app not know it on its own, today?
2. Why `== true` and not `!!`?
3. Your `verifica` gives 97 of 97. Say one thing that proves and one thing it does not.
4. On reopening with a session, why is there no welcome, even though it is the second time you open the app that day?

**Closing the stage.** You started with an empty environment. Today you have a native Android app, in Kotlin and
Compose, for a store where you sign in with Google or with Microsoft; where a person with the same email in both
reaches a single user, because that person confirmed it; that remembers you when you open it again and forgets you when you
ask it to; and that welcomes you only the first time. Each piece went through the same loop: predict, ask, read,
run and explain. And in step 5 you said, in your own words, which part Firebase put and which part you put.

**What comes next: stage 2.** The same store, on iPhone, with SwiftUI. The Android code is not translated line by
line: the app you finished today is the **specification** of what the iPhone one has to do, and your 97 tests say,
case by case, what "doing it" is. To build an iPhone app you need a Mac with Xcode, so that stage is Mac only. It is
published when it has been tested from end to end, like this one.

---

## Compare your answers

**Step 1.** Only in 1 and in 4. In 1, Firebase creates Ana and `isNewUser` is `true`. In 2 there was no sign-in: the
app asks the session who is still inside, and that does not say whether they are new. In 3, Ana already exists:
`isNewUser` is `false`. In 4, Luis is new and the resumed sign-in carries an `AuthResult` like any other: the tests
measure it with a double; with a real account, it is neither documented nor captured (step 4).

**Step 3.** They do not get in, even though Firebase did let them in. `!!` turns the `null` into an exception; your
sign-in catches it with its `catch (e: Exception)`, like any other failure, and the person sees «No se pudo entrar.
Revisa tu conexión y vuelve a intentar.» ("Could not sign in. Check your connection and try again."). The
connection was fine. And since Firebase had already saved the session, on reopening the app (lesson 07) it would
greet them as if nothing had happened: we reasoned that from the code, we did not measure it on a phone. With `?.`
and `== true`, a `null` becomes "it is not the first time", and the person gets in without a welcome.

**Step 5.** What is expected, with these ideas or yours:

- **Firebase did:** it verified Google's identity token on its servers; it talked to Microsoft using the client ID
  and the key you saved in its console, without the key touching your app; it created the user the first time you
  signed in, with a unique identifier; in this project, with the one-account-per-email-address rule that it has
  configured, it rejected the second sign-in with that email and, after the person confirmed it and the app asked
  for the link, joined the second provider to the same user; it saved the session on the phone and renews the ID
  token every hour with the refresh token; and it told you whether the user was new.
- **Without Firebase:** a server of your own that checks that each token is authentic, that it was issued for your
  app and that it has not expired; the exchange with Microsoft done from that server, which is where the key would
  live; a table of users with their identifier and with which accounts each one is reached; the duplicate-email
  rule, and the call that joins a new credential to the user who already exists; your own session tokens, how they
  are renewed and where they are stored on the phone; and the "new user" mark, which you would have to compute
  yourself when creating the row.
- **There is not yet:** a store profile. The authentication user has a fixed set of data: an identifier, an email,
  a name and a photo. The documentation itself says others cannot be added to it and that they go in other
  storage. Your address, your orders and your preferences have nowhere to live yet: that arrives when the course
  introduces storage.

**Step 6.**

- **A: correcta.** Firebase Authentication's page says the app hands the credentials to the SDK and that its
  services verify them and answer. And it is checked in your code: `VerificadorConFirebase` hands the token to
  `signInWithCredential` and never reviews it on its own.
- **B: exagerada.** "Completely" and "no need to review anything else" are not backed by any source. In the stage
  itself there are counterexamples: an open session stays open on a borrowed phone (lesson 07); the Microsoft key
  can leak in a screenshot (lesson 05); and if your app linked without asking, it would join accounts without
  permission (lesson 06). Firebase solves parts; the sign-in's security also depends on what you do.
- **C: sin comprobar.** It may be true or not: none of this series' pages says how Firebase stores the tokens on
  the phone. The `signOut` reference only says it deletes the user "from the disk cache". The answer cites
  nothing. To take it as good, someone would have to show the source.
- **What the answer checked:** nothing that can be seen. It says "I reviewed your app's sign-in", but it names no
  file, no line and no page. What you can check: A, with the documentation and with `Puertas.kt`.

**To wrap up:**

1. Firebase. Your app does not keep a list of who has already signed in; Firebase does have its users, and when it
   creates one it says so in `isNewUser`.
2. Because `additionalUserInfo` can come `null`, and `!!` turns that `null` into an exception: the person would see
   «No se pudo entrar» ("Could not sign in") without anything having failed. With `== true`, `null` is "no".
3. It proves what your `esPrimeraVez` decides, that your two sign-ins pass on that decision and when the home screen
   gives the welcome, with doubles. It does not prove what `isNewUser` answers with a real account, nor on linking.
4. Because on reopening there was no sign-in: there is no `AuthResult` and nobody to ask whether they are new. The
   app leaves it at "no".

---

## Lesson sources

Opened on Nov 19, 2026.

- Firebase. `AdditionalUserInfo` reference (it arrives after a sign-in, a link or a reauthentication; `isNewUser` says whether the user is new or existing; `getProfile` names Microsoft among its providers). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/AdditionalUserInfo
- Firebase. `AuthResult` reference (result of operations that affect authentication; `getAdditionalUserInfo` is `@Nullable` and its description names Facebook, GitHub, Google and Twitter). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/AuthResult
- Firebase. `FirebaseAuth` reference (`signInWithCredential` creates the account if it did not exist; `getPendingAuthResult` delivers the sign-in that was left halfway). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth
- Firebase. `FirebaseUser` reference (`startActivityForLinkWithProvider` returns an `AuthResult`; it does not say what `isNewUser` is on linking). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseUser
- Firebase. *Authenticate Using Microsoft on Android* (check the pending response first; the provider's data in `getAdditionalUserInfo()`; it does not mention `isNewUser`). https://firebase.google.com/docs/auth/android/microsoft-oauth
- Firebase. *Authenticate with Google on Android* (the first time the user signs in, an account is created in your project). https://firebase.google.com/docs/auth/android/google-signin
- Firebase. *Link Multiple Auth Providers to an Account on Android* (the same identifier no matter the provider; it does not mention `isNewUser`). https://firebase.google.com/docs/auth/android/account-linking
- Firebase. *Users in Firebase projects* (the user's fixed properties; no others are added to it: they go in other storage). https://firebase.google.com/docs/auth/users
- Firebase. *Firebase Authentication* ("How does it work?": the app passes the credentials and Firebase's services verify them). https://firebase.google.com/docs/auth
- Kotlin. *Null safety* (the `?.` operator returns `null` if what is on the left is `null`). https://kotlinlang.org/docs/null-safety.html
- Series trial, Nov 13, 2026, Mac with Apple silicon, with test code and real accounts: "(primera vez)" showed up only on the first sign-in, with Google and with Microsoft.
- Our own measurement: Mac with Apple silicon, Nov 19, 2026. The `verifica` on `inicio/` and `solucion/`, each step and each sabotage, with invented files and without touching the Firebase console or any account. Project versions: the series' own (AGP 9.4.1, Kotlin 2.4.21, Firebase BOM 35.2.0, which brings `firebase-auth` 25.0.0).
- Measurement on Linux Mint 22.3, Nov 19, 2026: the `verifica` on `solucion/` from a fresh clone, 97 of 97.
