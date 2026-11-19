# Pratique 02-B — Catalogue des composants de la boutique

**Avant de lire, six mots.** Une ligne chacun.

- **Composant :** une petite fonction `@Composable` qui sert dans plusieurs écrans, comme un bouton ou une carte.
- **Variante :** le même composant avec d'autres données ou dans un autre mode : désactivé, en sombre, avec un texte
  long.
- **Aperçu (preview) :** une fonction marquée `@Preview` qu'Android Studio dessine dans un panneau, sans émulateur.
- **Catalogue :** un écran, dans l'application de débogage, où vous voyez tous les composants et leurs variantes.
- **Test de capture :** un test qui dessine un composant, prend une image et la compare à une **image de référence**
  conservée dans le dépôt. Si un pixel a changé plus que ce qui est toléré, il passe au rouge.
- **`src/debug` :** un dossier de code qui n'entre que dans l'application de débogage, jamais dans celle que l'on
  publie.

**Ce que vous en retirez :** trois composants de la boutique —bouton principal, carte de produit et étiquette de l'état
d'une commande— vérifiés isolément dans leurs variantes, placés dans l'écran d'accueil de la 02, avec un catalogue dans
l'application de débogage et onze tests de capture qui passent au rouge quand quelqu'un change une couleur.

**Idée nouvelle de cette pratique :** un composant se conçoit et se vérifie **isolément**, dans toutes ses variantes,
avant d'être utilisé dans un écran. C'est une pratique : elle vient après la 02, en dehors de la séquence principale, et
elle est facultative pour la cohorte.

---

## La scène

Quelqu'un de l'équipe change une ligne : le vert de la boutique passe au rouge, pour essayer un bouton plus voyant. Ça
compile. Les sept tests de la 02 restent au vert. Personne n'ouvre l'application en mode sombre ni ne regarde la carte
d'un produit au nom long. On le livre.

Ce changement est mesuré. Avec `VerdeTienda` en rouge, les sept tests de la 02 ont donné 7 sur 7 au vert, et les onze
tests de capture de cette pratique ont donné **6 sur 11 au rouge** (19 novembre 2026). Ce n'est pas seulement le bouton
qui a changé : le prix de la carte et l'étiquette « Entregada » (Livrée) ont changé aussi, y compris en sombre. Les
tests de la 02 mesurent quel écran apparaît après chaque toucher, et ne regardent pas les couleurs.

La question de la pratique : comment voyez-vous toutes les variantes d'un composant sans parcourir l'application, et
comment savez-vous que l'une d'elles a changé sans que personne l'ait voulu ?

---

## Avant de commencer

**Ce dont vous avez besoin :** la [02](02-pantallas-que-cambian.md) terminée, l'environnement de la
[00](00-ambiente-listo.md) (`ANDROID_HOME` et un émulateur ou votre téléphone) et Android Studio pour le panneau
d'aperçus.

**Le projet de départ** est la solution de la 02, avec ce qui concerne cette pratique déjà configuré :

```bash
git pull
cd lecciones/02b-catalogo-de-componentes/inicio
```

Vos réponses vont dans `respuestas/02b.md`, à la racine du dépôt (`mkdir -p respuestas` s'il n'existe pas).

**Ce que le projet apporte déjà (échafaudage).** Cela n'est pas évalué ici :

| Ce qu'il apporte | Où | Pourquoi |
|---|---|---|
| La palette et la typographie de la boutique | `Tema.kt` | La pratique les fournit : personne ne les conçoit. Ne le modifiez pas, sauf un instant à l'étape 6 |
| Les dépendances et les plugins | `gradle/libs.versions.toml` et `app/build.gradle.kts` | Voir le tableau des versions ci-dessous |
| Les tests de la 02 | `RecorridoTest.kt` et `ArranqueTest.kt` | Ils doivent rester au vert avec les composants en place. Ne les modifiez pas |
| La mémoire, `gradlew`, Android 36 pour les tests et Java 25 | Comme dans la 02 | Expliqué dans la [01](01-mi-primera-pantalla.md) |

`Tema.kt` définit onze couleurs nommées (`VerdeTienda`, `AzulRecibida`, `AmbarEnviada`…), une palette claire et une
sombre, quatre styles de police et une fonction `TemaTienda(oscuro) { … }` qui enveloppe n'importe quel contenu avec tout
cela. Ouvrez-le et lisez-le avant l'étape 1 : vous allez l'utiliser à chaque étape.

**Pourquoi Roborazzi pour les captures.** Il a été choisi avec un seul critère : qu'il continue de fonctionner à
l'identique pour la cohorte suivante. L'outil officiel de Google ne le remplit pas aujourd'hui (19 nov. 2026) : sa page
dit que le plugin `com.android.compose.screenshot` « n'est plus maintenu », et son installation demande encore le drapeau
`android.experimental.enableScreenshotTest=true`. Google recommande de migrer vers les *test suites* d'AGP, qui
demandent AGP 9.6.0-alpha03 ou plus, une version préliminaire ; cette série est figée sur AGP 9.4.1, et la mettre à jour est une décision à part. Paparazzi,
l'autre option connue, en est à 2.0.0-alpha05. Roborazzi a une version stable sur Maven Central et tourne sur
Robolectric, que la série utilise déjà depuis la 01.

