# Votre boutique dans la poche

**Par Dorian Chávez, fondateur de Hábil et architecte d'intégration.**

**À qui il s'adresse :** à toute personne qui programme déjà dans un langage, utilise le terminal et git, et a réalisé
au moins une interface, web ou de bureau. Kotlin, Android et OAuth ne sont pas supposés connus : vous les apprenez ici.

**Ce que vous saurez faire à la fin :** construire de vos propres mains une application Android native, en Kotlin et
Jetpack Compose, où vous vous connectez avec votre compte Google ou Microsoft ; l'application vous accueille la première
fois et vous reconnaît quand vous revenez. Et expliquer ce que Firebase a fait pour vous et ce que vous avez fait vous-même.

**Ce dont vous avez besoin avant de commencer :** Linux sur un processeur x86_64 (testé sur Linux Mint 22.3), ou un Mac,
avec 16 Go de RAM pour l'émulateur (avec moins, vous utilisez votre téléphone Android). Windows n'est pas couvert. La
partie iOS se fait uniquement sur Mac.

## Le projet

La même boutique inventée de [« Ni una orden perdida »](https://www.habil.mx/fr/cours/ni-una-orden-perdida/) a maintenant
une application. Dans ce premier tronçon, vous arrivez jusqu'à la connexion : le client se connecte avec Google ou avec
Microsoft et l'application le reconnaît. Dans les suivants, l'application consomme l'API de la boutique et passe à iOS.

*Une note sur les langues :* le projet du cours livre ses textes d'écran en espagnol, et les fichiers de `lecciones/` ne
sont pas traduits. Dans les blocs de code, les sorties de terminal et les maquettes d'écran, les textes restent tels que
le projet les contient ; dans la prose de chaque leçon, ils sont donnés en français la première fois qu'ils apparaissent.

## Comment nous travaillons avec l'IA

Dans toutes les leçons, de la même manière : vous **prédisez** ce qui va se passer → vous demandez le changement à
Claude Code → vous **lisez** ce qu'il a généré → vous lancez le `verifica` → vous **expliquez** avec vos propres mots ce
qu'il fait. Si vous ne savez pas l'expliquer, vous n'avancez pas. Et chaque leçon contient au moins un exercice qui se
fait sans IA.

Chaque leçon apporte un projet `inicio/` et un `verifica` qui vous dit, en rouge ou en vert, si vous avez réussi. Vos
réponses écrites vont dans `respuestas/`. Chacun utilise son propre projet Firebase : `google-services.json` n'est
jamais versé dans le dépôt.

## Les leçons

Elles se font dans l'ordre : chacune part de ce que la précédente a laissé. Commencez par la [00](00-ambiente-listo.md),
qui prépare votre environnement.

### Tronçon 1 — Arriver à la connexion (Android)

| # | Leçon |
|---|---|
| 00 | [Environnement prêt](00-ambiente-listo.md) |
| 01 | [Mon premier écran tourne](01-mi-primera-pantalla.md) |
| 02 | [Des écrans qui changent](02-pantallas-que-cambian.md) |
| 02-B | Catalogue des composants de la boutique (pratique) |
| 03 | [L'application rencontre Firebase](03-la-app-conoce-a-firebase.md) |
| 04 | [Se connecter avec Google](04-entrar-con-google.md) |
| 05 | [Se connecter avec Microsoft](05-entrar-con-microsoft.md) |
| 06 | [Deux boutons, un seul compte](06-dos-botones-una-cuenta.md) |
| 07 | La session reste ouverte |
| 08 | La première fois |

Les leçons sans lien sont en préparation et sont publiées dans l'ordre.
