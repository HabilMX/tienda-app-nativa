# Lesson 06 — Two buttons, one account

**Before you read, three words.** One line each.

- **Collision:** what happens when someone signs in with one provider and their email already has a user in your
  project, created with another provider. You saw it in lesson 05.
- **Linking:** adding another way of signing in to a user that already exists. It does not create a new user.
- **One account per email address:** the option in your Firebase project that makes the collision exist.

**What you take away:** a person with the same email in Google and in Microsoft who ends up with **one** user in
your store, reached with either of the two buttons, because she confirmed it.

**This lesson's new idea:** one person, two providers. With "one account per email address", the second one is
linked to the account that already exists, and only with her confirmation. The rest —the screen that asks, the
linking gate and the note that tells a link from a sign-in— comes ready-made and explained, and is not assessed
here.

**A note on the app's texts.** As in the previous lessons, the project ships its on-screen texts in Spanish, and the
`verifica` looks for those exact texts. In code blocks, terminal outputs and on-screen sketches they stay as the
project has them; in the prose the lesson quotes them in English with the original in parentheses the first time:
the confirmation screen «Also with Microsoft?» («¿También con Microsoft?») with its buttons «Link Microsoft»
(«Enlazar Microsoft») and «Not now» («Ahora no»).

---

## The scene

Ana signed in to the store with Google last week. Today she taps the other button. Her Microsoft account has the
same email.

For her it is the same person signing in to the same store. For your project they are two different credentials
that claim to have the same email.

That was tried in this series' trial, on November 13, 2026, with a Microsoft account whose email had already
signed in with Google. Firebase did not let it through, and answered: "An account already exists with the same
email address but different sign-in credentials".

There are two bad endings. In one, the app creates a second user, and Ana's orders end up split between two
accounts that do not know each other. In the other, the app merges the two accounts on its own, without asking
anybody. Lesson 05 stopped at telling her "sign in with the other button". This one writes the good ending: she
signs in with the one she already had, the app asks her, and if she accepts, both buttons arrive at the same
place.

---

## Before you start

**What you need:** [05](05-entrar-con-microsoft.md) finished (its `verifica` at 44 of 44). Steps 1 to 4 ask for no
Microsoft account. Step 5 does: your registration in Entra from 05, working.

**The starter project.** From the root of the series repo:

```bash
git pull
cp lecciones/03-la-app-conoce-a-firebase/inicio/app/google-services.json lecciones/06-dos-botones-una-cuenta/inicio/app/
cd lecciones/06-dos-botones-una-cuenta/inicio
```

It starts from 05's solution, with this lesson's scaffolding on top. `./verifica solucion` comes out red on a clean
clone, because without your file the project does not build. Your answers go in `respuestas/06.md`.

**A rule that is still in force from 05.** Never paste or upload the value of the Entra key, an access token or an
identity token, nor screenshots or logs that carry them. The result of a link also carries an access token: this
app does not read it. `google-services.json` stays ignored because it belongs to your project: do not use it as
evidence. If a key or a token touched a file, the terminal or a screenshot, it is revoked and another one is
created.

**What this lesson does not do.** It does not undo a link, it does not save or close the session (lesson 07) and it
does not tell the first time apart (lesson 08).

---

## Step 1 · In the console: the rule that makes the collision exist

This lesson's clash —`ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL` when signing in with the second
provider— only appears with one option of your Firebase project: "one account per email address" (*One account per
email address*). Firebase's reference says that error is only thrown with it turned on, and recommends it. With
multiple accounts per email address, Firebase can create a second user: the first bad ending of the scene, and
nothing to resolve. The same exception has other codes, for example when linking an account that already belongs to
another user: they are not this lesson's case, and the app shows them as a failure.

Look for it in your console's Authentication settings and check that it is on. Write this line in
`respuestas/06.md`, without emails or keys:

```text
una cuenta por correo: activa
```

*(That is: "one account per email address: on".)*

The `verifica` does not see your console: it only looks for that line. We did not walk through the exact path to
that option in the console with an account; if you cannot find it, tell us where it was.

**What proves it:** your check, by hand. It is part of the evidence for 6.1.

---

## Step 2 · What the project already carries

