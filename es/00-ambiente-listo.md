# Lección 00 — Ambiente listo

**Antes de leer, siete palabras.** Una línea cada una; las demás de la serie llegan en la lección donde se usan.

- **Android Studio:** el programa donde escribes, compilas y corres la app.
- **SDK** (kit de desarrollo): las herramientas y librerías para compilar y correr apps de Android. Android Studio lo
  descarga, pero vive en su propia carpeta.
- **Emulador:** un teléfono Android que corre como programa dentro de tu computadora.
- **AVD** (dispositivo virtual): la ficha de *un* teléfono simulado — modelo, versión de Android, memoria. El
  emulador arranca un AVD.
- **Virtualización y aceleración:** la capacidad de tu procesador de correr otra máquina adentro; con ella el
  emulador va rápido, sin ella se arrastra.
- **Terminal:** la ventana donde escribes comandos.
- **`PATH`:** la lista de carpetas donde la terminal busca los comandos; si una herramienta no está ahí, tienes que
  escribir su ruta completa.

**Qué te llevas:** Android Studio, el SDK y un emulador funcionando en tu máquina, con las versiones exactas de la
serie; el emulador con tu teclado y tu cuenta de Google; y el proyecto de la tienda corriendo dentro de él.

**Idea nueva de esta lección (la única que se evalúa):** el ambiente de Android tiene tres piezas — Android Studio, el
SDK y el emulador — y cada una se comprueba con una evidencia concreta (un comando o una pantalla), no a ojo.

---

## La escena

Son las once de la noche y la app de la tienda tiene que estar corriendo mañana a las nueve. Abres Android Studio por
primera vez. Pide descargar «componentes». Le das que sí. Veinte minutos después aparece un teléfono dibujado en la
pantalla, con la hora y la batería llena. Tecleas tu correo para entrar con Google… y no pasa nada. Ni una letra.

Pruebas el teclado de la computadora en otra ventana: funciona. Vuelves al teléfono: nada. Al final escribes tu correo
letra por letra con el mouse, sobre el teclado en pantalla. Entonces Google te pide escanear un código QR con tu
celular. Lo escaneas. Gira, gira y dice que no se pudo.

Nada de eso es un error tuyo. Las dos cosas le pasaron a quien probó esta lección antes que tú, el 13 de noviembre de
2026, en una Mac nueva. Las dos tienen una causa concreta y un arreglo de una línea. Esta lección te las dice **antes**
de que te pasen, junto con los comandos que te dicen, en cada paso, si vas bien.

---

## Antes de empezar

**Qué necesitas:**

| | Sirve | No sirve |
|---|---|---|
| Sistema | Ubuntu 26.04 en un procesador **x86_64**, o una Mac | Windows (la serie no lo cubre) · Linux en procesador ARM: Android Studio no tiene soporte oficial ahí |
| Memoria | 16 GB de RAM para Android Studio con el emulador | Con 8 GB, usa tu teléfono en lugar del emulador (paso 3) |
| Disco | [pendiente: medir el espacio que ocupa todo lo de esta lección] | |
| Red | Para las descargas de esta lección y de la primera compilación | |
| Saber | Abrir una terminal y usar git | No se supone Kotlin ni Android |

**Las versiones de la serie.** Se probaron todas juntas en una fecha. No son «la más nueva»: son las que sabemos que
funcionan entre sí. Si instalas otras, los resultados de las lecciones pueden no coincidir con los tuyos.

| Pieza | Versión | Dónde la ves |
|---|---|---|
| Android Studio | Rabbit 2, 2026.2.2 Patch 1 | Android Studio › About |
| Android Gradle Plugin (AGP) | 9.4.1 | `gradle/libs.versions.toml` del proyecto |
| Gradle | 9.6.0 | `./gradlew -version` |
| Kotlin | 2.4.21 | `gradle/libs.versions.toml` |
| Java del proyecto | 25 | `jvmToolchain(25)` en `app/build.gradle.kts` |
| Plataforma de Android | 37 (`platforms;android-37.0`) | `sdkmanager --list_installed` |
| Emulador | 37.2.13 | `emulator -version` |

*Probadas el 13-nov-2026.*

---

## Una palabra más: nombre de paquete

**Nombre de paquete:** el identificador único de tu app, como `mx.habil.ejemplo.tienda`. No se cambia después sin
romper la conexión con Firebase. Lo vas a buscar en el paso 5.

---

## Paso 1 · ¿Tu máquina sirve? (antes de instalar nada)

