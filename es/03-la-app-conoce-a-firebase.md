# Lección 03 — La app conoce a Firebase

**Antes de leer, cuatro palabras.** Una línea cada una; las demás llegan en el paso donde se usan.

- **Firebase:** un servicio de Google que le resuelve a tu app cosas que tú no vas a programar, como guardar
  quién entró. En esta serie usas una sola parte, Authentication.
- **Certificado de depuración:** el documento con el que Android firma la app que compilas en tu computadora.
  Se crea solo, y cada computadora tiene el suyo.
- **Huella (SHA-1):** un resumen corto de ese certificado. Sirve para reconocerlo sin enseñar nada privado.
- **`google-services.json`:** el archivo de configuración que descargas de Firebase, con los datos de tu
  proyecto y de tu app.

**Qué te llevas:** tu app de la tienda conectada a **tu** proyecto de Firebase, con un archivo de configuración
que trae todo lo que la lección 04 necesita, y saber comprobarlo sin abrir la consola.

**Idea nueva de esta lección:** una app se conecta a un servicio externo con dos señas, su nombre de paquete y la
huella de su certificado. El servicio no conoce tu código: conoce esas dos señas. Lo demás —el plugin de Gradle y
la librería— viene dado y explicado, y no se evalúa aquí.

---

## La escena

Registras tu app en Firebase. El asistente te ofrece un botón: descargar `google-services.json`. Lo descargas,
lo pones en su carpeta y compilas.

Compila. Todo en verde.

Una lección después pones el botón «Entrar con Google», lo tocas y no entras. El mensaje no dice qué falta.

Eso pasó en el ensayo de esta serie, el 13 de noviembre de 2026, en una Mac: el archivo bajado antes de registrar
la huella y de habilitar Google no traía dos bloques que el botón necesita. Es lo que se vio ese día, no una regla
que Firebase publique. La regla estable sí está en su documentación: después de registrar la huella y de habilitar
Google, se descarga el archivo **actualizado**, porque ese es el que trae lo necesario para entrar con Google.

Y la parte de «compila en verde» está medida aquí, con un archivo inventado al que le faltan esos bloques (18 de
noviembre de 2026): `./gradlew assembleDebug` termina bien, y el `verifica` de esta lección da 15 de 18, con tres
marcas en rojo que dicen «descárgalo otra vez».

El archivo es una copia de lo que la consola sabía de tu app **en el momento en que lo bajaste**. Por eso el
orden importa. Primero conoces las dos señas de tu app, luego sigues el orden que funciona, y al final lo rompes
tú para leer el error.

---

## Antes de empezar

**Qué necesitas:** la [02](02-pantallas-que-cambian.md) terminada, el ambiente de la
[00](00-ambiente-listo.md) (`ANDROID_HOME` definido) y una cuenta de Google propia. Vas a entrar con ella a la
consola de Firebase desde tu navegador: aplica lo de «Cuenta y equipo de confianza» de la 00.

**Lo que es tuyo.** El proyecto de Firebase lo creas tú, con tu cuenta, y nadie más lo usa. En el ensayo del
13-nov-2026 se creó en el plan sin costo, sin registrar tarjeta.

**El proyecto de inicio.** Desde la raíz del repo de la serie:

```bash
git pull
cd lecciones/03-la-app-conoce-a-firebase/inicio
```

Ahí trabajas. Junto está `solucion/`: puedes leerla y compararla sin configurar nada. **`./verifica solucion` sale
en rojo en un clon limpio**, y es lo esperado: tu archivo no se sube, y con el plugin aplicado el proyecto no
compila sin él (medido el 18-nov-2026: 0 pruebas corridas). Si quieres revisarla, copia tu archivo a
`solucion/app/` —ahí también queda ignorado— y retíralo al terminar. Tus predicciones y explicaciones van en
`respuestas/03.md`, en la raíz del repo (`mkdir -p respuestas` si no existe).

**Lo que el proyecto ya trae (andamiaje).** No se evalúa aquí:

| Qué trae | Dónde | Por qué |
|---|---|---|
| Las tres pantallas de la 02 y sus siete pruebas | `app/src/main` y `app/src/test` | Partes de lo que ya escribiste. Conectar Firebase no debe cambiarlas |
| Las pruebas de la configuración y su revisor | `app/src/test/…/ConfiguracionTest.kt` y `RevisorDeConfiguracion.kt` | Son las que corre el `verifica`. No las cambies |
| Tres configuraciones inventadas | `app/src/test/resources/configuraciones/` | Con ellas las pruebas demuestran que el revisor sí distingue un archivo completo de uno incompleto |
| Un molde del archivo, sin datos de nadie | `app/google-services.json.example` | Para que sepas cómo se ve uno completo antes de tener el tuyo |

**Lo que no hace esta lección.** Todavía no hay botón de Google ni se entra con nadie: eso es la 04. Aquí la app
queda presentada con Firebase, y lo compruebas leyendo tu archivo.

---

## Paso 1 · Sin IA: las dos señas de tu app

Este paso es tuyo, sin Claude Code. Firebase te va a pedir dos datos. Los dos ya existen en tu computadora.

**La primera seña: el nombre de paquete.** Predice dónde está escrito y luego búscalo:

```bash
grep -n "applicationId" app/build.gradle.kts
```

Debe contestar una línea con `applicationId = "mx.habil.ejemplo.tienda"`. Según la documentación de Android, ese
identificador distingue a tu app en el teléfono y en la tienda de apps, y lo que varias herramientas llaman
«nombre de paquete» es en realidad ese valor.

**La segunda seña: la huella.** Android no instala una app sin firma. La que compilas para probar se firma sola,
con un certificado de depuración que las herramientas crean en tu carpeta personal. Pídele a Gradle el reporte:

```bash
./gradlew signingReport
```

Esto dio el 18-nov-2026 (aquí con la huella tapada; la tuya trae 20 pares):

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

Lee tres renglones:

- **`Variant: debug`** es la app de pruebas, la que instalas con `installDebug`. Su renglón **`SHA1`** es la
  huella que vas a registrar. Cópiala a `respuestas/03.md`.
- **`Store`** dice de qué archivo salió: el almacén de depuración de **esta** computadora.
- **`Variant: release`** dice `Config: none`: este proyecto todavía no tiene con qué firmar una versión para
  publicar. Es el tema del ejercicio 3.3.

**Predice.** Tu compañera corre el mismo comando, con el mismo proyecto, en su computadora. ¿Le sale la misma
huella? Escribe sí o no, y por qué, antes de seguir.

**La huella se puede enseñar; la clave no.** La documentación de Android lo separa así: el certificado y su
huella están hechos para compartirse con los servicios que los piden; la clave de firma y sus contraseñas se
guardan en secreto. El almacén de depuración solo sirve para desarrollar: la misma documentación dice que es
inseguro a propósito, y que por eso las tiendas no aceptan una app firmada con él.

**Lo demuestra:** tu huella de depuración copiada y tu predicción escrita (base de los objetivos 3.1 y 3.3).

---

## Paso 2 · Sin IA: lo que no se sube

También sin Claude Code, y **antes** de tener el archivo: así no existe ni un minuto en que git pueda llevárselo.

Crea el archivo `.gitignore` en la carpeta `inicio/`, con estas dos líneas:

```text
# Tu configuración de Firebase: cada quien tiene su propio proyecto.
google-services.json
```

Compruébalo. Este comando le pregunta a git si ignoraría el archivo y por cuál regla (solo mira):

```bash
git check-ignore -v app/google-services.json
```

Debe aparecer una línea con tu archivo `.gitignore`, el renglón 2, la regla `google-services.json` y la ruta
`app/google-services.json`. El comienzo de la línea cambia según desde dónde corras git; parado en `inicio/`, el
18-nov-2026 fue así:

```text
lecciones/03-la-app-conoce-a-firebase/inicio/.gitignore:2:google-services.json	app/google-services.json
```

El repo de la serie ya traía la misma regla en su raíz; git te reporta la más cercana al archivo, que es la tuya.
La tuya es la que protege al proyecto el día que viva en su propio repo.

**Por qué no se sube, dicho con cuidado.** No es porque sea un secreto. La documentación de Firebase dice que ese
archivo trae identificadores únicos pero no secretos, y que sus claves de API solo identifican a tu proyecto: lo
que protege los datos son las reglas de seguridad, los permisos y App Check. En esta serie se queda fuera por
otra razón: **cada quien tiene su propio proyecto**. Si subieras el tuyo, la app de tu compañera hablaría con tu
Firebase, y con una huella que no es la suya.

