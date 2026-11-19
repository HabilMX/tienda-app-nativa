# Lição 01 — Minha primeira tela roda

**Antes de ler, sete palavras.** Uma linha cada uma; as demais da série chegam na lição em que são usadas.

- **Kotlin:** a linguagem em que o app é escrito. Os arquivos dela terminam em `.kt`.
- **Compose** (Jetpack Compose): a biblioteca do Android para montar telas escrevendo funções em Kotlin.
- **Função composable:** uma função marcada com `@Composable` que descreve uma parte da tela.
- **Esboço:** o desenho de como a tela deve ficar. É com ele que se compara.
- **Modificador:** o que diz a um elemento quanto espaço ele ocupa e quanta margem deixa.
- **Estado:** um dado que a tela lembra e que pode mudar, como «o botão já foi tocado».
- **Teste simulado:** um teste que desenha a tela na memória do computador, sem emulador, e procura nela o que
  deveria estar ali.

**O que fica com quem termina:** a tela de boas-vindas da loja —nome, slogan e botão— escrita à mão, e saber ler uma
tela de Compose linha por linha e dizer o que cada uma muda na tela.

**Ideia nova desta lição (a única que se avalia):** no Compose, uma tela é uma função que descreve o que se vê. Não
existe um arquivo de layout à parte: lê-se a função e sabe-se o que vai aparecer.

**Uma nota sobre os textos do app.** O projeto do curso traz os textos de tela em espanhol, e os arquivos de
`lecciones/` não são traduzidos: as verificações automáticas do curso (o `verifica`) procuram esses textos exatos.
Por isso, nos blocos de código, nas saídas de terminal e nos esboços de tela, os textos ficam como o projeto os tem;
na prosa, a lição os cita em português e, na primeira vez, traz o original entre parênteses: o nome «A loja» (no
projeto, «La tienda»), o slogan «Os pedidos, no bolso» («Tus órdenes, en tu bolsillo»), o botão «Entrar» («Entrar») e
o cumprimento «Olá!» («¡Hola!»).

---

## A cena

Pede-se a tela de boas-vindas ao Claude Code. Em pouco tempo há um arquivo curto de Kotlin. Ele compila. Os testes
saem em verde. O app é instalado, toca-se em «Entrar» e o cumprimento aparece. Fecha-se o computador.

No dia seguinte alguém abre o app ao lado do esboço e pergunta: «por que o cumprimento aparece acima do botão, se aqui
ele vai abaixo?». Abre-se o arquivo. Veem-se `remember`, `mutableStateOf`, `Modifier`, um `if` no meio da tela.
Não se sabe qual dessas linhas move o cumprimento.

Esse código existe: é a listagem do exercício 1.3, e os testes dele saem 4 de 4 em verde com o cumprimento no lugar
errado (medido em 18 de novembro de 2026). O verde diz que o cumprimento aparece. Não diz onde.

Por isso a lição segue esta ordem: lê-se uma tela, escreve-se uma à mão, pede-se uma mudança ao Claude Code e, no
fim, explica-se —sem ajuda— o que faz cada bloco do que ele gerou.

---

## Antes de começar

**O que é preciso:**

| | Serve | Não serve |
|---|---|---|
| Lição anterior | A [00](00-ambiente-listo.md) concluída: `ANDROID_HOME` definido e um emulador ou um telefone Android | Pulá-la: sem o SDK, o projeto não compila |
| Conhecimento | Programar em alguma linguagem e já ter feito uma interface | Não se supõe Kotlin nem Compose |

**O projeto de início.** A partir da raiz do repositório da série:

```bash
git pull
cd lecciones/01-mi-primera-pantalla/inicio
```

É ali que se trabalha. Ao lado está `solucion/`, com a tela resolvida: serve para comparar no final, não para copiar.

**O que o projeto já traz (andaime).** Não é escrito à mão e não se avalia aqui, mas convém saber por que está ali:

