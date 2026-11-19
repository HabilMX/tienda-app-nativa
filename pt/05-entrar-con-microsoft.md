# Lição 05 — Entrar com a Microsoft

**Antes de ler, cinco palavras.** Uma linha cada uma; as demais chegam no passo em que são usadas.

- **Microsoft Entra:** o serviço da Microsoft onde se cadastram os apps que deixam entrar com uma conta da
  Microsoft. O console dele fica em `entra.microsoft.com`.
- **Diretório:** o espaço do Entra que pertence a uma pessoa ou a uma organização. Um app é registrado
  dentro de um diretório. A documentação em inglês o chama de *tenant*.
- **Endereço de retorno:** o endereço web para o qual a Microsoft manda a pessoa quando ela termina de entrar. A
  documentação o chama de *redirect URI*.
- **Chave de cliente:** uma senha do app, não de uma pessoa. Com ela, quem a tem se identifica perante a
  Microsoft como esse app. A documentação a chama de *client secret*.
- **Resultado pendente:** a resposta de uma entrada que terminou quando o app já não estava lá para
  recebê-la.

**O que fica com quem termina:** o botão «Entrar com a Microsoft» («Entrar con Microsoft») ao lado do botão do Google, escrito por quem estuda, que recolhe a entrada
mesmo que o Android tenha fechado o app no meio; o app registrado no Entra sem que a chave dele toque em um único
arquivo; e uma tabela própria que diz o que é igual e o que muda entre os dois provedores.

**Ideia nova desta lição:** um segundo provedor que se registra fora do Firebase, com um endereço de
retorno exato e uma chave que nunca toca o app. O resto —as bibliotecas, o navegador, a terceira «porta» e
o segundo botão na tela— já vem pronto e explicado, e não se avalia aqui.

**Uma nota sobre os textos do app.** Como nas lições anteriores, o projeto do curso traz os textos de tela em espanhol, e os arquivos de
`lecciones/` não são traduzidos: as verificações automáticas do curso (o `verifica`) procuram esses textos exatos.
Por isso, nos blocos de código, nas saídas de terminal e nos esboços de tela, os textos ficam como o projeto os tem;
na prosa, a lição os cita em português e, na primeira vez, traz o original entre parênteses.

---

## A cena

A página da Microsoft está aberta no telefone. A pessoa ainda não escolhe a conta. Atrás dessa página,
o app já não existe: o Android o fechou.

A página continua lá, esperando.

Isso foi provocado de propósito no ensaio desta série, em 13 de novembro de 2026, em um emulador. Com a
página da Microsoft à frente, fechou-se o processo do app pelo terminal, como o Android faria se
precisasse de memória. Depois a conta foi escolhida. O Android iniciou o app de novo, do zero, sem nada do que
ele tinha na memória.

A pessoa já tinha entrado. A pergunta é se o app fica sabendo, ou se pede que ela entre outra vez.

No ensaio ele ficou sabendo, porque ao iniciar perguntava uma coisa antes de tudo: ficou uma entrada pela metade? Essa
pergunta é uma linha, e esta lição a escreve. Também escreve o que vem antes: registrar o app na
Microsoft, o que não se faz no Firebase, e cuidar de uma chave que só aparece uma vez.

---

## Antes de começar

**O que é preciso:** a [04](04-entrar-con-google.md) concluída (o `verifica` dela em 20 de 20) e o ambiente da
[00](00-ambiente-listo.md). Para o passo 7 basta um emulador aberto ou um telefone conectado. Vale o que diz
«Conta e equipamento de confiança» na 00.

**Para os passos 8 e 9, além disso:** uma conta da Microsoft para entrar e um diretório do Entra onde seja possível
registrar um app. São duas coisas distintas, e a segunda é a que dá trabalho:

- O guia da Microsoft pede uma conta do Azure com assinatura ativa e o papel de *Application Developer*.
- No ensaio de 13/11/2026, uma conta pessoal que **já tinha diretório** registrou o app por
  `entra.microsoft.com`, sem assinatura e sem cartão.
- Nesse dia, uma conta **recém-criada** não conseguiu: «Nuevo registro» (novo registro) abriu um painel sem formulário. Abriu depois
  de ativar o teste gratuito do Azure, que pediu telefone verificado, endereço de cobrança e cartão.
- Quem é estudante deve conferir se se enquadra no Azure for Students. A página dele diz que não pede cartão, que
  verifica pelo e-mail da escola e que não vale para cursos online abertos em massa nem para capacitações de
  organizações com fins lucrativos. Esta lição não afirma que a pessoa se enquadre.

**Sem diretório,** os passos 1 a 7 se fazem completos, e eles não precisam de nenhuma conta. Os passos 8 e 9
ficam para quando houver um.

**O projeto de início.** A partir da raiz do repositório da série:

```bash
git pull
cp lecciones/03-la-app-conoce-a-firebase/inicio/app/google-services.json lecciones/05-entrar-con-microsoft/inicio/app/
cd lecciones/05-entrar-con-microsoft/inicio
```

O segundo comando leva o arquivo da 03 para o projeto desta lição. Ali ele também fica ignorado pelo git.
Ao lado está `solucion/`: dá para lê-la sem configurar nada, e **`./verifica solucion` sai em vermelho em um clone
limpo**, porque sem o arquivo o projeto não compila (medido em 18/11/2026: 0 testes executados). As
respostas vão em `respuestas/05.md`, na raiz do repositório.

**O que o projeto já traz (andaime).** Não se avalia aqui; o passo 3 o percorre:

| O que traz | Onde | Por quê |
|---|---|---|
| Tudo o que a 04 deixou: `EntradaConGoogle`, as duas portas dela, os testes e os dublês | Os mesmos arquivos, sem uma única mudança | O `verifica` os compara, byte a byte, com a solução da 04 |
| Um quinto resultado, `CuentaConOtroProveedor`, e a interface `EntradaQueSeRetoma` | `Entrada.kt` | Com a Microsoft há uma forma nova de não entrar, e uma entrada que pode ficar pela metade |
| A terceira porta, com a implementação de verdade | `PuertaDelNavegador.kt` | Por ali o código sai para o navegador e para o Firebase |
| O segundo botão e a mensagem da colisão | `PantallaEntrada.kt`, `Tienda.kt` | Estes dois mudaram, **sim**, em relação à 04. Vêm prontos: não se mexe em nenhuma tela |
| `MainActivity`, que monta as peças e as passa a `Tienda` | `MainActivity.kt` | Vem pronto e não se muda: o `verifica` o compara com o da lição |
| Onde se decide qual peça se monta; por ora, uma que não faz nada | `Armado.kt` | É o arquivo onde se liga o que for escrito |
| Os testes desta lição, os dublês deles e um «app de testes» | `app/src/test`, `app/src/androidTest` | São os que o `verifica` roda. Não os altere |
| Um esqueleto de `EntradaConMicrosoft` | `EntradaConMicrosoft.kt` | É o arquivo que quem estuda escreve |

