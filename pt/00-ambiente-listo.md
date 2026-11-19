# Lição 00 — Ambiente pronto

**Antes de ler, sete palavras.** Uma linha cada uma; as demais da série chegam na lição em que são usadas.

- **Android Studio:** o programa em que se escreve, compila e roda o app.
- **SDK** (kit de desenvolvimento): as ferramentas e bibliotecas para compilar e rodar apps Android. O Android Studio
  as baixa, mas elas vivem em uma pasta própria.
- **Emulador:** um telefone Android que roda como programa dentro do computador.
- **AVD** (dispositivo virtual): a ficha de *um* telefone simulado — modelo, versão do Android, memória. O emulador
  inicia um AVD.
- **Virtualização e aceleração:** a capacidade do processador de rodar outra máquina dentro de si; com ela o emulador
  é rápido, sem ela se arrasta.
- **Terminal:** a janela em que se digitam comandos.
- **`PATH`:** a lista de pastas em que o terminal procura os comandos; se uma ferramenta não está ali, é preciso
  digitar o caminho completo dela.

**O que fica com quem termina:** o Android Studio, o SDK e um emulador funcionando na máquina, com as versões exatas da
série; o emulador com o teclado do computador e uma conta do Google; e o projeto da loja rodando dentro dele.

**Ideia nova desta lição (a única que se avalia):** o ambiente Android tem três peças — Android Studio, SDK e
emulador — e cada uma se comprova com uma evidência concreta (um comando ou uma tela), não a olho.

**Uma nota sobre os textos do app.** O projeto do curso traz os textos de tela em espanhol, e os arquivos de
`lecciones/` não são traduzidos: as verificações automáticas do curso (o `verifica`) procuram esses textos exatos.
Por isso, nos blocos de código, nas saídas de terminal e nos esboços de tela, os textos ficam como o projeto os tem;
na prosa, a lição os cita em português e, na primeira vez, dá o original entre parênteses: «A loja roda no ambiente»
(no projeto, «La tienda corre en tu ambiente»).

---

## A cena

São onze da noite e o app da loja tem que estar rodando amanhã às nove. Abre-se o Android Studio pela primeira vez.
Ele pede para baixar «componentes». Responde-se que sim. Vinte minutos depois aparece na tela um telefone desenhado,
com a hora e a bateria cheia. Digita-se o e-mail para entrar com o Google… e nada acontece. Nem uma letra.

Testa-se o teclado do computador em outra janela: funciona. Volta-se ao telefone: nada. No fim, o e-mail é escrito
letra por letra com o mouse, no teclado da tela. Então o Google pede para escanear um código QR com o celular. Escaneia-se.
Gira, gira e diz que não foi possível.

Nada disso é erro de quem estuda. As duas coisas aconteceram com quem testou esta lição antes, em 13 de novembro de
2026, em um Mac novo. As duas têm uma causa concreta e uma correção de uma linha. Esta lição conta as duas **antes**
que aconteçam, junto com os comandos que dizem, em cada passo, se tudo vai bem.

---

## Antes de começar

**O que é preciso:**

| | Serve | Não serve |
|---|---|---|
| Sistema | Linux em um processador **x86_64** (a série foi testada no Linux Mint 22.3), ou um Mac | Windows (a série não o cobre) · Linux em processador ARM: o Android Studio não tem suporte oficial ali |
| Memória | 16 GB de RAM para o Android Studio com o emulador | Com 8 GB, usa-se um telefone Android no lugar do emulador (passo 3) |
| Disco | 20 GB livres. No Mac ocupou cerca de 12 GB: Android Studio 3.3, SDK 4.5, dispositivo virtual 3.1 e Gradle 1.1 (medido em 13/11/2026). No Linux Mint 22.3 ocupou cerca de 10 GB: Android Studio 1.6, SDK 3.9 (com a imagem do emulador dentro), dispositivo virtual 1.9 e Gradle 2.4 (medido em 18/11/2026) | Menos: o dispositivo virtual cresce com o uso |
| Rede | Para os downloads desta lição e da primeira compilação | |
| Conhecimento | Abrir um terminal e usar git | Não se supõe Kotlin nem Android |

