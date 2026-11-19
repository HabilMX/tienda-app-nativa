# Lesson 00 — Environment ready

**Before you read, seven words.** One line each; the rest of the series arrive in the lesson where they are used.

- **Android Studio:** the program where you write, build and run the app.
- **SDK** (software development kit): the tools and libraries for building and running Android apps. Android Studio
  downloads it, but it lives in its own folder.
- **Emulator:** an Android phone that runs as a program inside your computer.
- **AVD** (Android virtual device): the profile of *one* simulated phone — model, Android version, memory. The
  emulator starts an AVD.
- **Virtualization and acceleration:** your processor's ability to run another machine inside itself; with it the
  emulator is fast, without it it crawls.
- **Terminal:** the window where you type commands.
- **`PATH`:** the list of folders where the terminal looks for commands; if a tool is not in it, you have to type its
  full path.

**What you take away:** Android Studio, the SDK and an emulator working on your machine, with the exact versions of the
series; the emulator with your keyboard and your Google account; and the store project running inside it.

**This lesson's new idea (the only one that is assessed):** the Android environment has three pieces — Android Studio,
the SDK and the emulator — and each one is checked with concrete evidence (a command or a screen), not by eye.

**A note on the app's texts.** The course project ships its on-screen texts in Spanish, and the files in `lecciones/`
are not translated: the course's automatic checks (the `verifica`) look for those exact texts. So in code blocks,
terminal outputs and on-screen sketches the texts stay as the project has them, and in the prose the lesson quotes
them in English and, the first time, gives the original in parentheses: «The store runs in your environment» (in the
project, «La tienda corre en tu ambiente»).

---

## The scene

It is eleven at night and the store app has to be running tomorrow at nine. You open Android Studio for the first
time. It asks to download "components". You say yes. Twenty minutes later a drawn phone appears on the screen, with
the time and a full battery. You type your email to sign in with Google… and nothing happens. Not a single letter.

You try the computer's keyboard in another window: it works. You go back to the phone: nothing. In the end you type
your email letter by letter with the mouse, on the on-screen keyboard. Then Google asks you to scan a QR code with
your cell phone. You scan it. It spins and spins and says it could not be done.

None of that is your mistake. Both things happened to the person who tested this lesson before you, on November 13,
2026, on a new Mac. Both have a concrete cause and a one-line fix. This lesson tells you about them **before** they
happen to you, together with the commands that tell you, at each step, whether you are on track.

---

## Before you start

**What you need:**

| | Works | Does not work |
|---|---|---|
| System | Linux on an **x86_64** processor (the series was tested on Linux Mint 22.3), or a Mac | Windows (the series does not cover it) · Linux on an ARM processor: Android Studio has no official support there |
| Memory | 16 GB of RAM for Android Studio with the emulator | With 8 GB, use your phone instead of the emulator (step 3) |
| Disk | 20 GB free. On Mac it took about 12 GB: Android Studio 3.3, SDK 4.5, virtual device 3.1 and Gradle 1.1 (measured on Nov 13, 2026). On Linux Mint 22.3 it took about 10 GB: Android Studio 1.6, SDK 3.9 (with the emulator image inside), virtual device 1.9 and Gradle 2.4 (measured on Nov 18, 2026) | Less: the virtual device grows as you use it |
| Network | For this lesson's downloads and the first build | |
| Knowledge | Opening a terminal and using git | Kotlin and Android are not assumed |

**The series' versions.** They were all tested together on one date. They are not "the newest": they are the ones we
know work with each other. If you install others, the results of the lessons may not match yours.

| Piece | Version | Where you see it |
|---|---|---|
| Android Studio | Rabbit 2, 2026.2.2 Patch 1 | Android Studio › About |
| Android Gradle Plugin (AGP) | 9.4.1 | the project's `gradle/libs.versions.toml` |
| Gradle | 9.6.0 | `./gradlew -version` |
| Kotlin | 2.4.21 | `gradle/libs.versions.toml` |
| Project's Java | 25 | `jvmToolchain(25)` in `app/build.gradle.kts` |
| Android platform | 37 (`platforms;android-37.0`) | `sdkmanager --list_installed` |
| Emulator | 37.2.13 | `emulator -version` |

