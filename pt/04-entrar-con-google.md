# Lição 04 — Entrar com o Google

**Antes de ler, quatro palavras.** Uma linha cada uma; as demais chegam no passo em que são usadas.

- **Token de identidade:** um texto assinado pelo Google que diz quem a pessoa é e para quem foi emitido. Não é uma
  senha e expira.
- **Audiência:** o destinatário escrito dentro do token. Quem o recebe confere se ele traz o nome dele.
- **Credential Manager:** a peça do Android que mostra a janela para escolher uma conta e entrega ao app
  o que a pessoa escolheu.
- **ID de cliente web:** o identificador que o `google-services.json` traz no bloco `"client_type": 3`. Foi
  conferido na lição 03.

**O que fica com quem termina:** o botão «Entrar com o Google» («Entrar con Google») da loja, escrito por quem estuda, com uma
mensagem diferente para cada forma de não entrar, e saber dizer o que viaja em cada passo entre o app, o Google e o
Firebase.

**Ideia nova desta lição:** o token de identidade. O Google entrega uma prova assinada de quem a pessoa é, feita para
uma audiência, e o Firebase a verifica. O app não confere nada: só leva o token de um lado ao outro.
O resto —as bibliotecas, as funções que se pausam e as duas «portas»— já vem pronto e explicado, e não se avalia
aqui.

**Uma nota sobre os textos do app.** Como na lição 03, o projeto do curso traz os textos de tela em espanhol, e os arquivos de
`lecciones/` não são traduzidos: as verificações automáticas do curso (o `verifica`) procuram esses textos exatos.
Por isso, nos blocos de código, nas saídas de terminal e nos esboços de tela, os textos ficam como o projeto os tem;
na prosa, a lição os cita em português e, na primeira vez, traz o original entre parênteses.

---

## A cena

O emulador tem a conta do Google de quem estuda adicionada. Põe-se o botão, toca-se nele e o app responde que não há
nenhuma conta com a qual entrar.

A conta está lá. Aparece em Configurações.

Isso aconteceu no ensaio desta série, em 13 de novembro de 2026, em um Mac. O código pedia a credencial com
`GetGoogleIdOption` e `setFilterByAuthorizedAccounts(false)`, e o Credential Manager respondeu
`NoCredentialException`; no registro do sistema ficou o número `[28433]`. Mudou-se uma única coisa, a opção
da solicitação, para `GetSignInWithGoogleOption`: a janela de contas abriu e se entrou.

As duas opções existem e as duas pedem o mesmo, um token de identidade do Google. A documentação do Android
as separa pelo lugar em que são usadas: `GetGoogleIdOption` é para a bottom sheet do Credential Manager, e
`GetSignInWithGoogleOption` é para um botão. Esta lição faz o botão. Primeiro vê-se o que viaja, depois o botão é
escrito, e no final quebra-se tudo de propósito para reconhecer esse erro quando ele aparecer.

---

## Antes de começar

**O que é preciso:** a [03](03-la-app-conoce-a-firebase.md) concluída, com o `google-services.json` completo (o
`verifica` dela em 18 de 18), e o ambiente da [00](00-ambiente-listo.md). Para o passo 6 basta um emulador
aberto ou um telefone conectado; para o passo 7, além disso, que a imagem seja Google APIs e tenha a conta
adicionada (exercício 0.4). Vale o que diz «Conta e equipamento de confiança» na 00.

**O projeto de início.** A partir da raiz do repositório da série:

```bash
git pull
cp lecciones/03-la-app-conoce-a-firebase/inicio/app/google-services.json lecciones/04-entrar-con-google/inicio/app/
cd lecciones/04-entrar-con-google/inicio
```

O segundo comando leva o arquivo da 03 para o projeto desta lição. Ali ele também fica ignorado pelo git:
o projeto já traz um `.gitignore`. Ao lado está `solucion/`: dá para lê-la sem configurar nada, e
**`./verifica solucion` sai em vermelho em um clone limpo**, porque sem o arquivo o projeto não compila (medido em
18/11/2026: 0 testes executados). As respostas vão em `respuestas/04.md`, na raiz do repositório.

**O que o projeto já traz (andaime).** Não se avalia aqui; o passo 2 o percorre:

| O que traz | Onde | Por quê |
|---|---|---|
| Credential Manager 1.6.0 (duas bibliotecas) e `googleid` 1.1.1, junto ao BoM do Firebase 35.2.0 da 03 | `gradle/libs.versions.toml` e `app/build.gradle.kts` | São as peças que pedem o token. A série foi testada com essas versões |
| `Resultado`, e `Entrada` convertida em função que se pausa | `Entrada.kt` | Entrar já não responde na hora nem com um simples «sim ou não» |
| As duas portas, com a implementação de verdade | `Puertas.kt` | Por ali o código sai para falar com o telefone e com o Firebase |
| As telas, com o botão «Entrar con Google» e uma mensagem por caso | `Tienda.kt`, `PantallaEntrada.kt` | Já sabem mostrar o que a entrada responder |
| `MainActivity` com `crearEntrada`, que por ora devolve uma entrada que não deixa passar | `MainActivity.kt` | É onde o app monta a própria entrada. Recebe as duas portas para que um teste possa ler qual audiência foi pedida |
| Os testes simulados e os dublês deles | `app/src/test` | São os que o `verifica` roda. Não os altere |
| Os testes instrumentados, com uma cópia dos dublês, `androidx.test:runner` 1.7.0 e Espresso 3.7.0 | `app/src/androidTest` e o catálogo | Rodam dentro do Android (passo 6). O Espresso que o Compose traz por padrão não iniciou no Android 37: 0 de 4, com `NoSuchMethodException` (medido em 18/11/2026) |
| Um esqueleto de `EntradaConGoogle` | `EntradaConGoogle.kt` | É o arquivo que quem estuda escreve |

