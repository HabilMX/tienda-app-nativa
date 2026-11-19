# Prática 02-B — Catálogo de componentes da loja

**Antes de ler, seis palavras.** Uma linha cada uma.

- **Componente:** uma função `@Composable` pequena que se usa em várias telas, como um botão ou um cartão.
- **Variante:** o mesmo componente com outros dados ou em outro modo: desabilitado, no escuro, com um texto longo.
- **Preview:** uma função marcada com `@Preview` que o Android Studio desenha em um painel, sem emulador.
- **Catálogo:** uma tela dentro do app de depuração em que se veem todos os componentes e as variantes deles.
- **Teste de captura:** um teste que desenha um componente, tira uma imagem e a compara com uma **imagem de
  referência** guardada no repositório. Se um pixel mudou mais do que o tolerado, fica em vermelho.
- **`src/debug`:** uma pasta de código que só entra no app de depuração, nunca no que é publicado.

**O que fica com quem termina:** três componentes da loja —botão principal, cartão de produto e etiqueta do estado de um
pedido— revisados isolados nas variantes deles, colocados na tela de início da 02, com um catálogo no app de
depuração e onze testes de captura que ficam em vermelho quando alguém muda uma cor.

**Ideia nova desta prática:** um componente se desenha e se revisa **isolado**, em todas as variantes, antes de
ser usado em uma tela. É uma prática: vem depois da 02, fora da sequência principal, e é opcional para a
turma.

**Uma nota sobre os textos do app.** Como nas lições anteriores, o projeto do curso traz os textos de tela em espanhol,
e os arquivos de `lecciones/` não são traduzidos. Por isso, nos blocos de código, nas saídas de terminal e nos esboços
de tela, os textos ficam como o projeto os tem; na prosa, a prática os cita em português e, na primeira vez, traz o
original entre parênteses.

---

## A cena

Alguém da equipe muda uma linha: o verde da loja passa a vermelho, para testar um botão mais chamativo. Compila.
Os sete testes da 02 continuam em verde. Ninguém abre o app no modo escuro nem olha o cartão de um produto com
nome longo. Sobe.

Essa mudança está medida. Com `VerdeTienda` em vermelho, os sete testes da 02 deram 7 de 7 em verde, e os onze
testes de captura desta prática deram **6 de 11 em vermelho** (19 de novembro de 2026). Não mudou só o botão:
mudaram o preço do cartão e a etiqueta «Entregue» («Entregada»), também no escuro. Os testes da 02 medem que tela
fica depois de cada toque, e não olham cores.

A pergunta da prática: como ver todas as variantes de um componente sem percorrer o app, e como saber
quando uma muda sem que ninguém tenha querido?

---

## Antes de começar

**O que é preciso:** a [02](02-pantallas-que-cambian.md) concluída, o ambiente da [00](00-ambiente-listo.md)
(`ANDROID_HOME` e um emulador ou um telefone) e o Android Studio para o painel de previews.

**O projeto de início** é a solução da 02, com o que é desta prática já configurado:

```bash
git pull
cd lecciones/02b-catalogo-de-componentes/inicio
```

As respostas vão em `respuestas/02b.md`, na raiz do repositório (`mkdir -p respuestas` se não existir).

**O que o projeto já traz (andaime).** Não se avalia aqui:

| O que traz | Onde | Por quê |
|---|---|---|
| A paleta e a tipografia da loja | `Tema.kt` | A prática as fornece: ninguém as desenha. Não o altere, a não ser por um momento no passo 6 |
| As dependências e os plugins | `gradle/libs.versions.toml` e `app/build.gradle.kts` | Ver a tabela de versões abaixo |
| Os testes da 02 | `RecorridoTest.kt` e `ArranqueTest.kt` | Devem continuar em verde com os componentes no lugar. Não os altere |
| Memória, `gradlew`, Android 36 para os testes e Java 25 | Igual à 02 | Foi explicado na [01](01-mi-primera-pantalla.md) |

O `Tema.kt` define onze cores com nome (`VerdeTienda`, `AzulRecibida`, `AmbarEnviada`…), uma paleta clara e uma
escura, quatro estilos de letra e uma função `TemaTienda(oscuro) { … }` que envolve qualquer conteúdo com tudo isso.
Abra-o e leia-o antes do passo 1: será usado em cada passo.

**Por que o Roborazzi para as capturas.** Foi escolhido com um único critério: que continue funcionando igual para a
próxima turma. A ferramenta oficial do Google não o cumpre hoje (19/11/2026): a página dela diz que o plugin
`com.android.compose.screenshot` «já não é mantido», e a instalação ainda pede a opção
`android.experimental.enableScreenshotTest=true`. O Google recomenda migrar para as *test suites* do AGP, que exigem o AGP 9.6.0-alpha03 ou
superior, uma versão preliminar; esta série está fixada no AGP 9.4.1, e atualizá-lo é uma decisão à parte. O Paparazzi, a outra opção conhecida, está na
2.0.0-alpha05. O Roborazzi tem versão estável no Maven Central e roda sobre o Robolectric, que a série já usa desde a
01.

