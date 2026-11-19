# Leçon 07 — La session reste ouverte

**Avant de lire, cinq mots.** Une ligne chacun.

- **Session :** ce qui permet à l'application de savoir qui vous êtes sans vous le demander de nouveau.
- **Utilisateur actuel :** l'utilisateur que Firebase retient sur ce téléphone. Dans le code, il s'appelle
  `currentUser`.
- **Fermer la session :** demander au téléphone d'oublier cet utilisateur, pour que la prochaine fois on demande de se
  connecter.
- **Jeton (token) :** un justificatif numérique de la session. Ce n'est pas un mot de passe, et on ne le colle pas ni ne
  le publie non plus.
- **Credential Manager :** la pièce d'Android qui coordonne les identifiants et peut retenir quel compte vous avez
  choisi pour cette application.

**Ce que vous en retirez :** une boutique qui, à l'ouverture, vous salue si vous étiez déjà connecté et vous demande
de vous connecter sinon ; avec un bouton « Cerrar sesión » (Fermer la session) qui vous fait vraiment sortir. Et la
différence entre des choses qu'on confond souvent : l'utilisateur actuel, les jetons que garde Firebase, les identités
liées à votre utilisateur et ce qu'un fournisseur retient de son côté.

**Idée nouvelle de cette leçon :** le cycle de la session : se souvenir, fermer et expirer. Le reste —la question que
l'application pose en s'ouvrant, le bouton dans l'accueil et la porte qui parle avec Firebase et avec Credential
Manager— est fourni et expliqué, et n'est pas évalué ici.

---

## La scène

Nous sommes le 13 novembre 2026, lors de l'essai de cette série. L'application de test vient de saluer un compte
Google réel : « Hola, … (primera vez) » (Bonjour, … (première fois)). Quelqu'un la ferme et la rouvre.

L'application salue de nouveau. Avant que personne ne touche le bouton.

Dans les leçons 04, 05 et 06, votre application ne fait pas cela : elle s'ouvre toujours sur la bienvenue et vous
demande de vous connecter, même si vous vous êtes connecté il y a une minute. La leçon 06 l'a laissé noté : « cette
application n'a pas encore de moyen de fermer la session ».

Les deux choses sont vraies à la fois, et le mystère n'a qu'une réponse. Firebase se souvenait **bien** de vous depuis
la leçon 04 : il le conserve sur le téléphone de lui-même. L'application de l'essai le lui demandait à l'ouverture ; la
vôtre ne le lui a jamais demandé. Cette leçon lui apprend à demander, et à demander d'oublier.

---

## Avant de commencer

**Ce dont vous avez besoin :** la [06](06-dos-botones-una-cuenta.md) terminée (son `verifica` à 66 sur 66). Les étapes 1
à 3 et la 5 ne demandent aucun compte. L'étape 4 se fait sur votre téléphone ou émulateur, avec votre compte Google de
la 04.

**Le projet de départ.** Depuis la racine du dépôt de la série :

```bash
git pull
cp lecciones/03-la-app-conoce-a-firebase/inicio/app/google-services.json lecciones/07-la-sesion-se-queda/inicio/app/
cd lecciones/07-la-sesion-se-queda/inicio
```

Il part de la solution de la 06, avec l'échafaudage de cette leçon par-dessus. `./verifica solucion` sort au rouge dans
un clone propre, parce que sans votre fichier le projet ne compile pas. Vos réponses vont dans `respuestas/07.md`.

**Règle toujours en vigueur.** Ne collez ni ne versez jamais dans le dépôt un jeton, ni des captures ou des journaux qui
en contiennent. Cette leçon parle beaucoup de jetons, et c'est justement pour cela qu'elle n'en imprime aucun :
l'application ne lit que le nom de l'utilisateur.

**Ce que cette leçon ne fait pas.** Elle ne distingue pas la première fois (leçon 08). Elle n'efface pas votre compte
Google du téléphone ni votre utilisateur du projet Firebase : fermer la session n'est rien de cela, et l'étape 5 explique
pourquoi.

---