**Les versions**, figées et essayées avec cette pratique le 19 nov. 2026. Celles de Roborazzi, Showkase, KSP et
`material-icons-core` étaient les dernières publiées ce jour-là ; AGP 9.4.1 et le BOM 2026.09.00 de la série ne
l'étaient pas (il y avait déjà AGP 9.5.1 et le BOM 2026.11.01), et ils restent pour ne pas déplacer la série en cours de
cohorte :

| Pièce | Version | À quoi elle sert | Source |
|---|---|---|---|
| Roborazzi (`roborazzi`, `roborazzi-compose` et son plugin) | 1.79.0 | Tests de capture | Maven Central |
| Showkase (`showkase` et `showkase-processor`) | 1.0.5 | Le catalogue | Maven Central et son README |
| KSP (Kotlin Symbol Processing) | 2.3.14 | Lance le générateur de Showkase à la compilation | Maven Central et ses *releases* |
| `material-icons-core` | 1.7.8, celle du BOM 2026.09.00 | Icônes que Showkase utilise en interne | Le BOM de Compose |
| Robolectric | 4.18 | Il venait déjà de la 02 | — |

La 4.18.1 de Robolectric est apparue sur Maven Central le 18 nov. 2026. Cette pratique reste avec la 4.18 de la série :
c'est avec elle que tout a été mesuré.

**La maquette de l'écran d'accueil**, avec les trois composants sous le salut de la 02 :

```text
┌──────────────────────────┐
│       Hola, Ana          │
│  Aquí verás tus órdenes  │
│ ┌──────────────────────┐ │
│ │ Audífonos            │ │  ← tarjeta de producto
│ │ $899.00              │ │
│ └──────────────────────┘ │
│        ( Enviada )       │  ← etiqueta de estado
│ [   Ver mis órdenes    ] │  ← botón principal
└──────────────────────────┘
```

*(Les textes se lisent : « Bonjour, Ana », « Ici vous verrez vos commandes », la carte « Écouteurs » à 899,00 $,
l'étiquette « Expédiée » et le bouton « Voir mes commandes ». Les flèches disent : carte de produit, étiquette d'état,
bouton principal.)*

---

## Étape 1 · À la main : le bouton principal

Sans Claude Code. Créez `BotonPrincipal.kt` à côté de `Inicio.kt`, dans `app/src/main/java/mx/habil/ejemplo/tienda/` :

```kotlin
package mx.habil.ejemplo.tienda

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun BotonPrincipal(texto: String, alTocar: () -> Unit, habilitado: Boolean = true) {
    Button(
        onClick = alTocar,
        enabled = habilitado,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(texto, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
    }
}
```

- **`habilitado: Boolean = true`** est un paramètre avec une valeur par défaut : qui ne le passe pas reçoit `true`.
- **`MaterialTheme.typography.labelLarge`** ne dit pas quelle police utiliser : il demande le style `labelLarge` du
  thème qui l'entoure. À l'intérieur de `TemaTienda`, c'est celui de `Tema.kt`.
- **Le bouton ne dit pas sa couleur.** `Button` prend la couleur primaire du thème, qui dans la palette claire est
  `VerdeTienda`.

**Prédisez.** Écrivez dans `respuestas/02b.md`, avant de continuer : qu'est-ce qui change à l'écran avec
`habilitado = false` ? Et avec un texte qui ne tient pas sur une ligne ?

Vous ne pouvez pas encore le voir : aucun écran ne l'utilise. Vous le verrez dans le panneau de l'étape 3.