O catálogo de versões é o da 04, sem bibliotecas novas: a Microsoft vem no Firebase Authentication.

**O que esta lição não faz.** Não vincula o Google e a Microsoft em um mesmo usuário: aqui o app reconhece a
colisão e diz com qual botão entrar; vincular é a [06](06-dos-botones-una-cuenta.md). Não guarda a sessão nem
a encerra (lição 07), e não distingue a primeira vez (lição 08).

---

## Passo 1 · Sem IA: a mesma viagem, por outro caminho

Este passo é de quem estuda, sem Claude Code. É o exercício 5.3.

Na 04, o app pediu um token ao telefone e o levou ao Firebase. Com a Microsoft, o app não carrega nada:
abre o navegador e espera.

| # | De quem para quem | O que viaja |
|---|---|---|
| 1 | App → Firebase (a biblioteca dele) | Uma solicitação: «quero entrar com o provedor `microsoft.com`» |
| 2 | Firebase → o navegador | Abre a página da Microsoft, com o ID de cliente do registro e o endereço de retorno |
| 3 | A pessoa → Microsoft | A conta dela e o que a Microsoft pedir. Acontece no navegador, fora do app |
| 4 | Microsoft → Firebase (os servidores dele) | Um código de uso único, enviado ao endereço de retorno |
| 5 | Firebase → Microsoft | Esse código, junto com o ID de cliente **e a chave** guardada no console do Firebase |
| 6 | Firebase → app | Um usuário, com o identificador dele no projeto e, se o provedor o entregou, o nome |

A linha 5 é a que não existia com o Google. O guia do Firebase diz assim: o Firebase cuida de todo o fluxo e da
troca do código usando o ID de cliente e a chave configurados no console dele. E a documentação da
Microsoft explica por que essa chave não vai no app: pede que não seja usada em um app nativo, porque em um dispositivo
não dá para guardá-la de forma confiável.

Por isso a linha 2 exige exatidão. A documentação da Microsoft diz que o servidor dela só manda a pessoa para
um endereço que esteja adicionado ao registro do app, e que o endereço da solicitação deve coincidir
exatamente com um dos registrados. Se não coincidir, responde com um erro.

**Preveja** antes de seguir, em `respuestas/05.md`: das seis linhas, em quais participa o código que será
escrito por quem estuda?

**Faça a tabela.** Em `respuestas/05.md`, compare os dois provedores com estas cinco perguntas. Preencha-a sem
olhar «Compare as respostas» e depois compare:

1. Quem registra o app no provedor, e em qual console?
2. Qual peça mostra à pessoa onde entrar?
3. O que o app recebe do provedor?
4. Existe uma chave? Onde ela vive?
5. O que precisa coincidir exatamente para funcionar?

**O que comprova:** a tabela, comparada com a da lição (objetivo 5.3). Nenhum teste a lê.

---

## Passo 2 · A regra de dados, antes de mexer em qualquer coisa

Na 04 a regra foi: ninguém imprime, cola nem guarda o token. Aqui ela se estende a mais três coisas.

- **A chave de cliente nasce no Entra e é colada direto no Firebase.** Não passa pelo código, por um arquivo,
  por `respuestas/05.md`, pelo terminal, por uma captura de tela nem por uma mensagem. O Firebase a usa do lado do serviço dele, nunca no
  telefone. Dela só se anota a **data de expiração**.
- **O token de acesso da Microsoft não se lê.** O resultado do Firebase traz um, com o qual se poderia chamar
  serviços da Microsoft em nome da pessoa. Desse resultado só sai o nome.
- **O ID de cliente do Entra não vai no código.** Não é um segredo, mas o app não precisa dele: quem o usa
  é o Firebase. Na tabela do passo 8 ele, sim, é anotado.

Se a chave tocar um arquivo, o terminal ou uma captura, deixa de ser secreta: elimina-se no Entra e se cria
outra. Apagar o arquivo não basta. Ela se soma, com os tokens de acesso, à lista do que nunca se envia.

---

## Passo 3 · O que o projeto já traz

Abra estes arquivos; ainda não os edite. Aparecem três peças novas de Kotlin:

- **`String?`:** um tipo que termina em `?` aceita `null`. `String?` é «um texto, ou nada».
- **`a ?: b`:** se `a` não é `null`, devolve `a`; se é, devolve `b`.
- **`when { … }`:** uma lista de condições; fica com a primeira que for cumprida.

**`Entrada.kt`: duas coisas novas.** `Resultado` tem um quinto caso, `CuentaConOtroProveedor`. E há mais uma
interface:

```kotlin
interface EntradaQueSeRetoma : Entrada {
    suspend fun retomar(): Resultado?
}
```

É uma `Entrada` que, além de `entrar()`, sabe responder se ficou algo pela metade. Devolve um `Resultado`, ou
`null` se não ficou nada.

**`PuertaDelNavegador.kt`: a terceira porta.**

```kotlin
interface NavegadorDeProveedor {
    suspend fun pendiente(): String?
    suspend fun abrir(proveedor: OAuthProvider): String
}
```

`abrir` abre o navegador e devolve o nome de quem entrou; a versão de verdade dela chama
`startActivityForSignInWithProvider`. `pendiente` pergunta por uma entrada pela metade; a versão de verdade dela lê
`pendingAuthResult`. São as duas chamadas do guia do Firebase. Segundo a referência, esse resultado existe
quando se iniciou uma entrada pela web e o sistema limpou a tela que a esperava; traz o mesmo sucesso ou a
mesma exceção que a entrada teria dado; e, para uma entrada, é entregue **uma única vez**.

Esta porta só entrega um nome. É a regra de dados do passo 2 feita código: o token de acesso não sai
dali.

**`PantallaEntrada.kt` e `Tienda.kt`: o segundo botão.** `Tienda` recebe agora uma segunda entrada, opcional.
Se não a passarem, não há segundo botão e tudo é como na 04; por isso os testes da 04 continuam passando sem
mexer neles. E ao abrir pergunta se ficou algo pela metade:

```kotlin
val pendiente = entradaConMicrosoft?.retomar()
if (pendiente != null) recibe(pendiente)
```

Essas linhas vão dentro de um `LaunchedEffect`, que é a forma do Compose de dizer «faça isto uma vez ao
aparecer, não cada vez que a tela for redesenhada». Ele leva uma chave: se a chave mudar, o Compose
cancela o trabalho e o inicia de novo, segundo a documentação dele. Aqui a chave nunca muda, então a
pergunta é feita uma única vez, quando o app abre.

