# Lição 08 — A primeira vez

**Antes de ler, três palavras.** Uma linha cada uma.

- **Usuário novo:** o que o Firebase acaba de criar no projeto, nesta mesma entrada.
- **Resultado do início de sessão:** o que o Firebase responde quando alguém entra. No código se chama
  `AuthResult`.
- **`isNewUser`:** a pergunta que esse resultado sabe responder: este usuário é novo ou já existia?

**O que fica com quem termina:** uma loja que dá as boas-vindas a quem entra pela primeira vez, com o Google ou com a
Microsoft, e que não as dá de novo quando a pessoa volta. E uma conta honesta do que o Firebase fez ao longo de
todo o trecho, do que seria preciso construir sem ele e do que ainda não existe.

**Ideia nova desta lição:** distinguir o usuário novo do que volta. O resto —as portas que agora
respondem também se é a primeira vez, a linha de boas-vindas no início e o estado que a lembra— já vem
pronto e explicado, e não se avalia aqui.

**Uma nota sobre os textos do app.** Como nas lições anteriores, o projeto do curso traz os textos de tela em espanhol,
e os arquivos de `lecciones/` não são traduzidos: as verificações automáticas do curso (o `verifica`) procuram esses
textos exatos. Por isso, nos blocos de código, nas saídas de terminal e nos esboços de tela, os textos ficam como o projeto
os tem; na prosa, a lição os cita em português e, na primeira vez, traz o original entre parênteses.

---

## A cena

13 de novembro de 2026, no ensaio desta série. Uma conta real do Google entra pela primeira vez no app de
teste. A tela diz: «Olá, … (primeira vez)» («Hola, … (primera vez)»).

Alguém sai e volta a entrar com a mesma conta. Agora diz: «Olá, …» («Hola, …»). Sem o parêntese.

Na mesma noite acontece o mesmo com a Microsoft: a primeira entrada traz «(primeira vez)»; a segunda, não.

E aqui está o estranho. Esse app não tinha banco de dados. Não guardava uma lista de quem já tinha entrado. Não escrevia
nada no telefone. Como soube que na segunda vez já não era a primeira?

O app não sabia sozinho. Quem lhe disse foi o Firebase, em uma única palavra que pode vir escondida na resposta do início
de sessão. O app da série a vem recebendo desde a lição 04, e a joga no lixo cada vez: dessa
resposta, as portas só tiram o nome. Esta lição a resgata. Como essa palavra pode não vir, o app
pergunta de forma segura: se faltar, não há boas-vindas.

---

## Antes de começar

**O que é preciso:** a [07](07-la-sesion-se-queda.md) concluída (o `verifica` dela em 82 de 82). Os passos 1 a 3,
5 e 6 não pedem nenhuma conta. O passo 4 é no telefone ou no emulador, com a conta do Google da 04 e, se
foi feita a parte opcional, o registro da Microsoft da 05.

**O projeto de início.** A partir da raiz do repositório da série:

```bash
git pull
cp lecciones/03-la-app-conoce-a-firebase/inicio/app/google-services.json lecciones/08-la-primera-vez/inicio/app/
cd lecciones/08-la-primera-vez/inicio
```

Parte-se da solução da 07, com o andaime desta lição por cima. `./verifica solucion` sai em vermelho em um
clone limpo, porque sem o arquivo de quem estuda o projeto não compila. As respostas vão em `respuestas/08.md`.

**Duas «boas-vindas» que não são as mesmas.** A tela `Bienvenida`, desde a lição 02, é a capa: a que vê
todo mundo que ainda não entrou, com o botão «Entrar». As boas-vindas desta lição são outra coisa: uma linha no
**início**, embaixo de «Olá, Ana» («Hola, Ana»), que só o usuário novo vê. O texto é «Es tu primera vez aquí: te
damos la bienvenida.» («É a primeira vez aqui: damos as boas-vindas.»)

**Regra que continua em vigor.** Nunca cole nem suba um token, nem capturas ou registros que o tragam. O
resultado de um início de sessão traz tokens; as portas deste app só tiram dele o nome e um sim ou um não.

**O que esta lição não faz.** Não cria um perfil da loja: não guarda endereço, pedidos nem preferências.
Não muda o que fazem as telas da 04 à 07: entrar, os dois botões, o vínculo, reabrir e encerrar
a sessão se comportam igual, e os 82 testes delas o vigiam.

---

## Passo 1 · Preveja: quando aparecem as boas-vindas?

