# Lição 03 — O app conhece o Firebase

**Antes de ler, quatro palavras.** Uma linha cada uma; as demais chegam no passo em que são usadas.

- **Firebase:** um serviço do Google que resolve para o app coisas que não vão ser programadas à mão, como guardar
  quem entrou. Nesta série usa-se uma só parte, o Authentication.
- **Certificado de depuração:** o documento com que o Android assina o app compilado no computador.
  Ele é criado sozinho, e cada computador tem o próprio.
- **Impressão digital (SHA-1):** um resumo curto desse certificado. Serve para reconhecê-lo sem mostrar nada privado.
- **`google-services.json`:** o arquivo de configuração baixado do Firebase, com os dados do projeto
  e do app.

**O que fica com quem termina:** o app da loja conectado ao projeto do Firebase **de quem estuda**, com um arquivo de
configuração que traz tudo o que a lição 04 precisa, e saber comprová-lo sem abrir o console.

**Ideia nova desta lição:** um app se conecta a um serviço externo com duas senhas de identificação, o nome de pacote e a
impressão digital do certificado. O serviço não conhece o código: conhece essas duas senhas. O resto —o plugin do Gradle e
a biblioteca— já vem pronto e explicado, e não se avalia aqui.

**Uma nota sobre os textos do app.** O projeto do curso traz os textos de tela em espanhol, e os arquivos de
`lecciones/` não são traduzidos: as verificações automáticas do curso (o `verifica`) procuram esses textos exatos.
Por isso, nos blocos de código, nas saídas de terminal e nos esboços de tela, os textos ficam como o projeto os tem;
na prosa, a lição os cita em português e, na primeira vez, traz o original entre parênteses: a tela «A loja» («La
tienda») com o botão «Entrar» («Entrar»), e o botão «Entrar com o Google» («Entrar con Google»).

---

## A cena

Registra-se o app no Firebase. O assistente oferece um botão: baixar o `google-services.json`. Baixa-se,
coloca-se na pasta certa e compila-se.

Compila. Tudo em verde.

Uma lição depois, põe-se o botão «Entrar com o Google», toca-se nele e não se entra. A mensagem não diz o que falta.

Isso aconteceu no ensaio desta série, em 13 de novembro de 2026, em um Mac: o arquivo baixado antes de registrar
a impressão digital e de habilitar o Google não trazia dois blocos de que o botão precisa. É o que se viu nesse dia, não uma
regra que o Firebase publique. A regra estável está, sim, na documentação dele: depois de registrar a impressão digital e de habilitar o
Google, baixa-se o arquivo **atualizado**, porque é ele que traz o necessário para entrar com o Google.

E a parte de «compila em verde» está medida aqui, com um arquivo inventado ao qual faltam esses blocos (18 de
novembro de 2026): `./gradlew assembleDebug` termina bem, e o `verifica` desta lição dá 15 de 18, com três
marcas em vermelho que dizem «baixe-o de novo».

O arquivo é uma cópia do que o console sabia sobre o app **no momento em que foi baixado**. Por isso a
ordem importa. Primeiro conhecem-se as duas senhas do app, depois segue-se a ordem que funciona, e no final quebra-se
tudo de propósito para ler o erro.

---

## Antes de começar

**O que é preciso:** a [02](02-pantallas-que-cambian.md) concluída, o ambiente da
[00](00-ambiente-listo.md) (`ANDROID_HOME` definido) e uma conta do Google própria. Entra-se com ela
no console do Firebase pelo navegador: vale o que diz «Conta e equipamento de confiança» na 00.

**O que é de quem estuda.** O projeto do Firebase é criado por quem estuda, com a própria conta, e ninguém mais o usa. No ensaio de
13/11/2026 ele foi criado no plano sem custo, sem registrar cartão.

**O projeto de início.** A partir da raiz do repositório da série:

```bash
git pull
cd lecciones/03-la-app-conoce-a-firebase/inicio
```

É ali que se trabalha. Ao lado está `solucion/`: dá para lê-la e compará-la sem configurar nada. **`./verifica solucion` sai
em vermelho em um clone limpo**, e é o esperado: o arquivo de configuração não é enviado ao repositório, e com o plugin aplicado o projeto não
compila sem ele (medido em 18/11/2026: 0 testes executados). Para revisá-la, copie o arquivo para
`solucion/app/` —ali ele também fica ignorado— e retire-o ao terminar. As previsões e explicações vão em
`respuestas/03.md`, na raiz do repositório (`mkdir -p respuestas` se não existir).

**O que o projeto já traz (andaime).** Não se avalia aqui:

| O que traz | Onde | Por quê |
|---|---|---|
| As três telas da 02 e os sete testes delas | `app/src/main` e `app/src/test` | Parte-se do que já foi escrito. Conectar o Firebase não deve alterá-los |
| Os testes da configuração e o revisor deles | `app/src/test/…/ConfiguracionTest.kt` e `RevisorDeConfiguracion.kt` | São os que o `verifica` roda. Não os altere |
| Três configurações inventadas | `app/src/test/resources/configuraciones/` | Com elas os testes demonstram que o revisor de fato distingue um arquivo completo de um incompleto |
| Um molde do arquivo, sem dados de ninguém | `app/google-services.json.example` | Para saber como é um arquivo completo antes de ter o próprio |

**O que esta lição não faz.** Ainda não há botão do Google nem se entra com ninguém: isso é a 04. Aqui o app
fica apresentado ao Firebase, e a comprovação é feita lendo o arquivo.

---

## Passo 1 · Sem IA: as duas senhas do app

Este passo é de quem estuda, sem Claude Code. O Firebase vai pedir dois dados. Os dois já existem no computador.

**A primeira senha: o nome de pacote.** Preveja onde está escrito e depois procure:

```bash
grep -n "applicationId" app/build.gradle.kts
```

Deve responder uma linha com `applicationId = "mx.habil.ejemplo.tienda"`. Segundo a documentação do Android, esse
identificador distingue o app no telefone e na loja de apps, e o que várias ferramentas chamam de
«nome de pacote» é na verdade esse valor.

**A segunda senha: a impressão digital.** O Android não instala um app sem assinatura. O que se compila para testar é assinado sozinho,
com um certificado de depuração que as ferramentas criam na pasta pessoal. Peça o relatório ao Gradle:

```bash
./gradlew signingReport
```

Isto foi o que deu em 18/11/2026 (aqui com a impressão digital coberta; a de verdade traz 20 pares):

```text
Variant: debug
Config: debug
Store: ~/.android/debug.keystore
Alias: AndroidDebugKey
MD5: …
SHA1: XX:XX:XX:…
SHA-256: …
Valid until: domingo, 5 de noviembre de 2056
----------
Variant: release
Config: none
```

Leiam-se três linhas:

- **`Variant: debug`** é o app de testes, o que se instala com `installDebug`. A linha **`SHA1`** dele é a
  impressão digital que será registrada. Copie-a para `respuestas/03.md`.
- **`Store`** diz de que arquivo ela saiu: o armazenamento de depuração **deste** computador.
- **`Variant: release`** diz `Config: none`: este projeto ainda não tem com que assinar uma versão para
  publicar. É o tema do exercício 3.3.

**Preveja.** Uma colega roda o mesmo comando, com o mesmo projeto, no computador dela. Sai a mesma
impressão digital? Escreva sim ou não, e por quê, antes de seguir.

**A impressão digital pode ser mostrada; a chave, não.** A documentação do Android separa assim: o certificado e a
impressão digital existem para ser compartilhados com os serviços que os pedem; a chave de assinatura e as senhas dela
se guardam em segredo. O armazenamento de depuração só serve para desenvolver: a mesma documentação diz que é
inseguro de propósito, e que por isso as lojas não aceitam um app assinado com ele.

**O que comprova:** a impressão digital de depuração copiada e a previsão escrita (base dos objetivos 3.1 e 3.3).

---

## Passo 2 · Sem IA: o que não se envia

Também sem Claude Code, e **antes** de ter o arquivo: assim não existe nem um minuto em que o git possa levá-lo.

Crie o arquivo `.gitignore` na pasta `inicio/`, com estas duas linhas:

```text
# Tu configuración de Firebase: cada quien tiene su propio proyecto.
google-services.json
```

*(O comentário diz: «A configuração do Firebase: cada pessoa tem o próprio projeto.»)*

Comprove. Este comando pergunta ao git se ele ignoraria o arquivo e por qual regra (só olha):

```bash
git check-ignore -v app/google-services.json
```

Deve aparecer uma linha com o arquivo `.gitignore`, a linha 2, a regra `google-services.json` e o caminho
`app/google-services.json`. O começo da linha muda conforme o lugar de onde o git é rodado; de dentro de `inicio/`, em
18/11/2026, foi assim:

```text
lecciones/03-la-app-conoce-a-firebase/inicio/.gitignore:2:google-services.json	app/google-services.json
```

O repositório da série já trazia a mesma regra na raiz; o git informa a mais próxima do arquivo, que é a do projeto de quem estuda.
Essa é a que protege o projeto no dia em que ele viver em um repositório próprio.

**Por que não se envia, dito com cuidado.** Não é porque seja um segredo. A documentação do Firebase diz que esse
arquivo traz identificadores únicos mas não secretos, e que as chaves de API dele só identificam o projeto: o
que protege os dados são as regras de segurança, as permissões e o App Check. Nesta série ele fica de fora por
outra razão: **cada pessoa tem o próprio projeto**. Se o de uma pessoa fosse enviado, o app de uma colega falaria com o
Firebase dela, e com uma impressão digital que não é a dela.

