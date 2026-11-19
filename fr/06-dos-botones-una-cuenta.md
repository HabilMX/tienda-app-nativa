# Leçon 06 — Deux boutons, un seul compte

**Avant de lire, trois mots.** Une ligne chacun.

- **Collision :** ce qui se passe quand quelqu'un se connecte avec un fournisseur et que son adresse e-mail a déjà un
  utilisateur dans votre projet, créé avec un autre fournisseur. Vous l'avez vue dans la leçon 05.
- **Lier :** ajouter à un utilisateur qui existe déjà une autre manière de se connecter. Cela ne crée pas de nouvel
  utilisateur.
- **Un compte par adresse e-mail :** l'option de votre projet Firebase qui fait exister la collision. Firebase l'appelle
  *One account per email address*.

**Ce que vous en retirez :** une personne qui a la même adresse e-mail chez Google et chez Microsoft et qui se retrouve
avec **un** utilisateur dans votre boutique, auquel elle accède avec l'un ou l'autre des deux boutons, parce qu'elle l'a
confirmé.

**Idée nouvelle de cette leçon :** une personne, deux fournisseurs. Avec « un compte par adresse e-mail », le second se
lie au compte qui existe déjà, et seulement avec sa confirmation. Le reste —l'écran qui pose la question, la porte de la
liaison et la note qui distingue une liaison d'une connexion— est fourni et expliqué, et n'est pas évalué ici.

**Une note sur les textes de l'application.** Comme dans les leçons précédentes, le projet livre ses textes d'écran en
espagnol, et les `verifica` cherchent ces textes exacts. Dans les blocs de code, les sorties de terminal et les tableaux,
ils restent tels que le projet les contient ; dans la prose, la leçon les cite en français avec l'original entre
parenthèses la première fois : l'écran de confirmation « Aussi avec Microsoft ? » (« ¿También con Microsoft? »), avec
« Lier Microsoft » (« Enlazar Microsoft ») et « Pas maintenant » (« Ahora no »).

---

## La scène

Ana s'est connectée à la boutique avec Google la semaine dernière. Aujourd'hui, elle touche l'autre bouton. Son compte
Microsoft a la même adresse e-mail.

Pour elle, c'est la même personne qui entre dans la même boutique. Pour votre projet, ce sont deux identifiants
différents qui prétendent avoir la même adresse e-mail.

C'est ce qui s'est passé lors de l'essai de cette série, le 13 novembre 2026, avec un compte Microsoft dont l'adresse e-mail
avait déjà servi à se connecter avec Google. Firebase ne l'a pas laissée passer, et a répondu : « An account already exists with the
same email address but different sign-in credentials ».

Il y a deux mauvaises fins. Dans l'une, l'application crée un second utilisateur, et les commandes d'Ana se retrouvent
réparties entre deux comptes qui s'ignorent. Dans l'autre, l'application réunit les deux comptes de son côté, sans rien
demander à personne. La leçon 05 s'est arrêtée à lui dire « connectez-vous avec l'autre bouton ». Celle-ci écrit la
bonne fin : elle se connecte avec celui qu'elle avait déjà, l'application lui pose la question, et si elle accepte, les
deux boutons mènent au même endroit.

---

## Avant de commencer

**Ce dont vous avez besoin :** la [05](05-entrar-con-microsoft.md) terminée (son `verifica` à 44 sur 44). Les étapes 1 à
4 ne demandent aucun compte Microsoft. L'étape 5, si : votre enregistrement dans Entra de la 05, en état de marche.

**Le projet de départ.** Depuis la racine du dépôt de la série :

```bash
git pull
cp lecciones/03-la-app-conoce-a-firebase/inicio/app/google-services.json lecciones/06-dos-botones-una-cuenta/inicio/app/
cd lecciones/06-dos-botones-una-cuenta/inicio
```

Il part de la solution de la 05, avec l'échafaudage de cette leçon par-dessus. `./verifica solucion` sort au rouge dans
un clone propre, parce que sans votre fichier le projet ne compile pas. Vos réponses vont dans `respuestas/06.md`.