É o primeiro passo de sempre: **prever** antes de pedir qualquer coisa ao Claude Code. Ainda não leia o passo 2.

Pense no app **concluído** desta lição e nestes quatro momentos, em ordem:

1. A Ana entra com o Google pela primeira vez na vida, na loja.
2. A Ana fecha o app pela lista de apps recentes e o abre de novo (como na 07).
3. A Ana toca em «Encerrar sessão» («Cerrar sesión») e volta a entrar com o Google.
4. O Luis entra com a Microsoft pela primeira vez, e o Android fecha o app enquanto ele está na página da Microsoft.
   Ao voltar, o app retoma a entrada (como na 05).

Escreva em `respuestas/08.md` uma linha que comece assim, com os números dos momentos em que as boas-vindas
devem aparecer e, em uma frase, por quê:

```text
predicción 8.1: …
```

*(Em português: previsão 8.1. A palavra fica em espanhol porque é isso que o `verifica` procura.)*

**Meça o ponto de partida.** A partir da raiz do repositório:

```bash
./lecciones/08-la-primera-vez/verifica
```

Em 19/11/2026, com um arquivo inventado, a última linha da contagem foi
`Resultado: 84 de 97 pruebas en verde (13 en rojo, 0 con error, 0 saltadas)`. São 97: as 82 herdadas da
07, já em verde, e 15 da primeira vez. Dessas, duas já passam com o andaime (as do «usuário que volta»:
hoje o app nunca diz que é a primeira vez, e nesse caso acerta) e treze esperam o código de quem estuda.

*(Em português: «Resultado: 84 de 97 testes em verde (13 em vermelho, 0 com erro, 0 pulados)».)*

**O que comprova:** a previsão, contra o que se vir no passo 3 (os testes) e no passo 4 (o telefone). O que
o `verifica` revisa das respostas está em «Os exercícios».

---

## Passo 2 · O que o projeto já traz

**Onde vive a resposta.** Quando alguém entra, o Firebase responde com um `AuthResult`. A referência dele diz que
é o resultado «de operações que podem afetar o estado de autenticação». Além do usuário, traz
`getAdditionalUserInfo()`: informação adicional. E a referência de `AdditionalUserInfo` diz que contém
informação do usuário «como resultado de um início de sessão, um vínculo ou uma reautenticação bem-sucedidos». A função
`isNewUser()` «devolve se o usuário é novo ou existente». Em Kotlin se lê como uma propriedade:
`resultado.additionalUserInfo?.isNewUser`.

**Dois detalhes da documentação que decidem como se escreve essa linha:**

- A referência marca `getAdditionalUserInfo()` como `@Nullable`: pode vir `null`. O Kotlin a traz como um tipo
  que admite `null`, e o operador `?.` existe para isso: se o que está à esquerda é `null`, o resultado é `null`
  em vez de estourar.
- A mesma referência diz que a função devolve informação «se o provedor é Facebook, GitHub, Google ou Twitter».
  A Microsoft não está nessa lista. Mas a página de `AdditionalUserInfo`, em `getProfile()`, sim nomeia a
  Microsoft, e no ensaio de 13/11 a entrada com a Microsoft disse «(primeira vez)» na primeira vez e não na
  segunda. A documentação não é consistente aqui, e por isso o código não supõe nada: se não há informação,
  não há boas-vindas.

**O que mudou no app.** Um arquivo é novo e cinco mudaram em relação à solução da 07; nos
testes chegam três classes novas e os dublês delas, `DoblesDePrimeraVez.kt`. Abra-os; edite só os três que o
passo 3 indica:

| Arquivo | O que traz |
|---|---|
| `PrimeraVez.kt` (novo) | A forma da função `esPrimeraVez(resultado: AuthResult)`, vazia. É escrita no passo 3 |
| `Entrada.kt` | `Resultado.Entro` traz, além do nome, `primeraVez`. Vale `false` se ninguém o disser: assim tudo o que vai da 04 à 07 continua igual |
| `Puertas.kt` | `QuienEntro`: o nome e um sim ou um não. E na porta do Google, uma função nova: `verificarConPrimeraVez()` |
| `PuertaDelNavegador.kt` | Na porta da Microsoft, duas novas: `abrirConPrimeraVez()` e `pendienteConPrimeraVez()` |
| `Tienda.kt` | Guarda se quem entrou é novo e o passa ao início. Ao reabrir com sessão e ao encerrar a sessão, o deixa em `false` |
| `Inicio.kt` | Com `primeraVez` em `true`, acrescenta a linha de boas-vindas. Com `false`, é o início da 07 |

