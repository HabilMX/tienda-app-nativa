# Lição 06 — Dois botões, uma conta

**Antes de ler, três palavras.** Uma linha cada uma.

- **Colisão:** o que acontece quando alguém entra com um provedor e o e-mail dessa pessoa já tem usuário no projeto,
  criado com outro provedor. Foi vista na lição 05.
- **Vincular:** somar a um usuário que já existe outra forma de entrar. Não cria um usuário novo.
- **Uma conta por e-mail** (*one account per email address*): a opção do projeto do Firebase que faz a colisão existir.

**O que fica com quem termina:** uma pessoa com o mesmo e-mail no Google e na Microsoft que termina com **um** usuário na
loja, ao qual chega com qualquer um dos dois botões, porque ela o confirmou.

**Ideia nova desta lição:** uma pessoa, dois provedores. Com «uma conta por e-mail», o segundo se vincula
à conta que já existe, e só com a confirmação dela. O resto —a tela que pergunta, a porta do vínculo
e a nota que distingue um vínculo de uma entrada— já vem pronto e explicado, e não se avalia aqui.

**Uma nota sobre os textos do app.** Como nas lições anteriores, o projeto do curso traz os textos de tela em espanhol, e os arquivos de
`lecciones/` não são traduzidos: as verificações automáticas do curso (o `verifica`) procuram esses textos exatos.
Por isso, nos blocos de código, nas saídas de terminal e nos esboços de tela, os textos ficam como o projeto os tem;
na prosa, a lição os cita em português e, na primeira vez, traz o original entre parênteses.

---

## A cena

A Ana entrou na loja com o Google na semana passada. Hoje toca no outro botão. A conta da Microsoft dela tem o mesmo
e-mail.

Para ela é a mesma pessoa entrando na mesma loja. Para o projeto são duas credenciais distintas que
dizem ter o mesmo e-mail.

Isso foi testado no ensaio desta série, em 13 de novembro de 2026, com uma conta da Microsoft cujo e-mail já
tinha entrado com o Google. O Firebase não a deixou passar, e respondeu: «An account already exists with the same email
address but different sign-in credentials».

Há dois finais ruins. Em um, o app cria um segundo usuário, e os pedidos da Ana ficam repartidos entre duas
contas que não se conhecem. No outro, o app junta as duas contas por conta própria, sem perguntar a ninguém. A
lição 05 parou em dizer «entre com o outro botão». Esta escreve o final bom: a pessoa entra com o que já
tinha, o app pergunta, e se ela aceitar, os dois botões chegam ao mesmo lugar.

---

## Antes de começar

**O que é preciso:** a [05](05-entrar-con-microsoft.md) concluída (o `verifica` dela em 44 de 44). Os passos 1 a 4 não
pedem nenhuma conta da Microsoft. O passo 5 pede: o registro no Entra da 05, funcionando.

**O projeto de início.** A partir da raiz do repositório da série:

```bash
git pull
cp lecciones/03-la-app-conoce-a-firebase/inicio/app/google-services.json lecciones/06-dos-botones-una-cuenta/inicio/app/
cd lecciones/06-dos-botones-una-cuenta/inicio
```

Parte-se da solução da 05, com o andaime desta lição por cima. `./verifica solucion` sai em vermelho em um
clone limpo, porque sem o arquivo o projeto não compila. As respostas vão em `respuestas/06.md`.

**Regra que continua vigente da 05.** Nunca cole nem envie o valor da chave do Entra, um token de acesso ou
de identidade, nem capturas ou registros que os tragam. O resultado de um vínculo também traz um token de
acesso: este app não o lê. O `google-services.json` continua ignorado porque é do projeto de quem estuda: não o use
como evidência. Se uma chave ou um token tocou um arquivo, o terminal ou uma captura, ele se revoga e se cria outro.

**O que esta lição não faz.** Não desfaz um vínculo, não guarda a sessão nem a encerra (lição 07) e não
distingue a primeira vez (lição 08).

---

## Passo 1 · No console: a regra que faz a colisão existir

