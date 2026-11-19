# Leçon 02 — Des écrans qui changent

**Avant de lire, sept mots.** Une ligne chacun ; les autres mots de la série arrivent dans la leçon où ils servent.

- **État :** une donnée que l'écran retient et qui peut changer. Vous l'avez rencontré dans la 01 ; ici, c'est le sujet.
- **Recomposition :** quand Compose ré-exécute vos fonctions pour mettre l'écran à jour parce qu'une donnée a changé.
- **Événement :** quelque chose qui se produit et dont l'application est informée, comme un toucher sur un bouton.
- **Remonter l'état :** sortir un état d'un écran et le conserver dans la fonction qui l'appelle.
- **Interface :** une liste de ce que l'on peut demander à quelque chose, sans dire comment il s'y prend.
- **Implémentation :** une classe qui, elle, réalise ce que l'interface demande. Il peut y en avoir plusieurs.
- **Doublure de test :** une implémentation faite uniquement pour les tests, qui répond ce dont le test a besoin.

**Ce que vous en retirez :** une application à trois écrans —bienvenue, connexion et accueil— qui passe de l'un à
l'autre, et savoir dire pourquoi l'écran a changé chaque fois : quelle valeur a changé, qui l'a changée et qui l'a lue.

**Idée nouvelle de cette leçon :** l'état. Quand une valeur d'état que l'écran a lue change, Compose **programme une
recomposition** pour mettre à jour le nécessaire, et l'écran est redessiné. Passer d'un écran à un autre, c'est cela
même : une valeur qui change. Vous pratiquez en plus une chose d'appui, qui se vérifie aussi : séparer la décision
de laisser entrer dans un contrat, avec une implémentation réservée aux tests (étape 3).

**Une note sur les textes de l'application.** Le projet du cours livre ses textes d'écran en espagnol, et les fichiers
de `lecciones/` ne sont pas traduits : les vérifications automatiques du cours (les `verifica`) cherchent ces textes
exacts. Donc, dans les blocs de code, les sorties de terminal et les maquettes d'écran, les textes restent tels que le
projet les contient, et dans la prose la leçon les cite en français avec l'original entre parenthèses la première
fois : les écrans « Connectez-vous à votre compte » (« Entra a tu cuenta »), « Bonjour, Ana » (« Hola, Ana ») et « Vous verrez
ici vos commandes » (« Aquí verás tus órdenes »), le bouton « Continuer » (« Continuar ») et l'avis « Il n'est pas
encore possible d'entrer » (« Todavía no se puede entrar »). Ceux de la leçon 01 sont les mêmes : « La boutique » (« La
tienda »), « Entrer » (« Entrar ») et « Bonjour ! » (« ¡Hola! »).

---

## La scène

Vous avez les trois écrans qui fonctionnent. Avant de fermer, vous relisez le fichier pour le laisser propre et un mot
vous gêne : `remember`. La ligne se lit pareil sans lui. Vous l'effacez.

Ça compile. Vous ouvrez l'application : l'écran de bienvenue est identique. Vous touchez « Entrer ».

Il ne se passe rien. Pas de message d'erreur. L'écran de bienvenue est toujours là. Vous touchez encore. Rien.

Ce changement existe et il est mesuré : c'est le « Cassez-en un exprès » de l'étape 4. Avec ce seul mot effacé, les tests
simulés de la leçon passent de 7 sur 7 au vert à 1 sur 7, avec 6 au rouge et aucun en erreur (18 novembre 2026). Rien n'a
explosé. L'écran a cessé de changer, et le code ne dit pas pourquoi.

La réponse tient dans une question : quand vous touchez un bouton, où est conservé ce qui s'est passé ? D'abord vous
prédisez ce que fait une valeur d'état, puis vous construisez le parcours avec elle, et à la fin vous la cassez vous-même
pour l'expliquer.

---

## Avant de commencer

**Ce dont vous avez besoin :** la [01](01-mi-primera-pantalla.md) terminée —ici, `Column`, `Text` et `Button` ne sont plus expliqués— et l'environnement de la [00](00-ambiente-listo.md) : `ANDROID_HOME` défini et un émulateur ou votre téléphone.

**Le projet de départ.** Depuis la racine du dépôt de la série :

```bash
git pull
cd lecciones/02-pantallas-que-cambian/inicio
```

C'est là que vous travaillez. À côté se trouve `solucion/` : elle vous sert à la fin pour confronter vos décisions et
comprendre toute différence. Vos prédictions et explications vont dans `respuestas/02.md`, à la racine du dépôt (`mkdir -p
respuestas` s'il n'existe pas).

**Ce que le projet apporte déjà (échafaudage).** Il n'est pas évalué ici :

| Ce qu'il apporte | Où | Pourquoi |
|---|---|---|
| L'écran de bienvenue de la 01 | `Bienvenida.kt` | Vous partez de ce que vous avez déjà écrit |
| Les tests | `app/src/test/…/RecorridoTest.kt` et `ArranqueTest.kt` | Ce sont ceux que lance le `verifica`. Ne les modifiez pas |
| Mémoire, `gradlew`, Android 36 pour les tests et Java 25 | Comme dans la 01 | Cela a été expliqué dans la [01](01-mi-primera-pantalla.md) |