| O que traz | Onde | Por quê |
|---|---|---|
| `org.gradle.jvmargs=-Xmx4g` | `gradle.properties` | Dá 4 GB de memória ao Gradle, a ferramenta que compila. Sem ela, compilar com o Firebase (lição 03) falhou com «Java heap space» (medido em 13/11/2026) |
| `gradlew` já gerado | raiz do projeto | O script que baixa e roda a versão exata do Gradle. Gerá-lo com `gradle wrapper` **dentro** de um projeto com AGP 9.4 falhou (medido em 13/11). Não o gere de novo |
| `sdk=36` | `app/src/test/resources/robolectric.properties` | Os testes simulados usam uma cópia do Android 36. Sem fixá-la, usavam o Android 37 e nenhum rodou (medido em 13/11) |
| `jvmToolchain(25)` | `app/build.gradle.kts` | No ensaio de 13/11, o Robolectric, ao carregar o Android 36 dele, não iniciou com o Java 17 e pediu Java 21 ou superior. Por isso o projeto fixa 25 |

**O esboço.** Isto é o que deve aparecer ao terminar:

```text
┌────────────────────────────────┐
│                                │
│           La tienda            │  ← nome, com letra grande
│  Tus órdenes, en tu bolsillo   │  ← slogan
│          [ Entrar ]            │  ← botão
│            ¡Hola!              │  ← aparece só depois de tocar em «Entrar»
│                                │
└────────────────────────────────┘
  Tudo centralizado, no meio da tela, com 24 dp de margem.
```

**dp** é a unidade do Android para tamanhos: mais ou menos um pixel em uma tela de densidade média, ajustado para
parecer do mesmo tamanho em telas mais densas.

---

## Passo 1 · Ler uma tela antes de rodá-la

Abra `app/src/main/java/mx/habil/ejemplo/tienda/Bienvenida.kt`. Ele vem assim:

```kotlin
@Composable
fun Bienvenida() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Aquí va tu pantalla")
        Text("Lección 01")
    }
}
```

**Preveja primeiro.** Na raiz do repositório, crie a pasta de respostas se ela ainda não existir (`mkdir -p
respuestas`) e, dentro, o arquivo `respuestas/01.md`. É ali que ficam as previsões e as explicações. Escreva, antes de
rodar qualquer coisa, três linhas: quantos textos vão aparecer? Um ao lado do outro ou um embaixo do outro? Colados à
esquerda ou centralizados?

Para prever, leia a função de fora para dentro:

- **`@Composable`** marca a função como uma que descreve tela.
- **`fun Bienvenida()`** é uma função do Kotlin. Não devolve nada: descreve o que deve aparecer.
- **`Column( … ) { … }`** organiza o que leva dentro em sequência vertical: um embaixo do outro.
- **`Text("…")`** mostra um texto. Cada chamada é um elemento.
- **`Modifier.fillMaxSize().padding(24.dp)`** diz que a coluna ocupe todo o espaço que lhe derem e deixe 24 dp de
  margem.
- **`verticalArrangement = Arrangement.Center`** põe o conteúdo na metade da altura. Sobre o horizontal ninguém disse
  nada, e em uma `Column` isso começa, por padrão, em `Alignment.Start`: o lado em que o texto começa.

**Agora rode-a.** Com o emulador aberto ou o telefone conectado:

```bash
./gradlew installDebug      # compila e instala; não abre o app
```

Abra-a pela gaveta de apps e compare com as três linhas. Se uma previsão falhou, não a apague: escreva embaixo que
linha do código teria dado a resposta. No emulador do teste (Pixel 9, Android 37, 18/11/2026) apareceram dois
textos, «Aquí va tu pantalla» e embaixo «Lección 01», na metade da altura e colados à esquerda.

