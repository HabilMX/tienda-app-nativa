# Leçon 00 — Environnement prêt

**Avant de lire, sept mots.** Une ligne chacun ; les autres mots de la série arrivent dans la leçon où ils servent.

- **Android Studio :** le programme où tu écris, compiles et lances l'application.
- **SDK** (kit de développement) : les outils et bibliothèques pour compiler et lancer des applications Android. Android
  Studio le télécharge, mais il vit dans son propre dossier.
- **Émulateur :** un téléphone Android qui tourne comme un programme à l'intérieur de ton ordinateur.
- **AVD** (appareil virtuel) : la fiche d'*un* téléphone simulé — modèle, version d'Android, mémoire. L'émulateur
  démarre un AVD.
- **Virtualisation et accélération :** la capacité de ton processeur à faire tourner une autre machine à l'intérieur
  de lui-même ; avec elle l'émulateur est rapide, sans elle il se traîne.
- **Terminal :** la fenêtre où tu tapes des commandes.
- **`PATH` :** la liste des dossiers où le terminal cherche les commandes ; si un outil n'y figure pas, tu dois
  taper son chemin complet.

**Ce que tu en retires :** Android Studio, le SDK et un émulateur qui fonctionnent sur ta machine, avec les
versions exactes de la série ; l'émulateur avec ton clavier et ton compte Google ; et le projet de la boutique qui
tourne à l'intérieur.

**Idée nouvelle de cette leçon (la seule qui est évaluée) :** l'environnement Android compte trois pièces — Android
Studio, le SDK et l'émulateur — et chacune se vérifie avec une preuve concrète (une commande ou un écran), pas à l'œil.

**Une note sur les textes de l'application.** Le projet du cours livre ses textes d'écran en espagnol, et les fichiers
de `lecciones/` ne sont pas traduits : les vérifications automatiques du cours (les `verifica`) cherchent ces textes
exacts. Donc, dans les blocs de code, les sorties de terminal et les maquettes d'écran, les textes restent tels que le
projet les contient, et dans la prose la leçon les cite en français et, la première fois, donne l'original entre
parenthèses : « La boutique tourne dans ton environnement » (dans le projet, « La tienda corre en tu ambiente »).

---

## La scène

Il est onze heures du soir et l'application de la boutique doit tourner demain à neuf heures. Tu ouvres Android
Studio pour la première fois. Il demande de télécharger des « composants ». Tu dis oui. Vingt minutes plus tard, un
téléphone dessiné apparaît à l'écran, avec l'heure et la batterie pleine. Tu tapes ton adresse e-mail pour te
connecter avec Google… et il ne se passe rien. Pas une seule lettre.

Tu essaies le clavier de l'ordinateur dans une autre fenêtre : il fonctionne. Tu reviens au téléphone : rien. Au
bout du compte, tu écris ton adresse lettre par lettre avec la souris, sur le clavier à l'écran. Puis Google te
demande de scanner un code QR avec ton portable. Tu le scannes. Ça tourne, ça tourne, et il dit que ça n'a pas pu
se faire.

Rien de tout cela n'est une faute de ta part. Les deux choses sont arrivées à la personne qui a testé cette leçon
avant te, le 13 novembre 2026, sur un Mac neuf. Toutes deux ont une cause concrète et un correctif d'une ligne. Cette
leçon te les dit **avant** qu'elles ne t'arrivent, avec les commandes qui te disent, à chaque étape, si tu es
sur la bonne voie.

---

## Avant de commencer

**Ce dont tu as besoin :**