**Firebase sets the order.** Its guide on linking accounts asks that the person **first** sign in with any provider
and that **afterwards**, to that user who already signed in, the other one is added. That way, it says, the person
has the same user identifier no matter which one they sign in with. For Microsoft, that provider's guide names the
call: `startActivityForLinkWithProvider`, on the user who already signed in.

Three files are new and four changed with respect to 05's solution. Open them; do not edit them:

| File | What it carries |
|---|---|
| `Enlace.kt` (new) | `ResultadoDeEnlace` (linked, no session, canceled, failure), `EnlaceRetomado` and the `Enlace` interface, with `enlazar()` and `retomar()` |
| `PuertaDelEnlace.kt` (new) | The `EnlazadorDeProveedor` gate. `enlazar` makes the Firebase call; if nobody has signed in, it throws `NadieHaEntrado` without opening anything. `pendiente` picks up a link that was left halfway |
| `PantallaEnlace.kt` (new) | The confirmation: «¿También con Microsoft?» («Also with Microsoft?»), with «Enlazar Microsoft» («Link Microsoft») and «Ahora no» («Not now») |
| `Tienda.kt` | Remembers whether there was a collision. When that person later signs in with the right button, it shows the confirmation instead of the home screen. Only «Enlazar Microsoft» calls your code |
| `PuertaDelNavegador.kt` | One line: it does not take what is pending if what was left halfway was a link |
| `MainActivity.kt`, `Armado.kt` | They also assemble the link. For now `Armado.kt` returns `EnlaceSinConectar()` |

Everything else is identical to 05's solution, and the `verifica` compares it file by file. It is a restriction of
this activity, not of the objective: a change to a given file can be valid code and still come out red, because the
lesson pins them so it can say what it measures.

**A link left halfway is not a sign-in left halfway.** Firebase's reference says that, if the screen dies during a
link, its outcome is picked up with `getPendingAuthResult`: the same place where a sign-in left halfway arrives.
What follows is a decision of this app, not a quotation: that result does not carry a label that is used here to
know whether it was a link or a sign-in. Read always as a sign-in, a canceled link would look like "you could not
sign in". That is why the gate saves a note on the phone before opening the browser to link: "there is a link in
progress". It is a yes or a no; it saves no accounts, tokens or keys. When the app opens, the sign-in gate does not
take what is pending if that note is set, and `Tienda` asks your link first.

**Measure the starting point.** From the repo root:

```bash
./lecciones/06-dos-botones-una-cuenta/verifica
```

On Nov 18, 2026, with an invented file, the last line was
`Resultado: 46 de 66 pruebas en verde (20 en rojo, 0 con error, 0 saltadas)`. There are 66: the 44 you inherit from
05, already green, and 22 for the link. Of those, two already pass with the scaffolding and twenty wait for your
code.

*(Result: 46 of 66 tests green: 20 red, 0 with error, 0 skipped.)*

---

## Step 3 · With Claude Code: link and resume

It comes in the same way as always: **you predict → you ask → you read what it generated → you run the `verifica` →
you explain.**

1. **Predict**, in `respuestas/06.md`: at the moment of the collision, before the person signs in with Google, to
   which user could Microsoft be linked?
2. **Ask** Claude Code, standing in `inicio/`: "In `EnlaceConMicrosoft.kt`, take `EntradaConMicrosoft.kt` as a
   reference. `enlazar()` builds the same provider, calls `enlazador.enlazar` and returns
   `ResultadoDeEnlace.Enlazado`. `retomar()` calls `enlazador.pendiente()`: if it returns `null`, it returns `null`;
   otherwise, it returns an `EnlaceRetomado` with its name and with `Enlazado` if its `falla` is `null`, or with the
   translated failure. The failures are translated in a separate function: `NadieHaEntrado` is `SinSesion`; a
   `FirebaseAuthWebException` with `ERROR_WEB_CONTEXT_CANCELED`, `Cancelado`; any other, `Fallo`.
   `CancellationException` is let through. In `Armado.kt`, change only the body of `armarEnlaceConMicrosoft` and
   delete `EnlaceSinConectar` there. Do not change any other file."
3. **Read.** The core must look like this:

```kotlin
override suspend fun enlazar(): ResultadoDeEnlace =
    try {
        val proveedor = OAuthProvider.newBuilder("microsoft.com")
            .addCustomParameter("prompt", "select_account")
            .build()
        enlazador.enlazar(proveedor)
        ResultadoDeEnlace.Enlazado
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        resultadoDe(e)
    }

override suspend fun retomar(): EnlaceRetomado? {
    val pendiente = enlazador.pendiente()
    if (pendiente == null) return null
    val resultado = if (pendiente.falla == null) ResultadoDeEnlace.Enlazado else resultadoDe(pendiente.falla)
    return EnlaceRetomado(pendiente.nombre, resultado)
}

private fun resultadoDe(falla: Exception): ResultadoDeEnlace = when {
    falla is NadieHaEntrado -> ResultadoDeEnlace.SinSesion
    falla is FirebaseAuthWebException && falla.errorCode == "ERROR_WEB_CONTEXT_CANCELED" ->
        ResultadoDeEnlace.Cancelado
    else -> ResultadoDeEnlace.Fallo
}
```

   Check three things: that `enlazar()` calls the link's gate and does not sign in again; that `retomar()` does not
   always answer "linked"; and that nobody touched the tests, the gates or the screens.

4. **Run the `verifica`.** All 66 tests can end up green. The delivery is still not complete: the «Escrito»
   (written) marks for `respuestas/06.md` are missing, and so is the real level from step 5. The `verifica` says so
   at the end, in «Estado del objetivo 6.1» (status of objective 6.1).
5. **Explain**, in three lines: why the link is made on the user who already signed in, and not at the collision.

**What it proves, and how far.** The `verifica` uses a fake project (`ProyectoFalso.kt`) with two people: Ana, with
the same email in Google and in Microsoft, and Luis, with another email. It proves that before linking Ana clashes
and no second user is created; that after linking her two accesses arrive at the same user; and that Luis remains
a different one. It proves the confirmation, with your classes drawn in `Tienda`: at the collision linking is not
offered, after signing in she is asked without anything being linked, and «Ahora no» does not link. And it proves
that a link left halfway is resumed as a link: the app opens on the confirmation, with «Listo» (Done) or with its
message.

The sabotages were measured on the solution on Nov 18, 2026: not recognizing `NadieHaEntrado`, 63 of 66;
`retomar()` always returning `null`, 60; one that always answers "linked", 63; reading the link left halfway as a
sign-in, 64.

Three limits. "The same user" is that of the fake project, which follows "one account per email address" written by
hand: it does **not** prove that Firebase links your accounts. "Left halfway" was said by a double: no test closed
the app mid-link, and the phone's note was only exercised with nothing pending. And that the sign-in gate respects
that note is seen by no test (without that line, all 66 are green): what backs it is that the file is identical in
`inicio/` and in `solucion/`.

---

## Step 4 · Without AI: what if the app linked on its own?

This is exercise 6.2 and it is done **without Claude Code**. The confirmation is a criterion of this series: linking
gives another account entry to this user, and that is decided by the person who has just authenticated with the
provider of the account that already exists. Here you check it by removing it.

**1. Predict**, in `respuestas/06.md`: if the app linked as soon as the person signs in, without asking, what would
Ana see? And what could happen if the Microsoft account that clashed were not hers?

**2. Break it and measure.** In `Tienda.kt`, inside `recibe`, change this line:

```kotlin
pantalla = if (huboColision && enlace != null) Pantalla.CONFIRMAR_ENLACE else Pantalla.INICIO
```

for these two:

```kotlin
if (huboColision && enlace != null) alcance.launch { enlace.enlazar() }
pantalla = Pantalla.INICIO
```

Run the `verifica`. This is what it gave on Nov 18, 2026 (trimmed):

```text
❌ Restricción de esta actividad (no del objetivo): estos archivos vienen dados y ya no son iguales … Tienda.kt
❌ La confirmación: 2 de 4 pruebas en verde. Nada se enlaza sin el toque de la persona, y solo se le ofrece …
❌ Al tocar «Enlazar Microsoft» no se enlaza, o después Microsoft no llega al mismo usuario (paso 3)
Resultado: 63 de 66 pruebas en verde (3 en rojo, 0 con error, 0 saltadas), leído de …
```

*(In English the marks say: restriction of this activity (not of the objective): these files come given and are no
longer equal … `Tienda.kt`; the confirmation: 2 of 4 tests green, nothing is linked without the person's tap, and
it is only offered to her …; on tapping «Enlazar Microsoft» nothing is linked, or afterwards Microsoft does not
arrive at the same user (step 3). Result: 63 of 66 tests green, 3 red.)*

