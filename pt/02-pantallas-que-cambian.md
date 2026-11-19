# Lição 02 — Telas que mudam

**Antes de ler, sete palavras.** Uma linha cada uma; as demais da série chegam na lição em que são usadas.

- **Estado:** um dado que a tela lembra e que pode mudar. Apareceu na 01; aqui é o tema.
- **Recomposição:** quando o Compose volta a executar as funções para atualizar a tela porque um dado mudou.
- **Evento:** algo que acontece e de que o app toma conhecimento, como um toque em um botão.
- **Elevar o estado:** tirar um estado de uma tela e guardá-lo na função que a chama.
- **Interface:** uma lista do que se pode pedir a algo, sem dizer como isso é resolvido.
- **Implementação:** uma classe que de fato resolve o que a interface pede. Pode haver várias.
- **Dublê de teste:** uma implementação feita só para os testes, que responde o que o teste precisa.

**O que fica com quem termina:** um app com três telas —boas-vindas, entrada e início— que passa de uma a outra, e saber
dizer por que a tela mudou a cada vez: que valor mudou, quem o mudou e quem o leu.

**Ideia nova desta lição:** o estado. Quando muda um valor de estado que a tela leu, o Compose **programa uma
recomposição** para atualizar o necessário, e a tela é desenhada de novo. Passar de uma tela a outra é isso mesmo: um
valor que muda. Pratica-se também uma coisa de apoio, que também se comprova: separar a decisão de entrar em um
contrato, com uma implementação só para os testes (passo 3).

**Uma nota sobre os textos do app.** O projeto do curso traz os textos de tela em espanhol, e os arquivos de
`lecciones/` não são traduzidos: as verificações automáticas do curso (o `verifica`) procuram esses textos exatos.
Por isso, nos blocos de código, nas saídas de terminal e nos esboços de tela, os textos ficam como o projeto os tem;
na prosa, a lição os cita em português e, na primeira vez, traz o original entre parênteses: as telas «Entre na conta»
(«Entra a tu cuenta»), «Olá, Ana» («Hola, Ana») e «Aqui aparecem os pedidos» («Aquí verás tus órdenes»), o botão
«Continuar» («Continuar») e o aviso «Ainda não é possível entrar» («Todavía no se puede entrar»). Os da lição 01 são os
mesmos: «A loja» («La tienda»), «Entrar» («Entrar») e «Olá!» («¡Hola!»).

---

## A cena

As três telas estão funcionando. Antes de fechar, revisa-se o arquivo para deixá-lo limpo e uma palavra atrapalha:
`remember`. A linha se lê igual sem ela. Apaga-se.

Compila. Abre-se o app: a tela de boas-vindas está idêntica. Toca-se em «Entrar».

Nada acontece. Não há mensagem de erro. A tela de boas-vindas continua ali. Toca-se de novo. Nada.

Essa mudança existe e está medida: é o «Quebre uma de propósito» do passo 4. Com essa única palavra apagada, os testes
simulados da lição passam de 7 de 7 em verde para 1 de 7, com 6 em vermelho e nenhum com erro (18 de novembro de
2026). Nada explodiu. A tela deixou de mudar, e o código não diz por quê.

A resposta está em uma pergunta: quando se toca em um botão, onde fica guardado o que aconteceu? Primeiro se prevê o que
faz um valor de estado, depois se constrói o percurso com ele, e no final quebra-se tudo de propósito para explicar.

---

## Antes de começar

**O que é preciso:** a [01](01-mi-primera-pantalla.md) concluída —aqui não se explica de novo `Column`, `Text` nem
`Button`— e o ambiente da [00](00-ambiente-listo.md): `ANDROID_HOME` definido e um emulador ou um telefone Android.

**O projeto de início.** A partir da raiz do repositório da série:

```bash
git pull
cd lecciones/02-pantallas-que-cambian/inicio
```

É ali que se trabalha. Ao lado está `solucion/`: serve no final para contrastar as decisões e entender qualquer diferença. As previsões e explicações
vão em `respuestas/02.md`, na raiz do repositório (`mkdir -p respuestas` se não existir).

**O que o projeto já traz (andaime).** Não se avalia aqui:

| O que traz | Onde | Por quê |
|---|---|---|
| A tela de boas-vindas da 01 | `Bienvenida.kt` | Parte-se do que já foi escrito |
| Os testes | `app/src/test/…/RecorridoTest.kt` e `ArranqueTest.kt` | São os que o `verifica` roda. Não os altere |
| Memória, `gradlew`, Android 36 para os testes e Java 25 | Igual à 01 | Foi explicado na [01](01-mi-primera-pantalla.md) |

