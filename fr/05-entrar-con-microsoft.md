# Leçon 05 — Se connecter avec Microsoft

**Avant de lire, cinq mots.** Une ligne chacun ; les autres arrivent à l'étape où ils servent.

- **Microsoft Entra :** le service de Microsoft où l'on enregistre les applications qui permettent de se connecter avec
  un compte Microsoft. Sa console est sur `entra.microsoft.com`.
- **Répertoire :** l'espace d'Entra qui appartient à une personne ou à une organisation. Une application s'enregistre
  dans un répertoire. La documentation en anglais l'appelle *tenant*.
- **Adresse de redirection :** l'adresse web vers laquelle Microsoft renvoie la personne quand elle a fini de se
  connecter. La documentation l'appelle *redirect URI*.
- **Clé client :** un mot de passe de l'application, pas d'une personne. Avec elle, celui qui la détient s'identifie
  auprès de Microsoft comme étant cette application. La documentation l'appelle *client secret*.
- **Résultat en attente :** la réponse d'une connexion qui s'est terminée alors que ton application n'était plus là
  pour la recevoir.

**Ce que tu en retires :** le bouton « Se connecter avec Microsoft » (« Entrar con Microsoft ») à côté de celui de
Google, écrit par toi, qui récupère la connexion même si Android a fermé ton application en cours de route ; ton
application enregistrée dans Entra sans que sa clé touche un seul fichier ; et un tableau à toi qui dit ce qui est
pareil et ce qui change entre les deux fournisseurs.

**Idée nouvelle de cette leçon :** un second fournisseur qui s'enregistre en dehors de Firebase, avec une adresse de
redirection exacte et une clé qui ne touche jamais l'application. Le reste —les bibliothèques, le navigateur, la
troisième « porte » et le second bouton sur l'écran— est fourni et expliqué, et n'est pas évalué ici.

**Une note sur les textes de l'application.** Comme dans les leçons précédentes, le projet livre ses textes d'écran en
espagnol, et les `verifica` cherchent ces textes exacts. Dans les blocs de code, les sorties de terminal et les
tableaux de messages, ils restent tels que le projet les contient ; dans la prose, la leçon les cite en français avec
l'original entre parenthèses la première fois : le bouton « Se connecter avec Microsoft » (« Entrar con Microsoft »),
l'écran « La boutique » (« La tienda ») et son bouton « Entrer » (« Entrar »).

---

## La scène

La page de Microsoft est ouverte sur le téléphone. La personne n'a pas encore choisi son compte. Derrière cette page,
ton application n'existe plus : Android l'a fermée.

La page est toujours là, en attente.

Cela a été provoqué exprès lors de l'essai de cette série, le 13 novembre 2026, dans un émulateur. Avec la page de
Microsoft au premier plan, on a fermé le processus de l'application depuis le terminal, comme le ferait Android s'il
avait besoin de mémoire. Ensuite, on a choisi le compte. Android a relancé l'application de zéro, sans rien de ce
qu'elle avait en mémoire.

La personne était déjà entrée. La question est de savoir si l'application s'en rend compte, ou si elle lui demande de
se connecter une seconde fois.

Lors de l'essai, elle s'en est rendu compte, parce qu'au démarrage elle posait une question avant toute autre : y
a-t-il une connexion restée à moitié faite ? Cette question tient en une ligne, et cette leçon l'écrit. Elle écrit aussi
ce qui vient avant : enregistrer ton application auprès de Microsoft, ce qui ne se fait pas dans Firebase, et prendre
soin d'une clé qui ne s'affiche qu'une seule fois.

---

## Avant de commencer

**Ce dont tu as besoin :** la [04](04-entrar-con-google.md) terminée (son `verifica` à 20 sur 20) et l'environnement
de la [00](00-ambiente-listo.md). Pour l'étape 7, un émulateur ouvert ou ton téléphone connecté suffit. Applique ce
qui est dit dans « Compte et machine de confiance » de la 00.

**Pour les étapes 8 et 9, en plus :** un compte Microsoft pour se connecter et un répertoire Entra où tu peux
enregistrer une application. Ce sont deux choses différentes, et la seconde est celle qui coûte :

- Le guide de Microsoft demande un compte Azure avec un abonnement actif et le rôle *Application Developer*.
- Lors de l'essai du 13 nov. 2026, un compte personnel qui **avait déjà un répertoire** a enregistré l'application par
  `entra.microsoft.com`, sans abonnement et sans carte.
- Ce jour-là, un compte **tout juste créé** n'a pas pu : « Nuevo registro » (Nouvel enregistrement) a ouvert un panneau
  sans formulaire. Il s'est ouvert après l'activation de l'essai gratuit d'Azure, qui a demandé un téléphone vérifié,
  une adresse de facturation et une carte.
- Si tu es étudiant, regarde si tu remplis les conditions d'Azure for Students. Sa page dit qu'elle ne demande
  pas de carte, qu'elle vérifie avec l'adresse e-mail de ton école et qu'elle ne s'applique pas aux cours en ligne
  ouverts à tous ni aux formations d'organisations à but lucratif. Cette leçon n'affirme pas que tu y as droit.

**Si tu n'as pas de répertoire,** tu fais en entier les étapes 1 à 7, qui ne demandent aucun compte. Les étapes
8 et 9 attendent que tu en aies un.

**Le projet de départ.** Depuis la racine du dépôt de la série :

```bash
git pull
cp lecciones/03-la-app-conoce-a-firebase/inicio/app/google-services.json lecciones/05-entrar-con-microsoft/inicio/app/
cd lecciones/05-entrar-con-microsoft/inicio
```

La deuxième commande amène ton fichier de la 03 dans le projet de cette leçon. Il y est aussi ignoré par git. À côté
se trouve `solucion/` : tu peux la lire sans rien configurer, et **`./verifica solucion` sort au rouge dans un clone
propre**, parce que sans ton fichier le projet ne compile pas (mesuré le 18 nov. 2026 : 0 test exécuté). Tes réponses
vont dans `respuestas/05.md`, à la racine du dépôt.

**Ce que le projet apporte déjà (échafaudage).** Il n'est pas évalué ici ; l'étape 3 le parcourt :

| Ce qu'il apporte | Où | Pourquoi |
|---|---|---|
| Tout ce que la 04 a laissé : `EntradaConGoogle`, ses deux portes, ses tests et ses doublures | Les mêmes fichiers, sans un seul changement | Le `verifica` les compare, octet par octet, avec la solution de la 04 |
| Un cinquième résultat, `CuentaConOtroProveedor`, et l'interface `EntradaQueSeRetoma` | `Entrada.kt` | Avec Microsoft, il y a une nouvelle manière de ne pas entrer, et une connexion qui peut rester à moitié faite |
| La troisième porte, avec son implémentation réelle | `PuertaDelNavegador.kt` | C'est par là que ton code sort vers le navigateur et vers Firebase |
| Le second bouton et le message de la collision | `PantallaEntrada.kt`, `Tienda.kt` | Ces deux-là **ont** changé par rapport à la 04. Ils sont fournis : tu ne touches aucun écran |
| `MainActivity`, qui assemble les pièces et les passe à `Tienda` | `MainActivity.kt` | Fourni et non modifiable : le `verifica` le compare avec celui de la leçon |
| L'endroit où se décide quelle pièce est assemblée ; pour l'instant, une qui ne fait rien | `Armado.kt` | C'est le fichier où tu branches ce que tu écris |
| Les tests de cette leçon, leurs doublures et une « application de tests » | `app/src/test`, `app/src/androidTest` | Ce sont ceux que lance le `verifica`. Ne les modifie pas |
| Un squelette de `EntradaConMicrosoft` | `EntradaConMicrosoft.kt` | C'est le fichier que tu écris |

