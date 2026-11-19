# Leçon 04 — Se connecter avec Google

**Avant de lire, quatre mots.** Une ligne chacun ; les autres arrivent à l'étape où ils servent.

- **Jeton d'identité :** un texte signé par Google qui dit qui tu es et pour qui il a été émis. Ce n'est pas ton
  mot de passe, et il expire.
- **Audience :** le destinataire écrit à l'intérieur du jeton. Celui qui le reçoit vérifie qu'il porte son nom.
- **Credential Manager :** la pièce d'Android qui affiche la fenêtre pour choisir un compte et remet à ton
  application ce que la personne a choisi.
- **ID client web :** l'identifiant que ton `google-services.json` contient dans le bloc `"client_type": 3`. Tu
  l'as vérifié dans la leçon 03.

**Ce que tu en retires :** le bouton « Se connecter avec Google » (« Entrar con Google ») de ta boutique, écrit par
te, avec un message différent pour chaque manière de ne pas entrer, et savoir dire ce qui voyage à chaque étape entre
ton application, Google et Firebase.

**Idée nouvelle de cette leçon :** le jeton d'identité. Google remet une preuve signée de qui tu es, faite pour une
audience, et Firebase la vérifie. Ton application ne vérifie rien : elle porte seulement le jeton d'un côté à
l'autre. Le reste —les bibliothèques, les fonctions qui se mettent en pause et les deux « portes »— est fourni et
expliqué, et n'est pas évalué ici.

**Une note sur les textes de l'application.** Comme dans la leçon 03, le projet livre ses textes d'écran en espagnol, et
les vérifications automatiques du cours (les `verifica`) cherchent ces textes exacts. Dans les blocs de code, les
sorties de terminal et les maquettes d'écran, ils restent tels que le projet les contient ; dans la prose, la leçon les
cite en français avec l'original entre parenthèses la première fois : le bouton « Se connecter avec Google » (« Entrar
con Google ») et l'écran « La boutique » (« La tienda »).

---

## La scène

L'émulateur a ton compte Google ajouté. Tu places le bouton, tu le touches, et l'application répond qu'il n'y a
aucun compte avec lequel se connecter.

Le compte est pourtant là. Tu le vois dans Settings.

Cela s'est produit lors de l'essai de cette série, le 13 novembre 2026, sur un Mac. Le code demandait la credential
avec `GetGoogleIdOption` et `setFilterByAuthorizedAccounts(false)`, et Credential Manager a répondu
`NoCredentialException` ; dans le journal du système est restée la mention `[28433]`. Une seule chose a été changée,
l'option de la requête, pour `GetSignInWithGoogleOption` : la fenêtre des comptes s'est ouverte et la connexion a
réussi.

Les deux options existent et demandent la même chose, un jeton d'identité de Google. La documentation d'Android les
sépare selon l'endroit où elles servent : `GetGoogleIdOption` est pour la *bottom sheet* de Credential Manager (le
panneau qui monte du bas de l'écran), et `GetSignInWithGoogleOption` est pour un bouton. Cette leçon fait le bouton.
D'abord tu vois ce qui voyage, ensuite tu l'écris, et à la fin tu le casses toi-même pour reconnaître cette
erreur quand tu la rencontreras.

---

## Avant de commencer

**Ce dont tu as besoin :** la [03](03-la-app-conoce-a-firebase.md) terminée, avec ton `google-services.json`
complet (son `verifica` à 18 sur 18), et l'environnement de la [00](00-ambiente-listo.md). Pour l'étape 6, un émulateur
ouvert ou ton téléphone connecté suffit ; pour l'étape 7, il faut en plus que l'image soit Google APIs et ait ton
compte ajouté (exercice 0.4). Applique ce qui est dit dans « Compte et machine de confiance » de la 00.

**Le projet de départ.** Depuis la racine du dépôt de la série :

```bash
git pull
cp lecciones/03-la-app-conoce-a-firebase/inicio/app/google-services.json lecciones/04-entrar-con-google/inicio/app/
cd lecciones/04-entrar-con-google/inicio
```

La deuxième commande amène ton fichier de la 03 dans le projet de cette leçon. Il y est aussi ignoré par git : le
projet apporte déjà son `.gitignore`. À côté se trouve `solucion/` : tu peux la lire sans rien configurer, et
**`./verifica solucion` sort au rouge dans un clone propre**, parce que sans ton fichier le projet ne compile pas
(mesuré le 18 nov. 2026 : 0 test exécuté). Tes réponses vont dans `respuestas/04.md`, à la racine du dépôt.

**Ce que le projet apporte déjà (échafaudage).** Il n'est pas évalué ici ; l'étape 2 le parcourt :

| Ce qu'il apporte | Où | Pourquoi |
|---|---|---|
| Credential Manager 1.6.0 (deux bibliothèques) et `googleid` 1.1.1, à côté du BoM de Firebase 35.2.0 de la 03 | `gradle/libs.versions.toml` et `app/build.gradle.kts` | Ce sont les pièces qui demandent le jeton. La série est testée avec ces versions |
| `Resultado`, et `Entrada` transformée en fonction qui se met en pause | `Entrada.kt` | Se connecter ne répond plus instantanément ni par un simple « oui ou non » |
| Les deux portes, avec leur implémentation réelle | `Puertas.kt` | C'est par là que ton code sort pour parler au téléphone et à Firebase |
| Les écrans, avec le bouton « Entrar con Google » et un message par cas | `Tienda.kt`, `PantallaEntrada.kt` | Ils savent déjà afficher ce que ta connexion répond |
| `MainActivity` avec `crearEntrada`, qui renvoie pour l'instant une connexion qui ne laisse passer personne | `MainActivity.kt` | C'est là que l'application construit sa connexion. Elle reçoit les deux portes pour qu'un test puisse lire quelle audience a été demandée |
| Les tests simulés et leurs doublures | `app/src/test` | Ce sont ceux que lance le `verifica`. Ne les modifie pas |
| Les tests instrumentés, avec une copie des doublures, `androidx.test:runner` 1.7.0 et Espresso 3.7.0 | `app/src/androidTest` et le catalogue | Ils s'exécutent à l'intérieur d'Android (étape 6). L'Espresso que Compose apporte par défaut n'a pas démarré sur Android 37 : 0 sur 4, avec `NoSuchMethodException` (mesuré le 18 nov. 2026) |
| Un squelette de `EntradaConGoogle` | `EntradaConGoogle.kt` | C'est le fichier que tu écris |