**A navegação também é andaime.** O Android tem uma biblioteca para se mover entre telas, a Navigation. Aqui
ela não se usa: o percurso é montado com um valor de estado e um `when`, que são entregues escritos no passo 2.

**O esboço.** Três telas, uma de cada vez:

```text
┌──────────────────────┐    ┌──────────────────────┐    ┌──────────────────────┐
│      La tienda       │    │  Entra a tu cuenta   │    │      Hola, Ana       │
│ Tus órdenes, en tu   │ ─▶ │    [ Continuar ]     │ ─▶ │ Aquí verás tus       │
│      bolsillo        │    │ Todavía no se puede  │    │ órdenes              │
│     [ Entrar ]       │    │ entrar (si no pasó)  │    │                      │
└──────────────────────┘    └──────────────────────┘    └──────────────────────┘
   boas-vindas (bienvenida)        entrada (entrada)           início (inicio)
```

*(No esboço, «si no pasó» quer dizer «se não passou».)*

Da entrada ao início passa-se tocando em «Continuar», **só se alguém decide que a passagem é permitida**. Quem decide é
o passo 3.

---

## Passo 1 · Preveja o que faz um valor de estado

Este passo é de quem estuda, sem Claude Code. Abra `app/src/main/java/mx/habil/ejemplo/tienda/Bienvenida.kt`. É a tela
da 01, com estas três peças:

```kotlin
    var saludando by remember { mutableStateOf(false) }      // o estado
    …
        Button(onClick = { saludando = true }) {             // o evento o muda
            Text("Entrar")
        }
        if (saludando) {                                     // a tela o lê
            Text("¡Hola!")
        }
```

**Preveja primeiro.** Troque `saludando = true` por `saludando = !saludando` (o `!` inverte: de `false` a `true`
e de volta). Escreva em `respuestas/02.md`, antes de rodar qualquer coisa: o que aparece depois do primeiro toque, do
segundo e do terceiro?

**Agora comprove.** Com o emulador aberto ou o telefone conectado:

```bash
./gradlew installDebug      # compila e instala; não abre o app
```

Abra o app, toque três vezes e compare. Se a previsão falhou, não a apague: anote embaixo que linha teria dado
a resposta.

**O que aconteceu, em ordem.** É a cadeia de toda a lição:

1. Toca-se no botão: esse é o **evento**.
2. O `onClick` muda o valor. Não desenha nada.
3. O Compose vigia esse valor porque ele nasceu de `mutableStateOf`. Ao mudar, **programa uma recomposição**: voltar a
   executar o que o leu e precisa ser atualizado.
4. Neste exemplo, a função roda de novo. O `remember` devolve a ela o valor guardado, não um novo.
5. O `if` lê o valor de agora, e a descrição da tela sai diferente.

Não se escreve código supondo quantas vezes nem em que ordem uma função `@Composable` será executada: segundo a
documentação, o Compose pode pular as que não mudaram e executar outras muitas vezes.

Ninguém «acrescenta» nem «retira» o cumprimento: a função descreve a tela para o valor daquele momento.

Antes de seguir, devolva à mão a linha do botão a `Button(onClick = { saludando = true })`. Assim se conserva
qualquer outra mudança que tenha sido feita. Para confirmar que o arquivo ficou como veio, este comando não deve
mostrar nada (só olha, não altera o arquivo):

```bash
git diff -- app/src/main/java/mx/habil/ejemplo/tienda/Bienvenida.kt
```

**O que comprova:** a previsão escrita, comparada com a execução (objetivo 2.1).

---

## Passo 2 · À mão: um valor decide que tela aparece

Também sem Claude Code. Escrevem-se duas telas e a função que escolhe qual delas é desenhada.

**Preveja primeiro.** Se «em que tela estou» é um valor de estado, em qual função ele deve viver: em
`Bienvenida`, na tela de entrada ou em uma terceira que chame as duas? Escreva qual e por quê.

**1. A tela de boas-vindas deixa de guardar estado e avisa.** No `Bienvenida.kt`, apague a linha de `saludando`, o bloco
`if (saludando) { … }` e os quatro `import` de `androidx.compose.runtime` que não sejam `Composable`. Mudam duas
linhas:

```kotlin
fun Bienvenida(alEntrar: () -> Unit) {
    …
        Button(onClick = alEntrar) {
```