**La navigation fait aussi partie de l'échafaudage.** Android dispose d'une bibliothèque pour passer d'un écran à un
autre, Navigation. Ici, elle n'est pas utilisée : le parcours se construit avec une valeur d'état et un `when`, que je
vous donne écrits à l'étape 2.

**La maquette.** Trois écrans, un à la fois :

```text
┌──────────────────────┐    ┌──────────────────────┐    ┌──────────────────────┐
│      La tienda       │    │  Entra a tu cuenta   │    │      Hola, Ana       │
│ Tus órdenes, en tu   │ ─▶ │    [ Continuar ]     │ ─▶ │ Aquí verás tus       │
│      bolsillo        │    │ Todavía no se puede  │    │ órdenes              │
│     [ Entrar ]       │    │ entrar (si no pasó)  │    │                      │
└──────────────────────┘    └──────────────────────┘    └──────────────────────┘
   bienvenue (bienvenida)       connexion (entrada)       accueil (inicio)
```

*(Dans la maquette, « si no pasó » signifie « si l'accès n'a pas été accordé ».)*

De la connexion à l'accueil, vous passez en touchant « Continuer », **seulement si quelqu'un décide que vous pouvez
passer**. Qui décide, c'est l'étape 3.

---

## Étape 1 · Prédisez ce que fait une valeur d'état

Cette étape est la vôtre, sans Claude Code. Ouvrez `app/src/main/java/mx/habil/ejemplo/tienda/Bienvenida.kt`. C'est
l'écran de la 01, avec ces trois éléments :

```kotlin
    var saludando by remember { mutableStateOf(false) }      // l'état
    …
        Button(onClick = { saludando = true }) {             // l'événement le change
            Text("Entrar")
        }
        if (saludando) {                                     // l'écran le lit
            Text("¡Hola!")
        }
```

**Prédisez d'abord.** Remplacez `saludando = true` par `saludando = !saludando` (le `!` inverse : de `false` à `true` et
inversement). Écrivez dans `respuestas/02.md`, avant de lancer quoi que ce soit : que voyez-vous après le premier toucher,
après le deuxième et après le troisième ?

**Maintenant, vérifiez.** Avec l'émulateur ouvert ou votre téléphone branché :

```bash
./gradlew installDebug      # compile et installe ; n'ouvre pas l'application
```

Ouvrez l'application, touchez trois fois et comparez. Si votre prédiction était fausse, ne l'effacez pas : notez en
dessous quelle ligne vous aurait donné la réponse.

**Ce qui s'est passé, dans l'ordre.** C'est la chaîne de toute la leçon :

1. Vous touchez le bouton : c'est l'**événement**.
2. Le `onClick` change la valeur. Il ne dessine rien.
3. Compose surveille cette valeur parce qu'elle est née de `mutableStateOf`. Quand elle change, il **programme une
   recomposition** : ré-exécuter ce qui l'a lue et qu'il faut mettre à jour.
4. Dans cet exemple, votre fonction s'exécute de nouveau. `remember` lui renvoie la valeur conservée, pas une nouvelle.
5. Le `if` lit la valeur de maintenant, et la description de l'écran sort différente.

N'écrivez pas de code en supposant combien de fois ni dans quel ordre une fonction `@Composable` sera exécutée : selon
la documentation, Compose peut sauter celles qui n'ont pas changé et en exécuter d'autres de nombreuses fois.

Personne n'« ajoute » ni ne « retire » le salut : la fonction décrit l'écran pour la valeur du moment.

Avant de continuer, remettez à la main la ligne du bouton sur `Button(onClick = { saludando = true })`. Ainsi vous
conservez tout autre changement que vous auriez fait. Pour confirmer que le fichier est revenu comme il était, cette
commande ne doit rien afficher (elle regarde seulement, elle ne modifie pas votre fichier) :

```bash
git diff -- app/src/main/java/mx/habil/ejemplo/tienda/Bienvenida.kt
```

**Ce qui le prouve :** votre prédiction écrite, comparée à l'exécution (objectif 2.1).

---

## Étape 2 · À la main : une valeur décide quel écran s'affiche

Toujours sans Claude Code. Vous allez écrire deux écrans et la fonction qui choisit lequel est dessiné.

**Prédisez d'abord.** Si « sur quel écran suis-je » est une valeur d'état, dans quelle fonction doit-elle vivre : dans
`Bienvenida`, dans l'écran de connexion ou dans une troisième qui appelle les deux ? Écrivez laquelle et pourquoi.

**1. L'écran de bienvenue cesse de conserver l'état et prévient.** Dans `Bienvenida.kt`, supprimez la ligne de `saludando`, le bloc
`if (saludando) { … }` et les quatre `import` d'`androidx.compose.runtime` autres que `Composable`. Changez deux lignes :