**Segunda previsão.** E se o `Column` for removido e os dois `Text` ficarem soltos? Segundo a documentação do
Android, o Compose empilha um texto sobre o outro. Experimente, anote o que aparece e devolva o `Column`.

**Quem chama `Bienvenida()`?** O `MainActivity.kt`, na mesma pasta: a linha `setContent { Bienvenida() }`
diz que função desenha a tela. Ela não se altera.

**O que comprova:** a previsão escrita, comparada com a execução (objetivo 1.1).

---

## Passo 2 · Kotlin: o que já se conhece com outro nome

Basta reconhecer três coisas que já se usam em outra linguagem.

| Em Kotlin | O que é | Onde se vê |
|---|---|---|
| `val nombre = "La tienda"` | Uma variável que se atribui uma vez e não muda mais | `val pantalla = …` em `BienvenidaTest.kt` |
| `var saludando = false` | Uma variável que pode mudar | Escreve-se no passo 4 |
| `fun doble(n: Int): Int { return n * 2 }` | Uma função: nome, parâmetros com o tipo e tipo de retorno | `fun Bienvenida()` |
| `fun doble(n: Int) = n * 2` | A mesma, curta: o corpo é uma expressão e o tipo de retorno é inferido | |
| `Bundle?` | Um tipo que **pode** ser nulo. Sem o `?`, não pode | `savedInstanceState: Bundle?` em `MainActivity.kt` |

Três detalhes que mudam em relação a outras linguagens:

- **O tipo vem depois do nome:** `n: Int`. Em uma variável quase nunca se escreve: em `val x = 5`, o Kotlin deduz
  que é `Int`.
- **Os nulos se declaram.** Uma variável `String` não pode guardar `null`. Se o valor pode faltar, o tipo é `String?`,
  e então `lema.length` dá erro ao compilar; `lema?.length` devolve `null` se `lema` é nulo; e
  `lema?.length ?: 0` devolve `0` nesse caso.
- **As chaves no final são uma função passada a outra.** Em `Column( … ) { … }`, o que vai entre chaves é
  uma função sem nome (uma *lambda*). Se o último parâmetro de uma função é outra função, o Kotlin permite
  escrevê-la fora dos parênteses. Por isso o Compose parece blocos aninhados.

**O que comprova:** a revisão 1.4, em «Os exercícios».

---

## Passo 3 · Escrever a boas-vindas, à mão

Este passo é de quem estuda, sem Claude Code: são umas dez linhas.

**Preveja primeiro.** Quantas chamadas a funções composable serão necessárias dentro do `Column`, e qual delas vai
levar outra dentro?

No `Bienvenida.kt`:

1. **Acrescente três linhas ao bloco de `import`.** Um `import` diz ao Kotlin de que biblioteca sai cada nome:

```kotlin
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
```

2. **Centralize o horizontal.** Ponha uma vírgula depois de `Arrangement.Center` e, embaixo, o parâmetro que faltava:

```kotlin
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
```

3. **Troque os dois textos pelos três elementos do esboço:**

```kotlin
        Text("La tienda", style = MaterialTheme.typography.headlineLarge)
        Text("Tus órdenes, en tu bolsillo")
        Button(onClick = { }) {
            Text("Entrar")
        }
```

`style = MaterialTheme.typography.headlineLarge` aplica ao nome um dos tamanhos de letra do Material, o conjunto de
componentes visuais do Android. `Button` leva duas coisas: `onClick`, a função chamada quando alguém o toca —por
enquanto vazia—, e entre chaves o que se vê dentro do botão, que é outro `Text`.

**Rode o `verifica`.** A partir da raiz do repositório:

```bash
./lecciones/01-mi-primera-pantalla/verifica
```

Ele não altera o código: roda os testes simulados (`./gradlew testDebugUnitTest`) e traduz o resultado. Na primeira
vez precisa de rede e demora, porque baixa a biblioteca de testes (Robolectric) e uma cópia do Android: no ensaio de
13/11 foram 1 minuto e 21 segundos em um Mac; nas seguintes, segundos.