Le catalogue de versions est celui de la 04, sans nouvelle bibliothèque : Microsoft est inclus dans Firebase
Authentication.

**Ce que cette leçon ne fait pas.** Elle ne lie pas Google et Microsoft à un même utilisateur : ici, l'application
reconnaît la collision et dit avec quel bouton se connecter ; lier, c'est la [06](06-dos-botones-una-cuenta.md). Elle ne
conserve ni ne ferme la session (leçon 07), et ne distingue pas la première fois (leçon 08).

---

## Étape 1 · Sans IA : le même voyage, par un autre chemin

Cette étape est la tienne, sans Claude Code. C'est l'exercice 5.3.

Dans la 04, ton application a demandé un jeton au téléphone et l'a porté à Firebase. Avec Microsoft, ton
application ne porte rien : elle ouvre le navigateur et attend.

| # | De qui à qui | Ce qui voyage |
|---|---|---|
| 1 | Ton application → Firebase (sa bibliothèque) | Une demande : « je veux me connecter avec le fournisseur `microsoft.com` » |
| 2 | Firebase → le navigateur | Ouvre la page de Microsoft, avec l'ID client de ton enregistrement et ton adresse de redirection |
| 3 | La personne → Microsoft | Son compte et ce que Microsoft lui demande. Cela se passe dans le navigateur, en dehors de ton application |
| 4 | Microsoft → Firebase (ses serveurs) | Un code à usage unique, envoyé à ton adresse de redirection |
| 5 | Firebase → Microsoft | Ce code, avec l'ID client **et la clé** que tu as gardés dans la console de Firebase |
| 6 | Firebase → ton application | Un utilisateur, avec son identifiant dans ton projet et, si le fournisseur l'a remis, son nom |

La ligne 5 est celle qui n'existait pas avec Google. Le guide de Firebase le dit ainsi : Firebase gère tout le flux et
l'échange du code en utilisant l'ID client et la clé configurés dans sa console. Et la documentation de Microsoft
explique pourquoi cette clé ne va pas dans l'application : elle demande de ne pas l'utiliser dans une application
native, parce que sur un appareil on ne peut pas la conserver de façon fiable.

C'est pourquoi la ligne 2 exige de l'exactitude. La documentation de Microsoft dit que son serveur n'envoie la personne
qu'à une adresse ajoutée à l'enregistrement de l'application, et que l'adresse de la demande doit coïncider exactement
avec l'une de celles enregistrées. Si elle ne coïncide pas, il répond par une erreur.

**Prédis** avant de continuer, dans `respuestas/05.md` : sur les six lignes, dans lesquelles le code que tu vas
écrire intervient-il ?

**Fais ton tableau.** Dans `respuestas/05.md`, compare les deux fournisseurs avec ces cinq questions. Remplis-le
sans regarder « Compare tes réponses », puis compare :

1. Qui enregistre l'application auprès du fournisseur, et dans quelle console ?
2. Quelle pièce montre à la personne où se connecter ?
3. Que reçoit ton application du fournisseur ?
4. Y a-t-il une clé ? Où vit-elle ?
5. Qu'est-ce qui doit coïncider exactement pour que cela fonctionne ?

**Ce qui le prouve :** ton tableau, comparé à celui de la leçon (objectif 5.3). Aucun test ne le lit.

---

## Étape 2 · La règle sur les données, avant de toucher à quoi que ce soit

Dans la 04, la règle était : personne n'affiche, ne colle ni ne conserve le jeton. Ici, elle s'étend à trois choses de
plus.

- **La clé client naît dans Entra et se colle directement dans Firebase.** Elle ne passe pas par le code, un fichier,
  `respuestas/05.md`, le terminal, une capture ni un message. Firebase l'utilise du côté de son service, jamais sur le
  téléphone. On n'en note que la **date d'expiration**.
- **Le jeton d'accès de Microsoft ne se lit pas.** Le résultat de Firebase en contient un, avec lequel on pourrait
  appeler des services de Microsoft au nom de la personne. De ce résultat, seul le nom sort.
- **L'ID client d'Entra ne va pas dans le code.** Ce n'est pas un secret, mais l'application n'en a pas besoin : c'est
  Firebase qui l'utilise. Dans ton tableau de l'étape 8, tu le notes bel et bien.

Si la clé vient à toucher un fichier, le terminal ou une capture, elle n'est plus secrète : on la supprime dans Entra et
l'on en crée une autre. Effacer le fichier ne suffit pas. Ajoute-la, avec les jetons d'accès, à la liste de ce qui
n'est jamais versé dans le dépôt.

---

## Étape 3 · Ce que le projet apporte déjà

Ouvre ces fichiers ; ne les modifie pas encore. Tu vas voir trois nouvelles pièces de Kotlin :

- **`String?` :** un type qui se termine par `?` accepte `null`. `String?` signifie « un texte, ou rien ».
- **`a ?: b` :** si `a` n'est pas `null`, renvoie `a` ; s'il l'est, renvoie `b`.
- **`when { … }` :** une liste de conditions ; elle retient la première qui est remplie.

**`Entrada.kt` : deux nouveautés.** `Resultado` a un cinquième cas, `CuentaConOtroProveedor`. Et il y a une interface de
plus :

```kotlin
interface EntradaQueSeRetoma : Entrada {
    suspend fun retomar(): Resultado?
}
```

C'est une `Entrada` qui, en plus d'`entrar()`, sait répondre si quelque chose est resté à moitié fait. Elle renvoie un
`Resultado`, ou `null` s'il n'est rien resté.

**`PuertaDelNavegador.kt` : la troisième porte.**

```kotlin
interface NavegadorDeProveedor {
    suspend fun pendiente(): String?
    suspend fun abrir(proveedor: OAuthProvider): String
}
```

`abrir` ouvre le navigateur et renvoie le nom de celui qui s'est connecté ; sa version réelle appelle
`startActivityForSignInWithProvider`. `pendiente` demande s'il y a une connexion à moitié faite ; sa version réelle lit
`pendingAuthResult`. Ce sont les deux appels du guide de Firebase. Selon la référence, ce résultat existe quand une
connexion a été lancée par le web et que le système a nettoyé l'écran qui l'attendait ; il contient le même succès ou la
même exception que la connexion aurait donnés ; et pour une connexion, il est remis **une seule fois**.

Cette porte ne remet qu'un nom. C'est la règle sur les données de l'étape 2 faite code : le jeton d'accès n'en sort pas.

**`PantallaEntrada.kt` et `Tienda.kt` : le second bouton.** `Tienda` reçoit maintenant une seconde connexion,
facultative. Si on ne la lui passe pas, il n'y a pas de second bouton et tout est comme dans la 04 ; c'est pourquoi les
tests de la 04 continuent de passer sans qu'on y touche. Et à l'ouverture, elle demande s'il est resté quelque chose à
moitié fait :

```kotlin
val pendiente = entradaConMicrosoft?.retomar()
if (pendiente != null) recibe(pendiente)
```

Ces lignes vont dans un `LaunchedEffect`, qui est la manière qu'a Compose de dire « fais cela une fois à l'apparition,
pas à chaque fois que l'écran se redessine ». Il porte une clé : si la clé change, Compose annule le travail et le
relance, selon sa documentation. Ici la clé ne change jamais, donc la question se pose une seule fois, à l'ouverture de
l'application.

