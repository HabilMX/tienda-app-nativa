# Leçon 03 — L'application rencontre Firebase

**Avant de lire, quatre mots.** Une ligne chacun ; les autres arrivent à l'étape où ils servent.

- **Firebase :** un service de Google qui règle pour ton application des choses que tu ne vas pas programmer, comme
  retenir qui s'est connecté. Dans cette série, tu n'utilises qu'une seule partie, Authentication.
- **Certificat de débogage :** le document avec lequel Android signe l'application que tu compiles sur ton
  ordinateur. Il se crée tout seul, et chaque ordinateur a le sien.
- **Empreinte (SHA-1) :** un court condensé de ce certificat. Elle sert à le reconnaître sans rien montrer de privé.
- **`google-services.json` :** le fichier de configuration que tu télécharges depuis Firebase, avec les données de
  ton projet et de ton application.

**Ce que tu en retires :** ton application de boutique connectée à **ton** projet Firebase, avec un fichier de
configuration qui contient tout ce dont la leçon 04 a besoin, et savoir le vérifier sans ouvrir la console.

**Idée nouvelle de cette leçon :** une application se connecte à un service externe avec deux repères, son nom de
paquet et l'empreinte de son certificat. Le service ne connaît pas ton code : il connaît ces deux repères. Le reste
—le plugin Gradle et la bibliothèque— est fourni et expliqué, et n'est pas évalué ici.

**Une note sur les textes de l'application.** Le projet du cours livre ses textes d'écran en espagnol, et les fichiers
de `lecciones/` ne sont pas traduits : les vérifications automatiques du cours (les `verifica`) cherchent ces textes
exacts. Donc, dans les blocs de code, les sorties de terminal et les maquettes d'écran, les textes restent tels que le
projet les contient, et dans la prose la leçon les cite en français avec l'original entre parenthèses la première
fois : l'écran « La boutique » (« La tienda ») avec son bouton « Entrer » (« Entrar »), et le bouton « Se connecter avec
Google » (« Entrar con Google »).

---

## La scène

Tu enregistres ton application dans Firebase. L'assistant te propose un bouton : télécharger
`google-services.json`. Tu le télécharges, le mets dans son dossier et compiles.

Ça compile. Tout au vert.

Une leçon plus tard, tu ajoutes le bouton « Se connecter avec Google », tu le touches et tu n'entres pas. Le
message ne dit pas ce qui manque.

Cela s'est produit lors de l'essai de cette série, le 13 novembre 2026, sur un Mac : le fichier téléchargé avant
d'enregistrer l'empreinte et d'activer Google ne contenait pas deux blocs dont le bouton a besoin. C'est ce qui a été vu
ce jour-là, pas une règle que Firebase publie. La règle stable, elle, figure dans sa documentation : après avoir
enregistré l'empreinte et activé Google, tu télécharges le fichier **mis à jour**, parce que c'est lui qui contient ce
qu'il faut pour se connecter avec Google.

Et la partie « ça compile au vert » est mesurée ici, avec un fichier inventé auquel manquent ces blocs (18 novembre
2026) : `./gradlew assembleDebug` se termine bien, et le `verifica` de cette leçon donne 15 sur 18, avec trois coches
rouges qui disent « télécharge-le à nouveau ».

Le fichier est une copie de ce que la console savait de ton application **au moment où tu l'as téléchargé**. C'est
pourquoi l'ordre compte. D'abord tu connais les deux repères de ton application, puis tu suis l'ordre qui
fonctionne, et à la fin tu le casses toi-même pour lire l'erreur.

---

## Avant de commencer

**Ce dont tu as besoin :** la [02](02-pantallas-que-cambian.md) terminée, l'environnement de la
[00](00-ambiente-listo.md) (`ANDROID_HOME` défini) et un compte Google à toi. Tu vas t'en servir pour entrer dans
la console Firebase depuis ton navigateur : applique ce qui est dit dans « Compte et machine de confiance » de la 00.

**Ce qui est à toi.** Le projet Firebase, c'est toi qui le crées, avec ton compte, et personne d'autre ne
l'utilise. Lors de l'essai du 13 nov. 2026, il a été créé dans l'offre sans frais, sans enregistrer de carte.

**Le projet de départ.** Depuis la racine du dépôt de la série :

```bash
git pull
cd lecciones/03-la-app-conoce-a-firebase/inicio
```

C'est là que tu travailles. À côté se trouve `solucion/` : tu peux la lire et la comparer sans rien configurer.
**`./verifica solucion` sort au rouge dans un clone propre**, et c'est ce qui est attendu : ton fichier n'est pas
versé dans le dépôt, et avec le plugin appliqué le projet ne compile pas sans lui (mesuré le 18 nov. 2026 : 0 test exécuté). Si tu
veux la passer en revue, copie ton fichier dans `solucion/app/` —il y est aussi ignoré— et retire-le à la fin. Tes
prédictions et explications vont dans `respuestas/03.md`, à la racine du dépôt (`mkdir -p respuestas` s'il n'existe
pas).

**Ce que le projet apporte déjà (échafaudage).** Il n'est pas évalué ici :

