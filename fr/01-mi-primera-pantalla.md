# Leçon 01 — Mon premier écran tourne

**Avant de lire, sept mots.** Une ligne chacun ; les autres mots de la série arrivent dans la leçon où ils servent.

- **Kotlin :** le langage dans lequel vous écrivez l'application. Ses fichiers se terminent par `.kt`.
- **Compose** (Jetpack Compose) : la bibliothèque d'Android pour construire des écrans en écrivant des fonctions Kotlin.
- **Fonction composable :** une fonction marquée `@Composable` qui décrit une partie de l'écran.
- **Maquette :** le dessin de ce à quoi l'écran doit ressembler. C'est avec elle que vous comparez.
- **Modificateur :** ce qui dit à un élément combien de place il occupe et quelle marge il laisse.
- **État :** une donnée que l'écran retient et qui peut changer, comme « le bouton a déjà été touché ».
- **Test simulé :** un test qui dessine votre écran dans la mémoire de votre ordinateur, sans émulateur, et y cherche ce
  qui devrait s'y trouver.

**Ce que vous en retirez :** l'écran d'accueil de la boutique —nom, slogan et bouton— écrit par vous, et savoir lire un
écran Compose ligne par ligne et dire ce que chacune change à l'écran.

**Idée nouvelle de cette leçon (la seule qui est évaluée) :** en Compose, un écran est une fonction qui décrit ce qui
s'affiche. Il n'y a pas de fichier de mise en page à part : vous lisez la fonction et vous savez ce qui va apparaître.

**Une note sur les textes de l'application.** Le projet du cours livre ses textes d'écran en espagnol, et les fichiers
de `lecciones/` ne sont pas traduits : les vérifications automatiques du cours (les `verifica`) cherchent ces textes
exacts. Donc, dans les blocs de code, les sorties de terminal et les maquettes d'écran, les textes restent tels que le
projet les contient, et dans la prose la leçon les cite en français avec l'original entre parenthèses la première
fois : le nom « La boutique » (dans le projet, « La tienda »), le slogan « Vos commandes, dans votre poche » (« Tus
órdenes, en tu bolsillo »), le bouton « Entrer » (« Entrar ») et le salut « Bonjour ! » (« ¡Hola! »).

---

## La scène

Vous demandez à Claude Code l'écran d'accueil. En peu de temps, vous avez un court fichier Kotlin. Il compile. Les tests
passent au vert. Vous l'installez, vous touchez « Entrer » et le salut apparaît. Vous fermez l'ordinateur.

Le lendemain, quelqu'un ouvre l'application à côté de la maquette et vous demande : « pourquoi le salut s'affiche
au-dessus du bouton, alors qu'ici il va en dessous ? » Vous ouvrez le fichier. Vous voyez `remember`, `mutableStateOf`,
`Modifier`, un `if` au milieu de l'écran. Vous ne savez pas laquelle de ces lignes déplace le salut.

Ce code existe : c'est le listing de l'exercice 1.3, et ses tests passent 4 sur 4 au vert avec le salut au mauvais
endroit (mesuré le 18 novembre 2026). Le vert dit que le salut apparaît. Il ne dit pas où.

C'est pourquoi la leçon suit cet ordre : vous lisez un écran, vous en écrivez un à la main, vous demandez un changement
à Claude Code et, à la fin, vous expliquez —sans aide— ce que fait chaque bloc de ce qu'il a généré.

---

## Avant de commencer

**Ce dont vous avez besoin :**

| | Convient | Ne convient pas |
|---|---|---|
| Leçon précédente | La [00](00-ambiente-listo.md) terminée : `ANDROID_HOME` défini et un émulateur ou votre téléphone | La sauter : sans le SDK, le projet ne compile pas |
| Savoir | Programmer dans un langage et avoir réalisé une interface | Kotlin et Compose ne sont pas supposés connus |

**Le projet de départ.** Depuis la racine du dépôt de la série :

```bash
git pull
cd lecciones/01-mi-primera-pantalla/inicio
```

C'est là que vous travaillez. À côté se trouve `solucion/`, avec l'écran résolu : il sert à comparer à la fin, pas à
copier.

**Ce que le projet apporte déjà (échafaudage).** Vous ne l'écrivez pas et il n'est pas évalué ici, mais il est utile de
savoir pourquoi il est là :

