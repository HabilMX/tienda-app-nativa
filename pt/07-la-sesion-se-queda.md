# Lição 07 — A sessão permanece

**Antes de ler, cinco palavras.** Uma linha cada uma.

- **Sessão:** o que permite ao app saber quem a pessoa é sem perguntar de novo.
- **Usuário atual:** o usuário que o Firebase lembra neste telefone. No código se chama `currentUser`.
- **Encerrar a sessão:** pedir ao telefone que esqueça esse usuário, para que da próxima vez se peça para entrar.
- **Token:** um comprovante digital da sessão. Não é uma senha, e também não se cola nem se publica.
- **Credential Manager:** a peça do Android que coordena as credenciais e pode lembrar qual conta a pessoa escolheu
  para este app.

**O que fica com quem termina:** uma loja que, ao ser aberta, dá um olá a quem já tinha entrado e pede para entrar a quem
não tinha; com um botão «Encerrar sessão» («Cerrar sesión») que de verdade tira a pessoa. E a diferença entre coisas que
costumam ser confundidas: o usuário atual, os tokens que o Firebase guarda, as identidades vinculadas ao usuário e o
que um provedor lembra por conta própria.

**Ideia nova desta lição:** o ciclo da sessão: lembrar, encerrar e expirar. O resto —a pergunta que o
app faz ao abrir, o botão no início e a porta que fala com o Firebase e com o Credential Manager— já vem
pronto e explicado, e não se avalia aqui.

**Uma nota sobre os textos do app.** Como nas lições anteriores, o projeto do curso traz os textos de tela em espanhol,
e os arquivos de `lecciones/` não são traduzidos: as verificações automáticas do curso (o `verifica`) procuram esses
textos exatos. Por isso, nos blocos de código, nas saídas de terminal e nos esboços de tela, os textos ficam como o projeto
os tem; na prosa, a lição os cita em português e, na primeira vez, traz o original entre parênteses.

---

## A cena

É 13 de novembro de 2026, no ensaio desta série. O app de teste acaba de dar um olá a uma conta real
do Google: «Olá, … (primeira vez)» («Hola, … (primera vez)»). Alguém o fecha e o abre de novo.

O app dá o olá outra vez. Antes que alguém toque no botão.

Nas lições 04, 05 e 06 o app de quem estuda não faz isso: abre sempre nas boas-vindas e pede para entrar, ainda que
se tenha entrado um minuto antes. A lição 06 deixou anotado: «este app ainda não tem como encerrar a sessão».

As duas coisas são verdadeiras ao mesmo tempo, e o mistério tem uma só resposta. O Firebase **sim** lembrava a pessoa desde a
lição 04: guarda isso no telefone por conta própria. O app do ensaio perguntava a ele ao abrir; o app da série nunca
lhe perguntou. Esta lição ensina a perguntar, e a pedir que esqueça.

---

## Antes de começar

**O que é preciso:** a [06](06-dos-botones-una-cuenta.md) concluída (o `verifica` dela em 66 de 66). Os passos 1 a 3
e o 5 não pedem nenhuma conta. O passo 4 é no telefone ou no emulador, com a conta do Google da 04.

**O projeto de início.** A partir da raiz do repositório da série:

```bash
git pull
cp lecciones/03-la-app-conoce-a-firebase/inicio/app/google-services.json lecciones/07-la-sesion-se-queda/inicio/app/
cd lecciones/07-la-sesion-se-queda/inicio
```

Parte-se da solução da 06, com o andaime desta lição por cima. `./verifica solucion` sai em vermelho em um
clone limpo, porque sem o arquivo de quem estuda o projeto não compila. As respostas vão em `respuestas/07.md`.

**Regra que continua em vigor.** Nunca cole nem suba um token, nem capturas ou registros que o tragam. Esta
lição fala muito de tokens, e justamente por isso não imprime nenhum: o app só lê o nome do usuário.

**O que esta lição não faz.** Não distingue a primeira vez (lição 08). Não apaga a conta do Google do
telefone nem o usuário do projeto do Firebase: encerrar a sessão não é nada disso, e o passo 5 explica por quê.

---

## Passo 1 · Sem IA: aonde chega o app ao ser reaberto?