O choque desta lição —`ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL` ao entrar com o segundo
provedor— só aparece com uma opção do projeto do Firebase: «uma conta por e-mail» (*One account per
email address*). A referência do Firebase diz que esse erro só é lançado com ela ativada, e a recomenda.
Com várias contas por e-mail, o Firebase pode criar um segundo usuário: o primeiro final ruim da cena, e
nada a resolver. A mesma exceção tem outros códigos, por exemplo ao vincular uma conta que já é de outro
usuário: não são o caso desta lição, e o app os mostra como falha.

Procure-a na configuração do Authentication do console e confira se está ativa. Anote em
`respuestas/06.md` esta linha, sem e-mails nem chaves:

```text
una cuenta por correo: activa
```

*(Em português: «uma conta por e-mail: ativa». A linha fica em espanhol, porque é o que o `verifica` procura.)*

O `verifica` não vê o console: só procura essa linha. O caminho exato dessa opção no
console não o percorremos com uma conta; se não a encontrar, conte-nos onde estava.

**O que comprova:** a conferência, à mão. É parte da evidência do 6.1.

---

## Passo 2 · O que o projeto já traz

**A ordem quem põe é o Firebase.** O guia dele para vincular contas pede que **primeiro** a pessoa entre com qualquer
provedor e que **depois**, a esse usuário que já entrou, se some o outro. Assim, diz o guia, a pessoa tem o
mesmo identificador de usuário não importa com qual entre. Para a Microsoft, o guia desse provedor nomeia a
chamada: `startActivityForLinkWithProvider`, sobre o usuário que já entrou.

Três arquivos são novos e quatro mudaram em relação à solução da 05. Abra-os; não os edite:

| Arquivo | O que traz |
|---|---|
| `Enlace.kt` (novo) | `ResultadoDeEnlace` (vinculado, sem sessão, cancelado, falha), `EnlaceRetomado` e a interface `Enlace`, com `enlazar()` e `retomar()` |
| `PuertaDelEnlace.kt` (novo) | A porta `EnlazadorDeProveedor`. `enlazar` faz a chamada do Firebase; se ninguém entrou, lança `NadieHaEntrado` sem abrir nada. `pendiente` recolhe um vínculo que ficou pela metade |
| `PantallaEnlace.kt` (novo) | A confirmação: «¿También con Microsoft?» («Também com a Microsoft?»), com «Enlazar Microsoft» («Vincular a Microsoft») e «Ahora no» («Agora não») |
| `Tienda.kt` | Lembra se houve uma colisão. Quando essa pessoa entra depois com o botão certo, mostra a confirmação no lugar do início. Só «Enlazar Microsoft» chama o código de quem estuda |
| `PuertaDelNavegador.kt` | Uma linha: não toma o pendente se o que ficou pela metade foi um vínculo |
| `MainActivity.kt`, `Armado.kt` | Montam também o vínculo. Por ora `Armado.kt` devolve `EnlaceSinConectar()` |

O resto é idêntico à solução da 05, e o `verifica` o compara arquivo por arquivo. É uma restrição
desta atividade, não do objetivo: uma mudança em um arquivo pronto pode ser código válido e ainda assim sair em
vermelho, porque a lição os fixa para poder dizer o que mede.

**Um vínculo pela metade não é uma entrada pela metade.** A referência do Firebase diz que, se a tela morre
durante um vínculo, o desfecho dele se recolhe com `getPendingAuthResult`: o mesmo lugar por onde chega uma entrada
que ficou pela metade. O que segue é uma decisão deste app, não uma citação: esse resultado não traz uma etiqueta
que aqui se use para saber se foi vínculo ou entrada. Lido sempre como entrada, um vínculo cancelado seria visto
como «não foi possível entrar». Por isso a porta guarda uma nota no telefone antes de abrir o navegador para
vincular: «há um vínculo em andamento». É um sim ou um não; não guarda contas, tokens nem chaves. Quando o app abre, a
porta de entrar não toma o pendente se essa nota estiver posta, e `Tienda` pergunta primeiro ao vínculo.

**Meça o ponto de partida.** A partir da raiz do repositório:

```bash
./lecciones/06-dos-botones-una-cuenta/verifica
```

Em 18/11/2026, com um arquivo inventado, a última linha foi
`Resultado: 46 de 66 pruebas en verde (20 en rojo, 0 con error, 0 saltadas)`. São 66: as 44 herdadas da
05, já em verde, e 22 do vínculo. Dessas, duas já passam com o andaime e vinte esperam o código de quem estuda.

*(Em português: «Resultado: 46 de 66 testes em verde (20 em vermelho, 0 com erro, 0 pulados)».)*

---

## Passo 3 · Com o Claude Code: vincular e retomar

Entra como sempre: **prever → pedir → ler o que ele gerou → rodar o `verifica` → explicar.**

1. **Preveja**, em `respuestas/06.md`: no momento da colisão, antes de a pessoa entrar com o Google,
   a qual usuário se poderia vincular a Microsoft?
2. **Peça** ao Claude Code, a partir de `inicio/`: «Em `EnlaceConMicrosoft.kt`, tome `EntradaConMicrosoft.kt`
   como referência. `enlazar()` monta o mesmo provedor, chama `enlazador.enlazar` e devolve
   `ResultadoDeEnlace.Enlazado`. `retomar()` chama `enlazador.pendiente()`: se devolver `null`, devolve `null`;
   se não, devolve um `EnlaceRetomado` com o nome dele e com `Enlazado` se a `falla` dele for `null`, ou com a falha
   traduzida. As falhas são traduzidas em uma função à parte: `NadieHaEntrado` é `SinSesion`; uma
   `FirebaseAuthWebException` com `ERROR_WEB_CONTEXT_CANCELED`, `Cancelado`; qualquer outra, `Fallo`.
   `CancellationException` se deixa passar. Em `Armado.kt`, mude só o corpo de `armarEnlaceConMicrosoft` e
   apague ali `EnlaceSinConectar`. Não altere nenhum outro arquivo.»
3. **Leia.** O centro deve se parecer com isto:

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

   Confira três coisas: que `enlazar()` chame a porta do vínculo e não volte a entrar; que `retomar()` não
   responda sempre «vinculado»; e que ninguém tenha mexido nos testes, nas portas nem nas telas.

4. **Rode o `verifica`.** Os 66 testes podem ficar em verde. A entrega ainda não está completa: faltam
   as marcas «Escrito» de `respuestas/06.md`, e o nível real do passo 5. O `verifica` diz isso no final, em
   «Estado del objetivo 6.1» (estado do objetivo 6.1).
5. **Explique**, em três linhas: por que o vínculo é feito sobre o usuário que já entrou, e não na colisão.

**O que comprova, e até onde.** O `verifica` usa um projeto de mentira (`ProyectoFalso.kt`) com duas
pessoas: a Ana, com o mesmo e-mail no Google e na Microsoft, e o Luis, com outro e-mail. Comprova que antes de
vincular a Ana esbarra na colisão e não se cria um segundo usuário; que depois de vincular os dois acessos dela chegam ao mesmo
usuário; e que o Luis continua sendo outro. Comprova a confirmação, com as classes de quem estuda desenhadas em `Tienda`: na
colisão não se oferece vincular, depois de entrar pergunta-se sem vincular nada, e «Ahora no» não vincula. E comprova
que um vínculo pela metade é retomado como vínculo: o app abre na confirmação, com «Listo» (pronto) ou com a mensagem dele.

As sabotagens foram medidas sobre a solução em 18/11/2026: não reconhecer `NadieHaEntrado`, 63 de 66;
`retomar()` que sempre devolve `null`, 60; um que sempre responde «vinculado», 63; ler o vínculo pela metade
como entrada, 64.

Três limites. «O mesmo usuário» é o do projeto de mentira, que segue «uma conta por e-mail» escrita à
mão: **não** comprova que o Firebase vincule as contas de quem estuda. «Ficou pela metade» foi um dublê que disse: nenhum teste fechou
o app no meio do vínculo, e a nota do telefone só foi exercitada sem nada pendente. E que a porta de entrar
respeite essa nota nenhum teste vê (sem essa linha, os 66 dão verde): o que a respalda é que o arquivo seja
idêntico em `inicio/` e em `solucion/`.