| Ce qu'il apporte | Où | Pourquoi |
|---|---|---|
| `org.gradle.jvmargs=-Xmx4g` | `gradle.properties` | Donne 4 Go de mémoire à Gradle, l'outil qui compile. Sans elle, compiler avec Firebase (leçon 03) échouait avec « Java heap space » (mesuré le 13 nov. 2026) |
| `gradlew` déjà généré | racine du projet | Le script qui télécharge et lance la version exacte de Gradle. Le générer avec `gradle wrapper` **à l'intérieur** d'un projet avec AGP 9.4 a échoué (mesuré le 13 nov.). Ne le régénérez pas |
| `sdk=36` | `app/src/test/resources/robolectric.properties` | Les tests simulés utilisent une copie d'Android 36. Sans ce réglage, ils prenaient Android 37 et aucun ne tournait (mesuré le 13 nov.) |
| `jvmToolchain(25)` | `app/build.gradle.kts` | Lors de l'essai du 13 nov., Robolectric, en chargeant son Android 36, n'a pas démarré avec Java 17 et a demandé Java 21 ou plus. C'est pourquoi le projet fixe 25 |

**La maquette.** Voici ce qui doit s'afficher à la fin :

```text
┌────────────────────────────────┐
│                                │
│           La tienda            │  ← nom, en grandes lettres
│  Tus órdenes, en tu bolsillo   │  ← slogan
│          [ Entrar ]            │  ← bouton
│            ¡Hola!              │  ← n'apparaît qu'après avoir touché « Entrar »
│                                │
└────────────────────────────────┘
  Tout centré, à mi-écran, avec 24 dp de marge.
```

**dp** est l'unité d'Android pour les tailles : à peu près un pixel sur un écran de densité moyenne, ajustée pour
paraître de la même taille sur les écrans plus denses.

---

## Étape 1 · Lisez un écran avant de le lancer

Ouvrez `app/src/main/java/mx/habil/ejemplo/tienda/Bienvenida.kt`. Voici comment il se présente :

```kotlin
@Composable
fun Bienvenida() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Aquí va tu pantalla")
        Text("Lección 01")
    }
}
```