**O truque das funções novas.** As três trazem um corpo padrão que responde «não é a primeira vez».
Por isso os dublês da 04 à 07, que não sabem nada disto, continuam compilando e os testes deles continuam em verde.
As portas de verdade substituem esse corpo: leem o `AuthResult` do Firebase e perguntam à `esPrimeraVez`
**de quem estuda**. Para fora da porta só sai `QuienEntro`: o nome e um `Boolean`. O resultado completo,
com os tokens, fica lá dentro.

O resto é idêntico à solução da 07, e o `verifica` o compara arquivo por arquivo. É uma restrição
desta atividade, não do objetivo: se um desses arquivos foi mudado e o app faz o que a lição pede, esse
vermelho não diz que o 8.1 não foi entendido. Devolva-o ao que vinha, ou peça a alguém que revise a mudança.

---

## Passo 3 · Com o Claude Code: decidir a primeira vez

Entra como sempre: **prever → pedir → ler o que ele gerou → rodar o `verifica` → explicar.**

1. **Preveja**, em `respuestas/08.md`, em uma linha livre: se `additionalUserInfo` vier `null` e se escrever
   `resultado.additionalUserInfo!!.isNewUser`, o que acontece com a pessoa que tenta entrar?
2. **Peça** ao Claude Code, parado em `inicio/`: «En `PrimeraVez.kt`, `esPrimeraVez` regresa `true` solo si
   `resultado.additionalUserInfo?.isNewUser` es `true`; con `null` regresa `false`. En `EntradaConGoogle.kt`
   cambia `verificador.verificar(token)` por `verificador.verificarConPrimeraVez(token)` y pasa su nombre y su
   `primeraVez` a `Resultado.Entro`. En `EntradaConMicrosoft.kt`, en `retomar()` y en `entrar()`, usa
   `navegador.pendienteConPrimeraVez()` y `navegador.abrirConPrimeraVez(proveedor)` en lugar de `pendiente()` y
   `abrir(proveedor)`, y pasa igual el nombre y la `primeraVez`. No cambies ningún otro archivo.»

   *(Em português: em `PrimeraVez.kt`, `esPrimeraVez` devolve `true` só se `resultado.additionalUserInfo?.isNewUser` for
   `true`; com `null` devolve `false`. Em `EntradaConGoogle.kt`, troque `verificador.verificar(token)` por
   `verificador.verificarConPrimeraVez(token)` e passe o nome e a `primeraVez` a `Resultado.Entro`. Em
   `EntradaConMicrosoft.kt`, em `retomar()` e em `entrar()`, use `navegador.pendienteConPrimeraVez()` e
   `navegador.abrirConPrimeraVez(proveedor)` em vez de `pendiente()` e `abrir(proveedor)`, e passe igualmente o nome e a
   `primeraVez`. Não mude nenhum outro arquivo.)*
3. **Leia.** O centro deve se parecer com isto:

```kotlin
// PrimeraVez.kt
fun esPrimeraVez(resultado: AuthResult): Boolean =
    resultado.additionalUserInfo?.isNewUser == true

// EntradaConGoogle.kt, no passo 4 de entrar()
val quien = verificador.verificarConPrimeraVez(token)
Resultado.Entro(quien.nombre, quien.primeraVez)

// EntradaConMicrosoft.kt, em entrar()
val quien = navegador.pendienteConPrimeraVez() ?: navegador.abrirConPrimeraVez(proveedor)
Resultado.Entro(quien.nombre, quien.primeraVez)
```

   Confira três coisas. Que diga `== true` e não `!!`: com `?.`, um `null` vira `null`, e `null == true` é
   `false`. Que em `EntradaConMicrosoft.kt` mudem **as duas** funções, `retomar()` e `entrar()`: se só
   muda uma, a outra continua jogando fora a primeira vez. E que ninguém tenha mexido nos testes, nas portas nem nas
   telas.

4. **Rode o `verifica`.** Os 97 testes podem ficar em verde. A entrega ainda não está completa: faltam as
   linhas do 8.1, do 8.2 e do 8.3 em `respuestas/08.md`, e o `verifica` diz isso no final.
5. **Explique**, em três linhas: por que a decisão vive dentro da porta e para as telas só sai um
   `Boolean`.