**O que esta lição não faz.** Não guarda a sessão nem a encerra (lição 07), e não distingue a primeira vez
(lição 08).

---

## Passo 1 · Sem IA: a viagem do token

Este passo é de quem estuda, sem Claude Code. Antes de escrever código, veja o que viaja.

| # | De quem para quem | O que viaja |
|---|---|---|
| 1 | App → Credential Manager | Uma solicitação: «quero um token de identidade do Google, para esta audiência». A audiência é o ID de cliente web |
| 2 | Credential Manager → a pessoa | A janela de contas. É o Android que a desenha, não o app |
| 3 | Google → Credential Manager → app | Um token de identidade, assinado pelo Google, com essa audiência dentro |
| 4 | App → Firebase | O mesmo token, sem mexer nele |
| 5 | Firebase → app | Um usuário, com o identificador dele no projeto e, se o provedor o entregou, o nome. Este app mostra «cliente» se faltar |

A senha da pessoa não está em nenhuma linha. Ela a digita, se for preciso, em uma janela do Google; ao app só chega
o resultado.

**Abra um token.** Um token de identidade tem três partes separadas por pontos: um cabeçalho, um conteúdo e
uma assinatura. O conteúdo não está criptografado: está codificado, que é apenas um modo de passar texto por qualquer
canal. Este conteúdo é **inventado** para a lição; não foi emitido pelo Google nem serve para entrar em nada:

```bash
printf '%s' 'eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhdWQiOiIxMDAwMDAwMDAwMDAtd2ViaW52ZW50YWRvLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMDAwMDAwMDAwMDAwMDAwMDAwMDAwIiwibmFtZSI6IkFuYSBJbnZlbnRhZGEiLCJleHAiOjE3OTUwNDY0MDB9' | base64 -d
```

No Mac da série (macOS 27) as duas formas funcionaram, `base64 -d` e `base64 -D` (medido em 18/11/2026).
Se o Mac não aceitar `-d`, use `-D`; no Linux é `-d`. O exemplo foi montado para ser decodificado assim, de forma simples.
Um token real usa uma variante, Base64URL, e pode pedir mais passos. **Não decodifique nem cole aqui um token
real:** o exercício se faz com o inventado.

**Preveja** antes de rodar: dá para lê-lo sem nenhuma chave? Isto foi o que respondeu no Mac em 18/11/2026:

```text
{"iss":"https://accounts.google.com","aud":"100000000000-webinventado.apps.googleusercontent.com","sub":"000000000000000000000","name":"Ana Inventada","exp":1795046400}
```

Cinco dados, com os nomes que a documentação do Google usa:

- **`iss`**, quem o emitiu. Deve ser o Google.
- **`aud`**, a audiência: para quem é. Deve ser um ID de cliente do projeto.
- **`sub`**, o identificador da conta. A documentação pede que se use este, e não o e-mail, para reconhecer uma
  pessoa.
- **`name`**, o nome.
- **`exp`**, quando expira.

Qualquer um pode ler um token. O que ninguém mais consegue é **assiná-lo** como o Google. Por isso quem o recebe
confere quatro coisas, segundo essa documentação: a assinatura, que `iss` seja o Google, que `aud` seja dele e que `exp` não
tenha passado. Nesta série, quem faz isso é o Firebase: a documentação dele diz que se passam as credenciais e que os
serviços dele as verificam e respondem.

**Desenhe a viagem.** Em `respuestas/04.md`, ou em papel com uma foto, desenhe a viagem com cinco setas e escreva
sobre cada uma o que viaja. Faça-o sem olhar a tabela e depois compare.

**O que comprova:** o diagrama, comparado com a tabela (objetivo 4.1). Nenhum teste o lê.

---

## Passo 2 · O que o projeto já traz

Quatro arquivos mudaram em relação à 03. Abra-os; ainda não os edite. Aparecem palavras novas de
Kotlin. Para esta lição bastam três, e as demais são peças prontas:

- **`suspend`:** esta função espera.
- **`try` / `catch`:** traduz um problema em um resultado (passo 4).
- **`sealed`:** obriga a cobrir todos os resultados.

**`Entrada.kt`: o contrato cresceu.** Na 02, `entrar()` devolvia um nome ou `null`. Agora diz:

```kotlin
interface Entrada {
    suspend fun entrar(): Resultado
}
```

- **`suspend`** marca uma função que pode ser pausada e continuada depois sem bloquear o resto, segundo a
  documentação do Kotlin. Entrar com o Google abre uma janela, espera uma pessoa e fala pela rede: enquanto
  isso a tela continua viva.
- **`Resultado`** é uma lista fechada (`sealed`) de quatro casos: `Entro(nombre)`, `SinCuentas`, `Cancelada` e
  `Fallo` («entrou», «sem contas», «cancelada» e «falha»). Um `null` não bastava para dizer **por que** não se entrou. Como a lista é fechada, o compilador
  sabe que não existe um quinto caso.

**`Puertas.kt`: por onde o código sai.** Duas interfaces de uma única função:

```kotlin
interface SelectorDeCuentas {
    suspend fun pedir(solicitud: GetCredentialRequest): GetCredentialResponse
}

interface VerificadorDeToken {
    suspend fun verificar(tokenDeIdentidad: String): String
}
```

