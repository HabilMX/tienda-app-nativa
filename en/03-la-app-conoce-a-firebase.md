# Lesson 03 — The app meets Firebase

**Before you read, four words.** One line each; the rest arrive at the step where they are used.

- **Firebase:** a Google service that solves things for your app that you are not going to program, like keeping
  track of who signed in. In this series you use a single part, Authentication.
- **Debug certificate:** the document Android uses to sign the app you build on your computer. It is created
  automatically, and each computer has its own.
- **Fingerprint (SHA-1):** a short digest of that certificate. It is used to recognize it without showing anything
  private.
- **`google-services.json`:** the configuration file you download from Firebase, with the data of your project and
  your app.

**What you take away:** your store app connected to **your** Firebase project, with a configuration file that carries
everything lesson 04 needs, and knowing how to check it without opening the console.

**This lesson's new idea:** an app connects to an external service with two identifiers, its package name and the
fingerprint of its certificate. The service does not know your code: it knows those two identifiers. The rest —the
Gradle plugin and the library— comes ready-made and explained, and is not assessed here.

**A note on the app's texts.** The course project ships its on-screen texts in Spanish, and the files in `lecciones/`
are not translated: the course's automatic checks (the `verifica`) look for those exact texts. So in code blocks,
terminal outputs and on-screen sketches the texts stay as the project has them, and in the prose the lesson quotes
them in English with the original in parentheses the first time: the screen «The store» («La tienda») with its
«Enter» («Entrar») button, and the button «Sign in with Google» («Entrar con Google»).

---

## The scene

You register your app in Firebase. The wizard offers you a button: download `google-services.json`. You download it,
put it in its folder and build.

It builds. All green.

One lesson later you add the «Sign in with Google» button, tap it and you do not get in. The message does not say what
is missing.

That happened in this series' trial, on November 13, 2026, on a Mac: the file downloaded before registering the
fingerprint and enabling Google did not carry two blocks that the button needs. That is what was seen that day, not a
rule Firebase publishes. The stable rule is in its documentation: after registering the fingerprint and enabling
Google, you download the **updated** file, because that is the one that carries what is needed to sign in with Google.

And the "builds green" part is measured here, with an invented file that lacks those blocks (November 18, 2026):
`./gradlew assembleDebug` finishes fine, and this lesson's `verifica` gives 15 of 18, with three red marks that say
"download it again".

The file is a copy of what the console knew about your app **at the moment you downloaded it**. That is why the order
matters. First you meet your app's two identifiers, then you follow the order that works, and in the end you break it
yourself to read the error.

---

## Before you start

**What you need:** [02](02-pantallas-que-cambian.md) finished, the environment from [00](00-ambiente-listo.md)
(`ANDROID_HOME` defined) and a Google account of your own. You will sign in with it to the Firebase console from your
browser: what is said under "Account and trusted machine" in 00 applies.

**What is yours.** You create the Firebase project yourself, with your account, and nobody else uses it. In the
Nov 13, 2026 trial it was created on the no-cost plan, without registering a card.

**The starter project.** From the root of the series repo:

```bash
git pull
cd lecciones/03-la-app-conoce-a-firebase/inicio
```

That is where you work. Next to it is `solucion/`: you can read it and compare it without configuring anything.
**`./verifica solucion` comes out red on a clean clone**, and that is expected: your file is not uploaded, and with
the plugin applied the project does not build without it (measured on Nov 18, 2026: 0 tests run). If you want to
review it, copy your file to `solucion/app/` —it is ignored there too— and remove it when you finish. Your
predictions and explanations go in `respuestas/03.md`, at the repo root (`mkdir -p respuestas` if it does not exist).

**What the project already carries (scaffolding).** It is not assessed here:

| What it carries | Where | Why |
|---|---|---|
| The three screens from 02 and their seven tests | `app/src/main` and `app/src/test` | You start from what you already wrote. Connecting Firebase must not change them |
| The configuration tests and their checker | `app/src/test/…/ConfiguracionTest.kt` and `RevisorDeConfiguracion.kt` | They are the ones the `verifica` runs. Do not change them |
| Three invented configurations | `app/src/test/resources/configuraciones/` | With them the tests show that the checker does tell a complete file from an incomplete one |
| A template of the file, with nobody's data | `app/google-services.json.example` | So you know what a complete one looks like before you have yours |

**What this lesson does not do.** There is no Google button yet and nobody signs in: that is 04. Here the app ends up
introduced to Firebase, and you check it by reading your file.

---

## Step 1 · Without AI: your app's two identifiers

This step is yours, without Claude Code. Firebase is going to ask you for two pieces of data. Both already exist on
your computer.

**The first identifier: the package name.** Predict where it is written and then look for it:

```bash
grep -n "applicationId" app/build.gradle.kts
```

It must answer a line with `applicationId = "mx.habil.ejemplo.tienda"`. According to Android's documentation, that
identifier distinguishes your app on the phone and in the app store, and what several tools call the "package name" is
actually that value.

**The second identifier: the fingerprint.** Android does not install an unsigned app. The one you build for testing
is signed automatically, with a debug certificate that the tools create in your home folder. Ask Gradle for the report:

```bash
./gradlew signingReport
```

This is what it gave on Nov 18, 2026 (here with the fingerprint covered; yours has 20 pairs):

```text
Variant: debug
Config: debug
Store: ~/.android/debug.keystore
Alias: AndroidDebugKey
MD5: …
SHA1: XX:XX:XX:…
SHA-256: …
Valid until: domingo, 5 de noviembre de 2056
----------
Variant: release
Config: none
```

Read three lines:

- **`Variant: debug`** is the test app, the one you install with `installDebug`. Its **`SHA1`** line is the
  fingerprint you are going to register. Copy it to `respuestas/03.md`.
- **`Store`** says which file it came from: the debug store of **this** computer.
- **`Variant: release`** says `Config: none`: this project has nothing yet to sign a version for publishing with. It
  is the topic of exercise 3.3.

**Predict.** Your teammate runs the same command, with the same project, on her computer. Does she get the same
fingerprint? Write yes or no, and why, before continuing.

**The fingerprint can be shown; the key cannot.** Android's documentation separates them like this: the certificate
and its fingerprint are made to be shared with the services that ask for them; the signing key and its passwords are
kept secret. The debug store is only for developing: the same documentation says it is insecure on purpose, and that
is why the stores do not accept an app signed with it.

**What proves it:** your debug fingerprint copied and your prediction written (basis of objectives 3.1 and 3.3).

---

## Step 2 · Without AI: what does not get uploaded

Also without Claude Code, and **before** you have the file: that way there is not even a minute in which git could
take it.

Create the `.gitignore` file in the `inicio/` folder, with these two lines:

```text
# Tu configuración de Firebase: cada quien tiene su propio proyecto.
google-services.json
```

*(The comment says: "Your Firebase configuration: everyone has their own project.")*

Check it. This command asks git whether it would ignore the file and by which rule (it only looks):

```bash
git check-ignore -v app/google-services.json
```

A line must appear with your `.gitignore` file, line 2, the rule `google-services.json` and the path
`app/google-services.json`. The beginning of the line changes depending on where you run git; standing in `inicio/`,
on Nov 18, 2026 it was like this:

```text
lecciones/03-la-app-conoce-a-firebase/inicio/.gitignore:2:google-services.json	app/google-services.json
```

The series repo already carried the same rule at its root; git reports the one closest to the file, which is yours.
Yours is the one that protects the project the day it lives in its own repo.

**Why it is not uploaded, said carefully.** It is not because it is a secret. Firebase's documentation says that file
carries unique but non-secret identifiers, and that its API keys only identify your project: what protects the data
are the security rules, the permissions and App Check. In this series it stays out for another reason: **everyone has
their own project**. If you uploaded yours, your teammate's app would talk to your Firebase, and with a fingerprint
that is not hers.

**What is never uploaded**, in any repo:

- Signing keys (`.jks` or `.keystore` files) and their passwords.
- Passwords, of anything.
- The Microsoft client secret you will create in lesson 05.

**What proves it:** your explanation in `respuestas/03.md` (objective 3.2). The `verifica` only checks that the line is
in your `.gitignore`, that the `.example` template exists and that git tracks no `google-services.json`.

---

## Step 3 · In the console: the order that works

Two more words, which you will now see written out:

- **OAuth:** the agreement by which an app asks Google to confirm who you are, without seeing your password. You use
  it in lesson 04; today you only prepare its data.
- **OAuth client:** the record by which Google recognizes an app that is going to ask for that.

Open `app/google-services.json.example`. This is what a complete file looks like. Find two blocks inside
`oauth_client`: the one that says `"client_type": 1`, with `certificate_hash`, and the one that says
`"client_type": 3`. The first is the Android client, with your fingerprint; the second, the web client.

**Predict first.** If you downloaded the file right after registering the app, before giving the fingerprint and
enabling Google, which of those blocks would you expect to find? Write it down.

Now follow this order in the [Firebase console](https://console.firebase.google.com). The order is that of the
Nov 13, 2026 trial. The names of menus and buttons are written as Firebase's documentation writes them on Nov 18,
2026; the console may show them translated or have moved them.

1. **Create the project.** When creating it, the console offers two things the documentation marks as optional: its
   AI assistance and Google Analytics. Turn both off: this series does not use them.
2. **Register the Android app.** In "Android package name" goes `mx.habil.ejemplo.tienda`, copied exactly. The
   documentation warns that it is case-sensitive and that it **cannot be changed** after registering: with another
   name you have to register another app. In the trial, the wizard did not ask for the fingerprint and offered to
   download the file. **Do not download it yet.**
3. **Add the fingerprint.** In "Project settings", on the "Your apps" card, choose your app, tap "Add fingerprint",
   paste the `SHA1` from step 1 and tap "Save".
4. **Enable Google.** The documentation places it at "Security > Authentication", "Sign-in method" tab: enable the
   Google provider and save. In the trial it asked for a public-facing name for the project and a support email: use
   your account's.
5. **Only now, download `google-services.json`**, from the same "Your apps" card, and put it in `inicio/app/`. The
   documentation asks to keep only the most recent file.

**A single location.** The plugin also accepts a file per variant, for example in `app/src/debug/`. This series uses
only `app/google-services.json`, so there is a single file to check, and the `verifica` does not look at the other
folders: with the file somewhere else it comes out red even though the project builds (measured on Nov 18, 2026: 12 of
18, with a notice of where it found it).

**Check it without opening the console.** From the repo root:

```bash
./lecciones/03-la-app-conoce-a-firebase/verifica
```

At this point **you are on track** if the marks for your file are green and only those for step 4 are red. With a
complete invented file, this is what it gave on Nov 18, 2026 (trimmed):

```text
✅ Texto: tu app/google-services.json nombra el paquete mx.habil.ejemplo.tienda
✅ Simulado: una app de tu archivo es del paquete mx.habil.ejemplo.tienda
✅ Simulado: tu archivo trae un cliente de OAuth de Android (tipo 1)
✅ Simulado: ese cliente trae la huella SHA-1 del almacén de depuración de esta computadora
✅ Simulado: tu archivo trae un cliente de OAuth web (tipo 3), el que usará el botón de Google en la lección 04
❌ La app compilada no lleva el project_id y el google_app_id de tu archivo: falta aplicar el plugin …
❌ Ninguna librería de Firebase está entre las dependencias de la app, o no arrancó con el proyecto …
❌ Falta Authentication: la app no trae firebase-auth entre sus dependencias. El BoM no la agrega: …
Resultado: 15 de 18 pruebas en verde (3 en rojo, 0 con error, 0 saltadas), leído de …
```

*(That is the `verifica`'s output, exactly as the script prints it, in Spanish. In English the marks say: your file
names the package; an app in your file is from the package; your file carries an Android OAuth client (type 1); that
client carries the SHA-1 fingerprint of this computer's debug store; your file carries a web OAuth client (type 3),
the one the Google button will use in lesson 04; and, in red: the built app does not carry your file's `project_id`
and `google_app_id`, the plugin is missing; no Firebase library is among the app's dependencies; Authentication is
missing, the app does not carry `firebase-auth`, and the BoM does not add it. Result: 15 of 18 tests green.)*

If instead you see "descárgalo otra vez después de la huella" (download it again after the fingerprint), the file was
downloaded too early: repeat point 5. There is no need to delete anything in the console.

**What the fingerprint mark compares.** The test opens your computer's debug store, calculates its SHA-1 and looks for
it in your file. It asks nothing of Firebase.

**What proves it:** the four "Simulado" marks for your file say what the file carries (objective 3.5). That you
followed the order in your own project no local test can see: that is confirmed when you sign in, in lesson 04.

---

## Step 4 · With Claude Code: the plugin and the library

Your file is already in its folder, and the app does not read it yet. Two pieces of scaffolding are missing:

| Piece | What it does | When it works |
|---|---|---|
| The `google-services` plugin | Reads your `google-services.json` and turns it into resources the app will be able to read. According to its documentation, it can also add basic dependencies for the services you have enabled | At build time. It does not run as code on the phone |
| The Firebase BoM and `firebase-auth` | The BoM is a list of versions that work together: you pin a single one and the libraries go without a version. `firebase-auth` is the Authentication one, and it is the one that provides functions at run time | Inside the app |

This is where the tool comes in, and it always comes in the same way: **you predict → you ask → you read what it
generated → you run the `verifica` → you explain it in your own words.**

1. **Predict.** Write in `respuestas/03.md`: which files are going to change? Is any of them a `.kt`?
2. **Ask** Claude Code, standing in `inicio/`: "In `gradle/libs.versions.toml` add the plugin
   `com.google.gms.google-services` at version 4.6.0 and the Firebase BoM `com.google.firebase:firebase-bom`
   at 35.2.0, with the `firebase-auth` library without a version. Declare the plugin in the root `build.gradle.kts`
   with `apply false` and apply it in `app/build.gradle.kts`; in that same file add the BoM and `firebase-auth`. Do
   not change any `.kt` file, or the tests, or `app/google-services.json`, and do not copy any value from that file
   anywhere else."
3. **Read** the whole change before accepting it. It must be three files, with these pieces:

```toml
# gradle/libs.versions.toml — one new line in each section
[versions]
googleServices = "4.6.0"
firebaseBom = "35.2.0"

[libraries]
firebase-bom = { group = "com.google.firebase", name = "firebase-bom", version.ref = "firebaseBom" }
firebase-auth = { group = "com.google.firebase", name = "firebase-auth" }

[plugins]
google-services = { id = "com.google.gms.google-services", version.ref = "googleServices" }
```

```kotlin
// the root build.gradle.kts — declared, not applied
    alias(libs.plugins.google.services) apply false
```

```kotlin
// app/build.gradle.kts — applied here, and the two dependencies are added
plugins {
    …
    alias(libs.plugins.google.services)
}
…
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
```

   Five things to check as you read:

   - **The versions are the series' ones:** 4.6.0 and 35.2.0, the ones Firebase's documentation shows on
     Nov 18, 2026. If another appears, ask for it to be corrected: the series is tested with those.
   - **Both lines are there, the BoM and `implementation(libs.firebase.auth)`.** The BoM only chooses compatible
     versions; the second line is the one that adds Authentication. With the BoM and another Firebase library in its
     place, the `verifica` gives 17 of 18 and says "Falta Authentication" (Authentication is missing) (measured on
     Nov 18, 2026).
   - **`firebase-auth` carries no version.** The BoM supplies it. A loose version there undoes what the BoM resolves.
   - **The catalog's hyphen becomes a dot:** `google-services` in the catalog is written
     `libs.plugins.google.services` in the Gradle file.
   - **Nobody touched your file or copied its values.** `git status` must not show `google-services.json` (it is
     ignored), and no `.kt` must appear changed.

4. **Run the `verifica`.** Now the line is
   `Resultado: 18 de 18 pruebas en verde (0 en rojo, 0 con error, 0 saltadas)` (result: 18 of 18 tests green, 0 red,
   0 with error, 0 skipped). One mark stays red, the one for `respuestas/03.md`: it gets filled in step 5.
5. **Explain**, in three lines of `respuestas/03.md`: what the plugin does with your file, and at what moment.

**See it yourself (optional).** The plugin leaves what it generated in a `values.xml` under `app/build/generated/res/`.
The exact folder is an internal detail and may change from one version to another, so look for it instead of
memorizing it. Standing in `inicio/`:

```bash
find app/build/generated/res -name values.xml | xargs grep -l google_app_id
```

With the series' versions it answered `app/build/generated/res/processDebugGoogleServices/values/values.xml`
(Nov 18, 2026). Open it and look for `project_id`, `google_app_id` and `default_web_client_id`. With the complete
invented file it carried six names: `default_web_client_id`,
`google_api_key`, `google_app_id`, `google_crash_reporting_api_key`, `google_storage_bucket` and `project_id`. With
the file without OAuth clients, `default_web_client_id` **does not appear**. That is the piece of data the Google
button is going to ask for in lesson 04, and it is the reason for the scene: without a web client in the file, the app
builds without it.

**What each mark proves, and what it does not.**

- **"Simulado", the ones for your file:** they read the text of `app/google-services.json`. They prove what it
  carries.
- **"Simulado", the one for the built app:** it read the resources the plugin generated, as the app would read them.
  It proves that your file reached the app in this build.
- **"Simulado", the one for the library:** the test asked Firebase to start with those resources and read which
  project it ended up with. In the installed app, according to Firebase's documentation, the library does that start
  when the app opens.
- **"Simulado", the one for Authentication:** it asked whether the main class of `firebase-auth` travels in the app.
  It did not use it: it created no session.
- **"Texto" and "Git":** they looked for words in your files and asked git what it tracks.

None of them talked to Firebase. A well-formed file from a project you have already deleted passes green, and so does
one assembled by hand with that shape. That the file comes from your project, that the fingerprint is registered and
that Google is enabled is checked in the only possible way: by signing in, in lesson 04.

**In the installed app.** `./gradlew installDebug` and open it: the welcome must show, as in 02. On the trial emulator
(Pixel 9, Android 37, Nov 18, 2026), with the solution and an invented file, the app opened on «La tienda» with its
«Entrar» button, and the system log (`adb logcat`) showed the line
`FirebaseInitProvider: FirebaseApp initialization successful`. The buttons were not tapped there: the journey is
measured by the simulated tests.

**If a line is still not clear,** before continuing try to explain it in your own words. If it still does not come,
go back to that fragment or ask for a clarification.

**What it proves, and how far.** At the simulated level, the 18 of 18 proves that this file has the shape and the data
the lesson expects, that they reached this build and that Authentication travels in the app. It **does not prove**
that the file comes from your project, that you followed the order or that Google is enabled. That part of objective
3.1 is proven at the real level, when you sign in with your account in lesson 04.

---

## Step 5 · Without AI: break one on purpose

This is exercise 3.4 and it is done **without Claude Code**. You are going to cause the error of "the configuration is
for another package" without touching your file or the console: by changing the identifier on the app's side.

**1. Predict.** In `app/build.gradle.kts` you are going to change `applicationId = "mx.habil.ejemplo.tienda"` to
`applicationId = "mx.habil.ejemplo.otra"`. Write: does it build?, who complains, Kotlin, Gradle or Firebase?

**2. Break it and measure.** Make the change and build:

```bash
./gradlew assembleDebug
```

This is what it gave on Nov 18, 2026 (the path, trimmed):

```text
* What went wrong:
Execution failed for task ':app:processDebugGoogleServices' …
> No matching client found for package name 'mx.habil.ejemplo.otra' in …/app/google-services.json
```

Copy into `respuestas/03.md` the line that starts with `> No matching client found`.

**3. Diagnose**, in your own words, before reading what follows: what did it look for, where did it look and why did
it not find it?

The name of the task says it: `processDebugGoogleServices` is the plugin, at build time. It looked in your file's
`client` list for an app whose `package_name` was this one's `applicationId`, and there is none. Neither the network
nor Firebase took part: the error came from comparing two texts on your computer.

**4. The fix that tempts, and why not.** Editing the `package_name` inside the file by hand removes the build error.
Do not do it: the file is a copy of what is in the console, and the console still has the other app registered. What
is right is for the two identifiers to truly match: either the app uses the registered package, or you register
another app with the new package and download **its** file.

**5. Fix it.** Put the line back to `applicationId = "mx.habil.ejemplo.tienda"`. This command must show only the
changes from step 4 (it only looks):

```bash
git diff -- app/build.gradle.kts
```

Run the `verifica`: 18 of 18, and now also the `respuestas/03.md` mark.

That green confirms two things: that the project was repaired and that you pasted the error line. It does not know
whether you broke it, nor does it read your prediction or grade your diagnosis. 3.4 is assessed with your
explanation, and is finished when you can tick all three:

- [ ] Your prediction from point 1 is written, with what you saw below it.
- [ ] You pasted the error line.
- [ ] You said who complained, which two texts it compared and why editing the file by hand is not the fix.

**What proves it:** "Break one on purpose" (objective 3.4).

---

## Your exercises

Steps 1 and 2, 3.3 and 3.4 are done **without AI**.

| # | You do | It is done when |
|---|---|---|
| 3.1 | You follow the order: project → app → fingerprint → Google → download | The `verifica` gives 18 of 18 with your file: that validates the file and the build. That the project is yours, with the fingerprint registered and Google enabled, is validated by the real sign-in in lesson 04 |
| 3.2 | You explain why `google-services.json` is not uploaded in this series and name what is never uploaded | Your explanation is in `respuestas/03.md`. The `verifica` only checks the `.gitignore`, the `.example` and what git tracks |
| 3.3 | You tell the debug fingerprint from the one of a published release | You answered the three cases below |
| 3.4 | You break the package and diagnose the error | Your evidence is the three boxes of step 5, written by you. The `verifica` only confirms that the project was repaired and that you pasted the error |
| 3.5 | You check, without opening the console, that your file carries the client with your fingerprint and the web client | The "Simulado" marks for your file are green. The fingerprint they compare is the debug one of this computer |

**Exercise 3.3 · Which fingerprint do you register?** The one you registered today is your computer's debug one.
According to Android's and Google's documentation, an app has more than one:

| Who signs the app | Where its fingerprint comes from |
|---|---|
| Your computer, when building for testing | The debug store: `./gradlew signingReport`, `debug` variant |
| You, with your own key, to publish | The store of that key. It is not the debug one |
| Google Play, with Play App Signing | The Play Console, on the app's signing page. Google keeps the key and it is not the one you use to upload the app |

Answer in `respuestas/03.md` which fingerprint has to be registered in Firebase in each case:

1. Your teammate clones your project and wants to sign in with Google from her computer, against your Firebase.
2. You publish the app on Google Play with Play App Signing and users download it from there.
3. You change computers and keep developing.

**Time:** allow about three hours, with the Firebase console and the "Break one on purpose". It is an estimate: we
adjust it with how long the first cohort takes.

---

## Not running?

The messages in English are the ones the `google-services` 4.6.0 plugin gave on Nov 18, 2026.

| You see | Probable cause | What you do |
|---|---|---|
| `File google-services.json is missing.` | The plugin is already applied and the file is not in `app/` | Put it in `inicio/app/`, with that exact name. On `solucion/` it is what you expect on a clean clone |
| ❌ «Falta Authentication» (Authentication is missing) | The BoM is there and the library is missing | Add `implementation(libs.firebase.auth)` in `app/build.gradle.kts` |
| ℹ️ «Hay uno en …/app/src/debug/…» (There is one in …/app/src/debug/…) | The file is in a variant folder | Move it to `app/`: this series uses a single location |
| `No matching client found for package name '…'` | The app's `applicationId` and the file's package do not match | Step 5. If the file is for another package, register the app with the series' package and download its file |
| ❌ «no trae un cliente de OAuth de Android (tipo 1)» (does not carry an Android OAuth client (type 1)) | You downloaded it before registering the fingerprint or enabling Google | Download it again (step 3, point 5) |
| ❌ «Ningún cliente de Android de tu archivo trae la huella de depuración de esta computadora» (No Android client in your file carries this computer's debug fingerprint) | You registered another fingerprint, or the file was downloaded on another computer | Run `./gradlew signingReport`, register that `SHA1` and download again |
| ❌ «no trae un cliente de OAuth web (tipo 3)» (does not carry a web OAuth client (type 3)) | You downloaded it before enabling Google | Enable Google and download again |
| ❌ «Git sí rastrea un google-services.json» (Git does track a google-services.json) | It was added before the `.gitignore` existed | `git rm --cached <file>` takes it out of git without deleting your copy |
| `keytool -list -v` ends with `MissingFormatArgumentException` | It happened on the series' Mac with Java 25 and the system in Spanish (Nov 18, 2026) | Use `./gradlew signingReport`, which is what the lesson asks for |
| `~/.android/debug.keystore` does not exist and you do not have the fingerprint | You have not built anything on this computer yet: the store is created by the first debug build, not by `signingReport` (measured on Linux Mint 22.3, Nov 18, 2026) | Run `./gradlew assembleDebug` and then `./gradlew signingReport` |

On Linux Mint 22.3 (x86_64), this lesson's `verifica` came out the same as on the Mac (Nov 18, 2026): on `inicio/`, 11
of 18 green; on `solucion/` without your file, "0 pruebas corridas" (0 tests run) with the notice; and with a test
file that carried that computer's fingerprint, 18 of 18. The Firebase dependencies added about 200 MB to `~/.gradle`.

---

## To wrap up

**Answer without reading again:**

1. What are the two identifiers by which Firebase recognizes your app, and where is each one on your computer?
2. Why is the file downloaded at the end and not when the wizard offers it?
3. Your `verifica` gives 18 of 18. Say one thing that proves and one thing it does not.
4. Why is `google-services.json` not uploaded in this series? And what is never uploaded in any?

**What comes next:** in L04 you add the button and sign in with Google. That is where everything that is written in
your file today gets checked, for real.

---

## Compare your answers

**Step 1.** She does not get the same one. The debug store is created by the tools in each person's home folder, so
each computer has its own certificate and fingerprint. The project is the same; the signature is not.

**Step 3.** In the Nov 13, 2026 trial, neither of the two: the file downloaded before the fingerprint and before
enabling Google did not carry type 1 (Android, with `certificate_hash`) or type 3 (web). It is an observation from
that day. What Firebase's documentation does state is the opposite: the **updated** file, downloaded after those
two steps, carries the OAuth information the button needs. That is why it is downloaded at the end.

**Step 4.** Three files change: `gradle/libs.versions.toml`, the root `build.gradle.kts` and `app/build.gradle.kts`.
No `.kt`: connecting the app touched no screen, and that is why the seven tests from 02 are still green. The plugin
reads `app/google-services.json` at build time and generates app resources (`project_id`, `google_app_id` and, if the
file carries a web client, `default_web_client_id`). The plugin does not run on the phone: what works at run time are
the libraries you declared, like `firebase-auth`.

**Step 5.** It does not build. Gradle complains, in the task of the `google-services` plugin; neither Kotlin nor
Firebase. It compared the app's `applicationId` with the file's `package_name`s. Editing the file removes the error
and leaves the console with another app registered: the identifiers would still not match where it matters.

**Exercise 3.3.**

1. Her computer's **debug** one, added to your app in Firebase: an app accepts several fingerprints. Afterwards she
   needs the file downloaded again.
2. That of the certificate Google Play signs the app with, which is looked up in the Play Console. The debug one and
   that of the key you upload the app with do not work for what users download.
3. The debug one of the new computer: it is another store and another fingerprint.

**To wrap up:**

1. The package name, in `applicationId` in `app/build.gradle.kts`, and the fingerprint of the certificate the app is
   signed with; the debug one comes from the store in your home folder and `signingReport` prints it.
2. Because the file is a copy of what the console knows at that moment, and Firebase asks to download the updated one
   after the fingerprint and after enabling Google. In the trial, the earlier one did not carry the OAuth clients.
3. It proves that the text of your file carries the package, the Android client with your debug fingerprint and the
   web client, that the built app carries that data and that Authentication travels in the app. It does not prove that
   the file comes from your project, that the project exists or that you can sign in with Google.
4. Because everyone has their own project, not because it is secret. Signing keys, passwords and the Microsoft client
   secret are never uploaded.

---

## Lesson sources

Opened on Nov 18, 2026.

- Firebase. *Add Firebase to your Android project* (steps, the package cannot be changed, plugin 4.6.0 and BoM 35.2.0, "unique, but non-secret identifiers"). https://firebase.google.com/docs/android/setup
- Firebase. *Google services Gradle plugin and JSON config file* (which resources the plugin generates and which field each comes from; the error messages). https://firebase.google.com/docs/android/google-services-plugin-and-file
- Firebase. *Understand Firebase for Android* (what the BoM is; the plugin works at build time). https://firebase.google.com/docs/android/learn-more
- Firebase. *Learn about and manage API keys for Firebase* (the keys identify, they do not authorize). https://firebase.google.com/docs/projects/api-keys
- Firebase. *Authenticate with Google on Android* (fingerprint, enabling Google and downloading the updated file). https://firebase.google.com/docs/auth/android/google-signin
- Firebase. *FirebaseInitProvider* ("Initializes Firebase APIs at app startup time"). https://firebase.google.com/docs/reference/android/com/google/firebase/provider/FirebaseInitProvider
- Firebase Help. *Download Firebase config file or object* and *Add a SHA fingerprint*. https://support.google.com/firebase/answer/7015592 · https://support.google.com/firebase/answer/9137403
- Google for Developers. *Client authentication* (`./gradlew signingReport`, debug store, Play App Signing). https://developers.google.com/android/guides/client-auth
- Android Developers. *Sign your app* (debug certificate, keys, fingerprints for providers, Play App Signing). https://developer.android.com/studio/publish/app-signing
- Android Developers. *Configure the app module* (the `applicationId` and the "package name"). https://developer.android.com/build/configure-app-module
- Android Developers. *Migrate your build to version catalogs* (`libs.versions.toml`, `alias`). https://developer.android.com/build/migrate-to-catalogs
- Google's Maven repository: its indexes list `google-services` 4.6.0 and `firebase-bom` 35.2.0. https://dl.google.com/dl/android/maven2/com/google/gms/google-services/maven-metadata.xml · https://dl.google.com/dl/android/maven2/com/google/firebase/firebase-bom/maven-metadata.xml
- Series trial, Nov 13, 2026, Mac with Apple silicon: the order of the console steps and what was seen that day (the wizard did not ask for the fingerprint; enabling Google asked for a public-facing name and support email; the file downloaded too early carried no OAuth clients; no-cost plan). The menu and button names in this lesson do not come from the trial: they are those of the documentation cited above.
- Our own measurement: Mac with Apple silicon, Nov 18, 2026 (`signingReport`; the `verifica` on `inicio/` and `solucion/`; the invented files: complete, without clients, for another package and with another fingerprint; the resources the plugin generates; the error messages; `keytool`; the app installed on a new emulator, without an account, which was deleted when finished). The measurements were made with invented files, not with a real Firebase project.

External review (Nov 18, 2026): WITH CHANGES, 2 high priority, 6 medium and 3 low; applied.