**O que comprova, e até onde.** Alguns testes dão à `esPrimeraVez` um `AuthResult` de mentira e olham o que ela
decide: com `isNewUser` em `true`, em `false` e sem informação adicional. Outros dão às duas entradas portas
que respondem «é novo» ou «já existia» e olham se o `Resultado` o traz, também ao retomar com a Microsoft. E os
do percurso desenham a loja com o projeto de mentira da 06: a Ana entra com o Google e vê as boas-vindas;
encerra a sessão, volta a entrar e já não as vê, e continua sendo o mesmo usuário. O mesmo com o Luis e a Microsoft. Ao
reabrir com sessão, sem boas-vindas. Ao retomar com a Microsoft um usuário novo, com boas-vindas.

As sabotagens foram medidas sobre a solução em 19/11/2026:

| Sabotagem | Resultado |
|---|---|
| `esPrimeraVez` com `!= false`: um `null` conta como novo | 96 de 97 |
| `esPrimeraVez` com `additionalUserInfo!!` | 96 de 97 |
| `esPrimeraVez` ao contrário (`isNewUser == false`) | 95 de 97 |
| `EntradaConGoogle` que não passa a `primeraVez` | 93 de 97 |
| `EntradaConMicrosoft` que não a passa em `entrar()` | 93 de 97 |
| `EntradaConMicrosoft` que não a passa em `retomar()` | 95 de 97 |
| `EntradaConGoogle` que sempre diz que é a primeira vez | 92 de 97, e três das vermelhas são herdadas de lições anteriores |
| `esPrimeraVez` que só lê `isNewUser` quando o resultado é o dublê dos testes | 96 de 97: cada caso se testa também com o resultado em outra classe |
| `EntradaConGoogle` que falha só quando a porta é `VerificadorConFirebase` | 97 de 97: os testes não o veem, porque só lhe passam dublês. Quem o pega é a marca da porta concreta |

Cada uma deixa ainda a marca dela em vermelho na saída, com o passo que a conserta.

Três limites. «É novo» nos testes é decidido por um dublê: o projeto de mentira diz «novo» quando essa entrada
criou um usuário, que é o que o Firebase promete, mas não é o Firebase. Nenhum teste vê um `AuthResult` de verdade.
E nenhum olha o que `isNewUser` responde ao vincular: este app não usa esse resultado para as boas-vindas (o passo 4
diz por quê).

Esta lição não tem nível instrumentado, como a 06 e a 07: as boas-vindas são uma linha de texto em uma tela
que já foi testada dentro do Android na 04, e o novo é uma decisão, que se testa no computador.

---

## Passo 4 · No telefone: a primeira vez de verdade

Instale e abra o app com o `google-services.json`, como na 04:

```bash
./gradlew installDebug
```

**O problema da conta de quem estuda.** A conta do Google já entrou no projeto na 04. Para o Firebase ela já não é nova,
então com ela **não** se verão as boas-vindas. É exatamente o que esta lição promete, e é a primeira
comprovação: entre com a conta e veja que diga «Olá, …» («Hola, …») sem a linha de boas-vindas.

Para vê-la, o Firebase precisa criar um usuário. Há dois caminhos:

- **Outra conta do Google** que nunca tenha entrado no projeto, se houver uma de testes.
- **Apagar o usuário** no console do Firebase, em Authentication › Users. É um projeto de testes, mas
  apagar um usuário não se desfaz, e com ele vai qualquer vínculo feito na 06.

Entre. As boas-vindas devem aparecer. Toque em «Encerrar sessão» («Cerrar sesión»), volte a entrar com a mesma conta, e já não devem aparecer.
Compare com a previsão.

O que já se viu no ensaio de 13/11/2026, com um código de teste: `isNewUser` valeu `true` na primeira vez e
`false` ao voltar, com o Google e com a Microsoft. O que **ainda não capturamos com uma conta real** é este
percurso com o código desta lição: a linha de boas-vindas no início e que ela não volte ao entrar de novo.
Se aparecer diferente, conte para nós.

**Dois casos que a documentação não fecha.** Foram procurados nas páginas do Firebase em 19/11/2026:

- **Ao vincular** (lição 06). `AdditionalUserInfo` diz que também chega depois de um vínculo, mas nenhuma
  página diz quanto vale `isNewUser` nesse caso: nem a referência, nem o guia de vincular contas, nem o da
  Microsoft. **Não está documentado.** Este app não pergunta ao vínculo: quem vincula já entrou antes com o outro
  botão, e essa entrada já decidiu se havia boas-vindas.