A primeira pede uma credencial ao telefone; a versão de verdade dela chama o Credential Manager. A segunda
entrega o token; a versão de verdade dela chama o Firebase com `GoogleAuthProvider.getCredential` e
`signInWithCredential`, que são as duas chamadas do guia do Firebase. É a mesma ideia da 02: a classe
recebe as portas e não sabe quais lhe coube. No app são as de verdade; nos testes, duas «de mentira» (*fake gates*) que
vivem em `src/test`.

A porta do telefone é um recorte didático: passa a `Activity` direto para o Credential Manager. O guia
vigente do Android recomenda envolvê-la em um `MutableContextWrapper`, para não retê-la se o Android recriar
a tela com a janela aberta. Segundo as notas de versão, a biblioteca atualiza esse invólucro por
conta própria desde a 1.7.0-alpha03, e a série adota a 1.6.0. O arquivo diz isso em um comentário.

**`MainActivity.kt`: onde se monta.** `onCreate` chama `crearEntrada()` e passa o resultado para `Tienda`.
`crearEntrada` recebe as duas portas, com as de verdade por padrão, e por ora devolve
`EntradaSinProveedor()`, a que não deixa passar. O corpo dela é alterado no passo 3.

**`Tienda.kt`: quem chama.** O botão já não chama `entrar()` direto. Faz isso dentro de `alcance.launch { … }`.
`rememberCoroutineScope` dá um lugar onde rodar tarefas que se pausam, e a documentação do Compose diz que
esse lugar é cancelado sozinho quando a tela deixa de existir. Ali mesmo está `mensajeDe`, com um texto por caso.

**Meça o ponto de partida.** A partir da raiz do repositório:

```bash
./lecciones/04-entrar-con-google/verifica
```

Em 18/11/2026, com um arquivo inventado completo, a última linha foi
`Resultado: 6 de 20 pruebas en verde (14 en rojo, 0 con error, 0 saltadas)`. As seis verdes são do andaime:
as telas, os textos delas e o recurso que o plugin gerou. As catorze vermelhas esperam o código de quem estuda.

*(Em português: «Resultado: 6 de 20 testes em verde (14 em vermelho, 0 com erro, 0 pulados)».)*

---

## Passo 3 · Com o Claude Code: o caminho em que tudo dá certo

Aqui entra a ferramenta, e entra sempre do mesmo jeito: **prever → pedir → ler o que ele gerou → rodar o
`verifica` → explicar com palavras próprias.**

1. **Preveja.** Escreva em `respuestas/04.md`: das cinco linhas da viagem, quais acontecem dentro de
   `EntradaConGoogle` e quais atrás de uma porta?
2. **Peça** ao Claude Code, a partir de `inicio/`: «Em `EntradaConGoogle.kt`, escreva `entrar()` só para o
   caso em que tudo dá certo: monte um `GetSignInWithGoogleOption` com `idDeClienteWeb`, coloque-o em um
   `GetCredentialRequest`, peça-o com `selector.pedir`, extraia o token de identidade com
   `GoogleIdTokenCredential.createFrom` e entregue-o a `verificador.verificar`; devolva `Resultado.Entro` com o
   nome. Se a credencial não for do tipo Google, devolva `Resultado.Fallo`. Não capture exceções ainda.
   Em `MainActivity.kt`, mude só o corpo de `crearEntrada` para que devolva `EntradaConGoogle` com
   `getString(R.string.default_web_client_id)` e as duas portas que recebe, `selector` e `verificador`; e
   apague `EntradaSinProveedor` de `Entrada.kt`. Não altere nenhum outro arquivo, não escreva nenhum ID de
   cliente no código e não imprima nem guarde o token.»
3. **Leia** a mudança completa antes de aceitá-la. O centro deve se parecer com isto:

```kotlin
override suspend fun entrar(): Resultado {
    // Linha 1: a solicitação, com a audiência.
    val opcion = GetSignInWithGoogleOption.Builder(idDeClienteWeb).build()
    val solicitud = GetCredentialRequest.Builder().addCredentialOption(opcion).build()
    // Linhas 2 e 3: o telefone mostra a janela e devolve o que foi escolhido.
    val credencial = selector.pedir(solicitud).credential
    if (credencial !is CustomCredential ||
        credencial.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
        return Resultado.Fallo
    }
    val token = GoogleIdTokenCredential.createFrom(credencial.data).idToken
    // Linhas 4 e 5: o Firebase o verifica e responde quem é.
    return Resultado.Entro(verificador.verificar(token))
}
```

   Seis coisas a conferir ao ler:

   - **A opção é `GetSignInWithGoogleOption`.** Se aparecer `GetGoogleIdOption`, compare-a com o que
     um botão precisa e peça a mudança explicando a diferença: é a da cena.
   - **A audiência chega por parâmetro.** Em `EntradaConGoogle.kt` não deve haver nenhum texto que termine em
     `.apps.googleusercontent.com`.
   - **`crearEntrada` lê `R.string.default_web_client_id` e o passa a `EntradaConGoogle`.** Esse recurso foi
     gerado pelo plugin com o cliente web do arquivo (lição 03, passo 4). E `onCreate` continua chamando
     `crearEntrada()`.
   - **O token vai como está.** `idToken` entra em `verificar` sem cortes nem mudanças.
   - **Regra de dados: ninguém imprime, cola nem guarda o token.** Nem no registro (`Log`, `println`), nem em um
     arquivo ou nas preferências, nem em `respuestas/04.md`. No app ele vai direto da credencial para o Firebase.
     A documentação do Google diz que esses tokens são sensíveis e podem ser usados indevidamente se alguém
     os interceptar. Os tokens inventados dos testes são a única exceção, e é didática: os dublês os
     guardam, sim, em uma lista, porque não são de ninguém. À lista da lição 03 do que nunca se envia
     (chaves de assinatura, senhas, a chave da Microsoft) somam-se os tokens de identidade reais.
   - **Ninguém mexeu nos testes, em `Puertas.kt` nem no `google-services.json`.**