**As versões da série.** Todas foram testadas juntas em uma data. Não são «as mais novas»: são as que sabemos que
funcionam entre si. Se outras forem instaladas, os resultados das lições podem não coincidir com os de quem estuda.

| Peça | Versão | Onde se vê |
|---|---|---|
| Android Studio | Rabbit 2, 2026.2.2 Patch 1 | Android Studio › About |
| Android Gradle Plugin (AGP) | 9.4.1 | `gradle/libs.versions.toml` do projeto |
| Gradle | 9.6.0 | `./gradlew -version` |
| Kotlin | 2.4.21 | `gradle/libs.versions.toml` |
| Java do projeto | 25 | `jvmToolchain(25)` em `app/build.gradle.kts` |
| Plataforma do Android | 37 (`platforms;android-37.0`) | `sdkmanager --list_installed` |
| Emulador | 37.2.13 | `emulator -version` |

*Testadas em 13/11/2026.*

---

## Mais uma palavra: nome de pacote

**Nome de pacote:** o identificador único do app, como `mx.habil.ejemplo.tienda`. Não se muda depois sem quebrar a
conexão com o Firebase. Ele será procurado no passo 5.

---

## Passo 1 · A máquina serve? (antes de instalar qualquer coisa)

**Preveja primeiro.** Antes de rodar o que está abaixo, escreva em uma linha: o processador tem a virtualização
ligada? Como se saberia?

**No Linux** (assim saiu no Linux Mint 22.3 em 18/11: `x86_64`, `VT-x`, `/dev/kvm` do grupo `kvm` e o usuário em `kvm`):

```bash
uname -m                                  # deve dizer x86_64
lscpu | grep -i virtualiz                 # deve dizer VT-x (Intel) ou AMD-V (AMD)
ls -l /dev/kvm                            # deve existir
groups                                    # o usuário deve aparecer em «kvm»
```

Se `/dev/kvm` não existe, ainda não se pode usar o KVM. As causas possíveis são três: a virtualização está desligada
na BIOS/UEFI, a máquina está dentro de outra máquina virtual (VirtualBox, VMware, Docker: ali o emulador acelerado não
roda) ou o KVM não está instalado. O teste que decide é `emulator -accel-check`, no passo 3.

Se o usuário não está em `kvm`: este passo pede a senha de administração e muda as permissões do usuário.
**Faça-o somente no próprio computador** ou se quem administra a máquina indicar; em um laboratório, não use `sudo`:
peça que o usuário seja adicionado ao grupo.

```bash
sudo gpasswd -a "$USER" kvm               # adiciona o usuário ao grupo
```

e **encerre a sessão e entre de novo**: o grupo novo não vale para a sessão aberta. Confira com `id -nG`.
Medido no Linux Mint em 13/11: sem o grupo, o emulador inicia sem aceleração.

**No Mac:**

```bash
uname -m                                  # arm64 em Apple silicon; x86_64 em Intel
```

O instalador do Android Studio tem que ser o do processador da máquina. No Mac a aceleração vem do
Hypervisor.Framework, que acompanha o macOS: não há nada a ligar.

**O que comprova:** o `verifica` da pré-verificação sai em verde ou diz o que falta (objetivo 0.1).
O `verifica` está no repositório da série. A partir da raiz dele: `./lecciones/00-ambiente-listo/verifica`. Ele só lê,
não instala nem altera nada. Na primeira vez marca em vermelho o que ainda não foi feito (o app aberto, o exercício
0.5): é normal. Rode-o de novo ao final da lição.

---

## Passo 2 · Android Studio e o SDK

**Baixe o Android Studio** de developer.android.com/studio, na versão da tabela.

- **Mac:** abra o `.dmg` e arraste o Android Studio para Aplicativos. Com Homebrew, `brew install --cask
  android-studio` faz o mesmo (foi assim que se instalou no teste de 13/11).