**Prédisez d'abord.** À la racine du dépôt, créez le dossier des réponses s'il n'existe pas encore (`mkdir -p
respuestas`) et, dedans, le fichier `respuestas/01.md`. C'est là que vont vos prédictions et vos explications.
Écrivez, avant de lancer quoi que ce soit, trois lignes : combien de textes vont s'afficher ? l'un à côté de l'autre ou
l'un sous l'autre ? collés à gauche ou centrés ?

Pour prédire, lisez la fonction de l'extérieur vers l'intérieur :

- **`@Composable`** marque la fonction comme une fonction qui décrit un écran.
- **`fun Bienvenida()`** est une fonction Kotlin. Elle ne renvoie rien : elle décrit ce qui doit s'afficher.
- **`Column( … ) { … }`** dispose ce qu'elle contient en séquence verticale : l'un sous l'autre.
- **`Text("…")`** affiche un texte. Chaque appel est un élément.
- **`Modifier.fillMaxSize().padding(24.dp)`** dit que la colonne occupe tout l'espace qui lui est donné et laisse 24 dp
  de marge.
- **`verticalArrangement = Arrangement.Center`** place le contenu à mi-hauteur. Pour l'horizontal, personne n'a rien
  dit, et dans une `Column` cela commence, par défaut, à `Alignment.Start` : le côté où commence le texte.

**Maintenant, lancez-la.** Avec l'émulateur ouvert ou votre téléphone branché :

```bash
./gradlew installDebug      # compile et installe ; n'ouvre pas l'application
```

Ouvrez-la depuis le tiroir d'applications et comparez avec vos trois lignes. Si une prédiction s'est révélée fausse, ne
l'effacez pas : écrivez en dessous quelle ligne du code vous aurait donné la réponse. Sur l'émulateur du test (Pixel 9,
Android 37, 18 nov. 2026), deux textes se sont affichés, « Aquí va tu pantalla » (Votre écran va ici) et en dessous « Lección 01 »
(Leçon 01), à mi-hauteur et collés à gauche.

**Seconde prédiction.** Et si vous retirez la `Column` et laissez les deux `Text` seuls ? Selon la documentation
d'Android, Compose superpose un texte sur l'autre. Essayez, notez ce que vous voyez et remettez la `Column`.

**Qui appelle `Bienvenida()` ?** `MainActivity.kt`, dans le même dossier : sa ligne `setContent { Bienvenida() }` dit
quelle fonction dessine l'écran. Vous ne la changez pas.

**Ce qui le prouve :** votre prédiction écrite, comparée à l'exécution (objectif 1.1).

---

## Étape 2 · Kotlin : ce que vous connaissez déjà sous un autre nom

Il suffit de reconnaître trois choses que vous utilisez déjà dans un autre langage.

| En Kotlin | Ce que c'est | Où vous le voyez |
|---|---|---|
| `val nombre = "La tienda"` | Une variable affectée une seule fois et qui ne change plus | `val pantalla = …` dans `BienvenidaTest.kt` |
| `var saludando = false` | Une variable qui, elle, peut changer | Vous l'écrivez à l'étape 4 |
| `fun doble(n: Int): Int { return n * 2 }` | Une fonction : nom, paramètres avec leur type et type de retour | `fun Bienvenida()` |
| `fun doble(n: Int) = n * 2` | La même, en court : le corps est une expression et le type de retour est déduit | |
| `Bundle?` | Un type qui **peut** être nul. Sans le `?`, il ne peut pas | `savedInstanceState: Bundle?` dans `MainActivity.kt` |

Trois détails qui, eux, changent par rapport à d'autres langages :

- **Le type va après le nom :** `n: Int`. Pour une variable, vous ne l'écrivez presque jamais : dans `val x = 5`,
  Kotlin déduit que c'est un `Int`.
- **Les nuls se déclarent.** Une variable `String` ne peut pas contenir `null`. Si elle peut manquer, le type est
  `String?`, et alors `lema.length` provoque une erreur à la compilation ; `lema?.length` renvoie `null` si `lema` est
  nul ; et `lema?.length ?: 0` renvoie `0` dans ce cas.
- **Les accolades finales sont une fonction que vous passez à une autre.** Dans `Column( … ) { … }`, ce qui se trouve
  entre accolades est une fonction sans nom (une *lambda*). Si le dernier paramètre d'une fonction est une autre
  fonction, Kotlin permet de l'écrire en dehors des parenthèses. C'est pourquoi Compose ressemble à des blocs imbriqués.

**Ce qui le prouve :** la révision 1.4, dans « Vos exercices ».

---

## Étape 3 · Écrivez l'accueil, à la main

Cette étape est la vôtre, sans Claude Code : ce sont une dizaine de lignes.

**Prédisez d'abord.** De combien d'appels à des fonctions composables aurez-vous besoin dans la `Column`, et
lequel d'entre eux va en contenir un autre ?

Dans `Bienvenida.kt` :

1. **Ajoutez trois lignes au bloc d'`import`.** Un `import` dit à Kotlin de quelle bibliothèque vient chaque nom :

```kotlin
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
```

2. **Centrez l'horizontal.** Mettez une virgule après `Arrangement.Center` et, en dessous, le paramètre qui manquait :

```kotlin
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
```

3. **Remplacez les deux textes par les trois éléments de la maquette :**

```kotlin
        Text("La tienda", style = MaterialTheme.typography.headlineLarge)
        Text("Tus órdenes, en tu bolsillo")
        Button(onClick = { }) {
            Text("Entrar")
        }