**Tudo vai bem** se aparecerem três marcas em verde e uma em vermelho:

```text
✅ Simulado: la pantalla muestra el nombre «La tienda»
✅ Simulado: la pantalla muestra el lema «Tus órdenes, en tu bolsillo»
✅ Simulado: hay un elemento «Entrar» con acción de toque y rol de botón
❌ Al tocar el botón «Entrar» no aparece «¡Hola!» (o ya estaba desde antes): ese cambio lo pides en el paso 4
Resultado: 3 de 4 pruebas en verde (1 en rojo, 0 con error, 0 saltadas), leído de …
```

*(Essa é a saída do `verifica`, exatamente como o script a imprime, em espanhol. Em português ela diz: a tela mostra o
nome; a tela mostra o slogan; há um elemento «Entrar» com ação de toque e papel de botão; e, em vermelho, «ao tocar o
botão «Entrar» não aparece «Olá!» (ou já estava ali antes): essa mudança é pedida no passo 4»; e o resultado, 3 de 4
testes em verde.)*

Esse vermelho é o esperado: o botão ainda não faz nada. A marca de `respuestas/01.md`, que ainda não foi preenchido,
também sai em vermelho.

**O que cada marca comprova, e o que não.** «Simulado» significa que o teste desenhou a função em memória e encontrou
esse texto, e que «Entrar» pode ser tocado e se declara botão (um `Text` ao qual só se acrescenta um toque não passa).
Não comprova o tamanho da letra, a margem, a centralização nem a ordem: isso se vê à mão, com
`./gradlew installDebug` e o esboço ao lado.

**O que comprova:** o `verifica` simulado encontra os três elementos **e** o app instalado foi comparado com o
esboço (objetivo 1.2).

---

## Passo 4 · Com o Claude Code: que o botão cumprimente

Aqui entra a ferramenta, e entra sempre do mesmo jeito: **prever → pedir → ler o que ele gerou → rodar o
`verifica` → explicar com palavras próprias.**

1. **Preveja.** Escreva em `respuestas/01.md`: para que «Olá!» apareça só depois de tocar o botão, o que a tela
   precisa lembrar? Qual das linhas já escritas vai mudar?
2. **Peça** ao Claude Code, de dentro de `inicio/`: «No `Bienvenida.kt`, faça com que, ao tocar em "Entrar", apareça o
   texto "¡Hola!" embaixo do botão. Antes de tocar, não deve aparecer. Não mude mais nada nem mexa nos testes.»
3. **Leia** a mudança completa antes de aceitá-la. Não precisa ser idêntica a esta, mas deve trazer estas três
   peças:

```kotlin
    var saludando by remember { mutableStateOf(false) }      // 1. o estado
    …
        Button(onClick = { saludando = true }) {             // 2. o evento
            Text("Entrar")
        }
        if (saludando) {                                     // 3. a tela lê o estado
            Text("¡Hola!")
        }
```

4. **Rode o `verifica`.** Agora os quatro testes devem sair em verde.
5. **Explique**, em duas linhas de `respuestas/01.md`, o que acontece entre o toque no botão e o aparecimento do
   cumprimento.

Hoje não se projeta estado do zero: apenas se rastreia a mudança que o Claude Code gerou. Que estado convém guardar e
como conservá-lo é a ideia central da L02. Para rastreá-la é preciso saber o que cada peça faz:

- **`mutableStateOf(false)`** cria um valor que o Compose vigia. Quando ele muda, o Compose programa a atualização
  da parte da tela que o leu. Isso se chama **recomposição**.
- **`remember { … }`** guarda esse valor enquanto esta tela continuar viva. Não é guardar em disco: se o Android
  recria a tela, ele volta a `false`.
- **`by`** permite usar `saludando` como uma variável normal. Pede dois `import`: `androidx.compose.runtime.getValue` e
  `androidx.compose.runtime.setValue`.