Copy into `respuestas/06.md` the line that says «La confirmación» (The confirmation).

**3. Fix it.** Put the original line back. Run the `verifica`: 66 of 66, and `Tienda.kt` once again equal to the
lesson's.

**4. Explain**, without reading again: why the app asks for confirmation before linking, and what would happen if it
linked on its own. Compare with "Compare your answers".

**What proves it:** your explanation (objective 6.2). The `verifica` only looks for the pasted line: it does not
know whether you broke it, nor does it grade what you wrote. Offering the link before signing in, at the collision
itself, was measured separately: 62 of 66.

---

## Step 5 · On the phone: the manual test

It is the real evidence for 6.1, and **it cannot yet be completed with these instructions**. What follows says what
has to be observed and what is still to be measured. It requires 05's real level, with your registration in Entra
working, and "one account per email address" on (step 1).

**The walkthrough.** With a Microsoft account whose email has already signed in with Google: tap «Sign in with
Microsoft» («Entrar con Microsoft») and read the message; sign in with Google; accept «Link Microsoft» («Enlazar
Microsoft»). According to Firebase's guide, after a link the person can sign in with any of the linked providers
and reach the same data.

**What is missing: signing out.** To check the Microsoft button you have to sign in again from scratch, and this app
still has no way to sign out: that is lesson 07. The proposal for the trial, without getting ahead on code, is to
delete the app's data on the phone:

```bash
adb shell pm clear mx.habil.ejemplo.tienda
```

The `adb` documentation says that command deletes all the data associated with an app. With a real account we have
not yet checked three things: that the app is left without a session, that the user and their providers are still in
Firebase, and that on tapping «Sign in with Microsoft» you arrive at the same user. We also did not measure where
the console shows the two providers, nor did we kill the process mid-link with `adb shell am kill` (which does not
sign the session out: it is another test). If you try it, tell us what you saw.

**Your record.** When you do it, write in `respuestas/06.md`, without emails or tokens:

| What you write | Example |
|---|---|
| The date, on a line that starts like this | `prueba manual: 2026-11-30` |
| Before linking | one user, with Google |
| After linking | the same user, with Google and Microsoft |

The `verifica` only reads the first line, and even with it, it says it did not see the test.

*(`prueba manual` means "manual test".)*

In the Nov 13, 2026 trial, with test code that did have a way to sign out, the short path was seen: already inside
with Google, Microsoft was linked, the same user was left with both providers, and on signing out and signing in
with Microsoft it arrived at that user.

**What proves it:** it is the only thing that proves Firebase linked your accounts. A 66 of 66 does not replace it.

---

## Your exercises

Step 4 is done **without AI**.

| # | You do | It is done when |
|---|---|---|
| 6.1 | You resolve the collision: the person signs in with the provider she already had and links the other, with confirmation | Three pieces of evidence, and all three are needed. The line «una cuenta por correo: activa» in `respuestas/06.md`, after checking it in your console. Simulated: «Estado del objetivo 6.1» says simulated level complete, with a fake project of two people; it validates the order, the confirmation, the link left halfway and that the two accesses arrive at the **fake** same user. Real: the manual test from step 5, with your record written down; it is the only thing that validates that Firebase linked your accounts, and we have not yet run it with a real account |
| 6.2 | You explain why the app asks for confirmation before linking and what would happen if it linked on its own | Your prediction, the pasted red and your explanation are in `respuestas/06.md`, and they match "Compare your answers". You compare the explanation yourself |

**Time:** allow an hour and a half without the manual test, and half an hour more with it. It is an estimate: we
adjust it with how long the first cohort takes.

---

## Not running?