| Ce qu'il apporte | Où | Pourquoi |
|---|---|---|
| Les trois écrans de la 02 et leurs sept tests | `app/src/main` et `app/src/test` | Tu pars de ce que tu as déjà écrit. Connecter Firebase ne doit pas les modifier |
| Les tests de la configuration et leur vérificateur | `app/src/test/…/ConfiguracionTest.kt` et `RevisorDeConfiguracion.kt` | Ce sont ceux que lance le `verifica`. Ne les modifie pas |
| Trois configurations inventées | `app/src/test/resources/configuraciones/` | Grâce à elles, les tests démontrent que le vérificateur distingue bien un fichier complet d'un fichier incomplet |
| Un modèle du fichier, sans données de personne | `app/google-services.json.example` | Pour que tu saches à quoi ressemble un fichier complet avant d'avoir le tien |

**Ce que cette leçon ne fait pas.** Il n'y a pas encore de bouton Google et personne ne se connecte : c'est la 04. Ici,
l'application est simplement présentée à Firebase, et tu le vérifies en lisant ton fichier.

---

## Étape 1 · Sans IA : les deux repères de ton application

Cette étape est la tienne, sans Claude Code. Firebase va te demander deux données. Les deux existent déjà sur ton
ordinateur.

**Le premier repère : le nom de paquet.** Prédis où il est écrit, puis cherche-le :

```bash
grep -n "applicationId" app/build.gradle.kts
```

Il doit répondre par une ligne avec `applicationId = "mx.habil.ejemplo.tienda"`. Selon la documentation d'Android, cet
identifiant distingue ton application sur le téléphone et dans la boutique d'applications, et ce que plusieurs outils
appellent « nom de paquet » est en réalité cette valeur.

**Le second repère : l'empreinte.** Android n'installe pas d'application sans signature. Celle que tu compiles pour
tester se signe toute seule, avec un certificat de débogage que les outils créent dans ton dossier personnel.
Demande à Gradle le rapport :

```bash
./gradlew signingReport
```