- **Ao retomar** (lição 05). `getPendingAuthResult()` entrega um `AuthResult`, o mesmo tipo de uma entrada
  normal, e o app o lê igual. Mas nenhuma página diz de forma explícita que ali `isNewUser` se comporte igual:
  **não está documentado**. No ensaio retomou-se uma entrada com uma conta que já existia, e não se testou com
  uma nova. Se for testado e se vir outra coisa, conte para nós.

**O que comprova:** o que for observado. Não há marca no `verifica` para este passo.

---

## Passo 5 · Sem IA: o que o Firebase fez

Este é o exercício 8.2, o de fechamento do trecho, e se faz **sem o Claude Code**. Também não volte a ler as
lições anteriores enquanto o responde: primeiro escreva o que lembrar; depois, se quiser, revise.

Oito lições, um app que deixa entrar com dois provedores, lembra a pessoa e sabe se é a primeira vez. Agora a
conta honesta. Escreva em `respuestas/08.md` três linhas que comecem assim:

```text
8.2 Firebase hizo: …
8.2 sin Firebase: …
8.2 todavía no hay: …
```

*(Em português: 8.2 o Firebase fez; 8.2 sem o Firebase; 8.2 ainda não há. As linhas ficam em espanhol porque é isso que o
`verifica` procura.)*

1. **O Firebase fez:** pelo menos três coisas que o Firebase fez no trecho. Pense no que acontece entre a
   escolha da conta e o momento em que o app diz «Olá».
2. **Sem o Firebase:** para cada uma, que peça seria preciso construir. Não basta «um servidor»: diga o que esse
   servidor teria que fazer.
3. **Ainda não há:** algo que uma loja de verdade precisa saber da pessoa e que o app, hoje, não tem onde
   guardar.

**O que comprova:** as três linhas, contra «Compare as respostas» (o que o `verifica` revisa, em «Os
exercícios»).

---

## Passo 6 · Julgar uma resposta do Claude Code

Este é o exercício 8.3. Imagine que, ao terminar o passo 3, pergunta-se ao Claude Code: «Quão seguro é o
início de sessão do app?». Uma resposta **como esta** é verossímil. Não é uma citação de uma conversa real:
está escrita para este exercício.

> Revisei o início de sessão do app. Três coisas:
>
> **A.** O app não valida o token de identidade do Google: entrega-o ao Firebase, e são os servidores do
> Firebase que o verificam antes de deixar alguém entrar.
>
> **B.** Com o Firebase, o início de sessão já é completamente seguro: ninguém poderá entrar na loja com uma
> conta que não seja a própria, então não é preciso revisar mais nada.
>
> **C.** O Firebase guarda o token de atualização criptografado no telefone, então, mesmo que alguém tire uma cópia
> dos dados do app, ela não serve.

Soa bem. As três frases estão ditas com a mesma segurança. O trabalho é separá-las.

**Agora é a vez de quem estuda.** Para cada afirmação, escreva em `respuestas/08.md` um de três juízos —`correcta`, `exagerada` ou
`sin comprobar`— no começo da linha e, depois, por quê. Por exemplo, `8.3-A: correcta porque …`:

```text
8.3-A: <juicio> porque …
8.3-B: <juicio> porque …
8.3-C: <juicio> porque …
8.3 comprobé: …
fuente 8.3: https://…
```

*(Em português: `correcta` é correta, `exagerada` é exagerada e `sin comprobar` é sem comprovar; `juicio` é juízo, `porque` é porque,
`comprobé` é comprovei e `fuente` é fonte. As palavras ficam em espanhol porque é isso que o `verifica` procura.)*

Em `8.3 comprobé` diga duas coisas: o que da resposta foi comprovado pela própria resposta (citou um arquivo, uma linha,
uma página?) e o que foi comprovado por quem estuda, e como. Em `fuente 8.3`, o endereço de uma página oficial usada
para comprovar algo.

**O que comprova:** os três juízos contra «Compare as respostas», e a comprovação (o que o
`verifica` revisa, em «Os exercícios»).

---

## Os exercícios

O passo 5 se faz **sem IA**. Das respostas em `respuestas/08.md`, o `verifica` só revisa o formato:
que estejam as linhas, os juízos e a fonte. Não sabe se se acertou nem se se escreveu sem IA; isso quem fecha é quem estuda
ao comparar com «Compare as respostas».