**Predice primero.** Antes de correr lo de abajo, escribe en una línea: ¿crees que tu procesador tiene la
virtualización encendida? ¿Cómo lo sabrías?

**En Ubuntu** [pendiente: medir en Ubuntu]:

```bash
uname -m                                  # debe decir x86_64
lscpu | grep -i virtualiz                 # debe decir VT-x (Intel) o AMD-V (AMD)
ls -l /dev/kvm                            # debe existir
groups                                    # tu usuario debe aparecer en «kvm»
```

Si `/dev/kvm` no existe, todavía no puedes usar KVM. Las causas posibles son tres: la virtualización está apagada
en el BIOS/UEFI, estás dentro de otra máquina virtual (VirtualBox, VMware, Docker: ahí el emulador acelerado no
corre) o KVM no está instalado. La prueba que decide es `emulator -accel-check`, en el paso 3.

Si tu usuario no está en `kvm`: este paso pide la contraseña de administración y cambia los permisos de tu usuario.
**Hazlo solo en tu propia computadora** o si la persona que administra el equipo te lo indica; en un laboratorio, no
uses `sudo`: pide que te agreguen al grupo.

```bash
sudo gpasswd -a "$USER" kvm               # te agrega al grupo
```

y **cierra la sesión y vuelve a entrar**: el grupo nuevo no aplica a la sesión abierta. Compruébalo con `id -nG`.
Medido en Linux Mint el 13-nov: sin el grupo, el emulador arranca sin aceleración.

**En Mac:**

```bash
uname -m                                  # arm64 en Apple silicon; x86_64 en Intel
```

El instalador de Android Studio tiene que ser el de tu procesador. En Mac la aceleración la da Hypervisor.Framework,
que viene con macOS: no hay que encender nada.

**Lo demuestra:** el `verifica` de la precomprobación sale en verde o te dice qué falta (objetivo 0.1).
[pendiente: `verifica` de la 00 en el repo de la serie]

---

## Paso 2 · Android Studio y el SDK

**Descarga Android Studio** de developer.android.com/studio, la versión de la tabla.

- **Mac:** abre el `.dmg` y arrastra Android Studio a Aplicaciones. Si usas Homebrew, `brew install --cask
  android-studio` hace lo mismo (así se instaló en la prueba del 13-nov).
- **Ubuntu** [pendiente: medir en Ubuntu]: descomprime el `.tar.gz` y corre `bin/studio.sh`.

Al abrirlo la primera vez, el asistente ofrece descargar el SDK. Acepta las licencias y deja la carpeta que propone:

| Sistema | Carpeta del SDK |
|---|---|
| Mac | `~/Library/Android/sdk` |
| Ubuntu | `~/Android/Sdk` [pendiente: medir en Ubuntu] |

**Ahora la terminal.** Agrega estas líneas al final de tu `~/.zshrc` (Mac) o `~/.bashrc` (Ubuntu), cambiando la
carpeta por la tuya, y abre una terminal nueva:

```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"
```

Si `sdkmanager` no existe, instala las **Command-line Tools** desde Android Studio › Settings › Languages &
Frameworks › Android SDK › SDK Tools. Luego las piezas de la serie:

```bash
yes | sdkmanager --licenses > /dev/null
sdkmanager "platform-tools" "emulator" "platforms;android-37.0"
sdkmanager --list_installed
```

> **Ojo con el nombre:** el paquete es `platforms;android-37.0`, no `android-37`. Con el nombre corto,
> `sdkmanager` no encuentra nada. Y en la versión de 2026, `sdkmanager` avisa que está en desuso y que lo reemplaza
> `android sdk`; sigue funcionando y la serie lo usa porque es el que documenta developer.android.com.

**Lo demuestra** (objetivo 0.2), con cuatro evidencias que guardas:

1. Android Studio › About: Rabbit 2, 2026.2.2 Patch 1.
2. `sdkmanager --list_installed`: `platform-tools`, `emulator` y `platforms;android-37.0`.
3. `emulator -version`: 37.2.13.
4. En el proyecto (paso 5): `./gradlew -version` dice Gradle 9.6.0, y la tabla de arriba te dice en qué archivo ves
   AGP, Kotlin y `jvmToolchain(25)`.

El `verifica` de la 00 comprueba lo que se puede automatizar; el About de Studio lo ves tú.

---

## Paso 3 · El emulador, con aceleración

**Predice primero.** De las tres piezas, ¿cuál crees que tardará más en quedar lista, y qué salida te lo probaría?