**`MainActivity.kt` et `Armado.kt`.** `onCreate` assemble les pièces et les passe à `Tienda`. Quelle pièce est
assemblée pour Microsoft, c'est une fonction d'`Armado.kt` qui le décide, et elle renvoie pour l'instant
`MicrosoftSinConectar()` : elle ne laisse passer personne. Tu changes `Armado.kt` ; on ne touche pas à
`MainActivity.kt`.

**Les tests.** Sur ton ordinateur, Firebase ne se prépare pas tout seul comme sur un téléphone : sans aide, les tests
ont donné « Default FirebaseApp is not initialized » (mesuré le 18 nov. 2026). C'est pourquoi `src/test` apporte
`AppDePruebas`, qui le prépare à la main. Préparer n'est pas se connecter.

**Mesure le point de départ.** Depuis la racine du dépôt :

```bash
./lecciones/05-entrar-con-microsoft/verifica
```

Le 18 nov. 2026, avec un fichier inventé complet, la dernière ligne a été
`Resultado: 23 de 44 pruebas en verde (21 en rojo, 0 con error, 0 saltadas)`. Il y en a 44 parce qu'on y retrouve les 20
de la leçon 04 et 24 nouveaux. Les 20 de la 04 sont déjà au vert, et trois des nouveaux aussi : ils sont de
l'échafaudage. Les 21 autres attendent ton code.

*(Résultat : 23 tests sur 44 au vert, 21 au rouge, 0 avec erreur, 0 sauté.)*

---

## Étape 4 · Avec Claude Code : se connecter et reprendre

C'est ici qu'entre l'outil, et il entre toujours de la même manière : **tu prédis → tu demandes → tu lis ce
qu'il a généré → tu lances le `verifica` → tu l'expliques avec tes propres mots.**

1. **Prédis.** Écris dans `respuestas/05.md` : si, à l'ouverture de l'application, aucune connexion n'est restée à
   moitié faite, que doit renvoyer `retomar()`, et que doit-il se passer sur l'écran ?
2. **Demande** à Claude Code, depuis `inicio/`, la seconde connexion en prenant la première comme référence. C'est
   l'exercice 5.4, et la demande est courte exprès : « Prends `EntradaConGoogle.kt` comme référence et écris
   `EntradaConMicrosoft.kt` seulement pour le cas où tout se passe bien, avec `retomar()` et `entrar()`, en utilisant la
   porte `navegador`. N'attrape pas encore les exceptions. Dans `Armado.kt`, change seulement le corps de
   `armarEntradaConMicrosoft` pour qu'il renvoie `EntradaConMicrosoft` avec la porte qu'il reçoit, et supprime-y
   `MicrosoftSinConectar`. Ne modifie aucun autre fichier. »
3. **Lis** le changement complet avant de l'accepter et **liste ce qu'il n'a pas bien copié.** La première connexion
   est une bonne référence pour le style et une mauvaise pour le fond : Google remet un jeton et Microsoft non.
   Réponds par oui ou non aux huit questions de « Compare tes réponses », exercice 5.4, et note dans
   `respuestas/05.md` chaque « non » avec le fichier et la ligne. Puis demande la correction de chacun, en disant
   pourquoi. Si les huit donnent « oui », écris « No encontré desvíos en las ocho revisiones » (Je n'ai trouvé aucun
   écart dans les huit vérifications) et conserve le tableau : n'invente pas une erreur. Le cœur doit ressembler à
   ceci :

```kotlin
override suspend fun retomar(): Resultado? {
    val nombre = navegador.pendiente()
    return if (nombre == null) null else Resultado.Entro(nombre)
}

override suspend fun entrar(): Resultado {
    // Lignes 1 et 2 : la demande, avec le fournisseur.
    val proveedor = OAuthProvider.newBuilder("microsoft.com")
        .addCustomParameter("prompt", "select_account")
        .build()
    // D'abord, ce qui est en attente. Seulement s'il n'y a rien (null), on ouvre le navigateur.
    val nombre = navegador.pendiente() ?: navegador.abrir(proveedor)
    return Resultado.Entro(nombre)
}
```

4. **Lance le `verifica`.** Avec ce code, il a donné `Resultado: 32 de 44 pruebas en verde` le 18 nov. 2026. Les rouges
   sont les manières de ne pas entrer : l'étape 5.
5. **Explique**, en trois lignes de `respuestas/05.md` : pourquoi `entrar()` demande ce qui est en attente alors que
   `retomar()` l'a déjà fait à l'ouverture de l'application.

**Que fait `select_account` ?** Selon la documentation de Microsoft, il interrompt la connexion automatique et affiche
la liste des comptes à choisir. Microsoft le marque comme facultatif : cette leçon le recommande, et c'est la valeur
qu'a utilisée l'essai du 13 nov. 2026. Ce n'est pas une condition pour réussir : aucun test ne lit ce paramètre, et s'il
manque, le `verifica` se contente de le signaler.

**Ce qui le prouve, et jusqu'où.** Au niveau simulé, le `verifica` prouve qu'un toucher ouvre le navigateur une seule
fois et avec le fournisseur `microsoft.com` ; qu'avec quelque chose en attente, il ne se rouvre pas ; et que `retomar()`
renvoie qui s'est connecté ou `null`. Un autre test ouvre `MainActivity`, lui passe la porte factice dans
`crearEntradaConMicrosoft` et lit quel fournisseur a été demandé.

La porte était factice : aucun navigateur ne s'est ouvert. « Quelque chose est resté à moitié fait », c'est la doublure
qui l'a dit ; aucun test n'a fermé l'application.

**Et l'application installée utilise-t-elle ta pièce ?** Aucun test ne le voit de bout en bout. Il y a deux appuis :

- `MainActivity.kt` doit être identique à celui de la leçon. Si tu le changes, le `verifica` le marque au rouge même
  si les 44 tests sont au vert (mesuré le 18 nov. 2026).
- Si `Armado.kt` assemble autre chose, un test passe au rouge (43 sur 44).

Les coches « Texto: » ne font que chercher des mots dans tes fichiers. La preuve complète est le niveau réel, à
l'étape 9.

---

## Étape 5 · Sans IA : les trois manières de ne pas entrer

Cette étape se fait **sans Claude Code**. C'est une fonction à trois branches, et c'est toi qui l'écris.

Quand la porte du navigateur ne peut pas remettre un nom, elle lance une exception de Firebase. La référence de
Firebase liste celles de cet appel. Trois groupes comptent ici :

| Exception | Quand, selon la référence | Ta connexion répond |
|---|---|---|
| `FirebaseAuthUserCollisionException` avec le code `ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL` | Cette adresse e-mail a déjà un utilisateur avec une autre manière de se connecter. N'arrive qu'avec l'option « un compte par adresse e-mail » activée (leçon 06) | `Resultado.CuentaConOtroProveedor` |
| `FirebaseAuthWebException` avec le code `ERROR_WEB_CONTEXT_CANCELED` | La personne a annulé l'opération en attente | `Resultado.Cancelada` |
| Toute autre, y compris ces deux classes avec un autre code | La référence nomme une autre opération web en cours, un navigateur sans prise en charge, une panne dans la page et le fournisseur mal configuré. Tout le reste qui échoue entre ici aussi | `Resultado.Fallo` |

Lors de l'essai du 13 nov. 2026, on s'est connecté avec un compte Microsoft dont l'adresse e-mail était déjà entrée avec
Google, et Firebase a répondu avec cette exception et ce texte : « An account already exists with the same email address
but different sign-in credentials ».

La classe ne suffit pas. La même exception contient deux autres codes, et la référence demande de vérifier le code pour
connaître la cause : seul celui-ci veut dire « connecte-toi avec l'autre bouton ». Une autre collision s'affiche comme
une panne, sans lui inventer une cause.