4. **Rode o `verifica`.** Com esse código deu `Resultado: 13 de 20 pruebas en verde (7 en rojo, …)` em
   18/11/2026. As sete vermelhas são os casos em que não se entra: o passo 4.
5. **Explique**, em três linhas de `respuestas/04.md`: o que `verificar` recebe, de onde saiu e quem o assinou.

*(Em português: «Resultado: 13 de 20 testes em verde (7 em vermelho, …)».)*

**Por que o ID de cliente web, e não o do Android?** Porque o token não é para o app: é para quem vai
verificá-lo. O guia do Firebase o escreve assim no exemplo dele: o ID de cliente do servidor, não o do Android.
Nesta série esse servidor é o Firebase.

**Por que esse ID não é uma senha?** Por três razões que se podem comprovar:

- Viaja dentro do app, como recurso. Qualquer pessoa que tenha o app instalado pode lê-lo.
- Vem do `google-services.json`, e a documentação do Firebase diz que esse arquivo traz identificadores
  únicos, mas não secretos.
- Diz apenas **para quem** o token é pedido. Não prova quem a pessoa é nem deixa ninguém entrar: o que prova algo é
  a assinatura do Google, e essa o app não tem.

**O que comprova, e até onde.** No nível simulado, o `verifica` comprova que a classe pede uma única
opção e que é a do botão, que a audiência dela é o ID que recebeu, e que o token passa como está de uma porta à
outra. E um teste abre `MainActivity`, passa as portas de mentira a `crearEntrada` e lê a audiência que
foi pedida: deve ser o cliente web do arquivo. Com outro recurso no lugar, esse teste fica vermelho mesmo que
o nome correto continue escrito no arquivo (medido em 18/11/2026: 19 de 20).

As portas eram de mentira: nada foi pedido ao Google nem ao Firebase. E resta uma lacuna que nenhum teste vê:
que `onCreate` passe a `Tienda` o que `crearEntrada()` devolve. Isso o `verifica` só procura como texto, e
o chama assim, «presencia textual» («presença textual»). Com `onCreate` montando outra entrada, os 20 testes dão verde e a única
marca vermelha é essa (medido em 18/11/2026).

---

## Passo 4 · Sem IA: as três formas de não entrar

Este é o exercício 4.3 e se faz **sem Claude Code**. São três blocos `catch`, escritos por quem estuda.

Quando a porta do telefone não consegue devolver uma credencial, lança uma exceção. A documentação do Android
lista as que importam aqui:

| Exceção | Quando, segundo a documentação | A entrada responde |
|---|---|---|
| `NoCredentialException` | Não há uma credencial disponível para a pessoa: por exemplo, não há contas no dispositivo ou é preciso autenticar de novo. A exceção não diz qual das causas foi | `Resultado.SinCuentas` |
| `GetCredentialCancellationException` | A pessoa cancelou. O mesmo guia avisa que muitos «cancelamentos» seguidos podem esconder uma configuração errada | `Resultado.Cancelada` |
| Qualquer outra | Uma falha do telefone, ou o Firebase rejeitou o token | `Resultado.Fallo` |

**1. Preveja.** As duas primeiras são filhas de uma mesma classe, `GetCredentialException`. Se um
`catch` geral vier primeiro e os dois específicos depois, qual das três mensagens a pessoa verá sempre?

**2. Escreva.** Envolva o que foi escrito no passo 3 com `return try { … }` e acrescente, nesta ordem:

```kotlin
} catch (e: NoCredentialException) {
    Resultado.SinCuentas
} catch (e: GetCredentialCancellationException) {
    Resultado.Cancelada
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Resultado.Fallo
}
```

O terceiro não é um caso da entrada. `CancellationException` (de `kotlinx.coroutines`) é o Kotlin avisando que
a tarefa foi cancelada, por exemplo porque a tela foi fechada. A documentação do Kotlin pede que, se for
capturada, seja lançada de novo. Como o `catch` geral que vem abaixo também a capturaria, ela vai antes e é deixada passar.

**3. Rode o `verifica`.** Agora a linha é `Resultado: 20 de 20 pruebas en verde`. Fica uma marca em vermelho, a
de `respuestas/04.md`: ela se preenche no passo 5.

**4. Confira a previsão.** Mova o `catch (e: Exception)` para cima dos outros e rode o `verifica`: em
18/11/2026 deu 16 de 20, com «sin cuentas» e «cancelación» em vermelho. Devolva-o ao lugar.

Os textos que a pessoa vê já estão em `mensajeDe`, em `Tienda.kt`:

| Resultado | Mensagem |
|---|---|
| `SinCuentas` | «No encontré una cuenta de Google disponible para entrar. Agrega una o vuelve a iniciar sesión en Google y prueba otra vez.» *(Não encontrei uma conta do Google disponível para entrar. Adicione uma ou entre de novo no Google e tente outra vez.)* |
| `Cancelada` | «Cerraste la ventana sin elegir cuenta. Toca el botón cuando quieras intentar otra vez.» *(Fechou-se a janela sem escolher uma conta. Toque no botão quando quiser tentar outra vez.)* |
| `Fallo` | «No se pudo entrar. Revisa tu conexión y vuelve a intentar.» *(Não foi possível entrar. Confira a conexão e tente de novo.)* |