`alEntrar: () -> Unit` é um parâmetro cujo valor é uma função: não recebe nada (`()`) e não devolve um dado
(`Unit`). A tela a executa ao tocar o botão, e não sabe o que acontece depois.

**2. A tela de entrada.** Crie `PantallaEntrada.kt` na mesma pasta. Ele começa com `package
mx.habil.ejemplo.tienda` e os mesmos `import` do `Bienvenida.kt`:

```kotlin
@Composable
fun PantallaEntrada(rechazada: Boolean, alContinuar: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Entra a tu cuenta", style = MaterialTheme.typography.headlineLarge)
        Button(onClick = alContinuar) {
            Text("Continuar")
        }
        if (rechazada) {
            Text("Todavía no se puede entrar")
        }
    }
}
```

Ela também não guarda nada: recebe um dado (`rechazada`) e avisa de um toque (`alContinuar`).

**3. A função que guarda o estado.** Crie `Tienda.kt`:

```kotlin
package mx.habil.ejemplo.tienda

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

enum class Pantalla { BIENVENIDA, ENTRADA, INICIO }

@Composable
fun Tienda() {
    var pantalla by remember { mutableStateOf(Pantalla.BIENVENIDA) }

    when (pantalla) {
        Pantalla.BIENVENIDA -> Bienvenida(alEntrar = { pantalla = Pantalla.ENTRADA })
        Pantalla.ENTRADA -> PantallaEntrada(rechazada = false, alContinuar = { })
        Pantalla.INICIO -> Text("Aquí va el inicio")
    }
}
```

- **`enum class Pantalla { … }`** é uma lista fechada: uma `Pantalla` só pode ser uma dessas três.
- **`when (pantalla) { … }`** executa o ramo desse valor. Se faltar um dos três, não compila.
- **`alEntrar = { pantalla = Pantalla.ENTRADA }`** é a função que se passa à tela de boas-vindas. Ela avisa; aqui
  o estado é alterado.

**4. Que o app inicie em `Tienda`.** No `MainActivity.kt`, a linha de `setContent` passa a ser
`setContent { Tienda() }`.

**Comprove no app.** `./gradlew installDebug`, abra-o e toque em «Entrar»: deve ficar a tela «Entra a tu cuenta» e a de
boas-vindas já não deve aparecer. «Continuar» ainda não faz nada.
Com a solução pronta, no emulador do teste (Pixel 9, Android 37, 18/11/2026) viu-se isto: a tela de boas-vindas
com «La tienda», «Tus órdenes, en tu bolsillo» e «Entrar»; ao tocar em «Entrar», «Entra a tu cuenta» e «Continuar»; e ao
tocar em «Continuar», o aviso «Todavía no se puede entrar». A tela de início não aparece no app instalado: só
chegam a ela os testes.

**O que acabou de ser feito.** O estado não ficou em nenhuma tela: foi levado à função que as chama. Isso é
**elevar o estado**, e a documentação do Android o recomenda: o estado vive no ancestral comum mais próximo
de quem o lê e de quem o muda; para baixo viajam dados, para cima viajam eventos.

**O `verifica` ainda não consegue rodar os testes.** A partir da raiz do repositório:

```bash
./lecciones/02-pantallas-que-cambian/verifica
```

Este vermelho é o esperado **somente** ao terminar o passo 2, e somente porque ainda faltam `Entrada`, `EntradaFalsa` e
o parâmetro `entrada` de `Tienda`. **Tudo vai bem** se entre as linhas aparecerem estas (medido em 18/11/2026; as do
Gradle se repetem várias vezes):

```text
❌ No encontré «interface Entrada» en src/main: sin ella las pruebas no compilan (paso 3)
✅ Texto: en src/main hay un «fun Tienda(»
❌ No encontré «class EntradaFalsa» en src/test: la escribes tú en EntradaFalsa.kt (paso 3)
ℹ️  Estado guiado del paso 2: arriba faltan piezas del paso 3, y mientras falten las pruebas no compilan.
❌ Las pruebas no llegaron a correr. Esto dijo Gradle:
     e: RecorridoTest.kt:45:38 No parameter with name 'entrada' found.
     e: RecorridoTest.kt:45:48 Unresolved reference 'EntradaFalsa'.
Resultado: 0 pruebas corridas.
```

