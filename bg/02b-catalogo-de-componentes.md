# Практика 02-B — Каталог на компонентите на магазина

**Преди да четеш, шест думи.** По един ред за всяка.

- **Компонент:** малка функция `@Composable`, която се използва на няколко екрана, като бутон или карта.
- **Вариант:** същият компонент с други данни или в друг режим: изключен, в тъмен режим, с дълъг текст.
- **Preview:** функция, отбелязана с `@Preview`, която Android Studio чертае в панел, без емулатор.
- **Каталог:** екран в приложението за отстраняване на грешки, където виждаш всички компоненти и техните
  варианти.
- **Тест със снимка:** тест, който чертае един компонент, прави снимка и я сравнява с **еталонна снимка**,
  запазена в хранилището. Ако някой пиксел се е променил повече от допустимото, тестът става червен.
- **`src/debug`:** папка с код, който влиза само в приложението за отстраняване на грешки, никога в онова, което
  се публикува.

**Какво отнасяш:** три компонента на магазина —основен бутон, карта на продукт и етикет за статуса на поръчка—,
прегледани изолирано във вариантите си, вградени в началния екран от урок 02, с каталог в приложението за
отстраняване на грешки и единайсет теста със снимка, които стават червени, когато някой смени един цвят.

**Новата идея на тази практика:** един компонент се проектира и се преглежда **изолирано**, във всичките му
варианти, преди да се използва на някой екран. Това е практика: идва след урок 02, извън основната последователност,
и за групата е по избор.

**Бележка за текстовете на приложението.** Както в предишните уроци, проектът носи екранните си текстове на
испански, а `verifica` търси точно тези текстове. В блоковете с код, в изходите на терминала и в скиците на екрана
те остават такива, каквито са в проекта; в прозата практиката ги дава на български и първия път поставя оригинала в
скоби: бутонът „Виж поръчките ми“ (в проекта: „Ver mis órdenes“), етикетът „Доставена“ (в проекта: „Entregada“) и
другите две състояния, „Получена“ („Recibida“) и „Изпратена“ („Enviada“).

---

## Сцената

Някой от екипа променя един ред: зеленото на магазина става червено, за да изпробва по-ярък бутон. Компилира се.
Седемте теста от урок 02 си остават зелени. Никой не отваря приложението в тъмен режим и никой не гледа картата на
продукт с дълго име. Качва се.

Тази промяна е измерена. С `VerdeTienda` в червено седемте теста от урок 02 дадоха 7 от 7 в зелено, а единайсетте
теста със снимка от тази практика дадоха **6 от 11 в червено** (19 ноември 2026 г.). Не се промени само бутонът:
промениха се и цената на картата, и етикетът „Доставена“ („Entregada“), също и в тъмен режим. Тестовете от урок 02
мерят кой екран остава след всяко докосване и не гледат цветове.

Въпросът на практиката: как виждаш всички варианти на един компонент, без да обикаляш приложението, и как научаваш,
когато някой се промени, без никой да го е искал?

---

## Преди да започнеш

**Какво ти трябва:** завършеният [урок 02](02-pantallas-que-cambian.md), средата от
[урок 00](00-ambiente-listo.md) (`ANDROID_HOME` и емулатор или твоят телефон) и Android Studio за панела с
preview-тата.

**Началният проект** е решението на урок 02, с настроеното за тази практика:

```bash
git pull
cd lecciones/02b-catalogo-de-componentes/inicio
```

Отговорите ти отиват в `respuestas/02b.md`, в корена на хранилището (`mkdir -p respuestas`, ако не съществува).

**Какво вече носи проектът (скелето).** Тук не се оценява:

| Какво носи | Къде | Защо |
|---|---|---|
| Палитрата и шрифтът на магазина | `Tema.kt` | Дава ги практиката: никой не ги проектира. Не го променяй, освен за миг в стъпка 6 |
| Зависимостите и приставките | `gradle/libs.versions.toml` и `app/build.gradle.kts` | Виж таблицата с версиите по-долу |
| Тестовете от урок 02 | `RecorridoTest.kt` и `ArranqueTest.kt` | Трябва да останат зелени с вградените компоненти. Не ги променяй |
| Памет, `gradlew`, Android 36 за тестовете и Java 25 | Както в урок 02 | Обяснено в [урок 01](01-mi-primera-pantalla.md) |