*Tested on Nov 13, 2026.*

---

## One more word: package name

**Package name:** your app's unique identifier, such as `mx.habil.ejemplo.tienda`. It cannot be changed later without
breaking the connection to Firebase. You will look for it in step 5.

---

## Step 1 · Is your machine up to it? (before installing anything)

**Predict first.** Before running what is below, write down in one line: do you think your processor has
virtualization turned on? How would you know?

**On Linux** (this is how it came out on Linux Mint 22.3 on Nov 18: `x86_64`, `VT-x`, `/dev/kvm` in the `kvm` group and your user in `kvm`):

```bash
uname -m                                  # must say x86_64
lscpu | grep -i virtualiz                 # must say VT-x (Intel) or AMD-V (AMD)
ls -l /dev/kvm                            # must exist
groups                                    # your user must appear in «kvm»
```

If `/dev/kvm` does not exist, you cannot use KVM yet. There are three possible causes: virtualization is turned off
in the BIOS/UEFI, you are inside another virtual machine (VirtualBox, VMware, Docker: the accelerated emulator does
not run there) or KVM is not installed. The test that decides is `emulator -accel-check`, in step 3.

If your user is not in `kvm`: this step asks for the administrator password and changes your user's permissions.
**Do it only on your own computer** or if the person who administers the machine tells you to; in a lab, do not use
`sudo`: ask to be added to the group.

```bash
sudo gpasswd -a "$USER" kvm               # adds you to the group
```

and **log out and log back in**: the new group does not apply to the open session. Check it with `id -nG`.
Measured on Linux Mint on Nov 13: without the group, the emulator starts without acceleration.

**On Mac:**

```bash
uname -m                                  # arm64 on Apple silicon; x86_64 on Intel
```

The Android Studio installer has to be the one for your processor. On Mac, acceleration is provided by
Hypervisor.Framework, which comes with macOS: there is nothing to turn on.

**What proves it:** the pre-check `verifica` comes out green or tells you what is missing (objective 0.1).
The `verifica` is in the series repo. From its root: `./lecciones/00-ambiente-listo/verifica`. It only reads; it does
not install or change anything. The first time it marks in red what you have not done yet (the app open, exercise
0.5): that is normal. Run it again at the end of the lesson.

---

## Step 2 · Android Studio and the SDK

**Download Android Studio** from developer.android.com/studio, the version in the table.

- **Mac:** open the `.dmg` and drag Android Studio to Applications. If you use Homebrew, `brew install --cask
  android-studio` does the same (that is how it was installed in the Nov 13 test).
- **Linux:** unpack the `.tar.gz` and run `bin/studio.sh` from your graphical session. In the test (Linux Mint 22.3)
  the downloaded file matched the sha256 published at developer.android.com/studio.

When you open it the first time, the wizard offers to download the SDK. Accept the licenses, keep the folder it
proposes and **write it down**: you will use it in the terminal. These are the ones we used in the test:

| System | SDK folder |
|---|---|
| Mac | `~/Library/Android/sdk` |
| Linux | `~/Android/Sdk` (on Linux Mint 22.3 the SDK was installed from the terminal in that folder; if the wizard proposes another, use yours) |

**Now the terminal.** Add these lines at the end of your `~/.zshrc` (Mac) or `~/.bashrc` (Linux), changing the
folder to yours, and open a new terminal:

```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"
```

If `sdkmanager` does not exist, install the **Command-line Tools** from Android Studio › Settings › Languages &
Frameworks › Android SDK › SDK Tools. Then the series' pieces:

```bash
yes | sdkmanager --licenses > /dev/null
sdkmanager "platform-tools" "emulator" "platforms;android-37.0"
sdkmanager --list_installed
```

> **Watch the name:** the package is `platforms;android-37.0`, not `android-37`. With the short name,
> `sdkmanager` finds nothing. And in the 2026 version, `sdkmanager` warns that it is deprecated and being replaced
> by `android sdk`; it still works and the series uses it because it is the one developer.android.com documents.

**What proves it** (objective 0.2), with four pieces of evidence that you keep:

1. Android Studio › About: Rabbit 2, 2026.2.2 Patch 1.
2. `sdkmanager --list_installed`: `platform-tools`, `emulator` and `platforms;android-37.0`.
3. `emulator -version`: 37.2.13.
4. In the project (step 5): `./gradlew -version` says Gradle 9.6.0, and the table above tells you in which file you
   see AGP, Kotlin and `jvmToolchain(25)`.

Lesson 00's `verifica` checks what can be automated; the Studio About screen you check yourself.

---

## Step 3 · The emulator, with acceleration

**Predict first.** Of the three pieces, which do you think will take longest to be ready, and which output would
prove it to you?

**Download an Android image with Google.** To sign in with Google in lesson 04 you need an image that includes Google
Play services. The series fixes `google_apis_playstore`, which also includes the Play Store; an image marked
**Google APIs** also includes Play services. What does not work is a `default` image (Android without Google
services).

```bash
# Mac with Apple silicon:
sdkmanager "system-images;android-37.0;google_apis_playstore;arm64-v8a"
# Linux x86_64 (measured on Linux Mint 22.3):
sdkmanager "system-images;android-37.0;google_apis_playstore;x86_64"
```

**Create the device** (change `arm64-v8a` to `x86_64` on Linux):

```bash
echo no | avdmanager create avd -n tienda37 \
  -k "system-images;android-37.0;google_apis_playstore;arm64-v8a" -d pixel_9
```

**Confirm acceleration before starting it:**

```bash
emulator -accel-check
```

On the test Mac it answered `0`, `Hypervisor.Framework OS X Version 27.0` and `accel`. The `0` at the start means
"all good". On Linux Mint 22.3 it answered `KVM (version 12) is installed and usable.`, with exit code `0`.

On Linux, `avdmanager` may print `Error: Could not load devices from …/devices.xml` and still create the device:
in the test it ended up with `hw.device.name=pixel_9`. Check it with `avdmanager list avd`.

**Start it:**

```bash
emulator -avd tienda37
```

The first start takes a while. On the test Mac (Apple silicon, 32 GB) it was 57 seconds; a later one, 24. Your
machine will have its own number. When you see the home screen, in another terminal:

```bash
adb devices
```

A row must appear whose name starts with `emulator-` and whose state is `device`. The number changes depending on
how many emulators you have open (the first is usually `emulator-5554`).

**Can your machine not manage it?** If you have no acceleration or you have 8 GB, use your Android phone. Settings ›
About phone › tap "Build number" seven times; then Settings › System › Developer options › USB debugging. Connect it,
accept the prompt on the phone and run `adb devices`: it must appear with its serial number and the word `device`.
That prompt authorizes **that** computer's key to debug your phone until you revoke it: accept it only on your own
computer. If you used a borrowed one, when you finish go to Developer options › **Revoke USB debugging
authorizations**.

**What proves it:** exercise 0.3 (below).

---

## Step 4 · The first time: your keyboard and your account

This is where the two things from the scene happen.

### The keyboard that does not type

A device created from the terminal comes with the computer's keyboard **turned off**: the line `hw.keyboard=no` in
its `config.ini` file. It is fixed with the emulator **closed**:

1. Find the file: in Android Studio, Device Manager › the three dots of `tienda37` › **Show on Disk**. `config.ini`
   is there. (It is almost always `~/.android/avd/tienda37.avd/`, but it changes if you defined `ANDROID_AVD_HOME`.)
2. Change `hw.keyboard=no` to `hw.keyboard=yes`. If the line does not exist, add it at the end.
3. Check before starting:

```bash
grep '^hw.keyboard=' PATH/config.ini      # must say hw.keyboard=yes; if it does not appear, do not start yet
```

Shortcut for the usual path (Mac and Linux):

```bash
sed -i.bak 's/^hw.keyboard=no$/hw.keyboard=yes/' ~/.android/avd/tienda37.avd/config.ini
```

Start the emulator again. Now your keyboard types.

### Account and trusted machine

The emulator keeps what you do in it —apps, settings and your account session— on the computer. That is why:

- **Sign in with your account only on your personal computer** or on an authorized machine.
- **In a lab or on a borrowed machine,** use your own phone (step 3) or a test account with no personal data; do not
  type your main account's password there.
- If you had to sign in on someone else's machine, when you finish remove your account and wipe the emulator's data:
  Device Manager › the device's three dots › **Wipe Data**.

### Your Google account

In the emulator: Settings › Google › add your account. If your account uses a **passkey**, Google shows you a QR code
to scan with your phone. That step connects the computer and the phone over Bluetooth, and the emulator does not have
it: in this lesson's test the QR did not complete. If it happens to you, tap **"Try another way"** and sign in with
your password or with the prompt that arrives on your phone.

**What proves it:** exercise 0.4 (below).

---

## Step 5 · The store project runs

The series repo is public on GitHub: https://github.com/HabilMX/tienda-app-nativa

**Predict first.** How long do you think the first build will take, and why would the second one be faster?

```bash
git clone https://github.com/HabilMX/tienda-app-nativa.git
cd tienda-app-nativa/lecciones/00-ambiente-listo/inicio
./gradlew -version          # Gradle 9.6.0 and the Java version
./gradlew installDebug      # builds it and INSTALLS it on the emulator or the phone; it does not open it
```

Now **open** it from the app drawer of the emulator or the phone. **You are on track** if the screen «The store runs
in your environment» appears (in the project, «La tienda corre en tu ambiente»).

The first build downloads libraries and takes several minutes. The starter project already carries configuration that
is explained in L01: do not change it here; first check that you can install and open the app.

### With Claude Code, in this order

The series uses Claude Code in every lesson, always the same way: **you predict → you ask → you read what it
generated → you run the `verifica` → you explain it in your own words.** If you still cannot explain it, stop, check
it again and ask for a clarification before moving on.

1. **Predict** without opening the files: in which file do you think the app's package name is decided?
2. **Ask** Claude Code: "Explain this project's structure to me: what each Gradle file does and where the package
   name is decided".