Voici ce que cela a donné le 18 nov. 2026 (ici avec l'empreinte masquée ; la tienne comporte 20 paires) :

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

Lis trois lignes :

- **`Variant: debug`** est l'application de test, celle que tu installes avec `installDebug`. Sa ligne **`SHA1`** est
  l'empreinte que tu vas enregistrer. Copie-la dans `respuestas/03.md`.
- **`Store`** dit de quel fichier elle provient : le magasin de clés de débogage (debug keystore) de **cet** ordinateur.
- **`Variant: release`** dit `Config: none` : ce projet n'a pas encore de quoi signer une version à publier. C'est le
  sujet de l'exercice 3.3.

**Prédis.** Ton collègue lance la même commande, avec le même projet, sur son ordinateur. Obtient-elle la même
empreinte ? Écris oui ou non, et pourquoi, avant de continuer.

**L'empreinte peut se montrer ; la clé, non.** La documentation d'Android les sépare ainsi : le certificat et son
empreinte sont faits pour être partagés avec les services qui les demandent ; la clé de signature et ses mots de passe
se gardent secrets. Le keystore de débogage ne sert qu'à développer : la même documentation dit qu'il est peu sûr
volontairement, et que c'est pourquoi les boutiques n'acceptent pas une application signée avec lui.

**Ce qui le prouve :** ton empreinte de débogage copiée et ta prédiction écrite (base des objectifs 3.1 et 3.3).

---

## Étape 2 · Sans IA : ce qui ne va pas dans le dépôt

Toujours sans Claude Code, et **avant** d'avoir le fichier : ainsi il n'existe pas une seule minute où git pourrait
l'emporter.

Crée le fichier `.gitignore` dans le dossier `inicio/`, avec ces deux lignes :

```text
# Tu configuración de Firebase: cada quien tiene su propio proyecto.
google-services.json
```

*(Le commentaire, en espagnol, dit : « Ta configuration Firebase : chacun a son propre projet ».)*

Vérifie-le. Cette commande demande à git s'il ignorerait le fichier et par quelle règle (elle regarde seulement) :

```bash
git check-ignore -v app/google-services.json
```

Une ligne doit apparaître avec ton fichier `.gitignore`, la ligne 2, la règle `google-services.json` et le chemin
`app/google-services.json`. Le début de la ligne change selon l'endroit d'où tu lances git ; depuis `inicio/`, le 18
nov. 2026, cela a donné ceci :

```text
lecciones/03-la-app-conoce-a-firebase/inicio/.gitignore:2:google-services.json	app/google-services.json
```

Le dépôt de la série avait déjà la même règle à sa racine ; git te rapporte la plus proche du fichier, qui est la
tienne. C'est la tienne qui protège le projet le jour où il vivra dans son propre dépôt.

**Pourquoi il ne va pas dans le dépôt, dit avec prudence.** Ce n'est pas parce que c'est un secret. La documentation de
Firebase dit que ce fichier contient des identifiants uniques mais non secrets, et que ses clés d'API ne font
qu'identifier ton projet : ce qui protège les données, ce sont les règles de sécurité, les permissions et App Check.
Dans cette série, il reste en dehors pour une autre raison : **chacun a son propre projet**. Si tu versais le tien dans le dépôt,
l'application de ton collègue parlerait avec ton Firebase, et avec une empreinte qui n'est pas la sienne.

**Ce qui ne va jamais dans le dépôt**, quel qu'il soit :

- Les clés de signature (fichiers `.jks` ou `.keystore`) et leurs mots de passe.
- Les mots de passe, quels qu'ils soient.
- La clé secrète de Microsoft que tu vas créer dans la leçon 05.

**Ce qui le prouve :** ton explication dans `respuestas/03.md` (objectif 3.2). Le `verifica` vérifie seulement que la
ligne figure dans ton `.gitignore`, que le modèle `.example` existe et que git ne suit aucun `google-services.json`.

---

## Étape 3 · Dans la console : l'ordre qui fonctionne

Deux mots de plus, que tu vas maintenant voir écrits :

- **OAuth :** l'accord par lequel une application demande à Google de confirmer qui tu es, sans voir ton mot de
  passe. Tu l'utilises dans la leçon 04 ; aujourd'hui, tu ne prépares que ses données.
- **Client OAuth :** la fiche avec laquelle Google reconnaît une application qui va lui demander cela.

Ouvre `app/google-services.json.example`. Voici à quoi ressemble un fichier complet. Repère deux blocs dans
`oauth_client` : celui qui dit `"client_type": 1`, avec `certificate_hash`, et celui qui dit `"client_type": 3`. Le
premier est le client Android, avec ton empreinte ; le second, le client web.

**Prédis d'abord.** Si tu téléchargeais le fichier juste après avoir enregistré l'application, avant de donner
l'empreinte et d'activer Google, lesquels de ces blocs t'attendrais-tu à trouver ? Écris-le.

Maintenant, suis cet ordre dans la [console Firebase](https://console.firebase.google.com). L'ordre est celui de
l'essai du 13 nov. 2026. Les noms de menus et de boutons sont donnés comme les écrit la documentation de Firebase au 18
nov. 2026, en anglais ; la console peut les afficher traduits ou les avoir déplacés.

1. **Crée le projet.** À la création, la console propose deux choses que la documentation signale comme facultatives :
   son assistance par IA et Google Analytics. Désactive les deux : cette série ne les utilise pas.
2. **Enregistre l'application Android.** Dans « Android package name » va `mx.habil.ejemplo.tienda`, copié tel quel. La
   documentation avertit qu'il distingue majuscules et minuscules et qu'**il ne peut pas être changé** après
   l'enregistrement : avec un autre nom, il faut enregistrer une autre application. Lors de l'essai, l'assistant n'a pas
   demandé l'empreinte et a proposé de télécharger le fichier. **Ne le télécharge pas encore.**
3. **Ajoute l'empreinte.** Dans « Project settings », sur la carte « Your apps », choisis ton application,
   touche « Add fingerprint », colle le `SHA1` de l'étape 1 et enregistre (« Save »).
4. **Active Google.** La documentation le situe dans « Security > Authentication », onglet « Sign-in method » :
   active le fournisseur Google et enregistre. Lors de l'essai, il a demandé un nom public pour le projet et une
   adresse e-mail d'assistance : utilise celle de ton compte.
5. **Seulement maintenant, télécharge `google-services.json`**, depuis la même carte « Your apps », et place-le dans
   `inicio/app/`. La documentation demande de ne garder que le fichier le plus récent.

**Un seul emplacement.** Le plugin accepte aussi un fichier par variante, par exemple dans `app/src/debug/`. Cette série
n'utilise que `app/google-services.json`, afin qu'il n'y ait qu'un seul fichier à vérifier, et le `verifica` ne regarde
pas les autres dossiers : avec le fichier ailleurs, il sort au rouge même si le projet compile (mesuré le 18 nov. 2026 :
12 sur 18, avec un avis indiquant où il l'a trouvé).

**Vérifie sans ouvrir la console.** Depuis la racine du dépôt :

```bash
./lecciones/03-la-app-conoce-a-firebase/verifica
```

À ce stade, **tu es sur la bonne voie** si les coches de ton fichier sont au vert et que seules celles de
l'étape 4 restent au rouge. Avec un fichier inventé complet, cela a donné ceci le 18 nov. 2026 (abrégé) :

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

*(C'est la sortie du `verifica`, exactement comme le script l'affiche, en espagnol. En français, les cinq coches vertes
disent : ton `app/google-services.json` nomme le paquet `mx.habil.ejemplo.tienda` ; une application de ton fichier
est du paquet `mx.habil.ejemplo.tienda` ; ton fichier contient un client OAuth Android (type 1) ; ce client contient
l'empreinte SHA-1 du keystore de débogage de cet ordinateur ; ton fichier contient un client OAuth web (type 3), celui
qu'utilisera le bouton Google dans la leçon 04. Les trois rouges disent : l'application compilée ne porte pas le
`project_id` et le `google_app_id` de ton fichier, il faut appliquer le plugin ; aucune bibliothèque Firebase ne
figure parmi les dépendances de l'application, ou elle n'a pas démarré avec le projet ; il manque Authentication,
l'application n'a pas `firebase-auth` parmi ses dépendances, le BoM ne l'ajoute pas. Et le résultat : 15 tests sur 18
au vert.)*

Si, à la place, tu vois « descárgalo otra vez después de la huella » (« télécharge-le à nouveau après l'empreinte »),
le fichier a été téléchargé trop tôt : refaites le point 5. Inutile de rien supprimer dans la console.

**Ce que compare la coche de l'empreinte.** Le test ouvre le keystore de débogage de ton ordinateur, calcule son SHA-1
et le cherche dans ton fichier. Il ne demande rien à Firebase.

**Ce qui le prouve :** les quatre coches « Simulado » de ton fichier disent ce que contient le fichier (objectif 3.5).
Que tu aies suivi l'ordre dans ton propre projet, aucun test local ne peut le voir : cela se confirme en se
connectant, dans la leçon 04.

---

## Étape 4 · Avec Claude Code : le plugin et la bibliothèque

Ton fichier est déjà dans son dossier, et l'application ne le lit pas encore. Il manque deux éléments d'échafaudage :

| Élément | Ce qu'il fait | Quand il travaille |
|---|---|---|
| Le plugin `google-services` | Lit ton `google-services.json` et le transforme en ressources que l'application pourra lire. Selon sa documentation, il peut aussi ajouter des dépendances de base pour les services que tu as activés | À la compilation. Il ne s'exécute pas comme du code sur le téléphone |
| Le BoM de Firebase et `firebase-auth` | Le BoM est une liste de versions qui fonctionnent ensemble : tu n'en fixes qu'une et les bibliothèques vont sans version. `firebase-auth` est celle d'Authentication, et c'est elle qui apporte des fonctions à l'exécution | À l'intérieur de l'application |

C'est ici qu'entre l'outil, et il entre toujours de la même manière : **tu prédis → tu demandes → tu lis ce
qu'il a généré → tu lances le `verifica` → tu l'expliques avec tes propres mots.**

1. **Prédis.** Écris dans `respuestas/03.md` : quels fichiers vont changer ? L'un d'eux est-il un `.kt` ?
2. **Demande** à Claude Code, depuis `inicio/` : « Dans `gradle/libs.versions.toml`, ajoute le plugin
   `com.google.gms.google-services` en version 4.6.0 et le BoM de Firebase `com.google.firebase:firebase-bom`
   en 35.2.0, avec la bibliothèque `firebase-auth` sans version. Déclare le plugin dans le `build.gradle.kts` de la
   racine avec `apply false` et applique-le dans `app/build.gradle.kts` ; au même endroit, ajoute le BoM et
   `firebase-auth`. Ne modifie aucun fichier `.kt`, ni les tests, ni `app/google-services.json`, et ne copie aucune
   valeur de ce fichier ailleurs. »
3. **Lis** le changement complet avant de l'accepter. Il doit s'agir de trois fichiers, avec ces éléments :

```toml
# gradle/libs.versions.toml — une nouvelle ligne dans chaque section
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
// build.gradle.kts de la racine — il est déclaré, sans être appliqué
    alias(libs.plugins.google.services) apply false
```

```kotlin
// app/build.gradle.kts — ici il est appliqué, et les deux dépendances sont ajoutées
plugins {
    …
    alias(libs.plugins.google.services)
}
…
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
```

   Cinq choses à vérifier en lisant :

   - **Les versions sont celles de la série :** 4.6.0 et 35.2.0, celles que montre la documentation de Firebase au 18
     nov. 2026. S'il en apparaît une autre, demande qu'il la corrige : la série est testée avec celles-ci.
   - **Les deux lignes sont là, le BoM et `implementation(libs.firebase.auth)`.** Le BoM ne fait que choisir des
     versions compatibles ; la seconde ligne est celle qui ajoute Authentication. Avec le BoM et une autre bibliothèque
     Firebase à sa place, le `verifica` donne 17 sur 18 et dit « Falta Authentication » (« Il manque Authentication »)
     (mesuré le 18 nov. 2026).
   - **`firebase-auth` ne porte pas de version.** C'est le BoM qui la lui donne. Une version isolée à cet endroit
     défait ce que le BoM résout.
   - **Le tiret du catalogue devient un point :** `google-services` dans le catalogue s'écrit
     `libs.plugins.google.services` dans le fichier Gradle.
   - **Personne n'a touché ton fichier ni copié ses valeurs.** `git status` ne doit pas montrer
     `google-services.json` (il est ignoré), et aucun `.kt` ne doit apparaître modifié.

4. **Lance le `verifica`.** Maintenant la ligne est
   `Resultado: 18 de 18 pruebas en verde (0 en rojo, 0 con error, 0 saltadas)`. Il reste une coche au rouge, celle de
   `respuestas/03.md` : elle se remplit à l'étape 5.
5. **Explique**, en trois lignes de `respuestas/03.md` : ce que fait le plugin de ton fichier, et à quel moment.

**Regarde-le toi-même (facultatif).** Le plugin laisse ce qu'il a généré dans un `values.xml` sous
`app/build/generated/res/`. Le dossier exact est un détail interne et peut changer d'une version à l'autre : cherche-le
plutôt que de le mémoriser. Depuis `inicio/` :

```bash
find app/build/generated/res -name values.xml | xargs grep -l google_app_id
```

Avec les versions de la série, cela a répondu `app/build/generated/res/processDebugGoogleServices/values/values.xml`
(18 nov. 2026). Ouvre-le et cherche `project_id`, `google_app_id` et `default_web_client_id`. Avec le fichier inventé
complet, il contenait six noms : `default_web_client_id`, `google_api_key`, `google_app_id`,
`google_crash_reporting_api_key`, `google_storage_bucket` et `project_id`. Avec le fichier sans clients OAuth,
`default_web_client_id` **n'apparaît pas**. C'est la donnée que le bouton Google va demander dans la leçon 04, et c'est
la raison de la scène : sans client web dans le fichier, l'application compile sans lui.

**Ce que prouve chaque coche, et ce qu'elle ne prouve pas.**

- **« Simulado », celles de ton fichier :** elles ont lu le texte de `app/google-services.json`. Elles prouvent ce
  qu'il contient.
- **« Simulado », celle de l'application compilée :** elle a lu les ressources générées par le plugin, comme les lirait
  l'application. Elle prouve que ton fichier est arrivé dans l'application lors de cette compilation.
- **« Simulado », celle de la bibliothèque :** le test a demandé à Firebase de démarrer avec ces ressources et a lu avec
  quel projet il s'est retrouvé. Dans l'application installée, selon la documentation de Firebase, ce démarrage est fait
  par la bibliothèque à l'ouverture de l'application.
- **« Simulado », celle d'Authentication :** elle a demandé si la classe principale de `firebase-auth` voyage dans
  l'application. Elle ne l'a pas utilisée : elle n'a créé aucune session.
- **« Texto » et « Git » :** elles ont cherché des mots dans tes fichiers et demandé à git ce qu'il suit.

Aucune n'a parlé à Firebase. Un fichier bien formé d'un projet que tu as déjà supprimé passe au vert, et un fichier
monté à la main avec cette forme aussi. Que le fichier vienne de ton projet, que l'empreinte soit enregistrée et que
Google soit activé se vérifie de la seule manière possible : en se connectant, dans la leçon 04.

**Dans l'application installée.** `./gradlew installDebug` et ouvre-la : l'accueil doit s'afficher, comme dans la 02.
Sur l'émulateur du test (Pixel 9, Android 37, 18 nov. 2026), avec la solution et un fichier inventé, l'application s'est
ouverte sur « La tienda » avec son bouton « Entrar », et le journal du système (`adb logcat`) a montré la ligne
`FirebaseInitProvider: FirebaseApp initialization successful`. Aucun bouton n'y a été touché : le parcours est mesuré
par les tests simulés.

**Si une ligne n'est toujours pas claire,** avant de continuer, essaie de l'expliquer avec tes propres mots. Si cela ne
vient toujours pas, reviens à ce fragment ou demande une précision.

**Ce qui le prouve, et jusqu'où.** Au niveau simulé, le 18 sur 18 prouve que ce fichier a la forme et les données que la
leçon attend, qu'elles sont arrivées dans cette compilation et qu'Authentication voyage dans l'application. Il **ne
prouve pas** que le fichier vienne de ton projet, que tu aies suivi l'ordre ni que Google soit activé. Cette partie
de l'objectif 3.1 se démontre au niveau réel, en te connectant avec ton compte dans la leçon 04.

---

## Étape 5 · Sans IA : casses-en un exprès

C'est l'exercice 3.4 et il se fait **sans Claude Code**. Tu vas provoquer l'erreur « la configuration est d'un autre
paquet » sans toucher à ton fichier ni à la console : en changeant le repère du côté de l'application.

**1. Prédis.** Dans `app/build.gradle.kts`, tu vas changer `applicationId = "mx.habil.ejemplo.tienda"` en
`applicationId = "mx.habil.ejemplo.otra"`. Écris : est-ce que ça compile ? qui se plaint, Kotlin, Gradle ou Firebase ?

**2. Casse-le et mesure.** Fais le changement et compile :

```bash
./gradlew assembleDebug
```

Voici ce que cela a donné le 18 nov. 2026 (le chemin, abrégé) :

```text
* What went wrong:
Execution failed for task ':app:processDebugGoogleServices' …
> No matching client found for package name 'mx.habil.ejemplo.otra' in …/app/google-services.json
```

Copie dans `respuestas/03.md` la ligne qui commence par `> No matching client found`.

**3. Diagnostique**, avec tes propres mots, avant de lire la suite : qu'a-t-il cherché, où l'a-t-il cherché et
pourquoi ne l'a-t-il pas trouvé ?

Le nom de la tâche le dit : `processDebugGoogleServices` est le plugin, à la compilation. Il a cherché dans la liste
`client` de ton fichier une application dont le `package_name` serait l'`applicationId` de celle-ci, et il n'y en a
aucune. Ni le réseau ni Firebase n'est intervenu : l'erreur est née de la comparaison de deux textes sur ton
ordinateur.

**4. Le correctif tentant, et pourquoi il ne convient pas.** Modifier à la main le `package_name` dans le fichier supprime l'erreur de
compilation. Ne le fais pas : le fichier est une copie de ce qui se trouve dans la console, et la console conserve
toujours l'autre application enregistrée. Ce qui convient, c'est que les deux repères coïncident vraiment : soit
l'application utilise le paquet enregistré, soit tu enregistres une autre application avec le nouveau paquet et tu
télécharges **son** fichier.

**5. Répare-le.** Remets la ligne à `applicationId = "mx.habil.ejemplo.tienda"`. Cette commande ne doit montrer que
les changements de l'étape 4 (elle regarde seulement) :

```bash
git diff -- app/build.gradle.kts
```

Lance le `verifica` : 18 sur 18, et maintenant aussi la coche de `respuestas/03.md`.

Ce vert confirme deux choses : que le projet est réparé et que tu as collé la ligne de l'erreur. Il ne sait pas si
tu l'as cassé, ne lit pas ta prédiction ni ne note ton diagnostic. Le 3.4 s'évalue avec ton explication, et
il est terminé quand tu peux cocher les trois cases :

- [ ] Ta prédiction du point 1 est écrite, avec ce que tu as vu en dessous.
- [ ] Tu as collé la ligne de l'erreur.
- [ ] Tu as dit qui s'est plaint, quels deux textes il a comparés et pourquoi modifier le fichier à la main n'est pas
      le correctif.

**Ce qui le prouve :** « Casses-en un exprès » (objectif 3.4).

---

## Tes exercices

Les étapes 1 et 2, le 3.3 et le 3.4 se font **sans IA**.

| # | Tu fais | C'est fait quand |
|---|---|---|
| 3.1 | Tu suis l'ordre : projet → application → empreinte → Google → téléchargement | Le `verifica` donne 18 sur 18 avec ton fichier : cela valide le fichier et la compilation. Que le projet est le tien, avec l'empreinte enregistrée et Google activé, c'est la connexion réelle de la leçon 04 qui le valide |
| 3.2 | Tu expliques pourquoi `google-services.json` ne va pas dans le dépôt de cette série et nommes ce qui n'y va jamais | Ton explication est dans `respuestas/03.md`. Le `verifica` vérifie seulement le `.gitignore`, le `.example` et ce que git suit |
| 3.3 | Tu distingues l'empreinte de débogage de celle d'une version publiée | Tu as répondu aux trois cas ci-dessous |
| 3.4 | Tu casses le paquet et diagnostiques l'erreur | Ta preuve, ce sont les trois cases de l'étape 5, écrites par toi. Le `verifica` confirme seulement que le projet est réparé et que tu as collé l'erreur |
| 3.5 | Tu vérifies, sans ouvrir la console, que ton fichier contient le client avec ton empreinte et le client web | Les coches « Simulado » de ton fichier sont au vert. L'empreinte qu'elles comparent est celle de débogage de cet ordinateur |

**Exercice 3.3 · Quelle empreinte enregistres-tu ?** Celle que tu as enregistrée aujourd'hui est celle de débogage
de ton ordinateur. Selon la documentation d'Android et de Google, une application en a plus d'une :

| Qui signe l'application | D'où vient son empreinte |
|---|---|
| Ton ordinateur, à la compilation pour tester | Du keystore de débogage : `./gradlew signingReport`, variante `debug` |
| Toi, avec ta propre clé, pour publier | Du keystore de cette clé. Ce n'est pas celle de débogage |
| Google Play, avec Play App Signing | De la Play Console, sur la page de signature de l'application. La clé est conservée par Google et n'est pas celle que tu utilises pour envoyer l'application |

Réponds dans `respuestas/03.md` : quelle empreinte faut-il enregistrer dans Firebase dans chaque cas ?

1. Ton collègue clone ton projet et veut se connecter avec Google depuis son ordinateur, sur ton Firebase.
2. Tu publies l'application sur Google Play avec Play App Signing et les utilisateurs la téléchargent de là.
3. Tu changes d'ordinateur et continues à développer.

**Durée :** compte environ trois heures, avec la console Firebase et le « Casses-en un exprès ». C'est une
estimation : nous l'ajusterons avec le temps que prendra la première cohorte.

---

## Ça ne tourne pas ?

Les messages en anglais sont ceux que le plugin `google-services` 4.6.0 a donnés le 18 nov. 2026.

| Tu vois | Cause probable | Ce que tu fais |
|---|---|---|
| `File google-services.json is missing.` | Le plugin est déjà appliqué et le fichier n'est pas dans `app/` | Place-le dans `inicio/app/`, avec ce nom exact. Sur `solucion/`, c'est ce qui est attendu dans un clone propre |
| ❌ « Falta Authentication » | Le BoM est là et la bibliothèque manque | Ajoute `implementation(libs.firebase.auth)` dans `app/build.gradle.kts` |
| ℹ️ « Hay uno en …/app/src/debug/… » (il y en a un dans …/app/src/debug/…) | Le fichier est dans un dossier de variante | Déplace-le dans `app/` : cette série utilise un seul emplacement |
| `No matching client found for package name '…'` | L'`applicationId` de l'application et le paquet du fichier ne coïncident pas | Étape 5. Si le fichier est d'un autre paquet, enregistre l'application avec le paquet de la série et télécharge son fichier |
| ❌ « no trae un cliente de OAuth de Android (tipo 1) » (ne contient pas de client OAuth Android (type 1)) | Tu l'as téléchargé avant d'enregistrer l'empreinte ou d'activer Google | Télécharge-le à nouveau (étape 3, point 5) |
| ❌ « Ningún cliente de Android de tu archivo trae la huella de depuración de esta computadora » (aucun client Android de ton fichier ne contient l'empreinte de débogage de cet ordinateur) | Tu as enregistré une autre empreinte, ou le fichier a été téléchargé sur un autre ordinateur | Lance `./gradlew signingReport`, enregistre ce `SHA1` et télécharge à nouveau |
| ❌ « no trae un cliente de OAuth web (tipo 3) » (ne contient pas de client OAuth web (type 3)) | Tu l'as téléchargé avant d'activer Google | Active Google et télécharge à nouveau |
| ❌ « Git sí rastrea un google-services.json » (git suit bien un google-services.json) | Il a été ajouté avant que le `.gitignore` n'existe | `git rm --cached <fichier>` le retire de git sans supprimer ta copie |
| `keytool -list -v` se termine par `MissingFormatArgumentException` | Cela s'est produit sur le Mac de la série avec Java 25 et le système en espagnol (18 nov. 2026) | Utilise `./gradlew signingReport`, ce que demande la leçon |
| `~/.android/debug.keystore` n'existe pas et tu n'as pas l'empreinte | Tu n'as encore rien compilé sur cet ordinateur : le keystore est créé par la première compilation de débogage, pas par `signingReport` (mesuré sur Linux Mint 22.3, 18 nov. 2026) | Lance `./gradlew assembleDebug` puis `./gradlew signingReport` |

Sur Linux Mint 22.3 (x86_64), le `verifica` de cette leçon a donné le même résultat que sur le Mac (18 nov. 2026) : sur
`inicio/`, 11 sur 18 au vert ; sur `solucion/` sans ton fichier, « 0 pruebas corridas » (0 test exécuté) avec l'avis ;
et avec un fichier de test contenant l'empreinte de cet ordinateur, 18 sur 18. Les dépendances de Firebase ont ajouté
environ 200 Mo à `~/.gradle`.

---

## Pour conclure

**Réponds sans relire :**

1. Quels sont les deux repères avec lesquels Firebase reconnaît ton application, et où se trouve chacun sur ton
   ordinateur ?
2. Pourquoi le fichier se télécharge-t-il à la fin et non quand l'assistant le propose ?
3. Ton `verifica` donne 18 sur 18. Dis une chose que cela prouve et une qu'il ne prouve pas.
4. Pourquoi `google-services.json` n'est-il pas versé dans le dépôt de cette série ? Et quelle chose n'y est jamais versée, nulle
   part ?

**La suite :** dans la L04, tu places le bouton et tu te connectes avec Google. C'est là que se vérifie, pour de
vrai, tout ce qui est aujourd'hui resté écrit dans ton fichier.

---

## Compare tes réponses

**Étape 1.** Non, elle n'obtient pas la même. Le keystore de débogage est créé par les outils dans le dossier personnel de
chacun, donc chaque ordinateur a son certificat et son empreinte. Le projet est le même ; la signature, non.

**Étape 3.** Lors de l'essai du 13 nov. 2026, aucun des deux : le fichier téléchargé avant l'empreinte et l'activation de
Google ne contenait ni le type 1 (Android, avec `certificate_hash`) ni le type 3 (web). C'est une observation de ce
jour-là. Ce que la documentation de Firebase assure, c'est l'inverse : le fichier **mis à jour**, téléchargé après ces
deux étapes, contient les informations OAuth dont le bouton a besoin. C'est pourquoi il se télécharge à la fin.

**Étape 4.** Trois fichiers changent : `gradle/libs.versions.toml`, le `build.gradle.kts` de la racine et
`app/build.gradle.kts`. Aucun `.kt` : connecter l'application n'a touché aucun écran, et c'est pourquoi les sept tests
de la 02 restent au vert. Le plugin lit `app/google-services.json` à la compilation et génère des ressources de
l'application (`project_id`, `google_app_id` et, si le fichier contient un client web, `default_web_client_id`). Le
plugin ne s'exécute pas sur le téléphone : ce qui travaille à l'exécution, ce sont les bibliothèques que tu as
déclarées, comme `firebase-auth`.

**Étape 5.** Ça ne compile pas. C'est Gradle qui se plaint, dans la tâche du plugin `google-services` ; ni Kotlin ni
Firebase. Il a comparé l'`applicationId` de l'application avec les `package_name` du fichier. Modifier le fichier fait
disparaître l'erreur et laisse la console avec une autre application enregistrée : les repères continueraient de ne pas
coïncider là où cela compte.

**Exercice 3.3.**

1. Celle de débogage **de son ordinateur**, ajoutée à ton application dans Firebase : une application accepte
   plusieurs empreintes. Ensuite, elle a besoin de télécharger à nouveau le fichier.
2. Celle du certificat avec lequel Google Play signe l'application, qui se consulte dans la Play Console. Celle de
   débogage et celle de la clé avec laquelle tu envoies l'application ne servent pas pour ce que téléchargent les
   utilisateurs.
3. Celle de débogage du nouvel ordinateur : c'est un autre keystore et une autre empreinte.

**Pour conclure :**

1. Le nom de paquet, dans l'`applicationId` de `app/build.gradle.kts`, et l'empreinte du certificat avec lequel
   l'application est signée ; celle de débogage provient du keystore de ton dossier personnel et c'est `signingReport`
   qui l'affiche.
2. Parce que le fichier est une copie de ce que la console sait à ce moment-là, et que Firebase demande de télécharger
   le fichier mis à jour après l'empreinte et l'activation de Google. Lors de l'essai, le précédent ne contenait pas les
   clients OAuth.
3. Cela prouve que le texte de ton fichier contient le paquet, le client Android avec ton empreinte de débogage et
   le client web, que l'application compilée porte ces données et qu'Authentication voyage dans l'application. Cela ne
   prouve pas que le fichier vienne de ton projet, que le projet existe ni que tu puisses te connecter avec Google.
4. Parce que chacun a son propre projet, pas parce qu'il est secret. Ne sont jamais versés dans le dépôt les clés de signature, les
   mots de passe ni la clé secrète de Microsoft.

---

## Sources de la leçon

Ouvertes le 18 nov. 2026.

- Firebase. *Add Firebase to your Android project* (étapes, le paquet ne peut pas être changé, le plugin 4.6.0 et le BoM 35.2.0, « unique, but non-secret identifiers »). https://firebase.google.com/docs/android/setup
- Firebase. *Google services Gradle plugin and JSON config file* (quelles ressources le plugin génère et de quel champ vient chacune ; les messages d'erreur). https://firebase.google.com/docs/android/google-services-plugin-and-file
- Firebase. *Understand Firebase for Android* (ce qu'est le BoM ; le plugin travaille à la compilation). https://firebase.google.com/docs/android/learn-more
- Firebase. *Learn about and manage API keys for Firebase* (les clés identifient, elles n'autorisent pas). https://firebase.google.com/docs/projects/api-keys
- Firebase. *Authenticate with Google on Android* (empreinte, activer Google et télécharger le fichier mis à jour). https://firebase.google.com/docs/auth/android/google-signin
- Firebase. *FirebaseInitProvider* (« Initializes Firebase APIs at app startup time »). https://firebase.google.com/docs/reference/android/com/google/firebase/provider/FirebaseInitProvider
- Aide de Firebase. *Download Firebase config file or object* et *Add a SHA fingerprint*. https://support.google.com/firebase/answer/7015592 · https://support.google.com/firebase/answer/9137403
- Google for Developers. *Client authentication* (`./gradlew signingReport`, magasin de débogage, Play App Signing). https://developers.google.com/android/guides/client-auth
- Android Developers. *Sign your app* (certificat de débogage, clés, empreintes pour les fournisseurs, Play App Signing). https://developer.android.com/studio/publish/app-signing
- Android Developers. *Configure the app module* (l'`applicationId` et le « nom de paquet »). https://developer.android.com/build/configure-app-module
- Android Developers. *Migrate your build to version catalogs* (`libs.versions.toml`, `alias`). https://developer.android.com/build/migrate-to-catalogs
- Dépôt Maven de Google : ses index listent `google-services` 4.6.0 et `firebase-bom` 35.2.0. https://dl.google.com/dl/android/maven2/com/google/gms/google-services/maven-metadata.xml · https://dl.google.com/dl/android/maven2/com/google/firebase/firebase-bom/maven-metadata.xml
- Essai de la série, 13 nov. 2026, Mac avec Apple silicon : l'ordre des étapes de la console et ce qui a été vu ce jour-là (l'assistant n'a pas demandé l'empreinte ; activer Google a demandé un nom public et une adresse d'assistance ; le fichier téléchargé trop tôt ne contenait pas de clients OAuth ; offre sans frais). Les noms de menus et de boutons de cette leçon ne viennent pas de l'essai : ce sont ceux de la documentation citée ci-dessus.
- Nos propres mesures : Mac avec Apple silicon, 18 nov. 2026 (`signingReport` ; le `verifica` sur `inicio/` et `solucion/` ; les fichiers inventés : complet, sans clients, d'un autre paquet et avec une autre empreinte ; les ressources que génère le plugin ; les messages d'erreur ; le `keytool` ; l'application installée dans un émulateur neuf, sans compte, qui a été supprimé à la fin). Les mesures ont été faites avec des fichiers inventés, pas avec un vrai projet Firebase.

Revue externe (18 nov. 2026) : AVEC CHANGEMENTS, 2 de priorité haute, 6 moyenne et 3 basse ; appliquée.