`Tema.kt` определя единайсет цвята с име (`VerdeTienda`, `AzulRecibida`, `AmbarEnviada`…), една светла и една тъмна
палитра, четири стила на шрифта и функция `TemaTienda(oscuro) { … }`, която обвива всяко съдържание с всичко това.
Отвори го и го прочети преди стъпка 1: ще го използваш във всяка стъпка.

**Защо Roborazzi за снимките.** Избран е по един-единствен критерий: да продължи да работи по същия начин за
следващата група. Официалният инструмент на Google днес не го изпълнява (19 ноември 2026 г.): страницата му казва, че
приставката `com.android.compose.screenshot` „вече не се поддържа“, а инсталирането ѝ още иска флага
`android.experimental.enableScreenshotTest=true`. Google препоръчва миграция към *test suites* на AGP, които искат
AGP 9.6.0-alpha03 или по-нова, предварителна версия; тази поредица е фиксирана на AGP 9.4.1 и обновяването ѝ е
отделно решение. Paparazzi, другият известен вариант, е на 2.0.0-alpha05. Roborazzi има стабилна версия в Maven
Central и работи върху Robolectric, който поредицата използва от урок 01.

**Версиите**, фиксирани и изпробвани с тази практика на 19 ноември 2026 г. Тези на Roborazzi, Showkase, KSP и
`material-icons-core` бяха най-новите, публикувани онзи ден; AGP 9.4.1 и BoM 2026.09.00 на поредицата не бяха (вече
имаше AGP 9.5.1 и BoM 2026.11.01) и остават такива, за да не се мести поредицата по средата на групата:

| Част | Версия | За какво | Източник |
|---|---|---|---|
| Roborazzi (`roborazzi`, `roborazzi-compose` и неговата приставка) | 1.79.0 | Тестовете със снимка | Maven Central |
| Showkase (`showkase` и `showkase-processor`) | 1.0.5 | Каталогът | Maven Central и неговият README |
| KSP (Kotlin Symbol Processing) | 2.3.14 | Пуска генератора на Showkase при компилиране | Maven Central и неговите *releases* |
| `material-icons-core` | 1.7.8, тази на BoM 2026.09.00 | Иконите, които Showkase използва отвътре | BoM на Compose |
| Robolectric | 4.18 | Вече идваше от урок 02 | — |

Robolectric 4.18.1 се появи в Maven Central на 18 ноември 2026 г. Тази практика остава на 4.18 на поредицата: с нея
е измерено всичко.

**Скицата на началния екран**, с трите компонента под поздрава от урок 02:

```text
┌──────────────────────────┐
│       Hola, Ana          │
│  Aquí verás tus órdenes  │
│ ┌──────────────────────┐ │
│ │ Audífonos            │ │  ← карта на продукта
│ │ $899.00              │ │
│ └──────────────────────┘ │
│        ( Enviada )       │  ← етикет за статуса
│ [   Ver mis órdenes    ] │  ← основен бутон
└──────────────────────────┘
```

---

## Стъпка 1 · На ръка: основният бутон

Без Claude Code. Създай `BotonPrincipal.kt` до `Inicio.kt`, в `app/src/main/java/mx/habil/ejemplo/tienda/`:

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

- **`habilitado: Boolean = true`** е параметър със стойност по подразбиране: който не го подаде, получава `true`.
- **`MaterialTheme.typography.labelLarge`** не казва кой шрифт да се използва: иска стила `labelLarge` на темата,
  която го обгражда. Вътре в `TemaTienda` това е този от `Tema.kt`.
- **Бутонът не казва цвета си.** `Button` взема основния цвят на темата, който в светлата палитра е `VerdeTienda`.

**Предвиди.** Запиши в `respuestas/02b.md`, преди да продължиш: какво се променя на екрана с `habilitado =
false`? А с текст, който не се събира в един ред?

Още не можеш да го видиш: няма екран, който да го използва. Ще го видиш в панела от стъпка 3.