```kotlin
fun Bienvenida(alEntrar: () -> Unit) {
    …
        Button(onClick = alEntrar) {
```

`alEntrar: () -> Unit` est un paramètre dont la valeur est une fonction : elle ne reçoit rien (`()`) et ne renvoie pas
de donnée (`Unit`). L'écran l'exécute quand le bouton est touché, et il ne sait pas ce qui se passe ensuite.

**2. L'écran de connexion.** Créez `PantallaEntrada.kt` dans le même dossier. Il commence par `package
mx.habil.ejemplo.tienda` et les mêmes `import` que `Bienvenida.kt` :

```kotlin
@Composable
fun PantallaEntrada(rechazada: Boolean, alContinuar: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Entra a tu cuenta", style = MaterialTheme.typography.headlineLarge)
        Button(onClick = alContinuar) {
            Text("Continuar")
        }
        if (rechazada) {
            Text("Todavía no se puede entrar")
        }
    }
}
```

Lui non plus ne conserve rien : il reçoit une donnée (`rechazada`) et signale un toucher (`alContinuar`).

**3. La fonction qui conserve l'état.** Créez `Tienda.kt` :

```kotlin
package mx.habil.ejemplo.tienda

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

enum class Pantalla { BIENVENIDA, ENTRADA, INICIO }