**O que nunca se envia**, em nenhum repositório:

- As chaves de assinatura (arquivos `.jks` ou `.keystore`) e as senhas delas.
- Senhas, de qualquer coisa.
- A chave secreta da Microsoft que será criada na lição 05.

**O que comprova:** a explicação em `respuestas/03.md` (objetivo 3.2). O `verifica` só comprova que a linha
está no `.gitignore`, que o molde `.example` existe e que o git não rastreia nenhum `google-services.json`.

---

## Passo 3 · No console: a ordem que funciona

Mais duas palavras, que agora aparecem escritas:

- **OAuth:** o acordo pelo qual um app pede ao Google que confirme quem é a pessoa, sem ver a senha dela. Usa-se
  na lição 04; hoje só se preparam os dados dele.
- **Cliente OAuth:** a ficha com que o Google reconhece um app que vai pedir isso.

Abra `app/google-services.json.example`. É assim que se vê um arquivo completo. Localize dois blocos dentro de
`oauth_client`: o que diz `"client_type": 1`, com `certificate_hash`, e o que diz `"client_type": 3`. O
primeiro é o cliente Android, com a impressão digital; o segundo, o cliente web.

**Preveja primeiro.** Se o arquivo fosse baixado logo depois de registrar o app, antes de informar a impressão digital e de
habilitar o Google, quais desses blocos se esperaria encontrar? Escreva.

