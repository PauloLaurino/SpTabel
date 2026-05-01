#!/usr/bin/env bash
# update_tomcat_defaults.sh
#
# Faz um scan em `tools/` procurando ocorrências de '/usr/local/tomcat*' e apresenta um diff
# de substituição sugerida para '/usr/local/tomcat10'. Não altera nada por padrão (dry-run).
# Uso:
#   ./tools/update_tomcat_defaults.sh         # apenas mostra o que mudaria (dry-run)
#   ./tools/update_tomcat_defaults.sh --apply # aplica as mudanças e cria backups *.bak

set -euo pipefail

ROOT_DIR=$(cd "$(dirname "$0")/.." && pwd)
SEARCH_DIR="$ROOT_DIR/tools"
TARGET="/usr/local/tomcat10"

APPLY=0
for arg in "$@"; do
  case "$arg" in
    --apply) APPLY=1 ;;
    --help|-h) echo "Usage: $0 [--apply]"; exit 0 ;;
  esac
done

echo "Scanning files under: $SEARCH_DIR for /usr/local/tomcat* patterns"

MAPFILE=changes_preview.txt
rm -f "$MAPFILE"

grep -R --line-number -E "/usr/local/tomcat[0-9]*" "$SEARCH_DIR" || true

while IFS= read -r file; do
  # Skip binary files
  if file "$file" | grep -qi text; then
    sed -n "1,200p" "$file" > /dev/null 2>&1 || true
  fi
done < <(grep -R --line-number -E "/usr/local/tomcat[0-9]*" "$SEARCH_DIR" | cut -d: -f1 | sort -u)

if [ "$APPLY" -eq 0 ]; then
  echo "Dry-run. To apply changes run: $0 --apply"
  echo "Found occurrences (showing lines):"
  grep -R --line-number -E "/usr/local/tomcat[0-9]*" "$SEARCH_DIR" || true
  exit 0
fi

echo "Applying replacements: '/usr/local/tomcat[0-9]*' -> $TARGET"
FILES=$(grep -R --line-number -E "/usr/local/tomcat[0-9]*" "$SEARCH_DIR" | cut -d: -f1 | sort -u)
if [ -z "$FILES" ]; then
  echo "Nenhum arquivo a alterar."; exit 0
fi

for f in $FILES; do
  echo "Backing up $f -> ${f}.bak"
  cp "$f" "${f}.bak"
  sed -E -e "s#/usr/local/tomcat[0-9]+#$TARGET#g" "${f}.bak" > "$f"
  echo "Patched $f"
done

echo "Substituições aplicadas. Revise os arquivos e faça commit apropriado. Backups com extensão .bak criados." 
