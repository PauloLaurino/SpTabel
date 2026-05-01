#!/usr/bin/env bash
# apply_tomcat_permissions.sh
#
# Ajusta ownership e permissões recomendadas para uma instalação Tomcat dedicada.
# Uso (no servidor Linux):
#   sudo ./tools/apply_tomcat_permissions.sh /usr/local/tomcat10 tomcat
# Opções:
#   $1 = INSTALL_DIR (default: /usr/local/tomcat10)
#   $2 = SERVICE_USER (default: tomcat)
#   $3 = MODE (optional) - 'tomcat-owned' ou 'root-owned' (default: tomcat-owned)
#   --dry-run para listar ações sem aplicar

set -euo pipefail

INSTALL_DIR=${1:-/usr/local/tomcat10}
SERVICE_USER=${2:-tomcat}
MODE=${3:-tomcat-owned}
DRY_RUN=0

for arg in "$@"; do
  if [ "$arg" = "--dry-run" ]; then
    DRY_RUN=1
  fi
done

echo "Aplicando permissons. Install dir: $INSTALL_DIR ; Service user: $SERVICE_USER ; Mode: $MODE ; dry-run=$DRY_RUN"

if [ ! -d "$INSTALL_DIR" ]; then
  echo "ERRO: diretório não encontrado: $INSTALL_DIR" >&2
  exit 2
fi

# Diretórios que precisam ser graváveis pelo usuário do serviço
WRITABLE_DIRS=("$INSTALL_DIR/webapps" "$INSTALL_DIR/work" "$INSTALL_DIR/temp" "$INSTALL_DIR/logs" "$INSTALL_DIR/conf" )

echo "Will adjust ownership and permissions on the following dirs:"
for d in "${WRITABLE_DIRS[@]}"; do
  echo "  - $d"
done

if [ "$DRY_RUN" -eq 1 ]; then
  echo "DRY-RUN: nothing will be changed. Rerun without --dry-run to apply.";
fi

if [ "$MODE" = "tomcat-owned" ]; then
  # Recommended: most files remain root-owned, but writable dirs are tomcat-owned
  for dir in "${WRITABLE_DIRS[@]}"; do
    if [ -d "$dir" ]; then
      echo "chown -R $SERVICE_USER:$SERVICE_USER $dir"
      if [ "$DRY_RUN" -eq 0 ]; then sudo chown -R $SERVICE_USER:$SERVICE_USER "$dir"; fi
      echo "chmod -R 750 $dir"
      if [ "$DRY_RUN" -eq 0 ]; then sudo chmod -R 750 "$dir"; fi
    else
      echo "Note: directory does not exist, creating: $dir"
      if [ "$DRY_RUN" -eq 0 ]; then sudo mkdir -p "$dir"; sudo chown -R $SERVICE_USER:$SERVICE_USER "$dir"; sudo chmod -R 750 "$dir"; fi
    fi
  done

  # Logs folder should be readable by root too; keep conf readable only by root
  echo "chmod 640 $INSTALL_DIR/conf/* 2>/dev/null || true"
  if [ "$DRY_RUN" -eq 0 ]; then sudo chmod 640 "$INSTALL_DIR/conf"/* 2>/dev/null || true; fi

elif [ "$MODE" = "root-owned" ]; then
  # Simpler: make entire tomcat tree owned by root, but writable dirs group-writable by tomcat
  echo "chown -R root:root $INSTALL_DIR"
  if [ "$DRY_RUN" -eq 0 ]; then sudo chown -R root:root "$INSTALL_DIR"; fi

  for dir in "${WRITABLE_DIRS[@]}"; do
    echo "chgrp -R $SERVICE_USER $dir && chmod -R 750 $dir"
    if [ "$DRY_RUN" -eq 0 ]; then sudo chgrp -R $SERVICE_USER "$dir"; sudo chmod -R 750 "$dir"; fi
  done
else
  echo "Modo desconhecido: $MODE" >&2
  exit 3
fi

echo "Sync umask and systemd tmp cleanup recommendations:"
echo " - Verifique se o unit file usa 'User=$SERVICE_USER' e 'Group=$SERVICE_USER' (ex: /etc/systemd/system/spdealer.service)"
echo " - Se o Tomcat precisar recriar work/tmp, ele deve ter permissões para isso (já aplicadas acima)"

if [ "$DRY_RUN" -eq 0 ]; then
  echo "Recarregando systemd e reiniciando serviço spdealer (não forçado)."
  sudo systemctl daemon-reload || true
  echo "sudo systemctl restart spdealer"
  sudo systemctl restart spdealer || echo "Aviso: falha ao reiniciar spdealer. Verifique logs."
  echo "Verifique status: sudo systemctl status spdealer -l --no-pager"
fi

echo "Concluído."