**Descarga una imagen de Android con Google.** Para entrar con Google en la lección 04 necesitas una imagen que traiga
Google Play services. La serie fija `google_apis_playstore`, que además trae Play Store; una imagen marcada
**Google APIs** también trae Play services. Lo que no sirve es una imagen `default` (Android sin servicios de Google).

```bash
# Mac con Apple silicon:
sdkmanager "system-images;android-37.0;google_apis_playstore;arm64-v8a"
# Ubuntu x86_64 [pendiente: medir en Ubuntu]:
sdkmanager "system-images;android-37.0;google_apis_playstore;x86_64"
```

**Crea el dispositivo** (cambia `arm64-v8a` por `x86_64` en Ubuntu):

```bash
echo no | avdmanager create avd -n tienda37 \
  -k "system-images;android-37.0;google_apis_playstore;arm64-v8a" -d pixel_9
```

**Confirma la aceleración antes de arrancarlo:**

```bash
emulator -accel-check
```

En la Mac de la prueba respondió `0`, `Hypervisor.Framework OS X Version 27.0` y `accel`. El `0` del principio es
«todo bien». En Ubuntu debe hablar de KVM [pendiente: medir en Ubuntu].

**Arráncalo:**

```bash
emulator -avd tienda37
```

El primer arranque tarda. En la Mac de la prueba (Apple silicon, 32 GB) fueron 57 segundos; uno posterior, 24. Tu
máquina tendrá su propio número. Cuando veas la pantalla de inicio, en otra terminal:

```bash
adb devices
```

Debe aparecer una fila cuyo nombre empiece con `emulator-` y cuyo estado sea `device`. El número cambia según los
emuladores que tengas abiertos (el primero suele ser `emulator-5554`).

**¿Tu máquina no puede?** Si no tienes aceleración o tienes 8 GB, usa tu teléfono Android. Ajustes › Acerca del
teléfono › toca siete veces «Número de compilación»; luego Ajustes › Sistema › Opciones de desarrollador › Depuración
por USB. Conéctalo, acepta el aviso en el teléfono y corre `adb devices`: debe aparecer con su número de serie y la
palabra `device`. Ese aviso autoriza la llave de **esa** computadora a depurar tu teléfono hasta que la revoques:
acéptalo solo en tu propia computadora. Si usaste una prestada, al terminar ve a Opciones de desarrollador ›
**Revocar autorizaciones de depuración USB**.

**Lo demuestra:** ejercicio 0.3 (abajo).

---

## Paso 4 · La primera vez: tu teclado y tu cuenta

Aquí pasan las dos cosas de la escena.

### El teclado que no escribe

Un dispositivo creado desde la terminal trae el teclado de la computadora **apagado**: la línea `hw.keyboard=no` en
su archivo `config.ini`. Se arregla con el emulador **cerrado**:

1. Encuentra el archivo: en Android Studio, Device Manager › los tres puntos de `tienda37` › **Show on Disk**. Ahí
   está `config.ini`. (Casi siempre es `~/.android/avd/tienda37.avd/`, pero cambia si definiste `ANDROID_AVD_HOME`.)
2. Cambia `hw.keyboard=no` por `hw.keyboard=yes`. Si la línea no existe, agrégala al final.
3. Comprueba antes de arrancar:

```bash
grep '^hw.keyboard=' RUTA/config.ini      # debe decir hw.keyboard=yes; si no aparece, no arranques todavía
```

Atajo para la ruta de siempre (Mac y Ubuntu):

```bash
sed -i.bak 's/^hw.keyboard=no$/hw.keyboard=yes/' ~/.android/avd/tienda37.avd/config.ini
```

Vuelve a arrancar el emulador. Ahora tu teclado escribe.

### Cuenta y equipo de confianza

El emulador guarda lo que haces en él —apps, ajustes y la sesión de tu cuenta— en la computadora. Por eso:

- **Inicia sesión con tu cuenta solo en tu computadora personal** o en un equipo autorizado.
- **En laboratorio o equipo prestado,** usa tu teléfono propio (paso 3) o una cuenta de prueba sin datos personales;
  no escribas ahí la contraseña de tu cuenta principal.
- Si iniciaste sesión por necesidad en un equipo ajeno, al terminar saca tu cuenta y borra los datos del emulador:
  Device Manager › los tres puntos del dispositivo › **Wipe Data**.

### Tu cuenta de Google