**`MainActivity.kt` e `Armado.kt`.** `onCreate` monta as peças e as passa a `Tienda`. Qual peça se monta
para a Microsoft quem decide é uma função de `Armado.kt`, que por ora devolve `MicrosoftSinConectar()`: não deixa
passar ninguém. Quem estuda muda `Armado.kt`; `MainActivity.kt` não se mexe.

**Os testes.** No computador, o Firebase não se prepara sozinho como em um telefone: sem ajuda, os testes
deram «Default FirebaseApp is not initialized» (medido em 18/11/2026). Por isso `src/test` traz `AppDePruebas`,
que o prepara à mão. Preparar não é conectar-se.

**Meça o ponto de partida.** A partir da raiz do repositório:

```bash
./lecciones/05-entrar-con-microsoft/verifica
```

Em 18/11/2026, com um arquivo inventado completo, a última linha foi
`Resultado: 23 de 44 pruebas en verde (21 en rojo, 0 con error, 0 saltadas)`. São 44 porque entram as 20 da
lição 04 e 24 novas. As 20 da 04 já estão em verde, e três das novas também: são do andaime.
As outras 21 esperam o código de quem estuda.

*(Em português: «Resultado: 23 de 44 testes em verde (21 em vermelho, 0 com erro, 0 pulados)».)*

---

## Passo 4 · Com o Claude Code: entrar e retomar

Aqui entra a ferramenta, e entra sempre do mesmo jeito: **prever → pedir → ler o que ele gerou → rodar o
`verifica` → explicar com palavras próprias.**

1. **Preveja.** Escreva em `respuestas/05.md`: se ao abrir o app não ficou nenhuma entrada pela metade, o que `retomar()`
   deve devolver, e o que deve acontecer na tela?
2. **Peça** ao Claude Code, a partir de `inicio/`, a segunda entrada tomando a primeira como referência. Este
   é o exercício 5.4, e o pedido é curto de propósito: «Tome `EntradaConGoogle.kt` como referência e escreva
   `EntradaConMicrosoft.kt` só para o caso em que tudo dá certo, com `retomar()` e `entrar()`, usando a
   porta `navegador`. Não capture exceções ainda. Em `Armado.kt`, mude só o corpo de
   `armarEntradaConMicrosoft` para que devolva `EntradaConMicrosoft` com a porta que recebe, e apague ali
   `MicrosoftSinConectar`. Não altere nenhum outro arquivo.»
3. **Leia** a mudança completa antes de aceitá-la e **liste o que ele não copiou bem.** A primeira entrada é uma
   boa referência para o estilo e uma má para o fundo: o Google entrega um token e a Microsoft não. Responda
   sim ou não às oito perguntas de «Compare as respostas», exercício 5.4, e anote em `respuestas/05.md`
   cada «não» com o arquivo e a linha. Depois peça a correção de cada um, dizendo por quê. Se as oito
   derem «sim», escreva «No encontré desvíos en las ocho revisiones» (não encontrei desvios nas oito revisões) e conserve a tabela: não invente um erro.
   O centro deve ficar parecido com isto:

```kotlin
override suspend fun retomar(): Resultado? {
    val nombre = navegador.pendiente()
    return if (nombre == null) null else Resultado.Entro(nombre)
}

override suspend fun entrar(): Resultado {
    // Linhas 1 e 2: a solicitação, com o provedor.
    val proveedor = OAuthProvider.newBuilder("microsoft.com")
        .addCustomParameter("prompt", "select_account")
        .build()
    // Primeiro, o pendente. Só se não houver (null) o navegador é aberto.
    val nombre = navegador.pendiente() ?: navegador.abrir(proveedor)
    return Resultado.Entro(nombre)
}
```

4. **Rode o `verifica`.** Com esse código deu `Resultado: 32 de 44 pruebas en verde` em 18/11/2026. As vermelhas
   são as formas de não entrar: o passo 5.
5. **Explique**, em três linhas de `respuestas/05.md`: por que `entrar()` pergunta pelo pendente se
   `retomar()` já o fez ao abrir o app.

*(Em português: «Resultado: 32 de 44 testes em verde».)*

**O que faz `select_account`?** Segundo a documentação da Microsoft, interrompe o início de sessão automático
e mostra a lista de contas para escolher. A Microsoft o marca como opcional: esta lição o recomenda, e é
o valor que o ensaio de 13/11/2026 usou. Não é condição para passar: nenhum teste lê esse parâmetro, e
se faltar, o `verifica` só avisa.

**O que comprova, e até onde.** No nível simulado, o `verifica` comprova que um toque abre o navegador
uma única vez e com o provedor `microsoft.com`; que, com algo pela metade, ele não se abre de novo; e que `retomar()`
devolve quem entrou ou `null`. Outro teste abre `MainActivity`, passa a porta de mentira a
`crearEntradaConMicrosoft` e lê qual provedor foi pedido.

A porta era de mentira: nenhum navegador foi aberto. «Ficou algo pela metade» foi o dublê que disse; nenhum teste
fechou o app.

**E o app instalado usa a peça de quem estuda?** Nenhum teste vê isso de ponta a ponta. Há dois respaldos:

- `MainActivity.kt` tem de ser idêntico ao da lição. Se for mudado, o `verifica` o marca em vermelho
  mesmo que os 44 testes deem verde (medido em 18/11/2026).
- Se `Armado.kt` montar outra coisa, um teste fica vermelho (43 de 44).

As marcas «Texto:» só procuram palavras nos arquivos. A prova completa é o nível real, no passo 9.

---

## Passo 5 · Sem IA: as três formas de não entrar

Este passo se faz **sem Claude Code**. É uma função de três ramos, escrita por quem estuda.

Quando a porta do navegador não consegue entregar um nome, lança uma exceção do Firebase. A referência do
Firebase lista as desta chamada. Três grupos importam aqui:

| Exceção | Quando, segundo a referência | A entrada responde |
|---|---|---|
| `FirebaseAuthUserCollisionException` com o código `ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL` | Esse e-mail já tem usuário com outra forma de entrar. Só acontece com a opção «uma conta por e-mail» ativada (lição 06) | `Resultado.CuentaConOtroProveedor` |
| `FirebaseAuthWebException` com o código `ERROR_WEB_CONTEXT_CANCELED` | A pessoa cancelou a operação pendente | `Resultado.Cancelada` |
| Qualquer outra, inclusive essas duas classes com outro código | A referência nomeia outra operação web em andamento, um navegador sem suporte, uma falha dentro da página e o provedor mal configurado. Aqui entra também tudo o mais que falhar | `Resultado.Fallo` |

No ensaio de 13/11/2026 entrou-se com uma conta da Microsoft cujo
e-mail já tinha entrado com o Google, e o Firebase respondeu com essa exceção e este texto: «An account already
exists with the same email address but different sign-in credentials».

A classe não basta. A mesma exceção traz outros dois códigos, e a referência pede que se confira o código para
saber a causa: só este quer dizer «entre com o outro botão». Outra colisão é mostrada como falha, sem
inventar uma causa.