| You see | Probable cause | What you do |
|---|---|---|
| `File google-services.json is missing.` | You did not copy your file to `inicio/app/` | The `cp` from "Before you start" |
| ❌ «Restricción de esta actividad… lo heredado ya no es idéntico a la solución de la lección 05» (Restriction of this activity… what is inherited is no longer identical to lesson 05's solution) | Something inherited changed | `git diff -- <file>` and undo it by hand |
| ❌ «Restricción de esta actividad… ya no son iguales en inicio/ y en solucion/» (Restriction of this activity… they are no longer equal in inicio/ and in solucion/) with `Tienda.kt` | You come from step 4 | Put the original line back |
| ❌ «Enlazar sin haber entrado primero» (Linking without having signed in first) | Your `resultadoDe` does not recognize `NadieHaEntrado` | Step 3 |
| ❌ «Retomar el enlace» (Resume the link) | `retomar()` does not use what `enlazador.pendiente()` returns, or always answers the same | Step 3 |
| You signed in with Microsoft with an email that had already signed in with Google, and the collision message did not show | Your project allows multiple accounts per email address: Firebase created another user | Step 1. Do not link two users created separately as if it were the lesson's case |
| «Primero entra con el botón que ya usabas…» (First sign in with the button you already used…) | Linking was requested with nobody inside | Sign in first; the link is on a user who already signed in |

On Linux Mint 22.3 (x86_64), this lesson's `verifica` came out the same as on the Mac (Nov 18, 2026), at the
simulated level and with a test file that carried that computer's fingerprint: on `inicio/`, 46 of 66 green; on
`solucion/`, 66 of 66.

---

## To wrap up

**Answer without reading again:**

1. Which option of your project makes this lesson's clash appear, and what happens without it?
2. Why do you sign in first and link afterwards?
3. Your `verifica` gives 66 of 66 and "simulated level: complete". Say one thing that proves and one thing it does
   not.
4. Why can a link left halfway not be read as a sign-in left halfway?

**What comes next:** in L07 the session stays: the app remembers you when you come back, and you learn to close it.

---

## Compare your answers

**Step 3.** To none: at the collision nobody has signed in, so there is no user to add anything to. That is why the
order is sign in first and link afterwards, and why the gate throws `NadieHaEntrado`.

**Step 4.** Ana would see nothing: she would enter the home screen and, behind it, the browser would open to link
without her having asked for it. And if the Microsoft account that clashed belonged to someone else with access to
that email, that account would be left with entry to Ana's user without her deciding it. The confirmation puts the
decision in the person who has just authenticated with the provider of the account that already exists. That proves
she signed in with that provider; it proves nothing else about who she is.

**To wrap up:**

1. "One account per email address". Without it, Firebase can create a second user with the same email, and this
   lesson's clash does not appear.
2. Because linking is adding a provider to a user who already signed in: before signing in there is nobody to add it
   to.
3. It proves what your link asks for and what it answers, that nothing is linked without the person's tap and
   that, in a fake project, the two accesses arrive at the same user. It does not prove that Firebase links your
   accounts.
4. Because Firebase delivers them through the same place and does not say which one it was. Read as a sign-in, a
   canceled link would look like "you could not sign in".

---

## Lesson sources

Opened on Nov 18, 2026.

- Firebase. *Link Multiple Auth Providers to an Account on Android* (first you sign in and then you link; the same identifier with any provider; after linking you can sign in with either. Its example uses `linkWithCredential`; for Microsoft, that provider's guide points to `startActivityForLinkWithProvider`). https://firebase.google.com/docs/auth/android/account-linking
- Firebase. *Authenticate Using Microsoft on Android* (linking Microsoft to a user who already signed in). https://firebase.google.com/docs/auth/android/microsoft-oauth
- Firebase. *Authenticate Using Microsoft with JavaScript* (the Microsoft and Google case with "one account per email address": sign in with the provider that already exists and link the other). https://firebase.google.com/docs/auth/web/microsoft-oauth
- Firebase. `FirebaseUser` reference (`startActivityForLinkWithProvider` and its pending result). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseUser
- Android Developers. *Android Debug Bridge* (`pm clear`: deletes all the data associated with an app; `am kill`). https://developer.android.com/tools/adb
- Firebase. `FirebaseAuth` reference (`getPendingAuthResult`). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth
- Firebase. `FirebaseAuthUserCollisionException` reference (`ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL` only with "one account per email address", which it recommends). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuthUserCollisionException
- Series trial, Nov 13, 2026, Mac with Apple silicon, with test code: the collision with a real account, and the link after signing in with Google.
- Our own measurement: Mac with Apple silicon, Nov 18, 2026. The `verifica` on `inicio/` and `solucion/`, each step and each sabotage, with invented files and without touching the Firebase console, the Entra one or any account.