Este é o exercício 7.1 e se faz **sem o Claude Code**. Ainda não leia o passo 2.

Pense no app **concluído** desta lição: o que já pergunta ao Firebase ao abrir. Escreva em
`respuestas/07.md` duas linhas que comecem assim:

```text
predicción 7.1 con sesión: …
predicción 7.1 sin sesión: …
escrita el AAAA-MM-DD, antes del paso 2 y sin IA
```

*(Em português: previsão 7.1 com sessão; previsão 7.1 sem sessão; escrita em AAAA-MM-DD, antes do passo 2 e sem IA. As linhas ficam em
espanhol porque é isso que o `verifica` procura.)*

Na primeira: entrou-se com o Google, o app foi fechado pela lista de apps recentes e é aberto de novo. Que
tela aparece, e a janela para escolher conta se abre? Na segunda: tocou-se em «Encerrar sessão» («Cerrar sesión») e o app é aberto
de novo. Que tela aparece, e o que acontece ao tocar em «Entrar»?

**Meça o ponto de partida.** A partir da raiz do repositório:

```bash
./lecciones/07-la-sesion-se-queda/verifica
```

Em 19/11/2026, com um arquivo inventado, a última linha da contagem foi
`Resultado: 68 de 82 pruebas en verde (14 en rojo, 0 con error, 0 saltadas)`. São 82: as 66 herdadas da
06, já em verde, e 16 da sessão. Dessas, duas já passam com o andaime e catorze esperam o código de quem estuda.

*(Em português: «Resultado: 68 de 82 testes em verde (14 em vermelho, 0 com erro, 0 pulados)».)*

**O que comprova:** a previsão, contra o que se vir no passo 3 (os testes) e no passo 4 (o telefone). O
`verifica` só confirma que as linhas foram deixadas: não vê quando foram escritas, nem se se usou IA, nem se se acertou.
Por isso, guarde-se a previsão com a data, antes do passo 2, e declare-se embaixo, em uma linha, que foi feita sem
IA. O verde não avalia o conteúdo.

---

## Passo 2 · O que o projeto já traz

**O Firebase já dizia como perguntar.** O guia dele para entrar com o Google no Android diz: ao iniciar a `Activity`,
confira se a pessoa já iniciou sessão, e isso se faz lendo `currentUser`. A referência de `FirebaseAuth` o
resume: `getCurrentUser()` devolve o usuário que iniciou sessão, ou `null` se não houver nenhum.

No app, três arquivos são novos e quatro mudaram em relação à solução da 06; nos testes chegam três
classes novas e o dublê delas, `DoblesDeSesion.kt`. Abra-os; edite só os dois que o passo 3 indica:

| Arquivo | O que traz |
|---|---|
| `Sesion.kt` (novo) | A interface `Sesion`: `quienSigueAdentro()` e `cerrar()` |
| `PuertaDeLaSesion.kt` (novo) | A porta `RecuerdoDeLaSesion` com três funções: `usuarioActual()`, `olvidarEnFirebase()` e `olvidarEnElTelefono()`; a implementação de verdade, `RecuerdoConFirebase`; e à parte, o `LimpiadorDeCredenciales` que fala com o Credential Manager |
| `SesionDeLaTienda.kt` (novo) | A forma da classe de quem estuda, vazia. É escrita no passo 3 |
| `Tienda.kt` | Ao abrir, pergunta **uma vez** à sessão quem continua dentro. Se alguém, abre no início com o nome; se ninguém, nas boas-vindas |
| `Inicio.kt` | O botão «Encerrar sessão» («Cerrar sesión»). Só é desenhado se há sessão: sem ela, o início é o da 06 |
| `MainActivity.kt`, `Armado.kt` | Montam também a sessão. Por ora o `Armado.kt` devolve `SesionSinConectar()`, que não lembra ninguém: assim se comporta o app da 06 |

O resto é idêntico à solução da 06, e o `verifica` o compara arquivo por arquivo. É uma restrição
desta atividade, não do objetivo.

**As três funções da porta, cada uma com a fonte dela:**

- `usuarioActual()` lê `FirebaseAuth.getInstance().currentUser` e devolve só o nome.
- `olvidarEnFirebase()` chama `signOut()`. A referência diz que encerra a sessão do usuário atual **e o
  apaga do cache em disco**. Essa frase resolve o mistério da cena: o Firebase guarda o usuário no
  disco do telefone, e por isso ele sobrevive ao fechamento do app.