---

## Passo 4 · Sem IA: e se o app vinculasse sozinho?

Este é o exercício 6.2 e se faz **sem Claude Code**. A confirmação é um critério desta série: vincular
dá a outra conta a entrada neste usuário, e isso quem decide é a pessoa que acabou de se autenticar com o
provedor da conta que já existe. Aqui isso é comprovado retirando-a.

**1. Preveja**, em `respuestas/06.md`: se o app vinculasse assim que a pessoa entra, sem perguntar, o que veria
a Ana? E o que poderia acontecer se a conta da Microsoft que colidiu não fosse dela?

**2. Quebre e meça.** Em `Tienda.kt`, dentro de `recibe`, troque esta linha:

```kotlin
pantalla = if (huboColision && enlace != null) Pantalla.CONFIRMAR_ENLACE else Pantalla.INICIO
```

por estas duas:

```kotlin
if (huboColision && enlace != null) alcance.launch { enlace.enlazar() }
pantalla = Pantalla.INICIO
```

Rode o `verifica`. Isto foi o que deu em 18/11/2026 (recortado):

```text
❌ Restricción de esta actividad (no del objetivo): estos archivos vienen dados y ya no son iguales … Tienda.kt
❌ La confirmación: 2 de 4 pruebas en verde. Nada se enlaza sin el toque de la persona, y solo se le ofrece …
❌ Al tocar «Enlazar Microsoft» no se enlaza, o después Microsoft no llega al mismo usuario (paso 3)
Resultado: 63 de 66 pruebas en verde (3 en rojo, 0 con error, 0 saltadas), leído de …
```

*(Essa é a saída do `verifica`, em espanhol, como o script a imprime. Em português: restrição desta atividade (não do
objetivo), estes arquivos vêm prontos e já não são iguais … `Tienda.kt`; a confirmação: 2 de 4 testes em verde, nada se
vincula sem o toque da pessoa, e só se oferece …; ao tocar «Enlazar Microsoft» não se vincula, ou depois a Microsoft
não chega ao mesmo usuário (passo 3). Resultado: 63 de 66 testes em verde.)*

Copie para `respuestas/06.md` a linha que diz «La confirmación».

**3. Conserte.** Devolva a linha original. Rode o `verifica`: 66 de 66, e `Tienda.kt` outra vez igual ao da
lição.

**4. Explique**, sem reler: por que o app pede confirmação antes de vincular, e o que aconteceria se vinculasse
sozinho. Compare com «Compare as respostas».

**O que comprova:** a explicação de quem estuda (objetivo 6.2). O `verifica` só procura a linha colada: não sabe se
a quebra foi feita nem avalia o que foi escrito. Oferecer o vínculo antes de entrar, na própria colisão, foi medido
à parte: 62 de 66.

---

## Passo 5 · No telefone: o teste manual

É a evidência real do 6.1, e **ainda não pode ser concluída com estas instruções**. O que segue diz o que
é preciso observar e o que falta medir. Pede o nível real da 05, com o registro no Entra funcionando, e «uma
conta por e-mail» ativa (passo 1).

**O percurso.** Com uma conta da Microsoft cujo e-mail já entrou com o Google: toque em «Entrar con Microsoft» e leia
a mensagem; entre com o Google; aceite «Enlazar Microsoft». Segundo o guia do Firebase, depois de um vínculo a
pessoa pode entrar com qualquer um dos provedores vinculados e chegar aos mesmos dados.

**O que falta: sair.** Para conferir o botão da Microsoft é preciso entrar de novo do zero, e este app
ainda não tem como encerrar a sessão: é a lição 07. A proposta para o ensaio, sem adiantar código, é
apagar os dados do app no telefone:

```bash
adb shell pm clear mx.habil.ejemplo.tienda
```