- **Linux:** descompacte o `.tar.gz` e rode `bin/studio.sh` a partir da sessão gráfica. No teste (Linux Mint 22.3)
  o arquivo baixado coincidiu com o sha256 publicado em developer.android.com/studio.

Ao abri-lo pela primeira vez, o assistente oferece baixar o SDK. Aceite as licenças, deixe a pasta que ele propõe e
**anote-a**: ela será usada no terminal. Estas são as que usamos no teste:

| Sistema | Pasta do SDK |
|---|---|
| Mac | `~/Library/Android/sdk` |
| Linux | `~/Android/Sdk` (no Linux Mint 22.3 o SDK foi instalado pelo terminal nessa pasta; se o assistente propuser outra, vale a que ele propuser) |

**Agora o terminal.** Acrescente estas linhas ao final do `~/.zshrc` (Mac) ou do `~/.bashrc` (Linux), trocando a
pasta pela que foi anotada, e abra um terminal novo:

```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"
```

Se `sdkmanager` não existe, instale as **Command-line Tools** em Android Studio › Settings › Languages &
Frameworks › Android SDK › SDK Tools. Depois, as peças da série:

```bash
yes | sdkmanager --licenses > /dev/null
sdkmanager "platform-tools" "emulator" "platforms;android-37.0"
sdkmanager --list_installed
```

> **Atenção ao nome:** o pacote é `platforms;android-37.0`, não `android-37`. Com o nome curto,
> o `sdkmanager` não encontra nada. E na versão de 2026, o `sdkmanager` avisa que está em desuso e que o substitui
> `android sdk`; ele continua funcionando e a série o usa porque é o que developer.android.com documenta.

**O que comprova** (objetivo 0.2), com quatro evidências que ficam guardadas:

1. Android Studio › About: Rabbit 2, 2026.2.2 Patch 1.
2. `sdkmanager --list_installed`: `platform-tools`, `emulator` e `platforms;android-37.0`.
3. `emulator -version`: 37.2.13.
4. No projeto (passo 5): `./gradlew -version` diz Gradle 9.6.0, e a tabela acima indica em que arquivo se veem
   AGP, Kotlin e `jvmToolchain(25)`.

O `verifica` da 00 comprova o que se pode automatizar; a tela About do Studio é conferida à mão.

---

## Passo 3 · O emulador, com aceleração

**Preveja primeiro.** Das três peças, qual vai demorar mais para ficar pronta, e que saída o provaria?

**Baixe uma imagem do Android com Google.** Para entrar com o Google na lição 04 é preciso uma imagem que traga o
Google Play services. A série fixa `google_apis_playstore`, que além disso traz a Play Store; uma imagem marcada
**Google APIs** também traz o Play services. O que não serve é uma imagem `default` (Android sem serviços do Google).

```bash
# Mac com Apple silicon:
sdkmanager "system-images;android-37.0;google_apis_playstore;arm64-v8a"
# Linux x86_64 (medido no Linux Mint 22.3):
sdkmanager "system-images;android-37.0;google_apis_playstore;x86_64"
```

**Crie o dispositivo** (troque `arm64-v8a` por `x86_64` no Linux):

```bash
echo no | avdmanager create avd -n tienda37 \
  -k "system-images;android-37.0;google_apis_playstore;arm64-v8a" -d pixel_9
```

**Confirme a aceleração antes de iniciá-lo:**

```bash
emulator -accel-check
```

No Mac do teste respondeu `0`, `Hypervisor.Framework OS X Version 27.0` e `accel`. O `0` do início quer dizer
«tudo certo». No Linux Mint 22.3 respondeu `KVM (version 12) is installed and usable.`, com código de saída `0`.

No Linux, o `avdmanager` pode imprimir `Error: Could not load devices from …/devices.xml` e mesmo assim criar o
dispositivo: no teste ele ficou com `hw.device.name=pixel_9`. Confira com `avdmanager list avd`.

**Inicie-o:**

```bash
emulator -avd tienda37
```

A primeira inicialização demora. No Mac do teste (Apple silicon, 32 GB) foram 57 segundos; uma posterior, 24. Cada
máquina terá o próprio número. Quando aparecer a tela inicial, em outro terminal:

```bash
adb devices
```

Deve aparecer uma linha cujo nome comece com `emulator-` e cujo estado seja `device`. O número muda conforme os
emuladores abertos (o primeiro costuma ser `emulator-5554`).

**A máquina não dá conta?** Sem aceleração, ou com 8 GB, use um telefone Android. Configurações › Sobre o dispositivo ›
toque sete vezes em «Número da versão»; depois, Configurações › Sistema › Opções do desenvolvedor › Depuração USB.
Conecte-o, aceite o aviso no telefone e rode `adb devices`: ele deve aparecer com o número de série e a palavra
`device`. Esse aviso autoriza a chave **daquele** computador a depurar o telefone até que seja revogada: aceite-o
somente no próprio computador. Se foi usado um emprestado, ao terminar vá a Opções do desenvolvedor ›
**Revogar autorizações de depuração do adb**.

**O que comprova:** exercício 0.3 (abaixo).

---

## Passo 4 · A primeira vez: o teclado e a conta

Aqui acontecem as duas coisas da cena.

### O teclado que não escreve

Um dispositivo criado pelo terminal vem com o teclado do computador **desligado**: a linha `hw.keyboard=no` no
arquivo `config.ini` dele. A correção se faz com o emulador **fechado**:

1. Encontre o arquivo: no Android Studio, Device Manager › os três pontos de `tienda37` › **Show on Disk**. O
   `config.ini` está ali. (Quase sempre é `~/.android/avd/tienda37.avd/`, mas muda se `ANDROID_AVD_HOME` foi definida.)
2. Troque `hw.keyboard=no` por `hw.keyboard=yes`. Se a linha não existe, acrescente-a ao final.
3. Confira antes de iniciar:

```bash
grep '^hw.keyboard=' CAMINHO/config.ini      # deve dizer hw.keyboard=yes; se não aparecer, não inicie ainda
```

Atalho para o caminho de sempre (Mac e Linux):

```bash
sed -i.bak 's/^hw.keyboard=no$/hw.keyboard=yes/' ~/.android/avd/tienda37.avd/config.ini
```

Inicie o emulador de novo. Agora o teclado do computador escreve.

### Conta e equipamento de confiança

O emulador guarda no computador o que se faz nele —apps, configurações e a sessão da conta. Por isso:

- **Entre com a conta somente no computador pessoal** ou em um equipamento autorizado.
- **Em laboratório ou equipamento emprestado,** use um telefone próprio (passo 3) ou uma conta de teste sem dados
  pessoais; não digite ali a senha da conta principal.
- Se foi preciso entrar em um equipamento alheio, ao terminar retire a conta e apague os dados do emulador:
  Device Manager › os três pontos do dispositivo › **Wipe Data**.

### A conta do Google

No emulador: Configurações › Google › adicionar a conta. Se a conta usa **chave de acesso** (passkey), o Google mostra
um código QR para escanear com o telefone. Esse passo conecta o computador e o telefone por Bluetooth, e o emulador
não o tem: no teste desta lição o QR não completou. Se acontecer, toque em **«Try another way»** (na tela do Google, em
inglês) e entre com a senha ou com o aviso que chega ao telefone.

**O que comprova:** exercício 0.4 (abaixo).

---

## Passo 5 · O projeto da loja roda

O repositório da série é público no GitHub: https://github.com/HabilMX/tienda-app-nativa

**Preveja primeiro.** Quanto tempo a primeira compilação vai levar, e por que a segunda seria mais rápida?

```bash
git clone https://github.com/HabilMX/tienda-app-nativa.git
cd tienda-app-nativa/lecciones/00-ambiente-listo/inicio
./gradlew -version          # Gradle 9.6.0 e a versão do Java
./gradlew installDebug      # compila e a INSTALA no emulador ou no telefone; não a abre
```

Agora **abra-a** pela gaveta de apps do emulador ou do telefone. Tudo vai bem se aparecer a tela «A loja roda no
ambiente» (no projeto, «La tienda corre en tu ambiente»).