| # | Faz-se | Está pronto quando |
|---|---|---|
| 8.1 | Decidir com `isNewUser` se as boas-vindas aparecem e comprovar que aparecem só na primeira vez | O `verifica` diz «Nivel simulado: completo», com os 97 testes em verde, e a `predicción 8.1` está comparada com o que se viu. É o nível simulado; o passo 4 é o teste no telefone |
| 8.2 | Explicar o que o Firebase fez, o que se construiria sem ele e o que ainda não existe | As três linhas `8.2`, escritas sem IA, nomeiam a verificação do token, a criação do usuário, a sessão que permanece e o que ainda não existe (o perfil) |
| 8.3 | Julgar uma resposta do Claude Code sobre a segurança do início de sessão | Os três juízos coincidem com «Compare as respostas», cada um com o porquê e com o que o respalda: a página oficial, ou o arquivo e a linha |

**Tempo:** calcule uma hora e meia sem o passo 4, e vinte minutos a mais com ele. É uma estimativa: será ajustada com
o tempo que a primeira turma levar.

---

## Não roda?

| O que aparece | Causa provável | O que fazer |
|---|---|---|
| `File google-services.json is missing.` | O arquivo não foi copiado para `inicio/app/` | O `cp` de «Antes de começar» |
| `NotImplementedError` nos testes da primeira vez | `PrimeraVez.kt` continua com o `TODO` | Passo 3 |
| ❌ «Con additionalUserInfo en null tu esPrimeraVez truena o dice que sí» (com additionalUserInfo em null, o esPrimeraVez estoura ou diz que sim) | Usou-se `!!`, ou `!= false` | Passo 3: `?.` e `== true` |
| ❌ «Retomar con Microsoft» (retomar com a Microsoft) e o resto da Microsoft em verde | Mudou-se `entrar()` mas não `retomar()`, ou ao contrário | Passo 3: as duas funções |
| ❌ «De las 82 pruebas heredadas…» (dos 82 testes herdados…) | A entrada sempre diz que é a primeira vez, e o `Resultado` já não é o que os testes anteriores esperam | Passar a `primeraVez` que a porta respondeu, nada mais |
| ❌ «Restricción de esta actividad… ya no son iguales en inicio/ y en solucion/» (restrição desta atividade… já não são iguais em inicio/ e em solucion/) | Mudou um arquivo dado | `git diff -- <arquivo>` e desfazer à mão |
| No telefone, as boas-vindas nunca saem | A conta já existia no projeto, desde a 04 | Passo 4: outra conta, ou apagar o usuário |
| No telefone, as boas-vindas saem toda vez que se entra | O usuário foi apagado no console entre uma entrada e outra, ou algo diferente que não vimos | Conferir Authentication › Users; se o usuário continua ali e as boas-vindas saem toda vez, conte para nós |
| ❌ «EntradaConGoogle.kt o EntradaConMicrosoft.kt nombra una puerta concreta» (EntradaConGoogle.kt ou EntradaConMicrosoft.kt nomeia uma porta concreta) | Uma das entradas nomeia uma porta de verdade (`VerificadorConFirebase`, `NavegadorConFirebase`) ou um dublê dos testes. Os testes só lhes passam dublês: se o código pergunta qual lhe coube, o verde não diz o que ele faz com a de verdade | Que as entradas falem só com as interfaces das portas. A porta de verdade é escolhida pela `MainActivity.kt`, que vem dada (passo 3) |
| ❌ «Las pruebas no llegaron a correr» (os testes não chegaram a rodar) e o que vem depois não diz a causa | O Gradle falhou por algo que o `verifica` não reconhece; ele mostra as últimas linhas | Rode `./inicio/gradlew -p inicio testDebugUnitTest --stacktrace` e leia o final. Se for compartilhar, tire antes tokens e caminhos do computador |

O `verifica` desta lição foi medido em um Mac com Apple silicon. Não foi medido no Linux.

---

## Para fechar

**Responda sem reler:**

1. Quem sabe se é a primeira vez: o app ou o Firebase? Por que o app não pode saber sozinho, hoje?
2. Por que `== true` e não `!!`?
3. O `verifica` dá 97 de 97. Diga uma coisa que isso comprova e uma que não.
4. Ao reabrir com sessão, por que não há boas-vindas, ainda que seja a segunda vez que o app é aberto nesse dia?