- `olvidarEnElTelefono()` pede ao limpador dela que chame `clearCredentialState` do Credential Manager. O guia
  do Firebase pede que isso seja feito ao encerrar a sessão, além do `signOut`. A referência do Credential Manager explica
  para quê: um provedor de credenciais pode guardar uma sessão ativa de um app e dar prioridade a ela na próxima
  vez. Este aviso pede que limpe esse estado; assim ele pode deixar de priorizar a credencial anterior. As
  opções que aparecem no final são decididas pelo provedor, e se conferem no telefone.
- O limpador fica à parte, na própria porta, por uma razão dos testes: assim um teste pode dar à
  porta de verdade um limpador de mentira e ver que ela de fato o chama ao encerrar a sessão.

**Um aviso da própria documentação.** A página do Firebase para gerenciar usuários avisa de dois
casos. `getCurrentUser` pode devolver `null` porque o Firebase ainda não terminou de se preparar. E pode devolver
um usuário cujo token já não é válido, por exemplo se ele foi apagado de outro dispositivo e o token do telefone
não foi renovado. Para reagir a isso, ela oferece um `AuthStateListener`, que avisa cada vez que o estado
do token muda.

Esta lição faz uma única leitura ao abrir, como o guia do Google, para estudar o ciclo básico. Essa leitura
não afirma que o Firebase já tenha recuperado a sessão, nem que o token do telefone continue válido. Um app
de produção escuta o `AuthStateListener` ou algo equivalente. Confira o resultado real no telefone: um
82 de 82 não comprova que a sessão seja guardada de verdade.

---

## Passo 3 · Com o Claude Code: lembrar e encerrar

Entra como sempre: **prever → pedir → ler o que ele gerou → rodar o `verifica` → explicar.**

1. **Preveja**, em `respuestas/07.md`: se `cerrar()` pedisse ao Firebase que esquecesse e o aviso ao telefone
   falhasse, a pessoa fica dentro ou fora?
2. **Peça** ao Claude Code, parado em `inicio/`: «En `SesionDeLaTienda.kt`, `quienSigueAdentro()` regresa lo
   que diga `recuerdo.usuarioActual()`. `cerrar()` primero llama `recuerdo.olvidarEnFirebase()` y después
   `recuerdo.olvidarEnElTelefono()` dentro de un `try`: `CancellationException` se deja pasar y
   `ClearCredentialException` se atrapa sin hacer nada más, porque la sesión ya quedó cerrada. En `Armado.kt`
   cambia solo el cuerpo de `armarSesion` para que regrese `SesionDeLaTienda`, y borra ahí `SesionSinConectar`.
   No cambies ningún otro archivo.»

   *(Em português: em `SesionDeLaTienda.kt`, `quienSigueAdentro()` devolve o que `recuerdo.usuarioActual()` disser.
   `cerrar()` primeiro chama `recuerdo.olvidarEnFirebase()` e depois `recuerdo.olvidarEnElTelefono()` dentro de um
   `try`: `CancellationException` se deixa passar e `ClearCredentialException` se captura sem fazer mais nada, porque a
   sessão já ficou encerrada. Em `Armado.kt`, mude só o corpo de `armarSesion` para que devolva `SesionDeLaTienda`, e
   apague ali `SesionSinConectar`. Não mude nenhum outro arquivo.)*
3. **Leia.** O centro deve se parecer com isto:

```kotlin
override fun quienSigueAdentro(): String? = recuerdo.usuarioActual()

override suspend fun cerrar() {
    recuerdo.olvidarEnFirebase()
    try {
        recuerdo.olvidarEnElTelefono()
    } catch (e: CancellationException) {
        throw e
    } catch (e: ClearCredentialException) {
        // O aviso não pôde ser dado. A sessão do Firebase já está encerrada.
    }
}
```

   Confira quatro coisas: que `cerrar()` faça **as duas** coisas e nessa ordem; que o `catch` não seja um
   `Exception` genérico que engula o cancelamento; que a classe só conheça `RecuerdoDeLaSesion`, sem
   perguntar que porta lhe coube; e que ninguém tenha mexido nos testes, nas portas nem nas telas.

