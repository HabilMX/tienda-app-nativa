# Tu tienda en el bolsillo

**Por Dorian Chávez, fundador de Hábil y arquitecto de integración.**

**Para quién es:** quien ya programa en algún lenguaje, usa la terminal y git, y ha hecho al menos una interfaz, web o
de escritorio. No hace falta saber Kotlin, Android ni OAuth: se aprenden aquí.

**Qué vas a terminar sabiendo:** construir con tus manos una app Android nativa, en Kotlin y Jetpack Compose, donde
entras con tu cuenta de Google o de Microsoft; la app te da la bienvenida la primera vez y te recuerda al volver. Y
explicar qué hizo Firebase por ti y qué hiciste tú.

**Lo que necesitas antes de empezar:** Linux en un procesador x86_64 (probado en Linux Mint 22.3), o una Mac, con 16 GB de RAM para el
emulador (con menos, usas tu teléfono Android). Windows no está cubierto. La parte de iOS es solo en Mac.

## El proyecto

La misma tienda inventada de [«Ni una orden perdida»](https://github.com/HabilMX/ni-una-orden-perdida) ahora tiene
app. En este primer tramo llegas hasta el inicio de sesión: el cliente entra con Google o con Microsoft y la app lo
reconoce. En los siguientes, la app consume la API de la tienda y se pasa a iOS.

## Cómo se trabaja con IA

En todas las lecciones igual: **predices** qué va a pasar → le pides el cambio a Claude Code → **lees** lo que generó
→ corres el `verifica` → **explicas** con tus palabras qué hace. Si no lo puedes explicar, no avanzas. Y en cada
lección hay al menos un ejercicio que se hace sin IA.

Cada lección trae un proyecto `inicio/` y un `verifica` que te dice, en rojo o en verde, si ya lo lograste. Tus
respuestas escritas van en `respuestas/`. Cada quien usa su propio proyecto de Firebase: `google-services.json` nunca
se sube.

## Las lecciones

Se hacen en orden: cada una parte de lo que dejó la anterior. Empieza por la [00](00-ambiente-listo.md), que deja
listo tu ambiente.

### Tramo 1 — Llegar al inicio de sesión (Android)

| # | Lección |
|---|---|
| 00 | [Ambiente listo](00-ambiente-listo.md) |
| 01 | [Mi primera pantalla corre](01-mi-primera-pantalla.md) |
| 02 | [Pantallas que cambian](02-pantallas-que-cambian.md) |
| 02-B | Catálogo de componentes de la tienda (práctica) |
| 03 | [La app conoce a Firebase](03-la-app-conoce-a-firebase.md) |
| 04 | [Entrar con Google](04-entrar-con-google.md) |
| 05 | [Entrar con Microsoft](05-entrar-con-microsoft.md) |
| 06 | La sesión se queda |
| 07 | La primera vez |

Las lecciones sin enlace están en preparación y se publican en orden.
