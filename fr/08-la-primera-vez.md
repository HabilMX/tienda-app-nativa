# Leçon 08 — La première fois

**Avant de lire, trois mots.** Une ligne chacun.

- **Utilisateur nouveau :** celui que Firebase vient de créer dans votre projet, lors de cette même connexion.
- **Résultat de la connexion :** ce que Firebase répond quand quelqu'un se connecte. Dans le code, il s'appelle
  `AuthResult`.
- **`isNewUser` :** la question à laquelle ce résultat sait répondre : cet utilisateur est-il nouveau, ou existait-il
  déjà ?

**Ce que vous en retirez :** une boutique qui souhaite la bienvenue à la personne qui se connecte pour la première fois,
avec Google ou avec Microsoft, et qui ne la lui redonne pas quand elle revient. Et un compte honnête de ce que Firebase
a fait pour vous sur tout le tronçon, de ce que vous devriez construire sans lui et de ce qui n'existe pas encore.

**Idée nouvelle de cette leçon :** distinguer l'utilisateur nouveau de celui qui revient. Le reste —les portes qui
répondent maintenant aussi si c'est la première fois, la ligne de bienvenue dans l'accueil et l'état qui s'en souvient—
est fourni et expliqué, et n'est pas évalué ici.

---

## La scène

13 novembre 2026, lors de l'essai de cette série. Un compte Google réel se connecte pour la première fois à
l'application de test. L'écran dit : « Hola, … (primera vez) » (Bonjour, … (première fois)).

Quelqu'un sort et se reconnecte avec le même compte. Maintenant, il dit : « Hola, … ». Sans la parenthèse.

Cette même nuit, la même chose se produit avec Microsoft : la première connexion apporte « (primera vez) » ; la seconde,
non.

Et voici ce qui est étrange. Cette application n'avait pas de base de données. Elle ne gardait pas de liste de ceux qui
s'étaient déjà connectés. Elle n'écrivait rien sur le téléphone. Comment a-t-elle su que la deuxième fois n'était plus la
première ?

L'application ne le savait pas d'elle-même. C'est Firebase qui le lui a dit, en un seul mot qui peut arriver caché dans
la réponse de la connexion. Votre application le reçoit depuis la leçon 04, et l'a jeté à la poubelle à chaque fois : de
cette réponse, vos portes ne tirent que le nom. Cette leçon le récupère. Comme ce mot peut ne pas arriver, votre
application posera la question de façon sûre : s'il manque, il n'y a pas de bienvenue.

---

## Avant de commencer

**Ce dont vous avez besoin :** la [07](07-la-sesion-se-queda.md) terminée (son `verifica` à 82 sur 82). Les étapes 1 à
3, 5 et 6 ne demandent aucun compte. L'étape 4 se fait sur votre téléphone ou émulateur, avec votre compte Google de la
04 et, si vous avez fait la partie facultative, votre enregistrement Microsoft de la 05.

**Le projet de départ.** Depuis la racine du dépôt de la série :

```bash
git pull
cp lecciones/03-la-app-conoce-a-firebase/inicio/app/google-services.json lecciones/08-la-primera-vez/inicio/app/
cd lecciones/08-la-primera-vez/inicio
```

Il part de la solution de la 07, avec l'échafaudage de cette leçon par-dessus. `./verifica solucion` sort au rouge dans
un clone propre, parce que sans votre fichier le projet ne compile pas. Vos réponses vont dans `respuestas/08.md`.