4. **Rode o `verifica`.** Os 82 testes podem ficar em verde. A entrega ainda não está completa: faltam
   as linhas do 7.1 e do 7.3 em `respuestas/07.md`, e o `verifica` diz isso no final.
5. **Explique**, em três linhas: por que o Firebase esquece primeiro e o aviso ao telefone vem depois, dentro do
   `try`.

**O que comprova, e até onde.** Os testes usam o projeto de mentira da 06 como se fosse a memória do
telefone. A Ana entra com o Google; o teste «fecha e volta a abrir» o app, e a Ana continua dentro sem que se abra
a janela de contas. Toca em «Encerrar sessão» («Cerrar sesión»), o app volta às boas-vindas, é reaberto e pede para entrar; ao entrar
outra vez chega ao **mesmo** usuário. Outros testes dão à classe uma porta de mentira e olham o que ela pediu:
que esqueça uma vez, que avise uma vez, que um aviso que falha não derrube o app e que o cancelamento não seja engolido.
E com a `MainActivity` medem que a sessão que o app monta é a escrita por quem estuda; que com a porta de verdade, no
computador, ninguém continua dentro; e que, com essa mesma porta de verdade e um limpador de mentira, encerrar a sessão
pede ao Credential Manager que limpe. Além disso, o `verifica` confere como texto que `SesionDeLaTienda` só
programe contra `RecuerdoDeLaSesion`: se nomear uma porta concreta ou um dublê, o nível simulado fica
incompleto.

As sabotagens foram medidas sobre a solução em 19/11/2026:

| Sabotagem | Resultado |
|---|---|
| `quienSigueAdentro()` que sempre responde `null` | 78 de 82 |
| `cerrar()` sem `olvidarEnFirebase()` | 78 de 82 |
| `cerrar()` sem o aviso ao telefone | 78 de 82 |
| O aviso sem `try` | 81 de 82 |
| `catch (e: Exception)` que engole o cancelamento | 81 de 82 |
| `quienSigueAdentro()` que sempre responde que há alguém | 71 de 82, e duas das vermelhas são herdadas de lições anteriores |
| O aviso ao telefone só quando a porta **não** é a de verdade | 81 de 82, e a marca de «só programa contra `RecuerdoDeLaSesion`» em vermelho |

Cada uma deixa ainda a marca dela em vermelho na saída, com o passo que a conserta.

A última é a que justifica o limpador à parte: sem um teste que encerre a sessão com a porta de verdade, uma
classe que só se comporta bem com os dublês sairia em verde.

Três limites. «Reabrir» nos testes é desenhar o app do zero com a mesma memória de mentira: nenhum
teste mata o processo nem lê o disco do telefone. Nenhum vê um usuário real do Firebase: no computador
nunca existe um. E o Credential Manager de verdade nenhum teste o chama: no computador ele não responde
(medido em 19/11/2026: dez segundos sem resposta). Os testes veem que se pede para limpar, não o que a janela de contas oferece
depois: isso só se vê no telefone.

---

## Passo 4 · No telefone: reabrir e encerrar

Instale e abra o app com o `google-services.json`, como na 04:

```bash
./gradlew installDebug
```

**Com sessão.** Entre com o Google. Feche o app pela lista de apps recentes (deslizando-o para fora) e abra-o de novo
pela gaveta. Compare com a primeira previsão.

**Sem sessão.** Toque em «Encerrar sessão» («Cerrar sesión»). Feche o app do mesmo jeito e abra-o. Toque em «Entrar» e em «Entrar com o
Google» («Entrar con Google»). Compare com a segunda previsão.

O que já se viu no ensaio de 13/11/2026, com um código de teste: ao reabrir, o app deu o olá sem que se tocasse no
botão. O que **ainda não capturamos com uma conta real** é este percurso com o código desta lição: o
botão «Encerrar sessão», o app que ao reabrir pede para entrar e o que a janela de contas mostra depois. Se aparecer
diferente, conte para nós.