**O que comprova:** o `verifica` simulado, os três casos (objetivo 4.3). Cada caso leva testes de dois tipos:
uns lançam a exceção contra a classe e leem o que ela respondeu; outro desenha a tela com a classe e procura a
mensagem. As exceções foram lançadas por um dublê: nenhuma veio de um telefone.

---

## Passo 5 · Sem IA: quebre uma de propósito

Também **sem Claude Code**. Vai-se provocar o erro da cena.

**1. Preveja.** Vai-se trocar a opção do botão por `GetGoogleIdOption`, a da bottom sheet do Credential Manager.
Não é «a do início automático»: segundo o guia do Android, esse só acontece se também se ativar
`setAutoSelectEnabled(true)` e as condições dele forem cumpridas, e aqui não se ativa. Compila? Quantos testes
ficam vermelhos, e quais?

**2. Quebre e meça.** Em `EntradaConGoogle.kt`, troque a linha da opção por esta (e o `import` dela):

```kotlin
val opcion = GetGoogleIdOption.Builder()
    .setServerClientId(idDeClienteWeb)
    .setFilterByAuthorizedAccounts(false)
    .build()
```

Rode o `verifica`. Isto foi o que deu em 18/11/2026 (recortado):

```text
❌ Algún .kt de la app nombra GetGoogleIdOption: es la opción de la hoja de Credential Manager, no la del …
❌ Tu entrada pidió otra opción: la solicitud llevó GetGoogleIdOption y debe llevar GetSignInWithGoogleOption
❌ La audiencia de la solicitud no es el ID de cliente que recibió EntradaConGoogle (o la opción no es …
❌ La Entrada que arma MainActivity.crearEntrada no pide el token con el cliente web de tu archivo como …
Resultado: 17 de 20 pruebas en verde (3 en rojo, 0 con error, 0 saltadas), leído de …
```

*(Essa é a saída do `verifica`, em espanhol, como o script a imprime. Em português: algum `.kt` do app nomeia
`GetGoogleIdOption`, que é a opção da bottom sheet do Credential Manager, não a do botão; a entrada pediu outra
opção, pois a solicitação levou `GetGoogleIdOption` e deve levar `GetSignInWithGoogleOption`; a audiência da
solicitação não é o ID de cliente que `EntradaConGoogle` recebeu; e a entrada que `MainActivity.crearEntrada` monta não pede o token
com o cliente web do arquivo. Resultado: 17 de 20 testes em verde.)*

Copie para `respuestas/04.md` a linha que diz «Tu entrada pidió otra opción».

**3. O que esses testes não dizem.** Compila, e 17 de 20 continuam em verde: o token inventado passa igual pelas
duas portas de mentira. Os testes só sabem que foi pedida **outra** opção. O que essa opção faz em um
telefone foi medido à parte:

- Em 13/11/2026, no emulador com uma conta adicionada: `NoCredentialException` e `[28433]` no registro.
- Em 18/11/2026, em um emulador novo **sem** conta (Pixel 9, imagem `google_apis_playstore` do Android 37,
  com um arquivo inventado): o app mostrou a mensagem de «sem contas» e o registro disse `[28433]`. Com a opção do botão, nesse mesmo emulador, o Android abriu a tela do Google para
  adicionar uma conta; ao voltar com «voltar», o app mostrou a mensagem do cancelamento.

São observações desses dois dias, com essas versões; não uma regra que o Google publique.

**4. Conserte.** Devolva a linha a `GetSignInWithGoogleOption.Builder(idDeClienteWeb).build()` e o `import` dela.
Rode o `verifica`: 20 de 20, e agora também a marca de `respuestas/04.md`.

O verde confirma o que esses testes conferem e que a linha está no arquivo; a explicação de quem estuda completa a
evidência. O exercício está concluído quando as três caixas podem ser marcadas:

- [ ] A previsão do ponto 1 está escrita, com o que se viu embaixo.
- [ ] Foi colada a linha do vermelho.
- [ ] Foi dito por que 17 testes continuaram em verde com um código que, no telefone, não deixa entrar.

**O que comprova:** «Quebre uma de propósito». É a base do objetivo 4.2: saber por que o botão leva essa opção.

---

## Passo 6 · Dentro do Android: o nível instrumentado

Os testes dos passos anteriores rodam no computador, com uma imitação do Android. Estes rodam
**dentro** do Android, no emulador ou no telefone. Com o emulador aberto, a partir da raiz do repositório:

```bash
./lecciones/04-entrar-con-google/verifica inicio instrumentado
```

Repete o nível simulado e depois instala o app e o app de testes dele no dispositivo. Ali desenha `Tienda`
com a `EntradaConGoogle` de quem estuda e as mesmas duas portas de mentira, toca nos botões e procura os textos. Com a
solução, em um emulador novo e sem conta (Pixel 9, imagem `google_apis_playstore` do Android 37), isto deu
em 18/11/2026 (recortado):

```text
✅ Instrumentado: dentro de Android, con las puertas de mentira, tocar «Entrar con Google» lleva al inicio …
✅ Instrumentado: dentro de Android, cada uno de los tres casos muestra su mensaje sin pasar al inicio (3 de 3)
Resultado: 20 de 20 pruebas en verde (0 en rojo, 0 con error, 0 saltadas), leído de …
Instrumentado: 4 de 4 pruebas en verde (0 en rojo, 0 con error, 0 saltadas), leído de …
```