A documentação do `adb` diz que esse comando apaga todos os dados associados a um app. Com uma conta real ainda não
comprovamos três coisas: que o app fica sem sessão, que o usuário e os provedores dele continuam no Firebase, e que
ao tocar em «Entrar con Microsoft» se chega ao mesmo usuário. Também não medimos onde o console mostra os dois
provedores, nem fechamos o processo no meio de um vínculo com `adb shell am kill` (que não encerra a sessão: é outro
teste). Se for testado, conte-nos o que se viu.

**O registro.** Quando for feito, anote em `respuestas/06.md`, sem e-mails nem tokens:

| O que se anota | Exemplo |
|---|---|
| A data, em uma linha que comece assim | `prueba manual: 2026-11-30` |
| Antes de vincular | um usuário, com o Google |
| Depois de vincular | o mesmo usuário, com o Google e a Microsoft |

O `verifica` só lê a primeira linha, e mesmo com ela diz que não viu o teste.

No ensaio de 13/11/2026, com um código de teste que tinha, sim, como sair, viu-se o caminho curto: já
dentro com o Google vinculou-se a Microsoft, o mesmo usuário ficou com os dois provedores, e ao sair e entrar com a
Microsoft chegou-se a ele.

**O que comprova:** é a única coisa que comprova que o Firebase vinculou as contas. Um 66 de 66 não a substitui.

---

## Os exercícios

O passo 4 se faz **sem IA**.

| # | Faz-se | Está pronto quando |
|---|---|---|
| 6.1 | Resolver a colisão: a pessoa entra com o provedor que já tinha e vincula o outro, com confirmação | Três evidências, e as três são necessárias. A linha «una cuenta por correo: activa» em `respuestas/06.md`, depois de conferi-la no console. Simulado: «Estado del objetivo 6.1» diz nível simulado completo, com um projeto de mentira de duas pessoas; valida a ordem, a confirmação, o vínculo pela metade e que os dois acessos chegam ao mesmo usuário **de mentira**. Real: o teste manual do passo 5, com o registro anotado; é a única coisa que valida que o Firebase vinculou as contas, e ainda não a rodamos com uma conta real |
| 6.2 | Explicar por que o app pede confirmação antes de vincular e o que aconteceria se vinculasse sozinho | A previsão, o vermelho colado e a explicação estão em `respuestas/06.md`, e coincidem com «Compare as respostas». Quem compara a explicação é quem estuda |

**Tempo:** calcule uma hora e meia sem o teste manual, e meia hora a mais com ele. É uma estimativa: será ajustada com o tempo que a
primeira turma levar.

---

## Não roda?

| O que aparece | Causa provável | O que fazer |
|---|---|---|
| `File google-services.json is missing.` | O arquivo não foi copiado para `inicio/app/` | O `cp` de «Antes de começar» |
| ❌ «Restricción de esta actividad… lo heredado ya no es idéntico a la solución de la lección 05» (restrição desta atividade… o herdado já não é idêntico à solução da lição 05) | Mudou algo herdado | `git diff -- <arquivo>` e desfazer à mão |
| ❌ «Restricción de esta actividad… ya no son iguales en inicio/ y en solucion/» com `Tienda.kt` (já não são iguais em inicio/ e em solucion/) | Vem do passo 4 | Devolver a linha original |
| ❌ «Enlazar sin haber entrado primero» (vincular sem ter entrado antes) | O `resultadoDe` não reconhece `NadieHaEntrado` | Passo 3 |
| ❌ «Retomar el enlace» (retomar o vínculo) | `retomar()` não usa o que `enlazador.pendiente()` devolve, ou responde sempre o mesmo | Passo 3 |
| Entrou-se com a Microsoft com um e-mail que já tinha entrado com o Google, e não saiu a mensagem da colisão | O projeto permite várias contas por e-mail: o Firebase criou outro usuário | Passo 1. Não vincular dois usuários criados separadamente como se fosse o caso da lição |
| «Primero entra con el botón que ya usabas…» (primeiro entre com o botão que já usava…) | Pediu-se para vincular sem ninguém dentro | Entrar primeiro; o vínculo é sobre um usuário que já entrou |