Com a Microsoft convém saber uma coisa. O guia do Firebase para a Microsoft documenta o encerramento no app, com
`signOut`, e não diz nada do navegador. No ensaio, ao voltar a entrar com a Microsoft, a página dela já mostrava
uma sessão ativa do navegador, com a conta pronta para ser escolhida sem senha. Isso foi visto nesse
navegador, naquele dia; não é uma garantia para qualquer navegador. Em um computador compartilhado, confira essa sessão e
encerre-a separadamente.

**E a dívida da 06.** O teste manual do passo 5 dela pedia para sair a fim de voltar a entrar com a Microsoft, e
propunha apagar os dados do app. Agora existe «Encerrar sessão»: é a forma de sair que o próprio app
oferece.

**O que comprova:** o que for observado. Não há marca no `verifica` para este passo.

---

## Passo 5 · Quatro coisas que não são a mesma coisa

Este é o exercício 7.3. Ao reabrir com sessão e ao encerrá-la, intervêm quatro coisas distintas, e cada uma
tem a fonte dela:

| | O que é | O que «Encerrar sessão» faz com ela neste app | Fonte |
|---|---|---|---|
| **O usuário atual** | O usuário do projeto que o Firebase lembra neste telefone (`currentUser`) | `signOut()` encerra a sessão dele e o apaga do cache em disco: `currentUser` fica em `null` | Referência de `FirebaseAuth` (`getCurrentUser`, `signOut`) |
| **Os tokens do Firebase** | O de identidade dura uma hora. O de atualização serve para pedir outro, e expira se o usuário é apagado ou desativado, ou diante de uma mudança maior na conta dele, como a senha ou o e-mail. O servidor também pode revogá-lo, e redefinir a senha o revoga | `signOut()` limpa o estado no telefone. Não é uma revogação no servidor | *Manage User Sessions* do Firebase |
| **As identidades vinculadas** (`providerData`) | As identidades de cada provedor vinculadas ao usuário do Firebase: com o Google e com a Microsoft, se foram vinculadas na 06 | Nada: nem `signOut` nem `clearCredentialState` as desvinculam | Referência de `FirebaseUser` (`getProviderData`) |
| **O que um provedor lembra por conta própria** | O estado de credencial que um provedor do telefone pode guardar para este app. À parte estão a conta do Google no telefone e a sessão da Microsoft no navegador | `clearCredentialState` só pede que se limpe esse estado local para este app. Não apaga a conta do Google do telefone nem a sessão do navegador da Microsoft | Referência de `CredentialManager` (`clearCredentialState`) |

A página de sessões é da parte de servidor do Firebase, mas descreve a sessão de qualquer usuário:
«as sessões do Firebase Authentication são de longa duração». Por isso o app não pede para entrar a cada hora: o
token de identidade expira, e o Firebase pede outro com o de atualização.

**Agora é a vez de quem estuda.** Para cada afirmação, escreva em `respuestas/07.md` `verdadera` ou `falsa`, assim: `7.3-1: falsa`.
*(Em português: verdadeira ou falsa; as palavras ficam em espanhol porque é isso que o `verifica` procura.)*

1. Encerrar a sessão no app apaga a conta do Google do telefone.
2. O token de identidade do Firebase dura uma hora; por isso, no dia seguinte, o app pede para entrar outra vez.
3. No código desta lição, se ao abrir o Firebase já tem um usuário atual, o app dá o olá sem abrir a
   janela de contas do Google.
4. Depois de `signOut()`, `currentUser` é `null`.
5. Encerrar a sessão no app desvincula a Microsoft do usuário do Firebase: ela desaparece de `providerData`.

Escolha **uma** e justifique-a com a documentação: copie o endereço da página oficial em uma linha que
comece com `fuente 7.3: https://`, e embaixo, com palavras próprias, o que ela diz.

**O que comprova:** cinco de cinco contra «Compare as respostas», e a justificativa. O `verifica` só
confirma que as cinco linhas e a fonte foram deixadas: compare-as com essa seção. O verde não avalia o
conteúdo.

---

## Os exercícios

O passo 1 se faz **sem IA**.