**Deux « bienvenues » qui ne sont pas les mêmes.** L'écran `Bienvenida`, depuis la leçon 02, est la page de garde : celle
que voit toute personne qui ne s'est pas connectée, avec le bouton « Entrar » (Entrer). La bienvenue de cette leçon est
autre chose : une ligne dans l'**accueil**, sous « Hola, Ana », que seul voit l'utilisateur nouveau. Le texte dit :
« Es tu primera vez aquí: te damos la bienvenida. » (C'est votre première fois ici : nous vous souhaitons la bienvenue.)

**Règle toujours en vigueur.** Ne collez ni ne versez jamais dans le dépôt un jeton, ni des captures ou des journaux qui
en contiennent. Le résultat d'une connexion contient des jetons ; les portes de cette application n'en tirent que le nom
et un oui ou un non.

**Ce que cette leçon ne fait pas.** Elle ne crée pas de profil de la boutique : elle ne conserve ni adresse, ni
commandes, ni préférences. Elle ne change pas ce que font les écrans de la 04 à la 07 : se connecter, les deux boutons,
la liaison, rouvrir et fermer la session se comportent pareil, et leurs 82 tests y veillent.

---

## Étape 1 · Prédisez : quand la bienvenue apparaît-elle ?

C'est la première étape de toujours : **vous prédisez** avant de rien demander à Claude Code. Ne lisez pas encore
l'étape 2.

Pensez à l'application **terminée** de cette leçon et à ces quatre moments, dans l'ordre :

1. Ana se connecte avec Google pour la première fois de sa vie à votre boutique.
2. Ana ferme l'application depuis la liste des applications récentes et la rouvre (comme dans la 07).
3. Ana touche « Cerrar sesión » (Fermer la session) et se reconnecte avec Google.
4. Luis se connecte avec Microsoft pour la première fois, et Android ferme l'application pendant qu'il est sur la page de
   Microsoft. Au retour, l'application reprend la connexion (comme dans la 05).

Écrivez dans `respuestas/08.md` une ligne qui commence ainsi, avec les numéros des moments où vous croyez que la
bienvenue apparaît et, en une phrase, pourquoi :

```text
predicción 8.1: …
```

*(La ligne dit « prédiction 8.1 ». Elle reste en espagnol parce que c'est ce que cherche le `verifica`.)*

**Mesurez le point de départ.** Depuis la racine du dépôt :

```bash
./lecciones/08-la-primera-vez/verifica
```

Le 19 nov. 2026, avec un fichier inventé, la dernière ligne du décompte a été
`Resultado: 84 de 97 pruebas en verde (13 en rojo, 0 con error, 0 saltadas)`. Il y en a 97 : les 82 dont vous héritez de
la 07, déjà au vert, et 15 de la première fois. Parmi celles-ci, deux passent déjà avec l'échafaudage (celles de
« l'utilisateur qui revient » : aujourd'hui votre application ne dit jamais que c'est la première fois, et dans ce cas
elle a raison) et treize attendent votre code.

*(Résultat : 84 tests sur 97 au vert, 13 au rouge, 0 avec erreur, 0 sauté.)*

**Ce qui le prouve :** votre prédiction, face à ce que vous verrez à l'étape 3 (les tests) et à l'étape 4 (votre
téléphone). Ce que vérifie le `verifica` de vos réponses est dans « Vos exercices ».

---

## Étape 2 · Ce que le projet apporte déjà

**Où vit la réponse.** Quand quelqu'un se connecte, Firebase répond avec un `AuthResult`. Sa référence dit que c'est le
résultat « d'opérations qui peuvent affecter l'état d'authentification ». En plus de l'utilisateur, il apporte
`getAdditionalUserInfo()` : des informations supplémentaires. Et la référence d'`AdditionalUserInfo` dit qu'il contient
des informations sur l'utilisateur « à la suite d'une connexion, d'une liaison ou d'une réauthentification réussies ».
Sa fonction `isNewUser()` « indique si l'utilisateur est nouveau ou existant ». En Kotlin, elle se lit comme une
propriété : `resultado.additionalUserInfo?.isNewUser`.

**Deux détails de la documentation qui décident de la façon d'écrire cette ligne :**

- La référence marque `getAdditionalUserInfo()` comme `@Nullable` : il peut arriver `null`. Kotlin l'apporte comme un
  type qui admet `null`, et l'opérateur `?.` existe pour cela : si ce qui est à gauche est `null`, le résultat est
  `null` au lieu de planter.
- La même référence dit qu'il renvoie des informations « si le fournisseur est Facebook, GitHub, Google ou Twitter ».
  Microsoft n'est pas dans cette liste. Mais la page d'`AdditionalUserInfo`, dans `getProfile()`, nomme bien Microsoft,
  et lors de l'essai du 13 nov., la connexion avec Microsoft a dit « (primera vez) » la première fois et pas la seconde.
  La documentation n'est pas cohérente ici, et c'est pourquoi le code ne suppose rien : s'il n'y a pas d'information, il
  n'y a pas de bienvenue.

**Ce qui a changé dans l'application.** Un fichier est nouveau et cinq ont changé par rapport à la solution de la 07 ;
dans les tests arrivent trois nouvelles classes et leurs doublures, `DoblesDePrimeraVez.kt`. Ouvrez-les ; ne modifiez que
les trois que dit l'étape 3 :

| Fichier | Ce qu'il apporte |
|---|---|
| `PrimeraVez.kt` (nouveau) | La forme de votre fonction `esPrimeraVez(resultado: AuthResult)`, vide. Vous l'écrivez à l'étape 3 |
| `Entrada.kt` | `Resultado.Entro` apporte, en plus du nom, `primeraVez`. Il vaut `false` si personne ne le dit : ainsi tout ce qui va de la 04 à la 07 reste pareil |
| `Puertas.kt` | `QuienEntro` : le nom et un oui ou un non. Et dans la porte de Google, une nouvelle fonction : `verificarConPrimeraVez()` |
| `PuertaDelNavegador.kt` | Dans la porte de Microsoft, deux nouvelles : `abrirConPrimeraVez()` et `pendienteConPrimeraVez()` |
| `Tienda.kt` | Il retient si celui qui s'est connecté est nouveau et le passe à l'accueil. En rouvrant avec une session et en fermant la session, il le remet à `false` |
| `Inicio.kt` | Avec `primeraVez` à `true`, il ajoute la ligne de bienvenue. Avec `false`, c'est l'accueil de la 07 |

**L'astuce des nouvelles fonctions.** Les trois apportent un corps par défaut qui répond « ce n'est pas la première
fois ». C'est pourquoi les doublures de la 04 à la 07, qui ne savent rien de tout cela, continuent de compiler et leurs
tests restent au vert. Les vraies portes remplacent ce corps : elles lisent l'`AuthResult` de Firebase et interrogent
**votre** `esPrimeraVez`. Vers l'extérieur de la porte ne sort que `QuienEntro` : le nom et un `Boolean`. Le résultat
complet, avec ses jetons, reste à l'intérieur.

Le reste est identique à la solution de la 07, et le `verifica` le compare fichier par fichier. C'est une contrainte de
cette activité, pas de l'objectif : si vous avez modifié l'un de ces fichiers et que votre application fait bien ce que
demande la leçon, ce rouge ne dit pas que vous n'avez pas compris le 8.1. Remettez-le comme il venait, ou demandez que
quelqu'un relise votre changement.

---

## Étape 3 · Avec Claude Code : décider la première fois

Il entre comme toujours : **vous prédisez → vous demandez → vous lisez ce qu'il a généré → vous lancez le `verifica` →
vous l'expliquez.**

1. **Prédisez**, dans `respuestas/08.md`, en une ligne libre : si `additionalUserInfo` arrive `null` et que vous écrivez
   `resultado.additionalUserInfo!!.isNewUser`, qu'arrive-t-il à la personne qui essaie de se connecter ?
2. **Demandez** à Claude Code, depuis `inicio/` : « Dans `PrimeraVez.kt`, `esPrimeraVez` renvoie `true` seulement si
   `resultado.additionalUserInfo?.isNewUser` est `true` ; avec `null` il renvoie `false`. Dans `EntradaConGoogle.kt`,
   remplace `verificador.verificar(token)` par `verificador.verificarConPrimeraVez(token)` et passe son nom et sa
   `primeraVez` à `Resultado.Entro`. Dans `EntradaConMicrosoft.kt`, dans `retomar()` et dans `entrar()`, utilise
   `navegador.pendienteConPrimeraVez()` et `navegador.abrirConPrimeraVez(proveedor)` à la place de `pendiente()` et
   `abrir(proveedor)`, et passe de la même façon le nom et la `primeraVez`. Ne change aucun autre fichier. »
3. **Lisez.** Le cœur doit ressembler à ceci :

```kotlin
// PrimeraVez.kt
fun esPrimeraVez(resultado: AuthResult): Boolean =
    resultado.additionalUserInfo?.isNewUser == true

// EntradaConGoogle.kt, à l'étape 4 de entrar()
val quien = verificador.verificarConPrimeraVez(token)
Resultado.Entro(quien.nombre, quien.primeraVez)

// EntradaConMicrosoft.kt, dans entrar()
val quien = navegador.pendienteConPrimeraVez() ?: navegador.abrirConPrimeraVez(proveedor)
Resultado.Entro(quien.nombre, quien.primeraVez)
```

   Vérifiez trois choses. Qu'il y ait `== true` et non `!!` : avec `?.`, un `null` devient `null`, et `null == true`
   vaut `false`. Que dans `EntradaConMicrosoft.kt` **les deux** fonctions changent, `retomar()` et `entrar()` : si une
   seule change, l'autre continue de jeter la première fois. Et que personne n'ait touché aux tests, aux portes ni aux
   écrans.

4. **Lancez le `verifica`.** Les 97 tests peuvent être au vert. Le rendu n'est pas encore complet : il manque vos lignes
   du 8.1, du 8.2 et du 8.3 dans `respuestas/08.md`, et le `verifica` le dit à la fin.
5. **Expliquez**, en trois lignes : pourquoi la décision vit à l'intérieur de la porte et vers les écrans ne sort qu'un
   `Boolean`.

**Ce qui le prouve, et jusqu'où.** Des tests donnent à votre `esPrimeraVez` un `AuthResult` factice et regardent ce
qu'elle décide : avec `isNewUser` à `true`, à `false` et sans information supplémentaire. D'autres donnent à vos deux
connexions des portes qui répondent « il est nouveau » ou « il existait déjà » et regardent si le `Resultado` le porte,
aussi en reprenant avec Microsoft. Et ceux du parcours dessinent la boutique avec le projet factice de la 06 : Ana se
connecte avec Google et voit la bienvenue ; elle ferme la session, se reconnecte et ne la voit plus, et c'est toujours le
même utilisateur. De même avec Luis et Microsoft. En rouvrant avec une session, sans bienvenue. En reprenant avec
Microsoft un utilisateur nouveau, avec bienvenue.

Les sabotages ont été mesurés sur la solution le 19 nov. 2026 :

| Sabotage | Résultat |
|---|---|
| `esPrimeraVez` avec `!= false` : un `null` compte comme nouveau | 96 sur 97 |
| `esPrimeraVez` avec `additionalUserInfo!!` | 96 sur 97 |
| `esPrimeraVez` à l'envers (`isNewUser == false`) | 95 sur 97 |
| `EntradaConGoogle` qui ne passe pas la `primeraVez` | 93 sur 97 |
| `EntradaConMicrosoft` qui ne la passe pas dans `entrar()` | 93 sur 97 |
| `EntradaConMicrosoft` qui ne la passe pas dans `retomar()` | 95 sur 97 |
| `EntradaConGoogle` qui dit toujours que c'est la première fois | 92 sur 97, et trois des rouges sont héritées de leçons précédentes |
| `esPrimeraVez` qui ne lit `isNewUser` que lorsque le résultat est la doublure des tests | 96 sur 97 : chaque cas est aussi testé avec le résultat d'une autre classe |
| `EntradaConGoogle` qui n'échoue que lorsque la porte est `VerificadorConFirebase` | 97 sur 97 : les tests ne le voient pas, parce qu'ils ne lui passent que des doublures. C'est la marque de la porte concrète qui l'attrape |

Chacun laisse en outre sa marque au rouge dans la sortie, avec l'étape qui le corrige.

Trois limites. « Il est nouveau », dans les tests, c'est une doublure qui le décide : le projet factice dit « nouveau »
quand cette connexion a créé un utilisateur, ce que Firebase promet, mais ce n'est pas Firebase. Aucun test ne voit un
vrai `AuthResult`. Et aucun ne regarde ce que répond `isNewUser` lors d'une liaison : cette application n'utilise pas ce
résultat pour la bienvenue (l'étape 4 dit pourquoi).

Cette leçon n'a pas de niveau instrumenté, comme la 06 et la 07 : la bienvenue est une ligne de texte dans un écran qui a
déjà été testé à l'intérieur d'Android dans la 04, et ce qui est nouveau est une décision, qui se teste sur votre
ordinateur.

---

## Étape 4 · Sur le téléphone : la vraie première fois

Installez et ouvrez l'application avec votre `google-services.json`, comme dans la 04 :

```bash
./gradlew installDebug
```

**Le problème de votre compte.** Votre compte Google est déjà entré dans votre projet dans la 04. Pour Firebase, il n'est
plus nouveau, donc avec lui vous ne verrez **pas** la bienvenue. C'est justement ce que cette leçon promet, et c'est
votre première vérification : connectez-vous avec votre compte et regardez qu'il dise « Hola, … » sans la ligne de
bienvenue.

Pour la voir, il faut que Firebase crée un utilisateur. Vous avez deux chemins :

- **Un autre compte Google** qui ne s'est jamais connecté à votre projet, si vous en avez un de test.
- **Supprimer votre utilisateur** dans la console de Firebase, dans Authentication › Users. C'est votre projet de test,
  mais supprimer un utilisateur ne se défait pas, et avec lui part toute liaison que vous avez faite dans la 06.

Connectez-vous. La bienvenue doit apparaître. Touchez « Cerrar sesión », reconnectez-vous avec le même compte, et elle ne
doit plus apparaître. Comparez avec votre prédiction.

Ce que l'on a déjà vu lors de l'essai du 13 nov. 2026, avec un code de test : `isNewUser` a valu `true` la première fois
et `false` au retour, avec Google et avec Microsoft. Ce que nous **n'avons pas encore capturé avec un compte réel**, c'est
ce parcours avec le code de cette leçon : la ligne de bienvenue dans votre accueil et qu'elle ne revienne pas en se
reconnectant. S'il vous apparaît différent, dites-le-nous.

**Deux cas que la documentation ne tranche pas.** On les a cherchés dans les pages de Firebase le 19 nov. 2026 :

- **En liant** (leçon 06). `AdditionalUserInfo` dit qu'il arrive aussi après une liaison, mais aucune page ne dit ce que
  vaut `isNewUser` dans ce cas : ni la référence, ni le guide pour lier des comptes, ni celui de Microsoft. **Ce n'est
  pas documenté.** Cette application n'interroge pas la liaison : qui lie s'est déjà connecté avant avec l'autre bouton,
  et cette connexion a déjà décidé s'il y avait une bienvenue.
- **En reprenant** (leçon 05). `getPendingAuthResult()` livre un `AuthResult`, le même type qu'une connexion normale, et
  l'application le lit pareil. Mais aucune page ne dit de façon explicite qu'ici `isNewUser` se comporte pareil :
  **ce n'est pas documenté**. Lors de l'essai, on a repris une connexion avec un compte qui existait déjà, et on ne l'a
  pas essayé avec un nouveau. Si vous l'essayez et voyez autre chose, dites-le-nous.

**Ce qui le prouve :** ce que vous observez. Il n'y a pas de marque dans le `verifica` pour cette étape.

---

## Étape 5 · Sans IA : ce que Firebase a fait pour vous

C'est l'exercice 8.2, celui de la clôture du tronçon, et il se fait **sans Claude Code**. Ne relisez pas non plus les
leçons précédentes en y répondant : écrivez d'abord ce dont vous vous souvenez ; ensuite, si vous voulez, vérifiez.

Huit leçons, une application qui vous laisse entrer avec deux fournisseurs, se souvient de vous et sait si c'est votre
première fois. Maintenant le compte honnête. Écrivez dans `respuestas/08.md` trois lignes qui commencent ainsi :

```text
8.2 Firebase hizo: …
8.2 sin Firebase: …
8.2 todavía no hay: …
```

*(Les lignes disent : 8.2 Firebase a fait… ; 8.2 sans Firebase… ; 8.2 il n'y a pas encore… Elles restent en espagnol
parce que c'est ce que cherche le `verifica`.)*

1. **Firebase a fait :** au moins trois choses que Firebase a faites pour vous sur le tronçon. Pensez à ce qui se passe
   entre le moment où vous choisissez votre compte et celui où l'application dit « Hola ».
2. **Sans Firebase :** pour chacune, quelle pièce devriez-vous construire. « Un serveur » ne suffit pas : dites ce que ce
   serveur devrait faire.
3. **Il n'y a pas encore :** quelque chose qu'une vraie boutique a besoin de savoir de vous et que votre application, aujourd'hui,
   n'a pas où conserver.

**Ce qui le prouve :** vos trois lignes, contre « Comparez vos réponses » (ce que vérifie le `verifica`, dans « Vos
exercices »).

---

## Étape 6 · Juger une réponse de Claude Code

C'est l'exercice 8.3. Imaginez qu'à la fin de l'étape 3 vous demandiez à Claude Code : « Quel est le niveau de sécurité
de la connexion de mon application ? ». Une réponse **comme celle-ci** est vraisemblable. Ce n'est pas la citation d'une
conversation réelle : elle est écrite pour cet exercice.

> J'ai passé en revue la connexion de votre application. Trois choses :
>
> **A.** Votre application ne valide pas le jeton d'identité de Google : elle le remet à Firebase, et ce sont les
> serveurs de Firebase qui le vérifient avant de laisser entrer qui que ce soit.
>
> **B.** Avec Firebase, votre connexion est déjà complètement sûre : personne ne pourra entrer dans la boutique avec un
> compte qui n'est pas le sien, donc il n'est pas nécessaire de vérifier autre chose.
>
> **C.** Firebase conserve le jeton d'actualisation chiffré sur le téléphone, donc même si quelqu'un extrait une copie
> des données de l'application, elle ne lui sert à rien.

Cela sonne bien. Les trois phrases sont dites avec la même assurance. Votre travail est de les séparer.

**À vous.** Pour chaque affirmation, écrivez dans `respuestas/08.md` l'un de trois jugements —`correcta` (correcte),
`exagerada` (exagérée) ou `sin comprobar` (non vérifiée)— au début de la ligne et, ensuite, pourquoi. Par exemple,
`8.3-A: correcta porque …` :

```text
8.3-A: <juicio> porque …
8.3-B: <juicio> porque …
8.3-C: <juicio> porque …
8.3 comprobé: …
fuente 8.3: https://…
```

*(« porque » : parce que ; « comprobé » : j'ai vérifié ; « fuente » : source. Elles restent en espagnol parce que c'est
ce que cherche le `verifica`.)*

Dans `8.3 comprobé`, dites deux choses : ce que la réponse a vérifié elle-même (a-t-elle cité un fichier, une ligne, une
page ?) et ce que vous avez vérifié, vous, et comment. Dans `fuente 8.3`, l'adresse d'une page officielle que vous avez
utilisée pour vérifier quelque chose.

**Ce qui le prouve :** vos trois jugements contre « Comparez vos réponses », et votre vérification (ce que vérifie le
`verifica`, dans « Vos exercices »).

---

## Vos exercices

L'étape 5 se fait **sans IA**. De vos réponses dans `respuestas/08.md`, le `verifica` ne vérifie que le format : que les
lignes, les jugements et la source y soient. Il ne sait pas si vous avez deviné juste ni si vous avez écrit sans IA ;
cela, vous le concluez vous-même en comparant avec « Comparez vos réponses ».

| # | Vous faites | C'est fait quand |
|---|---|---|
| 8.1 | Vous décidez avec `isNewUser` si l'on montre la bienvenue et vous démontrez qu'elle n'apparaît que la première fois | Le `verifica` dit « Nivel simulado: completo » (niveau simulé : complet), avec les 97 tests au vert, et votre `predicción 8.1` est comparée avec ce que vous avez vu. C'est le niveau simulé ; l'étape 4 est votre essai sur le téléphone |
| 8.2 | Vous expliquez ce que Firebase a fait pour vous, ce que vous construiriez sans lui et ce qui n'existe pas encore | Vos trois lignes `8.2`, écrites sans IA, nomment la vérification du jeton, la création de l'utilisateur, la session qui reste et ce qui n'existe pas encore (le profil) |
| 8.3 | Vous jugez une réponse de Claude Code sur la sécurité de la connexion | Vos trois jugements coïncident avec « Comparez vos réponses », chacun avec son pourquoi et avec ce qui l'appuie : la page officielle, ou le fichier et la ligne |

**Durée :** comptez une heure et demie sans l'étape 4, et vingt minutes de plus avec elle. C'est une estimation : nous
l'ajusterons avec le temps que prendra la première cohorte.

---

## Ça ne tourne pas ?

| Vous voyez | Cause probable | Ce que vous faites |
|---|---|---|
| `File google-services.json is missing.` | Vous n'avez pas copié votre fichier dans `inicio/app/` | Le `cp` de « Avant de commencer » |
| `NotImplementedError` dans les tests de la première fois | `PrimeraVez.kt` en est encore à son `TODO` | Étape 3 |
| ❌ « Con additionalUserInfo en null tu esPrimeraVez truena o dice que sí » (avec `additionalUserInfo` à `null`, votre `esPrimeraVez` plante ou dit oui) | Vous avez utilisé `!!`, ou `!= false` | Étape 3 : `?.` et `== true` |
| ❌ « Retomar con Microsoft » (reprendre avec Microsoft) et tout le reste de Microsoft au vert | Vous avez changé `entrar()` mais pas `retomar()`, ou l'inverse | Étape 3 : les deux fonctions |
| ❌ « De las 82 pruebas heredadas… » (des 82 tests hérités…) | Votre connexion dit toujours que c'est la première fois, et le `Resultado` n'est plus celui qu'attendent les tests d'avant | Passez la `primeraVez` qu'a répondue la porte, rien de plus |
| ❌ « Restricción de esta actividad… ya no son iguales en inicio/ y en solucion/ » (contrainte de cette activité… ils ne sont plus égaux dans inicio/ et dans solucion/) | Un fichier fourni a changé | `git diff -- <fichier>` et défaites-le à la main |
| Sur le téléphone, la bienvenue n'apparaît jamais | Votre compte existait déjà dans votre projet, depuis la 04 | Étape 4 : un autre compte, ou supprimez votre utilisateur |
| Sur le téléphone, la bienvenue apparaît chaque fois que vous vous connectez | Vous avez supprimé l'utilisateur dans la console entre une connexion et l'autre, ou quelque chose de différent que nous n'avons pas vu | Vérifiez Authentication › Users ; si l'utilisateur est toujours là et que la bienvenue apparaît chaque fois, dites-le-nous |
| ❌ « EntradaConGoogle.kt o EntradaConMicrosoft.kt nombra una puerta concreta » (`EntradaConGoogle.kt` ou `EntradaConMicrosoft.kt` nomme une porte concrète) | L'une de vos connexions nomme une vraie porte (`VerificadorConFirebase`, `NavegadorConFirebase`) ou une doublure des tests. Les tests ne leur passent que des doublures : si votre code demande laquelle lui est échue, le vert ne dit pas ce qu'il fait avec la vraie | Que vos connexions ne parlent qu'aux interfaces de leurs portes. La vraie porte, c'est `MainActivity.kt` qui la choisit, et il est fourni (étape 3) |
| ❌ « Las pruebas no llegaron a correr » (les tests n'ont pas pu se lancer) et ce qui suit ne dit pas la cause | Gradle a échoué pour quelque chose que le `verifica` ne reconnaît pas ; il vous montre ses dernières lignes | Lancez `./inicio/gradlew -p inicio testDebugUnitTest --stacktrace` et lisez la fin. Si vous la partagez, retirez d'abord les jetons et les chemins de votre ordinateur |

Le `verifica` de cette leçon a été mesuré sur un Mac avec Apple silicon. Il n'a pas été mesuré sous Linux.

---

## Pour conclure

**Répondez sans relire :**

1. Qui sait si c'est la première fois : votre application ou Firebase ? Pourquoi votre application ne peut-elle pas le
   savoir seule, aujourd'hui ?
2. Pourquoi `== true` et non `!!` ?
3. Votre `verifica` donne 97 sur 97. Dites une chose que cela démontre et une qu'il ne démontre pas.
4. En rouvrant avec une session, pourquoi n'y a-t-il pas de bienvenue, alors que c'est la deuxième fois que vous ouvrez
   l'application ce jour-là ?

**Clôture du tronçon.** Vous avez commencé avec un environnement vide. Aujourd'hui vous avez une application Android
native, en Kotlin et Compose, d'une boutique où l'on se connecte avec Google ou avec Microsoft ; où une personne qui a
la même adresse e-mail dans les deux arrive à un seul utilisateur, parce que cette personne l'a confirmé ; qui se
souvient de vous quand vous la rouvrez et vous oublie quand vous le lui demandez ; et qui vous souhaite la bienvenue
seulement la première fois. Chaque pièce est passée par le même tour : prédire, demander, lire, lancer et expliquer. Et
à l'étape 5, vous avez dit, avec vos mots, quelle part Firebase a prise et quelle part vous avez prise.

**La suite : le tronçon 2.** La même boutique, sur iPhone, avec SwiftUI. On ne traduit pas le code d'Android ligne par
ligne : l'application que vous avez terminée aujourd'hui est la **spécification** de ce que celle de l'iPhone doit faire,
et vos 97 tests disent, cas par cas, ce que veut dire « le faire ». Pour compiler une application d'iPhone, il faut un
Mac avec Xcode, donc ce tronçon n'est que sur Mac. Il sera publié quand il aura été essayé de bout en bout, comme
celui-ci.

---

## Comparez vos réponses

**Étape 1.** Seulement au 1 et au 4. Au 1, Firebase crée Ana et `isNewUser` vaut `true`. Au 2, il n'y a pas eu de
connexion : l'application demande à la session qui est encore à l'intérieur, et cela ne dit pas si c'est un nouveau. Au
3, Ana existe déjà : `isNewUser` vaut `false`. Au 4, Luis est nouveau et la connexion reprise apporte un `AuthResult`
comme n'importe quelle autre : les tests le mesurent avec une doublure ; avec un compte réel, ce n'est ni documenté ni
capturé (étape 4).

**Étape 3.** Elle n'entre pas, bien que Firebase l'ait laissée entrer. `!!` convertit le `null` en exception ; votre
connexion l'attrape avec son `catch (e: Exception)`, comme n'importe quelle autre panne, et la personne voit « No se
pudo entrar. Revisa tu conexión y vuelve a intentar. » (Impossible d'entrer. Vérifiez votre connexion et réessayez.). La
connexion était bonne. Et comme Firebase avait déjà conservé la session, en rouvrant l'application (leçon 07), celle-ci la
saluerait comme si de rien n'était : nous l'avons raisonné à partir du code, nous ne l'avons pas mesuré sur un
téléphone. Avec `?.` et `== true`, un `null` devient « ce n'est pas la première fois », et la personne entre sans
bienvenue.

**Étape 5.** Ce que l'on attend, avec ces idées ou les vôtres :

- **Firebase a fait :** il a vérifié le jeton d'identité de Google sur ses serveurs ; il a parlé avec Microsoft en
  utilisant l'ID client et la clé que vous avez enregistrés dans sa console, sans que la clé touche votre application ;
  il a créé l'utilisateur la première fois que vous vous êtes connecté, avec un identifiant unique ; dans ce projet, avec
  la règle d'un compte par adresse e-mail configurée dans ce projet, il a rejeté la seconde connexion avec cette adresse et,
  après que la personne l'a confirmé et que l'application a demandé la liaison, il a uni le second fournisseur au même
  utilisateur ; il a conservé la session sur le téléphone et renouvelle chaque heure le jeton d'identité avec celui
  d'actualisation ; et il vous a dit si l'utilisateur était nouveau.
- **Sans Firebase :** un serveur à vous qui vérifie que chaque jeton est authentique, qu'il a été émis pour votre
  application et qu'il n'a pas expiré ; l'échange avec Microsoft fait depuis ce serveur, qui est là où vivrait la clé ;
  une table d'utilisateurs avec leur identifiant et avec quels comptes on arrive à chacun ; la règle de l'adresse e-mail
  répétée, et l'appel qui unit un nouvel identifiant à l'utilisateur qui existe déjà ; vos propres jetons de session,
  comment ils se renouvellent et où ils se conservent sur le téléphone ; et la marque « utilisateur nouveau », que vous
  devriez calculer vous-même en créant la ligne.
- **Il n'y a pas encore :** un profil de la boutique. L'utilisateur d'authentification a un ensemble fixe de données : un
  identifiant, une adresse e-mail, un nom et une photo. La documentation elle-même dit qu'on ne peut pas lui en ajouter
  d'autres et qu'elles vont dans un autre stockage. Votre adresse, vos commandes et vos préférences n'ont pas encore où
  vivre : cela arrive quand le cours introduira le stockage.

**Étape 6.**

- **A : correcte.** La page de Firebase Authentication dit que l'application passe les identifiants au SDK et que ses
  services les vérifient et répondent. Et cela se vérifie dans votre code : `VerificadorConFirebase` remet le jeton à
  `signInWithCredential` et ne le vérifie jamais de son côté.
- **B : exagérée.** « Complètement » et « il n'est pas nécessaire de vérifier autre chose », aucune source ne le soutient.
  Dans le tronçon lui-même il y a des contre-exemples : une session ouverte reste ouverte sur un téléphone prêté (leçon
  07) ; la clé de Microsoft peut fuir dans une capture (leçon 05) ; et si votre application liait sans demander, elle
  réunirait des comptes sans permission (leçon 06). Firebase résout des parties ; la sécurité de la connexion dépend
  aussi de ce que vous faites.
- **C : non vérifiée.** Cela peut être vrai ou non : aucune des pages de cette série ne dit comment Firebase conserve les
  jetons sur le téléphone. La référence de `signOut` dit seulement qu'il supprime l'utilisateur « du cache sur disque ».
  La réponse ne cite rien. Pour la tenir pour bonne, quelqu'un devrait montrer la source.
- **Ce que la réponse a vérifié :** rien que l'on voie. Elle dit « j'ai passé en revue la connexion de votre
  application », mais ne nomme ni un fichier, ni une ligne, ni une page. Ce que vous pouvez vérifier : A, avec la
  documentation et avec `Puertas.kt`.

**Pour conclure :**

1. Firebase. Votre application ne garde pas de liste de ceux qui se sont déjà connectés ; Firebase, lui, a ses
   utilisateurs, et quand il en crée un, il le dit dans `isNewUser`.
2. Parce qu'`additionalUserInfo` peut arriver `null`, et que `!!` convertit ce `null` en exception : la personne verrait
   « No se pudo entrar » sans que rien n'ait échoué. Avec `== true`, `null` est « non ».
3. Il démontre ce que décide votre `esPrimeraVez`, que vos deux connexions passent cette décision et quand l'accueil
   donne la bienvenue, avec des doublures. Il ne démontre pas ce que répond `isNewUser` avec un compte réel, ni lors
   d'une liaison.
4. Parce qu'en rouvrant il n'y a pas eu de connexion : il n'y a pas d'`AuthResult` et personne à qui demander s'il est
   nouveau. L'application le laisse à « non ».

---

## Sources de la leçon

Ouvertes le 19 nov. 2026.

- Firebase. Référence d'`AdditionalUserInfo` (arrive après une connexion, une liaison ou une réauthentification ; `isNewUser` dit si l'utilisateur est nouveau ou existant ; `getProfile` nomme Microsoft parmi ses fournisseurs). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/AdditionalUserInfo
- Firebase. Référence d'`AuthResult` (résultat d'opérations qui affectent l'authentification ; `getAdditionalUserInfo` est `@Nullable` et sa description nomme Facebook, GitHub, Google et Twitter). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/AuthResult
- Firebase. Référence de `FirebaseAuth` (`signInWithCredential` crée le compte s'il n'existait pas ; `getPendingAuthResult` livre la connexion restée à moitié faite). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth
- Firebase. Référence de `FirebaseUser` (`startActivityForLinkWithProvider` renvoie un `AuthResult` ; ne dit pas ce que vaut `isNewUser` lors d'une liaison). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseUser
- Firebase. *Authenticate Using Microsoft on Android* (vérifier d'abord la réponse en attente ; les données du fournisseur dans `getAdditionalUserInfo()` ; ne mentionne pas `isNewUser`). https://firebase.google.com/docs/auth/android/microsoft-oauth
- Firebase. *Authenticate with Google on Android* (la première fois que l'utilisateur se connecte, un compte est créé dans votre projet). https://firebase.google.com/docs/auth/android/google-signin
- Firebase. *Link Multiple Auth Providers to an Account on Android* (le même identifiant quel que soit le fournisseur ; ne mentionne pas `isNewUser`). https://firebase.google.com/docs/auth/android/account-linking
- Firebase. *Users in Firebase projects* (propriétés fixes de l'utilisateur ; on ne lui en ajoute pas d'autres : elles vont dans un autre stockage). https://firebase.google.com/docs/auth/users
- Firebase. *Firebase Authentication* (« How does it work? » : l'application passe les identifiants et les services de Firebase les vérifient). https://firebase.google.com/docs/auth
- Kotlin. *Null safety* (l'opérateur `?.` renvoie `null` si ce qui est à gauche est `null`). https://kotlinlang.org/docs/null-safety.html
- Essai de la série, 13 nov. 2026, Mac avec Apple silicon, avec un code de test et des comptes réels : « (primera vez) » n'est apparu qu'à la première connexion, avec Google et avec Microsoft.
- Nos propres mesures : Mac avec Apple silicon, 19 nov. 2026. Le `verifica` sur `inicio/` et `solucion/`, chaque étape et chaque sabotage, avec des fichiers inventés et sans toucher à la console de Firebase ni à aucun compte. Versions du projet : celles de la série (AGP 9.4.1, Kotlin 2.4.21, Firebase BOM 35.2.0, qui apporte `firebase-auth` 25.0.0).