**Ce qui le prouve :** le composant compile et le `verifica` trouve « fun BotonPrincipal( » (une partie de l'objectif
2B.1).

---

## Étape 2 · Avec Claude Code : la carte, l'étiquette et l'écran d'accueil

C'est ici qu'entre l'outil, toujours de la même manière : **vous prédisez → vous demandez → vous lisez ce qu'il a
généré → vous lancez le `verifica` → vous l'expliquez avec vos mots.**

1. **Prédisez.** Écrivez dans `respuestas/02b.md` : lesquels de ces fichiers vont changer ? `Inicio.kt`,
   `MainActivity.kt`, `Tienda.kt`, `Tema.kt`.
2. **Demandez** à Claude Code, depuis `inicio/` : « Crée `TarjetaProducto.kt` avec un composable
   `TarjetaProducto(nombre: String, precio: String)` : une `Card` pleine largeur, coins de 16 dp et couleur de surface
   du thème ; à l'intérieur, le nom avec le style `titleMedium` sur deux lignes au plus et avec « … » s'il ne tient pas,
   et dessous le prix en gras avec la couleur primaire du thème. Crée `EtiquetaEstado.kt` avec un
   `enum class EstadoOrden(val texto: String)` avec `RECIBIDA("Recibida")`, `ENVIADA("Enviada")` et
   `ENTREGADA("Entregada")` ; une fonction `colorDe(estado)` qui renvoie `AzulRecibida`, `AmbarEnviada` et `VerdeTienda`
   de `Tema.kt` ; et un composable `EtiquetaEstado(estado: EstadoOrden)` : une pastille de cette couleur avec le texte en
   blanc et le style `labelSmall`. Dans `Inicio.kt`, sous les deux textes, ajoute une `TarjetaProducto` de "Audífonos
   inalámbricos" à "$899.00", une `EtiquetaEstado(EstadoOrden.ENVIADA)` et un `BotonPrincipal("Ver mis órdenes")`
   qui ne fait rien pour l'instant. Dans `MainActivity`, enveloppe `Tienda` avec `TemaTienda(oscuro =
   isSystemInDarkTheme())`. Ne modifie ni `Tema.kt`, ni `Tienda.kt`, ni les tests. »
3. **Lisez** le changement avant de l'accepter. Les noms ci-dessus sont le contrat de cette pratique : les aperçus, le
   catalogue et les captures les appellent ainsi. La pièce la plus difficile à lire est l'étiquette :

```kotlin
enum class EstadoOrden(val texto: String) {
    RECIBIDA("Recibida"),
    ENVIADA("Enviada"),
    ENTREGADA("Entregada"),
}

fun colorDe(estado: EstadoOrden): Color = when (estado) {
    EstadoOrden.RECIBIDA -> AzulRecibida
    EstadoOrden.ENVIADA -> AmbarEnviada
    EstadoOrden.ENTREGADA -> VerdeTienda
}