*(Em português: dentro do Android, com as portas de mentira, tocar «Entrar con Google» leva ao início; dentro do
Android, cada um dos três casos mostra a mensagem correspondente sem passar ao início (3 de 3). Resultado: 20 de 20 testes em
verde. Instrumentado: 4 de 4 testes em verde.)*

Sobre o projeto de início sem mexer deu 0 de 4, e sem nenhum dispositivo conectado o `verifica` sai em vermelho
e diz que não rodou.

**O que comprova, e até onde.** Comprova que as telas, o toque e a classe de quem estuda funcionam dentro do Android,
com as bibliotecas do Credential Manager e do `googleid` carregadas no dispositivo. **Não** comprova nada do
projeto do Firebase: as portas continuam de mentira, a janela de contas não se abre e não é preciso
conta nem rede até o Google. Por isso passou em um emulador sem conta.

---

## Passo 7 · No telefone: o nível real

Até aqui nada falou com o Google. Este passo fala, uma vez, à mão. É o único que comprova o que a lição 03
deixou escrito no arquivo: que a impressão digital está registrada, que o Google está habilitado e que o ID de cliente é
do projeto.

Com o emulador aberto e a conta adicionada, a partir de `inicio/`:

```bash
./gradlew installDebug
```

Abra «A loja» («La tienda»), toque em «Entrar» e depois em «Entrar com o Google» («Entrar con Google»).

**O que se viu no ensaio de 13/11/2026** (Mac, emulador com imagem `google_apis_playstore` e uma conta
adicionada): abriu-se a janela de contas do Android, escolheu-se a conta e o app cumprimentou com o nome dessa
conta. Esse ensaio usou um código de teste com as mesmas chamadas, não o de `solucion/`:
a classe `VerificadorConFirebase` desta lição compila, mas ainda não a rodamos contra um projeto real
do Firebase. Se ao testá-la aparecer algo diferente do que este passo descreve, conte-nos o que apareceu.

**Se a conta usa chave de acesso (passkey).** No ensaio, o Google pediu para escanear um código QR com o
telefone, e isso não se completou no emulador. Tocou-se em **«Try another way»** e se entrou com a senha ou com o
aviso no telefone (lição 00, exercício 0.4).

**A caixa de permissões.** Segundo a ajuda do Google, ao escolher «Sign in with Google» aceitam-se compartilhar com esse
app três dados: nome, e-mail e foto de perfil. Com eles o app pode criar a conta da pessoa.
O texto exato da caixa não foi capturado no ensaio; se o que aparecer disser outra coisa, conte-nos como diz.

Este é o exercício 4.4. Classifique cada dado em `respuestas/04.md` antes de olhar a tabela de «Compare as
respostas»: **o que é, para que se pede e se o app o recebe.**

1. O nome.
2. O e-mail.
3. A foto de perfil.
4. A senha do Google.

**Ao terminar, em um equipamento que não é de quem estuda:** retire a conta do emulador e apague os dados dele (lição 00).

**O que comprova:** a entrada real, com a conta, comprova que a impressão digital, o ID de cliente e o provedor estão bem
configurados. Não comprova os três casos de não entrar: esses o nível simulado cobre.

---

## Os exercícios

Os passos 1, 4 e 5 se fazem **sem IA**.

| # | Faz-se | Está pronto quando |
|---|---|---|
| 4.1 | Desenhar a viagem com cinco setas e o que viaja em cada uma | O diagrama coincide com a tabela do passo 1. Quem compara é quem estuda |
| 4.2 | Implementar `EntradaConGoogle` com a opção do botão e o ID de cliente web, e explicar por que esse ID não é uma senha | Três evidências. Simulado: o `verifica` dá 20 de 20, o que valida o que a classe pede e para onde leva o token, com portas de mentira; as marcas «Texto» dele são presença textual. Instrumentado: `./verifica inicio instrumentado` dá 4 de 4, o que valida o mesmo dentro do Android. Real: entra-se com a conta no passo 7, que é a única coisa que valida o projeto. A explicação está em `respuestas/04.md` |
| 4.3 | Tratar sem contas, cancelamento e erro, cada um com a própria mensagem | As três marcas de casos do `verifica` estão em verde. As exceções são lançadas por um dublê |
| 4.4 | Classificar os quatro dados do passo 7 | 4 de 4 contra a tabela de «Compare as respostas» |

**Tempo:** calcule umas três horas e meia, com o «Quebre uma de propósito», o nível instrumentado e a entrada
real. É uma estimativa: será ajustada com o tempo que a primeira turma levar.

---

## Não roda?