**1. Preveja.** As duas primeiras linhas nomeiam classes distintas. Se na segunda a condição do
código for retirada e ficar só «é uma `FirebaseAuthWebException`», qual mensagem verá uma pessoa para quem falhou a
página, sem ter cancelado nada?

**2. Escreva.** Acrescente esta função dentro da classe:

```kotlin
private fun resultadoDe(falla: Exception): Resultado = when {
    falla is FirebaseAuthUserCollisionException &&
        falla.errorCode == "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" ->
        Resultado.CuentaConOtroProveedor
    falla is FirebaseAuthWebException && falla.errorCode == "ERROR_WEB_CONTEXT_CANCELED" ->
        Resultado.Cancelada
    else -> Resultado.Fallo
}
```

E envolva o corpo de `retomar()` e o de `entrar()` com `try { … }` e estes dois `catch`, nesta ordem:

```kotlin
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    resultadoDe(e)
}
```

O primeiro é o da 04: `CancellationException` não é uma falha da entrada, é o Kotlin avisando que a
tarefa foi cancelada, e se deixa passar. `retomar()` leva os mesmos `catch` porque, segundo a referência, o
resultado pendente traz também as exceções com que a entrada teria terminado.

**3. Rode o `verifica`.** Agora a linha é `Resultado: 44 de 44 pruebas en verde`. Fica uma marca em vermelho,
a de `respuestas/05.md`: ela se preenche no passo 6.

**4. Confira a previsão.** Retire `&& falla.errorCode == "ERROR_WEB_CONTEXT_CANCELED"` e rode o
`verifica`: ficam vermelhos mais dois testes, os dois do caso «erro» (medido em 18/11/2026 sobre a
solução: 42 de 44). Devolva-o. Se o que se retirar for a condição do código da colisão, fica vermelho
um (43 de 44).

Os textos que a pessoa vê já estão em `mensajeDe`:

| Resultado | Mensagem |
|---|---|
| `Cancelada` | «Cerraste la ventana sin elegir cuenta. Toca el botón cuando quieras intentar otra vez.» *(Fechou-se a janela sem escolher uma conta. Toque no botão quando quiser tentar outra vez.)* |
| `Fallo` | «No se pudo entrar. Revisa tu conexión y vuelve a intentar.» *(Não foi possível entrar. Confira a conexão e tente de novo.)* |
| `CuentaConOtroProveedor` | «Ese correo ya entró a la tienda con otro botón. Entra con el que usaste la primera vez.» *(Esse e-mail já entrou na loja com outro botão. Entre com o que foi usado da primeira vez.)* |

Os dois primeiros são os da 04. O terceiro não diz «Google» de propósito: a exceção não traz com qual
provedor o usuário foi criado.

**O que comprova:** o `verifica` simulado, os três casos. Cada caso leva testes de dois tipos: uns lançam
a exceção contra a classe e leem o que ela respondeu; outro desenha a tela com a classe e procura a mensagem dela.
As exceções foram montadas por um dublê, com códigos escritos à mão: nenhuma veio do Firebase. Qual exceção dá em
um telefone real fechar a aba do navegador, isso ainda não medimos com uma conta.

---

## Passo 6 · Sem IA: quebre uma de propósito

Também **sem Claude Code**. Vai-se provocar o final ruim da cena.

**1. Preveja.** Vai-se fazer `retomar()` responder sempre `null`, como se nunca ficasse nada pela metade.
Compila? O botão da Microsoft continua funcionando? Quantos testes ficam vermelhos?

**2. Quebre e meça.** Em `EntradaConMicrosoft.kt`, troque as duas linhas do corpo de `retomar()` por um
`null` puro, dentro do `try`. Rode o `verifica`. Isto foi o que deu em 18/11/2026 (recortado):

```text
❌ Tu entrada no retoma: quedó una entrada a medias y retomar() no la recogió (paso 4)
❌ Retomar: 1 de 4 pruebas en verde. Revisa retomar(), que entrar() pregunte primero por lo pendiente, y …
❌ Al abrir la app: 0 de 3 pruebas en verde. Tienda.kt viene dado y llama a retomar(): revisa lo que …
Resultado: 37 de 44 pruebas en verde (7 en rojo, 0 con error, 0 saltadas), leído de …
```

*(Essa é a saída do `verifica`, em espanhol, como o script a imprime. Em português: a entrada não retoma, ficou
uma entrada pela metade e `retomar()` não a recolheu (passo 4); Retomar: 1 de 4 testes em verde, confira `retomar()` e
que `entrar()` pergunte primeiro pelo pendente; Ao abrir o app: 0 de 3 testes em verde, `Tienda.kt` vem pronto e chama
`retomar()`, confira o que … Resultado: 37 de 44 testes em verde.)*

Copie para `respuestas/05.md` a linha que diz «Tu entrada no retoma».

**3. O que esses testes não dizem.** Compila, e os testes do botão continuam em verde: tocar em «Entrar con
Microsoft» leva ao início do mesmo jeito. O defeito só existe se o Android fecha o app no meio da entrada, e isso não acontece
em um teste que dura um segundo. Em um telefone isso foi medido à parte, em 13/11/2026, com um código de ensaio:

- Com a página da Microsoft à frente, fechou-se o processo do app com `adb shell am kill` e depois se escolheu
  a conta: o Android iniciou o app de novo e o resultado pendente entregou a entrada.
- A opção de desenvolvedor «Não manter atividades» **não** serviu para testá-lo: destrói a tela, mas
  o processo continua vivo e termina a entrada por conta própria. Para ver o caso é preciso fechar o processo, não a
  tela.

**4. Conserte.** Devolva as duas linhas de `retomar()`. Rode o `verifica`: 44 de 44, e agora também a
marca de `respuestas/05.md`.

O exercício está concluído quando as três caixas podem ser marcadas:

- [ ] A previsão do ponto 1 está escrita, com o que se viu embaixo.
- [ ] Foi colada a linha do vermelho.
- [ ] Foi dito por que os testes do botão continuaram em verde com um código que, em um telefone com pouca
      memória, faria a pessoa entrar duas vezes.

**O que comprova:** «Quebre uma de propósito». É a base do objetivo 5.2: saber o que `retomar()` recolhe.

---

## Passo 7 · Dentro do Android: o nível instrumentado

Com o emulador aberto, a partir da raiz do repositório:

```bash
./lecciones/05-entrar-con-microsoft/verifica inicio instrumentado
```

Repete o nível simulado e depois instala o app e o app de testes dele no dispositivo. Ali desenha `Tienda` com
a `EntradaConMicrosoft` de quem estuda e a mesma porta de mentira, toca nos botões e procura os textos. Com a solução, em
um emulador novo e sem conta (Pixel 9, imagem `google_apis_playstore` do Android 37), isto deu em 18/11/2026
(recortado):