**As versões**, fixadas e testadas com esta prática em 19/11/2026. As do Roborazzi, Showkase, KSP e
`material-icons-core` eram as últimas publicadas naquele dia; o AGP 9.4.1 e o BOM 2026.09.00 da série não (já havia o
AGP 9.5.1 e o BOM 2026.11.01), e ficam como estão para não mexer na série no meio de uma turma:

| Peça | Versão | Para quê | Fonte |
|---|---|---|---|
| Roborazzi (`roborazzi`, `roborazzi-compose` e o plugin dele) | 1.79.0 | Testes de captura | Maven Central |
| Showkase (`showkase` e `showkase-processor`) | 1.0.5 | O catálogo | Maven Central e o README dele |
| KSP (Kotlin Symbol Processing) | 2.3.14 | Roda o gerador do Showkase ao compilar | Maven Central e as *releases* dele |
| `material-icons-core` | 1.7.8, a do BOM 2026.09.00 | Ícones que o Showkase usa por dentro | O BOM do Compose |
| Robolectric | 4.18 | Já vinha da 02 | — |

A 4.18.1 do Robolectric apareceu no Maven Central em 18/11/2026. Esta prática segue com a 4.18 da série: foi com ela que tudo foi medido.

**O esboço da tela de início**, com os três componentes embaixo da saudação da 02:

```text
┌──────────────────────────┐
│       Hola, Ana          │
│  Aquí verás tus órdenes  │
│ ┌──────────────────────┐ │
│ │ Audífonos            │ │  ← cartão de produto
│ │ $899.00              │ │
│ └──────────────────────┘ │
│        ( Enviada )       │  ← etiqueta de estado
│ [   Ver mis órdenes    ] │  ← botão principal
└──────────────────────────┘
```

*(No esboço, «Hola, Ana» é «Olá, Ana»; «Aquí verás tus órdenes», «Aqui aparecem os pedidos»; «Audífonos», «Fones de ouvido»;
«Enviada», «Enviada»; e «Ver mis órdenes», «Ver meus pedidos».)*

---

## Passo 1 · À mão: o botão principal

Sem o Claude Code. Crie `BotonPrincipal.kt` ao lado de `Inicio.kt`, em `app/src/main/java/mx/habil/ejemplo/tienda/`:

```kotlin
package mx.habil.ejemplo.tienda

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun BotonPrincipal(texto: String, alTocar: () -> Unit, habilitado: Boolean = true) {
    Button(
        onClick = alTocar,
        enabled = habilitado,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(texto, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
    }
}
```

- **`habilitado: Boolean = true`** é um parâmetro com valor padrão: quem não o passa recebe `true`.
- **`MaterialTheme.typography.labelLarge`** não diz que letra usar: pede o estilo `labelLarge` do tema que estiver
  em volta. Dentro de `TemaTienda`, é o do `Tema.kt`.
- **O botão não diz a própria cor.** O `Button` toma a cor primária do tema, que na paleta clara é `VerdeTienda`.

**Preveja.** Escreva em `respuestas/02b.md`, antes de seguir: o que muda na tela com `habilitado = false`? E com
um texto que não cabe em uma linha?

Ainda não dá para vê-lo: não há tela que o use. Será visto no painel do passo 3.