No Linux Mint 22.3 (x86_64), o `verifica` desta lição saiu igual ao do Mac (18/11/2026), no nível
simulado e com um arquivo de teste que trazia a impressão digital desse computador: sobre `inicio/`, 46 de 66 em verde;
sobre `solucion/`, 66 de 66.

---

## Para fechar

**Responda sem reler:**

1. Qual opção do projeto faz aparecer o choque desta lição, e o que acontece sem ela?
2. Por que primeiro se entra e depois se vincula?
3. O `verifica` dá 66 de 66 e «nível simulado: completo». Diga uma coisa que isso comprova e uma que não.
4. Por que um vínculo pela metade não pode ser lido como uma entrada pela metade?

**O que vem a seguir:** na L07 a sessão permanece: o app reconhece a pessoa ao voltar, e ela aprende a encerrá-la.

---

## Compare as respostas

**Passo 3.** A nenhum: na colisão ninguém entrou, então não há usuário ao qual somar nada. Por isso a
ordem é entrar primeiro e vincular depois, e por isso a porta lança `NadieHaEntrado`.

**Passo 4.** A Ana não veria nada: entraria no início e, por trás, o navegador se abriria para vincular sem
que ela tivesse pedido. E se a conta da Microsoft que colidiu fosse de outra pessoa com acesso a esse e-mail, essa conta
ficaria com entrada no usuário da Ana sem que ela o decidisse. A confirmação põe a decisão na pessoa
que acabou de se autenticar com o provedor da conta que já existe. Isso prova que ela entrou com esse provedor;
não prova mais nada sobre quem ela é.

**Para fechar:**

1. «Uma conta por e-mail». Sem ela, o Firebase pode criar um segundo usuário com o mesmo e-mail, e não aparece
   o choque desta lição.
2. Porque vincular é somar um provedor a um usuário que já entrou: antes de entrar não há a quem.
3. Comprova o que o vínculo pede e o que responde, que nada se vincula sem o toque da pessoa e que, em um
   projeto de mentira, os dois acessos chegam ao mesmo usuário. Não comprova que o Firebase vincule as contas de quem estuda.
4. Porque o Firebase os entrega pelo mesmo lugar e não diz qual foi. Lido como entrada, um vínculo cancelado
   seria visto como «não foi possível entrar».

---

## Fontes da lição

Abertas em 18/11/2026.

- Firebase. *Link Multiple Auth Providers to an Account on Android* (primeiro se entra e depois se vincula; o mesmo identificador com qualquer provedor; depois de vincular é possível entrar com qualquer um. O exemplo dele usa `linkWithCredential`; para a Microsoft, o guia desse provedor indica `startActivityForLinkWithProvider`). https://firebase.google.com/docs/auth/android/account-linking
- Firebase. *Authenticate Using Microsoft on Android* (vincular a Microsoft a um usuário que já entrou). https://firebase.google.com/docs/auth/android/microsoft-oauth
- Firebase. *Authenticate Using Microsoft with JavaScript* (o caso Microsoft e Google com «uma conta por e-mail»: entrar com o provedor que já existe e vincular o outro). https://firebase.google.com/docs/auth/web/microsoft-oauth
- Firebase. Referência de `FirebaseUser` (`startActivityForLinkWithProvider` e o resultado pendente dele). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseUser
- Android Developers. *Android Debug Bridge* (`pm clear`: apaga todos os dados associados a um app; `am kill`). https://developer.android.com/tools/adb
- Firebase. Referência de `FirebaseAuth` (`getPendingAuthResult`). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth
- Firebase. Referência de `FirebaseAuthUserCollisionException` (`ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL` só com «uma conta por e-mail», que ela recomenda). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuthUserCollisionException
- Ensaio da série, 13/11/2026, Mac com Apple silicon, com um código de teste: a colisão com uma conta real, e o vínculo depois de entrar com o Google.
- Medição própria: Mac com Apple silicon, 18/11/2026. O `verifica` sobre `inicio/` e `solucion/`, cada passo e cada sabotagem, com arquivos inventados e sem tocar o console do Firebase, o do Entra nem nenhuma conta.