**Fechamento do trecho.** O trecho começou com um ambiente vazio. Hoje existe um app Android nativo, em Kotlin e Compose,
de uma loja em que se entra com o Google ou com a Microsoft; em que uma pessoa com o mesmo e-mail nos dois chega a
um só usuário, porque essa pessoa o confirmou; que lembra de quem o abre de novo e esquece quando isso lhe é pedido; e que
dá as boas-vindas só na primeira vez. Cada peça passou pela mesma volta: prever, pedir, ler, rodar e
explicar. E no passo 5 foi dito, com palavras próprias, que parte o Firebase pôs e que parte foi posta por quem estuda.

**O que vem a seguir: o trecho 2.** A mesma loja, no iPhone, com SwiftUI. Não se traduz o código do Android linha
por linha: o app concluído hoje é a **especificação** do que o do iPhone tem que fazer, e os 97
testes dizem, caso por caso, o que é «fazê-lo». Para compilar um app de iPhone é preciso um Mac com Xcode, então
esse trecho é só no Mac. Será publicado quando estiver testado de ponta a ponta, como este.

---

## Compare as respostas

**Passo 1.** Só no 1 e no 4. No 1, o Firebase cria a Ana e `isNewUser` vale `true`. No 2 não houve início
de sessão: o app pergunta à sessão quem continua dentro, e isso não diz se é novo. No 3, a Ana já existe:
`isNewUser` vale `false`. No 4, o Luis é novo e a entrada retomada traz um `AuthResult` como qualquer outra:
os testes o medem com um dublê; com uma conta real, não está documentado nem capturado (passo 4).

**Passo 3.** Não entra, ainda que o Firebase a tenha deixado entrar. O `!!` converte o `null` em uma exceção; a entrada a
captura com o `catch (e: Exception)`, como qualquer outra falha, e a pessoa vê «Não foi possível entrar. Confira a
conexão e tente de novo.» («No se pudo entrar. Revisa tu conexión y vuelve a intentar.»). A conexão estava boa. E como o Firebase já tinha guardado a sessão, ao reabrir o
app (lição 07) ele cumprimentaria a pessoa como se nada tivesse acontecido: isso foi raciocinado a partir do código, não medido em um telefone. Com `?.` e
`== true`, um `null` vira «não é a primeira vez», e a pessoa entra sem boas-vindas.

**Passo 5.** O que se espera, com estas ideias ou outras:

- **O Firebase fez:** verificou o token de identidade do Google nos servidores dele; falou com a Microsoft usando o ID
  de cliente e a chave guardados no console dele, sem que a chave tocasse o app; criou o usuário na primeira
  vez que a pessoa entrou, com um identificador único; neste projeto, com a regra de uma conta por e-mail que o
  projeto tem configurada, rejeitou o segundo início de sessão com esse e-mail e, depois de essa pessoa confirmar e o app
  pedir o vínculo, uniu o segundo provedor ao mesmo usuário; guardou a sessão no telefone e renova o token de identidade a cada hora com o de
  atualização; e disse se o usuário era novo.
- **Sem o Firebase:** um servidor próprio que confira que cada token é autêntico, que foi emitido para o app e que não
  expirou; a troca com a Microsoft feita a partir desse servidor, que é onde viveria a chave; uma tabela de
  usuários com o identificador deles e com que contas se chega a cada um; a regra do e-mail repetido, e a chamada
  que une uma credencial nova ao usuário que já existe;
  tokens de sessão próprios, como se renovam e onde se guardam no telefone; e a marca de «usuário novo»,
  que seria preciso calcular ao criar a linha.
- **Ainda não há:** um perfil da loja. O usuário de autenticação tem um conjunto fixo de dados: um
  identificador, um e-mail, um nome e uma foto. A própria documentação diz que não se podem acrescentar outros e
  que vão em outro armazenamento. O endereço, os pedidos e as preferências não têm onde viver ainda: isso
  chega quando o curso introduzir armazenamento.

**Passo 6.**

- **A: correta.** A página do Firebase Authentication diz que o app passa as credenciais ao SDK e que os serviços
  dele as verificam e respondem. E se comprova no código: `VerificadorConFirebase` entrega o token a
  `signInWithCredential` e nunca o revisa por conta própria.