**Доказва се с:** компонентът се компилира и `verifica` намира „fun BotonPrincipal(“ (част от цел 2B.1).

---

## Стъпка 2 · С Claude Code: картата, етикетът и началният екран

Тук влиза инструментът, винаги по един и същ начин: **предвиждаш → искаш → четеш какво е генерирал → пускаш
`verifica` → обясняваш със свои думи.**

1. **Предвиди.** Запиши в `respuestas/02b.md`: кои от тези файлове ще се променят? `Inicio.kt`, `MainActivity.kt`,
   `Tienda.kt`, `Tema.kt`.
2. **Поискай** от Claude Code, стоейки в `inicio/`: „Създай `TarjetaProducto.kt` с composable
   `TarjetaProducto(nombre: String, precio: String)`: `Card` с пълна ширина, ъгли от 16 dp и цвят на повърхността от
   темата; вътре името със стила `titleMedium` най-много на два реда и с „…“, ако не се събира, а под него цената с
   удебелен шрифт и основния цвят на темата. Създай `EtiquetaEstado.kt` с `enum class EstadoOrden(val texto:
   String)` със `RECIBIDA("Recibida")`, `ENVIADA("Enviada")` и `ENTREGADA("Entregada")`; функция `colorDe(estado)`,
   която връща `AzulRecibida`, `AmbarEnviada` и `VerdeTienda` от `Tema.kt`; и composable `EtiquetaEstado(estado:
   EstadoOrden)`: хапче с този цвят, с бял текст и стила `labelSmall`. В `Inicio.kt`, под двата текста, добави
   `TarjetaProducto` с «Audífonos inalámbricos» на «$899.00», `EtiquetaEstado(EstadoOrden.ENVIADA)` и
   `BotonPrincipal("Ver mis órdenes")`, който засега не прави нищо. В `MainActivity` обвий `Tienda` с
   `TemaTienda(oscuro = isSystemInDarkTheme())`. Не променяй `Tema.kt`, `Tienda.kt`, нито тестовете.“
3. **Прочети** промяната, преди да я приемеш. Имената по-горе са договорът на тази практика: preview-тата,
   каталогът и снимките ги викат така. Частта, която най-трудно се чете, е етикетът:

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

   Три неща за проверка, докато четеш:

   - **`enum class EstadoOrden(val texto: String)`** е затвореният списък от урок 02, сега с една данна за всяка
     стойност: `EstadoOrden.ENVIADA.texto` е `"Enviada"`.
   - **`RoundedCornerShape(50)`** закръглява ъглите до 50 % от височината: затова изглежда като хапче.
   - **`"$899.00"` не вмъква никаква променлива.** В Kotlin `$` съставя шаблон само ако след него има име или
     фигурна скоба; когато след него има число, е знакът за долар.

4. **Пусни `verifica`** от корена на хранилището:

```bash
./lecciones/02b-catalogo-de-componentes/verifica
```

   Седемте теста от урок 02 трябва да останат зелени: началният екран от урок 02 все още казва „Здравей, Ана“
   („Hola, Ana“) и има още три неща. Измерено на 19 ноември 2026 г. в тази точка: знаците на компонентите, на
   `Inicio.kt` и на `MainActivity.kt` в зелено, `Resultado: 7 de 18 pruebas esperadas en verde (solo corrieron 7…` *(Резултат: 7 от 18 очаквани
   теста са зелени; единадесетте теста със снимка още не съществуват)*, и в червено само тези от стъпки 3 до 6 (preview-та, `@ShowkaseRoot`, изображения, снимки, саботаж и
   `respuestas/02b.md`). Това червено сега се очаква.
5. **Обясни** в два реда на `respuestas/02b.md`: защо `TarjetaProducto` не получава никакъв цвят и откъде го взема?

**Доказва се с:** трите компонента се компилират и началният екран от урок 02 ги използва, с тестовете от урок 02 в
зелено (цел 2B.1).

---

## Стъпка 3 · Без ИИ: вариантите в панела за preview-та

Това е упражнение 2B.2 и се прави **без Claude Code**. Панелът за preview-та на Android Studio чертае функция
`@Preview`, без да компилира цялото приложение и без да отваря емулатора.

**1. Напиши този на бутона.** В края на `BotonPrincipal.kt` добави това, с неговите `import` (`Box`, `padding`,
`Preview`, `PreviewParameter` и `PreviewParameterProvider`; Android Studio ти ги предлага):

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

- **`data class`** е клас за пазене на данни: Kotlin сам му пише как да се сравнява и как да се отпечатва.
- **`PreviewParameterProvider`** е интерфейс, като `Entrada` от урок 02: обещава последователност от стойности.
  Документацията на Android казва, че се чертае **по едно preview за всяка стойност**.
- **`getDisplayName`** дава име на всяко от тях. Без него, според документацията, те се наричат по позицията си (0,
  1, 2…). Вътрешният списък е препоръката на същата страница, за да се намира името по позиция.
- **`oscuro` е във варианта.** Тъмният режим на preview-то не зависи от твоя компютър, нито от телефона: решава го
  всеки вариант. Затова снимките от стъпка 5 излизат еднакви на всяка машина, която чертае по същия начин.

**2. Напиши другите две по същия образец.** В `TarjetaProducto.kt`: `VarianteTarjeta(nombre, producto, precio,
oscuro)`, `VariantesTarjeta` и `TarjetaProductoPreview`, с `group = "Tarjetas"`. В `EtiquetaEstado.kt`:
`VarianteEtiqueta(nombre, estado, oscuro)`, `VariantesEtiqueta` и `EtiquetaEstadoPreview`, с `group = "Etiquetas"` и
без `widthDp`. Ето вариантите, в този ред:

| Компонент | Варианти (`nombre`) |
|---|---|
| Карта | «claro» (светъл) и «oscuro» (тъмен), с «Audífonos inalámbricos» на «$899.00»; «texto largo» (дълъг текст), с «Juego de seis tazas de cerámica esmaltada hechas a mano en Dolores Hidalgo, Guanajuato» на «$1,249.00» |
| Етикет | «recibida» (получена), «enviada» (изпратена) и «entregada» (доставена), в светъл режим; «entregada en oscuro» (доставена в тъмен режим) |

**3. Предвиди.** Преди да отвориш панела, запиши: колко preview-та ще видиш общо? Коя карта ще завърши с „…“?

**4. Погледни го.** Отвори `BotonPrincipal.kt` в Android Studio и избери изгледа **Split** или **Design**, горе
вдясно в редактора; ако поиска да компилира, съгласи се. Според документацията панелът чертае по едно preview за
всяка стойност на доставчика, с името, което дава `getDisplayName`. Тази практика не е измервала панела в Android
Studio: ако при теб изглежда различно, разкажи ни.

Направи екранна снимка на панела с четирите варианта на бутона и я запази като `respuestas/02b-panel.png`. Под
предвиждането си от стъпка 1 запиши в какво си познал.

**Доказва се с:** твоята екранна снимка на панела и твоето предвиждане, сравнени (цел 2B.2). `verifica` проверява само
това, че всеки компонент носи своето `@Preview` с `@PreviewParameter`: панела не го вижда. А че си го направил без
ИИ, е работно правило, което декларираш в отговора си: нито една снимка, нито `verifica` могат да го докажат.

---

## Стъпка 4 · С Claude Code: каталогът с Showkase

Панелът живее в твоя Android Studio. Каталогът живее **в приложението**: всеки от екипа го отваря в телефона си,
без отворен проект. Showkase, на Airbnb, го сглобява от същите `@Preview`: при компилиране KSP (генератор на код,
който работи заедно с компилатора на Kotlin) търси preview-тата и пише кода, който ги събира.

1. **Предвиди.** Защо мислиш, че каталогът трябва да е в `src/debug`, а не в `src/main`?
2. **Поискай** от Claude Code, стоейки в `inicio/`: „Създай `app/src/debug/java/mx/habil/ejemplo/tienda/Catalogo.kt`
   с клас `CatalogoTienda`, отбелязан с `@ShowkaseRoot`, който имплементира `ShowkaseRootModule`, и
   `CatalogoActivity`, която в `onCreate` отваря `Showkase.getBrowserIntent(this)` и се затваря с `finish()`. Създай
   `app/src/debug/AndroidManifest.xml`, който регистрира `CatalogoActivity` със собствена начална икона и етикет
   «Catálogo». Не пипай нищо от `src/main`.“
3. **Прочети** промяната. Трябва да носи тези части:

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

   - **`@ShowkaseRoot`** отбелязва корена на каталога. README на Showkase иска по един за модул.
   - **`getBrowserIntent`** не съществува, докато не компилираш: пише го KSP. Ако Android Studio го оцвети в
     червено, компилирай веднъж.
   - **Preview-тата трябва да са публични или `internal`.** README го казва: `private` не влиза в каталога.

4. **Погледни го в емулатора.** `./gradlew installDebug`. В телефона се появяват две икони: „Магазинът“ („La
   tienda“) и „Catálogo“. Отвори „Catálogo“.

   В емулатора на изпробването (Pixel 9, Android 37, 19 ноември 2026 г.) се видя това: списък със заглавие
   „Components“ (текстовете на Showkase идват на английски) с три групи, „Botones (1)“, „Etiquetas (1)“ и „Tarjetas
   (1)“. „(1)“ брои компоненти, не варианти. При докосване на „Botones“ и после на „Botón principal“ се появиха
   четирите варианта: зелен с „Comprar“, приглушено сив, светлозелен върху тъмен фон и дългият текст на два реда.
   И четирите носят същото заглавие, „Botón principal“: Showkase не използва имената от `getDisplayName`. При
   етикетите се видяха „Recibida“ в синьо, „Enviada“ в кехлибарено и две „Entregada“ в зелено, втората върху тъмен
   фон.

5. **Обясни** в `respuestas/02b.md` какво видя различно между панела от стъпка 3 и каталога.

**Доказва се с:** каталогът се отваря в емулатора с трите групи и техните варианти (цел 2B.3). `verifica` проверява
онова, което Showkase е генерирал при компилиране: корена на каталога и трите му групи, че активност от `src/debug`
го отваря със собствена икона и че `src/main` не го назовава. Текст, който казва „@ShowkaseRoot“, не генерира нищо и
остава в червено. Онова, което се вижда в емулатора, го разказваш ти, а твоята снимка е доказателството.

---

## Стъпка 5 · С Claude Code: една снимка за всяко preview

Да виждаш вариантите е полезно, докато някой ги гледа. Тестът със снимка ги гледа всеки път, когато се пускат
тестовете.

1. **Предвиди.** Колко еталонни снимки ще имаш накрая?
2. **Поискай** от Claude Code, стоейки в `inicio/`: „Създай
   `app/src/test/java/mx/habil/ejemplo/tienda/CapturasTest.kt`: тест на Robolectric с
   `ParameterizedRobolectricTestRunner` и `@GraphicsMode(GraphicsMode.Mode.NATIVE)`, с по един случай за всеки
   вариант на `VariantesBoton`, `VariantesTarjeta` и `VariantesEtiqueta`, наречени `boton_<nombre>`,
   `tarjeta_<nombre>` и `etiqueta_<nombre>`, с интервалите, сменени с долна черта. Всеки случай чертае своето preview
   с `createComposeRule()` и вика `onRoot().captureRoboImage("src/test/capturas/<caso>.png")`. Списъкът със случаите
   трябва да излиза от същите доставчици, не написан на ръка. Не пипай нищо друго.“
3. **Прочети** промяната. Сърцето е това:

```kotlin
@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CapturasTest(private val archivo: String, private val preview: @Composable () -> Unit) {
    // … previews(): по един случай за всеки вариант, взет от доставчиците …

    @get:Rule
    val regla = createComposeRule()

    @Test
    fun captura() {
        regla.setContent { preview() }
        regla.onRoot().captureRoboImage("src/test/capturas/$archivo.png")
    }
}
```

   - **`ParameterizedRobolectricTestRunner`** пуска един и същ тест по веднъж за всеки случай от списъка: единайсет
     случая, единайсет теста.
   - **`@GraphicsMode(NATIVE)`** иска от Robolectric да чертае истински пиксели. README на Roborazzi го изисква.
   - **`captureRoboImage`** прави снимката. Какво се прави с нея, зависи от задачата на Gradle, която пускаш.

4. **Запиши еталоните.** Стоейки в `inicio/`:

```bash
./gradlew recordRoborazziDebug
```

   Остават единайсет файла `.png` в `app/src/test/capturas/`. Отвори ги: това са твоите компоненти, всеки в своя
   вариант. Тези снимки **се качват в хранилището**: те са онова, с което ще сравнява следващото пускане.
5. **Пусни `verifica`.** Сега пуска `verifyRoborazziDebug`, която сравнява всяка снимка с нейния еталон. С решението
   на 19 ноември 2026 г. даде `Resultado: 18 de 18 pruebas en verde` *(Резултат: 18 от 18 теста са зелени)*:
   седемте от урок 02 и единайсетте снимки.

**Внимавай с каква задача пускаш.** Според README на Roborazzi `recordRoborazziDebug` записва (или записва
отново) еталоните, а `verifyRoborazziDebug` сравнява и се проваля, ако нещо се е променило. `./gradlew
testDebugUnitTest` сам по себе си пуска тестовете, без да сравнява нищо: със счупения цвят от стъпка 6 даде 18 от 18
в зелено (измерено на 19 ноември 2026 г.). `verifica` използва `verify`.

**Еталоните на `solucion/` са записани на Mac.** На Linux Mint 22.3 съвпаднаха и единадесетте (19 ноември 2026 г.):
решението даде 18 от 18, а саботажът на цвета направи 6 от 11 червени, както на Mac. Ако на твоята машина не съвпадат,
запиши своите с `recordRoborazziDebug`, както в точка 4.

**Доказва се с:** единайсет теста със снимка в зелено спрямо техните еталони (първата част от цел 2B.4).

---

## Стъпка 6 · Без ИИ: счупи един цвят нарочно

Това е краят на 2B.4 и се прави **без Claude Code**. Ще предизвикаш сцената от началото.

**1. Предвиди.** В `Tema.kt` `VerdeTienda` ще мине от `0xFF0B6E4F` на `0xFFC62828`, един червен цвят. Запиши в
`respuestas/02b.md` кои от единайсетте снимки мислиш, че ще станат червени, и защо. Подсказка: потърси във
твоите файлове кой използва `VerdeTienda` направо и кой използва основния цвят на темата, и в коя палитра.

**2. Счупи го и измери.** Смени реда:

```kotlin
val VerdeTienda = Color(0xFFC62828)
```

и пусни, стоейки в `inicio/`:

```bash
./gradlew verifyRoborazziDebug
```

На 19 ноември 2026 г., с решението, завърши така (номерът на реда зависи от твоя файл):

```text
CapturasTest > [boton_habilitado] > captura[boton_habilitado] FAILED
    java.lang.AssertionError at CapturasTest.kt:53
…
18 tests completed, 6 failed
FAILURE: Build failed with an exception.
```

Шестте в червено бяха `boton_habilitado`, `boton_texto_largo`, `tarjeta_claro`, `tarjeta_texto_largo`,
`etiqueta_entregada` и `etiqueta_entregada_en_oscuro`. Всяка оставя изображение с разликата в
`app/build/outputs/roborazzi/`, например `boton_habilitado_compare.png`.

**3. Прочети разликата.** Отвори `boton_habilitado_compare.png`. Носи три панела: **Reference** (еталонът,
зеленият бутон), **Diff** (онова, което се е променило, отбелязано в червено) и **New** (онова, което сега е
начертано, червеният бутон). Постави в `respuestas/02b.md` пътя до това изображение и запиши какво виждаш в
средния панел.

**4. Обясни защо тези шест, а не другите пет**, преди да прочетеш „Сравни отговорите си“.

**5. Поправи го.** Върни реда на `val VerdeTienda = Color(0xFF0B6E4F)` и пусни отново
`./gradlew verifyRoborazziDebug`: единайсетте стават пак зелени. После пусни `verifica`. Той прави същото сам, в
копие на твоя проект, без да докосва твоята папка: сменя цвета, изисква поне една снимка в червено с нейното
изображение на разликата, връща го и изисква единайсетте в зелено. Ако твоите снимки не засичат промяната, този знак
остава в червено, дори и единайсетте да минават.

**Една граница, измерена.** Промяна от една-единствена единица в синьото, от `0xFF0B6E4F` на `0xFF0B6E4E`, даде 11 от
11 в зелено (19 ноември 2026 г.). Тези снимки използват допустимото отклонение по подразбиране на Roborazzi 1.79.0:
`CapturasTest` не му подава никаква опция за сравнение. Неговият README описва това максимално разстояние на пиксел
(0.001 по подразбиране) и опциите за по-голямо допускане, предвидени за изглаждането на ръбовете. Затова критерият на
тази практика е промяната от зелено в червено, която се вижда, а не засичането на всяко число.

2B.4 е завършено, когато можеш да отметнеш и четирите:

- [ ] Предвиждането ти от точка 1 е написано, с онова, което е излязло под него.
- [ ] Поставил си пътя до изображение `_compare.png` и си описал средния му панел.
- [ ] Обяснил си защо тези снимки станаха червени, а другите не.
- [ ] `verifica` дава 18 от 18, със знаците на саботажа в зелено.

**Доказва се с:** пускане в зелено, пускане в червено с разликата на изображението, пускане в зелено отново (цел
2B.4).

---

## Твоите упражнения

Стъпка 1, 2B.2 (стъпка 3) и стъпка 6 се правят **без ИИ**.

| # | Правиш | Готово е, когато |
|---|---|---|
| 2B.1 | Пишеш бутона и преглеждаш картата и етикета, които е генерирал Claude Code; началният екран ги използва | Знаците „Texto“ (текст) на компонентите и на `Inicio.kt` в зелено, и 7-те теста от урок 02 в зелено |
| 2B.2 | Пишеш preview-тата с `@PreviewParameter` и четеш панела без емулатор | Имаш `respuestas/02b-panel.png` и твоето предвиждане, сравнено |
| 2B.3 | Сглобяваш каталога в `src/debug` и го отваряш в емулатора | Знаците „Compilado“ (компилирано) на каталога в зелено, и си записал какво си видял в каталога и с какво се различава от панела |
| 2B.4 | Превръщаш всяко preview в снимка и чупиш един цвят | Четирите отметки от стъпка 6, и `verifica` на 18 от 18 |

**Време:** отдели около два часа и половина, с вече инсталиран емулатор. Това е оценка: ще я уточним според времето,
което отнеме на първата група.

---

## Не ти ли тръгва?

| Виждаш | Вероятна причина | Какво правиш |
|---|---|---|
| При отваряне на „Catálogo“ приложението се затваря; в `adb logcat` се появява `ClassNotFoundException: Didn't find class "androidx.compose.material.icons.Icons$Filled"` | Showkase 1.0.5 използва икони, които версиите на Compose от тази поредица вече не носят сами (измерено на 19 ноември 2026 г.) | Провери дали `app/build.gradle.kts` още носи `debugImplementation(libs.compose.material.icons.core)`; идва в началния проект |
| `Unresolved reference 'getBrowserIntent'` | KSP още не е генерирал кода на Showkase | Компилирай веднъж (`./gradlew assembleDebug`) |
| Един компонент не се появява в каталога | Неговото preview е `private` | Махни му `private`: README на Showkase иска публични или `internal` preview-та |
| ❌ „CapturasTest corrió 10 pruebas y esperaba 11“ (CapturasTest пусна 10 теста, а очакваше 11) | Липсва един вариант в някой доставчик или списъкът със случаите е написан на ръка | Сравни доставчиците си с таблицата от стъпка 3 |
| ❌ „N de 11 capturas no coinciden“ (N от 11 снимки не съвпадат), без да си чупил нищо | Променил си компонент нарочно или еталоните ти са от друга машина | Ако промяната е онази, която си искал, запиши отново с `./gradlew recordRoborazziDebug` и прегледай новите изображения, преди да ги качиш |
| ❌ „Tema.kt cambió“ (Tema.kt се е променил) | Цветът от стъпка 6 не се е върнал | Върни реда на ръка и погледни `git diff` |
| ❌ „Las pruebas no llegaron a correr“ (тестовете не се изпълниха) и редовете, които показва, не стигат | `verifica` показва само първите редове на грешката | От папката на проекта пусни `./gradlew verifyRoborazziDebug --stacktrace > fallo.txt 2>&1` и прочети последните 40 реда на `fallo.txt`, преди да заключиш, че е грешка при компилиране |

---

## В заключение

**Отговори, без да четеш отново:**

1. Какво печелиш, когато виждаш един компонент изолирано, във всичките му варианти, преди да го поставиш на екран?
2. Защо каталогът живее в `src/debug`?
3. Единайсетте ти снимки са в зелено. Кажи едно нещо, което това доказва, и едно, което не доказва.
4. Защо седемте теста от урок 02 не забелязаха червения цвят?

**Какво следва:** връщаш се към основната последователност, в урок 03.

---

## Сравни отговорите си

**Стъпка 1.** Изключен, бутонът се оцветява в приглушено сиво и не отговаря на докосване; дългият текст се разделя на
два реда, центриран (измерено в снимките `boton_deshabilitado` и `boton_texto_largo`, 19 ноември 2026 г.).

**Стъпка 2.** Променят се `Inicio.kt` и `MainActivity.kt`; `Tienda.kt` и `Tema.kt` не. Картата не получава цветове,
защото ги иска от темата, която я обгражда, с `MaterialTheme.colorScheme`: вътре в `TemaTienda` са тези от `Tema.kt`.

**Стъпка 3.** Единайсет preview-та: четири на бутона, три на картата и четири на етикета. С „…“ завършва картата с
чашите: името ѝ не се събира в два реда при ширина 320 dp.

**Стъпка 4.** `src/main` пътува в приложението, което се публикува; `src/debug`, според документацията на Android,
само в това за отстраняване на грешки. Каталог с всички компоненти не върши работа на клиента. Между панела и
каталога: панелът показва имената от `getDisplayName` и живее в Android Studio; каталогът повтаря заглавието на
preview-то във всеки вариант и живее в телефона.

**Стъпка 5.** Единайсет изображения, по едно за всеки вариант.

**Стъпка 6.** В червено стават тези, които рисуват `VerdeTienda`. В светлата палитра `VerdeTienda` е основният цвят:
използват го включеният бутон, този с дългия текст и цената на двете светли карти. Етикетът „Доставена“ („Entregada“)
го използва директно с `colorDe`, без да минава през темата: затова се променя и в тъмен режим. Не се променят
изключеният бутон, който използва сиво от темата; бутонът и картата в тъмен режим, чийто основен цвят е
`VerdeClaro`; и „Получена“ („Recibida“) и „Изпратена“ („Enviada“), които имат собствен цвят.

**В заключение:**

1. Виждаш наведнъж случаите, които в приложението трудно се откриват —изключен, тъмен, дълъг текст— и поправяш
   компонента, преди да е стигнал до няколко екрана.
2. За да пътува само в приложението за отстраняване на грешки, а не в онова, което се публикува.
3. Доказва, че всеки вариант се чертае като своя еталон и че видима промяна на цвят ги прави червени. Не доказва, че
   дизайнът е добър, нито засича много малка промяна на цвят, нито как изглежда на истински телефон.
4. Защото мерят кой екран остава и кой текст се появява след всяко докосване; нито един не гледа пиксели.

---

## Източници на урока

Отворени на 19 ноември 2026 г.

- Android Developers. *Preview your UI with composable previews* (`@PreviewParameter`, по едно preview за всяка стойност, `getDisplayName`). https://developer.android.com/develop/ui/compose/tooling/previews
- Android Developers. *Compose Preview Screenshot Testing* (приставката вече не се поддържа; експериментален флаг). https://developer.android.com/studio/preview/compose-screenshot-testing
- Android Developers. *Test suites in the Android Gradle plugin* (експериментални; AGP 9.6.0-alpha03 или по-нова). https://developer.android.com/studio/preview/test-suites
- Android Developers. *Material Design 3 in Compose* (`MaterialTheme`, светла и тъмна палитра). https://developer.android.com/develop/ui/compose/designsystems/material3
- Android Developers. *Configure build variants* (папката `src/debug`). https://developer.android.com/build/build-variants
- Android Developers. *Android Gradle plugin release notes* (актуалните версии на AGP). https://developer.android.com/build/releases/about-agp
- Kotlin. *Kotlin Symbol Processing API*. https://kotlinlang.org/docs/ksp-overview.html
- Airbnb. *Showkase* (README: инсталиране, `@ShowkaseRoot`, публични preview-та). https://github.com/airbnb/Showkase
- Roborazzi (README: задачите `record` и `verify`, `@GraphicsMode`, компараторът). https://github.com/takahirom/roborazzi
- Maven Central (версиите на Roborazzi, Showkase, KSP, Paparazzi и Robolectric). https://repo1.maven.org/maven2/io/github/takahirom/roborazzi/roborazzi/
- Собствено измерване: Mac с Apple silicon, 19 ноември 2026 г. (`verifica` върху `inicio/` и `solucion/`, един саботаж за всеки знак, цветът в червено и с една единица, каталогът в емулатор Pixel 9 с Android 37 и грешката с иконите).
- Измерване на Linux Mint 22.3, 19 ноември 2026 г.: `verifica` върху `solucion/` от ново копие на хранилището, 18 от 18,
  единадесетте снимки съвпадат с еталоните, а саботажът на цвета прави 6 от 11 червени.