**1. Prédis.** Les deux premières lignes nomment des classes différentes. Si, dans la seconde, tu retires la
condition du code et ne laisse que « c'est une `FirebaseAuthWebException` », quel message verra une personne dont la
page a échoué, sans qu'elle ait rien annulé ?

**2. Écris-le.** Ajoute cette fonction dans ta classe :

```kotlin
private fun resultadoDe(falla: Exception): Resultado = when {
    falla is FirebaseAuthUserCollisionException &&
        falla.errorCode == "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" ->
        Resultado.CuentaConOtroProveedor
    falla is FirebaseAuthWebException && falla.errorCode == "ERROR_WEB_CONTEXT_CANCELED" ->
        Resultado.Cancelada
    else -> Resultado.Fallo
}
```

Et enveloppe le corps de `retomar()` et celui d'`entrar()` avec `try { … }` et ces deux `catch`, dans cet ordre :

```kotlin
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    resultadoDe(e)
}
```

Le premier est celui de la 04 : `CancellationException` n'est pas une panne de la connexion, c'est Kotlin qui signale que
la tâche a été annulée, et on la laisse passer. `retomar()` porte les mêmes `catch` parce que, selon la référence, le
résultat en attente contient aussi les exceptions avec lesquelles la connexion aurait fini.

**3. Lance le `verifica`.** Maintenant la ligne est `Resultado: 44 de 44 pruebas en verde` (résultat : 44 tests sur 44
au vert). Il reste une coche au rouge, celle de `respuestas/05.md` : elle se remplit à l'étape 6.

**4. Vérifie ta prédiction.** Retire `&& falla.errorCode == "ERROR_WEB_CONTEXT_CANCELED"` et lance le `verifica` :
deux tests de plus passent au rouge, les deux du cas « error » (mesuré le 18 nov. 2026 sur la solution : 42 sur 44).
Remets-le. Si ce que tu retires est la condition du code de la collision, un seul passe au rouge (43 sur 44).

Les textes que voit la personne sont déjà dans `mensajeDe` :