- **`onClick = { saludando = true }`** é o evento: só muda o estado. Não desenha nada.
- **`if (saludando) { Text("¡Hola!") }`** é Kotlin normal dentro da tela: se o estado é `true`, esse texto
  existe; se não, não.

A ideia da lição, completa: toca-se → o estado muda → o Compose atualiza o que leu esse estado, que aqui é
o `if` → a descrição da tela agora inclui o cumprimento. Ninguém «acrescenta» o texto. E o Compose pode voltar a
executar só o necessário: não se escreve código supondo que a função inteira vai rodar.

**Se alguma linha ainda não ficou clara,** pause aqui: peça ao Claude Code que a explique, comprove-a com uma
mudança pequena e o `verifica`, e continue quando for possível dizê-la com palavras próprias.

---

## Passo 5 · Sem IA: explicar cinco blocos e corrigir um

Este é o exercício 1.3 e se faz **sem Claude Code**, sobre o `Bienvenida.kt` de quem estuda. Se o arquivo já cumpre o
esboço, use para a correção esta listagem, que é uma resposta possível ao mesmo pedido:

```kotlin
19  @Composable
20  fun Bienvenida() {
21      var saludando by remember { mutableStateOf(false) }
22
23      Column(
24          modifier = Modifier.fillMaxSize().padding(24.dp),
25          verticalArrangement = Arrangement.Center,
26          horizontalAlignment = Alignment.CenterHorizontally
27      ) {
28          Text("La tienda", style = MaterialTheme.typography.headlineLarge)
29          Text("Tus órdenes, en tu bolsillo")
30          if (saludando) {
31              Text("¡Hola!")
32          }
33          Button(onClick = { saludando = true }) {
34              Text("Entrar")
35          }
36      }
37  }
```

Com esta listagem o `verifica` dá 4 de 4 em verde (medido em 18/11/2026). E mesmo assim ela não cumpre o esboço.

**Fazem-se duas coisas, por escrito, em `respuestas/01.md`:**

1. **Explicar cinco blocos,** um de cada tipo: **estado**, **texto**, **modificador**, **evento** e **função**.
   De cada um, escrever a linha no formato `Bienvenida.kt:21` e o que muda **na tela** se for removido ou alterado.
   «Guarda um booleano» não é um efeito visível; «sem esta linha o cumprimento nunca aparece» é.
2. **Corrigir o bloco que não cumpre o esboço.** Dizer qual é e por que os testes não o detectam, consertá-lo,
   rodar o `verifica` e instalar o app de novo.

O `verifica` só comprova que foram anotadas pelo menos cinco referências no formato `Bienvenida.kt:N`; não avalia as
explicações nem a correção. E um 4 de 4 confirma comportamento, não aparência. O 1.3 está concluído quando
as quatro caixas podem ser marcadas:

- [ ] Estão os cinco tipos: estado, texto, modificador, evento e função.
- [ ] Cada um diz um efeito que se vê na tela.
- [ ] Foi explicado por que o cumprimento aparecia acima e por que os testes não o detectaram.
- [ ] Depois de movê-lo, o 4 de 4 continua em verde **e** no app instalado se viu «Olá!» embaixo de «Entrar». Anote
      essa observação junto da correção.

**O que comprova:** exercício 1.3 (objetivo 1.3).

---

## Os exercícios

O 1.2 e o 1.3 se fazem **sem IA**.

| # | Faz-se | Está pronto quando |
|---|---|---|
| 1.1 | Prever o que a função do passo 1 desenha e rodá-la | Estão em mãos as três linhas e o que se viu |
| 1.2 | Escrever nome, slogan e botão a partir do esboço | O `verifica` marca em verde os três elementos **e** o app instalado foi comparado com o esboço: grupo centralizado e nome com letra grande |
| 1.3 | Explicar cinco blocos e corrigir o que não cumpre o esboço | As quatro caixas do passo 5 estão marcadas: o 4 de 4 continua em verde **e** o cumprimento apareceu embaixo do botão no app instalado |
| 1.4 | Responder à revisão de Kotlin | Quatro de cinco corretas, **cada uma com o respectivo porquê** |

