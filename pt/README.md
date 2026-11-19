# A loja no bolso

**Por Dorian Chávez, fundador da Hábil e arquiteto de integração.**

**Página do curso:** [habil.mx/pt/cursos/tienda-app-nativa/](https://www.habil.mx/pt/cursos/tienda-app-nativa/), com o programa e as lições em cinco idiomas.

**Para quem é:** quem já programa em alguma linguagem, usa o terminal e o git, e já fez pelo menos uma interface, web ou
de desktop. Não é preciso saber Kotlin, Android nem OAuth: aprendem-se aqui.

**O que se vai saber ao terminar:** construir com as próprias mãos um app Android nativo, em Kotlin e Jetpack Compose,
em que se entra com uma conta do Google ou da Microsoft; o app dá as boas-vindas na primeira vez e reconhece quem volta. E
explicar o que o Firebase fez e o que foi feito à mão.

**O que é preciso antes de começar:** Linux em um processador x86_64 (testado no Linux Mint 22.3), ou um Mac, com 16 GB de RAM para o
emulador (com menos, usa-se um telefone Android). O Windows não é coberto. A parte de iOS é somente no Mac.

## O projeto

A mesma loja inventada de [«Ni una orden perdida»](https://www.habil.mx/pt/cursos/ni-una-orden-perdida/) agora tem
app. Neste primeiro trecho chega-se até o login: o cliente entra com o Google ou com a Microsoft e o app o
reconhece. Nos seguintes, o app consome a API da loja e passa para o iOS.

*Uma nota sobre os idiomas:* o projeto do curso traz os textos de tela em espanhol, e os arquivos de `lecciones/` não são
traduzidos. Nos blocos de código, nas saídas de terminal e nos esboços de tela, os textos ficam como o projeto os tem; a prosa
de cada lição os traz em português na primeira vez em que aparecem.

## Como se trabalha com IA

Em todas as lições do mesmo jeito: **prever** o que vai acontecer → pedir a mudança ao Claude Code → **ler** o que ele
gerou → rodar o `verifica` → **explicar** com palavras próprias o que ele faz. Se não for possível explicar, não se
avança. E em cada lição há pelo menos um exercício feito sem IA.

Cada lição traz um projeto `inicio/` e um `verifica` que diz, em vermelho ou em verde, se o objetivo já foi alcançado. As
respostas escritas ficam em `respuestas/`. Cada pessoa usa o próprio projeto do Firebase: o `google-services.json` nunca
é enviado ao repositório.

## As lições

Fazem-se em ordem: cada uma parte do que a anterior deixou. Comece pela [00](00-ambiente-listo.md), que deixa
o ambiente pronto.

### Trecho 1 — Chegar ao login (Android)

| # | Lição |
|---|---|
| 00 | [Ambiente pronto](00-ambiente-listo.md) |
| 01 | [Minha primeira tela roda](01-mi-primera-pantalla.md) |
| 02 | [Telas que mudam](02-pantallas-que-cambian.md) |
| 02-B | [Catálogo de componentes da loja](02b-catalogo-de-componentes.md) (prática) |
| 03 | [O app conhece o Firebase](03-la-app-conoce-a-firebase.md) |
| 04 | [Entrar com o Google](04-entrar-con-google.md) |
| 05 | [Entrar com a Microsoft](05-entrar-con-microsoft.md) |
| 06 | [Dois botões, uma conta](06-dos-botones-una-cuenta.md) |
| 07 | [A sessão permanece](07-la-sesion-se-queda.md) |
| 08 | [A primeira vez](08-la-primera-vez.md) |