- **B: exagerada.** «Completamente» e «não é preciso revisar mais nada» nenhuma fonte os sustenta. No
  próprio trecho há contraexemplos: uma sessão aberta continua aberta em um telefone emprestado (lição 07); a chave
  da Microsoft pode vazar em uma captura (lição 05); e se o app vinculasse sem perguntar, juntaria contas
  sem permissão (lição 06). O Firebase resolve partes; a segurança do início de sessão também depende do que
  se faz do lado do app.
- **C: sem comprovar.** Pode ser verdadeira ou não: nenhuma das páginas desta série diz como o Firebase guarda os
  tokens no telefone. A referência de `signOut` só diz que apaga o usuário «do cache em disco». A
  resposta não cita nada. Para dá-la por boa, alguém teria que mostrar a fonte.
- **O que a resposta comprovou:** nada que se veja. Diz «revisei o início de sessão do app», mas não nomeia um
  arquivo, uma linha nem uma página. O que quem estuda pode comprovar: A, com a documentação e com `Puertas.kt`.

**Para fechar:**

1. O Firebase. O app não guarda uma lista de quem já entrou; o Firebase sim tem os usuários dele, e ao criar um
   o diz em `isNewUser`.
2. Porque `additionalUserInfo` pode vir `null`, e o `!!` converte esse `null` em uma exceção: a pessoa veria
   «Não foi possível entrar» sem que nada tivesse falhado. Com `== true`, `null` é «não».
3. Comprova o que a `esPrimeraVez` decide, que as duas entradas passam essa decisão e quando o início dá as
   boas-vindas, com dublês. Não comprova o que `isNewUser` responde com uma conta real, nem ao vincular.
4. Porque ao reabrir não houve início de sessão: não há `AuthResult` e não há a quem perguntar se é novo. O
   app o deixa em «não».

---

## Fontes da lição

Abertas em 19/11/2026.

- Firebase. Referência de `AdditionalUserInfo` (chega depois de um início de sessão, um vínculo ou uma reautenticação;
  `isNewUser` diz se o usuário é novo ou existente; `getProfile` nomeia a Microsoft entre os provedores).
  https://firebase.google.com/docs/reference/android/com/google/firebase/auth/AdditionalUserInfo
- Firebase. Referência de `AuthResult` (resultado de operações que afetam a autenticação;
  `getAdditionalUserInfo` é `@Nullable` e a descrição dela nomeia Facebook, GitHub, Google e Twitter).
  https://firebase.google.com/docs/reference/android/com/google/firebase/auth/AuthResult
- Firebase. Referência de `FirebaseAuth` (`signInWithCredential` cria a conta se não existia; `getPendingAuthResult`
  entrega a entrada que ficou pela metade). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth
- Firebase. Referência de `FirebaseUser` (`startActivityForLinkWithProvider` devolve um `AuthResult`; não diz quanto
  vale `isNewUser` ao vincular). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseUser
- Firebase. *Authenticate Using Microsoft on Android* (conferir primeiro a resposta pendente; os dados do
  provedor em `getAdditionalUserInfo()`; não menciona `isNewUser`). https://firebase.google.com/docs/auth/android/microsoft-oauth
- Firebase. *Authenticate with Google on Android* (na primeira vez que o usuário entra, cria-se uma conta no
  projeto). https://firebase.google.com/docs/auth/android/google-signin
- Firebase. *Link Multiple Auth Providers to an Account on Android* (o mesmo identificador, não importa o
  provedor; não menciona `isNewUser`). https://firebase.google.com/docs/auth/android/account-linking
- Firebase. *Users in Firebase projects* (propriedades fixas do usuário; não se acrescentam outras: vão em outro
  armazenamento). https://firebase.google.com/docs/auth/users
- Firebase. *Firebase Authentication* («How does it work?»: o app passa as credenciais e os serviços do
  Firebase as verificam). https://firebase.google.com/docs/auth
- Kotlin. *Null safety* (o operador `?.` devolve `null` se o que está à esquerda é `null`).
  https://kotlinlang.org/docs/null-safety.html
- Ensaio da série, 13/11/2026, Mac com Apple silicon, com um código de teste e contas reais: «(primeira vez)»
  saiu só na primeira entrada, com o Google e com a Microsoft.
- Medição própria: Mac com Apple silicon, 19/11/2026. O `verifica` sobre `inicio/` e `solucion/`, cada passo e cada
  sabotagem, com arquivos inventados e sem tocar no console do Firebase nem em nenhuma conta. Versões do projeto:
  as da série (AGP 9.4.1, Kotlin 2.4.21, Firebase BOM 35.2.0, que traz `firebase-auth` 25.0.0).