```

`style = MaterialTheme.typography.headlineLarge` donne au nom l'une des tailles de caractères de Material, l'ensemble de
composants visuels d'Android. `Button` prend deux choses : `onClick`, la fonction appelée quand quelqu'un le touche
—pour l'instant vide—, et, entre accolades, ce qui s'affiche à l'intérieur du bouton, qui est un autre `Text`.

**Lancez le `verifica`.** Depuis la racine du dépôt :

```bash
./lecciones/01-mi-primera-pantalla/verifica
```

Il ne modifie pas votre code : il lance les tests simulés (`./gradlew testDebugUnitTest`) et vous traduit le résultat.
La première fois, il a besoin du réseau et prend du temps, car il télécharge la bibliothèque de tests (Robolectric) et
une copie d'Android : lors de l'essai du 13 nov., cela a pris 1 minute 21 secondes sur un Mac ; les suivantes, quelques
secondes.

**Vous êtes sur la bonne voie** si vous voyez trois coches vertes et une rouge :

```text
✅ Simulado: la pantalla muestra el nombre «La tienda»
✅ Simulado: la pantalla muestra el lema «Tus órdenes, en tu bolsillo»
✅ Simulado: hay un elemento «Entrar» con acción de toque y rol de botón
❌ Al tocar el botón «Entrar» no aparece «¡Hola!» (o ya estaba desde antes): ese cambio lo pides en el paso 4
Resultado: 3 de 4 pruebas en verde (1 en rojo, 0 con error, 0 saltadas), leído de …
```

*(C'est la sortie du `verifica`, exactement comme le script l'affiche, en espagnol. En français, elle dit : l'écran
affiche le nom ; l'écran affiche le slogan ; il y a un élément « Entrer » avec une action de toucher et un rôle de
bouton ; et, en rouge, « en touchant le bouton « Entrer », « Bonjour ! » n'apparaît pas (ou y était déjà avant) : ce
changement, vous le demandez à l'étape 4 » ; et le résultat, 3 tests sur 4 au vert.)*

Ce rouge est le rouge attendu : le bouton ne fait encore rien. La coche pour `respuestas/01.md`, que vous n'avez pas
encore remplie, sort elle aussi en rouge.

**Ce que chaque coche prouve, et ce qu'elle ne prouve pas.** « Simulado » (simulé) signifie que le test a dessiné votre
fonction en mémoire et y a trouvé ce texte, et que « Entrer » peut être touché et se déclare comme un bouton (un
`Text` auquel vous ajoutez seulement un toucher ne passe pas). Cela ne prouve ni la taille des caractères, ni la marge,
ni le centrage, ni l'ordre : cela, vous le voyez vous-même, avec `./gradlew installDebug` et la maquette à côté.

**Ce qui le prouve :** le `verifica` simulé trouve les trois éléments **et** vous avez comparé l'application installée
avec la maquette (objectif 1.2).

---

## Étape 4 · Avec Claude Code : que le bouton salue

C'est ici qu'entre l'outil, et il entre toujours de la même manière : **vous prédisez → vous demandez → vous lisez ce
qu'il a généré → vous lancez le `verifica` → vous l'expliquez avec vos propres mots.**

1. **Prédisez.** Écrivez dans `respuestas/01.md` : pour que « Bonjour ! » n'apparaisse qu'après avoir touché le bouton,
   que doit retenir l'écran ? Laquelle des lignes que vous avez déjà écrites va changer ?
2. **Demandez** à Claude Code, depuis `inicio/` : « Dans `Bienvenida.kt`, fais en sorte qu'en touchant "Entrar" le texte
   "¡Hola!" apparaisse sous le bouton. Avant de toucher, il ne doit pas être visible. Ne change rien d'autre et ne
   touche pas aux tests. »
3. **Lisez** le changement complet avant de l'accepter. Il n'a pas à être identique à ceci, mais il doit contenir ces
   trois éléments :

```kotlin
    var saludando by remember { mutableStateOf(false) }      // 1. l'état
    …
        Button(onClick = { saludando = true }) {             // 2. l'événement
            Text("Entrar")
        }
        if (saludando) {                                     // 3. l'écran lit l'état
            Text("¡Hola!")
        }