**Ce que cette leçon ne fait pas.** La session n'est ni conservée ni fermée (leçon 07), et la première fois n'est pas
distinguée (leçon 08).

---

## Étape 1 · Sans IA : le voyage du jeton

Cette étape est la tienne, sans Claude Code. Avant d'écrire du code, regarde ce qui voyage.

| # | De qui à qui | Ce qui voyage |
|---|---|---|
| 1 | Ton application → Credential Manager | Une demande : « je veux un jeton d'identité de Google, pour cette audience ». L'audience est ton ID client web |
| 2 | Credential Manager → la personne | La fenêtre des comptes. C'est Android qui la dessine, pas ton application |
| 3 | Google → Credential Manager → ton application | Un jeton d'identité, signé par Google, avec cette audience à l'intérieur |
| 4 | Ton application → Firebase | Le même jeton, sans y toucher |
| 5 | Firebase → ton application | Un utilisateur, avec son identifiant dans ton projet et, si le fournisseur l'a remis, son nom. Cette application affiche « cliente » s'il manque |

Ton mot de passe n'est dans aucune ligne. Tu le saisis, si besoin, dans une fenêtre de Google ; à ton
application, seul le résultat parvient.

**Ouvre un jeton.** Un jeton d'identité a trois parties séparées par des points : un en-tête, un contenu et une
signature. Le contenu n'est pas chiffré : il est encodé, ce qui n'est qu'une manière de faire passer du texte par
n'importe quel canal. Ce contenu est **inventé** pour la leçon ; Google ne l'a pas émis et il ne sert à entrer nulle
part :

```bash
printf '%s' 'eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhdWQiOiIxMDAwMDAwMDAwMDAtd2ViaW52ZW50YWRvLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMDAwMDAwMDAwMDAwMDAwMDAwMDAwIiwibmFtZSI6IkFuYSBJbnZlbnRhZGEiLCJleHAiOjE3OTUwNDY0MDB9' | base64 -d
```

Sur le Mac de la série (macOS 27), les deux formes ont fonctionné, `base64 -d` et `base64 -D` (mesuré le 18 nov. 2026).
Si ton Mac n'accepte pas `-d`, utilise `-D` ; sous Linux, c'est `-d`. L'exemple est monté pour se décoder aussi
simplement. Un jeton réel utilise une variante, Base64URL, et peut demander plus d'étapes. **Ne décode pas et ne colle
pas ici un jeton réel :** l'exercice se fait avec celui qui est inventé.

**Prédis** avant de le lancer : vas-tu pouvoir le lire sans aucune clé ? Voici ce que le Mac a répondu le 18 nov.
2026 :

```text
{"iss":"https://accounts.google.com","aud":"100000000000-webinventado.apps.googleusercontent.com","sub":"000000000000000000000","name":"Ana Inventada","exp":1795046400}
```