A primeira compilação baixa bibliotecas e leva vários minutos. O projeto de início já traz uma configuração que se
explica na L01: aqui não a altere; primeiro confira que é possível instalar e abrir o app.

### Com o Claude Code, nesta ordem

A série usa o Claude Code em todas as lições, sempre do mesmo jeito: **prever → pedir → ler o que ele gerou → rodar
o `verifica` → explicar com palavras próprias.** Se ainda não for possível explicar, pare, confira de novo e
peça um esclarecimento antes de seguir.

1. **Preveja** sem abrir os arquivos: em que arquivo se decide o nome de pacote do app?
2. **Peça** ao Claude Code: «Explique-me a estrutura deste projeto: o que cada arquivo do Gradle faz e onde se decide
   o nome de pacote».
3. **Leia** a resposta e escolha **uma** afirmação concreta (por exemplo, «o `applicationId` está em
   `app/build.gradle.kts`»).
4. **Comprove-a** nos arquivos: abra-o e anote em `respuestas/00.md` (na raiz do repositório) a
   afirmação, o arquivo e a linha em que ela está, por exemplo `app/build.gradle.kts:8`. Se a afirmação era falsa,
   anote isso também: vale igual.
5. **Rode o `verifica`** e **explique** em duas linhas o que foi comprovado.

**O que comprova:** exercício 0.5 (abaixo).

---

## Os exercícios

Os exercícios 0.3 e 0.4 são feitos **sem IA**: são comandos e telas que é preciso ver com os próprios olhos.

| # | Faz-se | Está pronto quando |
|---|---|---|
| 0.1 | Rodar a pré-verificação do passo 1 | O `verifica` sai em verde, ou está escrito o que falta e como foi resolvido |
| 0.2 | Instalar o Studio e o SDK do passo 2 | Estão em mãos as quatro evidências do passo 2 e elas coincidem com a tabela de versões |
| 0.3 | Iniciar o emulador com aceleração, ou conectar o telefone | Está em mãos a saída de `emulator -accel-check` com «accel» (ou o telefone em `adb devices` como `device`) |
| 0.4 | Ligar o teclado e adicionar a conta | Escreve-se com o teclado do computador no emulador e a conta aparece em Configurações › Google |
| 0.5 | Rodar o projeto e comprovar uma afirmação do Claude Code | O app foi aberto e mostra «A loja roda no ambiente», e estão anotados: a afirmação, o arquivo e a linha em que foi comprovada, e se era verdadeira |

**Tempo:** na primeira vez, contando os downloads, reserve uma tarde. No Linux Mint 22.3, os downloads do SDK, do
emulador (que iniciou em 109 segundos) e a primeira compilação (3 min 51 s) levaram 10 minutos de relógio com boa
rede; some o assistente do Android Studio e as pausas.

---

## Não roda?

| O que aparece | Causa provável | O que fazer |
|---|---|---|
| `sdkmanager: command not found` | Faltam as Command-line Tools ou o `PATH` | Instalá-las (passo 2) e abrir um terminal novo |
| O `sdkmanager` não encontra `platforms;android-37` | O nome está incompleto | É `platforms;android-37.0` |
| `-accel-check` não diz «accel» (Linux) | Virtualização desligada, a máquina está dentro de outra máquina virtual, KVM sem instalar ou o usuário fora de `kvm` | Passo 1: BIOS/UEFI, sair da VM, ou o grupo `kvm` (com permissão), e entrar de novo na sessão |
| O emulador leva minutos e se arrasta | Sem aceleração | Igual à linha anterior, ou usar um telefone |
| O teclado do computador não escreve no emulador | `hw.keyboard=no` | Passo 4, com o emulador fechado |
| O QR da chave de acesso não termina | O emulador não tem Bluetooth até o telefone | «Try another way» |
| O `avdmanager` não reconhece `pixel_9` | Esse perfil não existe na versão instalada | Rodar `avdmanager list device -c` e usar um nome da lista |
| `adb devices` não mostra o telefone | Depuração USB desligada ou aviso sem aceitar | Conferir o aviso na tela do telefone |
| `adb devices` diz `unauthorized` | A chave do computador não foi aceita | Desbloquear o telefone, aceitar o aviso e repetir |
| O Linux não detecta o telefone por USB | Permissões do sistema (`plugdev`, regras udev) | No Android Studio, Tools › Troubleshoot Device Connections; se pedir permissões, resolver com quem administra o equipamento |
| `installDebug` terminou bem, mas o app não aparece | `installDebug` instala, não abre | Abri-lo pela gaveta de apps (passo 5) |
| «Java heap space» ao compilar | Memória do Gradle | Conferir que o `gradle.properties` traz `-Xmx4g` |