En el emulador: Ajustes › Google › agrega tu cuenta. Si tu cuenta usa **llave de acceso** (passkey), Google te
muestra un código QR para escanear con tu teléfono. Ese paso conecta la computadora y el teléfono por Bluetooth, y el
emulador no lo trae: en la prueba de esta lección el QR no completó. Si te pasa, toca **«Try another way»** y entra
con tu contraseña o con el aviso que te llega al teléfono.

**Lo demuestra:** ejercicio 0.4 (abajo).

---

## Paso 5 · El proyecto de la tienda corre

El repo de la serie es público en GitHub: https://github.com/HabilMX/tienda-app-nativa

**Predice primero.** ¿Cuánto crees que tarde la primera compilación, y por qué la segunda sería más rápida?

```bash
git clone https://github.com/HabilMX/tienda-app-nativa.git
cd tienda-app-nativa/lecciones/00-ambiente-listo/inicio
./gradlew -version          # Gradle 9.6.0 y la versión de Java
./gradlew installDebug      # compila y la INSTALA en el emulador o el teléfono; no la abre
```

Ahora **ábrela** desde el cajón de apps del emulador o del teléfono. **Vas bien** si aparece la pantalla «La tienda
corre en tu ambiente».

La primera compilación descarga librerías y tarda varios minutos. El proyecto de inicio ya trae configuración que se
explica en la L01: aquí no la cambies; primero comprueba que puedes instalar y abrir la app.

### Con Claude Code, en este orden

La serie usa Claude Code en todas las lecciones, siempre igual: **predices → le pides → lees lo que generó → corres
el `verifica` → lo explicas con tus palabras.** Si todavía no lo puedes explicar, detente, vuelve a comprobarlo y
pide una aclaración antes de seguir.

1. **Predice** sin abrir los archivos: ¿en qué archivo crees que se decide el nombre de paquete de la app?
2. **Pídele** a Claude Code: «Explícame la estructura de este proyecto: qué hace cada archivo de Gradle y dónde se
   decide el nombre de paquete».
3. **Lee** su respuesta y elige **una** afirmación concreta (por ejemplo, «el `applicationId` está en
   `app/build.gradle.kts`»).
4. **Compruébala tú** en los archivos: ábrelo y anota el archivo y la línea donde está. Si la afirmación era falsa,
   anótalo también: eso vale igual.
5. **Corre el `verifica`** y **explica** en dos líneas qué comprobaste.

**Lo demuestra:** ejercicio 0.5 (abajo).

---

## Tus ejercicios

Los ejercicios 0.3 y 0.4 se hacen **sin IA**: son comandos y pantallas que tienes que ver tú.

| # | Haces | Está hecho cuando |
|---|---|---|
| 0.1 | Corres la precomprobación del paso 1 | El `verifica` sale en verde, o tienes escrito qué falta y cómo lo arreglaste |
| 0.2 | Instalas Studio y el SDK del paso 2 | Tienes las cuatro evidencias del paso 2 y coinciden con la tabla de versiones |
| 0.3 | Arrancas el emulador con aceleración, o conectas tu teléfono | Tienes la salida de `emulator -accel-check` con «accel» (o tu teléfono en `adb devices` como `device`) |
| 0.4 | Enciendes el teclado y agregas tu cuenta | Escribes con tu teclado en el emulador y tu cuenta aparece en Ajustes › Google |
| 0.5 | Corres el proyecto y compruebas una afirmación de Claude Code | Abriste la app y ves «La tienda corre en tu ambiente», y tienes: la afirmación, el archivo y la línea donde la comprobaste, y si era cierta |

**Tiempo:** la primera vez, contando descargas, calcula una tarde. [pendiente: medir el tiempo total en Ubuntu]

---

## ¿No te corre?