**Règle toujours en vigueur depuis la 05.** Ne collez ni ne versez jamais dans le dépôt la valeur de la clé d'Entra, un
jeton d'accès ou d'identité, ni des captures ou des journaux qui en contiennent. Le résultat d'une liaison contient lui
aussi un jeton d'accès : cette application ne le lit pas. `google-services.json` reste ignoré parce qu'il est de votre
projet : ne l'utilisez pas comme preuve. Si une clé ou un jeton a touché un fichier, le terminal ou une capture, on le
révoque et l'on en crée un autre.

**Ce que cette leçon ne fait pas.** Elle ne défait pas une liaison, ne conserve ni ne ferme la session (leçon 07) et ne
distingue pas la première fois (leçon 08).

---

## Étape 1 · Dans la console : la règle qui fait exister la collision

Le choc de cette leçon —`ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL` en se connectant avec le second fournisseur—
n'apparaît qu'avec une option de votre projet Firebase : « un compte par adresse e-mail » (*One account per email
address*). La référence de Firebase dit que cette erreur n'est lancée qu'avec elle activée, et la recommande. Avec
plusieurs comptes par adresse e-mail, Firebase peut créer un second utilisateur : la première mauvaise fin de la scène,
et rien à résoudre. La même exception a d'autres codes, par exemple en liant un compte qui appartient déjà à un autre
utilisateur : ils ne sont pas le cas de cette leçon, et l'application les affiche comme une panne.

Cherchez-la dans la configuration d'Authentication de votre console et vérifiez qu'elle est active. Notez dans
`respuestas/06.md` cette ligne, sans adresses e-mail ni clés :

```text
una cuenta por correo: activa
```

*(La ligne dit « un compte par adresse e-mail : actif ». Elle reste en espagnol parce que c'est ce que cherche le
`verifica`.)*

Le `verifica` ne voit pas votre console : il ne cherche que cette ligne. Le chemin exact de cette option dans la
console, nous ne l'avons pas parcouru avec un compte ; si vous ne la trouvez pas, dites-nous où elle était.

**Ce qui le prouve :** votre vérification, à la main. Elle fait partie de la preuve du 6.1.

---

## Étape 2 · Ce que le projet apporte déjà

**L'ordre, c'est Firebase qui le fixe.** Son guide pour lier des comptes demande que la personne se connecte
**d'abord** avec n'importe quel fournisseur et que **ensuite**, à cet utilisateur qui est déjà entré, on ajoute l'autre.
Ainsi, dit-il, la personne a le même identifiant d'utilisateur quel que soit le fournisseur avec lequel elle entre. Pour
Microsoft, le guide de ce fournisseur nomme l'appel : `startActivityForLinkWithProvider`, sur l'utilisateur qui est déjà
entré.

Trois fichiers sont nouveaux et quatre ont changé par rapport à la solution de la 05. Ouvrez-les ; ne les modifiez pas :

| Fichier | Ce qu'il contient |
|---|---|
| `Enlace.kt` (nouveau) | `ResultadoDeEnlace` (lié, sans session, annulé, panne), `EnlaceRetomado` et l'interface `Enlace`, avec `enlazar()` et `retomar()` |
| `PuertaDelEnlace.kt` (nouveau) | La porte `EnlazadorDeProveedor`. `enlazar` fait l'appel de Firebase ; si personne n'est entré, elle lance `NadieHaEntrado` sans rien ouvrir. `pendiente` récupère une liaison restée à moitié faite |
| `PantallaEnlace.kt` (nouveau) | La confirmation : « ¿También con Microsoft? » (Aussi avec Microsoft ?), avec « Enlazar Microsoft » (Lier Microsoft) et « Ahora no » (Pas maintenant) |
| `Tienda.kt` | Il retient s'il y a eu une collision. Quand cette personne se connecte ensuite avec le bon bouton, il affiche la confirmation à la place de l'accueil. Seul « Enlazar Microsoft » appelle votre code |
| `PuertaDelNavegador.kt` | Une ligne : elle ne prend pas ce qui est en attente si ce qui est resté à moitié fait était une liaison |
| `MainActivity.kt`, `Armado.kt` | Ils assemblent aussi la liaison. Pour l'instant, `Armado.kt` renvoie `EnlaceSinConectar()` |