---

## Para fechar

**Responda sem reler:**

1. Que três peças formam o ambiente Android, e com que evidência se comprova cada uma?
2. Por que o emulador precisa de uma imagem com Google e não de qualquer uma?
3. O dispositivo foi criado pelo terminal e o teclado do computador não escreve. O que se muda e onde?
4. O que fazer com a conta do Google se a máquina não é própria?

**O que vem a seguir:** já existe um lugar onde rodar o app. Na L01 escreve-se a primeira tela da loja em Compose, e o
`verifica` comprova o que se vê **sem** abrir o emulador.

---

## Compare as respostas

1. Android Studio (a tela About), o SDK (`sdkmanager --list_installed`) e o emulador (`emulator -accel-check`
   e `adb devices`).
2. Porque entrar com o Google precisa do Google Play services. As imagens `google_apis_playstore` (a da
   série) e as marcadas Google APIs o trazem; uma imagem `default` não.
3. `hw.keyboard=no` por `hw.keyboard=yes` no `config.ini` do dispositivo (que se encontra com Show on Disk), com o
   emulador fechado, e iniciá-lo de novo.
4. Não usar ali a conta principal: usar um telefone próprio ou uma conta de teste. Se já se entrou, retira-se a conta e
   apagam-se os dados do emulador com Wipe Data.

---

## Fontes da lição

Abertas em 13/11/2026.

- Android Developers. *Install Android Studio* (requisitos; Linux ARM sem suporte oficial). https://developer.android.com/studio/install
- Android Developers. *Start the emulator from the command line* (`-accel-check`, KVM e Hypervisor.Framework). https://developer.android.com/studio/run/emulator-commandline
- Android Developers. *Configure hardware acceleration* (não roda dentro de outra VM; permissões do KVM). https://developer.android.com/studio/run/emulator-acceleration
- Android Developers. *Create and manage virtual devices* (imagens com Google APIs e Play; dados do AVD; Show on Disk; Wipe Data). https://developer.android.com/studio/run/managing-avds
- Android Developers. *sdkmanager* e *avdmanager* (listar pacotes e perfis). https://developer.android.com/tools/sdkmanager · https://developer.android.com/tools/avdmanager
- Android Developers. *Environment variables* (`ANDROID_AVD_HOME`, `PATH`). https://developer.android.com/tools/variables
- Android Developers. *Run apps on a hardware device* (chave RSA, revogar autorizações, `plugdev`, Troubleshoot Device Connections). https://developer.android.com/studio/run/device
- Android Developers. *Android Debug Bridge* (serial `emulator-<porta>`). https://developer.android.com/tools/adb
- Android Developers. *Build your app from the command line* (`installDebug` compila e instala). https://developer.android.com/build/building-cmdline
- Android Developers. *Android Gradle plugin release notes* (o AGP 9.4 exige Gradle 9.6.0) e *Java versions in Android builds* (toolchain). https://developer.android.com/build/releases/about-agp · https://developer.android.com/build/jdks
- Android Developers. *Authenticate across form factors* (o emulador não serve para o fluxo de chave de acesso entre dispositivos). https://developer.android.com/identity/form-factors
- Medição própria: ensaio em um Mac com Apple silicon e em um Linux Mint x86_64, 13/11/2026; ambiente completo no Linux
  Mint 22.3 x86_64 (KVM, SDK, emulador, primeira compilação e instalação), 18/11/2026.
