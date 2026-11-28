#!/usr/bin/env bash
# Comprueba que ningún verifica tenga una variable pegada a un carácter que no es ASCII.
#
# Por qué existe: el bash que trae la Mac de fábrica (3.2) con el idioma en UTF-8
# lee el primer byte de un carácter no ASCII pegado a una variable como parte de su
# nombre: la comilla de «$pieza», pero también —, …, é, ñ o ✅. Como
# los verifica traen `set -u`, el script se detiene con «unbound variable» antes de
# revisar nada, y el alumno con Mac se queda sin revisor. El bash de Linux y el de
# Homebrew no tienen ese problema, así que la máquina de CI (Ubuntu) nunca lo vería
# corriendo los verifica: por eso se revisa el TEXTO, no la ejecución.
# Se arregla con llaves: «${pieza}».
#
# Uso:  ./verificar-bash32.sh           → 0 si está limpio, 1 si encuentra alguno
#       ./verificar-bash32.sh --probar  → solo la autoprueba
#
# Trae autoprueba: siembra el caso en una copia temporal y exige que lo cache. Una
# puerta que no demuestra que detecta no es una puerta.
set -uo pipefail
cd "$(dirname "$0")"

# La variable sin llaves seguida de un byte no ASCII (≥ 0x80). Se busca con LC_ALL=C
# para que grep vea bytes y no caracteres.
PATRON=$'\\$[A-Za-z_][A-Za-z0-9_]*[\x80-\xff]'

buscar() {   # $1 = carpeta; imprime archivo:línea de cada caso
  LC_ALL=C grep -rnE --include=verifica "$PATRON" "$1"
  local rc=$?
  [ $rc -le 1 ] || { echo "❌ la búsqueda no se pudo hacer (rc=$rc)" >&2; return 2; }
  return 0
}

autoprueba() {
  local t; t="$(mktemp -d)"
  mkdir -p "$t/l/x"
  local c sembrado
  for c in '»' '—' '…' 'é' '✅'; do              # cada uno truena en bash 3.2 (medido por QA)
    printf '%s\n' 'set -u' 'p=a' "echo \"hay \$p$c\"" > "$t/l/x/verifica"
    sembrado="$(buscar "$t")" || { rm -rf "$t"; return 1; }
    [ -n "$sembrado" ] || { rm -rf "$t"; echo "❌ autoprueba: no cacé \$p$c" >&2; return 1; }
  done
  printf '%s\n' 'set -u' 'p=a' 'echo "hay un «${p}»"' > "$t/l/x/verifica"
  local limpio; limpio="$(buscar "$t")" || { rm -rf "$t"; return 1; }
  rm -rf "$t"
  if [ -n "$limpio" ]; then
    echo "❌ autoprueba: la búsqueda no distingue «\$p» de «\${p}»: esta puerta no sirve" >&2
    return 1
  fi
  echo "✅ autoprueba: caza «\$p» y deja pasar «\${p}»"
}

autoprueba || exit 1
[ "${1:-}" = "--probar" ] && exit 0

casos="$(buscar lecciones)" || exit 1
if [ -n "$casos" ]; then
  echo "❌ variables pegadas a un carácter no ASCII (con el bash de la Mac detienen el verifica):"
  echo "$casos" | sed 's/^/   /'
  echo "   Arréglalo con llaves: «\${variable}»."
  exit 1
fi
echo "✅ ningún verifica tiene una variable pegada a un carácter no ASCII"