@Composable
fun Tienda() {
    var pantalla by remember { mutableStateOf(Pantalla.BIENVENIDA) }

    when (pantalla) {
        Pantalla.BIENVENIDA -> Bienvenida(alEntrar = { pantalla = Pantalla.ENTRADA })
        Pantalla.ENTRADA -> PantallaEntrada(rechazada = false, alContinuar = { })
        Pantalla.INICIO -> Text("Aquí va el inicio")
    }
}
```

- **`enum class Pantalla { … }`** est une liste fermée : une `Pantalla` ne peut être que l'une de ces trois valeurs.
- **`when (pantalla) { … }`** exécute la branche de cette valeur. S'il vous en manque une des trois, ça ne compile pas.
- **`alEntrar = { pantalla = Pantalla.ENTRADA }`** est la fonction que vous passez à l'écran de bienvenue. Lui prévient ; ici,
  c'est l'état qui est modifié.

**4. Faire démarrer l'application sur `Tienda`.** Dans `MainActivity.kt`, la ligne de `setContent` devient
`setContent { Tienda() }`.

**Vérifiez-le dans l'application.** `./gradlew installDebug`, ouvrez-la et touchez « Entrer » : l'écran « Connectez-vous à
votre compte » doit s'afficher et l'écran de bienvenue ne doit plus se voir. « Continuer » ne fait encore rien.
Avec la solution terminée, sur l'émulateur du test (Pixel 9, Android 37, 18 nov. 2026), voici ce qui s'est affiché : l'écran de bienvenue avec
« La tienda », « Tus órdenes, en tu bolsillo » et « Entrar » ; en touchant « Entrar », « Entra a tu cuenta » et
« Continuar » ; et en touchant « Continuar », l'avis « Todavía no se puede entrar ». L'écran d'accueil
n'apparaît pas dans l'application installée : seuls les tests y arrivent.

**Ce que vous venez de faire.** L'état n'est resté dans aucun écran : vous l'avez remonté dans la fonction qui les
appelle. C'est **remonter l'état**, et la documentation d'Android le recommande : l'état vit dans l'ancêtre commun le
plus proche de ceux qui le lisent et le modifient ; vers le bas voyagent des données, vers le haut voyagent des
événements.

**Le `verifica` ne peut pas encore lancer les tests.** Depuis la racine du dépôt :

```bash
./lecciones/02-pantallas-que-cambian/verifica
```

Ce rouge est celui attendu **seulement** à la fin de l'étape 2, et seulement parce qu'il manque encore `Entrada`,
`EntradaFalsa` et le paramètre `entrada` de `Tienda`. **Vous êtes sur la bonne voie** si vous voyez parmi les lignes
celles-ci (mesuré le 18 nov. 2026 ; celles de Gradle se répètent plusieurs fois) :

```text
❌ No encontré «interface Entrada» en src/main: sin ella las pruebas no compilan (paso 3)
✅ Texto: en src/main hay un «fun Tienda(»
❌ No encontré «class EntradaFalsa» en src/test: la escribes tú en EntradaFalsa.kt (paso 3)
ℹ️  Estado guiado del paso 2: arriba faltan piezas del paso 3, y mientras falten las pruebas no compilan.
❌ Las pruebas no llegaron a correr. Esto dijo Gradle:
     e: RecorridoTest.kt:45:38 No parameter with name 'entrada' found.
     e: RecorridoTest.kt:45:48 Unresolved reference 'EntradaFalsa'.
Resultado: 0 pruebas corridas.
```

*(C'est la sortie du `verifica`, exactement comme le script l'affiche, en espagnol. En français, elle dit : « Je n'ai
pas trouvé « interface Entrada » dans src/main : sans elle les tests ne compilent pas (étape 3) » ; « Texte : dans
src/main il y a un « fun Tienda( » » ; « Je n'ai pas trouvé « class EntradaFalsa » dans src/test : vous l'écrivez
vous-même dans EntradaFalsa.kt (étape 3) » ; « État guidé de l'étape 2 : il manque ci-dessus des éléments de l'étape 3,
et tant qu'ils manquent les tests ne compilent pas » ; « Les tests n'ont pas pu s'exécuter. Voici ce que Gradle a
dit » ; et « Résultat : 0 test exécuté ».)*

Si les lignes de Gradle mentionnent un autre fichier —`Tienda.kt`, `Bienvenida.kt`, `PantallaEntrada.kt`—, ou si vous
avez déjà commencé l'étape 3, ce n'est pas ce rouge : c'est une erreur à corriger avant de continuer. Par exemple, avec
`recuerda` écrit à la place de `remember`, Gradle a dit `e: Tienda.kt:14:21 Unresolved reference 'recuerda'.` (mesuré le
18 nov. 2026). Et comme les tests ne tournent pas encore, cette étape se vérifie dans l'application installée.

**Ce qui le prouve :** dans l'application installée, « Entrer » vous mène à l'écran de connexion (première moitié de
l'objectif 2.2).

---

## Étape 3 · Avec Claude Code : qui décide si vous entrez

Il manque l'écran d'accueil, et il manque de décider qui peut y arriver. Aujourd'hui il n'y a pas de fournisseur (Google
arrive dans la leçon 04), et pourtant les tests doivent parcourir les trois écrans.

C'est pourquoi cette décision ne s'écrit à l'intérieur d'aucun écran. Elle se déclare à part, comme **interface** : « à
ceci, l'on peut demander d'entrer, et cela répond par le nom de la personne entrée ou par rien ». Et l'on écrit deux
**implémentations** :

| Implémentation | Où elle vit | Ce qu'elle répond | Qui l'utilise |
|---|---|---|---|
| `EntradaSinProveedor` | `app/src/main` (l'application) | Toujours « non » : `null` | L'application installée |
| `EntradaFalsa` | `app/src/test` (les tests) | Toujours « oui », avec le nom que vous lui donnez | Seulement les tests |

`EntradaFalsa` est une **doublure de test**. Pour que l'application réelle ne laisse passer personne sans fournisseur,
nous maintenons cette règle : **l'application ne contient aucun moyen d'entrer sans fournisseur.** Selon la documentation
d'Android, une doublure qui vit dans le code de test ne voyage pas avec l'application.

C'est ici qu'entre l'outil, et il entre toujours de la même manière : **vous prédisez → vous demandez → vous lisez ce
qu'il a généré → vous lancez le `verifica` → vous l'expliquez avec vos propres mots.**

1. **Prédisez.** Écrivez dans `respuestas/02.md` : parmi les fichiers `Bienvenida.kt`, `PantallaEntrada.kt`, `Tienda.kt`
   et `MainActivity.kt`, lesquels vont changer et lesquels non ?
2. **Demandez** à Claude Code, depuis `inicio/` : « Crée `Entrada.kt` avec une interface `Entrada` qui a
   `fun entrar(): String?` et une classe `EntradaSinProveedor` qui renvoie `null`. Crée `Inicio.kt` avec un
   composable `Inicio(nombre: String)` qui affiche "Hola, " suivi du nom et, en dessous, "Aquí verás tus
   órdenes". Fais en sorte que `Tienda` reçoive `entrada: Entrada` : en touchant "Continuar", elle appelle
   `entrada.entrar()` ; si cela renvoie un nom, elle passe à l'écran d'accueil avec ce nom ; si cela renvoie `null`,
   elle reste et met `rechazada` à `true`. Dans `MainActivity`, utilise `EntradaSinProveedor()`. Dans `app/src/test`, à
   côté de `RecorridoTest.kt`, crée `EntradaFalsa.kt` : elle reçoit un nom, le renvoie toujours et compte dans `veces`
   combien de fois il lui a été demandé d'entrer. Ne modifie ni `RecorridoTest.kt` ni les deux écrans qui existent déjà. »
3. **Lisez** le changement complet avant de l'accepter. Le corps des fonctions peut varier. Ce que les tests appellent
   par son nom fait partie du contrat de cette leçon et doit rester tel quel : `Entrada` avec `fun entrar(): String?`,
   `EntradaSinProveedor()`, `Tienda(entrada = …)`, `EntradaFalsa(nombre)` avec son compteur `veces`, et les textes des
   écrans. Avec d'autres noms, votre conception peut être tout aussi bonne, mais ces tests ne compilent pas. Il doit
   contenir ces quatre éléments :

```kotlin
// Entrada.kt — le contrat et l'implémentation de l'application
interface Entrada {
    fun entrar(): String?            // le nom de la personne entrée, ou null si elle n'a pas pu
}

class EntradaSinProveedor : Entrada {
    override fun entrar(): String? = null
}
```

```kotlin
// EntradaFalsa.kt — dans app/src/test, pas dans app/src/main
class EntradaFalsa(private val nombre: String) : Entrada {
    var veces = 0

    override fun entrar(): String? {
        veces++
        return nombre
    }
}
```

```kotlin
// Tienda.kt — reçoit l'entrée et conserve trois valeurs d'état
@Composable
fun Tienda(entrada: Entrada) {
    var pantalla by remember { mutableStateOf(Pantalla.BIENVENIDA) }
    var nombre by remember { mutableStateOf<String?>(null) }
    var rechazada by remember { mutableStateOf(false) }

    when (pantalla) {
        Pantalla.BIENVENIDA -> Bienvenida(alEntrar = { pantalla = Pantalla.ENTRADA })
        Pantalla.ENTRADA -> PantallaEntrada(
            rechazada = rechazada,
            alContinuar = {
                val quien = entrada.entrar()
                if (quien != null) {
                    nombre = quien
                    pantalla = Pantalla.INICIO
                } else {
                    rechazada = true
                }
            }
        )
        Pantalla.INICIO -> Inicio(nombre = nombre ?: "")
    }
}
```

```kotlin
// MainActivity.kt — le seul endroit de l'application qui nomme une implémentation
        setContent { Tienda(entrada = EntradaSinProveedor()) }
```

   Quatre choses à vérifier en lisant :

   - **`: Entrada`** après le nom de la classe dit « cette classe respecte cette interface », et **`override`** marque la
     fonction qui la respecte. Sans `override`, ça ne compile pas.
   - **`entrada.entrar()` est à l'intérieur de `alContinuar`**, c'est-à-dire à l'intérieur de l'événement. Laissé libre
     dans le corps de `Tienda`, il s'exécuterait chaque fois que la fonction est dessinée, et Compose peut l'exécuter de
     nombreuses fois.
   - **`Tienda` ne dit pas quelle entrée lui est échue.** Elle ne connaît que l'interface.
   - Dans `Inicio.kt`, vous verrez `"Hola, $nombre"` : le signe `$` insère la valeur de la variable dans le texte.

4. **Lancez le `verifica`.** Maintenant les tests tournent : sept coches « Simulado » au vert —six du parcours et une du
   démarrage— et la ligne `Resultado: 7 de 7 pruebas en verde (0 en rojo, 0 con error, 0 saltadas)`. Il reste une coche
   au rouge, celle de `respuestas/02.md` : elle se remplit à l'étape 4.
5. **Expliquez**, en trois lignes de `respuestas/02.md`, ce qui se passe entre le moment où vous touchez « Continuer » et
   celui où « Bonjour, Ana » apparaît, en nommant les valeurs d'état qui changent.

**Montrez que changer l'entrée ne touche pas aux écrans.** Les tests ont déjà utilisé `EntradaFalsa("Ana")`,
`EntradaFalsa("Luis")` et `EntradaSinProveedor()` avec les mêmes trois écrans. Maintenant, montrez-le vous-même. Depuis
`inicio/`, lancez ceci et collez la sortie dans `respuestas/02.md` :

```bash
grep -rlE "EntradaSinProveedor|EntradaFalsa" app/src | sort
```

Dans `solucion/`, la sortie compte quatre fichiers (mesuré le 18 nov. 2026) : `Entrada.kt` et `MainActivity.kt` dans
`src/main`, et `EntradaFalsa.kt` et `RecorridoTest.kt` dans `src/test`. Aucun écran n'apparaît. Écrivez en dessous quels
fichiers vous toucheriez pour mettre en place une autre manière d'entrer, et lesquels non.

**Ce que prouve chaque coche, et ce qu'elle ne prouve pas.**

- **« Simulado », les six du parcours :** elles ont dessiné `Tienda` en mémoire avec l'entrée choisie par le test, ont
  touché les boutons et cherché les textes. Elles prouvent quel écran reste après chaque toucher, avec deux
  implémentations.
- **« Simulado », celle du démarrage :** elle a ouvert `MainActivity`, la classe qu'Android ouvre quand l'icône
  est touchée, et a touché « Entrer » et « Continuer ». Elle prouve que, par ce chemin, l'application ne laisse passer
  personne.
- **« Texto » :** elle a seulement cherché des mots dans vos fichiers, sans compter les commentaires. Elle ne prouve pas
  que le code fasse ce que ces mots suggèrent.

Aucune ne prouve qu'il n'existe pas **une autre** manière d'entrer sans fournisseur : une deuxième entrée portant un
autre nom dans `src/main`, utilisée depuis un autre endroit, passe au vert. Cela, c'est vous qui le vérifiez : lisez
`MainActivity.kt` et confirmez qu'elle appelle `Tienda(entrada = EntradaSinProveedor())`, et qu'il n'y a dans `src/main`
aucune autre classe qui respecte `Entrada`. Elles ne prouvent pas non plus l'apparence des écrans, ce que fait le bouton
« retour », ni rien de Google.

**Dans l'application installée,** vous arrivez jusqu'à l'écran de connexion, et « Continuer » doit répondre « Todavía no se
puede entrar » (« Il n'est pas encore possible d'entrer ») : l'application utilise `EntradaSinProveedor`. À l'accueil
seuls les tests arrivent.

**Si une ligne n'est toujours pas claire,** avant de continuer, essayez de l'expliquer avec vos propres mots. Si cela ne
vient toujours pas, revenez à ce fragment ou demandez une précision.

**Ce qui le prouve :** le `verifica` simulé parcourt les trois écrans (objectif 2.2) et le fait avec deux implémentations
de `Entrada`, plus votre sortie de `grep` et votre lecture de `MainActivity.kt` (objectif 2.4).

---

## Étape 4 · Sans IA : cassez-en un exprès

C'est l'exercice 2.3 et il se fait **sans Claude Code**. Avec les sept tests au vert, vous allez provoquer la scène du
début.

**1. Prédisez.** Dans `Tienda.kt`, la ligne de l'état va devenir ceci, sans `remember` :

```kotlin
    var pantalla by mutableStateOf(Pantalla.BIENVENIDA)
```

Écrivez dans `respuestas/02.md` : est-ce que ça compile ? que voyez-vous à l'ouverture de l'application ? que se passe-t-il
quand vous touchez « Entrer » ?

**2. Cassez-le et mesurez.** Faites le changement et lancez le `verifica`. Voici ce que cela a donné le 18 nov. 2026 :

```text
✅ Simulado: la app empieza en la bienvenida, con «La tienda» y el botón «Entrar», y sin nada de las otras dos
❌ Al tocar el botón «Entrar» no aparece la pantalla de entrada con «Entra a tu cuenta» y un botón «Continuar» …
❌ conLaEntradaFalsaLlegaAlInicio quedó en rojo, pero depende de la marca anterior …
   (y cuatro más iguales)
Resultado: 1 de 7 pruebas en verde (6 en rojo, 0 con error, 0 saltadas), leído de …
```

*(L'espagnol est la sortie du `verifica`, telle qu'affichée. En français : « Simulé : l'application démarre sur
l'écran de bienvenue, avec « La tienda » et le bouton « Entrar », et rien des deux autres » ; « En touchant le bouton « Entrar »,
l'écran de connexion avec « Entra a tu cuenta » et un bouton « Continuar » n'apparaît pas … » ; « … est resté au rouge, mais
dépend de la coche précédente … » ; « (et quatre autres identiques) » ; « Résultat : 1 test sur 7 au vert (6 au rouge,
0 en erreur, 0 sauté), lu depuis … ».)*

Copiez dans `respuestas/02.md` la ligne rouge qui commence par « Al tocar el botón «Entrar» no aparece la pantalla de
entrada ». Puis installez l'application et regardez-le vous-même.

**3. Expliquez pourquoi**, avec vos propres mots, avant de lire la suite.

Ce que dit la documentation d'Android : `remember` conserve une valeur dans la mémoire de l'écran et, à chaque
recomposition, renvoie **cette même** valeur. Sans `remember`, la ligne est ré-exécutée en entier. La chaîne, déduite de
cette documentation (le `verifica` mesure le résultat, pas chaque étape), est la suivante :

1. Vous touchez « Entrer ». L'événement change la valeur en `ENTRADA`.
2. Compose s'en aperçoit, parce que la valeur naît toujours de `mutableStateOf`, et ré-exécute `Tienda`.
3. `Tienda` s'exécute depuis le début et la première ligne crée une **nouvelle** valeur, à `BIENVENIDA`.
4. Le `when` lit cette nouvelle valeur. L'écran de bienvenue est de nouveau dessiné.

La valeur a bien changé. Ce qui s'est perdu, c'est l'endroit où elle était conservée.

**4. Une autre manière de le casser.** Laissez la ligne comme une variable ordinaire : `var pantalla =
Pantalla.BIENVENIDA`. Le `verifica` donne le même 1 sur 7 (mesuré le 18 nov. 2026), pour une autre raison : selon la
documentation, Compose n'est informé que des changements de valeurs qu'il peut observer. La valeur change et personne ne
redessine.

**5. Réparez-le.** Remettez la ligne à `var pantalla by remember { mutableStateOf(Pantalla.BIENVENIDA) }` et lancez le
`verifica` : sept sur sept, et maintenant aussi la coche de `respuestas/02.md`.

Le `verifica` vérifie seulement que vous avez collé la ligne rouge : il ne sait pas si vous l'avez cassé ni ne note votre
explication. Le 2.3 est terminé quand vous pouvez cocher les quatre cases :

- [ ] Votre prédiction du point 1 est écrite, avec ce que vous avez vu en dessous.
- [ ] Vous avez collé la ligne rouge et le « Resultado » qui vous a été donné.
- [ ] Vous avez expliqué, étape par étape, pourquoi l'écran cesse de se mettre à jour sans `remember`.
- [ ] Vous avez dit en quoi la seconde manière de le casser ressemble à la première, et en quoi elle diffère.

**Une limite.** Selon la documentation d'Android, `remember` ne conserve pas la valeur lors d'un changement de
configuration, comme faire pivoter le téléphone ; pour cela existe `rememberSaveable`. Cette leçon ne l'utilise ni ne
le teste. Sur l'émulateur du test (18 nov. 2026), en faisant pivoter le téléphone alors que l'écran « Entra a tu
cuenta » était affiché, l'application est revenue à l'écran de bienvenue : l'état s'est perdu, comme le dit la documentation. Et « retour »,
depuis l'un ou l'autre des deux écrans, a fermé l'application et ramené à l'accueil du téléphone : le parcours de cette
leçon ne gère pas « retour ».

**Ce qui le prouve :** « Cassez-en un exprès » (objectif 2.3).

---

## Vos exercices

Le 2.1, l'étape 2 et le 2.3 se font **sans IA**.

| # | Vous faites | C'est fait quand |
|---|---|---|
| 2.1 | Vous prédisez ce que fait l'état de l'étape 1 et vous le lancez | Vous avez votre prédiction et ce que vous avez vu |
| 2.2 | Vous construisez bienvenue → connexion → accueil | Le `verifica` donne 7 sur 7 **et** dans l'application installée vous avez vu « Todavía no se puede entrar » |
| 2.3 | Vous cassez l'état et expliquez pourquoi l'écran cesse de se mettre à jour | Votre preuve, ce sont les quatre cases de l'étape 4, écrites par vous. Le `verifica` confirme seulement que le projet est réparé (7 sur 7) et que vous avez collé le rouge |
| 2.4 | Vous séparez la décision d'entrer dans `Entrada` et montrez que la changer ne touche pas aux écrans | Les tests avec les deux implémentations et celui du démarrage sont au vert, vous avez collé votre sortie de `grep` **et** vous avez lu que `src/main` ne contient pas d'autre entrée |

**Durée :** comptez environ trois heures, avec le « Cassez-en un exprès ». C'est une estimation : nous l'ajusterons avec
le temps que prendra la première cohorte.

---

## Ça ne tourne pas ?

Les messages en anglais sont ceux que le compilateur de la série (Kotlin 2.4.21) a donnés le 18 nov. 2026.

| Vous voyez | Cause probable | Ce que vous faites |
|---|---|---|
| `No value passed for parameter 'alEntrar'` | Quelqu'un appelle `Bienvenida()` sans la fonction | Passez-la-lui : `Bienvenida(alEntrar = { … })` |
| `'when' expression must be exhaustive. Add the 'INICIO' branch or an 'else' branch.` | Il manque au `when` l'un des trois écrans | Ajoutez la branche qu'indique le message |
| `'entrar' hides member of supertype 'Entrada' and needs an 'override' modifier.` | La classe respecte l'interface sans le dire | Écrivez `override fun entrar()` |
| `Smart cast to 'String' is impossible, because 'nombre' is a delegated property.` | Vous avez testé `if (nombre != null)` sur un état et l'avez utilisé comme `String` | Conservez d'abord le résultat dans un `val`, comme `val quien = entrada.entrar()` |
| ❌ « EntradaFalsa aparece en src/main » | L'entrée qui laisse passer est restée dans l'application | Déplacez-la vers `app/src/test/java/mx/habil/ejemplo/tienda/` |
| ❌ sur « solo al tocar Continuar » (seulement en touchant « Continuer ») | `entrada.entrar()` est resté hors de l'événement | Il va dans `alContinuar = { … }` |
| `RecorridoTest.kt` ou `ArranqueTest.kt` a changé | Quelqu'un (vous ou Claude Code) a touché aux tests | Regardez ce qui a changé avec le `git diff` que donne le message et annulez-le à la main. Restaurer le fichier avec git supprime tout ce qu'il contient : ne le faites qu'après avoir revu ce diff. Ensuite, répétez la demande en précisant de ne pas y toucher |

Sur Linux Mint 22.3 (x86_64), le `verifica` de cette leçon est sorti au vert sur la solution : 7 sur 7 (18 nov. 2026).

---

## Pour conclure

**Répondez sans relire :**

1. Vous touchez « Entrer » et l'écran change. Que s'est-il passé entre les deux, dans l'ordre ?
2. Que fait `remember`, et que voyez-vous dans l'application si vous le retirez ?
3. Vos sept tests simulés sont au vert. Dites une chose que cela prouve et une qu'il ne prouve pas.
4. Pourquoi `EntradaFalsa` vit-elle dans `src/test` et non dans `src/main` ?

**La suite :** dans la L03, l'application rencontre Firebase, et dans la L04, vous entrez avec Google.

---

## Comparez vos réponses

**Étape 1.** Après le premier toucher, vous voyez « Bonjour ! » (« ¡Hola! »), après le deuxième il disparaît et après le
troisième il revient (mesuré le 18 nov. 2026 avec un test simulé).

**Étape 2.** Dans une troisième fonction qui appelle les deux, `Tienda`. Si la valeur vivait dans `Bienvenida`, l'écran
de connexion ne pourrait pas la lire, et quand l'écran de bienvenue cesserait d'être dessiné, elle se perdrait avec lui.

**Étape 3.** `Tienda.kt` et `MainActivity.kt` changent ; `Bienvenida.kt` et `PantallaEntrada.kt` non : ils ne
faisaient déjà que recevoir des données et signaler des touchers. Entre le toucher et « Bonjour, Ana » : `alContinuar`
appelle `entrada.entrar()`, qui renvoie `"Ana"` ; elle est conservée dans `nombre` et `pantalla` passe à `INICIO` ;
Compose ré-exécute `Tienda` et le `when` choisit `Inicio`. Pour mettre en place une autre manière d'entrer, avec ce qu'il y a
dans cette leçon, vous écrivez une nouvelle implémentation et changez la ligne de `MainActivity.kt` qui la choisit ; les
écrans et `Tienda.kt` ne sont pas touchés. Ce que cela donne avec un vrai Google se voit dans la L04.

**Étape 4.** Oui, ça compile, et les tests simulés trouvent l'écran de bienvenue à l'ouverture ; en touchant « Entrer », l'écran
ne change pas. La seconde manière ressemble à la première dans le décompte (1 sur 7) et non dans la cause : sans
`remember`, Compose redessine avec une valeur tout juste créée ; avec une variable ordinaire, il n'est jamais informé.

**Pour conclure :**

1. L'événement a changé une valeur d'état ; Compose a ré-exécuté la fonction qui l'a lue ; le `when` a choisi un autre
   écran.
2. Il conserve la valeur d'une recomposition à la suivante. Sans lui, l'application reste sur l'écran de bienvenue.
3. Cela prouve quel écran reste après chaque toucher, avec une entrée qui laisse passer et une qui ne le fait pas, et
   que l'application ouverte depuis `MainActivity` ne laisse passer personne. Cela ne prouve ni leur apparence, ni ce
   que fait le bouton « retour », ni qu'il n'existe pas une autre manière d'entrer par un autre chemin, ni que Google
   fonctionne.
4. Parce qu'elle laisse passer n'importe qui. Dans `src/test`, seuls les tests l'utilisent et elle ne voyage pas dans
   l'application installée.

---

## Sources de la leçon

Ouvertes le 18 nov. 2026.

- Android Developers. *State and Jetpack Compose*. https://developer.android.com/develop/ui/compose/state
- Android Developers. *Thinking in Compose* (recomposition ; les effets vont dans des événements). https://developer.android.com/develop/ui/compose/mental-model
- Android Developers. *Lifecycle of composables* (ce qui déclenche une recomposition). https://developer.android.com/develop/ui/compose/lifecycle
- Android Developers. *Where to hoist state* (l'ancêtre commun le plus proche). https://developer.android.com/develop/ui/compose/state-hoisting
- Android Developers. *Navigation* (la bibliothèque que cette leçon n'utilise pas). https://developer.android.com/guide/navigation
- Android Developers. *Use test doubles in Android* (ce qu'est une doublure ; dit que celle qui vit dans le code de test ne voyage pas avec l'application). https://developer.android.com/training/testing/fundamentals/test-doubles
- Android Developers. *Android build structure* (différence entre `src/main` et `src/test`). https://developer.android.com/build/android-build-structure
- Android Developers. *Build local unit tests* et *Test in Android Studio*. https://developer.android.com/training/testing/local-tests · https://developer.android.com/studio/test/test-in-android-studio
- Android Developers. *Test your Compose layout*. https://developer.android.com/develop/ui/compose/testing
- Android Developers. *SemanticsMatcher* et *Role* (rôle de bouton dans les tests). https://developer.android.com/reference/kotlin/androidx/compose/ui/test/SemanticsMatcher · https://developer.android.com/reference/kotlin/androidx/compose/ui/semantics/Role
- Kotlin. *Interfaces*. https://kotlinlang.org/docs/interfaces.html
- Kotlin. *Enum classes*. https://kotlinlang.org/docs/enum-classes.html
- Kotlin. *Conditions and loops* (`when`). https://kotlinlang.org/docs/control-flow.html
- Kotlin. *Higher-order functions and lambdas* (types de fonction comme `() -> Unit`) et *Functions* (`Unit`). https://kotlinlang.org/docs/lambdas.html · https://kotlinlang.org/docs/functions.html
- Kotlin. *Strings* (modèles avec `$`). https://kotlinlang.org/docs/strings.html
- Nos propres mesures : Mac avec Apple silicon, 18 nov. 2026 (le `verifica` sur `inicio/` et `solucion/` ; les deux manières de casser l'état ; la prédiction de l'étape 1 ; l'état intermédiaire de l'étape 2, avec et sans erreur ; messages du compilateur).

Revue externe (18 nov. 2026) : AVEC CHANGEMENTS, 3 de priorité haute, 4 moyenne et 2 basse ; appliquée.