**Lo que sí nunca se sube**, en ningún repo:

- Las claves de firma (archivos `.jks` o `.keystore`) y sus contraseñas.
- Contraseñas, de lo que sea.
- La clave secreta de Microsoft que vas a crear en la lección 05.

**Lo demuestra:** tu explicación en `respuestas/03.md` (objetivo 3.2). El `verifica` solo comprueba que la línea
está en tu `.gitignore`, que el molde `.example` existe y que git no rastrea ningún `google-services.json`.

---

## Paso 3 · En la consola: el orden que funciona

Dos palabras más, que ahora sí vas a ver escritas:

- **OAuth:** el acuerdo con el que una app le pide a Google que confirme quién eres, sin ver tu contraseña. Lo
  usas en la lección 04; hoy solo preparas sus datos.
- **Cliente de OAuth:** la ficha con la que Google reconoce a una app que le va a pedir eso.

Abre `app/google-services.json.example`. Así se ve un archivo completo. Localiza dos bloques dentro de
`oauth_client`: el que dice `"client_type": 1`, con `certificate_hash`, y el que dice `"client_type": 3`. El
primero es el cliente de Android, con tu huella; el segundo, el cliente web.

**Predice primero.** Si descargaras el archivo justo después de registrar la app, antes de dar la huella y de
habilitar Google, ¿cuáles de esos bloques esperarías encontrar? Escríbelo.