Le reste est identique à la solution de la 05, et le `verifica` le compare fichier par fichier. C'est une contrainte de
cette activité, pas de l'objectif : un changement dans un fichier fourni peut être du code valide et sortir pourtant au
rouge, parce que la leçon les fixe pour pouvoir dire ce qu'elle mesure.

**Une liaison à moitié faite n'est pas une connexion à moitié faite.** La référence de Firebase dit que, si l'écran
meurt pendant une liaison, son dénouement se récupère avec `getPendingAuthResult` : le même endroit par où arrive une
connexion restée à moitié faite. Ce qui suit est une décision de cette application, pas une citation : ce résultat ne
contient pas d'étiquette que l'on utilise ici pour savoir si c'était une liaison ou une connexion. Lue toujours comme une
connexion, une liaison annulée apparaîtrait comme « vous n'avez pas pu entrer ». C'est pourquoi la porte garde une note
sur le téléphone avant d'ouvrir le navigateur pour lier : « il y a une liaison en cours ». C'est un oui ou un non ; elle
ne conserve ni comptes, ni jetons, ni clés. À l'ouverture de l'application, la porte de connexion ne prend pas ce qui est
en attente si cette note est posée, et `Tienda` interroge d'abord votre liaison.

**Mesurez le point de départ.** Depuis la racine du dépôt :

```bash
./lecciones/06-dos-botones-una-cuenta/verifica
```

Le 18 nov. 2026, avec un fichier inventé, la dernière ligne a été
`Resultado: 46 de 66 pruebas en verde (20 en rojo, 0 con error, 0 saltadas)`. Il y en a 66 : les 44 dont vous héritez de
la 05, déjà au vert, et 22 de la liaison. Parmi celles-ci, deux passent déjà avec l'échafaudage et vingt attendent votre
code.

*(Résultat : 46 tests sur 66 au vert, 20 au rouge, 0 avec erreur, 0 sauté.)*

---

## Étape 3 · Avec Claude Code : lier et reprendre

Il entre comme toujours : **vous prédisez → vous demandez → vous lisez ce qu'il a généré → vous lancez le `verifica` →
vous l'expliquez.**

1. **Prédisez**, dans `respuestas/06.md` : au moment de la collision, avant que la personne se connecte avec Google, à
   quel utilisateur pourrait-on lier Microsoft ?
2. **Demandez** à Claude Code, depuis `inicio/` : « Dans `EnlaceConMicrosoft.kt`, prends `EntradaConMicrosoft.kt` comme
   référence. `enlazar()` monte le même fournisseur, appelle `enlazador.enlazar` et renvoie
   `ResultadoDeEnlace.Enlazado`. `retomar()` appelle `enlazador.pendiente()` : s'il renvoie `null`, renvoie `null` ;
   sinon, renvoie un `EnlaceRetomado` avec son nom et avec `Enlazado` si sa `falla` est `null`, ou avec la panne
   traduite. Les pannes se traduisent dans une fonction à part : `NadieHaEntrado` est `SinSesion` ; une
   `FirebaseAuthWebException` avec `ERROR_WEB_CONTEXT_CANCELED`, `Cancelado` ; toute autre, `Fallo`.
   `CancellationException` se laisse passer. Dans `Armado.kt`, change seulement le corps de `armarEnlaceConMicrosoft` et
   supprime-y `EnlaceSinConectar`. Ne modifie aucun autre fichier. »
3. **Lisez.** Le cœur doit ressembler à ceci :

```kotlin
override suspend fun enlazar(): ResultadoDeEnlace =
    try {
        val proveedor = OAuthProvider.newBuilder("microsoft.com")
            .addCustomParameter("prompt", "select_account")
            .build()
        enlazador.enlazar(proveedor)
        ResultadoDeEnlace.Enlazado
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        resultadoDe(e)
    }

override suspend fun retomar(): EnlaceRetomado? {
    val pendiente = enlazador.pendiente()
    if (pendiente == null) return null
    val resultado = if (pendiente.falla == null) ResultadoDeEnlace.Enlazado else resultadoDe(pendiente.falla)
    return EnlaceRetomado(pendiente.nombre, resultado)
}

private fun resultadoDe(falla: Exception): ResultadoDeEnlace = when {
    falla is NadieHaEntrado -> ResultadoDeEnlace.SinSesion
    falla is FirebaseAuthWebException && falla.errorCode == "ERROR_WEB_CONTEXT_CANCELED" ->
        ResultadoDeEnlace.Cancelado
    else -> ResultadoDeEnlace.Fallo
}
```

   Vérifiez trois choses : que `enlazar()` appelle la porte de la liaison et ne se reconnecte pas ; que `retomar()` ne
   réponde pas toujours « lié » ; et que personne n'ait touché les tests, les portes ni les écrans.