```text
✅ Instrumentado: dentro de Android, con la puerta de mentira, tocar «Entrar con Microsoft» lleva al inicio …
✅ Instrumentado: dentro de Android, cancelación, error y colisión muestran cada uno su mensaje sin pasar …
✅ Instrumentado: dentro de Android, con una entrada a medias que contesta el doble, la app llega al inicio …
✅ Instrumentado: dentro de Android, las 4 pruebas del botón de Google de la lección 04 siguen en verde
Resultado: 44 de 44 pruebas en verde (0 en rojo, 0 con error, 0 saltadas), leído de …
Instrumentado: 9 de 9 pruebas en verde (0 en rojo, 0 con error, 0 saltadas), leído de …
```

*(Em português: dentro do Android, com a porta de mentira, tocar «Entrar con Microsoft» leva ao início; dentro do
Android, cancelamento, erro e colisão mostram cada um a mensagem correspondente sem passar …; dentro do Android, com uma entrada
pela metade que o dublê responde, o app chega ao início …; dentro do Android, os 4 testes do botão do Google da lição
04 continuam em verde. Resultado: 44 de 44 testes em verde. Instrumentado: 9 de 9 testes em verde.)*

São nove: cinco desta lição e as quatro da 04. Sobre o projeto de início sem mexer deu 4 de 9, as
quatro da 04. Sem nenhum dispositivo conectado, o `verifica` sai em vermelho e diz que não rodou.

**O que comprova, e até onde.** Comprova que as telas, o toque e a classe de quem estuda funcionam dentro do Android,
com a biblioteca do Firebase Authentication do dispositivo montando o provedor. **Não** comprova nada do
registro no Entra nem do projeto: a porta continua de mentira, o navegador não se abre e não é preciso
conta nem rede até a Microsoft. E o teste de «retomar» é o dublê respondendo que ficou algo pela metade: o Android
não fechou nenhum app.

**Veja o botão de verdade, sem registro.** Nesse mesmo emulador, com um arquivo inventado, o app foi instalado e
tocou-se em «Entrar con Microsoft» (18/11/2026). Nenhuma página se abriu. O app mostrou «No se pudo entrar.
Revisa tu conexión y vuelve a intentar.» (não foi possível entrar; confira a conexão e tente de novo), e o registro do sistema disse
`[GetAuthDomainTask] Error getting project config. Failed with API key not valid`. É o esperado: o arquivo
era inventado. Com o arquivo de verdade e a Microsoft ainda sem habilitar no Firebase, o resultado ainda não o
medimos; se aparecer, conte-nos qual mensagem deu.

---

## Passo 8 · Nos consoles: registrar o app sem que a chave toque em um arquivo

Este passo e o seguinte pedem o diretório de «Antes de começar». Aqui não se escreve código. Abra os dois
consoles em duas abas, porque três dados vão cruzar de uma para a outra:

| Dado | Nasce em | É colado em |
|---|---|---|
| O endereço de retorno | Firebase | Entra |
| O *Application (client) ID* | Entra | Firebase |
| A chave de cliente | Entra | Firebase, e em nenhum outro lugar |

Os menus e botões ficam em inglês, como na documentação. Em espanhol, o ensaio de 13/11/2026 viu «Registros de aplicaciones», «Nuevo registro» e «Registrar una
aplicación» (na console em espanhol). Os demais rótulos em espanhol não os percorremos de ponta a ponta: se o console disser outra coisa,
oriente-se pela versão em inglês de cada um e conte-nos como aparece.

**1. No Firebase, copie o endereço de retorno.** Em Authentication, aba *Sign-in method*, escolha a Microsoft
e habilite-a. Ainda não salve: o painel pede o ID e a chave, que ainda não existem. Deixe essa aba aberta
assim, pela metade; volta-se a ela no ponto 5. No ensaio, o endereço foi copiado desse mesmo painel. Tem esta forma:

```text
https://<tu-proyecto>.firebaseapp.com/__/auth/handler
```

O guia do Firebase sobre redirecionamentos diz que o final `/__/auth/handler` importa.

**2. No Entra, entre por `entra.microsoft.com`.** Não por `portal.azure.com`: no ensaio, a conta sem
assinatura deu ali o erro `AADSTS16000`, e pelo Entra abriu o próprio diretório.

**3. Registre o app.** Segundo o guia da Microsoft: *Entra ID* › *App registrations* › *New registration*.

- **Name:** um que seja reconhecível. O guia avisa que as pessoas que entrarem podem vê-lo.
- **Supported account types:** *Any Entra ID Tenant + Personal Microsoft accounts*, para contas de
  organizações e pessoais. É a que o ensaio usou.
- *Register*.

A página *Overview* mostra o **Application (client) ID**. Esse, sim, se anota, e já dá para colá-lo na
aba do Firebase. Não é a chave: a chave ainda não existe.

**4. Adicione o endereço de retorno.** Segundo o guia: *Manage* › *Authentication* › *Add Redirect URI* ›
plataforma **Web** › cole o endereço do ponto 1 › *Configure*. Cole-o; não o digite à mão. A Microsoft pede
que comece com `https` e distingue maiúsculas de minúsculas.

**5. Crie a chave.** Segundo o guia: *Certificates & secrets* › *Client secrets* › *New client secret*. Dê
uma descrição e escolha uma expiração. O guia diz que não pode passar de 24 meses e recomenda menos de 12.
*Add*.

A chave aparece na coluna **Value**, e o guia avisa que ela não volta a ser mostrada depois de sair da
página. Com essa página aberta:

- Copie-a com o botão de copiar e cole-a **direto** no campo da chave do painel da Microsoft, na
  aba do Firebase. Confira se o *Application (client) ID* do ponto 3 já está no campo dele. *Save*.
- Não a cole em nenhum outro lugar. Depois copie qualquer outro texto, para tirá-la da área de transferência
  (recomendação desta série).
- Anote a **data de expiração**. Quando ela chegar, a entrada com a Microsoft deixa de funcionar até que se crie
  outra chave e se a leve ao Firebase.
- Não tire nenhuma captura de tela enquanto a coluna *Value* mostrar a chave.

Um limite, para não tirar daqui uma arquitetura: o Firebase pede esse ID e essa chave para este provedor, e
o mesmo guia da Microsoft recomenda certificados em vez de chaves antes de levar um app à produção. Para
um produto real, isso se revisa com quem cuida da segurança: a política do Entra, a rotação da chave e
o tipo de credencial.

**6. Deixe a evidência.** Em `respuestas/05.md`:

| Dado | O que se anota |
|---|---|
| *Application (client) ID* | Sim, completo |
| Tipo de contas | Sim, o nome da opção |
| Endereço de retorno | Sim, completo |
| Expiração da chave | Sim, a data |
| Valor da chave | **Nunca** |