**Revisão 1.4.** Responda sem rodar o código. O `verifica` não avalia as previsões nem esta revisão:
compare-as com «Compare as respostas» e guarde o que foi escrito.

1. `val nombre = "La tienda"` e, na linha seguinte, `nombre = "Otra"`. Compila?
2. `var saludando = false`. Ninguém escreveu o tipo. Qual é, e quem o decidiu?
3. `fun doble(n: Int) = n * 2`. O que `doble(4)` devolve, e de que tipo é?
4. No `MainActivity.kt` está `savedInstanceState: Bundle?`. O que esse `?` avisa?
5. `val lema: String? = null`. O que acontece com `lema.length`, com `lema?.length` e com `lema?.length ?: 0`?

**Tempo:** calcule umas duas horas, com a compilação e as previsões. É uma estimativa: será ajustada com o tempo que
a primeira turma levar.

---

## Não roda?

As mensagens em inglês são as que o compilador da série (Kotlin 2.4.21) deu em 18/11/2026.

| O que aparece | Causa provável | O que fazer |
|---|---|---|
| `Unresolved reference 'Button'` | Falta o `import` desse nome, ou está escrito errado | Acrescentar o `import` do passo 3 e conferir maiúsculas |
| `'val' cannot be reassigned` | O estado foi declarado com `val` e depois é alterado | É `var saludando by remember { … }` |
| `Property delegate must have a 'getValue(…)' method` | Usou-se `by` sem os dois `import` | Acrescentar `getValue` e `setValue` (passo 4) |
| «Las pruebas no llegaron a correr» (os testes não chegaram a rodar) | O código não compila | Ler a linha que começa com `e:`: traz arquivo, linha e coluna |
| ❌ no botão, com «Entrar» visível | É um `Text`, solto ou com `clickable`: não se declara botão | Tem que ser um `Button` com o `Text` dentro |
| ❌ no cumprimento, embora «Olá!» apareça | O cumprimento está sempre ali, não só depois do toque | Vai dentro do `if (saludando)` |
| `BienvenidaTest.kt` mudou | Alguém (quem estuda ou o Claude Code) mexeu nos testes | Recuperá-lo com o comando que a mensagem dá e repetir o pedido, pedindo que não os toque |

No Linux Mint 22.3 (x86_64), o `verifica` desta lição saiu em verde sobre a solução: 4 de 4, em 64 segundos contando a primeira compilação (18/11/2026). Se o SDK não traz `build-tools`, o Gradle os baixa sozinho na primeira vez.

---

## Para fechar

**Responda sem reler:**

1. O que é uma tela no Compose, em uma frase?
2. Toca-se em «Entrar» e aparece «Olá!». O que aconteceu no meio, em ordem?
3. Os quatro testes simulados estão em verde. Diga uma coisa que isso comprova e uma que não.

**O que vem a seguir:** na L02 o app tem mais de uma tela e passa de uma a outra.

---

## Compare as respostas

**Passo 1.** Dois textos, um embaixo do outro (`Column`), na metade da altura (`Arrangement.Center`) e do lado esquerdo
(ninguém indicou o alinhamento horizontal).

**Passo 3.** Quatro chamadas: `Text`, `Text`, `Button` e, dentro do botão, outro `Text`.

**Exercício 1.3**, sobre a listagem do passo 5:

| Bloco | Linha | Efeito visível |
|---|---|---|
| Estado | `Bienvenida.kt:21` | Sem ela o cumprimento não pode aparecer depois do toque |
| Texto | `Bienvenida.kt:28` | É o nome; sem `style` apareceria do mesmo tamanho que o slogan |
| Modificador | `Bienvenida.kt:24` | Sem `fillMaxSize()` a coluna só ocupa o que o conteúdo mede: já não há espaço em que centralizar |
| Evento | `Bienvenida.kt:33` | Com `onClick = { }` o botão pode ser tocado, mas nada acontece |
| Função | `Bienvenida.kt:20` | É a tela completa; o `MainActivity` a chama |

O bloco que não cumpre é o das linhas 30 a 32: está **antes** do botão, então o cumprimento aparece acima de
«Entrar» e o esboço o pede embaixo. Corrige-se movendo essas três linhas para depois da 35. Os testes não o
detectam porque verificam que o texto exista e apareça, não em que posição está.

**Revisão 1.4:**

1. Não. Um `val` não pode ser reatribuído; o compilador diz `'val' cannot be reassigned`.
2. `Boolean`. O Kotlin o deduziu a partir do valor `false`.
3. Devolve `8`, do tipo `Int`: o corpo é uma expressão e o tipo de retorno é inferido.
4. Que esse parâmetro pode chegar como `null`, e o Kotlin não permite usá-lo sem considerar esse caso.
5. `lema.length` não compila; `lema?.length` dá `null`; `lema?.length ?: 0` dá `0`.

**Para fechar:**

1. Uma função marcada com `@Composable` que descreve o que se vê.
2. O `onClick` mudou o estado para `true`; o Compose voltou a rodar a função; o `if` agora inclui o texto.
3. Comprova que os três elementos e o cumprimento existem e que o botão responde. Não comprova como aparecem nem em que
   ordem estão.

---

## Fontes da lição

Abertas em 18/11/2026.

- Android Developers. *Thinking in Compose*. https://developer.android.com/develop/ui/compose/mental-model
- Android Developers. *Jetpack Compose Tutorial*. https://developer.android.com/develop/ui/compose/tutorial
- Android Developers. *Compose layout basics*. https://developer.android.com/develop/ui/compose/layouts/basics
- Android Developers. *Column* (valores padrão). https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/Column.composable
- Android Developers. *Compose modifiers*. https://developer.android.com/develop/ui/compose/modifiers
- Android Developers. *State and Jetpack Compose*. https://developer.android.com/develop/ui/compose/state
- Android Developers. *Button*. https://developer.android.com/develop/ui/compose/components/button
- Android Developers. *Style text* e *Material Design 3 in Compose*. https://developer.android.com/develop/ui/compose/text/style-text · https://developer.android.com/develop/ui/compose/designsystems/material3
- Android Developers. *Support different pixel densities* (dp). https://developer.android.com/training/multiscreen/screendensities
- Android Developers. *Test your Compose layout*. https://developer.android.com/develop/ui/compose/testing
- Android Developers. *Role* e *SemanticsMatcher* (papel de botão nos testes). https://developer.android.com/reference/kotlin/androidx/compose/ui/semantics/Role · https://developer.android.com/reference/kotlin/androidx/compose/ui/test/SemanticsMatcher
- Android Developers. *Robolectric strategies*. https://developer.android.com/training/testing/local-tests/robolectric
- Kotlin. *Basic syntax*. https://kotlinlang.org/docs/basic-syntax.html
- Kotlin. *Null safety*. https://kotlinlang.org/docs/null-safety.html
- Kotlin. *Higher-order functions and lambdas*. https://kotlinlang.org/docs/lambdas.html
- Medição própria: ensaio em um Mac com Apple silicon, 13/11/2026 (memória do Gradle, `gradle wrapper`, Java e Android dos testes simulados) e 18/11/2026 (o `verifica` sobre `inicio/`, `solucion/` e a listagem do 1.3; mensagens do compilador).

Revisão externa (18/11/2026): COM ALTERAÇÕES, 3 de prioridade alta, 5 média e 2 baixa; aplicada.
