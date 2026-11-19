# Lesson 07 — The session stays

**Before you read, five words.** One line each.

- **Session:** what lets the app know who you are without asking you again.
- **Current user:** the user that Firebase remembers on this phone. In the code it is called `currentUser`.
- **Signing out:** asking the phone to forget that user, so that the next time you are asked to sign in.
- **Token:** a digital proof of the session. It is not a password, and it is not pasted or published either.
- **Credential Manager:** the Android component that coordinates credentials and can remember which account you
  chose for this app.

**What you take away:** a store that, when you open it, greets you if you had already signed in and asks you to sign
in if not; with a «Cerrar sesión» ("Sign out") button that really takes you out. And the difference between things
that are often confused: the current user, the tokens Firebase keeps, the identities linked to your user and what a
provider remembers on its own side.

**This lesson's new idea:** the session's cycle: remember, close and expire. The rest —the question the app asks
when it opens, the button on the home screen and the gate that talks to Firebase and to Credential Manager— comes
ready-made and explained, and is not assessed here.

**A note on the app's texts.** As in the previous lessons, the project ships its on-screen texts in Spanish, and the
`verifica` looks for those exact texts. In code blocks, terminal outputs and on-screen sketches they stay as the
project has them; in the prose the lesson quotes them in English with the original in parentheses the first time:
the home screen's button «Sign out» («Cerrar sesión»), and the sign-in buttons «Sign in» («Entrar») and «Sign in
with Google» («Entrar con Google»).

---

## The scene

It is November 13, 2026, in this series' trial. The test app has just greeted a real Google account: "Hola, …
(primera vez)" ("Hello, … (first time)"). Someone closes it and opens it again.

The app greets again. Before anybody taps the button.

In lessons 04, 05 and 06 your app does not do that: it always opens on the welcome screen and asks you to sign in,
even if you signed in a minute ago. Lesson 06 left it noted: "this app still has no way to sign out".

Both things are true at once, and the mystery has a single answer. Firebase **did** remember you since lesson 04:
it keeps it on the phone on its own. The trial app asked it when it opened; yours has never asked. This lesson
teaches it to ask, and to ask it to forget.

---

## Before you start

**What you need:** [06](06-dos-botones-una-cuenta.md) finished (its `verifica` at 66 of 66). Steps 1 to 3 and 5 ask
for no account. Step 4 is on your phone or emulator, with your Google account from 04.

**The starter project.** From the root of the series repo:

```bash
git pull
cp lecciones/03-la-app-conoce-a-firebase/inicio/app/google-services.json lecciones/07-la-sesion-se-queda/inicio/app/
cd lecciones/07-la-sesion-se-queda/inicio
```

It starts from 06's solution, with this lesson's scaffolding on top. `./verifica solucion` comes out red on a clean
clone, because without your file the project does not build. Your answers go in `respuestas/07.md`.

**A rule that is still in force.** Never paste or upload a token, nor screenshots or logs that carry one. This
lesson talks a lot about tokens, and for that very reason it prints none: the app only reads the user's name.

**What this lesson does not do.** It does not tell the first time apart (lesson 08). It does not delete your Google
account from the phone or your user from the Firebase project: signing out is neither of those, and step 5 explains
why.

---

## Step 1 · Without AI: where does the app land when you reopen it?

This is exercise 7.1 and it is done **without Claude Code**. Do not read step 2 yet.

Think about this lesson's **finished** app: the one that already asks Firebase when it opens. Write in
`respuestas/07.md` two lines that start like this:

```text
predicción 7.1 con sesión: …
predicción 7.1 sin sesión: …
escrita el AAAA-MM-DD, antes del paso 2 y sin IA
```

