# Your store in your pocket

**By Dorian Chávez, founder of Hábil and integration architect.**

**Course page:** [habil.mx/en/courses/tienda-app-nativa/](https://www.habil.mx/en/courses/tienda-app-nativa/), with the syllabus and the lessons in five languages.

**Who it is for:** anyone who already programs in some language, uses the terminal and git, and has built at least one
interface, web or desktop. Kotlin, Android and OAuth are not assumed: you learn them here.

**What you will know by the end:** how to build with your own hands a native Android app, in Kotlin and Jetpack
Compose, where you sign in with your Google or Microsoft account; the app welcomes you the first time and remembers
you when you come back. And how to explain what Firebase did for you and what you did.

**What you need before you start:** Linux on an x86_64 processor (tested on Linux Mint 22.3), or a Mac, with 16 GB of
RAM for the emulator (with less, you use your Android phone). Windows is not covered. The iOS part is on Mac only.

## The project

The same invented store from [«Ni una orden perdida»](https://www.habil.mx/en/courses/ni-una-orden-perdida/) now has an
app. In this first stage you get as far as sign-in: the customer signs in with Google or with Microsoft and the app
recognizes them. In the following ones, the app consumes the store's API and moves to iOS.

*A note on languages:* the course project ships its on-screen texts in Spanish, and the files in `lecciones/` are not
translated. In code blocks, terminal outputs and on-screen sketches the texts stay as the project has them; the prose
of each lesson gives them in English the first time they appear.

## How you work with AI

In every lesson the same way: you **predict** what is going to happen → you ask Claude Code for the change → you
**read** what it generated → you run the `verifica` → you **explain** in your own words what it does. If you cannot
explain it, you do not move on. And in each lesson there is at least one exercise done without AI.

Each lesson brings an `inicio/` project and a `verifica` that tells you, in red or in green, whether you have already
achieved it. Your written answers go in `respuestas/`. Everyone uses their own Firebase project: `google-services.json`
is never uploaded.

## The lessons

They are done in order: each one starts from what the previous one left. Start with [00](00-ambiente-listo.md), which
gets your environment ready.

### Stage 1 — Getting to sign-in (Android)

| # | Lesson |
|---|---|
| 00 | [Environment ready](00-ambiente-listo.md) |
| 01 | [My first screen runs](01-mi-primera-pantalla.md) |
| 02 | [Screens that change](02-pantallas-que-cambian.md) |
| 02-B | Store component catalog (practice) |
| 03 | [The app meets Firebase](03-la-app-conoce-a-firebase.md) |
| 04 | [Signing in with Google](04-entrar-con-google.md) |
| 05 | [Signing in with Microsoft](05-entrar-con-microsoft.md) |
| 06 | [Two buttons, one account](06-dos-botones-una-cuenta.md) |
| 07 | The session stays |
| 08 | The first time |

The lessons without a link are in preparation and are published in order.