```

4. **Lancez le `verifica`.** Maintenant, les quatre tests doivent passer au vert.
5. **Expliquez**, en deux lignes de `respuestas/01.md`, ce qui se passe entre le moment où vous touchez le bouton et
   celui où le salut apparaît.

Aujourd'hui, vous ne concevez pas l'état de zéro : vous suivez seulement le changement généré par Claude Code. Quel
état il convient de conserver et comment le conserver est l'idée centrale de la L02. Pour le suivre, vous devez savoir
ce que fait chaque élément :

- **`mutableStateOf(false)`** crée une valeur que Compose surveille. Quand elle change, Compose programme la mise à jour
  de la partie de l'écran qui l'a lue. Cela s'appelle la **recomposition**.
- **`remember { … }`** conserve cette valeur tant que cet écran reste vivant. Ce n'est pas une sauvegarde sur disque :
  si Android recrée l'écran, elle revient à `false`.
- **`by`** permet d'utiliser `saludando` comme une variable normale. Il demande deux `import` :
  `androidx.compose.runtime.getValue` et `androidx.compose.runtime.setValue`.
- **`onClick = { saludando = true }`** est l'événement : il ne fait que changer l'état. Il ne dessine rien.
- **`if (saludando) { Text("¡Hola!") }`** est du Kotlin normal à l'intérieur de l'écran : si l'état est `true`, ce texte
  existe ; sinon, non.

L'idée de la leçon, complète : vous touchez → l'état change → Compose met à jour ce qui a lu cet état, ici le `if` →
la description de l'écran inclut maintenant le salut. Personne n'« ajoute » le texte. Et Compose peut ne ré-exécuter
que le nécessaire : n'écrivez pas de code en supposant que toute la fonction sera exécutée.

**Si une ligne n'est toujours pas claire,** faites une pause ici : demandez à Claude Code de l'expliquer, vérifiez-la
avec un petit changement et le `verifica`, et continuez quand vous pourrez la dire avec vos propres mots.

---

## Étape 5 · Sans IA : expliquez cinq blocs et corrigez-en un

C'est l'exercice 1.3 et il se fait **sans Claude Code**, sur votre propre `Bienvenida.kt`. Si le vôtre respecte déjà la
maquette, utilisez pour la correction ce listing, qui est une réponse possible à la même demande :

```kotlin
19  @Composable
20  fun Bienvenida() {
21      var saludando by remember { mutableStateOf(false) }
22
23      Column(
24          modifier = Modifier.fillMaxSize().padding(24.dp),
25          verticalArrangement = Arrangement.Center,
26          horizontalAlignment = Alignment.CenterHorizontally
27      ) {
28          Text("La tienda", style = MaterialTheme.typography.headlineLarge)
29          Text("Tus órdenes, en tu bolsillo")
30          if (saludando) {
31              Text("¡Hola!")
32          }
33          Button(onClick = { saludando = true }) {
34              Text("Entrar")
35          }
36      }
37  }
```

Avec ce listing, le `verifica` donne 4 sur 4 au vert (mesuré le 18 nov. 2026). Et pourtant il ne respecte pas la maquette.

**Vous faites deux choses, par écrit, dans `respuestas/01.md` :**

1. **Expliquez cinq blocs,** un de chaque type : **état**, **texte**, **modificateur**, **événement** et **fonction**.
   Pour chacun, écrivez sa ligne au format `Bienvenida.kt:21` et ce qui change **à l'écran** si vous le retirez ou le
   modifiez. « Conserve un booléen » n'est pas un effet visible ; « sans cette ligne, le salut n'apparaît jamais » en est
   un.
2. **Corrigez le bloc qui ne respecte pas la maquette.** Dites lequel c'est et pourquoi les tests ne le détectent pas,
   corrigez-le, lancez le `verifica` et réinstallez l'application.

Le `verifica` vérifie seulement que vous avez noté au moins cinq références au format `Bienvenida.kt:N` ; il n'évalue ni
vos explications ni la correction. Et un 4 sur 4 confirme un comportement, pas l'apparence. Le 1.3 est terminé quand
vous pouvez cocher les quatre cases :

- [ ] Les cinq types sont là : état, texte, modificateur, événement et fonction.
- [ ] Chacun donne un effet visible à l'écran.
- [ ] Vous avez expliqué pourquoi le salut s'affichait au-dessus et pourquoi les tests ne l'ont pas détecté.
- [ ] Après l'avoir déplacé, le 4 sur 4 reste au vert **et** dans l'application installée, vous avez vu « ¡Hola! »
      sous « Entrar ». Notez cette observation à côté de la correction.

**Ce qui le prouve :** exercice 1.3 (objectif 1.3).

---

## Vos exercices

Le 1.2 et le 1.3 se font **sans IA**.

| # | Vous faites | C'est fait quand |
|---|---|---|
| 1.1 | Vous prédisez ce que dessine la fonction de l'étape 1 et vous la lancez | Vous avez vos trois lignes et ce que vous avez vu |
| 1.2 | Vous écrivez nom, slogan et bouton à partir de la maquette | Le `verifica` marque en vert les trois éléments **et** vous avez comparé l'application installée avec la maquette : groupe centré et nom en grandes lettres |
| 1.3 | Vous expliquez cinq blocs et corrigez celui qui ne respecte pas la maquette | Vous avez coché les quatre cases de l'étape 5 : le 4 sur 4 reste au vert **et** vous avez vu le salut sous le bouton dans l'application installée |
| 1.4 | Vous répondez à la révision de Kotlin | Quatre réponses sur cinq correctes, **chacune avec son pourquoi** |

**Révision 1.4.** Répondez sans lancer le code. Le `verifica` ne note ni vos prédictions ni cette révision : comparez-les
vous-même avec « Comparez vos réponses » et conservez ce que vous avez écrit.

1. `val nombre = "La tienda"` puis, à la ligne suivante, `nombre = "Otra"`. Est-ce que cela compile ?
2. `var saludando = false`. Personne n'a écrit le type. Quel est-il et qui l'a décidé ?
3. `fun doble(n: Int) = n * 2`. Que renvoie `doble(4)` et de quel type est-ce ?
4. Dans `MainActivity.kt`, il est écrit `savedInstanceState: Bundle?`. Que vous signale ce `?` ?
5. `val lema: String? = null`. Que se passe-t-il avec `lema.length`, avec `lema?.length` et avec `lema?.length ?: 0` ?

**Durée :** comptez environ deux heures, avec la compilation et vos prédictions. C'est une estimation : nous
l'ajusterons avec le temps que prendra la première cohorte.

---

## Ça ne tourne pas ?

Les messages en anglais sont ceux que le compilateur de la série (Kotlin 2.4.21) a donnés le 18 nov. 2026.

| Vous voyez | Cause probable | Ce que vous faites |
|---|---|---|
| `Unresolved reference 'Button'` | L'`import` de ce nom manque, ou il est mal écrit | Ajoutez l'`import` de l'étape 3 et vérifiez les majuscules |
| `'val' cannot be reassigned` | Vous avez déclaré l'état avec `val` puis vous le modifiez | C'est `var saludando by remember { … }` |
| `Property delegate must have a 'getValue(…)' method` | Vous avez utilisé `by` sans ses deux `import` | Ajoutez `getValue` et `setValue` (étape 4) |
| « Las pruebas no llegaron a correr » (les tests n'ont pas pu s'exécuter) | Le code ne compile pas | Lisez la ligne qui commence par `e:` : elle contient fichier, ligne et colonne |
| ❌ sur le bouton, avec « Entrer » visible | C'est un `Text`, seul ou avec `clickable` : il ne se déclare pas comme un bouton | Cela doit être un `Button` avec le `Text` à l'intérieur |
| ❌ sur le salut, alors que « ¡Hola! » s'affiche | Le salut est toujours là, pas seulement après avoir touché | Il va à l'intérieur du `if (saludando)` |
| `BienvenidaTest.kt` a changé | Quelqu'un (vous ou Claude Code) a touché aux tests | Récupérez-le avec la commande que donne le message et répétez la demande en précisant de ne pas y toucher |

Sur Linux Mint 22.3 (x86_64), le `verifica` de cette leçon est sorti au vert sur la solution : 4 sur 4, en 64 secondes en
comptant la première compilation (18 nov. 2026). Si votre SDK n'apporte pas `build-tools`, Gradle le télécharge tout
seul la première fois.

---

## Pour conclure

**Répondez sans relire :**

1. Qu'est-ce qu'un écran en Compose, en une phrase ?
2. Vous touchez « Entrer » et « Bonjour ! » apparaît. Que s'est-il passé entre les deux, dans l'ordre ?
3. Vos quatre tests simulés sont au vert. Dites une chose que cela prouve et une qu'il ne prouve pas.

**La suite :** dans la L02, l'application compte plus d'un écran et passe de l'un à l'autre.

---

## Comparez vos réponses

**Étape 1.** Deux textes, l'un sous l'autre (`Column`), à mi-hauteur (`Arrangement.Center`) et du côté gauche (personne
n'a indiqué l'alignement horizontal).

**Étape 3.** Quatre appels : `Text`, `Text`, `Button` et, à l'intérieur du bouton, un autre `Text`.

**Exercice 1.3**, sur le listing de l'étape 5 :

| Bloc | Ligne | Effet visible |
|---|---|---|
| État | `Bienvenida.kt:21` | Sans elle, le salut ne peut pas apparaître après le toucher |
| Texte | `Bienvenida.kt:28` | C'est le nom ; sans `style`, il serait de la même taille que le slogan |
| Modificateur | `Bienvenida.kt:24` | Sans `fillMaxSize()`, la colonne n'occupe que ce que mesure son contenu : il n'y a plus d'espace où centrer |
| Événement | `Bienvenida.kt:33` | Avec `onClick = { }`, le bouton peut être touché, mais il ne se passe rien |
| Fonction | `Bienvenida.kt:20` | C'est l'écran complet ; `MainActivity` l'appelle |

Le bloc qui ne respecte pas la maquette est celui des lignes 30 à 32 : il se trouve **avant** le bouton, donc le salut
s'affiche au-dessus de « Entrer » alors que la maquette le demande en dessous. Il se corrige en déplaçant ces trois
lignes après la 35. Les tests ne le détectent pas parce qu'ils vérifient que le texte existe et se voit, pas à quelle
position il se trouve.

**Révision 1.4 :**

1. Non. Un `val` ne peut pas être réaffecté ; le compilateur dit `'val' cannot be reassigned`.
2. `Boolean`. Kotlin l'a déduit à partir de la valeur `false`.
3. Elle renvoie `8`, de type `Int` : le corps est une expression et le type de retour est déduit.
4. Que ce paramètre peut valoir `null`, et Kotlin ne vous laisse pas l'utiliser sans tenir compte de ce cas.
5. `lema.length` ne compile pas ; `lema?.length` donne `null` ; `lema?.length ?: 0` donne `0`.

**Pour conclure :**

1. Une fonction marquée `@Composable` qui décrit ce qui s'affiche.
2. Le `onClick` a changé l'état en `true` ; Compose a ré-exécuté la fonction ; le `if` inclut maintenant le texte.
3. Cela prouve que les trois éléments et le salut existent et que le bouton répond. Cela ne prouve ni leur apparence ni
   l'ordre dans lequel ils se trouvent.

---

## Sources de la leçon

Ouvertes le 18 nov. 2026.

- Android Developers. *Thinking in Compose*. https://developer.android.com/develop/ui/compose/mental-model
- Android Developers. *Jetpack Compose Tutorial*. https://developer.android.com/develop/ui/compose/tutorial
- Android Developers. *Compose layout basics*. https://developer.android.com/develop/ui/compose/layouts/basics
- Android Developers. *Column* (valeurs par défaut). https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/Column.composable
- Android Developers. *Compose modifiers*. https://developer.android.com/develop/ui/compose/modifiers
- Android Developers. *State and Jetpack Compose*. https://developer.android.com/develop/ui/compose/state
- Android Developers. *Button*. https://developer.android.com/develop/ui/compose/components/button
- Android Developers. *Style text* et *Material Design 3 in Compose*. https://developer.android.com/develop/ui/compose/text/style-text · https://developer.android.com/develop/ui/compose/designsystems/material3
- Android Developers. *Support different pixel densities* (dp). https://developer.android.com/training/multiscreen/screendensities
- Android Developers. *Test your Compose layout*. https://developer.android.com/develop/ui/compose/testing
- Android Developers. *Role* et *SemanticsMatcher* (rôle de bouton dans les tests). https://developer.android.com/reference/kotlin/androidx/compose/ui/semantics/Role · https://developer.android.com/reference/kotlin/androidx/compose/ui/test/SemanticsMatcher
- Android Developers. *Robolectric strategies*. https://developer.android.com/training/testing/local-tests/robolectric
- Kotlin. *Basic syntax*. https://kotlinlang.org/docs/basic-syntax.html
- Kotlin. *Null safety*. https://kotlinlang.org/docs/null-safety.html
- Kotlin. *Higher-order functions and lambdas*. https://kotlinlang.org/docs/lambdas.html
- Nos propres mesures : essai sur un Mac avec Apple silicon, 13 nov. 2026 (mémoire de Gradle, `gradle wrapper`, Java et Android des tests simulés) et 18 nov. 2026 (le `verifica` sur `inicio/`, `solucion/` et le listing du 1.3 ; messages du compilateur).

Revue externe (18 nov. 2026) : AVEC CHANGEMENTS, 3 de priorité haute, 5 moyenne et 2 basse ; appliquée.