Ahora sigue este orden en la [consola de Firebase](https://console.firebase.google.com). El orden es el del ensayo
del 13-nov-2026. Los nombres de menús y botones van como los escribe la documentación de Firebase al 18-nov-2026,
en inglés; la consola puede mostrarlos traducidos o haberlos movido.

1. **Crea el proyecto.** Al crearlo, la consola ofrece dos cosas que la documentación marca como opcionales: su
   asistencia con IA y Google Analytics. Apaga las dos: esta serie no las usa.
2. **Registra la app de Android.** En «Android package name» va `mx.habil.ejemplo.tienda`, copiado tal cual. La
   documentación avisa que distingue mayúsculas y que **no se puede cambiar** después de registrarlo: con otro
   nombre hay que registrar otra app. En el ensayo, el asistente no pidió la huella y ofreció descargar el
   archivo. **Todavía no lo descargues.**
3. **Agrega la huella.** En «Project settings», en la tarjeta «Your apps», elige tu app, toca «Add fingerprint»,
   pega el `SHA1` del paso 1 y guarda («Save»).
4. **Habilita Google.** La documentación lo ubica en «Security > Authentication», pestaña «Sign-in method»:
   habilita el proveedor Google y guarda. En el ensayo pidió un nombre público para el proyecto y un correo de
   soporte: usa el de tu cuenta.
5. **Hasta ahora, descarga `google-services.json`**, desde la misma tarjeta «Your apps», y ponlo en `inicio/app/`.
   La documentación pide dejar solo el archivo más reciente.

**Una sola ubicación.** El plugin también acepta un archivo por variante, por ejemplo en `app/src/debug/`. Esta
serie usa solo `app/google-services.json`, para que haya un único archivo que revisar, y el `verifica` no mira las
otras carpetas: con el archivo en otro lado sale en rojo aunque el proyecto compile (medido el 18-nov-2026: 12 de
18, con un aviso de dónde lo encontró).

**Compruébalo sin abrir la consola.** Desde la raíz del repo:

```bash
./lecciones/03-la-app-conoce-a-firebase/verifica
```

En este punto **vas bien** si las marcas de tu archivo están en verde y quedan en rojo solo las del paso 4. Con un
archivo inventado completo, esto dio el 18-nov-2026 (recortado):

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

Si en lugar de eso ves «descárgalo otra vez después de la huella», el archivo se bajó antes de tiempo: repite el
punto 5. No hace falta borrar nada en la consola.

**Qué compara la marca de la huella.** La prueba abre el almacén de depuración de tu computadora, calcula su
SHA-1 y lo busca en tu archivo. No le pregunta nada a Firebase.

**Lo demuestra:** las cuatro marcas «Simulado» de tu archivo dicen qué trae el archivo (objetivo 3.5). Que seguiste
el orden en tu propio proyecto no lo puede ver ninguna prueba local: eso se confirma al entrar, en la lección 04.

---

## Paso 4 · Con Claude Code: el plugin y la librería

Tu archivo ya está en su carpeta, y la app todavía no lo lee. Faltan dos piezas de andamiaje:

| Pieza | Qué hace | Cuándo trabaja |
|---|---|---|
| El plugin `google-services` | Lee tu `google-services.json` y lo convierte en recursos que la app podrá leer. Según su documentación, también puede agregar dependencias básicas de los servicios que tengas habilitados | Al compilar. No se ejecuta como código en el teléfono |
| El BoM de Firebase y `firebase-auth` | El BoM es una lista de versiones que funcionan juntas: fijas una sola y las librerías van sin versión. `firebase-auth` es la de Authentication, y es la que aporta funciones al correr | Dentro de la app |

Aquí entra la herramienta, y entra siempre igual: **predices → le pides → lees lo que generó → corres el
`verifica` → lo explicas con tus palabras.**

1. **Predice.** Escribe en `respuestas/03.md`: ¿qué archivos van a cambiar? ¿Alguno de ellos es un `.kt`?
2. **Pídele** a Claude Code, parado en `inicio/`: «En `gradle/libs.versions.toml` agrega el plugin
   `com.google.gms.google-services` en la versión 4.6.0 y el BoM de Firebase `com.google.firebase:firebase-bom`
   en la 35.2.0, con la librería `firebase-auth` sin versión. Declara el plugin en el `build.gradle.kts` de la
   raíz con `apply false` y aplícalo en `app/build.gradle.kts`; ahí mismo agrega el BoM y `firebase-auth`. No
   cambies ningún archivo `.kt`, ni las pruebas, ni `app/google-services.json`, y no copies ningún valor de ese
   archivo a otro lado.»
3. **Lee** el cambio completo antes de aceptarlo. Deben ser tres archivos, con estas piezas:

```toml
# gradle/libs.versions.toml — una línea nueva en cada sección
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
// build.gradle.kts de la raíz — se declara, sin aplicarse
    alias(libs.plugins.google.services) apply false
```

```kotlin
// app/build.gradle.kts — aquí sí se aplica, y se agregan las dos dependencias
plugins {
    …
    alias(libs.plugins.google.services)
}
…
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
```

   Cinco cosas que revisar al leer:

   - **Las versiones son las de la serie:** 4.6.0 y 35.2.0, las que muestra la documentación de Firebase al
     18-nov-2026. Si aparece otra, pide que la corrija: la serie está probada con esas.
   - **Están las dos líneas, el BoM y `implementation(libs.firebase.auth)`.** El BoM solo elige versiones
     compatibles; la segunda línea es la que agrega Authentication. Con el BoM y otra librería de Firebase en su
     lugar, el `verifica` da 17 de 18 y dice «Falta Authentication» (medido el 18-nov-2026).
   - **`firebase-auth` no lleva versión.** Se la pone el BoM. Una versión suelta ahí deshace lo que el BoM
     resuelve.
   - **El guion del catálogo se vuelve punto:** `google-services` en el catálogo se escribe
     `libs.plugins.google.services` en el archivo de Gradle.
   - **Nadie tocó tu archivo ni copió sus valores.** `git status` no debe mostrar `google-services.json` (está
     ignorado), y ningún `.kt` debe aparecer cambiado.

4. **Corre el `verifica`.** Ahora la línea es
   `Resultado: 18 de 18 pruebas en verde (0 en rojo, 0 con error, 0 saltadas)`. Queda una marca en rojo, la de
   `respuestas/03.md`: se llena en el paso 5.
5. **Explica**, en tres líneas de `respuestas/03.md`: qué hace el plugin con tu archivo, y en qué momento.

**Míralo tú (opcional).** El plugin deja lo que generó en un `values.xml` debajo de `app/build/generated/res/`. La
carpeta exacta es un detalle interno y puede cambiar de una versión a otra, así que búscalo en lugar de
memorizarla. Parado en `inicio/`:

```bash
find app/build/generated/res -name values.xml | xargs grep -l google_app_id
```

Con las versiones de la serie contestó `app/build/generated/res/processDebugGoogleServices/values/values.xml`
(18-nov-2026). Ábrelo y busca `project_id`, `google_app_id` y `default_web_client_id`. Con el archivo inventado
completo traía seis nombres: `default_web_client_id`,
`google_api_key`, `google_app_id`, `google_crash_reporting_api_key`, `google_storage_bucket` y `project_id`. Con
el archivo sin clientes de OAuth, `default_web_client_id` **no aparece**. Ese es el dato que el botón de Google va
a pedir en la lección 04, y es la razón de la escena: sin cliente web en el archivo, la app compila sin él.

**Qué demuestra cada marca, y qué no.**

- **«Simulado», las de tu archivo:** leyeron el texto de `app/google-services.json`. Demuestran qué trae.
- **«Simulado», la de la app compilada:** leyó los recursos que generó el plugin, como los leería la app.
  Demuestra que tu archivo llegó a la app en esta compilación.
- **«Simulado», la de la librería:** la prueba le pidió a Firebase que arrancara con esos recursos y leyó con qué
  proyecto quedó. En la app instalada, según la documentación de Firebase, ese arranque lo hace la librería al
  abrir la app.
- **«Simulado», la de Authentication:** preguntó si la clase principal de `firebase-auth` viaja en la app. No la
  usó: no creó ninguna sesión.
- **«Texto» y «Git»:** buscaron palabras en tus archivos y le preguntaron a git qué rastrea.

Ninguna habló con Firebase. Un archivo bien formado de un proyecto que ya borraste pasa en verde, y uno armado a
mano con esa forma, también. Que el archivo viene de tu proyecto, que la huella está registrada y que Google
está habilitado se comprueba de la única forma posible: entrando, en la lección 04.

**En la app instalada.** `./gradlew installDebug` y ábrela: debe verse la bienvenida, igual que en la 02. En el
emulador de la prueba (Pixel 9, Android 37, 18-nov-2026), con la solución y un archivo inventado, la app abrió en
«La tienda» con su botón «Entrar», y el registro del sistema (`adb logcat`) mostró la línea
`FirebaseInitProvider: FirebaseApp initialization successful`. Ahí no se tocaron los botones: el recorrido lo
miden las pruebas simuladas.

**Si una línea todavía no te queda clara,** antes de seguir intenta explicarla con tus palabras. Si aún no sale,
vuelve a ese fragmento o pide una aclaración.

**Lo demuestra, y hasta dónde.** En el nivel simulado, el 18 de 18 demuestra que este archivo tiene la forma y los
datos que la lección espera, que llegaron a esta compilación y que Authentication viaja en la app. **No
demuestra** que el archivo venga de tu proyecto, que hayas seguido el orden ni que Google esté habilitado. Esa
parte del objetivo 3.1 se demuestra en el nivel real, al entrar con tu cuenta en la lección 04.

---

## Paso 5 · Sin IA: rompe uno a propósito

Este es el ejercicio 3.4 y se hace **sin Claude Code**. Vas a provocar el error de «la configuración es de otro
paquete» sin tocar tu archivo ni la consola: cambiando la seña del lado de la app.

**1. Predice.** En `app/build.gradle.kts` vas a cambiar `applicationId = "mx.habil.ejemplo.tienda"` por
`applicationId = "mx.habil.ejemplo.otra"`. Escribe: ¿compila?, ¿quién se queja, Kotlin, Gradle o Firebase?

**2. Rómpelo y mide.** Haz el cambio y compila:

```bash
./gradlew assembleDebug
```

Esto dio el 18-nov-2026 (la ruta, recortada):

```text
* What went wrong:
Execution failed for task ':app:processDebugGoogleServices' …
> No matching client found for package name 'mx.habil.ejemplo.otra' in …/app/google-services.json
```

Copia a `respuestas/03.md` la línea que empieza con `> No matching client found`.

**3. Diagnostica**, con tus palabras, antes de leer lo que sigue: ¿qué buscó, dónde lo buscó y por qué no lo
halló?

El nombre de la tarea lo dice: `processDebugGoogleServices` es el plugin, al compilar. Buscó en la lista
`client` de tu archivo una app cuyo `package_name` fuera el `applicationId` de esta, y no hay ninguna. No intervino
la red ni Firebase: el error salió de comparar dos textos en tu computadora.

**4. El arreglo que tienta, y por qué no.** Editar a mano el `package_name` dentro del archivo quita el error de
compilación. No lo hagas: el archivo es una copia de lo que hay en la consola, y la consola sigue teniendo
registrada la otra app. Lo que corresponde es que las dos señas coincidan de verdad: o la app usa el paquete
registrado, o registras otra app con el paquete nuevo y descargas **su** archivo.

**5. Arréglalo.** Regresa la línea a `applicationId = "mx.habil.ejemplo.tienda"`. Este comando debe mostrar solo
los cambios del paso 4 (solo mira):

```bash
git diff -- app/build.gradle.kts
```

Corre el `verifica`: 18 de 18, y ahora también la marca de `respuestas/03.md`.

Ese verde confirma dos cosas: que el proyecto quedó reparado y que pegaste la línea del error. No sabe si lo
rompiste, ni lee tu predicción, ni califica tu diagnóstico. El 3.4 se evalúa con tu explicación, y está terminado
cuando puedes marcar las tres:

- [ ] Tu predicción del punto 1 está escrita, con lo que viste debajo.
- [ ] Pegaste la línea del error.
- [ ] Dijiste quién se quejó, qué dos textos comparó y por qué editar el archivo a mano no es el arreglo.

**Lo demuestra:** «Rompe uno a propósito» (objetivo 3.4).

---

## Tus ejercicios

Los pasos 1 y 2, el 3.3 y el 3.4 se hacen **sin IA**.

| # | Haces | Está hecho cuando |
|---|---|---|
| 3.1 | Sigues el orden: proyecto → app → huella → Google → descarga | El `verifica` da 18 de 18 con tu archivo: eso valida el archivo y la compilación. Que el proyecto es tuyo, con la huella registrada y Google habilitado, lo valida la entrada real de la lección 04 |
| 3.2 | Explicas por qué `google-services.json` no se sube en esta serie y nombras lo que nunca se sube | Tu explicación está en `respuestas/03.md`. El `verifica` solo revisa el `.gitignore`, el `.example` y lo que git rastrea |
| 3.3 | Distingues la huella de depuración de la de una versión publicada | Contestaste los tres casos de abajo |
| 3.4 | Rompes el paquete y diagnosticas el error | Tu evidencia son las tres casillas del paso 5, escritas por ti. El `verifica` solo confirma que el proyecto quedó reparado y que pegaste el error |
| 3.5 | Compruebas, sin abrir la consola, que tu archivo trae el cliente con tu huella y el cliente web | Las marcas «Simulado» de tu archivo están en verde. La huella que comparan es la de depuración de esta computadora |

**Ejercicio 3.3 · ¿Cuál huella registras?** La que registraste hoy es la de depuración de tu computadora. Según la
documentación de Android y de Google, una app tiene más de una:

| Quién firma la app | De dónde sale su huella |
|---|---|
| Tu computadora, al compilar para probar | Del almacén de depuración: `./gradlew signingReport`, variante `debug` |
| Tú, con tu propia clave, para publicar | Del almacén de esa clave. No es la de depuración |
| Google Play, con Play App Signing | De la Play Console, en la página de firma de la app. La clave la guarda Google y no es la que tú usas para subir la app |

Contesta en `respuestas/03.md` cuál huella hay que registrar en Firebase en cada caso:

1. Tu compañera clona tu proyecto y quiere entrar con Google desde su computadora, contra tu Firebase.
2. Publicas la app en Google Play con Play App Signing y los usuarios la bajan de ahí.
3. Cambias de computadora y sigues desarrollando.

**Tiempo:** calcula unas tres horas, con la consola de Firebase y el «Rompe uno a propósito». Es un estimado: lo
ajustamos con lo que tarde la primera cohorte.

---

## ¿No te corre?

Los mensajes en inglés son los que dio el plugin `google-services` 4.6.0 el 18-nov-2026.

| Ves | Causa probable | Qué haces |
|---|---|---|
| `File google-services.json is missing.` | El plugin ya está aplicado y el archivo no está en `app/` | Ponlo en `inicio/app/`, con ese nombre exacto. Sobre `solucion/` es lo esperado en un clon limpio |
| ❌ «Falta Authentication» | Está el BoM y falta la librería | Agrega `implementation(libs.firebase.auth)` en `app/build.gradle.kts` |
| ℹ️ «Hay uno en …/app/src/debug/…» | El archivo está en una carpeta de variante | Muévelo a `app/`: esta serie usa una sola ubicación |
| `No matching client found for package name '…'` | El `applicationId` de la app y el paquete del archivo no coinciden | Paso 5. Si el archivo es de otro paquete, registra la app con el paquete de la serie y descarga su archivo |
| ❌ «no trae un cliente de OAuth de Android (tipo 1)» | Lo bajaste antes de registrar la huella o de habilitar Google | Descárgalo otra vez (paso 3, punto 5) |
| ❌ «Ningún cliente de Android de tu archivo trae la huella de depuración de esta computadora» | Registraste otra huella, o el archivo se bajó en otra computadora | Corre `./gradlew signingReport`, registra ese `SHA1` y descarga otra vez |
| ❌ «no trae un cliente de OAuth web (tipo 3)» | Lo bajaste antes de habilitar Google | Habilita Google y descarga otra vez |
| ❌ «Git sí rastrea un google-services.json» | Se agregó antes de existir el `.gitignore` | `git rm --cached <archivo>` lo saca de git sin borrar tu copia |
| `keytool -list -v` termina con `MissingFormatArgumentException` | Pasó en la Mac de la serie con Java 25 y el sistema en español (18-nov-2026) | Usa `./gradlew signingReport`, que es lo que pide la lección |
| No existe `~/.android/debug.keystore` y no tienes la huella | Todavía no compilas nada en esta computadora: el almacén lo crea la primera compilación de depuración, no `signingReport` (medido en Linux Mint 22.3, 18-nov-2026) | Corre `./gradlew assembleDebug` y luego `./gradlew signingReport` |

En Linux Mint 22.3 (x86_64), el `verifica` de esta lección salió igual que en la Mac (18-nov-2026): sobre `inicio/`, 11 de
18 en verde; sobre `solucion/` sin tu archivo, «0 pruebas corridas» con el aviso; y con un archivo de prueba que traía la
huella de esa computadora, 18 de 18. Las dependencias de Firebase le sumaron unos 200 MB a `~/.gradle`.

---

## Para cerrar

**Contesta sin volver a leer:**

1. ¿Cuáles son las dos señas con las que Firebase reconoce a tu app, y dónde está cada una en tu computadora?
2. ¿Por qué el archivo se descarga al final y no cuando el asistente lo ofrece?
3. Tu `verifica` da 18 de 18. Di una cosa que eso demuestra y una que no.
4. ¿Por qué `google-services.json` no se sube en esta serie? ¿Y qué cosa nunca se sube en ninguna?

**Lo que sigue:** en la L04 pones el botón y entras con Google. Ahí se comprueba, de verdad, todo lo que hoy
quedó escrito en tu archivo.

---

## Compara tus respuestas

**Paso 1.** No le sale la misma. El almacén de depuración lo crean las herramientas en la carpeta personal de
cada quien, así que cada computadora tiene su certificado y su huella. El proyecto es el mismo; la firma no.

**Paso 3.** En el ensayo del 13-nov-2026, ninguno de los dos: el archivo bajado antes de la huella y de
habilitar Google no traía el tipo 1 (Android, con `certificate_hash`) ni el tipo 3 (web). Es una observación de
ese día. Lo que la documentación de Firebase sí garantiza es lo contrario: el archivo **actualizado**, bajado
después de esos dos pasos, trae la información de OAuth que el botón necesita. Por eso se descarga al final.

**Paso 4.** Cambian tres archivos: `gradle/libs.versions.toml`, el `build.gradle.kts` de la raíz y
`app/build.gradle.kts`. Ningún `.kt`: conectar la app no tocó ninguna pantalla, y por eso las siete pruebas de la
02 siguen en verde. El plugin lee `app/google-services.json` al compilar y genera recursos de la app
(`project_id`, `google_app_id` y, si el archivo trae cliente web, `default_web_client_id`). El plugin no se
ejecuta en el teléfono: lo que trabaja al correr son las librerías que declaraste, como `firebase-auth`.

**Paso 5.** No compila. Se queja Gradle, en la tarea del plugin `google-services`; ni Kotlin ni Firebase. Comparó
el `applicationId` de la app con los `package_name` del archivo. Editar el archivo quita el error y deja la
consola con otra app registrada: las señas seguirían sin coincidir donde importa.

**Ejercicio 3.3.**

1. La de depuración **de su computadora**, agregada a tu app en Firebase: una app acepta varias huellas. Después
   ella necesita el archivo descargado de nuevo.
2. La del certificado con que Google Play firma la app, que se consulta en la Play Console. La de depuración y
   la de la clave con que subes la app no sirven para lo que bajan los usuarios.
3. La de depuración de la computadora nueva: es otro almacén y otra huella.

**Para cerrar:**

1. El nombre de paquete, en `applicationId` de `app/build.gradle.kts`, y la huella del certificado con que se
   firma la app; la de depuración sale del almacén de tu carpeta personal y la imprime `signingReport`.
2. Porque el archivo es una copia de lo que la consola sabe en ese momento, y Firebase pide descargar el
   actualizado después de la huella y de habilitar Google. En el ensayo, el anterior no traía los clientes de
   OAuth.
3. Demuestra que el texto de tu archivo trae el paquete, el cliente de Android con tu huella de depuración y el
   cliente web, que la app compilada lleva esos datos y que Authentication viaja en la app. No demuestra que el
   archivo venga de tu proyecto, que el proyecto exista ni que puedas entrar con Google.
4. Porque cada quien tiene su propio proyecto, no porque sea secreto. Nunca se suben las claves de firma, las
   contraseñas ni la clave secreta de Microsoft.

---

## Fuentes de la lección

Abiertas el 18-nov-2026.

- Firebase. *Add Firebase to your Android project* (pasos, el paquete no se puede cambiar, el plugin 4.6.0 y el BoM 35.2.0, «unique, but non-secret identifiers»). https://firebase.google.com/docs/android/setup
- Firebase. *Google services Gradle plugin and JSON config file* (qué recursos genera el plugin y de qué campo sale cada uno; los mensajes de error). https://firebase.google.com/docs/android/google-services-plugin-and-file
- Firebase. *Understand Firebase for Android* (qué es el BoM; el plugin trabaja al compilar). https://firebase.google.com/docs/android/learn-more
- Firebase. *Learn about and manage API keys for Firebase* (las claves identifican, no autorizan). https://firebase.google.com/docs/projects/api-keys
- Firebase. *Authenticate with Google on Android* (huella, habilitar Google y descargar el archivo actualizado). https://firebase.google.com/docs/auth/android/google-signin
- Firebase. *FirebaseInitProvider* («Initializes Firebase APIs at app startup time»). https://firebase.google.com/docs/reference/android/com/google/firebase/provider/FirebaseInitProvider
- Ayuda de Firebase. *Download Firebase config file or object* y *Add a SHA fingerprint*. https://support.google.com/firebase/answer/7015592 · https://support.google.com/firebase/answer/9137403
- Google for Developers. *Client authentication* (`./gradlew signingReport`, almacén de depuración, Play App Signing). https://developers.google.com/android/guides/client-auth
- Android Developers. *Sign your app* (certificado de depuración, claves, huellas para proveedores, Play App Signing). https://developer.android.com/studio/publish/app-signing
- Android Developers. *Configure the app module* (el `applicationId` y el «nombre de paquete»). https://developer.android.com/build/configure-app-module
- Android Developers. *Migrate your build to version catalogs* (`libs.versions.toml`, `alias`). https://developer.android.com/build/migrate-to-catalogs
- Repositorio Maven de Google: sus índices listan `google-services` 4.6.0 y `firebase-bom` 35.2.0. https://dl.google.com/dl/android/maven2/com/google/gms/google-services/maven-metadata.xml · https://dl.google.com/dl/android/maven2/com/google/firebase/firebase-bom/maven-metadata.xml
- Ensayo de la serie, 13-nov-2026, Mac con Apple silicon: el orden de los pasos de consola y lo que se vio ese día (el asistente no pidió la huella; habilitar Google pidió nombre público y correo de soporte; el archivo bajado antes de tiempo no traía clientes de OAuth; plan sin costo). Los nombres de menús y botones de esta lección no salen del ensayo: son los de la documentación citada arriba.
- Medición propia: Mac con Apple silicon, 18-nov-2026 (`signingReport`; el `verifica` sobre `inicio/` y `solucion/`; los archivos inventados: completo, sin clientes, de otro paquete y con otra huella; los recursos que genera el plugin; los mensajes de error; el `keytool`; la app instalada en un emulador nuevo, sin cuenta, que se borró al terminar). Las mediciones se hicieron con archivos inventados, no con un proyecto real de Firebase.

Revisión externa (18-nov-2026): CON CAMBIOS, 2 de prioridad alta, 6 media y 3 baja; aplicada.