3. **Read** its answer and pick **one** concrete claim (for example, "the `applicationId` is in
   `app/build.gradle.kts`").
4. **Check it yourself** in the files: open it and write in `respuestas/00.md` (at the repo root) the claim, the file
   and the line where it is, for example `app/build.gradle.kts:8`. If the claim was false, write that down too: it
   counts just the same.
5. **Run the `verifica`** and **explain** in two lines what you checked.

**What proves it:** exercise 0.5 (below).

---

## Your exercises

Exercises 0.3 and 0.4 are done **without AI**: they are commands and screens you have to see yourself.

| # | You do | It is done when |
|---|---|---|
| 0.1 | You run the step 1 pre-check | The `verifica` comes out green, or you have written down what is missing and how you fixed it |
| 0.2 | You install Studio and the step 2 SDK | You have the four pieces of evidence from step 2 and they match the version table |
| 0.3 | You start the emulator with acceleration, or connect your phone | You have the output of `emulator -accel-check` with "accel" (or your phone in `adb devices` as `device`) |
| 0.4 | You turn on the keyboard and add your account | You type with your keyboard in the emulator and your account appears in Settings › Google |
| 0.5 | You run the project and check a claim from Claude Code | You opened the app and see «The store runs in your environment», and you have: the claim, the file and the line where you checked it, and whether it was true |

**Time:** the first time, counting downloads, allow an afternoon. On Linux Mint 22.3, the downloads of the SDK, the
emulator (it started in 109 seconds) and the first build (3 min 51 s) took 10 clock minutes with a good network; add
the Android Studio wizard and your breaks.

---

## Not running?

| You see | Probable cause | What you do |
|---|---|---|
| `sdkmanager: command not found` | The Command-line Tools or the `PATH` are missing | Install them (step 2) and open a new terminal |
| `sdkmanager` does not find `platforms;android-37` | The name is incomplete | It is `platforms;android-37.0` |
| `-accel-check` does not say "accel" (Linux) | Virtualization off, you are inside another virtual machine, KVM not installed or your user outside `kvm` | Step 1: BIOS/UEFI, out of the VM, or the `kvm` group (with permission), and log back in |
| The emulator takes minutes and crawls | No acceleration | Same as the previous row, or use your phone |
| Your keyboard does not type in the emulator | `hw.keyboard=no` | Step 4, with the emulator closed |
| The passkey QR does not finish | The emulator has no Bluetooth to your phone | "Try another way" |
| `avdmanager` does not recognize `pixel_9` | That profile is not in your version | Run `avdmanager list device -c` and use a name from the list |
| `adb devices` does not show your phone | USB debugging off or prompt not accepted | Check the prompt on the phone's screen |
| `adb devices` says `unauthorized` | You did not accept the computer's key | Unlock the phone, accept the prompt and repeat |
| Linux does not detect the phone over USB | System permissions (`plugdev`, udev rules) | In Android Studio, Tools › Troubleshoot Device Connections; if it asks for permissions, do it with whoever administers the machine |
| `installDebug` finished fine but you do not see the app | `installDebug` installs, it does not open | Open it from the app drawer (step 5) |
| "Java heap space" while building | Gradle memory | Check that `gradle.properties` carries `-Xmx4g` |

---

## To wrap up

**Answer without reading again:**

1. Which three pieces make up the Android environment, and with what evidence do you check each one?
2. Why does the emulator need an image with Google and not just any image?
3. You created the device from the terminal and your keyboard does not type. What do you change and where?
4. What do you do with your Google account if the machine is not yours?

**What comes next:** you now have somewhere to run the app. In L01 you write the store's first screen in Compose, and
the `verifica` checks what shows **without** opening the emulator.

---

## Compare your answers

1. Android Studio (the About screen), the SDK (`sdkmanager --list_installed`) and the emulator (`emulator -accel-check`
   and `adb devices`).
2. Because signing in with Google needs Google Play services. The `google_apis_playstore` images (the series' one) and
   those marked Google APIs include them; a `default` image does not.
3. `hw.keyboard=no` to `hw.keyboard=yes` in the device's `config.ini` (you find it with Show on Disk), with the
   emulator closed, and you start it again.
4. Do not use your main account there: use your phone or a test account. If you already signed in, remove it and wipe
   the emulator's data with Wipe Data.

---

## Lesson sources

Opened on Nov 13, 2026.

- Android Developers. *Install Android Studio* (requirements; Linux ARM without official support). https://developer.android.com/studio/install
- Android Developers. *Start the emulator from the command line* (`-accel-check`, KVM and Hypervisor.Framework). https://developer.android.com/studio/run/emulator-commandline
- Android Developers. *Configure hardware acceleration* (does not run inside another VM; KVM permissions). https://developer.android.com/studio/run/emulator-acceleration
- Android Developers. *Create and manage virtual devices* (images with Google APIs and Play; AVD data; Show on Disk; Wipe Data). https://developer.android.com/studio/run/managing-avds
- Android Developers. *sdkmanager* and *avdmanager* (listing packages and profiles). https://developer.android.com/tools/sdkmanager · https://developer.android.com/tools/avdmanager
- Android Developers. *Environment variables* (`ANDROID_AVD_HOME`, `PATH`). https://developer.android.com/tools/variables
- Android Developers. *Run apps on a hardware device* (RSA key, revoking authorizations, `plugdev`, Troubleshoot Device Connections). https://developer.android.com/studio/run/device
- Android Developers. *Android Debug Bridge* (serial `emulator-<port>`). https://developer.android.com/tools/adb
- Android Developers. *Build your app from the command line* (`installDebug` builds and installs). https://developer.android.com/build/building-cmdline
- Android Developers. *Android Gradle plugin release notes* (AGP 9.4 requires Gradle 9.6.0) and *Java versions in Android builds* (toolchain). https://developer.android.com/build/releases/about-agp · https://developer.android.com/build/jdks
- Android Developers. *Authenticate across form factors* (the emulator does not work for the cross-device passkey flow). https://developer.android.com/identity/form-factors
- Own measurement: trial on a Mac with Apple silicon and on Linux Mint x86_64, Nov 13, 2026; complete environment on
  Linux Mint 22.3 x86_64 (KVM, SDK, emulator, first build and installation), Nov 18, 2026.