## Étape 1 · Sans IA : où l'application arrive-t-elle quand on la rouvre ?

C'est l'exercice 7.1 et il se fait **sans Claude Code**. Ne lisez pas encore l'étape 2.

Pensez à l'application **terminée** de cette leçon : celle qui interroge déjà Firebase à l'ouverture. Écrivez dans
`respuestas/07.md` deux lignes qui commencent ainsi :

```text
predicción 7.1 con sesión: …
predicción 7.1 sin sesión: …
escrita el AAAA-MM-DD, antes del paso 2 y sin IA
```

*(Les lignes disent : prédiction 7.1 avec session, prédiction 7.1 sans session, écrite le AAAA-MM-JJ, avant l'étape 2
et sans IA. Elles restent en espagnol parce que c'est ce que cherche le `verifica`.)*

Dans la première : vous vous êtes connecté avec Google, vous avez fermé l'application depuis la liste des applications
récentes et vous la rouvrez. Quel écran voyez-vous, et la fenêtre pour choisir un compte s'ouvre-t-elle ? Dans la
seconde : vous avez touché « Cerrar sesión » et vous la rouvrez. Quel écran voyez-vous, et que se passe-t-il quand vous
touchez « Entrar » (Entrer) ?

**Mesurez le point de départ.** Depuis la racine du dépôt :

```bash
./lecciones/07-la-sesion-se-queda/verifica
```

Le 19 nov. 2026, avec un fichier inventé, la dernière ligne du décompte a été
`Resultado: 68 de 82 pruebas en verde (14 en rojo, 0 con error, 0 saltadas)`. Il y en a 82 : les 66 dont vous héritez de
la 06, déjà au vert, et 16 de la session. Parmi celles-ci, deux passent déjà avec l'échafaudage et quatorze attendent
votre code.

*(Résultat : 68 tests sur 82 au vert, 14 au rouge, 0 avec erreur, 0 sauté.)*

**Ce qui le prouve :** votre prédiction, face à ce que vous verrez à l'étape 3 (les tests) et à l'étape 4 (votre
téléphone). Le `verifica` confirme seulement que vous avez laissé les lignes : il ne voit ni quand vous les avez
écrites, ni si vous avez utilisé une IA, ni si vous avez deviné juste. C'est pourquoi conservez votre prédiction avec
sa date, avant l'étape 2, et déclarez dessous, en une ligne, que vous l'avez faite sans IA. Le vert ne note pas le
contenu.

---

## Étape 2 · Ce que le projet apporte déjà

**Firebase disait déjà comment demander.** Son guide pour se connecter avec Google sur Android dit : « au démarrage de
votre Activity, vérifiez si l'utilisateur est déjà connecté », et il le fait en lisant `currentUser`. La référence de
`FirebaseAuth` le résume : `getCurrentUser()` renvoie l'utilisateur qui s'est connecté, ou `null` s'il n'y en a aucun.

Dans l'application, trois fichiers sont nouveaux et quatre ont changé par rapport à la solution de la 06 ; dans les
tests arrivent trois nouvelles classes et leur doublure, `DoblesDeSesion.kt`. Ouvrez-les ; ne modifiez que les deux que
dit l'étape 3 :

| Fichier | Ce qu'il apporte |
|---|---|
| `Sesion.kt` (nouveau) | L'interface `Sesion` : `quienSigueAdentro()` et `cerrar()` |
| `PuertaDeLaSesion.kt` (nouveau) | La porte `RecuerdoDeLaSesion` avec trois fonctions : `usuarioActual()`, `olvidarEnFirebase()` et `olvidarEnElTelefono()` ; son implémentation réelle, `RecuerdoConFirebase` ; et, à part, le `LimpiadorDeCredenciales` qui parle avec Credential Manager |
| `SesionDeLaTienda.kt` (nouveau) | La forme de votre classe, vide. Vous l'écrivez à l'étape 3 |
| `Tienda.kt` | À l'ouverture, il demande **une fois** à la session qui est encore à l'intérieur. S'il y a quelqu'un, il s'ouvre sur l'accueil avec son nom ; s'il n'y a personne, sur la bienvenue |
| `Inicio.kt` | Le bouton « Cerrar sesión ». Il ne se dessine que s'il y a une session : sans elle, l'accueil est celui de la 06 |
| `MainActivity.kt`, `Armado.kt` | Ils assemblent aussi la session. Pour l'instant, `Armado.kt` renvoie `SesionSinConectar()`, qui ne se souvient de personne : c'est ainsi que se comporte votre application de la 06 |

Le reste est identique à la solution de la 06, et le `verifica` le compare fichier par fichier. C'est une contrainte de
cette activité, pas de l'objectif.

**Les trois fonctions de la porte, chacune avec sa source :**

- `usuarioActual()` lit `FirebaseAuth.getInstance().currentUser` et ne renvoie que son nom.
- `olvidarEnFirebase()` appelle `signOut()`. La référence dit qu'il ferme la session de l'utilisateur actuel **et le
  supprime du cache sur disque**. Cette phrase résout le mystère de la scène : Firebase conserve l'utilisateur sur le
  disque du téléphone, et c'est pourquoi il survit à la fermeture de l'application.
- `olvidarEnElTelefono()` demande à son nettoyeur d'appeler `clearCredentialState` de Credential Manager. Le guide de
  Firebase demande de le faire en fermant la session, en plus de `signOut`. La référence de Credential Manager explique
  à quoi cela sert : un fournisseur d'identifiants peut conserver une session active de votre application et lui donner
  la priorité la fois suivante. Cet avis lui demande de nettoyer cet état ; ainsi il peut cesser de privilégier
  l'identifiant précédent. Les options qui apparaissent ensuite, c'est le fournisseur qui les décide, et elles se
  vérifient sur le téléphone.
- Le nettoyeur est à part, dans sa propre porte, pour une raison liée aux tests : ainsi un test peut donner à la vraie
  porte un faux nettoyeur et voir qu'elle l'appelle bien en fermant la session.

**Un avertissement de la documentation elle-même.** La page de Firebase pour gérer les utilisateurs prévient de deux
cas. `getCurrentUser` peut renvoyer `null` parce que Firebase n'a pas encore fini de se préparer. Et il peut renvoyer un
utilisateur dont le jeton n'est plus valide, par exemple s'il a été supprimé depuis un autre appareil et que le jeton du
téléphone n'a pas été renouvelé. Pour réagir à cela, elle offre un `AuthStateListener`, qui prévient chaque fois que
l'état du jeton change.

Cette leçon fait une seule lecture à l'ouverture, comme le guide de Google, pour étudier le cycle de base. Cette lecture
ne garantit pas que Firebase ait déjà récupéré la session, ni que le jeton du téléphone soit encore valide. Une
application de production écoute l'`AuthStateListener` ou quelque chose d'équivalent. Vérifiez le résultat réel sur
votre téléphone : un 82 sur 82 ne démontre pas que la session se conserve vraiment.

---

## Étape 3 · Avec Claude Code : se souvenir et fermer

Il entre comme toujours : **vous prédisez → vous demandez → vous lisez ce qu'il a généré → vous lancez le `verifica` →
vous l'expliquez.**

1. **Prédisez**, dans `respuestas/07.md` : si `cerrar()` demandait à Firebase d'oublier et que l'avis au téléphone
   échouait, la personne reste-t-elle à l'intérieur ou à l'extérieur ?
2. **Demandez** à Claude Code, depuis `inicio/` : « Dans `SesionDeLaTienda.kt`, `quienSigueAdentro()` renvoie ce que
   dit `recuerdo.usuarioActual()`. `cerrar()` appelle d'abord `recuerdo.olvidarEnFirebase()` puis
   `recuerdo.olvidarEnElTelefono()` dans un `try` : `CancellationException` se laisse passer et
   `ClearCredentialException` s'attrape sans rien faire d'autre, parce que la session est déjà fermée. Dans `Armado.kt`,
   change seulement le corps de `armarSesion` pour qu'il renvoie `SesionDeLaTienda`, et supprime-y `SesionSinConectar`.
   Ne change aucun autre fichier. »
3. **Lisez.** Le cœur doit ressembler à ceci :

```kotlin
override fun quienSigueAdentro(): String? = recuerdo.usuarioActual()

override suspend fun cerrar() {
    recuerdo.olvidarEnFirebase()
    try {
        recuerdo.olvidarEnElTelefono()
    } catch (e: CancellationException) {
        throw e
    } catch (e: ClearCredentialException) {
        // L'avis n'a pas pu être donné. La session de Firebase est déjà fermée.
    }
}
```

   Vérifiez quatre choses : que `cerrar()` fasse **les deux** choses et dans cet ordre ; que le `catch` ne soit pas un
   `Exception` tout court qui avale l'annulation ; que votre classe ne connaisse que `RecuerdoDeLaSesion`, sans demander
   quelle porte lui est échue ; et que personne n'ait touché aux tests, aux portes ni aux écrans.

4. **Lancez le `verifica`.** Les 82 tests peuvent être au vert. Le rendu n'est pas encore complet : il manque vos
   lignes du 7.1 et du 7.3 dans `respuestas/07.md`, et le `verifica` le dit à la fin.
5. **Expliquez**, en trois lignes : pourquoi Firebase oublie d'abord et l'avis au téléphone vient ensuite, à l'intérieur
   du `try`.

**Ce qui le prouve, et jusqu'où.** Les tests utilisent le projet factice de la 06 comme s'il était la mémoire du
téléphone. Ana se connecte avec Google ; le test « ferme et rouvre » l'application, et Ana reste à l'intérieur sans que
la fenêtre des comptes s'ouvre. Elle touche « Cerrar sesión », l'application revient à la bienvenue, se rouvre et
demande de se connecter ; en se reconnectant, elle arrive au **même** utilisateur. D'autres tests donnent à votre classe
une porte factice et regardent ce qu'elle lui a demandé : d'oublier une fois, d'avertir une fois, qu'un avis qui échoue
ne fasse pas planter l'application et que l'annulation ne soit pas avalée. Et avec `MainActivity`, ils mesurent que la
session que l'application assemble est la vôtre ; qu'avec la vraie porte, sur votre ordinateur, personne n'est resté à
l'intérieur ; et que, avec cette même vraie porte et un faux nettoyeur, fermer la session demande à Credential Manager
de nettoyer. En outre, le `verifica` vérifie comme texte que `SesionDeLaTienda` ne programme que contre
`RecuerdoDeLaSesion` : s'il nomme une porte concrète ou une doublure, le niveau simulé reste incomplet.

Les sabotages ont été mesurés sur la solution le 19 nov. 2026 :

| Sabotage | Résultat |
|---|---|
| `quienSigueAdentro()` qui répond toujours `null` | 78 sur 82 |
| `cerrar()` sans `olvidarEnFirebase()` | 78 sur 82 |
| `cerrar()` sans l'avis au téléphone | 78 sur 82 |
| L'avis sans `try` | 81 sur 82 |
| `catch (e: Exception)` qui avale l'annulation | 81 sur 82 |
| `quienSigueAdentro()` qui répond toujours qu'il y a quelqu'un | 71 sur 82, et deux des rouges sont héritées de leçons précédentes |
| L'avis au téléphone seulement quand la porte n'est **pas** la vraie | 81 sur 82, et la marque « ne programme que contre `RecuerdoDeLaSesion` » au rouge |

Chacun laisse en outre sa marque au rouge dans la sortie, avec l'étape qui le corrige.

Le dernier est celui qui justifie le nettoyeur à part : sans un test qui ferme la session avec la vraie porte, une
classe qui ne se comporte bien qu'avec les doublures sortirait au vert.

Trois limites. « Rouvrir » dans les tests, c'est dessiner l'application de zéro avec la même mémoire factice : aucun test
ne tue le processus ni ne lit le disque du téléphone. Aucun ne voit un vrai utilisateur de Firebase : sur votre
ordinateur, il n'y en a jamais. Et le vrai Credential Manager, aucun test ne l'appelle : sur votre ordinateur, il ne
répond pas (mesuré le 19 nov. 2026 : dix secondes sans réponse). Les tests voient que l'on lui demande de nettoyer, pas
ce qu'offre ensuite la fenêtre des comptes : cela ne se voit que sur le téléphone.

---

## Étape 4 · Sur le téléphone : rouvrir et fermer

Installez et ouvrez l'application avec votre `google-services.json`, comme dans la 04 :

```bash
./gradlew installDebug
```

**Avec session.** Connectez-vous avec Google. Fermez l'application depuis la liste des applications récentes
(faites-la glisser hors de l'écran) et rouvrez-la depuis le tiroir. Comparez avec votre première prédiction.

**Sans session.** Touchez « Cerrar sesión ». Fermez l'application de la même manière et ouvrez-la. Touchez « Entrar » et
« Entrar con Google » (Entrer avec Google). Comparez avec votre seconde prédiction.

Ce que l'on a déjà vu lors de l'essai du 13 nov. 2026, avec un code de test : en rouvrant, l'application a salué sans
que l'on touche le bouton. Ce que nous **n'avons pas encore capturé avec un compte réel**, c'est ce parcours avec le code
de cette leçon : le bouton « Cerrar sesión », l'application qui demande de se connecter en rouvrant et ce que montre
ensuite la fenêtre des comptes. S'il vous apparaît différent, dites-le-nous.

Avec Microsoft, il convient de savoir quelque chose. Le guide de Firebase pour Microsoft documente la fermeture dans
l'application, avec `signOut`, et ne dit rien du navigateur. Lors de l'essai, en se reconnectant avec Microsoft, sa page
montrait déjà une session active du navigateur, avec le compte prêt à être choisi sans mot de passe. Nous l'avons vu
dans ce navigateur, ce jour-là ; ce n'est pas une garantie pour n'importe quel navigateur. Sur un poste partagé,
vérifiez cette session et fermez-la séparément.

**Et la dette de la 06.** L'essai manuel de son étape 5 demandait de sortir pour se reconnecter avec Microsoft, et
proposait d'effacer les données de l'application. Maintenant vous avez « Cerrar sesión » : c'est la manière de sortir que
l'application offre elle-même.

**Ce qui le prouve :** ce que vous observez. Il n'y a pas de marque dans le `verifica` pour cette étape.

---

## Étape 5 · Quatre choses qui ne sont pas les mêmes

C'est l'exercice 7.3. En rouvrant avec une session et en la fermant, quatre choses différentes interviennent, et chacune
a sa source :

| | Ce que c'est | Ce que lui fait « Cerrar sesión » dans cette application | Source |
|---|---|---|---|
| **L'utilisateur actuel** | L'utilisateur de votre projet que Firebase retient sur ce téléphone (`currentUser`) | `signOut()` ferme sa session et le supprime du cache sur disque : `currentUser` devient `null` | Référence de `FirebaseAuth` (`getCurrentUser`, `signOut`) |
| **Les jetons de Firebase** | Celui d'identité dure une heure. Celui d'actualisation sert à en demander un autre, et il expire si l'utilisateur est supprimé ou désactivé, ou lors d'un changement majeur de son compte, comme le mot de passe ou l'adresse e-mail. Le serveur peut aussi le révoquer, et réinitialiser le mot de passe le révoque | `signOut()` nettoie l'état sur le téléphone. Ce n'est pas une révocation sur le serveur | *Manage User Sessions* de Firebase |
| **Les identités liées** (`providerData`) | Les identités de chaque fournisseur liées à votre utilisateur Firebase : avec Google et avec Microsoft, si vous les avez liées dans la 06 | Rien : ni `signOut` ni `clearCredentialState` ne les délient | Référence de `FirebaseUser` (`getProviderData`) |
| **Ce qu'un fournisseur retient de son côté** | L'état d'identifiants qu'un fournisseur du téléphone peut conserver pour cette application. À part se trouvent votre compte Google sur le téléphone et votre session Microsoft dans le navigateur | `clearCredentialState` demande seulement de nettoyer cet état local pour cette application. Il n'efface ni le compte Google du téléphone ni la session du navigateur de Microsoft | Référence de `CredentialManager` (`clearCredentialState`) |

La page des sessions relève de la partie serveur de Firebase, mais elle décrit la session de n'importe quel utilisateur :
« les sessions de Firebase Authentication sont de longue durée ». C'est pourquoi l'application ne vous demande pas de vous
connecter toutes les heures : le jeton d'identité expire, et Firebase en demande un autre avec celui d'actualisation.

**À vous.** Pour chaque affirmation, écrivez dans `respuestas/07.md` `verdadera` (vraie) ou `falsa` (fausse), ainsi :
`7.3-1: falsa`.

1. Fermer la session dans l'application efface votre compte Google du téléphone.
2. Le jeton d'identité de Firebase dure une heure ; c'est pourquoi, le lendemain, l'application vous demande de vous
   connecter de nouveau.
3. Dans le code de cette leçon, si, à l'ouverture, Firebase a déjà un utilisateur actuel, l'application salue sans ouvrir
   la fenêtre des comptes de Google.
4. Après `signOut()`, `currentUser` vaut `null`.
5. Fermer la session dans l'application délie Microsoft de votre utilisateur Firebase : il disparaît de `providerData`.

Choisissez-en **une** et justifiez-la avec la documentation : copiez l'adresse de la page officielle sur une ligne qui
commence par `fuente 7.3: https://` (source 7.3), et dessous, avec vos mots, ce qu'elle dit.

**Ce qui le prouve :** cinq sur cinq contre « Comparez vos réponses », et votre justification. Le `verifica` confirme
seulement que vous avez laissé les cinq lignes et la source : comparez-les vous-même avec cette section. Le vert ne note
pas le contenu.

---

## Vos exercices

L'étape 1 se fait **sans IA**.

| # | Vous faites | C'est fait quand |
|---|---|---|
| 7.1 | Vous prédisez sur quel écran arrive l'application quand on la rouvre avec et sans session, et vous le vérifiez | Vos deux lignes `predicción 7.1` sont dans `respuestas/07.md`, avec leur date, écrites avant l'étape 2 et déclarées sans IA, et vous les avez comparées avec les tests de l'étape 3 et avec votre téléphone à l'étape 4. Le `verifica` voit seulement que les lignes existent |
| 7.2 | Vous implémentez la fermeture de session de façon qu'au retour on demande de se connecter de nouveau | Le `verifica` dit « Nivel simulado: completo » (niveau simulé : complet), avec les 82 tests au vert. C'est le niveau simulé ; l'étape 4 est votre essai sur le téléphone |
| 7.3 | Vous distinguez l'utilisateur actuel, les jetons de Firebase, les identités liées et ce que retient un fournisseur | Cinq sur cinq contre « Comparez vos réponses », et une justifiée avec sa source officielle. Cela, vous le comparez vous-même : le `verifica` ne le note pas |

**Durée :** comptez une heure et quart sans l'étape 4, et vingt minutes de plus avec elle. C'est une estimation : nous
l'ajusterons avec le temps que prendra la première cohorte.

---

## Ça ne tourne pas ?

| Vous voyez | Cause probable | Ce que vous faites |
|---|---|---|
| `File google-services.json is missing.` | Vous n'avez pas copié votre fichier dans `inicio/app/` | Le `cp` de « Avant de commencer » |
| `NotImplementedError` dans les tests de la session | `SesionDeLaTienda.kt` en est encore à ses `TODO` | Étape 3 |
| ❌ « Restricción de esta actividad… ya no son iguales en inicio/ y en solucion/ » (contrainte de cette activité… ils ne sont plus égaux dans inicio/ et dans solucion/) | Un fichier fourni a changé | `git diff -- <fichier>` et défaites-le à la main |
| ❌ « Cuando el aviso falla » (quand l'avis échoue) | Votre `catch` attrape `Exception` et avale l'annulation, ou il n'y a pas de `try` | Étape 3 : `CancellationException` d'abord, et on la relance |
| ❌ « De las 66 pruebas heredadas… » (des 66 tests hérités…) et plusieurs de la session au rouge | Votre session dit qu'il y a toujours quelqu'un à l'intérieur, et l'application ne s'ouvre plus sur la bienvenue | `quienSigueAdentro()` répond ce que dit la porte, rien de plus |
| Sur le téléphone, l'application s'ouvre sur la bienvenue alors que vous êtes connecté | Vous avez installé celle de `inicio/` avant l'étape 3, ou vous avez effacé les données de l'application | Terminez l'étape 3 et réinstallez |
| En vous reconnectant avec Microsoft, on ne vous demande pas de mot de passe | Cela peut être la session de Microsoft dans le navigateur, qui n'est pas celle de votre application | Étape 4. Sur un poste partagé, vérifiez-la et fermez-la dans le navigateur |
| L'application s'ouvre sur la bienvenue, et la session apparaît après | Firebase peut ne pas avoir fini de se préparer quand l'application a fait sa lecture unique | N'effacez pas les données et ne donnez pas l'essai pour bon : notez le cas. Cette leçon fait une lecture unique ; une version robuste écoute l'`AuthStateListener` (étape 2) |
| ❌ « SesionDeLaTienda solo programa contra RecuerdoDeLaSesion… » (`SesionDeLaTienda` ne programme que contre `RecuerdoDeLaSesion`…) | Votre classe nomme une porte concrète ou une doublure | Étape 3 : votre classe ne connaît que l'interface |

Le `verifica` de cette leçon a été mesuré sur un Mac avec Apple silicon et sous Linux Mint 22.3, le 19 nov. 2026 : avec la solution, 82 sur 82 sur les deux machines.

---

## Pour conclure

**Répondez sans relire :**

1. Pourquoi l'application de la 06 s'ouvrait-elle sur la bienvenue alors que Firebase se souvenait de vous ?
2. Quelles deux choses fait « Cerrar sesión », et pourquoi dans cet ordre ?
3. Votre `verifica` donne 82 sur 82. Dites une chose que cela démontre et une qu'il ne démontre pas.
4. Si le jeton d'identité dure une heure, pourquoi l'application ne vous demande-t-elle pas de vous connecter toutes les
   heures ?

**La suite :** dans la L08, la première fois : l'application distingue l'utilisateur nouveau de celui qui revient.

---

## Comparez vos réponses

**Étape 1.** Avec session : si Firebase a déjà l'utilisateur actuel quand l'application fait sa lecture, l'application
s'ouvre directement sur l'accueil, avec votre nom, et la fenêtre des comptes ne s'ouvre pas. C'est ce que mesurent les
tests, et ce qu'a vu l'essai avec un compte réel ; avec une seule lecture, ce n'est pas une garantie (étape 2). Sans
session : elle s'ouvre sur la bienvenue ; en touchant « Entrar », vous arrivez à l'écran avec les boutons, et en vous
connectant avec Google, oui, la fenêtre s'ouvre.

**Étape 3.** À l'extérieur. Firebase a oublié d'abord, et c'est ce qui décide si quelqu'un reste à l'intérieur. L'avis au
téléphone ne change que les options offertes la prochaine fois ; s'il échoue, il ne laisse personne à l'intérieur.

**Étape 5.**

1. Fausse. `clearCredentialState` demande seulement de nettoyer l'état qu'un fournisseur conservait pour votre
   application ; le compte Google reste sur le téléphone.
2. Fausse. Le jeton d'identité dure une heure, mais Firebase en demande un autre avec celui d'actualisation : les
   sessions sont de longue durée. Celui d'actualisation expire ou est révoqué pour d'autres raisons (l'utilisateur est
   supprimé ou désactivé, le mot de passe ou l'adresse e-mail change, ou le serveur le révoque), pas par le simple
   passage du temps.
3. Vraie. L'application lit `currentUser` une fois à l'ouverture, et avec un utilisateur actuel elle va directement à
   l'accueil ; les tests mesurent que la fenêtre ne s'ouvre pas. Que Firebase l'ait déjà prêt à l'ouverture est une
   autre question (étape 2).
4. Vraie. La référence de `signOut()` dit qu'il ferme la session de l'utilisateur actuel et le supprime du cache sur
   disque ; `getCurrentUser()` renvoie `null` s'il n'y en a aucun.
5. Fausse. `providerData` sont les identités liées à votre utilisateur Firebase, et fermer la session ne les délie pas :
   ni `signOut` ni `clearCredentialState` ne touchent à ce lien.

**Pour conclure :**

1. Parce que l'application ne le lui demandait jamais : elle s'ouvrait toujours sur la bienvenue. Firebase, lui,
   conservait bien l'utilisateur.
2. Elle demande à Firebase d'oublier l'utilisateur et prévient les fournisseurs du téléphone. D'abord Firebase, parce
   que c'est cela qui ferme la session ; l'avis peut échouer sans laisser personne à l'intérieur.
3. Il démontre ce que décide votre session à l'ouverture, ce qu'elle demande en fermant (aussi avec la vraie porte) et
   sur quel écran arrive l'application, avec une mémoire factice. Il ne démontre pas que Firebase se souvienne de votre
   utilisateur sur votre téléphone, ni ce qu'offre la fenêtre des comptes après la fermeture.
4. Parce que Firebase utilise le jeton d'actualisation pour obtenir un nouveau jeton d'identité. Fermer la session dans
   l'application ne révoque pas ce jeton sur le serveur : cela nettoie l'état du téléphone.

---

## Sources de la leçon

Ouvertes le 19 nov. 2026.

- Firebase. *Authenticate with Google on Android* (vérifier `currentUser` au démarrage de l'Activity ; pour sortir, `signOut` et `clearCredentialState`, avec sa `ClearCredentialException`). https://firebase.google.com/docs/auth/android/google-signin
- Firebase. *Manage Users in Firebase* (Android) (`getCurrentUser` peut renvoyer `null` si Firebase n'a pas fini de se préparer, ou un utilisateur dont le jeton n'est plus valide ; `AuthStateListener` ; `getProviderData`). https://firebase.google.com/docs/auth/android/manage-users
- Firebase. *Manage User Sessions* (sessions de longue durée ; jeton d'identité d'une heure ; quand expire celui d'actualisation ; réinitialiser le mot de passe le révoque ; `revokeRefreshTokens` depuis le serveur). https://firebase.google.com/docs/auth/admin/manage-sessions
- Firebase. Référence de `FirebaseAuth` (`getCurrentUser` ; `signOut` ferme la session et supprime l'utilisateur du cache sur disque ; `addAuthStateListener`). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth
- Firebase. Référence de `FirebaseUser` (`getProviderData` : les identités liées à l'utilisateur ; `getDisplayName`). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseUser
- Firebase. *Authenticate Using Microsoft on Android* (pour sortir, `signOut` ; ne mentionne pas la session du navigateur). https://firebase.google.com/docs/auth/android/microsoft-oauth
- Android Developers. Référence de `CredentialManager` (`clearCredentialState` : quand l'appeler et pour quoi). https://developer.android.com/reference/kotlin/androidx/credentials/CredentialManager
- Essai de la série, 13 nov. 2026, Mac avec Apple silicon, avec un code de test et un compte réel : en rouvrant, l'application a salué avant que l'on touche le bouton ; en se reconnectant avec Microsoft, sa page montrait déjà le compte connecté.
- Nos propres mesures : Mac avec Apple silicon, 19 nov. 2026. Le `verifica` sur `inicio/` et `solucion/`, deux fois de suite chacun, chaque étape et chaque sabotage, avec des fichiers inventés et sans toucher à la console de Firebase ni à aucun compte.
- Mesure sous Linux Mint 22.3, 19 nov. 2026 : le `verifica` sur `solucion/` depuis un clone neuf, 82 sur 82.