*(Le contenu décodé du jeton inventé : l'émetteur est Google, l'audience est un ID client web inventé, `sub` est un
identifiant de compte inventé, le nom est « Ana Inventada » (Ana inventée) et l'expiration est un horodatage.)*

Cinq données, avec les noms qu'utilise la documentation de Google :

- **`iss`**, qui l'a émis. Ce doit être Google.
- **`aud`**, l'audience : pour qui il est. Ce doit être un ID client de ton projet.
- **`sub`**, l'identifiant du compte. La documentation demande d'utiliser celui-ci, et non l'adresse e-mail, pour
  reconnaître une personne.
- **`name`**, le nom.
- **`exp`**, quand il expire.

N'importe qui peut lire un jeton. Ce que personne d'autre ne peut faire, c'est le **signer** comme Google. C'est
pourquoi celui qui le reçoit vérifie quatre choses, selon cette documentation : la signature, que `iss` soit Google,
que `aud` soit le sien et que `exp` ne soit pas passé. Dans cette série, c'est Firebase qui le fait : sa documentation
dit que tu lui passes les identifiants et que ses services les vérifient et te répondent.

**Dessine le tien.** Dans `respuestas/04.md`, ou sur papier avec une photo, dessine le voyage avec cinq flèches et
écris sur chacune ce qui voyage. Fais-le sans regarder le tableau, puis compare.

**Ce qui le prouve :** ton schéma, comparé au tableau (objectif 4.1). Aucun test ne le lit.

---

## Étape 2 · Ce que le projet apporte déjà

Quatre fichiers ont changé par rapport à la 03. Ouvre-les ; ne les modifie pas encore. Tu vas voir de nouveaux
mots de Kotlin. Pour cette leçon, trois suffisent, et les autres sont des pièces fournies :

- **`suspend` :** cette fonction attend.
- **`try` / `catch` :** transforme un problème en résultat (étape 4).
- **`sealed` :** oblige à couvrir tous les résultats.

**`Entrada.kt` : le contrat a grandi.** Dans la 02, `entrar()` renvoyait un nom ou `null`. Maintenant il dit :

```kotlin
interface Entrada {
    suspend fun entrar(): Resultado
}
```

- **`suspend`** marque une fonction qui peut se mettre en pause et reprendre plus tard sans bloquer le reste, selon la
  documentation de Kotlin. Se connecter avec Google ouvre une fenêtre, attend une personne et parle par le réseau :
  pendant ce temps, l'écran reste vivant.
- **`Resultado`** est une liste fermée (`sealed`) de quatre cas : `Entro(nombre)`, `SinCuentas`, `Cancelada` et
  `Fallo`. Un `null` ne suffisait pas à dire **pourquoi** on n'est pas entré. Comme la liste est fermée, le
  compilateur sait qu'il n'existe pas de cinquième cas.

**`Puertas.kt` : par où sort ton code.** Deux interfaces d'une seule fonction :

```kotlin
interface SelectorDeCuentas {
    suspend fun pedir(solicitud: GetCredentialRequest): GetCredentialResponse
}

interface VerificadorDeToken {
    suspend fun verificar(tokenDeIdentidad: String): String
}
```

La première demande une credential au téléphone ; sa version réelle appelle Credential Manager. La seconde remet le
jeton ; sa version réelle appelle Firebase avec `GoogleAuthProvider.getCredential` et `signInWithCredential`, qui sont
les deux appels du guide de Firebase. C'est la même idée que dans la 02 : ta classe reçoit les portes et ne sait pas
lesquelles lui sont échues. Dans l'application, ce sont les réelles ; dans les tests, deux portes factices qui vivent
dans `src/test`.

La porte du téléphone est un raccourci pédagogique : elle passe l'`Activity` directement à Credential Manager. Le guide
actuel d'Android recommande de l'envelopper dans un `MutableContextWrapper`, pour ne pas la retenir si Android recrée
l'écran alors que la fenêtre est ouverte. Selon les notes de version, la bibliothèque met à jour cette enveloppe toute
seule depuis la 1.7.0-alpha03, et la série retient la 1.6.0. Le fichier le dit dans un commentaire.

**`MainActivity.kt` : où tout s'assemble.** `onCreate` appelle `crearEntrada()` et passe le résultat à `Tienda`.
`crearEntrada` reçoit les deux portes, avec les réelles par défaut, et renvoie pour l'instant `EntradaSinProveedor()`,
celle qui ne laisse passer personne. Son corps, tu le changes à l'étape 3.

**`Tienda.kt` : qui appelle.** Le bouton n'appelle plus `entrar()` directement. Il le fait dans `alcance.launch { … }`.
`rememberCoroutineScope` donne un endroit où exécuter des tâches qui se mettent en pause, et la documentation de
Compose dit que cet endroit s'annule tout seul quand l'écran cesse d'exister. C'est là aussi que se trouve `mensajeDe`,
avec un texte par cas.

**Mesure le point de départ.** Depuis la racine du dépôt :

```bash
./lecciones/04-entrar-con-google/verifica
```

Le 18 nov. 2026, avec un fichier inventé complet, la dernière ligne a été
`Resultado: 6 de 20 pruebas en verde (14 en rojo, 0 con error, 0 saltadas)`. Les six vertes sont celles de
l'échafaudage : les écrans, leurs textes et la ressource générée par le plugin. Les quatorze rouges attendent ton
code.

*(Résultat : 6 tests sur 20 au vert, 14 au rouge, 0 avec erreur, 0 sauté.)*

---

## Étape 3 · Avec Claude Code : le chemin où tout se passe bien

C'est ici qu'entre l'outil, et il entre toujours de la même manière : **tu prédis → tu demandes → tu lis ce
qu'il a généré → tu lances le `verifica` → tu l'expliques avec tes propres mots.**

1. **Prédis.** Écris dans `respuestas/04.md` : parmi les cinq lignes du voyage, lesquelles se passent à l'intérieur
   de `EntradaConGoogle` et lesquelles derrière une porte ?
2. **Demande** à Claude Code, depuis `inicio/` : « Dans `EntradaConGoogle.kt`, écris `entrar()` seulement pour le cas
   où tout se passe bien : construis un `GetSignInWithGoogleOption` avec `idDeClienteWeb`, mets-le dans un
   `GetCredentialRequest`, demande-le avec `selector.pedir`, extrais le jeton d'identité avec
   `GoogleIdTokenCredential.createFrom` et remets-le à `verificador.verificar` ; renvoie `Resultado.Entro` avec le
   nom. Si la credential n'est pas du type Google, renvoie `Resultado.Fallo`. N'attrape pas encore les exceptions.
   Dans `MainActivity.kt`, change seulement le corps de `crearEntrada` pour qu'il renvoie `EntradaConGoogle` avec
   `getString(R.string.default_web_client_id)` et les deux portes qu'il reçoit, `selector` et `verificador` ; et
   supprime `EntradaSinProveedor` de `Entrada.kt`. Ne modifie aucun autre fichier, n'écris aucun ID client dans le
   code et n'affiche ni ne conserve le jeton. »
3. **Lis** le changement complet avant de l'accepter. Le cœur doit ressembler à ceci :

```kotlin
override suspend fun entrar(): Resultado {
    // Ligne 1 : la demande, avec l'audience.
    val opcion = GetSignInWithGoogleOption.Builder(idDeClienteWeb).build()
    val solicitud = GetCredentialRequest.Builder().addCredentialOption(opcion).build()
    // Lignes 2 et 3 : le téléphone affiche la fenêtre et renvoie ce qui a été choisi.
    val credencial = selector.pedir(solicitud).credential
    if (credencial !is CustomCredential ||
        credencial.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
        return Resultado.Fallo
    }
    val token = GoogleIdTokenCredential.createFrom(credencial.data).idToken
    // Lignes 4 et 5 : Firebase le vérifie et répond qui c'est.
    return Resultado.Entro(verificador.verificar(token))
}
```

   Six choses à vérifier en lisant :

   - **L'option est `GetSignInWithGoogleOption`.** Si `GetGoogleIdOption` apparaît, compare-la avec ce dont a besoin
     un bouton et demande le changement en expliquant la différence : c'est celle de la scène.
   - **L'audience arrive par paramètre.** Dans `EntradaConGoogle.kt`, il ne doit y avoir aucun texte qui se termine par
     `.apps.googleusercontent.com`.
   - **`crearEntrada` lit `R.string.default_web_client_id` et le passe à `EntradaConGoogle`.** Cette ressource, le
     plugin l'a générée avec le client web de ton fichier (leçon 03, étape 4). Et `onCreate` continue d'appeler
     `crearEntrada()`.
   - **Le jeton circule tel quel.** `idToken` entre dans `verificar` sans coupure ni modification.
   - **Règle sur les données : personne n'affiche, ne colle ni ne conserve le jeton.** Ni dans le journal (`Log`,
     `println`), ni dans un fichier ou les préférences, ni dans `respuestas/04.md`. Dans l'application, il va
     directement de la credential à Firebase. La documentation de Google dit que ces jetons sont sensibles et qu'on
     peut en abuser si quelqu'un les intercepte. Les jetons inventés des tests sont la seule exception, et elle est
     pédagogique : les doublures les gardent bel et bien dans une liste, parce qu'ils n'appartiennent à personne. À la
     liste de la leçon 03 de ce qui n'est jamais versé dans le dépôt (clés de signature, mots de passe, la clé de
     Microsoft), ajoute les jetons d'identité réels.
   - **Personne n'a touché les tests, `Puertas.kt` ni ton `google-services.json`.**