Agora siga esta ordem no [console do Firebase](https://console.firebase.google.com). A ordem é a do ensaio
de 13/11/2026. Os nomes de menus e botões vão como a documentação do Firebase os escreve em 18/11/2026, em
inglês, exceto os que a versão em português da documentação publica («Nome do pacote Android», «Método de login» e
«Salvar»); o console pode mostrá-los traduzidos ou tê-los movido.

1. **Crie o projeto.** Ao criá-lo, o console oferece duas coisas que a documentação marca como opcionais: a
   assistência com IA dele e o Google Analytics. Desligue as duas: esta série não as usa.
2. **Registre o app Android.** Em «Nome do pacote Android» (Android package name) vai `mx.habil.ejemplo.tienda`, copiado exatamente. A
   documentação avisa que ele diferencia maiúsculas e minúsculas e que **não pode ser alterado** depois de registrado: com outro
   nome é preciso registrar outro app. No ensaio, o assistente não pediu a impressão digital e ofereceu baixar o
   arquivo. **Ainda não o baixe.**
3. **Adicione a impressão digital.** Em «Project settings», no cartão «Your apps», escolha o app, toque em «Add fingerprint»,
   cole o `SHA1` do passo 1 e salve.
4. **Habilite o Google.** A documentação o localiza em «Security > Authentication», aba «Método de login» (Sign-in method):
   habilite o provedor Google e salve («Salvar»). No ensaio ele pediu um nome público para o projeto e um e-mail de
   suporte: use o da própria conta.
5. **Só agora, baixe o `google-services.json`**, do mesmo cartão «Your apps», e ponha-o em `inicio/app/`.
   A documentação pede que fique somente o arquivo mais recente.

**Um único lugar.** O plugin também aceita um arquivo por variante, por exemplo em `app/src/debug/`. Esta
série usa somente `app/google-services.json`, para que haja um único arquivo a revisar, e o `verifica` não olha as
outras pastas: com o arquivo em outro lugar sai em vermelho mesmo que o projeto compile (medido em 18/11/2026: 12 de
18, com um aviso de onde o encontrou).

**Comprove sem abrir o console.** A partir da raiz do repositório:

```bash
./lecciones/03-la-app-conoce-a-firebase/verifica
```

Neste ponto **tudo vai bem** se as marcas do arquivo estiverem em verde e ficarem em vermelho só as do passo 4. Com um
arquivo inventado completo, isto foi o que deu em 18/11/2026 (recortado):

```text
✅ Texto: tu app/google-services.json nombra el paquete mx.habil.ejemplo.tienda
✅ Simulado: una app de tu archivo es del paquete mx.habil.ejemplo.tienda
✅ Simulado: tu archivo trae un cliente de OAuth de Android (tipo 1)
✅ Simulado: ese cliente trae la huella SHA-1 del almacén de depuración de esta computadora
✅ Simulado: tu archivo trae un cliente de OAuth web (tipo 3), el que usará el botón de Google en la lección 04
❌ La app compilada no lleva el project_id y el google_app_id de tu archivo: falta aplicar el plugin …
❌ Ninguna librería de Firebase está entre las dependencias de la app, o no arrancó con el proyecto …
❌ Falta Authentication: la app no trae firebase-auth entre sus dependencias. El BoM no la agrega: …
Resultado: 15 de 18 pruebas en verde (3 en rojo, 0 con error, 0 saltadas), leído de …
```

*(Essa é a saída do `verifica`, exatamente como o script a imprime, em espanhol. Em português as marcas dizem: o
arquivo nomeia o pacote; um app do arquivo é do pacote; o arquivo traz um cliente OAuth Android (tipo 1); esse
cliente traz a impressão digital SHA-1 do armazenamento de depuração deste computador; o arquivo traz um cliente OAuth
web (tipo 3), o que o botão do Google vai usar na lição 04; e, em vermelho: o app compilado não leva o `project_id`
nem o `google_app_id` do arquivo, falta aplicar o plugin; nenhuma biblioteca do Firebase está entre as dependências do
app; falta o Authentication, o app não traz `firebase-auth` entre as dependências, e o BoM não a adiciona. Resultado:
15 de 18 testes em verde.)*

Se em vez disso aparecer «descárgalo otra vez después de la huella» (baixe-o de novo depois da impressão digital), o
arquivo foi baixado cedo demais: repita o ponto 5. Não é preciso apagar nada no console.

**O que a marca da impressão digital compara.** O teste abre o armazenamento de depuração do computador, calcula o
SHA-1 dele e o procura no arquivo. Não pergunta nada ao Firebase.

**O que comprova:** as quatro marcas «Simulado» do arquivo dizem o que ele traz (objetivo 3.5). Que a ordem
foi seguida no projeto de quem estuda, nenhum teste local consegue ver: isso se confirma ao entrar, na lição 04.

---

## Passo 4 · Com o Claude Code: o plugin e a biblioteca

O arquivo já está na pasta dele, e o app ainda não o lê. Faltam duas peças de andaime:

| Peça | O que faz | Quando trabalha |
|---|---|---|
| O plugin `google-services` | Lê o `google-services.json` e o converte em recursos que o app poderá ler. Segundo a documentação dele, também pode adicionar dependências básicas dos serviços habilitados | Ao compilar. Não é executado como código no telefone |
| O BoM do Firebase e o `firebase-auth` | O BoM é uma lista de versões que funcionam juntas: fixa-se uma só e as bibliotecas vão sem versão. O `firebase-auth` é a do Authentication, e é a que fornece funções em execução | Dentro do app |

Aqui entra a ferramenta, e entra sempre do mesmo jeito: **prever → pedir → ler o que ele gerou → rodar o
`verifica` → explicar com palavras próprias.**

1. **Preveja.** Escreva em `respuestas/03.md`: que arquivos vão mudar? Algum deles é um `.kt`?
2. **Peça** ao Claude Code, de dentro de `inicio/`: «Em `gradle/libs.versions.toml` adicione o plugin
   `com.google.gms.google-services` na versão 4.6.0 e o BoM do Firebase `com.google.firebase:firebase-bom`
   na 35.2.0, com a biblioteca `firebase-auth` sem versão. Declare o plugin no `build.gradle.kts` da
   raiz com `apply false` e aplique-o em `app/build.gradle.kts`; ali mesmo adicione o BoM e `firebase-auth`. Não
   mude nenhum arquivo `.kt`, nem os testes, nem `app/google-services.json`, e não copie nenhum valor desse
   arquivo para outro lugar.»
3. **Leia** a mudança completa antes de aceitá-la. Devem ser três arquivos, com estas peças:

```toml
# gradle/libs.versions.toml — uma linha nova em cada seção
[versions]
googleServices = "4.6.0"
firebaseBom = "35.2.0"

[libraries]
firebase-bom = { group = "com.google.firebase", name = "firebase-bom", version.ref = "firebaseBom" }
firebase-auth = { group = "com.google.firebase", name = "firebase-auth" }

[plugins]
google-services = { id = "com.google.gms.google-services", version.ref = "googleServices" }
```

```kotlin
// build.gradle.kts da raiz — é declarado, sem ser aplicado
    alias(libs.plugins.google.services) apply false
```

```kotlin
// app/build.gradle.kts — aqui sim é aplicado, e as duas dependências são adicionadas
plugins {
    …
    alias(libs.plugins.google.services)
}
…
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
```

   Cinco coisas a conferir na leitura:

   - **As versões são as da série:** 4.6.0 e 35.2.0, as que a documentação do Firebase mostra em
     18/11/2026. Se aparecer outra, peça que seja corrigida: a série foi testada com essas.
   - **Estão as duas linhas, o BoM e `implementation(libs.firebase.auth)`.** O BoM só escolhe versões
     compatíveis; a segunda linha é a que adiciona o Authentication. Com o BoM e outra biblioteca do Firebase no
     lugar dela, o `verifica` dá 17 de 18 e diz «Falta Authentication» (medido em 18/11/2026).
   - **`firebase-auth` não leva versão.** O BoM a coloca. Uma versão solta ali desfaz o que o BoM
     resolve.
   - **O hífen do catálogo vira ponto:** `google-services` no catálogo se escreve
     `libs.plugins.google.services` no arquivo do Gradle.
   - **Ninguém tocou no arquivo nem copiou os valores dele.** O `git status` não deve mostrar `google-services.json` (está
     ignorado), e nenhum `.kt` deve aparecer alterado.

4. **Rode o `verifica`.** Agora a linha é
   `Resultado: 18 de 18 pruebas en verde (0 en rojo, 0 con error, 0 saltadas)`. Fica uma marca em vermelho, a de
   `respuestas/03.md`: ela se preenche no passo 5.
5. **Explique**, em três linhas de `respuestas/03.md`: o que o plugin faz com o arquivo, e em que momento.

**Veja com os próprios olhos (opcional).** O plugin deixa o que gerou em um `values.xml` embaixo de `app/build/generated/res/`. A
pasta exata é um detalhe interno e pode mudar de uma versão para outra, então procure-a em vez de
decorá-la. De dentro de `inicio/`:

```bash
find app/build/generated/res -name values.xml | xargs grep -l google_app_id
```

Com as versões da série respondeu `app/build/generated/res/processDebugGoogleServices/values/values.xml`
(18/11/2026). Abra-o e procure `project_id`, `google_app_id` e `default_web_client_id`. Com o arquivo inventado
completo trazia seis nomes: `default_web_client_id`,
`google_api_key`, `google_app_id`, `google_crash_reporting_api_key`, `google_storage_bucket` e `project_id`. Com
o arquivo sem clientes OAuth, `default_web_client_id` **não aparece**. Esse é o dado que o botão do Google vai
pedir na lição 04, e é a razão da cena: sem cliente web no arquivo, o app compila sem ele.

**O que cada marca comprova, e o que não.**

- **«Simulado», as do arquivo:** leram o texto de `app/google-services.json`. Comprovam o que ele traz.
- **«Simulado», a do app compilado:** leu os recursos que o plugin gerou, como o app os leria.
  Comprova que o arquivo chegou ao app nesta compilação.
- **«Simulado», a da biblioteca:** o teste pediu ao Firebase que iniciasse com esses recursos e leu com que
  projeto ele ficou. No app instalado, segundo a documentação do Firebase, essa inicialização é feita pela biblioteca ao
  abrir o app.
- **«Simulado», a do Authentication:** perguntou se a classe principal do `firebase-auth` viaja no app. Não a
  usou: não criou nenhuma sessão.
- **«Texto» e «Git»:** procuraram palavras nos arquivos e perguntaram ao git o que ele rastreia.

Nenhuma falou com o Firebase. Um arquivo bem formado de um projeto já apagado passa em verde, e um montado
à mão com essa forma, também. Que o arquivo vem do projeto de quem estuda, que a impressão digital está registrada e que o Google
está habilitado só se comprova da única forma possível: entrando, na lição 04.

**No app instalado.** `./gradlew installDebug` e abra-o: deve aparecer a tela de boas-vindas, igual à 02. No
emulador do teste (Pixel 9, Android 37, 18/11/2026), com a solução e um arquivo inventado, o app abriu em
«A loja» com o botão «Entrar», e o registro do sistema (`adb logcat`) mostrou a linha
`FirebaseInitProvider: FirebaseApp initialization successful`. Ali os botões não foram tocados: o percurso é
medido pelos testes simulados.

**Se alguma linha ainda não ficou clara,** antes de seguir tente explicá-la com palavras próprias. Se ainda não sair,
volte a esse trecho ou peça um esclarecimento.

**O que comprova, e até onde.** No nível simulado, o 18 de 18 comprova que este arquivo tem a forma e os
dados que a lição espera, que chegaram a esta compilação e que o Authentication viaja no app. **Não
comprova** que o arquivo venha do projeto de quem estuda, que a ordem tenha sido seguida nem que o Google esteja habilitado. Essa
parte do objetivo 3.1 se comprova no nível real, ao entrar com a conta na lição 04.

---

## Passo 5 · Sem IA: quebre uma de propósito

Este é o exercício 3.4 e se faz **sem Claude Code**. Vai-se provocar o erro de «a configuração é de outro
pacote» sem tocar no arquivo nem no console: mudando a senha do lado do app.

**1. Preveja.** Em `app/build.gradle.kts` vai-se trocar `applicationId = "mx.habil.ejemplo.tienda"` por
`applicationId = "mx.habil.ejemplo.otra"`. Escreva: compila? Quem reclama, o Kotlin, o Gradle ou o Firebase?

**2. Quebre e meça.** Faça a mudança e compile:

```bash
./gradlew assembleDebug
```

Isto foi o que deu em 18/11/2026 (o caminho, recortado):

```text
* What went wrong:
Execution failed for task ':app:processDebugGoogleServices' …
> No matching client found for package name 'mx.habil.ejemplo.otra' in …/app/google-services.json
```

Copie para `respuestas/03.md` a linha que começa com `> No matching client found`.

**3. Diagnostique**, com palavras próprias, antes de ler o que segue: o que foi procurado, onde foi procurado e por que não
foi encontrado?

O nome da tarefa diz: `processDebugGoogleServices` é o plugin, ao compilar. Ele procurou na lista
`client` do arquivo um app cujo `package_name` fosse o `applicationId` deste, e não há nenhum. Não interveio
a rede nem o Firebase: o erro saiu de comparar dois textos no computador.

**4. O conserto que tenta, e por que não.** Editar à mão o `package_name` dentro do arquivo tira o erro de
compilação. Não o faça: o arquivo é uma cópia do que há no console, e o console continua tendo
registrado o outro app. O que cabe é que as duas senhas coincidam de verdade: ou o app usa o pacote
registrado, ou se registra outro app com o pacote novo e se baixa **o arquivo dele**.

**5. Conserte.** Devolva a linha a `applicationId = "mx.habil.ejemplo.tienda"`. Este comando deve mostrar somente
as mudanças do passo 4 (só olha):

```bash
git diff -- app/build.gradle.kts
```

Rode o `verifica`: 18 de 18, e agora também a marca de `respuestas/03.md`.

Esse verde confirma duas coisas: que o projeto foi consertado e que a linha do erro foi colada. Não sabe se foi
quebrado, nem lê a previsão, nem avalia o diagnóstico. O 3.4 se avalia com a explicação, e está concluído
quando as três caixas podem ser marcadas:

- [ ] A previsão do ponto 1 está escrita, com o que se viu embaixo.
- [ ] Foi colada a linha do erro.
- [ ] Foi dito quem reclamou, que dois textos comparou e por que editar o arquivo à mão não é o conserto.

**O que comprova:** «Quebre uma de propósito» (objetivo 3.4).

---

## Os exercícios

Os passos 1 e 2, o 3.3 e o 3.4 se fazem **sem IA**.

| # | Faz-se | Está pronto quando |
|---|---|---|
| 3.1 | Seguir a ordem: projeto → app → impressão digital → Google → download | O `verifica` dá 18 de 18 com o arquivo: isso valida o arquivo e a compilação. Que o projeto é de quem estuda, com a impressão digital registrada e o Google habilitado, valida a entrada real da lição 04 |
| 3.2 | Explicar por que o `google-services.json` não se envia nesta série e nomear o que nunca se envia | A explicação está em `respuestas/03.md`. O `verifica` só revisa o `.gitignore`, o `.example` e o que o git rastreia |
| 3.3 | Distinguir a impressão digital de depuração da de uma versão publicada | Os três casos abaixo foram respondidos |
| 3.4 | Quebrar o pacote e diagnosticar o erro | A evidência são as três caixas do passo 5, escritas à mão. O `verifica` só confirma que o projeto foi consertado e que o erro foi colado |
| 3.5 | Comprovar, sem abrir o console, que o arquivo traz o cliente com a impressão digital e o cliente web | As marcas «Simulado» do arquivo estão em verde. A impressão digital que elas comparam é a de depuração deste computador |

**Exercício 3.3 · Qual impressão digital registrar?** A que foi registrada hoje é a de depuração do computador. Segundo a
documentação do Android e do Google, um app tem mais de uma:

| Quem assina o app | De onde sai a impressão digital |
|---|---|
| O computador, ao compilar para testar | Do armazenamento de depuração: `./gradlew signingReport`, variante `debug` |
| A própria pessoa, com a chave própria, para publicar | Do armazenamento dessa chave. Não é a de depuração |
| O Google Play, com o Play App Signing | Do Play Console, na página de assinatura do app. A chave fica com o Google e não é a usada para enviar o app |

Responda em `respuestas/03.md` qual impressão digital deve ser registrada no Firebase em cada caso:

1. Uma colega clona o projeto e quer entrar com o Google a partir do computador dela, contra o Firebase de quem o criou.
2. O app é publicado no Google Play com o Play App Signing e os usuários o baixam de lá.
3. Muda-se de computador e continua-se desenvolvendo.

**Tempo:** calcule umas três horas, com o console do Firebase e o «Quebre uma de propósito». É uma estimativa: será
ajustada com o tempo que a primeira turma levar.

---

## Não roda?

As mensagens em inglês são as que o plugin `google-services` 4.6.0 deu em 18/11/2026.

| O que aparece | Causa provável | O que fazer |
|---|---|---|
| `File google-services.json is missing.` | O plugin já está aplicado e o arquivo não está em `app/` | Colocá-lo em `inicio/app/`, com esse nome exato. Sobre `solucion/` é o esperado em um clone limpo |
| ❌ «Falta Authentication» | Está o BoM e falta a biblioteca | Adicionar `implementation(libs.firebase.auth)` em `app/build.gradle.kts` |
| ℹ️ «Hay uno en …/app/src/debug/…» (há um em …/app/src/debug/…) | O arquivo está em uma pasta de variante | Movê-lo para `app/`: esta série usa um único lugar |
| `No matching client found for package name '…'` | O `applicationId` do app e o pacote do arquivo não coincidem | Passo 5. Se o arquivo é de outro pacote, registrar o app com o pacote da série e baixar o arquivo dele |
| ❌ «no trae un cliente de OAuth de Android (tipo 1)» (não traz um cliente OAuth Android) | Foi baixado antes de registrar a impressão digital ou de habilitar o Google | Baixá-lo de novo (passo 3, ponto 5) |
| ❌ «Ningún cliente de Android de tu archivo trae la huella de depuración de esta computadora» (nenhum cliente Android do arquivo traz a impressão digital de depuração deste computador) | Registrou-se outra impressão digital, ou o arquivo foi baixado em outro computador | Rodar `./gradlew signingReport`, registrar esse `SHA1` e baixar de novo |
| ❌ «no trae un cliente de OAuth web (tipo 3)» (não traz um cliente OAuth web) | Foi baixado antes de habilitar o Google | Habilitar o Google e baixar de novo |
| ❌ «Git sí rastrea un google-services.json» (o git rastreia um google-services.json) | Foi adicionado antes de existir o `.gitignore` | `git rm --cached <arquivo>` o tira do git sem apagar a cópia |
| `keytool -list -v` termina com `MissingFormatArgumentException` | Aconteceu no Mac da série com Java 25 e o sistema em espanhol (18/11/2026) | Usar `./gradlew signingReport`, que é o que a lição pede |
| Não existe `~/.android/debug.keystore` e não se tem a impressão digital | Ainda não se compilou nada neste computador: o armazenamento é criado pela primeira compilação de depuração, não pelo `signingReport` (medido no Linux Mint 22.3, 18/11/2026) | Rodar `./gradlew assembleDebug` e depois `./gradlew signingReport` |

No Linux Mint 22.3 (x86_64), o `verifica` desta lição saiu igual ao do Mac (18/11/2026): sobre `inicio/`, 11 de
18 em verde; sobre `solucion/` sem o arquivo, «0 testes executados» com o aviso; e com um arquivo de teste que trazia a
impressão digital desse computador, 18 de 18. As dependências do Firebase somaram uns 200 MB a `~/.gradle`.

---

## Para fechar

**Responda sem reler:**

1. Quais são as duas senhas com que o Firebase reconhece o app, e onde está cada uma no computador?
2. Por que o arquivo é baixado no final e não quando o assistente o oferece?
3. O `verifica` dá 18 de 18. Diga uma coisa que isso comprova e uma que não.
4. Por que o `google-services.json` não se envia nesta série? E o que nunca se envia em nenhuma?

**O que vem a seguir:** na L04 põe-se o botão e entra-se com o Google. Ali se comprova, de verdade, tudo o que hoje
ficou escrito no arquivo.

---

## Compare as respostas

**Passo 1.** Não sai a mesma. O armazenamento de depuração é criado pelas ferramentas na pasta pessoal de
cada pessoa, então cada computador tem o próprio certificado e a própria impressão digital. O projeto é o mesmo; a assinatura não.

**Passo 3.** No ensaio de 13/11/2026, nenhum dos dois: o arquivo baixado antes da impressão digital e de
habilitar o Google não trazia o tipo 1 (Android, com `certificate_hash`) nem o tipo 3 (web). É uma observação desse
dia. O que a documentação do Firebase garante é o contrário: o arquivo **atualizado**, baixado
depois desses dois passos, traz a informação de OAuth de que o botão precisa. Por isso se baixa no final.

**Passo 4.** Mudam três arquivos: `gradle/libs.versions.toml`, o `build.gradle.kts` da raiz e
`app/build.gradle.kts`. Nenhum `.kt`: conectar o app não tocou em nenhuma tela, e por isso os sete testes da
02 continuam em verde. O plugin lê `app/google-services.json` ao compilar e gera recursos do app
(`project_id`, `google_app_id` e, se o arquivo traz cliente web, `default_web_client_id`). O plugin não é
executado no telefone: o que trabalha em execução são as bibliotecas declaradas, como `firebase-auth`.

**Passo 5.** Não compila. Quem reclama é o Gradle, na tarefa do plugin `google-services`; nem o Kotlin nem o Firebase. Ele comparou
o `applicationId` do app com os `package_name` do arquivo. Editar o arquivo tira o erro e deixa o
console com outro app registrado: as senhas continuariam sem coincidir onde importa.

**Exercício 3.3.**

1. A de depuração **do computador dela**, adicionada ao app no Firebase: um app aceita várias impressões digitais. Depois
   ela precisa baixar o arquivo de novo.
2. A do certificado com que o Google Play assina o app, que se consulta no Play Console. A de depuração e
   a da chave com que o app é enviado não servem para o que os usuários baixam.
3. A de depuração do computador novo: é outro armazenamento e outra impressão digital.

**Para fechar:**

1. O nome de pacote, no `applicationId` de `app/build.gradle.kts`, e a impressão digital do certificado com que o
   app é assinado; a de depuração sai do armazenamento da pasta pessoal e é impressa pelo `signingReport`.
2. Porque o arquivo é uma cópia do que o console sabe naquele momento, e o Firebase pede que se baixe o
   atualizado depois da impressão digital e de habilitar o Google. No ensaio, o anterior não trazia os clientes
   OAuth.
3. Comprova que o texto do arquivo traz o pacote, o cliente Android com a impressão digital de depuração e o
   cliente web, que o app compilado leva esses dados e que o Authentication viaja no app. Não comprova que o
   arquivo venha do projeto de quem estuda, que o projeto exista nem que seja possível entrar com o Google.
4. Porque cada pessoa tem o próprio projeto, não porque seja secreto. Nunca se enviam as chaves de assinatura, as
   senhas nem a chave secreta da Microsoft.

---

## Fontes da lição

Abertas em 18/11/2026.

- Firebase. *Add Firebase to your Android project* (passos, o pacote não pode ser alterado, o plugin 4.6.0 e o BoM 35.2.0, «unique, but non-secret identifiers»). https://firebase.google.com/docs/android/setup
- Firebase. *Google services Gradle plugin and JSON config file* (que recursos o plugin gera e de que campo sai cada um; as mensagens de erro). https://firebase.google.com/docs/android/google-services-plugin-and-file
- Firebase. *Understand Firebase for Android* (o que é o BoM; o plugin trabalha ao compilar). https://firebase.google.com/docs/android/learn-more
- Firebase. *Learn about and manage API keys for Firebase* (as chaves identificam, não autorizam). https://firebase.google.com/docs/projects/api-keys
- Firebase. *Authenticate with Google on Android* (impressão digital, habilitar o Google e baixar o arquivo atualizado). https://firebase.google.com/docs/auth/android/google-signin
- Firebase. *FirebaseInitProvider* («Initializes Firebase APIs at app startup time»). https://firebase.google.com/docs/reference/android/com/google/firebase/provider/FirebaseInitProvider
- Ajuda do Firebase. *Download Firebase config file or object* e *Add a SHA fingerprint*. https://support.google.com/firebase/answer/7015592 · https://support.google.com/firebase/answer/9137403
- Google for Developers. *Client authentication* (`./gradlew signingReport`, armazenamento de depuração, Play App Signing). https://developers.google.com/android/guides/client-auth
- Android Developers. *Sign your app* (certificado de depuração, chaves, impressões digitais para provedores, Play App Signing). https://developer.android.com/studio/publish/app-signing
- Android Developers. *Configure the app module* (o `applicationId` e o «nome de pacote»). https://developer.android.com/build/configure-app-module
- Android Developers. *Migrate your build to version catalogs* (`libs.versions.toml`, `alias`). https://developer.android.com/build/migrate-to-catalogs
- Repositório Maven do Google: os índices dele listam `google-services` 4.6.0 e `firebase-bom` 35.2.0. https://dl.google.com/dl/android/maven2/com/google/gms/google-services/maven-metadata.xml · https://dl.google.com/dl/android/maven2/com/google/firebase/firebase-bom/maven-metadata.xml
- Ensaio da série, 13/11/2026, Mac com Apple silicon: a ordem dos passos do console e o que se viu nesse dia (o assistente não pediu a impressão digital; habilitar o Google pediu nome público e e-mail de suporte; o arquivo baixado cedo demais não trazia clientes OAuth; plano sem custo). Os nomes de menus e botões desta lição não saem do ensaio: são os da documentação citada acima.
- Medição própria: Mac com Apple silicon, 18/11/2026 (`signingReport`; o `verifica` sobre `inicio/` e `solucion/`; os arquivos inventados: completo, sem clientes, de outro pacote e com outra impressão digital; os recursos que o plugin gera; as mensagens de erro; o `keytool`; o app instalado em um emulador novo, sem conta, que foi apagado ao terminar). As medições foram feitas com arquivos inventados, não com um projeto real do Firebase.

Revisão externa (18/11/2026): COM ALTERAÇÕES, 2 de prioridade alta, 6 média e 3 baixa; aplicada.