| Ves | Causa probable | Qué haces |
|---|---|---|
| `sdkmanager: command not found` | Faltan las Command-line Tools o el `PATH` | Instálalas (paso 2) y abre una terminal nueva |
| `sdkmanager` no encuentra `platforms;android-37` | El nombre está incompleto | Es `platforms;android-37.0` |
| `-accel-check` no dice «accel» (Ubuntu) | Virtualización apagada, estás dentro de otra máquina virtual, KVM sin instalar o tu usuario fuera de `kvm` | Paso 1: BIOS/UEFI, fuera de la VM, o el grupo `kvm` (con permiso), y vuelve a entrar a la sesión |
| El emulador tarda minutos y se arrastra | Sin aceleración | Igual que la fila anterior, o usa tu teléfono |
| Tu teclado no escribe en el emulador | `hw.keyboard=no` | Paso 4, con el emulador cerrado |
| El QR de la llave de acceso no termina | El emulador no tiene Bluetooth hacia tu teléfono | «Try another way» |
| `avdmanager` no reconoce `pixel_9` | Ese perfil no está en tu versión | Corre `avdmanager list device -c` y usa un nombre de la lista |
| `adb devices` no muestra tu teléfono | Depuración USB apagada o aviso sin aceptar | Revisa el aviso en la pantalla del teléfono |
| `adb devices` dice `unauthorized` | No aceptaste la llave de la computadora | Desbloquea el teléfono, acepta el aviso y repite |
| Ubuntu no detecta el teléfono por USB [pendiente: medir en Ubuntu] | Permisos del sistema (`plugdev`, reglas udev) | En Android Studio, Tools › Troubleshoot Device Connections; si pide permisos, hazlo con quien administra el equipo |
| `installDebug` terminó bien pero no ves la app | `installDebug` instala, no abre | Ábrela desde el cajón de apps (paso 5) |
| «Java heap space» al compilar | Memoria de Gradle | Revisa que `gradle.properties` traiga `-Xmx4g` |

---

## Para cerrar

**Contesta sin volver a leer:**

1. ¿Qué tres piezas forman el ambiente de Android, y con qué evidencia compruebas cada una?
2. ¿Por qué el emulador necesita una imagen con Google y no cualquiera?
3. Creaste el dispositivo desde la terminal y tu teclado no escribe. ¿Qué cambias y dónde?
4. ¿Qué haces con tu cuenta de Google si la máquina no es tuya?

**Lo que sigue:** ya tienes dónde correr la app. En la L01 escribes la primera pantalla de la tienda en Compose, y el
`verifica` comprueba lo que se ve **sin** abrir el emulador.

---

## Compara tus respuestas

1. Android Studio (la pantalla About), el SDK (`sdkmanager --list_installed`) y el emulador (`emulator -accel-check`
   y `adb devices`).
2. Porque entrar con Google necesita Google Play services. Lo traen las imágenes `google_apis_playstore` (la de la
   serie) y las marcadas Google APIs; una imagen `default` no.
3. `hw.keyboard=no` por `hw.keyboard=yes` en el `config.ini` del dispositivo (lo encuentras con Show on Disk), con el
   emulador cerrado, y lo vuelves a arrancar.
4. No la uses ahí con tu cuenta principal: tu teléfono o una cuenta de prueba. Si ya entraste, la sacas y borras los
   datos del emulador con Wipe Data.

---

## Fuentes de la lección

Abiertas el 13-nov-2026.

- Android Developers. *Install Android Studio* (requisitos; Linux ARM sin soporte oficial). https://developer.android.com/studio/install
- Android Developers. *Start the emulator from the command line* (`-accel-check`, KVM y Hypervisor.Framework). https://developer.android.com/studio/run/emulator-commandline
- Android Developers. *Configure hardware acceleration* (no corre dentro de otra VM; permisos de KVM). https://developer.android.com/studio/run/emulator-acceleration
- Android Developers. *Create and manage virtual devices* (imágenes con Google APIs y Play; datos del AVD; Show on Disk; Wipe Data). https://developer.android.com/studio/run/managing-avds
- Android Developers. *sdkmanager* y *avdmanager* (listar paquetes y perfiles). https://developer.android.com/tools/sdkmanager · https://developer.android.com/tools/avdmanager
- Android Developers. *Environment variables* (`ANDROID_AVD_HOME`, `PATH`). https://developer.android.com/tools/variables
- Android Developers. *Run apps on a hardware device* (llave RSA, revocar autorizaciones, `plugdev`, Troubleshoot Device Connections). https://developer.android.com/studio/run/device
- Android Developers. *Android Debug Bridge* (serial `emulator-<puerto>`). https://developer.android.com/tools/adb
- Android Developers. *Build your app from the command line* (`installDebug` compila e instala). https://developer.android.com/build/building-cmdline
- Android Developers. *Android Gradle plugin release notes* (AGP 9.4 pide Gradle 9.6.0) y *Java versions in Android builds* (toolchain). https://developer.android.com/build/releases/about-agp · https://developer.android.com/build/jdks
- Android Developers. *Authenticate across form factors* (el emulador no sirve para el flujo de llave de acceso entre dispositivos). https://developer.android.com/identity/form-factors
- Medición propia: ensayo en Mac con Apple silicon y en Linux Mint x86_64, 13-nov-2026.