4. **Lance le `verifica`.** Avec ce code, il a donné `Resultado: 13 de 20 pruebas en verde (7 en rojo, …)` le 18 nov.
   2026. Les sept rouges sont les cas où l'on n'entre pas : l'étape 4.
5. **Explique**, en trois lignes de `respuestas/04.md` : ce que reçoit `verificar`, d'où cela vient et qui l'a signé.

**Pourquoi l'ID client web, et pas celui d'Android ?** Parce que le jeton n'est pas pour ton application : il est pour
celui qui va le vérifier. Le guide de Firebase l'écrit ainsi dans son exemple : l'ID client de ton serveur, et non
celui d'Android. Dans cette série, ce serveur est Firebase.

**Pourquoi cet ID n'est-il pas un mot de passe ?** Pour trois raisons que tu peux vérifier :

- Il voyage dans ton application, comme ressource. Quiconque a l'application installée peut le lire.
- Il vient de `google-services.json`, et la documentation de Firebase dit que ce fichier contient des identifiants
  uniques mais non secrets.
- Il dit seulement **pour qui** le jeton est demandé. Il ne prouve pas qui tu es et ne fait entrer personne : ce
  qui prouve quelque chose, c'est la signature de Google, et celle-là, ton application ne l'a pas.

**Ce qui le prouve, et jusqu'où.** Au niveau simulé, le `verifica` prouve que ta classe demande une seule option et
que c'est celle du bouton, que son audience est l'ID qu'elle a reçu, et que le jeton passe tel quel d'une porte à
l'autre. Et un test ouvre `MainActivity`, passe les portes factices à `crearEntrada` et lit l'audience demandée : ce doit
être le client web de ton fichier. Avec une autre ressource à sa place, ce test passe au rouge même si le bon nom reste
écrit dans le fichier (mesuré le 18 nov. 2026 : 19 sur 20).

Les portes étaient factices : rien n'a été demandé à Google ni à Firebase. Et il reste un trou qu'aucun test ne voit :
que `onCreate` passe à `Tienda` ce que renvoie `crearEntrada()`. Cela, le `verifica` ne le cherche que comme du texte, et
il l'appelle ainsi, « présence textuelle ». Avec un `onCreate` qui monte une autre connexion, les 20 tests sont au vert
et la seule chose au rouge est cette coche (mesuré le 18 nov. 2026).

---

## Étape 4 · Sans IA : les trois manières de ne pas entrer

C'est l'exercice 4.3 et il se fait **sans Claude Code**. Ce sont trois blocs `catch`, et c'est toi qui les écris.

Quand la porte du téléphone ne peut pas renvoyer de credential, elle lance une exception. La documentation d'Android
liste celles qui comptent ici :

| Exception | Quand, selon la documentation | Ta connexion répond |
|---|---|---|
| `NoCredentialException` | Aucune credential n'est disponible pour la personne : par exemple, il n'y a pas de comptes sur l'appareil ou ils doivent se réauthentifier. L'exception ne dit pas laquelle des causes c'était | `Resultado.SinCuentas` |
| `GetCredentialCancellationException` | La personne a annulé. Le même guide prévient que beaucoup d'« annulations » à la suite peuvent cacher une mauvaise configuration | `Resultado.Cancelada` |
| Toute autre | Une panne du téléphone, ou Firebase a rejeté le jeton | `Resultado.Fallo` |

**1. Prédis.** Les deux premières sont filles d'une même classe, `GetCredentialException`. Si tu mets d'abord un
`catch` général et ensuite les deux spécifiques, lequel des trois messages la personne verra-t-elle toujours ?

**2. Écris-le.** Enveloppe ce que tu as écrit à l'étape 3 avec `return try { … }` et ajoute, dans cet ordre :

```kotlin
} catch (e: NoCredentialException) {
    Resultado.SinCuentas
} catch (e: GetCredentialCancellationException) {
    Resultado.Cancelada
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Resultado.Fallo
}
```

Le troisième n'est pas un cas de la connexion. `CancellationException` (de `kotlinx.coroutines`) est Kotlin qui signale
que la tâche a été annulée, par exemple parce que l'écran s'est fermé. La documentation de Kotlin demande que, si tu
l'attrapes, tu la relances. Comme le `catch` général du dessous l'attraperait aussi, il va avant et la laisse passer.

**3. Lance le `verifica`.** Maintenant la ligne est `Resultado: 20 de 20 pruebas en verde` (résultat : 20 tests sur 20
au vert). Il reste une coche au rouge, celle de `respuestas/04.md` : elle se remplit à l'étape 5.

**4. Vérifie ta prédiction.** Déplace le `catch (e: Exception)` au-dessus des autres et lance le `verifica` : le
18 nov. 2026, il a donné 16 sur 20, avec « sin cuentas » (sans comptes) et « cancelación » (annulation) au rouge.
Remets-le à sa place.

Les textes que voit la personne sont déjà dans `mensajeDe`, dans `Tienda.kt` :