*(That is: "prediction 7.1 with a session", "prediction 7.1 without a session", and "written on YYYY-MM-DD, before
step 2 and without AI".)*

In the first: you signed in with Google, closed the app from the recent apps list and open it again. Which screen
do you see, and does the window to choose an account open? In the second: you tapped «Sign out» («Cerrar sesión»)
and open it again. Which screen do you see, and what happens when you tap «Sign in» («Entrar»)?

**Measure the starting point.** From the repo root:

```bash
./lecciones/07-la-sesion-se-queda/verifica
```

On Nov 19, 2026, with an invented file, the last line of the count was
`Resultado: 68 de 82 pruebas en verde (14 en rojo, 0 con error, 0 saltadas)`. There are 82: the 66 you inherit from
06, already green, and 16 for the session. Of those, two already pass with the scaffolding and fourteen wait for
your code.

*(Result: 68 of 82 tests green: 14 red, 0 with error, 0 skipped.)*

**What proves it:** your prediction, against what you see in step 3 (the tests) and in step 4 (your phone). The
`verifica` only confirms that you left the lines: it does not see when you wrote them, nor whether you used AI,
nor whether you got it right. That is why you keep your prediction with its date, before step 2, and declare below
it, in one line, that you made it without AI. The green does not grade the content.

---

## Step 2 · What the project already carries

**Firebase already said how to ask.** Its guide for signing in with Google on Android says: "when initializing your
Activity, check to see if the user is currently signed in", and it does so by reading `currentUser`. The
`FirebaseAuth` reference sums it up: `getCurrentUser()` returns the user who signed in, or `null` if there is none.

In the app, three files are new and four changed with respect to 06's solution; in the tests, three new classes and
their double, `DoblesDeSesion.kt`, arrive. Open them; edit only the two that step 3 names:

| File | What it carries |
|---|---|
| `Sesion.kt` (new) | The `Sesion` interface: `quienSigueAdentro()` and `cerrar()` |
| `PuertaDeLaSesion.kt` (new) | The `RecuerdoDeLaSesion` gate with three functions: `usuarioActual()`, `olvidarEnFirebase()` and `olvidarEnElTelefono()`; its real implementation, `RecuerdoConFirebase`; and, separately, the `LimpiadorDeCredenciales` that talks to Credential Manager |
| `SesionDeLaTienda.kt` (new) | The shape of your class, empty. You write it in step 3 |
| `Tienda.kt` | When it opens, it asks the session **once** who is still inside. If someone, it opens on the home screen with their name; if nobody, on the welcome screen |
| `Inicio.kt` | The «Cerrar sesión» button. It is only drawn if there is a session: without one, the home screen is 06's |
| `MainActivity.kt`, `Armado.kt` | They also assemble the session. For now `Armado.kt` returns `SesionSinConectar()`, which remembers nobody: that is how your app from 06 behaves |

Everything else is identical to 06's solution, and the `verifica` compares it file by file. It is a restriction of
this activity, not of the objective.

**The gate's three functions, each with its source:**

- `usuarioActual()` reads `FirebaseAuth.getInstance().currentUser` and returns only its name.
- `olvidarEnFirebase()` calls `signOut()`. The reference says it signs out the current user **and deletes them from
  the disk cache**. That sentence solves the scene's mystery: Firebase keeps the user on the phone's disk, and that
  is why it survives closing the app.
- `olvidarEnElTelefono()` asks its cleaner to call Credential Manager's `clearCredentialState`. Firebase's guide
  asks for it when signing out, in addition to `signOut`. The Credential Manager reference explains what it is
  for: a credential provider can store an active session of your app and give it priority the next time. This
  notice asks it to clear that state; that way it can stop prioritizing the previous credential. The options that
  show up at the end are decided by the provider, and are checked on the phone.
- The cleaner goes apart, in its own gate, for a reason that has to do with the tests: that way a test can give the
  real gate a fake cleaner and see that it does call it when signing out.

**A warning from the documentation itself.** Firebase's page on managing users warns of two cases.
`getCurrentUser` can return `null` because Firebase has not finished preparing yet. And it can return a user whose
token is no longer valid, for example if they were deleted from another device and the phone's token has not been
renewed. To react to that it offers an `AuthStateListener`, which notifies every time the token's state changes.

This lesson does a single read on opening, like Google's guide, to study the basic cycle. That read does not
guarantee that Firebase has already recovered the session, or that the phone's token is still valid. A production
app listens to the `AuthStateListener` or something equivalent. Check the real result on your phone: an 82 of 82
does not prove that the session is really saved.

---

## Step 3 · With Claude Code: remember and sign out

It comes in the same way as always: **you predict → you ask → you read what it generated → you run the `verifica` →
you explain.**

1. **Predict**, in `respuestas/07.md`: if `cerrar()` asked Firebase to forget and the notice to the phone failed,
   is the person left inside or outside?
2. **Ask** Claude Code, standing in `inicio/`: "In `SesionDeLaTienda.kt`, `quienSigueAdentro()` returns whatever
   `recuerdo.usuarioActual()` says. `cerrar()` first calls `recuerdo.olvidarEnFirebase()` and then
   `recuerdo.olvidarEnElTelefono()` inside a `try`: `CancellationException` is let through and
   `ClearCredentialException` is caught without doing anything else, because the session is already closed. In
   `Armado.kt` change only the body of `armarSesion` so that it returns `SesionDeLaTienda`, and delete
   `SesionSinConectar` there. Do not change any other file."
3. **Read.** The core must look like this:

```kotlin
override fun quienSigueAdentro(): String? = recuerdo.usuarioActual()

override suspend fun cerrar() {
    recuerdo.olvidarEnFirebase()
    try {
        recuerdo.olvidarEnElTelefono()
    } catch (e: CancellationException) {
        throw e
    } catch (e: ClearCredentialException) {
        // The notice could not be given. Firebase's session is already closed.
    }
}
```

   Check four things: that `cerrar()` does **both** things and in that order; that the `catch` is not a bare
   `Exception` that swallows the cancellation; that your class only knows `RecuerdoDeLaSesion`, without asking which
   gate it got; and that nobody touched the tests, the gates or the screens.

4. **Run the `verifica`.** All 82 tests can end up green. The delivery is still not complete: your lines for 7.1 and
   7.3 in `respuestas/07.md` are missing, and the `verifica` says so at the end.
5. **Explain**, in three lines: why Firebase forgets first and the notice to the phone comes after, inside the
   `try`.

**What it proves, and how far.** The tests use 06's fake project as if it were the phone's memory. Ana signs in
with Google; the test "closes and reopens" the app, and Ana is still inside without the accounts window opening.
She taps «Cerrar sesión», the app goes back to the welcome screen, is reopened and asks her to sign in; on signing
in again she reaches the **same** user. Other tests give your class a fake gate and look at what it was asked: to
forget once, to notify once, that a notice that fails does not blow up the app, and that the cancellation is not
swallowed. And with `MainActivity` they measure that the session the app assembles is yours; that with the real
gate, on your computer, nobody is inside; and that, with that same real gate and a fake cleaner, signing out asks
Credential Manager to clear. In addition, the `verifica` reviews as text that `SesionDeLaTienda` only programs
against `RecuerdoDeLaSesion`: if it names a specific gate or a double, the simulated level is left incomplete.

The sabotages were measured on the solution on Nov 19, 2026:

| Sabotage | Result |
|---|---|
| `quienSigueAdentro()` that always answers `null` | 78 of 82 |
| `cerrar()` without `olvidarEnFirebase()` | 78 of 82 |
| `cerrar()` without the notice to the phone | 78 of 82 |
| The notice without a `try` | 81 of 82 |
| `catch (e: Exception)` that swallows the cancellation | 81 of 82 |
| `quienSigueAdentro()` that always answers that someone is inside | 71 of 82, and two of the red ones are inherited from earlier lessons |
| The notice to the phone only when the gate is **not** the real one | 81 of 82, and the «solo programa contra `RecuerdoDeLaSesion`» (only programs against `RecuerdoDeLaSesion`) mark in red |

Each one also leaves its mark in red in the output, with the step that fixes it.

The last one is what justifies the separate cleaner: without a test that signs out with the real gate, a class that
behaves well only with the doubles would come out green.

Three limits. "Reopening" in the tests is drawing the app from scratch with the same fake memory: no test kills the
process or reads the phone's disk. No test sees a real Firebase user: on your computer there never is one. And no
test calls the real Credential Manager: on your computer it does not answer (measured on Nov 19, 2026: ten seconds
with no response). The tests see that it is asked to clear, not what the accounts window offers afterwards: that is
only seen on the phone.

---

## Step 4 · On the phone: reopen and sign out

Install and open the app with your `google-services.json`, as in 04:

```bash
./gradlew installDebug
```

**With a session.** Sign in with Google. Close the app from the recent apps list (swipe it away) and open it again
from the app drawer. Compare with your first prediction.

**Without a session.** Tap «Cerrar sesión». Close the app the same way and open it. Tap «Entrar» and «Entrar con
Google». Compare with your second prediction.

What was already seen in the Nov 13, 2026 trial, with test code: on reopening, the app greeted without tapping the
button. What we have **not yet captured with a real account** is this walkthrough with this lesson's code: the
«Cerrar sesión» button, the app that on reopening asks you to sign in, and what the accounts window shows
afterwards. If it appears different to you, tell us.

With Microsoft it is worth knowing something. Firebase's guide for Microsoft documents signing out in the app, with
`signOut`, and says nothing about the browser. In the trial, on signing in with Microsoft again, its page already
showed an active browser session, with the account ready to choose without a password. We saw that in that browser,
that day; it is not a guarantee for any browser. On a shared computer, check that session and close it separately.

**And lesson 06's debt.** The manual test in its step 5 asked to sign out in order to sign in again with Microsoft,
and proposed deleting the app's data. Now you have «Cerrar sesión»: it is the way out that the app itself offers.

**What proves it:** what you observe. There is no mark in the `verifica` for this step.

---

## Step 5 · Four things that are not the same

This is exercise 7.3. When you reopen with a session and when you sign out, four different things are involved, and
each one has its source:

| | What it is | What «Cerrar sesión» does to it in this app | Source |
|---|---|---|---|
| **The current user** | The user of your project that Firebase remembers on this phone (`currentUser`) | `signOut()` signs them out and deletes them from the disk cache: `currentUser` is left as `null` | `FirebaseAuth` reference (`getCurrentUser`, `signOut`) |
| **Firebase's tokens** | The ID token lasts one hour. The refresh token is used to ask for another one, and expires if the user is deleted or disabled, or on a major change in their account, such as the password or the email. The server can also revoke it, and resetting the password revokes it | `signOut()` clears the state on the phone. It is not a revocation on the server | Firebase's *Manage User Sessions* |
| **The linked identities** (`providerData`) | The identities of each provider linked to your Firebase user: with Google and with Microsoft, if you linked them in 06 | Nothing: neither `signOut` nor `clearCredentialState` unlinks them | `FirebaseUser` reference (`getProviderData`) |
| **What a provider remembers on its own side** | The credential state that a provider on the phone can store for this app. Apart from it are your Google account on the phone and your Microsoft session in the browser | `clearCredentialState` only asks to clear that local state for this app. It does not delete the Google account from the phone or the Microsoft browser session | `CredentialManager` reference (`clearCredentialState`) |

The sessions page belongs to Firebase's server side, but it describes the session of any user: "Firebase
Authentication sessions are long lived". That is why the app does not ask you to sign in every hour: the ID token
expires, and Firebase asks for another one with the refresh token.

**Now you.** For each statement, write in `respuestas/07.md` `verdadera` or `falsa` ("true" or "false"), like this:
`7.3-1: falsa`.

1. Signing out in the app deletes your Google account from the phone.
2. Firebase's ID token lasts one hour; that is why, the next day, the app asks you to sign in again.
3. In this lesson's code, if on opening Firebase already has a current user, the app greets without opening Google's
   accounts window.
4. After `signOut()`, `currentUser` is `null`.
5. Signing out in the app unlinks Microsoft from your Firebase user: it disappears from `providerData`.

Choose **one** and justify it with the documentation: copy the address of the official page in a line that starts
`fuente 7.3: https://` ("source 7.3"), and below it, in your own words, what it says.

**What proves it:** five out of five against "Compare your answers", and your justification. The `verifica` only
confirms that you left the five lines and the source: compare them yourself with that section. The green does not
grade the content.

---

## Your exercises

Step 1 is done **without AI**.

| # | You do | It is done when |
|---|---|---|
| 7.1 | You predict which screen the app lands on when reopened with and without a session, and you check it | Your two `predicción 7.1` lines are in `respuestas/07.md`, with their date, written before step 2 and declared without AI, and you compared them with the tests from step 3 and with your phone in step 4. The `verifica` only sees that the lines exist |
| 7.2 | You implement signing out so that on coming back you are asked to sign in again | The `verifica` says «Nivel simulado: completo» (simulated level: complete), with the 82 tests green. It is the simulated level; step 4 is your test on the phone |
| 7.3 | You tell apart the current user, Firebase's tokens, the linked identities and what a provider remembers | Five out of five against "Compare your answers", and one justified with its official source. You compare that yourself: the `verifica` does not grade it |

**Time:** allow an hour and a quarter without step 4, and twenty minutes more with it. It is an estimate: we adjust
it with how long the first cohort takes.

---

## Not running?

| You see | Probable cause | What you do |
|---|---|---|
| `File google-services.json is missing.` | You did not copy your file to `inicio/app/` | The `cp` from "Before you start" |
| `NotImplementedError` in the session tests | `SesionDeLaTienda.kt` still has its `TODO`s | Step 3 |
| ❌ «Restricción de esta actividad… ya no son iguales en inicio/ y en solucion/» (Restriction of this activity… they are no longer equal in inicio/ and in solucion/) | A given file changed | `git diff -- <file>` and undo it by hand |
| ❌ «Cuando el aviso falla» (When the notice fails) | Your `catch` catches `Exception` and swallows the cancellation, or there is no `try` | Step 3: `CancellationException` first, and it is rethrown |
| ❌ «De las 66 pruebas heredadas…» (Of the 66 inherited tests…) and several session tests in red | Your session says someone is always inside, and the app no longer opens on the welcome screen | `quienSigueAdentro()` answers whatever the gate says, nothing more |
| On the phone, the app opens on the welcome screen even though you signed in | You installed the one from `inicio/` before step 3, or you deleted the app's data | Finish step 3 and install again |
| On signing in with Microsoft again it does not ask for your password | It may be the Microsoft session in the browser, which is not your app's | Step 4. On a shared computer, check it and close it in the browser |
| The app opens on the welcome screen, and the session shows up afterwards | Firebase may not have finished preparing when the app did its single read | Do not delete data or take the test as good: note the case. This lesson does a single read; a robust version listens to the `AuthStateListener` (step 2) |
| ❌ «SesionDeLaTienda solo programa contra RecuerdoDeLaSesion…» (SesionDeLaTienda only programs against RecuerdoDeLaSesion…) | Your class names a specific gate or a double | Step 3: your class only knows the interface |

This lesson's `verifica` was measured on a Mac with Apple silicon and on Linux Mint 22.3, on Nov 19, 2026: with the solution, 82 of 82 on both.

---

## To wrap up

**Answer without reading again:**

1. Why did 06's app open on the welcome screen even though Firebase remembered you?
2. What two things does «Cerrar sesión» do, and why in that order?
3. Your `verifica` gives 82 of 82. Say one thing that proves and one thing it does not.
4. If the ID token lasts one hour, why does the app not ask you to sign in every hour?

**What comes next:** in L08, the first time: the app tells the new user apart from the one who comes back.

---

## Compare your answers

**Step 1.** With a session: if Firebase already has the current user when the app does its read, the app opens
straight on the home screen, with your name, and the accounts window does not open. It is what the tests measure,
and what the trial saw with a real account; with a single read, it is not a guarantee (step 2). Without a session:
it opens on the welcome screen; on tapping «Entrar» you reach the screen with the buttons, and on signing in with
Google, the window does open.

**Step 3.** Outside. Firebase forgot first, and that is what decides whether someone is still inside. The notice to
the phone only changes which options are offered next time; if it fails, it leaves nobody inside.

**Step 5.**

1. False. `clearCredentialState` only asks to clear the state that a provider kept for your app; the Google account
   stays on the phone.
2. False. The ID token lasts one hour, but Firebase asks for another with the refresh token: sessions are long
   lived. The refresh token expires or is revoked for other reasons (the user is deleted or disabled, the password
   or the email changes, or the server revokes it), not because time passes.
3. True. The app reads `currentUser` once on opening, and with a current user it goes straight to the home screen;
   the tests measure that the window does not open. Whether Firebase already has it ready on opening is another
   question (step 2).
4. True. The `signOut()` reference says it signs out the current user and deletes them from the disk cache;
   `getCurrentUser()` returns `null` if there is none.
5. False. `providerData` are the identities linked to your Firebase user, and signing out does not unlink them:
   neither `signOut` nor `clearCredentialState` touches that link.

**To wrap up:**

1. Because the app never asked it: it always opened on the welcome screen. Firebase did keep the user.
2. It asks Firebase to forget the user and notifies the phone's providers. Firebase first, because that is what
   closes the session; the notice can fail without leaving anyone inside.
3. It proves what your session decides on opening, what it asks for on signing out (also with the real gate) and
   which screen the app lands on, with a fake memory. It does not prove that Firebase remembers your user on your
   phone, nor what the accounts window offers after signing out.
4. Because Firebase uses the refresh token to get a new ID token. Signing out in the app does not revoke that token
   on the server: it clears the phone's state.

---

## Lesson sources

Opened on Nov 19, 2026.

- Firebase. *Authenticate with Google on Android* (check `currentUser` when initializing the Activity; to sign out, `signOut` and `clearCredentialState`, with its `ClearCredentialException`). https://firebase.google.com/docs/auth/android/google-signin
- Firebase. *Manage Users in Firebase* (Android) (`getCurrentUser` can return `null` if Firebase has not finished preparing, or a user whose token is no longer valid; `AuthStateListener`; `getProviderData`). https://firebase.google.com/docs/auth/android/manage-users
- Firebase. *Manage User Sessions* (long-lived sessions; one-hour ID token; when the refresh token expires; resetting the password revokes it; `revokeRefreshTokens` from the server). https://firebase.google.com/docs/auth/admin/manage-sessions
- Firebase. `FirebaseAuth` reference (`getCurrentUser`; `signOut` signs out and deletes the user from the disk cache; `addAuthStateListener`). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth
- Firebase. `FirebaseUser` reference (`getProviderData`: the identities linked to the user; `getDisplayName`). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseUser
- Firebase. *Authenticate Using Microsoft on Android* (to sign out, `signOut`; it does not mention the browser session). https://firebase.google.com/docs/auth/android/microsoft-oauth
- Android Developers. `CredentialManager` reference (`clearCredentialState`: when to call it and what for). https://developer.android.com/reference/kotlin/androidx/credentials/CredentialManager
- Series trial, Nov 13, 2026, Mac with Apple silicon, with test code and a real account: on reopening, the app greeted before the button was tapped; on signing in with Microsoft again, its page already showed the signed-in account.
- Our own measurement: Mac with Apple silicon, Nov 19, 2026. The `verifica` on `inicio/` and `solucion/`, twice in a row each, each step and each sabotage, with invented files and without touching the Firebase console or any account.
- Measurement on Linux Mint 22.3, Nov 19, 2026: the `verifica` on `solucion/` from a fresh clone, 82 of 82.