E uma captura de *Certificates & secrets* tirada **depois** de sair dessa página e voltar a entrar. Se na
captura ficar à vista qualquer parte do valor, cubra-a antes de guardá-la.

**7. Peça ao `verifica` que procure.** Rode-o: as duas marcas «Clave:» devem estar em verde. Para saber se
essa busca enxerga mesmo, semeie uma chave **inventada**. Este comando a monta por partes, para que não fique escrita
em lugar nenhum, e a acrescenta como comentário ao final de um arquivo:

```bash
printf '// %s%s%s\n' 'Inv8' 'Q~' "$(printf 'x%.0s' $(seq 34))" >> lecciones/05-entrar-con-microsoft/inicio/app/src/main/java/mx/habil/ejemplo/tienda/Armado.kt
```

Rode o `verifica`. Em 18/11/2026, sobre o projeto de início sem mexer, ele respondeu com o arquivo e o número
da linha, sem imprimir o texto (o número da linha pode ser outro):

```text
❌ Hay un texto con forma de clave de cliente de Entra en: inicio/app/src/main/java/…/Armado.kt:19. No la imprimo. …
```

*(Em português: há um texto com forma de chave de cliente do Entra em …/Armado.kt:19. Não a imprimo.)*

Apague essa última linha no editor e rode o `verifica` de novo: verde.

**O que comprova, e até onde.** As marcas «Clave:» comprovam que nenhum arquivo de texto de `inicio/` nem
`respuestas/05.md` traz um texto com a forma dessas chaves nem nomeia um segredo de cliente. A forma é
inspirada em uma regra pública do gitleaks, mas o `verifica` não executa o gitleaks nem substitui uma varredura de
segredos: é mais simples, pode marcar texto inocente, e deixa escapar outras formas, os binários, as variáveis
de ambiente e o histórico do git. Também não vê o terminal, as capturas nem a área de transferência. Essa parte
é cuidada por quem estuda, e a tabela e a captura são comparadas por quem estuda.

---

## Passo 9 · No telefone: o nível real

Este passo fala, sim, com a Microsoft, uma vez, à mão. É o único que comprova o passo 8: que o endereço de
retorno é o exato, que o ID é o do registro e que a chave guardada é a vigente.

Com o emulador aberto, a partir de `inicio/`:

```bash
./gradlew installDebug
```

Abra «A loja» («La tienda»), toque em «Entrar» e depois em «Entrar com a Microsoft» («Entrar con Microsoft»).

**O que se viu no ensaio de 13/11/2026** (Mac, emulador, contas pessoais). Esse ensaio usou um código de
teste com as mesmas chamadas, não o de `solucion/`:

| Caso | O que se viu |
|---|---|
| Primeira vez | O navegador abriu com a página da Microsoft; digitou-se a conta; o app cumprimentou com o nome |
| A permissão | Uma tela «Let this app access your info?», com a marca **unverified** junto ao nome do app, «Read your profile» e os botões *Deny* e *Accept*. É o esperado em um app de exemplo sem editor verificado. A Microsoft guardou a aceitação: não voltou a aparecer |
| Entrar de novo | A Microsoft mostrou a conta já iniciada, para escolhê-la sem senha |
| Um e-mail que já entrou com o Google | A exceção de colisão do passo 5 |
| O app fechado no meio da entrada | O que o passo 6 diz: o resultado pendente entregou a entrada |

A classe `NavegadorConFirebase` desta lição só a rodamos até o erro do arquivo inventado do passo 7;
a entrada completa com uma conta real ainda não. Se ao testar aparecer algo diferente, conte-nos o que apareceu.

**Se a conta usa chave de acesso (passkey).** No ensaio, a tela «Sign in with your passkey» ficou
em «Verifying…» no emulador. Tocou-se em *Cancel* e se entrou com a senha.

**Se o navegador oferecer guardar a senha:** em um equipamento que não é de quem estuda, «Não».

**Teste o final da cena.** Com a página da Microsoft à frente e antes de escolher a conta, em outro
terminal:

```bash
adb shell am kill mx.habil.ejemplo.tienda
adb shell pidof mx.habil.ejemplo.tienda
```

O primeiro fecha os processos do app que é seguro fechar, segundo a documentação do `adb`. O segundo não
deve imprimir nada: o processo já não existe. Agora escolha a conta. O app deve iniciar e cumprimentar a pessoa sem que
ela toque no botão de novo.

**Ao terminar, em um equipamento que não é de quem estuda:** encerre a sessão da Microsoft no navegador do emulador e apague
os dados dele (lição 00).

**O que comprova:** a entrada real comprova que o registro, o endereço de retorno, o ID e a chave estão
certos. Não comprova os casos de não entrar: esses o nível simulado cobre.

---

## Os exercícios

Os passos 1, 5 e 6 se fazem **sem IA**.

| # | Faz-se | Está pronto quando |
|---|---|---|
| 5.1 | Registrar o app no Entra com o endereço de retorno exato e levar o ID e a chave ao Firebase | Três evidências. A tabela do passo 8 e a captura sem a chave à vista: quem compara é quem estuda. As duas marcas «Clave:» do `verifica` em verde, que só dizem que nenhum **arquivo** de `inicio/` nem `respuestas/05.md` traz essa forma ou esse nome. E a entrada real do passo 9, que é a única coisa que valida o registro |
| 5.2 | Implementar `EntradaConMicrosoft` sem mudar as telas, e retomar o resultado pendente | Três evidências. Simulado: as marcas dele no `verifica` em verde; valida o que a classe pede e o que responde, com uma porta de mentira, e que os 20 testes da 04 continuam em verde com os arquivos idênticos. **Não** comprova por si só que o app instalado use a entrada de quem estuda: isso o respaldam `MainActivity.kt` idêntico ao da lição e o teste do que `Armado.kt` monta. Instrumentado: 9 de 9, o mesmo dentro do Android. Real: entra-se no passo 9 e fecha-se o processo no meio da entrada; é a única coisa que valida que o Android de fato devolve a pessoa ao app |
| 5.3 | Comparar o Google e a Microsoft com as cinco perguntas do passo 1 | A tabela coincide com a de «Compare as respostas». Quem compara é quem estuda |
| 5.4 | Pedir ao Claude Code a segunda entrada tomando a primeira como referência, e listar o que ele não copiou bem | A lista de sim e não do passo 4, com arquivo e linha em cada «não», contra as oito perguntas de «Compare as respostas». Se as oito derem «sim», a entrega é essa tabela e a frase «No encontré desvíos en las ocho revisiones». Quem a avalia é quem estuda; o `verifica` não a lê |

**Tempo:** calcule umas quatro horas, com o registro no Entra, o «Quebre uma de propósito», o nível
instrumentado e a entrada real; sem os passos 8 e 9, umas duas horas e meia. É uma estimativa: será ajustada com o tempo que a
primeira turma levar. Conseguir o diretório não está contado: no ensaio, a assinatura de teste levou
uns 15 minutos para aparecer como ativa.