| Résultat | Message |
|---|---|
| `SinCuentas` | « No encontré una cuenta de Google disponible para entrar. Agrega una o vuelve a iniciar sesión en Google y prueba otra vez. » (Je n'ai pas trouvé de compte Google disponible pour entrer. Ajoutes-en un ou reconnecte-toi à Google, puis réessaye.) |
| `Cancelada` | « Cerraste la ventana sin elegir cuenta. Toca el botón cuando quieras intentar otra vez. » (Tu as fermé la fenêtre sans choisir de compte. Touche le bouton quand tu voudras réessayer.) |
| `Fallo` | « No se pudo entrar. Revisa tu conexión y vuelve a intentar. » (Impossible d'entrer. Vérifie ta connexion et réessaye.) |

**Ce qui le prouve :** le `verifica` simulé, les trois cas (objectif 4.3). Chaque cas comporte des tests de deux types :
les uns lancent l'exception à ta classe et lisent ce qu'elle a répondu ; un autre dessine l'écran avec ta classe et
cherche son message. Les exceptions ont été lancées par une doublure : aucune ne venait d'un téléphone.

---

## Étape 5 · Sans IA : casses-en un exprès

Aussi **sans Claude Code**. Tu vas provoquer l'erreur de la scène.

**1. Prédis.** Tu vas remplacer l'option du bouton par `GetGoogleIdOption`, celle de la *bottom sheet* de
Credential Manager. Ce n'est pas « celle de la connexion automatique » : selon le guide d'Android, celle-ci n'a lieu que
si l'on active en plus `setAutoSelectEnabled(true)` et que ses conditions sont remplies, et ici on ne l'active pas. Est-ce
que ça compile ? Combien de tests passent au rouge, et lesquels ?

**2. Casse-le et mesure.** Dans `EntradaConGoogle.kt`, remplace la ligne de l'option par celle-ci (et son `import`) :

```kotlin
val opcion = GetGoogleIdOption.Builder()
    .setServerClientId(idDeClienteWeb)
    .setFilterByAuthorizedAccounts(false)
    .build()
```

Lance le `verifica`. Voici ce que cela a donné le 18 nov. 2026 (abrégé) :

```text
❌ Algún .kt de la app nombra GetGoogleIdOption: es la opción de la hoja de Credential Manager, no la del …
❌ Tu entrada pidió otra opción: la solicitud llevó GetGoogleIdOption y debe llevar GetSignInWithGoogleOption
❌ La audiencia de la solicitud no es el ID de cliente que recibió EntradaConGoogle (o la opción no es …
❌ La Entrada que arma MainActivity.crearEntrada no pide el token con el cliente web de tu archivo como …
Resultado: 17 de 20 pruebas en verde (3 en rojo, 0 con error, 0 saltadas), leído de …
```

*(En français, les coches disent : un `.kt` de l'application nomme `GetGoogleIdOption`, qui est l'option de la *bottom
sheet* de Credential Manager, pas celle du bouton ; ta connexion a demandé une autre option : la requête portait
`GetGoogleIdOption` et doit porter `GetSignInWithGoogleOption` ; l'audience de la requête n'est pas l'ID client que
`EntradaConGoogle` a reçu (ou l'option n'est pas …) ; la connexion que monte `MainActivity.crearEntrada` ne demande pas
le jeton avec le client web de ton fichier comme …. Résultat : 17 tests sur 20 au vert, 3 au rouge.)*

Copie dans `respuestas/04.md` la ligne qui dit « Tu entrada pidió otra opción » (Ta connexion a demandé une autre
option).

**3. Ce que ces tests ne te disent pas.** Ça compile, et 17 sur 20 restent au vert : le jeton inventé passe de la même
manière par les deux portes factices. Les tests savent seulement que tu as demandé **une autre** option. Ce que
cette option fait sur un téléphone a été mesuré à part :

- Le 13 nov. 2026, dans l'émulateur avec un compte ajouté : `NoCredentialException` et `[28433]` dans le journal.
- Le 18 nov. 2026, dans un émulateur neuf **sans** compte (Pixel 9, image `google_apis_playstore` d'Android 37, avec un
  fichier inventé) : l'application a affiché le message de « sans comptes » et le journal a dit `[28433]`. Avec l'option
  du bouton, dans ce même émulateur, Android a ouvert l'écran de Google pour ajouter un compte ; au retour avec le bouton « retour » (« atrás »), l'application a affiché le message de
  l'annulation.

Ce sont des observations de ces deux jours, avec ces versions ; pas une règle que Google publie.

**4. Répare-le.** Remets la ligne à `GetSignInWithGoogleOption.Builder(idDeClienteWeb).build()` et son `import`.
Lance le `verifica` : 20 sur 20, et maintenant aussi la coche de `respuestas/04.md`.

Le vert confirme ce que vérifient ces tests et que la ligne est dans ton fichier ; ton explication complète la
preuve. L'exercice est terminé quand tu peux cocher les trois cases :

- [ ] Ta prédiction du point 1 est écrite, avec ce que tu as vu en dessous.
- [ ] Tu as collé la ligne du rouge.
- [ ] Tu as dit pourquoi 17 tests sont restés au vert avec un code qui, sur le téléphone, ne laisse pas entrer.

**Ce qui le prouve :** « Casses-en un exprès ». C'est une base de l'objectif 4.2 : savoir pourquoi le bouton porte cette
option.

---

## Étape 6 · À l'intérieur d'Android : le niveau instrumenté

Les tests des étapes précédentes s'exécutent sur ton ordinateur, avec une imitation d'Android. Ceux-ci s'exécutent
**à l'intérieur** d'Android, dans ton émulateur ou sur ton téléphone. Avec l'émulateur ouvert, depuis la racine du
dépôt :

```bash
./lecciones/04-entrar-con-google/verifica inicio instrumentado
```

Il répète le niveau simulé, puis installe l'application et son application de tests sur l'appareil. Là, il dessine
`Tienda` avec ta `EntradaConGoogle` et les deux mêmes portes factices, touche les boutons et cherche les textes. Avec
la solution, dans un émulateur neuf et sans compte (Pixel 9, image `google_apis_playstore` d'Android 37), cela a donné le
18 nov. 2026 (abrégé) :

```text
✅ Instrumentado: dentro de Android, con las puertas de mentira, tocar «Entrar con Google» lleva al inicio …
✅ Instrumentado: dentro de Android, cada uno de los tres casos muestra su mensaje sin pasar al inicio (3 de 3)
Resultado: 20 de 20 pruebas en verde (0 en rojo, 0 con error, 0 saltadas), leído de …
Instrumentado: 4 de 4 pruebas en verde (0 en rojo, 0 con error, 0 saltadas), leído de …
```

*(Instrumenté : à l'intérieur d'Android, avec les portes factices, toucher « Entrar con Google » mène à l'accueil ;
à l'intérieur d'Android, chacun des trois cas affiche son message sans passer à l'accueil (3 sur 3). Résultat : 20
tests sur 20 au vert. Instrumenté : 4 sur 4 au vert.)*

Sur le projet de départ non modifié, il a donné 0 sur 4, et sans aucun appareil connecté le `verifica` sort au rouge et
dit qu'il ne s'est pas exécuté.

**Ce qui le prouve, et jusqu'où.** Il prouve que les écrans, le toucher et ta classe fonctionnent à l'intérieur
d'Android, avec les bibliothèques de Credential Manager et de `googleid` chargées sur l'appareil. Il ne prouve **rien** de
ton projet Firebase : les portes restent factices, la fenêtre des comptes ne s'ouvre pas et il ne faut ni compte ni
réseau vers Google. C'est pourquoi il a réussi sur un émulateur sans compte.

---

## Étape 7 · Sur le téléphone : le niveau réel

Jusqu'ici, rien n'a parlé à Google. Cette étape le fait, une fois, à la main. C'est la seule qui vérifie ce que la leçon
03 a laissé écrit dans ton fichier : que l'empreinte est enregistrée, que Google est activé et que l'ID client est
celui de ton projet.

Avec l'émulateur ouvert et ton compte ajouté, depuis `inicio/` :

```bash
./gradlew installDebug
```

Ouvre « La tienda » (La boutique), touche « Entrar » (Entrer) puis « Entrar con Google ».

**Ce qui a été vu lors de l'essai du 13 nov. 2026** (Mac, émulateur avec l'image `google_apis_playstore` et un compte
ajouté) : la fenêtre des comptes d'Android s'est ouverte, le compte a été choisi et l'application a salué avec le nom de
ce compte. Cet essai a utilisé un code de test avec les mêmes appels, pas celui de `solucion/` : la classe
`VerificadorConFirebase` de cette leçon compile, mais nous ne l'avons pas encore exécutée contre un vrai projet Firebase.
Si, en l'essayant, tu vois autre chose que ce que décrit cette étape, dis-nous ce qui est apparu.

**Si ton compte utilise une clé d'accès (passkey).** Lors de l'essai, Google a demandé de scanner un code QR avec le
téléphone, et cela n'a pas abouti dans l'émulateur. On a touché **« Try another way »** et l'on est entré avec le mot de
passe ou avec la notification sur le téléphone (leçon 00, exercice 0.4).

**La boîte de permissions.** Selon l'aide de Google, en choisissant « Sign in with Google » tu acceptes de partager
avec cette application trois données : nom, adresse e-mail et photo de profil. Avec elles, l'application peut créer ton
compte. Le texte exact de la boîte, nous ne l'avons pas capturé lors de l'essai ; si la tienne dit autre chose, dis-nous
ce qu'elle dit.

C'est l'exercice 4.4. Classe chaque donnée dans `respuestas/04.md` avant de regarder le tableau de « Compare tes
réponses » : **ce que c'est, pourquoi elle est demandée et si ton application la reçoit.**

1. Ton nom.
2. Ton adresse e-mail.
3. Ta photo de profil.
4. Ton mot de passe Google.

**Pour terminer, sur une machine qui n'est pas la tienne :** retire ton compte de l'émulateur et efface ses données
(leçon 00).

**Ce qui le prouve :** la connexion réelle, avec ton compte, prouve que l'empreinte, l'ID client et le fournisseur sont
bien configurés. Elle ne prouve pas les trois cas où l'on n'entre pas : ceux-là, le niveau simulé les couvre.

---

## Tes exercices

Les étapes 1, 4 et 5 se font **sans IA**.

| # | Tu fais | C'est fait quand |
|---|---|---|
| 4.1 | Tu dessines le voyage avec cinq flèches et ce qui voyage sur chacune | Ton schéma coïncide avec le tableau de l'étape 1. Tu le compares toi-même |
| 4.2 | Tu implémentes `EntradaConGoogle` avec l'option du bouton et l'ID client web, et tu expliques pourquoi cet ID n'est pas un mot de passe | Trois preuves. Simulé : le `verifica` donne 20 sur 20, ce qui valide ce que demande ta classe et où elle emmène le jeton, avec des portes factices ; ses coches « Texto » sont de la présence textuelle. Instrumenté : `./verifica inicio instrumentado` donne 4 sur 4, ce qui valide la même chose à l'intérieur d'Android. Réel : tu entres avec ton compte à l'étape 7, ce qui est la seule chose qui valide ton projet. Ton explication est dans `respuestas/04.md` |
| 4.3 | Tu gères sans comptes, annulation et erreur, chacun avec son message | Les trois coches de cas du `verifica` sont au vert. Les exceptions sont lancées par une doublure |
| 4.4 | Tu classes les quatre données de l'étape 7 | 4 sur 4 contre le tableau de « Compare tes réponses » |

**Durée :** compte environ trois heures et demie, avec le « Casses-en un exprès », le niveau instrumenté et la connexion
réelle. C'est une estimation : nous l'ajusterons avec le temps que prendra la première cohorte.

---

## Ça ne tourne pas ?

| Tu vois | Cause probable | Ce que tu fais |
|---|---|---|
| `File google-services.json is missing.` | Tu n'as pas copié ton fichier dans `inicio/app/` | Le `cp` de « Avant de commencer ». Sur `solucion/`, c'est ce qui est attendu dans un clone propre |
| `Unresolved reference 'default_web_client_id'` | Ton fichier ne contient pas de client web (type 3), et le plugin n'a pas généré la ressource. Mesuré le 18 nov. 2026 avec un fichier inventé sans clients | Télécharge-le à nouveau après l'empreinte et l'activation de Google (leçon 03, étape 3) |
| ❌ « sin cuentas » et « cancelación » au rouge, avec les `catch` écrits | Le `catch (e: Exception)` est avant les spécifiques | Étape 4 : les spécifiques vont en premier |
| ❌ « Tu entrada pidió otra opción » (ta connexion a demandé une autre option) | `GetGoogleIdOption` est resté | Étape 5, point 4 |
| Sur le téléphone, « No encontré una cuenta de Google disponible… » avec le compte ajouté | Lors de l'essai du 13 nov. 2026, l'option était `GetGoogleIdOption`. Selon le guide, cela arrive aussi si le compte doit se reconnecter | Vérifie que l'option est `GetSignInWithGoogleOption`, et que ton compte est toujours connecté dans Settings |
| ❌ « Instrumentado: no hay ningún emulador abierto ni teléfono conectado » (aucun émulateur ouvert ni téléphone connecté) | `adb devices` ne liste aucun appareil à l'état `device` | Ouvre l'émulateur (leçon 00) et attends la fin du démarrage |
| Dans les tests instrumentés, `NoSuchMethodException: android.hardware.input.InputManager.getInstance` | La ligne d'Espresso 3.7.0 a été retirée de l'échafaudage et c'est celui que Compose apporte par défaut qui est resté (mesuré le 18 nov. 2026 sur Android 37) | Remets `androidTestImplementation(libs.espresso.core)` |
| Sur le téléphone, un écran de Google s'ouvre pour se connecter | L'émulateur n'a aucun compte (mesuré le 18 nov. 2026) | Ajoute ton compte là, ou dans Settings (leçon 00, exercice 0.4) |
| Google demande de scanner un code QR | Ton compte utilise une clé d'accès | « Try another way » |
| Sur le téléphone, « No se pudo entrar… » (Impossible d'entrer…) | Avec un vrai projet, nous n'avons pas encore mesuré laquelle des causes le produit. Les trois plus probables : l'empreinte non enregistrée, Google non activé dans Firebase ou l'émulateur sans réseau | Revois la leçon 03, étape 3, et vérifie que l'émulateur a du réseau |
| `GetCredentialProviderConfigurationException` | Selon le guide d'Android, la bibliothèque `credentials-play-services-auth` manque. Non mesuré ici | Vérifie que `app/build.gradle.kts` contient toujours les trois lignes de l'échafaudage |
| Tu as changé d'ordinateur et tu n'as pas l'empreinte de débogage de celui-ci | Sans `google-services.json`, le projet ne compile pas, et le keystore de débogage est créé par la première compilation : un cercle (mesuré sur Linux Mint 22.3, 18 nov. 2026) | Copie dans `inicio/app/` ton fichier de la 03 même s'il contient l'empreinte de l'autre ordinateur, lance `./gradlew assembleDebug` (il compile quand même), obtiens l'empreinte avec `./gradlew signingReport`, enregistre-la dans Firebase et télécharge à nouveau ton fichier |
| ❌ « EntradaConGoogle.kt nombra una puerta concreta » (`EntradaConGoogle.kt` nomme une porte concrète) | Ton code nomme la vraie porte (`VerificadorConFirebase`, `SelectorConCredentialManager`) ou une doublure des tests. Les tests ne lui passent que des doublures : si ton code demande laquelle lui est échue, le vert ne dit pas ce qu'il fait avec la vraie | Que ton code ne parle qu'à `SelectorDeCuentas` et `VerificadorDeToken`. La vraie porte, c'est `MainActivity.kt` qui la choisit, et il est fourni (étape 3) |

Sur Linux Mint 22.3 (x86_64), le `verifica` de cette leçon a donné le même résultat que sur le Mac (18 nov. 2026), au
niveau simulé et avec un fichier de test qui contenait l'empreinte de cet ordinateur : sur `inicio/`, 6 sur 20 au vert ;
sur `solucion/`, 20 sur 20. Sans fichier, « 0 pruebas corridas » (0 test exécuté), comme sur le Mac. Le niveau
instrumenté n'a pas été mesuré sous Linux. Les dépendances de cette leçon ont ajouté environ 300 Mo à `~/.gradle`.

---

## Pour conclure

**Réponds sans relire :**

1. Qu'est-ce qu'un jeton d'identité, qui le signe et qui le vérifie dans cette série ?
2. Que dit l'audience, et d'où ton application tire-t-elle la valeur qu'elle y met ?
3. Ton `verifica` donne 20 sur 20. Dis une chose que cela prouve et une qu'il ne prouve pas.
4. Pourquoi le `catch` général va-t-il à la fin ?

**La suite :** dans la L05, tu ajoutes un second fournisseur, Microsoft, sans changer les écrans.

---

## Compare tes réponses

**Étape 1.** Oui, on peut le lire sans clé : le contenu d'un jeton n'est qu'encodé. Ce qui protège le jeton, c'est la
signature, pas un chiffrement. Ton schéma doit avoir les cinq flèches du tableau, avec l'audience sur la première, le
jeton sur la troisième et sur la quatrième, et aucune flèche portant ton mot de passe vers l'application.

**Étape 3.** À l'intérieur de `EntradaConGoogle`, on construit la demande (une partie de la ligne 1) et l'on passe le
jeton d'une porte à l'autre. Derrière la porte du téléphone, les lignes 2 et 3 ; derrière celle du vérificateur, la 4 et
la 5. `verificar` reçoit le jeton d'identité, qui est sorti de la credential remise par le téléphone et qu'a signé
Google.

**Étape 4.** Toujours celui de `Fallo` : le `catch` général attrape aussi les deux filles, et les spécifiques du dessous
ne sont jamais atteints.

**Étape 5.** Ça compile, et trois tests passent au rouge : celui qui vérifie l'option et les deux qui lui lisent
l'audience, à ta classe et à la connexion que monte l'application. Les 17 autres restent au vert parce qu'ils
utilisent des portes factices, qui remettent le jeton inventé quelle que soit l'option demandée. C'est pourquoi le
niveau simulé ne suffit pas pour dire « sur le téléphone, on entre ».

**Exercice 4.4.**

| Donnée | Ce que c'est | Pourquoi elle est demandée | L'application la reçoit-elle ? |
|---|---|---|---|
| Nom | Le nom de ton compte Google | Pour créer ton compte dans l'application | Oui, dans ce flux Google, si le compte en a un. Il arrive par Firebase et se voit dans « Hola, … » ; s'il manque, l'application dit « cliente » |
| Adresse e-mail | L'adresse de ton compte | Pour créer ton compte dans l'application | Oui : la credential la contient, et l'utilisateur Firebase a une adresse e-mail principale. Cette application ne l'affiche pas |
| Photo de profil | L'adresse de ta photo | Pour créer ton compte dans l'application | Oui : la credential contient son adresse. Cette application ne l'utilise pas |
| Mot de passe | La clé de ton compte Google | Elle n'est pas demandée pour l'application | Non. L'aide de Google ne la liste pas parmi ce qui est partagé, et la credential que reçoit l'application n'a pas de champ pour elle |

**Pour conclure :**

1. Un texte signé par Google qui dit qui tu es et pour qui il a été émis. Google le signe et Firebase le vérifie ;
   ton application ne fait que le porter.
2. Pour qui est le jeton. Ton application le tire de la ressource `default_web_client_id`, que le plugin a générée avec
   le client web de ton `google-services.json`.
3. Il prouve ce que demande ta classe (l'option et l'audience), que le jeton passe d'une porte à l'autre, et quel
   résultat et quel message donne chaque cas. Il ne prouve pas que ton projet Firebase est bien configuré ni que
   quelqu'un puisse entrer : les portes étaient factices.
4. Parce qu'il attrape tout, y compris les exceptions spécifiques. S'il va en premier, les autres ne s'exécutent jamais.

---

## Sources de la leçon

Ouvertes le 18 nov. 2026.

- Android Developers. *Implement Sign in with Google* (le flux du bouton avec `GetSignInWithGoogleOption` ; celui de la *bottom sheet* avec `GetGoogleIdOption` ; les conditions de la connexion automatique ; le `MutableContextWrapper` ; `GoogleIdTokenCredential.createFrom` ; valider le jeton sur le serveur). https://developer.android.com/identity/sign-in/credential-manager-siwg-implementation
- Android Developers. *Sign in with Google overview* (pourquoi un bouton en plus de la *bottom sheet* ; avec le bouton, on peut ajouter un compte). https://developer.android.com/identity/sign-in/credential-manager-siwg
- Android Developers. *Troubleshoot Credential Manager* (`NoCredentialException`, `GetCredentialCancellationException`, `GetCredentialProviderConfigurationException`). https://developer.android.com/identity/sign-in/credential-manager-troubleshooting-guide
- Android Developers. *androidx.credentials.exceptions* (la description de chaque exception). https://developer.android.com/reference/kotlin/androidx/credentials/exceptions/package-summary
- Android Developers. *Credentials*, notes de version (la 1.6.0 est la stable, du 8 avr. 2026 ; le changement automatique de contexte est arrivé dans la 1.7.0-alpha03). https://developer.android.com/jetpack/androidx/releases/credentials
- Android Developers. *Side-effects in Compose* (`rememberCoroutineScope`). https://developer.android.com/develop/ui/compose/side-effects
- Android Developers. Guide des tests de Compose (les dépendances des tests et `app/src/androidTest`). https://developer.android.com/develop/ui/compose/testing
- Google for Developers. *OpenID Connect* (les jetons d'identité sont sensibles ; on les valide avant d'utiliser leurs données ; leurs valeurs sont en Base64URL). https://developers.google.com/identity/openid-connect/openid-connect
- Google for Developers. *GoogleIdTokenCredential* (les données que contient la credential). https://developers.google.com/identity/android-credential-manager/android/reference/com/google/android/libraries/identity/googleid/GoogleIdTokenCredential
- Google for Developers. *Verify the Google ID token on your server side* (signature, `aud`, `iss`, `exp` ; utiliser `sub` et non l'adresse e-mail). https://developers.google.com/identity/gsi/web/guides/verify-google-id-token
- Aide des comptes Google. *Sign in with Google* (ce qui est partagé : nom, adresse e-mail et photo de profil). https://support.google.com/accounts/answer/12849458
- Firebase. *Authenticate with Google on Android* (les bibliothèques, `default_web_client_id`, `GoogleAuthProvider.getCredential` et `signInWithCredential` ; son exemple utilise `GetGoogleIdOption` et Credential Manager 1.3.0). https://firebase.google.com/docs/auth/android/google-signin
- Firebase. *Firebase Authentication* (tu lui passes les identifiants et ses services les vérifient). https://firebase.google.com/docs/auth
- Firebase. *Users in Firebase projects* (identifiant, adresse e-mail, nom et photo de l'utilisateur). https://firebase.google.com/docs/auth/users
- Kotlin. *Coroutines basics* (`suspend`, `launch`), *Cancellation and timeouts* (relancer `CancellationException`) et *Sealed classes and interfaces*. https://kotlinlang.org/docs/coroutines-basics.html · https://kotlinlang.org/docs/coroutines-cancellation.html · https://kotlinlang.org/docs/sealed-classes.html
- Dépôt Maven de Google : ses index listent `credentials` 1.6.0, `googleid` 1.1.1 et `firebase-bom` 35.2.0. Il existe une version plus récente de `googleid` ; la série retient la 1.1.1, qui est celle du guide de Firebase et celle de l'essai. Les mêmes index listent `androidx.test:runner` 1.7.0 et `espresso-core` 3.7.0 comme les stables les plus récentes. https://dl.google.com/dl/android/maven2/androidx/credentials/credentials/maven-metadata.xml · https://dl.google.com/dl/android/maven2/com/google/android/libraries/identity/googleid/googleid/maven-metadata.xml · https://dl.google.com/dl/android/maven2/com/google/firebase/firebase-bom/maven-metadata.xml · https://dl.google.com/dl/android/maven2/androidx/test/runner/maven-metadata.xml · https://dl.google.com/dl/android/maven2/androidx/test/espresso/espresso-core/maven-metadata.xml
- Essai de la série, 13 nov. 2026, Mac avec Apple silicon : la connexion réelle avec un compte ; `GetGoogleIdOption` avec `NoCredentialException` et `[28433]` ; la clé d'accès et « Try another way ».
- Nos propres mesures : Mac avec Apple silicon, 18 nov. 2026. Le `verifica` sur `inicio/` et `solucion/`, chaque étape de la leçon et chaque sabotage, avec des fichiers inventés et sans toucher à la console Firebase ni à aucun compte. Et l'application dans un émulateur neuf, sans compte, qui a été supprimé à la fin : les deux options, et l'annulation avec « retour ». Les tests instrumentés ont été mesurés dans un autre émulateur neuf, sans compte, lui aussi supprimé.

Revue externe (18 nov. 2026) : AVEC CHANGEMENTS, 3 de priorité haute, 5 moyenne et 2 basse ; appliquée.