**O que comprova:** o componente compila e o `verifica` encontra «fun BotonPrincipal(» (parte do objetivo 2B.1).

---

## Passo 2 · Com o Claude Code: o cartão, a etiqueta e a tela de início

Aqui entra a ferramenta, sempre do mesmo jeito: **prever → pedir → ler o que ele gerou → rodar o `verifica` →
explicar com palavras próprias.**

1. **Preveja.** Escreva em `respuestas/02b.md`: quais destes arquivos vão mudar? `Inicio.kt`,
   `MainActivity.kt`, `Tienda.kt`, `Tema.kt`.
2. **Peça** ao Claude Code, parado em `inicio/`: «Crea `TarjetaProducto.kt` con un composable
   `TarjetaProducto(nombre: String, precio: String)`: una `Card` de ancho completo, esquinas de 16 dp y color de
   superficie del tema; adentro, el nombre con el estilo `titleMedium` en a lo más dos renglones y con «…» si no cabe,
   y debajo el precio en negritas con el color primario del tema. Crea `EtiquetaEstado.kt` con un
   `enum class EstadoOrden(val texto: String)` con `RECIBIDA("Recibida")`, `ENVIADA("Enviada")` y
   `ENTREGADA("Entregada")`; una función `colorDe(estado)` que regrese `AzulRecibida`, `AmbarEnviada` y `VerdeTienda`
   de `Tema.kt`; y un composable `EtiquetaEstado(estado: EstadoOrden)`: una píldora de ese color con el texto en blanco
   y el estilo `labelSmall`. En `Inicio.kt`, debajo de los dos textos, agrega una `TarjetaProducto` de "Audífonos
   inalámbricos" a "$899.00", una `EtiquetaEstado(EstadoOrden.ENVIADA)` y un `BotonPrincipal("Ver mis órdenes")`
   que por ahora no haga nada. En `MainActivity`, envuelve `Tienda` con `TemaTienda(oscuro = isSystemInDarkTheme())`.
   No cambies `Tema.kt`, `Tienda.kt` ni las pruebas.»

   *(Em português: crie `TarjetaProducto.kt` com um composable `TarjetaProducto(nombre: String, precio: String)`: um
   `Card` de largura total, cantos de 16 dp e cor de superfície do tema; dentro, o nome com o estilo `titleMedium` em no
   máximo duas linhas e com «…» se não couber, e embaixo o preço em negrito com a cor primária do tema. Crie
   `EtiquetaEstado.kt` com um `enum class EstadoOrden(val texto: String)` com `RECIBIDA("Recibida")`,
   `ENVIADA("Enviada")` e `ENTREGADA("Entregada")`; uma função `colorDe(estado)` que devolva `AzulRecibida`,
   `AmbarEnviada` e `VerdeTienda` do `Tema.kt`; e um composable `EtiquetaEstado(estado: EstadoOrden)`: uma pílula dessa
   cor com o texto em branco e o estilo `labelSmall`. Em `Inicio.kt`, embaixo dos dois textos, acrescente um
   `TarjetaProducto` de "Audífonos inalámbricos" a "$899.00", um `EtiquetaEstado(EstadoOrden.ENVIADA)` e um
   `BotonPrincipal("Ver mis órdenes")` que por ora não faça nada. Na `MainActivity`, envolva `Tienda` com
   `TemaTienda(oscuro = isSystemInDarkTheme())`. Não mude `Tema.kt`, `Tienda.kt` nem os testes.)*
3. **Leia** a mudança antes de aceitá-la. Os nomes acima são o contrato desta prática: as previews, o
   catálogo e as capturas os chamam assim. A peça mais difícil de ler é a etiqueta:

```kotlin
enum class EstadoOrden(val texto: String) {
    RECIBIDA("Recibida"),
    ENVIADA("Enviada"),
    ENTREGADA("Entregada"),
}

fun colorDe(estado: EstadoOrden): Color = when (estado) {
    EstadoOrden.RECIBIDA -> AzulRecibida
    EstadoOrden.ENVIADA -> AmbarEnviada
    EstadoOrden.ENTREGADA -> VerdeTienda
}

@Composable
fun EtiquetaEstado(estado: EstadoOrden) {
    Surface(shape = RoundedCornerShape(50), color = colorDe(estado)) {
        Text(
            estado.texto,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}
```

   Três coisas a conferir ao ler:

   - **`enum class EstadoOrden(val texto: String)`** é a lista fechada da 02, agora com um dado por valor:
     `EstadoOrden.ENVIADA.texto` é `"Enviada"`.
   - **`RoundedCornerShape(50)`** arredonda os cantos a 50 % da altura: por isso fica com forma de pílula.
   - **`"$899.00"` não insere nenhuma variável.** Em Kotlin, o `$` só monta um template se vier seguido de um nome ou
     de uma chave; seguido de um número, é o símbolo de pesos.

4. **Rode o `verifica`** a partir da raiz do repositório:

```bash
./lecciones/02b-catalogo-de-componentes/verifica
```

   Os sete testes da 02 devem continuar em verde: o início da 02 continua dizendo «Olá, Ana» («Hola, Ana») e tem mais três
   coisas. Medido em 19/11/2026 neste ponto: as marcas dos componentes, do `Inicio.kt` e da `MainActivity.kt` em
   verde, `Resultado: 7 de 7 pruebas en verde`, e em vermelho só as dos passos 3 a 6 (previews, `@ShowkaseRoot`,
   imagens, capturas, sabotagem e `respuestas/02b.md`). Esse vermelho é o esperado agora.

   *(Em português: «Resultado: 7 de 7 testes em verde».)*
5. **Explique** em duas linhas de `respuestas/02b.md`: por que `TarjetaProducto` não recebe nenhuma cor, e de onde ela
   a tira?

**O que comprova:** os três componentes compilam e a tela de início da 02 os usa, com os testes da 02 em
verde (objetivo 2B.1).

---

## Passo 3 · Sem IA: as variantes no painel de previews

Este é o exercício 2B.2 e se faz **sem o Claude Code**. O painel de previews do Android Studio desenha uma função
`@Preview` sem compilar o app completo nem abrir o emulador.

**1. Escreva a do botão.** No final de `BotonPrincipal.kt`, acrescente isto, com os respectivos `import` (`Box`, `padding`,
`Preview`, `PreviewParameter` e `PreviewParameterProvider`; o Android Studio os oferece):

```kotlin
data class VarianteBoton(
    val nombre: String, val texto: String, val habilitado: Boolean, val oscuro: Boolean
)

class VariantesBoton : PreviewParameterProvider<VarianteBoton> {
    private val lista = listOf(
        VarianteBoton("habilitado", "Comprar", habilitado = true, oscuro = false),
        VarianteBoton("deshabilitado", "Comprar", habilitado = false, oscuro = false),
        VarianteBoton("oscuro", "Comprar", habilitado = true, oscuro = true),
        VarianteBoton(
            "texto largo", "Agregar al carrito y seguir viendo la tienda",
            habilitado = true, oscuro = false
        ),
    )
    override val values = lista.asSequence()
    override fun getDisplayName(index: Int): String? = lista.getOrNull(index)?.nombre
}

@Preview(name = "Botón principal", group = "Botones", widthDp = 320)
@Composable
fun BotonPrincipalPreview(@PreviewParameter(VariantesBoton::class) variante: VarianteBoton) {
    TemaTienda(oscuro = variante.oscuro) {
        Box(Modifier.padding(16.dp)) {
            BotonPrincipal(variante.texto, alTocar = {}, habilitado = variante.habilitado)
        }
    }
}
```

- **`data class`** é uma classe para guardar dados: o Kotlin escreve sozinho como ela se compara e como se imprime.
- **`PreviewParameterProvider`** é uma interface, como a `Entrada` da 02: promete uma sequência de valores. A
  documentação do Android diz que se desenha **uma preview para cada valor**.
- **`getDisplayName`** dá nome a cada uma. Sem ele, segundo a documentação, elas se chamam pela posição (0, 1,
  2…). A lista por dentro é a recomendação dessa mesma página, para achar o nome pela posição.
- **`oscuro` vai na variante.** O modo escuro da preview não depende do computador nem do telefone: quem
  o decide é cada variante. Por isso as capturas do passo 5 saem iguais em qualquer máquina que desenhe igual.

**2. Escreva as outras duas com o mesmo molde.** Em `TarjetaProducto.kt`, `VarianteTarjeta(nombre, producto, precio,
oscuro)`, `VariantesTarjeta` e `TarjetaProductoPreview`, com `group = "Tarjetas"`. Em `EtiquetaEstado.kt`,
`VarianteEtiqueta(nombre, estado, oscuro)`, `VariantesEtiqueta` e `EtiquetaEstadoPreview`, com `group = "Etiquetas"` e
sem `widthDp`. Estas são as variantes, nesta ordem:

| Componente | Variantes (`nombre`) |
|---|---|
| Cartão | «claro» e «oscuro», com «Audífonos inalámbricos» a «$899.00»; «texto largo», com «Juego de seis tazas de cerámica esmaltada hechas a mano en Dolores Hidalgo, Guanajuato» a «$1,249.00» |
| Etiqueta | «recibida», «enviada» e «entregada», no claro; «entregada en oscuro» |

*(Os nomes das variantes e os textos entre «» ficam em espanhol, como o projeto os tem. Em português, os de claro e escuro são
«claro» e «escuro»; «Audífonos inalámbricos» são «Fones de ouvido sem fio»; e «Juego de seis tazas…» é «Jogo de seis xícaras
de cerâmica esmaltada feitas à mão em Dolores Hidalgo, Guanajuato».)*

**3. Preveja.** Antes de abrir o painel, escreva: quantas previews vão aparecer no total? Que cartão vai terminar em
«…»?

**4. Olhe.** Abra `BotonPrincipal.kt` no Android Studio e escolha a vista **Split** ou **Design**, no alto à direita
do editor; se ele pedir para compilar, aceite. Segundo a documentação, o painel desenha uma preview para cada valor do
provedor, com o nome que `getDisplayName` dá. Esta prática não mediu o painel no Android Studio: se aparecer
diferente, conte para nós.

Tire uma captura do painel com as quatro variantes do botão e guarde-a como `respuestas/02b-panel.png`. Embaixo da
previsão do passo 1, anote o que acertou.

**O que comprova:** a captura do painel e a previsão, comparadas (objetivo 2B.2). O `verifica` só confere que
cada componente traz o `@Preview` dele com `@PreviewParameter`: não vê o painel. E que isto tenha sido feito sem IA é uma regra de
trabalho que se declara na resposta: nem uma captura nem o `verifica` podem comprová-lo.

---

## Passo 4 · Com o Claude Code: o catálogo com o Showkase

O painel vive no Android Studio de quem estuda. O catálogo vive **no app**: qualquer pessoa da equipe o abre no próprio telefone, sem
o projeto aberto. O Showkase, do Airbnb, o monta com as mesmas `@Preview`: ao compilar, o KSP (um gerador de código
que roda junto com o compilador do Kotlin) procura as previews e escreve o código que as junta.

1. **Preveja.** Por que o catálogo deve ir em `src/debug` e não em `src/main`?
2. **Peça** ao Claude Code, parado em `inicio/`: «Crea `app/src/debug/java/mx/habil/ejemplo/tienda/Catalogo.kt` con
   una clase `CatalogoTienda` marcada con `@ShowkaseRoot` que implemente `ShowkaseRootModule`, y una
   `CatalogoActivity` que en `onCreate` abra `Showkase.getBrowserIntent(this)` y se cierre con `finish()`. Crea
   `app/src/debug/AndroidManifest.xml` que registre `CatalogoActivity` con su propio ícono de inicio y la etiqueta
   «Catálogo». No toques nada de `src/main`.»

   *(Em português: crie `Catalogo.kt` em `app/src/debug/java/mx/habil/ejemplo/tienda/` com uma classe `CatalogoTienda`
   marcada com `@ShowkaseRoot` que implemente `ShowkaseRootModule`, e uma `CatalogoActivity` que em `onCreate` abra
   `Showkase.getBrowserIntent(this)` e se feche com `finish()`. Crie `app/src/debug/AndroidManifest.xml` que registre
   `CatalogoActivity` com o próprio ícone de início e o rótulo «Catálogo». Não toque em nada de `src/main`.)*
3. **Leia** a mudança. Deve trazer estas peças:

```kotlin
@ShowkaseRoot
class CatalogoTienda : ShowkaseRootModule

class CatalogoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Showkase.getBrowserIntent(this))
        finish()
    }
}
```

   - **`@ShowkaseRoot`** marca a raiz do catálogo. O README do Showkase pede uma por módulo.
   - **`getBrowserIntent`** não existe até compilar: quem o escreve é o KSP. Se o Android Studio o pintar de vermelho, compile
     uma vez.
   - **As previews devem ser públicas ou `internal`.** O README diz: uma `private` não entra no catálogo.

4. **Olhe no emulador.** `./gradlew installDebug`. No telefone aparecem dois ícones: «A loja» («La tienda») e «Catálogo».
   Abra «Catálogo».

   No emulador do teste (Pixel 9, Android 37, 19/11/2026) viu-se isto: uma lista intitulada «Components» (os
   textos do Showkase vêm em inglês) com três grupos, «Botones (1)», «Etiquetas (1)» e «Tarjetas (1)». O «(1)»
   conta componentes, não variantes. Ao tocar em «Botones» e depois em «Botón principal» apareceram as quatro variantes:
   verde com «Comprar», cinza apagado, verde claro sobre fundo escuro, e o texto longo em duas linhas. As quatro
   levam o mesmo título, «Botón principal»: o Showkase não usou os nomes de `getDisplayName`. Nas etiquetas
   viram-se «Recibida» em azul, «Enviada» em âmbar e duas «Entregada» em verde, a segunda sobre fundo escuro.

5. **Explique** em `respuestas/02b.md` o que foi visto de diferente entre o painel do passo 3 e o catálogo.

**O que comprova:** o catálogo abre no emulador com os três grupos e as variantes deles (objetivo 2B.3). O `verifica`
confere o que o Showkase gerou ao compilar: a raiz do catálogo e os três grupos dele, que uma atividade de `src/debug`
o abre com o próprio ícone, e que `src/main` não o nomeia. Um texto que diga «@ShowkaseRoot» não gera nada e fica
em vermelho. O que se vê no emulador, quem conta é quem estuda, e a captura é a evidência.

---

## Passo 5 · Com o Claude Code: uma captura por preview

Ver as variantes serve enquanto alguém as olha. Um teste de captura as olha cada vez que os testes rodam.

1. **Preveja.** Quantas imagens de referência vão existir ao terminar?
2. **Peça** ao Claude Code, parado em `inicio/`: «Crea `app/src/test/java/mx/habil/ejemplo/tienda/CapturasTest.kt`:
   una prueba de Robolectric con `ParameterizedRobolectricTestRunner` y `@GraphicsMode(GraphicsMode.Mode.NATIVE)`,
   con un caso por cada variante de `VariantesBoton`, `VariantesTarjeta` y `VariantesEtiqueta`, llamados
   `boton_<nombre>`, `tarjeta_<nombre>` y `etiqueta_<nombre>` con los espacios cambiados por guion bajo. Cada caso
   dibuja su preview con `createComposeRule()` y llama a `onRoot().captureRoboImage("src/test/capturas/<caso>.png")`.
   La lista de casos debe salir de los mismos proveedores, no escrita a mano. No toques nada más.»

   *(Em português: crie `CapturasTest.kt` em `app/src/test/java/mx/habil/ejemplo/tienda/`: um teste do Robolectric com
   `ParameterizedRobolectricTestRunner` e `@GraphicsMode(GraphicsMode.Mode.NATIVE)`, com um caso por variante de
   `VariantesBoton`, `VariantesTarjeta` e `VariantesEtiqueta`, chamados `boton_<nombre>`, `tarjeta_<nombre>` e
   `etiqueta_<nombre>` com os espaços trocados por sublinhado. Cada caso desenha a preview dele com `createComposeRule()` e
   chama `onRoot().captureRoboImage("src/test/capturas/<caso>.png")`. A lista de casos deve sair dos mesmos provedores,
   não escrita à mão. Não toque em mais nada.)*
3. **Leia** a mudança. O coração é este:

```kotlin
@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CapturasTest(private val archivo: String, private val preview: @Composable () -> Unit) {
    // … previews(): um caso por variante, tirado dos provedores …

    @get:Rule
    val regla = createComposeRule()

    @Test
    fun captura() {
        regla.setContent { preview() }
        regla.onRoot().captureRoboImage("src/test/capturas/$archivo.png")
    }
}
```

   - **`ParameterizedRobolectricTestRunner`** roda o mesmo teste uma vez para cada caso da lista: onze casos,
     onze testes.
   - **`@GraphicsMode(NATIVE)`** pede ao Robolectric que desenhe pixels de verdade. O README do Roborazzi o exige.
   - **`captureRoboImage`** tira a imagem. O que se faz com ela depende da tarefa do Gradle que for rodada.

4. **Grave as referências.** Parado em `inicio/`:

```bash
./gradlew recordRoborazziDebug
```

   Ficam onze arquivos `.png` em `app/src/test/capturas/`. Abra-os: são os componentes de quem estuda, cada um na variante dele.
   Essas imagens **sobem para o repositório**: são o que a próxima rodada vai comparar.
5. **Rode o `verifica`.** Agora ele roda `verifyRoborazziDebug`, que compara cada captura com a referência dela. Com a
   solução, em 19/11/2026 deu `Resultado: 18 de 18 pruebas en verde`: as 7 da 02 e as 11 capturas.

   *(Em português: «Resultado: 18 de 18 testes em verde».)*

**Cuidado com a tarefa que se roda.** Segundo o README do Roborazzi, `recordRoborazziDebug` grava (ou volta a gravar) as
referências e `verifyRoborazziDebug` compara e falha se algo mudou. `./gradlew testDebugUnitTest` sozinho roda os
testes sem comparar nada: com a cor quebrada do passo 6 deu 18 de 18 em verde (medido em 19/11/2026). O `verifica`
usa `verify`.

**As referências de `solucion/` foram gravadas em um Mac.** Não se mediu se coincidem no Linux. No Linux,
gravem-se as referências na própria máquina com `recordRoborazziDebug`, como no ponto 4.

**O que comprova:** onze testes de captura em verde contra as referências deles (primeira parte do objetivo 2B.4).

---

## Passo 6 · Sem IA: quebre uma cor de propósito

Este é o fechamento do 2B.4 e se faz **sem o Claude Code**. Vai-se provocar a cena do começo.

**1. Preveja.** No `Tema.kt`, `VerdeTienda` vai passar de `0xFF0B6E4F` para `0xFFC62828`, um vermelho. Escreva em
`respuestas/02b.md` quais das onze capturas vão ficar em vermelho, e por quê. Dica: procure nos
arquivos quem usa `VerdeTienda` diretamente e quem usa a cor primária do tema, e em qual paleta.

**2. Quebre e meça.** Mude a linha:

```kotlin
val VerdeTienda = Color(0xFFC62828)
```

e rode, parado em `inicio/`:

```bash
./gradlew verifyRoborazziDebug
```

Em 19/11/2026, com a solução, terminou assim (o número da linha depende do arquivo):

```text
CapturasTest > [boton_habilitado] > captura[boton_habilitado] FAILED
    java.lang.AssertionError at CapturasTest.kt:53
…
18 tests completed, 6 failed
FAILURE: Build failed with an exception.
```

As seis em vermelho foram `boton_habilitado`, `boton_texto_largo`, `tarjeta_claro`, `tarjeta_texto_largo`,
`etiqueta_entregada` e `etiqueta_entregada_en_oscuro`. Cada uma deixa uma imagem de diferença em
`app/build/outputs/roborazzi/`, por exemplo `boton_habilitado_compare.png`.

**3. Leia a diferença.** Abra `boton_habilitado_compare.png`. Ela traz três painéis: **Reference** (a referência, o
botão verde), **Diff** (o que mudou, marcado em vermelho) e **New** (o que foi desenhado agora, o botão vermelho). Cole em
`respuestas/02b.md` o caminho dessa imagem e escreva o que se vê no painel do meio.

**4. Explique por que essas seis e não as outras cinco**, antes de ler «Compare as respostas».

**5. Conserte.** Devolva a linha a `val VerdeTienda = Color(0xFF0B6E4F)` e rode outra vez
`./gradlew verifyRoborazziDebug`: as onze voltam ao verde. Depois rode o `verifica`. Ele faz o mesmo por conta própria
em uma cópia do projeto, sem tocar na pasta de quem estuda: muda a cor, exige pelo menos uma captura em vermelho com a imagem de
diferença, a devolve e exige as onze em verde. Se as capturas não detectam a mudança, essa marca fica em vermelho mesmo que
as onze passem.

**Um limite, medido.** Uma mudança de uma única unidade no azul, de `0xFF0B6E4F` para `0xFF0B6E4E`, deu 11 de 11 em verde
(19/11/2026). Estas capturas usam a tolerância padrão do Roborazzi 1.79.0: o `CapturasTest` não lhe passa nenhuma
opção de comparação. O README dele descreve essa distância máxima por pixel (0.001 por padrão) e as opções para
tolerar mais, pensadas para o suavizado das bordas. Por isso o critério desta prática é a mudança de verde para vermelho
que se vê, e não detectar cada número.

O 2B.4 está concluído quando as quatro caixas podem ser marcadas:

- [ ] A previsão do ponto 1 está escrita, com o que saiu embaixo.
- [ ] Colou-se o caminho de uma imagem `_compare.png` e descreveu-se o painel do meio dela.
- [ ] Explicou-se por que essas capturas ficaram em vermelho e as demais não.
- [ ] O `verifica` dá 18 de 18, com as marcas da sabotagem em verde.

**O que comprova:** rodada em verde, rodada em vermelho com a diferença de imagem, rodada em verde outra vez (objetivo
2B.4).

---

## Os exercícios

O passo 1, o 2B.2 (passo 3) e o passo 6 se fazem **sem IA**.

| # | Faz-se | Está pronto quando |
|---|---|---|
| 2B.1 | Escrever o botão e revisar o cartão e a etiqueta que o Claude Code gerou; a tela de início os usa | As marcas «Texto» dos componentes e do `Inicio.kt` em verde, e os 7 testes da 02 em verde |
| 2B.2 | Escrever as previews com `@PreviewParameter` e ler o painel sem emulador | Existe `respuestas/02b-panel.png` e a previsão comparada |
| 2B.3 | Montar o catálogo em `src/debug` e abri-lo no emulador | As marcas «Compilado» do catálogo em verde, e está escrito o que se viu no catálogo e em que ele difere do painel |
| 2B.4 | Converter cada preview em uma captura e quebrar uma cor | As quatro caixas do passo 6, e o `verifica` em 18 de 18 |

**Tempo:** calcule umas duas horas e meia, com o emulador já instalado. É uma estimativa: será ajustada com o tempo que
a primeira turma levar.

---

## Não roda?

| O que aparece | Causa provável | O que fazer |
|---|---|---|
| Ao abrir «Catálogo», o app fecha; no `adb logcat` aparece `ClassNotFoundException: Didn't find class "androidx.compose.material.icons.Icons$Filled"` | O Showkase 1.0.5 usa ícones que as versões do Compose desta série já não trazem sozinhas (medido em 19/11/2026) | Confira se o `app/build.gradle.kts` continua trazendo `debugImplementation(libs.compose.material.icons.core)`; vem no projeto de início |
| `Unresolved reference 'getBrowserIntent'` | O KSP ainda não gerou o código do Showkase | Compile uma vez (`./gradlew assembleDebug`) |
| Um componente não aparece no catálogo | A preview dele é `private` | Tire o `private`: o README do Showkase pede previews públicas ou `internal` |
| ❌ «CapturasTest corrió 10 pruebas y esperaba 11» (o CapturasTest rodou 10 testes e esperava 11) | Falta uma variante em um provedor, ou a lista de casos foi escrita à mão | Compare os provedores com a tabela do passo 3 |
| ❌ «N de 11 capturas no coinciden» (N de 11 capturas não coincidem) sem que nada tenha sido quebrado | Mudou-se um componente de propósito, ou as referências são de outra máquina | Se a mudança é a que se queria, grave de novo com `./gradlew recordRoborazziDebug` e revise as imagens novas antes de subi-las |
| ❌ «Tema.kt cambió» (o Tema.kt mudou) | A cor do passo 6 não voltou | Devolva a linha à mão e olhe o `git diff` |
| ❌ «Las pruebas no llegaron a correr» (os testes não chegaram a rodar) e as linhas que ele mostra não bastam | O `verifica` só mostra as primeiras linhas do erro | Da pasta do projeto, rode `./gradlew verifyRoborazziDebug --stacktrace > fallo.txt 2>&1` e leia as últimas 40 linhas de `fallo.txt` antes de concluir que é um erro de compilação |

---

## Para fechar

**Responda sem reler:**

1. O que se ganha ao ver um componente isolado, em todas as variantes, antes de colocá-lo em uma tela?
2. Por que o catálogo vive em `src/debug`?
3. As onze capturas estão em verde. Diga uma coisa que isso comprova e uma que não.
4. Por que os sete testes da 02 não perceberam a cor vermelha?

**O que vem a seguir:** volta-se à sequência principal, na L03.

---

## Compare as respostas

**Passo 1.** Desabilitado, o botão fica cinza apagado e não responde ao toque; o texto longo se parte em duas
linhas, centralizado (medido nas capturas `boton_deshabilitado` e `boton_texto_largo`, 19/11/2026).

**Passo 2.** Mudam `Inicio.kt` e `MainActivity.kt`; `Tienda.kt` e `Tema.kt` não. O cartão não recebe cores porque
as pede ao tema que o envolve com `MaterialTheme.colorScheme`: dentro de `TemaTienda`, são as do `Tema.kt`.

**Passo 3.** Onze previews: quatro do botão, três do cartão e quatro da etiqueta. Termina em «…» o cartão das
xícaras: o nome dele não cabe em duas linhas a 320 dp de largura.

**Passo 4.** `src/main` viaja no app que é publicado; `src/debug`, segundo a documentação do Android, só no de
depuração. Um catálogo com todos os componentes não serve ao cliente. Entre o painel e o catálogo: o painel
mostra os nomes de `getDisplayName` e vive no Android Studio; o catálogo repete o título da preview em cada
variante e vive no telefone.

**Passo 5.** Onze imagens, uma por variante.

**Passo 6.** Ficam em vermelho as que pintam `VerdeTienda`. Na paleta clara, `VerdeTienda` é a cor primária: a
usam o botão habilitado, o de texto longo e o preço dos dois cartões claros. A etiqueta «Entregue» («Entregada») a usa
direto com `colorDe`, sem passar pelo tema: por isso também muda no escuro. Não mudam o botão desabilitado, que
usa um cinza do tema; o botão e o cartão no escuro, cuja primária é `VerdeClaro`; e «Recibida» e «Enviada», que
têm cor própria.

**Para fechar:**

1. Veem-se de uma vez os casos que no app custam a achar —desabilitado, escuro, texto longo— e conserta-se o
   componente antes que ele chegue a várias telas.
2. Para que viaje só no app de depuração e não no que é publicado.
3. Comprova que cada variante é desenhada igual à referência dela e que uma mudança visível de cor as põe em vermelho. Não
   comprova que o desenho seja bom, nem detecta uma mudança de cor muito pequena, nem como fica em um telefone real.
4. Porque medem que tela fica e que texto aparece depois de cada toque; nenhum olha pixels.

---

## Fontes da lição

Abertas em 19/11/2026.

- Android Developers. *Preview your UI with composable previews* (`@PreviewParameter`, uma preview por valor,
  `getDisplayName`). https://developer.android.com/develop/ui/compose/tooling/previews
- Android Developers. *Compose Preview Screenshot Testing* (o plugin já não é mantido; opção experimental).
  https://developer.android.com/studio/preview/compose-screenshot-testing
- Android Developers. *Test suites in the Android Gradle plugin* (experimental; AGP 9.6.0-alpha03 ou superior).
  https://developer.android.com/studio/preview/test-suites
- Android Developers. *Material Design 3 in Compose* (`MaterialTheme`, paleta clara e escura).
  https://developer.android.com/develop/ui/compose/designsystems/material3
- Android Developers. *Configure build variants* (a pasta `src/debug`). https://developer.android.com/build/build-variants
- Android Developers. *Android Gradle plugin release notes* (versões vigentes do AGP).
  https://developer.android.com/build/releases/about-agp
- Kotlin. *Kotlin Symbol Processing API*. https://kotlinlang.org/docs/ksp-overview.html
- Airbnb. *Showkase* (README: instalação, `@ShowkaseRoot`, previews públicas). https://github.com/airbnb/Showkase
- Roborazzi (README: tarefas `record` e `verify`, `@GraphicsMode`, comparador). https://github.com/takahirom/roborazzi
- Maven Central (versões do Roborazzi, Showkase, KSP, Paparazzi e Robolectric). https://repo1.maven.org/maven2/io/github/takahirom/roborazzi/roborazzi/
- Medição própria: Mac com Apple silicon, 19/11/2026 (o `verifica` sobre `inicio/` e `solucion/`, uma sabotagem por
  marca, a cor em vermelho e em uma unidade, o catálogo em um emulador Pixel 9 com Android 37, e a falha dos
  ícones).