*(Essa é a saída do `verifica`, exatamente como o script a imprime, em espanhol. Em português ela diz: «Não encontrei
«interface Entrada» em src/main: sem ela os testes não compilam (passo 3)»; «Texto: em src/main há um «fun
Tienda(»»; «Não encontrei «class EntradaFalsa» em src/test: ela é escrita em EntradaFalsa.kt (passo 3)»; «Estado
guiado do passo 2: acima faltam peças do passo 3, e enquanto faltarem os testes não compilam»; «Os testes não chegaram
a rodar. Isto foi o que o Gradle disse»; e «Resultado: 0 testes executados».)*

Se as linhas do Gradle mencionam outro arquivo —`Tienda.kt`, `Bienvenida.kt`, `PantallaEntrada.kt`—, ou se o passo 3
já foi iniciado, não é este vermelho: é um erro que precisa ser corrigido antes de continuar. Por exemplo, com
`recuerda` escrito no lugar de `remember`, o Gradle disse `e: Tienda.kt:14:21 Unresolved reference 'recuerda'.`
(medido em 18/11/2026). E como os testes ainda não rodam, este passo se comprova no app instalado.

**O que comprova:** no app instalado, «Entrar» leva à tela de entrada (primeira metade do objetivo
2.2).

---

## Passo 3 · Com o Claude Code: quem decide se a entrada é permitida

Falta o início, e falta decidir quem pode chegar a ele. Hoje não há provedor (o Google chega na lição 04), e
mesmo assim os testes precisam percorrer as três telas.

Por isso essa decisão não se escreve dentro de nenhuma tela. Ela é declarada à parte, como **interface**: «a isto se
pode pedir que entre, e responde com o nome de quem entrou ou com nada». E escrevem-se duas **implementações**:

| Implementação | Onde vive | O que responde | Quem a usa |
|---|---|---|---|
| `EntradaSinProveedor` | `app/src/main` (o app) | Sempre «não»: `null` | O app instalado |
| `EntradaFalsa` | `app/src/test` (os testes) | Sempre «sim», com o nome que lhe for dado | Só os testes |

`EntradaFalsa` é um **dublê de teste**. Para que o app real não deixe passar ninguém sem provedor, mantém-se
esta regra: **o app não traz nenhuma forma de entrar sem provedor.** Segundo a documentação do Android, um dublê
que vive no código de testes não viaja com o app.

Aqui entra a ferramenta, e entra sempre do mesmo jeito: **prever → pedir → ler o que ele gerou → rodar o
`verifica` → explicar com palavras próprias.**

1. **Preveja.** Escreva em `respuestas/02.md`: dos arquivos `Bienvenida.kt`, `PantallaEntrada.kt`, `Tienda.kt` e
   `MainActivity.kt`, quais vão mudar e quais não?
2. **Peça** ao Claude Code, de dentro de `inicio/`: «Crie `Entrada.kt` com uma interface `Entrada` que tenha
   `fun entrar(): String?` e uma classe `EntradaSinProveedor` que devolva `null`. Crie `Inicio.kt` com um
   composable `Inicio(nombre: String)` que mostre "Hola, " seguido do nome e, embaixo, "Aquí verás tus
   órdenes". Faça `Tienda` receber `entrada: Entrada`: ao tocar em "Continuar" ela chama `entrada.entrar()`; se
   devolver um nome, passa ao início com esse nome; se devolver `null`, fica onde está e põe `rechazada` em `true`.
   No `MainActivity` use `EntradaSinProveedor()`. Em `app/src/test`, ao lado de `RecorridoTest.kt`, crie
   `EntradaFalsa.kt`: ela recebe um nome, devolve-o sempre e conta em `veces` quantas vezes pediram que entrasse.
   Não mude `RecorridoTest.kt` nem as duas telas que já existem.»
3. **Leia** a mudança completa antes de aceitá-la. O corpo das funções pode variar. O que os testes
   chamam pelo nome é parte do contrato desta lição e tem que ficar exatamente assim: `Entrada` com
   `fun entrar(): String?`, `EntradaSinProveedor()`, `Tienda(entrada = …)`, `EntradaFalsa(nombre)` com o
   contador `veces`, e os textos das telas. Com outros nomes o projeto pode ser igualmente bom, mas
   estes testes não compilam. A mudança deve trazer estas quatro peças:

```kotlin
// Entrada.kt — o contrato e a implementação do app
interface Entrada {
    fun entrar(): String?            // o nome de quem entrou, ou null se não conseguiu
}

class EntradaSinProveedor : Entrada {
    override fun entrar(): String? = null
}
```

```kotlin
// EntradaFalsa.kt — em app/src/test, não em app/src/main
class EntradaFalsa(private val nombre: String) : Entrada {
    var veces = 0

    override fun entrar(): String? {
        veces++
        return nombre
    }
}
```

```kotlin
// Tienda.kt — recebe a entrada e guarda três valores de estado
@Composable
fun Tienda(entrada: Entrada) {
    var pantalla by remember { mutableStateOf(Pantalla.BIENVENIDA) }
    var nombre by remember { mutableStateOf<String?>(null) }
    var rechazada by remember { mutableStateOf(false) }

    when (pantalla) {
        Pantalla.BIENVENIDA -> Bienvenida(alEntrar = { pantalla = Pantalla.ENTRADA })
        Pantalla.ENTRADA -> PantallaEntrada(
            rechazada = rechazada,
            alContinuar = {
                val quien = entrada.entrar()
                if (quien != null) {
                    nombre = quien
                    pantalla = Pantalla.INICIO
                } else {
                    rechazada = true
                }
            }
        )
        Pantalla.INICIO -> Inicio(nombre = nombre ?: "")
    }
}
```

```kotlin
// MainActivity.kt — o único lugar do app que nomeia uma implementação
        setContent { Tienda(entrada = EntradaSinProveedor()) }
```

   Quatro coisas a conferir na leitura:

   - **`: Entrada`** depois do nome da classe diz «esta classe cumpre essa interface», e **`override`** marca
     a função que a cumpre. Sem `override`, não compila.
   - **`entrada.entrar()` está dentro de `alContinuar`**, ou seja, dentro do evento. Solta no corpo de
     `Tienda`, seria executada toda vez que a função é desenhada, e o Compose pode executá-la muitas vezes.
   - **`Tienda` não diz qual entrada lhe coube.** Só conhece a interface.
   - No `Inicio.kt` aparece `"Hola, $nombre"`: o sinal `$` coloca o valor da variável dentro do texto.

4. **Rode o `verifica`.** Agora sim os testes rodam: sete marcas «Simulado» em verde —seis do percurso e
   uma da inicialização— e a linha `Resultado: 7 de 7 pruebas en verde (0 en rojo, 0 con error, 0 saltadas)`. Fica
   uma marca em vermelho, a de `respuestas/02.md`: ela se preenche no passo 4.
5. **Explique**, em três linhas de `respuestas/02.md`: o que acontece entre o toque em «Continuar» e o aparecimento de
   «Olá, Ana», nomeando os valores de estado que mudam.

*(As sete marcas e a linha de resultado são impressas pelo `verifica` em espanhol; o resultado quer dizer «7 de 7 testes
em verde (0 em vermelho, 0 com erro, 0 pulados)».)*

**Mostre que trocar a entrada não mexe nas telas.** Os testes já usaram `EntradaFalsa("Ana")`,
`EntradaFalsa("Luis")` e `EntradaSinProveedor()` com as mesmas três telas. Agora é a vez de mostrá-lo. De dentro de
`inicio/`, rode isto e cole a saída em `respuestas/02.md`:

```bash
grep -rlE "EntradaSinProveedor|EntradaFalsa" app/src | sort
```

Em `solucion/` a saída são quatro arquivos (medido em 18/11/2026): `Entrada.kt` e `MainActivity.kt` em
`src/main`, e `EntradaFalsa.kt` e `RecorridoTest.kt` em `src/test`. Nenhuma tela aparece. Escreva embaixo que
arquivos seriam tocados para adotar outra forma de entrar, e quais não.

**O que cada marca comprova, e o que não.**

- **«Simulado», as seis do percurso:** desenharam `Tienda` em memória com a entrada que o teste escolheu,
  tocaram os botões e procuraram os textos. Comprovam que tela fica depois de cada toque, com duas
  implementações.
- **«Simulado», a da inicialização:** abriu o `MainActivity`, a classe que o Android abre ao tocar o ícone, e tocou em
  «Entrar» e «Continuar». Comprova que por esse caminho o app não deixa passar.
- **«Texto»:** só procurou palavras nos arquivos, sem contar os comentários. Não comprova que o código faça
  o que essas palavras sugerem.

Nenhuma comprova que não exista **outra** forma de entrar sem provedor: uma segunda entrada com outro nome em
`src/main`, usada de outro lugar, passa em verde. Isso se confere à mão: leia o `MainActivity.kt` e confirme que ele chama
`Tienda(entrada = EntradaSinProveedor())`, e que em `src/main` não há outra classe que cumpra `Entrada`. Também não
comprovam a aparência das telas, o que faz o botão «voltar» nem nada do Google.

**No app instalado** chega-se até a tela de entrada, e «Continuar» deve responder «Todavía no se puede
entrar»: o app usa `EntradaSinProveedor`. Ao início só chegam os testes.

**Se alguma linha ainda não ficou clara,** antes de seguir tente explicá-la com palavras próprias. Se ainda não sair,
volte a esse trecho ou peça um esclarecimento.

**O que comprova:** o `verifica` simulado percorre as três telas (objetivo 2.2) e o faz com duas
implementações de `Entrada`, mais a saída do `grep` e a leitura do `MainActivity.kt` (objetivo 2.4).

---

## Passo 4 · Sem IA: quebre uma de propósito

Este é o exercício 2.3 e se faz **sem Claude Code**. Com os sete testes em verde, vai-se provocar a cena
do início.

**1. Preveja.** No `Tienda.kt`, a linha do estado vai ficar assim, sem `remember`:

```kotlin
    var pantalla by mutableStateOf(Pantalla.BIENVENIDA)
```

Escreva em `respuestas/02.md`: compila? O que aparece ao abrir o app? O que acontece ao tocar em «Entrar»?

**2. Quebre e meça.** Faça a mudança e rode o `verifica`. Isto foi o que deu em 18/11/2026:

```text
✅ Simulado: la app empieza en la bienvenida, con «La tienda» y el botón «Entrar», y sin nada de las otras dos
❌ Al tocar el botón «Entrar» no aparece la pantalla de entrada con «Entra a tu cuenta» y un botón «Continuar» …
❌ conLaEntradaFalsaLlegaAlInicio quedó en rojo, pero depende de la marca anterior …
   (y cuatro más iguales)
Resultado: 1 de 7 pruebas en verde (6 en rojo, 0 con error, 0 saltadas), leído de …
```

*(O espanhol é a saída do `verifica`, como é impressa. Em português: «Simulado: o app começa na tela de
boas-vindas, com «La tienda» e o botão «Entrar», e sem nada das outras duas»; «Ao tocar o botão «Entrar» não aparece a
tela de entrada com «Entra a tu cuenta» e um botão «Continuar» …»; «… ficou em vermelho, mas depende da marca
anterior …»; «(e mais quatro iguais)»; «Resultado: 1 de 7 testes em verde (6 em vermelho, 0 com erro, 0 pulados), lido
de …».)*

Copie para `respuestas/02.md` a linha vermelha que começa com «Al tocar el botón «Entrar» no aparece la pantalla de
entrada». Depois instale o app e veja com os próprios olhos.

**3. Explique por quê**, com palavras próprias, antes de ler o que segue.

O que diz a documentação do Android: `remember` guarda um valor na memória da tela e, a cada
recomposição, devolve **esse mesmo** valor. Sem `remember`, a linha volta a ser executada inteira. A cadeia,
deduzida dessa documentação (o `verifica` mede o resultado, não cada passo), fica assim:

1. Toca-se em «Entrar». O evento muda o valor para `ENTRADA`.
2. O Compose toma conhecimento, porque o valor continua nascendo de `mutableStateOf`, e volta a executar `Tienda`.
3. `Tienda` roda desde o início e a primeira linha cria um valor **novo**, em `BIENVENIDA`.
4. O `when` lê esse valor novo. A tela de boas-vindas é desenhada de novo.

O valor de fato mudou. O que se perdeu foi o lugar em que ele era guardado.

**4. Outra forma de quebrar.** Deixe a linha como uma variável comum: `var pantalla = Pantalla.BIENVENIDA`. O
`verifica` dá o mesmo 1 de 7 (medido em 18/11/2026), por outra razão: segundo a documentação, o Compose só
toma conhecimento das mudanças em valores que consegue observar. O valor muda e ninguém volta a desenhar.

**5. Conserte.** Devolva a linha a `var pantalla by remember { mutableStateOf(Pantalla.BIENVENIDA) }` e rode o
`verifica`: sete de sete, e agora também a marca de `respuestas/02.md`.

O `verifica` só comprova que a linha vermelha foi colada: não sabe se foi quebrado nem avalia a explicação. O 2.3
está concluído quando as quatro caixas podem ser marcadas:

- [ ] A previsão do ponto 1 está escrita, com o que se viu embaixo.
- [ ] Foram coladas a linha vermelha e o «Resultado» que apareceu.
- [ ] Foi explicado, passo a passo, por que a tela deixa de ser atualizada sem `remember`.
- [ ] Foi dito em que a segunda forma de quebrar se parece e em que não se parece.

**Um limite.** Segundo a documentação do Android, `remember` não conserva o valor em uma mudança de configuração,
como girar o telefone; para isso existe `rememberSaveable`. Esta lição não o usa nem o testa.
No emulador do teste (18/11/2026), ao girar o telefone estando em «Entra a tu cuenta», o app voltou à tela de
boas-vindas: o estado se perdeu, como diz a documentação. E «voltar», de qualquer uma das duas telas, fechou o
app e levou ao início do telefone: o percurso desta lição não trata «voltar».

**O que comprova:** «Quebre uma de propósito» (objetivo 2.3).

---

## Os exercícios

O 2.1, o passo 2 e o 2.3 se fazem **sem IA**.

| # | Faz-se | Está pronto quando |
|---|---|---|
| 2.1 | Prever o que faz o estado do passo 1 e rodá-lo | Estão em mãos a previsão e o que se viu |
| 2.2 | Construir boas-vindas → entrada → início | O `verifica` dá 7 de 7 **e** no app instalado se viu «Todavía no se puede entrar» |
| 2.3 | Quebrar o estado e explicar por que a tela deixa de ser atualizada | A evidência são as quatro caixas do passo 4, escritas à mão. O `verifica` só confirma que o projeto foi consertado (7 de 7) e que o vermelho foi colado |
| 2.4 | Separar a decisão de entrar em `Entrada` e mostrar que trocá-la não mexe nas telas | Os testes com as duas implementações e o da inicialização estão em verde, a saída do `grep` foi colada **e** foi lido que `src/main` não traz outra entrada |

**Tempo:** calcule umas três horas, com o «Quebre uma de propósito». É uma estimativa: será ajustada com o tempo que a
primeira turma levar.

---

## Não roda?

As mensagens em inglês são as que o compilador da série (Kotlin 2.4.21) deu em 18/11/2026.

| O que aparece | Causa provável | O que fazer |
|---|---|---|
| `No value passed for parameter 'alEntrar'` | Alguém chama `Bienvenida()` sem a função | Passá-la: `Bienvenida(alEntrar = { … })` |
| `'when' expression must be exhaustive. Add the 'INICIO' branch or an 'else' branch.` | Ao `when` falta uma das três telas | Acrescentar o ramo que a mensagem indica |
| `'entrar' hides member of supertype 'Entrada' and needs an 'override' modifier.` | A classe cumpre a interface sem dizê-lo | Escrever `override fun entrar()` |
| `Smart cast to 'String' is impossible, because 'nombre' is a delegated property.` | Perguntou-se `if (nombre != null)` sobre um estado e usou-se como `String` | Guardar primeiro o resultado em um `val`, como `val quien = entrada.entrar()` |
| ❌ «EntradaFalsa aparece en src/main» (aparece em src/main) | A entrada que deixa passar ficou dentro do app | Movê-la para `app/src/test/java/mx/habil/ejemplo/tienda/` |
| ❌ em «solo al tocar Continuar» (só ao tocar em Continuar) | `entrada.entrar()` ficou fora do evento | Vai dentro de `alContinuar = { … }` |
| `RecorridoTest.kt` ou `ArranqueTest.kt` mudou | Alguém (quem estuda ou o Claude Code) mexeu nos testes | Ver o que mudou com o `git diff` que a mensagem dá e desfazer à mão. Restaurar o arquivo com o git descarta tudo o que ele tiver: fazê-lo só depois de revisar esse diff. Depois repetir o pedido, pedindo que não os toque |

No Linux Mint 22.3 (x86_64), o `verifica` desta lição saiu em verde sobre a solução: 7 de 7 (18/11/2026).

---

## Para fechar

**Responda sem reler:**

1. Toca-se em «Entrar» e a tela muda. O que aconteceu no meio, em ordem?
2. O que faz `remember`, e o que se vê no app se ele for retirado?
3. Os sete testes simulados estão em verde. Diga uma coisa que isso comprova e uma que não.
4. Por que `EntradaFalsa` vive em `src/test` e não em `src/main`?

**O que vem a seguir:** na L03 o app conhece o Firebase, e na L04 entra-se com o Google.

---

## Compare as respostas

**Passo 1.** Depois do primeiro toque aparece «Olá!», depois do segundo desaparece e depois do terceiro volta (medido em
18/11/2026 com um teste simulado).

**Passo 2.** Em uma terceira função que chame as duas, `Tienda`. Se o valor vivesse em `Bienvenida`, a tela de entrada
não poderia lê-lo, e ao deixar de ser desenhada a tela de boas-vindas o levaria consigo.

**Passo 3.** Mudam `Tienda.kt` e `MainActivity.kt`; `Bienvenida.kt` e `PantallaEntrada.kt` não: elas já só recebiam
dados e avisavam de toques. Entre o toque e «Olá, Ana»: `alContinuar` chama `entrada.entrar()`, que devolve
`"Ana"`; isso se guarda em `nombre` e `pantalla` passa a `INICIO`; o Compose volta a executar `Tienda` e o `when` escolhe
`Inicio`. Para adotar outra forma de entrar, com o que há nesta lição, escreve-se uma implementação nova e
muda-se a linha do `MainActivity.kt` que a escolhe; as telas e `Tienda.kt` não se tocam. Como fica com o
Google de verdade se vê na L04.

**Passo 4.** Compila, e os testes simulados encontram a tela de boas-vindas ao abrir; ao tocar em «Entrar» a tela não
muda. A segunda forma se parece na contagem (1 de 7) e não na causa: sem `remember`, o Compose volta a
desenhar com um valor recém-criado; com uma variável comum, nunca toma conhecimento.

**Para fechar:**

1. O evento mudou um valor de estado; o Compose voltou a executar a função que o leu; o `when` escolheu outra
   tela.
2. Guarda o valor entre uma recomposição e a seguinte. Sem ele, o app fica na tela de boas-vindas.
3. Comprova que tela fica depois de cada toque, com uma entrada que deixa passar e com uma que não, e que o app
   aberto a partir do `MainActivity` não deixa passar. Não comprova a aparência, o que faz o botão «voltar», que não haja
   outra forma de entrar por outro caminho, nem que o Google funcione.
4. Porque deixa qualquer um passar. Em `src/test` só os testes a usam e ela não viaja no app instalado.

---

## Fontes da lição

Abertas em 18/11/2026.

- Android Developers. *State and Jetpack Compose*. https://developer.android.com/develop/ui/compose/state
- Android Developers. *Thinking in Compose* (recomposição; os efeitos vão em eventos). https://developer.android.com/develop/ui/compose/mental-model
- Android Developers. *Lifecycle of composables* (o que dispara uma recomposição). https://developer.android.com/develop/ui/compose/lifecycle
- Android Developers. *Where to hoist state* (o ancestral comum mais próximo). https://developer.android.com/develop/ui/compose/state-hoisting
- Android Developers. *Navigation* (a biblioteca que esta lição não usa). https://developer.android.com/guide/navigation
- Android Developers. *Use test doubles in Android* (o que é um dublê; diz que o que vive no código de testes não viaja com o app). https://developer.android.com/training/testing/fundamentals/test-doubles
- Android Developers. *Android build structure* (diferença entre `src/main` e `src/test`). https://developer.android.com/build/android-build-structure
- Android Developers. *Build local unit tests* e *Test in Android Studio*. https://developer.android.com/training/testing/local-tests · https://developer.android.com/studio/test/test-in-android-studio
- Android Developers. *Test your Compose layout*. https://developer.android.com/develop/ui/compose/testing
- Android Developers. *SemanticsMatcher* e *Role* (papel de botão nos testes). https://developer.android.com/reference/kotlin/androidx/compose/ui/test/SemanticsMatcher · https://developer.android.com/reference/kotlin/androidx/compose/ui/semantics/Role
- Kotlin. *Interfaces*. https://kotlinlang.org/docs/interfaces.html
- Kotlin. *Enum classes*. https://kotlinlang.org/docs/enum-classes.html
- Kotlin. *Conditions and loops* (`when`). https://kotlinlang.org/docs/control-flow.html
- Kotlin. *Higher-order functions and lambdas* (tipos de função como `() -> Unit`) e *Functions* (`Unit`). https://kotlinlang.org/docs/lambdas.html · https://kotlinlang.org/docs/functions.html
- Kotlin. *Strings* (templates com `$`). https://kotlinlang.org/docs/strings.html
- Medição própria: Mac com Apple silicon, 18/11/2026 (o `verifica` sobre `inicio/` e `solucion/`; as duas formas de quebrar o estado; a previsão do passo 1; o estado intermediário do passo 2, com e sem uma errata; mensagens do compilador).

Revisão externa (18/11/2026): COM ALTERAÇÕES, 3 de prioridade alta, 4 média e 2 baixa; aplicada.