4. **Lancez le `verifica`.** Les 66 tests peuvent être au vert. Le rendu n'est pas encore complet : il manque les coches
   « Escrito » (Écrit) de `respuestas/06.md`, et le niveau réel de l'étape 5. Le `verifica` le dit à la fin, dans
   « Estado del objetivo 6.1 » (État de l'objectif 6.1).
5. **Expliquez**, en trois lignes : pourquoi la liaison se fait sur l'utilisateur qui est déjà entré, et non dans la
   collision.

**Ce qui le prouve, et jusqu'où.** Le `verifica` utilise un projet factice (`ProyectoFalso.kt`) avec deux personnes :
Ana, avec la même adresse e-mail chez Google et chez Microsoft, et Luis, avec une autre adresse. Il prouve qu'avant de
lier, Ana se heurte à la collision et qu'aucun second utilisateur n'est créé ; qu'après la liaison, ses deux accès mènent
au même utilisateur ; et que Luis reste un autre. Il prouve la confirmation, avec vos classes dessinées dans `Tienda` :
dans la collision on ne propose pas de lier, après la connexion on pose la question sans rien lier, et « Ahora no » ne
lie pas. Et il prouve qu'une liaison à moitié faite se reprend comme une liaison : l'application s'ouvre sur la
confirmation, avec « Listo » (Prêt) ou avec son message.

Les sabotages ont été mesurés sur la solution le 18 nov. 2026 : ne pas reconnaître `NadieHaEntrado`, 63 sur 66 ;
`retomar()` qui renvoie toujours `null`, 60 ; une qui répond toujours « lié », 63 ; lire la liaison à moitié faite comme
une connexion, 64.

Trois limites. « Le même utilisateur » est celui du projet factice, qui suit « un compte par adresse e-mail » écrit à la
main : cela ne prouve **pas** que Firebase lie vos comptes. « Resté à moitié fait » a été dit par une doublure : aucun
test n'a fermé l'application au milieu d'une liaison, et la note du téléphone n'a été exercée que sans rien en attente.
Et que la porte de connexion respecte cette note, aucun test ne le voit (sans cette ligne, les 66 donnent du vert) : ce
qui l'appuie, c'est que le fichier est identique dans `inicio/` et dans `solucion/`.

---

## Étape 4 · Sans IA : et si l'application liait toute seule ?

C'est l'exercice 6.2 et il se fait **sans Claude Code**. La confirmation est un critère de cette série : lier donne à un
autre compte l'accès à cet utilisateur, et c'est la personne qui vient de s'authentifier avec le fournisseur du compte
qui existe déjà qui en décide. Ici, vous le vérifiez en la retirant.

**1. Prédisez**, dans `respuestas/06.md` : si l'application liait dès que la personne entre, sans demander, que
verrait Ana ? Et que pourrait-il se passer si le compte Microsoft qui est entré en collision n'était pas le sien ?

**2. Cassez-le et mesurez.** Dans `Tienda.kt`, à l'intérieur de `recibe`, changez cette ligne :

```kotlin
pantalla = if (huboColision && enlace != null) Pantalla.CONFIRMAR_ENLACE else Pantalla.INICIO
```

par ces deux-ci :

```kotlin
if (huboColision && enlace != null) alcance.launch { enlace.enlazar() }
pantalla = Pantalla.INICIO
```

Lancez le `verifica`. Voici ce que cela a donné le 18 nov. 2026 (abrégé) :

```text
❌ Restricción de esta actividad (no del objetivo): estos archivos vienen dados y ya no son iguales … Tienda.kt
❌ La confirmación: 2 de 4 pruebas en verde. Nada se enlaza sin el toque de la persona, y solo se le ofrece …
❌ Al tocar «Enlazar Microsoft» no se enlaza, o después Microsoft no llega al mismo usuario (paso 3)
Resultado: 63 de 66 pruebas en verde (3 en rojo, 0 con error, 0 saltadas), leído de …
```

*(En français, les coches disent : contrainte de cette activité (pas de l'objectif) : ces fichiers sont fournis et ne
sont plus égaux … `Tienda.kt` ; la confirmation : 2 tests sur 4 au vert, rien ne se lie sans le toucher de la personne,
et on ne lui propose que … ; en touchant « Enlazar Microsoft », rien ne se lie, ou ensuite Microsoft n'arrive pas au même
utilisateur (étape 3). Résultat : 63 tests sur 66 au vert, 3 au rouge.)*

Copiez dans `respuestas/06.md` la ligne qui dit « La confirmación » (La confirmation).

**3. Réparez-le.** Remettez la ligne d'origine. Lancez le `verifica` : 66 sur 66, et `Tienda.kt` de nouveau identique à
celui de la leçon.

**4. Expliquez**, sans relire : pourquoi l'application demande une confirmation avant de lier, et ce qui se passerait si
elle liait toute seule. Comparez avec « Comparez vos réponses ».

**Ce qui le prouve :** votre explication (objectif 6.2). Le `verifica` ne cherche que la ligne collée : il ne sait pas si
vous l'avez cassé ni ne note ce que vous avez écrit. Proposer la liaison avant d'entrer, dans la collision même, a été
mesuré à part : 62 sur 66.

---

## Étape 5 · Sur le téléphone : l'essai manuel

C'est la preuve réelle du 6.1, et **elle ne peut pas encore se faire en entier avec ces instructions**. Ce qui suit dit
ce qu'il faut observer et ce qu'il reste à mesurer. Elle demande le niveau réel de la 05, avec votre enregistrement dans
Entra en état de marche, et « un compte par adresse e-mail » actif (étape 1).

**Le parcours.** Avec un compte Microsoft dont l'adresse e-mail est déjà entrée avec Google : touchez « Entrar con
Microsoft » (Entrer avec Microsoft) et lisez le message ; connectez-vous avec Google ; acceptez « Enlazar Microsoft ».
Selon le guide de Firebase, après une liaison la personne peut se connecter avec n'importe lequel des fournisseurs liés
et arriver aux mêmes données.

**Ce qui manque : sortir.** Pour vérifier le bouton de Microsoft, il faut se reconnecter à partir de zéro, et cette
application n'a pas encore de moyen de fermer la session : c'est la leçon 07. La proposition pour l'essai, sans anticiper
de code, est d'effacer les données de l'application sur le téléphone :

```bash
adb shell pm clear mx.habil.ejemplo.tienda
```

La documentation d'`adb` dit que cette commande efface toutes les données associées à une application. Avec un compte
réel, nous n'avons pas encore vérifié trois choses : que l'application se retrouve sans session, que l'utilisateur et ses
fournisseurs restent dans Firebase, et qu'en touchant « Entrar con Microsoft » on arrive au même utilisateur. Nous
n'avons pas non plus mesuré où la console montre les deux fournisseurs, ni fermé le processus au milieu d'une liaison
avec `adb shell am kill` (qui ne ferme pas la session : c'est un autre essai). Si vous l'essayez, dites-nous ce que vous
avez vu.

**Votre registre.** Quand vous l'aurez faite, notez dans `respuestas/06.md`, sans adresses e-mail ni jetons :

| Ce que vous notez | Exemple |
|---|---|
| La date, sur une ligne qui commence ainsi | `prueba manual: 2026-11-30` |
| Avant de lier | un utilisateur, avec Google |
| Après avoir lié | le même utilisateur, avec Google et Microsoft |

Le `verifica` ne lit que la première ligne, et même avec elle il dit qu'il n'a pas vu l'essai.

Lors de l'essai du 13 nov. 2026, avec un code de test qui avait bel et bien un moyen de sortir, on a vu le chemin court :
une fois entré avec Google, on a lié Microsoft, le même utilisateur s'est retrouvé avec les deux fournisseurs, et en
sortant puis en se connectant avec Microsoft, on est arrivé à lui.

**Ce qui le prouve :** c'est la seule chose qui prouve que Firebase a lié vos comptes. Un 66 sur 66 ne la remplace pas.

---

## Vos exercices

L'étape 4 se fait **sans IA**.

| # | Vous faites | C'est fait quand |
|---|---|---|
| 6.1 | Vous résolvez la collision : la personne entre avec le fournisseur qu'elle avait déjà et lie l'autre, avec confirmation | Trois preuves, et il faut les trois. La ligne « una cuenta por correo: activa » dans `respuestas/06.md`, après l'avoir vérifiée dans votre console. Simulé : « Estado del objetivo 6.1 » dit niveau simulé complet, avec un projet factice de deux personnes ; il valide l'ordre, la confirmation, la liaison à moitié faite et que les deux accès mènent au même utilisateur **factice**. Réel : l'essai manuel de l'étape 5, avec votre enregistrement noté ; c'est la seule chose qui valide que Firebase a lié vos comptes, et nous ne l'avons pas encore exécuté avec un compte réel |
| 6.2 | Vous expliquez pourquoi l'application demande une confirmation avant de lier et ce qui se passerait si elle liait toute seule | Votre prédiction, le rouge collé et votre explication sont dans `respuestas/06.md`, et ils coïncident avec « Comparez vos réponses ». L'explication, vous la comparez vous-même |

**Durée :** comptez une heure et demie sans l'essai manuel, et une demi-heure de plus avec. C'est une estimation : nous
l'ajusterons avec le temps que prendra la première cohorte.

---

## Ça ne tourne pas ?

| Vous voyez | Cause probable | Ce que vous faites |
|---|---|---|
| `File google-services.json is missing.` | Vous n'avez pas copié votre fichier dans `inicio/app/` | Le `cp` de « Avant de commencer » |
| ❌ « Restricción de esta actividad… lo heredado ya no es idéntico a la solución de la lección 05 » (contrainte de cette activité… ce qui est hérité n'est plus identique à la solution de la leçon 05) | Quelque chose d'hérité a changé | `git diff -- <fichier>` et défaites-le à la main |
| ❌ « Restricción de esta actividad… ya no son iguales en inicio/ y en solucion/ » (… ne sont plus égaux dans inicio/ et dans solucion/) avec `Tienda.kt` | Vous venez de l'étape 4 | Remettez la ligne d'origine |
| ❌ « Enlazar sin haber entrado primero » (lier sans être entré d'abord) | Votre `resultadoDe` ne reconnaît pas `NadieHaEntrado` | Étape 3 |
| ❌ « Retomar el enlace » (reprendre la liaison) | `retomar()` n'utilise pas ce que renvoie `enlazador.pendiente()`, ou répond toujours la même chose | Étape 3 |
| Vous êtes entré avec Microsoft avec une adresse e-mail déjà entrée avec Google, et le message de la collision n'est pas sorti | Votre projet permet plusieurs comptes par adresse e-mail : Firebase a créé un autre utilisateur | Étape 1. Ne liez pas deux utilisateurs créés séparément comme si c'était le cas de la leçon |
| « Primero entra con el botón que ya usabas… » (D'abord, connectez-vous avec le bouton que vous utilisiez déjà…) | On a demandé de lier sans que personne soit entré | Connectez-vous d'abord ; la liaison se fait sur un utilisateur qui est déjà entré |
| ❌ « EnlaceConMicrosoft.kt nombra una puerta concreta » (`EnlaceConMicrosoft.kt` nomme une porte concrète) | Votre code nomme la vraie porte (`EnlazadorConFirebase`) ou une doublure des tests. Les tests ne lui passent que des doublures : si votre code demande laquelle lui est échue, le vert ne dit pas ce qu'il fait avec la vraie | Que votre code ne parle qu'à `EnlazadorDeProveedor`. La vraie porte, c'est `MainActivity.kt` qui la choisit, et il est fourni (étape 3) |

Sur Linux Mint 22.3 (x86_64), le `verifica` de cette leçon a donné le même résultat que sur le Mac (18 nov. 2026), au
niveau simulé et avec un fichier de test qui contenait l'empreinte de cet ordinateur : sur `inicio/`, 46 sur 66 au vert ;
sur `solucion/`, 66 sur 66.

---

## Pour conclure

**Répondez sans relire :**

1. Quelle option de votre projet fait apparaître le choc de cette leçon, et que se passe-t-il sans elle ?
2. Pourquoi se connecte-t-on d'abord et lie-t-on ensuite ?
3. Votre `verifica` donne 66 sur 66 et « niveau simulé : complet ». Dites une chose que cela prouve et une qu'il ne
   prouve pas.
4. Pourquoi une liaison à moitié faite ne peut-elle pas se lire comme une connexion à moitié faite ?

**La suite :** dans la L07, la session reste : l'application se souvient de vous à votre retour, et vous apprenez à la
fermer.

---

## Comparez vos réponses

**Étape 3.** À aucun : dans la collision, personne n'est entré, donc il n'y a pas d'utilisateur auquel ajouter quoi que
ce soit. C'est pourquoi l'ordre est de se connecter d'abord et de lier ensuite, et c'est pourquoi la porte lance
`NadieHaEntrado`.

**Étape 4.** Ana ne verrait rien : elle arriverait à l'accueil et, par derrière, le navigateur s'ouvrirait pour lier sans
qu'elle l'ait demandé. Et si le compte Microsoft qui est entré en collision était celui d'une autre personne ayant accès
à cette adresse e-mail, ce compte aurait accès à l'utilisateur d'Ana sans qu'elle l'ait décidé. La confirmation place la
décision chez la personne qui vient de s'authentifier avec le fournisseur du compte qui existe déjà. Cela prouve qu'elle
est entrée avec ce fournisseur ; cela ne prouve rien de plus sur qui elle est.

**Pour conclure :**

1. « Un compte par adresse e-mail ». Sans elle, Firebase peut créer un second utilisateur avec la même adresse e-mail, et
   le choc de cette leçon n'apparaît pas.
2. Parce que lier, c'est ajouter un fournisseur à un utilisateur qui est déjà entré : avant d'entrer, il n'y a personne à
   qui l'ajouter.
3. Il prouve ce que demande votre liaison et ce qu'elle répond, que rien ne se lie sans le toucher de la personne et que,
   dans un projet factice, les deux accès mènent au même utilisateur. Il ne prouve pas que Firebase lie vos comptes.
4. Parce que Firebase les remet par le même endroit et ne dit pas lequel c'était. Lue comme une connexion, une liaison
   annulée apparaîtrait comme « vous n'avez pas pu entrer ».

---

## Sources de la leçon

Ouvertes le 18 nov. 2026.

- Firebase. *Link Multiple Auth Providers to an Account on Android* (on se connecte d'abord et l'on lie ensuite ; le même identifiant avec n'importe quel fournisseur ; après la liaison, on peut se connecter avec n'importe lequel. Son exemple utilise `linkWithCredential` ; pour Microsoft, le guide de ce fournisseur indique `startActivityForLinkWithProvider`). https://firebase.google.com/docs/auth/android/account-linking
- Firebase. *Authenticate Using Microsoft on Android* (lier Microsoft à un utilisateur qui est déjà entré). https://firebase.google.com/docs/auth/android/microsoft-oauth
- Firebase. *Authenticate Using Microsoft with JavaScript* (le cas Microsoft et Google avec « un compte par adresse e-mail » : se connecter avec le fournisseur qui existe déjà et lier l'autre). https://firebase.google.com/docs/auth/web/microsoft-oauth
- Firebase. Référence de `FirebaseUser` (`startActivityForLinkWithProvider` et son résultat en attente). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseUser
- Android Developers. *Android Debug Bridge* (`pm clear` : efface toutes les données associées à une application ; `am kill`). https://developer.android.com/tools/adb
- Firebase. Référence de `FirebaseAuth` (`getPendingAuthResult`). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth
- Firebase. Référence de `FirebaseAuthUserCollisionException` (`ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL` seulement avec « un compte par adresse e-mail », qu'elle recommande). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuthUserCollisionException
- Essai de la série, 13 nov. 2026, Mac avec Apple silicon, avec un code de test : la collision avec un compte réel, et la liaison après s'être connecté avec Google.
- Nos propres mesures : Mac avec Apple silicon, 18 nov. 2026. Le `verifica` sur `inicio/` et `solucion/`, chaque étape et chaque sabotage, avec des fichiers inventés et sans toucher à la console de Firebase, à celle d'Entra ni à aucun compte.