---

## Não roda?

| O que aparece | Causa provável | O que fazer |
|---|---|---|
| `File google-services.json is missing.` | O arquivo não foi copiado para `inicio/app/` | O `cp` de «Antes de começar». Sobre `solucion/` é o esperado em um clone limpo |
| ❌ «Estos archivos ya no son los de la lección 04» (estes arquivos já não são os da lição 04) | Alguém, quem estuda ou o Claude Code, mudou um teste, uma porta ou uma tela da 04 | `git diff -- <arquivo>` e desfazer à mão |
| ❌ «Tu entrada no retoma» (a entrada não retoma) | `retomar()` não usa o que `pendiente()` devolve | Passo 4. Se vem do «Quebre uma de propósito», passo 6, ponto 4 |
| ❌ O caso «erro» em vermelho, com `resultadoDe` escrita | Toda `FirebaseAuthWebException` está sendo contada como cancelamento | Passo 5: falta a condição do código |
| ❌ «Hay un texto con forma de clave de cliente de Entra» (há um texto com forma de chave de cliente do Entra) | Uma chave, ou algo com a forma dela, está em um arquivo | Se for a chave de quem estuda: apagá-la do arquivo, eliminá-la no Entra e criar outra (passo 8). Se for a inventada do controle: apagar essa linha |
| Nos testes, `Default FirebaseApp is not initialized` | Mudou-se `robolectric.properties` ou `AppDePruebas.kt` (medido em 18/11/2026 sem eles) | Devolvê-los: são andaime |
| Nos testes instrumentados, `NoSuchMethodException: android.hardware.input.InputManager.getInstance` | Retirou-se a linha do Espresso 3.7.0 e ficou o que o Compose traz por padrão. Medido em 18/11/2026 no Android 37: 0 de 9 | Devolver `androidTestImplementation(libs.espresso.core)` |
| No telefone, «No se pudo entrar…» sem que nenhuma página se abra | Com um arquivo inventado, o registro disse `API key not valid` (medido em 18/11/2026). Com o arquivo de verdade, o que dá com a Microsoft sem habilitar ou com a chave vencida ainda não medimos | Conferir se o `google-services.json` próprio foi copiado e o passo 8 |
| No navegador, um erro `AADSTS50011` | Segundo a Microsoft: o endereço de retorno da solicitação não coincide com nenhum dos registrados. Não foi medido aqui | Passo 8, ponto 4: colá-lo de novo, completo |
| Em `portal.azure.com`, `AADSTS16000` | Medido em 13/11/2026 com uma conta sem assinatura | Entrar por `entra.microsoft.com` |
| «Nuevo registro» abre um painel sem formulário | A conta não tem diretório (medido em 13/11/2026 com uma conta recém-criada) | «Antes de começar»: é preciso ter um diretório primeiro |
| «Ese correo ya entró a la tienda con otro botón…» (esse e-mail já entrou na loja com outro botão) | Esse e-mail já tem usuário no projeto, criado com outro provedor; nesta série, o Google | Entrar com o Google. Que os dois botões cheguem ao mesmo usuário é a lição 06 |
| A tela da chave de acesso fica em «Verifying…» | Medido em 13/11/2026 no emulador | *Cancel* e entrar com a senha |
| ❌ «EntradaConMicrosoft.kt nombra una puerta concreta» (EntradaConMicrosoft.kt nomeia uma porta concreta) | O código nomeia a porta de verdade (`NavegadorConFirebase`) ou um dublê dos testes. Os testes só lhe passam dublês: se o código pergunta qual lhe coube, o verde não diz o que ele faz com a de verdade | Que o código fale só com `NavegadorDeProveedor`. A porta de verdade é escolhida pela `MainActivity.kt`, que vem dada (passo 4) |

No Linux Mint 22.3 (x86_64), o `verifica` desta lição saiu igual ao do Mac (18/11/2026), no nível
simulado e com um arquivo de teste que trazia a impressão digital desse computador: sobre `inicio/`, 23 de 44 em verde;
sobre `solucion/`, 44 de 44. O nível instrumentado não foi medido no Linux.

---

## Para fechar

**Responda sem reler:**

1. Onde vive a chave de cliente, quem a usa e por que não vai no app?
2. O que precisa coincidir exatamente entre o Firebase e o Entra?
3. O `verifica` dá 44 de 44 e as marcas «Clave:» estão em verde. Diga uma coisa que isso comprova e duas que não.
4. Por que «Não manter atividades» não serve para testar `retomar()`?

**O que vem a seguir:** na [06](06-dos-botones-una-cuenta.md), a pessoa com o mesmo e-mail nos dois
provedores termina com um único usuário, e só se ela o confirmar.

---

## Compare as respostas

**Passo 1.** O código de quem estuda participa na linha 1, quando monta a solicitação, e recebe a linha 6. As linhas 2 a 5
acontecem entre o Firebase, o navegador e a Microsoft. A tabela:

| Pergunta | Google (lição 04) | Microsoft (esta lição) |
|---|---|---|
| Quem registra o app, e onde? | Quem estuda registra o app no Firebase e adiciona a impressão digital; os clientes do Google não são cadastrados à mão: chegam no `google-services.json` depois de habilitar o Google (lição 03) | Quem estuda, à mão, no console do Entra, que fica fora do Firebase |
| O que mostra à pessoa onde entrar? | O Credential Manager, com a janela de contas do Android | O navegador, com a página da Microsoft |
| O que o app recebe do provedor? | Um token de identidade, que o próprio app leva ao Firebase | Nada: o código viaja da Microsoft ao Firebase. O app recebe do Firebase o usuário |
| Existe uma chave? Onde ela vive? | Quem estuda não cuida de nenhuma: no app só vai o ID de cliente web, que não é segredo | Sim, a chave de cliente. Cria-se no Entra e vive no console do Firebase |
| O que precisa coincidir? | O nome de pacote e a impressão digital | O endereço de retorno, caractere por caractere |

**O que é igual é o padrão:** uma `Entrada` com a porta dela, um `Resultado` para cada forma de terminar, os
testes com um dublê, e o Firebase entregando no final um usuário. As telas não souberam que havia um
provedor novo.

**Passo 4.** `retomar()` devolve `null` e a tela fica na boas-vindas, sem mensagem. `entrar()` pergunta
de novo porque é o que o guia do Firebase pede antes de abrir outro fluxo: quem toca no botão não deve entrar duas vezes se já houver uma resposta esperando.

**Exercício 5.4.** Oito perguntas de sim ou não sobre o que o Claude Code gerou. O esperado são oito «sim»; cada
«não» é algo que ele não copiou bem e vai na lista com arquivo e linha. A lista diz o que revisar, não o que vai
falhar: não se mediu o que a ferramenta gera.