| # | Faz-se | Está pronto quando |
|---|---|---|
| 7.1 | Prever a que tela o app chega ao ser reaberto com e sem sessão, e comprová-lo | As duas linhas `predicción 7.1` estão em `respuestas/07.md`, com a data, escritas antes do passo 2 e declaradas sem IA, e foram comparadas com os testes do passo 3 e com o telefone no passo 4. O `verifica` só vê que as linhas existem |
| 7.2 | Implementar o encerramento da sessão de modo que ao voltar se peça para entrar outra vez | O `verifica` diz «Nivel simulado: completo», com os 82 testes em verde. É o nível simulado; o passo 4 é o teste no telefone |
| 7.3 | Distinguir o usuário atual, os tokens do Firebase, as identidades vinculadas e o que um provedor lembra | Cinco de cinco contra «Compare as respostas», e uma justificada com a fonte oficial. Isso é comparado por quem estuda: o `verifica` não o avalia |

**Tempo:** calcule uma hora e quinze minutos sem o passo 4, e vinte minutos a mais com ele. É uma estimativa: será ajustada
com o tempo que a primeira turma levar.

---

## Não roda?

| O que aparece | Causa provável | O que fazer |
|---|---|---|
| `File google-services.json is missing.` | O arquivo não foi copiado para `inicio/app/` | O `cp` de «Antes de começar» |
| `NotImplementedError` nos testes da sessão | `SesionDeLaTienda.kt` continua com os `TODO` | Passo 3 |
| ❌ «Restricción de esta actividad… ya no son iguales en inicio/ y en solucion/» (restrição desta atividade… já não são iguais em inicio/ e em solucion/) | Mudou um arquivo dado | `git diff -- <arquivo>` e desfazer à mão |
| ❌ «Cuando el aviso falla» (quando o aviso falha) | O `catch` captura `Exception` e engole o cancelamento, ou não há `try` | Passo 3: `CancellationException` primeiro, e se lança de novo |
| ❌ «De las 66 pruebas heredadas…» (dos 66 testes herdados…) e vários da sessão em vermelho | A sessão diz que sempre há alguém dentro, e o app já não abre nas boas-vindas | `quienSigueAdentro()` responde o que a porta disser, nada mais |
| No telefone, o app abre nas boas-vindas ainda que se tenha entrado | Foi instalado o de `inicio/` antes do passo 3, ou os dados do app foram apagados | Terminar o passo 3 e instalar de novo |
| Ao voltar a entrar com a Microsoft, não se pede senha | Pode ser a sessão da Microsoft no navegador, que não é a do app | Passo 4. Em um computador compartilhado, conferir essa sessão e encerrá-la no navegador |
| O app abre nas boas-vindas, e a sessão aparece depois | O Firebase pode não ter terminado de se preparar quando o app fez a única leitura | Não apagar dados nem dar o teste por bom: anotar o caso. Esta lição faz uma leitura única; uma versão robusta escuta o `AuthStateListener` (passo 2) |
| ❌ «SesionDeLaTienda solo programa contra RecuerdoDeLaSesion…» (SesionDeLaTienda só programa contra RecuerdoDeLaSesion…) | A classe nomeia uma porta concreta ou um dublê | Passo 3: a classe só conhece a interface |

O `verifica` desta lição foi medido em um Mac com Apple silicon e no Linux Mint 22.3, em 19/11/2026: com a solução, 82 de 82 nos dois.

---

## Para fechar

**Responda sem reler:**

1. Por que o app da 06 abria nas boas-vindas ainda que o Firebase lembrasse a pessoa?
2. Que duas coisas faz «Encerrar sessão», e por que nessa ordem?
3. O `verifica` dá 82 de 82. Diga uma coisa que isso comprova e uma que não.
4. Se o token de identidade dura uma hora, por que o app não pede para entrar a cada hora?

**O que vem a seguir:** na L08, a primeira vez: o app distingue o usuário novo do que volta.

---

## Compare as respostas

**Passo 1.** Com sessão: se o Firebase já tem o usuário atual quando o app faz a leitura, o app abre
direto no início, com o nome, e a janela de contas não se abre. É o que os testes medem, e o que
o ensaio viu com uma conta real; com uma única leitura, não é uma garantia (passo 2). Sem sessão: abre nas
boas-vindas; ao tocar em «Entrar» chega-se à tela com os botões, e ao entrar com o Google, sim, a
janela se abre.

**Passo 3.** Fora. O Firebase esqueceu primeiro, e é isso que decide se alguém continua dentro. O aviso ao
telefone só muda que opções se oferecem da próxima vez; se falha, não deixa ninguém dentro.