@Composable
fun EtiquetaEstado(estado: EstadoOrden) {
    Surface(shape = RoundedCornerShape(50), color = colorDe(estado)) {
        Text(
            estado.texto,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}
```

   Trois choses à vérifier en lisant :

   - **`enum class EstadoOrden(val texto: String)`** est la liste fermée de la 02, maintenant avec une donnée par
     valeur : `EstadoOrden.ENVIADA.texto` vaut `"Enviada"`.
   - **`RoundedCornerShape(50)`** arrondit les coins à 50 % de la hauteur : c'est pourquoi cela ressemble à une
     pastille.
   - **`"$899.00"` n'insère aucune variable.** En Kotlin, `$` ne forme un modèle de chaîne que s'il est suivi d'un nom
     ou d'une accolade ; suivi d'un chiffre, c'est le signe du dollar (ou du peso).

4. **Lancez le `verifica`** depuis la racine du dépôt :

```bash
./lecciones/02b-catalogo-de-componentes/verifica
```

   Les sept tests de la 02 doivent rester au vert : l'accueil de la 02 dit toujours « Hola, Ana » et contient trois
   choses de plus. Mesuré le 19 nov. 2026 à ce stade : les marques des composants, de `Inicio.kt` et de
   `MainActivity.kt` au vert, `Resultado: 7 de 18 pruebas esperadas en verde (solo corrieron 7…` (les onze tests de capture n'existent pas encore), et au rouge seulement celles des étapes 3 à 6
   (aperçus, `@ShowkaseRoot`, images, captures, sabotage et `respuestas/02b.md`). Ce rouge est attendu maintenant.

   *(Résultat : 7 tests attendus sur 18 au vert.)*

5. **Expliquez** en deux lignes de `respuestas/02b.md` : pourquoi `TarjetaProducto` ne reçoit-elle aucune couleur, et
   d'où la tire-t-elle ?

**Ce qui le prouve :** les trois composants compilent et l'écran d'accueil de la 02 les utilise, avec les tests de la 02
au vert (objectif 2B.1).

---

## Étape 3 · Sans IA : les variantes dans le panneau d'aperçus

C'est l'exercice 2B.2, et il se fait **sans Claude Code**. Le panneau d'aperçus d'Android Studio dessine une fonction
`@Preview` sans compiler l'application complète ni ouvrir l'émulateur.

**1. Écrivez celle du bouton.** À la fin de `BotonPrincipal.kt`, ajoutez ceci, avec ses `import` (`Box`, `padding`,
`Preview`, `PreviewParameter` et `PreviewParameterProvider` ; Android Studio vous les propose) :

```kotlin
data class VarianteBoton(
    val nombre: String, val texto: String, val habilitado: Boolean, val oscuro: Boolean
)

class VariantesBoton : PreviewParameterProvider<VarianteBoton> {
    private val lista = listOf(
        VarianteBoton("habilitado", "Comprar", habilitado = true, oscuro = false),
        VarianteBoton("deshabilitado", "Comprar", habilitado = false, oscuro = false),
        VarianteBoton("oscuro", "Comprar", habilitado = true, oscuro = true),
        VarianteBoton(
            "texto largo", "Agregar al carrito y seguir viendo la tienda",
            habilitado = true, oscuro = false
        ),
    )
    override val values = lista.asSequence()
    override fun getDisplayName(index: Int): String? = lista.getOrNull(index)?.nombre
}

@Preview(name = "Botón principal", group = "Botones", widthDp = 320)
@Composable
fun BotonPrincipalPreview(@PreviewParameter(VariantesBoton::class) variante: VarianteBoton) {
    TemaTienda(oscuro = variante.oscuro) {
        Box(Modifier.padding(16.dp)) {
            BotonPrincipal(variante.texto, alTocar = {}, habilitado = variante.habilitado)
        }
    }
}
```

- **`data class`** est une classe pour conserver des données : Kotlin lui écrit tout seul comment se comparer et
  comment s'imprimer.
- **`PreviewParameterProvider`** est une interface, comme l'`Entrada` de la 02 : elle promet une séquence de valeurs. La
  documentation d'Android dit que l'on dessine **un aperçu par valeur**.
- **`getDisplayName`** donne un nom à chacun. Sans lui, selon la documentation, ils s'appellent par leur position (0, 1,
  2…). La liste en interne est la recommandation de cette même page, pour retrouver le nom à partir de la position.
- **`oscuro` va dans la variante.** Le mode sombre de l'aperçu ne dépend ni de votre ordinateur ni du téléphone : c'est
  chaque variante qui le décide. C'est pourquoi les captures de l'étape 5 sortent identiques sur n'importe quelle machine
  qui dessine de la même manière.

**2. Écrivez les deux autres sur le même modèle.** Dans `TarjetaProducto.kt`, `VarianteTarjeta(nombre, producto,
precio, oscuro)`, `VariantesTarjeta` et `TarjetaProductoPreview`, avec `group = "Tarjetas"`. Dans `EtiquetaEstado.kt`,
`VarianteEtiqueta(nombre, estado, oscuro)`, `VariantesEtiqueta` et `EtiquetaEstadoPreview`, avec `group = "Etiquetas"` et
sans `widthDp`. Voici les variantes, dans cet ordre :

| Composant | Variantes (`nombre`) |
|---|---|
| Carte | « claro » et « oscuro », avec « Audífonos inalámbricos » à « $899.00 » ; « texto largo », avec « Juego de seis tazas de cerámica esmaltada hechas a mano en Dolores Hidalgo, Guanajuato » à « $1,249.00 » |
| Étiquette | « recibida », « enviada » et « entregada », en clair ; « entregada en oscuro » |

*(Les noms sont ceux du code : « claro » (clair), « oscuro » (sombre), « texto largo » (texte long), « recibida »
(reçue), « enviada » (expédiée), « entregada » (livrée). Le texte long est un nom de produit : « Juego de seis tazas de
cerámica esmaltada hechas a mano… » (Jeu de six tasses en céramique émaillée faites à la main…).)*

**3. Prédisez.** Avant d'ouvrir le panneau, écrivez : combien d'aperçus allez-vous voir en tout ? Quelle carte va se
terminer par « … » ?

**4. Regardez.** Ouvrez `BotonPrincipal.kt` dans Android Studio et choisissez la vue **Split** ou **Design**, en haut à
droite de l'éditeur ; s'il vous demande de compiler, acceptez. Selon la documentation, le panneau dessine un aperçu par
valeur du fournisseur, avec le nom que donne `getDisplayName`. Cette pratique n'a pas mesuré le panneau dans Android
Studio : s'il vous apparaît différent, dites-le-nous.

Prenez une capture du panneau avec les quatre variantes du bouton et enregistrez-la sous `respuestas/02b-panel.png`.
Sous votre prédiction de l'étape 1, notez ce que vous avez deviné juste.

**Ce qui le prouve :** votre capture du panneau et votre prédiction, comparées (objectif 2B.2). Le `verifica` vérifie
seulement que chaque composant apporte son `@Preview` avec `@PreviewParameter` : il ne voit pas le panneau. Et que vous
l'ayez fait sans IA est une règle de travail que vous déclarez dans votre réponse : ni une capture ni le `verifica` ne
peuvent le démontrer.

---

## Étape 4 · Avec Claude Code : le catalogue avec Showkase

Le panneau vit dans votre Android Studio. Le catalogue vit **dans l'application** : n'importe qui dans l'équipe l'ouvre
sur son téléphone, sans le projet ouvert. Showkase, d'Airbnb, le construit avec les mêmes `@Preview` : à la
compilation, KSP (un générateur de code qui tourne à côté du compilateur de Kotlin) cherche les aperçus et écrit le code
qui les rassemble.

1. **Prédisez.** Pourquoi pensez-vous que le catalogue doit aller dans `src/debug` et non dans `src/main` ?
2. **Demandez** à Claude Code, depuis `inicio/` : « Crée
   `app/src/debug/java/mx/habil/ejemplo/tienda/Catalogo.kt` avec une classe `CatalogoTienda` marquée `@ShowkaseRoot` qui
   implémente `ShowkaseRootModule`, et une `CatalogoActivity` qui, dans `onCreate`, ouvre
   `Showkase.getBrowserIntent(this)` et se ferme avec `finish()`. Crée `app/src/debug/AndroidManifest.xml` qui enregistre
   `CatalogoActivity` avec sa propre icône de lancement et l'étiquette « Catálogo ». Ne touche à rien de `src/main`. »
3. **Lisez** le changement. Il doit apporter ces pièces :

```kotlin
@ShowkaseRoot
class CatalogoTienda : ShowkaseRootModule

class CatalogoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Showkase.getBrowserIntent(this))
        finish()
    }
}
```

   - **`@ShowkaseRoot`** marque la racine du catalogue. Le README de Showkase en demande une par module.
   - **`getBrowserIntent`** n'existe pas tant que vous n'avez pas compilé : c'est KSP qui l'écrit. Si Android Studio le
     souligne en rouge, compilez une fois.
   - **Les aperçus doivent être publics ou `internal`.** Le README le dit : un aperçu `private` n'entre pas dans le
     catalogue.

4. **Regardez-le dans l'émulateur.** `./gradlew installDebug`. Sur le téléphone apparaissent deux icônes : « La tienda »
   et « Catálogo ». Ouvrez « Catálogo ».

   Dans l'émulateur de l'essai (Pixel 9, Android 37, 19 nov. 2026), voici ce que l'on a vu : une liste intitulée
   « Components » (les textes de Showkase sont en anglais) avec trois groupes, « Botones (1) », « Etiquetas (1) » et
   « Tarjetas (1) ». Le « (1) » compte des composants, pas des variantes. En touchant « Botones » puis « Botón
   principal », les quatre variantes sont apparues : vert avec « Comprar », gris éteint, vert clair sur fond sombre, et
   le texte long sur deux lignes. Les quatre portent le même titre, « Botón principal » : Showkase n'a pas utilisé les
   noms de `getDisplayName`. Dans les étiquettes, on a vu « Recibida » en bleu, « Enviada » en ambre et deux
   « Entregada » en vert, la seconde sur fond sombre.

5. **Expliquez** dans `respuestas/02b.md` ce que vous avez vu de différent entre le panneau de l'étape 3 et le
   catalogue.

**Ce qui le prouve :** le catalogue s'ouvre dans l'émulateur avec les trois groupes et leurs variantes (objectif 2B.3).
Le `verifica` vérifie ce que Showkase a généré à la compilation : la racine du catalogue et ses trois groupes, qu'une
activité de `src/debug` l'ouvre avec son icône propre, et que `src/main` ne le nomme pas. Une chaîne qui dirait
« @ShowkaseRoot » ne génère rien et reste au rouge. Ce que l'on voit dans l'émulateur, c'est vous qui le racontez, et
votre capture en est la preuve.

---

## Étape 5 · Avec Claude Code : une capture par aperçu

Voir les variantes sert tant que quelqu'un les regarde. Un test de capture les regarde chaque fois que les tests
tournent.

1. **Prédisez.** Combien d'images de référence aurez-vous à la fin ?
2. **Demandez** à Claude Code, depuis `inicio/` : « Crée
   `app/src/test/java/mx/habil/ejemplo/tienda/CapturasTest.kt` : un test Robolectric avec
   `ParameterizedRobolectricTestRunner` et `@GraphicsMode(GraphicsMode.Mode.NATIVE)`, avec un cas pour chaque variante
   de `VariantesBoton`, `VariantesTarjeta` et `VariantesEtiqueta`, appelés `boton_<nombre>`, `tarjeta_<nombre>` et
   `etiqueta_<nombre>` avec les espaces remplacés par des tirets bas. Chaque cas dessine son aperçu avec
   `createComposeRule()` et appelle `onRoot().captureRoboImage("src/test/capturas/<caso>.png")`. La liste des cas doit
   sortir des mêmes fournisseurs, pas être écrite à la main. Ne touche à rien d'autre. »
3. **Lisez** le changement. Le cœur est celui-ci :

```kotlin
@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CapturasTest(private val archivo: String, private val preview: @Composable () -> Unit) {
    // … previews() : un cas par variante, tiré des fournisseurs …

    @get:Rule
    val regla = createComposeRule()

    @Test
    fun captura() {
        regla.setContent { preview() }
        regla.onRoot().captureRoboImage("src/test/capturas/$archivo.png")
    }
}
```

   - **`ParameterizedRobolectricTestRunner`** lance le même test une fois par cas de la liste : onze cas, onze tests.
   - **`@GraphicsMode(NATIVE)`** demande à Robolectric de dessiner de vrais pixels. Le README de Roborazzi l'exige.
   - **`captureRoboImage`** prend l'image. Ce qu'il en fait dépend de la tâche Gradle que vous lancez.

4. **Enregistrez les références.** Depuis `inicio/` :

```bash
./gradlew recordRoborazziDebug
```

   Onze fichiers `.png` restent dans `app/src/test/capturas/`. Ouvrez-les : ce sont vos composants, chacun dans sa
   variante. Ces images **se versent dans le dépôt** : c'est ce que la prochaine exécution va comparer.
5. **Lancez le `verifica`.** Il lance maintenant `verifyRoborazziDebug`, qui compare chaque capture à sa référence. Avec
   la solution, le 19 nov. 2026, il a donné `Resultado: 18 de 18 pruebas en verde` : les 7 de la 02 et les 11 captures.

   *(Résultat : 18 tests sur 18 au vert.)*

**Attention à la tâche que vous lancez.** Selon le README de Roborazzi, `recordRoborazziDebug` enregistre (ou
réenregistre) les références et `verifyRoborazziDebug` compare et échoue si quelque chose a changé.
`./gradlew testDebugUnitTest` tout seul lance les tests sans rien comparer : avec la couleur cassée de l'étape 6, il a
donné 18 sur 18 au vert (mesuré le 19 nov. 2026). Le `verifica` utilise `verify`.

**Les références de `solucion/` ont été enregistrées sur un Mac.** Sous Linux Mint 22.3, les onze ont coïncidé
(19 nov. 2026) : la solution a donné 18 sur 18 et le sabotage de la couleur en a mis 6 sur 11 au rouge, comme sur le Mac.
Si elles ne coïncident pas sur votre machine, enregistrez les vôtres avec `recordRoborazziDebug`, comme au point 4.

**Ce qui le prouve :** onze tests de capture au vert contre leurs références (première partie de l'objectif 2B.4).

---

## Étape 6 · Sans IA : cassez une couleur exprès

C'est la clôture du 2B.4 et elle se fait **sans Claude Code**. Vous allez provoquer la scène du début.

**1. Prédisez.** Dans `Tema.kt`, `VerdeTienda` va passer de `0xFF0B6E4F` à `0xFFC62828`, un rouge. Écrivez dans
`respuestas/02b.md` lesquelles des onze captures vont, selon vous, passer au rouge, et pourquoi. Indice : cherchez
dans vos fichiers qui utilise `VerdeTienda` directement et qui utilise la couleur primaire du thème, et dans quelle
palette.

**2. Cassez-le et mesurez.** Changez la ligne :

```kotlin
val VerdeTienda = Color(0xFFC62828)
```

et lancez, depuis `inicio/` :

```bash
./gradlew verifyRoborazziDebug
```

Le 19 nov. 2026, avec la solution, cela s'est terminé ainsi (le numéro de ligne dépend de votre fichier) :

```text
CapturasTest > [boton_habilitado] > captura[boton_habilitado] FAILED
    java.lang.AssertionError at CapturasTest.kt:53