| | Convient | Ne convient pas |
|---|---|---|
| Système | Linux sur un processeur **x86_64** (la série a été testée sur Linux Mint 22.3), ou un Mac | Windows (la série ne le couvre pas) · Linux sur processeur ARM : Android Studio n'y a pas de prise en charge officielle |
| Mémoire | 16 Go de RAM pour Android Studio avec l'émulateur | Avec 8 Go, utilise ton téléphone au lieu de l'émulateur (étape 3) |
| Disque | 20 Go libres. Sur Mac, cela a occupé environ 12 Go : Android Studio 3,3, SDK 4,5, appareil virtuel 3,1 et Gradle 1,1 (mesuré le 13 nov. 2026). Sur Linux Mint 22.3, cela a occupé environ 10 Go : Android Studio 1,6, SDK 3,9 (avec l'image de l'émulateur à l'intérieur), appareil virtuel 1,9 et Gradle 2,4 (mesuré le 18 nov. 2026) | Moins : l'appareil virtuel grossit à l'usage |
| Réseau | Pour les téléchargements de cette leçon et la première compilation | |
| Savoir | Ouvrir un terminal et utiliser git | Kotlin et Android ne sont pas supposés connus |

**Les versions de la série.** Elles ont toutes été testées ensemble à une date donnée. Ce ne sont pas « les plus
récentes » : ce sont celles dont nous savons qu'elles fonctionnent entre elles. Si tu en installes d'autres, les
résultats des leçons peuvent ne pas coïncider avec les tiens.

| Pièce | Version | Où la voir |
|---|---|---|
| Android Studio | Rabbit 2, 2026.2.2 Patch 1 | Android Studio › About |
| Android Gradle Plugin (AGP) | 9.4.1 | `gradle/libs.versions.toml` du projet |
| Gradle | 9.6.0 | `./gradlew -version` |
| Kotlin | 2.4.21 | `gradle/libs.versions.toml` |
| Java du projet | 25 | `jvmToolchain(25)` dans `app/build.gradle.kts` |
| Plateforme Android | 37 (`platforms;android-37.0`) | `sdkmanager --list_installed` |
| Émulateur | 37.2.13 | `emulator -version` |

*Testées le 13 nov. 2026.*

---

## Un mot de plus : nom de paquet

**Nom de paquet :** l'identifiant unique de ton application, comme `mx.habil.ejemplo.tienda`. Le changer
ensuite casserait la connexion avec Firebase. Tu le chercheras à l'étape 5.

---

## Étape 1 · Ta machine convient-elle ? (avant d'installer quoi que ce soit)

**Prédis d'abord.** Avant de lancer ce qui suit, écris en une ligne : penses-tu que la virtualisation est activée
sur ton processeur ? Comment le saurais-tu ?

**Sous Linux** (voici ce que cela a donné sur Linux Mint 22.3 le 18 nov. : `x86_64`, `VT-x`, `/dev/kvm` du groupe `kvm`
et ton utilisateur dans `kvm`) :

```bash
uname -m                                  # doit afficher x86_64
lscpu | grep -i virtualiz                 # doit afficher VT-x (Intel) ou AMD-V (AMD)
ls -l /dev/kvm                            # doit exister
groups                                    # ton utilisateur doit figurer dans « kvm »
```

Si `/dev/kvm` n'existe pas, tu ne peux pas encore utiliser KVM. Il y a trois causes possibles : la virtualisation
est désactivée dans le BIOS/UEFI, tu es à l'intérieur d'une autre machine virtuelle (VirtualBox, VMware, Docker : là,
l'émulateur accéléré ne tourne pas) ou KVM n'est pas installé. Le test qui tranche est `emulator -accel-check`, à
l'étape 3.

Si ton utilisateur n'est pas dans `kvm` : cette étape demande le mot de passe d'administration et modifie les
permissions de ton utilisateur. **Ne la fais que sur ton propre ordinateur** ou si la personne qui administre la
machine te l'indique ; dans un laboratoire, n'utilise pas `sudo` : demande qu'on t'ajoute au groupe.

```bash
sudo gpasswd -a "$USER" kvm               # t'ajoute au groupe
```

et **ferme la session puis rouvre-la** : le nouveau groupe ne s'applique pas à la session ouverte. Vérifie avec
`id -nG`. Mesuré sur Linux Mint le 13 nov. : sans le groupe, l'émulateur démarre sans accélération.

**Sur Mac :**

```bash
uname -m                                  # arm64 sur Apple silicon ; x86_64 sur Intel
```

L'installateur d'Android Studio doit être celui de ton processeur. Sur Mac, l'accélération est fournie par
Hypervisor.Framework, qui vient avec macOS : il n'y a rien à activer.

**Ce qui le prouve :** le `verifica` du précontrôle sort en vert ou te dit ce qui manque (objectif 0.1).
Le `verifica` se trouve dans le dépôt de la série. Depuis sa racine : `./lecciones/00-ambiente-listo/verifica`. Il se
contente de lire, il n'installe ni ne change rien. La première fois, il marque en rouge ce que tu n'as pas encore
fait (l'application ouverte, l'exercice 0.5) : c'est normal. Relance-le à la fin de la leçon.

---

## Étape 2 · Android Studio et le SDK

**Télécharge Android Studio** depuis developer.android.com/studio, dans la version du tableau.

- **Mac :** ouvre le `.dmg` et glisse Android Studio dans Applications. Si tu utilises Homebrew, `brew install --cask
  android-studio` fait la même chose (c'est ainsi qu'il a été installé lors du test du 13 nov.).
- **Linux :** décompresse le `.tar.gz` et lance `bin/studio.sh` depuis ta session graphique. Lors du test (Linux
  Mint 22.3), le fichier téléchargé correspondait au sha256 publié sur developer.android.com/studio.

À la première ouverture, l'assistant propose de télécharger le SDK. Accepte les licences, garde le dossier qu'il
propose et **note-le** : tu l'utiliseras dans le terminal. Voici ceux que nous avons utilisés lors du test :

| Système | Dossier du SDK |
|---|---|
| Mac | `~/Library/Android/sdk` |
| Linux | `~/Android/Sdk` (sur Linux Mint 22.3, le SDK a été installé depuis le terminal dans ce dossier ; si l'assistant t'en propose un autre, utilise le tien) |

**Maintenant, le terminal.** Ajoute ces lignes à la fin de ton `~/.zshrc` (Mac) ou `~/.bashrc` (Linux), en
remplaçant le dossier par le tien, et ouvre un nouveau terminal :

```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"
```

Si `sdkmanager` n'existe pas, installe les **Command-line Tools** depuis Android Studio › Settings › Languages &
Frameworks › Android SDK › SDK Tools. Ensuite, les pièces de la série :

```bash
yes | sdkmanager --licenses > /dev/null
sdkmanager "platform-tools" "emulator" "platforms;android-37.0"
sdkmanager --list_installed
```

> **Attention au nom :** le paquet est `platforms;android-37.0`, pas `android-37`. Avec le nom court,
> `sdkmanager` ne trouve rien. Et dans la version de 2026, `sdkmanager` avertit qu'il est obsolète et que `android sdk`
> le remplace ; il fonctionne toujours et la série l'utilise parce que c'est celui que documente developer.android.com.

**Ce qui le prouve** (objectif 0.2), avec quatre preuves que tu conserves :

1. Android Studio › About : Rabbit 2, 2026.2.2 Patch 1.
2. `sdkmanager --list_installed` : `platform-tools`, `emulator` et `platforms;android-37.0`.
3. `emulator -version` : 37.2.13.
4. Dans le projet (étape 5) : `./gradlew -version` affiche Gradle 9.6.0, et le tableau ci-dessus te dit dans quel
   fichier tu vois AGP, Kotlin et `jvmToolchain(25)`.

Le `verifica` de la 00 contrôle ce qui peut être automatisé ; l'écran About de Studio, c'est toi qui le regardes.

---

## Étape 3 · L'émulateur, avec accélération

**Prédis d'abord.** Parmi les trois pièces, laquelle penses-tu qui mettra le plus de temps à être prête, et quelle
sortie te le prouverait ?

**Télécharge une image Android avec Google.** Pour te connecter avec Google dans la leçon 04, il te faut une image
qui contienne les Google Play services. La série retient `google_apis_playstore`, qui apporte aussi le Play Store ; une
image marquée **Google APIs** apporte aussi les Play services. Ce qui ne convient pas, c'est une image `default`
(Android sans services Google).

```bash
# Mac avec Apple silicon :
sdkmanager "system-images;android-37.0;google_apis_playstore;arm64-v8a"
# Linux x86_64 (mesuré sur Linux Mint 22.3) :
sdkmanager "system-images;android-37.0;google_apis_playstore;x86_64"
```

**Crée l'appareil** (remplace `arm64-v8a` par `x86_64` sous Linux) :

```bash
echo no | avdmanager create avd -n tienda37 \
  -k "system-images;android-37.0;google_apis_playstore;arm64-v8a" -d pixel_9
```

**Confirme l'accélération avant de le démarrer :**

```bash
emulator -accel-check
```

Sur le Mac du test, il a répondu `0`, `Hypervisor.Framework OS X Version 27.0` et `accel`. Le `0` du début signifie
« tout va bien ». Sur Linux Mint 22.3, il a répondu `KVM (version 12) is installed and usable.`, avec le code de sortie
`0`.

Sous Linux, `avdmanager` peut afficher `Error: Could not load devices from …/devices.xml` et créer tout de même
l'appareil : lors du test, il s'est retrouvé avec `hw.device.name=pixel_9`. Vérifie avec `avdmanager list avd`.

**Démarre-le :**

```bash
emulator -avd tienda37
```

Le premier démarrage prend du temps. Sur le Mac du test (Apple silicon, 32 Go), cela a pris 57 secondes ; un démarrage
ultérieur, 24. Ta machine aura son propre chiffre. Quand tu vois l'écran d'accueil, dans un autre terminal :

```bash
adb devices
```

Une ligne doit apparaître dont le nom commence par `emulator-` et dont l'état est `device`. Le numéro change selon les
émulateurs que tu as ouverts (le premier est généralement `emulator-5554`).

**Ta machine n'y arrive pas ?** Si tu n'as pas d'accélération ou si tu as 8 Go, utilise ton téléphone
Android. Settings › About phone › touche sept fois « Build number » ; puis Settings › System › Developer options ›
USB debugging. Branche-le, accepte l'avis sur le téléphone et lance `adb devices` : il doit apparaître avec son
numéro de série et le mot `device`. Cet avis autorise la clé de **cet** ordinateur à déboguer ton téléphone jusqu'à ce
que tu la révoques : ne l'accepte que sur ton propre ordinateur. Si tu en as utilisé un emprunté, à la fin
va dans Developer options › **Revoke USB debugging authorizations**.

**Ce qui le prouve :** exercice 0.3 (ci-dessous).

---

## Étape 4 · La première fois : ton clavier et ton compte

C'est ici que se produisent les deux choses de la scène.

### Le clavier qui n'écrit pas

Un appareil créé depuis le terminal arrive avec le clavier de l'ordinateur **désactivé** : la ligne `hw.keyboard=no`
dans son fichier `config.ini`. Cela se corrige avec l'émulateur **fermé** :

1. Trouve le fichier : dans Android Studio, Device Manager › les trois points de `tienda37` › **Show on Disk**.
   `config.ini` est là. (C'est presque toujours `~/.android/avd/tienda37.avd/`, mais cela change si tu as défini
   `ANDROID_AVD_HOME`.)
2. Remplace `hw.keyboard=no` par `hw.keyboard=yes`. Si la ligne n'existe pas, ajoute-la à la fin.
3. Vérifie avant de démarrer :

```bash
grep '^hw.keyboard=' CHEMIN/config.ini    # doit afficher hw.keyboard=yes ; s'il n'apparaît pas, ne démarre pas encore
```

Raccourci pour le chemin habituel (Mac et Linux) :

```bash
sed -i.bak 's/^hw.keyboard=no$/hw.keyboard=yes/' ~/.android/avd/tienda37.avd/config.ini
```

Redémarre l'émulateur. Maintenant, ton clavier écrit.

### Compte et machine de confiance

L'émulateur conserve ce que tu y fais —applications, réglages et la session de ton compte— sur l'ordinateur.
C'est pourquoi :

- **Ne tu connectes avec ton compte que sur ton ordinateur personnel** ou sur une machine autorisée.
- **Dans un laboratoire ou sur une machine empruntée,** utilise ton propre téléphone (étape 3) ou un compte de test
  sans données personnelles ; n'y tape pas le mot de passe de ton compte principal.
- Si tu t'es connecté par nécessité sur une machine qui n'est pas la tienne, à la fin retire ton compte et
  efface les données de l'émulateur : Device Manager › les trois points de l'appareil › **Wipe Data**.

### Ton compte Google

Dans l'émulateur : Settings › Google › ajoute ton compte. Si ton compte utilise une **clé d'accès** (passkey),
Google te montre un code QR à scanner avec ton téléphone. Cette étape relie l'ordinateur et le téléphone par
Bluetooth, et l'émulateur n'en dispose pas : lors du test de cette leçon, le QR n'a pas abouti. Si cela t'arrive,
touche **« Try another way »** et connecte-toi avec ton mot de passe ou avec l'avis qui arrive sur ton téléphone.

**Ce qui le prouve :** exercice 0.4 (ci-dessous).

---

## Étape 5 · Le projet de la boutique tourne

Le dépôt de la série est public sur GitHub : https://github.com/HabilMX/tienda-app-nativa

**Prédis d'abord.** Combien de temps penses-tu que durera la première compilation, et pourquoi la seconde serait-elle
plus rapide ?

```bash
git clone https://github.com/HabilMX/tienda-app-nativa.git
cd tienda-app-nativa/lecciones/00-ambiente-listo/inicio
./gradlew -version          # Gradle 9.6.0 et la version de Java
./gradlew installDebug      # la compile et l'INSTALLE sur l'émulateur ou le téléphone ; ne l'ouvre pas
```

Maintenant, **ouvre-la** depuis le tiroir d'applications de l'émulateur ou du téléphone. **Tu es sur la bonne
voie** si l'écran « La boutique tourne dans ton environnement » apparaît (dans le projet, « La tienda corre en tu
ambiente »).

La première compilation télécharge des bibliothèques et dure plusieurs minutes. Le projet de départ apporte déjà une
configuration qui s'explique dans la L01 : ne la modifie pas ici ; commence par vérifier que tu peux installer et
ouvrir l'application.

### Avec Claude Code, dans cet ordre

La série utilise Claude Code dans toutes les leçons, toujours de la même manière : **tu prédis → tu demandes →
tu lis ce qu'il a généré → tu lances le `verifica` → tu l'expliques avec tes propres mots.** Si tu ne sais
toujours pas l'expliquer, arrête-toi, revérifie et demande une précision avant de continuer.

1. **Prédis** sans ouvrir les fichiers : dans quel fichier penses-tu que se décide le nom de paquet de l'application ?
2. **Demande** à Claude Code : « Explique-moi la structure de ce projet : ce que fait chaque fichier Gradle et où se
   décide le nom de paquet ».
3. **Lis** sa réponse et choisis **une** affirmation concrète (par exemple, « l'`applicationId` se trouve dans
   `app/build.gradle.kts` »).
4. **Vérifie-la toi-même** dans les fichiers : ouvre-le et note dans `respuestas/00.md` (à la racine du dépôt)
   l'affirmation, le fichier et la ligne où elle se trouve, par exemple `app/build.gradle.kts:8`. Si l'affirmation était
   fausse, note-le aussi : cela compte tout autant.
5. **Lance le `verifica`** et **explique** en deux lignes ce que tu as vérifié.

**Ce qui le prouve :** exercice 0.5 (ci-dessous).

---

## Tes exercices

Les exercices 0.3 et 0.4 se font **sans IA** : ce sont des commandes et des écrans que tu dois voir toi-même.

| # | Tu fais | C'est fait quand |
|---|---|---|
| 0.1 | Tu lances le précontrôle de l'étape 1 | Le `verifica` sort en vert, ou tu as écrit ce qui manque et comment tu l'as réglé |
| 0.2 | Tu installes Studio et le SDK de l'étape 2 | Tu as les quatre preuves de l'étape 2 et elles coïncident avec le tableau des versions |
| 0.3 | Tu démarres l'émulateur avec accélération, ou tu branches ton téléphone | Tu as la sortie de `emulator -accel-check` avec « accel » (ou ton téléphone dans `adb devices` comme `device`) |
| 0.4 | Tu actives le clavier et ajoutes ton compte | Tu écris avec ton clavier dans l'émulateur et ton compte apparaît dans Settings › Google |
| 0.5 | Tu lances le projet et vérifies une affirmation de Claude Code | Tu as ouvert l'application et tu vois « La boutique tourne dans ton environnement », et tu as : l'affirmation, le fichier et la ligne où tu l'as vérifiée, et si elle était vraie |

**Durée :** la première fois, en comptant les téléchargements, prévois un après-midi. Sur Linux Mint 22.3, les
téléchargements du SDK, l'émulateur (il a démarré en 109 secondes) et la première compilation (3 min 51 s) ont pris 10
minutes montre en main avec un bon réseau ; ajoute l'assistant d'Android Studio et tes pauses.

---

## Ça ne tourne pas ?

| Tu vois | Cause probable | Ce que tu fais |
|---|---|---|
| `sdkmanager: command not found` | Les Command-line Tools ou le `PATH` manquent | Installe-les (étape 2) et ouvre un nouveau terminal |
| `sdkmanager` ne trouve pas `platforms;android-37` | Le nom est incomplet | C'est `platforms;android-37.0` |
| `-accel-check` n'affiche pas « accel » (Linux) | Virtualisation désactivée, tu es dans une autre machine virtuelle, KVM non installé ou ton utilisateur hors de `kvm` | Étape 1 : BIOS/UEFI, sortir de la VM, ou le groupe `kvm` (avec autorisation), et rouvrir la session |
| L'émulateur met des minutes et se traîne | Pas d'accélération | Comme la ligne précédente, ou utilise ton téléphone |
| Ton clavier n'écrit pas dans l'émulateur | `hw.keyboard=no` | Étape 4, avec l'émulateur fermé |
| Le QR de la clé d'accès n'aboutit pas | L'émulateur n'a pas de Bluetooth vers ton téléphone | « Try another way » |
| `avdmanager` ne reconnaît pas `pixel_9` | Ce profil n'existe pas dans ta version | Lance `avdmanager list device -c` et utilise un nom de la liste |
| `adb devices` n'affiche pas ton téléphone | Débogage USB désactivé ou avis non accepté | Vérifie l'avis sur l'écran du téléphone |
| `adb devices` affiche `unauthorized` | Tu n'as pas accepté la clé de l'ordinateur | Déverrouille le téléphone, accepte l'avis et recommence |
| Linux ne détecte pas le téléphone en USB | Permissions du système (`plugdev`, règles udev) | Dans Android Studio, Tools › Troubleshoot Device Connections ; s'il demande des permissions, fais-le avec la personne qui administre la machine |
| `installDebug` s'est bien terminé mais tu ne vois pas l'application | `installDebug` installe, il n'ouvre pas | Ouvre-la depuis le tiroir d'applications (étape 5) |
| « Java heap space » à la compilation | Mémoire de Gradle | Vérifie que `gradle.properties` contient `-Xmx4g` |

---

## Pour conclure

**Réponds sans relire :**

1. Quelles trois pièces forment l'environnement Android, et avec quelle preuve vérifies-tu chacune ?
2. Pourquoi l'émulateur a-t-il besoin d'une image avec Google et pas de n'importe laquelle ?
3. Tu as créé l'appareil depuis le terminal et ton clavier n'écrit pas. Que changes-tu, et où ?
4. Que fais-tu de ton compte Google si la machine n'est pas la tienne ?

**La suite :** tu as maintenant où faire tourner l'application. Dans la L01, tu écris le premier écran de la
boutique en Compose, et le `verifica` contrôle ce qui s'affiche **sans** ouvrir l'émulateur.

---

## Compare tes réponses

1. Android Studio (l'écran About), le SDK (`sdkmanager --list_installed`) et l'émulateur (`emulator -accel-check` et
   `adb devices`).
2. Parce que se connecter avec Google demande les Google Play services. Les images `google_apis_playstore` (celle de la
   série) et celles marquées Google APIs les apportent ; une image `default`, non.
3. `hw.keyboard=no` par `hw.keyboard=yes` dans le `config.ini` de l'appareil (tu le trouves avec Show on Disk), avec
   l'émulateur fermé, puis tu le redémarres.
4. N'y utilise pas ton compte principal : ton téléphone ou un compte de test. Si tu t'y es déjà connecté,
   retire-le et efface les données de l'émulateur avec Wipe Data.

---

## Sources de la leçon

Ouvertes le 13 nov. 2026.

- Android Developers. *Install Android Studio* (prérequis ; Linux ARM sans prise en charge officielle). https://developer.android.com/studio/install
- Android Developers. *Start the emulator from the command line* (`-accel-check`, KVM et Hypervisor.Framework). https://developer.android.com/studio/run/emulator-commandline
- Android Developers. *Configure hardware acceleration* (ne tourne pas dans une autre VM ; permissions de KVM). https://developer.android.com/studio/run/emulator-acceleration
- Android Developers. *Create and manage virtual devices* (images avec Google APIs et Play ; données de l'AVD ; Show on Disk ; Wipe Data). https://developer.android.com/studio/run/managing-avds
- Android Developers. *sdkmanager* et *avdmanager* (lister paquets et profils). https://developer.android.com/tools/sdkmanager · https://developer.android.com/tools/avdmanager
- Android Developers. *Environment variables* (`ANDROID_AVD_HOME`, `PATH`). https://developer.android.com/tools/variables
- Android Developers. *Run apps on a hardware device* (clé RSA, révocation des autorisations, `plugdev`, Troubleshoot Device Connections). https://developer.android.com/studio/run/device
- Android Developers. *Android Debug Bridge* (numéro de série `emulator-<port>`). https://developer.android.com/tools/adb
- Android Developers. *Build your app from the command line* (`installDebug` compile et installe). https://developer.android.com/build/building-cmdline
- Android Developers. *Android Gradle plugin release notes* (AGP 9.4 exige Gradle 9.6.0) et *Java versions in Android builds* (toolchain). https://developer.android.com/build/releases/about-agp · https://developer.android.com/build/jdks
- Android Developers. *Authenticate across form factors* (l'émulateur ne convient pas au flux de clé d'accès entre appareils). https://developer.android.com/identity/form-factors
- Nos propres mesures : essai sur un Mac avec Apple silicon et sur Linux Mint x86_64, 13 nov. 2026 ; environnement complet
  sur Linux Mint 22.3 x86_64 (KVM, SDK, émulateur, première compilation et installation), 18 nov. 2026.