| O que aparece | Causa provável | O que fazer |
|---|---|---|
| `File google-services.json is missing.` | O arquivo não foi copiado para `inicio/app/` | O `cp` de «Antes de começar». Sobre `solucion/` é o esperado em um clone limpo |
| ❌ «sin cuentas» e «cancelación» em vermelho, com os `catch` escritos | O `catch (e: Exception)` está antes dos específicos | Passo 4: os específicos vão primeiro |
| `Unresolved reference 'default_web_client_id'` | O arquivo não traz cliente web (tipo 3), e o plugin não gerou o recurso. Medido em 18/11/2026 com um arquivo inventado sem clientes | Baixá-lo de novo depois da impressão digital e de habilitar o Google (lição 03, passo 3) |
| ❌ «Tu entrada pidió otra opción» (a entrada pediu outra opção) | Ficou `GetGoogleIdOption` | Passo 5, ponto 4 |
| No telefone, «No encontré una cuenta de Google disponible…» com a conta adicionada | No ensaio de 13/11/2026 a opção era `GetGoogleIdOption`. Segundo o guia, também acontece se a conta precisa entrar de novo | Conferir se a opção é `GetSignInWithGoogleOption`, e se a conta continua iniciada em Configurações |
| ❌ «Instrumentado: no hay ningún emulador abierto ni teléfono conectado» (não há nenhum emulador aberto nem telefone conectado) | `adb devices` não lista nenhum dispositivo no estado `device` | Abrir o emulador (lição 00) e esperar que termine de iniciar |
| Nos testes instrumentados, `NoSuchMethodException: android.hardware.input.InputManager.getInstance` | A linha do Espresso 3.7.0 foi retirada do andaime e ficou o que o Compose traz por padrão (medido em 18/11/2026 no Android 37) | Devolver `androidTestImplementation(libs.espresso.core)` |
| No telefone, abre-se uma tela do Google para iniciar sessão | O emulador não tem nenhuma conta (medido em 18/11/2026) | Adicionar a conta ali, ou em Configurações (lição 00, exercício 0.4) |
| O Google pede para escanear um código QR | A conta usa chave de acesso | «Try another way» |
| No telefone, «No se pudo entrar…» (não foi possível entrar) | Com um projeto real ainda não medimos qual das causas o produz. As três mais prováveis: a impressão digital sem registrar, o Google sem habilitar no Firebase ou o emulador sem rede | Rever a lição 03, passo 3, e conferir se o emulador tem rede |
| `GetCredentialProviderConfigurationException` | Segundo o guia do Android, falta a biblioteca `credentials-play-services-auth`. Não foi medido aqui | Conferir se `app/build.gradle.kts` ainda traz as três linhas do andaime |
| Trocou-se de computador e não se tem a impressão digital de depuração deste | Sem `google-services.json` o projeto não compila, e o armazenamento de depuração é criado pela primeira compilação: um círculo vicioso (medido no Linux Mint 22.3, 18/11/2026) | Copiar para `inicio/app/` o arquivo da 03 mesmo que traga a impressão digital do outro computador, rodar `./gradlew assembleDebug` (compila igual), tirar a impressão digital com `./gradlew signingReport`, registrá-la no Firebase e baixar o arquivo de novo |

No Linux Mint 22.3 (x86_64), o `verifica` desta lição saiu igual ao do Mac (18/11/2026), no nível
simulado e com um arquivo de teste que trazia a impressão digital desse computador: sobre `inicio/`, 6 de 20 em verde; sobre
`solucion/`, 20 de 20. Sem arquivo, «0 testes executados», como no Mac. O nível instrumentado não foi medido no Linux.
As dependências desta lição somaram uns 300 MB a `~/.gradle`.

---

## Para fechar

**Responda sem reler:**

1. O que é um token de identidade, quem o assina e quem o verifica nesta série?
2. O que diz a audiência, e de onde o app tira o valor que coloca ali?
3. O `verifica` dá 20 de 20. Diga uma coisa que isso comprova e uma que não.
4. Por que o `catch` geral vai no final?

**O que vem a seguir:** na L05 acrescenta-se um segundo provedor, a Microsoft, sem mudar as telas.

---

## Compare as respostas

**Passo 1.** Dá, sim, para ler sem chave: o conteúdo de um token está apenas codificado. O que protege o token é
a assinatura, não uma criptografia. O diagrama deve trazer as cinco setas da tabela, com a audiência na primeira,
o token na terceira e na quarta, e nenhuma seta com a senha da pessoa para o app.

**Passo 3.** Dentro de `EntradaConGoogle` acontece montar a solicitação (parte da linha 1) e passar o token de uma
porta à outra. Atrás da porta do telefone, as linhas 2 e 3; atrás da do verificador, a 4 e a 5.
`verificar` recebe o token de identidade, que saiu da credencial que o telefone entregou e que o Google assinou.

**Passo 4.** Sempre a de `Fallo`: o `catch` geral captura também as duas filhas, e os específicos que vêm abaixo
nunca são alcançados.

**Passo 5.** Compila, e ficam vermelhos três testes: o que confere a opção e os dois que leem a audiência,
da classe e da entrada que o app monta. Os outros 17 continuam em verde porque usam portas de mentira, que entregam o token inventado sem importar qual opção
foi pedida. Por isso o nível simulado não basta para dizer «no telefone entra».

**Exercício 4.4.**

| Dado | O que é | Para que se pede | O app o recebe? |
|---|---|---|---|
| Nome | O nome da conta do Google | Para criar a conta da pessoa no app | Sim, neste fluxo do Google, se a conta o tem. Chega pelo Firebase e aparece em «Hola, …» («Olá, …»); se faltar, o app diz «cliente» |
| E-mail | O endereço da conta | Para criar a conta da pessoa no app | Sim: a credencial o traz, e o usuário do Firebase tem um e-mail principal. Este app não o mostra |
| Foto de perfil | O endereço da foto | Para criar a conta da pessoa no app | Sim: a credencial traz o endereço dela. Este app não a usa |
| Senha | A senha da conta do Google | Não se pede para o app | Não. A ajuda do Google não a lista entre o que se compartilha, e a credencial que o app recebe não tem um campo para ela |

**Para fechar:**

1. Um texto assinado pelo Google que diz quem a pessoa é e para quem foi emitido. Quem o assina é o Google e quem o verifica
   é o Firebase; o app só o leva.