| # | Pergunta | Por quê |
|---|---|---|
| 1 | O provedor é o texto `"microsoft.com"`, sem `https://`? | Assim o guia do Firebase o escreve |
| 2 | Deixou de fora o que era do Google: `GetCredentialRequest`, `GetSignInWithGoogleOption`, `GoogleIdTokenCredential`? | Aqui não há token para pedir ao telefone |
| 3 | A classe recebe só a porta `navegador`, sem `idDeClienteWeb` nem outro identificador? | O ID e a chave vivem nos consoles |
| 4 | `entrar()` pergunta `pendiente()` antes de `abrir`? | É o que o guia pede, para não fazer entrar duas vezes |
| 5 | `retomar()` nunca chama `abrir`? | É chamado quando o app abre, sem que ninguém toque em um botão |
| 6 | Não há escrito nenhum ID de cliente, nenhuma chave nem o identificador de um diretório? | Regra de dados do passo 2. O parâmetro `tenant` com o valor `common` não é um dado de quem estuda: é o valor padrão |
| 7 | Ninguém nomeia `accessToken`, `idToken` nem `OAuthCredential`? | Regra de dados: do Firebase só sai o nome |
| 8 | Ficaram intocados os testes, as portas, as telas e o `google-services.json`? | O `verifica` compara esses arquivos |

**Passo 5.** Veria o do cancelamento, «Cerraste la ventana…» («Fechou-se a janela…»), embora não tenha fechado nada. Sem a condição, toda
falha do navegador é contada como cancelamento.

**Passo 6.** Compila, e o botão continua funcionando. Ficam vermelhos sete testes: os de `retomar()` e os três
que abrem o app com algo pela metade. Os do botão continuam em verde porque nenhum fecha o app: neles, o
navegador de mentira responde na hora.

**Para fechar:**

1. Nasce no Entra e vive no console do Firebase. Quem a usa são os servidores do Firebase, para trocar o código da Microsoft pelos
   dados da pessoa. Não vai no app porque qualquer pessoa que tenha o app instalado poderia tirá-la: a Microsoft
   pede que não seja usada em apps nativos.
2. O endereço de retorno: o que o Firebase usa e o que foi registrado no Entra.
3. Comprova o que a classe pede e o que responde em cada caso, com uma porta de mentira, e que nenhum arquivo
   revisado traz um texto com forma de chave. Não comprova que o registro no Entra funcione, nem que a chave não
   esteja no terminal, em uma captura ou no histórico do git.
4. Porque destrói a tela mas deixa vivo o processo, que termina a entrada por conta própria. `retomar()`
   existe para quando o processo já não está.

---

## Fontes da lição

Abertas em 18/11/2026.

- Firebase. *Authenticate Using Microsoft on Android* (o provedor, o resultado pendente, a entrada e a troca do código com a chave do console). https://firebase.google.com/docs/auth/android/microsoft-oauth
- Firebase. Referência de `FirebaseAuth` (`getPendingAuthResult` e as exceções de `startActivityForSignInWithProvider`). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth
- Firebase. Referência de `FirebaseAuthUserCollisionException` (os três códigos; `ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL` só com «uma conta por e-mail», que ela recomenda; conferir o código). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuthUserCollisionException
- Firebase. Referência de `FirebaseAuthWebException` (os quatro códigos; `ERROR_WEB_CONTEXT_CANCELED`). https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuthWebException
- Firebase. *Best practices for using signInWithRedirect* (o endereço `https://<project>.firebaseapp.com/__/auth/handler`). https://firebase.google.com/docs/auth/web/redirect-best-practices
- Microsoft Learn. *How to register an app in Microsoft Entra ID* (requisitos, passos e tipos de conta). https://learn.microsoft.com/en-us/entra/identity-platform/quickstart-register-app
- Microsoft Learn. *How to add a redirect URI to your application* (os passos e a plataforma Web). https://learn.microsoft.com/en-us/entra/identity-platform/how-to-add-redirect-uri
- Microsoft Learn. *Redirect URI (reply URL) best practices and limitations* (endereços registrados, `https`, maiúsculas e `AADSTS50011`). https://learn.microsoft.com/en-us/entra/identity-platform/reply-url
- Microsoft Learn. *Add and manage app credentials in Microsoft Entra ID* (a chave: 24 meses no máximo e um valor que não volta a ser mostrado). https://learn.microsoft.com/en-us/entra/identity-platform/how-to-add-credentials
- Microsoft Learn. *Microsoft identity platform and OAuth 2.0 authorization code flow* (`select_account`, a coincidência exata e a chave fora dos apps nativos). https://learn.microsoft.com/en-us/entra/identity-platform/v2-oauth2-auth-code-flow
- Microsoft Azure. *Azure for Students* (condições da oferta). https://azure.microsoft.com/en-us/pricing/offers/ms-azr-0170p
- Android Developers. *Side-effects in Compose* (`LaunchedEffect`). https://developer.android.com/develop/ui/compose/side-effects
- Android Developers. *Android Debug Bridge* (`am kill`). https://developer.android.com/tools/adb
- Kotlin. *Null safety* (os tipos com `?` e o operador `?:`). https://kotlinlang.org/docs/null-safety.html
- gitleaks, ferramenta pública de busca de segredos: a regra `azure-ad-client-secret` dela inspirou a forma que o `verifica` procura, que não executa o gitleaks nem aplica os delimitadores dele nem o limiar de entropia. Não é um formato publicado pela Microsoft. https://github.com/gitleaks/gitleaks/blob/master/config/gitleaks.toml
- Repositório Maven do Google: o BoM do Firebase 35.2.0 continua sendo o mais recente e traz `firebase-auth` 25.0.0. https://dl.google.com/dl/android/maven2/com/google/firebase/firebase-bom/maven-metadata.xml · https://dl.google.com/dl/android/maven2/com/google/firebase/firebase-bom/35.2.0/firebase-bom-35.2.0.pom
- Ensaio da série, 13/11/2026, Mac com Apple silicon, com um código de teste: o registro no Entra por `entra.microsoft.com`; `AADSTS16000` no portal do Azure; a conta recém-criada e o teste gratuito; a entrada real, a permissão, a colisão e o resultado pendente depois de `adb shell am kill`; a chave de acesso no emulador.
- Medição própria: Mac com Apple silicon, 18/11/2026. O `verifica` sobre `inicio/` e `solucion/`, cada passo da lição e cada sabotagem, com arquivos inventados e sem tocar o console do Entra, o do Azure nem o do Firebase, nem nenhuma conta. Os testes instrumentados e o botão da Microsoft com um arquivo inventado, em um emulador novo, sem conta, que foi apagado ao terminar. Os quatro códigos de `FirebaseAuthWebException` foram conferidos também dentro da biblioteca `firebase-auth` 25.0.0.