**Passo 5.**

1. Falsa. `clearCredentialState` só pede que se limpe o estado que um provedor guardava para o app; a conta
   do Google continua no telefone.
2. Falsa. O token de identidade dura uma hora, mas o Firebase pede outro com o de atualização: as sessões são
   de longa duração. O de atualização expira ou é revogado por outras razões (o usuário é apagado ou
   desativado, muda a senha ou o e-mail, ou o servidor o revoga), não pela passagem do tempo.
3. Verdadeira. O app lê `currentUser` uma vez ao abrir, e com um usuário atual vai direto ao início; os
   testes medem que a janela não se abre. Que o Firebase já o tenha pronto ao abrir é outra pergunta (passo 2).
4. Verdadeira. A referência de `signOut()` diz que encerra a sessão do usuário atual e o apaga do cache em
   disco; `getCurrentUser()` devolve `null` se não houver nenhum.
5. Falsa. `providerData` são as identidades vinculadas ao usuário do Firebase, e encerrar a sessão não as
   desvincula: nem `signOut` nem `clearCredentialState` tocam nesse vínculo.

**Para fechar:**

1. Porque o app nunca lhe perguntava: abria sempre nas boas-vindas. O Firebase sim guardava o usuário.
2. Pede ao Firebase que esqueça o usuário e avisa os provedores do telefone. Primeiro o Firebase, porque
   é isso que encerra a sessão; o aviso pode falhar sem deixar ninguém dentro.
3. Comprova o que a sessão decide ao abrir, o que pede ao encerrar (também com a porta de verdade) e a que
   tela o app chega, com uma memória de mentira. Não comprova que o Firebase lembre o usuário no
   telefone, nem o que a janela de contas oferece depois de encerrar.
4. Porque o Firebase usa o token de atualização para conseguir um novo token de identidade. Encerrar a sessão no
   app não revoga esse token no servidor: limpa o estado do telefone.

---

## Fontes da lição

Abertas em 19/11/2026.

- Firebase. *Authenticate with Google on Android* (conferir `currentUser` ao iniciar a Activity; para sair, `signOut` e `clearCredentialState`, com a `ClearCredentialException` dele). https://firebase.google.com/docs/auth/android/google-signin
- Firebase. *Manage Users in Firebase* (Android) (`getCurrentUser` pode devolver `null` se o Firebase não termina de se preparar, ou um usuário cujo token já não é válido; `AuthStateListener`; `getProviderData`). https://firebase.google.com/docs/auth/android/manage-users
- Firebase. *Manage User Sessions* (sessões de longa duração; token de identidade de uma hora; quando expira o de atualização; redefinir a senha o revoga; `revokeRefreshTokens` a partir do servidor). https://firebase.google.com/docs/auth/admin/manage-sessions
- Firebase. Referência de `FirebaseAuth` (`getCurrentUser`; `signOut` encerra a sessão e apaga o usuário do cache em disco; `addAuthStateListener`). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth
- Firebase. Referência de `FirebaseUser` (`getProviderData`: as identidades vinculadas ao usuário; `getDisplayName`). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseUser
- Firebase. *Authenticate Using Microsoft on Android* (para sair, `signOut`; não menciona a sessão do navegador). https://firebase.google.com/docs/auth/android/microsoft-oauth
- Android Developers. Referência de `CredentialManager` (`clearCredentialState`: quando chamá-lo e para quê). https://developer.android.com/reference/kotlin/androidx/credentials/CredentialManager
- Ensaio da série, 13/11/2026, Mac com Apple silicon, com um código de teste e uma conta real: ao reabrir, o app deu o olá antes de se tocar no botão; ao voltar a entrar com a Microsoft, a página dela já mostrava a conta iniciada.
- Medição própria: Mac com Apple silicon, 19/11/2026. O `verifica` sobre `inicio/` e `solucion/`, duas vezes seguidas cada um, cada passo e cada sabotagem, com arquivos inventados e sem tocar no console do Firebase nem em nenhuma conta.
- Medição no Linux Mint 22.3, 19/11/2026: o `verifica` sobre `solucion/` a partir de um clone novo, 82 de 82.