…
18 tests completed, 6 failed
FAILURE: Build failed with an exception.
```

Les six au rouge ont été `boton_habilitado`, `boton_texto_largo`, `tarjeta_claro`, `tarjeta_texto_largo`,
`etiqueta_entregada` et `etiqueta_entregada_en_oscuro`. Chacune laisse une image de différence dans
`app/build/outputs/roborazzi/`, par exemple `boton_habilitado_compare.png`.

**3. Lisez la différence.** Ouvrez `boton_habilitado_compare.png`. Elle contient trois panneaux : **Reference** (la
référence, le bouton vert), **Diff** (ce qui a changé, marqué en rouge) et **New** (ce que l'on a dessiné maintenant, le
bouton rouge). Collez dans `respuestas/02b.md` le chemin de cette image et écrivez ce que vous voyez dans le panneau du
milieu.

**4. Expliquez pourquoi ces six et pas les cinq autres**, avant de lire « Comparez vos réponses ».

**5. Réparez-le.** Remettez la ligne à `val VerdeTienda = Color(0xFF0B6E4F)` et relancez
`./gradlew verifyRoborazziDebug` : les onze repassent au vert. Puis lancez le `verifica`. Il fait la même chose de son
côté, sur une copie de votre projet, sans toucher à votre dossier : il change la couleur, exige au moins une capture au
rouge avec son image de différence, la remet et exige les onze au vert. Si vos captures ne détectent pas le changement,
cette marque reste au rouge même si les onze passent.

**Une limite, mesurée.** Un changement d'une seule unité dans le bleu, de `0xFF0B6E4F` à `0xFF0B6E4E`, a donné 11 sur 11
au vert (19 nov. 2026). Ces captures utilisent la tolérance par défaut de Roborazzi 1.79.0 : `CapturasTest` ne lui passe
aucune option de comparaison. Son README décrit cette distance maximale par pixel (0.001 par défaut) et les options pour
tolérer davantage, pensées pour le lissage des bords. C'est pourquoi le critère de cette pratique est le changement du
vert au rouge que l'on voit, et non détecter chaque nombre.

Le 2B.4 est terminé quand vous pouvez cocher les quatre :

- [ ] Votre prédiction du point 1 est écrite, avec ce qui est sorti en dessous.
- [ ] Vous avez collé le chemin d'une image `_compare.png` et décrit son panneau du milieu.
- [ ] Vous avez expliqué pourquoi ces captures sont passées au rouge et pas les autres.
- [ ] Le `verifica` donne 18 sur 18, avec les marques du sabotage au vert.

**Ce qui le prouve :** une exécution au vert, une exécution au rouge avec la différence d'image, une exécution au vert
de nouveau (objectif 2B.4).

---

## Vos exercices

L'étape 1, le 2B.2 (étape 3) et l'étape 6 se font **sans IA**.

| # | Vous faites | C'est fait quand |
|---|---|---|
| 2B.1 | Vous écrivez le bouton et vous vérifiez la carte et l'étiquette que Claude Code a générées ; l'écran d'accueil les utilise | Les marques « Texto » (Texte) des composants et de `Inicio.kt` au vert, et les 7 tests de la 02 au vert |
| 2B.2 | Vous écrivez les aperçus avec `@PreviewParameter` et vous lisez le panneau sans émulateur | Vous avez `respuestas/02b-panel.png` et votre prédiction comparée |
| 2B.3 | Vous construisez le catalogue dans `src/debug` et vous l'ouvrez dans l'émulateur | Les marques « Compilado » (Compilé) du catalogue au vert, et vous avez écrit ce que vous avez vu dans le catalogue et en quoi il diffère du panneau |
| 2B.4 | Vous convertissez chaque aperçu en capture et vous cassez une couleur | Les quatre cases de l'étape 6, et le `verifica` à 18 sur 18 |

**Durée :** comptez environ deux heures et demie, avec l'émulateur déjà installé. C'est une estimation : nous
l'ajusterons avec le temps que prendra la première cohorte.

---

## Ça ne tourne pas ?

| Vous voyez | Cause probable | Ce que vous faites |
|---|---|---|
| En ouvrant « Catálogo », l'application se ferme ; dans `adb logcat` apparaît `ClassNotFoundException: Didn't find class "androidx.compose.material.icons.Icons$Filled"` | Showkase 1.0.5 utilise des icônes que les versions de Compose de cette série n'apportent plus d'elles-mêmes (mesuré le 19 nov. 2026) | Vérifiez que `app/build.gradle.kts` apporte toujours `debugImplementation(libs.compose.material.icons.core)` ; elle vient dans le projet de départ |
| `Unresolved reference 'getBrowserIntent'` | KSP n'a pas encore généré le code de Showkase | Compilez une fois (`./gradlew assembleDebug`) |
| Un composant n'apparaît pas dans le catalogue | Son aperçu est `private` | Retirez-lui `private` : le README de Showkase demande des aperçus publics ou `internal` |
| ❌ « CapturasTest corrió 10 pruebas y esperaba 11 » (`CapturasTest` a lancé 10 tests et en attendait 11) | Il manque une variante dans un fournisseur, ou la liste des cas a été écrite à la main | Comparez vos fournisseurs avec le tableau de l'étape 3 |
| ❌ « N de 11 capturas no coinciden » (N captures sur 11 ne coïncident pas) sans que vous ayez rien cassé | Vous avez changé un composant exprès, ou vos références viennent d'une autre machine | Si le changement est celui que vous vouliez, réenregistrez avec `./gradlew recordRoborazziDebug` et vérifiez les nouvelles images avant de les verser |
| ❌ « Tema.kt cambió » (`Tema.kt` a changé) | La couleur de l'étape 6 n'est pas revenue | Remettez la ligne à la main et regardez `git diff` |
| ❌ « Las pruebas no llegaron a correr » (les tests n'ont pas pu se lancer) et les lignes qu'il montre ne suffisent pas | Le `verifica` ne montre que les premières lignes de l'erreur | Depuis le dossier du projet, lancez `./gradlew verifyRoborazziDebug --stacktrace > fallo.txt 2>&1` et lisez les 40 dernières lignes de `fallo.txt` avant de conclure que c'est une erreur de compilation |

---

## Pour conclure

**Répondez sans relire :**

1. Que gagnez-vous à voir un composant isolé, dans toutes ses variantes, avant de le mettre dans un écran ?
2. Pourquoi le catalogue vit-il dans `src/debug` ?
3. Vos onze captures sont au vert. Dites une chose que cela démontre et une chose qu'il ne démontre pas.
4. Pourquoi les sept tests de la 02 ne se sont-ils pas rendu compte de la couleur rouge ?

**La suite :** vous revenez à la séquence principale, dans la L03.

---

## Comparez vos réponses

**Étape 1.** Désactivé, le bouton se peint en gris éteint et ne répond pas au toucher ; le texte long se coupe en deux
lignes, centré (mesuré dans les captures `boton_deshabilitado` et `boton_texto_largo`, 19 nov. 2026).

**Étape 2.** `Inicio.kt` et `MainActivity.kt` changent ; `Tienda.kt` et `Tema.kt`, non. La carte ne reçoit pas de
couleurs parce qu'elle les demande au thème qui l'entoure avec `MaterialTheme.colorScheme` : à l'intérieur de
`TemaTienda`, ce sont celles de `Tema.kt`.

**Étape 3.** Onze aperçus : quatre du bouton, trois de la carte et quatre de l'étiquette. La carte des tasses se termine
par « … » : son nom ne tient pas sur deux lignes à 320 dp de large.

**Étape 4.** `src/main` voyage dans l'application que l'on publie ; `src/debug`, selon la documentation d'Android,
seulement dans celle de débogage. Un catalogue avec tous les composants ne sert pas au client. Entre le panneau et le
catalogue : le panneau montre les noms de `getDisplayName` et vit dans Android Studio ; le catalogue répète le titre de
l'aperçu dans chaque variante et vit sur le téléphone.

**Étape 5.** Onze images, une par variante.

**Étape 6.** Passent au rouge celles qui peignent `VerdeTienda`. Dans la palette claire, `VerdeTienda` est la couleur
primaire : le bouton activé, celui du texte long et le prix des deux cartes claires l'utilisent. L'étiquette
« Entregada » l'utilise directement avec `colorDe`, sans passer par le thème : c'est pourquoi elle change aussi en
sombre. Ne changent pas : le bouton désactivé, qui utilise un gris du thème ; le bouton et la carte en sombre, dont la
primaire est `VerdeClaro` ; ainsi que « Recibida » et « Enviada », qui ont leur propre couleur.

**Pour conclure :**

1. Vous voyez d'un coup les cas qui, dans l'application, sont difficiles à trouver —désactivé, sombre, texte long— et
   vous corrigez le composant avant qu'il arrive dans plusieurs écrans.
2. Pour qu'il voyage seulement dans l'application de débogage et non dans celle que l'on publie.
3. Il démontre que chaque variante se dessine comme sa référence et qu'un changement visible de couleur les fait passer
   au rouge. Il ne démontre pas que le design soit bon, ne détecte pas un changement de couleur très petit, ni ne dit
   comment cela se voit sur un vrai téléphone.
4. Parce qu'ils mesurent quel écran reste et quel texte apparaît après chaque toucher ; aucun ne regarde les pixels.

---

## Sources de la leçon

Ouvertes le 19 nov. 2026.

- Android Developers. *Preview your UI with composable previews* (`@PreviewParameter`, un aperçu par valeur, `getDisplayName`). https://developer.android.com/develop/ui/compose/tooling/previews
- Android Developers. *Compose Preview Screenshot Testing* (le plugin n'est plus maintenu ; drapeau expérimental). https://developer.android.com/studio/preview/compose-screenshot-testing
- Android Developers. *Test suites in the Android Gradle plugin* (expérimental ; AGP 9.6.0-alpha03 ou plus). https://developer.android.com/studio/preview/test-suites
- Android Developers. *Material Design 3 in Compose* (`MaterialTheme`, palette claire et sombre). https://developer.android.com/develop/ui/compose/designsystems/material3
- Android Developers. *Configure build variants* (le dossier `src/debug`). https://developer.android.com/build/build-variants
- Android Developers. *Android Gradle plugin release notes* (versions en vigueur d'AGP). https://developer.android.com/build/releases/about-agp
- Kotlin. *Kotlin Symbol Processing API*. https://kotlinlang.org/docs/ksp-overview.html
- Airbnb. *Showkase* (README : installation, `@ShowkaseRoot`, aperçus publics). https://github.com/airbnb/Showkase
- Roborazzi (README : tâches `record` et `verify`, `@GraphicsMode`, comparateur). https://github.com/takahirom/roborazzi
- Maven Central (versions de Roborazzi, Showkase, KSP, Paparazzi et Robolectric). https://repo1.maven.org/maven2/io/github/takahirom/roborazzi/roborazzi/
- Nos propres mesures : Mac avec Apple silicon, 19 nov. 2026 (le `verifica` sur `inicio/` et `solucion/`, un sabotage par marque, la couleur en rouge et d'une unité, le catalogue dans un émulateur Pixel 9 avec Android 37, et la panne des icônes).
- Mesure sous Linux Mint 22.3, 19 nov. 2026 : le `verifica` sur `solucion/` depuis un clone neuf, 18 sur 18, les onze
  captures identiques à leurs références et le sabotage de la couleur avec 6 sur 11 au rouge.