| Résultat | Message |
|---|---|
| `Cancelada` | « Cerraste la ventana sin elegir cuenta. Toca el botón cuando quieras intentar otra vez. » (Tu as fermé la fenêtre sans choisir de compte. Touche le bouton quand tu voudras réessayer.) |
| `Fallo` | « No se pudo entrar. Revisa tu conexión y vuelve a intentar. » (Impossible d'entrer. Vérifie ta connexion et réessaye.) |
| `CuentaConOtroProveedor` | « Ese correo ya entró a la tienda con otro botón. Entra con el que usaste la primera vez. » (Cette adresse e-mail est déjà entrée dans la boutique avec un autre bouton. Connecte-toi avec celui que tu as utilisé la première fois.) |

Les deux premiers sont ceux de la 04. Le troisième ne dit pas « Google » exprès : l'exception ne dit pas avec quel
fournisseur l'utilisateur a été créé.

**Ce qui le prouve :** le `verifica` simulé, les trois cas. Chaque cas comporte des tests de deux types : les uns lancent
l'exception à ta classe et lisent ce qu'elle a répondu ; un autre dessine l'écran avec ta classe et cherche son
message. Les exceptions ont été montées par une doublure, avec des codes écrits à la main : aucune ne venait de
Firebase. Quelle exception donne, sur un téléphone réel, la fermeture de l'onglet du navigateur, nous ne l'avons pas
encore mesuré avec un compte.

---

## Étape 6 · Sans IA : casses-en un exprès

Aussi **sans Claude Code**. Tu vas provoquer la mauvaise fin de la scène.

**1. Prédis.** Tu vas faire en sorte que `retomar()` réponde toujours `null`, comme s'il ne restait jamais rien à
moitié fait. Est-ce que ça compile ? Le bouton de Microsoft continue-t-il de fonctionner ? Combien de tests passent au
rouge ?

**2. Casse-le et mesure.** Dans `EntradaConMicrosoft.kt`, remplace les deux lignes du corps de `retomar()` par un
simple `null`, à l'intérieur du `try`. Lance le `verifica`. Voici ce que cela a donné le 18 nov. 2026 (abrégé) :

```text
❌ Tu entrada no retoma: quedó una entrada a medias y retomar() no la recogió (paso 4)
❌ Retomar: 1 de 4 pruebas en verde. Revisa retomar(), que entrar() pregunte primero por lo pendiente, y …
❌ Al abrir la app: 0 de 3 pruebas en verde. Tienda.kt viene dado y llama a retomar(): revisa lo que …
Resultado: 37 de 44 pruebas en verde (7 en rojo, 0 con error, 0 saltadas), leído de …
```

*(En français, les coches disent : ta connexion ne reprend pas : une connexion est restée à moitié faite et
`retomar()` ne l'a pas récupérée (étape 4) ; Reprendre : 1 test sur 4 au vert, vérifie `retomar()` et que `entrar()`
demande d'abord ce qui est en attente, et … ; À l'ouverture de l'application : 0 test sur 3 au vert, `Tienda.kt` est
fourni et appelle `retomar()`, vérifie ce que … Résultat : 37 tests sur 44 au vert, 7 au rouge.)*

Copie dans `respuestas/05.md` la ligne qui dit « Tu entrada no retoma » (Ta connexion ne reprend pas).

**3. Ce que ces tests ne te disent pas.** Ça compile, et les tests du bouton restent au vert : toucher « Entrar con
Microsoft » mène à l'accueil quand même. Le défaut n'existe que si Android ferme l'application au milieu d'une
connexion, et cela n'arrive pas dans un test qui dure une seconde. Sur un téléphone, cela a été mesuré à part, le 13 nov.
2026, avec un code d'essai :

- Avec la page de Microsoft au premier plan, on a fermé le processus de l'application avec `adb shell am kill`, puis on a
  choisi le compte : Android a relancé l'application et le résultat en attente a remis la connexion.
- L'option de développeur « No conservar actividades » (Ne pas conserver les activités) **n'a pas** servi à le tester :
  elle détruit l'écran, mais le processus reste vivant et termine la connexion tout seul. Pour voir le cas, il faut
  fermer le processus, pas l'écran.

**4. Répare-le.** Remets les deux lignes de `retomar()`. Lance le `verifica` : 44 sur 44, et maintenant aussi la
coche de `respuestas/05.md`.

L'exercice est terminé quand tu peux cocher les trois cases :

- [ ] Ta prédiction du point 1 est écrite, avec ce que tu as vu en dessous.
- [ ] Tu as collé la ligne du rouge.
- [ ] Tu as dit pourquoi les tests du bouton sont restés au vert avec un code qui, sur un téléphone à court de
      mémoire, ferait entrer la personne deux fois.

**Ce qui le prouve :** « Casses-en un exprès ». C'est une base de l'objectif 5.2 : savoir ce que récupère `retomar()`.

---

## Étape 7 · À l'intérieur d'Android : le niveau instrumenté

Avec l'émulateur ouvert, depuis la racine du dépôt :

```bash
./lecciones/05-entrar-con-microsoft/verifica inicio instrumentado
```

Il répète le niveau simulé, puis installe l'application et son application de tests sur l'appareil. Là, il dessine
`Tienda` avec ta `EntradaConMicrosoft` et la même porte factice, touche les boutons et cherche les textes. Avec la
solution, dans un émulateur neuf et sans compte (Pixel 9, image `google_apis_playstore` d'Android 37), cela a donné le
18 nov. 2026 (abrégé) :

```text
✅ Instrumentado: dentro de Android, con la puerta de mentira, tocar «Entrar con Microsoft» lleva al inicio …
✅ Instrumentado: dentro de Android, cancelación, error y colisión muestran cada uno su mensaje sin pasar …
✅ Instrumentado: dentro de Android, con una entrada a medias que contesta el doble, la app llega al inicio …
✅ Instrumentado: dentro de Android, las 4 pruebas del botón de Google de la lección 04 siguen en verde
Resultado: 44 de 44 pruebas en verde (0 en rojo, 0 con error, 0 saltadas), leído de …
Instrumentado: 9 de 9 pruebas en verde (0 en rojo, 0 con error, 0 saltadas), leído de …
```

*(Instrumenté : à l'intérieur d'Android, avec la porte factice, toucher « Entrar con Microsoft » mène à l'accueil ; à
l'intérieur d'Android, annulation, erreur et collision affichent chacune leur message sans passer à l'accueil ; à
l'intérieur d'Android, avec une connexion à moitié faite que la doublure déclare, l'application arrive à l'accueil ; à
l'intérieur d'Android, les 4 tests du bouton de Google de la leçon 04 restent au vert. Résultat : 44 tests sur 44 au
vert. Instrumenté : 9 sur 9 au vert.)*

Il y en a neuf : cinq de cette leçon et les quatre de la 04. Sur le projet de départ non modifié, il a donné 4 sur 9, les
quatre de la 04. Sans aucun appareil connecté, le `verifica` sort au rouge et dit qu'il ne s'est pas exécuté.

**Ce qui le prouve, et jusqu'où.** Il prouve que les écrans, le toucher et ta classe fonctionnent à l'intérieur
d'Android, avec la bibliothèque de Firebase Authentication de l'appareil qui monte le fournisseur. Il ne prouve **rien**
de ton enregistrement dans Entra ni de ton projet : la porte reste factice, le navigateur ne s'ouvre pas et il ne
faut ni compte ni réseau vers Microsoft. Et le test de « retomar » est la doublure qui répond que quelque chose est resté
à moitié fait : Android n'a fermé aucune application.

**Regarde le vrai bouton, sans enregistrement.** Dans ce même émulateur, avec un fichier inventé, on a installé
l'application et touché « Entrar con Microsoft » (18 nov. 2026). Aucune page ne s'est ouverte. L'application a affiché
« No se pudo entrar. Revisa tu conexión y vuelve a intentar. » (Impossible d'entrer. Vérifie ta connexion et
réessaye.), et le journal du système a dit `[GetAuthDomainTask] Error getting project config. Failed with API key not
valid`. C'est ce qui est attendu : le fichier était inventé. Avec ton vrai fichier et Microsoft pas encore activé dans
Firebase, nous n'avons pas encore mesuré le résultat ; si tu le vois, dis-nous quel message tu as obtenu.

---

## Étape 8 · Dans les consoles : enregistrer l'application sans que la clé touche un fichier

Cette étape et la suivante demandent le répertoire de « Avant de commencer ». Ici, on n'écrit pas de code. Ouvre les
deux consoles dans deux onglets, parce que trois données vont passer de l'une à l'autre :

| Donnée | Naît dans | Se colle dans |
|---|---|---|
| L'adresse de redirection | Firebase | Entra |
| L'*Application (client) ID* | Entra | Firebase |
| La clé client | Entra | Firebase, et nulle part ailleurs |

Les menus et les boutons sont en anglais, comme dans la documentation. En espagnol, l'essai du 13 nov. 2026 a vu
« Registros de aplicaciones », « Nuevo registro » et « Registrar una aplicación » (dans la console en espagnol). Les
autres libellés en espagnol, nous ne les avons pas parcourus de bout en bout : si ta console dit autre chose, guide-toi sur la version anglaise de chacun et dis-nous comment il apparaît.

**1. Dans Firebase, copie ton adresse de redirection.** Dans Authentication, onglet *Sign-in method*, choisis
Microsoft et active-le. N'enregistre pas encore : le panneau demande l'ID et la clé, que tu n'as pas encore.
Laisse cet onglet ouvert ainsi, à moitié rempli ; tu y reviendras au point 5. Lors de l'essai, l'adresse a été copiée
depuis ce même panneau. Elle a cette forme :

```text
https://<tu-proyecto>.firebaseapp.com/__/auth/handler
```

Le guide de Firebase sur les redirections dit que la fin `/__/auth/handler` compte.

**2. Dans Entra, entre par `entra.microsoft.com`.** Pas par `portal.azure.com` : lors de l'essai, le compte sans
abonnement y a donné l'erreur `AADSTS16000`, et par Entra il a ouvert son propre répertoire.

**3. Enregistre l'application.** Selon le guide de Microsoft : *Entra ID* › *App registrations* › *New registration*.

- **Name :** un nom que tu reconnais. Le guide prévient que les personnes qui se connectent peuvent le voir.
- **Supported account types :** *Any Entra ID Tenant + Personal Microsoft accounts*, pour les comptes d'organisations et
  les comptes personnels. C'est celui qu'a utilisé l'essai.
- *Register*.

La page *Overview* montre l'**Application (client) ID**. Celui-là, on le note, et tu peux déjà le coller dans
l'onglet de Firebase. Ce n'est pas la clé : la clé n'existe pas encore.

**4. Ajoute l'adresse de redirection.** Selon le guide : *Manage* › *Authentication* › *Add Redirect URI* › plateforme
**Web** › colle l'adresse du point 1 › *Configure*. Colle-la ; ne la tape pas à la main. Microsoft demande qu'elle
commence par `https` et distingue majuscules et minuscules.

**5. Crée la clé.** Selon le guide : *Certificates & secrets* › *Client secrets* › *New client secret*. Donne-lui une
description et choisis une expiration. Le guide dit qu'elle ne peut pas dépasser 24 mois et recommande moins de 12.
*Add*.

La clé apparaît dans la colonne **Value**, et le guide prévient qu'elle ne s'affiche plus une fois que tu as quitté
la page. Avec cette page ouverte :

- Copie-la avec le bouton de copie et colle-la **directement** dans le champ de la clé du panneau de Microsoft, dans
  l'onglet de Firebase. Vérifie que l'*Application (client) ID* du point 3 est déjà dans son champ. *Save*.
- Ne la colle nulle part ailleurs. Ensuite, copie n'importe quel autre texte, pour la faire sortir du presse-papiers
  (recommandation de cette série).
- Note la **date d'expiration**. Quand elle arrivera, la connexion avec Microsoft cessera de fonctionner jusqu'à ce que
  tu créais une autre clé et l'apportais à Firebase.
- Ne fais aucune capture tant que la colonne *Value* montre la clé.

Une frontière, pour ne pas en tirer une architecture : Firebase demande cet ID et cette clé pour ce fournisseur, et ce
même guide de Microsoft recommande des certificats plutôt que des clés avant de mettre une application en production.
Pour un produit réel, cela se revoit avec la personne qui s'occupe de la sécurité : la politique d'Entra, la rotation de
la clé et le type d'identifiant.

**6. Laisse la preuve.** Dans `respuestas/05.md` :

| Donnée | Ce que tu notes |
|---|---|
| *Application (client) ID* | Oui, en entier |
| Type de comptes | Oui, le nom de l'option |
| Adresse de redirection | Oui, en entier |
| Expiration de la clé | Oui, la date |
| Valeur de la clé | **Jamais** |

Et une capture de *Certificates & secrets* prise **après** avoir quitté cette page et y être revenu. Si une partie de la
valeur reste visible sur la capture, masque-la avant de l'enregistrer.

**7. Demande au `verifica` de chercher.** Lance-le : les deux coches « Clave: » doivent être au vert. Pour savoir que
cette recherche voit bien, sème une clé **inventée**. Cette commande la monte par morceaux, pour qu'elle ne soit écrite
nulle part, et l'ajoute comme commentaire à la fin d'un fichier :

```bash
printf '// %s%s%s\n' 'Inv8' 'Q~' "$(printf 'x%.0s' $(seq 34))" >> lecciones/05-entrar-con-microsoft/inicio/app/src/main/java/mx/habil/ejemplo/tienda/Armado.kt
```

Lance le `verifica`. Le 18 nov. 2026, sur le projet de départ non modifié, il a répondu avec le fichier et le numéro de
ligne, sans afficher le texte (ton numéro de ligne peut être différent) :

```text
❌ Hay un texto con forma de clave de cliente de Entra en: inicio/app/src/main/java/…/Armado.kt:19. No la imprimo. …
```

*(En français : il y a un texte qui a la forme d'une clé client d'Entra dans : … Je ne l'affiche pas. …)*

Supprime cette dernière ligne dans ton éditeur et relance le `verifica` : vert.

**Ce qui le prouve, et jusqu'où.** Les coches « Clave: » prouvent qu'aucun fichier texte de `inicio/` ni
`respuestas/05.md` ne contient un texte ayant la forme de ces clés ni ne nomme un secret client. La forme est inspirée
d'une règle publique de gitleaks, mais le `verifica` n'exécute pas gitleaks et ne remplace pas une analyse de secrets :
il est plus simple, il peut marquer du texte innocent, et d'autres formes lui échappent, ainsi que les binaires, les
variables d'environnement et l'historique de git. Il ne voit pas non plus ton terminal, tes captures ni ton
presse-papiers. Cette partie, c'est toi qui y veilles, et ton tableau et ta capture, c'est toi qui les compares.

---

## Étape 9 · Sur le téléphone : le niveau réel

Cette étape parle bel et bien à Microsoft, une fois, à la main. C'est la seule qui vérifie l'étape 8 : que l'adresse de
redirection est la bonne, que l'ID est celui de ton enregistrement et que la clé que tu as conservée est la clé
en vigueur.

Avec l'émulateur ouvert, depuis `inicio/` :

```bash
./gradlew installDebug
```

Ouvre « La tienda » (La boutique), touche « Entrar » (Entrer) puis « Entrar con Microsoft ».

**Ce qui a été vu lors de l'essai du 13 nov. 2026** (Mac, émulateur, comptes personnels). Cet essai a utilisé un code de
test avec les mêmes appels, pas celui de `solucion/` :

| Cas | Ce qui a été vu |
|---|---|
| Première fois | Le navigateur s'est ouvert sur la page de Microsoft ; on a saisi le compte ; l'application a salué avec le nom |
| La permission | Un écran « Let this app access your info? », avec la marque **unverified** à côté du nom de l'application, « Read your profile » et les boutons *Deny* et *Accept*. C'est ce qui est attendu pour une application d'exemple sans éditeur vérifié. Microsoft a conservé l'acceptation : elle n'est pas réapparue |
| Se reconnecter | Microsoft a montré le compte déjà connecté, pour le choisir sans mot de passe |
| Une adresse e-mail déjà entrée avec Google | L'exception de collision de l'étape 5 |
| L'application fermée au milieu d'une connexion | Ce qu'on voit à l'étape 6 : le résultat en attente a remis la connexion |

La classe `NavegadorConFirebase` de cette leçon, nous ne l'avons exécutée que jusqu'à l'erreur du fichier inventé de
l'étape 7 ; la connexion complète avec un compte réel, pas encore. Si, en l'essayant, tu vois quelque chose de
différent, dis-nous ce qui est apparu.

**Si ton compte utilise une clé d'accès (passkey).** Lors de l'essai, l'écran « Sign in with your passkey » est resté
sur « Verifying… » dans l'émulateur. On a touché *Cancel* et l'on est entré avec le mot de passe.

**Si le navigateur propose d'enregistrer le mot de passe :** sur une machine qui n'est pas la tienne, « No ».

**Essaie la fin de la scène.** Avec la page de Microsoft au premier plan et avant de choisir un compte, dans un autre
terminal :

```bash
adb shell am kill mx.habil.ejemplo.tienda
adb shell pidof mx.habil.ejemplo.tienda
```

La première ferme les processus de l'application qu'il est sûr de fermer, selon la documentation d'`adb`. La seconde ne
doit rien afficher : le processus n'existe plus. Maintenant, choisis ton compte. L'application doit démarrer et te
saluer sans que tu touches le bouton une seconde fois.

**Pour terminer, sur une machine qui n'est pas la tienne :** ferme la session Microsoft dans le navigateur de l'émulateur
et efface ses données (leçon 00).

**Ce qui le prouve :** la connexion réelle prouve que l'enregistrement, l'adresse de redirection, l'ID et la clé sont
corrects. Elle ne prouve pas les cas où l'on n'entre pas : ceux-là, le niveau simulé les couvre.

---

## Tes exercices

Les étapes 1, 5 et 6 se font **sans IA**.

| # | Tu fais | C'est fait quand |
|---|---|---|
| 5.1 | Tu enregistres l'application dans Entra avec l'adresse de redirection exacte et tu apportes son ID et sa clé à Firebase | Trois preuves. Ton tableau de l'étape 8 et ta capture sans clé visible : tu les compares toi-même. Les deux coches « Clave: » du `verifica` au vert, qui disent seulement qu'aucun **fichier** de `inicio/` ni `respuestas/05.md` ne contient cette forme ou ce nom. Et la connexion réelle de l'étape 9, qui est la seule chose qui valide l'enregistrement |
| 5.2 | Tu implémentes `EntradaConMicrosoft` sans changer les écrans, et tu reprends le résultat en attente | Trois preuves. Simulé : ses coches du `verifica` au vert ; cela valide ce que demande ta classe et ce qu'elle répond, avec une porte factice, et que les 20 tests de la 04 restent au vert avec leurs fichiers identiques. Cela **n'atteste pas** à lui seul que l'application installée utilise ta connexion : cela, `MainActivity.kt` identique à celui de la leçon et le test de ce qu'assemble `Armado.kt` l'appuient. Instrumenté : 9 sur 9, la même chose à l'intérieur d'Android. Réel : tu entres à l'étape 9 et tu fermes le processus au milieu d'une connexion ; c'est la seule chose qui valide qu'Android te ramène vraiment dans l'application |
| 5.3 | Tu compares Google et Microsoft avec les cinq questions de l'étape 1 | Ton tableau coïncide avec celui de « Compare tes réponses ». Tu le compares toi-même |
| 5.4 | Tu demandes à Claude Code la seconde connexion en prenant la première comme référence, et tu listes ce qu'il n'a pas bien copié | Ta liste de oui et de non de l'étape 4, avec fichier et ligne pour chaque « non », contre les huit questions de « Compare tes réponses ». Si les huit donnent « oui », ton rendu est ce tableau et la phrase « No encontré desvíos en las ocho revisiones ». C'est toi qui la notes ; le `verifica` ne la lit pas |

**Durée :** compte environ quatre heures, avec l'enregistrement dans Entra, le « Casses-en un exprès », le niveau
instrumenté et la connexion réelle ; sans les étapes 8 et 9, environ deux heures et demie. C'est une estimation : nous
l'ajusterons avec le temps que prendra la première cohorte. Obtenir le répertoire n'est pas compté : lors de l'essai,
l'abonnement d'essai a mis environ 15 minutes à apparaître comme actif.

---

## Ça ne tourne pas ?

| Tu vois | Cause probable | Ce que tu fais |
|---|---|---|
| `File google-services.json is missing.` | Tu n'as pas copié ton fichier dans `inicio/app/` | Le `cp` de « Avant de commencer ». Sur `solucion/`, c'est ce qui est attendu dans un clone propre |
| ❌ « Estos archivos ya no son los de la lección 04 » (ces fichiers ne sont plus ceux de la leçon 04) | Quelqu'un, toi ou Claude Code, a changé un test, une porte ou un écran de la 04 | `git diff -- <fichier>` et défaites-le à la main |
| ❌ « Tu entrada no retoma » (ta connexion ne reprend pas) | `retomar()` n'utilise pas ce que renvoie `pendiente()` | Étape 4. Si tu viens du « Casses-en un », étape 6, point 4 |
| ❌ Le cas « error » au rouge, avec `resultadoDe` écrite | Toute `FirebaseAuthWebException` est comptée comme une annulation | Étape 5 : il manque la condition du code |
| ❌ « Hay un texto con forma de clave de cliente de Entra » (il y a un texte qui a la forme d'une clé client d'Entra) | Une clé, ou quelque chose qui en a la forme, est dans un fichier | Si c'est ta clé : efface-la du fichier, supprime-la dans Entra et crées-en une autre (étape 8). Si c'est celle, inventée, du contrôle : efface cette ligne |
| Dans les tests, `Default FirebaseApp is not initialized` | `robolectric.properties` ou `AppDePruebas.kt` ont été changés (mesuré le 18 nov. 2026 sans eux) | Remets-les : c'est de l'échafaudage |
| Dans les tests instrumentés, `NoSuchMethodException: android.hardware.input.InputManager.getInstance` | La ligne d'Espresso 3.7.0 a été retirée et c'est celui que Compose apporte par défaut qui est resté. Mesuré le 18 nov. 2026 sur Android 37 : 0 sur 9 | Remets `androidTestImplementation(libs.espresso.core)` |
| Sur le téléphone, « No se pudo entrar… » (Impossible d'entrer…) sans qu'aucune page s'ouvre | Avec un fichier inventé, le journal a dit `API key not valid` (mesuré le 18 nov. 2026). Avec ton fichier, ce que cela donne avec Microsoft non activé ou avec la clé expirée, nous ne l'avons pas encore mesuré | Vérifie que tu as copié ton `google-services.json` et l'étape 8 |
| Dans le navigateur, une erreur `AADSTS50011` | Selon Microsoft : l'adresse de redirection de la demande ne coïncide avec aucune de celles enregistrées. Non mesuré ici | Étape 8, point 4 : colle-la à nouveau, en entier |
| Sur `portal.azure.com`, `AADSTS16000` | Mesuré le 13 nov. 2026 avec un compte sans abonnement | Entre par `entra.microsoft.com` |
| « Nuevo registro » (Nouvel enregistrement) ouvre un panneau sans formulaire | Ton compte n'a pas de répertoire (mesuré le 13 nov. 2026 avec un compte tout juste créé) | « Avant de commencer » : il te faut d'abord un répertoire |
| « Ese correo ya entró a la tienda con otro botón… » (cette adresse e-mail est déjà entrée dans la boutique avec un autre bouton…) | Cette adresse e-mail a déjà un utilisateur dans ton projet, créé avec un autre fournisseur ; dans cette série, Google | Connecte-toi avec Google. Que les deux boutons mènent au même utilisateur, c'est la leçon 06 |
| L'écran de la clé d'accès reste sur « Verifying… » | Mesuré le 13 nov. 2026 dans l'émulateur | *Cancel* et connecte-toi avec le mot de passe |
| ❌ « EntradaConMicrosoft.kt nombra una puerta concreta » (`EntradaConMicrosoft.kt` nomme une porte concrète) | Ton code nomme la vraie porte (`NavegadorConFirebase`) ou une doublure des tests. Les tests ne lui passent que des doublures : si ton code demande laquelle lui est échue, le vert ne dit pas ce qu'il fait avec la vraie | Que ton code ne parle qu'à `NavegadorDeProveedor`. La vraie porte, c'est `MainActivity.kt` qui la choisit, et il est fourni (étape 4) |

Sur Linux Mint 22.3 (x86_64), le `verifica` de cette leçon a donné le même résultat que sur le Mac (18 nov. 2026), au
niveau simulé et avec un fichier de test qui contenait l'empreinte de cet ordinateur : sur `inicio/`, 23 sur 44 au vert ;
sur `solucion/`, 44 sur 44. Le niveau instrumenté n'a pas été mesuré sous Linux.

---

## Pour conclure

**Réponds sans relire :**

1. Où vit la clé client, qui l'utilise et pourquoi ne va-t-elle pas dans l'application ?
2. Qu'est-ce qui doit coïncider exactement entre Firebase et Entra ?
3. Ton `verifica` donne 44 sur 44 et les coches « Clave: » sont au vert. Dis une chose que cela prouve et deux qu'il
   ne prouve pas.
4. Pourquoi « No conservar actividades » ne sert-il pas à tester `retomar()` ?

**La suite :** dans la [06](06-dos-botones-una-cuenta.md), la personne qui a la même adresse e-mail chez les deux
fournisseurs se retrouve avec un seul utilisateur, et seulement si elle le confirme.

---

## Compare tes réponses

**Étape 1.** Ton code intervient à la ligne 1, quand il monte la demande, et reçoit la ligne 6. Les lignes 2 à 5 se
passent entre Firebase, le navigateur et Microsoft. Ton tableau :

| Question | Google (leçon 04) | Microsoft (cette leçon) |
|---|---|---|
| Qui enregistre l'application, et où ? | Tu enregistres l'application dans Firebase et tu ajoutes l'empreinte ; les clients Google, tu ne les crées pas à la main : ils arrivent dans ton `google-services.json` après l'activation de Google (leçon 03) | Toi, à la main, dans la console d'Entra, qui est en dehors de Firebase |
| Que montre-t-il à la personne pour se connecter ? | Credential Manager, avec la fenêtre des comptes d'Android | Le navigateur, avec la page de Microsoft |
| Que reçoit ton application du fournisseur ? | Un jeton d'identité, qu'elle porte elle-même à Firebase | Rien : le code voyage de Microsoft à Firebase. Ton application reçoit l'utilisateur de Firebase |
| Y a-t-il une clé ? Où vit-elle ? | Tu n'en gères aucune : dans l'application ne va que l'ID client web, qui n'est pas secret | Oui, la clé client. Tu la crées dans Entra et elle vit dans la console de Firebase |
| Qu'est-ce qui doit coïncider ? | Le nom de paquet et l'empreinte | L'adresse de redirection, caractère par caractère |

**Ce qui est pareil, c'est le schéma :** une `Entrada` avec sa porte, un `Resultado` pour chaque manière de finir, les
tests avec une doublure, et Firebase qui remet à la fin un utilisateur. Les écrans n'ont pas su qu'il y avait un nouveau
fournisseur.

**Étape 4.** `retomar()` renvoie `null` et l'écran reste sur la bienvenue, sans message. `entrar()` pose la question de
nouveau parce que c'est ce que demande le guide de Firebase avant d'ouvrir un autre flux : celui qui touche le bouton ne
doit pas entrer deux fois s'il y a déjà une réponse qui attend.

**Exercice 5.4.** Huit questions oui ou non sur ce que Claude Code a généré. Ce qui est attendu, ce sont huit « oui » ;
chaque « non » est quelque chose qu'il n'a pas bien copié et va dans ta liste avec fichier et ligne. La liste dit quoi
vérifier, pas ce qui va échouer : on n'a pas mesuré ce que génère l'outil.

| # | Question | Pourquoi |
|---|---|---|
| 1 | Le fournisseur est-il le texte `"microsoft.com"`, sans `https://` ? | C'est ainsi que l'écrit le guide de Firebase |
| 2 | A-t-il laissé de côté ce qui était de Google : `GetCredentialRequest`, `GetSignInWithGoogleOption`, `GoogleIdTokenCredential` ? | Ici, il n'y a pas de jeton à demander au téléphone |
| 3 | La classe reçoit-elle seulement la porte `navegador`, sans `idDeClienteWeb` ni autre identifiant ? | L'ID et la clé vivent dans les consoles |
| 4 | `entrar()` demande-t-il `pendiente()` avant `abrir` ? | C'est ce que demande le guide, pour ne pas faire entrer deux fois |
| 5 | `retomar()` n'appelle-t-il jamais `abrir` ? | Il est appelé à l'ouverture de l'application, sans que personne touche un bouton |
| 6 | N'y a-t-il d'écrit aucun ID client, aucune clé ni l'identifiant d'un répertoire ? | Règle sur les données de l'étape 2. Le paramètre `tenant` avec la valeur `common` n'est pas une donnée à toi : c'est la valeur par défaut |
| 7 | Personne ne nomme-t-il `accessToken`, `idToken` ni `OAuthCredential` ? | Règle sur les données : de Firebase, seul le nom sort |
| 8 | Les tests, les portes, les écrans et ton `google-services.json` sont-ils restés intacts ? | Le `verifica` compare ces fichiers |

**Étape 5.** Elle verrait celui de l'annulation, « Cerraste la ventana… » (Tu as fermé la fenêtre…), alors qu'elle
n'a rien fermé. Sans la condition, toute panne du navigateur est comptée comme une annulation.

**Étape 6.** Ça compile, et le bouton continue de fonctionner. Sept tests passent au rouge : ceux de `retomar()` et les
trois qui ouvrent l'application avec quelque chose à moitié fait. Ceux du bouton restent au vert parce qu'aucun ne ferme
l'application : dans ceux-là, le navigateur factice répond instantanément.

**Pour conclure :**

1. Elle naît dans Entra et vit dans la console de Firebase. Ce sont les serveurs de Firebase qui l'utilisent pour
   échanger le code de Microsoft contre les données de la personne. Elle ne va pas dans l'application parce que quiconque
   a l'application installée pourrait l'en extraire : Microsoft demande de ne pas l'utiliser dans les applications
   natives.
2. L'adresse de redirection : celle qu'utilise Firebase et celle que tu as enregistrée dans Entra.
3. Il prouve ce que demande ta classe et ce qu'elle répond dans chaque cas, avec une porte factice, et qu'aucun
   fichier vérifié ne contient un texte ayant la forme d'une clé. Il ne prouve pas que ton enregistrement dans Entra
   fonctionne, ni que la clé ne se trouve pas dans ton terminal, dans une capture ou dans l'historique de git.
4. Parce qu'elle détruit l'écran mais laisse vivant le processus, qui termine la connexion tout seul. `retomar()` existe
   pour le moment où le processus n'est plus là.

---

## Sources de la leçon

Ouvertes le 18 nov. 2026.

- Firebase. *Authenticate Using Microsoft on Android* (le fournisseur, le résultat en attente, la connexion et l'échange du code avec la clé de la console). https://firebase.google.com/docs/auth/android/microsoft-oauth
- Firebase. Référence de `FirebaseAuth` (`getPendingAuthResult` et les exceptions de `startActivityForSignInWithProvider`). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth
- Firebase. Référence de `FirebaseAuthUserCollisionException` (ses trois codes ; `ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL` seulement avec « un compte par adresse e-mail », qu'elle recommande ; vérifier le code). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuthUserCollisionException
- Firebase. Référence de `FirebaseAuthWebException` (ses quatre codes ; `ERROR_WEB_CONTEXT_CANCELED`). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuthWebException
- Firebase. *Best practices for using signInWithRedirect* (l'adresse `https://<project>.firebaseapp.com/__/auth/handler`). https://firebase.google.com/docs/auth/web/redirect-best-practices
- Microsoft Learn. *How to register an app in Microsoft Entra ID* (conditions, étapes et types de comptes). https://learn.microsoft.com/en-us/entra/identity-platform/quickstart-register-app
- Microsoft Learn. *How to add a redirect URI to your application* (les étapes et la plateforme Web). https://learn.microsoft.com/en-us/entra/identity-platform/how-to-add-redirect-uri
- Microsoft Learn. *Redirect URI (reply URL) best practices and limitations* (adresses enregistrées, `https`, majuscules et `AADSTS50011`). https://learn.microsoft.com/en-us/entra/identity-platform/reply-url
- Microsoft Learn. *Add and manage app credentials in Microsoft Entra ID* (la clé : 24 mois au maximum et une valeur qui ne s'affiche plus). https://learn.microsoft.com/en-us/entra/identity-platform/how-to-add-credentials
- Microsoft Learn. *Microsoft identity platform and OAuth 2.0 authorization code flow* (`select_account`, la coïncidence exacte et la clé hors des applications natives). https://learn.microsoft.com/en-us/entra/identity-platform/v2-oauth2-auth-code-flow
- Microsoft Azure. *Azure for Students* (conditions de l'offre). https://azure.microsoft.com/en-us/pricing/offers/ms-azr-0170p
- Android Developers. *Side-effects in Compose* (`LaunchedEffect`). https://developer.android.com/develop/ui/compose/side-effects
- Android Developers. *Android Debug Bridge* (`am kill`). https://developer.android.com/tools/adb
- Kotlin. *Null safety* (les types avec `?` et l'opérateur `?:`). https://kotlinlang.org/docs/null-safety.html
- gitleaks, outil public de recherche de secrets : sa règle `azure-ad-client-secret` a inspiré la forme que cherche le `verifica`, qui n'exécute pas gitleaks ni n'applique ses délimiteurs ni son seuil d'entropie. Ce n'est pas un format publié par Microsoft. https://github.com/gitleaks/gitleaks/blob/master/config/gitleaks.toml
- Dépôt Maven de Google : le BoM de Firebase 35.2.0 reste le plus récent et apporte `firebase-auth` 25.0.0. https://dl.google.com/dl/android/maven2/com/google/firebase/firebase-bom/maven-metadata.xml · https://dl.google.com/dl/android/maven2/com/google/firebase/firebase-bom/35.2.0/firebase-bom-35.2.0.pom
- Essai de la série, 13 nov. 2026, Mac avec Apple silicon, avec un code de test : l'enregistrement dans Entra par `entra.microsoft.com` ; `AADSTS16000` dans le portail d'Azure ; le compte tout juste créé et l'essai gratuit ; la connexion réelle, la permission, la collision et le résultat en attente après `adb shell am kill` ; la clé d'accès dans l'émulateur.
- Nos propres mesures : Mac avec Apple silicon, 18 nov. 2026. Le `verifica` sur `inicio/` et `solucion/`, chaque étape de la leçon et chaque sabotage, avec des fichiers inventés et sans toucher à la console d'Entra, à celle d'Azure ni à celle de Firebase, ni à aucun compte. Les tests instrumentés et le bouton de Microsoft avec un fichier inventé, dans un émulateur neuf, sans compte, qui a été supprimé à la fin. Les quatre codes de `FirebaseAuthWebException` ont en outre été recoupés dans la bibliothèque `firebase-auth` 25.0.0.