2. Para quem é o token. O app o tira do recurso `default_web_client_id`, que o plugin gerou com o
   cliente web do `google-services.json`.
3. Comprova o que a classe pede (a opção e a audiência), que o token passa de uma porta à outra, e qual
   resultado e qual mensagem cada caso dá. Não comprova que o projeto do Firebase esteja bem configurado nem que
   alguém consiga entrar: as portas eram de mentira.
4. Porque captura tudo, inclusive as exceções específicas. Se vier primeiro, os outros nunca rodam.

---

## Fontes da lição

Abertas em 18/11/2026.

- Android Developers. *Implement Sign in with Google* (o fluxo do botão com `GetSignInWithGoogleOption`; o da bottom sheet com `GetGoogleIdOption`; as condições do início automático; o `MutableContextWrapper`; `GoogleIdTokenCredential.createFrom`; validar o token no servidor). https://developer.android.com/identity/sign-in/credential-manager-siwg-implementation
- Android Developers. *Sign in with Google overview* (por que um botão além da bottom sheet; com o botão é possível adicionar uma conta). https://developer.android.com/identity/sign-in/credential-manager-siwg
- Android Developers. *Troubleshoot Credential Manager* (`NoCredentialException`, `GetCredentialCancellationException`, `GetCredentialProviderConfigurationException`). https://developer.android.com/identity/sign-in/credential-manager-troubleshooting-guide
- Android Developers. *androidx.credentials.exceptions* (a descrição de cada exceção). https://developer.android.com/reference/kotlin/androidx/credentials/exceptions/package-summary
- Android Developers. *Credentials*, notas de versão (a 1.6.0 é a estável, de 8/4/2026; a troca automática de contexto chegou na 1.7.0-alpha03). https://developer.android.com/jetpack/androidx/releases/credentials
- Android Developers. *Side-effects in Compose* (`rememberCoroutineScope`). https://developer.android.com/develop/ui/compose/side-effects
- Android Developers. Guia de testes do Compose (as dependências dos testes e `app/src/androidTest`). https://developer.android.com/develop/ui/compose/testing
- Google for Developers. *OpenID Connect* (os tokens de identidade são sensíveis; são validados antes de usar os dados deles; os valores vão em Base64URL). https://developers.google.com/identity/openid-connect/openid-connect
- Google for Developers. *GoogleIdTokenCredential* (os dados que a credencial traz). https://developers.google.com/identity/android-credential-manager/android/reference/com/google/android/libraries/identity/googleid/GoogleIdTokenCredential
- Google for Developers. *Verify the Google ID token on your server side* (assinatura, `aud`, `iss`, `exp`; usar `sub` e não o e-mail). https://developers.google.com/identity/gsi/web/guides/verify-google-id-token
- Ajuda das Contas do Google. *Sign in with Google* (o que se compartilha: nome, e-mail e foto de perfil). https://support.google.com/accounts/answer/12849458
- Firebase. *Authenticate with Google on Android* (as bibliotecas, `default_web_client_id`, `GoogleAuthProvider.getCredential` e `signInWithCredential`; o exemplo dele usa `GetGoogleIdOption` e o Credential Manager 1.3.0). https://firebase.google.com/docs/auth/android/google-signin
- Firebase. *Firebase Authentication* (passam-se as credenciais e os serviços dele as verificam). https://firebase.google.com/docs/auth
- Firebase. *Users in Firebase projects* (identificador, e-mail, nome e foto do usuário). https://firebase.google.com/docs/auth/users
- Kotlin. *Coroutines basics* (`suspend`, `launch`), *Cancellation and timeouts* (lançar de novo a `CancellationException`) e *Sealed classes and interfaces*. https://kotlinlang.org/docs/coroutines-basics.html · https://kotlinlang.org/docs/coroutines-cancellation.html · https://kotlinlang.org/docs/sealed-classes.html
- Repositório Maven do Google: os índices dele listam `credentials` 1.6.0, `googleid` 1.1.1 e `firebase-bom` 35.2.0. Existe uma versão mais nova do `googleid`; a série adota a 1.1.1, que é a do guia do Firebase e a do ensaio. Os mesmos índices listam `androidx.test:runner` 1.7.0 e `espresso-core` 3.7.0 como as estáveis mais recentes. https://dl.google.com/dl/android/maven2/androidx/credentials/credentials/maven-metadata.xml · https://dl.google.com/dl/android/maven2/com/google/android/libraries/identity/googleid/googleid/maven-metadata.xml · https://dl.google.com/dl/android/maven2/com/google/firebase/firebase-bom/maven-metadata.xml · https://dl.google.com/dl/android/maven2/androidx/test/runner/maven-metadata.xml · https://dl.google.com/dl/android/maven2/androidx/test/espresso/espresso-core/maven-metadata.xml
- Ensaio da série, 13/11/2026, Mac com Apple silicon: a entrada real com uma conta; `GetGoogleIdOption` com `NoCredentialException` e `[28433]`; a chave de acesso e «Try another way».
- Medição própria: Mac com Apple silicon, 18/11/2026. O `verifica` sobre `inicio/` e `solucion/`, cada passo da lição e cada sabotagem, com arquivos inventados e sem tocar o console do Firebase nem nenhuma conta. E o app em um emulador novo, sem conta, que foi apagado ao terminar: as duas opções, e o cancelamento com «voltar». Os testes instrumentados foram medidos em outro emulador novo, sem conta, também apagado.

Revisão externa (18/11/2026): COM ALTERAÇÕES, 3 de prioridade alta, 5 média e 2 baixa; aplicada.
